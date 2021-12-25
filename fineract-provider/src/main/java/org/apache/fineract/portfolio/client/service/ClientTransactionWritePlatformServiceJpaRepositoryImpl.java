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
package org.apache.fineract.portfolio.client.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Set;


import org.apache.fineract.accounting.journalentry.service.AccountingProcessorHelper;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;

import org.apache.fineract.accounting.producttoaccountmapping.service.GlAccountChitReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.office.domain.OrganisationCurrencyRepositoryWrapper;

import org.apache.fineract.portfolio.account.service.BranchesAccountReadPlatformService;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientTransactionData;
import org.apache.fineract.portfolio.client.data.ClientTransactionDataValidator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientCharge;
import org.apache.fineract.portfolio.client.domain.ClientChargePaidBy;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.ClientTransaction;
import org.apache.fineract.portfolio.client.domain.ClientTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ClientTransactionCannotBeUndoneException;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

@Service
public class ClientTransactionWritePlatformServiceJpaRepositoryImpl implements ClientTransactionWritePlatformService {
    private static final Logger LOG = LoggerFactory.getLogger(ClientTransactionWritePlatformServiceJpaRepositoryImpl.class);


    private final ClientTransactionRepositoryWrapper clientTransactionRepository;
    private final ClientTransactionReadPlatformService clientTransactionReadPlatformService;
    private final ClientRepositoryWrapper clientRepository;
    private final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepository;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final ClientTransactionDataValidator clientTransactionDataValidator;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final PlatformSecurityContext context;
    private final AccountingProcessorHelper accountingProcessorHelper;
    final GlAccountChitReadPlatformService glAccountChitReadPlatformService;
    final BranchesAccountReadPlatformService branchesAccountReadPlatformService;
    final  ClientReadPlatformService clientReadPlatformService;
    
    @Autowired
    public ClientTransactionWritePlatformServiceJpaRepositoryImpl(final ClientTransactionRepositoryWrapper clientTransactionRepository,
            final ClientRepositoryWrapper clientRepositoryWrapper,
            final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepositoryWrapper,
            JournalEntryWritePlatformService journalEntryWritePlatformService,
            ClientTransactionDataValidator clientTransactionDataValidator,
            PaymentDetailWritePlatformService paymentDetailWritePlatformService,
            PlatformSecurityContext context,
            AccountingProcessorHelper accountingProcessorHelper,final GlAccountChitReadPlatformService glAccountChitReadPlatformService,
            final BranchesAccountReadPlatformService branchesAccountReadPlatformService,ClientTransactionReadPlatformService clientTransactionReadPlatformService,ClientReadPlatformService clientReadPlatformService) {
        this.clientTransactionRepository = clientTransactionRepository;
        this.clientRepository = clientRepositoryWrapper;
        this.organisationCurrencyRepository = organisationCurrencyRepositoryWrapper;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.clientTransactionDataValidator = clientTransactionDataValidator;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.context = context;
        this.accountingProcessorHelper = accountingProcessorHelper;
        this.glAccountChitReadPlatformService =glAccountChitReadPlatformService;
        this.branchesAccountReadPlatformService = branchesAccountReadPlatformService;
        this.clientTransactionReadPlatformService = clientTransactionReadPlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
    }

    @Override
    public CommandProcessingResult undo(Long clientId, Long transactionId) {

        final Client client = this.clientRepository.getActiveClientInUserScope(clientId);

        final ClientTransaction clientTransaction = this.clientTransactionRepository.findOneWithNotFoundDetection(clientId, transactionId);

        // validate that transaction can be undone
        if (clientTransaction.isReversed()) {
            throw new ClientTransactionCannotBeUndoneException(clientId, transactionId);
        }

        // mark transaction as reversed
        clientTransaction.reverse();

        // revert any charges paid back to their original state
        if (clientTransaction.isPayChargeTransaction() || clientTransaction.isWaiveChargeTransaction()) {
            // undo charge
            final Set<ClientChargePaidBy> chargesPaidBy = clientTransaction.getClientChargePaidByCollection();
            for (final ClientChargePaidBy clientChargePaidBy : chargesPaidBy) {
                final ClientCharge clientCharge = clientChargePaidBy.getClientCharge();
                clientCharge.setCurrency(
                        organisationCurrencyRepository.findOneWithNotFoundDetection(clientCharge.getCharge().getCurrencyCode()));
                if (clientTransaction.isPayChargeTransaction()) {
                    clientCharge.undoPayment(clientTransaction.getAmount());
                } else if (clientTransaction.isWaiveChargeTransaction()) {
                    clientCharge.undoWaiver(clientTransaction.getAmount());
                }
            }
        }

        // generate accounting entries
        this.clientTransactionRepository.saveAndFlush(clientTransaction);
        generateAccountingEntries(clientTransaction);

        return new CommandProcessingResultBuilder() //
                .withEntityId(transactionId) //
                .withOfficeId(client.officeId()) //
                .withClientId(clientId) //
                .build();
    }

