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
package org.apache.fineract.portfolio.account.data;

import java.io.Serializable;

@SuppressWarnings("unused")
public class BranchesAccountData implements Serializable {

	private final Long branchId;
	
	private final Long cashglAccountId;
	
	private final Long bankglAccountId;
	
	private final String cashglAccount;
	private final String bankglAccount;
	
	public BranchesAccountData(Long branchId, Long cashglAccountId,  
			Long bankglAccountId,String cashglAccount,
			String bankglAccount) {
		this.branchId = branchId;
		this.cashglAccountId = cashglAccountId;
		this.bankglAccountId = bankglAccountId;
		this.cashglAccount= cashglAccount;
		this.bankglAccount = bankglAccount;
	}
	
	
	public static BranchesAccountData instance(final Long branchId, 
			final Long cashglAccountId, final Long bankglAccountId,String cashglAccount,
			String bankglAccount) {
		return new BranchesAccountData(branchId, cashglAccountId, bankglAccountId,cashglAccount,bankglAccount);
		
	}

	public Long getBranchId() {
		return branchId;
	}

	
	public Long getCashglAccountId() {
		return cashglAccountId;
	}

	public Long getBankglAccountId() {
		return bankglAccountId;
	}
		
		
}
