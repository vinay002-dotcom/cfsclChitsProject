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
@Table(name = "chit_subscriber_charge")
public class ChitSubscriberCharge extends AbstractPersistableCustom {



	@Column(name="chit_subscriber_id")
	private Long subscriberId;

	@Column(name="chit_cycle_id")
	private Long chitCycleId;

	@Column(name="chit_charge_id")
	private Long chitChargeId;


	@Column(name="amount")
	private Long amount;

	@Column(name = "is_paid")
	private Boolean ispaid;

	@Column(name = "is_waived")
	private Boolean iswaived;


	@Column(name = "due_date")
	private LocalDate dueDate;
	
	@Column(name = "staff_id")
	private Long staffId;

	protected ChitSubscriberCharge()
	{

	}

	public ChitSubscriberCharge(Long subscriberId, Long chitCycleId, Long chitChargeId, Long amount, Boolean ispaid,
			Boolean iswaived, LocalDate dueDate,Long staffId) 
	{
		this.subscriberId = subscriberId;
		this.chitCycleId = chitCycleId;
		this.chitChargeId = chitChargeId;
		this.amount = amount;
		this.ispaid = ispaid;
		this.iswaived = iswaived;
		this.dueDate = dueDate;
		this.staffId = staffId;
	}


	public Map<String, Object> update(final JsonObject command)
	{
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

		final String amountParamName = "amount";
		if (command.get(amountParamName)!=null && !command.get(amountParamName).isJsonNull()) 
		{

			final Long newValue = command.get(amountParamName).getAsLong();

			actualChanges.put(amountParamName, newValue);
			this.amount = newValue;
		}

		final String subscriberIdParamName = "subscriberId";
		if (command.get(subscriberIdParamName)!=null && !command.get(subscriberIdParamName).isJsonNull()) 
		{

			final Long newValue = command.get(subscriberIdParamName).getAsLong();

			actualChanges.put(subscriberIdParamName, newValue);
			this.subscriberId = newValue;
		}

		final String chitCycleIdParamName = "chitCycleId";
		if (command.get(chitCycleIdParamName)!=null && !command.get(chitCycleIdParamName).isJsonNull()) 
		{

			final Long newValue = command.get(chitCycleIdParamName).getAsLong();

			actualChanges.put(chitCycleIdParamName, newValue);
			this.chitCycleId = newValue;
		}

		final String chitChargeIdParamName = "chitChargeId";
		if (command.get(chitChargeIdParamName)!=null && !command.get(chitChargeIdParamName).isJsonNull()) 
		{

			final Long newValue = command.get(chitChargeIdParamName).getAsLong();

			actualChanges.put(chitChargeIdParamName, newValue);
			this.chitChargeId = newValue;
		}

		final String ispaidParamName = "ispaid";
		if (command.get(ispaidParamName)!=null && !command.get(ispaidParamName).isJsonNull()) 
		{

			final Boolean newValue = command.get(ispaidParamName).getAsBoolean();

			actualChanges.put(ispaidParamName, newValue);
			this.ispaid = newValue;
		}

		final String iswaivedParamName = "iswaived";
		if (command.get(iswaivedParamName)!=null && !command.get(iswaivedParamName).isJsonNull()) 
		{

			final Boolean newValue = command.get(iswaivedParamName).getAsBoolean();

			actualChanges.put(iswaivedParamName, newValue);
			this.iswaived = newValue;
		}

		final String dueDateParamName = "dueDate";
		if (command.get(dueDateParamName)!=null && !command.get(dueDateParamName).isJsonNull()) 
		{
			final String tempdata = command.get(dueDateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate newValue = LocalDate.parse(tempdata, formatter);
			actualChanges.put(dueDateParamName, newValue);
			this.dueDate = newValue;
		}
		return actualChanges;
	}
	
	public static ChitSubscriberCharge create(final JsonObject command,Long staffId) {

		Long amount = null;
		Long subscriberId = null;
		Long chitCycleId = null;
		Long chitChargeId = null;
		Boolean ispaid = null;
		Boolean iswaived = null;
		LocalDate dueDate = null;
		
		
		String amountParamName = "amount";
		if (command.get(amountParamName)!=null && !command.get(amountParamName).isJsonNull()) 
		{

			amount = command.get(amountParamName).getAsLong();

		}

		final String subscriberIdParamName = "subscriberId";
		if (command.get(subscriberIdParamName)!=null && !command.get(subscriberIdParamName).isJsonNull()) 
		{
			subscriberId = command.get(subscriberIdParamName).getAsLong();
		}
		

		final String chitCycleIdParamName = "chitCycleId";
		if (command.get(chitCycleIdParamName)!=null && !command.get(chitCycleIdParamName).isJsonNull()) 
		{

			chitCycleId = command.get(chitCycleIdParamName).getAsLong();


		}

		final String chitChargeIdParamName = "chitChargeId";
		if (command.get(chitChargeIdParamName)!=null && !command.get(chitChargeIdParamName).isJsonNull()) 
		{

			chitChargeId = command.get(chitChargeIdParamName).getAsLong();

		}

		final String ispaidParamName = "ispaid";
		if (command.get(ispaidParamName)!=null && !command.get(ispaidParamName).isJsonNull()) 
		{

			ispaid = command.get(ispaidParamName).getAsBoolean();

		}

		final String iswaivedParamName = "iswaived";
		if (command.get(iswaivedParamName)!=null && !command.get(iswaivedParamName).isJsonNull()) 
		{

			iswaived = command.get(iswaivedParamName).getAsBoolean();


		}

		final String dueDateParamName = "dueDate";
		if (command.get(dueDateParamName)!=null && !command.get(dueDateParamName).isJsonNull()) 
		{
			final String tempdata = command.get(dueDateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			dueDate = LocalDate.parse(tempdata, formatter);

		}

		return new ChitSubscriberCharge(subscriberId, chitCycleId, chitChargeId, amount ,ispaid,iswaived,dueDate,staffId);
	}



}
