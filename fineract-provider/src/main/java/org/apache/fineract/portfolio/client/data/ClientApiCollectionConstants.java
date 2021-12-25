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

import org.apache.fineract.portfolio.client.api.ClientApiConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class ClientApiCollectionConstants extends ClientApiConstants{

    protected static final Set<String> CLIENT_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(familyMembers,address,localeParamName, dateFormatParamName, groupIdParamName, accountNoParamName, externalIdParamName,externalIddParamName,ageParamName,
            		mobileNoParamName,gstNoParamName,ageParamName,incdailysalesParamName,exprawmaterialParamName, expstaffsalParamName,exppowertelephoneParamName,exprepairsmaintainanceParamName, expcommbrokerageParamName, increntParamName, incinterestParamName, incothersParamName,tothouseholdincParamName, exphouseholdParamName, expotherloansParamName,totnetdispfamilyParamName,
            		idproofNoParamName, addrproofNoParamName, expinterestParamName,expofficerentParamName,exptravelParamName,expothersParamName,totbusinessprofitParamName, incspouseParamName, emailAddressParamName, firstnameParamName,fsfirstnameParamName,   
                    maidennameParamName, custmothernameParamName, alternateMobileNoParamName, secIdProofNoParamName, secaddressproofnoParamName,lastverifiedmobileParamName,otherexpensestfParamName,othersrcinctfParamName,otherobligationsParamName,lastverifiedsecondaryidParamName ,adharParamName,nregaParamName, panParamName, officeIdParamName,
                    activeParamName, activationDateParamName, staffIdParamName, submittedOnDateParamName,savingsProductIdParamName,nomgenderidParamName,nomrelationshipidParamName,nommaritalidParamName,nomprofessionidParamName,nomageParamName,nomeducationalidParamName,
                    dateOfBirthParamName, lastverifiedSecondaryidDateParamName,lastverifiedmobiledateParamName,lastverifiedSecondaryidDateParamName, genderIdParamName,statusOneIdParamName,statusTwoIdParamName,introducerIdParamName,sourceoneIdParamName,sourcetwoIdParamName,purposeoneIdParamName,purposetwoIdParamName,fatherspouseIdParamName,educationIdParamName, maritalIdParamName, professionIdParamName, belongingIdParamName, annualIdParamName,
                    landIdParamName, houseIdParamName, formIdParamName, titleIdParamName, religionIdParamName,alternateNoIdParamName, idproofIdParamName, addrproofIdParamName,clientTypeIdParamName,  clientClassificationIdParamName,lastverifiedadharParamName,
                    clientNonPersonDetailsParamName, displaynameParamName, legalFormIdParamName, datatables, isStaffParamName,lastverifiedsecondaryidParamName,lastverifiedadhardateParamName,debtParamName,incomeParamName,debtincratioParamName,AmountAppliedParamName,cpvdataParamName,spousenameParamName));

    protected static final Set<String> CLIENT_NON_PERSON_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(familyMembers,address,localeParamName, dateFormatParamName, incorpNumberParamName, remarksParamName, incorpValidityTillParamName,
                    constitutionIdParamName, mainBusinessLineIdParamName, datatables));

    protected static final Set<String> CLIENT_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,ageParamName,
            dateFormatParamName, accountNoParamName, externalIdParamName,externalIddParamName,mobileNoParamName,gstNoParamName,incdailysalesParamName,exprawmaterialParamName,expstaffsalParamName,exppowertelephoneParamName,exprepairsmaintainanceParamName,expcommbrokerageParamName,increntParamName,incinterestParamName,incothersParamName,tothouseholdincParamName,exphouseholdParamName,expotherloansParamName,
            totnetdispfamilyParamName,idproofNoParamName, addrproofNoParamName,expinterestParamName,expofficerentParamName,exptravelParamName,expothersParamName,totbusinessprofitParamName, incspouseParamName,statusOneParamName,statusTwoParamName, emailAddressParamName, firstnameParamName, 
            fsfirstnameParamName,maidennameParamName,custmothernameParamName,alternateMobileNoParamName, secIdProofNoParamName,lastverifiedmobileParamName,otherexpensestfParamName,othersrcinctfParamName,otherobligationsParamName,lastverifiedsecondaryidParamName, secaddressproofnoParamName, adharParamName,nregaParamName,panParamName,
            fullnameParamName, activeParamName, activationDateParamName, staffIdParamName, savingsProductIdParamName,nomgenderidParamName,nomrelationshipidParamName,nommaritalidParamName,nomprofessionidParamName,nomageParamName,nomeducationalidParamName,
            dateOfBirthParamName, lastverifiedSecondaryidDateParamName,lastverifiedmobiledateParamName,lastverifiedSecondaryidDateParamName,genderIdParamName,statusOneIdParamName,statusTwoIdParamName,introducerIdParamName,sourceoneIdParamName,sourcetwoIdParamName,purposeoneIdParamName,purposetwoIdParamName,fatherspouseIdParamName,educationIdParamName, maritalIdParamName, professionIdParamName, belongingIdParamName, annualIdParamName,
            landIdParamName, houseIdParamName, formIdParamName, titleIdParamName, religionIdParamName,alternateNoIdParamName, idproofIdParamName, addrproofIdParamName,clientTypeIdParamName, clientClassificationIdParamName, submittedOnDateParamName,lastverifiedadharParamName,
            clientNonPersonDetailsParamName, displaynameParamName, legalFormIdParamName, isStaffParamName,lastverifiedsecondaryidParamName,lastverifiedadhardateParamName,familyMembers,address,debtParamName,incomeParamName,debtincratioParamName,AmountAppliedParamName,cpvdataParamName,spousenameParamName));

    protected static final Set<String> CLIENT_NON_PERSON_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, incorpNumberParamName, remarksParamName, incorpValidityTillParamName,
            constitutionIdParamName, mainBusinessLineIdParamName,familyMembers,address));


    /**
     * These parameters will match the class level parameters of
     * {@link ClientData}. Where possible, we try to get response parameters to
     * match those of request parameters.
     */

    protected static final Set<String> ACTIVATION_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(localeParamName, dateFormatParamName, activationDateParamName));
    protected static final Set<String> REACTIVATION_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(localeParamName, dateFormatParamName, reactivationDateParamName));

    protected static final Set<String> CLIENT_CLOSE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(localeParamName, dateFormatParamName, closureDateParamName, closureReasonIdParamName));

    protected static final Set<String> CLIENT_REJECT_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(localeParamName, dateFormatParamName, rejectionDateParamName, rejectionReasonIdParamName));

    protected static final Set<String> CLIENT_WITHDRAW_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(localeParamName, dateFormatParamName, withdrawalDateParamName, withdrawalReasonIdParamName));

    protected static final Set<String> UNDOREJECTION_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(localeParamName, dateFormatParamName, reopenedDateParamName));

    protected static final Set<String> UNDOWITHDRAWN_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(localeParamName, dateFormatParamName, reopenedDateParamName));

    protected static final Set<String> CLIENT_CHARGES_ADD_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(chargeIdParamName, amountParamName, dueAsOfDateParamName, dateFormatParamName, localeParamName));

    protected static final Set<String> CLIENT_CHARGES_PAY_CHARGE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(amountParamName,
            transactionDateParamName, dateFormatParamName, localeParamName, paymentTypeIdParamName, transactionAccountNumberParamName,
            checkNumberParamName, routingCodeParamName, receiptNumberParamName, bankNumberParamName));


    protected static final Set<String> CLIENT_TRAN_CHIT_ADV_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(amountParamName,
        currencyParamName, transactionDateParamName, dateFormatParamName, localeParamName, paymentTypeIdParamName, transactionAccountNumberParamName,
        checkNumberParamName, routingCodeParamName, receiptNumberParamName, bankNumberParamName, paymentNoteParamName,depositedDateParamName,transactionNoParamName,officeIdParamName));

}
