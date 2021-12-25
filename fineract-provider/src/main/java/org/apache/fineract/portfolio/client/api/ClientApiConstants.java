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
package org.apache.fineract.portfolio.client.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.fineract.portfolio.client.data.ClientData;

public class ClientApiConstants {

    public static final String CLIENT_RESOURCE_NAME = "client";
    public static final String CLIENT_CHARGES_RESOURCE_NAME = "CLIENTCHARGE";


    // Client Charge Action Names
    public static final String CLIENT_CHARGE_ACTION_CREATE = "CREATE";
    public static final String CLIENT_CHARGE_ACTION_DELETE = "DELETE";
    public static final String CLIENT_CHARGE_ACTION_WAIVE = "WAIVE";
    public static final String CLIENT_CHARGE_ACTION_PAY = "PAY";
    public static final String CLIENT_CHARGE_ACTION_INACTIVATE = "INACTIVATE";

    // Client charge associations and query parameters
    public static final String CLIENT_CHARGE_QUERY_PARAM_STATUS = "chargeStatus";
    public static final String CLIENT_CHARGE_QUERY_PARAM_STATUS_VALUE_ALL = "all";
    public static final String CLIENT_CHARGE_QUERY_PARAM_STATUS_VALUE_ACTIVE = "active";
    public static final String CLIENT_CHARGE_QUERY_PARAM_STATUS_VALUE_INACTIVE = "inactive";
    public static final String CLIENT_CHARGE_ASSOCIATIONS_TRANSACTIONS = "transactions";

    // Client transaction action names
    public static final String CLIENT_TRANSACTION_ACTION_READ = "READTRANSACTION";
    public static final String CLIENT_TRANSACTION_ACTION_UNDO = "UNDOTRANSACTION";
    public static final String CLIENT_TRANSACTION_ACTION_CREATE_CHITADV = "CLITRANCHITADV";

    // Commands
    public static final String CLIENT_CHARGE_COMMAND_WAIVE_CHARGE = "waive";
    public static final String CLIENT_CHARGE_COMMAND_PAY_CHARGE = "paycharge";
    public static final String CLIENT_CHARGE_COMMAND_INACTIVATE_CHARGE = "inactivate";
    public static final String CLIENT_TRANSACTION_COMMAND_UNDO = "undo";
    public static final String CLIENT_TRANSACTION_COMMAND_CHITADVANCE = "chit_advance";

    public static final String CLIENT_CLOSURE_REASON = "ClientClosureReason";
    public static final String CLIENT_ACTION_REASON = "ClientActionReason";
    public static final String CLIENT_REJECT_REASON = "ClientRejectReason";
    public static final String CLIENT_WITHDRAW_REASON = "ClientWithdrawReason";

    public static final String GENDER = "Gender";
    public static final String STATUSONE = "StatusOne";
    public static final String STATUSTWO = "StatusTwo";
    public static final String INTRODUCER = "Introducer";
    public static final String SOURCEONE = "Sourceone";
    public static final String SOURCETWO = "Sourcetwo";
    public static final String PURPOSEONE = "Purposeone";
    public static final String PURPOSETWO = "Purposetwo";
    public static final String FATHERSPOUSE = "Fatherspouse";
    public static final String ALTNO = "Alternate Mobile Number of";
    public static final String EDUCATION = "Education";
    public static final String MARITAL = "Marital";
    public static final String PROFESSION = "Profession";
    public static final String BELONGING = "Belonging";
    public static final String ANNUAL = "Annual";
    public static final String LAND = "Land";
    public static final String HOUSE = "House";
    public static final String FORM = "Form";
    public static final String TITLE = "Title";
    public static final String RELIGION = "Religion";
    public static final String IDPROOF = "Idproof";
    public static final String ADDRPROOF = "Addrproof";
    public static final String CLIENT_TYPE = "ClientType";
    public static final String CLIENT_CLASSIFICATION = "ClientClassification";
    
    public static final String CLIENT_NON_PERSON_CONSTITUTION = "Constitution";
    public static final String CLIENT_NON_PERSON_MAIN_BUSINESS_LINE = "Main Business Line";
    
    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String address = "address";
    public static final String familyMembers = "familyMembers";
    public static final String MARITALSTATUS="MARITALSTATUS";
    public static final String maritalStatusIdParamName="maritalStatusId";
    
    
    
    public static final String CLIENT_TYPE_INDIVIDUAL = "Individual";

