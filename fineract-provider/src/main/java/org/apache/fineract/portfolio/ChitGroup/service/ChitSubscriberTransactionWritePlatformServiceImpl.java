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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.accounting.common.AccountingConstants.CashAccountsForChit;
import org.apache.fineract.accounting.journalentry.service.AccountingProcessorHelper;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.producttoaccountmapping.service.GlAccountChitReadPlatformService;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.portfolio.ChitGroup.data.ChitChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberTransactionData;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitSubscriberTransaction;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitSubscriberTransactionRepository;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitTransactionEnum;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitBidNotFoundException;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitsCollectionException;
import org.apache.fineract.portfolio.account.data.BranchesAccountData;
import org.apache.fineract.portfolio.account.service.BranchesAccountReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailsReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

@Service
public class ChitSubscriberTransactionWritePlatformServiceImpl implements ChitSubscriberTransactionWritePlatformService
{
	private final ChitSubscriberTransactionRepository chitsubscriberchargeRepository;
	private final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService;
	private final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService;
	private final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices;
	private final ChitGroupReadPlatformService chitGroupReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final ClientRepositoryWrapper clientRepository;
	private final AccountingProcessorHelper accountingProcessorHelper;
	private final GlAccountChitReadPlatformService glAccountChitReadPlatformService;
	private final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService;
	private final BranchesAccountReadPlatformService branchesAccountReadPlatformService;
	private final ConfigurationReadPlatformService configurationReadPlatformService;
	private final ChitChargeReadPlatformServices chitChargeReadPlatformServices;
	@Autowired
	public ChitSubscriberTransactionWritePlatformServiceImpl(ChitSubscriberTransactionRepository chitsubscriberchargeRepository,
			final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService,
			final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService,
			final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices,
			final ChitGroupReadPlatformService chitGroupReadPlatformService,
			final ClientReadPlatformService clientReadPlatformService,
			final ClientRepositoryWrapper clientRepository, final AccountingProcessorHelper accountingProcessorHelper,
			final GlAccountChitReadPlatformService glAccountChitReadPlatformService,
			final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService,
			final BranchesAccountReadPlatformService branchesAccountReadPlatformService,
			final ConfigurationReadPlatformService configurationReadPlatformService,
			final ChitChargeReadPlatformServices chitChargeReadPlatformServices) {
		super();
		this.chitsubscriberchargeRepository = chitsubscriberchargeRepository;
		this.chitSubscriberTransactionReadPlatformService = chitSubscriberTransactionReadPlatformService;
		this.chitDemandScheduleReadPlatformService = chitDemandScheduleReadPlatformService;
		this.chitSubscriberChargeReadPlatformServices = chitSubscriberChargeReadPlatformServices;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
		this.clientRepository = clientRepository;
		this.accountingProcessorHelper = accountingProcessorHelper;
		this.glAccountChitReadPlatformService = glAccountChitReadPlatformService;
		this.paymentDetailsReadPlatformService = paymentDetailsReadPlatformService;
		this.branchesAccountReadPlatformService = branchesAccountReadPlatformService;
		this.configurationReadPlatformService = configurationReadPlatformService;
		this.chitChargeReadPlatformServices = chitChargeReadPlatformServices;
	}


