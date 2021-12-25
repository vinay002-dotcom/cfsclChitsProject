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
package org.apache.fineract.portfolio.client.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
//import org.apache.fineract.portfolio.client.data.ClientApiCollectionConstants;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import java.time.LocalDate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class ClientDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ClientDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiCollectionConstants.CLIENT_CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.clientNonPersonDetailsParamName, element)) {
	        final String clientNonPersonJson = this.fromApiJsonHelper.toJson(element.getAsJsonObject().get(ClientApiConstants.clientNonPersonDetailsParamName));
	        if(clientNonPersonJson != null)
	        	this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, clientNonPersonJson, ClientApiCollectionConstants.CLIENT_NON_PERSON_CREATE_REQUEST_DATA_PARAMETERS);
        }
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final Long officeId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.officeIdParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.officeIdParamName).value(officeId).notNull().integerGreaterThanZero();

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.groupIdParamName, element)) {
            final Long groupId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.groupIdParamName).value(groupId).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.staffIdParamName, element)) {
            final Long staffId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.staffIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.staffIdParamName).value(staffId).ignoreIfNull().longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.accountNoParamName, element)) {
            final String accountNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.accountNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.accountNoParamName).value(accountNo).notBlank().notExceedingLengthOf(20);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.savingsProductIdParamName, element)) {
            final Long savingsProductId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.savingsProductIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.savingsProductIdParamName).value(savingsProductId).ignoreIfNull()
                    .longGreaterThanZero();
            /*if (savingsProductId != null && this.fromApiJsonHelper.parameterExists(ClientApiConstants.datatables, element)) {
                final JsonArray datatables = this.fromApiJsonHelper.extractJsonArrayNamed(ClientApiConstants.datatables, element);
                if (datatables.size() > 0) {
                    baseDataValidator.reset().parameter(ClientApiConstants.savingsProductIdParamName).value(savingsProductId)
                            .failWithCodeNoParameterAddedToErrorCode("should.not.be.used.with.datatables.parameter");
                }
            }*/
        }

        if (isFullnameProvided(element) || isIndividualNameProvided(element)) {

            // 1. No individual name part provided and fullname provided
            if (isFullnameProvided(element) && !isIndividualNameProvided(element)) {
                fullnameCannotBeBlank(element, baseDataValidator);
            }

            // 2. no fullname provided and individual name part provided
            if (isIndividualNameProvided(element) && !isFullnameProvided(element)) {
                validateRequiredIndividualNamePartsExist(element, baseDataValidator);
            }

            // 3. both provided
            if (isFullnameProvided(element) && isIndividualNameProvided(element)) {
                validateIndividualNamePartsCannotBeUsedWithFullname(element, baseDataValidator);
            }
        } else {

            if (isFullnameParameterPassed(element) || isIndividualNamePartParameterPassed(element)) {

                // 1. No individual name parameter passed and fullname passed
                if (isFullnameParameterPassed(element) && !isIndividualNamePartParameterPassed(element)) {
                    fullnameCannotBeBlank(element, baseDataValidator);
                }

                // 2. no fullname passed and individual name part passed
                if (isIndividualNamePartParameterPassed(element) && !isFullnameParameterPassed(element)) {
                    validateRequiredIndividualNamePartsExist(element, baseDataValidator);
                }

                // 3. both parameter types passed
                if (isFullnameParameterPassed(element) && isIndividualNamePartParameterPassed(element)) {
                    baseDataValidator.reset().parameter(ClientApiConstants.idParamName).failWithCode(".no.name.details.passed");
                }

            } else {
                baseDataValidator.reset().parameter(ClientApiConstants.idParamName).failWithCode(".no.name.details.passed");
            }
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.maidennameParamName, element)) {
            final String maidenname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.maidennameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.maidennameParamName).value(maidenname).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.custmothernameParamName, element)) {
            final String custmothername = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.custmothernameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.custmothernameParamName).value(custmothername).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.adharParamName, element)) {
            final String adhar = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.adharParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.adharParamName).value(adhar).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.alternateMobileNoParamName, element)) {
            final String alternateMobileNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.alternateMobileNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.alternateMobileNoParamName).value(alternateMobileNo).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.secIdProofNoParamName, element)) {
            final String secIdProofNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.secIdProofNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.secIdProofNoParamName).value(secIdProofNo).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.secaddressproofnoParamName, element)) {
            final String secaddressproofno = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.secaddressproofnoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.secaddressproofnoParamName).value(secaddressproofno).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedmobileParamName, element)) {
            final String lastverifiedmobile = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastverifiedmobileParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedmobileParamName).value(lastverifiedmobile).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.otherexpensestfParamName, element)) {
            final String otherexpensestf = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.otherexpensestfParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.otherexpensestfParamName).value(otherexpensestf).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.othersrcinctfParamName, element)) {
            final String othersrcinctf = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.othersrcinctfParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.othersrcinctfParamName).value(othersrcinctf).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }

        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.otherobligationsParamName, element)) {
            final String otherobligations = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.otherobligationsParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.otherobligationsParamName).value(otherobligations).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedsecondaryidParamName, element)) {
            final String lastverifiedsecondaryid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastverifiedsecondaryidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedsecondaryidParamName).value(lastverifiedsecondaryid).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nregaParamName, element)) {
            final String nrega = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nregaParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nregaParamName).value(nrega).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.panParamName, element)) {
            final String pan = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.panParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.panParamName).value(pan).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIdParamName).value(externalId).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.externalIddParamName, element)) {
            final String externalIdd = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIddParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIddParamName).value(externalIdd).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.idproofNoParamName, element)) {
            final String idproofNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.idproofNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.idproofNoParamName).value(idproofNo).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.addrproofNoParamName, element)) {
            final String addrproofNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.addrproofNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.addrproofNoParamName).value(addrproofNo).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.mobileNoParamName, element)) {
            final String mobileNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.mobileNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.mobileNoParamName).value(mobileNo).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.gstNoParamName, element)) {
            final String gstNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.gstNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.gstNoParamName).value(gstNo).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.ageParamName, element)) {
            final String age = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.ageParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.ageParamName).value(age).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomrelationshipidParamName, element)) {
            final String nomrelationshipid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomrelationshipidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomrelationshipidParamName).value(nomrelationshipid).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomgenderidParamName, element)) {
            final String nomgenderid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomgenderidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomgenderidParamName).value(nomgenderid).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomageParamName, element)) {
            final String nomage = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomageParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomageParamName).value(nomage).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomprofessionidParamName, element)) {
            final String nomprofessionid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomprofessionidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomprofessionidParamName).value(nomprofessionid).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomeducationalidParamName, element)) {
            final String nomeducationalid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomeducationalidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomeducationalidParamName).value(nomeducationalid).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nommaritalidParamName, element)) {
            final String nommaritalid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nommaritalidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nommaritalidParamName).value(nommaritalid).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incdailysalesParamName, element)) {
            final String incdailysales = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incdailysalesParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incdailysalesParamName).value(incdailysales).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exprawmaterialParamName, element)) {
            final String exprawmaterial = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exprawmaterialParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.exprawmaterialParamName).value(exprawmaterial).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expstaffsalParamName, element)) {
            final String expstaffsal = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expstaffsalParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expstaffsalParamName).value(expstaffsal).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exppowertelephoneParamName, element)) {
            final String exppowertelephone = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exppowertelephoneParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.exppowertelephoneParamName).value(exppowertelephone).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exprepairsmaintainanceParamName, element)) {
            final String exprepairsmaintainance = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exprepairsmaintainanceParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.exprepairsmaintainanceParamName).value(exprepairsmaintainance).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expcommbrokerageParamName, element)) {
            final String expcommbrokerage = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expcommbrokerageParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expcommbrokerageParamName).value(expcommbrokerage).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.increntParamName, element)) {
            final String incrent = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.increntParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.increntParamName).value(incrent).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incinterestParamName, element)) {
            final String incinterest = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incinterestParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incinterestParamName).value(incinterest).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incothersParamName, element)) {
            final String incothers = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incothersParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incothersParamName).value(incothers).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.tothouseholdincParamName, element)) {
            final String tothouseholdinc = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.tothouseholdincParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.tothouseholdincParamName).value(tothouseholdinc).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exphouseholdParamName, element)) {
            final String exphousehold = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exphouseholdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.exphouseholdParamName).value(exphousehold).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expotherloansParamName, element)) {
            final String expotherloans = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expotherloansParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expotherloansParamName).value(expotherloans).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.totnetdispfamilyParamName, element)) {
            final String totnetdispfamily = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.totnetdispfamilyParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.totnetdispfamilyParamName).value(totnetdispfamily).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }

      
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expinterestParamName, element)) {
            final String expinterest = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expinterestParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expinterestParamName).value(expinterest).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expofficerentParamName, element)) {
            final String expofficerent = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expofficerentParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expofficerentParamName).value(expofficerent).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exptravelParamName, element)) {
            final String exptravel = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exptravelParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.exptravelParamName).value(exptravel).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expothersParamName, element)) {
            final String expothers = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expothersParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expothersParamName).value(expothers).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.totbusinessprofitParamName, element)) {
            final String totbusinessprofit = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.totbusinessprofitParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.totbusinessprofitParamName).value(totbusinessprofit).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incspouseParamName, element)) {
            final String incspouse = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incspouseParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incspouseParamName).value(incspouse).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
        
       
        
                  
       /* if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.groupParamName, element)) {
            final Long group = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.groupParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.groupParamName).value(group).ignoreIfNull()
                    .notExceedingLengthOf(500);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.centerParamName, element)) {
            final Long center = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.centerParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.centerParamName).value(center).ignoreIfNull()
                    .notExceedingLengthOf(500);
        }*/

        final Boolean active = this.fromApiJsonHelper.extractBooleanNamed(ClientApiConstants.activeParamName, element);
        if (active != null) {
            if (active.booleanValue()) {
                final LocalDate joinedDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.activationDateParamName,
                        element);
                baseDataValidator.reset().parameter(ClientApiConstants.activationDateParamName).value(joinedDate).notNull();
                /*if(this.fromApiJsonHelper.parameterExists(ClientApiConstants.datatables,element)){
                    baseDataValidator.reset().parameter(ClientApiConstants.activeParamName).value(active)
                            .failWithCodeNoParameterAddedToErrorCode("should.not.be.used.with.datatables.parameter");
                }*/
            }
        } else {
            baseDataValidator.reset().parameter(ClientApiConstants.activeParamName).value(active).trueOrFalseRequired(false);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.submittedOnDateParamName, element)) {
            final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.submittedOnDateParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.submittedOnDateParamName).value(submittedOnDate).notNull();
        }
        
      

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.dateOfBirthParamName, element)) {
            final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.dateOfBirthParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.dateOfBirthParamName).value(dateOfBirth).notNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedSecondaryidDateParamName, element)) {
            final LocalDate lastverifiedSecondaryidDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.lastverifiedSecondaryidDateParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedSecondaryidDateParamName).value(lastverifiedSecondaryidDate).notNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedmobiledateParamName, element)) {
            final LocalDate lastverifiedmobiledate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.lastverifiedmobiledateParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedmobiledateParamName).value(lastverifiedmobiledate).notNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedadhardateParamName, element)) {
            final LocalDate lastverifiedadhardate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.lastverifiedadhardateParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedadhardateParamName).value(lastverifiedadhardate).notNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.genderIdParamName, element)) {
            final Integer genderId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.genderIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.genderIdParamName).value(genderId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.statusOneIdParamName, element)) {
            final Integer statusOneId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.statusOneIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.statusOneIdParamName).value(statusOneId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.statusTwoIdParamName, element)) {
            final Integer statusTwoId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.statusTwoIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.statusTwoIdParamName).value(statusTwoId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.introducerIdParamName, element)) {
            final Integer introducerId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.introducerIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.introducerIdParamName).value(introducerId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.sourceoneIdParamName, element)) {
            final Integer sourceoneId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.sourceoneIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.sourceoneIdParamName).value(sourceoneId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.sourcetwoIdParamName, element)) {
            final Integer sourcetwoId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.sourcetwoIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.sourcetwoIdParamName).value(sourcetwoId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.purposeoneIdParamName, element)) {
            final Integer purposeoneId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.purposeoneIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.purposeoneIdParamName).value(purposeoneId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.purposetwoIdParamName, element)) {
            final Integer purposetwoId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.purposetwoIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.purposetwoIdParamName).value(purposetwoId).integerGreaterThanZero();
        }
     
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.fatherspouseIdParamName, element)) {
            final Integer fatherspouseId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.fatherspouseIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.fatherspouseIdParamName).value(fatherspouseId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.educationIdParamName, element)) {
            final Integer educationId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.educationIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.educationIdParamName).value(educationId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.maritalIdParamName, element)) {
            final Integer maritalId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.maritalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.maritalIdParamName).value(maritalId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.professionIdParamName, element)) {
            final Integer professionId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.professionIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.professionIdParamName).value(professionId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.belongingIdParamName, element)) {
            final Integer belongingId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.belongingIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.belongingIdParamName).value(belongingId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.annualIdParamName, element)) {
            final Integer annualId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.annualIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.annualIdParamName).value(annualId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.educationIdParamName, element)) {
            final Integer landId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.landIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.educationIdParamName).value(landId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.houseIdParamName, element)) {
            final Integer houseId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.houseIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.houseIdParamName).value(houseId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.formIdParamName, element)) {
            final Integer formId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.formIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.formIdParamName).value(formId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.titleIdParamName, element)) {
            final Integer titleId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.titleIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.titleIdParamName).value(titleId).integerGreaterThanZero();
        }
          
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.religionIdParamName, element)) {
            final Integer religionId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.religionIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.religionIdParamName).value(religionId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.alternateNoIdParamName, element)) {
            final Integer alternateNoId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.alternateNoIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.alternateNoIdParamName).value(alternateNoId).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.idproofIdParamName, element)) {
            final Integer idproofId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.idproofIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.idproofIdParamName).value(idproofId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.addrproofIdParamName, element)) {
            final Integer addrproofId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.addrproofIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.addrproofIdParamName).value(addrproofId).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.clientTypeIdParamName, element)) {
            final Integer clientType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.clientTypeIdParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.clientTypeIdParamName).value(clientType).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.clientClassificationIdParamName, element)) {
            final Integer clientClassification = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ClientApiConstants.clientClassificationIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.clientClassificationIdParamName).value(clientClassification)
                    .integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.legalFormIdParamName, element)) {
        	final Integer legalFormId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.legalFormIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.legalFormIdParamName).value(legalFormId).ignoreIfNull().inMinMaxRange(1, 2);
        }

        if(this.fromApiJsonHelper.parameterExists(ClientApiConstants.datatables, element)){
            final JsonArray datatables = this.fromApiJsonHelper.extractJsonArrayNamed(ClientApiConstants.datatables, element);
            baseDataValidator.reset().parameter(ClientApiConstants.datatables).value(datatables).notNull().jsonArrayNotEmpty();
        }

		if (this.fromApiJsonHelper.parameterExists("isStaff", element)) {
            final Boolean isStaffFlag = this.fromApiJsonHelper.extractBooleanNamed("isStaff", element);
            baseDataValidator.reset().parameter("isStaff").value(isStaffFlag).notNull();
        }
		
        List<ApiParameterError> dataValidationErrorsForClientNonPerson = getDataValidationErrorsForCreateOnClientNonPerson(element.getAsJsonObject().get(ClientApiConstants.clientNonPersonDetailsParamName));
        dataValidationErrors.addAll(dataValidationErrorsForClientNonPerson);
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    List<ApiParameterError> getDataValidationErrorsForCreateOnClientNonPerson(JsonElement element)
    {
    	
    	final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incorpNumberParamName, element)) {
            final String incorpNumber = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incorpNumberParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incorpNumberParamName).value(incorpNumber).ignoreIfNull()
            		.notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.remarksParamName, element)) {
            final String remarks = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.remarksParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.remarksParamName).value(remarks).ignoreIfNull()
                    .notExceedingLengthOf(150);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incorpValidityTillParamName, element)) {
            final LocalDate incorpValidityTill = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.incorpValidityTillParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incorpValidityTillParamName).value(incorpValidityTill).ignoreIfNull();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.constitutionIdParamName, element)) {
            final Integer constitution = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.constitutionIdParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.constitutionIdParamName).value(constitution).ignoreIfNull().integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.mainBusinessLineIdParamName, element)) {
            final Integer mainBusinessLine = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.mainBusinessLineIdParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.mainBusinessLineIdParamName).value(mainBusinessLine).integerGreaterThanZero();
        }

		return dataValidationErrors;
    }

    private void validateIndividualNamePartsCannotBeUsedWithFullname(final JsonElement element, final DataValidatorBuilder baseDataValidator) {
        final String firstnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.firstnameParamName, element);
        if (StringUtils.isNotBlank(firstnameParam)) {
            final String fullnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.fullnameParamName).value(fullnameParam)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.firstnameParamName, firstnameParam);
        }

        final String middlenameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.spousenameParamName, element);
        if (StringUtils.isNotBlank(middlenameParam)) {
            final String fullnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.fullnameParamName).value(fullnameParam)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.spousenameParamName, middlenameParam);
        }

