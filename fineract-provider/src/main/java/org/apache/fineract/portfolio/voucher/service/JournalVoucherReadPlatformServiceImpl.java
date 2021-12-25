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
package org.apache.fineract.portfolio.voucher.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.accounting.journalentry.data.TransactionDetailData;
import org.apache.fineract.accounting.journalentry.data.TransactionTypeEnumData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberTransactionData;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitGroup;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitGroupRepository;
import org.apache.fineract.portfolio.ChitGroup.service.ChitDemandScheduleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberTransactionReadPlatformService;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientTransactionReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.note.data.NoteData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionEnumData;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.apache.fineract.portfolio.voucher.Exception.JournalVoucherException;
import org.apache.fineract.portfolio.voucher.data.JournalVoucherEntriesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class JournalVoucherReadPlatformServiceImpl implements JournalVoucherReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final PaginationHelper<JournalVoucherEntriesData> paginationHelper1 = new PaginationHelper<>();
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final ClientReadPlatformService clientReadPlatformService;
    private final ClientTransactionReadPlatformService clientTransactionReadPlatformService;
    private final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService;
    private final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService;
    private final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices;
    private final ChitGroupReadPlatformService chitGroupReadPlatformService;
	private final ClientRepositoryWrapper clientRepository;
	private final ChitGroupRepository chitGroupRepository;
	
	@Autowired
	JournalVoucherReadPlatformServiceImpl(final PlatformSecurityContext context, final ColumnValidator columnValidator,
			final RoutingDataSource dataSource,
			final CodeValueReadPlatformService codeValueReadPlatformService,final FromJsonHelper fromJsonHelper,
			final ClientReadPlatformService clientReadPlatformService,
			final ClientTransactionReadPlatformService clientTransactionReadPlatformService,
			final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService,
			final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService,
			final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices,
			final ChitGroupReadPlatformService chitGroupReadPlatformService,
			final ClientRepositoryWrapper clientRepository,final ChitGroupRepository chitGroupRepository){
		this.context = context;
		this.columnValidator = columnValidator;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.clientReadPlatformService = clientReadPlatformService;
		this.clientTransactionReadPlatformService = clientTransactionReadPlatformService;
		this.chitSubscriberTransactionReadPlatformService = chitSubscriberTransactionReadPlatformService;
		this.chitDemandScheduleReadPlatformService = chitDemandScheduleReadPlatformService;
		this.chitSubscriberChargeReadPlatformServices = chitSubscriberChargeReadPlatformServices;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.clientRepository =clientRepository;
		this.chitGroupRepository = chitGroupRepository;
		
	}
		
		
