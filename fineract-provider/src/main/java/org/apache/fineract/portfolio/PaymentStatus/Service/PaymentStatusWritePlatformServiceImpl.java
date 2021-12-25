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
package org.apache.fineract.portfolio.PaymentStatus.Service;


import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.Map;


import javax.persistence.PersistenceException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.accounting.journalentry.data.JournalEntryData;
import org.apache.fineract.accounting.journalentry.service.JournalEntryReadPlatformService;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
//import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.ChitGroup.data.ChitChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitCycleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberTransactionData;
import org.apache.fineract.portfolio.ChitGroup.service.ChitChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitCycleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitDemandScheduleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitDemandScheduleWritePlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberTransactionReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberTransactionWritePlatformService;
import org.apache.fineract.portfolio.PaymentStatus.domain.PaymentStatus;
import org.apache.fineract.portfolio.PaymentStatus.domain.PaymentStatusRepository;

import org.apache.fineract.portfolio.PaymentStatus.serialization.PaymentStatusCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.client.data.ClientTransactionData;
import org.apache.fineract.portfolio.client.service.ClientTransactionReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientTransactionWritePlatformService;
import org.apache.fineract.portfolio.paymentdetail.Exception.PaymentDetailNotFoundException;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailsReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

@Service
public class PaymentStatusWritePlatformServiceImpl implements PaymentStatusWritePlatformService
{
	//private final PaymentStatusRepository paymentStatusRepository;

	private final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService;

	private final ClientTransactionReadPlatformService clientTransactionReadPlatformService ; 

	private final ClientTransactionWritePlatformService clientTransactionWritePlatformService ; 

	private final ChitSubscriberTransactionWritePlatformService chitSubscriberTransactionWritePlatformService ; 

	private final JournalEntryReadPlatformService journalEntryReadPlatformService ; 

	private final JournalEntryWritePlatformService journalEntryWritePlatformService ; 

	private final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService;

	private final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices ;

	private final ChitCycleReadPlatformService chitCycleReadPlatformService ;

	private final ChitGroupReadPlatformService chitGroupReadPlatformService;

	private final ChitChargeReadPlatformServices chitChargeReadPlatformServices;

	private final ChitDemandScheduleWritePlatformService chitDemandScheduleWritePlatformService;
	
	private final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService;
	
	private final PaymentStatusCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	//private final OfficeRepositoryWrapper officeRepositoryWrapper;
	private final PaymentStatusRepository paymentStatusRepository;

	@Autowired
	public PaymentStatusWritePlatformServiceImpl(ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService ,
			ClientTransactionReadPlatformService clientTransactionReadPlatformService, ClientTransactionWritePlatformService clientTransactionWritePlatformService, 
			ChitSubscriberTransactionWritePlatformService chitSubscriberTransactionWritePlatformService,
			JournalEntryReadPlatformService journalEntryReadPlatformService, JournalEntryWritePlatformService journalEntryWritePlatformService,
			final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService, final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices,
			final ChitCycleReadPlatformService chitCycleReadPlatformService,
			final ChitGroupReadPlatformService chitGroupReadPlatformService,
			final ChitChargeReadPlatformServices chitChargeReadPlatformServices,
			final ChitDemandScheduleWritePlatformService chitDemandScheduleWritePlatformService,
			final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService,
			final PaymentStatusCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final PaymentStatusRepository paymentStatusRepository) {


		this.chitSubscriberTransactionReadPlatformService = chitSubscriberTransactionReadPlatformService;
		this.clientTransactionReadPlatformService = clientTransactionReadPlatformService;
		this.clientTransactionWritePlatformService = clientTransactionWritePlatformService;
		this.chitSubscriberTransactionWritePlatformService = chitSubscriberTransactionWritePlatformService;
		this.journalEntryReadPlatformService = journalEntryReadPlatformService;
		this.journalEntryWritePlatformService = journalEntryWritePlatformService;
		this.chitDemandScheduleReadPlatformService = chitDemandScheduleReadPlatformService;
		this.chitSubscriberChargeReadPlatformServices = chitSubscriberChargeReadPlatformServices;
		this.chitCycleReadPlatformService = chitCycleReadPlatformService;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.chitChargeReadPlatformServices = chitChargeReadPlatformServices;
		this.chitDemandScheduleWritePlatformService = chitDemandScheduleWritePlatformService;
		this.paymentDetailsReadPlatformService = paymentDetailsReadPlatformService;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.paymentStatusRepository = paymentStatusRepository;
	}


