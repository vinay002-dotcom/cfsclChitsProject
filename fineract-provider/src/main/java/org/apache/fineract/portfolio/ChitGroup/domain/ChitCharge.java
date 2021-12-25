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

package org.apache.fineract.portfolio.ChitGroup.domain;


import java.util.LinkedHashMap;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import com.google.gson.JsonObject;

@Entity
@Table(name = "chit_charge")
public class ChitCharge extends AbstractPersistableCustom {
	
	@Column(name="amount")
	private Double amount;
	
	@Column(name="name")
	private String name;
	
	@Column(name="isEnabled")
	private Boolean isEnabled;
	
	protected ChitCharge()
	{
		
	}
	private ChitCharge(Double amount, String name,Boolean isEnabled) {
		
		this.amount = amount;
		this.name = name;
		this.isEnabled = isEnabled;
	}



	public Map<String, Object> update(final JsonObject command) {
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

	
		
		
		final String nameParamName = "name";
		if (command.get(nameParamName)!=null && !command.get(nameParamName).isJsonNull()) 
		{
			
			final String newValue = command.get(nameParamName).getAsString();
			actualChanges.put(nameParamName, newValue);
			this.name = newValue;
		}
		
		final String amountParamName = "amount";
		if (command.get(amountParamName)!=null && !command.get(amountParamName).isJsonNull()) 
		{
			
			final Double newValue = command.get(amountParamName).getAsDouble();
			
			actualChanges.put(amountParamName, newValue);
			this.amount = newValue;
		}

		final String isEnabledParamName  = "isEnabled";
		if (command.get(isEnabledParamName)!=null && !command.get(isEnabledParamName).isJsonNull()) 
		{
			
			final Boolean newValue = command.get(isEnabledParamName).getAsBoolean();
			
			actualChanges.put(amountParamName, newValue);
			this.isEnabled = newValue;
		}
		
		return actualChanges;
	}
		   
		
	public static ChitCharge create(final JsonObject command) {
		
		Double amount = null;
		String name = null;
		Boolean isEnabled = null;
		
		final String amountParamName = "amount";
		if (command.get(amountParamName)!=null && !command.get(amountParamName).isJsonNull()) 
		{
			amount = command.get(amountParamName).getAsDouble();
			
		}
		final String nameParamName = "name";
		if (command.get(nameParamName)!=null && !command.get(nameParamName).isJsonNull()) 
		{
			
			name = command.get("name").getAsString();
		}
		final String isEnabledParamName  = "isEnabled";
		if (command.get(isEnabledParamName)!=null && !command.get(isEnabledParamName).isJsonNull()) 
		{
			
			isEnabled = command.get(isEnabledParamName).getAsBoolean();
			
		}
		return new ChitCharge(amount, name,isEnabled);
	}



	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public Double getAmount() {
		return amount;
	}



	public void setAmount(Double amount) {
		this.amount = amount;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

}