    // request parameters
    public static final String cpvdataParamName = "cpvData";
    public static final String idParamName = "id";
    public static final String groupIdParamName = "groupId";
    public static final String accountNoParamName = "accountNo";
    public static final String externalIdParamName = "externalId";
    public static final String externalIddParamName = "externalIdd";
    public static final String mobileNoParamName = "mobileNo";
    public static final String gstNoParamName = "gstNo";
    public static final String ageParamName = "age";
    public static final String nomrelationshipidParamName = "nomrelationshipid";
    public static final String nomgenderidParamName = "nomgenderid";
    public static final String nomageParamName = "nomage";
    public static final String nomprofessionidParamName = "nomprofessionid";
    public static final String nomeducationalidParamName = "nomeducationalid";
    public static final String nommaritalidParamName = "nommaritalid";
    public static final String incdailysalesParamName = "incdailysales";
    public static final String exprawmaterialParamName = "exprawmaterial";
    public static final String expstaffsalParamName = "expstaffsal";
    public static final String exppowertelephoneParamName = "exppowertelephone";
    public static final String exprepairsmaintainanceParamName = "exprepairsmaintainance";
    public static final String expcommbrokerageParamName = "expcommbrokerage";
    public static final String increntParamName = "incrent";
    public static final String incinterestParamName = "incinterest";
    public static final String incothersParamName = "incothers";
    public static final String tothouseholdincParamName = "tothouseholdinc";
    public static final String exphouseholdParamName = "exphousehold";
    public static final String expotherloansParamName = "expotherloans";
    public static final String totnetdispfamilyParamName = "totnetdispfamily";

    public static final String foaThreeParamName = "foaThree";
    public static final String idproofNoParamName = "idproofNo";
    public static final String addrproofNoParamName = "addrproofNo";
    public static final String alternateMobileNoParamName = "alternateMobileNo";
    public static final String secIdProofNoParamName = "secIdProofNo";
    public static final String secaddressproofnoParamName = "secaddressproofno";
    public static final String lastverifiedmobileParamName = "lastverifiedmobile";
    public static final String otherexpensestfParamName = "otherexpensestf";
    public static final String othersrcinctfParamName = "othersrcinctf";
    public static final String otherobligationsParamName = "otherobligations";
   
    public static final String expinterestParamName = "expinterest";
    public static final String expofficerentParamName = "expofficerent";
    public static final String exptravelParamName = "exptravel";
    public static final String expothersParamName = "expothers";
    public static final String totbusinessprofitParamName = "totbusinessprofit";
    public static final String incspouseParamName = "incspouse";
    public static final String statusOneParamName = "statusOne";
    public static final String statusTwoParamName = "statusTwo";
	public static final String emailAddressParamName = "emailAddress";
    public static final String firstnameParamName = "firstname";
    public static final String spousenameParamName = "spousename";
    public static final String lastnameParamName = "lastname";
    public static final String fsfirstnameParamName = "fsfirstname";
    public static final String fsmiddlenameParamName = "fsmiddlename";
    public static final String fslastnameParamName = "fslastname";
    public static final String maidennameParamName = "maidenname";
    public static final String custmothernameParamName = "custmothername";
    public static final String adharParamName = "adhar";
    public static final String nregaParamName = "nrega";
    public static final String panParamName = "pan";
    public static final String fullnameParamName = "fullname";
    public static final String displaynameParamName = "displayname";
    public static final String officeIdParamName = "officeId";
    public static final String transferOfficeIdParamName = "transferOfficeIdParamName";
    public static final String activeParamName = "active";
    public static final String activationDateParamName = "activationDate";
    public static final String reactivationDateParamName = "reactivationDate";
    public static final String staffIdParamName = "staffId";
	public static final String isStaffParamName = "isStaff";
	public static final String lastverifiedsecondaryidParamName = "lastverifiedsecondaryid";
	public static final String lastverifiedadharParamName = "lastverifiedadhar";
    public static final String closureDateParamName = "closureDate";
    public static final String closureReasonIdParamName = "closureReasonId";
    public static final String reopenedDateParamName = "reopenedDate";
    //public static final String groupParamName = "group";
    //public static final String centerParamName = "center";

    public static final String rejectionDateParamName = "rejectionDate";
    public static final String rejectionReasonIdParamName = "rejectionReasonId";
    public static final String withdrawalDateParamName = "withdrawalDate";
    public static final String withdrawalReasonIdParamName = "withdrawalReasonId";
    
