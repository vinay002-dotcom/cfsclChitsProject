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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import com.google.gson.JsonObject;

@Entity
@Table(name = "chit_subscriber_transaction")
public class ChitSubscriberTransaction extends AbstractPersistableCustom
{
	@Column(name = "chit_demand_schedule_id")
	private Long chitdemandscheduleId;
	
	@Column(name = "chit_subscriber_id")
	private Long chitsubscriberId;
	
	@Column(name = "chit_subscriber_charge_id")
	private Long chitsubscriberchargeId ;
	
	@Column(name = "amount")
	private Double amount;
	
	@Column(name = "tran_type_enum")
	private ChitTransactionEnum trantypeenum ;
	
	@Column(name = "payment_detail_id")
	private Long paymentdetailId;
	
	@Column(name = "transaction_date")
	private LocalDateTime transactionDate;
	
	@Column(name = "is_reversed")
	private Boolean isreversed;
	
	@Column(name = "is_processed")
	private Boolean isprocessed;
	
	@Column(name = "waiveoff_amount")
	private Long waiveOffAmount;
	
	protected ChitSubscriberTransaction()
	{
		
	}
	
	public ChitSubscriberTransaction(Long chitdemandscheduleId, Long chitsubscriberId, Long chitsubscriberchargeId,
			Double amount, ChitTransactionEnum trantypeenum, Long paymentdetailId, LocalDateTime transactionDate, Boolean isreversed,
			Boolean isprocessed)
	{
		this.chitdemandscheduleId = chitdemandscheduleId;
		this.chitsubscriberId = chitsubscriberId;
		this.chitsubscriberchargeId = chitsubscriberchargeId;
		this.amount = amount;
		this.trantypeenum = trantypeenum;
		this.paymentdetailId = paymentdetailId;
		this.transactionDate = transactionDate;
		this.isreversed = isreversed;
		this.isprocessed = isprocessed;
	}
	
	public ChitSubscriberTransaction(Long chitdemandscheduleId, Long chitsubscriberId, Long chitsubscriberchargeId,
			Double amount, ChitTransactionEnum trantypeenum, Long paymentdetailId, LocalDateTime transactionDate, Boolean isreversed,
			Boolean isprocessed, Long waiveOffAmount)
	{
		this.chitdemandscheduleId = chitdemandscheduleId;
		this.chitsubscriberId = chitsubscriberId;
		this.chitsubscriberchargeId = chitsubscriberchargeId;
		this.amount = amount;
		this.trantypeenum = trantypeenum;
		this.paymentdetailId = paymentdetailId;
		this.transactionDate = transactionDate;
		this.isreversed = isreversed;
		this.isprocessed = isprocessed;
		this.waiveOffAmount = waiveOffAmount;
	}
	
		
	public Map<String, Object> update(final JsonObject command)
	{
		final Map<String, Object> actualChanges = new LinkedHashMap<>(10);
		
		final String chitdemandscheduleIdParamName = "chitdemandscheduleId";
		if (command.get(chitdemandscheduleIdParamName)!=null && !command.get(chitdemandscheduleIdParamName).isJsonNull()) 
		{

			final Long newValue = command.get(chitdemandscheduleIdParamName).getAsLong();

			actualChanges.put(chitdemandscheduleIdParamName, newValue);
			this.chitdemandscheduleId = newValue;
		}
		
		final String chitsubscriberIdParamName = "chitsubscriberId";
		if (command.get(chitsubscriberIdParamName)!=null && !command.get(chitsubscriberIdParamName).isJsonNull()) 
		{

			final Long newValue = command.get(chitsubscriberIdParamName).getAsLong();

			actualChanges.put(chitsubscriberIdParamName, newValue);
			this.chitsubscriberId = newValue;
		}
		
		final String amountParamName = "amount";
		if (command.get(amountParamName)!=null && !command.get(amountParamName).isJsonNull()) 
		{

			final Double newValue = command.get(amountParamName).getAsDouble();

			actualChanges.put(chitsubscriberIdParamName, newValue);
			this.amount = newValue;
		}
		
		final String trantypeParamName = "trantype";
		if (command.get(trantypeParamName)!=null && !command.get(trantypeParamName).isJsonNull()) 
		{

			final String enumdata = command.get(trantypeParamName).getAsString();
			ChitTransactionEnum newValue = ChitTransactionEnum.valueOf(enumdata);
			actualChanges.put(chitsubscriberIdParamName, newValue);
			this.trantypeenum = newValue;
		}
		
		final String paymentdetailIdParamName = "paymentdetailId";
		if (command.get(paymentdetailIdParamName)!=null && !command.get(paymentdetailIdParamName).isJsonNull()) 
		{

			final Long newValue = command.get(paymentdetailIdParamName).getAsLong();

			actualChanges.put(paymentdetailIdParamName, newValue);
			this.paymentdetailId = newValue;
		}
		
		final String transactionDateParamName = "transactionDate";
		if (command.get(transactionDateParamName)!=null && !command.get(transactionDateParamName).isJsonNull()) 
		{
		 
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime newValue = LocalDateTime.parse(command.get(transactionDateParamName).getAsString(), formatter);
			actualChanges.put(transactionDateParamName, newValue);
			this.transactionDate = newValue;
		}
		
		final String isreversedParamName = "isreversed";
		if (command.get(isreversedParamName)!=null && !command.get(isreversedParamName).isJsonNull()) 
		{

			final Boolean newValue = command.get(isreversedParamName).getAsBoolean();

			actualChanges.put(isreversedParamName, newValue);
			this.isreversed = newValue;
		}

		final String isprocessedParamName = "isprocessed";
		if (command.get(isprocessedParamName)!=null && !command.get(isprocessedParamName).isJsonNull()) 
		{

			final Boolean newValue = command.get(isprocessedParamName).getAsBoolean();

			actualChanges.put(isprocessedParamName, newValue);
			this.isprocessed = newValue;
		}
		
		final String waiveOffAmountParamName = "waiveOffAmount";
		if (command.get(waiveOffAmountParamName)!=null && !command.get(waiveOffAmountParamName).isJsonNull()) 
		{

			final Long newValue = command.get(waiveOffAmountParamName).getAsLong();

			actualChanges.put(waiveOffAmountParamName, newValue);
			this.waiveOffAmount = newValue;
		}

		return actualChanges;
		
	}
	
	
	
