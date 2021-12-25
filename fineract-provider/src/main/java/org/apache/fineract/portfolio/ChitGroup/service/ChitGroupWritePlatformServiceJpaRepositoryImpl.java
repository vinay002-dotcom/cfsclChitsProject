/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.ChitGroup.service;

import static java.time.temporal.TemporalAdjusters.dayOfWeekInMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.accounting.common.AccountingConstants.CashAccountsForChit;
import org.apache.fineract.accounting.journalentry.service.AccountingProcessorHelper;
import org.apache.fineract.accounting.journalentry.service.JournalEntryReadPlatformService;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.producttoaccountmapping.service.GlAccountChitReadPlatformService;
import org.apache.fineract.accounting.producttoaccountmapping.service.GlAccountChitWritePlatformService;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.api.CodeValuesApiResource;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeReadPlatformService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.JsonParserHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.organisation.staff.exception.StaffNotFoundException;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsWinnerData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitCycleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleForMobile;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberTransactionData;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitBids;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitGroup;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitGroupRepository;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitGroupSubscriber;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitSubscriberCharge;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitSubscriberRepository;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitSubscriberTransaction;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitTransactionEnum;
import org.apache.fineract.portfolio.ChitGroup.domain.subscriberStatusEnum;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitBidNotFoundException;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitBidsWinnerException;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupCycleException;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitSubscriberInvalidException;
import org.apache.fineract.portfolio.ChitGroup.exception.SomethingWentWrongException;
import org.apache.fineract.portfolio.ChitGroup.handler.FindWorkingDays;
import org.apache.fineract.portfolio.ChitGroup.serialization.ChitGroupCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.account.data.BranchesAccountData;
import org.apache.fineract.portfolio.account.service.BranchesAccountReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientTransactionReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientTransactionWritePlatformService;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailsReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.fineract.portfolio.client.data.ClientTransactionChitAdvanceAdjustment;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitBidsRepository;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitDemandSchedule;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitSubscriberChargeRepository;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitDemandScheduleRepository;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitSubscriberTransactionRepository;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;

@Service
public class ChitGroupWritePlatformServiceJpaRepositoryImpl implements ChitGroupWritePlatformServices {
	private static final Logger LOG = LoggerFactory.getLogger(ChitGroupWritePlatformServiceJpaRepositoryImpl.class);
	private final ChitGroupCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ChitGroupRepository chitGroupRepository;
	private final OfficeRepositoryWrapper officeRepositoryWrapper;
	private final JdbcTemplate jdbcTemplate;

	private FindWorkingDays findWorkingDays = new FindWorkingDays();

	private final ChitSubscriberRepository chitSubscriberRepository;

	private final ChitGroupReadPlatformService chitGroupReadPlatformService;

	private final ClientRepositoryWrapper clientRepository;
	private final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService;

	private final ClientTransactionChitAdvanceAdjustment clientTransactionChitAdvanceAdjustment;

	private final ClientReadPlatformService clientReadPlatformService;

	private final ClientTransactionReadPlatformService clientTransactionReadPlatformService;

	private final ClientTransactionWritePlatformService clientTransactionWritePlatformService;

	private final ChitSubscriberTransactionWritePlatformService chitSubscriberTransactionWritePlatformService;

	private final ChitChargeReadPlatformServices chitChargeReadPlatformServices;

	private final ChitSubscriberChargeWritePlatformService chitSubscriberChargeWritePlatformService;

	private final ChitDemandScheduleWritePlatformService chitDemandScheduleWritePlatformService ;

	private final ChitCycleReadPlatformService chitCycleReadPlatformService ;

	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

	private final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices;

	private final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService;

	private final ChitBidsReadPlatformService chitBidsReadPlatformService;

	private final ChitBidsWritePlatformService chitBidsWritePlatformService;

	private final ConfigurationReadPlatformService configurationReadPlatformService;

	private final AccountingProcessorHelper accountingProcessorHelper;	

	private final GlAccountChitWritePlatformService glAccountChitWritePlatformService;

	private final GlAccountChitReadPlatformService glAccountChitReadPlatformService;

	private final CodeValuesApiResource codeValuesApiResource;

	private final CodeReadPlatformService codeReadPlatformService;

	private final CodeValueReadPlatformService codeValueReadPlatformService;

	private final ChitBidsRepository chitBidsRepository;
	
	private final ChitSubscriberChargeRepository chitSubscriberChargeRepository;
	
	private final ChitDemandScheduleRepository chitDemandScheduleRepository;
	
	private final ChitSubscriberTransactionRepository chitSubscriberTransactionRepository;
	
	private FromJsonHelper fromJsonHelper;
	
	private PaymentDetailWritePlatformService paymentDetailWritePlatformService;
	private  JournalEntryReadPlatformService journalEntryReadPlatformService;
	private JournalEntryWritePlatformService journalEntryWritePlatformService;
	