//	@Override
//	public JsonObject getJournalVoucher(Long id) {
//		try {
//			String sql = null;
//			JsonObject dataToAdd = new JsonObject();
//			if(id!=null && id!=0) {
//				sql = " SELECT jv.id as Id, jv.journal_transaction_id AS  journltransactionId, jv.voucher_type_id AS vouchedType, \r\n"
//						+ " cv.code_value AS voucherName, jv.voucher_number AS voucherNumber, jv.vendor_name as vendorName \r\n"
//						+ " FROM m_journal_vouchers jv\r\n"
//						+ " LEFT JOIN m_code_value cv ON cv.id = jv.voucher_type_id "
//						+ " WHERE jv.id = "+ id +" ";
//			} else {
//				sql = " SELECT jv.id as Id, jv.journal_transaction_id AS  journltransactionId, jv.voucher_type_id AS vouchedType, \r\n"
//						+ " cv.code_value AS voucherName, jv.voucher_number AS voucherNumber, jv.vendor_name as vendorName \r\n"
//						+ " FROM m_journal_vouchers jv\r\n"
//						+ " LEFT JOIN m_code_value cv ON cv.id = jv.voucher_type_id ";
//			}
//			
//			//final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
//			//ArrayList<JsonObject> arr = new ArrayList<JsonObject>(); 
//			
//			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
//			ArrayList<JsonObject> arr = new ArrayList<JsonObject>();
//
//			while(rs.next()) {
//				JsonObject data = new JsonObject();
//				data.addProperty("Id", rs.getLong("Id"));
//				data.addProperty("journltransactionId", rs.getString("journltransactionId"));
//				data.addProperty("vouchedType", rs.getInt("vouchedType"));
//				data.addProperty("voucherName", rs.getString("voucherName"));
//				data.addProperty("voucherNumber", rs.getString("voucherNumber"));
//				data.addProperty("vendorName", rs.getString("vendorName"));
//				
//				arr.add(data);
//			}
//			JsonElement parseData = this.fromJsonHelper.parse(arr.toString());
//			dataToAdd.add("JournalVouchersDetails", parseData);
//			
//			return dataToAdd;
//		} catch (final EmptyResultDataAccessException e) {
//			throw new  JournalVoucherException(null, e);
//		}
//		
//	}
	
	
	@Transactional
	@Override
	public void deleteJournalVoucher(final Long Id) throws SQLException {
		try {
			final String sql = "delete from m_journal_vouchers where id = ?";
			this.jdbcTemplate.update(sql, new Object[] { Id });
		} catch (DataAccessException e) {
			throw new SQLException(e);
		}

	}

	@Override
	public String getVoucherNumber(Integer codeValueId) {
		try {
			final String sql =" SELECT MAX(jv.voucher_number) as voucherNumber \r\n"
					+ " FROM m_journal_vouchers as jv \r\n"
					+ " WHERE jv.voucher_type_id = "+codeValueId+" ";
			
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
			String voucherNumber = null;
			while(rs.next()) {
				voucherNumber = rs.getString("voucherNumber");
			}
			return voucherNumber;
		}catch (final EmptyResultDataAccessException e) {
			return null;
		}
		
	}
	

    private static final class GLJournalEntryMapper implements RowMapper<JournalVoucherEntriesData>  {

        private final JournalEntryAssociationParametersData associationParametersData;
        private final ClientReadPlatformService clientReadPlatformService;
        private final ClientTransactionReadPlatformService clientTransactionReadPlatformService;
        private final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService;
        private final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService;
        private final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices;
        private final ChitGroupReadPlatformService chitGroupReadPlatformService;
        private final ClientRepositoryWrapper clientRepository;
        private final ChitGroupRepository chitGroupRepository;
        
        GLJournalEntryMapper(final JournalEntryAssociationParametersData associationParametersData,final ClientReadPlatformService clientReadPlatformService,final ClientTransactionReadPlatformService clientTransactionReadPlatformService,
        		final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService, final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService,final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices,
        		final ChitGroupReadPlatformService chitGroupReadPlatformService,final ClientRepositoryWrapper clientRepository,final ChitGroupRepository chitGroupRepository) {
            if (associationParametersData == null) {
                this.associationParametersData = new JournalEntryAssociationParametersData();
            } else {
                this.associationParametersData = associationParametersData;
            }
           this.clientReadPlatformService = clientReadPlatformService;
           this.clientTransactionReadPlatformService = clientTransactionReadPlatformService;
           this.chitSubscriberTransactionReadPlatformService = chitSubscriberTransactionReadPlatformService;
           this.chitDemandScheduleReadPlatformService = chitDemandScheduleReadPlatformService;
           this.chitSubscriberChargeReadPlatformServices = chitSubscriberChargeReadPlatformServices;
           this.chitGroupReadPlatformService = chitGroupReadPlatformService;
           this.clientRepository = clientRepository;
           this.chitGroupRepository = chitGroupRepository;
        }
        
        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" journalEntry.client_transaction_id as clientTran,journalEntry.chit_subs_transaction_id as chitsubstransactionid,journalEntry.id as id,journalEntry.client_transaction_id AS clientTranId, glAccount.classification_enum as classification ,").append("journalEntry.transaction_id,")
                    .append(" glAccount.name as glAccountName, glAccount.gl_code as glAccountCode,glAccount.id as glAccountId, ")
                    .append(" journalEntry.office_id as officeId, office.name as officeName, journalEntry.ref_num as referenceNumber, ")
                    .append(" journalEntry.manual_entry as manualEntry,journalEntry.entry_date as transactionDate, ")
                    .append(" journalEntry.type_enum as entryType,journalEntry.amount as amount, journalEntry.transaction_id as transactionId,")
                    .append(" journalEntry.entity_type_enum as entityType, journalEntry.entity_id as entityId,journalEntry.chitgprSubs as chitgprSubs,journalEntry.chit_id as chitId,creatingUser.id as createdByUserId, ")
                    .append(" creatingUser.username as createdByUserName, journalEntry.description as comments, ")
                    .append(" journalEntry.created_date as createdDate, journalEntry.reversed as reversed, ")
                    .append(" journalEntry.currency_code as currencyCode, curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ")
                    .append(" journalEntry.chitgprSubs as chitgprSubs, journalEntry.description as description, ")
                    .append(" curr.display_symbol as currencyDisplaySymbol, curr.decimal_places as currencyDigits, curr.currency_multiplesof as inMultiplesOf, ")
            		.append(" jv.id AS voucherId, jv.voucher_type_id as voucherTypeId, jv.voucher_number AS voucherNumber, ")
            		.append(" jv.vendor_name AS vendorName,cvv.code_value AS voucherName ");
