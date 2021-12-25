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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberTransactionData;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitTransactionEnum;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailsReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

@Service
public class ChitSubscriberTransactionReadPlatformServiceImpl implements ChitSubscriberTransactionReadPlatformService
{
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitBidsData> paginationHelper = new PaginationHelper<>();
	
	private final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService;
	
	@Autowired
	public ChitSubscriberTransactionReadPlatformServiceImpl(PlatformSecurityContext context,
			ColumnValidator columnValidator,final RoutingDataSource dataSource,final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService) {
	
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.columnValidator = columnValidator;
		this.paymentDetailsReadPlatformService = paymentDetailsReadPlatformService;
	}
	
	private static final class ChitsSubscriberChargeMapper implements RowMapper<ChitSubscriberTransactionData>
	{
		
	
		private final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService;
		
		
		@Autowired
		public ChitsSubscriberChargeMapper(PaymentDetailsReadPlatformService paymentDetailsReadPlatformService) {
			super();
			this.paymentDetailsReadPlatformService = paymentDetailsReadPlatformService;
		}

		public String schema() 
		{
			return "cs.id as id,cs.chit_demand_schedule_id as chitdemandscheduleId,cs.chit_subscriber_id as chitsubscriberId,cs.chit_subscriber_charge_id as chitsubscriberchargeId,cs.amount as amount,cs.tran_type_enum as trantypeEnum , cs.payment_detail_id as paymentdetailId,cs.transaction_date as transactiondate,cs.is_reversed as isreversed,cs.is_processed as isprocessed, cs.waiveoff_amount as waiveoffamount from chit_subscriber_transaction cs ";
		}
		
