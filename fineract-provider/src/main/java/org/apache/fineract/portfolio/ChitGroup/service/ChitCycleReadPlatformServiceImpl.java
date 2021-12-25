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
import java.time.ZonedDateTime;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.portfolio.ChitGroup.data.ChitCycleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleData;

import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ChitCycleReadPlatformServiceImpl implements ChitCycleReadPlatformService
{
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitDemandScheduleData> paginationHelper = new PaginationHelper<>();
	private final ChitDemandScheduleMapper mapper = new ChitDemandScheduleMapper();
	private final ChitScheduleMapper chitmapper = new ChitScheduleMapper();
	
	
	@Autowired
	public ChitCycleReadPlatformServiceImpl(PlatformSecurityContext context,
			ColumnValidator columnValidator,final RoutingDataSource dataSource) {
	
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.columnValidator = columnValidator;
	}
	
	private static final class ChitDemandScheduleMapper implements RowMapper<ChitCycleData>
	{
		public String schema() 
		{
			return "cd.id as id,cd.cycle_number as cyclenumber,cd.auction_date as auctiondate,cd.dividend as dividend,"
					+ "cd.subscriptionPayble as subscriptionPayble,cd.gstAmount as gstAmount,cd.foremanCommissionAmount as foremanCommissionAmount,"
					+ "cd.verificationAmount as verificationAmount from chit_group_cycle cd";
		}
		
		@Override
		public ChitCycleData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Long id = rs.getLong("id");
			Long cycle = rs.getLong("cyclenumber");
			ZonedDateTime auctiondate = JdbcSupport.getLocalDateTime(rs, "auctiondate");
			LocalDate temp = null;
			if(auctiondate!=null)
			{
				temp = auctiondate.toLocalDate();
			}
			Double verificationAmount = rs.getDouble("verificationAmount");
			Double subscriptionPayble = rs.getDouble("subscriptionPayble");
			Double gstAmount = rs.getDouble("gstAmount");
			Double foremanCommissionAmount = rs.getDouble("foremanCommissionAmount");
			Long dividend = rs.getLong("dividend");
			return ChitCycleData.instance(id,null,cycle,temp,null,null,dividend,null,subscriptionPayble,gstAmount,foremanCommissionAmount,verificationAmount);
		}
		
	}
	
	@Override
	public ChitCycleData retrieveAll(Long chitId,Long cycleNumber) {
		try {
			
			final ChitDemandScheduleMapper rm = new ChitDemandScheduleMapper();
			final String sql = "select " + rm.schema() + " WHERE cd.chit_id = ? and cd.cycle_number = ?" ;
			////System.out.println(sql);
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {chitId,cycleNumber});
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(chitId, e);
		}
	}

	@Override
	public ChitCycleData retrievecycleid(Long cycleId) {
		////System.out.println("inside read method");
		try {
		
			final ChitDemandScheduleMapper rm = new ChitDemandScheduleMapper();
			final String sql = "select " + rm.schema() + " WHERE cd.id = ? " ;
			////System.out.println("sql "+sql);
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {cycleId});
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(cycleId, e);
		}
	}
	
	private static final class ChitScheduleMapper implements RowMapper<ChitCycleData>
	{
		public String schema() 
		{
			return "cd.id as id,cd.cycle_number as cyclenumber,cd.chit_id as chitId,cd.auction_date as auctiondate,"
					+ "cd.subscriptionPayble as subscriptionPayble,cd.gstAmount as gstAmount,cd.foremanCommissionAmount as foremanCommissionAmount,"
					+ "cd.verificationAmount as verificationAmount,cd.dividend as dividend from chit_group_cycle cd";
		}
		
		@Override
		public ChitCycleData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Long id = rs.getLong("id");
			Long chitGroupId = rs.getLong("chitId");
			Long cycleNumber = rs.getLong("cycleNumber");
			ZonedDateTime auctiondate = JdbcSupport.getLocalDateTime(rs, "auctiondate");
			LocalDate temp = null;
			if(auctiondate!=null)
			{
				temp = auctiondate.toLocalDate();
			}
			Double verificationAmount = rs.getDouble("verificationAmount");
			Double subscriptionPayble = rs.getDouble("subscriptionPayble");
			Double gstAmount = rs.getDouble("gstAmount");
			Double foremanCommissionAmount = rs.getDouble("foremanCommissionAmount");
			Long dividend = rs.getLong("dividend");
			return ChitCycleData.instance(id,chitGroupId,cycleNumber,temp,null,null,dividend,null,subscriptionPayble,gstAmount,foremanCommissionAmount,verificationAmount);
		}
		
	}
	
	@Override
	public ChitCycleData getChitDataByChitIdAndCycleNumber(Long chitId, Long cycleNumber)
	{
		try {
			final ChitScheduleMapper cm = new ChitScheduleMapper();
			String sql = "select " + cm.schema() + " where cd.chit_id = ? and cd.cycle_number = ? ";
			return this.jdbcTemplate.queryForObject(sql, cm, new Object[] {chitId,cycleNumber});
		} catch(final EmptyResultDataAccessException e ) {
			throw new ChitGroupNotFoundException(chitId, e);
		}
		
		
	}
}
