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
import java.math.BigDecimal;
import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

import org.apache.fineract.organisation.office.data.OfficeData;
//import org.apache.fineract.organisation.staff.data.StaffData;


@SuppressWarnings("unused")
public class ChitGroupData implements Serializable
{
	private final Long id;
	private final String name;
	private final Long officeId;
	private final String officeName;
	private final Long staffid;
	private final LocalDate startdate;
	private String dateFormat;
	private String locale;
	private final String chitcyclefrequency;
	private final String chitcollectionfrequency;
	private final Long chitduration;
	private final Long chitvalue;
	private final Long monthlycontribution;
	private final Long auctiondayValue;
	private final String auctionday;
	private final String auctiondayType;
	private final String auctionweekValue;
	private final LocalTime auctiontime;
	private final Long currentcycle;
	private final LocalDate nextauctiondate;
	private final Long status;
	private final Long commissionEarned;
	private final Long chitaum;
	private final Long amountDisbursed;
	private final Long amountNotDisbursed;
	private final Long enrollmentFees;
	private final BigDecimal minBidPerct;
	private final BigDecimal maxBidPerct;
	private final BigDecimal prizMemPenPerct;
	private final BigDecimal nonPrizMemPenPerct;
	private final String fdrAcNumber;
	private final LocalDate fdrIssueDate;
	private final LocalDate fdrMatuDate;
	private final Long fdrDepAmount;
	private final Integer fdrDuration;
	private final BigDecimal fdrRatIntPerct;
	private final Long fdrRateIntAmt;
	private final String fdrIntPayCycle;
	private final String fdrBankName;
	private final String fdrBankBranchName;
	private final Long fdrMatuAmount;
   
	private final LocalDate psoAppDate;
	private final LocalDate psoIssueDate;
	private final String psoNumber;
	private final LocalDate ccAppDate;
	private final LocalDate ccIssueDate;
	private final String ccNumber;
	private final LocalDate endDate;
	private transient Integer rowIndex;
	
	public static ChitGroupData importInstance(Long id,String name,Long officeId, String officeName, Long staffid,LocalDate startdate,String chitcyclefrequency, String chitcollectionfrequency, Long chitduration,Long chitvalue,Long monthlycontribution,Long auctiondayValue,
											   String auctionday,String auctiondayType,String auctionweekValue,LocalTime auctiontime,Long currentcycle, LocalDate nextauctiondate, Long status,Long commissionEarned,Long chitaum,Long amountDisbursed,Long amountNotDisbursed,
											   Long enrollmentFees,BigDecimal minBidPerct,  BigDecimal maxBidPerct,  BigDecimal prizMemPenPerct,  BigDecimal nonPrizMemPenPerct,  String fdrAcNumber,  LocalDate fdrIssueDate,  LocalDate fdrMatuDate,  Long fdrDepAmount,  Integer fdrDuration,  BigDecimal fdrRatIntPerct,  Long fdrRateIntAmt,  String fdrIntPayCycle,  String fdrBankName,  String fdrBankBranchName,  Long fdrMatuAmount, LocalDate psoAppDate, LocalDate psoIssueDate, String psoNumber, LocalDate ccAppDate, LocalDate ccIssueDate, String ccNumber, String locale,String dateFormat
											   )
	{
		return new ChitGroupData(id,name,officeId, officeName, staffid,startdate,chitcyclefrequency,chitcollectionfrequency,chitduration,chitvalue,monthlycontribution,auctiondayValue,auctionday,auctiondayType,auctionweekValue,auctiontime,
				 currentcycle, nextauctiondate,status,commissionEarned,chitaum,amountDisbursed,amountNotDisbursed,enrollmentFees, minBidPerct, maxBidPerct, prizMemPenPerct, nonPrizMemPenPerct, fdrAcNumber, fdrIssueDate, fdrMatuDate, fdrDepAmount, fdrDuration, fdrRatIntPerct, fdrRateIntAmt, fdrIntPayCycle, fdrBankName, fdrBankBranchName, fdrMatuAmount, psoAppDate, psoIssueDate, psoNumber, ccAppDate, ccIssueDate, ccNumber, locale,dateFormat,null);
	}
	
	 public LocalDate getEndDate() {
		return endDate;
	}

	public static ChitGroupData lookup(final Long id, final String name) {
	        return new ChitGroupData(id,name, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,null);
	    }
	
	public Collection<OfficeData> getAllowedOffices() {
		return allowedOffices;
	}

