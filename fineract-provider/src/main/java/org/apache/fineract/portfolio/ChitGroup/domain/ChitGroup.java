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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
//import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
//import javax.persistence.UniqueConstraint;
//import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
//import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
//import org.apache.fineract.infrastructure.core.service.DateUtils;
//import org.apache.fineract.infrastructure.documentmanagement.domain.Image;
import org.apache.fineract.organisation.office.domain.Office;

import com.google.gson.JsonObject;


@Entity
@Table(name = "chit_group")
public class ChitGroup extends AbstractPersistableCustom
{

	// @Column(name = "ID", length = 11 )
	// private Long id;
	
	@Column(name = "name" , length = 45)
	private String name;
	
    @ManyToOne
	@JoinColumn(name = "branch_id")
	private Office office;
    
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private LocalDate startdate;
   
    @Column(name = "staff_id")
	private Long staffid;
    
    @Column(name = "chit_cycle_frequency")
    private String chitcyclefrequency;
    
    @Column(name = "chit_collection_frequency")
    private String chitcollectionfrequency;
	
    @Column(name = "chit_duration")
    private Long chitduration;
    
    @Column(name = "chit_value")
    private Long chitvalue;
    
    @Column(name = "monthly_contribution")
    private Long monthlycontribution;

    @Column(name = "auction_day")
    private String auctionday;
    
    @Column(name = "auction_day_value")
    private Long auctiondayValue;
    
    @Column(name = "auction_day_type")
    private String auctiondayType;
    
    @Column(name = "auction_week_value")
    private String auctionweekValue;
    
    @Column(name = "auction_time")
    private LocalTime auctiontime;
    
    @Column(name = "current_cycle")
    private Long currentcycle;
    
    @Column(name = "next_auction_date")
	@Temporal(TemporalType.DATE)
    private LocalDate nextauctiondate;

	@Column(name = "status")
    private Long status;
    
    @Column(name = "commission_earned")
    private Long commissionEarned;
    
    @Column(name = "chit_aum")
    private Long chitaum;
    
    @Column(name = "amount_disbursed")
    private Long amountDisbursed;
     
    @Column(name = "amount_not_disbursed")
    private Long amountNotDisbursed;
    
    @Column(name = "enrollment_fees")
    private Long enrollmentFees;

    @Column(name = "min_bid_perct", scale = 4, precision = 2)
    private BigDecimal minBidPerct;

    @Column(name = "max_bid_perct", scale = 4, precision = 2)
    private BigDecimal maxBidPerct;

    @Column(name = "priz_mem_pen_perct", scale = 4, precision = 2)
    private BigDecimal prizMemPenPerct;

    @Column(name = "non_priz_mem_pen_perct", scale = 4, precision = 2)
    private BigDecimal nonPrizMemPenPerct;

	@Column(name = "fdr_ac_number")
    private String fdrAcNumber;

    @Column(name = "fdr_issue_date")
    @Temporal(TemporalType.DATE)
	private LocalDate fdrIssueDate;

    @Column(name = "fdr_matu_date")
    @Temporal(TemporalType.DATE)
	private LocalDate fdrMatuDate;

	@Column(name = "fdr_dep_amount")
    private Long fdrDepAmount;

    @Column(name = "fdr_duration")
    private Integer fdrDuration;

    @Column(name = "fdr_rat_int_perct", scale = 4, precision = 2)
    private BigDecimal fdrRatIntPerct;
	
	@Column(name = "fdr_rate_int_amt")
    private Long fdrRateIntAmt;

	@Column(name = "fdr_int_pay_cycle")
    private String fdrIntPayCycle;

	@Column(name = "fdr_bankname")
    private String fdrBankName;

	@Column(name = "fdr_bankbranch_name")
    private String fdrBankBranchName;

	@Column(name = "fdr_matu_amount")
    private Long fdrMatuAmount;

	@Column(name = "pso_appl_date")
    private LocalDate psoAppDate;

	@Column(name = "pso_issue_date")
    private LocalDate psoIssueDate;

	@Column(name = "pso_number")
    private String psoNumber;

	@Column(name = "cc_appl_date")
    private LocalDate ccAppDate;

	@Column(name = "cc_issue_date")
    private LocalDate ccIssueDate;

	@Column(name = "cc_number")
    private String ccNumber;
	
	@Column(name = "end_date")
    private LocalDate endDate;

