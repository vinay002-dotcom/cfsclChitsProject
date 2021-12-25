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
import java.util.ArrayList;


import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsData;

import org.apache.fineract.portfolio.ChitGroup.data.NsChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class NsChitGroupReadPlatformServiceImpl implements NsChitGroupReadPlatformService
{
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitBidsData> paginationHelper = new PaginationHelper<>();
	private final NsChitsMapper chitmapper = new NsChitsMapper();
	private final FromJsonHelper fromJsonHelper;
	
	@Autowired
	public NsChitGroupReadPlatformServiceImpl(PlatformSecurityContext context,
			ColumnValidator columnValidator,final RoutingDataSource dataSource,FromJsonHelper fromJsonHelper) {
	
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.columnValidator = columnValidator;
		this.fromJsonHelper = fromJsonHelper;
	}

	private static final class NsChitsMapper implements RowMapper<NsChitGroupData> 
	{
		public String schema() 
		{
			return "ch.id as id , ch.chit_name as chitName,ch.chit_duration as chitDuration,ch.chit_value as chitValue,ch.no_of_subscribers as noOfSubscribers,"
					+ "ch.min_bid_perct as minbidperct,ch.max_bid_perct as maxbidperct,ch.isEnabled as isEnabled, ch.enrollment_fee as enrollmentfee from ns_chit_group ch";
		}
		
		@Override
		public NsChitGroupData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Long id = rs.getLong("id");
			String name = rs.getString("chitName");
			Double amount = rs.getDouble("chitValue");
			Long duration = rs.getLong("chitDuration");
			Long Subscribers = rs.getLong("noOfSubscribers");	
			Double minPer = rs.getDouble("minbidperct");
			Double maxbidperct = rs.getDouble("maxbidperct");
			Boolean isEnabled = rs.getBoolean("isEnabled");
			Double enrollmentfee = rs.getDouble("enrollmentfee");
			return NsChitGroupData.instance(id, name, amount,duration,Subscribers,minPer,maxbidperct,isEnabled, enrollmentfee);
		}
	}
		
	@Override
	public JsonObject retrieveAll() {
		
		try {
			JsonObject dataTobeSent = new JsonObject();
			final String sql = "select ch.id as id , ch.chit_name as chitName,ch.chit_duration as chitDuration,ch.chit_value as chitValue,ch.no_of_subscribers as noOfSubscribers,"
					+ "ch.min_bid_perct as minbidperct,ch.max_bid_perct as maxbidperct,ch.isEnabled as isEnabled,ch.enrollment_fee as enrollmentfee  from ns_chit_group ch";
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
			ArrayList<JsonObject> arr = new ArrayList<JsonObject>(); 

			while (rs.next()) {	
				JsonObject json = new JsonObject();
				json.addProperty("id", rs.getLong("id"));
				json.addProperty("name",rs.getString("chitName"));
				json.addProperty("chitValue",rs.getDouble("chitValue"));
				json.addProperty("chitDuration", rs.getLong("chitDuration"));
				json.addProperty("NoOfSubs", rs.getLong("noOfSubscribers"));
				json.addProperty("minPercent",rs.getDouble("minbidperct"));
				json.addProperty("maxPercent", rs.getDouble("maxbidperct"));
				json.addProperty("isEnabled", rs.getBoolean("isEnabled"));
				json.addProperty("enrollmentfee", rs.getDouble("enrollmentfee"));
				arr.add(json);
			}
			JsonElement parse = fromJsonHelper.parse(arr.toString());
			dataTobeSent.add("Product", parse);	
			return dataTobeSent;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}

	@Override
	public NsChitGroupData retrieveById(Long id) {
		try {
			final NsChitsMapper rm = new NsChitsMapper();
			final String sql = "select " + rm.schema() + " where ch.id = ? ";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(id, e);
		}
	}
}