	private ChitGroupData(Long id,String name,Long officeId, String officeName, Long staffid,LocalDate startdate,String chitcyclefrequency,String chitcollectionfrequency,Long chitduration,Long chitvalue,Long monthlycontribution,Long auctiondayValue,String auctionday,String auctiondayType,
	String auctionweekValue,LocalTime auctiontime,Long currentcycle,LocalDate nextauctiondate, Long status,Long commissionEarned,Long chitaum,Long amountDisbursed,Long amountNotDisbursed,Long enrollmentFees, BigDecimal minBidPerct,  BigDecimal maxBidPerct,  BigDecimal prizMemPenPerct,  BigDecimal nonPrizMemPenPerct,  String fdrAcNumber,  LocalDate fdrIssueDate,  LocalDate fdrMatuDate,  Long fdrDepAmount,  Integer fdrDuration,  BigDecimal fdrRatIntPerct,  Long fdrRateIntAmt,  String fdrIntPayCycle,  String fdrBankName,  String fdrBankBranchName,  Long fdrMatuAmount, LocalDate psoAppDate, LocalDate psoIssueDate, String psoNumber, LocalDate ccAppDate, LocalDate ccIssueDate, String ccNumber, String locale,String dateFormat,
	LocalDate endDate)
	{
		this.id = id ; 
		this.name = name ;
		this.officeId = officeId;
		this.officeName = officeName;
		this.staffid=staffid;
		this.startdate = startdate;
		this.auctionday = auctionday;
		this.auctiondayValue = auctiondayValue;
		this.amountDisbursed=amountDisbursed;
		this.amountNotDisbursed = amountNotDisbursed;
		this.auctiondayType= auctiondayType;
		this.auctionweekValue=auctionweekValue;
		this.auctiontime = auctiontime;
		this.chitaum=chitaum;
		this.chitcyclefrequency=chitcyclefrequency;
		this.chitcollectionfrequency=chitcollectionfrequency;
		this.chitduration = chitduration;
		this.chitvalue = chitvalue;
		this.commissionEarned=commissionEarned;
		this.currentcycle = currentcycle;
		this.nextauctiondate = nextauctiondate;
		this.enrollmentFees = enrollmentFees;
		this.monthlycontribution = monthlycontribution;
		this.status = status;
		this.locale = locale;
		this.dateFormat = locale;
		this.allowedOffices = null;
		this.minBidPerct = minBidPerct;
		this.maxBidPerct = maxBidPerct;
		this.prizMemPenPerct = prizMemPenPerct;
		this.nonPrizMemPenPerct = nonPrizMemPenPerct;
		this.fdrAcNumber = fdrAcNumber;
		this.fdrIssueDate = fdrIssueDate;
		this.fdrMatuDate = fdrMatuDate;
		this.fdrDepAmount = fdrDepAmount;
		this.fdrDuration = fdrDuration;
		this.fdrRatIntPerct = fdrRatIntPerct;
		this.fdrRateIntAmt = fdrRateIntAmt;
		this.fdrIntPayCycle = fdrIntPayCycle;
		this.fdrBankName = fdrBankName;
		this.fdrBankBranchName = fdrBankBranchName;
		this.fdrMatuAmount = fdrMatuAmount;
		this.psoAppDate = psoAppDate; 
		this.psoIssueDate = psoIssueDate; 
		this.psoNumber = psoNumber; 
		this.ccAppDate = ccAppDate; 
		this.ccIssueDate = ccIssueDate; 
		this.ccNumber = ccNumber; 
		this.endDate = endDate;
	}
	public Integer getRowIndex() {
        return rowIndex;
    }
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Long getOfficeId()
	{
		return officeId;
	}
	public static ChitGroupData instance(Long id,String name,Long officeId, String officeName, Long staffid,LocalDate startdate,String chitcyclefrequency,String chitcollectionfrequency,Long chitduration,Long chitvalue,Long monthlycontribution,Long auctiondayValue,String auctionday,String auctiondayType,
			String auctionweekValue,LocalTime auctiontime,Long currentcycle,LocalDate nextauctiondate,Long status,Long commissionEarned,Long chitaum,Long amountDisbursed,Long amountNotDisbursed,Long enrollmentFees, BigDecimal minBidPerct,  BigDecimal maxBidPerct,  BigDecimal prizMemPenPerct,  BigDecimal nonPrizMemPenPerct,  String fdrAcNumber,  LocalDate fdrIssueDate,  LocalDate fdrMatuDate,  Long fdrDepAmount,  Integer fdrDuration,  BigDecimal fdrRatIntPerct,  Long fdrRateIntAmt,  String fdrIntPayCycle,  String fdrBankName,  String fdrBankBranchName,  Long fdrMatuAmount, LocalDate psoAppDate, LocalDate psoIssueDate, String psoNumber, LocalDate ccAppDate, LocalDate ccIssueDate, String ccNumber, String locale,String dateFormat,
			LocalDate endDate) 
	{
		return new ChitGroupData(id,name,officeId,officeName,staffid,startdate,chitcyclefrequency,chitcollectionfrequency, chitduration,chitvalue,monthlycontribution,auctiondayValue,auctionday,auctiondayType,auctionweekValue,auctiontime,
				 currentcycle,nextauctiondate,status,commissionEarned,chitaum,amountDisbursed,amountNotDisbursed,enrollmentFees,minBidPerct, maxBidPerct, prizMemPenPerct, nonPrizMemPenPerct, fdrAcNumber, fdrIssueDate, fdrMatuDate, fdrDepAmount, fdrDuration, fdrRatIntPerct, fdrRateIntAmt, fdrIntPayCycle, fdrBankName, fdrBankBranchName, fdrMatuAmount, psoAppDate, psoIssueDate, psoNumber, ccAppDate, ccIssueDate, ccNumber, locale,dateFormat,endDate);
	}
	