	public static ChitSubscriberTransaction create(final JsonObject command) 
	{
		Long chitdemandscheduleId = null;
		Long chitsubscriberId = null;
		Double amount = null;
		ChitTransactionEnum trantype = null;
		Long paymentdetailId = null;
		LocalDateTime transactionDate  = null;
		Boolean isreversed = null;
		Boolean isprocessed = null;
		Long chitsubscriberchargeId = null;
		
		final String chitdemandscheduleIdParamName = "chitdemandscheduleId";
		if (command.get(chitdemandscheduleIdParamName)!=null && !command.get(chitdemandscheduleIdParamName).isJsonNull()) 
		{
			chitdemandscheduleId = command.get(chitdemandscheduleIdParamName).getAsLong();	
		}
		
		final String chitsubscriberchargeIdIdParamName = "chitsubscriberchargeId";
		if (command.get(chitsubscriberchargeIdIdParamName)!=null && !command.get(chitsubscriberchargeIdIdParamName).isJsonNull()) 
		{
			chitsubscriberchargeId = command.get(chitsubscriberchargeIdIdParamName).getAsLong();	
		}
		
		final String chitsubscriberIdParamName = "chitsubscriberId";
		if (command.get(chitsubscriberIdParamName)!=null && !command.get(chitsubscriberIdParamName).isJsonNull()) 
		{

			chitsubscriberId = command.get(chitsubscriberIdParamName).getAsLong();

			
		}
		
		final String amountParamName = "amount";
		if (command.get(amountParamName)!=null && !command.get(amountParamName).isJsonNull()) 
		{

			amount = command.get(amountParamName).getAsDouble();

		}
		
		final String trantypeParamName = "trantype";
		if (command.get(trantypeParamName)!=null && !command.get(trantypeParamName).isJsonNull()) 
		{

			final String enumdata = command.get(trantypeParamName).getAsString();
			trantype = ChitTransactionEnum.valueOf(enumdata);
			
		}
		
		final String paymentdetailIdParamName = "paymentdetailId";
		if (command.get(paymentdetailIdParamName)!=null && !command.get(paymentdetailIdParamName).isJsonNull()) 
		{

			paymentdetailId = command.get(paymentdetailIdParamName).getAsLong();

			
		}
		
		final String transactionDateParamName = "transactionDate";
		if (command.get(transactionDateParamName)!=null && !command.get(transactionDateParamName).isJsonNull()) 
		{
		 
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			transactionDate = LocalDateTime.parse(command.get(transactionDateParamName).getAsString(), formatter);
			
		}
		
		final String isreversedParamName = "isreversed";
		if (command.get(isreversedParamName)!=null && !command.get(isreversedParamName).isJsonNull()) 
		{
			isreversed = command.get(isreversedParamName).getAsBoolean();
		}

		final String isprocessedParamName = "isprocessed";
		if (command.get(isprocessedParamName)!=null && !command.get(isprocessedParamName).isJsonNull()) 
		{
			 isprocessed = command.get(isprocessedParamName).getAsBoolean();

		}
		
		return new ChitSubscriberTransaction(chitdemandscheduleId,chitsubscriberId,chitsubscriberchargeId,amount,trantype,paymentdetailId,transactionDate,isreversed,isprocessed);
	}

	
	
	public Long getWaiveOffAmount() {
		return waiveOffAmount;
	}

	public void setWaiveOffAmount(Long waiveOffAmount) {
		this.waiveOffAmount = waiveOffAmount;
	}

	public Long getChitdemandscheduleId() {
		return chitdemandscheduleId;
	}

	public void setChitdemandscheduleId(Long chitdemandscheduleId) {
		this.chitdemandscheduleId = chitdemandscheduleId;
	}

	public Long getChitsubscriberId() {
		return chitsubscriberId;
	}

	public void setChitsubscriberId(Long chitsubscriberId) {
		this.chitsubscriberId = chitsubscriberId;
	}

	public Long getChitsubscriberchargeId() {
		return chitsubscriberchargeId;
	}

	public void setChitsubscriberchargeId(Long chitsubscriberchargeId) {
		this.chitsubscriberchargeId = chitsubscriberchargeId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public ChitTransactionEnum getTrantypeenum() {
		return trantypeenum;
	}

	public void setTrantypeenum(ChitTransactionEnum trantypeenum) {
		this.trantypeenum = trantypeenum;
	}

	public Long getPaymentdetailId() {
		return paymentdetailId;
	}

	public void setPaymentdetailId(Long paymentdetailId) {
		this.paymentdetailId = paymentdetailId;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Boolean getIsreversed() {
		return isreversed;
	}

	public void setIsreversed(Boolean isreversed) {
		this.isreversed = isreversed;
	}

	public Boolean getIsprocessed() {
		return isprocessed;
	}

	public void setIsprocessed(Boolean isprocessed) {
		this.isprocessed = isprocessed;
	}
	
	
}
