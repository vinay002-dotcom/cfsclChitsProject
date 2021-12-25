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
@Table(name = "chit_group_cycle")
public class ChitCycle extends AbstractPersistableCustom {
	
	@Column(name="chit_id")
	private Long chitid;
	
	@Column(name="cycle_number")
	private Long cyclenumber;
	
	@Column(name="auction_date")
	private LocalDateTime auctiondate;
	
	@Column(name="start_date")
	private LocalDate startdate;
	
	
	@Column(name="end_date")
	private LocalDate endDate;
	
	@Column(name = "dividend")
	private Long  dividend;
	
	
	@Column(name = "bid_minutes_filing_due_date")
	private LocalDate  bidminutesfilingduedate;
	
	@Column(name = "subscriptionPayble")
	private Double subscriptionPayble;
	
	@Column(name = "gstAmount")
	private Double gstAmount;
	
	@Column(name = "foremanCommissionAmount")
	private Double foremanCommissionAmount;
	
	@Column(name = "verificationAmount")
	private Double verificationAmount;
	
	protected ChitCycle()
	{
		
	}

//	private ChitCycle(final Long subscriberId,final Long chitCycleId, final Long bidAmount, final Boolean bidWon)
//	{			
//		this.subscriberId=subscriberId;
//		this.chitCycleId=chitCycleId;
//		this.bidAmount=bidAmount;
//		this.bidWon=bidWon;
//	}
		
	private ChitCycle(Long chitid, Long cyclenumber, LocalDateTime auctiondate, LocalDate startdate, LocalDate endDate,
			Long dividend, LocalDate bidminutesfilingduedate,Double subscriptionPayble,Double gstAmount,Double foremanCommissionAmount,Double verificationAmount) {
		super();
		this.chitid = chitid;
		this.cyclenumber = cyclenumber;
		this.auctiondate = auctiondate;
		this.startdate = startdate;
		this.endDate = endDate;
		this.dividend = dividend;
		this.bidminutesfilingduedate = bidminutesfilingduedate;
		this.subscriptionPayble = subscriptionPayble;
		this.gstAmount = gstAmount;
		this.foremanCommissionAmount = foremanCommissionAmount;
		this.verificationAmount = verificationAmount;
	}

