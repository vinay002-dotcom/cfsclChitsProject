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
package org.apache.fineract.infrastructure.pincode.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.codes.exception.CodeNotFoundException;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformServiceImpl;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.pincode.data.PincodeData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
//import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
//import org.apache.fineract.portfolio.savings.domain.SavingsAccountStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service

public class PincodeReadPlatfromServiceImpl implements PincodeReadPlatformServices
{
	 private final JdbcTemplate jdbcTemplate;
	 private final PlatformSecurityContext context;
	 private final pincodeMapper pincodemapper= new pincodeMapper();
	 private final ColumnValidator columnValidator;
	  private final PaginationHelper<PincodeData> paginationHelper = new PaginationHelper<>();
	  private final CodeValueReadPlatformServiceImpl codereadservices;
	 
	@Autowired
	public PincodeReadPlatfromServiceImpl(PlatformSecurityContext context,
			ColumnValidator columnValidator,final RoutingDataSource dataSource,final CodeValueReadPlatformServiceImpl codereadservices) {
		super();
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.columnValidator = columnValidator;
		this.codereadservices = codereadservices;
	}

	 private  final static class pincodeMapper implements RowMapper<PincodeData>
	 {
		 
		 public String schema() 
		 {
			 return 
			 		 "cv_taluk.id as talukId,  cv_dist.id as districtId,  cv_state.id as stateId,"
			 		+ "c.pincode as pincode, c.post_office_name as Areaname,c.taluk as taluk,c.district as district, c.state as state from pin_code c "
			 		+ "left join m_code_value cv_taluk on cv_taluk.code_value = c.taluk left join m_code_value cv_dist on cv_dist.code_value = c.district "
			 		+ "left join m_code_value cv_state on cv_state.code_value = c.state";
		 }
		 
		 public String schema2()
		 {
			 return " and cv_taluk.code_id in (select id from m_code where code_name='Taluka') and "
			 		+ "cv_dist.code_id in (select id from m_code where code_name='District') and "
			 		+ "cv_state.code_id in (select id from m_code where code_name='State');" ;
		 }

		@Override
		public PincodeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException 
		{
			final String Areaname = rs.getString("Areaname");
		
			final String taluk = rs.getString("taluk");
			final Long talukaid = rs.getLong("talukId");
			
			final String district = rs.getString("district");
			final Long districtid = rs.getLong("districtId");
			final String state = rs.getString("state");
			final Long stateid = rs.getLong("stateId");
			final Long pincode = rs.getLong("pincode");
			
			// ////System.out.println("Areaname "+Areaname);
			return PincodeData.importInstance(Areaname, null, null, taluk, district, state,pincode,talukaid,districtid,stateid);
		}
		
	
		 
	 }

	@Override
	public Page<PincodeData> retriveaddress(Long pincode)
	{
	    try {
            this.context.authenticatedUser();

            final pincodeMapper rm = new pincodeMapper();
            final String sql = "select " + rm.schema() + " where c.pincode = ?" + rm.schema2();
            final String sqlCountRows = "SELECT FOUND_ROWS()";
            // ////System.out.println(sql + "------------sql ");
            return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sql, new Object[] {pincode}, this.pincodemapper);
        } catch (final EmptyResultDataAccessException e) {
            throw new CodeNotFoundException(pincode, e);
        }
    }
}
