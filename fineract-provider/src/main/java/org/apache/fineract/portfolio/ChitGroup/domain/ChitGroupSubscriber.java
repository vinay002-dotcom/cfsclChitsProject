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
@Table(name = "chit_group_subscriber")

public class ChitGroupSubscriber  extends AbstractPersistableCustom 
{
	@Column(name = "chit_id")
	private Long chitId;
	
	
	@Column(name = "client_id")
	private Long clientId;
	
	@Column(name = "chit_number")
	private Long chitNumber;
	
	@Column(name = "prized_subscriber")
	private Boolean prizedsubscriber;
	
	
	@Column(name = "prized_cycle")
	private Long prizedcycle;
	
	@Column(name = "is_active")
	private Boolean isactive;
	
	@Column(name = "bid_advance")
	private Boolean bidAdvance;
	
	@Column(name = "toBePaidAmount")
	private Double toBePaidAmount;
	
	@Column(name = "is_processed")
	private Boolean isProcessed;
	
	@Column(name = "status_id")
	private Integer status;
	
	@Column(name = "sub_status_type")
	private subscriberStatusEnum subStatusEnum ;
	

	public Map<String, Object> update(final JsonObject command) {
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);
		System.out.println("001");

		final String prizedsubscriberParamName = "prizedsubscriber";
		if (command.get("prizedsubscriber")!=null && !command.get("prizedsubscriber").isJsonNull()) 
		{
			final Boolean newValue = command.get("prizedsubscriber").getAsBoolean();
			actualChanges.put(prizedsubscriberParamName, newValue);
			this.prizedsubscriber = newValue;
		}

		final String prizedcycleParamName = "prizedcycle";
		if (command.get("prizedcycle")!=null && !command.get("prizedcycle").isJsonNull()) 
		{
			final Long newValue = command.get(prizedcycleParamName).getAsLong();
			actualChanges.put(prizedcycleParamName, newValue);
			this.prizedcycle = newValue;
		}
		
		final String isActiveParamName = "isactive";
		if (command.get("isactive")!=null && !command.get("isactive").isJsonNull()) 
		{
			final Boolean newValue = command.get(isActiveParamName).getAsBoolean();
			actualChanges.put(isActiveParamName, newValue);
			this.isactive = newValue;
		}
		
		final String bidAdvanceParamName = "bidAdvance";
		if (command.get("bidAdvance")!=null && !command.get("bidAdvance").isJsonNull()) 
		{
			final Boolean newValue = command.get(bidAdvanceParamName).getAsBoolean();
			actualChanges.put(bidAdvanceParamName, newValue);
			this.bidAdvance = newValue;
		}
		
		final String toBePaidAmountParamName = "toBePaidAmount";
		if (command.get("toBePaidAmount")!=null && !command.get("toBePaidAmount").isJsonNull()) 
		{
			final Double newValue = command.get(toBePaidAmountParamName).getAsDouble();
			actualChanges.put(toBePaidAmountParamName, newValue);
			this.toBePaidAmount = newValue;
		}
		
		final String isProcessedParamName = "isProcessed";
		if (command.get("isProcessed")!=null && !command.get("isProcessed").isJsonNull()) 
		{
			final Boolean newValue = command.get(isProcessedParamName).getAsBoolean();
			actualChanges.put(isProcessedParamName, newValue);
			this.isProcessed = newValue;
		}

		final String statusParamName = "status";
		if (command.get("status")!=null && !command.get("status").isJsonNull()) 
		{
			final Integer newValue = command.get(statusParamName).getAsInt();
			actualChanges.put(statusParamName, newValue);
			this.status = newValue;
		}
		
		final String subStatusEnumParamName = "subStatusEnum";
		if (command.get("subStatusEnum")!=null && !command.get("subStatusEnum").isJsonNull()) 
		{
			Integer enumdata = command.get(subStatusEnumParamName).getAsInt(); 
			subscriberStatusEnum newValue = null;
			if(enumdata == 1) {
				 newValue = subscriberStatusEnum.BID_ADVANCE;
			} else if(enumdata == 2) {
				 newValue = subscriberStatusEnum.TERMINATE;
			} else if(enumdata == 3) {
				 newValue = subscriberStatusEnum.FORECLOSURE;
			}
			
			actualChanges.put(subStatusEnumParamName, newValue);
			this.subStatusEnum = newValue;
		}
		