	private final Collection<OfficeData> allowedOffices;
    public static ChitGroupData templateData(final ChitGroupData ChitGroup,final Collection<OfficeData> allowedOffices) {
        return new ChitGroupData(ChitGroup.id,ChitGroup.name,ChitGroup.officeId,ChitGroup.officeName, allowedOffices, ChitGroup.staffid,ChitGroup.startdate,ChitGroup.chitcyclefrequency,ChitGroup.chitcollectionfrequency, ChitGroup.chitduration,ChitGroup.chitvalue,ChitGroup.monthlycontribution,ChitGroup.auctiondayValue,ChitGroup.auctionday,ChitGroup.auctiondayType,ChitGroup.auctionweekValue,ChitGroup.auctiontime,
        		ChitGroup.currentcycle,ChitGroup.nextauctiondate, ChitGroup.status,ChitGroup.commissionEarned,ChitGroup.chitaum,ChitGroup.amountDisbursed,ChitGroup.amountNotDisbursed,ChitGroup.enrollmentFees, ChitGroup.minBidPerct, ChitGroup.maxBidPerct, ChitGroup.prizMemPenPerct, ChitGroup.nonPrizMemPenPerct, ChitGroup.fdrAcNumber, ChitGroup.fdrIssueDate, ChitGroup.fdrMatuDate, ChitGroup.fdrDepAmount, ChitGroup.fdrDuration, ChitGroup.fdrRatIntPerct, ChitGroup.fdrRateIntAmt, ChitGroup.fdrIntPayCycle, ChitGroup.fdrBankName, ChitGroup.fdrBankBranchName, ChitGroup.fdrMatuAmount, ChitGroup.psoAppDate, ChitGroup.psoIssueDate, ChitGroup.psoNumber, ChitGroup.ccAppDate, ChitGroup.ccIssueDate, ChitGroup.ccNumber,ChitGroup.endDate);
    }
	
	private ChitGroupData(Long id,String name,Long officeId,String officeName, final Collection<OfficeData> allowedOffices, Long staffid,LocalDate startdate,String chitcyclefrequency,String chitcollectionfrequency, Long chitduration,Long chitvalue,Long monthlycontribution,Long auctiondayValue,String auctionday,String auctiondayType,
			String auctionweekValue,LocalTime auctiontime,Long currentcycle,LocalDate nextauctiondate, Long status,Long commissionEarned,Long chitaum,Long amountDisbursed,Long amountNotDisbursed,Long enrollmentFees, BigDecimal minBidPerct,  BigDecimal maxBidPerct,  BigDecimal prizMemPenPerct,  BigDecimal nonPrizMemPenPerct,  String fdrAcNumber,  LocalDate fdrIssueDate,  LocalDate fdrMatuDate,  Long fdrDepAmount,  Integer fdrDuration,  BigDecimal fdrRatIntPerct,  Long fdrRateIntAmt,  String fdrIntPayCycle,  String fdrBankName,  String fdrBankBranchName,  Long fdrMatuAmount, LocalDate psoAppDate, LocalDate psoIssueDate, String psoNumber, LocalDate ccAppDate, LocalDate ccIssueDate, String ccNumber,LocalDate endDate)
	{
		this.id = id ; 
		this.name = name ;
		this.officeId = officeId;
		this.officeName = officeName;
		this.staffid=staffid;
		this.startdate = startdate;
		this.auctiondayValue = auctiondayValue;
		this.auctionday = auctionday;
		this.amountDisbursed=amountDisbursed;
		this.amountNotDisbursed = amountNotDisbursed;
		this.auctiondayType= auctiondayType;
		this.auctionweekValue=auctionweekValue;
		this.auctiontime = auctiontime;
		this.chitaum=chitaum;
		this.chitcyclefrequency=chitcyclefrequency;
		this.chitcollectionfrequency=chitcollectionfrequency;
		this.chitduration = chitduration;
		this.chitvalue = chitvalue;
		this.commissionEarned=commissionEarned;
		this.currentcycle = currentcycle;
		this.nextauctiondate = nextauctiondate;
		this.enrollmentFees = enrollmentFees;
		this.monthlycontribution = monthlycontribution;
		this.status = status;
		this.allowedOffices = allowedOffices;
		this.minBidPerct = minBidPerct;
		this.maxBidPerct = maxBidPerct;
		this.prizMemPenPerct = prizMemPenPerct;
		this.nonPrizMemPenPerct = nonPrizMemPenPerct;
		this.fdrAcNumber = fdrAcNumber;
		this.fdrIssueDate = fdrIssueDate;
		this.fdrMatuDate = fdrMatuDate;
		this.fdrDepAmount = fdrDepAmount;
		this.fdrDuration = fdrDuration;
		this.fdrRatIntPerct = fdrRatIntPerct;
		this.fdrRateIntAmt = fdrRateIntAmt;
		this.fdrIntPayCycle = fdrIntPayCycle;
		this.fdrBankName = fdrBankName;
		this.fdrBankBranchName = fdrBankBranchName;
		this.fdrMatuAmount = fdrMatuAmount;
		this.psoAppDate = psoAppDate; 
		this.psoIssueDate = psoIssueDate; 
		this.psoNumber = psoNumber; 
		this.ccAppDate = ccAppDate; 
		this.ccIssueDate = ccIssueDate; 
		this.ccNumber = ccNumber; 
		this.endDate = endDate;
	}
	public Long getStaffid() {
		return staffid;
	}

