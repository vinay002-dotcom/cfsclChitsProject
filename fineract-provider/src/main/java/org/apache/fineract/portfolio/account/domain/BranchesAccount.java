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

package org.apache.fineract.portfolio.account.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import com.google.gson.JsonObject;

@Entity
@Table(name = "branches_gl_account")
public class BranchesAccount extends AbstractPersistableCustom{
	
	@Column(name="id", length=20)
	private Long branchId;
	
	@Column(name="cash_glaccount_id", length=20)
	private Long cashglAccountId;
	
	@Column(name="bank_glaccount_id", length=20)
	private Long bankglAccountId;

	

	public BranchesAccount(Long branchId, Long cashglAccountId, Long bankglAccountId) {
		this.branchId = branchId;
		this.cashglAccountId = cashglAccountId;
		this.bankglAccountId = bankglAccountId;
	}



	public static BranchesAccount create(final JsonObject command)
	{
		Long branchId = null;
		Long cashglAccountId = null;
		Long bankglAccountId = null;
		
		final String branchParamName = "branchId";
		if(command.get(branchParamName)!=null && !command.get(branchParamName).isJsonNull())
		{
			branchId = command.get(branchParamName).getAsLong();

		}
		
		final String cashparamName = "cashglAccountId";
		if(command.get(cashparamName)!=null && !command.get(cashparamName).isJsonNull())
		{
			cashglAccountId = command.get(cashparamName).getAsLong();
		}
		
		final String bankparamName = "bankglAccountId";
		if(command.get(bankparamName)!=null && !command.get(bankparamName).isJsonNull())
		{
			bankglAccountId = command.get(bankparamName).getAsLong();
		}
		return new BranchesAccount(branchId,cashglAccountId,bankglAccountId);
		
	}
	
		
	protected BranchesAccount()
	{
		
	}
	
	public Map<String, Object> update(final JsonObject command)
	{
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);

//		final String branchIdParamName="branchId";
//		if(command.get(branchIdParamName)!=null && !command.get(branchIdParamName).isJsonNull())
//		{
//			final Long newValue = command.get(branchIdParamName).getAsLong();
//			actualChanges.put(branchIdParamName, newValue);
//			
//			this.branchId = newValue;
//		}
		
		final String cashglAccountIdParamName = "cashglAccountId";
		if(command.get(cashglAccountIdParamName)!=null && !command.get(cashglAccountIdParamName).isJsonNull())
		{
			final Long newValue = command.get(cashglAccountIdParamName).getAsLong();
			actualChanges.put(cashglAccountIdParamName, newValue);
			
			this.cashglAccountId = newValue;
				
		}
		
		final String bankglAccountIdParamName = "bankglAccountId";
		if(command.get(bankglAccountIdParamName)!=null && !command.get(bankglAccountIdParamName).isJsonNull())
		{
			final Long newValue = command.get(bankglAccountIdParamName).getAsLong();
			actualChanges.put(bankglAccountIdParamName, newValue);
			
			this.bankglAccountId = newValue;
			
		}
		return actualChanges;
	}


	
	public Long getBranchId() {
		return branchId;
	}


	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}


	public Long getCashglAccountId() {
		return cashglAccountId;
	}


	public void setCashglAccountId(Long cashglAccountId) {
		this.cashglAccountId = cashglAccountId;
	}


	public Long getBankglAccountId() {
		return bankglAccountId;
	}


	public void setBankglAccountId(Long bankglAccountId) {
		this.bankglAccountId = bankglAccountId;
	}

	
	
}