    public static final String submittedOnDateParamName = "submittedOnDate";
    public static final String savingsProductIdParamName = "savingsProductId";
    public static final String savingsAccountIdParamName = "savingsAccountId";
    public static final String dateOfBirthParamName = "dateOfBirth";
    public static final String lastverifiedSecondaryidDateParamName = "lastverifiedSecondaryidDate";
    public static final String lastverifiedmobiledateParamName = "lastverifiedmobiledate";
    public static final String lastverifiedadhardateParamName = "lastverifiedadhardate";
    
    public static final String genderIdParamName = "genderId";
    public static final String statusOneIdParamName = "statusOneId";
    public static final String statusTwoIdParamName = "statusTwoId";
    public static final String introducerIdParamName = "introducerId";
    public static final String sourceoneIdParamName = "sourceoneId";
    public static final String sourcetwoIdParamName = "sourcetwoId";
    public static final String purposeoneIdParamName = "purposeoneId";
    public static final String purposetwoIdParamName = "purposetwoId";    
    public static final String fatherspouseIdParamName = "fatherspouseId";
    public static final String educationIdParamName = "educationId";
    public static final String maritalIdParamName = "maritalId";
    public static final String professionIdParamName = "professionId";
    public static final String belongingIdParamName = "belongingId";
    public static final String annualIdParamName = "annualId";
    public static final String landIdParamName = "landId";
    public static final String houseIdParamName = "houseId";
    public static final String formIdParamName = "formId";
    public static final String titleIdParamName = "titleId";
    public static final String religionIdParamName = "religionId";
    public static final String alternateNoIdParamName = "alternateNoId";
    public static final String idproofIdParamName = "idproofId";
    public static final String addrproofIdParamName = "addrproofId";
    public static final String clientTypeIdParamName = "clientTypeId";
    public static final String clientTypeParamName = "clientType";
    public static final String clientClassificationIdParamName = "clientClassificationId";
    public static final String clientClassificationParamName = "clientClassification";
    public static final String legalFormIdParamName = "legalFormId";
    public static final String legalFormParamName = "legalForm"; 
    public static final String debtParamName = "debt";
    public static final String incomeParamName = "income";
    public static final String debtincratioParamName = "debtincratio";
    public static final String AmountAppliedParamName = "AmountApplied";
    // request parameters for payment details
    public static final String paymentTypeIdParamName = "paymentTypeId";
    public static final String transactionAccountNumberParamName = "accountNumber";
    public static final String checkNumberParamName = "checkNumber";
    public static final String routingCodeParamName = "routingCode";
    public static final String receiptNumberParamName = "receiptNumber";
    public static final String bankNumberParamName = "bankNumber";
    public static final String paymentNoteParamName = "paymentNote";
    
    //request parameters for client non person
    public static final String clientNonPersonDetailsParamName = "clientNonPersonDetails";
    public static final String incorpNumberParamName = "incorpNumber";
    public static final String remarksParamName = "remarks";
    public static final String incorpValidityTillParamName = "incorpValidityTillDate";
    public static final String constitutionIdParamName = "constitutionId";
    public static final String mainBusinessLineIdParamName = "mainBusinessLineId";
    

    // response parameters
    public static final String statusParamName = "status";
    public static final String hierarchyParamName = "hierarchy";
    public static final String displayNameParamName = "displayName";
    public static final String officeNameParamName = "officeName";
    public static final String staffNameParamName = "staffName";
    public static final String trasnferOfficeNameParamName = "transferOfficeName";
    public static final String transferToOfficeNameParamName = "transferToOfficeName";
    public static final String transferToOfficeIdParamName = "transferToOfficeId";
    public static final String imageKeyParamName = "imageKey";
    public static final String imageIdParamName = "imageId";
    public static final String imagePresentParamName = "imagePresent";
    public static final String timelineParamName = "timeline";

    // client charges response parameters
    public static final String chargeIdParamName = "chargeId";
    public static final String clientIdParamName = "clientId";
    public static final String chargesParamName = "charges";
    public static final String chargeNameParamName = "name";
    public static final String penaltyParamName = "penalty";
    public static final String chargeTimeTypeParamName = "chargeTimeType";
    public static final String dueAsOfDateParamName = "dueDate";
    public static final String transactionDateParamName = "transactionDate";
    public static final String chargeCalculationTypeParamName = "chargeCalculationType";
    public static final String currencyParamName = "currency";
    public static final String amountWaivedParamName = "amountWaived";
    public static final String amountWrittenOffParamName = "amountWrittenOff";
    public static final String amountOutstandingParamName = "amountOutstanding";
    public static final String amountOrPercentageParamName = "amountOrPercentage";
    public static final String amountParamName = "amount";
    public static final String amountPaidParamName = "amountPaid";
    public static final String chargeOptionsParamName = "chargeOptions";
    public static final String transactionsParamName = "transactions";

