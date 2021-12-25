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
package org.apache.fineract.portfolio.PaymentStatus.serialization;

import java.lang.reflect.Type;
import java.time.LocalDate;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class PaymentStatusCommandFromApiJsonDeserializer {

	private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("paymentType","status","tranId","officeId",
			   "dateFormat", "locale","endDate"));

   private final FromJsonHelper fromApiJsonHelper;
   
   
   @Autowired
   private PaymentStatusCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper)
   {
   	this.fromApiJsonHelper = fromApiJsonHelper;
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
       final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("PaymentStatus");
       
       final JsonElement element = this.fromApiJsonHelper.parse(json);

       final Long id = this.fromApiJsonHelper.extractLongNamed("id", element);
       baseDataValidator.reset().parameter("id").value(id);
       
       final Long paymentStatus = this.fromApiJsonHelper.extractLongNamed("paymentStatus", element);
       baseDataValidator.reset().parameter("paymentStatus").value(paymentStatus);
       
       final Long status = this.fromApiJsonHelper.extractLongNamed("status", element);
       baseDataValidator.reset().parameter("status").value(status);
      
       final LocalDate date = this.fromApiJsonHelper.extractLocalDateNamed("date", element);
       baseDataValidator.reset().parameter("date").value(date);
       
       final Long tranId = this.fromApiJsonHelper.extractLongNamed("tranId", element);
       baseDataValidator.reset().parameter("tranId").value(tranId);
       
       final Long officeId = this.fromApiJsonHelper.extractLongNamed("officeId", element);
       baseDataValidator.reset().parameter("officeId").value(tranId);
       
       
       // final Long staffid = this.fromApiJsonHelper.extractLongNamed("staffid", element);
       // baseDataValidator.reset().parameter("staffid").value(staffid);

       throwExceptionIfValidationWarningsExist(dataValidationErrors);
   }


		private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		    if (!dataValidationErrors.isEmpty()) {
		        throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
		                dataValidationErrors);
		    }
		}
 

}
