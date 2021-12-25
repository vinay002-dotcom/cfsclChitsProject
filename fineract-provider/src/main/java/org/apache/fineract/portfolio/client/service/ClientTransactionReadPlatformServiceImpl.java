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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.client.data.ClientTransactionData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.domain.ClientTransactionType;
import org.apache.fineract.portfolio.client.exception.ClientTransactionNotFoundException;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

@Service
public class ClientTransactionReadPlatformServiceImpl implements ClientTransactionReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final ClientTransactionMapper clientTransactionMapper;
    private final PaginationHelper<ClientTransactionData> paginationHelper;

    @Autowired
    public ClientTransactionReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.clientTransactionMapper = new ClientTransactionMapper();
        this.paginationHelper = new PaginationHelper<>();
    }

    private static final class ClientTransactionMapper implements RowMapper<ClientTransactionData> {

        private final String schemaSql;

        ClientTransactionMapper() {

            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("tr.id as transactionId, tr.transaction_type_enum as transactionType,  ");
            sqlBuilder.append("tr.transaction_date as transactionDate, tr.amount as transactionAmount, ");
            sqlBuilder.append("tr.created_date as submittedOnDate, tr.is_reversed as reversed,tr.adjusted as adjusted,tr.is_processed as isProcessed,tr.created_date as createddate, ");
            sqlBuilder.append("tr.external_id as externalId, o.name as officeName, o.id as officeId, ");
            sqlBuilder.append("c.id as clientId, c.account_no as accountNo, ccpb.client_charge_id as clientChargeId, ");
            sqlBuilder.append("pd.id as pdid,pd.payment_type_id as paymentType,pd.account_number as accountNumber,pd.check_number as checkNumber, ");
            sqlBuilder.append("pd.receipt_number as receiptNumber, pd.bank_number as bankNumber,pd.routing_code as routingCode, pd.transactionNo as transactionNo,pd.depositedDate as depositedDate,  ");
            sqlBuilder.append(
                    "tr.currency_code as currencyCode, curr.decimal_places as currencyDigits, curr.currency_multiplesof as inMultiplesOf, ");
            sqlBuilder.append("curr.name as currencyName, curr.internationalized_name_code as currencyNameCode,  ");
            sqlBuilder.append("curr.display_symbol as currencyDisplaySymbol,  ");
            sqlBuilder.append("pt.value as paymentTypeName  ");
            sqlBuilder.append("from m_client c  ");
            sqlBuilder.append("join m_client_transaction tr on tr.client_id = c.id ");
            sqlBuilder.append("join m_currency curr on curr.code = tr.currency_code ");
            sqlBuilder.append("left join m_payment_detail pd on tr.payment_detail_id = pd.id  ");
            sqlBuilder.append("left join m_payment_type pt  on pd.payment_type_id = pt.id ");
            sqlBuilder.append("left join m_office o on o.id = tr.office_id ");
            sqlBuilder.append("left join m_client_charge_paid_by ccpb on ccpb.client_transaction_id = tr.id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public ClientTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("transactionId");
            final Long clientId = rs.getLong("clientId");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final String externalId = rs.getString("externalId");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final EnumOptionData transactionType = ClientEnumerations.clientTransactionType(transactionTypeInt);

            final LocalDate date = JdbcSupport.getLocalDate(rs, "transactionDate");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "transactionAmount");
            final boolean reversed = rs.getBoolean("reversed");
            final Boolean adjusted = rs.getBoolean("adjusted");
            final Boolean isProcessed = rs.getBoolean("isProcessed");
            PaymentDetailData paymentDetailData = null;
           
            if (ClientTransactionType.fromInt(transactionType.getId().intValue()).equals(ClientTransactionType.CHIT_ADVANCE)) {
                final Long paymentTypeId = rs.getLong("paymentType");
                if (paymentTypeId != null) {
                	final Long pd = rs.getLong("pdid");
                    final String typeName = rs.getString("paymentTypeName");
                    final PaymentTypeData paymentType = PaymentTypeData.instance(paymentTypeId, typeName);
                    final String accountNumber = rs.getString("accountNumber");
                    final String checkNumber = rs.getString("checkNumber");
                    final String routingCode = rs.getString("routingCode");
                    final String receiptNumber = rs.getString("receiptNumber");
                    final String bankNumber = rs.getString("bankNumber");
                    final LocalDate depositedDate = JdbcSupport.getLocalDate(rs, "depositedDate");
                    final Long tranNum = rs.getLong("transactionNo");
                    paymentDetailData = new PaymentDetailData(pd, paymentType, accountNumber, checkNumber, routingCode, receiptNumber,
                            bankNumber,depositedDate,tranNum);
                }
            }
            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final ZonedDateTime createddate =  JdbcSupport.getLocalDateTime(rs, "createddate");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol,
                    currencyNameCode);

            return ClientTransactionData.create(id, officeId, officeName, transactionType, date, currency, paymentDetailData, amount,
                    externalId, submittedOnDate, reversed,adjusted,null,createddate,clientId,isProcessed);
        }
    }

    @Override
    public Page<ClientTransactionData> retrieveAllTransactions(Long clientId, SearchParameters searchParameters) {
        Object[] parameters = new Object[1];
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ").append(this.clientTransactionMapper.schema()).append(" where c.id = ? ");
        parameters[0] = clientId;
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        sqlBuilder.append(" order by tr.transaction_date DESC, tr.created_date DESC, tr.id DESC ");

        // apply limit and offsets
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), parameters,
                this.clientTransactionMapper);
    }

    @Override
    public Collection<ClientTransactionData> retrieveAllTransactions(Long clientId, Long chargeId) {
        Object[] parameters = new Object[1];
        String sql = "select " + this.clientTransactionMapper.schema() + " where c.id = ? ";
        if (chargeId != null) {
            parameters = new Object[2];
            parameters[1] = chargeId;
            sql = sql + " and ccpb.client_charge_id = ?";
        }
        parameters[0] = clientId;
        sql = sql + " order by tr.transaction_date DESC, tr.created_date DESC, tr.id DESC";
        return this.jdbcTemplate.query(sql, this.clientTransactionMapper, parameters);
    }
    
    @Override
    public ArrayList<JsonElement> retrieveAllTransactionforBulkApproval(Long staffId , String date) {
        
        String sql = "SELECT mct.client_id as clientid , mct.id AS tranId,c.firstname AS firstname,mct.amount,pd.payment_type_id as paymentType,pd.receipt_number AS recieptNumber FROM m_client_transaction mct "
        		+ "JOIN m_payment_detail pd ON pd.id = mct.payment_detail_id "
        		+ "JOIN m_client c ON c.id = mct.client_id WHERE c.staff_id = ? AND mct.transaction_date = ? AND mct.is_processed = '0';";
     
        final SqlRowSet rs =  this.jdbcTemplate.queryForRowSet(sql, new Object[] {staffId,date});
        ArrayList<JsonElement> arr = new ArrayList<JsonElement>(); 
    	while (rs.next()) {	
			JsonObject json = new JsonObject();
			json.addProperty("TransactionId", rs.getLong("tranId"));
			json.addProperty("FirstName", rs.getString("firstname"));
			json.addProperty("amount", rs.getBigDecimal("amount"));
			json.addProperty("type", rs.getInt("paymentType"));
			json.addProperty("clientid", rs.getInt("clientid"));
			json.addProperty("RecieptNumber", rs.getString("recieptNumber"));
			arr.add(json);
		}
    	return arr;
    }
    
    @Override
    public Collection<ClientTransactionData> retrieveAllTransactionsUsingId(Long clientId) {
        Object[] parameters = new Object[1];
        String sql = "select " + this.clientTransactionMapper.schema() + " where c.id = ? and tr.adjusted = false and tr.is_processed = true and tr.is_reversed = false";
        parameters[0] = clientId;
        sql = sql + " order by tr.transaction_date DESC, tr.created_date DESC, tr.id DESC";
        return this.jdbcTemplate.query(sql, this.clientTransactionMapper, parameters);
    }

    @Override
    public ClientTransactionData retrieveTransaction(Long clientId, Long transactionId) {
        try {
            final String sql = "select " + this.clientTransactionMapper.schema() + " where c.id = ? and tr.id= ?";
            return this.jdbcTemplate.queryForObject(sql, this.clientTransactionMapper, new Object[] { clientId, transactionId });
        } catch (final EmptyResultDataAccessException e) {
            throw new ClientTransactionNotFoundException(clientId, transactionId, e);
        }
    }
    
    @Override
    public ClientTransactionData retrieveTransactionByTransactionId(Long transactionId) {
        try {
            final String sql = "select " + this.clientTransactionMapper.schema() + " where tr.id= ?";
            return this.jdbcTemplate.queryForObject(sql, this.clientTransactionMapper, new Object[] {transactionId });
        } catch (final EmptyResultDataAccessException e) {
           return null;
        }
    }
    



    @Override
    public ArrayList<JsonElement> retrieveAllUnAdjustedOfChitSubscribers(String subscriberList) {
        try {
            

            final String sql = "select client_id as clientId, sum(amount) as amount from m_client_transaction ct where ct.adjusted=false and ct.is_processed = true and ct.client_id in (" + subscriberList + ") group by ct.client_id";
            final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);
			ArrayList<JsonElement> arr = new ArrayList<JsonElement>(); 

			while (rs.next()) {
                JsonObject json = new JsonObject();
				json.addProperty("clientId", rs.getLong("clientId"));
				json.addProperty("amount", rs.getBigDecimal("amount"));
				arr.add(json);
			}
            
            /// ////System.out.println("arr");       ////System.out.println(arr);       
			return arr;

        } catch (final EmptyResultDataAccessException e) {
            throw new ClientTransactionNotFoundException(e);
        }
    }

	@Override
	public Collection<ClientTransactionData> retrieveTransactionByPaymentDetailId(Long transactionId) {
		
	        String sql = "select " + this.clientTransactionMapper.schema() + " where tr.payment_detail_id = ? ";
	        sql = sql + " order by tr.transaction_date DESC, tr.created_date DESC, tr.id DESC";
	        return this.jdbcTemplate.query(sql, this.clientTransactionMapper,new Object[] {transactionId});
	}

	@Override
	public Double retriveAdvanceAmountForNotAdjusted(Long clientId) {
				double amount = 0.0;
				final String sql = "SELECT SUM(amount) as amount FROM m_client_transaction WHERE client_id = "+clientId+" AND adjusted = false AND is_processed = true ";
				final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
				JsonObject json = new JsonObject();
				while (rs.next()) 
				{	
					amount = rs.getDouble("amount");
				}	
		return amount;
	}
}
