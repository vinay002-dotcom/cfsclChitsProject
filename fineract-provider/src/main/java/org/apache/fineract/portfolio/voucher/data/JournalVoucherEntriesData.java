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
package org.apache.fineract.portfolio.voucher.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.journalentry.data.CreditDebit;
import org.apache.fineract.accounting.journalentry.data.TransactionDetailData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;


@SuppressWarnings("unused")
public class JournalVoucherEntriesData {


	    private final Long id;
	    private final Long officeId;
	    @SuppressWarnings("unused")
	    private final String officeName;
	    @SuppressWarnings("unused")
	    private final String glAccountName;
	    private final Long glAccountId;
	    @SuppressWarnings("unused")
	    private final String glAccountCode;
	    private final EnumOptionData glAccountType;
	    @SuppressWarnings("unused")
	    private final LocalDate transactionDate;
	    private final EnumOptionData entryType;
	    private final BigDecimal amount;
	    @SuppressWarnings("unused")
	    private final CurrencyData currency;
	    private final String transactionId;
	    @SuppressWarnings("unused")
	    private final Boolean manualEntry;
	    @SuppressWarnings("unused")
	    private final EnumOptionData entityType;
	    @SuppressWarnings("unused")
	    private final Long entityId;
	    @SuppressWarnings("unused")
	    private final Long createdByUserId;
	    @SuppressWarnings("unused")
	    private final LocalDate createdDate;
	    @SuppressWarnings("unused")
	    private final String createdByUserName;
	    @SuppressWarnings("unused")
	    private final String comments;
	    @SuppressWarnings("unused")
	    private final Boolean reversed;
	    private final String referenceNumber;
	    @SuppressWarnings("unused")
	    private final BigDecimal officeRunningBalance;
	    @SuppressWarnings("unused")
	    private final BigDecimal organizationRunningBalance;
	    @SuppressWarnings("unused")
	    private final Boolean runningBalanceComputed;

	    @SuppressWarnings("unused")
	    private final TransactionDetailData transactionDetails;
	    
	    private final String ClientName;
	    
	    private final Long clientId;

	    private final Long chitId;
	    
	    private final Long ticketNumber;
	    
	    private final String ChitName;
	    
	    
	    // import fields
	    private transient Integer rowIndex;
	    private String dateFormat;
	    private String locale;
	    private List<CreditDebit> credits;
	    private List<CreditDebit> debits;
	    private Long paymentTypeId;
		private String currencyCode;
	    private String accountNumber;
	    private String checkNumber;
	    private String routingCode;
	    private String receiptNumber;
	    private String bankNumber;
	    
	    private Long chitgprSubs;
	    private String description;
	    
	    private final Long voucherId;
		private final Integer voucherTypeId; 
		private final String voucherName;
		private final String voucherNumber;
		private final String vendorName;


	    private JournalVoucherEntriesData(Long officeId, LocalDate transactionDate, String currencyCode, Long paymentTypeId, Integer rowIndex,
	            List<CreditDebit> credits, List<CreditDebit> debits, String accountNumber, String checkNumber, String routingCode,
	            String receiptNumber, String bankNumber, String comments, String locale, String dateFormat,String ClientName,Long clientId,Long chitId,Long ticketNumber,String ChitName,
	            Long voucherId,Integer voucherTypeId,String voucherName,String voucherNumber,String vendorName) {

	        this.officeId = officeId;
	        this.dateFormat = dateFormat;
	        this.locale = locale;
	        this.transactionDate = transactionDate;
	        this.currencyCode = currencyCode;
	        this.rowIndex = rowIndex;
	        this.credits = credits;
	        this.debits = debits;
	        this.paymentTypeId = paymentTypeId;
	        this.accountNumber = accountNumber;
	        this.checkNumber = checkNumber;
	        this.routingCode = routingCode;
	        this.receiptNumber = receiptNumber;
	        this.bankNumber = bankNumber;
	        this.comments = comments;
	        this.id = null;
	        this.officeName = null;
	        this.glAccountName = null;
	        this.glAccountId = null;
	        this.glAccountCode = null;
	        this.glAccountType = null;
	        this.entryType = null;
	        this.amount = null;
	        this.currency = null;
	        this.transactionId = null;
	        this.manualEntry = null;
	        this.entityType = null;
	        this.entityId = null;
	        this.createdByUserId = null;
	        this.createdDate = null;
	        this.createdByUserName = null;
	        this.reversed = null;
	        this.referenceNumber = null;
	        this.officeRunningBalance = null;
	        this.organizationRunningBalance = null;
	        this.runningBalanceComputed = null;
	        this.transactionDetails = null;
	        this.clientId = clientId;
	        this.ClientName = ClientName;
	        this.chitId = chitId;
	        this.ticketNumber = ticketNumber;
	        this.ChitName = ChitName;
	        this.voucherId = voucherId;
	        this.voucherTypeId = voucherTypeId;
	        this.voucherName = voucherName;
	        this.voucherNumber = voucherNumber;
	        this.vendorName = vendorName;
	    }

