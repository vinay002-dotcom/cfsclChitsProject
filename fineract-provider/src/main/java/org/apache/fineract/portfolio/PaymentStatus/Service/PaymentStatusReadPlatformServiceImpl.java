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
package org.apache.fineract.portfolio.PaymentStatus.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.PaymentStatus.Data.PaymentStatusData;
import org.apache.fineract.portfolio.PaymentStatus.exception.PaymentStatusNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusReadPlatformServiceImpl implements PaymentStatusReadPlatformService{

	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	PaymentStatusReadPlatformServiceImpl(final RoutingDataSource datasource){
		this.jdbcTemplate = new JdbcTemplate(datasource); 
	}
	
	public static final class PaymentMapper implements RowMapper<PaymentStatusData>{
		
		public String  schema() {
			return " ps.id AS id, ps.payment_type AS paymenttype, ps.`status` AS statusValue, \r\n"
					+ "ps.date AS localdate,ps.tran_id as transactionId, ps.officeId AS officeId\r\n"
					+ " FROM payment_status ps ";
		}
		
		@Override
		public PaymentStatusData mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			
			final Long id = rs.getLong("id");
			final Long paymenttype = rs.getLong("paymenttype");
			final Long statusValue = rs.getLong("statusValue");
			final LocalDate localdate = rs.getDate("localdate").toLocalDate();
			final Long transactionId = rs.getLong("transactionId");
			final Long officeId = rs.getLong("officeId");
			
			return PaymentStatusData.instance(id, paymenttype, statusValue, localdate, transactionId, officeId);
		}
	}
	

	@Override
	public PaymentStatusData getPaymentDetails(Long id) {
		try {
			PaymentMapper pm = new PaymentMapper();
			String sql = "SELECT " + pm.schema() + " WHERE ps.id = ? ";		
			return this.jdbcTemplate.queryForObject(sql, pm, new Object[]{id});
		} catch (final EmptyResultDataAccessException e) {
			throw new PaymentStatusNotFoundException(id, e);
		}
	}
}