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
import java.time.LocalDate;

public class ChitDemandScheduleData implements Serializable 
{
	private Long id;
	private Long ChitSubscriberChargeId;
	private Long StaffId;
	private LocalDate demandDate;
	private Double installmentAmount;
	private Double dueAmount;
	private Double overdueAmount;
	private Double penaltyAmount;
	private Double collectedAmount;
	private Boolean isCalculated;
	private Long chitId;
	
	private ChitDemandScheduleData(Long id, Long ChitSubscriberChargeId, Long StaffId, LocalDate demandDate,
			Double installmentAmount, Double dueAmount, Double overdueAmount, Double penaltyAmount,
			Double collectedAmount,Boolean isCalculated, Long chitId) {

		this.id = id;
		this.ChitSubscriberChargeId = ChitSubscriberChargeId;
		this.StaffId = StaffId;
		this.demandDate = demandDate;
		this.installmentAmount = installmentAmount;
		this.dueAmount = dueAmount;
		this.overdueAmount = overdueAmount;
		this.penaltyAmount = penaltyAmount;
		this.collectedAmount = collectedAmount;
		this.isCalculated = isCalculated;
		this.chitId = chitId;
	}
	private ChitDemandScheduleData(Double collectedAmount, Double penaltyAmount) {
		this.collectedAmount = collectedAmount;
		this.penaltyAmount = penaltyAmount;
	}
	
	public static ChitDemandScheduleData instance(Long id, Long ChitSubscriberChargeId, Long StaffId, LocalDate demandDate,
			Double installmentAmount, Double dueAmount, Double overdueAmount, Double penaltyAmount,
			Double collectedAmount,Boolean isCalculated,Long chitId)
	{
		return new ChitDemandScheduleData(id,ChitSubscriberChargeId,StaffId,demandDate,installmentAmount,dueAmount,overdueAmount,penaltyAmount,collectedAmount,isCalculated,chitId);
	}
	
	public static ChitDemandScheduleData forClosureInstance(Double collectedAmount, Double penaltyAmount) {
		return new ChitDemandScheduleData(collectedAmount, penaltyAmount);
	}

	public Long getChitId() {
		return chitId;
	}

	public void setChitId(Long chitId) {
		this.chitId = chitId;
	}

	public Boolean getIsCalculated() {
		return isCalculated;
	}

	public void setIsCalculated(Boolean isCalculated) {
		this.isCalculated = isCalculated;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChitSubscriberChargeId() {
		return ChitSubscriberChargeId;
	}

	public void setChitSubscriberChargeId(Long ChitSubscriberChargeId) {
		this.ChitSubscriberChargeId = ChitSubscriberChargeId;
	}

	public Long getStaffId() {
		return StaffId;
	}

	public void setStaffId(Long StaffId) {
		this.StaffId = StaffId;
	}

	public LocalDate getDemandDate() {
		return demandDate;
	}

	public void setDemandDate(LocalDate demandDate) {
		this.demandDate = demandDate;
	}

	public Double getInstallmentAmount() {
		return installmentAmount;
	}

	public void setInstallmentAmount(Double installmentAmount) {
		this.installmentAmount = installmentAmount;
	}

	public Double getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(Double dueAmount) {
		this.dueAmount = dueAmount;
	}

	public Double getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(Double overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

	public Double getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(Double penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	public Double getCollectedAmount() {
		return collectedAmount;
	}

	public void setCollectedAmount(Double collectedAmount) {
		this.collectedAmount = collectedAmount;
	}

	public Long getId() {
		return id;
	}
	
	
	
}