//        final String lastnameParamName = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastnameParamName, element);
//        if (StringUtils.isNotBlank(lastnameParamName)) {
//            final String fullnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
//            baseDataValidator.reset().parameter(ClientApiConstants.fullnameParamName).value(fullnameParam)
//                    .mustBeBlankWhenParameterProvided(ClientApiConstants.lastnameParamName, lastnameParamName);
//        }
    }

    private void validateRequiredIndividualNamePartsExist(final JsonElement element, final DataValidatorBuilder baseDataValidator) {
        final String firstnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.firstnameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.firstnameParamName).value(firstnameParam).notBlank()
                .notExceedingLengthOf(50);

        // final String middlenameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.middlenameParamName, element);
        // baseDataValidator.reset().parameter(ClientApiConstants.middlenameParamName).value(middlenameParam).ignoreIfNull()
        //         .notExceedingLengthOf(50);

        // final String lastnameParamName = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastnameParamName, element);
        // baseDataValidator.reset().parameter(ClientApiConstants.lastnameParamName).value(lastnameParamName).ignoreIfNull()
        //         .notExceedingLengthOf(50);
        
        final String fsfirstnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fsfirstnameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.firstnameParamName).value(fsfirstnameParam)
                .notExceedingLengthOf(50);

        // final String fsmiddlenameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fsmiddlenameParamName, element);
        // baseDataValidator.reset().parameter(ClientApiConstants.middlenameParamName).value(fsmiddlenameParam).ignoreIfNull()
        //         .notExceedingLengthOf(50);

        // final String fslastnameParamName = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fslastnameParamName, element);
        // baseDataValidator.reset().parameter(ClientApiConstants.fslastnameParamName).value(fslastnameParamName).ignoreIfNull()
        //         .notExceedingLengthOf(50);
    }

    private void fullnameCannotBeBlank(final JsonElement element, final DataValidatorBuilder baseDataValidator) {
        final String fullnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.fullnameParamName).value(fullnameParam).notBlank().notExceedingLengthOf(100);
    }

    private boolean isIndividualNamePartParameterPassed(final JsonElement element) {
        return this.fromApiJsonHelper.parameterExists(ClientApiConstants.firstnameParamName, element);
//                || this.fromApiJsonHelper.parameterExists(ClientApiConstants.middlenameParamName, element)
//                || this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastnameParamName, element);
    }

    private boolean isFullnameParameterPassed(final JsonElement element) {
        return this.fromApiJsonHelper.parameterExists(ClientApiConstants.fullnameParamName, element);
    }

    private boolean isIndividualNameProvided(final JsonElement element) {
        final String firstname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.firstnameParamName, element);
