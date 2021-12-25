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

package org.apache.fineract.portfolio.client.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ClientFamilyMemberCommandFromApiJsonDeserializer {

    private final FromJsonHelper fromApiJsonHelper;
    private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("id", "clientId", "firstName", "middleName", "lastName",
            "qualificationId", "mobileNumber", "age", "isDependent", "relationshipId", "maritalStatusId", "genderId", "dateOfBirth",
            "professionId", "locale", "dateFormat", "familyMembers","isnominee","isnomineeaddr","nomadhar","nomsecondaryid","nomsecondaryidnum","nomhouseno",
           "nomstreetno", "nomarealocality", "nomtaluka" , "nomdistrict","nomstate","nompincode","nomvillage"));

    @Autowired
    private ClientFamilyMemberCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("FamilyMembers");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.extractArrayNamed("familyMembers", element) != null) {
            final JsonArray familyMembers = this.fromApiJsonHelper.extractJsonArrayNamed("familyMembers", element);
            baseDataValidator.reset().value(familyMembers).arrayNotEmpty();
        } else {
            baseDataValidator.reset().value(this.fromApiJsonHelper.extractJsonArrayNamed("familyMembers", element)).arrayNotEmpty();
        }

        validateForCreate(1, json);

    }

    public void validateForCreate(final long clientId, String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("FamilyMembers");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        baseDataValidator.reset().value(clientId).notBlank().integerGreaterThanZero();

        if (this.fromApiJsonHelper.extractStringNamed("firstName", element) != null) {
            final String firstName = this.fromApiJsonHelper.extractStringNamed("firstName", element);
            baseDataValidator.reset().parameter("firstName").value(firstName).notNull().notBlank().notExceedingLengthOf(100);
        } else {
            baseDataValidator.reset().parameter("firstName").value(this.fromApiJsonHelper.extractStringNamed("firstName", element))
                    .notNull().notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.extractStringNamed("lastName", element) != null) {
            final String lastName = this.fromApiJsonHelper.extractStringNamed("lastName", element);
            baseDataValidator.reset().parameter("lastName").value(lastName).notNull().notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.extractStringNamed("middleName", element) != null) {
            final String middleName = this.fromApiJsonHelper.extractStringNamed("middleName", element);
            baseDataValidator.reset().parameter("middleName").value(middleName).notNull().notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.extractLongNamed("qualificationId", element) != null) {
            final long qualificationId = this.fromApiJsonHelper.extractLongNamed("qualificationId", element);
            baseDataValidator.reset().parameter("qualificationId").value(qualificationId).notBlank().longGreaterThanZero();

        } else {
            baseDataValidator.reset().parameter("qualificationId").value(this.fromApiJsonHelper.extractLongNamed("qualificationId", element))
                    .notBlank().longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.extractStringNamed("mobileNumber", element) != null) {
            final String mobileNumber = this.fromApiJsonHelper.extractStringNamed("mobileNumber", element);
            baseDataValidator.reset().parameter("mobileNumber").value(mobileNumber).notNull().notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.extractBooleanNamed("isDependent", element) != null) {
            final Boolean isDependent = this.fromApiJsonHelper.extractBooleanNamed("isDependent", element);
            baseDataValidator.reset().parameter("isDependent").value(isDependent).notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractBooleanNamed("isnominee", element) != null) {
            final Boolean isnominee = this.fromApiJsonHelper.extractBooleanNamed("isnominee", element);
            baseDataValidator.reset().parameter("isnominee").value(isnominee).notNull().notBlank().notExceedingLengthOf(10);
        }

        if (this.fromApiJsonHelper.extractBooleanNamed("isnomineeaddr", element) != null) {
            final Boolean isnomineeaddr = this.fromApiJsonHelper.extractBooleanNamed("isnomineeaddr", element);
            baseDataValidator.reset().parameter("isnomineeaddr").value(isnomineeaddr).notNull().notBlank().notExceedingLengthOf(10);
        }

        if (this.fromApiJsonHelper.extractLongNamed("relationshipId", element) != null) {
            final long relationshipId = this.fromApiJsonHelper.extractLongNamed("relationshipId", element);
            baseDataValidator.reset().parameter("relationshipId").value(relationshipId).notBlank().longGreaterThanZero();

        } else {
            baseDataValidator.reset().parameter("relationshipId").value(this.fromApiJsonHelper.extractLongNamed("relationshipId", element))
                    .notBlank().longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.extractLongNamed("maritalStatusId", element) != null) {
            final long maritalStatusId = this.fromApiJsonHelper.extractLongNamed("maritalStatusId", element);
            baseDataValidator.reset().parameter("maritalStatusId").value(maritalStatusId).notBlank().longGreaterThanZero();

        }

        if (this.fromApiJsonHelper.extractLongNamed("genderId", element) != null) {
            final long genderId = this.fromApiJsonHelper.extractLongNamed("genderId", element);
            baseDataValidator.reset().parameter("genderId").value(genderId).notBlank().longGreaterThanZero();

        }

        if (this.fromApiJsonHelper.extractLongNamed("age", element) != null) {
            final long age = this.fromApiJsonHelper.extractLongNamed("age", element);
            baseDataValidator.reset().parameter("age").value(age).notBlank().longGreaterThanZero();

        }

        if (this.fromApiJsonHelper.extractLongNamed("professionId", element) != null) {
            final long professionId = this.fromApiJsonHelper.extractLongNamed("professionId", element);
            baseDataValidator.reset().parameter("professionId").value(professionId).notBlank().longGreaterThanZero();

        }

        if (this.fromApiJsonHelper.extractLocalDateNamed("dateOfBirth", element) != null) {
            final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed("dateOfBirth", element);
            baseDataValidator.reset().parameter("dateOfBirth").value(dateOfBirth).value(dateOfBirth).notNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());

        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomadhar", element) != null) {
            final String nomadhar = this.fromApiJsonHelper.extractStringNamed("nomadhar", element);
            baseDataValidator.reset().parameter("nomadhar").value(nomadhar).notNull().notBlank().notExceedingLengthOf(100);
        } else {
            baseDataValidator.reset().parameter("nomadhar").value(this.fromApiJsonHelper.extractStringNamed("nomadhar", element))
                    .notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomarealocality", element) != null) {
            final String nomarealocality = this.fromApiJsonHelper.extractStringNamed("nomarealocality", element);
            baseDataValidator.reset().parameter("nomarealocality").value(nomarealocality).notNull().notBlank().notExceedingLengthOf(100);
        } else {
            baseDataValidator.reset().parameter("nomarealocality").value(this.fromApiJsonHelper.extractStringNamed("nomarealocality", element))
                    .notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomtaluka", element) != null) {
            final Long nomtaluka = this.fromApiJsonHelper.extractLongNamed("nomtaluka", element);
            baseDataValidator.reset().parameter("nomtaluka").value(nomtaluka).notNull().notBlank().notExceedingLengthOf(100);
        } else {
            baseDataValidator.reset().parameter("nomtaluka").value(this.fromApiJsonHelper.extractStringNamed("nomtaluka", element))
                    .notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomdistrict", element) != null) {
            final Long nomdistrict = this.fromApiJsonHelper.extractLongNamed("nomdistrict", element);
            baseDataValidator.reset().parameter("nomdistrict").value(nomdistrict).notNull().notBlank().notExceedingLengthOf(100);
        } else {
            baseDataValidator.reset().parameter("nomdistrict").value(this.fromApiJsonHelper.extractStringNamed("nomdistrict", element))
                    .notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomstate", element) != null) {
            final Long nomdistrict = this.fromApiJsonHelper.extractLongNamed("nomstate", element);
            baseDataValidator.reset().parameter("nomstate").value(nomdistrict).notNull().notBlank().notExceedingLengthOf(100);
        } else {
            baseDataValidator.reset().parameter("nomstate").value(this.fromApiJsonHelper.extractStringNamed("nomstate", element))
                    .notNull().notBlank().notExceedingLengthOf(100);
        }
        
      
        
        if (this.fromApiJsonHelper.extractStringNamed("nomsecondaryidnum", element) != null) {
            final String nomsecondaryidnum = this.fromApiJsonHelper.extractStringNamed("nomsecondaryidnum", element);
            baseDataValidator.reset().parameter("nomsecondaryidnum").value(nomsecondaryidnum).notNull().notBlank().notExceedingLengthOf(100);
        } else {
            baseDataValidator.reset().parameter("nomsecondaryidnum").value(this.fromApiJsonHelper.extractStringNamed("nomsecondaryidnum", element))
                    .notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractLongNamed("nomsecondaryid", element) != null) {
            final Long nomsecondaryid = this.fromApiJsonHelper.extractLongNamed("nomsecondaryid", element);
            baseDataValidator.reset().parameter("nomsecondaryid").value(nomsecondaryid).notBlank().longGreaterThanZero();

        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomhouseno", element) != null) {
            final String nomhouseno = this.fromApiJsonHelper.extractStringNamed("nomhouseno", element);
            baseDataValidator.reset().parameter("nomhouseno").value(nomhouseno).notBlank();

        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomstreetno", element) != null) {
            final String nomstreetno = this.fromApiJsonHelper.extractStringNamed("nomstreetno", element);
            baseDataValidator.reset().parameter("nomstreetno").value(nomstreetno).notBlank();

        }
        
        if (this.fromApiJsonHelper.extractLongNamed("nompincode", element) != null) {
            final Long nompincode = this.fromApiJsonHelper.extractLongNamed("nompincode", element);
            baseDataValidator.reset().parameter("nompincode").value(nompincode).notBlank().longGreaterThanZero();

        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomvillage", element) != null) {
            final String nomvillage = this.fromApiJsonHelper.extractStringNamed("nomvillage", element);
            baseDataValidator.reset().parameter("nomvillage").value(nomvillage).notNull().notBlank().notExceedingLengthOf(100);
        }
        

    }

    public void validateForUpdate(final long familyMemberId, String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("FamilyMembers");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        baseDataValidator.reset().value(familyMemberId).notBlank().integerGreaterThanZero();

        if (this.fromApiJsonHelper.extractStringNamed("firstName", element) != null) {
            final String firstName = this.fromApiJsonHelper.extractStringNamed("firstName", element);
            baseDataValidator.reset().parameter("firstName").value(firstName).notNull().notBlank().notExceedingLengthOf(100);
        }

//        if (this.fromApiJsonHelper.extractStringNamed("lastName", element) != null) {
//            final String lastName = this.fromApiJsonHelper.extractStringNamed("lastName", element);
//            baseDataValidator.reset().parameter("lastName").value(lastName).notNull().notBlank().notExceedingLengthOf(100);
//        }

//        if (this.fromApiJsonHelper.extractStringNamed("middleName", element) != null) {
//            final String middleName = this.fromApiJsonHelper.extractStringNamed("middleName", element);
//            baseDataValidator.reset().parameter("middleName").value(middleName).notNull().notBlank().notExceedingLengthOf(100);
//        }

        if (this.fromApiJsonHelper.extractStringNamed("qualificationId", element) != null) {
            final String qualificationId = this.fromApiJsonHelper.extractStringNamed("qualificationId", element);
            baseDataValidator.reset().parameter("qualificationId").value(qualificationId).notNull().notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.extractLongNamed("relationshipId", element) != null) {
            final long relationshipId = this.fromApiJsonHelper.extractLongNamed("relationshipId", element);
            baseDataValidator.reset().parameter("relationshipId").value(relationshipId).notBlank().longGreaterThanZero();

        }

        if (this.fromApiJsonHelper.extractLongNamed("maritalStatusId", element) != null) {
            final long maritalStatusId = this.fromApiJsonHelper.extractLongNamed("maritalStatusId", element);
            baseDataValidator.reset().parameter("maritalStatusId").value(maritalStatusId).notBlank().longGreaterThanZero();

        }

        if (this.fromApiJsonHelper.extractLongNamed("genderId", element) != null) {
            final long genderId = this.fromApiJsonHelper.extractLongNamed("genderId", element);
            baseDataValidator.reset().parameter("genderId").value(genderId).longGreaterThanZero();

        }

        if (this.fromApiJsonHelper.extractLongNamed("professionId", element) != null) {
            final long professionId = this.fromApiJsonHelper.extractLongNamed("professionId", element);
            baseDataValidator.reset().parameter("professionId").value(professionId).longGreaterThanZero();

        }

        if (this.fromApiJsonHelper.extractLocalDateNamed("dateOfBirth", element) != null) {
            LocalDateTime currentDate = LocalDateTime.now(DateUtils.getDateTimeZoneOfTenant());

            final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed("dateOfBirth", element);
            baseDataValidator.reset().parameter("dateOfBirth").value(dateOfBirth).validateDateBefore(currentDate.toLocalDate());

        }
        
        if (this.fromApiJsonHelper.extractLongNamed("nomsecondaryid", element) != null) {
            final long nomsecondaryid = this.fromApiJsonHelper.extractLongNamed("nomsecondaryid", element);
            baseDataValidator.reset().parameter("nomsecondaryid").value(nomsecondaryid).longGreaterThanZero();

        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomhouseno", element) != null) {
            final String nomhouseno = this.fromApiJsonHelper.extractStringNamed("nomhouseno", element);
            baseDataValidator.reset().parameter("nomhouseno").value(nomhouseno);

        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomstreetno", element) != null) {
            final String nomstreetno = this.fromApiJsonHelper.extractStringNamed("nomstreetno", element);
            baseDataValidator.reset().parameter("nomstreetno").value(nomstreetno);

        }
        

        if (this.fromApiJsonHelper.extractStringNamed("nomadhar", element) != null) {
            final String nomadhar = this.fromApiJsonHelper.extractStringNamed("nomadhar", element);
            baseDataValidator.reset().parameter("nomadhar").value(nomadhar).notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomarealocality", element) != null) {
            final String nomarealocality = this.fromApiJsonHelper.extractStringNamed("nomarealocality", element);
            baseDataValidator.reset().parameter("nomarealocality").value(nomarealocality).notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomtaluka", element) != null) {
            final Long nomtaluka = this.fromApiJsonHelper.extractLongNamed("nomtaluka", element);
            baseDataValidator.reset().parameter("nomtaluka").value(nomtaluka).notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomdistrict", element) != null) {
            final Long nomdistrict = this.fromApiJsonHelper.extractLongNamed("nomdistrict", element);
            baseDataValidator.reset().parameter("nomdistrict").value(nomdistrict).notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomstate", element) != null) {
            final Long nomstate = this.fromApiJsonHelper.extractLongNamed("nomstate", element);
            baseDataValidator.reset().parameter("nomstate").value(nomstate).notNull().notBlank().notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomsecondaryidnum", element) != null) {
            final String nomsecondaryidnum = this.fromApiJsonHelper.extractStringNamed("nomsecondaryidnum", element);
            baseDataValidator.reset().parameter("nomsecondaryidnum").value(nomsecondaryidnum).notNull().notBlank().notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.extractLongNamed("nompincode", element) != null) {
            final long nompincode = this.fromApiJsonHelper.extractLongNamed("nompincode", element);
            baseDataValidator.reset().parameter("nompincode").value(nompincode).longGreaterThanZero();

        }
        
        if (this.fromApiJsonHelper.extractStringNamed("nomvillage", element) != null) {
            final String nomvillage = this.fromApiJsonHelper.extractStringNamed("nomvillage", element);
            baseDataValidator.reset().parameter("nomvillage").value(nomvillage).notNull().notBlank().notExceedingLengthOf(100);
        }

    }

    public void validateForDelete(final long familyMemberId) {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("FamilyMembers");

        // final JsonElement element = this.fromApiJsonHelper.parse(json);

        baseDataValidator.reset().value(familyMemberId).notBlank().integerGreaterThanZero();
    }

}
