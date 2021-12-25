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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;



@Entity
@Table(name = "chit_group_bids")
public class ChitBids extends AbstractPersistableCustom {
	
	
		
	@Column(name="chit_subscriber_id")
	private Long subscriberId;

	@Column(name="chit_cycle_id")
	private Long chitCycleId;

	@Column(name="bid_amount")
	private Double bidAmount;

	@Column(name="bid_won")
	private Boolean bidWon;

	@Column(name="bidder_participation")
	private Long bidderparticipationId;

	@Column(name = "bid_date")
	private LocalDate bidDate;
	
	@Column(name = "is_prize_money_paid")
	private Boolean isPaid;

	protected ChitBids()
	{

	}
	//	private ChitBids(final Long subscriberId,final Long chitCycleId, final Long bidAmount, final Boolean bidWon)
	//	{			
	//		this.subscriberId=subscriberId;
	//		this.chitCycleId=chitCycleId;
	//		this.bidAmount=bidAmount;
	//		this.bidWon=bidWon;
	//	}

	public ChitBids(Long subscriberId, Long chitCycleId, Double bidAmount, Boolean bidWon, Long bidderparticipationId,
			LocalDate bidDate,Boolean isPaid) {
		super();
		this.subscriberId = subscriberId;
		this.chitCycleId = chitCycleId;
		this.bidAmount = bidAmount;
		this.bidWon = bidWon;
		this.bidderparticipationId = bidderparticipationId;
		this.bidDate = bidDate;
		this.isPaid = isPaid;
	}



	public Map<String, Object> update(final JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);
//
//		final String bidAmountParamName = "bidAmount";
//		if (command.isChangeInBigDecimalParameterNamed(bidAmountParamName, this.bidAmount)) 
//		{
//			final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(bidAmountParamName);
//			actualChanges.put(bidAmountParamName, newValue);
//			this.bidAmount = newValue;
//		}

		final String bidWonParamName = "bidWon";
		if (command.isChangeInBooleanParameterNamed(bidWonParamName, this.bidWon)) 
		{
			final Boolean newValue = command.booleanPrimitiveValueOfParameterNamed(bidWonParamName);
			actualChanges.put(bidWonParamName, newValue);
			this.bidWon = newValue;
		}

		final String bidderparticipationIdParamName = "bidderparticipationId";
		if (command.isChangeInLongParameterNamed(bidderparticipationIdParamName, this.bidderparticipationId)) 
		{
			final Long newValue = command.longValueOfParameterNamed(bidderparticipationIdParamName);
			actualChanges.put(bidderparticipationIdParamName, newValue);
			this.bidderparticipationId = newValue;
		}

		final String bidDateParamName = "bidDate";
		if (command.isChangeInLocalDateParameterNamed(bidDateParamName, this.bidDate)) 
		{
			final LocalDate newValue = command.localDateValueOfParameterNamed(bidDateParamName);
			actualChanges.put(bidDateParamName, newValue);
			this.bidDate = newValue;
		}
		
		
		final String isPaidParamName = "isPaid";
		if (command.isChangeInBooleanParameterNamed(isPaidParamName, this.isPaid)) 
		{
			final Boolean newValue = command.booleanObjectValueOfParameterNamed(isPaidParamName);
			actualChanges.put(isPaidParamName, newValue);
			this.isPaid = newValue;
		}
		
