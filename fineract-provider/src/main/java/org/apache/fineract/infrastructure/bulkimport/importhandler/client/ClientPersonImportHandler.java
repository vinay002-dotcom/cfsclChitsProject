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
package org.apache.fineract.infrastructure.bulkimport.importhandler.client;

import com.google.gson.GsonBuilder;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.constants.ClientEntityConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.ClientPersonConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.infrastructure.bulkimport.data.Count;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandler;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandlerUtils;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.DateSerializer;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.address.data.AddressData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.IndexedColors;

//import org.apache.poi.ss.usermodel.*;
//import org.joda.time.LocalDate;
import java.time.LocalDate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientPersonImportHandler implements ImportHandler {

    private Workbook workbook;
    private List<ClientData> clients;

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

   @Autowired
    public ClientPersonImportHandler(final PortfolioCommandSourceWritePlatformService
            commandsSourceWritePlatformService) {
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @Override
    public Count process(Workbook workbook, String locale, String dateFormat) {
        this.workbook = workbook;
        this.clients=new ArrayList<>();
        readExcelFile(locale,dateFormat);
        return importEntity(dateFormat);
    }

    public void readExcelFile(final String locale, final String dateFormat) {
        Sheet clientSheet=workbook.getSheet(TemplatePopulateImportConstants.CLIENT_PERSON_SHEET_NAME);
        Integer noOfEntries= ImportHandlerUtils.getNumberOfRows(clientSheet,0);
        for (int rowIndex=1;rowIndex<=noOfEntries;rowIndex++){
            Row row;
                row=clientSheet.getRow(rowIndex);
                if (ImportHandlerUtils.isNotImported(row, ClientPersonConstants.STATUS_COL)){
                    clients.add(readClient(row,locale,dateFormat));
                }
        }
    }

    private ClientData readClient(Row row,final String locale, final String dateFormat) {
        Long legalFormId=1L;
        String firstName = ImportHandlerUtils.readAsString(ClientPersonConstants.FIRST_NAME_COL, row);
        String lastName = ImportHandlerUtils.readAsString(ClientPersonConstants.LAST_NAME_COL, row);
        String middleName = ImportHandlerUtils.readAsString(ClientPersonConstants.MIDDLE_NAME_COL, row);
        String fsfirstName = ImportHandlerUtils.readAsString(ClientPersonConstants.FSFIRST_NAME_COL, row);
        String fslastName = ImportHandlerUtils.readAsString(ClientPersonConstants.FSLAST_NAME_COL, row);
        String fsmiddleName = ImportHandlerUtils.readAsString(ClientPersonConstants.FSMIDDLE_NAME_COL, row);
        String maidenName = ImportHandlerUtils.readAsString(ClientPersonConstants.MAIDEN_NAME_COL, row);
        String custmotherName = ImportHandlerUtils.readAsString(ClientPersonConstants.CUSTMOTHER_NAME_COL, row);
        String adhar = ImportHandlerUtils.readAsString(ClientPersonConstants.ADHAR_COL, row);
        String nrega = ImportHandlerUtils.readAsString(ClientPersonConstants.NREGA_COL, row);
        String pan = ImportHandlerUtils.readAsString(ClientPersonConstants.PAN_COL, row);
        String officeName = ImportHandlerUtils.readAsString(ClientPersonConstants.OFFICE_NAME_COL, row);
        Long officeId = ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.OFFICE_SHEET_NAME), officeName);
        String staffName = ImportHandlerUtils.readAsString(ClientPersonConstants.STAFF_NAME_COL, row);
        Long staffId = ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.STAFF_SHEET_NAME), staffName);
        String externalId = ImportHandlerUtils.readAsString(ClientPersonConstants.EXTERNAL_ID_COL, row);
        String externalIdd = ImportHandlerUtils.readAsString(ClientPersonConstants.EXTERNAL_IDD_COL, row);
        LocalDate submittedOn=ImportHandlerUtils.readAsDate(ClientPersonConstants.SUBMITTED_ON_COL,row);
        LocalDate activationDate = ImportHandlerUtils.readAsDate(ClientPersonConstants.ACTIVATION_DATE_COL, row);
        Boolean active = ImportHandlerUtils.readAsBoolean(ClientPersonConstants.ACTIVE_COL, row);
        if (!active){
            activationDate=submittedOn;
        }
        String mobileNo=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.MOBILE_NO_COL, row)!=null)
            mobileNo = ImportHandlerUtils.readAsLong(ClientPersonConstants.MOBILE_NO_COL, row).toString();
        
        String gstNo=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.GST_NO_COL, row)!=null)
        	gstNo = ImportHandlerUtils.readAsLong(ClientPersonConstants.GST_NO_COL, row).toString();
        
        String age=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.AGE_COL, row)!=null)
            age = ImportHandlerUtils.readAsLong(ClientPersonConstants.AGE_COL, row).toString();
        
        String nomrelationshipid=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_RELATIONSHIP_ID_COL, row)!=null)
        	nomrelationshipid = ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_RELATIONSHIP_ID_COL, row).toString();
        
        String nomgenderid=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_GENDER_ID_COL, row)!=null)
        	nomgenderid = ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_GENDER_ID_COL, row).toString();
        
        String nomage=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_AGE_COL, row)!=null)
        	nomage = ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_AGE_COL, row).toString();
        
        String nomprofessionid=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_PROFESSION_ID_COL, row)!=null)
        	nomprofessionid = ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_PROFESSION_ID_COL, row).toString();
        
        String nomeducationalid=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_EDUCATION_ID_COL, row)!=null)
        	nomeducationalid = ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_EDUCATION_ID_COL, row).toString();
        
        String nommaritalid=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_MARITAL_ID_COL, row)!=null)
        	nommaritalid = ImportHandlerUtils.readAsLong(ClientEntityConstants.NOM_MARITAL_ID_COL, row).toString();
        
        String incdailysales=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.incdailysales_COL, row)!=null)
        	incdailysales = ImportHandlerUtils.readAsLong(ClientPersonConstants.MOBILE_NO_COL, row).toString();
       
        String exprawmaterial=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.exprawmaterial_COL, row)!=null)
            exprawmaterial = ImportHandlerUtils.readAsLong(ClientPersonConstants.exprawmaterial_COL, row).toString();
       
        String expstaffsal=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.expstaffsal_COL, row)!=null)
        	expstaffsal = ImportHandlerUtils.readAsLong(ClientPersonConstants.expstaffsal_COL, row).toString();
       
        String exppowertelephone=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.exppowertelephone_COL, row)!=null)
        	exppowertelephone = ImportHandlerUtils.readAsLong(ClientPersonConstants.exppowertelephone_COL, row).toString();
       
        String exprepairsmaintainance=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.exprepairsmaintainance_COL, row)!=null)
        	exprepairsmaintainance = ImportHandlerUtils.readAsLong(ClientPersonConstants.exprepairsmaintainance_COL, row).toString();
       
        String expcommbrokerage=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.expcommbrokerage_COL, row)!=null)
        	expcommbrokerage = ImportHandlerUtils.readAsLong(ClientPersonConstants.expcommbrokerage_COL, row).toString();
           
        String incrent=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.incrent_COL, row)!=null)
        	incrent = ImportHandlerUtils.readAsLong(ClientPersonConstants.MOBILE_NO_COL, row).toString();
       
        String incinterest=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.incinterest_COL, row)!=null)
            incinterest = ImportHandlerUtils.readAsLong(ClientPersonConstants.incinterest_COL, row).toString();
       
        String incothers=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.incothers_COL, row)!=null)
        	incothers = ImportHandlerUtils.readAsLong(ClientPersonConstants.incothers_COL, row).toString();
       
        String tothouseholdinc=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.tothouseholdinc_COL, row)!=null)
        	tothouseholdinc = ImportHandlerUtils.readAsLong(ClientPersonConstants.tothouseholdinc_COL, row).toString();
       
        String exphousehold=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.exphousehold_COL, row)!=null)
        	exphousehold = ImportHandlerUtils.readAsLong(ClientPersonConstants.exphousehold_COL, row).toString();
       
        String expotherloans=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.expotherloans_COL, row)!=null)
        	expotherloans = ImportHandlerUtils.readAsLong(ClientPersonConstants.expotherloans_COL, row).toString();
        
        String totnetdispfamily=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.totnetdispfamily_COL, row)!=null)
        	totnetdispfamily = ImportHandlerUtils.readAsLong(ClientPersonConstants.totnetdispfamily_COL, row).toString();
       

        
        String idproofNo=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.IDPROOF_NO_COL, row)!=null)
        	idproofNo = ImportHandlerUtils.readAsLong(ClientPersonConstants.MOBILE_NO_COL, row).toString();
        
        String addrproofNo=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.ADDRPROOF_NO_COL, row)!=null)
        	addrproofNo = ImportHandlerUtils.readAsLong(ClientPersonConstants.ADDRPROOF_NO_COL, row).toString();
        
        String expinterest=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.expinterest_COL, row)!=null)
        	expinterest = ImportHandlerUtils.readAsLong(ClientPersonConstants.expinterest_COL, row).toString();
        
        String expofficerent=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.expofficerent_COL, row)!=null)
        	expofficerent = ImportHandlerUtils.readAsLong(ClientPersonConstants.expofficerent_COL, row).toString();
        
        String exptravel=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.exptravel_COL, row)!=null)
        	exptravel = ImportHandlerUtils.readAsLong(ClientPersonConstants.exptravel_COL, row).toString();
        
        String expothers=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.expothers_COL, row)!=null)
        	expothers = ImportHandlerUtils.readAsLong(ClientPersonConstants.expothers_COL, row).toString();
        
        String totbusinessprofit=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.totbusinessprofit_COL, row)!=null)
        	totbusinessprofit = ImportHandlerUtils.readAsLong(ClientPersonConstants.totbusinessprofit_COL, row).toString();
        
        String incspouse=null;
        if (ImportHandlerUtils.readAsLong(ClientPersonConstants.incspouse_COL, row)!=null)
        	incspouse = ImportHandlerUtils.readAsLong(ClientPersonConstants.incspouse_COL, row).toString();
         
        LocalDate dob = ImportHandlerUtils.readAsDate(ClientPersonConstants.DOB_COL, row);
       

        String clientType=ImportHandlerUtils.readAsString(ClientPersonConstants.CLIENT_TYPE_COL, row);
        Long clientTypeId = null;
        if (clientType!=null) {
            String[] clientTypeAr = new String[1];//clientType.split("-");
            if (clientTypeAr[1] != null) {
                clientTypeId = Long.parseLong(clientTypeAr[1]);
            }
        }
        
     
        String gender=ImportHandlerUtils.readAsString(ClientPersonConstants.GENDER_COL, row);
        Long genderId = null;
        if (gender!=null) {
            String[] genderAr = new String[1];//gender.split("-");
            if (genderAr[1] != null)
                genderId = Long.parseLong(genderAr[1]);
        }
        
        String statusOne=ImportHandlerUtils.readAsString(ClientPersonConstants.STATUSONE_COL, row);
        Long statusOneId = null;
        if (statusOne!=null) {
            String[] statusOneAr = new String[1];//statusOne.split("-");
            if (statusOneAr[1] != null)
            	statusOneId = Long.parseLong(statusOneAr[1]);
        }
        
        String statusTwo=ImportHandlerUtils.readAsString(ClientPersonConstants.STATUSTWO_COL, row);
        Long statusTwoId = null;
        if (statusTwo!=null) {
            String[] statusTwoAr = new String[1];//statusTwo.split("-");
            if (statusTwoAr[1] != null)
            	statusTwoId = Long.parseLong(statusTwoAr[1]);
        }
        
        String introducer=ImportHandlerUtils.readAsString(ClientPersonConstants.INTRODUCER_COL, row);
        Long introducerId = null;
        if (introducer!=null) {
            String[] introducerAr = new String[1];//introducer.split("-");
            if (introducerAr[1] != null)
            	introducerId = Long.parseLong(introducerAr[1]);
        }
       
        String sourceone=ImportHandlerUtils.readAsString(ClientPersonConstants.SOURCEONE_COL, row);
        Long sourceoneId = null;
        if (sourceone!=null) {
            String[] sourceoneAr = new String[1];//sourceone.split("-");
            if (sourceoneAr[1] != null)
            	sourceoneId = Long.parseLong(sourceoneAr[1]);
        }

        String sourcetwo=ImportHandlerUtils.readAsString(ClientPersonConstants.SOURCETWO_COL, row);
        Long sourcetwoId = null;
        if (sourcetwo!=null) {
            String[] sourcetwoAr = new String[1];//sourcetwo.split("-");
            if (sourcetwoAr[1] != null)
            	sourcetwoId = Long.parseLong(sourcetwoAr[1]);
        }

        String purposeone=ImportHandlerUtils.readAsString(ClientPersonConstants.PURPOSEONE_COL, row);
        Long purposeoneId = null;
        if (purposeone!=null) {
            String[] purposeoneAr = new String[1];//purposeone.split("-");
            if (purposeoneAr[1] != null)
            	purposeoneId = Long.parseLong(purposeoneAr[1]);
        }

        String purposetwo=ImportHandlerUtils.readAsString(ClientPersonConstants.PURPOSETWO_COL, row);
        Long purposetwoId = null;
        if (purposetwo!=null) {
            String[] purposetwoAr = new String[1];//purposetwo.split("-");
            if (purposetwoAr[1] != null)
            	purposetwoId = Long.parseLong(purposetwoAr[1]);
        }
        String fatherspouse=ImportHandlerUtils.readAsString(ClientPersonConstants.FATHERSPOUSE_COL, row);
        Long fatherspouseId = null;
        if (fatherspouse!=null) {
            String[] fatherspouseAr = new String[1];//fatherspouse.split("-");
            if (fatherspouseAr[1] != null)
            	fatherspouseId = Long.parseLong(fatherspouseAr[1]);
        }
        
        String education=ImportHandlerUtils.readAsString(ClientPersonConstants.EDUCATION_COL, row);
        Long educationId = null;
        if (education!=null) {
            String[] educationAr = new String[1];//education.split("-");
            if (educationAr[1] != null)
            	educationId = Long.parseLong(educationAr[1]);
        }
        
        String marital=ImportHandlerUtils.readAsString(ClientPersonConstants.MARITAL_COL, row);
        Long maritalId = null;
        if (marital!=null) {
            String[] maritalAr = new String[1];//marital.split("-");
            if (maritalAr[1] != null)
            	maritalId = Long.parseLong(maritalAr[1]);
        }
        
        String profession=ImportHandlerUtils.readAsString(ClientPersonConstants.PROFESSION_COL, row);
        Long professionId = null;
        if (profession!=null) {
            String[] professionAr = new String[1];//profession.split("-");
            if (professionAr[1] != null)
            	professionId = Long.parseLong(professionAr[1]);
        }
        
        String belonging=ImportHandlerUtils.readAsString(ClientPersonConstants.BELONGING_COL, row);
        Long belongingId = null;
        if (belonging!=null) {
            String[] belongingAr = new String[1];//belonging.split("-");
            if (belongingAr[1] != null)
            	belongingId = Long.parseLong(belongingAr[1]);
        }
        
        String annual=ImportHandlerUtils.readAsString(ClientPersonConstants.ANNUAL_COL, row);
        Long annualId = null;
        if (annual!=null) {
            String[] annualAr = new String[1];//annual.split("-");
            if (annualAr[1] != null)
            	annualId = Long.parseLong(annualAr[1]);
        }
        
        String land=ImportHandlerUtils.readAsString(ClientPersonConstants.LAND_COL, row);
        Long landId = null;
        if (land!=null) {
            String[] landAr = new String[1];//land.split("-");
            if (landAr[1] != null)
            	landId = Long.parseLong(landAr[1]);
        }
        
        String house=ImportHandlerUtils.readAsString(ClientPersonConstants.HOUSE_COL, row);
        Long houseId = null;
        if (house!=null) {
            String[] houseAr = new String[1];//house.split("-");
            if (houseAr[1] != null)
            	houseId = Long.parseLong(houseAr[1]);
        }
        
        String form=ImportHandlerUtils.readAsString(ClientPersonConstants.FORM_COL, row);
        Long formId = null;
        if (form!=null) {
            String[] formAr = new String[1];//form.split("-");
            if (formAr[1] != null)
            	formId = Long.parseLong(formAr[1]);
        }
        
        String title=ImportHandlerUtils.readAsString(ClientPersonConstants.TITLE_COL, row);
        Long titleId = null;
        if (title!=null) {
            String[] titleAr = new String[1];//title.split("-");
            if (titleAr[1] != null)
            	titleId = Long.parseLong(titleAr[1]);
        }
        
        String religion=ImportHandlerUtils.readAsString(ClientPersonConstants.RELIGION_COL, row);
        Long religionId = null;
        if (religion!=null) {
            String[] religionAr = new String[1];//religion.split("-");
            if (religionAr[1] != null)
            	religionId = Long.parseLong(religionAr[1]);
        }
        
        String idproof=ImportHandlerUtils.readAsString(ClientPersonConstants.IDPROOF_COL, row);
        Long idproofId = null;
        if (idproof!=null) {
            String[] idproofAr = new String[1];//idproof.split("-");
            if (idproofAr[1] != null)
            	idproofId = Long.parseLong(idproofAr[1]);
        }
        
        String addrproof=ImportHandlerUtils.readAsString(ClientPersonConstants.ADDRPROOF_COL, row);
        Long addrproofId = null;
        if (addrproof!=null) {
            String[] addrproofAr = new String[1];//addrproof.split("-");
            if (addrproofAr[1] != null)
            	addrproofId = Long.parseLong(addrproofAr[1]);
        }
        
        String clientClassification= ImportHandlerUtils.readAsString(ClientPersonConstants.CLIENT_CLASSIFICATION_COL, row);
        Long clientClassicationId = null;
        if (clientClassification!=null) {
            String[] clientClassificationAr = new String[1];//clientClassification.split("-");
            if (clientClassificationAr[1] != null)
                clientClassicationId = Long.parseLong(clientClassificationAr[1]);
        }
        Boolean isStaff = ImportHandlerUtils.readAsBoolean(ClientPersonConstants.IS_STAFF_COL, row);

        AddressData addressDataObj=null;
        if (ImportHandlerUtils.readAsBoolean(ClientPersonConstants.ADDRESS_ENABLED_COL,row)) {
            String addressType=ImportHandlerUtils.readAsString(ClientPersonConstants.ADDRESS_TYPE_COL, row);
            Long addressTypeId = null;
            if (addressType!=null) {
                String[] addressTypeAr = new String[1];//addressType.split("-");

                if (addressTypeAr[1] != null)
                    addressTypeId = Long.parseLong(addressTypeAr[1]);
            }
            
            String houseNo = ImportHandlerUtils.readAsString(ClientPersonConstants.HOUSENO_COL, row);
            String street = ImportHandlerUtils.readAsString(ClientPersonConstants.STREET_COL, row);
            String addressLine1 = ImportHandlerUtils.readAsString(ClientPersonConstants.ADDRESS_LINE_1_COL, row);
            String addressLine2 = ImportHandlerUtils.readAsString(ClientPersonConstants.ADDRESS_LINE_1_COL, row);
            String addressLine3 = ImportHandlerUtils.readAsString(ClientPersonConstants.ADDRESS_LINE_1_COL, row);
            String city = ImportHandlerUtils.readAsString(ClientPersonConstants.CITY_COL, row);
            // String email = ImportHandlerUtils.readAsString(ClientPersonConstants.EMAIL_COL, row);
            // String landLine = ImportHandlerUtils.readAsString(ClientPersonConstants.LANDLINE_COL, row);
            // String mobileNum = ImportHandlerUtils.readAsString(ClientPersonConstants.MOBILENUM_COL, row);

            String postalCode = ImportHandlerUtils.readAsString(ClientPersonConstants.POSTAL_CODE_COL, row);
            Boolean isActiveAddress = ImportHandlerUtils.readAsBoolean(ClientPersonConstants.IS_ACTIVE_ADDRESS_COL, row);

            String stateProvince=ImportHandlerUtils.readAsString(ClientPersonConstants.STATE_PROVINCE_COL, row);
            Long stateProvinceId = null;
            if (stateProvince!=null) {
                String[] stateProvinceAr = new String[1];//stateProvince.split("-");
                if (stateProvinceAr[1] != null)
                    stateProvinceId = Long.parseLong(stateProvinceAr[1]);
            }
            String country=ImportHandlerUtils.readAsString(ClientPersonConstants.COUNTRY_COL, row);
            Long countryId=null;
            if (country!=null) {
                String[] countryAr = new String[1];//country.split("-");
                if (countryAr[1] != null)
                    countryId = Long.parseLong(countryAr[1]);
            }
            
            String district=ImportHandlerUtils.readAsString(ClientPersonConstants.DISTRICT_COL, row);
            Long districtId=null;
            if (district!=null) {
                String[] districtAr = new String[1];//district.split("-");
                if (districtAr[1] != null)
                	districtId = Long.parseLong(districtAr[1]);
            }
            String taluka= ImportHandlerUtils.readAsString(ClientPersonConstants.TALUKA_COL, row);
            Long talukaId = null;
            if (taluka!=null) {
                String[] talukaAr = new String[1];//taluka.split("-");
                if (talukaAr[1] != null)
                    talukaId = Long.parseLong(talukaAr[1]);
            }

             addressDataObj = new AddressData(addressTypeId, houseNo, street, addressLine1, addressLine2, addressLine3,
                    city, talukaId, districtId, postalCode, isActiveAddress, stateProvinceId, countryId,null,null);
        }
        return ClientData.importClientPersonInstance(legalFormId,row.getRowNum(),firstName,lastName,middleName,
        		fsfirstName,fslastName,fsmiddleName,maidenName,custmotherName, null, null, null, adhar,nrega, pan,null, null, null, null, null, null, submittedOn,activationDate,active,externalId,externalIdd,
                officeId,staffId,mobileNo,gstNo,age,nomrelationshipid,
                nomgenderid,
                nomage,
                nomprofessionid,
                nomeducationalid,
                nommaritalid,incdailysales, exprawmaterial,expstaffsal, exppowertelephone,exprepairsmaintainance,expcommbrokerage,incrent,incinterest,incothers,
                tothouseholdinc,exphousehold,expotherloans,totnetdispfamily,expinterest,expofficerent, exptravel,expothers,totbusinessprofit,incspouse,idproofNo, addrproofNo,
                dob,null,null, null, clientTypeId,  genderId,fatherspouseId, educationId, maritalId, professionId, belongingId, annualId,
                landId, houseId, formId, titleId, religionId,null,idproofId,addrproofId,clientClassicationId,isStaff,addressDataObj,locale,dateFormat,null,null,null,null,false);

        }

    public Count importEntity(String dateFormat) {
        Sheet clientSheet=workbook.getSheet(TemplatePopulateImportConstants.CLIENT_PERSON_SHEET_NAME);
        int successCount=0;
        int errorCount=0;
        String errorMessage="";
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new DateSerializer(dateFormat));
        for (ClientData client: clients) {
            try {
                String payload=gsonBuilder.create().toJson(client);
                final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                        .createClient() //
                        .withJson(payload) //
                        .build(); //
                final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(commandRequest);
                successCount++;
                Cell statusCell = clientSheet.getRow(client.getRowIndex()).createCell(ClientPersonConstants.STATUS_COL);
                statusCell.setCellValue(TemplatePopulateImportConstants.STATUS_CELL_IMPORTED);
                statusCell.setCellStyle(ImportHandlerUtils.getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            }catch (RuntimeException ex){
                errorCount++;
                ex.printStackTrace();
                errorMessage=ImportHandlerUtils.getErrorMessage(ex);
                ImportHandlerUtils.writeErrorMessage(clientSheet,client.getRowIndex(),errorMessage,ClientPersonConstants.STATUS_COL);
            }
        }
        clientSheet.setColumnWidth(ClientPersonConstants.STATUS_COL, TemplatePopulateImportConstants.SMALL_COL_SIZE);
        ImportHandlerUtils.writeString(ClientPersonConstants.STATUS_COL, clientSheet.
                getRow(TemplatePopulateImportConstants.ROWHEADER_INDEX), TemplatePopulateImportConstants.STATUS_COLUMN_HEADER);

        return Count.instance(successCount,errorCount);
    }


}