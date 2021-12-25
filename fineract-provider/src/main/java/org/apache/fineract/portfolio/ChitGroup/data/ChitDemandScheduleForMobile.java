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

public class ChitDemandScheduleForMobile implements Serializable 
{
	private Double installment;
	private Double dueAmount;
	private Double overDueAmount;
	private Double penaltyAmount;
	private Double totalAmount;
	private String mobileNo;
	private String firstName;
	private String adhar;
	private Long chitNumber ;
	private String chitGroupName;
	private Long ChitDemandId ;
	private Long ClientId ;
	private Long chitGroupId;
	private Long chitGroupSubscriberId;
	private Long chitSubsChargeId;
	
	private ChitDemandScheduleForMobile(Double installment, Double dueAmount, Double overDueAmount, Double penaltyAmount,Double totalAmount,
			String mobileNo, String firstName, String adhar, Long chitNumber, String chitGroupName,Long ChitDemandId,Long ClientId,Long chitGroupId,Long chitGroupSubscriberId,
			Long chitSubsChargeId) {
		super();
		this.installment = installment;
		this.dueAmount = dueAmount;
		this.overDueAmount = overDueAmount;
		this.penaltyAmount = penaltyAmount;
		this.mobileNo = mobileNo;
		this.firstName = firstName;
		this.adhar = adhar;
		this.chitNumber = chitNumber;
		this.chitGroupName = chitGroupName;
		this.totalAmount = totalAmount;
		this.ChitDemandId = ChitDemandId;
		this.ClientId = ClientId;
		this.chitGroupId = chitGroupId;
		this.chitGroupSubscriberId = chitGroupSubscriberId;
		this.chitSubsChargeId = chitSubsChargeId;
	}
	
	public static ChitDemandScheduleForMobile instance(Double installment, Double dueAmount, Double overDueAmount, Double penaltyAmount,Double totalAmount,
			String mobileNo, String firstName, String adhar, Long chitNumber, String chitGroupName,Long ChitDemandId,Long ClientId,Long chitGroupId,Long chitGroupSubscriberId,Long chitSubsChargeId)
	{
		return new ChitDemandScheduleForMobile(installment,dueAmount,overDueAmount,penaltyAmount,totalAmount,mobileNo,firstName,adhar,chitNumber,chitGroupName,ChitDemandId,ClientId,chitGroupId,chitGroupSubscriberId,chitSubsChargeId);
	}

	public Long getChitSubsChargeId() {
		return chitSubsChargeId;
	}

	public void setChitSubsChargeId(Long chitSubsChargeId) {
		this.chitSubsChargeId = chitSubsChargeId;
	}

	public Double getInstallment() {
		return installment;
	}

	public void setInstallment(Double installment) {
		this.installment = installment;
	}

	public Double getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(Double dueAmount) {
		this.dueAmount = dueAmount;
	}

	public Double getOverDueAmount() {
		return overDueAmount;
	}

	public void setOverDueAmount(Double overDueAmount) {
		this.overDueAmount = overDueAmount;
	}

	public Double getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(Double penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getAdhar() {
		return adhar;
	}

	public void setAdhar(String adhar) {
		this.adhar = adhar;
	}

	public Long getChitNumber() {
		return chitNumber;
	}

	public void setChitNumber(Long chitNumber) {
		this.chitNumber = chitNumber;
	}

	public String getChitGroupName() {
		return chitGroupName;
	}

	public void setChitGroupName(String chitGroupName) {
		this.chitGroupName = chitGroupName;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getChitDemandId() {
		return ChitDemandId;
	}

	public void setChitDemandId(Long ChitDemandId) {
		this.ChitDemandId = ChitDemandId;
	}

	public Long getClientId() {
		return ClientId;
	}

	public void setClientId(Long ClientId) {
		this.ClientId = ClientId;
	}

	public Long getChitGroupId() {
		return chitGroupId;
	}

	public void setChitGroupId(Long chitGroupId) {
		this.chitGroupId = chitGroupId;
	}

	public Long getChitGroupSubscriberId() {
		return chitGroupSubscriberId;
	}

	public void setChitGroupSubscriberId(Long chitGroupSubscriberId) {
		this.chitGroupSubscriberId = chitGroupSubscriberId;
	}
	
	
	
	
	
}
