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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.infrastructure.security.utils.SQLBuilder;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleForMobile;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;
import org.apache.fineract.portfolio.client.exception.ClientNotFoundException;
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
@SuppressWarnings("unused")
public class ChitGroupReadPlatformServiceImpl implements ChitGroupReadPlatformService {
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ChitGroupMapper chitGroupMapper = new ChitGroupMapper();
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitGroupData> paginationHelper = new PaginationHelper<>();
	private final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService;
	private final FromJsonHelper fromJsonHelper;
	@Autowired
	public ChitGroupReadPlatformServiceImpl(final PlatformSecurityContext context,
			final ColumnValidator columnValidator, final RoutingDataSource dataSource, 
			final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService,
			final FromJsonHelper fromJsonHelper) {
		this.columnValidator = columnValidator;
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.chitDemandScheduleReadPlatformService = chitDemandScheduleReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
	}

	private static final class ChitGroupMapper implements RowMapper<ChitGroupData> {
		public String schema() {
			return " c.id as id,c.name as name,c.branch_id as officeId,  o.name as officeName,c.start_date as startdate,c.staff_id as staffid,c.chit_cycle_frequency as chitcyclefrequency,c.chit_collection_frequency as chitcollectionfrequency, c.chit_duration as chitduration ,"
					+ "c.chit_value as chitvalue,c.monthly_contribution as monthlycontribution,c.auction_day as auctionday,c.auction_day_Type as auctiondayType,c.auction_week_Value as auctionweekValue,"
					+ "c.auction_time as auctiontime,c.current_cycle as currentcycle,c.next_auction_date as nextauctiondate,c.status as status,c.commission_earned as commissionEarned , c.chit_aum as chitaum , c.amount_disbursed as amountDisbursed,"
					+ "c.amount_not_disbursed as amountNotDisbursed ,c.enrollment_fees as enrollmentFees,c.auction_day_Value as auctiondayValue,c.end_date as endDate, c.min_bid_perct as minBidPerct, c.max_bid_perct as maxBidPerct, "
					+ "c.priz_mem_pen_perct as prizMemPenPerct, c.non_priz_mem_pen_perct as nonPrizMemPenPerct, c.fdr_ac_number as fdrAcNumber, c.fdr_issue_date as fdrIssueDate, c.fdr_matu_date as fdrMatuDate, "
					+ "c.fdr_dep_amount as fdrDepAmount, c.fdr_duration as fdrDuration, c.fdr_rat_int_perct as fdrRatIntPerct, c.fdr_rate_int_amt as fdrRateIntAmt, c.fdr_int_pay_cycle as fdrIntPayCycle, c.fdr_bankname as fdrBankName, c.fdr_bankbranch_name as fdrBankBranchName,c.fdr_matu_amount as fdrMatuAmount, "
					+ "c.pso_appl_date as psoAppDate, c.pso_issue_date as psoIssueDate, c.pso_number as psoNumber, c.cc_appl_date as ccAppDate, c.cc_issue_date as ccIssueDate, c.cc_number as ccNumber from chit_group c "
					+ " join m_office o on o.id = c.branch_id";
		}
		@Override
		public ChitGroupData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			final Long id = rs.getLong("id");
			final String name = rs.getString("name");
			final Long officeId = rs.getLong("officeId");
			final String officeName = rs.getString("officeName");
			final Long staffid = rs.getLong("staffid");
			final LocalDate startdate = JdbcSupport.getLocalDate(rs, "startdate");
			final String chitcyclefrequency = rs.getString("chitcyclefrequency");
			final String chitcollectionfrequency = rs.getString("chitcollectionfrequency");
			final Long chitduration = rs.getLong("chitduration");
			final Long auctiondayValue = rs.getLong("auctiondayValue");
			final Long chitvalue = rs.getLong("chitvalue");
			final Long monthlycontribution = rs.getLong("monthlycontribution");
			final String auctionday = rs.getString("auctionday");
			final String auctiondayType = rs.getString("auctiondayType");
			final String auctionweekValue = rs.getString("auctionweekValue");
			final LocalTime auctiontime = rs.getTime("auctiontime") != null ? rs.getTime("auctiontime").toLocalTime()
					: null;
			final Long currentcycle = rs.getLong("currentcycle");
			final LocalDate nextauctiondate = rs.getDate("nextauctiondate") != null ? rs.getDate("nextauctiondate").toLocalDate() : null;
			final Long status = rs.getLong("status");
			final Long commissionEarned = rs.getLong("commissionEarned");
			final Long chitaum = rs.getLong("chitaum");
			final Long amountDisbursed = rs.getLong("amountDisbursed");
			final Long amountNotDisbursed = rs.getLong("amountNotDisbursed");
			final Long enrollmentFees = rs.getLong("enrollmentFees");

