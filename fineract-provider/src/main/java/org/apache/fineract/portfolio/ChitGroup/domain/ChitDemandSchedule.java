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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import com.google.gson.JsonObject;

@Entity
@Table(name = "chit_demand_schedule")
public class ChitDemandSchedule  extends AbstractPersistableCustom 
{
	@Column(name = "chit_subscriber_charge_id")
	private Long chitsubscriberchargeId;

	@Column(name = "staff_id")
	private Long staffId;

	@Column(name = "demand_date")
	private LocalDate demandDate;

	@Column(name = "installment_amount")
	private Double installmentAmount;

	@Column(name = "due_amount")
	private Double dueAmount;

	@Column(name = "overdue_amount")
	private Double overdueAmount;

	@Column(name = "penalty_amount")
	private Double penaltyAmount;

	@Column(name = "collected_amount")
	private Double collectedAmount;
	
	@Column(name = "is_calculated")
	private Boolean isCalculated;
	
	@Column(name = "chit_id")
	private Long chitid;

	protected ChitDemandSchedule()
	{

	}

	public ChitDemandSchedule(Long chitsubscriberchargeId, Long staffId, LocalDate demandDate, Double installmentAmount,
			Double dueAmount, Double overdueAmount, Double penaltyAmount, Double collectedAmount,Boolean isCalculated,Long chitid) {
		super();
		this.chitsubscriberchargeId = chitsubscriberchargeId;
		this.staffId = staffId;
		this.demandDate = demandDate;
		this.installmentAmount = installmentAmount;
		this.dueAmount = dueAmount;
		this.overdueAmount = overdueAmount;
		this.penaltyAmount = penaltyAmount;
		this.collectedAmount = collectedAmount;
		this.isCalculated = isCalculated;
		this.chitid = chitid;
	}

