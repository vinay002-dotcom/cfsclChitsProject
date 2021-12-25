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
package org.apache.fineract.portfolio.ChitGroup.data;

import java.io.Serializable;

import org.apache.fineract.portfolio.ChitGroup.domain.subscriberStatusEnum;


@SuppressWarnings("unused")
public class ChitGroupSubscriberData implements Serializable
{
	private final Long id;
	private final Long chitId;
	private final Long clientId;
	private final Integer chitNumber;
	private final String name;
	private final String adhar;
	private final String mobileno;
	private transient Integer rowIndex;
	private final Boolean prizedsubscriber;
	private final Long prizedcycle;
	private final Boolean isActive;
	
	private final Boolean bidAdvance;
	private final Double toBePaidAmount;
	private final Boolean isProcessed;
	
	private Integer status;
	private subscriberStatusEnum subStatusEnum ;
	
	public static ChitGroupSubscriberData lookup(final Long id) {
		return new ChitGroupSubscriberData(id, null, null, null, null, null, null, null, null, null);
	}
	
	public static ChitGroupSubscriberData newlookup(final Long id) {
		return new ChitGroupSubscriberData(id, null, null, null, null, null, null, null, null, null,null, null, null);
	}
	
	
	public Boolean getIsActive() {
		return isActive;
	}

	private ChitGroupSubscriberData(Long id, Long chitId, Long clientId, Integer chitNumber, String name, String adhar, String mobileno,Boolean prizedsubscriber,Long prizedcycle,Boolean isActive) {
		this.id = id;
		this.chitId = chitId;
		this.clientId = clientId;
		this.chitNumber = chitNumber;
		this.name = name;
		this.adhar = adhar;
		this.mobileno = mobileno;
		this.prizedsubscriber = prizedsubscriber;
		this.prizedcycle = prizedcycle;
		this.isActive = isActive;
		this.bidAdvance = null;
		this.toBePaidAmount = null;
		this.isProcessed = null;
	}
	
	private ChitGroupSubscriberData(Long id, Long chitId, Long clientId, Integer chitNumber, String name, String adhar, 
			String mobileno,Boolean prizedsubscriber,Long prizedcycle,Boolean isActive, Boolean bidAdvance,Double toBePaidAmount,Boolean isProcessed) {
		this.id = id;
		this.chitId = chitId;
		this.clientId = clientId;
		this.chitNumber = chitNumber;
		this.name = name;
		this.adhar = adhar;
		this.mobileno = mobileno;
		this.prizedsubscriber = prizedsubscriber;
		this.prizedcycle = prizedcycle;
		this.isActive = isActive;
		this.bidAdvance = bidAdvance;
		this.toBePaidAmount = toBePaidAmount;
		this.isProcessed = isProcessed;
	}
	public Boolean getIsProcessed() {
		return isProcessed;
	}

	public Boolean getBidAdvance() {
		return bidAdvance;
	}

	public Double getToBePaidAmount() {
		return toBePaidAmount;
	}

	public Integer getRowIndex() {
        return rowIndex;
    }

	public Long getId() {
		return id;
	}
	
	public static ChitGroupSubscriberData instance(Long id, Long chitId, Long clientId, Integer chitNumber, String name,
			String adhar, String mobileno,Boolean prizedsubscriber,Long prizedcycle,Boolean isActive){
		return new ChitGroupSubscriberData(id, chitId, clientId, chitNumber, name, adhar, mobileno,prizedsubscriber,
				prizedcycle,isActive);
	}
	
	public static ChitGroupSubscriberData newinstance(Long id, Long chitId, Long clientId, Integer chitNumber, String name,
			String adhar, String mobileno,Boolean prizedsubscriber,Long prizedcycle,Boolean isActive,
			Boolean bidAdvance,Double toBePaidAmount, Boolean isProcessed){
		return new ChitGroupSubscriberData(id, chitId, clientId, chitNumber, name, adhar, mobileno,prizedsubscriber,
				prizedcycle,isActive, bidAdvance,toBePaidAmount, isProcessed);
	}

	public Boolean getPrizedsubscriber() {
		return prizedsubscriber;
	}

	public Long getPrizedcycle() {
		return prizedcycle;
	}

	public Long getClientId() {
		return clientId;
	}

	public Long getChitId() {
		return chitId;
	}

	public Integer getChitNumber() {
		return chitNumber;
	}

	public String getName() {
		return name;
	}

	public String getAdhar() {
		return adhar;
	}

	public String getMobileno() {
		return mobileno;
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

}
