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
@Table(name = "ns_chit_group")
public class NsChitGroup extends AbstractPersistableCustom 
{
	
	@Column(name="chit_name")
	private String chitName;
	
	@Column(name="chit_duration")
	private Long chitDuration;
	

	@Column(name="chit_value")
	private Double chitValue;
	
	@Column(name="no_of_subscribers")
	private Long noOfSubscribers;
	
	@Column(name = "min_bid_perct")
	private Double minPercent;
	
	@Column(name = "max_bid_perct")
	private Double maxPercent;
	
	@Column(name = "isEnabled")
	private Boolean isEnabled;
	
	@Column(name = "enrollment_fee")
	private Double enrollmentfee;
	
	protected NsChitGroup()
	{
		
	}
	
	public Map<String, Object> update(final JsonObject command) 
	{
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);
		
		if(command.get("chitName")!=null && !command.get("chitName").isJsonNull())
		{
			final String newValue = command.get("chitName").getAsString();
			actualChanges.put("chitName", newValue);
			this.chitName = newValue;
		}
		
		if(command.get("chitValue")!=null && !command.get("chitValue").isJsonNull())
		{
			final Double newValue = command.get("chitValue").getAsDouble();
			actualChanges.put("chitValue", newValue);
			this.chitValue = newValue;
		}
		
		if(command.get("chitDuration")!=null && !command.get("chitDuration").isJsonNull())
		{
			final Long newValue = command.get("chitDuration").getAsLong();
			actualChanges.put("chitDuration", newValue);
			this.chitDuration = newValue;
		}
		
		if(command.get("noOfSubscribers")!=null && !command.get("noOfSubscribers").isJsonNull())
		{
			final Long newValue = command.get("noOfSubscribers").getAsLong();
			actualChanges.put("noOfSubscribers", newValue);
			this.noOfSubscribers = newValue;
		}
		
		if(command.get("minPercent")!=null && !command.get("minPercent").isJsonNull())
		{
			final Double newValue = command.get("minPercent").getAsDouble();
			actualChanges.put("minPercent", newValue);
			this.minPercent = newValue;
		}
		
		if(command.get("maxPercent")!=null && !command.get("maxPercent").isJsonNull())
		{
			final Double newValue = command.get("maxPercent").getAsDouble();
			actualChanges.put("maxPercent", newValue);
			this.maxPercent = newValue;
		}
		
		if(command.get("isEnabled")!=null && !command.get("isEnabled").isJsonNull())
		{
			final Boolean newValue = command.get("isEnabled").getAsBoolean();
			actualChanges.put("isEnabled", newValue);
			this.isEnabled = newValue;
		}
		
		if(command.get("enrollmentfee")!=null && !command.get("enrollmentfee").isJsonNull())
		{
			final Double newValue = command.get("enrollmentfee").getAsDouble();
			actualChanges.put("enrollmentfee", newValue);
			this.enrollmentfee = newValue;
		}
		
		return actualChanges;
	}
	
	public static NsChitGroup create(JsonObject command)
	{
		
		String chitName = null;
		Double chitValue = null;
		Long chitDuration = null;
		Long noOfSubscribers = null;
		Double minPercent = null;
		Double maxPercent = null;
		Double enrollmentfee = null;
		Boolean isEnabled = null;
		
		if(command.get("chitName")!=null && !command.get("chitName").isJsonNull())
		{
			final String newValue = command.get("chitName").getAsString();
		
			chitName = newValue;
		}
		
		if(command.get("chitValue")!=null && !command.get("chitValue").isJsonNull())
		{
			final Double newValue = command.get("chitValue").getAsDouble();
			
			chitValue = newValue;
		}
		
		if(command.get("chitDuration")!=null && !command.get("chitDuration").isJsonNull())
		{
			final Long newValue = command.get("chitDuration").getAsLong();
			
			chitDuration = newValue;
		}
		
		if(command.get("noOfSubscribers")!=null && !command.get("noOfSubscribers").isJsonNull())
		{
			final Long newValue = command.get("noOfSubscribers").getAsLong();
			
			noOfSubscribers = newValue;
		}
		
		if(command.get("minPercent")!=null && !command.get("minPercent").isJsonNull())
		{
			minPercent = command.get("minPercent").getAsDouble();
			
		}
		
		if(command.get("maxPercent")!=null && !command.get("maxPercent").isJsonNull())
		{
			maxPercent = command.get("maxPercent").getAsDouble();
		}
		
		if(command.get("isEnabled")!=null && !command.get("isEnabled").isJsonNull())
		{
			isEnabled = command.get("isEnabled").getAsBoolean();
		}
		
		if(command.get("enrollmentfee")!=null && !command.get("enrollmentfee").isJsonNull())
		{
			enrollmentfee = command.get("enrollmentfee").getAsDouble();
		}
		
		
		return new NsChitGroup(chitName,chitDuration,chitValue,noOfSubscribers,minPercent,maxPercent,isEnabled, enrollmentfee);
	}

	public NsChitGroup(String chitName, Long chitDuration, Double chitValue, Long noOfSubscribers,Double minPercent,Double maxPercent,Boolean isEnabled, Double enrollmentfee) {
		super();
		this.chitName = chitName;
		this.chitDuration = chitDuration;
		this.chitValue = chitValue;
		this.noOfSubscribers = noOfSubscribers;
		this.minPercent = minPercent;
		this.maxPercent = maxPercent;
		this.isEnabled = isEnabled;
		this.enrollmentfee = enrollmentfee;
	}
	
	

	public Double getMinPercent() {
		return minPercent;
	}

	public void setMinPercent(Double minPercent) {
		this.minPercent = minPercent;
	}

	public Double getMaxPercent() {
		return maxPercent;
	}

	public void setMaxPercent(Double maxPercent) {
		this.maxPercent = maxPercent;
	}

	public Boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getChitName() {
		return chitName;
	}

	public void setChitName(String chitName) {
		this.chitName = chitName;
	}

	public Long getChitDuration() {
		return chitDuration;
	}

	public void setChitDuration(Long chitDuration) {
		this.chitDuration = chitDuration;
	}

	public Double getChitValue() {
		return chitValue;
	}

	public void setChitValue(Double chitValue) {
		this.chitValue = chitValue;
	}

	public Long getNoOfSubscribers() {
		return noOfSubscribers;
	}

	public void setNoOfSubscribers(Long noOfSubscribers) {
		this.noOfSubscribers = noOfSubscribers;
	}

}
