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


public final class ChitCycleData implements Serializable {

    private final Long id;

    private final Long chitId;

	private Long cycleNumber;
	
	private LocalDate auctiondate;
	
	private LocalDate startdate;
	
	private LocalDate enddate;
	
	private Long dividend;
	
	private LocalDate bidMinutesFilingDueDate;
	
	private Double subscriptionPayble;
	
	private Double gstAmount;
	
	private Double foremanCommissionAmount;
	
	private Double verificationAmount;
	
	//private Long auctionDate;

//    private ChitCycleData(final Long id, final Long chitId,final Integer cycleNumber) {
//        this.id = id;
//        this.chitId=chitId;
//        this.cycleNumber=cycleNumber;
//    }

  

	public static ChitCycleData instance(Long id, Long chitId, Long cycleNumber, LocalDate auctiondate, LocalDate startdate,
			LocalDate enddate, Long dividend, LocalDate bidMinutesFilingDueDate,Double subscriptionPayble,
			Double gstAmount, Double foremanCommissionAmount, Double verificationAmount) {
        return new ChitCycleData(id, chitId,cycleNumber,auctiondate,startdate,enddate,dividend,bidMinutesFilingDueDate,subscriptionPayble
        		,gstAmount,foremanCommissionAmount,verificationAmount);
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

	private ChitCycleData(Long id, Long chitId, Long cycleNumber, LocalDate auctiondate, LocalDate startdate,
			LocalDate enddate, Long dividend, LocalDate bidMinutesFilingDueDate, Double subscriptionPayble,
			Double gstAmount, Double foremanCommissionAmount, Double verificationAmount) {
		super();
		this.id = id;
		this.chitId = chitId;
		this.cycleNumber = cycleNumber;
		this.auctiondate = auctiondate;
		this.startdate = startdate;
		this.enddate = enddate;
		this.dividend = dividend;
		this.bidMinutesFilingDueDate = bidMinutesFilingDueDate;
		this.subscriptionPayble = subscriptionPayble;
		this.gstAmount = gstAmount;
		this.foremanCommissionAmount = foremanCommissionAmount;
		this.verificationAmount = verificationAmount;
	}

	public Long getId() {
        return id;
    }

    public Long getchitId() {
        return chitId;
    }

	public Long getCycleNumber() {
		return cycleNumber;
	}

	public void setCycleNumber(Long cycleNumber) {
		this.cycleNumber = cycleNumber;
	}

	public LocalDate getAuctiondate() {
		return auctiondate;
	}

	public void setAuctiondate(LocalDate auctiondate) {
		this.auctiondate = auctiondate;
	}

	public LocalDate getStartdate() {
		return startdate;
	}

	public void setStartdate(LocalDate startdate) {
		this.startdate = startdate;
	}

	public LocalDate getEnddate() {
		return enddate;
	}

	public void setEnddate(LocalDate enddate) {
		this.enddate = enddate;
	}

	public Long getDividend() {
		return dividend;
	}

	public void setDividend(Long dividend) {
		this.dividend = dividend;
	}

	public LocalDate getBidMinutesFilingDueDate() {
		return bidMinutesFilingDueDate;
	}

	public void setBidMinutesFilingDueDate(LocalDate bidMinutesFilingDueDate) {
		this.bidMinutesFilingDueDate = bidMinutesFilingDueDate;
	}

	public Long getChitId() {
		return chitId;
	}
    

}
