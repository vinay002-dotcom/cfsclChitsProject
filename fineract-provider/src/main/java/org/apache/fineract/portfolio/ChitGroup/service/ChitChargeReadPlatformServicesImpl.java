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
import java.util.Collection;

import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitChargeData;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ChitChargeReadPlatformServicesImpl implements ChitChargeReadPlatformServices
{
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitBidsData> paginationHelper = new PaginationHelper<>();
	private final ChitsChargeMapper chitmapper = new ChitsChargeMapper();
	
	
	@Autowired
	public ChitChargeReadPlatformServicesImpl(PlatformSecurityContext context,
			ColumnValidator columnValidator,final RoutingDataSource dataSource) {
	
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.columnValidator = columnValidator;
	}

	private static final class ChitsChargeMapper implements RowMapper<ChitChargeData> 
	{
		public String schema() 
		{
			return "ch.id as id, ch.name as name, ch.amount as amount,ch.isEnabled as isEnabled from chit_charge ch";
		}
		
		@Override
		public ChitChargeData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Long id = rs.getLong("id");
			String name = rs.getString("name");
			Double amount = rs.getDouble("amount");
			Boolean isEnabled = rs.getBoolean("isEnabled");
			return ChitChargeData.instance(id, name, amount,isEnabled);
		}
	}
		
	@Override
	public Collection<ChitChargeData> retrieveAll() {
		
		final ChitsChargeMapper cm = new ChitsChargeMapper();
		String sql = "select " + cm.schema();
		return this.jdbcTemplate.query(sql, this.chitmapper);
	}

	@Override
	public ChitChargeData retrieveNameById(Long id) {
		try {
			final ChitsChargeMapper rm = new ChitsChargeMapper();
			final String sql = "select " + rm.schema() + " where ch.id = ? ";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(id, e);
		}
	}

	@Override
	public ChitChargeData retrieveIdByName(String name) {
		try {
			final ChitsChargeMapper rm = new ChitsChargeMapper();
			final String sql = "select " + rm.schema() + " where ch.name = ? ";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { name });
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
}
