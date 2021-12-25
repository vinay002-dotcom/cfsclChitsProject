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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.infrastructure.security.utils.SQLBuilder;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsWinnerData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitCycleData;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupCycleWinnerNotFoundException;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ChitBidsReadPlatformServiceImpl implements ChitBidsReadPlatformService {
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ChitBidsMapper chitGroupMapper = new ChitBidsMapper();
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitBidsData> paginationHelper = new PaginationHelper<>();
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final FromJsonHelper fromJsonHelper;

	@Autowired
	public ChitBidsReadPlatformServiceImpl(final PlatformSecurityContext context,
			final ColumnValidator columnValidator, final RoutingDataSource dataSource,final CodeValueReadPlatformService codeValueReadPlatformService,final FromJsonHelper fromJsonHelper) {
		this.columnValidator = columnValidator;
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
	}

	private  final class ChitBidsMapper implements RowMapper<ChitBidsData> {
		public String schema() {
			return " cb.id as id, cb.chit_cycle_id as chitCycleId, cb.chit_subscriber_id as chitSubscriberId, cb.bid_amount as bidAmount, cb.bid_won as bidWon,cb.bidder_participation as bidderparticipation,cb.bid_date as bidDate,"
					+ "cb.is_prize_money_paid as isPaid from chit_group_bids cb join chit_group_cycle cc on cc.id=cb.chit_cycle_id";
		}

		@Override
		public ChitBidsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			final Long id = rs.getLong("id");
			final Long chitSubscriberId = rs.getLong("chitSubscriberId");
			final Long chitCycleId = rs.getLong("chitCycleId");
			final Double bidAmount = rs.getDouble("bidAmount");			
			final Boolean bidWon = rs.getBoolean("bidWon");
			final Long bidderparticipationId = rs.getLong("bidderparticipation");
			CodeValueData codeValueData = codeValueReadPlatformService.retrieveCodeValue(bidderparticipationId);
			final String bidderparticipation =  codeValueData.getName();
			final LocalDate bidDate = JdbcSupport.getLocalDate(rs, "bidDate");
			final Boolean isPaid = rs.getBoolean("isPaid");
			return ChitBidsData.instance(id, chitSubscriberId, chitCycleId, bidAmount, bidWon ,bidderparticipation,bidderparticipationId,bidDate,isPaid);
		}
	}
	
	private  final class ChitBidCountMapper implements RowMapper<ChitBidsData> {
		public String schema() {
			return " cb.id as id, cb.chit_cycle_id as chitCycleId, cb.chit_subscriber_id as chitSubscriberId, cb.bid_amount as bidAmount, cb.bid_won as bidWon,cb.bidder_participation as bidderparticipation,cb.bid_date as bidDate,"
					+ "cb.is_prize_money_paid as isPaid from chit_group_bids cb join chit_group_cycle cc on cc.id=cb.chit_cycle_id";
		}

		@Override
		public ChitBidsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			final Long id = rs.getLong("id");
			final Long chitSubscriberId = rs.getLong("chitSubscriberId");
			final Long chitCycleId = rs.getLong("chitCycleId");
			final Double bidAmount = rs.getDouble("bidAmount");			
			final Boolean bidWon = rs.getBoolean("bidWon");
			final Long bidderparticipationId = rs.getLong("bidderparticipation");
			CodeValueData codeValueData = codeValueReadPlatformService.retrieveCodeValue(bidderparticipationId);
			final String bidderparticipation =  codeValueData.getName();
			final LocalDate bidDate = JdbcSupport.getLocalDate(rs, "bidDate");
			//final int count = rs.getInt("count");
			final Boolean isPaid = rs.getBoolean("isPaid");
			return ChitBidsData.instanceForCount(id, chitSubscriberId, chitCycleId, bidAmount, bidWon ,bidderparticipation,bidderparticipationId,bidDate,isPaid);
		}
	}


	@Override
	public ChitBidsData retrieveChitBids(Long id) {

		try {
			final ChitBidsMapper rm = new ChitBidsMapper();
			final String sql = "select " + rm.schema() + " where cb.id = ? ";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(id, e);
		}
	}
	
	@Override
	public ChitBidsData retriveChitsDataForChitGroup(Long cycleId, Long chitGroupId, Long cycleNumber, int bidWon) {
		try {
			////System.out.println("cycleId "+cycleId);
			////System.out.println("chitGroupId "+chitGroupId);
			////System.out.println("cycleNumber "+cycleNumber);
			final ChitBidCountMapper rm = new ChitBidCountMapper();
			final String sql = " select " + rm.schema() + " left join chit_group_cycle cycle on cb.chit_cycle_id = ? \r\n"
					+ "where cycle.chit_id = ? and cycle.cycle_number= ? and cb.bid_won=? ";
			
			////System.out.println(sql);
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {cycleId, chitGroupId, cycleNumber, bidWon});
			
		} catch(final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(chitGroupId,e);
		}
	}
	
	@Override
	public ChitBidsData retriveChitsDataForReBidChitGroup(Long cycleId, Long chitGroupId, Long cycleNumber, Long chitSubId) {
		try {
			////System.out.println("cycleId "+cycleId);
			////System.out.println("chitGroupId "+chitGroupId);
			////System.out.println("cycleNumber "+cycleNumber);
			final ChitBidCountMapper rm = new ChitBidCountMapper();
			final String sql = " select " + rm.schema() + " left join chit_group_cycle cycle on cb.chit_cycle_id = ? \r\n"
					+ "where cycle.chit_id = ? and cycle.cycle_number= ? and cb.chit_subscriber_id = ? ";
			
			////System.out.println(sql);
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {cycleId, chitGroupId, cycleNumber,chitSubId});
			
		} catch(final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(chitGroupId,e);
		}
	}
	

	@Override
	public Collection<ChitBidsData> retrieveAll(Long chitId, Long cycleNumber) {
		final AppUser currentUser = this.context.authenticatedUser();

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select ");
		sqlBuilder.append(new ChitBidsMapper().schema());

		final SQLBuilder extraCriteria = new SQLBuilder();
		if (chitId != null) {
			extraCriteria.addNonNullCriteria("cc.chit_id =  ", chitId);
		}
		if (cycleNumber != null) {
			extraCriteria.addNonNullCriteria("cc.cycle_number = ", cycleNumber);
		}

		sqlBuilder.append(" ").append(extraCriteria.getSQLTemplate());

		return this.jdbcTemplate.query(sqlBuilder.toString(), this.chitGroupMapper, extraCriteria.getArguments());
	}

	private static final class ChitCycleMapper implements RowMapper<ChitCycleData> {
		public String schema() {
			return " select  cc.id as id, cc.chit_id as chitId, cc.cycle_number as cycleNumber ,cc.auction_date as auctiondate,cc.start_date as startdate,cc.end_date as enddate,cc.dividend as dividend,cc.bid_minutes_filing_due_date as bidminutesfilingduedate ,"
					+ "cc.subscriptionPayble as subscriptionPayble,cc.gstAmount as gstAmount,cc.foremanCommissionAmount as foremanCommissionAmount ,"
					+ "cc.verificationAmount as verificationAmount from chit_group_cycle cc where cc.chit_id=? and cc.cycle_number=?";
		}

		@Override
		public ChitCycleData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			final Long id = rs.getLong("id");
			
			final Long chitId = rs.getLong("chitId");
			
			final Long cycleNumber = rs.getLong("cycleNumber");
			
			final ZonedDateTime auctiondate = JdbcSupport.getLocalDateTime(rs, "auctiondate");
			
			final LocalDate startdate = JdbcSupport.getLocalDate(rs, "startdate");
			
			final LocalDate enddate = JdbcSupport.getLocalDate(rs, "enddate");
		
			final LocalDate bidminutesfilingduedate = JdbcSupport.getLocalDate(rs, "bidminutesfilingduedate");
			
			final Long dividend = rs.getLong("dividend");
			
			final Double subscriptionPayble = rs.getDouble("subscriptionPayble");
			
			final Double gstAmount = rs.getDouble("gstAmount");
			
			final Double foremanCommissionAmount = rs.getDouble("foremanCommissionAmount");
			
			final Double verificationAmount = rs.getDouble("verificationAmount");
			
			return ChitCycleData.instance(id, chitId, cycleNumber ,null,startdate,enddate,dividend,bidminutesfilingduedate,subscriptionPayble,gstAmount,foremanCommissionAmount,verificationAmount);
		}
	}

	@Override
	public ChitCycleData getChitCycle(Long chitId, Long cycleNumber) {
		try {
			final ChitCycleMapper rm = new ChitCycleMapper();
			final String sql = rm.schema() ;
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { chitId, cycleNumber });
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(chitId, e);
		}


	}

	private static final class ChitWinnerMapper implements RowMapper<ChitBidsWinnerData> {
		public String schema() {
			return " select bids.id as id, cycle.chit_id as chitId, cycle.id as chitCycleId, c.id as clientId, subs.chit_number as chitNumber,  c.firstname as name, c.adhar as adhar, "
				+ " c.mobile_no as mobileno, bids.bid_amount as bidAmount,bids.bid_date as biddate  from chit_group_cycle cycle left join chit_group_bids bids on bids.chit_cycle_id = cycle.id "
				+ " left join chit_group_subscriber subs on subs.id = bids.chit_subscriber_id left join m_client c on c.id = subs.client_id "
				+ " where cycle.chit_id = ? and cycle.cycle_number= ? and bids.bid_won=1 ";
		}

		@Override
		public ChitBidsWinnerData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			final Long id = rs.getLong("id");
			final Long chitId = rs.getLong("chitId");
			final Long chitCycleId = rs.getLong("chitCycleId");
			final Long clientId = rs.getLong("clientId");
			final Integer chitNumber = rs.getInt("chitNumber");
			final String name = rs.getString("name");
			final String adhar = rs.getString("adhar");
			final String mobileno = rs.getString("mobileno");
			final BigDecimal bidAmount = rs.getBigDecimal("bidAmount");
			final LocalDate biddate = JdbcSupport.getLocalDate(rs, "biddate");
			return ChitBidsWinnerData.instance(id, chitId, chitCycleId,clientId, chitNumber, name, adhar, mobileno, bidAmount,biddate);
		}
	}	
	
	
	@Override
	public ChitBidsWinnerData getChitWinnerData(Long chitId, Long cycleNumber) {
		try {
			final ChitWinnerMapper rm = new ChitWinnerMapper();
			final String sql = rm.schema() ;
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { chitId, cycleNumber });
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupCycleWinnerNotFoundException(chitId, cycleNumber, e);
		}
	}
	
	@Override
	public JsonObject getListofWinnersForPayable(Long chitId)
	{
		try {
			JsonObject dataTobeSent = new JsonObject();
			final String sql = "SELECT cb.bid_date as bidDate,cb.id AS bidId,cgs.id AS SubscriberId,cg.name AS chitName,cgs.chit_number AS ticketNumber,cb.bid_amount AS bidAmount,\r\n"
					+ "mc.firstname AS firstname,cgc.subscriptionPayble AS SubscriptionPayable, \r\n"
					+ "cb.bid_won as bidWon, cb.is_prize_money_paid as prizeMoneyPaid,cgs.prized_subscriber as prizedSub \r\n"
					+ "FROM chit_group_bids cb\r\n"
					+ "INNER JOIN chit_group_cycle cgc ON cgc.id = cb.chit_cycle_id\r\n"
					+ "INNER JOIN chit_group_subscriber cgs ON cgs.id = cb.chit_subscriber_id\r\n"
					+ "INNER JOIN m_client mc ON mc.id = cgs.client_id\r\n"
					+ "INNER JOIN chit_group cg ON cg.id = "+chitId+"\r\n"
					+ "WHERE cgc.chit_id = "+chitId+" AND cb.is_prize_money_paid = false and cgs.chit_number!=1";
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
			ArrayList<JsonObject> arr = new ArrayList<JsonObject>(); 

			while (rs.next()) {	
				JsonObject json = new JsonObject();
				json.addProperty("chitName", rs.getString("chitName"));
				json.addProperty("firstname", rs.getString("firstname"));
				json.addProperty("bidAmount", rs.getBigDecimal("bidAmount"));
				json.addProperty("SubscriptionPayable", rs.getBigDecimal("SubscriptionPayable"));
				json.addProperty("ticketNumber", rs.getInt("ticketNumber"));
				json.addProperty("SubscriberId", rs.getInt("SubscriberId"));
				json.addProperty("bidId", rs.getInt("bidId"));
				json.addProperty("bidDate",rs.getString("bidDate"));
				json.addProperty("bidWon", rs.getBoolean("bidWon"));
				json.addProperty("prizeMoneyPaid", rs.getBoolean("prizeMoneyPaid"));
				json.addProperty("prizedSub", rs.getBoolean("prizedSub"));
				arr.add(json);
			}
			JsonElement parse = fromJsonHelper.parse(arr.toString());
			dataTobeSent.add("ListOfSubscribersTobePaid", parse);	
			return dataTobeSent;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	
	@Override
	public JsonObject getReBidListofWinnersForPayable(Long chitId)
	{
		try {
			JsonObject dataTobeSent = new JsonObject();
			final String sql = "SELECT cs.id as subscriberId, cs.chit_id as groupId, cg.name AS chitName,cg.chit_value AS chitValue,\r\n"
					+ "cg.branch_id AS groupBranch, cs.chit_number as ticketNumber,\r\n"
					+ "cs.client_id as clientId, c.display_name AS clientName,\r\n"
					+ "cs.prized_subscriber as prizedsubscriber,\r\n"
					+ "cs.prized_cycle as prizedcycle,\r\n"
					+ "cs.is_active as isActive,\r\n"
					+ "cs.bid_advance as bidAdvance, cs.toBePaidAmount as toBePaidAmount, \r\n"
					+ "cs.is_processed as isProcessed, cs.sub_status_type as subscriberStatus, cs.status_id as statusId,\r\n"
					+ "cgb.id as bidId, cgb.bid_amount as bidAmount, cgb.bid_date as bidDate, \r\n"
					+ "cgb.bid_won as bidWon, \r\n"
					+ "cgb.is_prize_money_paid as prizeMoneyPaid,\r\n"
					+ "cv.id AS bidderParticipationId, cv.code_value AS bidderParticipation\r\n"
					+ "FROM chit_group_subscriber cs\r\n"
					+ "LEFT JOIN chit_group_bids cgb on cs.id = cgb.chit_subscriber_id\r\n"
					+ "JOIN m_client c ON c.id = cs.client_id\r\n"
					+ "join chit_group cg on cg.id = cs.chit_id \r\n"
					+ "LEFT JOIN m_code_value cv ON cgb.bidder_participation = cv.id\r\n"
					+ "WHERE cg.id = "+chitId+ "\r\n"
					+ "GROUP BY subscriberId";
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
			ArrayList<JsonObject> arr = new ArrayList<JsonObject>(); 

			while (rs.next()) {	
				
				JsonObject json = new JsonObject();
				json.addProperty("subscriberId", rs.getInt("subscriberId"));
				json.addProperty("groupId", rs.getInt("groupId"));
				json.addProperty("chitName", rs.getString("chitName"));
				json.addProperty("chitValue", rs.getInt("chitValue"));
				json.addProperty("groupBranch", rs.getInt("groupBranch"));
				json.addProperty("ticketNumber", rs.getInt("ticketNumber"));
				json.addProperty("clientId", rs.getInt("clientId"));
				json.addProperty("clientName", rs.getString("clientName"));
				json.addProperty("prizedsubscriber", rs.getBoolean("prizedsubscriber"));
				json.addProperty("prizedcycle",rs.getLong("prizedcycle"));
				json.addProperty("isActive", rs.getBoolean("isActive"));
				json.addProperty("bidAdvance", rs.getBoolean("bidAdvance"));
				json.addProperty("toBePaidAmount", rs.getDouble("toBePaidAmount"));
				json.addProperty("isProcessed", rs.getBoolean("isProcessed"));
				json.addProperty("bidId", rs.getInt("bidId"));
				json.addProperty("bidAmount", rs.getInt("bidAmount"));
				json.addProperty("bidDate",rs.getString("bidDate"));
				json.addProperty("bidWon", rs.getBoolean("bidWon"));
				json.addProperty("prizeMoneyPaid",rs.getBoolean("prizeMoneyPaid"));
				json.addProperty("bidderParticipationId",rs.getLong("bidderParticipationId"));
				json.addProperty("bidderParticipation",rs.getString("bidderParticipation"));
				json.addProperty("subscriberStatus",rs.getInt("subscriberStatus"));
				json.addProperty("statusId",rs.getLong("statusId"));
				
				arr.add(json);
			}
			JsonElement parse = fromJsonHelper.parse(arr.toString());
			dataTobeSent.add("ListOfAllSubscribersWithBids", parse);	
			return dataTobeSent;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	@Override
	public Long retriveCountChitsDataForChitGroup(Long cycleId, Long chitGroupId, Long cycleNumber, int bidWon) {
		try {
			final String sql = " select count(*) as Count  \r\n "
								+ " from chit_group_bids cb \r\n"
								+ " join chit_group_cycle cc on cc.id = cb.chit_cycle_id \r\n"
								+ " left join chit_group_cycle cycle on cb.chit_cycle_id = ? \r\n"
								+ " where cycle.chit_id = ? and cycle.cycle_number= ? and cb.bid_won=? ";

			final SqlRowSet rs =  this.jdbcTemplate.queryForRowSet(sql, new Object[] {cycleId, chitGroupId, cycleNumber, bidWon});
			Long count = 0l;
			while (rs.next()) 
			{	
				count = rs.getLong("Count");
			}	
		return count;
		} catch(final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(chitGroupId,e);
		}
	}

}
