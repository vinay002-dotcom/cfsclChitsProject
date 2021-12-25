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
import java.time.LocalDate;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.portfolio.ChitGroup.data.ChitAdvanceTransactionData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandDataForBalance;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleForMobile;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

@Service
public class ChitDemandScheduleReadPlatformServiceImpl implements ChitDemandScheduleReadPlatformService
{
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitDemandScheduleData> paginationHelper = new PaginationHelper<>();
	private final ChitDemandScheduleMapper mapper = new ChitDemandScheduleMapper();
	
	
	@Autowired
	public ChitDemandScheduleReadPlatformServiceImpl(PlatformSecurityContext context,
			ColumnValidator columnValidator,final RoutingDataSource dataSource) {
	
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.columnValidator = columnValidator;
	}
	
	private static final class ChitDemandScheduleMapper implements RowMapper<ChitDemandScheduleData>
	{
		public String schema() 
		{
			return "cd.id as id,cd.chit_id as chitid, cd.chit_subscriber_charge_id as chitsubscriberchargeid, cd.staff_id as staffid , cd.demand_date as demanddate , cd.installment_amount as installment,cd.due_amount as dueAmount,cd.overdue_amount as overdueAmount,cd.penalty_amount as penaltyAmount,cd.collected_amount as collectedamount,cd.is_calculated as isCalculated from chit_demand_schedule cd";
		}
		