    private void generateAccountingEntries(ClientTransaction clientTransaction) {
        Map<String, Object> accountingBridgeData = clientTransaction.toMapData();
        journalEntryWritePlatformService.createJournalEntriesForClientTransactions(accountingBridgeData);
    }

    @Override
    public CommandProcessingResult createClientTranChitAdvance(Long clientId, JsonCommand command) {
        try {
            this.clientTransactionDataValidator.validateChitAdvance(command.json());

            final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

            //final Locale locale = command.extractLocale();
            //final DateTimeFormatter fmt = DateTimeFormatter.ofPattern(command.dateFormat()).withLocale(locale);
            final LocalDate transactionDate = command.localDateValueOfParameterNamed(ClientApiConstants.transactionDateParamName);
            final String currencyCode = command.stringValueOfParameterNamed(ClientApiConstants.currencyParamName);
            final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed(ClientApiConstants.amountParamName);
            final Money amountInMoney = Money.of( new MonetaryCurrency(currencyCode, 2, null), amountPaid);

            // create Payment Transaction
            final Map<String, Object> changes = new LinkedHashMap<>();
            final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

            ClientTransaction clientTransaction = ClientTransaction.payChitAdvance(client, client.getOffice(), paymentDetail, transactionDate,
                amountInMoney, currencyCode, this.context.getAuthenticatedUserIfPresent(),null);
            ClientTransaction newClientTransaction = this.clientTransactionRepository.saveAndFlushAndReturn(clientTransaction);
            
            //Ledger entries are now moved to Bulk Approval 
            
//            ////System.out.println(newClientTransaction.getId()+" newClientTransaction");
//            Long creditGLAccountId = null;
//            Map<String, Object> mapData = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_SUBSCRIPTION_RECEIVE_ADVANCE.getValue().longValue());  // a BIG TODO. This has to be fetched from config.
//            for(Map.Entry<String, Object> itr : mapData.entrySet())
//            {
//            	if(itr.getKey().equals("glAccountId"))
//            	{
//            		creditGLAccountId = (Long)itr.getValue();
//            	}
//            }
//            ////System.out.println("creditGLAccountId "+creditGLAccountId);
//            this.accountingProcessorHelper.createCreditJournalEntryForChitAdvance(client.getOffice(), clientId, currencyCode, creditGLAccountId, newClientTransaction.getId(), transactionDate, amountPaid,null);
//            // generate accounting entries
//            
//            if(paymentDetail.getPaymentType().isCashPayment())
//            {
//            	Long debitGLAccountId = null;
//            	Collection<BranchesAccountData> CollectionData = branchesAccountReadPlatformService.retrieveBranchesById(client.officeId().longValue());
//            	Iterator<BranchesAccountData> itrBranch= CollectionData.iterator();
//            	while(itrBranch.hasNext())
//            	{
//            		
//            		BranchesAccountData itrData = itrBranch.next();
//            		////System.out.println("itrData cash "+itrData.getBranchId());
//            		debitGLAccountId = itrData.getCashglAccountId();
//            	}
//            	 ////System.out.println("debitGLAccountId "+debitGLAccountId);
//            	this.accountingProcessorHelper.createDebitJournalEntryForChitAdvance(client.getOffice(), clientId, currencyCode, debitGLAccountId, newClientTransaction.getId(), transactionDate, amountPaid,null);
//            }
//            else
//            {
//            	Long debitGLAccountId = null;
//            	Collection<BranchesAccountData> CollectionData = branchesAccountReadPlatformService.retrieveBranchesById(client.officeId().longValue());
//            	Iterator<BranchesAccountData> itrBranch= CollectionData.iterator();
//            	while(itrBranch.hasNext())
//            	{
//            		BranchesAccountData itrData = itrBranch.next();
//            		////System.out.println("itrData bank "+itrData.getBranchId());
//            		debitGLAccountId = itrData.getBankglAccountId();
//            	}
//            	 ////System.out.println("debitGLAccountId "+debitGLAccountId);
//            	this.accountingProcessorHelper.createDebitJournalEntryForChitAdvance(client.getOffice(), clientId, currencyCode, debitGLAccountId, newClientTransaction.getId(), transactionDate, amountPaid,null);
//            }
            
            Map<String,Object> reciept = this.Reciept(clientId, newClientTransaction.getId(),paymentDetail,amountPaid);
            /*
            Map<String, Object> accountingBridgeData = clientTransaction.toMapData();
            journalEntryWritePlatformService.createJournalEntriesForClientTransactions(accountingBridgeData);
    		*/
//        	for(Map.Entry<String, Object> itr : reciept.entrySet())
//			{
//				////System.out.println(itr.getValue() + " " +itr.getKey());
//			}
            return new CommandProcessingResultBuilder().with(reciept) //
                    .withTransactionId(clientTransaction.getId().toString())//
                    .withEntityId(client.getId()) //
                    .withOfficeId(client.getOffice().getId()).build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            LOG.error("Error occured.", dve);
            throw new PlatformDataIntegrityException("error.msg.client.transactions.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }

    }

	@Override
	public CommandProcessingResult UpdateClientTransaction(Long clientId,Long TransactionId, JsonObject command) {
		
		
		ClientTransaction data = clientTransactionRepository.findOneWithNotFoundDetection(clientId, TransactionId);
		if(data!=null)
		{
			final Map<String, Object> changesOnly = data.update(command);
			
			if(changesOnly!=null)
			{
				this.clientTransactionRepository.saveAndFlush(data);
			}
			
			return new CommandProcessingResultBuilder().withCommandId(clientId).withEntityId(clientId)
					.with(changesOnly).build();
		}
		else
		{
			return null;
		}
	}  
	
	Map<String,Object> Reciept(Long clientId,Long tranId,PaymentDetail paymentDetail,BigDecimal amountPaid)
	{
		Map<String,Object> reciept = new LinkedHashMap<>(9);
		
		Double Sub = 0.0;
		
		Double Opening_Balance = 0.0;
		
		Long TransactionId = this.clientTransactionReadPlatformService.retrieveTransactionByTransactionId(tranId).getPaymentId();
		
		String Name = this.clientReadPlatformService.retrieveOne(clientId).getFirstname();
		
		final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
		
		//TODO config office Address in m_office 
		
		Collection<ClientTransactionData> CollectionOfTransaction = this.clientTransactionReadPlatformService.retrieveAllTransactionsUsingId(clientId);
		
		Iterator<ClientTransactionData> iterator =  CollectionOfTransaction.iterator();
		
		Double TotalAmount = 0.0;
		while(iterator.hasNext())
		{
			ClientTransactionData itrData = iterator.next();
			if(!itrData.getPaymentDetailData().getId().equals(paymentDetail.getId()))
			{
				TotalAmount = itrData.getAmount().doubleValue() + TotalAmount;
			}
			
		}
		
		if(TotalAmount!=0.0)
		{
			
			Sub = this.clientTransactionReadPlatformService.retrieveTransactionByTransactionId(tranId).getAmount().doubleValue();
			Opening_Balance = TotalAmount;
		}
		else
		{
			Sub = amountPaid.doubleValue();
		}
		
		Double ClosingBalance = TotalAmount + Sub;
		
		reciept.put("ClientId", clientId);
		reciept.put("TransactionId", paymentDetail.getId());
		reciept.put("ClientName", Name);
		reciept.put("OfficeName", client.getOffice().getName());
		reciept.put("OfficeAddress",  client.getOffice().getAddress());
		reciept.put("OpeningBalance", Opening_Balance);
		reciept.put("Sub", Sub);
		reciept.put("ClosingBalance", ClosingBalance);
		return reciept;
	}
}