    // client transactions response parameters
    public static final String transactionAmountParamName = "transactionAmount";
    public static final String paymentDetailDataParamName = "paymentDetailData";
    public static final String reversedParamName = "reversed";
    public static final String dateParamName = "date";
    private static final String transactionTypeParamName = "type";
    private static final String transactionCurrencyParamName = "currency";

    // associations related part of response
    public static final String groupsParamName = "groups";

    // template related part of response
    public static final String officeOptionsParamName = "officeOptions";
    public static final String staffOptionsParamName = "staffOptions";

    public static final String datatables = "datatables";
    public static final String clientEntityName = "clients";

    public static final String depositedDateParamName = "depositedDate";
    public static final String transactionNoParamName = "transactionNo";

    /**
     * These parameters will match the class level parameters of
     * {@link ClientData}. Where possible, we try to get response parameters to
     * match those of request parameters.
     */
    protected static final Set<String> CLIENT_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName,
            accountNoParamName,
            externalIdParamName, externalIddParamName,statusParamName, activeParamName, activationDateParamName, firstnameParamName, spousenameParamName,
            lastnameParamName, fullnameParamName, displayNameParamName, mobileNoParamName,gstNoParamName,incdailysalesParamName,exprawmaterialParamName,expstaffsalParamName,
            exppowertelephoneParamName,exprepairsmaintainanceParamName,expcommbrokerageParamName,increntParamName,incinterestParamName,incothersParamName,
            tothouseholdincParamName,tothouseholdincParamName,tothouseholdincParamName,totnetdispfamilyParamName,totnetdispfamilyParamName,totnetdispfamilyParamName,idproofNoParamName, addrproofNoParamName,expinterestParamName,
            expofficerentParamName,exptravelParamName,expothersParamName, totbusinessprofitParamName,incspouseParamName, emailAddressParamName, officeIdParamName, officeNameParamName,
            transferToOfficeIdParamName, transferToOfficeNameParamName, hierarchyParamName, imageIdParamName, imagePresentParamName,
            staffIdParamName, staffNameParamName, timelineParamName, groupsParamName, officeOptionsParamName, staffOptionsParamName,
            dateOfBirthParamName,lastverifiedSecondaryidDateParamName,lastverifiedmobiledateParamName,lastverifiedadhardateParamName, genderIdParamName,statusOneIdParamName,statusTwoIdParamName,introducerIdParamName,sourceoneIdParamName,sourcetwoIdParamName,purposeoneIdParamName,purposetwoIdParamName,fatherspouseIdParamName,educationIdParamName,maritalIdParamName, professionIdParamName, belongingIdParamName,annualIdParamName,
            landIdParamName,houseIdParamName, formIdParamName, titleIdParamName,religionIdParamName,alternateNoIdParamName, idproofIdParamName,addrproofIdParamName,clientTypeParamName, clientClassificationParamName, legalFormParamName, 
            clientNonPersonDetailsParamName, isStaffParamName,lastverifiedadharParamName,debtParamName,incomeParamName,debtincratioParamName,depositedDateParamName,transactionNoParamName,cpvdataParamName));

    protected static final Set<String> CLIENT_CHARGES_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList
            (chargeIdParamName,
            clientIdParamName, chargeNameParamName, penaltyParamName, chargeTimeTypeParamName, dueAsOfDateParamName,
            chargeCalculationTypeParamName, currencyParamName, amountWaivedParamName, amountWrittenOffParamName, amountOutstandingParamName,
            amountOrPercentageParamName, amountParamName, amountPaidParamName, chargeOptionsParamName, transactionsParamName,depositedDateParamName,transactionNoParamName));

    protected static final Set<String> CLIENT_TRANSACTION_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList
            (idParamName,
            transactionAmountParamName, paymentDetailDataParamName, reversedParamName, dateParamName, officeIdParamName,
            officeNameParamName, transactionTypeParamName, transactionCurrencyParamName, externalIdParamName, externalIddParamName,submittedOnDateParamName,depositedDateParamName,transactionNoParamName));

}