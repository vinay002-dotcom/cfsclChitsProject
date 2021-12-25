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
package org.apache.fineract.portfolio.paymentdetail.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.paymentdetail.Exception.PaymentDetailNotFoundException;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentDetailsReadPlatformServiceImpl implements PaymentDetailsReadPlatformService {
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final PaginationHelper<ChitGroupData> paginationHelper = new PaginationHelper<>();
	private final PaymentDetailsMapper paymentDetailsMapper = new PaymentDetailsMapper();
	private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
	
	@Autowired
	public PaymentDetailsReadPlatformServiceImpl(final PlatformSecurityContext context,
			final RoutingDataSource dataSource, final ColumnValidator columnValidator,
			final PaymentTypeReadPlatformService paymentTypeReadPlatformService){
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.columnValidator = columnValidator;
		this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
		
	}
	
	private final class PaymentDetailsMapper implements RowMapper<PaymentDetailData>{
		public String schema() {
			return " pd.id as Id, pd.payment_type_id as paymentTypeId, pd.account_number as accountNumber ,"
					+ " pd.check_number as checkNumber, pd.routing_code as routingCode ,"
					+ " pd.receipt_number as receiptNumber, pd.bank_number as bankNumber , "
					+ " pd.transactionNo as transactionNo "
					+ " from m_payment_detail pd ";
		}
		@Override
		public PaymentDetailData mapRow(ResultSet rs,@SuppressWarnings("unused") int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			final Long id = rs.getLong("Id");
			final Long paymentTypeId = rs.getLong("paymentTypeId");
			PaymentTypeData paymentId = paymentTypeReadPlatformService.retrieveOne(paymentTypeId);
			final String accountNumber = rs.getString("accountNumber");
			final String checkNumber = rs.getString("checkNumber");
			final String routingCode = rs.getString("routingCode");
			final String receiptNumber = rs.getString("receiptNumber");
			final String bankNumber = rs.getString("bankNumber");
			final Long transactionNo = rs.getLong("transactionNo");
			
			return PaymentDetailData.instance(id, paymentId, accountNumber, checkNumber, routingCode, receiptNumber, bankNumber, transactionNo);
		}
		
	}

	@Override
	public PaymentDetailData retrivePaymentDetails(Long id) {
		try {
			PaymentDetailsMapper pm = new PaymentDetailsMapper();
			final String sql = "select " + pm.schema() + " where id = ? ";
			return this.jdbcTemplate.queryForObject(sql, pm, new Object[] {id});
		} catch(final EmptyResultDataAccessException e) {
			throw new PaymentDetailNotFoundException(id,e);
		}
		
	}

	@Override
	public Collection<PaymentDetailData> retrieveBySearch(String Fromdate,Long officeId,String toDate) {
		try {
			PaymentDetailsMapper pm = new PaymentDetailsMapper();
			final String sql = "select " + pm.schema() + " WHERE (pd.depositedDate between '"+Fromdate+"' and '"+toDate+"') and pd.officeId = "+officeId;
			System.out.println(sql);
			return this.jdbcTemplate.query(sql, pm, new Object[] {});
		} catch(final EmptyResultDataAccessException e) {
			throw new PaymentDetailNotFoundException(0l,e);
		}
	}

}
