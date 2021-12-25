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
package org.apache.fineract.accounting.producttoaccountmapping.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepository;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.ClientTransaction;
import org.apache.fineract.portfolio.client.domain.ClientTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GlAccountChitWritePlatformServiceImpl implements GlAccountChitWritePlatformService {

	public static final String CLIENT_TRANSACTION_IDENTIFIER = "A";
	
	private JournalEntryRepository glJournalEntryRepository;
	private final GLAccountRepositoryWrapper accountRepositoryWrapper;
	private final ClientTransactionRepositoryWrapper clientTransactionRepository;
	private final ClientRepositoryWrapper clientRepository;
	
	@Autowired
	public GlAccountChitWritePlatformServiceImpl(GlAccountChitReadPlatformService glAccountChitReadPlatformService,
			final GLAccountRepositoryWrapper accountRepositoryWrapper,final ClientTransactionRepositoryWrapper clientTransactionRepository,
			final ClientRepositoryWrapper clientRepository) {
		this.accountRepositoryWrapper = accountRepositoryWrapper;
		this.clientTransactionRepository = clientTransactionRepository;
		this.clientRepository = clientRepository;
	}
	
	private GLAccount getGLAccountById(final Long accountId) {
        return this.accountRepositoryWrapper.findOneWithNotFoundDetection(accountId);
    }
	
	@Override
	 public void createDebitJournalEntry(final Long clientId, final String currencyCode, final Long accountId,
	            final Long transactionId, final LocalDate transactionDate, final BigDecimal amount,final Long chitTransaction,Long chitSubsId) {
	        ////System.out.println("Hey Buddiee Im there In debit method");
	        final boolean manualEntry = false;
	        LoanTransaction loanTransaction = null;
	        SavingsAccountTransaction savingsAccountTransaction = null;
	        ClientTransaction clientTransaction = null;
	        final PaymentDetail paymentDetail = null;
	        final Long shareTransactionId = null;
	        String modifiedTransactionId = transactionId.toString();
	        modifiedTransactionId = CLIENT_TRANSACTION_IDENTIFIER + transactionId; // why needed?
	        Date tranDate = Date.from(transactionDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        final GLAccount debitAccount = getGLAccountById(accountId);
	        clientTransaction = this.clientTransactionRepository.findOneWithNotFoundDetection(clientId, transactionId);
	        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
	        Office officeId = client.getOffice();
	        final JournalEntry journalEntry = JournalEntry.createNew(officeId, paymentDetail, debitAccount, currencyCode, modifiedTransactionId,
	                manualEntry, tranDate, JournalEntryType.DEBIT, amount, null, PortfolioProductType.CLIENT.getValue(), clientId, null,
	                loanTransaction, savingsAccountTransaction, clientTransaction, shareTransactionId,chitTransaction,chitSubsId,null);
	        this.glJournalEntryRepository.saveAndFlush(journalEntry);
	    }
	
	@Override
	    public void createCreditJournalEntry(final Long clientId, final String currencyCode, final Long accountId,
	            final Long transactionId, final LocalDate transactionDate, final BigDecimal amount, final Long chitTransaction,Long chitSubsID) {
		 	////System.out.println("Hey Buddiee Im there In debit method");    
		 	final boolean manualEntry = false;
	        LoanTransaction loanTransaction = null;
	        SavingsAccountTransaction savingsAccountTransaction = null;
	        ClientTransaction clientTransaction = null;
	        final PaymentDetail paymentDetail = null;
	        final Long shareTransactionId = null;
	        String modifiedTransactionId = transactionId.toString();
	        modifiedTransactionId = CLIENT_TRANSACTION_IDENTIFIER + transactionId; // why needed?
	        Date tranDate = Date.from(transactionDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        final GLAccount creditAccount = getGLAccountById(accountId);
	        clientTransaction = this.clientTransactionRepository.findOneWithNotFoundDetection(clientId, transactionId);
	        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
	        Office officeId = client.getOffice();
	        final JournalEntry journalEntry = JournalEntry.createNew(officeId, paymentDetail, creditAccount, currencyCode, modifiedTransactionId,
	                manualEntry, tranDate, JournalEntryType.CREDIT, amount, null, PortfolioProductType.CLIENT.getValue(), clientId, null,
	                loanTransaction, savingsAccountTransaction, clientTransaction, shareTransactionId,chitTransaction,chitSubsID,null);
	        this.glJournalEntryRepository.saveAndFlush(journalEntry);
	    }

		
}