			final BigDecimal minBidPerct = rs.getBigDecimal("minBidPerct");
			final BigDecimal maxBidPerct = rs.getBigDecimal("maxBidPerct");
			final BigDecimal prizMemPenPerct = rs.getBigDecimal("prizMemPenPerct");
			final BigDecimal nonPrizMemPenPerct = rs.getBigDecimal("nonPrizMemPenPerct");
			final String fdrAcNumber = rs.getString("fdrAcNumber");
			final LocalDate fdrIssueDate = rs.getDate("fdrIssueDate") != null ? rs.getDate("fdrIssueDate").toLocalDate()
					: null;
			final LocalDate fdrMatuDate = rs.getDate("fdrMatuDate") != null ? rs.getDate("fdrMatuDate").toLocalDate()
					: null;
			final Long fdrDepAmount = rs.getLong("fdrDepAmount");
			final Integer fdrDuration = rs.getInt("fdrDuration");
			final BigDecimal fdrRatIntPerct = rs.getBigDecimal("fdrRatIntPerct");
			final Long fdrRateIntAmt = rs.getLong("fdrRateIntAmt");
			final String fdrIntPayCycle = rs.getString("fdrIntPayCycle");
			final String fdrBankName = rs.getString("fdrBankName");
			final String fdrBankBranchName = rs.getString("fdrBankBranchName");
			final Long fdrMatuAmount = rs.getLong("fdrMatuAmount");
			final LocalDate psoAppDate = rs.getDate("psoAppDate") != null ? rs.getDate("psoAppDate").toLocalDate()
					: null;
			final LocalDate psoIssueDate = rs.getDate("psoIssueDate") != null ? rs.getDate("psoIssueDate").toLocalDate()
					: null;
			final String psoNumber = rs.getString("psoNumber");
			final LocalDate ccAppDate = rs.getDate("ccAppDate") != null ? rs.getDate("ccAppDate").toLocalDate() : null;
			final LocalDate ccIssueDate = rs.getDate("ccIssueDate") != null ? rs.getDate("ccIssueDate").toLocalDate()
					: null;
			final String ccNumber = rs.getString("ccNumber");
			final LocalDate enddate = JdbcSupport.getLocalDate(rs, "endDate");