	public Map<String, Object> update(final JsonObject command)
	{
		final Map<String, Object> actualChanges = new LinkedHashMap<>(9);
		
		final String isCalculatedParamName = "isCalculated";
		if (command.get(isCalculatedParamName)!=null && !command.get(isCalculatedParamName).isJsonNull()) 
		{

			final Boolean newValue = command.get(isCalculatedParamName).getAsBoolean();
			actualChanges.put(isCalculatedParamName, newValue);
			this.isCalculated = newValue;
		}

		final String installmentAmountParamName = "installmentAmount";
		if (command.get(installmentAmountParamName)!=null && !command.get(installmentAmountParamName).isJsonNull()) 
		{

			final Double newValue = command.get(installmentAmountParamName).getAsDouble();
			actualChanges.put(installmentAmountParamName, newValue);
			this.installmentAmount = newValue;
		}

		final String dueAmountParamName = "dueAmount";
		if (command.get(dueAmountParamName)!=null && !command.get(dueAmountParamName).isJsonNull()) 
		{

			final Double newValue = command.get(dueAmountParamName).getAsDouble();
			actualChanges.put(dueAmountParamName, newValue);
			this.dueAmount = newValue;
		}

		final String overdueAmountParamName = "overdueAmount";
		if (command.get(overdueAmountParamName)!=null && !command.get(overdueAmountParamName).isJsonNull()) 
		{

			final Double newValue = command.get(overdueAmountParamName).getAsDouble();
			actualChanges.put(overdueAmountParamName, newValue);
			this.overdueAmount = newValue;
		}

		final String penaltyAmountParamName = "penaltyAmount";
		if (command.get(penaltyAmountParamName)!=null && !command.get(penaltyAmountParamName).isJsonNull()) 
		{

			final Double newValue = command.get(penaltyAmountParamName).getAsDouble();
			actualChanges.put(penaltyAmountParamName, newValue);
			this.penaltyAmount = newValue;
		}

		final String collectedAmountParamName = "collectedAmount";
		if (command.get(collectedAmountParamName)!=null && !command.get(collectedAmountParamName).isJsonNull()) 
		{

			final Double newValue = command.get(collectedAmountParamName).getAsDouble();
			actualChanges.put(collectedAmountParamName, newValue);
			this.collectedAmount = newValue;
		}

		final String staffIdParamName = "staffId";
		if (command.get(staffIdParamName)!=null && !command.get(staffIdParamName).isJsonNull()) 
		{

			final Long newValue = command.get(staffIdParamName).getAsLong();

			actualChanges.put(staffIdParamName, newValue);
			this.staffId = newValue;
		}



		final String demandDateParamName = "demandDate";
		if (command.get(demandDateParamName)!=null && !command.get(demandDateParamName).isJsonNull()) 
		{
			final String tempdata = command.get(demandDateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate newValue = LocalDate.parse(tempdata, formatter);
			actualChanges.put(demandDateParamName, newValue);
			this.demandDate = newValue;
		}
		return actualChanges;
	}

	public static ChitDemandSchedule create(JsonObject command)
	{
		Double installmentAmount = null;
		Double dueAmount = null;
		Double overdueAmount = null;
		Double penaltyAmount = null;
		Double collectedAmount = null;
		Long staffId = null;
		LocalDate demandDate = null;
		Long chitsubscriberchargeId = null;
		Boolean isCalculated = null;
		Long chitId = null;
		
		final String isCalculatedParamName = "isCalculated";
		if (command.get(isCalculatedParamName)!=null && !command.get(isCalculatedParamName).isJsonNull()) 
		{

			isCalculated = command.get(isCalculatedParamName).getAsBoolean();

		}
		else
		{
			isCalculated = false;
		}
		
		final String installmentAmountParamName = "installmentAmount";
		if (command.get(installmentAmountParamName)!=null && !command.get(installmentAmountParamName).isJsonNull()) 
		{

			installmentAmount = command.get(installmentAmountParamName).getAsDouble();

		}

		final String dueAmountParamName = "dueAmount";
		if (command.get(dueAmountParamName)!=null && !command.get(dueAmountParamName).isJsonNull()) 
		{

			dueAmount = command.get(dueAmountParamName).getAsDouble();

		}

		final String overdueAmountParamName = "overdueAmount";
		if (command.get(overdueAmountParamName)!=null && !command.get(overdueAmountParamName).isJsonNull()) 
		{

			overdueAmount = command.get(overdueAmountParamName).getAsDouble();

		}

		final String penaltyAmountParamName = "penaltyAmount";
		if (command.get(penaltyAmountParamName)!=null && !command.get(penaltyAmountParamName).isJsonNull()) 
		{

			penaltyAmount = command.get(penaltyAmountParamName).getAsDouble();

		}

		final String collectedAmountParamName = "collectedAmount";
		if (command.get(collectedAmountParamName)!=null && !command.get(collectedAmountParamName).isJsonNull()) 
		{

			collectedAmount = command.get(collectedAmountParamName).getAsDouble();

		}

		final String staffIdParamName = "staffId";
		if (command.get(staffIdParamName)!=null && !command.get(staffIdParamName).isJsonNull()) 
		{

			staffId = command.get(staffIdParamName).getAsLong();

		}
		
		final String chitIDParamName = "chitID";
		if (command.get(chitIDParamName)!=null && !command.get(chitIDParamName).isJsonNull()) 
		{

			chitId = command.get(chitIDParamName).getAsLong();

		}
		
		final String chitsubscriberchargeIdParamName = "chitsubscriberchargeId";
		if (command.get(chitsubscriberchargeIdParamName)!=null && !command.get(chitsubscriberchargeIdParamName).isJsonNull()) 
		{

			chitsubscriberchargeId = command.get(chitsubscriberchargeIdParamName).getAsLong();
			
			
		}



		final String demandDateParamName = "demandDate";
		if (command.get(demandDateParamName)!=null && !command.get(demandDateParamName).isJsonNull()) 
		{
			final String tempdata = command.get(demandDateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			demandDate = LocalDate.parse(tempdata, formatter);

		}
		
		return new ChitDemandSchedule(chitsubscriberchargeId,staffId,demandDate,installmentAmount,dueAmount,overdueAmount,penaltyAmount,collectedAmount,isCalculated,chitId);
	}

	public Long getChitsubscriberchargeId() {
		return chitsubscriberchargeId;
	}

	public void setChitsubscriberchargeId(Long chitsubscriberchargeId) {
		this.chitsubscriberchargeId = chitsubscriberchargeId;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
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
	
	

}