	@Transactional
	@Override
	public CommandProcessingResult createSubscriberChitCharge(JsonObject command) {

		try {
			//this.fromApiJsonDeserializer.validateForCreate(command.json());

			// final Long chitSubscriberId = command.longValueOfParameterNamed("chitSubscriberId");
			// ChitGroupSubscriberData subscriber = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscriberId);



			ChitSubscriberTransaction chitSubscriberCharge = ChitSubscriberTransaction.create(command);
			ChitSubscriberTransaction newchitsubscriberCharge = this.chitsubscriberchargeRepository.save(chitSubscriberCharge);

			CommandProcessingResult result = new CommandProcessingResultBuilder() //
					.withCommandId(newchitsubscriberCharge.getId()) //
					.withEntityId(newchitsubscriberCharge.getId()) //
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
	public CommandProcessingResult updateChitSubscriberCharge(Long id ,JsonObject command) 
	{
		try {
			//this.fromApiJsonDeserializer.validateForUpdate(command.json(), id);
			final ChitSubscriberTransaction chitChargeForUpdate = this.chitsubscriberchargeRepository.findById(id)
					.orElseThrow(() -> new ChitBidNotFoundException(id));
			final Map<String, Object> changesOnly = chitChargeForUpdate.update(command);

			if (!changesOnly.isEmpty()) {
				this.chitsubscriberchargeRepository.saveAndFlush(chitChargeForUpdate);
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
	public CommandProcessingResult deleteChitSubscriberCharge(final Long id) {

		final ChitSubscriberTransaction chitchargeForDelete = this.chitsubscriberchargeRepository.findById(id).orElseThrow(() -> new ChitBidNotFoundException(id));
		this.chitsubscriberchargeRepository.delete(chitchargeForDelete);
		return new CommandProcessingResultBuilder().withEntityId(chitchargeForDelete.getId()).build();
	}




	private void handleChitBidsDataIntegrityIssues(final JsonObject command, final Throwable realCause,
			final Exception dve) {

		// if (realCause.getMessage().contains("external_id")) {
		// final String externalId = command.stringValueOfParameterNamed("externalId");
		// throw new
		// PlatformDataIntegrityException("error.msg.staff.duplicate.externalId",
		// "Staff with externalId `" + externalId + "` already exists", "externalId",
		// externalId);
		// } 


		throw new PlatformDataIntegrityException("error.msg.chitCharge.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}

	@Transactional
	@Override
	public CommandProcessingResult collectionProcessing(Long id,JsonObject command) {
		// TODO Auto-generated method stub
		try {

			//System.out.println("s-1");
			ChitDemandScheduleData demandData = null;
			ChitSubscriberChargeData chargeData = null;
			ChitSubscriberTransactionData data = chitSubscriberTransactionReadPlatformService.retrieveById(id);
			ChitChargeData chitChargesData = null;
			ClientData clientData = null;
			ChitGroupSubscriberData groupSubscriberData = null;
			PaymentDetailData paymentDetail = null;
			if(data.getChitdemandscheduleId()!=null && data.getChitdemandscheduleId()!=0)
			{
				////System.out.println("s-2");
				demandData = chitDemandScheduleReadPlatformService.retrieveById(data.getChitdemandscheduleId()); 

				if(demandData.getChitSubscriberChargeId()!=null && demandData.getChitSubscriberChargeId()!=0)
				{
					////System.out.println("s-3");
					chargeData = chitSubscriberChargeReadPlatformServices.retrieveNameById(demandData.getChitSubscriberChargeId());

					if(chargeData.getChitChargeId()!=null && chargeData.getChitChargeId()!=0)
					{
						// //System.out.println("s-4");
						chitChargesData = this.chitChargeReadPlatformServices.retrieveNameById(chargeData.getChitChargeId());
					}

					groupSubscriberData = chitGroupReadPlatformService.getChitSubscriber(chargeData.getChitSubscriberId());
					if(groupSubscriberData!=null)
					{
						////System.out.println("s-5");
						clientData = clientReadPlatformService.retrieveOne(groupSubscriberData.getClientId());
					}
				}
				if(data.getPaymentdetailId()!= null && data.getPaymentdetailId()!=0) 
				{
					paymentDetail = paymentDetailsReadPlatformService.retrivePaymentDetails(data.getPaymentdetailId());
				}
			}

			if(data.getChitsubscriberchargeId()!=null && data.getChitsubscriberchargeId()!=0)
			{
				////System.out.println("s-6");
				chargeData = chitSubscriberChargeReadPlatformServices.retrieveNameById(data.getChitsubscriberchargeId());

				if(chargeData.getChitChargeId()!=null)
				{
					// //System.out.println("s-7");
					chitChargesData = this.chitChargeReadPlatformServices.retrieveNameById(chargeData.getChitChargeId());
				}

				groupSubscriberData = chitGroupReadPlatformService.getChitSubscriber(chargeData.getChitSubscriberId());
				if(groupSubscriberData!=null)
				{
					////System.out.println("s-8");
					clientData = clientReadPlatformService.retrieveOne(groupSubscriberData.getClientId());
				}
				if(data.getPaymentdetailId()!= null && data.getPaymentdetailId()!=0) 
				{
					paymentDetail = paymentDetailsReadPlatformService.retrivePaymentDetails(data.getPaymentdetailId());
				}
			}

			final Client client = this.clientRepository.findOneWithNotFoundDetection(clientData.getId());

			GlobalConfigurationPropertyData configureData = configurationReadPlatformService.retrieveGlobalConfiguration("GST");
			GlobalConfigurationPropertyData configureCgstData = configurationReadPlatformService.retrieveGlobalConfiguration("CGST");
			GlobalConfigurationPropertyData configureSgstData = configurationReadPlatformService.retrieveGlobalConfiguration("SGST");
			//			
			////System.out.println("step-1");
			////System.out.println(data.getChitsubscriberchargeId());
			if(data.getIsprocessed()==true)
			{
				throw new ChitsCollectionException(id);
			}
			else
			{	

				command.addProperty("isprocessed", true);

				this.updateChitSubscriberCharge(id, command);
				String currencyCode = "INR";

				BigDecimal collectedAmount = BigDecimal.valueOf(data.getAmount());

				if(paymentDetail.getPaymentType().getName().equalsIgnoreCase("CASH"))
				{
					Long debitGLAccountId = null;
					Collection<BranchesAccountData> CollectionData = branchesAccountReadPlatformService.retrieveBranchesById(client.officeId().longValue());
					Iterator<BranchesAccountData> itrBranch= CollectionData.iterator();
					while(itrBranch.hasNext())
					{

						BranchesAccountData itrData = itrBranch.next();
						////System.out.println("itrData cash "+itrData.getBranchId());
						debitGLAccountId = itrData.getCashglAccountId();
					}
					////System.out.println("debitGLAccountId "+debitGLAccountId);
					////System.out.println();
					if(collectedAmount.compareTo(BigDecimal.ZERO) !=0) {
						this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(client.getOffice(), clientData.getId(), currencyCode, debitGLAccountId, data.getId(), data.getTransactionDate().toLocalDate(), collectedAmount,data.getId(),"CR-",null);
					}
				}
				else
				{
					Long debitGLAccountId = null;
					Collection<BranchesAccountData> CollectionData = branchesAccountReadPlatformService.retrieveBranchesById(client.officeId().longValue());
					Iterator<BranchesAccountData> itrBranch= CollectionData.iterator();
					while(itrBranch.hasNext())
					{
						BranchesAccountData itrData = itrBranch.next();
						////System.out.println("itrData bank "+itrData.getBranchId());
						debitGLAccountId = itrData.getBankglAccountId();
					}
					////System.out.println("debitGLAccountId "+debitGLAccountId);
					if(collectedAmount.compareTo(BigDecimal.ZERO) !=0) {
						this.accountingProcessorHelper.createDebitJournalEntryForChitCollection(client.getOffice(),  clientData.getId(), currencyCode, debitGLAccountId, data.getId(), data.getTransactionDate().toLocalDate(), collectedAmount ,data.getId(),"CR-",null);
					}
				}

				if(data.getChitdemandscheduleId()!=null && data.getChitdemandscheduleId()!=0) 
				{
					Boolean prizedSubs = groupSubscriberData.getPrizedsubscriber();

					Long tranType = data.getTrantype();
					
					CashAccountsForChit glAccountType = null;

					Long installment = ChitTransactionEnum.INSTALLMENT_EMI.getValue().longValue();

					Long OverDue = ChitTransactionEnum.INSTALLMENT_OVERDUE.getValue().longValue();

					Long Penalty = ChitTransactionEnum.INSTALLMENT_PENALTY.getValue().longValue();

					if(tranType.compareTo(installment)==0)
					{
						Map<String, Object> mapData1 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION.getValue().longValue());  // a BIG TODO. This has to be fetched from config.
						Long emiAccountId= null;
						for(Map.Entry<String, Object> itr : mapData1.entrySet())
						{
							if(itr.getKey().equals("glAccountId"))
							{
								emiAccountId = (Long)itr.getValue();
							}
						}
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, emiAccountId, data.getId(), data.getTransactionDate().toLocalDate(), collectedAmount,data.getId(), "OS-",CashAccountsForChit.CHIT_OUSTANDING_SUBSCRIPTION,null,null);
					}
					else if(tranType.compareTo(OverDue)==0)
					{
						Long odAccountId= null;
						if(prizedSubs)
						{
							Map<String, Object> mapData2 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_RECEIVED_FROM_PRIZED_SUBSCRBER.getValue().longValue());  // a BIG TODO. This has to be fetched from config.

							for(Map.Entry<String, Object> itr : mapData2.entrySet())
							{
								if(itr.getKey().equals("glAccountId"))
								{
									odAccountId = (Long)itr.getValue();
								}
							}
							glAccountType = CashAccountsForChit.CHIT_RECEIVED_FROM_PRIZED_SUBSCRBER;
						}
						else
						{
							////System.out.println("inside non-prized");
							Map<String, Object> mapData2 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_RECEIVED_FROM_NON_PRIZED_SUBSCRBER.getValue().longValue());  // a BIG TODO. This has to be fetched from config.

							for(Map.Entry<String, Object> itr : mapData2.entrySet())
							{
								if(itr.getKey().equals("glAccountId"))
								{
									odAccountId = (Long)itr.getValue();
								}
							}
							glAccountType = CashAccountsForChit.CHIT_RECEIVED_FROM_NON_PRIZED_SUBSCRBER;
							
						}
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, odAccountId, data.getId(), data.getTransactionDate().toLocalDate(), collectedAmount,data.getId(), "OD-",glAccountType,null,null);
						
					}
					else if(tranType.compareTo(Penalty)==0)
					{
						Map<String, Object> mapData3 = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_PENALTY.getValue().longValue());  // a BIG TODO. This has to be fetched from config.
						Long plAccountId= null;
						for(Map.Entry<String, Object> itr : mapData3.entrySet())
						{
							if(itr.getKey().equals("glAccountId"))
							{
								plAccountId = (Long)itr.getValue();
							}
						}
						Double gstAmount = configureData.getValue().doubleValue();
						//										
						// CGST for Penalty Value
						Double cgstAmount = configureCgstData.getValue().doubleValue();
						Double cgstValue = cgstAmount/100;
						Double cgstDeductAmount = collectedAmount.doubleValue() * cgstValue;
						BigDecimal deductedAmountforCGST = new BigDecimal(cgstDeductAmount);
						BigDecimal cgstpenaltyValue = collectedAmount.subtract(deductedAmountforCGST);

						// SGST for Penalty Value
						Double sgstAmount = configureSgstData.getValue().doubleValue();
						Double sgstValue = sgstAmount/100;
						Double sgstDeductAmount = collectedAmount.doubleValue() * sgstValue;
						BigDecimal deductedAmountforSGST = new BigDecimal(sgstDeductAmount);
						BigDecimal sgstpenaltyValue = collectedAmount.subtract(deductedAmountforSGST);

						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, plAccountId, data.getId(), data.getTransactionDate().toLocalDate(), collectedAmount,data.getId(), "PL-",CashAccountsForChit.CHIT_PENALTY,null,null);
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, plAccountId, data.getId(), data.getTransactionDate().toLocalDate(), deductedAmountforCGST,data.getId(), "GT-",CashAccountsForChit.CHIT_CGST,null,null);
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, plAccountId, data.getId(), data.getTransactionDate().toLocalDate(), deductedAmountforSGST,data.getId(), "GT-",CashAccountsForChit.CHIT_SGST,null,null);
					}
				} 
				else if(data.getChitsubscriberchargeId()!=null && data.getChitsubscriberchargeId()!=0) 
				{
					//Long creditGLAccountId = null;

					BigDecimal chitSubTranAmount = collectedAmount;
					ChitSubscriberTransactionData UpdatedData = chitSubscriberTransactionReadPlatformService.retrieveById(id);
			
					Double cgstAmount = configureCgstData.getValue().doubleValue();
					Double cgstValue = cgstAmount/100;
					Double cgstDeductAmount = chitSubTranAmount.doubleValue() * cgstValue;
					BigDecimal deductedAmountforCGST = new BigDecimal(cgstDeductAmount);

					// SGST for PassBook Value
					Double sgstAmount = configureSgstData.getValue().doubleValue();
					Double sgstValue = sgstAmount/100;
					Double sgstDeductAmount = chitSubTranAmount.doubleValue() * sgstValue;
					BigDecimal deductedAmountforSGST = new BigDecimal(sgstDeductAmount);

					BigDecimal gstForcsValue = deductedAmountforCGST.add(deductedAmountforSGST);
					BigDecimal chitSubTransAmount = new BigDecimal(chitSubTranAmount.doubleValue());
					BigDecimal chitSubTransGstdeductValue = chitSubTransAmount.subtract(gstForcsValue);

					if(chitChargesData.getName().equalsIgnoreCase("CHEQUE_BOUNCE"))
					{
						////System.out.println("Im In Cheque!!! -- 003");
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, UpdatedData.getChitsubscriberchargeId(), data.getId(), data.getTransactionDate().toLocalDate(), chitSubTransGstdeductValue,null, "CQ-",CashAccountsForChit.CHIT_CHEQUE_BOUNCE_CHARGES,null,null);
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, UpdatedData.getChitsubscriberchargeId(), data.getId(), data.getTransactionDate().toLocalDate(), deductedAmountforCGST,null, "GT-",CashAccountsForChit.CHIT_CGST,null,null);
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, UpdatedData.getChitsubscriberchargeId(), data.getId(), data.getTransactionDate().toLocalDate(), deductedAmountforSGST,null, "GT-",CashAccountsForChit.CHIT_SGST,null,null);

					}
					if(chitChargesData.getName().equalsIgnoreCase("PASSBOOK/STATEMENT"))
					{
						////System.out.println("Im In Passbook!!! -- 004");
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, UpdatedData.getChitsubscriberchargeId(), data.getId(), data.getTransactionDate().toLocalDate(), chitSubTransGstdeductValue,null, "PB-",CashAccountsForChit.CHIT_PASSBOOK,null,null);
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, UpdatedData.getChitsubscriberchargeId(), data.getId(), data.getTransactionDate().toLocalDate(), deductedAmountforCGST,null, "GT-",CashAccountsForChit.CHIT_CGST,null,null);
						this.accountingProcessorHelper.createCreditJournalEntryForOthers(client.getOffice(),clientData.getId(), currencyCode, UpdatedData.getChitsubscriberchargeId(), data.getId(), data.getTransactionDate().toLocalDate(), deductedAmountforSGST,null, "GT-",CashAccountsForChit.CHIT_SGST,null,null);

					}

				}

			}

			return new CommandProcessingResultBuilder().withCommandId(id).withEntityId(id)
					.build();

		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitBidsDataIntegrityIssues(null, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}





}