			return ChitGroupData.instance(id, name, officeId, officeName, staffid, startdate, chitcyclefrequency,chitcollectionfrequency,
					chitduration, chitvalue, monthlycontribution, auctiondayValue, auctionday, auctiondayType,
					auctionweekValue, auctiontime, currentcycle, nextauctiondate, status, commissionEarned, chitaum, amountDisbursed,
					amountNotDisbursed, enrollmentFees, minBidPerct, maxBidPerct, prizMemPenPerct, nonPrizMemPenPerct,
					fdrAcNumber, fdrIssueDate, fdrMatuDate, fdrDepAmount, fdrDuration, fdrRatIntPerct, fdrRateIntAmt,
					fdrIntPayCycle, fdrBankName, fdrBankBranchName, fdrMatuAmount, psoAppDate, psoIssueDate, psoNumber,
					ccAppDate, ccIssueDate, ccNumber, null, null,enddate);
		}
	}

	private static final class ChitGroupLookupMapper implements RowMapper<ChitGroupData> {

		private final String schemaSql;

		ChitGroupLookupMapper() {

			final StringBuilder sqlBuilder = new StringBuilder(100);
			sqlBuilder.append("s.id as id, s.name as name ");
			sqlBuilder.append("from m_staff s ");
			sqlBuilder.append("join m_office o on o.id = s.branch_id ");

			this.schemaSql = sqlBuilder.toString();
		}

		public String schema() {
			return this.schemaSql;
		}

		@Override
		public ChitGroupData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String name = rs.getString("name");
			return ChitGroupData.lookup(id, name);
		}
	}

	@Override
	public ChitGroupData retrieveChitGroup(Long id) {
		final String hierarchy = this.context.authenticatedUser().getOffice().getHierarchy() + "%";

		try {
			final ChitGroupMapper rm = new ChitGroupMapper();
			final String sql = "select " + rm.schema() + " where c.id = ? and o.hierarchy like ? ";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id, hierarchy });
		} catch (final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(id, e);
		}
	}

	@Override
	public Collection<ChitGroupData> retrieveAll(SearchParameters searchParameters,
			final PaginationParameters parameters) {
		final AppUser currentUser = this.context.authenticatedUser();
		final String hierarchy = currentUser.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select ");
		sqlBuilder.append(new ChitGroupMapper().schema());

		final SQLBuilder extraCriteria = new SQLBuilder();
		final String name = searchParameters.getName();
		if (name != null) {
			extraCriteria.addNonNullCriteria("c.name like", "%" + name + "%");
		}
		final String status = searchParameters.getStatus();
		if (status != null) {
			extraCriteria.addNonNullCriteria("c.status like", "%" + status + "%");
		}
		extraCriteria.addCriteria(" o.hierarchy like ", hierarchySearchString);

		sqlBuilder.append(" ").append(extraCriteria.getSQLTemplate());

		if (parameters != null) {
			if (parameters.isOrderByRequested()) {
				sqlBuilder.append(parameters.orderBySql());
				this.columnValidator.validateSqlInjection(sqlBuilder.toString(), parameters.orderBySql());
			}

			if (parameters.isLimited()) {
				sqlBuilder.append(parameters.limitSql());
				this.columnValidator.validateSqlInjection(sqlBuilder.toString(), parameters.limitSql());
			}
		}
		return this.jdbcTemplate.query(sqlBuilder.toString(), this.chitGroupMapper, extraCriteria.getArguments());
	}
	
	@Override
	public Collection<ChitGroupData> retrieveAllActiveChitGroups() {
		try {
			final ChitGroupMapper rm = new ChitGroupMapper();
			final String sql = "select " + rm.schema() + " where c.status = 20 ";

			return this.jdbcTemplate.query(sql, rm);
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

    @Override
    public Page<ChitGroupData> retrievePagedAll(final SearchParameters searchParameters, final PaginationParameters parameters) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(new ChitGroupMapper().schema());

		final SQLBuilder extraCriteria = new SQLBuilder();
		final String name = searchParameters.getName();
		if (name != null) {
			extraCriteria.addNonNullCriteria("c.name like", "%" + name + "%");
		}
		final String status = searchParameters.getStatus();
		if (status != null) {
			extraCriteria.addNonNullCriteria("c.status like", "%" + status + "%");
		}
        extraCriteria.addCriteria(" o.hierarchy like ", hierarchySearchString);
        sqlBuilder.append(" ").append(extraCriteria.getSQLTemplate());

        if (parameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy()).append(' ').append(searchParameters.getSortOrder());
            this.columnValidator.validateSqlInjection(sqlBuilder.toString(), searchParameters.getOrderBy(),
                    searchParameters.getSortOrder());
        }

        if (parameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), extraCriteria.getArguments(),
                this.chitGroupMapper);
    }
	

	private static final class ChitGroupSubscriberMapper implements RowMapper<ChitGroupSubscriberData> {
		public String schema() {
			return " cs.id as id, cs.chit_id as chitid, cs.client_id as clientid, cs.chit_number as chitnumber, c.display_name as name, c.adhar as adhar, "
					+ "c.mobile_no as mobileno ,cs.prized_subscriber as prizedsubscriber,cs.prized_cycle as prizedcycle ,"
					+ " cs.is_active as isActive, cs.bid_advance as bidAdvance, cs.toBePaidAmount as toBePaidAmount, cs.is_processed as isProcessed "
				+ " from chit_group_subscriber cs join chit_group cg on cg.id = cs.chit_id join m_client c on c.id=cs.client_id ";
		}

		@Override
		public ChitGroupSubscriberData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			final Long id = rs.getLong("id");
			final Long chitid = rs.getLong("chitid");
			final Long clientid = rs.getLong("clientid");
			final Integer chitnumber = rs.getInt("chitnumber");
			final String name = rs.getString("name");
			final String adhar = rs.getString("adhar");
			final String mobileno = rs.getString("mobileno");
			final Long prizedcycle = rs.getLong("prizedcycle");
			final Boolean prizedsubscriber = rs.getBoolean("prizedsubscriber");
			final Boolean isActive = rs.getBoolean("isActive");
			final Boolean bidAdvance = rs.getBoolean("bidAdvance");
			final Double toBePaidAmount = rs.getDouble("toBePaidAmount");
			final Boolean isProcessed = rs.getBoolean("isProcessed");

			return ChitGroupSubscriberData.newinstance(id, chitid,clientid,chitnumber, name, adhar, mobileno,prizedsubscriber,prizedcycle,isActive,bidAdvance,toBePaidAmount,isProcessed);
		}
	}
	