	public LocalDate getStartdate() {
		return startdate;
	}

	public String getChitcyclefrequency() {
		return chitcyclefrequency;
	}

	public String getChitcollectionfrequency() {
		return chitcollectionfrequency;
	}

	public Long getChitduration() {
		return chitduration;
	}

	public Long getChitvalue() {
		return chitvalue;
	}

	public Long getMonthlycontribution() {
		return monthlycontribution;
	}

	public String getauctionday() {
		return auctionday;
	}
	
	public Long getAuctiondayValue() {
		return auctiondayValue;
	}

	public String getAuctiondayType() {
		return auctiondayType;
	}

	public String getAuctionweekValue() {
		return auctionweekValue;
	}

	public LocalTime getAuctiontime() {
		return auctiontime;
	}

	public Long getCurrentcycle() {
		return currentcycle;
	}

	public LocalDate getNextauctiondate() {
		return nextauctiondate;
	}

	public Long getStatus() {
		return status;
	}

	public Long getCommissionEarned() {
		return commissionEarned;
	}

	public Long getChitaum() {
		return chitaum;
	}

	public Long getAmountDisbursed() {
		return amountDisbursed;
	}

	public Long getAmountNotDisbursed() {
		return amountNotDisbursed;
	}

	public Long getEnrollmentFees() {
		return enrollmentFees;
	}

	public BigDecimal getMinBidPerct() {
		return minBidPerct;
	}

	public BigDecimal getMaxBidPerct() {
		return maxBidPerct;
	}

	public BigDecimal getPrizMemPenPerct() {
		return prizMemPenPerct;
	}

	public BigDecimal getNonPrizMemPenPerct() {
		return nonPrizMemPenPerct;
	}

	public String getFdrAcNumber() {
		return fdrAcNumber;
	}

	public LocalDate getFdrIssueDate() {
		return fdrIssueDate;
	}

	public LocalDate getFdrMatuDate() {
		return fdrMatuDate;
	}

	public Long getFdrDepAmount() {
		return fdrDepAmount;
	}

	public Integer getFdrDuration() {
		return fdrDuration;
	}

	public BigDecimal getFdrRatIntPerct() {
		return fdrRatIntPerct;
	}

	public Long getFdrRateIntAmt() {
		return fdrRateIntAmt;
	}

	public String getFdrIntPayCycle() {
		return fdrIntPayCycle;
	}

	public String getFdrBankName() {
		return fdrBankName;
	}

	public String getFdrBankBranchName() {
		return fdrBankBranchName;
	}

	public Long getFdrMatuAmount() {
		return fdrMatuAmount;
	}

	public LocalDate getPsoAppDate() {
		return psoAppDate;
	}

	public LocalDate getPsoIssueDate() {
		return psoIssueDate;
	}

	public String getPsoNumber() {
		return psoNumber;
	}

	public LocalDate getCcAppDate() {
		return ccAppDate;
	}

	public LocalDate getCcIssueDate() {
		return ccIssueDate;
	}

	public String getCcNumber() {
		return ccNumber;
	}


}
