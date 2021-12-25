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
package org.apache.fineract.accounting.producttoaccountmapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GlAccountChitReadPlatformServiceImpl implements GlAccountChitReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	
	
	
	

	@Autowired
	public GlAccountChitReadPlatformServiceImpl(final RoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private final static class GlProductAccountMapper implements RowMapper<Map<String, Object>> {

		public String schema() {
			return " ap.id as id, ap.gl_account_id as glAccountId, ap.product_id as productId, ap.product_type as productType, "
					+ " ap.financial_account_type as financialAccountType from acc_product_mapping ap ";
			
		}
		final static Map<String, Object> accountProductMap =  new LinkedHashMap<>(5);
		   
		@Override
		public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			    final Long id = rs.getLong("id");
	            final Long glAccountId = rs.getLong("glAccountId");
	            final Long productId = rs.getLong("productId");
	            final Long productType = JdbcSupport.getLong(rs, "productType");
	            final Integer financialAccountType = rs.getInt("financialAccountType");
	            
	         
	            	
	            accountProductMap.put("id", id);
	            accountProductMap.put("glAccountId", glAccountId);
	            accountProductMap.put("productId", productId);
	            accountProductMap.put("productType", productType);
	            accountProductMap.put("financialAccountType", financialAccountType);
	            
			return accountProductMap;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getProductAccountMappingData(Long productId, Long productType,Long financialAccountType){
		//final Map<String, Object> accountMappingDetails = new LinkedHashMap<>(8);

        final GlProductAccountMapper rm = new GlProductAccountMapper();
        final String sql = "select " + rm.schema() + " where ap.product_id = ? and ap.product_type = ? and ap.financial_account_type = ? ";
        final List<Map<String, Object>> listOfProductToGLAccountMap = this.jdbcTemplate.query(sql, rm,
        		new Object[] { productId, productType, financialAccountType });
        final Map<String, Object> listOfProductToGLAccountMaps= GlProductAccountMapper.accountProductMap;
        
        
        		//new Object[] { productId, PortfolioProductType.CHIT, CashAccountsForChit.OWN_CHITS });
        
       // accountMappingDetails.put(sql, listOfProductToGLAccountMaps);
        return listOfProductToGLAccountMaps;
		//return (Map<String, Object>) listOfProductToGLAccountMaps;
	}
	
}