//        final String middlename = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.middlenameParamName, element);
//        final String lastname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastnameParamName, element);

        return StringUtils.isNotBlank(firstname) ;//|| StringUtils.isNotBlank(middlename) || StringUtils.isNotBlank(lastname);
    }

    private boolean isFullnameProvided(final JsonElement element) {
        final String fullname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
        return StringUtils.isNotBlank(fullname);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiCollectionConstants.CLIENT_UPDATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.clientNonPersonDetailsParamName, element)) {
	        final String clientNonPersonJson = this.fromApiJsonHelper.toJson(element.getAsJsonObject().get(ClientApiConstants.clientNonPersonDetailsParamName));
	        if(clientNonPersonJson != null)
	        	this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, clientNonPersonJson, ClientApiCollectionConstants.CLIENT_NON_PERSON_UPDATE_REQUEST_DATA_PARAMETERS);
        }
	        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        boolean atLeastOneParameterPassedForUpdate = false;
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.accountNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String accountNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.accountNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.accountNoParamName).value(accountNo).notBlank().notExceedingLengthOf(20);
        }

        if (isFullnameProvided(element) || isIndividualNameProvided(element)) {

            // 1. No individual name part provided and fullname provided
            if (isFullnameProvided(element) && !isIndividualNameProvided(element)) {
                fullnameCannotBeBlank(element, baseDataValidator);
            }

            // 2. no fullname provided and individual name part provided
            if (isIndividualNameProvided(element) && !isFullnameProvided(element)) {
                validateRequiredIndividualNamePartsExist(element, baseDataValidator);
            }

            // 3. both provided
            if (isFullnameProvided(element) && isIndividualNameProvided(element)) {
                validateIndividualNamePartsCannotBeUsedWithFullname(element, baseDataValidator);
            }
        } else {

            if (isFullnameParameterPassed(element) || isIndividualNamePartParameterPassed(element)) {

                // 1. No individual name parameter passed and fullname passed
                if (isFullnameParameterPassed(element) && !isIndividualNamePartParameterPassed(element)) {
                    fullnameCannotBeBlank(element, baseDataValidator);
                }

                // 2. no fullname passed and individual name part passed
                if (isIndividualNamePartParameterPassed(element) && !isFullnameParameterPassed(element)) {
                    validateRequiredIndividualNamePartsExist(element, baseDataValidator);
                }

                // 3. both parameter types passed
                if (isFullnameParameterPassed(element) && isIndividualNamePartParameterPassed(element)) {
                    baseDataValidator.reset().parameter(ClientApiConstants.idParamName).failWithCode(".no.name.details.passed");
                }

            }
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.fullnameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }
        
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.cpvdataParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.address, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.familyMembers, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.AmountAppliedParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