		@Override
		public ChitSubscriberTransactionData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			LocalDateTime transDate = null;
			Long id = rs.getLong("id");
			Long chitdemandscheduleId = rs.getLong("chitdemandscheduleId");
			Long chitsubscriberId = rs.getLong("chitsubscriberId");
			Long chitsubscriberchargeId = rs.getLong("chitsubscriberchargeId");
			Double amount = rs.getDouble("amount");
			Long trantypeEnum = rs.getLong("trantypeEnum");
			Integer tran = rs.getInt("trantypeEnum");
			ChitTransactionEnum trantype = ChitTransactionEnum.fromInt(tran);
			Long paymentdetailId = rs.getLong("paymentdetailId");
			ZonedDateTime transactiondate = JdbcSupport.getDateTime(rs, "transactiondate");
			if(transactiondate!=null)
			{
				transDate = transactiondate.toLocalDateTime();
			}
			Boolean isreversed = rs.getBoolean("isreversed");
			Boolean isprocessed = rs.getBoolean("isprocessed");
			PaymentDetailData paymentDetailData = null;
			if(paymentdetailId!=null && paymentdetailId!=0l)
			{
				paymentDetailData = paymentDetailsReadPlatformService.retrivePaymentDetails(paymentdetailId);
			}
			Long waiveoffamount = rs.getLong("waiveoffamount");
			return ChitSubscriberTransactionData.newinstance(id, chitdemandscheduleId, chitsubscriberId, chitsubscriberchargeId, amount, trantype, trantypeEnum, paymentdetailId,transDate, isreversed, isprocessed,
					paymentDetailData, waiveoffamount);
		}
		
	}

	@Override
	public Collection<ChitSubscriberTransactionData> retrieveAll() 
	{
		try {
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper(this.paymentDetailsReadPlatformService);
			final String sql = "select " + rm.schema();

			return this.jdbcTemplate.query(sql, rm);
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}

	@Override
	public ChitSubscriberTransactionData retrieveById(Long id) {
		try {
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper(paymentDetailsReadPlatformService);
			final String sql = "select " + rm.schema() + " where cs.id = ? ";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public ArrayList<JsonElement> retrieveUnProcessedCollectionsByAgentByDate(Long agentId, String date) {
		
		try {
			final String sql = "select pd.payment_type_id as paymentType,pd.receipt_number AS recieptNumber, cg.name as chitGroupName, c.firstname as clientName, cst.amount, cst.id as subsTranId from  chit_subscriber_transaction cst"
			+  " left join m_payment_detail pd on pd.id = cst.payment_detail_id  left join chit_demand_schedule cds on cds.id= cst.chit_demand_schedule_id "
			+  " left join chit_subscriber_charge csc on csc.id = cds.chit_subscriber_charge_id "
			+  " left join chit_group_subscriber cgs on cgs.id = csc.chit_subscriber_id "
			+  " left join chit_group cg on cg.id = cgs.chit_id left join m_client c on c.id = cgs.client_id "
			+  " where cst.is_processed = 0 and date(cst.transaction_date) = date(?)  and cds.staff_id = ? ";

			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] { date, agentId});
			ArrayList<JsonElement> arr = new ArrayList<JsonElement>(); 

			while (rs.next()) {	
				JsonObject json = new JsonObject();
				json.addProperty("id", rs.getLong("subsTranId"));
				json.addProperty("chitGroup", rs.getString("chitGroupName"));
				json.addProperty("clientName", rs.getString("clientName"));
				json.addProperty("amount", rs.getBigDecimal("amount"));
				json.addProperty("type", rs.getInt("paymentType"));
				json.addProperty("RecieptNumber", rs.getString("recieptNumber"));
				arr.add(json);
			}
			
			return arr;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	@Override
	public ArrayList<JsonElement> retrieveUnProcessedCollectionsByAgentByDateForCharges(Long agentId, String date) {
		
		try {
			final String sql = "SELECT cc.name AS charge,cst.amount AS amount,cg.name as chitGroupName,c.firstname as firstname ,pd.id,pd.payment_type_id as paymentType ,pd.receipt_number as recieptNumber,cst.id AS tranId "
					+ "FROM chit_subscriber_transaction cst "
					+ "LEFT JOIN chit_subscriber_charge csg ON csg.id = cst.chit_subscriber_charge_id "
					+ "LEFT JOIN chit_group_subscriber cgs ON cgs.id = csg.chit_subscriber_id "
					+ "LEFT JOIN m_client c ON c.id = cgs.client_id "
					+ "LEFT JOIN m_payment_detail pd ON pd.id = cst.payment_detail_id "
					+ "LEFT JOIN chit_group cg ON cg.id = cgs.chit_id "
					+ "LEFT JOIN chit_charge cc ON cc.id = csg.chit_charge_id "
					+ "WHERE csg.staff_id = ? AND cst.is_processed = 0 AND date(cst.transaction_date) = date(?) ";

			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] { agentId,date});
			ArrayList<JsonElement> arr = new ArrayList<JsonElement>(); 

			while (rs.next()) {	
				JsonObject json = new JsonObject();
				json.addProperty("Transactionid", rs.getLong("tranId"));
				json.addProperty("chitGroup", rs.getString("chitGroupName"));
				json.addProperty("clientName", rs.getString("firstname"));
				json.addProperty("amount", rs.getBigDecimal("amount"));
				json.addProperty("type", rs.getInt("paymentType"));
				json.addProperty("RecieptNumber", rs.getString("recieptNumber"));
				json.addProperty("ChargeType", rs.getString("charge"));
				arr.add(json);
			}
			
			return arr;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}


	@Override
	public ChitSubscriberTransactionData retrieveData(Long chitsubsid,Integer trantype) {
		try {
	
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper(paymentDetailsReadPlatformService);
			final String sql = "select " + rm.schema() + " where cs.chit_subscriber_id = ? and cs.tran_type_enum = ? ";
			////System.out.println(sql);
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { chitsubsid,trantype });
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}	
	
	@Override
	public Collection<ChitSubscriberTransactionData> retrieveDataUsingDemandId(Long demandId) {
		try {
	
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper(paymentDetailsReadPlatformService);
			final String sql = "select " + rm.schema() + " where cs.chit_demand_schedule_id = ?  ";
			////System.out.println(sql);
			return this.jdbcTemplate.query(sql, rm, new Object[] { demandId });
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Collection<ChitSubscriberTransactionData> retrieveTransactionDataUsingPaymentId(Long tranId) {
		try {
			
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper(paymentDetailsReadPlatformService);
			final String sql = "select " + rm.schema() + " where cs.payment_detail_id = ?  ";
			////System.out.println(sql);
			return this.jdbcTemplate.query(sql, rm, new Object[] { tranId });
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}	
	
}
