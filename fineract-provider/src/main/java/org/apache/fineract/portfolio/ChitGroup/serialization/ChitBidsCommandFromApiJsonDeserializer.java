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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.ChitGroup.service.ChitBidsReadPlatformService;
//import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

    @SuppressWarnings("unused")
    @Component
    public final class ChitBidsCommandFromApiJsonDeserializer 
    {
        private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("chitSubscriberId", "chitCycleId", "bidAmount", "bidWon" ,"bidderparticipationId","bidDate","locale","dateFormat","isPaid"));
                    
        private final FromJsonHelper fromApiJsonHelper;
            
        private final ChitBidsReadPlatformService chitBidsReadPlatformService; 
	    
	    @Autowired
	    private ChitBidsCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper,ChitBidsReadPlatformService chitBidsReadPlatformService)
	    {
	    	this.fromApiJsonHelper = fromApiJsonHelper;
	    	this.chitBidsReadPlatformService = chitBidsReadPlatformService;
	    }
	    
	    public void validateForCreate(final String json)
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
	  
	        final Long chitSubscriberId = this.fromApiJsonHelper.extractLongNamed("chitSubscriberId", element);
	        baseDataValidator.reset().parameter("chitSubscriberId").value(chitSubscriberId).notNull().integerGreaterThanZero();

            final Long chitCycleId = this.fromApiJsonHelper.extractLongNamed("chitCycleId", element);
	        baseDataValidator.reset().parameter("chitCycleId").value(chitCycleId).notNull().integerGreaterThanZero();

//	        final Long bidAmount = this.fromApiJsonHelper.extractf("bidAmount", element);
//	        baseDataValidator.reset().parameter("bidAmount").value(bidAmount).notNull().integerGreaterThanZero();

	        final Boolean bidWon = this.fromApiJsonHelper.extractBooleanNamed("bidWon", element);
	        baseDataValidator.reset().parameter("bidWon").value(bidWon).validateForBooleanValue();

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
            
            // if (this.fromApiJsonHelper.parameterExists("chitSubscriberId", element)) {
            //     final Long chitSubscriberId = this.fromApiJsonHelper.extractLongNamed("chitSubscriberId", element);
            //     baseDataValidator.reset().parameter("chitSubscriberId").value(chitSubscriberId).notNull().integerGreaterThanZero();
            // }
            
            // if (this.fromApiJsonHelper.parameterExists("chitCycleId", element)) {
            //     final Long chitCycleId = this.fromApiJsonHelper.extractLongNamed("chitCycleId", element);
            //     baseDataValidator.reset().parameter("chitCycleId").value(chitCycleId).notNull().integerGreaterThanZero();
            // }

//            if (this.fromApiJsonHelper.parameterExists("bidAmount", element)) {
//                final Long bidAmount = this.fromApiJsonHelper.extractLongNamed("bidAmount", element);
//                baseDataValidator.reset().parameter("bidAmount").value(bidAmount).notNull().integerGreaterThanZero();
//            }

            if (this.fromApiJsonHelper.parameterExists("bidWon", element)) {
                final Boolean bidWon = this.fromApiJsonHelper.extractBooleanNamed("bidWon", element);
                baseDataValidator.reset().parameter("bidWon").value(bidWon).validateForBooleanValue();
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