		JsonElement data = command.parsedJson();
		JsonObject parsedData = data.getAsJsonObject();
		if(parsedData.get("bidAmount")!=null && !parsedData.get("bidAmount").isJsonNull())
		{
			final Double newValue = parsedData.get("bidAmount").getAsDouble();
			actualChanges.put("bidAmount", newValue);
			this.bidAmount = newValue;
		}
		return actualChanges;
	}
	
	
	public Map<String, Object> updateJson(final JsonObject command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);
		final String bidWonParamName = "bidWon";
		if (command.get(bidWonParamName)!=null) 
		{
			final Boolean newValue = command.get(bidWonParamName).getAsBoolean();
			actualChanges.put(bidWonParamName, newValue);
			this.bidWon = newValue;
		}

		final String isPaidParamName = "isPaid";
		if (command.get(isPaidParamName)!=null) 
		{
			final Boolean newValue = command.get(isPaidParamName).getAsBoolean();
			actualChanges.put(isPaidParamName, newValue);
			this.isPaid = newValue;
		}
		
		final String bidderparticipationIdParamName = "bidderparticipationId";
		if (command.get(bidderparticipationIdParamName)!=null) 
		{
			final Long newValue = command.get(bidderparticipationIdParamName).getAsLong();
			actualChanges.put(bidderparticipationIdParamName, newValue);
			this.bidderparticipationId = newValue;
		}
//		
//		JsonElement data = command.parsedJson();
//		JsonObject parsedData = data.getAsJsonObject();
//		if(parsedData.get("bidAmount")!=null && !parsedData.get("bidAmount").isJsonNull())
//		{
//			final Double newValue = parsedData.get("bidAmount").getAsDouble();
//			actualChanges.put("bidAmount", newValue);
//			this.bidAmount = newValue;
//		}
		return actualChanges;
	}
	





	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public static ChitBids create(final JsonCommand command) {
		
		Double bidAmount = null;
		Boolean isPaid = false;
		final String subscriberIdParamName = "chitSubscriberId";
		final Long subscriberId  = command.longValueOfParameterNamed(subscriberIdParamName);

		final String chitCycleIdParamName = "chitCycleId";
		final Long chitCycleId  = command.longValueOfParameterNamed(chitCycleIdParamName);

//		final String bidAmountParamName = "bidAmount";
//		final BigDecimal bidAmount  = command.bigDecimalValueOfParameterNamed(bidAmountParamName);

		final String bidWonParamName = "bidWon";
		final Boolean bidWon  = command.booleanPrimitiveValueOfParameterNamed(bidWonParamName);

		final String bidderparticipationIdParamName = "bidderparticipationId";
		final Long bidderparticipationId = command.longValueOfParameterNamed(bidderparticipationIdParamName);
		
		final String bidDateParamName = "bidDate";
		final LocalDate bidDate = command.localDateValueOfParameterNamed(bidDateParamName);
		
		JsonElement data = command.parsedJson();
		JsonObject parsedData = data.getAsJsonObject();
		if(parsedData.get("bidAmount")!=null && !parsedData.get("bidAmount").isJsonNull())
		{
			 bidAmount = parsedData.get("bidAmount").getAsDouble();
			
		}
		
		final String isPaidParamname = "isPaid";
		if(command.booleanObjectValueOfParameterNamed(isPaidParamname)!=null)
		{
			isPaid = command.booleanObjectValueOfParameterNamed(isPaidParamname);
		}
		return new ChitBids(subscriberId, chitCycleId, bidAmount, bidWon ,bidderparticipationId,bidDate,isPaid);
	}

	public Long getBidderparticipationId() {
		return bidderparticipationId;
	}

	public void setBidderparticipationId(Long bidderparticipationId) {
		this.bidderparticipationId = bidderparticipationId;
	}

	public LocalDate getBidDate() {
		return bidDate;
	}

	public void setBidDate(LocalDate bidDate) {
		this.bidDate = bidDate;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public void setChitCycleId(Long chitCycleId) {
		this.chitCycleId = chitCycleId;
	}

	public void setBidAmount(Double bidAmount) {
		this.bidAmount = bidAmount;
	}

	public void setBidWon(Boolean bidWon) {
		this.bidWon = bidWon;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public Long getChitCycleId() {
		return chitCycleId;
	}

	public Double getBidAmount() {
		return bidAmount;
	}

	public Boolean getBidWon() {
		return bidWon;
	}		

}
