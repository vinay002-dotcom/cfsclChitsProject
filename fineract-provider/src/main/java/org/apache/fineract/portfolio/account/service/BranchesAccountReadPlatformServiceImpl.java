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
package org.apache.fineract.portfolio.account.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.portfolio.account.data.BranchesAccountData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class BranchesAccountReadPlatformServiceImpl implements BranchesAccountReadPlatformService{

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<BranchesAccountData> paginationHelper = new PaginationHelper<>();
	private final BranchesMapper branchesMapper = new BranchesMapper();
	private final CashMapper cashMapper = new CashMapper();
	private final BankMapper bankMapper = new BankMapper();
	private final PlatformSecurityContext platformSecurityContext;
	private final GLAccountReadPlatformService gLAccountReadPlatformService;
	
	@Autowired
	public BranchesAccountReadPlatformServiceImpl(final RoutingDataSource dataSource,
			PlatformSecurityContext platformSecurityContext, ColumnValidator columnValidator,GLAccountReadPlatformService gLAccountReadPlatformService) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.platformSecurityContext = platformSecurityContext;
		this.gLAccountReadPlatformService = gLAccountReadPlatformService;
	}

	public final class BranchesMapper implements RowMapper<BranchesAccountData>
	{
		public String schema() {
			return " ba.id as branchId, ba.cash_glaccount_id as cashglAccountId, ba.bank_glaccount_id as bankglAccountId from branches_gl_account ba";
		}
		
		@Override
		public BranchesAccountData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long branchId = rs.getLong("branchId");
			Long cashglAccountId = rs.getLong("cashglAccountId");
			Long bankglAccountId = rs.getLong("bankglAccountId");
			String cashaccount = null;
			 JournalEntryAssociationParametersData associationParametersData = new JournalEntryAssociationParametersData(false,false);
			if(cashglAccountId!=null)
			{
				
				cashaccount = gLAccountReadPlatformService.retrieveGLAccountById(cashglAccountId,associationParametersData).getName();
			}
			String bankglAccount = null;
			if(bankglAccountId!=null)
			{
				bankglAccount = gLAccountReadPlatformService.retrieveGLAccountById(bankglAccountId,associationParametersData).getName();
			}
			return BranchesAccountData.instance(branchId, cashglAccountId, bankglAccountId,cashaccount,bankglAccount);
		}
		
	}
	
    public static final class CashMapper implements RowMapper<GLAccountData> {
		public String schema() {
			
			return "select * from acc_gl_account glacc\r\n"
					+ "where glacc.parent_id in (select agc.id from acc_gl_account agc where agc.name='Cash In Hand' and agc.classification_enum=1) and glacc.manual_journal_entries_allowed=1 and glacc.disabled=0\r\n";
		}

		@Override
		public GLAccountData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			 final Long id = rs.getLong("id");
	            final String name = rs.getString("name");            
	            return new GLAccountData(id, name, null);
	        }
		}
    
    public static final class BankMapper implements RowMapper<GLAccountData> {
		public String schema() {
			
			return "select * from acc_gl_account glacc\r\n"
					+ "where glacc.parent_id in (select agc.id from acc_gl_account agc where agc.name='Bank Accounts' and agc.classification_enum=1) and glacc.manual_journal_entries_allowed=1 and glacc.disabled=0\r\n";
		}

		@Override
		public GLAccountData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			 final Long id = rs.getLong("id");
	         final String name = rs.getString("name");
	         return new GLAccountData(id, name, null);
	        }
		}
	
	@Override
	public Collection<BranchesAccountData> retrieveBranchesData() {
		final BranchesMapper bm = new BranchesMapper();
		String sql = "select" + bm.schema();
		return this.jdbcTemplate.query(sql, this.branchesMapper);
	}
	
	@Override
	public Collection<BranchesAccountData> retrieveBranchesById(Long id) {
		final BranchesMapper bm = new BranchesMapper();
		String sql = "select" + bm.schema() + " where ba.id = ? ";
		return this.jdbcTemplate.query(sql, bm, new Object[] { id });
	}

	@Override
	public Collection<GLAccountData> retriveCashesData() {
		final CashMapper cm = new CashMapper();
		String sql = cm.schema();
		return this.jdbcTemplate.query(sql, this.cashMapper);
	}

	@Override
	public Collection<GLAccountData> retriveBanksData() {
		final BankMapper bm = new BankMapper();
		String sql = bm.schema();
		return this.jdbcTemplate.query(sql, this.bankMapper);
	}
	
	
}
