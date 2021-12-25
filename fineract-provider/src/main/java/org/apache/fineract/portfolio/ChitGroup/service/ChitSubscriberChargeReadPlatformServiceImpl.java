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
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupDropDownData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ChitSubscriberChargeReadPlatformServiceImpl implements ChitSubscriberChargeReadPlatformServices
{
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitBidsData> paginationHelper = new PaginationHelper<>();
	private final ChitsSubscriberChargeMapper mapper = new ChitsSubscriberChargeMapper();
	private final ChitsSubscriberDropDownMapper mapper1 = new ChitsSubscriberDropDownMapper();
	
	@Autowired
	public ChitSubscriberChargeReadPlatformServiceImpl(PlatformSecurityContext context,
			ColumnValidator columnValidator,final RoutingDataSource dataSource) {
	
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.columnValidator = columnValidator;
	}
	
	private static final class ChitsSubscriberChargeMapper implements RowMapper<ChitSubscriberChargeData>
	{
		public String schema() 
		{
			return "cs.id as id, cs.chit_subscriber_id as chitsubscriberid, cs.chit_charge_id as chitchargeid , cs.chit_cycle_id as chitcycleid , cs.amount as amount,cs.due_date as duedate,cs.is_paid as ispaid,cs.is_waived as iswaived, cs.staff_id as staffId from chit_subscriber_charge cs";
		}
		
		@Override
		public ChitSubscriberChargeData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Long id = rs.getLong("id");
			Long chitsubscriberid = rs.getLong("chitsubscriberid");
			Long chitchargeid = rs.getLong("chitchargeid");
			Long chitcycleid = rs.getLong("chitcycleid");
			Long amount = rs.getLong("amount");
			LocalDate duedate = JdbcSupport.getLocalDate(rs, "duedate");
			Boolean ispaid = rs.getBoolean("ispaid");
			Boolean iswaived = rs.getBoolean("iswaived");
			Long staffId = rs.getLong("staffId");
			
			return ChitSubscriberChargeData.instance(id, chitsubscriberid, chitchargeid, chitcycleid, amount, duedate, ispaid, iswaived,staffId);
		}
		
	}
	
	private static final class ChitsSubscriberDropDownMapper implements RowMapper<ChitGroupDropDownData>
	{
		public String schema() 
		{
			return " s.chit_id as chitid, s.chit_number as chitnumber, cg.name as name FROM chit_group_subscriber s LEFT JOIN chit_group cg ON cg.id = s.chit_id ";
		}
		
		@Override
		public ChitGroupDropDownData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Long chitId = rs.getLong("chitid");
			Long chitnumber = rs.getLong("chitnumber");
			String name = rs.getString("name"); 
			
			return ChitGroupDropDownData.instance(chitId,name,chitnumber);
		}
		
	}

	@Override
	public Collection<ChitSubscriberChargeData> retrieveAll() 
	{
		try {
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper();
			final String sql = "select " + rm.schema();

			return this.jdbcTemplate.query(sql, this.mapper);
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	@Override
	public Collection<ChitSubscriberChargeData> retrieveByChitSubscriberId(Long chitSubsId) 
	{
		try {
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper();
			final String sql = "select " + rm.schema() +" where cs.chit_subscriber_id = ?";

			return this.jdbcTemplate.query(sql, this.mapper,new Object[]{chitSubsId});
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	@Override
	public Collection<ChitSubscriberChargeData> retrieveByChargeId(Long SubsId,Long tranType,Long trantype1) 
	{
		try {
			////System.out.println("SubsId "+SubsId);
			////System.out.println("tranType "+tranType);
			////System.out.println("trantype1 "+trantype1);
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper();
			final String sql = "select " + rm.schema() + " where cs.chit_subscriber_id = ? and cs.chit_charge_id != ? and cs.chit_charge_id != ? and cs.is_paid = 0";
			////System.out.println("sql "+sql);
			return this.jdbcTemplate.query(sql, this.mapper , new Object[] {SubsId,tranType,trantype1});
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	@Override
	public
	Collection<ChitGroupDropDownData> retrieveChitDataForDropDown(Long clientId)
	{
		try {
			
			final ChitsSubscriberDropDownMapper rm = new ChitsSubscriberDropDownMapper();
			final String sql = "select " + rm.schema() + "WHERE s.client_id = ?";
			////System.out.println("sql "+sql);
			return this.jdbcTemplate.query(sql, this.mapper1 , new Object[] {clientId});
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}

	
	@Override
	public ChitSubscriberChargeData retrieveNameById(Long id) {
		try {
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper();
			final String sql = "select " + rm.schema() + " where cs.id = ? ";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(id, e);
		}
	}

	@Override
	public ChitSubscriberChargeData retrieveById(Long chitSubscriberId, Long chitchargeid, Long cycleId) {
		try {
			final ChitsSubscriberChargeMapper rm = new ChitsSubscriberChargeMapper();
			final String sql = "select " + rm.schema() + " where cs.chit_subscriber_id = ? AND cs.chit_charge_id= ? AND cs.chit_cycle_id = ? ";
			////System.out.println(sql+" sql");
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {chitSubscriberId,chitchargeid,cycleId});
		} catch (final EmptyResultDataAccessException e) {
			e.getStackTrace();	
			return null;
		}
		
	}
}