	    public Integer getRowIndex() {
	        return rowIndex;
	    }

	    public LocalDate getTransactionDate() {
	        return transactionDate;
	    }

	    public void addDebits(CreditDebit debit) {

	        this.debits.add(debit);
	    }

	    public void addCredits(CreditDebit credit) {
	        this.credits.add(credit);
	    }

	    public JournalVoucherEntriesData(final Long id, final Long officeId, final String officeName, final String glAccountName, final Long glAccountId,
	            final String glAccountCode, final EnumOptionData glAccountClassification, final LocalDate transactionDate,
	            final EnumOptionData entryType, final BigDecimal amount, final String transactionId, final Boolean manualEntry,
	            final EnumOptionData entityType, final Long entityId, final Long createdByUserId, final LocalDate createdDate,
	            final String createdByUserName, final String comments, final Boolean reversed, final String referenceNumber,
	            final BigDecimal officeRunningBalance, final BigDecimal organizationRunningBalance, final Boolean runningBalanceComputed,
	            final TransactionDetailData transactionDetailData, final CurrencyData currency,String ClientName,Long clientId,Long chitId,Long ticketNumber,String ChitName,
	            Long voucherId,Integer voucherTypeId,String voucherName,String voucherNumber,String vendorName) {
	        this.id = id;
	        this.officeId = officeId;
	        this.officeName = officeName;
	        this.glAccountName = glAccountName;
	        this.glAccountId = glAccountId;
	        this.glAccountCode = glAccountCode;
	        this.glAccountType = glAccountClassification;
	        this.transactionDate = transactionDate;
	        this.entryType = entryType;
	        this.amount = amount;
	        this.transactionId = transactionId;
	        this.manualEntry = manualEntry;
	        this.entityType = entityType;
	        this.entityId = entityId;
	        this.createdByUserId = createdByUserId;
	        this.createdDate = createdDate;
	        this.createdByUserName = createdByUserName;
	        this.comments = comments;
	        this.reversed = reversed;
	        this.referenceNumber = referenceNumber;
	        this.officeRunningBalance = officeRunningBalance;
	        this.organizationRunningBalance = organizationRunningBalance;
	        this.runningBalanceComputed = runningBalanceComputed;
	        this.transactionDetails = transactionDetailData;
	        this.currency = currency;
	        this.clientId = clientId;
	        this.ClientName = ClientName;
	        this.chitId = chitId;
	        this.ticketNumber = ticketNumber;
	        this.ChitName = ChitName;
	        this.voucherId = voucherId;
	        this.voucherTypeId = voucherTypeId;
	        this.voucherName = voucherName;
	        this.voucherNumber = voucherNumber;
	        this.vendorName = vendorName;
	    }
	    
