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
import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.infrastructure.bulkimport.data.Count;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandler;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandlerUtils;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.DateSerializer;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.address.data.AddressData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.data.ClientNonPersonData;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.time.LocalDate;


//import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ClientEntityImportHandler implements ImportHandler {

    private Workbook workbook;
    private List<ClientData> clients;

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public ClientEntityImportHandler(final PortfolioCommandSourceWritePlatformService
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
        Sheet clientSheet=workbook.getSheet(TemplatePopulateImportConstants.CLIENT_ENTITY_SHEET_NAME);
        Integer noOfEntries= ImportHandlerUtils.getNumberOfRows(clientSheet,0);
        for (int rowIndex=1;rowIndex<=noOfEntries;rowIndex++){
            Row row;
                row=clientSheet.getRow(rowIndex);
                if (ImportHandlerUtils.isNotImported(row, ClientEntityConstants.STATUS_COL)){
                    clients.add(readClient(row,locale,dateFormat));
                }
        }
    }

    private ClientData readClient(Row row,final String locale, final String dateFormat) {
        Long legalFormId=2L;
        String name = ImportHandlerUtils.readAsString(ClientEntityConstants.NAME_COL, row);
        String officeName = ImportHandlerUtils.readAsString(ClientEntityConstants.OFFICE_NAME_COL, row);
        Long officeId = ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.OFFICE_SHEET_NAME), officeName);
        String staffName = ImportHandlerUtils.readAsString(ClientEntityConstants.STAFF_NAME_COL, row);
        Long staffId = ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.STAFF_SHEET_NAME), staffName);
        LocalDate incorportionDate=ImportHandlerUtils.readAsDate(ClientEntityConstants.INCOPORATION_DATE_COL,row);
        LocalDate incorporationTill=ImportHandlerUtils.readAsDate(ClientEntityConstants.INCOPORATION_VALID_TILL_COL,row);
        String mobileNo=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.MOBILE_NO_COL, row)!=null)
         mobileNo = ImportHandlerUtils.readAsLong(ClientEntityConstants.MOBILE_NO_COL, row).toString();
        
        String gstNo=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.GST_NO_COL, row)!=null)
        	gstNo = ImportHandlerUtils.readAsLong(ClientEntityConstants.GST_NO_COL, row).toString();
        
        String age=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.AGE_COL, row)!=null)
         age = ImportHandlerUtils.readAsLong(ClientEntityConstants.AGE_COL, row).toString();
        
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
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.incdailysales_COL, row)!=null)
        	incdailysales = ImportHandlerUtils.readAsLong(ClientEntityConstants.incdailysales_COL, row).toString();
  
        String exprawmaterial=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.exprawmaterial_COL, row)!=null)
         exprawmaterial = ImportHandlerUtils.readAsLong(ClientEntityConstants.exprawmaterial_COL, row).toString();
  
        String expstaffsal=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.expstaffsal_COL, row)!=null)
         expstaffsal = ImportHandlerUtils.readAsLong(ClientEntityConstants.expstaffsal_COL, row).toString();
         
        String exppowertelephone=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.exppowertelephone_COL, row)!=null)
        	exppowertelephone = ImportHandlerUtils.readAsLong(ClientEntityConstants.exppowertelephone_COL, row).toString();
  
        String exprepairsmaintainance=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.exprepairsmaintainance_COL, row)!=null)
        	exprepairsmaintainance = ImportHandlerUtils.readAsLong(ClientEntityConstants.exprepairsmaintainance_COL, row).toString();
  
        String expcommbrokerage=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.expcommbrokerage_COL, row)!=null)
        	expcommbrokerage = ImportHandlerUtils.readAsLong(ClientEntityConstants.expcommbrokerage_COL, row).toString();
          
        String incrent=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.incrent_COL, row)!=null)
         incrent = ImportHandlerUtils.readAsLong(ClientEntityConstants.incrent_COL, row).toString();
  
        String incinterest=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.incinterest_COL, row)!=null)
         incinterest = ImportHandlerUtils.readAsLong(ClientEntityConstants.incinterest_COL, row).toString();
  
        String incothers=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.incothers_COL, row)!=null)
         incothers = ImportHandlerUtils.readAsLong(ClientEntityConstants.incothers_COL, row).toString();
  
        String tothouseholdinc=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.tothouseholdinc_COL, row)!=null)
        	tothouseholdinc = ImportHandlerUtils.readAsLong(ClientEntityConstants.tothouseholdinc_COL, row).toString();
  
        String exphousehold=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.exphousehold_COL, row)!=null)
        	exphousehold = ImportHandlerUtils.readAsLong(ClientEntityConstants.exphousehold_COL, row).toString();
  
        String expotherloans=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.expotherloans_COL, row)!=null)
        	expotherloans = ImportHandlerUtils.readAsLong(ClientEntityConstants.expotherloans_COL, row).toString();
  
        String totnetdispfamily=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.totnetdispfamily_COL, row)!=null)
        	totnetdispfamily = ImportHandlerUtils.readAsLong(ClientEntityConstants.totnetdispfamily_COL, row).toString();

        String idproofNo=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.IDPROOF_NO_COL, row)!=null)
         idproofNo = ImportHandlerUtils.readAsLong(ClientEntityConstants.IDPROOF_NO_COL, row).toString();

        String addrproofNo=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.ADDRPROOF_NO_COL, row)!=null)
         addrproofNo = ImportHandlerUtils.readAsLong(ClientEntityConstants.ADDRPROOF_NO_COL, row).toString();

        String expinterest=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.exp_interest_COL, row)!=null)
        	expinterest = ImportHandlerUtils.readAsLong(ClientEntityConstants.MOBILE_NO_COL, row).toString();
        
        String expofficerent=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.exp_office_rent_COL, row)!=null)
        	expofficerent = ImportHandlerUtils.readAsLong(ClientEntityConstants.exp_office_rent_COL, row).toString();
        
        String exptravel=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.exp_travel_COL, row)!=null)
        	exptravel = ImportHandlerUtils.readAsLong(ClientEntityConstants.exp_travel_COL, row).toString();
        
        String expothers=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.exp_others_COL, row)!=null)
        	expothers = ImportHandlerUtils.readAsLong(ClientEntityConstants.exp_others_COL, row).toString();
        
        String totbusinessprofit=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.tot_business_profit_COL, row)!=null)
        	totbusinessprofit = ImportHandlerUtils.readAsLong(ClientEntityConstants.tot_business_profit_COL, row).toString();
        
        String incspouse=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.inc_spouse_COL, row)!=null)
        	incspouse = ImportHandlerUtils.readAsLong(ClientEntityConstants.inc_spouse_COL, row).toString();
        
        String statusOne=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.STATUS_ONE_COL, row)!=null)
        	statusOne = ImportHandlerUtils.readAsLong(ClientEntityConstants.STATUS_ONE_COL, row).toString();
        
        String statusTwo=null;
        if (ImportHandlerUtils.readAsLong(ClientEntityConstants.STATUS_TWO_COL, row)!=null)
        	statusTwo = ImportHandlerUtils.readAsLong(ClientEntityConstants.STATUS_TWO_COL, row).toString();
        
        
        String clientType=ImportHandlerUtils.readAsString(ClientEntityConstants.CLIENT_TYPE_COL, row);
        Long clientTypeId = null;
        if (clientType!=null) {
            String[] clientTypeAr = new String[1];//clientType .split("-");
            if (clientTypeAr[1] != null) {
                clientTypeId = Long.parseLong(clientTypeAr[1]);
            }
        }
        
        String clientClassification= ImportHandlerUtils.readAsString(ClientEntityConstants.CLIENT_CLASSIFICATION_COL, row);
        Long clientClassicationId = null;
        if (clientClassification!=null) {
            String[] clientClassificationAr = new String[1];//clientClassification.split("-");
            if (clientClassificationAr[1] != null)
                clientClassicationId = Long.parseLong(clientClassificationAr[1]);
        }
        String incorporationNo=ImportHandlerUtils.readAsString(ClientEntityConstants.INCOPORATION_NUMBER_COL,row);

        String mainBusinessLine=ImportHandlerUtils.readAsString(ClientEntityConstants.MAIN_BUSINESS_LINE,row);
        Long mainBusinessId = null;
        if (mainBusinessLine!=null) {
            String[] mainBusinessLineAr = new String[1];//ImportHandlerUtils.readAsString(ClientEntityConstants.MAIN_BUSINESS_LINE, row).split("-");
            if (mainBusinessLineAr[1] != null)
                mainBusinessId = Long.parseLong(mainBusinessLineAr[1]);
        }
        String constitution= ImportHandlerUtils.readAsString(ClientEntityConstants.CONSTITUTION_COL,row);
        Long constitutionId = null;
        if (constitution!=null) {
            String[] constitutionAr = new String[1];//constitution.split("-");
            if (constitutionAr[1] != null)
                constitutionId = Long.parseLong(constitutionAr[1]);
        }
        String remarks = ImportHandlerUtils.readAsString(ClientEntityConstants.REMARKS_COL, row);

        ClientNonPersonData clientNonPersonData= ClientNonPersonData.importInstance(incorporationNo,incorporationTill,remarks,
                mainBusinessId,constitutionId,locale,dateFormat);

        String externalId= ImportHandlerUtils.readAsString(ClientEntityConstants.EXTERNAL_ID_COL, row);
        
        String externalIdd= ImportHandlerUtils.readAsString(ClientEntityConstants.EXTERNAL_IDD_COL, row);
        
        LocalDate dateOfBirth=ImportHandlerUtils.readAsDate(ClientEntityConstants.DATE_OF_BIRTH_COL,row);
      
        Boolean active = ImportHandlerUtils.readAsBoolean(ClientEntityConstants.ACTIVE_COL, row);

        LocalDate submittedOn=ImportHandlerUtils.readAsDate(ClientEntityConstants.SUBMITTED_ON_COL,row);

        LocalDate activationDate = ImportHandlerUtils.readAsDate(ClientEntityConstants.ACTIVATION_DATE_COL, row);
        if (!active){
            activationDate=submittedOn;
        }
        AddressData addressDataObj=null;
        if (ImportHandlerUtils.readAsBoolean(ClientEntityConstants.ADDRESS_ENABLED,row)) {
            String addressType = ImportHandlerUtils.readAsString(ClientEntityConstants.ADDRESS_TYPE_COL, row);
            Long addressTypeId = null;
            if (addressType!=null) {
                String[] addressTypeAr = new String[1];//addressType.split("-");
                if (addressTypeAr[1] != null)
                    addressTypeId = Long.parseLong(addressTypeAr[1]);
            }
            
            String houseNo = ImportHandlerUtils.readAsString(ClientEntityConstants.HOUSENO_COL, row);
            String street = ImportHandlerUtils.readAsString(ClientEntityConstants.STREET_COL, row);
            String addressLine1 = ImportHandlerUtils.readAsString(ClientEntityConstants.ADDRESS_LINE_1_COL, row);
            String addressLine2 = ImportHandlerUtils.readAsString(ClientEntityConstants.ADDRESS_LINE_2_COL, row);
            String addressLine3 = ImportHandlerUtils.readAsString(ClientEntityConstants.ADDRESS_LINE_3_COL, row);
            String townVillage = ImportHandlerUtils.readAsString(ClientEntityConstants.TOWNVILLAGE_COL, row);
            String city = ImportHandlerUtils.readAsString(ClientEntityConstants.CITY_COL, row);
            // String email = ImportHandlerUtils.readAsString(ClientEntityConstants.EMAIL_COL, row);
            // String landLine = ImportHandlerUtils.readAsString(ClientEntityConstants.LANDLINE_COL, row);
            // String mobileNum = ImportHandlerUtils.readAsString(ClientEntityConstants.MOBILENUM_COL, row);
          

            String postalCode = ImportHandlerUtils.readAsString(ClientEntityConstants.POSTAL_CODE_COL, row);
            Boolean isActiveAddress = ImportHandlerUtils.readAsBoolean(ClientEntityConstants.IS_ACTIVE_ADDRESS_COL, row);

            String stateProvince=ImportHandlerUtils.readAsString(ClientEntityConstants.STATE_PROVINCE_COL, row);
            Long stateProvinceId = null;
            if (stateProvince!=null) {
                String[] stateProvinceAr = new String[1];//stateProvince.split("-");
                if (stateProvinceAr[1] != null)
                    stateProvinceId = Long.parseLong(stateProvinceAr[1]);
            }
            String country= ImportHandlerUtils.readAsString(ClientEntityConstants.COUNTRY_COL, row);
            Long countryId = null;
            if (country!=null) {
                String[] countryAr = new String[1];//country.split("-");
                if (countryAr[1] != null)
                    countryId = Long.parseLong(countryAr[1]);
            }
            
            String district= ImportHandlerUtils.readAsString(ClientEntityConstants.DISTRICT_COL, row);
            Long districtId = null;
            if (district!=null) {
                String[] districtAr = new String[1];//district.split("-");
                if (districtAr[1] != null)
                	districtId = Long.parseLong(districtAr[1]);
            }
            
            String taluka= ImportHandlerUtils.readAsString(ClientEntityConstants.TALUKA_COL, row);
            Long talukaId = null;
            if (taluka!=null) {
                String[] talukaAr = new String[1];//taluka.split("-");
                if (talukaAr[1] != null)
                    talukaId = Long.parseLong(talukaAr[1]);
            }
            
            addressDataObj = new AddressData(addressTypeId, houseNo,street, addressLine1, addressLine2, addressLine3,
                    city, talukaId, districtId, postalCode, isActiveAddress, stateProvinceId, countryId,null,null);
        }
        return ClientData.importClientEntityInstance(legalFormId,row.getRowNum(),name,officeId,clientTypeId, clientClassicationId,
                staffId,active,activationDate,submittedOn, externalId, externalIdd,dateOfBirth,null,null, null, mobileNo,gstNo,age,nomrelationshipid,
                nomgenderid,
                nomage,
                nomprofessionid,
                nomeducationalid,
                nommaritalid,incdailysales,exprawmaterial,expstaffsal,
                exppowertelephone,exprepairsmaintainance,expcommbrokerage, incrent,incinterest,incothers,
                tothouseholdinc,exphousehold,expotherloans,totnetdispfamily,expinterest,expofficerent,exptravel,expothers,
                totbusinessprofit,incspouse,idproofNo,addrproofNo,clientNonPersonData,addressDataObj,locale,dateFormat,null,null,null,null,false);
    }

    public Count importEntity(String dateFormat) {
        Sheet clientSheet=workbook.getSheet(TemplatePopulateImportConstants.CLIENT_ENTITY_SHEET_NAME);

        int successCount = 0;
        int errorCount = 0;
        String errorMessage = "";

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
                Cell statusCell = clientSheet.getRow(client.getRowIndex()).createCell(ClientEntityConstants.STATUS_COL);
                statusCell.setCellValue(TemplatePopulateImportConstants.STATUS_CELL_IMPORTED);
                statusCell.setCellStyle(ImportHandlerUtils.getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            }catch (RuntimeException ex){
                errorCount++;
                ex.printStackTrace();
                errorMessage=ImportHandlerUtils.getErrorMessage(ex);
                ImportHandlerUtils.writeErrorMessage(clientSheet,client.getRowIndex(),errorMessage,ClientEntityConstants.STATUS_COL);
            }
        }
        clientSheet.setColumnWidth(ClientEntityConstants.STATUS_COL, TemplatePopulateImportConstants.SMALL_COL_SIZE);
        ImportHandlerUtils.writeString(ClientEntityConstants.STATUS_COL, clientSheet.getRow(TemplatePopulateImportConstants.ROWHEADER_INDEX),
                TemplatePopulateImportConstants.STATUS_COLUMN_HEADER);

        return Count.instance(successCount,errorCount);
    }


}
