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
package org.apache.fineract.portfolio.PaymentStatus.domain;

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
@Table(name = "payment_status")
public class PaymentStatus extends AbstractPersistableCustom {

	@Column(name = "payment_type")
	private Long paymentType;
	
	@Column(name="status")
	private Long status;
	
	@Column(name ="date")
	private LocalDate date;
	
	@Column(name="tran_id")
	private Long tranId;
	
	@Column(name="officeId")
	private Long officeId;
	
	
	protected PaymentStatus() {
		
	}
	
	public PaymentStatus(Long paymentType, Long status, LocalDate date, Long tranId, Long officeId) {
		super();
		this.paymentType = paymentType;
		this.status = status;
		this.date = date;
		this.tranId = tranId;
		this.officeId = officeId;
	}
	
	public static PaymentStatus create(JsonObject command)
	{
		Long paymentType = 0l;
		Long status = 0l;
		Long tranId = 0l;
		Long officeId = 0l;
		LocalDate date = null;
		if (command.get("paymentType")!=null && !command.get("paymentType").isJsonNull())
		{
			paymentType  = command.get("paymentType").getAsLong();		
		}
		if (command.get("status")!=null && !command.get("status").isJsonNull())
		{
			status  = command.get("status").getAsLong();		
		}
		if (command.get("date")!=null && !command.get("date").isJsonNull())
		{
			String temp = command.get("date").getAsString();
			date  = LocalDate.parse(temp);
		}
		if (command.get("tranId")!=null && !command.get("tranId").isJsonNull())
		{
			tranId  = command.get("tranId").getAsLong();		
		}
		if (command.get("officeId")!=null && !command.get("officeId").isJsonNull())
		{
			officeId  = command.get("officeId").getAsLong();		
		}
		
		return new PaymentStatus(paymentType,status,date,tranId,officeId);
		
	}
	
	public Map<String, Object> update(JsonObject command)
	{
		Map<String, Object> actualChanges = new LinkedHashMap<>(7);
		final String paymentTypeParamName = "paymentType";
		if (command.get(paymentTypeParamName)!=null) 
		{
			final Long newValue = command.get(paymentTypeParamName).getAsLong();
			actualChanges.put(paymentTypeParamName, newValue);
			this.paymentType = newValue;
		}
		final String statusParamName = "status";
		if (command.get(statusParamName)!=null) 
		{
			final Long newValue = command.get(statusParamName).getAsLong();
			actualChanges.put(statusParamName, newValue);
			this.status = newValue;
		}
		final String dateParamName = "date";
		if(command.get(dateParamName)!=null)
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate newValue = LocalDate.parse(command.get(dateParamName).getAsString(), formatter);
			actualChanges.put(dateParamName, formatter);
			this.date = newValue;
		}
		final String tranIdParamName = "tranId";
		if (command.get(tranIdParamName)!=null) 
		{
			final Long newValue = command.get(tranIdParamName).getAsLong();
			actualChanges.put(tranIdParamName, newValue);
			this.tranId = newValue;
		}
		final String officeIdParamName = "officeId";
		if (command.get(officeIdParamName)!=null) 
		{
			final Long newValue = command.get(officeIdParamName).getAsLong();
			actualChanges.put(officeIdParamName, newValue);
			this.officeId = newValue;
		}
		
		return actualChanges;
	}
	

	public Long getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Long paymentType) {
		this.paymentType = paymentType;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Long getTranId() {
		return tranId;
	}

	public void setTranId(Long tranId) {
		this.tranId = tranId;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}
	
	
}
