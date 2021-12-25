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

package org.apache.fineract.portfolio.creditreport.serialization;


import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

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
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class CreditReportCommandFromApiJsonDeserializer {

    private final FromJsonHelper fromApiJsonHelper;
    private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("id", "clientId", "bureau", "scoretype", "scorevalue",
            "scorecomments", "reportid", "dateofissue"));

    @Autowired
    private CreditReportCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }


    public void validateForCreate(final long clientId, String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("CredReport");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        baseDataValidator.reset().value(clientId).notBlank().integerGreaterThanZero();

        if (this.fromApiJsonHelper.extractStringNamed("bureau", element) != null) {
            final String bureau = this.fromApiJsonHelper.extractStringNamed("bureau", element);
            baseDataValidator.reset().parameter("bureau").value(bureau).notExceedingLengthOf(100);
        } else {
            baseDataValidator.reset().parameter("bureau").value(this.fromApiJsonHelper.extractStringNamed("bureau", element))
                    .notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.extractStringNamed("scoretype", element) != null) {
            final String scoretype = this.fromApiJsonHelper.extractStringNamed("scoretype", element);
            baseDataValidator.reset().parameter("scoretype").value(scoretype).notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.extractStringNamed("scorevalue", element) != null) {
            final String scorevalue = this.fromApiJsonHelper.extractStringNamed("scorevalue", element);
            baseDataValidator.reset().parameter("scorevalue").value(scorevalue).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("scorecomments", element) != null) {
            final String scorecomments = this.fromApiJsonHelper.extractStringNamed("scorecomments", element);
            baseDataValidator.reset().parameter("scorecomments").value(scorecomments).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("reportid", element) != null) {
            final String reportid = this.fromApiJsonHelper.extractStringNamed("reportid", element);
            baseDataValidator.reset().parameter("reportid").value(reportid).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("dateofissue", element) != null) {
            final String dateofissue = this.fromApiJsonHelper.extractStringNamed("dateofissue", element);
            baseDataValidator.reset().parameter("dateofissue").value(dateofissue).notExceedingLengthOf(100);
        }

    }

   

}