//        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastnameParamName, element)) {
//            atLeastOneParameterPassedForUpdate = true;
//        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.spousenameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.firstnameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }
        
//        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.fslastnameParamName, element)) {
//            atLeastOneParameterPassedForUpdate = true;
//            final String fslastname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fslastnameParamName, element);
//            baseDataValidator.reset().parameter(ClientApiConstants.fslastnameParamName).value(fslastname).notExceedingLengthOf(100);
//        }
        
//        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.fsmiddlenameParamName, element)) {
//            atLeastOneParameterPassedForUpdate = true;
//            final String fsmiddlename = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fsmiddlenameParamName, element);
//            baseDataValidator.reset().parameter(ClientApiConstants.fsmiddlenameParamName).value(fsmiddlename).notExceedingLengthOf(100);
//        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.fsfirstnameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String fsfirstname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fsfirstnameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.fsfirstnameParamName).value(fsfirstname).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.maidennameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String maidenname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.maidennameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.maidennameParamName).value(maidenname).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.custmothernameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String custmothername = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.custmothernameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.custmothernameParamName).value(custmothername).notExceedingLengthOf(100);
        }
       
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.alternateMobileNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String alternateMobileNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.alternateMobileNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.alternateMobileNoParamName).value(alternateMobileNo).notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.secIdProofNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String secIdProofNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.secIdProofNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.secIdProofNoParamName).value(secIdProofNo).notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.secaddressproofnoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String secaddressproofno = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.secaddressproofnoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.secaddressproofnoParamName).value(secaddressproofno).notExceedingLengthOf(100);
        }
        
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedmobileParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String lastverifiedmobile = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastverifiedmobileParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedmobileParamName).value(lastverifiedmobile).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.otherexpensestfParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String otherexpensestf = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.otherexpensestfParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.otherexpensestfParamName).value(otherexpensestf).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.othersrcinctfParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String othersrcinctf = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.othersrcinctfParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.othersrcinctfParamName).value(othersrcinctf).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.otherobligationsParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String otherobligations = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.otherobligationsParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.otherobligationsParamName).value(otherobligations).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedsecondaryidParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String lastverifiedsecondaryid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastverifiedsecondaryidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedsecondaryidParamName).value(lastverifiedsecondaryid).notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.adharParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String adhar = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.adharParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.adharParamName).value(adhar).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nregaParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String nrega = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nregaParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nregaParamName).value(nrega).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.panParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String pan = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.panParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.panParamName).value(pan).notExceedingLengthOf(100);
        }
    
       
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.externalIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String externalId = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIdParamName).value(externalId).notExceedingLengthOf(100);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.externalIddParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String externalIdd = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIddParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIddParamName).value(externalIdd).notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.mobileNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String mobileNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.mobileNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.mobileNoParamName).value(mobileNo).notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.gstNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String gstNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.gstNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.gstNoParamName).value(gstNo).notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.ageParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String age = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.ageParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.ageParamName).value(age).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomrelationshipidParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String nomrelationshipid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomrelationshipidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomrelationshipidParamName).value(nomrelationshipid).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomgenderidParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String nomgenderid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomgenderidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomgenderidParamName).value(nomgenderid).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomageParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String nomage = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomageParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomageParamName).value(nomage).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomprofessionidParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String nomprofessionid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomprofessionidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomprofessionidParamName).value(nomprofessionid).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nomeducationalidParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String nomeducationalid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nomeducationalidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nomeducationalidParamName).value(nomeducationalid).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.nommaritalidParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String nommaritalid = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.nommaritalidParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.nommaritalidParamName).value(nommaritalid).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incdailysalesParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String incdailysales = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incdailysalesParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incdailysalesParamName).value(incdailysales).notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exprawmaterialParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String exprawmaterial = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exprawmaterialParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.exprawmaterialParamName).value(exprawmaterial).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expstaffsalParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String expstaffsal = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expstaffsalParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expstaffsalParamName).value(expstaffsal).notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exppowertelephoneParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String exppowertelephone = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exppowertelephoneParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.exppowertelephoneParamName).value(exppowertelephone).notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exprepairsmaintainanceParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String exprepairsmaintainance = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exprepairsmaintainanceParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.exprepairsmaintainanceParamName).value(exprepairsmaintainance).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expcommbrokerageParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String expcommbrokerage = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expcommbrokerageParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expcommbrokerageParamName).value(expcommbrokerage).notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.increntParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String incrent = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.increntParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.increntParamName).value(incrent).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incinterestParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String incinterest = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incinterestParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incinterestParamName).value(incinterest).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incothersParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String incothers = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incothersParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incothersParamName).value(incothers).notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.tothouseholdincParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String tothouseholdinc = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.tothouseholdincParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.tothouseholdincParamName).value(tothouseholdinc).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exphouseholdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String exphousehold = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exphouseholdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incinterestParamName).value(exphousehold).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expotherloansParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String expotherloans = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expotherloansParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expotherloansParamName).value(expotherloans).notExceedingLengthOf(50);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.totnetdispfamilyParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String totnetdispfamily = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.totnetdispfamilyParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.totnetdispfamilyParamName).value(totnetdispfamily).notExceedingLengthOf(50);
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.idproofNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String idproofNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.idproofNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.idproofNoParamName).value(idproofNo).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.addrproofNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String addrproofNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.addrproofNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.addrproofNoParamName).value(addrproofNo).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expinterestParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String expinterest = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expinterestParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expinterestParamName).value(expinterest).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expofficerentParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String expofficerent = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expofficerentParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expofficerentParamName).value(expofficerent).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.exptravelParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String exptravel = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.exptravelParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.exptravelParamName).value(exptravel).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.expothersParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String expothers = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.expothersParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.expothersParamName).value(expothers).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.totbusinessprofitParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String totbusinessprofit = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.totbusinessprofitParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.totbusinessprofitParamName).value(totbusinessprofit).notExceedingLengthOf(50);
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incspouseParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String incspouse = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incspouseParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incspouseParamName).value(incspouse).notExceedingLengthOf(50);
        }
             
       
        /*if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.groupParamName, element)) {
            final Long group = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.groupParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.groupParamName).value(group).ignoreIfNull()
                    .notExceedingLengthOf(500);
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.centerParamName, element)) {
            final Long center = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.centerParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.centerParamName).value(center).ignoreIfNull()
                    .notExceedingLengthOf(500);
        }*/

        final Boolean active = this.fromApiJsonHelper.extractBooleanNamed(ClientApiConstants.activeParamName, element);
        if (active != null) {
            atLeastOneParameterPassedForUpdate = true;
            if (active.booleanValue()) {
                final LocalDate joinedDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.activationDateParamName,
                        element);
                baseDataValidator.reset().parameter(ClientApiConstants.activationDateParamName).value(joinedDate).notNull();
            }
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.staffIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Long staffId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.staffIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.staffIdParamName).value(staffId).ignoreIfNull().longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.savingsProductIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Long savingsProductId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.savingsProductIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.savingsProductIdParamName).value(savingsProductId).ignoreIfNull()
                    .longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.dateOfBirthParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final LocalDate dateOfBirth = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.dateOfBirthParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.dateOfBirthParamName).value(dateOfBirth).notNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedSecondaryidDateParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final LocalDate lastverifiedSecondaryidDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.lastverifiedSecondaryidDateParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedSecondaryidDateParamName).value(lastverifiedSecondaryidDate).notNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedmobiledateParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final LocalDate lastverifiedmobiledate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.lastverifiedmobiledateParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedmobiledateParamName).value(lastverifiedmobiledate).notNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastverifiedadhardateParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final LocalDate lastverifiedadhardate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.lastverifiedadhardateParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.lastverifiedadhardateParamName).value(lastverifiedadhardate).notNull()
                    .validateDateBefore(DateUtils.getLocalDateOfTenant());
        }
        
        

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.genderIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer genderId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.genderIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.genderIdParamName).value(genderId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.statusOneIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer statusOneId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.statusOneIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.statusOneIdParamName).value(statusOneId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.statusTwoIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer statusTwoId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.genderIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.genderIdParamName).value(statusTwoId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.introducerIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer introducerId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.introducerIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.introducerIdParamName).value(introducerId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.sourceoneIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer sourceoneId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.sourceoneIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.sourceoneIdParamName).value(sourceoneId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.sourcetwoIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer sourcetwoId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.sourcetwoIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.sourcetwoIdParamName).value(sourcetwoId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.purposeoneIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer purposeoneId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.purposeoneIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.purposeoneIdParamName).value(purposeoneId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.purposetwoIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer purposetwoId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.purposetwoIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.purposetwoIdParamName).value(purposetwoId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.fatherspouseIdParamName, element)) {
        	atLeastOneParameterPassedForUpdate = true;
            final Integer fatherspouseId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.fatherspouseIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.fatherspouseIdParamName).value(fatherspouseId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.educationIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer educationId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.educationIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.educationIdParamName).value(educationId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.maritalIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer maritalId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.maritalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.maritalIdParamName).value(maritalId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.professionIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer professionId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.professionIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.professionIdParamName).value(professionId).integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.belongingIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer belongingId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.belongingIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.belongingIdParamName).value(belongingId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.annualIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer annualId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.annualIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.annualIdParamName).value(annualId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.landIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer landId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.landIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.landIdParamName).value(landId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.houseIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer houseId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.houseIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.houseIdParamName).value(houseId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.formIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer formId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.formIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.formIdParamName).value(formId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.titleIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer titleId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.titleIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.titleIdParamName).value(titleId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.religionIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer religionId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.religionIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.religionIdParamName).value(religionId).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.alternateNoIdParamName, element)) {
        	atLeastOneParameterPassedForUpdate = true;
            final Integer alternateNoId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.alternateNoIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.alternateNoIdParamName).value(alternateNoId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.idproofIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer idproofId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.idproofIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.idproofIdParamName).value(idproofId).integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.addrproofIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer addrproofId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.addrproofIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.addrproofIdParamName).value(addrproofId).integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.clientTypeIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer clientType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.clientTypeIdParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.clientTypeIdParamName).value(clientType).integerGreaterThanZero();
        }
        
      
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.clientClassificationIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer clientClassification = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    ClientApiConstants.clientClassificationIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.clientClassificationIdParamName).value(clientClassification)
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.submittedOnDateParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final LocalDate submittedDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.submittedOnDateParamName,
                    element);
            baseDataValidator.reset().parameter(ClientApiConstants.submittedOnDateParamName).value(submittedDate).notNull();

        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.debtincratioParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.debtParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incomeParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }
        
        
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.legalFormIdParamName, element)) {
        	atLeastOneParameterPassedForUpdate = true;
        	final Integer legalFormId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.legalFormIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.legalFormIdParamName).value(legalFormId).ignoreIfNull().inMinMaxRange(1, 2);
        }

		if (this.fromApiJsonHelper.parameterExists("isStaff", element)) {
            final Boolean isStaffFlag = this.fromApiJsonHelper.extractBooleanNamed("isStaff", element);
            baseDataValidator.reset().parameter("isStaff").value(isStaffFlag).notNull();
        }

        Map<String, Object> parameterUpdateStatusDetails = getParameterUpdateStatusAndDataValidationErrorsForUpdateOnClientNonPerson(element.getAsJsonObject().get(ClientApiConstants.clientNonPersonDetailsParamName));
        boolean atLeastOneParameterPassedForClientNonPersonUpdate = (boolean) parameterUpdateStatusDetails.get("parameterUpdateStatus");
               
        if (!atLeastOneParameterPassedForUpdate && !atLeastOneParameterPassedForClientNonPersonUpdate) {
            final Object forceError = null;
            baseDataValidator.reset().anyOfNotNull(forceError);
        }
        
        @SuppressWarnings("unchecked")
		List<ApiParameterError> dataValidationErrorsForClientNonPerson = (List<ApiParameterError>) parameterUpdateStatusDetails.get("dataValidationErrors");        
        dataValidationErrors.addAll(dataValidationErrorsForClientNonPerson);
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    Map<String, Object> getParameterUpdateStatusAndDataValidationErrorsForUpdateOnClientNonPerson(JsonElement element)
    {
    	boolean atLeastOneParameterPassedForUpdate = false;
    	final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);
    	
    	if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incorpNumberParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String incorpNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.incorpNumberParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incorpNumberParamName).value(incorpNo).ignoreIfNull().notExceedingLengthOf(50);
        }
    	
    	if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.remarksParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String remarks = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.remarksParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.remarksParamName).value(remarks).notExceedingLengthOf(150);
        }
    	
    	if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.incorpValidityTillParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final LocalDate incorpValidityTill = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.incorpValidityTillParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.incorpValidityTillParamName).value(incorpValidityTill);
        }
    	
    	if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.constitutionIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer constitutionId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.constitutionIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.constitutionIdParamName).value(constitutionId).ignoreIfNull().integerGreaterThanZero();
        }
    	
    	if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.mainBusinessLineIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final Integer mainBusinessLineId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(ClientApiConstants.mainBusinessLineIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.mainBusinessLineIdParamName).value(mainBusinessLineId).integerGreaterThanZero();
        }
    	
    	Map<String, Object> parameterUpdateStatusDetails = new HashMap<>();
    	parameterUpdateStatusDetails.put("parameterUpdateStatus", atLeastOneParameterPassedForUpdate);
    	parameterUpdateStatusDetails.put("dataValidationErrors", dataValidationErrors);
    	
		return parameterUpdateStatusDetails;
    	
    }

    public void validateActivation(final JsonCommand command) {
        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiCollectionConstants.ACTIVATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate activationDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.activationDateParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.activationDateParamName).value(activationDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

    public void validateForUnassignStaff(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        final Set<String> supportedParametersUnassignStaff = new HashSet<>(Arrays.asList(ClientApiConstants.staffIdParamName));

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParametersUnassignStaff);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final String staffIdParameterName = ClientApiConstants.staffIdParamName;
        final Long staffId = this.fromApiJsonHelper.extractLongNamed(staffIdParameterName, element);
        baseDataValidator.reset().parameter(staffIdParameterName).value(staffId).notNull().longGreaterThanZero();

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }

    public void validateForAssignStaff(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        final Set<String> supportedParametersUnassignStaff = new HashSet<>(Arrays.asList(ClientApiConstants.staffIdParamName));

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParametersUnassignStaff);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final String staffIdParameterName = ClientApiConstants.staffIdParamName;
        final Long staffId = this.fromApiJsonHelper.extractLongNamed(staffIdParameterName, element);
        baseDataValidator.reset().parameter(staffIdParameterName).value(staffId).notNull().longGreaterThanZero();

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }

    public void validateClose(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiCollectionConstants.CLIENT_CLOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate closureDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.closureDateParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.closureDateParamName).value(closureDate).notNull();

        final Long closureReasonId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.closureReasonIdParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.closureReasonIdParamName).value(closureReasonId).notNull()
                .longGreaterThanZero();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForSavingsAccount(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        final Set<String> supportedParameters = new HashSet<>(Arrays.asList(ClientApiConstants.savingsAccountIdParamName));

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final String savingsIdParameterName = ClientApiConstants.savingsAccountIdParamName;
        final Long savingsId = this.fromApiJsonHelper.extractLongNamed(savingsIdParameterName, element);
        baseDataValidator.reset().parameter(savingsIdParameterName).value(savingsId).notNull().longGreaterThanZero();

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }

    public void validateRejection(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiCollectionConstants.CLIENT_REJECT_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate rejectionDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.rejectionDateParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.rejectionDateParamName).value(rejectionDate).notNull();

        final Long rejectionReasonId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.rejectionReasonIdParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.rejectionReasonIdParamName).value(rejectionReasonId).notNull()
                .longGreaterThanZero();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateWithdrawn(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiCollectionConstants.CLIENT_WITHDRAW_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate withdrawalDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.withdrawalDateParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.withdrawalDateParamName).value(withdrawalDate).notNull();

        final Long withdrawalReasonId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.withdrawalReasonIdParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.withdrawalReasonIdParamName).value(withdrawalReasonId).notNull()
                .longGreaterThanZero();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateReactivate(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiCollectionConstants.REACTIVATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate reactivationDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.reactivationDateParamName,
                element);
        baseDataValidator.reset().parameter(ClientApiConstants.reactivationDateParamName).value(reactivationDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }
    
    
    public void validateUndoRejection(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiCollectionConstants.UNDOREJECTION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate undoRejectionDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.reopenedDateParamName,
                element);
		baseDataValidator.reset().parameter(ClientApiConstants.reopenedDateParamName).value(undoRejectionDate).notNull()
				.validateDateBeforeOrEqual(DateUtils.getLocalDateOfTenant());

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }
    public void validateUndoWithDrawn(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiCollectionConstants.UNDOWITHDRAWN_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate undoWithdrawnDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.reopenedDateParamName,
                element);
		baseDataValidator.reset().parameter(ClientApiConstants.reopenedDateParamName).value(undoWithdrawnDate).notNull()
				.validateDateBeforeOrEqual(DateUtils.getLocalDateOfTenant());

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }
    
}
