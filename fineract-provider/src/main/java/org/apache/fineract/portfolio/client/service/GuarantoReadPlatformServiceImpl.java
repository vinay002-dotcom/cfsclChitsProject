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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsData;
import org.apache.fineract.portfolio.ChitGroup.exception.SomethingWentWrongException;
import org.apache.fineract.portfolio.client.data.GuarantorType;
import org.apache.fineract.portfolio.client.data.GurantorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GuarantoReadPlatformServiceImpl implements GuarantoReadPlatformService
{
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitBidsData> paginationHelper = new PaginationHelper<>();
	private final guarantorMapper mapper = new guarantorMapper();
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	
	
	@Autowired
	public GuarantoReadPlatformServiceImpl(PlatformSecurityContext context,
			ColumnValidator columnValidator,final RoutingDataSource dataSource,final CodeValueReadPlatformService codeValueReadPlatformService) {
	
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.columnValidator = columnValidator;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
	}

	private final class guarantorMapper implements RowMapper<GurantorData> 
	{
		
		public String schema() 
		{
			return "g.id as id,g.client_reln_cv_id as relationShipId,g.type_enum as typeenum,g.entity_id as entityId,g.firstname as firstname,g.lastName as lastname,"
					+ "g.dob as dob,g.address_line_1 as Addr1,g.address_line_2 as Addr2,g.city as city,g.state as state,"
					+ "g.country as country,g.zip as zip,g.mobile_number as Phone,g.comment as comment,g.is_active as isActive,g.client_id as ClientId,g.qualification as qualification,g.profession as profession from m_guarantor g";
		}
		
		@Override
		public GurantorData mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Long id = rs.getLong("id");
			Long clientRelationshipId = rs.getLong("relationShipId");
			GuarantorType typeEnum = GuarantorType.fromInt(rs.getInt("typeenum"));
			String entityid = rs.getString("entityId");
			String firstName = rs.getString("firstname");
			String lastName = rs.getString("lastname"); 
			LocalDate dob= JdbcSupport.getLocalDate(rs,"dob"); 
			String address1 = rs.getString("Addr1"); 
			String address2 = rs.getString("Addr2");  
			String city = rs.getString("city");  
			String state = rs.getString("state"); 
			String country = rs.getString("country"); 
			String zip = rs.getString("zip");
			String phone  = rs.getString("Phone");
			String comments = rs.getString("comment");
			Boolean isActive = rs.getBoolean("isActive");
			Long clientId = rs.getLong("ClientId");
			Long qualification = rs.getLong("qualification");
			Long profession = rs.getLong("profession");
			String professionType = null;
			if(profession!=null && profession!=0)
			{
				professionType = codeValueReadPlatformService.retrieveCodeValue(profession).getName();
			}
			String qualificationType = null;
			if(qualification!=null && qualification!=0)
			{
				qualificationType = codeValueReadPlatformService.retrieveCodeValue(qualification).getName();
			}
			String relationShip = null;
			if(clientRelationshipId!=null && clientRelationshipId!=0)
			{
				relationShip = codeValueReadPlatformService.retrieveCodeValue(clientRelationshipId).getName();
			}
			
			return GurantorData.instance(id,clientRelationshipId,typeEnum,entityid,firstName,lastName,dob,address1,address2,city,state,country,zip,phone,comments,isActive,clientId,qualification,profession,professionType,qualificationType,relationShip);
		}
	}

	@Override
	public Collection<GurantorData> RetrievGuarantorClientWise(Long ClientId) 
	{
		try {
			String sql = "select "+mapper.schema()+" where g.client_id = ?";
			guarantorMapper rm = new guarantorMapper();		
			return this.jdbcTemplate.query(sql, rm, new Object[] { ClientId });
		}catch (final EmptyResultDataAccessException e) {
			throw new  SomethingWentWrongException();
		}
	
	}
		
}