//            if(associationParametersData.isTranType())
//            {
//            	   
//            	sb.append(" ,cl.firstname as firstname,ct.client_id as clientId ");
//            }
            if (associationParametersData.isRunningBalanceRequired()) {
                sb.append(" ,journalEntry.is_running_balance_calculated as runningBalanceComputed, ")
                        .append(" journalEntry.office_running_balance as officeRunningBalance, ")
                        .append(" journalEntry.organization_running_balance as organizationRunningBalance ");
            }
            if (associationParametersData.isTransactionDetailsRequired()) {
                sb.append(" ,pd.receipt_number as receiptNumber, ").append(" pd.check_number as checkNumber, ")
                        .append(" pd.account_number as accountNumber, ").append(" pt.value as paymentTypeName, ")
                        .append(" pd.payment_type_id as paymentTypeId,").append(" pd.bank_number as bankNumber, ")
                        .append(" pd.routing_code as routingCode, ").append(" note.id as noteId, ")
                        .append(" note.note as transactionNote, ").append(" lt.transaction_type_enum as loanTransactionType, ")
                        .append(" st.transaction_type_enum as savingsTransactionType ");
                       
            }
//            sb.append(" from acc_gl_journal_entry as journalEntry ")
//                    .append(" left join acc_gl_account as glAccount on glAccount.id = journalEntry.account_id")
//                    .append(" left join m_office as office on office.id = journalEntry.office_id")
//                    .append(" left join m_appuser as creatingUser on creatingUser.id = journalEntry.createdby_id ")
//                    .append(" join m_currency curr on curr.code = journalEntry.currency_code ")
//                    .append("  LEFT JOIN m_journal_vouchers AS jv ON jv.journal_transaction_id = journalEntry.transaction_id ")
//                    .append(" LEFT JOIN m_code_value AS cvv ON cvv.id = jv.voucher_type_id ");
            
           sb.append(" from m_journal_vouchers as jv ") 
           		.append(" left join  acc_gl_journal_entry as journalentry on journalentry.transaction_id = jv.journal_transaction_id ")
           		.append(" left join acc_gl_account as glaccount on glaccount.id = journalentry.account_id ")   
           		.append(" left join m_office as office on office.id = journalentry.office_id ")
        	    .append(" left join m_appuser as creatinguser on creatinguser.id = journalentry.createdby_id  ")
        		.append(" join m_currency curr on curr.code = journalentry.currency_code  ")       
        		.append("  join m_code_value as cvv on cvv.id = jv.voucher_type_id  ")			 
        		.append(" LEFT JOIN m_payment_detail AS pl ON pl.payment_type_id = journalEntry.payment_details_id ")		  
        		.append(" LEFT JOIN m_payment_type AS pt ON pt.id = pl.payment_type_id  ");
            
            if (associationParametersData.isTransactionDetailsRequired()) {
                sb.append(" left join m_loan_transaction as lt on journalEntry.loan_transaction_id = lt.id ")
                        .append(" left join m_savings_account_transaction as st on journalEntry.savings_transaction_id = st.id ")
                        .append(" left join m_payment_detail as pd on lt.payment_detail_id = pd.id or st.payment_detail_id = pd.id or journalEntry.payment_details_id = pd.id")
                        .append(" left join m_payment_type as pt on pt.id = pd.payment_type_id ")
                        .append(" left join m_note as note on lt.id = note.loan_transaction_id or st.id = note.savings_account_transaction_id ");
            }
            
           
            return sb.toString();

        }

        @Override
        public JournalVoucherEntriesData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
        	
        	String chitName = null;
        	Long chitId = null;
        	Long ticketnum = null;
        	 Long clientId = null;
        	  String ClientName = null;
        	  Long chitgprSubs = null;
        	  String description = null;
            final Long id = rs.getLong("id");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final String glCode = rs.getString("glAccountCode");
            final String glAccountName = rs.getString("glAccountName");
            final Long glAccountId = rs.getLong("glAccountId");
            final int accountTypeId = JdbcSupport.getInteger(rs, "classification");
            final EnumOptionData accountType = AccountingEnumerations.gLAccountType(accountTypeId);
            final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
            final Boolean manualEntry = rs.getBoolean("manualEntry");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final int entryTypeId = JdbcSupport.getInteger(rs, "entryType");
            final EnumOptionData entryType = AccountingEnumerations.journalEntryType(entryTypeId);
            final String transactionId = rs.getString("transactionId");
            final Long tranId = rs.getLong("clientTranId");
             chitgprSubs = rs.getLong("chitgprSubs");
             description = rs.getString("description");
            final Long chitGrpId = rs.getLong("chitId");
            final String referenceNumber = rs.getString("referenceNumber");
            if(tranId!=null && tranId!=0)
            {
            	 clientId = clientTransactionReadPlatformService.retrieveTransactionByTransactionId(tranId).getClientId();
            	 if(clientId!=null)
            	 {
            		 ClientName = this.clientReadPlatformService.retrieveOne(clientId).getFirstname();
            	 }
            }
            
            if(chitGrpId!=null && chitGrpId!=0)
            {
            	chitName = this.chitGroupReadPlatformService.retrieveChitGroup(chitGrpId).getName();
            	chitId = chitGrpId;
            }
            
            final Long voucherId = rs.getLong("voucherId");
             Integer voucherTypeId = null;
		     String voucherName = null;
		     String voucherNumber = null;
		     String vendorName = null;
            if(voucherId!=null && voucherId!=0)
            {
            	 voucherTypeId = rs.getInt("voucherTypeId");
    		     voucherName = rs.getString("voucherName");
    		     voucherNumber = rs.getString("voucherNumber");
    		     vendorName = rs.getString("vendorName");
            }
            final Long chitSubsTranId = rs.getLong("chitsubstransactionid");
            if(chitSubsTranId!=null && chitSubsTranId!=0)
            {
            	
            	ChitSubscriberTransactionData tranData = chitSubscriberTransactionReadPlatformService.retrieveById(chitSubsTranId);
            	Long chitDemandId = tranData.getChitdemandscheduleId();
            	Long chitSubscriberChargeId = tranData.getChitsubscriberchargeId();
            	if(chitDemandId!=null && chitDemandId!=0)
            	{
            		
            		Long chitsubsId = this.chitDemandScheduleReadPlatformService.retrieveById(chitDemandId).getChitSubscriberChargeId();
            		
            		if(chitsubsId!=null && chitsubsId!=0)
            		{
            			
            			Long chitSubscrinerID = chitSubscriberChargeReadPlatformServices.retrieveNameById(chitsubsId).getChitSubscriberId();
            			
            			if(chitSubscrinerID!=null && chitSubscrinerID!=0)
            			{
            				
            				clientId = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscrinerID).getClientId();
            				chitId = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscrinerID).getChitId();
            				ticketnum = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscrinerID).getChitNumber().longValue();
            				if(chitId!=null && chitId!=0)
            				{
            					chitName = this.chitGroupReadPlatformService.retrieveChitGroup(chitId).getName();
            				}
            				 if(clientId!=null && clientId!=0)
                        	 {
            					
                        		 ClientName = this.clientReadPlatformService.retrieveOne(clientId).getFirstname();
                        	 }
            			}
            		}
            	}
            	else if(chitSubscriberChargeId!=null && chitSubscriberChargeId!=0)
            	{
            		
            		Long chitSubscrinerID = chitSubscriberChargeReadPlatformServices.retrieveNameById(chitSubscriberChargeId).getChitSubscriberId();
            		//////System.out.println("chitSubscrinerID "+chitSubscrinerID);
            		if(chitSubscrinerID!=null && chitSubscrinerID!=0)
            		{
            			clientId = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscrinerID).getClientId();
            			chitId = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscrinerID).getChitId();
        				ticketnum = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscrinerID).getChitNumber().longValue();
        				if(chitId!=null && chitId!=0)
        				{
        					chitName = this.chitGroupReadPlatformService.retrieveChitGroup(chitId).getName();
        				}
            			if(clientId!=null && clientId!=0)
            			{
            				//////System.out.println("clientId "+clientId);
            				
            				ClientName = this.clientReadPlatformService.retrieveOne(clientId).getFirstname();
            				
            				////System.out.println("clientName "+ClientName);
            			}
            		}

            	}
            }
            if(chitgprSubs!=null && chitgprSubs!=0)
            {
            	ChitGroupSubscriberData data = this.chitGroupReadPlatformService.getChitSubscriber(chitgprSubs);
            	clientId =  data.getClientId();
            	chitId = data.getChitId();
            	ClientName = this.clientRepository.findOneWithNotFoundDetection(clientId).getDisplayName();
            	ticketnum = data.getChitNumber().longValue();
            	Optional<ChitGroup> chitData = chitGroupRepository.findById(chitId);
            	if(chitData.isPresent())
            	{
            		chitName = chitData.get().getName();
            	}
            }
            final Integer entityTypeId = JdbcSupport.getInteger(rs, "entityType");
            EnumOptionData entityType = null;
            if (entityTypeId != null) {
                entityType = AccountingEnumerations.portfolioProductType(entityTypeId);

            }

            final Long entityId = JdbcSupport.getLong(rs, "entityId");
            