	private final ClientWritePlatformService clientWritePlatformService;
	final BranchesAccountReadPlatformService branchesAccountReadPlatformService;
	final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService;
	final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
	@Autowired
	public ChitGroupWritePlatformServiceJpaRepositoryImpl(
			final ChitGroupCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final ChitGroupRepository chitGroupRepository, final OfficeRepositoryWrapper officeRepositoryWrapper,
			final RoutingDataSource dataSource, final ChitSubscriberRepository chitSubscriberRepository,
			final ClientRepositoryWrapper clientRepository,final ChitGroupReadPlatformService chitGroupReadPlatformService , final ClientReadPlatformService clientReadPlatformService
			,final ClientTransactionReadPlatformService clientTransactionReadPlatformService,final ClientTransactionWritePlatformService clientTransactionWritePlatformService,
			final ChitSubscriberTransactionWritePlatformService chitSubscriberTransactionWritePlatformService,final ChitChargeReadPlatformServices chitChargeReadPlatformServices,
			final ChitSubscriberChargeWritePlatformService chitSubscriberChargeWritePlatformService,final ChitDemandScheduleWritePlatformService chitDemandScheduleWritePlatformService,final ChitCycleReadPlatformService chitCycleReadPlatformService,
			final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices,final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService, final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService,
			final ChitBidsReadPlatformService chitBidsReadPlatformService,
			final ConfigurationReadPlatformService configurationReadPlatformService,
			final ChitBidsWritePlatformService chitBidsWritePlatformService,
			final ClientTransactionChitAdvanceAdjustment clientTransactionChitAdvanceAdjustment,
			final AccountingProcessorHelper accountingProcessorHelper,
			final GlAccountChitWritePlatformService glAccountChitWritePlatformService,
			final GlAccountChitReadPlatformService glAccountChitReadPlatformService,final CodeValuesApiResource codeValuesApiResource, final CodeReadPlatformService codeReadPlatformService,
			final CodeValueReadPlatformService codeValueReadPlatformService,final ChitBidsRepository chitBidsRepository,final ChitSubscriberChargeRepository chitSubscriberChargeRepository,
			final ChitDemandScheduleRepository chitDemandScheduleRepository, final ChitSubscriberTransactionRepository chitSubscriberTransactionRepository,
			PaymentDetailWritePlatformService paymentDetailWritePlatformService,
			 FromJsonHelper fromJsonHelper, final  JournalEntryReadPlatformService journalEntryReadPlatformService,
			 final JournalEntryWritePlatformService journalEntryWritePlatformService,
			 final ClientWritePlatformService clientWritePlatformService,
			 final BranchesAccountReadPlatformService branchesAccountReadPlatformService,
			 final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService,
			 final PaymentTypeReadPlatformService paymentTypeReadPlatformService) {

		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.chitGroupRepository = chitGroupRepository;
		this.officeRepositoryWrapper = officeRepositoryWrapper;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.chitSubscriberRepository = chitSubscriberRepository;
		this.clientRepository = clientRepository;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
		this.clientTransactionReadPlatformService  = clientTransactionReadPlatformService;
		this.clientTransactionWritePlatformService = clientTransactionWritePlatformService;
		this.chitSubscriberTransactionWritePlatformService = chitSubscriberTransactionWritePlatformService;
		this.chitChargeReadPlatformServices = chitChargeReadPlatformServices;
		this.chitSubscriberChargeWritePlatformService = chitSubscriberChargeWritePlatformService;
		this.chitDemandScheduleWritePlatformService = chitDemandScheduleWritePlatformService;
		this.chitCycleReadPlatformService = chitCycleReadPlatformService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.chitSubscriberChargeReadPlatformServices = chitSubscriberChargeReadPlatformServices;
		this.chitDemandScheduleReadPlatformService = chitDemandScheduleReadPlatformService;
		this.chitSubscriberTransactionReadPlatformService = chitSubscriberTransactionReadPlatformService;
		this.chitBidsReadPlatformService = chitBidsReadPlatformService;
		this.configurationReadPlatformService = configurationReadPlatformService;
		this.chitBidsWritePlatformService = chitBidsWritePlatformService;
		this.clientTransactionChitAdvanceAdjustment = clientTransactionChitAdvanceAdjustment;
		this.accountingProcessorHelper = accountingProcessorHelper;
		this.glAccountChitWritePlatformService = glAccountChitWritePlatformService;
		this.glAccountChitReadPlatformService = glAccountChitReadPlatformService;
		this.codeValuesApiResource = codeValuesApiResource;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.codeReadPlatformService = codeReadPlatformService;
		this.chitBidsRepository = chitBidsRepository;
		this.chitSubscriberChargeRepository = chitSubscriberChargeRepository;
		this.chitDemandScheduleRepository = chitDemandScheduleRepository;
		this.chitSubscriberTransactionRepository = chitSubscriberTransactionRepository;
		
		this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
		this.fromJsonHelper =fromJsonHelper;
		this.journalEntryReadPlatformService = journalEntryReadPlatformService;
		this.journalEntryWritePlatformService = journalEntryWritePlatformService;
		this.clientWritePlatformService = clientWritePlatformService;
		this.branchesAccountReadPlatformService = branchesAccountReadPlatformService;
		this.paymentDetailsReadPlatformService = paymentDetailsReadPlatformService;
		this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult createChitGroup(final JsonCommand command) {

		try {
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final Long officeId = command.longValueOfParameterNamed("officeId");
			final Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);
			// System.out.println("------ in createChitGroup() --- ");
			// System.out.println(command.json().toString());
			ChitGroup chit = ChitGroup.create(office, command);
			ChitGroup newchit = this.chitGroupRepository.save(chit);
			// Add company as 1st Chit Subscriber
			Long clientId = this.clientRepository.getClientByAccountNumber("001").getId();
			JsonObject compSubscriber = new JsonObject();
			compSubscriber.addProperty("clientid", clientId);
			compSubscriber.addProperty("chitnumber", 1);
			this.createChitGroupSubscriber(newchit.getId(), compSubscriber); 
			CommandProcessingResult result = new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()).withOfficeId(officeId) //
					.withEntityId(newchit.getId()) //
					.build();
			return result;

		} catch (final JpaSystemException   | DataIntegrityViolationException  dve ) {
			handleChitGroupDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitGroupDataIntegrityIssues(command, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateChitGroup(Long id, JsonCommand command) {
		try {
			this.fromApiJsonDeserializer.validateForUpdate(command.json(), id);
			final ChitGroup chitgroupForUpdate = this.chitGroupRepository.findById(id)
					.orElseThrow(() -> new StaffNotFoundException(id));
			final Map<String, Object> changesOnly = chitgroupForUpdate.update(command);

			if (changesOnly.containsKey("officeId")) {
				final Long officeId = (Long) changesOnly.get("officeId");
				final Office newOffice = this.officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);
				chitgroupForUpdate.changeOffice(newOffice);
			}
			if (!changesOnly.isEmpty()) {
				this.chitGroupRepository.saveAndFlush(chitgroupForUpdate);
			}
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(id)
					.withOfficeId(chitgroupForUpdate.officeId()).with(changesOnly).build();
		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitGroupDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitGroupDataIntegrityIssues(command, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}


	@Transactional
	public CommandProcessingResult updateChitGroupwithJSon(Long id, JsonObject command) {
		try {
			this.fromApiJsonDeserializer.validateForUpdate(command.toString(), id);
			final ChitGroup chitgroupForUpdate = this.chitGroupRepository.findById(id)
					.orElseThrow(() -> new StaffNotFoundException(id));
			final Map<String, Object> changesOnly = chitgroupForUpdate.updatewithJson(command);

			if (changesOnly.containsKey("officeId")) {
				final Long officeId = (Long) changesOnly.get("officeId");
				final Office newOffice = this.officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);
				chitgroupForUpdate.changeOffice(newOffice);
			}
			if (!changesOnly.isEmpty()) {
				this.chitGroupRepository.saveAndFlush(chitgroupForUpdate);
			}
			return new CommandProcessingResultBuilder().withEntityId(id)
					.withOfficeId(chitgroupForUpdate.officeId()).with(changesOnly).build();
		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitGroupDataIntegrityIssues(null, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitGroupDataIntegrityIssues(null, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public void deleteChitGroup(final Long chitId) throws SQLException {
		try {
			String sql = "delete from chit_group_subscriber where chit_id = ?";
			this.jdbcTemplate.update(sql, new Object[] { chitId });
			sql = "delete from m_document where parent_entity_type='ChitGroups' and parent_entity_id = ?";
			this.jdbcTemplate.update(sql, new Object[] { chitId });
			sql = "delete from chit_group where id = ?";
			this.jdbcTemplate.update(sql, new Object[] { chitId });
		} catch (DataAccessException e) {
			throw new SQLException(e);
		}

	}

	private void handleChitGroupDataIntegrityIssues(final JsonCommand command, final Throwable realCause,
			final Exception dve) {

		// if (realCause.getMessage().contains("external_id")) {

		// final String externalId = command.stringValueOfParameterNamed("externalId");
		// throw new
		// PlatformDataIntegrityException("error.msg.staff.duplicate.externalId",
		// "Staff with externalId `" + externalId + "` already exists", "externalId",
		// externalId);
		// } else if (realCause.getMessage().contains("display_name")) {
		// final String lastname = command.stringValueOfParameterNamed("lastname");
		// String displayName = lastname;
		// if (!StringUtils.isBlank(displayName)) {
		// final String firstname = command.stringValueOfParameterNamed("firstname");
		// displayName = lastname + ", " + firstname;
		// }
		// throw new
		// PlatformDataIntegrityException("error.msg.staff.duplicate.displayName",
		// "A staff with the given display name '" + displayName + "' already exists",
		// "displayName", displayName);
		// }

		LOG.error("Error occured.", dve);
		throw new PlatformDataIntegrityException("error.msg.staff.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}

	//	@Transactional
	//	@Override
	//	public void createChitGroupSubscriber(final Long chitid, final JsonElement data) throws SQLException {
	//
	//		try {
	//				
	//			JsonObject subsData = data.getAsJsonObject();
	//			System.out.println("data "+subsData.toString());
	//			System.out.println("prized subscriber "+subsData.get("prizedsubscriber").getAsBoolean());
	//			Boolean prizedsubscriber = subsData.get("prizedsubscriber").getAsBoolean();
	//			System.out.println("prized cycle "+subsData.get("prizedcycle").getAsLong());
	//			Long prizedcycle = subsData.get("prizedcycle").getAsLong();
	//			// TODO Validation - check if this chit number is already taken. This is handled
	//			// in UI but better to handle here too. //
	//			final String sql = "insert chit_group_subscriber (chit_id,client_id,chit_number,prized_subscriber,prized_cycle) values (?,?,?,?,?)";
	//			this.jdbcTemplate.update(sql, new Object[] { chitid, subsData.get("clientid").getAsLong(),
	//					subsData.get("chitnumber").getAsInt(),prizedsubscriber,prizedcycle });
	//			// return this.jdbcTemplate. query(sql, new Object[] { id }, rm);
	//		} catch (DataAccessException e) {
	//			throw new SQLException(e);
	//		}
	//	}

	@Transactional
	@Override
	public void deleteChitGroupSubscriber(final Long subscriberId) throws SQLException {
		try {
			// TODO : Validate if the delete request belongs to company subscription. 
			// ChitNumber = 1 should not be deleted. Handled in UI but need here too.

			final String sql = "delete from chit_group_subscriber where id = ?";
			this.jdbcTemplate.update(sql, new Object[] { subscriberId });
		} catch (DataAccessException e) {
			throw new SQLException(e);
		}

	}

	@Transactional
	@Override
	public void activateChitGroup(final Long chitid, final JsonElement data) throws SQLException {
		try {
			// Identify all Auction_Dates for this Chit Configuration

			JsonObject chitData = data.getAsJsonObject();
			//System.out.println("ChitData");
			//System.out.println(chitData.toString());

			LocalDate startDate = JsonParserHelper.convertFrom(chitData.get("startdate").getAsString(), "startdate", chitData.get("dateFormat").getAsString(),
					JsonParserHelper.localeFromString(chitData.get("locale").getAsString()));

			String auctionday = chitData.get("auctionday").getAsString();
			int auctiondayValue = chitData.get("auctiondayValue").getAsInt();
			int auctionTimeHour = chitData.get("auctiontime") != null? chitData.getAsJsonObject("auctiontime").get("hour").getAsInt(): 0;
			int auctionTimeMin = chitData.get("auctiontime") != null? chitData.getAsJsonObject("auctiontime").get("minute").getAsInt(): 0;			
			String auctiondayType = chitData.get("auctiondayType").getAsString();
			String auctionweekValue = chitData.get("auctionweekValue").getAsString();
			int chitDuration = chitData.get("chitduration").getAsInt();
			ArrayList<LocalDate> calculated_chitAuctionDays = new ArrayList<LocalDate>();

			// For Calendar Days Calculation
			if (auctionday.equalsIgnoreCase("CalDay")) {
				LocalDate firstDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), auctiondayValue);

				if (startDate.getDayOfMonth() > auctiondayValue) {
					firstDate = firstDate.plusMonths(1);
				}
				calculated_chitAuctionDays.add(firstDate);
				// add next n-1 values
				for (int i = 1; i < chitDuration; i++) {
					firstDate = firstDate.plusMonths(1);
					calculated_chitAuctionDays.add(firstDate);
				}
				//calculated_chitAuctionDays.forEach( dt -> System.out.println(dt));
			}

			// For Flexible Days Calculations
			if (auctionday.equalsIgnoreCase("FlexDay")) {
				int whichWeek = 0;
				switch (auctiondayType) {
				case "First":
					whichWeek = 1;
					break;
				case "Second":
					whichWeek = 2;
					break;
				case "Third":
					whichWeek = 3;
					break;
				case "Fourth":
					whichWeek = 4;
					break;
				case "Last":
					whichWeek = 5;
					break;
				}
				DayOfWeek whichWeekDay = DayOfWeek.MONDAY;
				switch (auctionweekValue) {
				case "Monday":
					whichWeekDay = DayOfWeek.MONDAY;
					break;
				case "Tuesday":
					whichWeekDay = DayOfWeek.TUESDAY;
					break;
				case "Wednesday":
					whichWeekDay = DayOfWeek.WEDNESDAY;
					break;
				case "Thursday":
					whichWeekDay = DayOfWeek.THURSDAY;
					break;
				case "Friday":
					whichWeekDay = DayOfWeek.FRIDAY;
					break;
				case "Saturday":
					whichWeekDay = DayOfWeek.SATURDAY;
					break;
				case "Sunday":
					whichWeekDay = DayOfWeek.SUNDAY;
					break;
				}
				LocalDate firstDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth());
				if (whichWeek == 5)
					firstDate = firstDate.with(lastDayOfMonth()).with(previousOrSame(whichWeekDay));
				else
					firstDate = firstDate.with(dayOfWeekInMonth(whichWeek, whichWeekDay));

				// If start date is after firstDate, start auction from next month.
				if (startDate.isAfter(firstDate)) {
					firstDate = LocalDate.of(firstDate.getYear(), firstDate.getMonthValue(), 1);
					firstDate = firstDate.plusMonths(1); //
				}
				//System.out.println("--- First Date Month " + firstDate);

				for (int i = 0; i < chitDuration; i++) {
					if (whichWeek == 5) {
						firstDate = firstDate.with(lastDayOfMonth()).with(previousOrSame(whichWeekDay));
						calculated_chitAuctionDays.add(firstDate);
						firstDate = LocalDate.of(firstDate.getYear(), firstDate.getMonthValue(), 1);
						firstDate = firstDate.plusMonths(1);
					} else {
						firstDate = firstDate.with(dayOfWeekInMonth(whichWeek, whichWeekDay));
						calculated_chitAuctionDays.add(firstDate);
						firstDate = LocalDate.of(firstDate.getYear(), firstDate.getMonthValue(), 1);
						firstDate = firstDate.plusMonths(1);
					}
				}
				//calculated_chitAuctionDays.forEach(dt -> System.out.println(dt));
			}
			// Change Status to 20; Set Current_Cycle to 1 and set next Auction_Date;
			String sql = "update chit_group cg set cg.status=20, cg.current_cycle=1, cg.next_auction_date=? where id=?";
			LocalDate nxtAucDt = (LocalDate)calculated_chitAuctionDays.toArray()[0];

			this.jdbcTemplate.update(sql, nxtAucDt.atTime(auctionTimeHour, auctionTimeMin).toString(), chitid);

			// Create cycles for the chit
			sql = "insert into chit_group_cycle (chit_id, cycle_number, auction_date) values (?,?,?)";
			Object[] calculated_chitAuctionDays_Arr = calculated_chitAuctionDays.toArray();
			for (int cycleNumber=0; cycleNumber< calculated_chitAuctionDays_Arr.length; cycleNumber++) {
				nxtAucDt = (LocalDate)calculated_chitAuctionDays_Arr[cycleNumber];
				this.jdbcTemplate.update(sql, new Object[] { chitid, (cycleNumber+1), nxtAucDt.atTime(auctionTimeHour, auctionTimeMin).toString() });
			};
			this.chitActivation(chitid,data);
		} catch (DataAccessException e) {
			throw new SQLException(e);
		}

	}


	@Transactional
	@Override
	public CommandProcessingResult createChitGroupSubscriber(final Long ChitId,final JsonObject command) {

		try {
			//this.fromApiJsonDeserializer.validateForCreate(command.json());
			// final Long chitSubscriberId = command.longValueOfParameterNamed("chitSubscriberId");
			// ChitGroupSubscriberData subscriber = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscriberId);
			//System.out.println(command);
			
			/*
			 * here we are validating if client has already added more than ticketLimit we should not allow!
			  */
			Long clientId = null;
			if (command.get("clientid")!=null && !command.get("clientid").isJsonNull())
			{
				clientId  = command.get("clientid").getAsLong();
			}
			String accNo = this.clientReadPlatformService.retrieveOne(clientId).accountNo();
			Integer temp = Integer.parseInt(accNo);
			if(temp!=001) {
				Long ticketLimit = this.configurationReadPlatformService.retrieveGlobalConfiguration("ticketLimit").getValue().longValue();
				Long ticketNumber = this.chitGroupReadPlatformService.getSubsciberTicketCounts(clientId);
				if(ticketNumber>=ticketLimit) {
					throw new ChitSubscriberInvalidException(clientId,ticketLimit);
				}
			}

			ChitGroupSubscriber Subscriber = ChitGroupSubscriber.create(command,ChitId);
			ChitGroupSubscriber newchitBid = this.chitSubscriberRepository.save(Subscriber);

			
			
			CommandProcessingResult result = new CommandProcessingResultBuilder() //
					.withCommandId(ChitId) //
					.withEntityId(newchitBid.getId()) //
					.build();
			return result;

		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitGroupDataIntegrityIssues(null, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitGroupDataIntegrityIssues(null, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateChitgroupSubscriber(Long id, JsonObject command) {
		try {

			//this.fromApiJsonDeserializer.validateForUpdate(command.json(), id);
			final ChitGroupSubscriber chitgroupsubscriberforupdate = this.chitSubscriberRepository.findById(id)
					.orElseThrow(() -> new ChitBidNotFoundException(id));
			final Map<String, Object> changesOnly = chitgroupsubscriberforupdate.update(command);

			if (!changesOnly.isEmpty()) {
				this.chitSubscriberRepository.saveAndFlush(chitgroupsubscriberforupdate);
			}

			return new CommandProcessingResultBuilder().withCommandId(id).withEntityId(id)
					.with(changesOnly).build();
		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitGroupDataIntegrityIssues(null, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitGroupDataIntegrityIssues(null, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}


	@Transactional
	public void chitActivation(Long ChitId,JsonElement adjustmentData)
	{

		Long clientId;
		Long SubscriberId;
		Long staffId;

		BigDecimal remainingAdvance = BigDecimal.ZERO;
		int enrFee;


		Collection<ChitGroupSubscriberData> ChitSubscriber = chitGroupReadPlatformService.getChitSubscribers(ChitId);
		List<ChitGroupSubscriberData> ListofChitSubscirber = new ArrayList<ChitGroupSubscriberData>(ChitSubscriber);
		ChitGroupData chitGroup = chitGroupReadPlatformService.retrieveChitGroup(ChitId);
		GlobalConfigurationPropertyData configureCgstData = configurationReadPlatformService.retrieveGlobalConfiguration("CGST");
		GlobalConfigurationPropertyData configureSgstData = configurationReadPlatformService.retrieveGlobalConfiguration("SGST");
		//	
		//System.out.println("ListofChitSubscirber.size() "+ListofChitSubscirber.size());
		for(int i = 0 ; i < ListofChitSubscirber.size() ; i++)
		{
			BigDecimal totalAdvance = BigDecimal.ZERO;

			Object obj = ListofChitSubscirber.get(i);
			if(obj instanceof ChitGroupSubscriberData)
			{
				int chitnumber = ((ChitGroupSubscriberData) obj).getChitNumber();
				//System.out.println(chitnumber+" chitnumber");

				if(chitnumber!=1)
				{

					//System.out.println("inside if ");
					clientId = ((ChitGroupSubscriberData) obj).getClientId();
					SubscriberId = ((ChitGroupSubscriberData) obj).getId();
					Long chitId = ((ChitGroupSubscriberData) obj).getChitId();
					//adjusting amount for chitTransaction
					JsonObject datatobeparsed = adjustmentData.getAsJsonObject();
					JsonArray amountToBeAdjusteds = datatobeparsed.get("adjustAmount").getAsJsonArray();

					for(JsonElement amountToBeAdjusted: amountToBeAdjusteds)
					{
						Boolean validateChitSubsId = false;

						JsonObject jsonobject = amountToBeAdjusted.getAsJsonObject();
						if(jsonobject.get("subsId")!=null && !jsonobject.get("subsId").isJsonNull())
						{
							Long subsId = jsonobject.get("subsId").getAsLong();
							if(subsId.equals(SubscriberId))
							{
								validateChitSubsId = true;
							}
						}
						if(validateChitSubsId)
						{
							Double amount = null;
							if(jsonobject.get("amount")!=null && !jsonobject.get("amount").isJsonNull())
							{
								amount = jsonobject.get("amount").getAsDouble();
								totalAdvance = BigDecimal.valueOf(amount);
							}
							
							clientTransactionChitAdvanceAdjustment.ClientTransactionAdjustment(amount, clientId);	
						}
					}
					ClientData clientObject = clientReadPlatformService.retrieveOne(clientId);
					final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
					staffId = clientObject.getStaffId();
					//System.out.println(staffId+"staffId");
					Long cyclecount = 1l;
					ChitCycleData datafromchitcycle = chitCycleReadPlatformService.retrieveAll(chitId, cyclecount); //TODO hashmap convertion
					Long cycleId = datafromchitcycle.getId();
					//System.out.println("totalAdvance "+totalAdvance);
					remainingAdvance = totalAdvance;
					enrFee = chitGroup.getChitvalue() < 100000 ? 100 : (chitGroup.getChitvalue() >= 100000 && chitGroup.getChitvalue() < 200000 ? 200 : (chitGroup.getChitvalue() >= 200000 && chitGroup.getChitvalue() < 300000 ? 400 : 600));
					//System.out.println("enrFee "+enrFee);
					JsonObject dataToCreateChitSubsCharge = new JsonObject();
					dataToCreateChitSubsCharge.addProperty("subscriberId", SubscriberId);
					dataToCreateChitSubsCharge.addProperty("chitCycleId", cycleId);
					ChitChargeData chitchargeId = chitChargeReadPlatformServices.retrieveIdByName("ENROLLMENT_FEE");
					dataToCreateChitSubsCharge.addProperty("chitChargeId", chitchargeId.getId());
					dataToCreateChitSubsCharge.addProperty("amount", enrFee);
					dataToCreateChitSubsCharge.addProperty("ispaid", true);
					CommandProcessingResult Id = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharge);
					Long chitsubsId = Id.commandId();
					//System.out.println(Id+" Ids");
					//System.out.println(chitsubsId+" chitsubsId");
					JsonObject dataToCreateChitTransaction = new JsonObject();
					dataToCreateChitTransaction.addProperty("chitsubscriberchargeId",chitsubsId);
					dataToCreateChitTransaction.addProperty("amount", enrFee);
					dataToCreateChitTransaction.addProperty("trantype", "CHARGES"); 
					dataToCreateChitTransaction.addProperty("paymentdetailId", 0l);
					dataToCreateChitTransaction.addProperty("isprocessed", true);
					CommandProcessingResult chitSubTranData = this.chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataToCreateChitTransaction);
					Long chitSubTranId =  chitSubTranData.commandId();
					//System.out.println("chitSubTranId " +chitSubTranId);
					//TODO Create Ledger entries

					String currencyCode = "INR";
					LocalDate transactionDate = chitGroup.getStartdate();
					BigDecimal amountPaid = new BigDecimal(enrFee);
					//GST
					Double cgstAmount = configureCgstData.getValue().doubleValue();
					Double cgstValue = cgstAmount/100;
					Double cgstDeductAmount = amountPaid.doubleValue() * cgstValue;
					BigDecimal deductedAmountforCGST = new BigDecimal(cgstDeductAmount);
					BigDecimal cgstpenaltyValue = amountPaid.subtract(deductedAmountforCGST);

					// SGST for Penalty Value
					Double sgstAmount = configureSgstData.getValue().doubleValue();
					Double sgstValue = sgstAmount/100;
					Double sgstDeductAmount = amountPaid.doubleValue() * sgstValue;
					BigDecimal deductedAmountforSGST = new BigDecimal(sgstDeductAmount);
					BigDecimal sgstpenaltyValue = amountPaid.subtract(deductedAmountforSGST);
					BigDecimal addValue =  deductedAmountforCGST.add(deductedAmountforSGST);
					BigDecimal amountValue = amountPaid.subtract(addValue);
					this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(), clientId, currencyCode, null, chitSubTranId, transactionDate, amountValue,chitSubTranId, "JV-",CashAccountsForChit.CHIT_ENROLLMENT_FEE,null,chitId);

					this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientId, currencyCode,null, chitSubTranId, transactionDate,deductedAmountforCGST,  null, "GT-",CashAccountsForChit.CHIT_CGST,null,chitId);
					this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientId, currencyCode, null,chitSubTranId, transactionDate, deductedAmountforSGST, null, "GT-",CashAccountsForChit.CHIT_SGST,null,chitId);

					this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(), clientId, currencyCode, null, chitSubTranId, transactionDate, amountPaid,chitSubTranId, "JV-",CashAccountsForChit.CHIT_SUBSCRIPTION_RECEIVE_ADVANCE);


					if(remainingAdvance.compareTo(BigDecimal.valueOf(enrFee))>=0)
					{
						//System.out.println("inside remainingAdvance.compareTo(BigDecimal.valueOf(enrFee))>0 "+remainingAdvance);
						remainingAdvance = remainingAdvance.subtract(BigDecimal.valueOf(enrFee));
						//System.out.println("remainingAdvance.subtract(BigDecimal.valueOf(enrFee)) "+remainingAdvance);
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(), clientId, currencyCode, null, chitSubTranId, transactionDate, remainingAdvance,chitSubTranId, "JV-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,null,chitId);
						this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(), clientId, currencyCode, null, chitSubTranId, transactionDate, remainingAdvance,chitSubTranId, "JV-",CashAccountsForChit.CHIT_SUBSCRIPTION_RECEIVE_ADVANCE);
					}
					else
					{

						remainingAdvance = BigDecimal.ZERO;

					}

					JsonObject dataToCreateChitSubsCharges = new JsonObject();

					//creating data for chitsubscribercharge
					dataToCreateChitSubsCharges.addProperty("subscriberId", SubscriberId);
					dataToCreateChitSubsCharges.addProperty("chitCycleId", cycleId);
					ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
					dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
					dataToCreateChitSubsCharges.addProperty("amount", chitGroup.getMonthlycontribution());
					CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);
					//System.out.println(Ids+" Ids");
					Long tempChitSubscriberChargeId = Ids.commandId();
					//System.out.println(tempChitSubscriberChargeId+" tempChitSubscriberChargeId");
					//getting working and non-working days
					LocalDate chitStartDate = chitGroup.getStartdate();
					List<LocalDate> DemandDates = findWorkingDays.findWorkingDays(chitStartDate, true);
					List<LocalDate> DaysTill25 = new ArrayList<LocalDate>();
					List<LocalDate> DaysAfter25 = new ArrayList<LocalDate>();
					for(int k = 0 ; k < DemandDates.size() ; k++)
					{
						LocalDate datesobj = DemandDates.get(k);
						if(datesobj.getDayOfMonth()<=25)
						{
							DaysTill25.add(datesobj);
						}
						else
						{
							DaysAfter25.add(datesobj);
						}
					}

					if(remainingAdvance.compareTo(BigDecimal.valueOf(chitGroup.getMonthlycontribution()))<=0)
					{
						//System.out.println("inside remainingAdvance.compareTo(BigDecimal.valueOf(chitGroup.getMonthlycontribution()))<0 "+remainingAdvance);
						int start = 0;
						Boolean isCal = false;
						Double remanign = remainingAdvance.doubleValue();
						Double montlyContribution = chitGroup.getMonthlycontribution().doubleValue();
						if(montlyContribution.compareTo(remanign)==0)
						{
							isCal = true;
						}
						if(!(remainingAdvance.compareTo(BigDecimal.ZERO)==0))
						{

							JsonObject dataForChitDemand =  new JsonObject();
							dataForChitDemand.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
							dataForChitDemand.addProperty("staffId", staffId);

							LocalDate demandstartdate = chitStartDate.withDayOfMonth(1);
							dataForChitDemand.addProperty("demandDate", demandstartdate.toString());
							dataForChitDemand.addProperty("installmentAmount", remainingAdvance);
							dataForChitDemand.addProperty("dueAmount", 0.0);
							dataForChitDemand.addProperty("overdueAmount", 0.0);
							dataForChitDemand.addProperty("penaltyAmount", 0.0);
							dataForChitDemand.addProperty("collectedAmount", remainingAdvance);
							dataForChitDemand.addProperty("isCalculated", true);
							CommandProcessingResult result = chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemand);

							JsonObject dataForChitsubsTran =  new JsonObject();
							dataForChitsubsTran.addProperty("chitdemandscheduleId", result.commandId());
							dataForChitsubsTran.addProperty("amount", remainingAdvance);
							dataForChitsubsTran.addProperty("trantype", "INSTALLMENT_EMI");
							dataForChitsubsTran.addProperty("isprocessed", true);
							this.chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataForChitsubsTran);
							//TODO ledger Entries
							start = 1;
						}

						final GlobalConfigurationPropertyData Configuredscore = this.configurationReadPlatformService
								.retrieveGlobalConfiguration("DemandAmountRoundOffValue");
						Long nearestRoundTo = Configuredscore.getValue();

						Long normalEmiPerDay = chitGroup.getMonthlycontribution()/(DaysTill25.size()-start);

						Double perDayInstallmentAdjusted = Math.ceil(normalEmiPerDay/(nearestRoundTo*1.0)) * nearestRoundTo ;

						Double remainingInstallment = chitGroup.getMonthlycontribution() - remainingAdvance.doubleValue();

						for(int k = start ; k < DaysTill25.size() ; k++)
						{

							LocalDate demandDate = DaysTill25.get(k);

							if(remainingInstallment<=perDayInstallmentAdjusted)
							{
								//System.out.println("inside remainingAdvance.compareTo(BigDecimal.valueOf(perDayInstallmentAdjusted)<=0 "+remainingAdvance);
								JsonObject dataForChitDemands =  new JsonObject();
								dataForChitDemands.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
								dataForChitDemands.addProperty("staffId", staffId);
								dataForChitDemands.addProperty("demandDate", demandDate.toString());
								dataForChitDemands.addProperty("installmentAmount", remainingInstallment);
								dataForChitDemands.addProperty("dueAmount", 0.0);
								dataForChitDemands.addProperty("overdueAmount", 0.0);
								dataForChitDemands.addProperty("penaltyAmount", 0.0);
								dataForChitDemands.addProperty("collectedAmount", 0.0);
								dataForChitDemands.addProperty("isCalculated", isCal);
								chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
								remainingInstallment = 0.0;
							}
							else
							{
								JsonObject dataForChitDemands =  new JsonObject();
								dataForChitDemands.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
								dataForChitDemands.addProperty("staffId", staffId);
								dataForChitDemands.addProperty("demandDate", demandDate.toString());
								dataForChitDemands.addProperty("installmentAmount", perDayInstallmentAdjusted);
								dataForChitDemands.addProperty("dueAmount", 0.0);
								dataForChitDemands.addProperty("overdueAmount", 0.0);
								dataForChitDemands.addProperty("penaltyAmount", 0.0);
								dataForChitDemands.addProperty("collectedAmount", 0.0);
								dataForChitDemands.addProperty("isCalculated", isCal);
								chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
								remainingInstallment = remainingInstallment - perDayInstallmentAdjusted;
								//System.out.println("inside else of remainingAdvance.compareTo(BigDecimal.valueOf(perDayInstallmentAdjusted)<=0 "+remainingAdvance);

							}
						}

						for(int j = 0 ; j < DaysAfter25.size() ; j++)
						{
							LocalDate datefordemand = DaysAfter25.get(j);
							JsonObject dataForChitDemandss =  new JsonObject();
							dataForChitDemandss.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
							dataForChitDemandss.addProperty("staffId", staffId);
							dataForChitDemandss.addProperty("demandDate", datefordemand.toString());
							dataForChitDemandss.addProperty("installmentAmount", 0.0);
							dataForChitDemandss.addProperty("dueAmount", 0.0);
							dataForChitDemandss.addProperty("overdueAmount", 0.0);
							dataForChitDemandss.addProperty("penaltyAmount", 0.0);
							dataForChitDemandss.addProperty("collectedAmount", 0.0);
							dataForChitDemandss.addProperty("isCalculated", isCal);
							chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemandss);	 
						}

						remainingAdvance = BigDecimal.ZERO;
					}
					else
					{
						LocalDate datefordemand = chitGroup.getStartdate();
						LocalDate date = datefordemand.withDayOfMonth(1);
						JsonObject dataForChitDemandss =  new JsonObject();
						dataForChitDemandss.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
						dataForChitDemandss.addProperty("staffId", staffId);
						dataForChitDemandss.addProperty("demandDate", date.toString());
						dataForChitDemandss.addProperty("installmentAmount", chitGroup.getMonthlycontribution());
						dataForChitDemandss.addProperty("dueAmount", 0.0);
						dataForChitDemandss.addProperty("overdueAmount", 0.0);
						dataForChitDemandss.addProperty("penaltyAmount", 0.0);
						dataForChitDemandss.addProperty("collectedAmount", chitGroup.getMonthlycontribution());
						dataForChitDemandss.addProperty("isCalculated", true);
						CommandProcessingResult results = chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemandss);
						JsonObject dataForChitsubsTran =  new JsonObject();
						dataForChitsubsTran.addProperty("chitdemandscheduleId", results.commandId());
						dataForChitsubsTran.addProperty("amount", chitGroup.getMonthlycontribution());
						dataForChitsubsTran.addProperty("trantype", "INSTALLMENT_EMI");
						dataForChitsubsTran.addProperty("isprocessed", true);
						//TODO ledger entries
						this.chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataForChitsubsTran);
						remainingAdvance = remainingAdvance.subtract(BigDecimal.valueOf(chitGroup.getMonthlycontribution())) ;
						//System.out.println("main if else "+remainingAdvance);
					}

					LocalDate temp = chitGroup.getStartdate();
					while(remainingAdvance.compareTo(BigDecimal.ZERO)>0)
					{
						cyclecount=cyclecount+1l;
						//System.out.println("cyclecount "+cyclecount);
						JsonObject data = new JsonObject();
						ChitCycleData datafromchitcycle1 = chitCycleReadPlatformService.retrieveAll(chitId, cyclecount);
						Long cycleId1 = datafromchitcycle1.getId();

						temp = datafromchitcycle1.getAuctiondate().withDayOfMonth(1);
						LocalDate nextDay = findWorkingDays.validateworkingDayorNot(temp);
						data.addProperty("subscriberId", SubscriberId);
						data.addProperty("chitCycleId", cycleId1);
						ChitChargeData chitchargeIdss = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
						data.addProperty("chitChargeId", chitchargeIdss.getId());
						data.addProperty("amount", chitGroup.getMonthlycontribution());
						CommandProcessingResult Idss = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(data);
						Long tempChitSubscriberChargeIds = Idss.commandId();

						BigDecimal amount4ThisMonth = remainingAdvance.compareTo(BigDecimal.valueOf(chitGroup.getMonthlycontribution()))<0  ?  remainingAdvance : BigDecimal.valueOf(chitGroup.getMonthlycontribution());

						JsonObject dataForChitDemandss =  new JsonObject();
						dataForChitDemandss.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeIds);
						dataForChitDemandss.addProperty("staffId", staffId);
						dataForChitDemandss.addProperty("demandDate", nextDay.toString());
						dataForChitDemandss.addProperty("installmentAmount", amount4ThisMonth);
						dataForChitDemandss.addProperty("dueAmount", 0.0);
						dataForChitDemandss.addProperty("overdueAmount", 0.0);
						dataForChitDemandss.addProperty("penaltyAmount", 0.0);
						dataForChitDemandss.addProperty("collectedAmount", amount4ThisMonth);
						dataForChitDemandss.addProperty("isCalculated", true);
						CommandProcessingResult results = chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemandss);
						JsonObject dataForChitsubsTran =  new JsonObject();
						dataForChitsubsTran.addProperty("chitdemandscheduleId", results.commandId());
						dataForChitsubsTran.addProperty("amount", amount4ThisMonth);
						dataForChitsubsTran.addProperty("trantype", "INSTALLMENT_EMI");
						dataForChitsubsTran.addProperty("isprocessed", true);
						this.chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataForChitsubsTran);
						//TODO ledger entries
						if (remainingAdvance.compareTo(BigDecimal.valueOf(chitGroup.getMonthlycontribution()))>0)
							remainingAdvance = remainingAdvance.subtract(BigDecimal.valueOf(chitGroup.getMonthlycontribution()));
						else 
							remainingAdvance = BigDecimal.ZERO;
					}
				}
				else
				{
					LocalDate transactionDate = chitGroup.getStartdate();
					clientId = ((ChitGroupSubscriberData) obj).getClientId();
					SubscriberId = ((ChitGroupSubscriberData) obj).getId();
					Long chitId = ((ChitGroupSubscriberData) obj).getChitId();
					remainingAdvance = BigDecimal.valueOf(chitGroup.getMonthlycontribution());
					ClientData clientObject = clientReadPlatformService.retrieveOne(clientId);
					final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
					staffId = clientObject.getStaffId();
					JsonObject dataToCreateChitSubsCharges = new JsonObject();
					Long cyclecount = 1l;
					ChitCycleData datafromchitcycle = chitCycleReadPlatformService.retrieveAll(chitId, cyclecount); //TODO hashmap convertion
					Long cycleId = datafromchitcycle.getId();
					//creating data for chitsubscribercharge
					dataToCreateChitSubsCharges.addProperty("subscriberId", SubscriberId);
					dataToCreateChitSubsCharges.addProperty("chitCycleId", cycleId);
					ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
					dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
					dataToCreateChitSubsCharges.addProperty("amount", chitGroup.getMonthlycontribution());
					CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);
					//System.out.println(Ids+" Ids");
					Long tempChitSubscriberChargeId = Ids.commandId();
					//System.out.println(tempChitSubscriberChargeId+" tempChitSubscriberChargeId");
					//getting working and non-working days
					LocalDate chitStartDate = chitGroup.getStartdate();
					List<LocalDate> DemandDates = findWorkingDays.findWorkingDays(chitStartDate, true);
					List<LocalDate> DaysTill25 = new ArrayList<LocalDate>();
					List<LocalDate> DaysAfter25 = new ArrayList<LocalDate>();
					for(int k = 0 ; k < DemandDates.size() ; k++)
					{
						LocalDate datesobj = DemandDates.get(k);
						if(datesobj.getDayOfMonth()<=25)
						{
							DaysTill25.add(datesobj);
						}
						else
						{
							DaysAfter25.add(datesobj);
						}
					}

					if(remainingAdvance.compareTo(BigDecimal.valueOf(chitGroup.getMonthlycontribution()))<=0)
					{
						//System.out.println("inside remainingAdvance.compareTo(BigDecimal.valueOf(chitGroup.getMonthlycontribution()))<0 "+remainingAdvance);
						int start = 0;
						Boolean isCal = false;
						Double remanign = remainingAdvance.doubleValue();
						Double montlyContribution = chitGroup.getMonthlycontribution().doubleValue();
						if(montlyContribution.compareTo(remanign)==0)
						{
							isCal = true;
						}
						if(!(remainingAdvance.compareTo(BigDecimal.ZERO)==0))
						{

							JsonObject dataForChitDemand =  new JsonObject();
							dataForChitDemand.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
							dataForChitDemand.addProperty("staffId", staffId);

							LocalDate demandstartdate = chitStartDate.withDayOfMonth(1);
							dataForChitDemand.addProperty("demandDate", demandstartdate.toString());
							dataForChitDemand.addProperty("installmentAmount", remainingAdvance);
							dataForChitDemand.addProperty("dueAmount", 0.0);
							dataForChitDemand.addProperty("overdueAmount", 0.0);
							dataForChitDemand.addProperty("penaltyAmount", 0.0);
							dataForChitDemand.addProperty("collectedAmount", remainingAdvance);
							dataForChitDemand.addProperty("isCalculated", true);
							CommandProcessingResult result = chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemand);

							JsonObject dataForChitsubsTran =  new JsonObject();
							dataForChitsubsTran.addProperty("chitdemandscheduleId", result.commandId());
							dataForChitsubsTran.addProperty("amount", remainingAdvance);
							dataForChitsubsTran.addProperty("trantype", "INSTALLMENT_EMI");
							dataForChitsubsTran.addProperty("isprocessed", true);
							CommandProcessingResult chitSubTranId = this.chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataForChitsubsTran);
							String currencyCode ="INR";
							Long chitTran = chitSubTranId.commandId();
							this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(), clientId, currencyCode, null,chitTran, transactionDate, remainingAdvance,chitTran, "JV-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,null,chitId);
							this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(), clientId, currencyCode, null, chitTran, transactionDate, remainingAdvance,chitTran, "JV-",CashAccountsForChit.CHIT_FOREMAN);
							start = 1;
						}

						final GlobalConfigurationPropertyData Configuredscore = this.configurationReadPlatformService
								.retrieveGlobalConfiguration("DemandAmountRoundOffValue");
						Long nearestRoundTo = Configuredscore.getValue();

						Long normalEmiPerDay = chitGroup.getMonthlycontribution()/(DaysTill25.size()-start);

						Double perDayInstallmentAdjusted = Math.ceil(normalEmiPerDay/(nearestRoundTo*1.0)) * nearestRoundTo ;

						Double remainingInstallment = chitGroup.getMonthlycontribution() - remainingAdvance.doubleValue();

						for(int k = start ; k < DaysTill25.size() ; k++)
						{

							LocalDate demandDate = DaysTill25.get(k);

							if(remainingInstallment<=perDayInstallmentAdjusted)
							{
								//System.out.println("inside remainingAdvance.compareTo(BigDecimal.valueOf(perDayInstallmentAdjusted)<=0 "+remainingAdvance);
								JsonObject dataForChitDemands =  new JsonObject();
								dataForChitDemands.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
								dataForChitDemands.addProperty("staffId", staffId);
								dataForChitDemands.addProperty("demandDate", demandDate.toString());
								dataForChitDemands.addProperty("installmentAmount", remainingInstallment);
								dataForChitDemands.addProperty("dueAmount", 0.0);
								dataForChitDemands.addProperty("overdueAmount", 0.0);
								dataForChitDemands.addProperty("penaltyAmount", 0.0);
								dataForChitDemands.addProperty("collectedAmount", 0.0);
								dataForChitDemands.addProperty("isCalculated", isCal);
								chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
								remainingInstallment = 0.0;
							}
							else
							{
								JsonObject dataForChitDemands =  new JsonObject();
								dataForChitDemands.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
								dataForChitDemands.addProperty("staffId", staffId);
								dataForChitDemands.addProperty("demandDate", demandDate.toString());
								dataForChitDemands.addProperty("installmentAmount", perDayInstallmentAdjusted);
								dataForChitDemands.addProperty("dueAmount", 0.0);
								dataForChitDemands.addProperty("overdueAmount", 0.0);
								dataForChitDemands.addProperty("penaltyAmount", 0.0);
								dataForChitDemands.addProperty("collectedAmount", 0.0);
								dataForChitDemands.addProperty("isCalculated", isCal);
								chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
								remainingInstallment = remainingInstallment - perDayInstallmentAdjusted;
								//System.out.println("inside else of remainingAdvance.compareTo(BigDecimal.valueOf(perDayInstallmentAdjusted)<=0 "+remainingAdvance);

							}
						}

						for(int j = 0 ; j < DaysAfter25.size() ; j++)
						{
							LocalDate datefordemand = DaysAfter25.get(j);
							JsonObject dataForChitDemandss =  new JsonObject();
							dataForChitDemandss.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
							dataForChitDemandss.addProperty("staffId", staffId);
							dataForChitDemandss.addProperty("demandDate", datefordemand.toString());
							dataForChitDemandss.addProperty("installmentAmount", 0.0);
							dataForChitDemandss.addProperty("dueAmount", 0.0);
							dataForChitDemandss.addProperty("overdueAmount", 0.0);
							dataForChitDemandss.addProperty("penaltyAmount", 0.0);
							dataForChitDemandss.addProperty("collectedAmount", 0.0);
							dataForChitDemandss.addProperty("isCalculated", isCal);
							chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemandss);	 
						}

						remainingAdvance = BigDecimal.ZERO;
					}
					ChitCycleData cd  = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, 1l);
					// Bidding Process 
					Double bidAmt = chitGroup.getChitvalue().doubleValue();
					Long participantId = null;
					CodeData codevalues = codeReadPlatformService.retriveCode("BidderParticipation");
					Iterator<CodeValueData> itr = codeValueReadPlatformService.retrieveAllCodeValues(codevalues.getCodeId()).iterator();
					while(itr.hasNext())
					{
						CodeValueData itr1 = itr.next();
						if(itr1.getName().equalsIgnoreCase("Prize Bidder"))
						{
							participantId = itr1.getId();
						}
					}
					ChitBids cb = new ChitBids(SubscriberId,cd.getId(),bidAmt,true,participantId,transactionDate,false);
					chitBidsRepository.saveAndFlush(cb);

					//Ledger Posting
					//debit
					if(bidAmt!=null && bidAmt!=0)
					{
						Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_BIDS_AC.getValue().longValue());  
						Long plAccountId= null;
						for(Map.Entry<String, Object> itr1 : mapData3.entrySet())
						{
							if(itr1.getKey().equals("glAccountId"))
							{
								plAccountId = (Long)itr1.getValue();
							}
						}
						String currencyCode = "INR";
						Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(chitGroup.getOfficeId());
						this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,plAccountId,chitId, transactionDate, BigDecimal.valueOf(bidAmt) ,null,"DR-",SubscriberId);
					}
					//credit
					Map<String, Object> mapData5 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.OWN_CHITS.getValue().longValue());  
					Long plAccountId2= null;
					for(Map.Entry<String, Object> itr2 : mapData5.entrySet())
					{
						if(itr2.getKey().equals("glAccountId"))
						{
							plAccountId2 = (Long)itr2.getValue();
						}
					}
					Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(chitGroup.getOfficeId());
					String currencyCode = "INR";
					this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId2, chitId,transactionDate,BigDecimal.valueOf(bidAmt),null, "BV-",CashAccountsForChit.OWN_CHITS,SubscriberId,chitId);	
					
					
					Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_SUBSCRIPTION.getValue().longValue());  
					Long plAccountId1= null;
					for(Map.Entry<String, Object> itr1 : mapData4.entrySet())
					{
						if(itr1.getKey().equals("glAccountId"))
						{
							plAccountId1 = (Long)itr1.getValue();
						}
					}
					
					this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,plAccountId1,chitId, transactionDate, BigDecimal.valueOf(chitGroup.getChitvalue()) ,null,"DR-",SubscriberId);
					
					Map<String, Object> mapData51 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  
					Long plAccountId21= null;
					for(Map.Entry<String, Object> itr1 : mapData51.entrySet())
					{
						if(itr1.getKey().equals("glAccountId"))
						{
							plAccountId21 = (Long)itr1.getValue();
						}
					}
					this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId21,chitId,transactionDate,BigDecimal.valueOf(chitGroup.getChitvalue()),null, "BV-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,SubscriberId,chitId);
				}
			}
		}
	}


	@Override
	@Transactional
	public CommandProcessingResult firstAuctionToCompany(Long id)
	{
		try
		{
			JsonObject command2 = new JsonObject();
			JsonObject command = new JsonObject();
			ChitGroupData data = chitGroupReadPlatformService.retrieveChitGroup(id);
			ChitGroupSubscriberData chitsData = chitGroupReadPlatformService.getChitSubscriberWithClient(data.getId(), 1l);

			//System.out.println("chitsdata "+chitsData.getId());
			if(chitsData.getChitNumber() == 1)
			{
				ClientData  clientData = clientReadPlatformService.retrieveOne(chitsData.getClientId());
			}

			Boolean flag = false;
			if(chitsData.getPrizedsubscriber()!=null)
			{
				flag = chitsData.getPrizedsubscriber();
			}

			if(data.getCurrentcycle() != 1 || flag)
			{
				throw new ChitGroupCycleException(data.getCurrentcycle());
			}

			else {
				command.addProperty("amount", data.getChitvalue());
				command.addProperty("chitsubscriberId", chitsData.getId());
				command.addProperty("trantype", "WINNERPRIZEMONEY");
				command.addProperty("paymentdetailId", 2);
				command.addProperty("isprocessed", 1);
				chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(command);

				command2.addProperty("prizedsubscriber", true);
				command2.addProperty("prizedcycle", 1);
				this.updateChitgroupSubscriber(chitsData.getId(),command2);
			}

			return new CommandProcessingResultBuilder().withCommandId(id).withEntityId(id)
					.build();

		}catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitGroupDataIntegrityIssues(null, throwable, dve);
			return CommandProcessingResult.empty();
		}

	}


	@Transactional
	@Override
	public void move(Long chitId)
	{

		if(chitId==null)
		{
			throw new  ChitGroupNotFoundException(chitId);
		}

		//get chitGroup
		ChitGroupData chitGroup = chitGroupReadPlatformService.retrieveChitGroup(chitId);
		//System.out.println("chit grp cycle 1"+chitGroup.getCurrentcycle());
		Boolean ChitGroupNotLastCheck = false;
		if(chitGroup.getCurrentcycle()<chitGroup.getChitduration())
		{
			CheckForPenalties(chitId);
			checkForLedger(chitId);
			Long CycleValue = chitGroup.getCurrentcycle();
			if(chitGroup.getCurrentcycle()!=1l)
			{
				PrizeMoneyPosting(chitId,CycleValue);
			}
			Long cyclecount = CycleValue+1;
			ChitCycleData chitcycledata = chitCycleReadPlatformService.retrieveAll(chitId, cyclecount);
			//System.out.println("chitcycledata.getAuctiondate() "+chitcycledata.getAuctiondate());
			LocalDate nextAuctionTime = chitcycledata.getAuctiondate();
			JsonObject dataforupdatechitcycle = new JsonObject();
			dataforupdatechitcycle.addProperty("currentcycle", cyclecount);
			dataforupdatechitcycle.addProperty("nextauctiondate", nextAuctionTime+"");
			dataforupdatechitcycle.addProperty("locale", "en");
			dataforupdatechitcycle.addProperty("dateFormat", "yyyy-MM-dd");
			this.updateChitGroupwithJSon(chitId, dataforupdatechitcycle);
			ChitGroupNotLastCheck = true;

		}

		if(ChitGroupNotLastCheck)
		{
			//System.out.println("Step-1");
			Collection<ChitGroupSubscriberData> chitSubscribers = chitGroupReadPlatformService.getChitSubscribers(chitId);
			List<ChitGroupSubscriberData> ListofchitSubsribers = new ArrayList<ChitGroupSubscriberData>(chitSubscribers);
			ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
			Long chitChargeID = chitchargeIds.getId();

			for(int i = 0 ; i < ListofchitSubsribers.size() ; i++)
			{
				//System.out.println("Step-2");
				Object obj = ListofchitSubsribers.get(i);
				if(obj instanceof ChitGroupSubscriberData)
				{
					//System.out.println("Step-3");
					if(((ChitGroupSubscriberData) obj).getChitNumber()!=1)
					{

						Long clientId = ((ChitGroupSubscriberData) obj).getClientId();
						Long CycleValue = chitGroup.getCurrentcycle();
						Long cyclecount = CycleValue+1;
						ChitCycleData chitcycledata = chitCycleReadPlatformService.retrieveAll(chitId, cyclecount);
						ChitCycleData chitcycledata1 = chitCycleReadPlatformService.retrieveAll(chitId, CycleValue);			
						LocalDate nextAuctionTime = chitcycledata.getAuctiondate().withDayOfMonth(1);
						LocalDate demandDate = findWorkingDays.nextWorkingDayofNextMonth(nextAuctionTime);	
						ClientData client = this.clientReadPlatformService.retrieveOne(clientId);
						Long staffId = client.getStaffId();
						Long chitsubsccID = ((ChitGroupSubscriberData) obj).getId();
						ChitSubscriberChargeData chitsubsID = this.chitSubscriberChargeReadPlatformServices.retrieveById(chitsubsccID, chitChargeID, chitcycledata.getId());
						LocalDate demandstartdate = demandDate.withDayOfMonth(1);
						System.out.println("demandstartdate "+demandstartdate);
						List<LocalDate> DemandDates = findWorkingDays.findWorkingDays(demandstartdate, true);
						List<LocalDate> DaysTill25 = new ArrayList<LocalDate>();
						List<LocalDate> DaysAfter25 = new ArrayList<LocalDate>();


						if(chitsubsID!=null)
						{

							for(int k = 0 ; k < DemandDates.size() ; k++)
							{

								LocalDate datesobj = DemandDates.get(k);
								if(datesobj.getDayOfMonth()<=25)
								{
									DaysTill25.add(datesobj);
								}
								else
								{
									DaysAfter25.add(datesobj);
								}
							}

							ChitDemandScheduleData DemandScheduleData = this.chitDemandScheduleReadPlatformService.retrieveByIdAndDate(chitsubsID.getId(), staffId, DemandDates.get(0));

							//this conditions becomes true if and only if there are any advance payments made or if there are any overdues
							if(DemandScheduleData!=null)
							{
								//System.out.println("step =7 ");
								//getting monthly contribution
								Long dividend = 0l;
								if(chitcycledata1.getDividend()!=null && chitcycledata1.getDividend()!=0 )
								{
									dividend = chitcycledata1.getDividend();
									//System.out.println("inside demandshedule! NULL  "+dividend);
									Long dividendValueForIndividual =  dividend/chitGroup.getChitduration();

									Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_DIVIDEND_PAYBLE.getValue().longValue());  
									Long plAccountId= null;
									for(Map.Entry<String, Object> itr : mapData3.entrySet())
									{
										if(itr.getKey().equals("glAccountId"))
										{
											plAccountId = (Long)itr.getValue();
										}
									}
									String currencyCode = "INR";
									Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(chitGroup.getOfficeId());
									//this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, clientId, currencyCode,plAccountId,clientId, demandstartdate, BigDecimal.valueOf(dividendValueForIndividual) ,null,"DR-",chitsubsccID);

									Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  
									Long plAccountId1= null;
									for(Map.Entry<String, Object> itr : mapData4.entrySet())
									{
										if(itr.getKey().equals("glAccountId"))
										{
											plAccountId1 = (Long)itr.getValue();
										}
									}
									//this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId1, chitId, demandstartdate,BigDecimal.valueOf(dividendValueForIndividual),null, "OS-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,chitsubsccID);


								}
								Long monthlycontribution = (chitGroup.getMonthlycontribution() - (dividend/chitGroup.getChitduration()));

								Double CollectedAmount = 0.0;
								Boolean isMoreAmountLeft = false;

								if( DemandScheduleData.getCollectedAmount()!=null)
								{
									CollectedAmount = DemandScheduleData.getCollectedAmount();
									if(CollectedAmount>monthlycontribution)
									{
										CollectedAmount = DemandScheduleData.getCollectedAmount()-monthlycontribution;
										isMoreAmountLeft = true;
									}
								}

								//if advance amount is more than the monthly contribution then we move the remaining amount to next cycle 
								if(isMoreAmountLeft)
								{
									//System.out.println("is more amount left  "+isMoreAmountLeft);
									cyclecount = CycleValue+2;
									ChitCycleData tempchitcycledata = chitCycleReadPlatformService.retrieveAll(chitId, cyclecount);
									LocalDate tempnextAuctionTime = tempchitcycledata.getAuctiondate().withDayOfMonth(1);
									LocalDate tempdemandDate = findWorkingDays.nextWorkingDayofNextMonth(tempnextAuctionTime);

									//creating data for chitsubscribercharge
									JsonObject dataToCreateChitSubsCharges = new JsonObject();
									dataToCreateChitSubsCharges.addProperty("subscriberId", ((ChitGroupSubscriberData) obj).getId());
									dataToCreateChitSubsCharges.addProperty("chitCycleId", tempchitcycledata.getId());

									dataToCreateChitSubsCharges.addProperty("chitChargeId", chitChargeID);
									dataToCreateChitSubsCharges.addProperty("amount", chitGroup.getMonthlycontribution());	
									CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);

									JsonObject dataForChitDemands1 =  new JsonObject();
									dataForChitDemands1.addProperty("chitsubscriberchargeId", Ids.commandId());
									dataForChitDemands1.addProperty("staffId", staffId);
									dataForChitDemands1.addProperty("demandDate", tempdemandDate.toString());
									dataForChitDemands1.addProperty("installmentAmount", CollectedAmount);
									dataForChitDemands1.addProperty("dueAmount", 0.0);
									dataForChitDemands1.addProperty("overdueAmount", 0.0);
									dataForChitDemands1.addProperty("penaltyAmount",0.0);
									dataForChitDemands1.addProperty("collectedAmount", CollectedAmount);
									dataForChitDemands1.addProperty("isCalculated", true);
									CommandProcessingResult id = chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands1);
									JsonObject dataForChitsubsTran =  new JsonObject();
									dataForChitsubsTran.addProperty("chitdemandscheduleId", id.commandId());
									dataForChitsubsTran.addProperty("amount", CollectedAmount);
									dataForChitsubsTran.addProperty("trantype", "INSTALLMENT_EMI");
									dataForChitsubsTran.addProperty("isprocessed", true);
									this.chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataForChitsubsTran);	
									//TODO ledger entries
								}


								//if there is advance amount and if advance Amount is less than or equal to monthly contribution we create demandd schedule 
								Boolean isCal = false;
								Double remanign = DemandScheduleData.getCollectedAmount();
								Double montlyContribution = (chitGroup.getMonthlycontribution().doubleValue() - (dividend/chitGroup.getChitduration()));
								if(montlyContribution.compareTo(remanign)==0)
								{
									isCal = true;
								}
								
								if(DemandScheduleData.getCollectedAmount()!=null && DemandScheduleData.getCollectedAmount()>0.0)
								{
									System.out.println("step = 8  ");

									Double remainingAdvance = monthlycontribution-DemandScheduleData.getCollectedAmount();

									if(remainingAdvance<=monthlycontribution)
									{
										System.out.println("step = 9 ");
										final GlobalConfigurationPropertyData Configuredscore = this.configurationReadPlatformService
												.retrieveGlobalConfiguration("DemandAmountRoundOffValue");
										Long nearestRoundTo = Configuredscore.getValue();
										Integer noOfDays = DaysTill25.size()-1;
										Double normalEmiPerDay = (chitGroup.getMonthlycontribution().doubleValue() - (dividend/chitGroup.getChitduration()))/noOfDays.doubleValue();

										System.out.println(chitGroup.getMonthlycontribution() +" chitGroup.getMonthlycontribution()");
										System.out.println(normalEmiPerDay +" normalEmiPerDay");
										System.out.println(dividend +" dividend");
										System.out.println(chitGroup.getChitduration() +" chitGroup.getChitduration()");
										System.out.println(DaysTill25.size()-1 +" DaysTill25");
										System.out.println("nearestRoundTo "+nearestRoundTo);

										Double perDayInstallmentAdjusted = Math.ceil(normalEmiPerDay/(nearestRoundTo*1.0)) * nearestRoundTo ;

										Long tempId = DemandScheduleData.getChitSubscriberChargeId();


										for(int k = 1 ; k < DaysTill25.size() ; k++)
										{
											//System.out.println("Step-10");
											LocalDate demandDates = DaysTill25.get(k);
											System.out.println("remainingAdvance "+remainingAdvance);
											System.out.println("perDayInstallmentAdjusted "+perDayInstallmentAdjusted);
											if(remainingAdvance<=perDayInstallmentAdjusted)
											{

												JsonObject dataForChitDemands =  new JsonObject();
												dataForChitDemands.addProperty("chitsubscriberchargeId", tempId);
												dataForChitDemands.addProperty("staffId", staffId);
												dataForChitDemands.addProperty("demandDate", demandDates.toString());
												dataForChitDemands.addProperty("installmentAmount", remainingAdvance);
												dataForChitDemands.addProperty("dueAmount", 0.0);
												dataForChitDemands.addProperty("overdueAmount", 0.0);
												dataForChitDemands.addProperty("penaltyAmount", 0.0);
												dataForChitDemands.addProperty("collectedAmount", 0.0);
												dataForChitDemands.addProperty("isCalculated", isCal);
												chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
												remainingAdvance = 0.0;
											}
											else
											{
												JsonObject dataForChitDemands =  new JsonObject();
												dataForChitDemands.addProperty("chitsubscriberchargeId", tempId);
												dataForChitDemands.addProperty("staffId", staffId);
												dataForChitDemands.addProperty("demandDate", demandDates.toString());
												dataForChitDemands.addProperty("installmentAmount", perDayInstallmentAdjusted);
												dataForChitDemands.addProperty("dueAmount", 0.0);
												dataForChitDemands.addProperty("overdueAmount", 0.0);
												dataForChitDemands.addProperty("penaltyAmount", 0.0);
												dataForChitDemands.addProperty("collectedAmount", 0.0);
												dataForChitDemands.addProperty("isCalculated", isCal);
												chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
												remainingAdvance = remainingAdvance - perDayInstallmentAdjusted;


											}
										}

										for(int j = 0 ; j < DaysAfter25.size() ; j++)
										{
											//System.out.println("Step-11");
											LocalDate datefordemand = DaysAfter25.get(j);
											JsonObject dataForChitDemandss =  new JsonObject();
											dataForChitDemandss.addProperty("chitsubscriberchargeId", tempId);
											dataForChitDemandss.addProperty("staffId", staffId);
											dataForChitDemandss.addProperty("demandDate", datefordemand.toString());
											dataForChitDemandss.addProperty("installmentAmount", 0.0);
											dataForChitDemandss.addProperty("dueAmount", 0.0);
											dataForChitDemandss.addProperty("overdueAmount", 0.0);
											dataForChitDemandss.addProperty("penaltyAmount", 0.0);
											dataForChitDemandss.addProperty("collectedAmount", 0.0);
											dataForChitDemandss.addProperty("isCalculated", isCal);
											chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemandss);	 
										}


									}	
								}

								//checking whether there are any overdue or due 
								if((DemandScheduleData.getPenaltyAmount()!=null && DemandScheduleData.getPenaltyAmount()>0) || (DemandScheduleData.getOverdueAmount()!=null && DemandScheduleData.getOverdueAmount()>0))
								{

									//System.out.println("Step-12");

									ChitCycleData tempchitcycledata = chitCycleReadPlatformService.retrieveAll(chitId, cyclecount);
									JsonObject dataToCreateChitSubsCharges = new JsonObject();
									dataToCreateChitSubsCharges.addProperty("subscriberId", ((ChitGroupSubscriberData) obj).getId());
									dataToCreateChitSubsCharges.addProperty("chitCycleId", tempchitcycledata.getId());

									dataToCreateChitSubsCharges.addProperty("chitChargeId", chitChargeID);
									dataToCreateChitSubsCharges.addProperty("amount", chitGroup.getMonthlycontribution());	
									CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);




									final GlobalConfigurationPropertyData Configuredscore = this.configurationReadPlatformService
											.retrieveGlobalConfiguration("DemandAmountRoundOffValue");
									Long nearestRoundTo = Configuredscore.getValue();
									Integer days = DaysTill25.size();
									Double normalEmiPerDay = (chitGroup.getMonthlycontribution().doubleValue() - (dividend/chitGroup.getChitduration()))/days.doubleValue();

									Double perDayInstallmentAdjusted = Math.ceil(normalEmiPerDay/(nearestRoundTo*1.0)) * nearestRoundTo ;

									double remainingInstallment = (chitGroup.getMonthlycontribution().doubleValue() - (dividend/chitGroup.getChitduration()));


									Long tempId = DemandScheduleData.getChitSubscriberChargeId();

									//System.out.println(DemandScheduleData.getId()+"DemandScheduleData.getId()");

									//System.out.println(DemandScheduleData.getChitSubscriberChargeId()+"DemandScheduleData.getChitSubscriberChargeId()");

									//System.out.println("DaysTill25 "+DaysTill25.size());

									this.chitDemandScheduleWritePlatformService.deleteChitDemandSchedule(DemandScheduleData.getId());

									this.chitSubscriberChargeWritePlatformService.deleteChitSubscriberCharge(tempId);

									Double penalty = 0.0;

									Double overdue = 0.0;

									for(int k = 0 ; k < DaysTill25.size() ; k++)
									{

										if(k==0)
										{
											penalty =  DemandScheduleData.getPenaltyAmount();

											overdue  =  DemandScheduleData.getOverdueAmount();
										}
										else
										{
											penalty = 0.0;
											overdue = 0.0;
										}
										LocalDate demandDates = DaysTill25.get(k);
										if(remainingInstallment<=perDayInstallmentAdjusted)
										{
											System.out.println("Step-14");
											System.out.println("insid ei f");
											JsonObject dataForChitDemands =  new JsonObject();
											dataForChitDemands.addProperty("chitsubscriberchargeId",Ids.commandId());
											dataForChitDemands.addProperty("staffId", staffId);
											dataForChitDemands.addProperty("demandDate", demandDates.toString());
											dataForChitDemands.addProperty("installmentAmount", remainingInstallment);
											dataForChitDemands.addProperty("dueAmount", 0.0);
											dataForChitDemands.addProperty("overdueAmount", overdue);
											dataForChitDemands.addProperty("penaltyAmount", penalty);
											dataForChitDemands.addProperty("collectedAmount", 0.0);
											dataForChitDemands.addProperty("isCalculated", false);
											chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
											remainingInstallment = 0l;
										}
										else
										{
											//	System.out.println("Step-15");
											JsonObject dataForChitDemands =  new JsonObject();
											dataForChitDemands.addProperty("chitsubscriberchargeId",Ids.commandId());
											dataForChitDemands.addProperty("staffId", staffId);
											dataForChitDemands.addProperty("demandDate", demandDates.toString());
											dataForChitDemands.addProperty("installmentAmount", perDayInstallmentAdjusted);
											dataForChitDemands.addProperty("dueAmount", 0.0);
											dataForChitDemands.addProperty("overdueAmount", overdue);
											dataForChitDemands.addProperty("penaltyAmount", penalty);
											dataForChitDemands.addProperty("collectedAmount", 0.0);
											dataForChitDemands.addProperty("isCalculated", false);
											chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
											remainingInstallment = remainingInstallment - perDayInstallmentAdjusted;

										}

									}			
									for(int j = 0 ; j < DaysAfter25.size() ; j++)
									{
										LocalDate datefordemand = DaysAfter25.get(j);
										JsonObject dataForChitDemandss =  new JsonObject();
										dataForChitDemandss.addProperty("chitsubscriberchargeId",Ids.commandId());
										dataForChitDemandss.addProperty("staffId", staffId);
										dataForChitDemandss.addProperty("demandDate", datefordemand.toString());
										dataForChitDemandss.addProperty("installmentAmount", 0.0);
										dataForChitDemandss.addProperty("dueAmount", 0.0);
										dataForChitDemandss.addProperty("overdueAmount", 0.0);
										dataForChitDemandss.addProperty("penaltyAmount", 0.0);
										dataForChitDemandss.addProperty("collectedAmount", 0.0);
										dataForChitDemandss.addProperty("isCalculated", false);
										chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemandss);	 
									}
								}
							}
						}
						//if client has no advance in the demand schedule new demand schedule for the rest of the month is created here in the else part
						else
						{
							System.out.println("step =16 ");
							//creating data for chitsubscribercharge
							JsonObject dataToCreateChitSubsCharges = new JsonObject();
							dataToCreateChitSubsCharges.addProperty("subscriberId", ((ChitGroupSubscriberData) obj).getId());
							dataToCreateChitSubsCharges.addProperty("chitCycleId", chitcycledata.getId());

							dataToCreateChitSubsCharges.addProperty("chitChargeId", chitChargeID);
							dataToCreateChitSubsCharges.addProperty("amount", chitGroup.getMonthlycontribution());	
							CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);

							for(int k = 0 ; k < DemandDates.size() ; k++)
							{
								LocalDate datesobj = DemandDates.get(k);
								if(datesobj.getDayOfMonth()<=25)
								{
									DaysTill25.add(datesobj);
								}
								else
								{
									DaysAfter25.add(datesobj);
								}
							}

							Long dividend = 0l;
							if(chitcycledata1.getDividend()!=null && chitcycledata1.getDividend()!=0)
							{

								dividend = chitcycledata1.getDividend();
								System.out.println("Inside else of S-1 "+dividend);
								Long dividendValueForIndividual =  dividend/chitGroup.getChitduration();

								Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_DIVIDEND_PAYBLE.getValue().longValue());  
								Long plAccountId= null;
								for(Map.Entry<String, Object> itr : mapData3.entrySet())
								{
									if(itr.getKey().equals("glAccountId"))
									{
										plAccountId = (Long)itr.getValue();
									}
								}
								String currencyCode = "INR";
								Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(chitGroup.getOfficeId());
								//this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, clientId, currencyCode,plAccountId,clientId, demandstartdate, BigDecimal.valueOf(dividendValueForIndividual) ,null,"DR-",chitsubsccID);

								Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  
								Long plAccountId1= null;
								for(Map.Entry<String, Object> itr : mapData4.entrySet())
								{
									if(itr.getKey().equals("glAccountId"))
									{
										plAccountId1 = (Long)itr.getValue();
									}
								}
								//this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId1, chitId, demandstartdate,BigDecimal.valueOf(dividendValueForIndividual),null, "OS-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,chitsubsccID);


							}
							//Double remainingAdvance = monthlycontribution-DemandScheduleData.getCollectedAmount();

							final GlobalConfigurationPropertyData Configuredscore = this.configurationReadPlatformService
									.retrieveGlobalConfiguration("DemandAmountRoundOffValue");
							Long nearestRoundTo = Configuredscore.getValue();
							Integer Divide = DaysTill25.size();
							Double normalEmiPerDay = (chitGroup.getMonthlycontribution().doubleValue() - (dividend/chitGroup.getChitduration()))/Divide.doubleValue();

							Double perDayInstallmentAdjusted = Math.ceil(normalEmiPerDay/(nearestRoundTo*1.0)) * nearestRoundTo ;
							System.out.println("perDayInstallment "+perDayInstallmentAdjusted);
							double remainingInstallment = (chitGroup.getMonthlycontribution().doubleValue() - (dividend/chitGroup.getChitduration()));
							
							for(int k = 0 ; k < DaysTill25.size() ; k++)
							{

								LocalDate demandDates = DaysTill25.get(k);
								System.out.println("remainingInstallment "+remainingInstallment);
								System.out.println("perDayInstallmentAdjusted "+perDayInstallmentAdjusted);
								if(remainingInstallment<=perDayInstallmentAdjusted)
								{
									System.out.println("inside remaining isnt1");
									JsonObject dataForChitDemands =  new JsonObject();
									dataForChitDemands.addProperty("chitsubscriberchargeId", Ids.commandId());
									dataForChitDemands.addProperty("staffId", staffId);
									dataForChitDemands.addProperty("demandDate", demandDates.toString());
									dataForChitDemands.addProperty("installmentAmount", remainingInstallment);
									dataForChitDemands.addProperty("dueAmount", 0.0);
									dataForChitDemands.addProperty("overdueAmount", 0.0);
									dataForChitDemands.addProperty("penaltyAmount", 0.0);
									dataForChitDemands.addProperty("collectedAmount", 0.0);
									dataForChitDemands.addProperty("isCalculated", false);
									chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
									remainingInstallment = 0l;
								}
								else
								{
									System.out.println("inside remain else");
									
									JsonObject dataForChitDemands =  new JsonObject();
									dataForChitDemands.addProperty("chitsubscriberchargeId",Ids.commandId());
									dataForChitDemands.addProperty("staffId", staffId);
									dataForChitDemands.addProperty("demandDate", demandDates.toString());
									dataForChitDemands.addProperty("installmentAmount", perDayInstallmentAdjusted);
									dataForChitDemands.addProperty("dueAmount", 0.0);
									dataForChitDemands.addProperty("overdueAmount", 0.0);
									dataForChitDemands.addProperty("penaltyAmount", 0.0);
									dataForChitDemands.addProperty("collectedAmount", 0.0);
									dataForChitDemands.addProperty("isCalculated", false);
									chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemands);
									remainingInstallment = remainingInstallment - perDayInstallmentAdjusted;

								}
							}			
							for(int j = 0 ; j < DaysAfter25.size() ; j++)
							{
								LocalDate datefordemand = DaysAfter25.get(j);
								JsonObject dataForChitDemandss =  new JsonObject();
								dataForChitDemandss.addProperty("chitsubscriberchargeId",Ids.commandId());
								dataForChitDemandss.addProperty("staffId", staffId);
								dataForChitDemandss.addProperty("demandDate", datefordemand.toString());
								dataForChitDemandss.addProperty("installmentAmount", 0.0);
								dataForChitDemandss.addProperty("dueAmount", 0.0);
								dataForChitDemandss.addProperty("overdueAmount", 0.0);
								dataForChitDemandss.addProperty("penaltyAmount", 0.0);
								dataForChitDemandss.addProperty("collectedAmount", 0.0);
								dataForChitDemandss.addProperty("isCalculated", false);
								chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataForChitDemandss);	 
							}
						}
						//System.out.println("ith time :"+i);
					}
					else
					{
						Long clientId = ((ChitGroupSubscriberData) obj).getClientId();
						Long CycleValue = chitGroup.getCurrentcycle();
						Long cyclecount = CycleValue+1;
						ChitCycleData chitcycledata = chitCycleReadPlatformService.retrieveAll(chitId, cyclecount);
						ChitCycleData chitcycledata1 = chitCycleReadPlatformService.retrieveAll(chitId, CycleValue);			
						LocalDate nextAuctionTime = chitcycledata.getAuctiondate().withDayOfMonth(1);
						LocalDate demandDate = findWorkingDays.nextWorkingDayofNextMonth(nextAuctionTime);	
						ClientData client = this.clientReadPlatformService.retrieveOne(clientId);
						Long staffId = client.getStaffId();
						Long chitsubsccID = ((ChitGroupSubscriberData) obj).getId();
						//ChitSubscriberChargeData chitsubsID = this.chitSubscriberChargeReadPlatformServices.retrieveById(chitsubsccID, chitChargeID, chitcycledata.getId());
						LocalDate demandstartdate = demandDate.withDayOfMonth(1);
						System.out.println("demandstartdate "+demandstartdate);
						List<LocalDate> DemandDates = findWorkingDays.findWorkingDays(demandstartdate, true);
				
						
							Double dividend = chitcycledata1.getDividend().doubleValue()/chitGroup.getChitduration().doubleValue();
							Long div = Math.round(dividend);
							Long val = null;
							if(dividend>0.0)
							{
								val = chitGroup.getMonthlycontribution()-div;
							}
							else
							{
								val = chitGroup.getMonthlycontribution();
							}
							//creating chit subscriber charge
							ChitSubscriberCharge csg = new ChitSubscriberCharge(chitsubsccID,chitcycledata.getId(),chitChargeID,chitGroup.getMonthlycontribution(),true,false,null,staffId);
							ChitSubscriberCharge cgs = this.chitSubscriberChargeRepository.saveAndFlush(csg);
							//creating demand schedule for rest of the month
							ChitDemandSchedule cd = new ChitDemandSchedule(cgs.getId(),staffId,DemandDates.get(0),val.doubleValue(),0.0,0.0,0.0,val.doubleValue(),true,chitId);
							ChitDemandSchedule cd1 = chitDemandScheduleRepository.saveAndFlush(cd);
							//creating Transaction for the demand
							ChitSubscriberTransaction cst = new ChitSubscriberTransaction(cd1.getId(),null,null,val.doubleValue(),ChitTransactionEnum.INSTALLMENT_EMI,null,null,false,true);
							cst = chitSubscriberTransactionRepository.saveAndFlush(cst);
							//Ledger entries
							
							//credit Ledger
							Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_FOREMAN.getValue().longValue());  
							Long plAccountId= null;
							for(Map.Entry<String, Object> itr : mapData3.entrySet())
							{
								if(itr.getKey().equals("glAccountId"))
								{
									plAccountId = (Long)itr.getValue();
								}
							}
							String currencyCode = "INR";
							Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(chitGroup.getOfficeId());
							this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, clientId, currencyCode,plAccountId,clientId, demandstartdate, BigDecimal.valueOf(val) ,cst.getId(),"DR-",chitsubsccID);

							//Debit Ledger
							Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  
							Long plAccountId1= null;
							for(Map.Entry<String, Object> itr : mapData4.entrySet())
							{
								if(itr.getKey().equals("glAccountId"))
								{
									plAccountId1 = (Long)itr.getValue();
								}
							}
							this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId1, chitId, demandstartdate,BigDecimal.valueOf(val),cst.getId(), "OS-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,chitsubsccID,chitId);
							
							//Creating demands for rest of the month
							for(int k = 1 ; k < DemandDates.size() ; k++)
							{
								LocalDate date = DemandDates.get(k);
								ChitDemandSchedule cds = new ChitDemandSchedule(cgs.getId(),staffId,date,0.0,0.0,0.0,0.0,0.0,true,chitId);
								chitDemandScheduleRepository.saveAndFlush(cds);
							}
						
					}
				}
			}
		}
	}


	@Transactional
	void CheckForPenalties(Long chitId)
	{
		try
		{
			ChitGroupData chitdata = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
			Long cycleCount = chitdata.getCurrentcycle();
			//	System.out.println("inside check for penalties "+cycleCount);
			ChitCycleData chitcycledata = chitCycleReadPlatformService.retrieveAll(chitId,cycleCount);
			GlobalConfigurationPropertyData configureCgstData = configurationReadPlatformService.retrieveGlobalConfiguration("CGST");
			GlobalConfigurationPropertyData configureSgstData = configurationReadPlatformService.retrieveGlobalConfiguration("SGST");
			//	
			//getting the list of chitsubscribers
			Collection<ChitGroupSubscriberData> chitSubscribers = chitGroupReadPlatformService.getChitSubscribers(chitId);
			List<ChitGroupSubscriberData> ListofchitSubsribers = new ArrayList<ChitGroupSubscriberData>(chitSubscribers);
			ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
			Long chitChargeID = chitchargeIds.getId();
			Long cycleId = chitcycledata.getId();
			//checking for penalties overDues
			for(int i = 0 ; i < ListofchitSubsribers.size() ; i++)
			{
				Object obj = ListofchitSubsribers.get(i);

				cycleCount = chitdata.getCurrentcycle();

				if(obj instanceof ChitGroupSubscriberData)
				{
					if(obj instanceof ChitGroupSubscriberData)
					{
						if(((ChitGroupSubscriberData) obj).getChitNumber()!=1)
						{
							Long clientId = ((ChitGroupSubscriberData) obj).getClientId();
							ClientData clientsData= clientReadPlatformService.retrieveOne(clientId);
							final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
							Long staffId = this.clientReadPlatformService.retrieveOne(clientId).getStaffId();
							Long chitidSubsID = ((ChitGroupSubscriberData) obj).getId();

							ChitSubscriberChargeData chitSubscriberData = this.chitSubscriberChargeReadPlatformServices.retrieveById(chitidSubsID, chitChargeID, cycleId);
							Collection<ChitDemandScheduleData> chitdemandData = this.chitDemandScheduleReadPlatformService.retriveDemandSchedules(chitSubscriberData.getId(), false);
							int size = chitdemandData.size();
							//	System.out.println(chitdemandData.size()+" size");
							if(size>0)
							{
								List<ChitDemandScheduleData> chitdemandDataList = new ArrayList<ChitDemandScheduleData>(chitdemandData);
								for(int j = 0 ; j < chitdemandDataList.size() ; j++)
								{
									Object demandobject = chitdemandDataList.get(j);
									if(demandobject instanceof ChitDemandScheduleData)
									{
										Long demandID = ((ChitDemandScheduleData) demandobject).getId();
										JsonObject dataToUpdate = new JsonObject();
										dataToUpdate.addProperty("isCalculated", true);
										this.chitDemandScheduleWritePlatformService.updateChitDemandSchedule(demandID, dataToUpdate);
										System.out.println("inside chitdemans "+demandID);
									}
								}

								if(chitdemandDataList!=null)
								{
									Object demandobj = chitdemandDataList.get(0);
									if(demandobj instanceof ChitDemandScheduleData)
									{
										Double OverDue = ((ChitDemandScheduleData) demandobj).getOverdueAmount();
										Double due = ((ChitDemandScheduleData) demandobj).getDueAmount();
										Double penalty = ((ChitDemandScheduleData) demandobj).getPenaltyAmount();
										Double installment = ((ChitDemandScheduleData) demandobj).getInstallmentAmount();
										Boolean prizedSub = false;
										Double OverDuetobeUpdated = OverDue+penalty+due+installment;
										String currencyCode = "INR";
										Double pen = this.configurationReadPlatformService.retrieveGlobalConfiguration("penalty").getValue().doubleValue();
										Double penaltis = pen/100;
										Double amt = due+OverDue+penalty;
										if(amt>0.0)
										{
											System.out.println("inside due ");
											Long chitSubscriberChargeId = ((ChitDemandScheduleData) demandobj).getChitSubscriberChargeId();
											if(chitSubscriberChargeId!=null)
											{
												Long chitSubsId = this.chitSubscriberChargeReadPlatformServices.retrieveNameById(chitSubscriberChargeId).getChitSubscriberId();
												if(chitSubsId!=null)
												{
													prizedSub = this.chitGroupReadPlatformService.getChitSubscriber(chitSubsId).getPrizedsubscriber();
												}
											}

											if(prizedSub)
											{
												penalty = OverDuetobeUpdated * penaltis;

												BigDecimal penaltyAmount = new BigDecimal(penalty);
												Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_RECEIVED_FROM_PRIZED_SUBSCRBER.getValue().longValue());  // a BIG TODO. This has to be fetched from config.
												Long plAccountId= null;
												for(Map.Entry<String, Object> itr : mapData3.entrySet())
												{
													if(itr.getKey().equals("glAccountId"))
													{
														plAccountId = (Long)itr.getValue();
													}
												}
												LocalDate dt = LocalDate.now(ZoneId.systemDefault());
												//this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(), clientId, currencyCode, plAccountId, clientsData.getId(), dt, BigDecimal.valueOf(OverDuetobeUpdated),null, "PS-",CashAccountsForChit.CHIT_RECEIVED_FROM_PRIZED_SUBSCRBER);
												//this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(), clientId, currencyCode, plAccountId, clientsData.getId(), dt, penaltyAmount,null, "PS-",CashAccountsForChit.CHIT_PENALTY);
												Double cgstAmount = configureCgstData.getValue().doubleValue();
												Double cgstValue = cgstAmount/100;
												Double cgstDeductAmount = penaltyAmount.doubleValue() * cgstValue;
												BigDecimal deductedAmountforCGST = new BigDecimal(cgstDeductAmount);
												BigDecimal cgstpenaltyValue = penaltyAmount.subtract(deductedAmountforCGST);
												//this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(),clientId, currencyCode, plAccountId, clientsData.getId(), dt, deductedAmountforCGST,null, "GT-",CashAccountsForChit.CHIT_CGST);

												// SGST for Penalty Value
												Double sgstAmount = configureSgstData.getValue().doubleValue();
												Double sgstValue = sgstAmount/100;
												Double sgstDeductAmount = penaltyAmount.doubleValue() * sgstValue;
												BigDecimal deductedAmountforSGST = new BigDecimal(sgstDeductAmount);
												BigDecimal sgstpenaltyValue = penaltyAmount.subtract(deductedAmountforSGST);
												//this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(),clientId, currencyCode, plAccountId, clientsData.getId(), dt, deductedAmountforSGST,null, "GT-",CashAccountsForChit.CHIT_SGST);

											}
											else
											{
												LocalDate dt = LocalDate.now(ZoneId.systemDefault());
												penalty = 0.0;
												//BigDecimal penaltyAmount = new BigDecimal(penalty);
												Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_RECEIVED_FROM_NON_PRIZED_SUBSCRBER.getValue().longValue());  // a BIG TODO. This has to be fetched from config.
												Long plAccountId= null;
												for(Map.Entry<String, Object> itr : mapData3.entrySet())
												{
													if(itr.getKey().equals("glAccountId"))
													{
														plAccountId = (Long)itr.getValue();
													}
												}
												//this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(), clientId, currencyCode, plAccountId, clientId, dt,  BigDecimal.valueOf(OverDuetobeUpdated),null, "NS-",CashAccountsForChit.CHIT_RECEIVED_FROM_NON_PRIZED_SUBSCRBER);

											}

											//next cycle 
											cycleCount = cycleCount+1l;
											System.out.println(cycleCount + "cycleCount");
											ChitCycleData cycle = chitCycleReadPlatformService.retrieveAll(chitId,cycleCount);
											LocalDate auctiondate = cycle.getAuctiondate().withDayOfMonth(1);
											LocalDate newDate = this.findWorkingDays.validateworkingDayorNot(auctiondate);
											//		System.out.println(newDate+" newDate");
											JsonObject dataToCreateChitSubsCharges = new JsonObject();
											//creating data for chitsubscribercharge

											dataToCreateChitSubsCharges.addProperty("subscriberId", chitidSubsID);
											dataToCreateChitSubsCharges.addProperty("chitCycleId", cycle.getId());
											dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
											dataToCreateChitSubsCharges.addProperty("amount", chitdata.getMonthlycontribution());	
											CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);
											System.out.println(Ids.commandId()+" newDate if Ids for subsId "+chitidSubsID+" cycle "+cycle.getId()+" charge "+chitchargeIds.getId());
											Long tempChitSubscriberChargeId = Ids.commandId();
											JsonObject dataToCreate = new JsonObject();
											dataToCreate.addProperty("demandDate", newDate.toString());
											dataToCreate.addProperty("dueAmount", 0.0);
											dataToCreate.addProperty("installmentAmount", 0.0);
											dataToCreate.addProperty("overdueAmount", OverDuetobeUpdated);
											dataToCreate.addProperty("penaltyAmount", penalty);
											dataToCreate.addProperty("staffId", staffId);
											dataToCreate.addProperty("collectedAmount", 0.0);
											dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
											this.chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataToCreate);
										}
										else if(due<0)
										{
											//next cycle 
											cycleCount = cycleCount+1;
											ChitCycleData cycle = chitCycleReadPlatformService.retrieveAll(chitId,cycleCount);
											LocalDate auctiondate = cycle.getAuctiondate().withDayOfMonth(1);
											LocalDate newDate = this.findWorkingDays.validateworkingDayorNot(auctiondate);
											//		System.out.println(newDate+" newDate else");
											JsonObject dataToCreateChitSubsCharges = new JsonObject();
											//creating data for chitsubscribercharge
											due = due * -1;
											dataToCreateChitSubsCharges.addProperty("subscriberId", chitidSubsID);
											dataToCreateChitSubsCharges.addProperty("chitCycleId", cycle.getId());
											dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
											dataToCreateChitSubsCharges.addProperty("amount", chitdata.getMonthlycontribution());	
											CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);
											System.out.println(Ids.commandId()+" newDate if Ids for subsId "+chitidSubsID+" cycle "+cycle.getId()+" charge "+chitchargeIds.getId());
											Long tempChitSubscriberChargeId = Ids.commandId();
											JsonObject dataToCreate = new JsonObject();
											dataToCreate.addProperty("demandDate", newDate.toString());
											dataToCreate.addProperty("dueAmount", 0.0);
											dataToCreate.addProperty("installmentAmount", due);
											dataToCreate.addProperty("overdueAmount", 0.0);
											dataToCreate.addProperty("penaltyAmount", 0.0);
											dataToCreate.addProperty("staffId", staffId);
											dataToCreate.addProperty("collectedAmount", due);
											dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
											dataToCreate.addProperty("isCalculated", true);
											this.chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataToCreate);
										}		
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new SomethingWentWrongException();
		}
	}

	@Transactional
	void checkForLedger(Long chitId)
	{
		//System.out.println("inside ledger ");
		ChitGroupData chit = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);

		ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");

		Collection<ChitGroupSubscriberData> chitsubsData = this.chitGroupReadPlatformService.getChitSubscribers(chitId);

		Iterator<ChitGroupSubscriberData> itr = chitsubsData.iterator();

		//	System.out.println("inside ledger 1");

		while(itr.hasNext())
		{
			//	System.out.println("inside ledger 2");
			ChitGroupSubscriberData Subscriber = itr.next();

			Long currcycle = chit.getCurrentcycle() ;

			ChitCycleData cycleData = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, currcycle+1l);

			//	System.out.println("inside ledger 3");
			Long cycleId = cycleData.getId();

			LocalDate auctDate = cycleData.getAuctiondate().withDayOfMonth(1);


			LocalDate demndDate = this.findWorkingDays.validateworkingDayorNot(auctDate);

			//	System.out.println(demndDate+" demndDate");

			ChitSubscriberChargeData chitSubscriberCharge = this.chitSubscriberChargeReadPlatformServices.retrieveById(Subscriber.getId(), chitchargeIds.getId(), cycleId);

			//	System.out.println("hrloo");
			Boolean prizedSubs = Subscriber.getPrizedsubscriber();

			if(chitSubscriberCharge!=null)
			{
				//	System.out.println("inside ledger 4" +chitSubscriberCharge.getId());

				Long clientId = Subscriber.getClientId();
				Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
				String currencyCode = "INR";
				//				System.out.println("inside 1 ");
				System.out.println(chitSubscriberCharge.getId()+" chitSubscriberCharge.getId()");
				//	System.out.println(client.getStaff()+" client.getStaff()");
				System.out.println(demndDate+" demndDate");

				ChitDemandScheduleData DemandData = this.chitDemandScheduleReadPlatformService.retrieveByIdAndDate(chitSubscriberCharge.getId(), client.getStaff().getId(), demndDate);
				//	System.out.println("inside 2 ");
				Double OverdueAmount = DemandData.getOverdueAmount();
				//	System.out.println("OverdueAmount"+ OverdueAmount);
				Double penalty = DemandData.getPenaltyAmount();
				Double due = DemandData.getDueAmount();
				//	System.out.println("22"+ penalty);
				Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_RECEIVED_FROM_PRIZED_SUBSCRBER.getValue().longValue()); 
				Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_RECEIVED_FROM_NON_PRIZED_SUBSCRBER.getValue().longValue());// a BIG TODO. This has to be fetched from config.
				//	System.out.println("22");
				Long plAccountId= null;

				if(OverdueAmount!=null && OverdueAmount>0.0)
				{
					if(prizedSubs)
					{
						for(Map.Entry<String, Object> itr1 : mapData3.entrySet())
						{
							if(itr1.getKey().equals("glAccountId"))
							{
								plAccountId = (Long)itr1.getValue();
							}
						}

						LocalDate dt = LocalDate.now(ZoneId.systemDefault());
						this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(), clientId, currencyCode, plAccountId, clientId, dt, BigDecimal.valueOf(OverdueAmount),null, "PS-",CashAccountsForChit.CHIT_RECEIVED_FROM_PRIZED_SUBSCRBER);
						this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(), clientId, currencyCode, plAccountId, clientId, dt, BigDecimal.valueOf(penalty),null, "PS-",CashAccountsForChit.CHIT_PENALTY);
					}
					else
					{
						for(Map.Entry<String, Object> itr1 : mapData4.entrySet())
						{
							if(itr1.getKey().equals("glAccountId"))
							{
								plAccountId = (Long)itr1.getValue();
							}
						}
						//	System.out.println("usdb");
						LocalDate dt = LocalDate.now(ZoneId.systemDefault());
						this.accountingProcessorHelper.createDebitJournalEntryForOthers(client.getOffice(), clientId, currencyCode, plAccountId, clientId, dt, BigDecimal.valueOf(OverdueAmount),null, "NS-",CashAccountsForChit.CHIT_RECEIVED_FROM_NON_PRIZED_SUBSCRBER);
						//	System.out.println("usd2b");
					}
				}
			}
		}
	}


	
	@Transactional
	@Override
	public CommandProcessingResult prizeMoneyCalculations(Long chitId, Long cycleNumber,JsonObject body) {

		try {
			//System.out.println("Im in prizeMoneyCalc");
			JsonObject command = new JsonObject();

			JsonObject command2 = new JsonObject();
			//System.out.println("command2 is executed");
			ChitCycleData cycleData = chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, cycleNumber);
			//System.out.println(cycleData.getchitId()+ "1" + cycleData.getCycleNumber());

			ChitGroupData groupData = chitGroupReadPlatformService.retrieveChitGroup(cycleData.getChitId());
			//System.out.println(groupData.getName()+"2");		

			ChitBidsData bidData = chitBidsReadPlatformService.retriveChitsDataForChitGroup(cycleData.getId(), groupData.getId(), cycleData.getCycleNumber(), 1);

			Long bidCount = chitBidsReadPlatformService.retriveCountChitsDataForChitGroup(cycleData.getId(), groupData.getId(), cycleData.getCycleNumber(), 1);
			
			ChitGroupSubscriberData subscriberData = chitGroupReadPlatformService.getChitSubscriber(bidData.getSubscriberId());
			
			if(bidData.getId() == null || bidData.getId() == 0 || bidData == null || bidCount>1) {
				throw new ChitBidsWinnerException(bidData.getId());
			}

			Long chitValue = groupData.getChitvalue();

			GlobalConfigurationPropertyData formanCommissionDetails = configurationReadPlatformService.retrieveGlobalConfiguration("Foreman-Commission");
			GlobalConfigurationPropertyData gstDetails = configurationReadPlatformService.retrieveGlobalConfiguration("GST");
			GlobalConfigurationPropertyData verificationFeeDetails = configurationReadPlatformService.retrieveGlobalConfiguration("Verification-Fee");


			Double bidAmount = bidData.getBidAmount();

			Double foremanComissionValue = (double) formanCommissionDetails.getValue()/100;

			Double foremanCommissionAmount = foremanComissionValue * chitValue;

			Double dividend = bidAmount - foremanCommissionAmount;

			Double gstValue = (double) gstDetails.getValue()/100;

			Double gstAmount = foremanCommissionAmount * gstValue;

			Long verificationAmount = verificationFeeDetails.getValue();

			Double subscriptionPayble =  chitValue-bidAmount-gstAmount-verificationAmount;

			//TODO
			// Pay the winner
			// Insert payment_details // Steephan to tell how to pay the winner? cash/cheque etc.. if cheque will we need to get this info from UI?
			// Insert chit_subscriber_transaction {chit_subscriber_id = chit_group_subscriber.id , Amount= subscriptionPayable, Tran_type_enum = "WinnerPrizeMoney", payment_detail_id = from above insert }
			// Insert ledger entries  // TODO // Steephan  to provide

			// Commission to foreman/company
			//Insert payment_details // Steephan to tell how to pay the foremanCommissionAmount
			// Insert ledger entries // Steephan  to provide

			command.addProperty("prizedsubscriber", true);
			command.addProperty("prizedcycle", cycleData.getCycleNumber());
			this.updateChitgroupSubscriber(subscriberData.getId(), command);


			Double val = 0.0;
			if(body.get("EmiAmount")!=null && !body.get("EmiAmount").isJsonNull() && val.compareTo(body.get("EmiAmount").getAsDouble())!=0)
			{
				Double emiAmount = body.get("EmiAmount").getAsDouble();
				
				Long currentCyle = this.chitGroupReadPlatformService.retrieveChitGroup(chitId).getCurrentcycle();

				Long Nextcyclenumber = currentCyle + 1l ;

				ChitCycleData nextCycleData = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, Nextcyclenumber);

				ChitBidsWinnerData chitWinner = this.chitBidsReadPlatformService.getChitWinnerData(chitId, cycleNumber);

				JsonObject data = new JsonObject();

				Long nextCycleId = nextCycleData.getId();

				LocalDate nextAucDate = nextCycleData.getAuctiondate().withDayOfMonth(1);

				nextAucDate = this.findWorkingDays.validateworkingDayorNot(nextAucDate);

				Long clientId = chitWinner.getClientId();

				Long chitNum = chitWinner.getChitNumber().longValue();

				Long subsId = this.chitGroupReadPlatformService.getChitSubscriberUsingChitIDClientId(chitId, clientId, chitNum).getId();

				ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");

				ChitSubscriberChargeData chitsubschargeData = this.chitSubscriberChargeReadPlatformServices.retrieveById(subsId, chitchargeIds.getId(), nextCycleId);

				CommandProcessingResult tranId = null;

				subscriptionPayble = subscriptionPayble-emiAmount;

				if(emiAmount!=0.0)
				{
					if(chitsubschargeData!=null)
					{
						Long staffId = chitsubschargeData.getStaffId();
						ChitDemandScheduleData Demanddata = this.chitDemandScheduleReadPlatformService.retrieveByIdAndDate(chitsubschargeData.getId(), staffId, nextAucDate);
						Double collectedAmount = Demanddata.getCollectedAmount();
						collectedAmount = collectedAmount+emiAmount;
						data.addProperty("collectedAmount", collectedAmount);
						data.addProperty("installmentAmount", collectedAmount);
						Long demandId = Demanddata.getId();
						CommandProcessingResult id = this.chitDemandScheduleWritePlatformService.updateChitDemandSchedule(demandId, data);
						JsonObject dataForChitsubsTran =  new JsonObject();
						dataForChitsubsTran.addProperty("chitdemandscheduleId", id.commandId());
						dataForChitsubsTran.addProperty("amount", collectedAmount);
						dataForChitsubsTran.addProperty("trantype", "INSTALLMENT_EMI");
						dataForChitsubsTran.addProperty("isprocessed", true);
						tranId = this.chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataForChitsubsTran);	
					}
					else
					{
						data.addProperty("subscriberId", subsId);
						data.addProperty("chitCycleId", nextCycleId);
						data.addProperty("chitChargeId", chitchargeIds.getId());
						data.addProperty("amount", this.chitGroupReadPlatformService.retrieveChitGroup(chitId).getMonthlycontribution());	
						CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(data);
						Long tempChitSubscriberChargeId = Ids.commandId();
						JsonObject dataToCreate = new JsonObject();
						dataToCreate.addProperty("demandDate", nextAucDate.toString());
						dataToCreate.addProperty("dueAmount", 0.0);
						dataToCreate.addProperty("installmentAmount", emiAmount);
						dataToCreate.addProperty("overdueAmount", 0.0);
						dataToCreate.addProperty("penaltyAmount", 0.0);
						dataToCreate.addProperty("staffId", this.clientRepository.findOneWithNotFoundDetection(clientId).getStaff().getId());
						dataToCreate.addProperty("collectedAmount", emiAmount);
						dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
						dataToCreate.addProperty("isCalculated", true);
						CommandProcessingResult id = this.chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataToCreate);
						JsonObject dataForChitsubsTran =  new JsonObject();
						dataForChitsubsTran.addProperty("chitdemandscheduleId", id.commandId());
						dataForChitsubsTran.addProperty("amount", emiAmount);
						dataForChitsubsTran.addProperty("trantype", "INSTALLMENT_EMI");
						dataForChitsubsTran.addProperty("isprocessed", true);
						tranId = this.chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataForChitsubsTran);

					}

					if(tranId.commandId()!=null && tranId.commandId()!=0l)
					{
						ChitGroupData ChitData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
						//debit
						Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_SUBSCRIPTION_PAYBLE.getValue().longValue());  
						Long plAccountId= null;
						for(Map.Entry<String, Object> itr : mapData3.entrySet())
						{
							if(itr.getKey().equals("glAccountId"))
							{
								plAccountId = (Long)itr.getValue();
							}
						}
						String currencyCode = "INR";
						Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(ChitData.getOfficeId());
						this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,plAccountId,chitId, nextAucDate, BigDecimal.valueOf(emiAmount) ,tranId.commandId(),"DR-",subsId);

						//credit
						Map<String, Object> mapData5 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  
						Long plAccountId2= null;
						for(Map.Entry<String, Object> itr : mapData5.entrySet())
						{
							if(itr.getKey().equals("glAccountId"))
							{
								plAccountId2 = (Long)itr.getValue();
							}
						}
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId2, chitId,nextAucDate,BigDecimal.valueOf(emiAmount),tranId.commandId(), "OS-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,subsId,chitId);
					}
				}
			}
			
		


			if(cycleData.getchitId().equals(chitId) && cycleData.getCycleNumber().equals(cycleNumber))
			{
				//System.out.println(dividend+" dividend");	
				command2.addProperty("dividend", dividend);

				//System.out.println(subscriptionPayble+" subscriptionPayble");
				command2.addProperty("subscriptionPayble", subscriptionPayble);
				//System.out.println(gstAmount+" gstAmount");
				command2.addProperty("gstAmount", gstAmount);
				//System.out.println(foremanCommissionAmount+" foremanCommissionAmount");
				//System.out.println(verificationAmount+" verificationAmount");
				command2.addProperty("foremanCommissionAmount", foremanCommissionAmount);
				command2.addProperty("verificationAmount", verificationAmount);
				this.chitBidsWritePlatformService.updateChitGroupCycle(cycleData.getId(), command2);
			}
			
			System.out.println("groupData.getChitduration() "+groupData.getChitduration());
			System.out.println("cycleNumber "+cycleNumber);
			if(groupData.getChitduration().compareTo(cycleNumber)==0)
			{
				System.out.println("inside chit data");
				this.PrizeMoneyPosting(chitId, cycleNumber);
			}
			return new CommandProcessingResultBuilder().withCommandId(chitId).withEntityId(chitId).build();

		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitGroupDataIntegrityIssues(null, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}
	
	@Transactional
	@Override
	public void PrizeMoneyPosting(Long chitId,Long cycleId)
	{
		try {
			ChitGroupData ChitData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
			Long clientId = null;
			Long subsId = null;
			Iterator<ChitGroupSubscriberData> subs = this.chitGroupReadPlatformService.getChitSubscribers(chitId).iterator();
			LocalDate bidDate = this.chitBidsReadPlatformService.getChitWinnerData(chitId, cycleId).getBidDate();
			while(subs.hasNext())
			{
				ChitGroupSubscriberData subsData = subs.next();
				Long cycle = subsData.getPrizedcycle();
				if(cycle!=null)
				{
					if(subsData.getPrizedsubscriber() && cycle.equals(ChitData.getCurrentcycle()))
					{
						clientId = subsData.getClientId();
						subsId = subsData.getId();
					}
				}
			}
			//System.out.println("insdie prizemoney posting");
			ChitCycleData cycleData = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, cycleId);
			Long Dividend = cycleData.getDividend();
			Long cyclenum = cycleData.getCycleNumber() + 1;
			ChitCycleData cycleDataofNextMonth = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, cyclenum);
			LocalDate aucDate = cycleDataofNextMonth.getAuctiondate().withDayOfMonth(1);
			LocalDate dat = this.findWorkingDays.validateworkingDayorNot(aucDate);
			Long chitValue = ChitData.getChitvalue();
			Double foremancommissionAmount = cycleData.getForemanCommissionAmount();
			Double gstAmount = cycleData.getGstAmount();
			if(chitValue!=null && chitValue!=0)
			{
				Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_SUBSCRIPTION.getValue().longValue());  
				Long plAccountId1= null;
				for(Map.Entry<String, Object> itr : mapData4.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId1 = (Long)itr.getValue();
					}
				}
				String currencyCode = "INR";
				Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(ChitData.getOfficeId());
				this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,plAccountId1,chitId, bidDate, BigDecimal.valueOf(chitValue) ,null,"DR-",subsId);
				
				Map<String, Object> mapData5 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  
				Long plAccountId2= null;
				for(Map.Entry<String, Object> itr : mapData5.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId2 = (Long)itr.getValue();
					}
				}
				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId2,chitId,bidDate,BigDecimal.valueOf(chitValue),null, "BV-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,subsId,chitId);
				//System.out.println("insdie chitvalue posting");
				Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_BIDS_AC.getValue().longValue());  
				Long plAccountId= null;
				for(Map.Entry<String, Object> itr : mapData3.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId = (Long)itr.getValue();
					}
				}
				this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,plAccountId,chitId, bidDate, BigDecimal.valueOf(chitValue) ,null,"DR-",subsId);
			}


			if(foremancommissionAmount!=null && foremancommissionAmount!=0.0)
			{
				Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_FORMAN_COMMISSION.getValue().longValue());  
				Long plAccountId= null;
				for(Map.Entry<String, Object> itr : mapData3.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId = (Long)itr.getValue();
					}
				}
				String currencyCode = "INR";
				Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(ChitData.getOfficeId());

				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId,chitId,bidDate,BigDecimal.valueOf(foremancommissionAmount),null, "BV-",CashAccountsForChit.CHIT_FORMAN_COMMISSION,subsId,chitId);
			}

			if(gstAmount!=null  && gstAmount!=0.0)
			{
				Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_CGST.getValue().longValue());  
				Long plAccountId= null;
				for(Map.Entry<String, Object> itr : mapData3.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId = (Long)itr.getValue();
					}
				}
				Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(ChitData.getOfficeId());
				String currencyCode = "INR";
				Double penalty = gstAmount/2;
				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId, chitId,bidDate,BigDecimal.valueOf(penalty),null, "BV-",CashAccountsForChit.CHIT_CGST,subsId,chitId);

				Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_SGST.getValue().longValue());  
				Long plAccountId1= null;
				for(Map.Entry<String, Object> itr : mapData4.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId1 = (Long)itr.getValue();
					}
				}
				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId1, chitId,bidDate,BigDecimal.valueOf(penalty),null, "BV-",CashAccountsForChit.CHIT_SGST,subsId,chitId);
			}

			Double verificationAmount = cycleData.getVerificationAmount();

			if(verificationAmount!=null && verificationAmount!=0.0)
			{
				GlobalConfigurationPropertyData configureCgstData = configurationReadPlatformService.retrieveGlobalConfiguration("CGST");
				GlobalConfigurationPropertyData configureSgstData = configurationReadPlatformService.retrieveGlobalConfiguration("SGST");

				Double PercentageOfcgst = configureCgstData.getValue().doubleValue();

				Double cgst = PercentageOfcgst/100;

				Double penaltyAmountToBePaid = verificationAmount * cgst;


				Double PercentageOfsgst = configureSgstData.getValue().doubleValue();

				Double sgst = PercentageOfsgst/100;

				Double penaltyAmountToBePaidforSgst = verificationAmount * sgst;

				Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_CGST.getValue().longValue());  
				Long plAccountId= null;
				for(Map.Entry<String, Object> itr : mapData3.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId = (Long)itr.getValue();
					}
				}
				Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(ChitData.getOfficeId());
				String currencyCode = "INR";

				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId, chitId,bidDate,BigDecimal.valueOf(penaltyAmountToBePaidforSgst),null, "BV-",CashAccountsForChit.CHIT_CGST,subsId,chitId);

				Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_SGST.getValue().longValue());  
				Long plAccountId1= null;
				for(Map.Entry<String, Object> itr : mapData4.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId1 = (Long)itr.getValue();
					}
				}
				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId1,chitId,bidDate,BigDecimal.valueOf(penaltyAmountToBePaid),null, "BV-",CashAccountsForChit.CHIT_SGST,subsId,chitId);

				Map<String, Object> mapData5 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.VERIFICATION_CHARGES.getValue().longValue());  
				Long plAccountId2= null;
				for(Map.Entry<String, Object> itr : mapData5.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId2 = (Long)itr.getValue();
					}
				}
				verificationAmount = verificationAmount - (penaltyAmountToBePaidforSgst+penaltyAmountToBePaid);
				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId2,chitId,bidDate,BigDecimal.valueOf(verificationAmount),null, "BV-",CashAccountsForChit.VERIFICATION_CHARGES,subsId,chitId);
			}

			Double subscriptionPayable = cycleData.getSubscriptionPayble();

			if(subscriptionPayable!=null && subscriptionPayable!=0.0)
			{
				Map<String, Object> mapData5 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_SUBSCRIPTION_PAYBLE.getValue().longValue());  
				Long plAccountId2= null;
				for(Map.Entry<String, Object> itr : mapData5.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId2 = (Long)itr.getValue();
					}
				}
				Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(ChitData.getOfficeId());
				String currencyCode = "INR";
				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId2, chitId,bidDate,BigDecimal.valueOf(subscriptionPayable),null, "BV-",CashAccountsForChit.CHIT_SUBSCRIPTION_PAYBLE,subsId,chitId);
			}

			if(Dividend!=null && Dividend!=0)
			{
				//System.out.println(Dividend+"----Dividend inside prizemoney posting");
				Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_DIVIDEND_PAYBLE.getValue().longValue());  
				Long plAccountId= null;
				for(Map.Entry<String, Object> itr : mapData3.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId = (Long)itr.getValue();
					}
				}
				String currencyCode = "INR";
				Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(ChitData.getOfficeId());

				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId,chitId, bidDate,BigDecimal.valueOf(Dividend),null, "BV-",CashAccountsForChit.CHIT_DIVIDEND_PAYBLE,subsId,chitId);

				//outstanding subs debiting dividend
				Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_DIVIDEND_PAYBLE.getValue().longValue());  
				Long plAccountId1 = null;
				for(Map.Entry<String, Object> itr : mapData4.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId1 = (Long)itr.getValue();
					}
				}			
				this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,plAccountId1,chitId, dat, BigDecimal.valueOf(Dividend) ,null,"DR-",subsId);
				//outstanding subs credeting dividend

				Map<String, Object> mapData5 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  
				Long plAccountId2 = null;
				for(Map.Entry<String, Object> itr : mapData5.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId2 = (Long)itr.getValue();
					}
				}
				Long chitDuration = ChitData.getChitduration();
				Double dividendValue = Dividend.doubleValue() / chitDuration.doubleValue();
				chitDuration = chitDuration-1;
				dividendValue = dividendValue*chitDuration;
				Long div1 = Math.round(dividendValue);
				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId2,chitId, dat,BigDecimal.valueOf(div1),null, "OS-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,null,chitId);


				Map<String, Object> mapData6 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_DIVIDEND_OWN.getValue().longValue());  
				Long plAccountId3 = null;
				for(Map.Entry<String, Object> itr : mapData6.entrySet())
				{
					if(itr.getKey().equals("glAccountId"))
					{
						plAccountId3 = (Long)itr.getValue();
					}
				}
				Long chitDuration1 = ChitData.getChitduration();
				Double dividendValue1 = Dividend.doubleValue() / chitDuration1.doubleValue();
				Long div = Math.round(dividendValue1);
				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId3,chitId, dat,BigDecimal.valueOf(div),null, "OS-",CashAccountsForChit.CHIT_DIVIDEND_OWN,null,chitId);
			}
			
			

		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

	}
	
	@Transactional
	@Override
	public String closeChitGroup(final Long chitId,final LocalDate dateOfclosing) 
	{
		JsonObject respone = new JsonObject();
		ChitGroupData chitdata = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
		
		Long currentCycle = chitdata.getCurrentcycle();
		Long chitDuration = chitdata.getChitduration();
		
		if(currentCycle.compareTo(chitDuration)==0)
		{
			
			Iterator<ChitGroupSubscriberData> chitsubscribers = this.chitGroupReadPlatformService.getChitSubscribers(chitId).iterator();
			
			while(chitsubscribers.hasNext())
			{
				ChitGroupSubscriberData Subscribers = chitsubscribers.next();
				
				Long SubscriberId = Subscribers.getId();
				
				Long clientId = Subscribers.getClientId();
				
				Iterator<ChitDemandScheduleForMobile> demandDetails = this.chitDemandScheduleReadPlatformService.retriveData(clientId, dateOfclosing).iterator();
				
				while(demandDetails.hasNext())
				{
					ChitDemandScheduleForMobile demandData = demandDetails.next();
					
					Double OverDue = demandData.getOverDueAmount();
					Double Due =  demandData.getDueAmount();
					Double Penalty = demandData.getPenaltyAmount();
					Long demandId = demandData.getChitDemandId();
					Double CollectedAmount = this.chitDemandScheduleReadPlatformService.retrieveById(demandId).getCollectedAmount();
					Double totalAmount = OverDue+Due+Penalty;
					if(totalAmount.compareTo(CollectedAmount)==0)
					{
						JsonObject update = new JsonObject();
						update.addProperty("isactive", false);
						this.updateChitgroupSubscriber(SubscriberId, update);
					}
					else
					{
						JsonObject update = new JsonObject();
						update.addProperty("isactive", true);
						this.updateChitgroupSubscriber(SubscriberId, update);
						Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUTSTANDING_SUBSCRIPTION_CLOSED_GROUP.getValue().longValue());  
						Long plAccountId= null;
						for(Map.Entry<String, Object> itr : mapData3.entrySet())
						{
							if(itr.getKey().equals("glAccountId"))
							{
								plAccountId = (Long)itr.getValue();
							}
						}
						String currencyCode = "INR";
						Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(chitdata.getOfficeId());

						this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId,chitId,dateOfclosing,BigDecimal.valueOf(totalAmount),null, "BV-",CashAccountsForChit.CHIT_OUTSTANDING_SUBSCRIPTION_CLOSED_GROUP,SubscriberId,chitId);
					}
				}
			}
			JsonObject dataforupdatechitcycle = new JsonObject();
			dataforupdatechitcycle.addProperty("status", 30);
			dataforupdatechitcycle.addProperty("endDate", dateOfclosing.toString());
			this.updateChitGroupwithJSon(chitId, dataforupdatechitcycle);
			
			respone.addProperty("Status", "Success");
			respone.addProperty("Message", "The Group is Closed with the Status Code 30 ");
			return respone.toString();
		}
		else
		{
			
			respone.addProperty("Status", "Failed");
			respone.addProperty("Message", "The Group Cannot be Closed in the "+currentCycle+ " cycle");
			
			return respone.toString();
		}
			
	}
	
	@Transactional
	@Override
	public JsonObject winnerspayable(Long ChitSubsid,Double Amount,String date,Long bidId,Long accId,JsonObject apiRequestBodyAsJson)
	{
		Long paymentdetailId = null;
		if(apiRequestBodyAsJson.has("paymentInfo"))
		{
			JsonElement payment = apiRequestBodyAsJson.get("paymentInfo");
			JsonObject paymentObject = payment.getAsJsonObject();
			final Map<String, Object> changes = new LinkedHashMap<>();

			PaymentDetail responseofpaymentdetail = paymentDetailWritePlatformService.createAndPersistPaymentDetailJson(paymentObject, changes);
			paymentdetailId = responseofpaymentdetail.getId();
			
			if(paymentObject.get("accId")!=null && !paymentObject.get("accId").isJsonNull())
			{
				String accIdd= paymentObject.get("accId").getAsString();
				accId = Long.parseLong(accIdd);
			}
		}
		
		ChitSubscriberTransaction cst = new ChitSubscriberTransaction(null,ChitSubsid,null,Amount,ChitTransactionEnum.WINNERPRIZEMONEY,paymentdetailId,null,false,true);
		ChitSubscriberTransaction id = this.chitSubscriberTransactionRepository.saveAndFlush(cst);
		ChitGroupSubscriberData subsData = this.chitGroupReadPlatformService.getChitSubscriber(ChitSubsid);
		ChitGroupData chitdata = this.chitGroupReadPlatformService.retrieveChitGroup(subsData.getChitId());
		
		if(subsData.getBidAdvance()==true)
		{
			Amount = Amount - subsData.getToBePaidAmount();
		}
		
		Map<String, Object> mapData5 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_SUBSCRIPTION_PAYBLE.getValue().longValue());  
		Long plAccountId2= null;
		for(Map.Entry<String, Object> itr : mapData5.entrySet())
		{
			if(itr.getKey().equals("glAccountId"))
			{
				plAccountId2 = (Long)itr.getValue();
			}
		}
		Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(chitdata.getOfficeId());
		String currencyCode = "INR";
		LocalDate PayableDate = LocalDate.parse(date);
		this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, subsData.getChitId(), currencyCode,accId,subsData.getChitId(), PayableDate, BigDecimal.valueOf(Amount) ,id.getId(),"DR-",ChitSubsid);
		this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,subsData.getChitId(), currencyCode, plAccountId2,subsData.getChitId(),PayableDate,BigDecimal.valueOf(Amount),id.getId(), "CR-",CashAccountsForChit.CHIT_SUBSCRIPTION_PAYBLE,ChitSubsid,subsData.getChitId());
		JsonObject data = new JsonObject();
		data.addProperty("isPaid", true);
		this.chitBidsWritePlatformService.updateChitBidsJson(bidId, data);
		JsonObject response = new JsonObject();
		response.addProperty("Status", "Success");
		return response;
	}
	
	@Transactional
	@Override
	public JsonObject reBid(Long chitId, Long oldSubId, Long newSubId, Long cycle, LocalDate date, Long bidderparticipationId) {
		
			ChitCycleData cycleDatas = this.chitBidsReadPlatformService.getChitCycle(chitId, cycle);
			Long bidId = cycleDatas.getId().longValue();
			ChitBidsData cycleDataas = this.chitBidsReadPlatformService.retriveChitsDataForReBidChitGroup(bidId, chitId, cycle,oldSubId);
			//ChitBidsData newcycleDataas = this.chitBidsReadPlatformService.retriveChitsDataForReBidChitGroup(bidId, chitId, cycle,newSubId);
			ChitGroupSubscriberData oldSubData = this.chitGroupReadPlatformService.getChitSubscriber(oldSubId);
			ChitGroupSubscriberData newSubData = this.chitGroupReadPlatformService.getChitSubscriber(newSubId);
			ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
			JsonObject result = new JsonObject();
			if(cycleDataas.getBidWon() == true && cycleDataas.getIsPaid()!=true )
			{
//				if(newcycleDataas.getBidWon()!=true && newcycleDataas.getIsPaid()!=true && newSubData.getPrizedsubscriber()!=true)
				//{
					JsonObject d = new JsonObject();
					d.addProperty("bidWon", false);
					this.chitBidsWritePlatformService.updateChitBidsJson(cycleDataas.getId(), d);
			
					ChitBids cb = new ChitBids(newSubId,cycleDataas.getChitCycleId(),cycleDataas.getBidAmount(),true,bidderparticipationId,date,false);
					chitBidsRepository.saveAndFlush(cb);
					
					JsonObject dummy =new JsonObject();
					this.prizeMoneyCalculations(chitId, cycle, dummy);
					
					JsonObject com =new JsonObject();
					com.addProperty("prizedsubscriber", false);
					com.addProperty("prizedcycle", 0);
					this.updateChitgroupSubscriber(oldSubId, com);
					result.addProperty("status", "success");
//				} else {
//					result.addProperty("status", "New subscriber is not eligible For rebid!!!");
//				}
							
			} else {
				result.addProperty("status", "he Has Not Eligible for Rebid!");
			}	
		return result;	
	}

	@Transactional
	@Override
	public JsonObject bidAdvance(Long chitId, Long subscriberId,  Long bidAmountInPercent) {
		ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
		ChitGroupSubscriberData subdata = this.chitGroupReadPlatformService.getChitSubscriber(subscriberId);

		Double toBePaidAmount = 0.0;
		Long verificationFee = this.configurationReadPlatformService.retrieveGlobalConfiguration("Verification-Fee").getValue();
		Long formcommision = this.configurationReadPlatformService.retrieveGlobalConfiguration("Foreman-Commission").getValue();
		Long gst = this.configurationReadPlatformService.retrieveGlobalConfiguration("GST").getValue();
		Long chitValue = groupData.getChitvalue().longValue();
		Long bidPercent = bidAmountInPercent;
		Double advanceAmount = chitValue.doubleValue() - ((bidPercent.doubleValue()/100)*chitValue.doubleValue());
		
		Double verFee = verificationFee.doubleValue();
		
		Double formComm = ((formcommision.doubleValue()/100)*chitValue);
		
		Double gstAmount = ((gst.doubleValue()/100)*formComm);
		
		toBePaidAmount = advanceAmount - (verFee + gstAmount);
		
		JsonObject json = new JsonObject();
		JsonObject json2 = new JsonObject();
		
		if(subdata.getPrizedsubscriber()!=true && subdata.getPrizedcycle()==0)
		{
			if(subdata.getBidAdvance() != true)
			{
				 json.addProperty("bidAdvance", true);
				 json.addProperty("toBePaidAmount", toBePaidAmount);
				 json.addProperty("subStatusEnum", subscriberStatusEnum.BID_ADVANCE.getValue());
					
				 this.updateChitgroupSubscriber(subdata.getId(), json);
			} else if(subdata.getBidAdvance() == true && subdata.getToBePaidAmount() != null) {
				 json.addProperty("toBePaidAmount", toBePaidAmount);
				 this.updateChitgroupSubscriber(subdata.getId(), json);
			}else {
				System.out.println("Subscriber Already Taken the Advance!!!");
			}
			
			
			json2.addProperty("advanceAmount", advanceAmount);
			json2.addProperty("toBePaidAmount", toBePaidAmount);
			json2.addProperty("dividend", formComm);
			json2.addProperty("status", "success");
		} else {
			json2.addProperty("status", "Not allowed for Prized Subscribers!!");
		}
		
		return json2;
	}

	@Transactional
	@Override
	public JsonObject bidAdvancePayOut(Long chitId, Long subscriberId, JsonObject apiRequestBodyAsJson) {
		
		Long paymentdetailId = null;
		LocalDate transactionDate = null;
		Long accountId = null;
		
		if(apiRequestBodyAsJson.has("paymentInfo"))
		{
			JsonElement payment = apiRequestBodyAsJson.get("paymentInfo");
			JsonObject parsedData = payment.getAsJsonObject();
			final Map<String, Object> changes = new LinkedHashMap<>();

			PaymentDetail responseofpaymentdetail = paymentDetailWritePlatformService.createAndPersistPaymentDetailJson(parsedData, changes);
			paymentdetailId = responseofpaymentdetail.getId();
			
			
			if(parsedData.get("transactionDate")!=null && !parsedData.get("transactionDate").isJsonNull())
			{
				String datee = parsedData.get("transactionDate").getAsString();
				transactionDate = LocalDate.parse(datee); 
			}
			if(parsedData.get("accId")!=null && !parsedData.get("accId").isJsonNull())
			{
				String accId = parsedData.get("accId").getAsString();
				accountId =  Long.parseLong(accId);
			}
		}
		
		ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
		ChitGroupSubscriberData subdata = this.chitGroupReadPlatformService.getChitSubscriber(subscriberId);

		ChitSubscriberTransaction cst = new ChitSubscriberTransaction(null,subscriberId,null,subdata.getToBePaidAmount(),ChitTransactionEnum.CHARGES,paymentdetailId,null,false,true);
		ChitSubscriberTransaction id = this.chitSubscriberTransactionRepository.saveAndFlush(cst);
		
		String name = "BidAdvance";
		Integer codeValueId = 0;
		Collection<CodeValueData> codeData = this.codeValueReadPlatformService.retrieveCodeValuesByCode("subscriberStatus");
		Iterator<CodeValueData> itr = codeData.iterator();
		while(itr.hasNext())
		{
			CodeValueData valueData = itr.next();
			if(name.equalsIgnoreCase(valueData.getName()))
			{
				codeValueId = valueData.getId().intValue();
				break;
			}
		}
		
		JsonObject obj = new JsonObject();
		JsonObject object = new JsonObject();
		if(subdata.getBidAdvance()==true && subdata.getToBePaidAmount()!=null)
		{
			 object.addProperty("isProcessed", true);
			 object.addProperty("status", codeValueId);
			 this.updateChitgroupSubscriber(subdata.getId(), object);
			 
			 Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.BID_ADVANCE.getValue().longValue());  
				Long plAccountId= null;
				for(Map.Entry<String, Object> itr1 : mapData3.entrySet())
				{
					if(itr1.getKey().equals("glAccountId"))
					{
						plAccountId = (Long)itr1.getValue();
						System.out.println("accountId"+plAccountId);
					}
				}
				String currencyCode = "INR";
				Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(groupData.getOfficeId());
				this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,plAccountId,id.getId(), transactionDate, BigDecimal.valueOf(subdata.getToBePaidAmount()) ,id.getId(),"DR-",subscriberId);		 
				
				this.accountingProcessorHelper.createCreditJournalEntryForChitCollection(office, subdata.getChitId(), currencyCode,accountId,id.getId(), transactionDate, BigDecimal.valueOf(subdata.getToBePaidAmount().doubleValue()) ,id.getId(),"CR-", subscriberId);
				
				obj.addProperty("status", "success");
		} else {
			object.addProperty("status", "he has not taken advance!!");
		}
		return obj;
	}

	@Transactional
	@Override
	public JsonObject terminateSubscriber(Long chitId, Long clientId, Long ticketNumber) {
		// TODO Auto-generated method stub
		ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
		ChitGroupSubscriberData subscriberData = this.chitGroupReadPlatformService.getChitSubscriberUsingChitIDClientId(chitId, clientId, ticketNumber);
		ClientData clientData = this.clientReadPlatformService.retrieveOne(clientId);
    	ChitCycleData cycleData = chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, groupData.getCurrentcycle());

		Double sum= 0.0;
		Double paidAmount = 0.0;
		Double toBePaidAmount  = 0.0;
		Double actualPaidAmount = null;
		Long dividend = 0l;
		JsonObject json = new JsonObject();
		Long cycleNumber = groupData.getCurrentcycle().longValue();
		Long chitChargeId = this.chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT").getId();
			String name = "Terminated";
			Integer codeValueId = 0;
			Collection<CodeValueData> codeData = this.codeValueReadPlatformService.retrieveCodeValuesByCode("subscriberStatus");
			Iterator<CodeValueData> itr = codeData.iterator();
			while(itr.hasNext())
			{
				CodeValueData valueData = itr.next();
				if(name.equalsIgnoreCase(valueData.getName()))
				{
					codeValueId = valueData.getId().intValue();
					break;
				}
			}
			Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  
			Long plAccountId= null;
			for(Map.Entry<String, Object> itr2 : mapData3.entrySet())
			{
				if(itr2.getKey().equals("glAccountId"))
				{
					plAccountId = (Long)itr2.getValue();
				}
			}
			Map<String, Object> mapData2 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_TERMINATED_SUBSCRIBER_PAYBLE.getValue().longValue());  
			Long plAccountId1= null;
			for(Map.Entry<String, Object> itr4 : mapData3.entrySet())
			{
				if(itr4.getKey().equals("glAccountId"))
				{
					plAccountId1 = (Long)itr4.getValue();
				}
			}
			Map<String, Object> mapData1 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_TERMINATED_SUBSCRIBER_DIVIDEND.getValue().longValue());  
			Long plAccountId2= null;
			for(Map.Entry<String, Object> itr5 : mapData3.entrySet())
			{
				if(itr5.getKey().equals("glAccountId"))
				{
					plAccountId1 = (Long)itr5.getValue();
				}
			}
			if(subscriberData.getPrizedsubscriber() != true && subscriberData.getBidAdvance() != true && (subscriberData.getStatus() == null ||  !subscriberData.getStatus().equals(codeValueId))) {
				//ChitBidsData bidData = chitBidsReadPlatformService.retriveChitsDataForChitGroup(cycleData.getId(), chitId, cycleData.getCycleNumber(), 0);
				ChitBidsData bidData = null;
				try {
					bidData = this.chitBidsReadPlatformService.retriveChitsDataForReBidChitGroup(cycleData.getId(), chitId, cycleData.getCycleNumber(), subscriberData.getId());
				}catch(Exception e) {
					System.out.println("No Bid Data!!");
				}
				if(bidData != null) {
					if(bidData.getBidWon() != true && bidData.getIsPaid() != true) {
						while(cycleNumber!=0)
						{
							ChitCycleData groupCycleData = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, cycleNumber);
							ChitSubscriberChargeData chargeData = this.chitSubscriberChargeReadPlatformServices.retrieveById(subscriberData.getId(), chitChargeId, groupCycleData.getId().longValue());
							Double demandData = this.chitDemandScheduleReadPlatformService.getSubscriberPaidedAmount(chargeData.getId(), chargeData.getStaffId(), chitId);
							sum = sum + demandData;
							if(groupCycleData.getDividend() != null) {
								dividend = dividend.longValue() + groupCycleData.getDividend().longValue();
								}
							cycleNumber = cycleNumber - 1;
						}
						toBePaidAmount = groupData.getChitvalue() - sum;
						paidAmount = sum;
						
						json.addProperty("paidAmount", paidAmount);
						json.addProperty("ticketNumber", subscriberData.getChitNumber());
						json.addProperty("subscriberId", subscriberData.getId());
						json.addProperty("groupName", groupData.getName());
						json.addProperty("clientName", clientData.getFirstname());
						
						json.addProperty("status", "success"); 
						
					} else {
						json.addProperty("status", "Not allowed for Prized SUbscribers");
					}
				} else {
					while(cycleNumber!=0)
					{
						ChitCycleData groupCycleData = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, cycleNumber);
						ChitSubscriberChargeData chargeData = this.chitSubscriberChargeReadPlatformServices.retrieveById(subscriberData.getId(), chitChargeId, groupCycleData.getId().longValue());
						Double demandData = this.chitDemandScheduleReadPlatformService.getSubscriberPaidedAmount(chargeData.getId(), chargeData.getStaffId(), chitId);
						sum = sum + demandData;
						if(groupCycleData.getDividend() != null) {
						dividend = dividend.longValue() + groupCycleData.getDividend().longValue();
						}
						cycleNumber = cycleNumber - 1;
					}
					toBePaidAmount = groupData.getChitvalue() - sum;
					paidAmount = sum;
					
					json.addProperty("paidAmount", paidAmount);
					json.addProperty("ticketNumber", subscriberData.getChitNumber());
					json.addProperty("subscriberId", subscriberData.getId());
					json.addProperty("groupName", groupData.getName());
					json.addProperty("clientName", clientData.getFirstname());
					
					json.addProperty("status", "success");
			     }
				
				ChitSubscriberTransaction cst = new ChitSubscriberTransaction(null,subscriberData.getId(),null,paidAmount,null,null,null,false,true);
				ChitSubscriberTransaction id = this.chitSubscriberTransactionRepository.saveAndFlush(cst);
				
				String currencyCode = "INR";
				Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(groupData.getOfficeId());
				LocalDate curDate =  LocalDate.now(ZoneId.systemDefault());
				Double amountForoutsub = paidAmount + dividend;
				this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,plAccountId,chitId, curDate, BigDecimal.valueOf(amountForoutsub) ,id.getId(),"DR-",subscriberData.getId());
				
				this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId1,chitId,curDate,BigDecimal.valueOf(paidAmount),id.getId(), "CR-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,subscriberData.getId(),chitId);
			
				if(dividend != null || dividend != 0.0) {
					this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId2,chitId,curDate,BigDecimal.valueOf(dividend),id.getId(), "CR-",CashAccountsForChit.CHIT_TERMINATED_SUBSCRIBER_DIVIDEND,subscriberData.getId(),chitId);
				}
			} else {
				json.addProperty("status", "Not allowed for Prized SUbscribers");
			}
		return json;
	}
	
	@Transactional
	@Override
	public JsonObject forClosure(Long chitId, Long clientId, Long ticketNumber) {
		// TODO Auto-generated method stub
		ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
		ChitGroupSubscriberData subscriberData = this.chitGroupReadPlatformService.getChitSubscriberUsingChitIDClientId(chitId, clientId, ticketNumber);
		ClientData clientData = this.clientReadPlatformService.retrieveOne(clientId);
		
		Double sum= 0.0;
		Double paidAmount = 0.0;
		Double toBePaidAmount  = 0.0;
		JsonObject json = new JsonObject();
		Long cycleNumber = groupData.getCurrentcycle().longValue();
		Long chitChargeId = this.chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT").getId();
		if(subscriberData.getPrizedsubscriber() == true)
		{
			while(cycleNumber!=0)
			{
				ChitCycleData groupCycleData = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, cycleNumber);
				ChitSubscriberChargeData chargeData = this.chitSubscriberChargeReadPlatformServices.retrieveById(subscriberData.getId(), chitChargeId, groupCycleData.getId().longValue());
				Double demandData = this.chitDemandScheduleReadPlatformService.getSubscriberPaidedAmount(chargeData.getId(), chargeData.getStaffId(), chitId);
				sum = sum + demandData;
				cycleNumber = cycleNumber - 1;
			}
			toBePaidAmount = groupData.getChitvalue() - sum;
			paidAmount = sum;
				
			json.addProperty("paidAmount", paidAmount);
			json.addProperty("toBePaidAmount", toBePaidAmount);
			json.addProperty("ticketNumber", subscriberData.getChitNumber());
			json.addProperty("subscriberId", subscriberData.getId());
			json.addProperty("groupName", groupData.getName());
			json.addProperty("clientName", clientData.getFirstname());
			
			
			json.addProperty("status", "success"); 
		} else {
			json.addProperty("status", "Not allowed for Non Prized Subscriber!!");
		}
		return json;
	}
	
	@Transactional
	@Override
	public JsonObject forClosureAdjust(Long subscriberId,JsonObject command) {
		Long chitId = command.get("chitId").getAsLong();
		LocalDate demandDate = null;
		
		ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
		ChitCycleData cycleData = this.chitBidsReadPlatformService.getChitCycle(chitId, groupData.getCurrentcycle());
		ChitSubscriberChargeData chargeData = this.chitSubscriberChargeReadPlatformServices.retrieveById(subscriberId, 2l, cycleData.getId());
		Double collectedAmount = null;
		Long paymentdetailId = null;
		
		if(command.has("paymentInfo"))
		{
			JsonElement payment = command.get("paymentInfo");
			JsonObject parsedData = payment.getAsJsonObject();
			final Map<String, Object> changes = new LinkedHashMap<>();

			PaymentDetail responseofpaymentdetail = paymentDetailWritePlatformService.createAndPersistPaymentDetailJson(parsedData, changes);
			paymentdetailId = responseofpaymentdetail.getId();
			
			if(parsedData.get("amount")!= null && !parsedData.get("amount").isJsonNull()) {
				String parseAmount = parsedData.get("amount").getAsString();
				collectedAmount = Double.parseDouble(parseAmount);
			}
			if(parsedData.get("transactionDate") != null && !parsedData.get("transactionDate").isJsonNull()) {
				String dmdate = parsedData.get("transactionDate").getAsString();
				demandDate = LocalDate.parse(dmdate);
			}
		}
		
		ChitSubscriberTransaction cst = new ChitSubscriberTransaction(null,subscriberId,null,collectedAmount,ChitTransactionEnum.FORCLOSURE,paymentdetailId,null,false,true);
		ChitSubscriberTransaction id = this.chitSubscriberTransactionRepository.saveAndFlush(cst);
		
		ChitDemandScheduleData demandData = this.chitDemandScheduleReadPlatformService.retrieveByIdAndDate(chargeData.getId(), chargeData.getStaffId(), demandDate);
		
		//need clarity from steephan sir and yashas, here we just adjust the  amount existing demand  
		//i have written the code below but im not sure 
		
//		if(demandData.getIsCalculated() != true) {
//			
//			JsonObject json = new JsonObject();
//			json.addProperty("installmentAmount", collectedAmount);
//			this.chitDemandScheduleWritePlatformService.updateChitDemandSchedule(demandData.getId(), json);
//
//		}
		
		ChitGroupSubscriberData chitSubscriberData = this.chitGroupReadPlatformService.getChitSubscriber(subscriberId);
		JsonObject object = new JsonObject();
	
		object.addProperty("subStatusEnum", subscriberStatusEnum.FORECLOSURE.getValue());
		object.addProperty("toBePaidAmount", collectedAmount);
		this.updateChitgroupSubscriber(subscriberId, object);
		
		JsonObject obj = new JsonObject();
		obj.addProperty("status", "success");
		return obj;
	}
	
	@Transactional
	@Override
	public JsonObject forClosureApproval(Long subscriberId, JsonObject command) { 
		Integer codeValueId = null;
		Long accountId = null;
		Long chitId = command.get("chitId").getAsLong();
		ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
		
		String name = "Fore-Closed";
		Collection<CodeValueData> codeData = this.codeValueReadPlatformService.retrieveCodeValuesByCode("subscriberStatus");
		Iterator<CodeValueData> itr = codeData.iterator();
		
		while(itr.hasNext())
		{
			CodeValueData valueData = itr.next();
			if(name.equalsIgnoreCase(valueData.getName()))
			{
				codeValueId = valueData.getId().intValue();
				break;
			}
		}
		
		ChitSubscriberTransactionData chitSubData = this.chitSubscriberTransactionReadPlatformService.retrieveData(subscriberId, ChitTransactionEnum.FORCLOSURE.getValue().intValue());
		Collection<BranchesAccountData> branchData = this.branchesAccountReadPlatformService.retrieveBranchesById(groupData.getOfficeId());
		Iterator<BranchesAccountData> itr1 = branchData.iterator();
			
			while(itr1.hasNext())
			{
				BranchesAccountData valueData = itr1.next();
				if(groupData.getOfficeId().longValue() == valueData.getBranchId().longValue())
				{
					PaymentDetailData payData = this.paymentDetailsReadPlatformService.retrivePaymentDetails(chitSubData.getPaymentdetailId());
					PaymentTypeData  typeData = this.paymentTypeReadPlatformService.retrieveOne(payData.getPaymentType().getId());
					String search = "BANK";
					if(search.equalsIgnoreCase(typeData.getName())) {
						accountId = valueData.getBankglAccountId();
						break;
					} else {
						accountId = valueData.getCashglAccountId();
						break;
					}
				}
			}
		
		 Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  
			Long plAccountId= null;
			for(Map.Entry<String, Object> itr3 : mapData3.entrySet())
			{
				if(itr3.getKey().equals("glAccountId"))
				{
					plAccountId = (Long)itr3.getValue();
					System.out.println("accountId"+plAccountId);
				}
			}
			String currencyCode = "INR";
			Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(groupData.getOfficeId());
			LocalDate trnDate = chitSubData.getTransactionDate().toLocalDate();

			this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId,chitId,trnDate,BigDecimal.valueOf(chitSubData.getAmount()),chitSubData.getId(), "CR-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,subscriberId,chitId);

			this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,accountId,chitId, trnDate, BigDecimal.valueOf(chitSubData.getAmount()) ,chitSubData.getId(),"DR-",subscriberId);
			
			JsonObject object = new JsonObject();
			object.addProperty("status", codeValueId);
			object.addProperty("isactive", false);
			this.updateChitgroupSubscriber(subscriberId, object);
			
			JsonObject json = new JsonObject();
			json.addProperty("status", "success");
		return json;
	}

	@Transactional
	@Override
	public JsonObject terminatePayout(Long chitId, Long subscriberId, Double paybleAmount) {

		ChitGroupSubscriberData subscriberData = this.chitGroupReadPlatformService.getChitSubscriber(subscriberId);
		JsonObject json = new JsonObject();
		if(subscriberData.getBidAdvance() != true)
		{
			json.addProperty("subStatusEnum", subscriberStatusEnum.TERMINATE.getValue());
			json.addProperty("toBePaidAmount", paybleAmount);
			this.updateChitgroupSubscriber(subscriberId, json);
			json.addProperty("status", "success");
		}
		return json;
	}
	
	@Transactional
	@Override
	public JsonObject terminateApproval(Long subscriberId, JsonObject command) { 
		Integer codeValueId = null;
		Long accountId = null;
		Long chitId = command.get("chitId").getAsLong();
		ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
		ChitGroupSubscriberData subscriberData = this.chitGroupReadPlatformService.getChitSubscriber(subscriberId);
		String name = "Terminated";
		Long formcommision = this.configurationReadPlatformService.retrieveGlobalConfiguration("Foreman-Commission").getValue();
		Long gst = this.configurationReadPlatformService.retrieveGlobalConfiguration("GST").getValue();
		Long cgst = this.configurationReadPlatformService.retrieveGlobalConfiguration("CGST").getValue();
		Double formComm = ((formcommision.doubleValue()/100)*groupData.getChitvalue().doubleValue());
		
		Collection<CodeValueData> codeData = this.codeValueReadPlatformService.retrieveCodeValuesByCode("subscriberStatus");
		Iterator<CodeValueData> itr = codeData.iterator();
		
		while(itr.hasNext())
		{
			CodeValueData valueData = itr.next();
			if(name.equalsIgnoreCase(valueData.getName()))
			{
				codeValueId = valueData.getId().intValue();
				break;
			}
		}
		ChitSubscriberTransactionData chitSubData = this.chitSubscriberTransactionReadPlatformService.retrieveData(subscriberId, ChitTransactionEnum.TERMINATE.getValue());
		
		Collection<BranchesAccountData> branchData = this.branchesAccountReadPlatformService.retrieveBranchesById(groupData.getOfficeId());
		Iterator<BranchesAccountData> itr1 = branchData.iterator();
			
			while(itr1.hasNext())
			{
				BranchesAccountData valueData = itr1.next();
				if(groupData.getOfficeId().longValue() == valueData.getBranchId().longValue())
				{
					PaymentDetailData payData = this.paymentDetailsReadPlatformService.retrivePaymentDetails(chitSubData.getPaymentdetailId());
					PaymentTypeData  typeData = this.paymentTypeReadPlatformService.retrieveOne(payData.getPaymentType().getId());
					String search = "BANK";
					if(search.equalsIgnoreCase(typeData.getName())) {
						accountId = valueData.getBankglAccountId();
					} else {
						accountId = valueData.getCashglAccountId();
					}
					
					break;
				}
			}
		Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_TERMINATED_SUBSCRIBER_PAYBLE.getValue().longValue());  
			Long plAccountId= null;
			for(Map.Entry<String, Object> itr2 : mapData3.entrySet())
			{
				if(itr2.getKey().equals("glAccountId"))
				{
					plAccountId = (Long)itr2.getValue();
					System.out.println("accountId"+plAccountId);
				}
			}
			
			Map<String, Object> mapData4 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_REPLACEMENT_CHARGES.getValue().longValue());  
			Long plAccountId3= null;
			for(Map.Entry<String, Object> itr4 : mapData4.entrySet())
			{
				if(itr4.getKey().equals("glAccountId"))
				{
					plAccountId3 = (Long)itr4.getValue();
					System.out.println("accountId"+plAccountId3);
				}
			}
			
			String currencyCode = "INR";
			Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(groupData.getOfficeId());
			LocalDate trnDate = chitSubData.getTransactionDate().toLocalDate();
			Double stdDedAmount = formComm ;
			Double cgstValue = null;
			Double replacement = null;
			if(chitSubData.getWaiveOffAmount() == null || chitSubData.getWaiveOffAmount() == 0l) {
				 cgstValue = (cgst.doubleValue()/100) * stdDedAmount;
				 replacement = formComm;
			} else {
				stdDedAmount = stdDedAmount - chitSubData.getWaiveOffAmount();
				cgstValue = (cgst.doubleValue()/100) * stdDedAmount;
				replacement = stdDedAmount;
			}
			
			Double totAmt = subscriberData.getToBePaidAmount().doubleValue() + replacement.doubleValue();
			
			this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(office, chitId, currencyCode,plAccountId,chitId, trnDate, BigDecimal.valueOf(totAmt) ,chitSubData.getId(),"DR-",subscriberId);
			
			this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, accountId,chitId,trnDate,BigDecimal.valueOf(replacement),chitSubData.getId(), "CR-",CashAccountsForChit.CHIT_REPLACEMENT_CHARGES,subscriberId,chitId);
			
			this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId3,chitId,trnDate,BigDecimal.valueOf(cgstValue),chitSubData.getId(), "CR-",CashAccountsForChit.CHIT_CGST,subscriberId,chitId);
			
			this.accountingProcessorHelper.createCreditJournalEntryForOthers(office,chitId, currencyCode, plAccountId3,chitId,trnDate,BigDecimal.valueOf(cgstValue),chitSubData.getId(), "CR-",CashAccountsForChit.CHIT_SGST,subscriberId,chitId);
			
			this.accountingProcessorHelper.createCreditJournalEntryForChitCollection(office, chitId, currencyCode,accountId, chitId, trnDate, BigDecimal.valueOf(chitSubData.getAmount()) ,chitSubData.getId(),"CR-", subscriberId);
				
			JsonObject object = new JsonObject();
			object.addProperty("status", codeValueId);
			object.addProperty("isactive", false);
			this.updateChitgroupSubscriber(subscriberId, object);
			
			JsonObject json = new JsonObject();
			json.addProperty("status", "success");
			
		return json;
	}

	@Transactional
	@Override
	public JsonObject terminateAdjust(Long subscriberId, JsonObject command) {
			Long paymentdetailId = null;
			Double paidAmount = null;
			Long chitId = command.get("chitId").getAsLong();
			ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
			ChitGroupSubscriberData subscriberData = this.chitGroupReadPlatformService.getChitSubscriber(subscriberId);
			Long formcommision = this.configurationReadPlatformService.retrieveGlobalConfiguration("Foreman-Commission").getValue();
			Double formComm = ((formcommision.doubleValue()/100)*groupData.getChitvalue().doubleValue());
			Long waiveOffAmount = 0l;
			LocalDate transactionDate = null;
			
			if(command.get("waiveOffAmount") != null && !command.get("waiveOffAmount").isJsonNull()) {
				String amt = command.get("waiveOffAmount").getAsString();
				waiveOffAmount = Long.parseLong(amt);
			}
			
			if(command.has("paymentInfo"))
			{
				JsonElement payment = command.get("paymentInfo");
				JsonObject parsedData = payment.getAsJsonObject();
				final Map<String, Object> changes = new LinkedHashMap<>();

				PaymentDetail responseofpaymentdetail = paymentDetailWritePlatformService.createAndPersistPaymentDetailJson(parsedData, changes);
				paymentdetailId = responseofpaymentdetail.getId();
				
				if(parsedData.get("amount")!= null && !parsedData.get("amount").isJsonNull()) {
					String parseAmount = parsedData.get("amount").getAsString();
					paidAmount = Double.parseDouble(parseAmount);
				}
				if(parsedData.get("transactionDate")!= null && !parsedData.get("transactionDate").isJsonNull()) {
					String parseDate = parsedData.get("transactionDate").getAsString();
					transactionDate = LocalDate.parse(parseDate);
				}
			}
			
			String currencyCode = "INR";
			Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(groupData.getOfficeId());
			Double stdDedAmount = formComm;
				paidAmount = paidAmount - stdDedAmount;
			
			System.out.println(paidAmount+"001");
			if(waiveOffAmount != null || waiveOffAmount != 0l) {
				stdDedAmount = stdDedAmount - waiveOffAmount.doubleValue();
				System.out.println(stdDedAmount+"002");
				paidAmount = paidAmount + waiveOffAmount.doubleValue();
				System.out.println(paidAmount+"003");
			}
			
			 ZoneId zone = ZoneId.systemDefault();
			 LocalTime timeOfDayNow = LocalTime.now(zone);
			 LocalDateTime dateTime = transactionDate.atTime(timeOfDayNow);
				
			ChitSubscriberTransaction cst = new ChitSubscriberTransaction(null,subscriberId,null,paidAmount,ChitTransactionEnum.TERMINATE,paymentdetailId,dateTime,false,true,waiveOffAmount);
			ChitSubscriberTransaction id = this.chitSubscriberTransactionRepository.saveAndFlush(cst);
			
			JsonObject object = new JsonObject();
			object.addProperty("isProcessed", true);
			object.addProperty("toBePaidAmount", paidAmount);
			this.updateChitgroupSubscriber(subscriberId, object);
			
			JsonObject data = new JsonObject();
			data.addProperty("toBePaidAmount", subscriberData.getToBePaidAmount());
			data.addProperty("stdDedAmount", stdDedAmount);
			data.addProperty("waiveOffAmount", waiveOffAmount);
			data.addProperty("status", "success");
			return data;
	}
	
	@Transactional
	@Override
	public JsonObject replaceSubscriber(Long chitId, Long clientId) {
		// TODO Auto-generated method stub
		Double paybleAmount = null;
		Long dividend = 0l;
		Double perCycleAmount = null;
		ChitGroupData groupData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
    	Long cycleNumber = groupData.getCurrentcycle().longValue();
    	perCycleAmount = groupData.getChitvalue().doubleValue() / groupData.getChitduration().doubleValue();
    	paybleAmount = perCycleAmount * (cycleNumber -1);
    	while(cycleNumber!=0)
		{
			ChitCycleData groupCycleData = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, cycleNumber);
			if(groupCycleData.getDividend() != null || groupCycleData.getDividend() != 0l) {
				dividend += groupCycleData.getDividend().longValue();
				}
			cycleNumber = cycleNumber - 1;
		}
    	
    	if(dividend!=null || dividend!=0l) {
    		paybleAmount -= dividend;
    	}
    	
    	Double clientAdvanceAmount = this.clientTransactionReadPlatformService.retriveAdvanceAmountForNotAdjusted(clientId);
    	System.out.println(clientAdvanceAmount);
    	
    	JsonObject obj = new JsonObject();
    	obj.addProperty("toBePaidAmount", paybleAmount);
    	obj.addProperty("clientAdvanceAmount", clientAdvanceAmount);
    	obj.addProperty("status", "success");
    	
    	return obj;
	}
}