	public static ChitGroup create(final Office office,final JsonCommand command)
	{
		final String nameParamName = "name";
		final String name = command.stringValueOfParameterNamed(nameParamName);
	
		LocalDate startdate = null;

		final String startdateParamName = "startdate";
		if (command.hasParameter(startdateParamName)) {
			startdate = command.localDateValueOfParameterNamed(startdateParamName);
		}
		
		final String chitcyclefrequencyParamName = "chitcyclefrequency";
		final String chitcyclefrequency = command.stringValueOfParameterNamed(chitcyclefrequencyParamName);
		
		final String chitcollectionfrequencyParamName = "chitcollectionfrequency";
		final String chitcollectionfrequency = command.stringValueOfParameterNamed(chitcollectionfrequencyParamName);
		
		final String auctiondayParamName = "auctionday";
		final String auctionday = command.stringValueOfParameterNamed(auctiondayParamName);
		
		final String auctiondayTypeParamName = "auctiondayType";
		final String auctiondayType = command.stringValueOfParameterNamed(auctiondayTypeParamName);
		
		final String auctionweekValueParamName = "auctionweekValue";
		final String auctionweekValue = command.stringValueOfParameterNamed(auctionweekValueParamName);
		
		final String auctiontimeParamName = "auctiontime";
		final LocalTime auctiontime = LocalTime.parse(command.stringValueOfParameterNamed(auctiontimeParamName));		
		
		final String idParamName = "ID";
		final Long id  = command.longValueOfParameterNamed(idParamName);
		
		final String staffidParamName = "staffid";
		final Long staffid  = command.longValueOfParameterNamed(staffidParamName);
		
		final String auctiondayValueParamName = "auctiondayValue";
		final Long auctiondayValue  = command.longValueOfParameterNamed(auctiondayValueParamName);
		
		final String chitdurationParamName = "chitduration";
		final Long chitduration  = command.longValueOfParameterNamed(chitdurationParamName);
		
		final String chitvalueParamName = "chitvalue";
		final Long chitvalue  = command.longValueOfParameterNamed(chitvalueParamName);
		
		final String monthlycontributionParamName = "monthlycontribution";
		final Long monthlycontribution  = command.longValueOfParameterNamed(monthlycontributionParamName);
		
//	     final String auctiondayParamName = "auctionday";
//	     final String auctionday = command.stringValueOfParameterNamed(auctiondayParamName);
		
		final String currentcycleParamName = "currentcycle";
		final Long currentcycle = command.longValueOfParameterNamed(currentcycleParamName);

		LocalDate nextauctiondate = null;
		final String nextauctiondateParamName = "nextauctiondate";
		if (command.hasParameter(nextauctiondateParamName)) {
			nextauctiondate = command.localDateValueOfParameterNamed(nextauctiondateParamName);
		}
		
		final String statusParamName = "status";
		final Long status = command.longValueOfParameterNamed(statusParamName);
		
		final String commissionEarnedParamName = "commissionEarned";
		final Long commissionEarned = command.longValueOfParameterNamed(commissionEarnedParamName);
		
		final String chitaumParamName = "chitaum";
		final Long chitaum = command.longValueOfParameterNamed(chitaumParamName);
		
		final String amountDisbursedParamName = "amountDisbursed";
		final Long amountDisbursed = command.longValueOfParameterNamed(amountDisbursedParamName);
		
		
		final String amountNotDisbursedParamName = "amountNotDisbursed";
		final Long amountNotDisbursed = command.longValueOfParameterNamed(amountNotDisbursedParamName);
		
		
		final String enrollmentFeesParamName = "enrollmentFees";
		final Long enrollmentFees = command.longValueOfParameterNamed(enrollmentFeesParamName);
		
		final String minBidPerctParamName = "minBidPerct";
		final BigDecimal minBidPerct = command.bigDecimalValueOfParameterNamed(minBidPerctParamName);
		
		final String maxBidPerctParamName = "maxBidPerct";
		final BigDecimal maxBidPerct = command.bigDecimalValueOfParameterNamed(maxBidPerctParamName);

		final String prizMemPenPerctParamName = "prizMemPenPerct";
		final BigDecimal prizMemPenPerct = command.bigDecimalValueOfParameterNamed(prizMemPenPerctParamName);

		final String nonPrizMemPenPerctParamName = "nonPrizMemPenPerct";
		final BigDecimal nonPrizMemPenPerct = command.bigDecimalValueOfParameterNamed(nonPrizMemPenPerctParamName);
		
		final String fdrRatIntPerctParamName = "fdrRatIntPerct";
		final BigDecimal fdrRatIntPerct = command.bigDecimalValueOfParameterNamed(fdrRatIntPerctParamName);

		final String fdrAcNumberParamName = "fdrAcNumber";
		final String fdrAcNumber = command.stringValueOfParameterNamed(fdrAcNumberParamName);

		final String fdrIntPayCycleParamName = "fdrIntPayCycle";
		final String fdrIntPayCycle = command.stringValueOfParameterNamed(fdrIntPayCycleParamName);

		final String fdrBankNameParamName = "fdrBankName";
		final String fdrBankName = command.stringValueOfParameterNamed(fdrBankNameParamName);

		final String fdrBankBranchNameParamName = "fdrBankBranchName";
		final String fdrBankBranchName = command.stringValueOfParameterNamed(fdrBankBranchNameParamName);

		final String fdrDepAmountParamName = "fdrDepAmount";
		final Long fdrDepAmount = command.longValueOfParameterNamed(fdrDepAmountParamName);

		final String fdrRateIntAmtParamName = "fdrRateIntAmt";
		final Long fdrRateIntAmt = command.longValueOfParameterNamed(fdrRateIntAmtParamName);

		final String fdrMatuAmountParamName = "fdrMatuAmount";
		final Long fdrMatuAmount = command.longValueOfParameterNamed(fdrMatuAmountParamName);

		final String fdrDurationParamName = "fdrDuration";
		final Integer fdrDuration = command.integerValueOfParameterNamed(fdrDurationParamName);

	    LocalDate fdrIssueDate = null;
		final String fdrIssueDateParamName = "fdrIssueDate";
		if (command.hasParameter(fdrIssueDateParamName)) {
			fdrIssueDate = command.localDateValueOfParameterNamed(fdrIssueDateParamName);
		}

	    LocalDate fdrMatuDate = null;
		final String fdrMatuDateParamName = "fdrMatuDate";
		if (command.hasParameter(fdrMatuDateParamName)) {
			fdrMatuDate = command.localDateValueOfParameterNamed(fdrMatuDateParamName);
		}

	    LocalDate psoAppDate = null;
		final String psoAppDateParamName = "psoAppDate";
		if (command.hasParameter(psoAppDateParamName)) {
			psoAppDate = command.localDateValueOfParameterNamed(psoAppDateParamName);
		}
	    LocalDate psoIssueDate = null;
		final String psoIssueDateParamName = "psoIssueDate";
		if (command.hasParameter(psoIssueDateParamName)) {
			psoIssueDate = command.localDateValueOfParameterNamed(psoIssueDateParamName);
		}
		final String psoNumberParamName = "psoNumber";
		final String psoNumber = command.stringValueOfParameterNamed(psoNumberParamName);

	    LocalDate ccAppDate = null;
		final String ccAppDateParamName = "ccAppDate";
		if (command.hasParameter(ccAppDateParamName)) {
			ccAppDate = command.localDateValueOfParameterNamed(ccAppDateParamName);
		}
	    LocalDate ccIssueDate = null;
		final String ccIssueDateParamName = "ccIssueDate";
		if (command.hasParameter(ccIssueDateParamName)) {
			ccIssueDate = command.localDateValueOfParameterNamed(ccIssueDateParamName);
		}
		final String ccNumberParamName = "ccNumber";
		final String ccNumber = command.stringValueOfParameterNamed(ccNumberParamName);
 
		return new ChitGroup(office,id,name,staffid,startdate,chitcyclefrequency,chitcollectionfrequency,chitduration,chitvalue,monthlycontribution,
				auctiondayValue,auctionday, auctiondayType, auctionweekValue, auctiontime, currentcycle, nextauctiondate, status, commissionEarned,chitaum,amountDisbursed,amountNotDisbursed,
			enrollmentFees, minBidPerct, maxBidPerct, prizMemPenPerct, nonPrizMemPenPerct, fdrAcNumber, fdrIssueDate, fdrMatuDate, fdrDepAmount, fdrDuration, fdrRatIntPerct, fdrRateIntAmt, fdrIntPayCycle, fdrBankName, fdrBankBranchName, fdrMatuAmount, psoAppDate, psoIssueDate, psoNumber, ccAppDate, ccIssueDate, ccNumber,null);
	}
	