//            if(entityId!=null)
//            {
//            	Client clint = clientRepository.findOneWithNotFoundDetection(entityId);
//            	if(clint!=null)
//            	{
//            		ClientName = clint.getDisplayName();
//            		clientId = clint.getId();
//            	}
//            	Optional<ChitGroup> chitData = chitGroupRepository.findById(entityId);
//            	if(chitData.isPresent())
//            	{
//            		chitId = chitData.get().getId();
//            		chitName = chitData.get().getName();
//            	}
//            }
            final Long createdByUserId = rs.getLong("createdByUserId");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            final String createdByUserName = rs.getString("createdByUserName");
            final String comments = rs.getString("comments");
            final Boolean reversed = rs.getBoolean("reversed");
            BigDecimal officeRunningBalance = null;
            BigDecimal organizationRunningBalance = null;
            Boolean runningBalanceComputed = null;

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol,
                    currencyNameCode);

            if (associationParametersData.isRunningBalanceRequired()) {
                officeRunningBalance = rs.getBigDecimal("officeRunningBalance");
                organizationRunningBalance = rs.getBigDecimal("organizationRunningBalance");
                runningBalanceComputed = rs.getBoolean("runningBalanceComputed");
            }
            TransactionDetailData transactionDetailData = null;

            if (associationParametersData.isTransactionDetailsRequired()) {
                PaymentDetailData paymentDetailData = null;
                final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentTypeId");
                if (paymentTypeId != null) {
                    final String typeName = rs.getString("paymentTypeName");
                    final PaymentTypeData paymentType = PaymentTypeData.instance(paymentTypeId, typeName);
                    final String accountNumber = rs.getString("accountNumber");
                    final String checkNumber = rs.getString("checkNumber");
                    final String routingCode = rs.getString("routingCode");
                    final String receiptNumber = rs.getString("receiptNumber");
                    final String bankNumber = rs.getString("bankNumber");
                    paymentDetailData = new PaymentDetailData(id, paymentType, accountNumber, checkNumber, routingCode, receiptNumber,
                            bankNumber,null,null);
                }
                NoteData noteData = null;
                final Long noteId = JdbcSupport.getLong(rs, "noteId");
                if (noteId != null) {
                    final String note = rs.getString("transactionNote");
                    noteData = new NoteData(noteId, null, null, null, null, null, null, null, note, null, null, null, null, null, null);
                }
                Long transaction = null;
                if (entityType != null) {
                    transaction = Long.parseLong(transactionId.substring(2).trim());
                }

                TransactionTypeEnumData transactionTypeEnumData = null;

                if (PortfolioAccountType.fromInt(entityTypeId).isLoanAccount()) {
                    final LoanTransactionEnumData loanTransactionType = LoanEnumerations
                            .transactionType(JdbcSupport.getInteger(rs, "loanTransactionType"));
                    transactionTypeEnumData = new TransactionTypeEnumData(loanTransactionType.id(), loanTransactionType.getCode(),
                            loanTransactionType.getValue());
                } else if (PortfolioAccountType.fromInt(entityTypeId).isSavingsAccount()) {
                    final SavingsAccountTransactionEnumData savingsTransactionType = SavingsEnumerations
                            .transactionType(JdbcSupport.getInteger(rs, "savingsTransactionType"));
                    transactionTypeEnumData = new TransactionTypeEnumData(savingsTransactionType.getId(), savingsTransactionType.getCode(),
                            savingsTransactionType.getValue());
                }

                transactionDetailData = new TransactionDetailData(transaction, paymentDetailData, noteData, transactionTypeEnumData);
            }
           
            return new JournalVoucherEntriesData(id, officeId, officeName, glAccountName, glAccountId, glCode, accountType, transactionDate,
                    entryType, amount, transactionId, manualEntry, entityType, entityId, createdByUserId, createdDate, createdByUserName,
                    comments, reversed, referenceNumber, officeRunningBalance, organizationRunningBalance, runningBalanceComputed,
                    transactionDetailData, currency,ClientName,clientId,chitId,ticketnum,chitName,chitgprSubs,description,
                    voucherId, voucherTypeId,voucherName,voucherNumber,vendorName);
        }
    }


    @Override
    public Page<JournalVoucherEntriesData> retrieveAll(final SearchParameters searchParameters, final Long glAccountId,
            final Boolean onlyManualEntries, final Date fromDate, final Date toDate, final String transactionId, final String referenceNumber,
            final Integer entityType,
            final JournalEntryAssociationParametersData associationParametersData) {

        GLJournalEntryMapper rm = new GLJournalEntryMapper(associationParametersData,clientReadPlatformService,clientTransactionReadPlatformService,chitSubscriberTransactionReadPlatformService,chitDemandScheduleReadPlatformService,chitSubscriberChargeReadPlatformServices,
        		chitGroupReadPlatformService,clientRepository,chitGroupRepository);
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        
        sqlBuilder.append(rm.schema());

        final Object[] objectArray = new Object[15];
        int arrayPos = 0;
        String whereClose = " where ";

        if (StringUtils.isNotBlank(transactionId)) {
        	
            
            sqlBuilder.append(whereClose + " journalEntry.transaction_id = ?");
            objectArray[arrayPos] = transactionId;
            arrayPos = arrayPos + 1;
            whereClose = " and ";
        }
        
        if (StringUtils.isNotBlank(referenceNumber)) {
       	 
		      sqlBuilder.append(whereClose + " journalEntry.ref_num = ?");
		      objectArray[arrayPos] = referenceNumber;
		      arrayPos = arrayPos + 1;
		      whereClose = " and ";
		 }
        

        if (entityType != null && entityType != 0 && (onlyManualEntries == null)) {

            sqlBuilder.append(whereClose + " journalEntry.entity_type_enum = ?");

            objectArray[arrayPos] = entityType;
            arrayPos = arrayPos + 1;

            whereClose = " and ";
        }
        

        if (searchParameters.isOfficeIdPassed()) {
            sqlBuilder.append(whereClose + " journalEntry.office_id = ?");
            objectArray[arrayPos] = searchParameters.getOfficeId();
            arrayPos = arrayPos + 1;

            whereClose = " and ";
        }

        if (searchParameters.isCurrencyCodePassed()) {
            sqlBuilder.append(whereClose + " journalEntry.currency_code = ?");
            objectArray[arrayPos] = searchParameters.getCurrencyCode();
            arrayPos = arrayPos + 1;

            whereClose = " and ";
        }

        if (glAccountId != null && glAccountId != 0) {
            sqlBuilder.append(whereClose + " journalEntry.account_id = ?");
            objectArray[arrayPos] = glAccountId;
            arrayPos = arrayPos + 1;

            whereClose = " and ";
        }

        if (fromDate != null || toDate != null) {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String fromDateString = null;
            String toDateString = null;
            if (fromDate != null && toDate != null) {
                sqlBuilder.append(whereClose + " journalEntry.entry_date between ? and ? ");

                whereClose = " and ";

                fromDateString = df.format(fromDate);
                toDateString = df.format(toDate);
                objectArray[arrayPos] = fromDateString;
                arrayPos = arrayPos + 1;
                objectArray[arrayPos] = toDateString;
                arrayPos = arrayPos + 1;
            } else if (fromDate != null) {
                sqlBuilder.append(whereClose + " journalEntry.entry_date >= ? ");
                fromDateString = df.format(fromDate);
                objectArray[arrayPos] = fromDateString;
                arrayPos = arrayPos + 1;
                whereClose = " and ";

            } else if (toDate != null) {
                sqlBuilder.append(whereClose + " journalEntry.entry_date <= ? ");
                toDateString = df.format(toDate);
                objectArray[arrayPos] = toDateString;
                arrayPos = arrayPos + 1;

                whereClose = " and ";
            }
        }

        if (onlyManualEntries != null) {
            if (onlyManualEntries) {
                sqlBuilder.append(whereClose + " journalEntry.manual_entry = 1");

                whereClose = " and ";
            }
        }

        if (searchParameters.isLoanIdPassed()) {
            sqlBuilder.append(whereClose + " journalEntry.loan_transaction_id  in (select id from m_loan_transaction where loan_id = ?)");
            objectArray[arrayPos] = searchParameters.getLoanId();
            arrayPos = arrayPos + 1;

            whereClose = " and ";
        }
        if (searchParameters.isSavingsIdPassed()) {
            sqlBuilder.append(whereClose
                    + " journalEntry.savings_transaction_id in (select id from m_savings_account_transaction where savings_account_id = ?)");
            objectArray[arrayPos] = searchParameters.getSavingsId();
            arrayPos = arrayPos + 1;

            whereClose = " and ";
        }

        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
            this.columnValidator.validateSqlInjection(sqlBuilder.toString(), searchParameters.getOrderBy());

            if (searchParameters.isSortOrderProvided()) {
                sqlBuilder.append(' ').append(searchParameters.getSortOrder());
                this.columnValidator.validateSqlInjection(sqlBuilder.toString(), searchParameters.getOrderBy());
            }
        } else {
            sqlBuilder.append(" order by journalEntry.entry_date, journalEntry.id");
        }

        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        final Object[] finalObjectArray = Arrays.copyOf(objectArray, arrayPos);
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper1.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), finalObjectArray, rm);
    }


	@Override
	public
    List<JournalVoucherEntriesData> getJournalVoucher(Long id) {
		try {
			GLJournalEntryMapper gm = new GLJournalEntryMapper(null, clientReadPlatformService, clientTransactionReadPlatformService, chitSubscriberTransactionReadPlatformService, chitDemandScheduleReadPlatformService, chitSubscriberChargeReadPlatformServices, chitGroupReadPlatformService, clientRepository, chitGroupRepository);
			String sql = "SELECT "+ gm.schema() + " where jv.id = ? " ;
			return this.jdbcTemplate.query(sql, new Object[] { id }, gm);
		}catch(final EmptyResultDataAccessException e) {
			throw new JournalVoucherException(id, e);
		}
	}
	
	    
}
