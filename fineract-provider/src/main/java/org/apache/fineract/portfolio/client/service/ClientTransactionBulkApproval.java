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
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.fineract.accounting.common.AccountingConstants.CashAccountsForChit;
import org.apache.fineract.accounting.journalentry.service.AccountingProcessorHelper;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.producttoaccountmapping.service.GlAccountChitReadPlatformService;
import org.apache.fineract.portfolio.account.data.BranchesAccountData;
import org.apache.fineract.portfolio.account.service.BranchesAccountReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientTransactionData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

@Service
public class ClientTransactionBulkApproval
{
	private final ClientTransactionWritePlatformService clientTransactionWritePlatformService;
	private final ClientTransactionReadPlatformService clientTransactionReadPlatformService; 
    private final AccountingProcessorHelper accountingProcessorHelper;
    final GlAccountChitReadPlatformService glAccountChitReadPlatformService;
    final BranchesAccountReadPlatformService branchesAccountReadPlatformService;
    private final ClientRepositoryWrapper clientRepository;
	
	@Autowired
	public ClientTransactionBulkApproval(ClientTransactionWritePlatformService clientTransactionWritePlatformService,ClientTransactionReadPlatformService clientTransactionReadPlatformService,
			final AccountingProcessorHelper accountingProcessorHelper,
			final GlAccountChitReadPlatformService glAccountChitReadPlatformService,
			final BranchesAccountReadPlatformService branchesAccountReadPlatformService,final ClientRepositoryWrapper clientRepository) {
		super();
		this.clientTransactionWritePlatformService = clientTransactionWritePlatformService;
		this.clientTransactionReadPlatformService = clientTransactionReadPlatformService;
		this.accountingProcessorHelper = accountingProcessorHelper;
		this.glAccountChitReadPlatformService = glAccountChitReadPlatformService;
		this.branchesAccountReadPlatformService = branchesAccountReadPlatformService;
		this.clientRepository = clientRepository;
	}
	
	@Transactional
	public void BulkApproval(Long clientId,Long tranId,JsonObject command)
	{
		 final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

		ClientTransactionData transactionData = clientTransactionReadPlatformService.retrieveTransaction(clientId, tranId);
		String currencyCode =  transactionData.getCurrency().code();
		Long creditGLAccountId = null;
		PaymentDetailData paymentDetail = transactionData.getPaymentDetailData();
		BigDecimal amountPaid = transactionData.getAmount();
		LocalDate transactionDate = transactionData.getDate();
		Boolean isProcesed = transactionData.getIsProcessed();
		if(!isProcesed)
		{
			  Map<String, Object> mapData = glAccountChitReadPlatformService.getProductAccountMappingData(1l, PortfolioProductType.CHIT.getValue().longValue(), CashAccountsForChit.CHIT_SUBSCRIPTION_RECEIVE_ADVANCE.getValue().longValue());  // a BIG TODO. This has to be fetched from config.
	            for(Map.Entry<String, Object> itr : mapData.entrySet())
	            {
	            	if(itr.getKey().equals("glAccountId"))
	            	{
	            		creditGLAccountId = (Long)itr.getValue();
	            	}
	            }
	            ////System.out.println("creditGLAccountId "+creditGLAccountId);
	            this.accountingProcessorHelper.createCreditJournalEntryForChitAdvance(client.getOffice(), clientId, currencyCode, creditGLAccountId, tranId , transactionDate, amountPaid,null);
	            // generate accounting entries
	            
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
	            	
	            	this.accountingProcessorHelper.createDebitJournalEntryForChitAdvance(client.getOffice(), clientId, currencyCode, debitGLAccountId,tranId, transactionDate, amountPaid,null);
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
	            	this.accountingProcessorHelper.createDebitJournalEntryForChitAdvance(client.getOffice(), clientId, currencyCode, debitGLAccountId, tranId, transactionDate, amountPaid,null);
	            }
	           this.clientTransactionWritePlatformService.UpdateClientTransaction(clientId, tranId, command); 
		}
		
	}
}
