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
package org.apache.fineract.portfolio.ChitGroup.serialization;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
//import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupReadPlatformService;


@Component
public final class ChitGroupCommandFromApiJsonDeserializer 
{
	  private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("name","officeId","staffid","startdate","chitcyclefrequency","chitcollectionfrequency", "chitduration","chitvalue","monthlycontribution","auctiondayValue","auctionday","auctiondayType","auctionweekValue","auctiontime",
				 "currentcycle","nextauctiondate", "status","commissionEarned","chitaum","amountDisbursed","amountNotDisbursed","enrollmentFees","minBidPerct", "maxBidPerct", "prizMemPenPerct", "nonPrizMemPenPerct", "fdrAcNumber", "fdrIssueDate", "fdrMatuDate", "fdrDepAmount", "fdrDuration", "fdrRatIntPerct", "fdrRateIntAmt", "fdrIntPayCycle", "fdrBankName", "fdrBankBranchName", "fdrMatuAmount", "psoAppDate", "psoIssueDate", "psoNumber", "ccAppDate", "ccIssueDate", "ccNumber", "dateFormat", "locale","endDate"));

	    private final FromJsonHelper fromApiJsonHelper;
	    
	    private final ChitGroupReadPlatformService chitGroupReadPlatformService; 
	    
	    @Autowired
	    private ChitGroupCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper,ChitGroupReadPlatformService chitGroupReadPlatformService)
	    {
	    	this.fromApiJsonHelper = fromApiJsonHelper;
	    	this.chitGroupReadPlatformService = chitGroupReadPlatformService;
	    }
	    
	    public void validateForCreate(final String json)
	    {
            ////System.out.println("------ in validateForCreate() ---  1 ");
            ////System.out.println(json);
	        if (StringUtils.isBlank(json)) 
	        {
	            throw new InvalidJsonException();
	        }
            ////System.out.println("------ in validateForCreate() ---  2 ");

		    final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
	        ////System.out.println("------ in validateForCreate() ---  3 ");
	        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("ChitGroup");
	        
	        final JsonElement element = this.fromApiJsonHelper.parse(json);
	        
	
	        final Long id = this.fromApiJsonHelper.extractLongNamed("id", element);
	        baseDataValidator.reset().parameter("id").value(id);
	        
	        // final Long staffid = this.fromApiJsonHelper.extractLongNamed("staffid", element);
	        // baseDataValidator.reset().parameter("staffid").value(staffid);
	        
	        final Long chitduration = this.fromApiJsonHelper.extractLongNamed("chitduration", element);
	        baseDataValidator.reset().parameter("chitduration").value(chitduration);
	        
	        final Long chitvalue = this.fromApiJsonHelper.extractLongNamed("chitvalue", element);
	        baseDataValidator.reset().parameter("chitvalue").value(chitvalue);
	        
	        final Long monthlycontribution = this.fromApiJsonHelper.extractLongNamed("monthlycontribution", element);
	        baseDataValidator.reset().parameter("monthlycontribution").value(monthlycontribution);
	        
	        final Long auctiondayValue = this.fromApiJsonHelper.extractLongNamed("auctiondayValue", element);
	        baseDataValidator.reset().parameter("auctiondayValue").value(auctiondayValue);
	        
	        
	        final String auctionday = this.fromApiJsonHelper.extractStringNamed("auctionday", element);
	        baseDataValidator.reset().parameter("auctionday").value(auctionday);
	        
	        final Long currentcycle = this.fromApiJsonHelper.extractLongNamed("currentcycle", element);
	        baseDataValidator.reset().parameter("currentcycle").value(currentcycle);

            final LocalDate nextauctiondate = this.fromApiJsonHelper.extractLocalDateNamed("nextauctiondate", element);
	        baseDataValidator.reset().parameter("nextauctiondate").value(nextauctiondate);
	        
	        final Long status = this.fromApiJsonHelper.extractLongNamed("status", element);
	        baseDataValidator.reset().parameter("status").value(status);

	        final Long commissionEarned = this.fromApiJsonHelper.extractLongNamed("commissionEarned", element);
	        baseDataValidator.reset().parameter("commissionEarned").value(commissionEarned);
	        
	        final Long chitaum = this.fromApiJsonHelper.extractLongNamed("chitaum", element);
	        baseDataValidator.reset().parameter("chitaum").value(chitaum);
	        
	        final Long amountDisbursed = this.fromApiJsonHelper.extractLongNamed("amountDisbursed", element);
	        baseDataValidator.reset().parameter("amountDisbursed").value(amountDisbursed);
	
	        
	        final Long amountNotDisbursed = this.fromApiJsonHelper.extractLongNamed("amountNotDisbursed", element);
	        baseDataValidator.reset().parameter("amountNotDisbursed").value(amountNotDisbursed);
	        
	        final Long enrollmentFees = this.fromApiJsonHelper.extractLongNamed("enrollmentFees", element);
	        baseDataValidator.reset().parameter("enrollmentFees").value(enrollmentFees);
	        
	        final String name = this.fromApiJsonHelper.extractStringNamed("name", element);
	        baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(50);
	        
	        final String chitcyclefrequency = this.fromApiJsonHelper.extractStringNamed("chitcyclefrequency", element);
	        baseDataValidator.reset().parameter("chitcyclefrequency").value(chitcyclefrequency);
	        
	        final String chitcollectionfrequency = this.fromApiJsonHelper.extractStringNamed("chitcollectionfrequency", element);
	        baseDataValidator.reset().parameter("chitcollectionfrequency").value(chitcollectionfrequency);

            final String auctiondayType = this.fromApiJsonHelper.extractStringNamed("auctiondayType", element);
	        baseDataValidator.reset().parameter("auctiondayType").value(auctiondayType);
	        
	        final String auctionweekValue = this.fromApiJsonHelper.extractStringNamed("auctionweekValue", element);
	        baseDataValidator.reset().parameter("auctionweekValue").value(auctionweekValue);
	        
	        final Long officeId = this.fromApiJsonHelper.extractLongNamed("officeId", element);
	        baseDataValidator.reset().parameter("officeId").value(officeId).notNull().integerGreaterThanZero();
	        
	        if (this.fromApiJsonHelper.parameterExists("locale", element)) {
	            final String locale = this.fromApiJsonHelper.extractStringNamed("locale", element);
	            baseDataValidator.reset().parameter("locale").value(locale).notBlank();
	        }
	        
	        if (this.fromApiJsonHelper.parameterExists("dateFormat", element)) {
	            final String dateFormat = this.fromApiJsonHelper.extractStringNamed("dateFormat", element);
	            baseDataValidator.reset().parameter("dateFormat").value(dateFormat).notBlank();
	        }
	        	        
	        final LocalDate startdate = this.fromApiJsonHelper.extractLocalDateNamed("startdate", element);
	        baseDataValidator.reset().parameter("startdate").value(startdate).notNull();
	        
            final String sAucTime = this.fromApiJsonHelper.extractStringNamed("auctiontime", element);
	        final LocalTime auctiontime = LocalTime.parse(sAucTime);
	        baseDataValidator.reset().parameter("auctiontime").value(auctiontime);

            final BigDecimal minBidPerct = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("minBidPerct", element);
            baseDataValidator.reset().parameter("minBidPerct").value(minBidPerct);

            final BigDecimal maxBidPerct = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("maxBidPerct", element);
            baseDataValidator.reset().parameter("maxBidPerct").value(maxBidPerct);

            final BigDecimal prizMemPenPerct = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("prizMemPenPerct", element);
            baseDataValidator.reset().parameter("prizMemPenPerct").value(prizMemPenPerct);

            final BigDecimal nonPrizMemPenPerct = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("nonPrizMemPenPerct", element);
            baseDataValidator.reset().parameter("nonPrizMemPenPerct").value(nonPrizMemPenPerct);

            final String fdrAcNumber = this.fromApiJsonHelper.extractStringNamed("fdrAcNumber", element);
            baseDataValidator.reset().parameter("fdrAcNumber").value(fdrAcNumber);

            final LocalDate fdrIssueDate = this.fromApiJsonHelper.extractLocalDateNamed("fdrIssueDate", element);
            baseDataValidator.reset().parameter("fdrIssueDate").value(fdrIssueDate);

            final LocalDate fdrMatuDate = this.fromApiJsonHelper.extractLocalDateNamed("fdrMatuDate", element);
            baseDataValidator.reset().parameter("fdrMatuDate").value(fdrMatuDate);

            final Long fdrDepAmount = this.fromApiJsonHelper.extractLongNamed("fdrDepAmount", element);
            baseDataValidator.reset().parameter("fdrDepAmount").value(fdrDepAmount);

            final Integer fdrDuration = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("fdrDuration", element);
            baseDataValidator.reset().parameter("fdrDuration").value(fdrDuration);

            final BigDecimal fdrRatIntPerct = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("fdrRatIntPerct", element);
            baseDataValidator.reset().parameter("fdrRatIntPerct").value(fdrRatIntPerct);

            final Long fdrRateIntAmt = this.fromApiJsonHelper.extractLongNamed("fdrRateIntAmt", element);
            baseDataValidator.reset().parameter("fdrRateIntAmt").value(fdrRateIntAmt);

            final String fdrIntPayCycle = this.fromApiJsonHelper.extractStringNamed("fdrIntPayCycle", element);
            baseDataValidator.reset().parameter("fdrIntPayCycle").value(fdrIntPayCycle);

            final String fdrBankName = this.fromApiJsonHelper.extractStringNamed("fdrBankName", element);
            baseDataValidator.reset().parameter("fdrBankName").value(fdrBankName);

            final String fdrBankBranchName = this.fromApiJsonHelper.extractStringNamed("fdrBankBranchName", element);
            baseDataValidator.reset().parameter("fdrBankBranchName").value(fdrBankBranchName);

            final Long fdrMatuAmount = this.fromApiJsonHelper.extractLongNamed("fdrMatuAmount", element);
            baseDataValidator.reset().parameter("fdrMatuAmount").value(fdrMatuAmount);
            
            final LocalDate psoAppDate = this.fromApiJsonHelper.extractLocalDateNamed("psoAppDate", element);
            baseDataValidator.reset().parameter("psoAppDate").value(psoAppDate);
            final LocalDate psoIssueDate = this.fromApiJsonHelper.extractLocalDateNamed("psoIssueDate", element);
            baseDataValidator.reset().parameter("psoIssueDate").value(psoIssueDate);
            final String psoNumber = this.fromApiJsonHelper.extractStringNamed("psoNumber", element);
            baseDataValidator.reset().parameter("psoNumber").value(psoNumber);
            final LocalDate ccAppDate = this.fromApiJsonHelper.extractLocalDateNamed("ccAppDate", element);
            baseDataValidator.reset().parameter("ccAppDate").value(ccAppDate);
            final LocalDate ccIssueDate = this.fromApiJsonHelper.extractLocalDateNamed("ccIssueDate", element);
            baseDataValidator.reset().parameter("ccIssueDate").value(ccIssueDate);
            final String ccNumber = this.fromApiJsonHelper.extractStringNamed("ccNumber", element);
            baseDataValidator.reset().parameter("ccNumber").value(ccNumber);

	        throwExceptionIfValidationWarningsExist(dataValidationErrors);
	    }
        public void validateForUpdate(final String json) 
        {
            validateForUpdate(json, null);
        }
        
        public void validateForUpdate(final String json, Long id)
        {
            if (StringUtils.isBlank(json)) 
            {
                throw new InvalidJsonException();
            }
            final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
            this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
            
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("ChitGroup");
            final JsonElement element = this.fromApiJsonHelper.parse(json);
            
            if (this.fromApiJsonHelper.parameterExists("name", element)) {
                final String name = this.fromApiJsonHelper.extractStringNamed("name", element);
                baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(50);
            }
            
            if (this.fromApiJsonHelper.parameterExists("chitcyclefrequency", element)) {
                final String chitcyclefrequency = this.fromApiJsonHelper.extractStringNamed("chitcyclefrequency", element);
                baseDataValidator.reset().parameter("chitcyclefrequency").value(chitcyclefrequency).notExceedingLengthOf(20);
            }
            
            if (this.fromApiJsonHelper.parameterExists("chitcollectionfrequency", element)) {
                final String chitcollectionfrequency = this.fromApiJsonHelper.extractStringNamed("chitcollectionfrequency", element);
                baseDataValidator.reset().parameter("chitcollectionfrequency").value(chitcollectionfrequency).notExceedingLengthOf(20);
            }

            if (this.fromApiJsonHelper.parameterExists("auctiondayValue", element)) {
                final Long auctiondayValue = this.fromApiJsonHelper.extractLongNamed("auctiondayValue", element);
                baseDataValidator.reset().parameter("auctiondayValue").value(auctiondayValue).notNull().integerGreaterThanZero();
            }
            
            if (this.fromApiJsonHelper.parameterExists("auctionday", element)) {
                final String auctionday = this.fromApiJsonHelper.extractStringNamed("auctionday", element);
                baseDataValidator.reset().parameter("auctionday").value(auctionday).notExceedingLengthOf(50);
            }
            
            if (this.fromApiJsonHelper.parameterExists("auctiondayType", element)) {
                final String auctiondayType = this.fromApiJsonHelper.extractStringNamed("auctiondayType", element);
                baseDataValidator.reset().parameter("auctiondayType").value(auctiondayType).notExceedingLengthOf(50);
            }
            
            if (this.fromApiJsonHelper.parameterExists("auctionweekValue", element)) {
                final String auctionweekValue = this.fromApiJsonHelper.extractStringNamed("auctionweekValue", element);
                baseDataValidator.reset().parameter("auctionweekValue").value(auctionweekValue).notExceedingLengthOf(50);
            }
            
            if (this.fromApiJsonHelper.parameterExists("officeId", element)) {
                final Long officeId = this.fromApiJsonHelper.extractLongNamed("officeId", element);
                baseDataValidator.reset().parameter("officeId").value(officeId).notNull().integerGreaterThanZero();
            }
            
            // if (this.fromApiJsonHelper.parameterExists("staffid", element)) {
            //     final Long staffid = this.fromApiJsonHelper.extractLongNamed("staffid", element);
            //     baseDataValidator.reset().parameter("staffid").value(staffid).notNull().integerGreaterThanZero();
            // }
            if (this.fromApiJsonHelper.parameterExists("chitduration", element)) {
                final Long chitduration = this.fromApiJsonHelper.extractLongNamed("chitduration", element);
                baseDataValidator.reset().parameter("chitduration").value(chitduration).notNull().integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists("chitvalue", element)) {
                final Long chitvalue = this.fromApiJsonHelper.extractLongNamed("chitvalue", element);
                baseDataValidator.reset().parameter("chitvalue").value(chitvalue).notNull().integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists("monthlycontribution", element)) {
                final Long monthlycontribution = this.fromApiJsonHelper.extractLongNamed("monthlycontribution", element);
                baseDataValidator.reset().parameter("monthlycontribution").value(monthlycontribution).notNull().integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists("currentcycle", element)) {
                final Long currentcycle = this.fromApiJsonHelper.extractLongNamed("currentcycle", element);
                baseDataValidator.reset().parameter("currentcycle").value(currentcycle).notNull().integerGreaterThanZero();
            }

            if (this.fromApiJsonHelper.parameterExists("status", element)) {
                final Long status = this.fromApiJsonHelper.extractLongNamed("status", element);
                baseDataValidator.reset().parameter("status").value(status).notNull().integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists("commissionEarned", element)) {
                final Long commissionEarned = this.fromApiJsonHelper.extractLongNamed("commissionEarned", element);
                baseDataValidator.reset().parameter("commissionEarned").value(commissionEarned).notNull().integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists("chitaum", element)) {
                final Long chitaum = this.fromApiJsonHelper.extractLongNamed("chitaum", element);
                baseDataValidator.reset().parameter("chitaum").value(chitaum).notNull().integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists("amountDisbursed", element)) {
                final Long amountDisbursed = this.fromApiJsonHelper.extractLongNamed("amountDisbursed", element);
                baseDataValidator.reset().parameter("amountDisbursed").value(amountDisbursed).notNull().integerGreaterThanZero();
            }
            if (this.fromApiJsonHelper.parameterExists("amountNotDisbursed", element)) {
                final Long amountNotDisbursed = this.fromApiJsonHelper.extractLongNamed("amountNotDisbursed", element);
                baseDataValidator.reset().parameter("amountNotDisbursed").value(amountNotDisbursed).notNull().integerGreaterThanZero();
            }
            
            if (this.fromApiJsonHelper.parameterExists("enrollmentFees", element)) {
                final Long enrollmentFees = this.fromApiJsonHelper.extractLongNamed("enrollmentFees", element);
                baseDataValidator.reset().parameter("enrollmentFees").value(enrollmentFees).notNull().integerGreaterThanZero();
            }
            
            throwExceptionIfValidationWarningsExist(dataValidationErrors);
        }
        private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
            if (!dataValidationErrors.isEmpty()) {
                throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                        dataValidationErrors);
            }
        }
         
}		