	//	@Transactional
	//	@Override
	//	public CommandProcessingResult CreatePaymentStatusData(JsonObject command) {
	//
	//		try {
	//		
	//			PaymentStatus paymentData = PaymentStatus.create(command);
	//			PaymentStatus newPaymentData = this.paymentStatusRepository.save(paymentData);
	//
	//			CommandProcessingResult result = new CommandProcessingResultBuilder() //
	//					.withCommandId(newPaymentData.getId()) //
	//					.withEntityId(newPaymentData.getId()) //
	//					.build();
	//			return result;
	//
	//		} catch (final PersistenceException dve) {
	//			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
	//			handleChitBidsDataIntegrityIssues(command, throwable, dve);
	//			return CommandProcessingResult.empty();
	//		}
	//	}

	//	@Transactional
	//	@Override
	//	public CommandProcessingResult updatePaymentStatusData(Long id ,JsonObject command) 
	//	{
	//		try {
	//			
	//			final PaymentStatus PaymentStatusForUpdate = this.paymentStatusRepository.findById(id)
	//					.orElseThrow(() -> new ChitBidNotFoundException(id));
	//			final Map<String, Object> changesOnly = PaymentStatusForUpdate.update(command);
	//
	//			if (!changesOnly.isEmpty()) {
	//				this.paymentStatusRepository.saveAndFlush(PaymentStatusForUpdate);
	//			}
	//
	//			return new CommandProcessingResultBuilder().withCommandId(id).withEntityId(id).build();
	//	
	//		} catch (final PersistenceException dve) {
	//			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
	//			handleChitBidsDataIntegrityIssues(command, throwable, dve);
	//			return CommandProcessingResult.empty();
	//		}
	//
	//	}


	//	private void handleChitBidsDataIntegrityIssues(final JsonObject command, final Throwable realCause,
	//			final Exception dve) {
	//		throw new PlatformDataIntegrityException("error.msg.paymentStatus.unknown.data.integrity.issue",
	//				"Unknown data integrity issue with resource: " + realCause.getMessage());
	//	}


	@Override
	public String ReversePayments(Long TransactionId) 
	{
		Boolean flag = false;
		if(TransactionId!=null)
		{
			ClientTransactionData clienttranId = this.clientTransactionReadPlatformService.retrieveTransactionByTransactionId(TransactionId);
			String message = null;
			if(clienttranId!=null)
			{
				Long clientId = clienttranId.getClientId();
				JsonObject update = new JsonObject();
				message = clienttranId.getPaymentDetailData().getPaymentType().getName();
				update.addProperty("isreversed", true);
				CommandProcessingResult response = this.clientTransactionWritePlatformService.UpdateClientTransaction(clientId, TransactionId, update);

			}
			else
			{
				ChitSubscriberTransactionData chittransaction = chitSubscriberTransactionReadPlatformService.retrieveById(TransactionId);

				if(chittransaction!=null)
				{
					Long paymentId = chittransaction.getPaymentdetailId();
					message = paymentDetailsReadPlatformService.retrivePaymentDetails(paymentId).getPaymentType().getName();
					JsonObject update = new JsonObject();
					update.addProperty("isreversed", true);
					CommandProcessingResult response = chitSubscriberTransactionWritePlatformService.updateChitSubscriberCharge(TransactionId, update);
					Double amt = chittransaction.getAmount();
					Long demandId = chittransaction.getChitdemandscheduleId();
					LocalDateTime transactionDate = chittransaction.getTransactionDate();
					ChitDemandScheduleData demandData = chitDemandScheduleReadPlatformService.retrieveById(demandId);
					if(demandData!=null)
					{
						Long chitId = demandData.getChitId();
						ChitGroupData chitgroup = chitGroupReadPlatformService.retrieveChitGroup(chitId);

						if(chitgroup!=null)
						{
							Long currentCycle = chitgroup.getCurrentcycle();
							Long chitsubsId = demandData.getChitSubscriberChargeId();
							ChitSubscriberChargeData subsChargeData = this.chitSubscriberChargeReadPlatformServices.retrieveNameById(chitsubsId);
							if(subsChargeData!=null)
							{

								Long subsId = subsChargeData.getChitSubscriberId();

								ChitCycleData cycleData = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, currentCycle);

								Long cycleId = cycleData.getId();

								ChitChargeData chitchargeIdss = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
								
								

								ChitSubscriberChargeData chargeData = this.chitSubscriberChargeReadPlatformServices.retrieveById(subsId, chitchargeIdss.getId(), cycleId);

								if(chargeData!=null)
								{
									Iterator<ChitDemandScheduleData> demandDatas = this.chitDemandScheduleReadPlatformService.retriveDemandSchedules(chargeData.getId(), false).iterator();
									Long demandIds = null;


									demandIds = demandDatas.next().getId();
									ChitDemandScheduleData todaysdemand = this.chitDemandScheduleReadPlatformService.retrieveById(demandIds);
									System.out.println(demandIds+" demandIds");

									int Tranmonth = transactionDate.getMonthValue();

									int CurrMonth = todaysdemand.getDemandDate().getMonthValue();

									if(Tranmonth==CurrMonth)
									{
										Double due = todaysdemand.getDueAmount();
										Double payable = amt + due ; 
										JsonObject updateData = new JsonObject();
										updateData.addProperty("dueAmount", payable);
										this.chitDemandScheduleWritePlatformService.updateChitDemandSchedule(demandIds, updateData);
										System.out.println(demandIds+" demandIds");
									}
									else
									{
										Double Overdue = todaysdemand.getOverdueAmount();
										Double payable = amt + Overdue ; 
										JsonObject updateData = new JsonObject();
										updateData.addProperty("overdueAmount", payable);
										this.chitDemandScheduleWritePlatformService.updateChitDemandSchedule(demandIds, updateData);
									}
								}
							}
						}
					}
				}
			}

			Iterator<JournalEntryData> accEntries = journalEntryReadPlatformService.rertriveEntriesUsingTransactions(TransactionId).iterator();


			while(accEntries.hasNext() && message!=null)
			{

				JournalEntryData itrData = accEntries.next();
				Long id = itrData.getId();
				JsonObject obj = new JsonObject();
				obj.addProperty("reversed", true);
				obj.addProperty("description", message + " has been returned ");
				journalEntryWritePlatformService.updateJournalEntry(id, obj);

				flag = true;
			}
			
			//TODO create a record in payment_status

		}