	protected ChitGroup()
	{
		
	}
	
	private ChitGroup(final Office office,final Long id,final String name,final Long staffid,final LocalDate startdate,final String chitcyclefrequency,final String chitcollectionfrequency,final Long chitduration,final Long chitvalue,final Long monthlycontribution,
			final Long auctiondayValue, final String auctionday,final String auctiondayType,final String auctionweekValue,final LocalTime auctiontime,final Long currentcycle,final LocalDate nextauctiondate, final Long status,final Long commissionEarned,final Long chitaum,final Long amountDisbursed,final Long amountNotDisbursed,
			final Long enrollmentFees, final BigDecimal minBidPerct, final BigDecimal maxBidPerct, final BigDecimal prizMemPenPerct, final BigDecimal nonPrizMemPenPerct, final String fdrAcNumber, final LocalDate fdrIssueDate, final LocalDate fdrMatuDate, final Long fdrDepAmount, 
			final Integer fdrDuration, final BigDecimal fdrRatIntPerct, final Long fdrRateIntAmt, final String fdrIntPayCycle, final String fdrBankName, final String fdrBankBranchName, final Long fdrMatuAmount, 
			final LocalDate psoAppDate, final LocalDate psoIssueDate, final String psoNumber, final LocalDate ccAppDate, final LocalDate ccIssueDate, final String ccNumber,final LocalDate endDate)
	{
		this.office = office;
		//this.id = id;
		this.name = name;
		this.staffid=staffid;
		this.startdate = startdate;
		this.auctionday = auctionday;
		this.auctiondayValue=auctiondayValue;
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
	
	public Map<String,Object> updatewithJson(final JsonObject command)
	{
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);
		if(command.get("currentcycle")!=null && !command.get("currentcycle").isJsonNull())
		{
			
				final Long newValue = command.get("currentcycle").getAsLong();
				actualChanges.put("currentcycle", newValue);
				this.currentcycle = newValue;
		}
		
		
		final String transactionDateParamName = "nextauctiondate";
		if (command.get(transactionDateParamName)!=null && !command.get(transactionDateParamName).isJsonNull()) 
		{
		 
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate newValue = LocalDate.parse(command.get(transactionDateParamName).getAsString(), formatter);
			actualChanges.put(transactionDateParamName, newValue);
			this.nextauctiondate = newValue;
		}
		
		final String statusParamName = "status";
		if (command.get(statusParamName)!=null && !command.get(statusParamName).isJsonNull()) 
		{
		 
			
			Long newValue = command.get(statusParamName).getAsLong();
			actualChanges.put(statusParamName, newValue);
			this.status = newValue;
		}
		
		final String endDateParamName = "endDate";
		if (command.get(endDateParamName)!=null && !command.get(endDateParamName).isJsonNull()) 
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate newValue = LocalDate.parse(command.get(endDateParamName).getAsString(), formatter);
			actualChanges.put(endDateParamName, newValue);
			this.endDate = newValue;
		}
		
