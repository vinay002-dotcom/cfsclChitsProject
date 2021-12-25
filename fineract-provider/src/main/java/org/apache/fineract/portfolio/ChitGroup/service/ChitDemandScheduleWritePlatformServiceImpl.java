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


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.util.Map;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.ChitGroup.data.ChitChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitCycleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandDataForBalance;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitDemandSchedule;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitDemandScheduleRepository;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitSubscriberTransaction;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitSubscriberTransactionRepository;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitTransactionEnum;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitBidNotFoundException;

import org.apache.fineract.portfolio.ChitGroup.handler.FindWorkingDays;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ChitDemandScheduleWritePlatformServiceImpl implements ChitDemandScheduleWritePlatformService
{
	private Long ChitDemandid;
	private Long ClientId;
	private Long ChitId;
	private Long ChitSubscId;
	private Long DemandScheduleId;
	private Double PaidAmount;
	private Long ChitSubscriberChargeId;
	private Long paymentTypeId;
	private String paymentType;
	private String accountNumber;
	private String checkNumber;
	private String routingCode;
	private String receiptNumber;
	private String bankNumber;
	private Long paymentdetailId;
	private final ChitDemandScheduleRepository chitDemandScheduleRepository;
	private FindWorkingDays findWorkingDays = new FindWorkingDays();
	private PaymentDetailWritePlatformService paymentDetailWritePlatformService;
	private ChitSubscriberTransactionWritePlatformService chitSubscriberTransactionWritePlatformService;
	private ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService;
	private ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices;
	private ChitCycleReadPlatformService chitCycleReadPlatformService ;
	private final ChitSubscriberChargeWritePlatformService chitSubscriberChargeWritePlatformService;
	private final ChitChargeReadPlatformServices chitChargeReadPlatformServices;
	private final ChitGroupReadPlatformService chitGroupReadPlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final ClientRepositoryWrapper clientRepository;
	private final ConfigurationReadPlatformService configurationReadPlatformService;
	private final ChitSubscriberTransactionRepository chitSubscriberTransactionRepository;

	@Autowired
	public ChitDemandScheduleWritePlatformServiceImpl(ChitDemandScheduleRepository chitDemandScheduleRepository,PaymentDetailWritePlatformService paymentDetailWritePlatformService
			, ChitSubscriberTransactionWritePlatformService chitSubscriberTransactionWritePlatformService,ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService,
			ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices,ChitCycleReadPlatformService chitCycleReadPlatformService,ChitSubscriberChargeWritePlatformService chitSubscriberChargeWritePlatformService,
			ChitChargeReadPlatformServices chitChargeReadPlatformServices,ChitGroupReadPlatformService chitGroupReadPlatformService,final FromJsonHelper fromJsonHelper,final ClientRepositoryWrapper clientRepository,
			final ConfigurationReadPlatformService configurationReadPlatformService,final ChitSubscriberTransactionRepository chitSubscriberTransactionRepository) {
		super();
		this.chitDemandScheduleRepository = chitDemandScheduleRepository;
		this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
		this.chitSubscriberTransactionWritePlatformService = chitSubscriberTransactionWritePlatformService;
		this.chitDemandScheduleReadPlatformService = chitDemandScheduleReadPlatformService;
		this.chitSubscriberChargeReadPlatformServices = chitSubscriberChargeReadPlatformServices;
		this.chitSubscriberChargeWritePlatformService = chitSubscriberChargeWritePlatformService;
		this.chitChargeReadPlatformServices = chitChargeReadPlatformServices;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.chitCycleReadPlatformService = chitCycleReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.clientRepository = clientRepository;
		this.configurationReadPlatformService = configurationReadPlatformService;
		this.chitSubscriberTransactionRepository = chitSubscriberTransactionRepository;
	}


	@Transactional
	@Override
	public CommandProcessingResult createChitDemandSchedule(JsonObject command) {

		try {
			//this.fromApiJsonDeserializer.validateForCreate(command.json());

			// final Long chitSubscriberId = command.longValueOfParameterNamed("chitSubscriberId");
			// ChitGroupSubscriberData subscriber = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscriberId);

			if(command.get("chitsubscriberchargeId")!=null && !command.get("chitsubscriberchargeId").isJsonNull())
			{
				Long chitSubsChargeId = command.get("chitsubscriberchargeId").getAsLong();
				Long subsId = this.chitSubscriberChargeReadPlatformServices.retrieveNameById(chitSubsChargeId).getChitSubscriberId();
				if(subsId!=null)
				{
					Long chitId = this.chitGroupReadPlatformService.getChitSubscriber(subsId).getChitId();
					command.addProperty("chitID", chitId);
				}
			}

			ChitDemandSchedule chitDemandSchedule = ChitDemandSchedule.create(command);
			ChitDemandSchedule newchitDemandSchedule = this.chitDemandScheduleRepository.save(chitDemandSchedule);

			CommandProcessingResult result = new CommandProcessingResultBuilder() //
					.withCommandId(newchitDemandSchedule.getId()) //
					.withEntityId(newchitDemandSchedule.getId()) //
					.build();
			return result;

		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitBidsDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitBidsDataIntegrityIssues(command, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateChitDemandSchedule(Long id ,JsonObject command) 
	{
		try {
			//this.fromApiJsonDeserializer.validateForUpdate(command.json(), id);
			final ChitDemandSchedule chitDemandForUpdate = this.chitDemandScheduleRepository.findById(id)
					.orElseThrow(() -> new ChitBidNotFoundException(id));
			final Map<String, Object> changesOnly = chitDemandForUpdate.update(command);

			if (!changesOnly.isEmpty()) {
				this.chitDemandScheduleRepository.saveAndFlush(chitDemandForUpdate);
			}

			return new CommandProcessingResultBuilder().withCommandId(id).withEntityId(id)
					.with(changesOnly).build();
		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitBidsDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitBidsDataIntegrityIssues(command, throwable, dve);
			return CommandProcessingResult.empty();
		}

	}

	@Transactional
	@Override
	public CommandProcessingResult deleteChitDemandSchedule(final Long id) {

		final ChitDemandSchedule chitchargeForDelete = this.chitDemandScheduleRepository.findById(id).orElseThrow(() -> new ChitBidNotFoundException(id));
		this.chitDemandScheduleRepository.delete(chitchargeForDelete);
		return new CommandProcessingResultBuilder().withEntityId(chitchargeForDelete.getId()).build();
	}



	private void handleChitBidsDataIntegrityIssues(final JsonObject command, final Throwable realCause,
			final Exception dve) {

		throw new PlatformDataIntegrityException("error.msg.chitCharge.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}

	@Transactional
	@Override
	public JsonObject calculateDemand(String apirequestBody) 
	{
		Double penaltypercentage  = configurationReadPlatformService.retrieveGlobalConfiguration("penalty").getValue().doubleValue();
		Double  penalty = penaltypercentage/100l;

		JsonObject reciept = new JsonObject();
		JsonElement parsedData = this.fromJsonHelper.parse(apirequestBody);
		JsonObject temp = parsedData.getAsJsonObject();
		ArrayList<JsonObject> re = new ArrayList<JsonObject>();
		JsonArray tempdata = null;

		if(temp.get("collections")!=null && !temp.get("collections").isJsonNull())
		{
			tempdata = temp.get("collections").getAsJsonArray();
		}

		if(temp.has("paymentInfo"))
		{
			JsonElement payment = temp.get("paymentInfo");
			JsonObject paymentObject = payment.getAsJsonObject();
			final Map<String, Object> changes = new LinkedHashMap<>();

			PaymentDetail responseofpaymentdetail = paymentDetailWritePlatformService.createAndPersistPaymentDetailJson(paymentObject, changes);
			paymentdetailId = responseofpaymentdetail.getId();

		}

		for(int i = 0 ; i < tempdata.size() ; i++)
		{
			reciept = new JsonObject();
			Integer chitNum = null;
			JsonObject data = tempdata.get(i).getAsJsonObject();
			reciept.addProperty("TransactionId", paymentdetailId);
			if(data.get("ChitId")!=null && !data.get("ChitId").isJsonNull())
			{
				ChitId = data.get("ChitId").getAsLong();
				reciept.addProperty("ChitId", ChitId);
				String chitName  = this.chitGroupReadPlatformService.retrieveChitGroup(ChitId).getName();
				reciept.addProperty("GroupName", chitName);
			}

			if(data.get("ChitSubscriberChargeId")!=null && !data.get("ChitSubscriberChargeId").isJsonNull())
			{
				ChitSubscId = data.get("ChitSubscriberChargeId").getAsLong();
			}

			if(data.get("DemandScheduleId")!=null && !data.get("DemandScheduleId").isJsonNull())
			{
				DemandScheduleId = data.get("DemandScheduleId").getAsLong();
			}

			if(data.get("ChitSubsId")!=null && !data.get("ChitSubsId").isJsonNull())
			{
				ChitSubscriberChargeId = data.get("ChitSubsId").getAsLong();
				chitNum = this.chitGroupReadPlatformService.getChitSubscriber(ChitSubscriberChargeId).getChitNumber();
				reciept.addProperty("chitNum", chitNum);

			}


			if(data.get("PaidAmount")!=null && !data.get("PaidAmount").isJsonNull())
			{
				PaidAmount = data.get("PaidAmount").getAsDouble();
			}

			if(data.get("ClientId")!=null && !data.get("ClientId").isJsonNull())
			{
				ClientId = data.get("ClientId").getAsLong();
				reciept.addProperty("ClientId", ClientId);
				final Client client = this.clientRepository.findOneWithNotFoundDetection(ClientId);
				reciept.addProperty("ClientName", client.getFirstname());
				reciept.addProperty("OfficeName", client.getOffice().getName());
				reciept.addProperty("OfficeAddress",  client.getOffice().getAddress());
			}

			JsonObject op = new JsonObject();
			op.addProperty("DemandScheduleId", DemandScheduleId);
			op.addProperty("PaidAmount", PaidAmount);
			JsonElement payment = temp.get("paymentInfo");
			op.add("paymentInfo", payment);

			JsonObject status = this.calculateLastOSsubscription(op.toString());
			System.out.println(status.toString());
			Boolean flag = true;
			if(status.get("Status")!=null && !status.get("Status").isJsonNull())
			{
				if(status.get("Status").getAsString().equals("Success"))
				{
					flag = false;
					JsonElement ele = this.fromJsonHelper.parse(status.toString());
					reciept.add("lastDayPayment", ele);
					return reciept;
				}
				else
				{
					flag = true;

				}
			}

			if(flag)
			{
				ChitDemandScheduleData todaysData = chitDemandScheduleReadPlatformService.retrieveById(DemandScheduleId);

				System.out.println("inside flag ");
				Double todaysInstallment =  todaysData.getInstallmentAmount();
				Double todaysDue = todaysData.getDueAmount();
				Double todaysOverDue = todaysData.getOverdueAmount();
				Double todaysPenalty = todaysData.getPenaltyAmount();
				Double todaysCollectedAmount = todaysData.getCollectedAmount();
				LocalDate todaysDate = todaysData.getDemandDate();
				LocalDate nextDay = findWorkingDays.nextWorkingDay(todaysData.getDemandDate());
				Long staffId = todaysData.getStaffId();

				////System.out.println("todays Date "+todaysDate);
				////System.out.println("nextDay "+nextDay);
				ChitDemandScheduleData tomorrowsData = chitDemandScheduleReadPlatformService.retrieveByIdAndDate(ChitSubscId, staffId, nextDay);


				Double tomorrowDue = tomorrowsData.getDueAmount();
				Double tomorrowOverDue = tomorrowsData.getOverdueAmount();
				Double tomorrowPenalty = tomorrowsData.getPenaltyAmount();
				LocalDate tommorowDate = tomorrowsData.getDemandDate();
				Long tomorrowDemandScheduleId = tomorrowsData.getId();

				Boolean moreThanOneInstallment = false;
				Boolean lastDayofCycle = false;

				if(todaysCollectedAmount!=0.0)
				{
					moreThanOneInstallment = true;
					todaysDue = tomorrowDue;
					todaysOverDue = tomorrowOverDue;
					todaysPenalty = tomorrowPenalty;
				}

				////System.out.println(todaysDate.equals(nextDay));
				if(todaysDate.equals(nextDay))
				{
					lastDayofCycle = true;
				}

				JsonObject updatedata = new JsonObject();
				Double collected = todaysCollectedAmount+PaidAmount;
				updatedata.addProperty("collectedAmount",collected);
				this.updateChitDemandSchedule(DemandScheduleId, updatedata);

				Double remainingAmount = PaidAmount;
				Double emi = null;
				Double paidEmi = null;
				Double paidPenalty = null;
				Double paidOverDue = null;
				if(!lastDayofCycle)
				{
					if(remainingAmount>=todaysPenalty)
					{
						paidPenalty = todaysPenalty;
						tomorrowPenalty = 0.0;
						remainingAmount = remainingAmount - todaysPenalty;
					}
					else
					{
						tomorrowPenalty = todaysPenalty - remainingAmount;
						paidPenalty = remainingAmount;
						remainingAmount = 0.0;
					}


					if(moreThanOneInstallment)
					{
						emi = todaysDue;
					}
					else
					{
						emi = todaysInstallment+todaysDue;
					}

					if(remainingAmount>=emi)
					{
						paidEmi = emi;
						tomorrowDue = 0.0;
						remainingAmount = remainingAmount-emi;
					}
					else
					{
						tomorrowDue = emi - remainingAmount;
						paidEmi = remainingAmount;
						remainingAmount = 0.0;

					}


					if (remainingAmount >= todaysOverDue) {
						paidOverDue = todaysOverDue;
						tomorrowOverDue = 0.0;
						remainingAmount = remainingAmount - todaysOverDue;      
					} else {
						tomorrowOverDue =  todaysOverDue - remainingAmount;
						paidOverDue = remainingAmount;
						remainingAmount = 0.0;        
					}

					if (remainingAmount > 0) {
						tomorrowDue = -remainingAmount;
						paidEmi = paidEmi + remainingAmount;
					}
				}
				if(lastDayofCycle)
				{
					LocalDate newDate =  findWorkingDays.nextWorkingDayofNextMonth(todaysDate);

					Long tempChitSubscriberChargeId = null ;
					ChitSubscriberChargeData chitsubsData = chitSubscriberChargeReadPlatformServices.retrieveNameById(ChitSubscId);
					Long cycleId = chitsubsData.getChitCycleId();
					ChitCycleData response = chitCycleReadPlatformService.retrievecycleid(cycleId);

					Long CycleNumber = response.getCycleNumber();
					////System.out.println("CycleNumber "+CycleNumber);
					CycleNumber = CycleNumber+1;
					ChitCycleData tempchitcycleid = chitCycleReadPlatformService.retrieveAll(ChitId,CycleNumber);

					if(todaysCollectedAmount==0.0)
					{
						//retrieving Cycle Number 


						if(tomorrowDue>=0.0)
						{
							ChitGroupData chitdata = chitGroupReadPlatformService.retrieveChitGroup(ChitId);
							//retriving Cycle Id

							JsonObject dataToCreateChitSubsCharges = new JsonObject();
							//creating data for chitsubscribercharge

							dataToCreateChitSubsCharges.addProperty("subscriberId", ChitSubscriberChargeId);
							dataToCreateChitSubsCharges.addProperty("chitCycleId", tempchitcycleid.getId());
							ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
							dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
							dataToCreateChitSubsCharges.addProperty("amount", chitdata.getMonthlycontribution());	
							CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);

							tempChitSubscriberChargeId = Ids.commandId();

							tomorrowOverDue = tomorrowOverDue + tomorrowDue + tomorrowPenalty;

							tomorrowOverDue = tomorrowOverDue - remainingAmount ;

							Boolean prizedSubscriber = false;
							if(chitsubsData.getChitSubscriberId()!=null)
							{
								ChitGroupSubscriberData chitsubscriberData = this.chitGroupReadPlatformService.getChitSubscriber(chitsubsData.getChitSubscriberId());
								prizedSubscriber = chitsubscriberData.getPrizedsubscriber();
							}

							if(prizedSubscriber)
							{
								tomorrowPenalty = tomorrowOverDue * penalty.doubleValue();

								JsonObject dataToCreate = new JsonObject();
								dataToCreate.addProperty("demandDate", newDate.toString());
								dataToCreate.addProperty("dueAmount", 0.0);
								dataToCreate.addProperty("installmentAmount", 0.0);
								dataToCreate.addProperty("overdueAmount", tomorrowOverDue);
								dataToCreate.addProperty("penaltyAmount", tomorrowPenalty);
								dataToCreate.addProperty("staffId", staffId);
								dataToCreate.addProperty("collectedAmount", 0.0);
								dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
								this.createChitDemandSchedule(dataToCreate);
							}
							else
							{
								tomorrowPenalty = 0.0;
								JsonObject dataToCreate = new JsonObject();
								dataToCreate.addProperty("demandDate", newDate.toString());
								dataToCreate.addProperty("dueAmount", 0.0);
								dataToCreate.addProperty("installmentAmount", 0.0);
								dataToCreate.addProperty("overdueAmount", tomorrowOverDue);
								dataToCreate.addProperty("penaltyAmount", tomorrowPenalty);
								dataToCreate.addProperty("staffId", staffId);
								dataToCreate.addProperty("collectedAmount", 0.0);
								dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
								this.createChitDemandSchedule(dataToCreate);
							}

						}

						if(tomorrowDue<0.0)
						{
							ChitGroupData chitdata = chitGroupReadPlatformService.retrieveChitGroup(ChitId);
							//retriving Cycle Id

							JsonObject dataToCreateChitSubsCharges = new JsonObject();
							//creating data for chitsubscribercharge

							dataToCreateChitSubsCharges.addProperty("subscriberId", ChitSubscriberChargeId);
							dataToCreateChitSubsCharges.addProperty("chitCycleId", tempchitcycleid.getId());
							ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
							dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
							dataToCreateChitSubsCharges.addProperty("amount", chitdata.getMonthlycontribution());	
							CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);

							tempChitSubscriberChargeId = Ids.commandId();

							tomorrowDue = (tomorrowDue * -1);

							JsonObject dataToCreate = new JsonObject();
							dataToCreate.addProperty("demandDate", newDate.toString());
							dataToCreate.addProperty("dueAmount", 0.0);
							dataToCreate.addProperty("installmentAmount",tomorrowDue);
							dataToCreate.addProperty("overdueAmount", 0.0);
							dataToCreate.addProperty("penaltyAmount", 0.0);
							dataToCreate.addProperty("staffId", staffId);
							dataToCreate.addProperty("collectedAmount", tomorrowDue);
							dataToCreate.addProperty("isCalculated", true);
							dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
							this.createChitDemandSchedule(dataToCreate);
						}
					}
					else
					{

						////System.out.println();
						ChitSubscriberChargeData dataId = chitSubscriberChargeReadPlatformServices.retrieveById(ChitSubscriberChargeId,chitsubsData.getChitChargeId(),tempchitcycleid.getId());
						ChitDemandScheduleData ChitidData = chitDemandScheduleReadPlatformService.retrieveByIdAndDate(dataId.getId(), staffId, newDate);
						if(ChitidData!=null)
						{
							////System.out.println("inside if of else ");
							tomorrowOverDue = ChitidData.getOverdueAmount();
							tomorrowPenalty = ChitidData.getPenaltyAmount();
							tomorrowDue = ChitidData.getDueAmount();
							Long idforupdate = ChitidData.getId();
							//					tomorrowOverDue = tomorrowOverDue + tomorrowDue + tomorrowPenalty;
							////System.out.println("remanining amount = "+remainingAmount);
							////System.out.println("tomorrowOverDue = "+tomorrowOverDue);
							////System.out.println("tomorrowPenalty = "+tomorrowPenalty);
							////System.out.println("tomorrowDue = "+tomorrowDue);
							if (remainingAmount >= tomorrowOverDue) {
								paidOverDue = tomorrowOverDue;
								tomorrowOverDue = 0.0;
								remainingAmount = remainingAmount - tomorrowOverDue;      
							} else {
								tomorrowOverDue =  tomorrowOverDue - remainingAmount;
								paidOverDue = remainingAmount;
								remainingAmount = 0.0;        
							}

							Boolean prizedSubscriber = false;
							if(chitsubsData.getChitSubscriberId()!=null)
							{
								ChitGroupSubscriberData chitsubscriberData = this.chitGroupReadPlatformService.getChitSubscriber(chitsubsData.getChitSubscriberId());
								prizedSubscriber = chitsubscriberData.getPrizedsubscriber();
							}

							if(prizedSubscriber)
							{
								tomorrowPenalty = tomorrowOverDue * penalty.doubleValue();
							}
							else
							{
								tomorrowPenalty = tomorrowOverDue;
							}

							tomorrowDue = 0.0;
							if (remainingAmount > 0) {
								tomorrowDue = -remainingAmount;
								paidEmi = remainingAmount;
							}

							JsonObject dataToCreate = new JsonObject();
							dataToCreate.addProperty("dueAmount", tomorrowDue);
							dataToCreate.addProperty("installmentAmount", 0.0);
							dataToCreate.addProperty("overdueAmount", tomorrowOverDue);
							dataToCreate.addProperty("penaltyAmount", tomorrowPenalty);
							dataToCreate.addProperty("collectedAmount", 0.0);
							this.updateChitDemandSchedule(idforupdate, dataToCreate);		
						}
					}

					////System.out.println(newDate+" --newDate");
				}
				else
				{
					JsonObject dataforupdate = new JsonObject();
					dataforupdate.addProperty("dueAmount", tomorrowDue);
					dataforupdate.addProperty("overdueAmount", tomorrowOverDue);
					dataforupdate.addProperty("penaltyAmount", tomorrowPenalty);
					this.updateChitDemandSchedule(tomorrowDemandScheduleId, dataforupdate);
				}

				if(paidPenalty!=null)
				{
					if(paidPenalty>0.0)
					{
						JsonObject dataforpaidpenalty = new JsonObject();
						dataforpaidpenalty.addProperty("chitdemandscheduleId", DemandScheduleId);
						dataforpaidpenalty.addProperty("amount", paidPenalty);
						dataforpaidpenalty.addProperty("trantype","INSTALLMENT_PENALTY");
						dataforpaidpenalty.addProperty("paymentdetailId", paymentdetailId);
						dataforpaidpenalty.addProperty("isprocessed", false);
						chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataforpaidpenalty);

						reciept.addProperty("INSTALLMENT_PENALTY", paidPenalty);
						//TODO ledger entries
						//reciept.put("Penalty", paidPenalty);
					}
					else
					{
						reciept.addProperty("INSTALLMENT_PENALTY", 0.0);
					}
				}


				if(paidEmi!=null)
				{
					if(paidEmi>0.0)
					{

						JsonObject dataforpaidpenalty = new JsonObject();
						dataforpaidpenalty.addProperty("chitdemandscheduleId", DemandScheduleId);
						dataforpaidpenalty.addProperty("amount", paidEmi);
						dataforpaidpenalty.addProperty("trantype","INSTALLMENT_EMI");
						dataforpaidpenalty.addProperty("paymentdetailId", paymentdetailId);
						dataforpaidpenalty.addProperty("isprocessed", false);
						chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataforpaidpenalty);
						//TODO ledger entries
						//reciept.put("paidEmi", paidOverDue);
						reciept.addProperty("INSTALLMENT_EMI+DUE", paidEmi);
					}
					else
					{
						reciept.addProperty("INSTALLMENT_EMI+DUE", 0.0);
					}
				}

				if(paidOverDue!=null)
				{
					if(paidOverDue>0.0)
					{

						JsonObject dataforpaidpenalty = new JsonObject();
						dataforpaidpenalty.addProperty("chitdemandscheduleId", DemandScheduleId);
						dataforpaidpenalty.addProperty("amount", paidOverDue);
						dataforpaidpenalty.addProperty("trantype","INSTALLMENT_OVERDUE");
						dataforpaidpenalty.addProperty("paymentdetailId", paymentdetailId);
						dataforpaidpenalty.addProperty("isprocessed", false);
						chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataforpaidpenalty);
						reciept.addProperty("INSTALLMENT_OVERDUE", paidOverDue);
						//TODO ledger entries
						//reciept.put("OverDue", paidOverDue);
					}
					else
					{
						reciept.addProperty("INSTALLMENT_OVERDUE", 0.0);
					}
				}

				if(ChitSubscId!=null)
				{
					ChitDemandDataForBalance Balancedata = this.chitDemandScheduleReadPlatformService.getDemandBalance(ChitSubscId);
					Double collectedAmount = Balancedata.getCollectedAmount();
					reciept.addProperty("OpeningBalance", collectedAmount);
					Long MonthlyContribution = chitGroupReadPlatformService.retrieveChitGroup(ChitId).getMonthlycontribution();
					Long currentCycle = chitGroupReadPlatformService.retrieveChitGroup(ChitId).getCurrentcycle();
					Long dividend  = chitCycleReadPlatformService.retrieveAll(ChitId, currentCycle).getDividend();
					Long Remaining_Balance = null;
					if(dividend!=null)
					{
						Remaining_Balance = (long) ((MonthlyContribution-dividend)-collectedAmount);
					}
					else
					{
						Remaining_Balance = (long)(MonthlyContribution-collectedAmount);
					}
					reciept.addProperty("RemainingBalance", Remaining_Balance);
				}

				JsonObject updateData = new JsonObject();
				updateData.addProperty("isCalculated", true);
				this.updateChitDemandSchedule(DemandScheduleId, updateData);
				reciept.addProperty("status", "Success");
				// TODO Ledger entries 


				re.add(reciept);

			}
		}

		JsonObject resp = new JsonObject();
		JsonElement arr = this.fromJsonHelper.parse(re.toString());
		resp.add("Result",arr);
		return resp;

	}

	@Transactional
	@Override
	public JsonObject calculateLastOSsubscription(String body)
	{
		
		JsonElement dataTobeParsed = this.fromJsonHelper.parse(body);
		JsonObject ParsedData = dataTobeParsed.getAsJsonObject();
		JsonObject reposne = new JsonObject();
		Long demandId = null;
		Long paymentdetailId = null;
		Double Amount = null;

		if(ParsedData.get("DemandScheduleId")!=null && !ParsedData.get("DemandScheduleId").isJsonNull())
		{
			demandId = ParsedData.get("DemandScheduleId").getAsLong();
			System.out.println("inside cal demandId "+demandId);
		}

		if(ParsedData.get("PaidAmount")!=null && !ParsedData.get("PaidAmount").isJsonNull())
		{
			Amount = ParsedData.get("PaidAmount").getAsDouble();
		}

		if(demandId!=null)
		{
			ChitDemandScheduleData demandData = this.chitDemandScheduleReadPlatformService.retrieveById(demandId);

			if(demandData!=null)
			{

				Long chitId = demandData.getChitId();
				if(chitId!=null)
				{
					ChitGroupData chitData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
					LocalDate cd = LocalDate.now(ZoneId.systemDefault());
					if(chitData.getEndDate()!=null)
					{
						cd= chitData.getEndDate();
					}
					int date = cd.compareTo(demandData.getDemandDate());
					System.out.println(date +" date");
					if(chitData.getCurrentcycle().compareTo(chitData.getChitduration())==0 && date==0)
					{
						Double OverDue = demandData.getOverdueAmount();
						Double due = demandData.getDueAmount();
						Double Penalty = demandData.getPenaltyAmount();
						Double remainingAmount = Amount;
						Double penalty = null;
						Double paidEmi = null ;
						Double paidOverDue = null;
						Double emi = due + demandData.getInstallmentAmount();

						Double totalPayable = OverDue+emi+Penalty;

						System.out.println("totalpayable "+totalPayable);
						System.out.println("Amount "+Amount);
						if(totalPayable.compareTo(Amount)==0)
						{
							if(remainingAmount>=Penalty)
							{
								penalty = Penalty;
								remainingAmount = remainingAmount - Penalty;
							}
							if(remainingAmount>=emi)
							{
								paidEmi = emi;

								remainingAmount = remainingAmount-emi;
							}
							if (remainingAmount >= OverDue) {
								paidOverDue = OverDue;

								remainingAmount = remainingAmount - OverDue;      
							}

							JsonObject dataForChitDemandss =  new JsonObject();
							dataForChitDemandss.addProperty("installmentAmount", 0.0);
							dataForChitDemandss.addProperty("dueAmount", 0.0);
							dataForChitDemandss.addProperty("overdueAmount", 0.0);
							dataForChitDemandss.addProperty("penaltyAmount", 0.0);
							dataForChitDemandss.addProperty("collectedAmount", Amount);
							dataForChitDemandss.addProperty("isCalculated", false);
							this.updateChitDemandSchedule(demandId, dataForChitDemandss);
							
						
							if(ParsedData.has("paymentInfo"))
							{
								JsonElement payment = ParsedData.get("paymentInfo");
								JsonObject paymentObject = payment.getAsJsonObject();
								final Map<String, Object> changes = new LinkedHashMap<>();
								PaymentDetail responseofpaymentdetail = paymentDetailWritePlatformService.createAndPersistPaymentDetailJson(paymentObject, changes);
								paymentdetailId = responseofpaymentdetail.getId();	
							}

							if(paymentdetailId!=null && paymentdetailId!=0)
							{
								if(penalty!=null && penalty!=0.0)
								{
									ChitSubscriberTransaction cst = new ChitSubscriberTransaction(demandId,null,null,penalty,ChitTransactionEnum.INSTALLMENT_PENALTY,paymentdetailId,null,false,true);
									cst = chitSubscriberTransactionRepository.saveAndFlush(cst);
								}

								if(paidEmi!=null && paidEmi!=0.0)
								{
									ChitSubscriberTransaction cst = new ChitSubscriberTransaction(demandId,null,null,paidEmi,ChitTransactionEnum.INSTALLMENT_EMI,paymentdetailId,null,false,true);
									cst = chitSubscriberTransactionRepository.saveAndFlush(cst);
								}

								if(paidOverDue!=null && paidOverDue!=0.0)
								{
									ChitSubscriberTransaction cst = new ChitSubscriberTransaction(demandId,null,null,paidOverDue,ChitTransactionEnum.INSTALLMENT_OVERDUE,paymentdetailId,null,false,true);
									cst = chitSubscriberTransactionRepository.saveAndFlush(cst);
								}
							}

							reposne.addProperty("Status", "Success");
							reposne.addProperty("Message", "Amount Has Been Payed");
							
							
						}
					}
					else
					{
						reposne.addProperty("Status", "Failure");
						reposne.addProperty("Message", "Please Pay Full Amount ");
					}

				}
				else
				{
					reposne.addProperty("Status", "Failure");
					reposne.addProperty("Message", "Please Pay Full Amount ");
				}
			}
		}

		return reposne;

	}


}