		if(flag)
		{
			return "Success";
		}
		else
		{
			return "Failed";
		}


	}

	@Transactional
	@Override
	public CommandProcessingResult createPaymentStatus(JsonObject command) {
			try {
				//this.fromApiJsonDeserializer.validateForCreate(command.json());
				System.out.println("1");
				PaymentStatus ps = PaymentStatus.create(command);
				PaymentStatus newPs = this.paymentStatusRepository.save(ps);

				CommandProcessingResult result = new CommandProcessingResultBuilder() //
						.withCommandId(ps.getId()) //
						.withEntityId(newPs.getId()) //
						.build();
				return result;
				
			} catch (final JpaSystemException   | DataIntegrityViolationException  dve ) {
				handlePaymentStatusDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
				return CommandProcessingResult.empty();
			} catch (final PersistenceException dve) {
				Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
				handlePaymentStatusDataIntegrityIssues(command, throwable, dve);
				return CommandProcessingResult.empty();
			}
		}


	private void handlePaymentStatusDataIntegrityIssues(final JsonObject command, final Throwable realCause,
			final Exception dve) {

		// if (realCause.getMessage().contains("external_id")) {
		// final String externalId = command.stringValueOfParameterNamed("externalId");
		// throw new
		// PlatformDataIntegrityException("error.msg.staff.duplicate.externalId",
		// "Staff with externalId `" + externalId + "` already exists", "externalId",
		// externalId);
		// } 

		//LOG.error("Error occured.", dve);
		throw new PlatformDataIntegrityException("error.msg.paymentStatusId.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}

	@Transactional
	@Override
	public CommandProcessingResult updatePaymentStatus(Long id, JsonObject command) {
		try {
				final PaymentStatus PaymentStatusForUpdate = this.paymentStatusRepository.findById(id)
							.orElseThrow(() -> new PaymentDetailNotFoundException(id));
				final Map<String, Object> changesOnly = PaymentStatusForUpdate.update(command);
				if (!changesOnly.isEmpty()) {
					this.paymentStatusRepository.saveAndFlush(PaymentStatusForUpdate);
				}
				return new CommandProcessingResultBuilder().withCommandId(id).withEntityId(id).build();
				
			} catch (final PersistenceException dve) {
				Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
				handlePaymentStatusDataIntegrityIssues(command, throwable, dve);
				return CommandProcessingResult.empty();
			}
		
	}
		
}