		return actualChanges;
	}
	
	 public Map<String, Object> update(final JsonCommand command)
	 {
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);
		
		final String nameParamName = "name";
		if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
		final String newValue = command.stringValueOfParameterNamed(nameParamName);
		actualChanges.put(nameParamName, newValue);
		this.name = newValue;
		}
		
		final String startdateParamName = "startdate";
		if (command.isChangeInLocalDateParameterNamed(startdateParamName, this.startdate)) {
		final LocalDate newValue = command.localDateValueOfParameterNamed(startdateParamName);
		actualChanges.put(startdateParamName, newValue);
		this.startdate = newValue;
		}

		final String chitcyclefrequencyParamName = "chitcyclefrequency";
		if (command.isChangeInStringParameterNamed(chitcyclefrequencyParamName, this.chitcyclefrequency)) 
		{
			final String newValue = command.stringValueOfParameterNamed(chitcyclefrequencyParamName);
			actualChanges.put(chitcyclefrequencyParamName, newValue);
			this.chitcyclefrequency = newValue;
		}
		final String chitcollectionfrequencyParamName = "chitcollectionfrequency";
		if (command.isChangeInStringParameterNamed(chitcollectionfrequencyParamName, this.chitcollectionfrequency)) 
		{
			final String newValue = command.stringValueOfParameterNamed(chitcollectionfrequencyParamName);
			actualChanges.put(chitcollectionfrequencyParamName, newValue);
			this.chitcollectionfrequency = newValue;
		}

		final String auctiontimeParamName = "auctiontime";
		if(command.stringValueOfParameterNamed(auctiontimeParamName) != null) {
			final LocalTime newValue = LocalTime.parse(command.stringValueOfParameterNamed(auctiontimeParamName));	
			actualChanges.put(auctiontimeParamName, newValue);
			this.auctiontime = newValue;
		}
				
		final String auctiondayParamName = "auctionday";
		if (command.isChangeInStringParameterNamed(auctiondayParamName, this.auctionday)) 
		{
			final String newValue = command.stringValueOfParameterNamed(auctiondayParamName);
			actualChanges.put(auctiondayParamName, newValue);
			this.auctionday = newValue;
		}
		
		final String auctiondayTypeParamName = "auctiondayType";
		if (command.isChangeInStringParameterNamed(auctiondayTypeParamName, this.auctiondayType)) 
		{
			final String newValue = command.stringValueOfParameterNamed(auctiondayTypeParamName);
			actualChanges.put(auctiondayTypeParamName, newValue);
			this.auctiondayType = newValue;
		}
	     
		final String auctionweekValueParamName = "auctionweekValue";
		if (command.isChangeInStringParameterNamed(auctionweekValueParamName, this.auctionweekValue)) 
		{
			final String newValue = command.stringValueOfParameterNamed(auctionweekValueParamName);
			actualChanges.put(auctionweekValueParamName, newValue);
			this.auctionweekValue = newValue;
		}
	     
	     
	    // final String idParamName = "id";
		// if (command.isChangeInLongParameterNamed(idParamName, this.id)) 
		// {
		// 	final Long newValue = command.longValueOfParameterNamed(idParamName);
		// 	actualChanges.put(idParamName, newValue);
		// 	this.id = newValue;
		// }
		
		final String staffidParamName = "staffid";
		if (command.isChangeInLongParameterNamed(staffidParamName, this.staffid)) 
		{
			final Long newValue = command.longValueOfParameterNamed(staffidParamName);
			actualChanges.put(staffidParamName, newValue);
			this.staffid = newValue;
		}
		
		
		final String chitdurationParamName = "chitduration";
		if (command.isChangeInLongParameterNamed(chitdurationParamName, this.chitduration)) 
		{
			final Long newValue = command.longValueOfParameterNamed(chitdurationParamName);
			actualChanges.put(chitdurationParamName, newValue);
			this.chitduration = newValue;
		}
		
		final String chitvalueParamName = "chitvalue";
		if (command.isChangeInLongParameterNamed(chitvalueParamName, this.chitduration)) 
		{
			final Long newValue = command.longValueOfParameterNamed(chitvalueParamName);
			actualChanges.put(chitvalueParamName, newValue);
			this.chitvalue = newValue;
		}
		
		final String monthlycontributionParamName = "monthlycontribution";
		if (command.isChangeInLongParameterNamed(monthlycontributionParamName, this.monthlycontribution)) 
		{
			final Long newValue = command.longValueOfParameterNamed(monthlycontributionParamName);
			actualChanges.put(monthlycontributionParamName, newValue);
			this.monthlycontribution = newValue;
		}
		
		final String currentcycleParamName = "currentcycle";
		if (command.isChangeInLongParameterNamed(currentcycleParamName, this.currentcycle)) 
		{
			final Long newValue = command.longValueOfParameterNamed(currentcycleParamName);
			actualChanges.put(currentcycleParamName, newValue);
			this.currentcycle = newValue;
		}

		final String nextauctiondateParamName = "nextauctiondate";
		if (command.isChangeInLocalDateParameterNamed(nextauctiondateParamName, this.nextauctiondate)) {
		final LocalDate newValue = command.localDateValueOfParameterNamed(nextauctiondateParamName);
		actualChanges.put(nextauctiondateParamName, newValue);
		this.nextauctiondate = newValue;
		}

		final String statusParamName = "status";
		if (command.isChangeInLongParameterNamed(statusParamName, this.status)) 
		{
			final Long newValue = command.longValueOfParameterNamed(statusParamName);
			actualChanges.put(statusParamName, newValue);
			this.status = newValue;
		}
		
		final String commissionEarnedParamName = "commissionEarned";
		if (command.isChangeInLongParameterNamed(commissionEarnedParamName, this.commissionEarned)) 
		{
			final Long newValue = command.longValueOfParameterNamed(commissionEarnedParamName);
			actualChanges.put(commissionEarnedParamName, newValue);
			this.commissionEarned = newValue;
		}

		final String chitaumParamName = "chitaum";
		if (command.isChangeInLongParameterNamed(chitaumParamName, this.chitaum)) 
		{
			final Long newValue = command.longValueOfParameterNamed(chitaumParamName);
			actualChanges.put(chitaumParamName, newValue);
			this.chitaum = newValue;
		}
		
		final String amountDisbursedParamName = "amountDisbursed";
		if (command.isChangeInLongParameterNamed(amountDisbursedParamName, this.amountDisbursed)) 
		{
			final Long newValue = command.longValueOfParameterNamed(amountDisbursedParamName);
			actualChanges.put(amountDisbursedParamName, newValue);
			this.amountDisbursed = newValue;
		}
		
		final String amountNotDisbursedParamName = "amountNotDisbursed";
		if (command.isChangeInLongParameterNamed(amountNotDisbursedParamName, this.amountNotDisbursed)) 
		{
			final Long newValue = command.longValueOfParameterNamed(amountNotDisbursedParamName);
			actualChanges.put(amountNotDisbursedParamName, newValue);
			this.amountNotDisbursed = newValue;
		}
		
		final String enrollmentFeesParamName = "enrollmentFees";
		if (command.isChangeInLongParameterNamed(enrollmentFeesParamName, this.enrollmentFees)) 
		{
			final Long newValue = command.longValueOfParameterNamed(enrollmentFeesParamName);
			actualChanges.put(enrollmentFeesParamName, newValue);
			this.enrollmentFees = newValue;
		}
		
		final String auctiondayValueParamName = "auctiondayValue";
		if (command.isChangeInLongParameterNamed(auctiondayValueParamName, this.auctiondayValue)) 
		{
			final Long newValue = command.longValueOfParameterNamed(auctiondayValueParamName);
			actualChanges.put(auctiondayValueParamName, newValue);
			this.auctiondayValue = newValue;
		}
		
		final String officeIdParamName = "officeId";
		if (command.isChangeInLongParameterNamed(officeIdParamName, this.office.getId())) {
			final Long newValue = command.longValueOfParameterNamed(officeIdParamName);
			actualChanges.put(officeIdParamName, newValue);
		}
		
		final String minBidPerctParamName = "minBidPerct";
		if (command.isChangeInBigDecimalParameterNamed(minBidPerctParamName, this.minBidPerct)) {
			final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(minBidPerctParamName);
			actualChanges.put(minBidPerctParamName, newValue);
			this.minBidPerct = newValue;
		}

		final String maxBidPerctParamName = "maxBidPerct";
		if (command.isChangeInBigDecimalParameterNamed(maxBidPerctParamName, this.maxBidPerct)) {
			final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(maxBidPerctParamName);
			actualChanges.put(maxBidPerctParamName, newValue);
			this.maxBidPerct = newValue;
		}

		final String prizMemPenPerctParamName = "prizMemPenPerct";
		if (command.isChangeInBigDecimalParameterNamed(prizMemPenPerctParamName, this.prizMemPenPerct)) {
			final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(prizMemPenPerctParamName);
			actualChanges.put(prizMemPenPerctParamName, newValue);
			this.prizMemPenPerct = newValue;
		}

		final String nonPrizMemPenPerctParamName = "nonPrizMemPenPerct";
		if (command.isChangeInBigDecimalParameterNamed(nonPrizMemPenPerctParamName, this.nonPrizMemPenPerct)) {
			final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(nonPrizMemPenPerctParamName);
			actualChanges.put(nonPrizMemPenPerctParamName, newValue);
			this.nonPrizMemPenPerct = newValue;
		}

		final String fdrAcNumberParamName = "fdrAcNumber";
		if (command.isChangeInStringParameterNamed(fdrAcNumberParamName, this.fdrAcNumber)) {
			final String newValue = command.stringValueOfParameterNamed(fdrAcNumberParamName);
			actualChanges.put(fdrAcNumberParamName, newValue);
			this.fdrAcNumber = newValue;
		}

		final String fdrDepAmountParamName = "fdrDepAmount";
		if (command.isChangeInLongParameterNamed(fdrDepAmountParamName, this.fdrDepAmount)) {
			final Long newValue = command.longValueOfParameterNamed(fdrDepAmountParamName);
			actualChanges.put(fdrDepAmountParamName, newValue);
			this.fdrDepAmount = newValue;
		}

		final String fdrDurationParamName = "fdrDuration";
		if (command.isChangeInIntegerParameterNamed(fdrDurationParamName, this.fdrDuration)) {
			final Integer newValue = command.integerValueOfParameterNamed(fdrDurationParamName);
			actualChanges.put(fdrDurationParamName, newValue);
			this.fdrDuration = newValue;
		}

		final String fdrRatIntPerctParamName = "fdrRatIntPerct";
		if (command.isChangeInBigDecimalParameterNamed(fdrRatIntPerctParamName, this.fdrRatIntPerct)) {
			final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(fdrRatIntPerctParamName);
			actualChanges.put(fdrRatIntPerctParamName, newValue);
			this.fdrRatIntPerct = newValue;
		}
		
		final String fdrRateIntAmtParamName = "fdrRateIntAmt";
		if (command.isChangeInLongParameterNamed(fdrRateIntAmtParamName, this.fdrRateIntAmt)) {
			final Long newValue = command.longValueOfParameterNamed(fdrRateIntAmtParamName);
			actualChanges.put(fdrRateIntAmtParamName, newValue);
			this.fdrRateIntAmt = newValue;
		}

		final String fdrMatuAmountParamName = "fdrMatuAmount";
		if (command.isChangeInLongParameterNamed(fdrMatuAmountParamName, this.fdrMatuAmount)) {
			final Long newValue = command.longValueOfParameterNamed(fdrMatuAmountParamName);
			actualChanges.put(fdrMatuAmountParamName, newValue);
			this.fdrMatuAmount = newValue;
		}

		final String fdrIntPayCycleParamName = "fdrIntPayCycle";
		if (command.isChangeInStringParameterNamed(fdrIntPayCycleParamName, this.fdrIntPayCycle)) {
				final String newValue = command.stringValueOfParameterNamed(fdrIntPayCycleParamName);
				actualChanges.put(fdrIntPayCycleParamName, newValue);
				this.fdrIntPayCycle = newValue;
		}

		final String fdrBankNameParamName = "fdrBankName";
		if (command.isChangeInStringParameterNamed(fdrBankNameParamName, this.fdrBankName)) {
				final String newValue = command.stringValueOfParameterNamed(fdrBankNameParamName);
				actualChanges.put(fdrBankNameParamName, newValue);
				this.fdrBankName = newValue;
		}

		final String fdrBankBranchNameParamName = "fdrBankBranchName";
		if (command.isChangeInStringParameterNamed(fdrBankBranchNameParamName, this.fdrBankBranchName)) {
				final String newValue = command.stringValueOfParameterNamed(fdrBankBranchNameParamName);
				actualChanges.put(fdrBankBranchNameParamName, newValue);
				this.fdrBankBranchName = newValue;
		}

		final String fdrIssueDateParamName = "fdrIssueDate";
		if (command.isChangeInLocalDateParameterNamed(fdrIssueDateParamName, this.fdrIssueDate)) {
				final LocalDate newValue = command.localDateValueOfParameterNamed(fdrIssueDateParamName);
				actualChanges.put(fdrIssueDateParamName, newValue);
				this.fdrIssueDate = newValue;
		}

		final String fdrMatuDateParamName = "fdrMatuDate";
		if (command.isChangeInLocalDateParameterNamed(fdrMatuDateParamName, this.fdrMatuDate)) {
				final LocalDate newValue = command.localDateValueOfParameterNamed(fdrMatuDateParamName);
				actualChanges.put(fdrMatuDateParamName, newValue);
				this.fdrMatuDate = newValue;
		}

		final String psoAppDateParamName = "psoAppDate";
		if (command.isChangeInLocalDateParameterNamed(psoAppDateParamName, this.psoAppDate)) {
				final LocalDate newValue = command.localDateValueOfParameterNamed(psoAppDateParamName);
				actualChanges.put(psoAppDateParamName, newValue);
				this.psoAppDate = newValue;
		}
		final String psoIssueDateParamName = "psoIssueDate";
		if (command.isChangeInLocalDateParameterNamed(psoIssueDateParamName, this.psoIssueDate)) {
				final LocalDate newValue = command.localDateValueOfParameterNamed(psoIssueDateParamName);
				actualChanges.put(psoIssueDateParamName, newValue);
				this.psoIssueDate = newValue;
		}
		final String psoNumberParamName = "psoNumber";
		if (command.isChangeInStringParameterNamed(psoNumberParamName, this.psoNumber)) {
				final String newValue = command.stringValueOfParameterNamed(psoNumberParamName);
				actualChanges.put(psoNumberParamName, newValue);
				this.psoNumber = newValue;
		}
		final String ccAppDateParamName = "ccAppDate";
		if (command.isChangeInLocalDateParameterNamed(ccAppDateParamName, this.ccAppDate)) {
				final LocalDate newValue = command.localDateValueOfParameterNamed(ccAppDateParamName);
				actualChanges.put(ccAppDateParamName, newValue);
				this.ccAppDate = newValue;
		}
		final String ccIssueDateParamName = "ccIssueDate";
		if (command.isChangeInLocalDateParameterNamed(ccIssueDateParamName, this.ccIssueDate)) {
				final LocalDate newValue = command.localDateValueOfParameterNamed(ccIssueDateParamName);
				actualChanges.put(ccIssueDateParamName, newValue);
				this.ccIssueDate = newValue;
		}
		final String ccNumberParamName = "ccNumber";
		if (command.isChangeInStringParameterNamed(ccNumberParamName, this.ccNumber)) {
				final String newValue = command.stringValueOfParameterNamed(ccNumberParamName);
				actualChanges.put(ccNumberParamName, newValue);
				this.ccNumber = newValue;
		}

		return actualChanges;
	}

	// public Long getid() {
	// 	return id;
	// }

	public String getName() {
		return name;
	}
	
	  public Office office() {
	        return this.office;
	    }
	  
	  public Long getstaffid()
	  {
		  return this.staffid;
	  }
	  
	  public Long getauctiondayValue()
	  {
		  return this.auctiondayValue;
	  }
	  
	  public void changeOffice(final Office newOffice) {
	        this.office = newOffice;
	    }
	  

	  public Long officeId() {
	        return this.office.getId();
	    }

	@Override
	public String toString() {
		return "ChitGroup [amountDisbursed=" + amountDisbursed + ", amountNotDisbursed=" + amountNotDisbursed
				+ ", auctionday=" + auctionday + ", auctiondayType=" + auctiondayType + ", auctiondayValue="
				+ auctiondayValue + ", auctiontime=" + auctiontime + ", auctionweekValue=" + auctionweekValue
				+ ", ccAppDate=" + ccAppDate + ", ccIssueDate=" + ccIssueDate + ", ccNumber=" + ccNumber + ", chitaum="
				+ chitaum + ", chitcollectionfrequency=" + chitcollectionfrequency + ", chitcyclefrequency="
				+ chitcyclefrequency + ", chitduration=" + chitduration + ", chitvalue=" + chitvalue
				+ ", commissionEarned=" + commissionEarned + ", currentcycle=" + currentcycle + ", enrollmentFees="
				+ enrollmentFees + ", fdrAcNumber=" + fdrAcNumber + ", fdrBankBranchName=" + fdrBankBranchName
				+ ", fdrBankName=" + fdrBankName + ", fdrDepAmount=" + fdrDepAmount + ", fdrDuration=" + fdrDuration
				+ ", fdrIntPayCycle=" + fdrIntPayCycle + ", fdrIssueDate=" + fdrIssueDate + ", fdrMatuAmount="
				+ fdrMatuAmount + ", fdrMatuDate=" + fdrMatuDate + ", fdrRatIntPerct=" + fdrRatIntPerct
				+ ", fdrRateIntAmt=" + fdrRateIntAmt + ", maxBidPerct=" + maxBidPerct + ", minBidPerct=" + minBidPerct
				+ ", monthlycontribution=" + monthlycontribution + ", name=" + name + ", nextauctiondate="
				+ nextauctiondate + ", nonPrizMemPenPerct=" + nonPrizMemPenPerct + ", office=" + office
				+ ", prizMemPenPerct=" + prizMemPenPerct + ", psoAppDate=" + psoAppDate + ", psoIssueDate="
				+ psoIssueDate + ", psoNumber=" + psoNumber + ", staffid=" + staffid + ", startdate=" + startdate
				+ ", status=" + status + "]";
	}

		
	
}