		return actualChanges;
	}
	
	protected ChitGroupSubscriber()
	{
		
	}

	private ChitGroupSubscriber(Long chitId, Long clientId, Long chitNumber, Boolean prizedsubscriber,
			Long prizedcycle, Boolean isactive) {
		super();
	
		this.chitId = chitId;
		this.clientId = clientId;
		this.chitNumber = chitNumber;
		this.prizedsubscriber = prizedsubscriber;
		this.prizedcycle = prizedcycle;
		this.isactive = isactive;
	}
	
	

	@SuppressWarnings("unused")
	private ChitGroupSubscriber(Long chitId, Long clientId, Long chitNumber, Boolean prizedsubscriber,
			Long prizedcycle, Boolean isactive,  Boolean bidAdvance, Double toBePaidAmount, Boolean isProcessed) {
		super();
	
		this.chitId = chitId;
		this.clientId = clientId;
		this.chitNumber = chitNumber;
		this.prizedsubscriber = prizedsubscriber;
		this.prizedcycle = prizedcycle;
		this.isactive = isactive;
		this.bidAdvance = bidAdvance;
		this.toBePaidAmount = toBePaidAmount;
		this.isProcessed= isProcessed;
	}
	

	public Long getChitId() {
		return chitId;
	}

	public void setChitId(Long chitId) {
		this.chitId = chitId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getChitNumber() {
		return chitNumber;
	}

	public void setChitNumber(Long chitNumber) {
		this.chitNumber = chitNumber;
	}

	public Boolean getPrizedsubscriber() {
		return prizedsubscriber;
	}

	public void setPrizedsubscriber(Boolean prizedsubscriber) {
		this.prizedsubscriber = prizedsubscriber;
	}

	public Long getPrizedcycle() {
		return prizedcycle;
	}

	public void setPrizedcycle(Long prizedcycle) {
		this.prizedcycle = prizedcycle;
	}

	public Boolean getIsactive() {
		return isactive;
	}

	public void setIsactive(Boolean isactive) {
		this.isactive = isactive;
	}
	
	public Boolean getBidAdvance() {
		return bidAdvance;
	}

	public void setBidAdvance(Boolean bidAdvance) {
		this.bidAdvance = bidAdvance;
	}

	public Double getToBePaidAmount() {
		return toBePaidAmount;
	}

	public void setToBePaidAmount(Double toBePaidAmount) {
		this.toBePaidAmount = toBePaidAmount;
	}
	
	public Boolean getIsProcessed() {
		return isProcessed;
	}

	public void setIsProcessed(Boolean isProcessed) {
		this.isProcessed = isProcessed;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public subscriberStatusEnum getSubStatusEnum() {
		return subStatusEnum;
	}

	public void setSubStatusEnum(subscriberStatusEnum subStatusEnum) {
		this.subStatusEnum = subStatusEnum;
	}
	
	public static ChitGroupSubscriber create(final JsonObject command,final Long chitId) {
		

		Boolean prizedsubscriber = null;
		Long clientid = null;
		Long chitnumber = null;
		Long prizedcycle = null;
		Boolean isactive = null;
		if (command.get("clientid")!=null && !command.get("clientid").isJsonNull())
		{
			clientid  = command.get("clientid").getAsLong();
		
		}
		if (command.get("chitnumber")!=null && !command.get("chitnumber").isJsonNull())
		{
			chitnumber  = command.get("chitnumber").getAsLong();
		}
		
		if (command.get("prizedcycle")!=null && !command.get("prizedcycle").isJsonNull())
		{
			 prizedcycle  = command.get("prizedcycle").getAsLong();
		}
		
		if (command.get("prizedsubscriber")!=null && !command.get("prizedsubscriber").isJsonNull())
		{
			 prizedsubscriber  = command.get("prizedsubscriber").getAsBoolean();
		}
		else
		{
			prizedsubscriber = false;
		}
		
		if (command.get("isactive")!=null && !command.get("isactive").isJsonNull())
		{
			isactive  = command.get("isactive").getAsBoolean();
		}
		else
		{
			isactive = true;
		}
		
		
		return new ChitGroupSubscriber(chitId, clientid, chitnumber,prizedsubscriber, prizedcycle ,isactive);
	}

	
	

}