		@Override
		public ChitDemandScheduleData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Long id = rs.getLong("id");
			Long chitsubscriberid = rs.getLong("chitsubscriberchargeid");
			Long staffid = rs.getLong("staffid");
			LocalDate demanddate = JdbcSupport.getLocalDate(rs, "demanddate");
			Double installmentamount = rs.getDouble("installment");
			Double dueamount = rs.getDouble("dueAmount");
			Double overdueamount = rs.getDouble("overdueAmount");
			Double penaltyamount = rs.getDouble("penaltyAmount");
			Double collectedamount = rs.getDouble("collectedamount");
			Boolean isCalculated = rs.getBoolean("isCalculated");
			Long chitId = rs.getLong("chitid");
			return ChitDemandScheduleData.instance(id, chitsubscriberid, staffid, demanddate, installmentamount, dueamount, overdueamount, penaltyamount, collectedamount,isCalculated,chitId);
		}
		
	}
	
	private static final class ChitDemandScheduleForMobileMapper implements RowMapper<ChitDemandScheduleForMobile>
	{
		public String schema() 
		{
			return " cs.id as chitSubscriberChargeID,cd.id AS chitDemandId,cd.installment_amount AS installment,cd.due_amount AS dueAmount,cd.overdue_amount AS overdueAmount,cd.penalty_amount AS penaltyAmount,c.mobile_no AS mobileno,c.firstname AS firstname,c.adhar AS adhar,c.id AS clientId,cgs.id AS chitGroupSubscriberId,cgs.chit_number AS chitNumber,cg.name AS chitGroupName,cg.id AS chitGroupId"
					+ " FROM chit_demand_schedule cd"
					+ " JOIN chit_subscriber_charge cs ON cs.id=cd.chit_subscriber_charge_id"
					+ " JOIN chit_group_subscriber cgs ON cgs.id = cs.chit_subscriber_id"
					+ " JOIN m_client c ON c.id = cgs.client_id"
					+ " JOIN chit_group cg ON cg.id  = cgs.chit_id";
		}
		
		@Override
		public ChitDemandScheduleForMobile mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			String firstName =  rs.getString("firstname");
			String mobileNo = rs.getString("mobileno");
			String adhar = rs.getString("adhar");
			String chitName = rs.getString("chitGroupName");
			Long chitNumber = rs.getLong("chitNumber"); 
			Double installmentamount = rs.getDouble("installment");
			Double dueamount = rs.getDouble("dueAmount");
			Double overdueamount = rs.getDouble("overdueAmount");
			Double penaltyamount = rs.getDouble("penaltyAmount");
			Double TotalAmount = installmentamount+dueamount+overdueamount+penaltyamount;
			if(TotalAmount<0)
			{
				TotalAmount = 0.0;
			}
			Long ChitDemandId = rs.getLong("chitDemandId");
			Long ClientId = rs.getLong("clientId");
			Long chitGroupId = rs.getLong("chitGroupId");
			Long chitGroupSubscriberId = rs.getLong("chitGroupSubscriberId");
			Long chitSubsChargeId = rs.getLong("chitSubscriberChargeID");
			return ChitDemandScheduleForMobile.instance(installmentamount, dueamount, overdueamount, penaltyamount, TotalAmount, mobileNo, firstName, adhar, chitNumber,chitName,ChitDemandId,ClientId,chitGroupId,chitGroupSubscriberId,chitSubsChargeId);
		}
		
	}

	@Override
	public Collection<ChitDemandScheduleData> retrieveAll() 
	{
		try {
			final ChitDemandScheduleMapper rm = new ChitDemandScheduleMapper();
			final String sql = "select " + rm.schema();

			return this.jdbcTemplate.query(sql, this.mapper);
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}

	@Override
	public ChitDemandScheduleData retrieveById(Long id) {
		try {
			final ChitDemandScheduleMapper rm = new ChitDemandScheduleMapper();
			final String sql = "select " + rm.schema() + " where cd.id = ? ";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(id, e);
		}
	}
	
	@Override
	public ChitDemandScheduleData retrieveByIdAndDate(Long ChitSubscriberChargeid,Long staffId,LocalDate date) {
		try {
			
			final ChitDemandScheduleMapper rm = new ChitDemandScheduleMapper();
			final String sql = "select " + rm.schema() + " where cd.chit_subscriber_charge_id = ? and cd.staff_id = ? and cd.demand_date = ? ";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { ChitSubscriberChargeid,staffId,date.toString()});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Collection<ChitDemandScheduleForMobile> retriveData(Long clientId, LocalDate date) {
		try {
			
			final ChitDemandScheduleForMobileMapper rm = new ChitDemandScheduleForMobileMapper();
			final String sql = "select " + rm.schema() + " WHERE cd.demand_date = ? AND cd.chit_subscriber_charge_id IN (SELECT cs.id FROM chit_subscriber_charge cs WHERE cs.chit_subscriber_id IN (SELECT ch.id FROM chit_group_subscriber ch WHERE ch.client_id = ?))" ;
			////System.out.println(sql);
			return this.jdbcTemplate.query(sql, rm, new Object[] { date.toString(),clientId });
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(clientId, e);
		}
	}

	@Override
	public Collection<ChitDemandScheduleData> retriveDemandSchedules(Long SubsId, Boolean iscalculated) {
		try {
				////System.out.println("inside retreive SubsId "+SubsId);
				////System.out.println("inside retreive SubsId "+iscalculated);
			final ChitDemandScheduleMapper rm = new ChitDemandScheduleMapper();
			final String sql = "select " + rm.schema() + " where cd.chit_subscriber_charge_id = ? and cd.is_calculated = ? order by cd.demand_date asc ";
			////System.out.println(sql);
			return this.jdbcTemplate.query(sql, rm, new Object[] {SubsId,iscalculated});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public ChitDemandScheduleData getunproccesedDemands(Long SubsId, Boolean iscalculated,LocalDate date) {
		try {
				////System.out.println("inside retreive SubsId "+SubsId);
				////System.out.println("inside retreive SubsId "+iscalculated);
			final ChitDemandScheduleMapper rm = new ChitDemandScheduleMapper();
			final String sql = "select " + rm.schema() + " where cd.chit_subscriber_charge_id = ? and cd.is_calculated = ? and cd.demand_date = ?" ;
			////System.out.println(sql);
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {SubsId,iscalculated,date.toString()});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public ChitAdvanceTransactionData retrieveData(Long clientId) {
		try {
			
			final ChitAdvanceTransactionMapper rm = new ChitAdvanceTransactionMapper();
			final String sql = "select " + rm.schema(clientId) ;

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class ChitAdvanceTransactionMapper implements RowMapper<ChitAdvanceTransactionData>
	{
		public String schema(Long clientId) 
		{
			return " c.id, c.firstName, c.adhar, c.mobile_no, (select sum(amount) as amount from m_client_transaction where client_id = "+clientId+" ) amount  from m_client c where c.id = "+clientId ;
		}
		
		@Override
		public ChitAdvanceTransactionData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Long id = rs.getLong("id");
			String firstName = rs.getString("firstName");
			String Adhar = rs.getString("adhar");
			String mobileNo = rs.getString("mobile_no");
			Double AdvanceAmountRecieved = rs.getDouble("amount");
			return ChitAdvanceTransactionData.instance(id,firstName,Adhar,mobileNo,AdvanceAmountRecieved);
		}
		
	}
	
	
	@Override
	public ChitDemandDataForBalance getDemandBalance(Long subsId) {
		try {
			
			final ChitDemandTransactionMapper rm = new ChitDemandTransactionMapper();
			final String sql = "select " + rm.schema(subsId) ;

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class ChitDemandTransactionMapper implements RowMapper<ChitDemandDataForBalance>
	{
		public String schema(Long subsId) 
		{
			return "sum(collected_amount) as collected_amt FROM chit_demand_schedule d WHERE d.chit_subscriber_charge_id = "+subsId ;
		}
		
		@Override
		public ChitDemandDataForBalance mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Double Amount = rs.getDouble("collected_amt");
			return ChitDemandDataForBalance.instance(Amount);
		}
		
	}
	
	private static final class chitDemandForClosureMapper implements RowMapper<ChitDemandScheduleData>
	{
		public String schema(Long chitSubscriberChargeid, Long staffId, Long chitId) {
			return " SUM(cds.collected_amount) as collectedAmount, SUM(cds.penalty_amount) AS penalty \r\n"
					+ "from chit_demand_schedule cds \r\n"
					+ "where cds.chit_subscriber_charge_id = "+chitSubscriberChargeid+" \r\n"
					+ "and cds.staff_id = "+staffId+" and cds.is_calculated = 1 and cds.chit_id = "+chitId+"";
		}
		
		@Override
		public ChitDemandScheduleData mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			Double collectedAmount = rs.getDouble("collectedAmount");
			Double penaltyAmount = rs.getDouble("penalty");
			return ChitDemandScheduleData.forClosureInstance(collectedAmount,penaltyAmount);
		}	
	}
	
	@Override
	public Collection<ChitDemandScheduleData> retrieveForClosureData(Long chitSubscriberChargeid, Long staffId, Long chitId) {
		try {
			System.out.println("0011");
			final chitDemandForClosureMapper rm = new chitDemandForClosureMapper();
			System.out.println("0012");
			final String sql = "SELECT " + rm.schema(chitSubscriberChargeid,staffId,chitId) ;
			System.out.println("0013");
			return this.jdbcTemplate.query(sql, rm, new Object[] {});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}
	@Override
	public Double getSubscriberPaidedAmount(Long chitSubscriberChargeid, Long staffId, Long chitId) {

		try
		{
			final String sql = "SELECT  SUM(cds.collected_amount) as collectedAmount \r\n"
					+ "from chit_demand_schedule cds \r\n"
					+ "where cds.chit_subscriber_charge_id = "+chitSubscriberChargeid+" \r\n"
					+ "and cds.staff_id = "+staffId+" and cds.is_calculated = 1 and cds.chit_id = "+chitId+"";
			
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
			JsonObject json = new JsonObject();
			double totalAmount = 0.0;
			while (rs.next()) 
			{	
				totalAmount = rs.getDouble("collectedAmount");
			}	
				return totalAmount;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	
}