	    public JournalVoucherEntriesData(final Long id, final Long officeId, final String officeName, final String glAccountName, final Long glAccountId,
	            final String glAccountCode, final EnumOptionData glAccountClassification, final LocalDate transactionDate,
	            final EnumOptionData entryType, final BigDecimal amount, final String transactionId, final Boolean manualEntry,
	            final EnumOptionData entityType, final Long entityId, final Long createdByUserId, final LocalDate createdDate,
	            final String createdByUserName, final String comments, final Boolean reversed, final String referenceNumber,
	            final BigDecimal officeRunningBalance, final BigDecimal organizationRunningBalance, final Boolean runningBalanceComputed,
	            final TransactionDetailData transactionDetailData, final CurrencyData currency,String ClientName,Long clientId,
	            Long chitId,Long ticketNumber,String ChitName, Long chitgprSubs, String description,
	            Long voucherId,Integer voucherTypeId,String voucherName,String voucherNumber,String vendorName) {
	        this.id = id;
	        this.officeId = officeId;
	        this.officeName = officeName;
	        this.glAccountName = glAccountName;
	        this.glAccountId = glAccountId;
	        this.glAccountCode = glAccountCode;
	        this.glAccountType = glAccountClassification;
	        this.transactionDate = transactionDate;
	        this.entryType = entryType;
	        this.amount = amount;
	        this.transactionId = transactionId;
	        this.manualEntry = manualEntry;
	        this.entityType = entityType;
	        this.entityId = entityId;
	        this.createdByUserId = createdByUserId;
	        this.createdDate = createdDate;
	        this.createdByUserName = createdByUserName;
	        this.comments = comments;
	        this.reversed = reversed;
	        this.referenceNumber = referenceNumber;
	        this.officeRunningBalance = officeRunningBalance;
	        this.organizationRunningBalance = organizationRunningBalance;
	        this.runningBalanceComputed = runningBalanceComputed;
	        this.transactionDetails = transactionDetailData;
	        this.currency = currency;
	        this.clientId = clientId;
	        this.ClientName = ClientName;
	        this.chitId = chitId;
	        this.ticketNumber = ticketNumber;
	        this.ChitName = ChitName;
	        this.chitgprSubs = chitgprSubs;
	        this.description = description;
	        this.voucherId = voucherId;
	        this.voucherTypeId = voucherTypeId;
	        this.voucherName = voucherName;
	        this.voucherNumber = voucherNumber;
	        this.vendorName = vendorName;
	    }


	    public static JournalVoucherEntriesData fromGLAccountData(final GLAccountData glAccountData) {

	        final Long id = null;
	        final Long officeId = null;
	        final String officeName = null;
	        final String glAccountName = glAccountData.getName();
	        final Long glAccountId = glAccountData.getId();
	        final String glAccountCode = glAccountData.getGlCode();
	        final EnumOptionData glAccountClassification = glAccountData.getType();
	        final LocalDate transactionDate = null;
	        final EnumOptionData entryType = null;
	        final BigDecimal amount = null;
	        final String transactionId = null;
	        final Boolean manualEntry = null;
	        final EnumOptionData entityType = null;
	        final Long entityId = null;
	        final Long createdByUserId = null;
	        final LocalDate createdDate = null;
	        final String createdByUserName = null;
	        final String comments = null;
	        final Boolean reversed = null;
	        final String referenceNumber = null;
	        final BigDecimal officeRunningBalance = null;
	        final BigDecimal organizationRunningBalance = null;
	        final Boolean runningBalanceComputed = null;
	        final TransactionDetailData transactionDetailData = null;
	        final CurrencyData currency = null;
	        final String ClientName = null;
	        final Long clientId = null;
	        final Long chitId = null;
	        final Long ticketNumber = null;
	       final String ChitName = null;
	       final Long voucherId = null;
			final Integer voucherTypeId = null; 
			final String voucherName = null;
			final String voucherNumber= null;
			final String vendorName = null;

	        return new JournalVoucherEntriesData(id, officeId, officeName, glAccountName, glAccountId, glAccountCode, glAccountClassification,
	                transactionDate, entryType, amount, transactionId, manualEntry, entityType, entityId, createdByUserId, createdDate,
	                createdByUserName, comments, reversed, referenceNumber, officeRunningBalance, organizationRunningBalance,
	                runningBalanceComputed, transactionDetailData, currency,ClientName,clientId,chitId,ticketNumber,ChitName,
	                voucherId,voucherTypeId,voucherName,voucherNumber,vendorName);
	    }

	    public Long getId() {
	        return this.id;
	    }

	    public Long getGlAccountId() {
	        return this.glAccountId;
	    }

	    public EnumOptionData getGlAccountType() {
	        return this.glAccountType;
	    }

	    public BigDecimal getAmount() {
	        return this.amount;
	    }

	    public EnumOptionData getEntryType() {
	        return this.entryType;
	    }

	    public Long getOfficeId() {
	        return this.officeId;
	    }

	    public String getTransactionId() {
	        return transactionId;
	    }

		public Long getVoucherId() {
			return voucherId;
		}

		public Integer getVoucherTypeId() {
			return voucherTypeId;
		}

		public String getVoucherName() {
			return voucherName;
		}

		public String getVoucherNumber() {
			return voucherNumber;
		}

		public String getVendorName() {
			return vendorName;
		}

	    
}