//	private static final class ChitGroupSubscriberClientMapper implements RowMapper<ChitGroupSubscriberData>{
//		public String schema() {
//			 return " cs.id as id, cs.chit_id as chitid, cs.client_id as clientid, cs.chit_number as chitnumber, c.display_name as name, c.adhar as adhar, c.mobile_no as mobileno ,cs.prized_subscriber as prizedsubscriber,cs.prized_cycle as prizedcycle , cs.is_active as isActive"
//				+ " from chit_group_subscriber cs join chit_group cg on cg.id = cs.chit_id join m_client c on c.id=cs.client_id ";
//		}
//
//		@Override
//		public ChitGroupSubscriberData mapRow(ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
//			// TODO Auto-generated method stub
//			
//			final Long id= rs.getLong
//			return null;
//		}
//		
//		
//	}

	// Gets by Chit Group
	@Override
	public Collection<ChitGroupSubscriberData> getChitSubscribers(Long id) {

		try {
			final ChitGroupSubscriberMapper rm = new ChitGroupSubscriberMapper();
			final String sql = "select " + rm.schema() + " where cs.chit_id = ? and cs.is_active = true order by cs.chit_number";

			return this.jdbcTemplate.query(sql, new Object[] { id }, rm);
		} catch (final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(id, e);
		}
	}
	
	
	@Override
	public Collection<ChitGroupSubscriberData> getChitSubscribersUsingClientId(Long id) {

		try {
			final ChitGroupSubscriberMapper rm = new ChitGroupSubscriberMapper();
			final String sql = "select " + rm.schema() + " where cs.client_id = ? order by cs.client_id";

			return this.jdbcTemplate.query(sql, new Object[] { id }, rm);
		} catch (final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(id, e);
		}
	}
	
	// Get one Subscriber record by Primary key id
	@Override
	public ChitGroupSubscriberData getChitSubscriber(Long id) {
		try {
			final ChitGroupSubscriberMapper rm = new ChitGroupSubscriberMapper();
			final String sql = "select " + rm.schema() + " where cs.id = ? ";

			return this.jdbcTemplate.queryForObject(sql, new Object[] { id }, rm);
		} catch (final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(id, e);
		}
	}
	
	//Get Subscriber records by chits Id and client id chits id is always 1
	@Override
	public ChitGroupSubscriberData getChitSubscriberWithClient(Long id, Long cyclenumber) {
		try {
			final ChitGroupSubscriberMapper rm = new ChitGroupSubscriberMapper();
			final String sql = " select " + rm.schema() + " where cs.chit_id = ? and cs.chit_number = ?";
			
			return this.jdbcTemplate.queryForObject(sql,rm, new Object[] {id, cyclenumber});
		} catch (final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(id, e);
		}
	}
	
	public Collection<ChitGroupSubscriberData> getNonActiveChitSubscriber(Long id) {
		try {
			final ChitGroupSubscriberMapper rm = new ChitGroupSubscriberMapper();
			final String sql = " select " + rm.schema() + " where cs.chit_id = ? and cs.is_active = ?";
			
			return this.jdbcTemplate.query(sql,rm, new Object[] {id, true});
		} catch (final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(id, e);
		}
	}
	
	public ChitGroupSubscriberData getIndividualNonActiveChitSubscriber(Long id,Long chitId,Long chitNum) {
		try {
			final ChitGroupSubscriberMapper rm = new ChitGroupSubscriberMapper();
			final String sql = " select " + rm.schema() + " where cs.client_id = ? and cs.chit_id = ? and cs.chit_number = ? and cs.is_active = ?";
			return this.jdbcTemplate.queryForObject(sql,rm, new Object[] {id,chitId,chitNum,true});
		} catch (final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(id, e);
		}
	}
	
	
	@Override
	public ChitGroupSubscriberData getChitSubscriberUsingChitIDClientId(Long chitId,Long clientId,Long chitNumber) {
		try {
			final ChitGroupSubscriberMapper rm = new ChitGroupSubscriberMapper();
			final String sql = " select " + rm.schema() + " where cs.chit_id = ? and cs.client_id = ? and cs.chit_number = ?";
			
			return this.jdbcTemplate.queryForObject(sql,rm, new Object[] {chitId, clientId,chitNumber});
		} catch (final EmptyResultDataAccessException e) {
			throw new ChitGroupNotFoundException(chitId, e);
		}
	}
	
	
	@Override
	public JsonObject getSubscriberListoFClosedGroups(Long chitId)
	{
		List<JsonObject> re = new ArrayList<JsonObject>();
		
		ChitGroupData chit = this.retrieveChitGroup(chitId);
		if(chit.getStatus().compareTo(30l)==0)
		{
			Iterator<ChitGroupSubscriberData> subscriberList = this.getNonActiveChitSubscriber(chitId).iterator();
			while(subscriberList.hasNext())
			{
				JsonObject data = new JsonObject();
				ChitGroupSubscriberData Subscriber = subscriberList.next();
				
				Long clientId = Subscriber.getClientId();
				
				LocalDate endDate = chit.getEndDate();
				
				Iterator<ChitDemandScheduleForMobile> demanddetails = this.chitDemandScheduleReadPlatformService.retriveData(clientId, endDate).iterator();
				
				while(demanddetails.hasNext())
				{
					ChitDemandScheduleForMobile demand = demanddetails.next();
					Long demandId = demand.getChitDemandId();
					Double installment = demand.getInstallment();
					Double due = demand.getDueAmount();
					Double overDue = demand.getOverDueAmount();
					Double penalty = demand.getPenaltyAmount();
					Double totalAmount = installment+due+overDue+penalty;
					Double collectedAmount = this.chitDemandScheduleReadPlatformService.retrieveById(demandId).getCollectedAmount();
					
					if(totalAmount.compareTo(collectedAmount)!=0)
					{
						
						Double payable = totalAmount - collectedAmount;
						data.addProperty("ClientId", demand.getClientId());
						data.addProperty("ClientName", demand.getFirstName());
						data.addProperty("ChitId", demand.getChitGroupId());
						data.addProperty("ChitName",demand.getChitGroupName());
						data.addProperty("ChitNumber", demand.getChitNumber());
						data.addProperty("DemandId", demandId);
						data.addProperty("DemandDate", endDate.toString());
						data.addProperty("PayableAmount", payable);
						re.add(data);
					}
				}
			}
		}
		
		JsonObject resp = new JsonObject();
		JsonElement arr = this.fromJsonHelper.parse(re.toString());
		resp.add("Result",arr);
		
		return resp;
		
	}
	
	
	@Override
	public JsonObject getSingleSubscriberClosedGroups(Long clientId,Long chitId,Long chitNum)
	{
		List<JsonObject> re = new ArrayList<JsonObject>();
	
		ChitGroupData chit = this.retrieveChitGroup(chitId);
		if(chit.getStatus().compareTo(30l)==0)
		{
			ChitGroupSubscriberData subscriberList = this.getIndividualNonActiveChitSubscriber(clientId,chitId,chitNum);
			if(subscriberList!=null)
			{
				
				LocalDate endDate = chit.getEndDate();
				
				Iterator<ChitDemandScheduleForMobile> demanddetails = this.chitDemandScheduleReadPlatformService.retriveData(clientId, endDate).iterator();
				
				while(demanddetails.hasNext())
				{
					JsonObject data = new JsonObject();
					ChitDemandScheduleForMobile demand = demanddetails.next();
					Long demandId = demand.getChitDemandId();
					Double installment = demand.getInstallment();
					Double due = demand.getDueAmount();
					Double overDue = demand.getOverDueAmount();
					Double penalty = demand.getPenaltyAmount();
					Double totalAmount = installment+due+overDue+penalty;
					Double collectedAmount = this.chitDemandScheduleReadPlatformService.retrieveById(demandId).getCollectedAmount();
					
					if(totalAmount.compareTo(collectedAmount)!=0)
					{
						
						Double payable = totalAmount - collectedAmount;
						data.addProperty("ClientId", demand.getClientId());
						data.addProperty("ClientName", demand.getFirstName());
						data.addProperty("ChitId", demand.getChitGroupId());
						data.addProperty("ChitName",demand.getChitGroupName());
						data.addProperty("ChitNumber", demand.getChitNumber());
						data.addProperty("DemandId", demandId);
						data.addProperty("DemandDate", endDate.toString());
						data.addProperty("PayableAmount", payable);
						re.add(data);
					}
				}
			}
		}
		
		JsonObject resp = new JsonObject();
		JsonElement arr = this.fromJsonHelper.parse(re.toString());
		resp.add("Result",arr);
		
		return resp;
		
	}
	
	
	@Override
	public ArrayList<JsonElement> retriveDashBoardData(Long id) {
		
		try {
			final String sql = "\r\n"
					+ "SELECT cg.`status` AS 'CurrentStatus',cg.chit_value AS chitValue, mc.firstname AS 'WinnerName', cg.start_date AS 'StartDate',\r\n"
					+ "cgc.cycle_number AS 'CurrentCycle',cgs.chit_number AS 'TicketNumber',\r\n"
					+ "cg.next_auction_date AS 'NextBidDate' ,\r\n"
					+ "cgb.bid_amount AS 'BidAmount', cgb.bid_date AS 'BidDate'\r\n"
					+ "FROM chit_group cg \r\n"
					+ "INNER JOIN chit_group_cycle cgc ON cgc.chit_id = cg.id\r\n"
					+ "INNER JOIN chit_group_bids cgb ON cgb.chit_cycle_id = cgc.id \r\n"
					+ "INNER JOIN chit_group_subscriber cgs ON cgs.id = cgb.chit_subscriber_id\r\n"
					+ "INNER JOIN m_client mc ON mc.id = cgs.client_id\r\n"
					+ "WHERE cgb.bid_won='1' and cg.id = ? ";

			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] { id });
			ArrayList<JsonElement> arr = new ArrayList<JsonElement>(); 

			while (rs.next()) {	
				JsonObject json = new JsonObject();
				json.addProperty("status", rs.getLong("CurrentStatus"));
				json.addProperty("chitNumber", rs.getLong("TicketNumber"));
				json.addProperty("chitValue", rs.getLong("chitValue"));
				json.addProperty("winnerName", rs.getString("WinnerName"));
				json.addProperty("startDate", rs.getDate("StartDate").toString());
				json.addProperty("currentCycle", rs.getLong("CurrentCycle"));
				json.addProperty("bidDate",rs.getString("BidDate").toString());
				json.addProperty("nextBidDate",rs.getString("NextBidDate").toString());
				json.addProperty("bidAmount", rs.getLong("BidAmount"));
				arr.add(json);
			}
			
			return arr;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
		
	@Override
	public ArrayList<JsonElement> retriveChitGroupCycleData(Long chitId, int cycleNumber)
	{
		Collection<ChitGroupSubscriberData> collectionData = this.getChitSubscribers(chitId);
		Iterator<ChitGroupSubscriberData> itrData = collectionData.iterator();
		ChitGroupData grpData = this.retrieveChitGroup(chitId);
		ArrayList<JsonElement> listarray = new ArrayList<JsonElement>();
		Long chitDur = grpData.getChitduration();
		
		while(itrData.hasNext())
		{
			ChitGroupSubscriberData Subscribers = itrData.next();
			Long clientId = Subscribers.getClientId();
			Integer chitNumber = Subscribers.getChitNumber();
			JsonObject jsonObject = getChitsData(chitId, cycleNumber,clientId,chitNumber);
			Long div = jsonObject.get("Dividend").getAsLong();
			Double dividend = (div.doubleValue()/chitDur.doubleValue());
			Long ticketNumber = jsonObject.get("TicketNumber").getAsLong();
			String subscriberName = jsonObject.get("SubscriberName").getAsString();
			BigDecimal totalAmountCollected = jsonObject.get("TotalAmountCollected").getAsBigDecimal();
			BigDecimal totalDemand = jsonObject.get("TotalDemand").getAsBigDecimal();
			BigDecimal totalAmountDue = jsonObject.get("TotalAmountDue").getAsBigDecimal();
			
			jsonObject.addProperty("TotalDividendEarned", dividend);
			jsonObject.addProperty("TicketNumber", ticketNumber);
			jsonObject.addProperty("SubscriberName", subscriberName);
			jsonObject.addProperty("TotalAmountCollected", totalAmountCollected);
			jsonObject.addProperty("TotalDemand",totalDemand);
			jsonObject.addProperty("TotalAmountDue", totalAmountDue);
			
			listarray.add(jsonObject);
		}
		return listarray;
		
	}
	
	JsonObject getChitsData(Long chitId, int cycleNumber, Long clientId, Integer chitNumber)
	{
		try
		{
			final String sql = "SELECT cgc.dividend AS 'Dividend',\r\n"
					+ "	mc.display_name AS 'SubscriberName',\r\n"
					+ "	cgs.chit_number AS 'TicketNumber',\r\n"
					+ "	SUM(installment_amount) AS 'TotalDemand',\r\n"
					+ "	SUM(collected_amount) AS 'TotalAmountCollected',\r\n"
					+ "	cd.due_amount AS 'TotalAmountDue'\r\n"
					+ "	FROM chit_demand_schedule cd\r\n"
					+ "INNER JOIN chit_subscriber_charge csg ON csg.id = cd.chit_subscriber_charge_id\r\n"
					+ "INNER JOIN chit_group_subscriber cgs ON cgs.id = csg.chit_subscriber_id\r\n"
					+ "INNER JOIN chit_group_cycle cgc ON cgc.id = csg.chit_cycle_id\r\n"
					+ "INNER JOIN m_client mc ON mc.id =  cgs.client_id\r\n"
					+ "WHERE  cgc.chit_id = ? AND  cgc.cycle_number <= ? AND mc.id = ? AND cgs.chit_number = ? ";
			
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] { chitId, cycleNumber, clientId, chitNumber });
			JsonObject json = new JsonObject();

			while (rs.next()) {	
				json.addProperty("TicketNumber", rs.getLong("TicketNumber"));
				json.addProperty("SubscriberName", rs.getString("SubscriberName"));
				json.addProperty("TotalAmountCollected", rs.getBigDecimal("TotalAmountCollected"));
				json.addProperty("TotalDemand", rs.getBigDecimal("TotalDemand"));
				json.addProperty("TotalAmountDue", rs.getBigDecimal("TotalAmountDue"));
				json.addProperty("Dividend", rs.getLong("Dividend"));
			}	
			return json;	
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	@Override
	public Long getSubsciberTicketCounts(Long clientId) {
		try
		{
			Long count = 0l;
			String sql = "SELECT COUNT(*) as Count \r\n"
					+ "FROM chit_group_subscriber \r\n"
					+ "WHERE client_id = ? and is_active = 1 ";
			final SqlRowSet rs =  this.jdbcTemplate.queryForRowSet(sql, new Object[] {clientId});
			while (rs.next()) 
			{	
				count = rs.getLong("Count");
			}	
			return count;
		}catch(final EmptyResultDataAccessException e) {
		throw new ClientNotFoundException(clientId,e);
		}
	}
}