	public Map<String, Object> update(final JsonObject command) {
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

		final String auctiondateParamName = "auctiondate";
		if (command.get(auctiondateParamName)!=null && !command.get(auctiondateParamName).isJsonNull()) 
		{
		 
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime newValue = LocalDateTime.parse(command.get(auctiondateParamName).getAsString(), formatter);
			actualChanges.put(auctiondateParamName, newValue);
			this.auctiondate = newValue;
		}
		
		
		final String SubscriptionPayableParamName = "subscriptionPayble";
		if (command.get(SubscriptionPayableParamName)!=null && !command.get(SubscriptionPayableParamName).isJsonNull()) 
		{
			Double  newValue= command.get(SubscriptionPayableParamName).getAsDouble();
			actualChanges.put(SubscriptionPayableParamName, newValue);
			this.subscriptionPayble = newValue;
			
		}
		
		final String gstAmountParamName = "gstAmount";
		if (command.get(gstAmountParamName)!=null && !command.get(gstAmountParamName).isJsonNull()) 
		{
			Double  newValue = command.get(gstAmountParamName).getAsDouble();
			actualChanges.put(gstAmountParamName, newValue);
			this.gstAmount = newValue;
		}
		
		final String foremanCommissionAmountParamName = "foremanCommissionAmount";
		if (command.get(foremanCommissionAmountParamName)!=null && !command.get(foremanCommissionAmountParamName).isJsonNull()) 
		{
			Double  newValue = command.get(foremanCommissionAmountParamName).getAsDouble();
			actualChanges.put(foremanCommissionAmountParamName, newValue);
			this.foremanCommissionAmount = newValue;
		}
		
		final String verificationAmountParamName = "verificationAmount";
		if (command.get(verificationAmountParamName)!=null && !command.get(verificationAmountParamName).isJsonNull()) 
		{
			Double newValue = command.get(verificationAmountParamName).getAsDouble();
			actualChanges.put(verificationAmountParamName, newValue);
			this.verificationAmount = newValue;
		}
		final String startdateParamName = "startdate";
		if (command.get(startdateParamName)!=null && !command.get(startdateParamName).isJsonNull()) 
		{
			
			final String tempdata = command.get(startdateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate newValue = LocalDate.parse(tempdata, formatter);
			actualChanges.put(startdateParamName, newValue);
			this.startdate = newValue;
		}
		
		final String endDateParamName = "endDate";
		if (command.get(endDateParamName)!=null && !command.get(endDateParamName).isJsonNull()) 
		{
			
			final String tempdata = command.get(endDateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate newValue = LocalDate.parse(tempdata, formatter);
			actualChanges.put(endDateParamName, newValue);
			this.endDate = newValue;
		}
		
		final String bidminutesfilingduedateParamName = "bidminutesfilingduedate";
		if (command.get(bidminutesfilingduedateParamName)!=null && !command.get(bidminutesfilingduedateParamName).isJsonNull()) 
		{
			
			final String tempdata = command.get(bidminutesfilingduedateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate newValue = LocalDate.parse(tempdata, formatter);
			actualChanges.put(bidminutesfilingduedateParamName, newValue);
			this.bidminutesfilingduedate = newValue;
		}
		
		final String dividendParamName = "dividend";
		if (command.get(dividendParamName)!=null && !command.get(dividendParamName).isJsonNull()) 
		{
			
			final Long newValue = command.get(dividendParamName).getAsLong();
			
			actualChanges.put(bidminutesfilingduedateParamName, newValue);
			this.dividend = newValue;
		}


		return actualChanges;
	}
		   
		
	public static ChitCycle create(final Long chitid,final JsonObject command) {
		
		Long dividend = null;
		LocalDateTime auctiondate = null;
		LocalDate startdate = null;
		LocalDate endDate = null;
		LocalDate bidminutesfilingduedate = null;
		Long cyclenumber = null;
		Double subscriptionPayble = null;
		Double gstAmount = null;
		Double foremanCommissionAmount = null;
		Double verificationAmount = null;
		////System.out.println("command "+command.toString());
		final String auctiondateParamName = "auctiondate";
		if (command.get(auctiondateParamName)!=null && !command.get(auctiondateParamName).isJsonNull()) 
		{
			////System.out.println(command.get(auctiondateParamName).toString());
			////System.out.println(command.get(auctiondateParamName).getAsString());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			auctiondate = LocalDateTime.parse(command.get(auctiondateParamName).getAsString(), formatter);
		}
		
		
		final String startdateParamName = "startdate";
		if (command.get(startdateParamName)!=null && !command.get(startdateParamName).isJsonNull()) 
		{
			
			final String tempdata = command.get(startdateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			startdate = LocalDate.parse(tempdata, formatter);
			
		}
		
		final String endDateParamName = "endDate";
		if (command.get(endDateParamName)!=null && !command.get(endDateParamName).isJsonNull()) 
		{
			
			final String tempdata = command.get(endDateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			endDate = LocalDate.parse(tempdata, formatter);
			
		}
		
		final String bidminutesfilingduedateParamName = "bidminutesfilingduedate";
		if (command.get(bidminutesfilingduedateParamName)!=null && !command.get(bidminutesfilingduedateParamName).isJsonNull()) 
		{
			
			final String tempdata = command.get(bidminutesfilingduedateParamName).getAsString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			bidminutesfilingduedate = LocalDate.parse(tempdata, formatter);
			
		}
		
		final String dividendParamName = "dividend";
		if (command.get(dividendParamName)!=null && !command.get(dividendParamName).isJsonNull()) 
		{
			dividend = command.get(dividendParamName).getAsLong();
			
		}
		
		final String cyclenumberParamName = "cyclenumber";
		if (command.get(cyclenumberParamName)!=null && !command.get(cyclenumberParamName).isJsonNull()) 
		{
			cyclenumber = command.get(cyclenumberParamName).getAsLong();
			
		}
		
		final String SubscriptionPayableParamName = "subscriptionPayble";
		if (command.get(SubscriptionPayableParamName)!=null && !command.get(SubscriptionPayableParamName).isJsonNull()) 
		{
			subscriptionPayble = command.get(SubscriptionPayableParamName).getAsDouble();
			
		}
		
		final String gstAmountParamName = "gstAmount";
		if (command.get(gstAmountParamName)!=null && !command.get(gstAmountParamName).isJsonNull()) 
		{
			gstAmount = command.get(gstAmountParamName).getAsDouble();
			
		}
		
		final String foremanCommissionAmountParamName = "foremanCommissionAmount";
		if (command.get(foremanCommissionAmountParamName)!=null && !command.get(foremanCommissionAmountParamName).isJsonNull()) 
		{
			foremanCommissionAmount = command.get(foremanCommissionAmountParamName).getAsDouble();
			
		}
		
		final String verificationAmountParamName = "verificationAmount";
		if (command.get(verificationAmountParamName)!=null && !command.get(verificationAmountParamName).isJsonNull()) 
		{
			verificationAmount = command.get(verificationAmountParamName).getAsDouble();
			
		}
		
		return new ChitCycle(chitid, cyclenumber, auctiondate, startdate ,endDate,dividend,bidminutesfilingduedate,subscriptionPayble,gstAmount,foremanCommissionAmount,verificationAmount);
	}

	public Double getSubscriptionPayble() {
		return subscriptionPayble;
	}

	public void setSubscriptionPayble(Double subscriptionPayble) {
		this.subscriptionPayble = subscriptionPayble;
	}

	public Double getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(Double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public Double getForemanCommissionAmount() {
		return foremanCommissionAmount;
	}

	public void setForemanCommissionAmount(Double foremanCommissionAmount) {
		this.foremanCommissionAmount = foremanCommissionAmount;
	}

	public Double getVerificationAmount() {
		return verificationAmount;
	}

	public void setVerificationAmount(Double verificationAmount) {
		this.verificationAmount = verificationAmount;
	}

	public Long getChitid() {
		return chitid;
	}

	public void setChitid(Long chitid) {
		this.chitid = chitid;
	}

	public Long getCyclenumber() {
		return cyclenumber;
	}

	public void setCyclenumber(Long cyclenumber) {
		this.cyclenumber = cyclenumber;
	}

	public LocalDateTime getAuctiondate() {
		return auctiondate;
	}

	public void setAuctiondate(LocalDateTime auctiondate) {
		this.auctiondate = auctiondate;
	}

	public LocalDate getStartdate() {
		return startdate;
	}

	public void setStartdate(LocalDate startdate) {
		this.startdate = startdate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Long getDividend() {
		return dividend;
	}

	public void setDividend(Long dividend) {
		this.dividend = dividend;
	}

	public LocalDate getBidminutesfilingduedate() {
		return bidminutesfilingduedate;
	}

	public void setBidminutesfilingduedate(LocalDate bidminutesfilingduedate) {
		this.bidminutesfilingduedate = bidminutesfilingduedate;
	}

		
		
}
