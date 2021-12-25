/**
 * Licensed to the Apache Software Foundation (ASF) under one;
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
package org.apache.fineract.portfolio.client.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.accountdetails.data.AccountSummaryCollectionData;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.address.data.AddressData;
import org.apache.fineract.portfolio.address.service.AddressReadPlatformService;

import org.apache.fineract.portfolio.client.api.ClientAddressApiResources;
import org.apache.fineract.portfolio.client.api.ClientsApiResource;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.serialization.AdharDataCommand;
import org.apache.fineract.portfolio.client.serialization.AdharWithoutMobileCommandData;
import org.apache.fineract.portfolio.client.serialization.DLDATA;
import org.apache.fineract.portfolio.client.serialization.PanAuthCommand;
import org.apache.fineract.portfolio.client.serialization.PassportDataCommand;
import org.apache.fineract.portfolio.client.serialization.VoterIdDataCommand;
import org.apache.fineract.portfolio.client.serialization.mobileotpresponsedata;

import org.apache.fineract.portfolio.creditreport.service.CreditReportsReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import java.text.SimpleDateFormat;

import java.util.Map;
import java.util.Optional;


import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookPopulatorService;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookService;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


// This file is used to get the request from UI and filter the clientId from json Object and send the remaining body to karza for pan authentication 
// we obtain response from karza and if the client id is present we update the values if client id is not present we will create new client 

@Component
public class ExternalApiWritePlatformServices 
{
	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("consent","pan","clientid","officeId","dlNo","consent","dob","epic_no","adharnumber","otp","shareCode","accessKey","clientData","firstname","request_id","mobile","firstname","passportNo","doi","name","fileNo","aadhaarNo","status-code"));
	private final Set<String> supportedParameters1 =  new HashSet<>(Arrays.asList("officeId","adharnumber","consent","otp","shareCode","accessKey","clientData","clientid","firstname","aadhaarNo")); 
	private final FromJsonHelper fromApiJsonHelper;
	 private final PanAuthCommand panauthcommand;
	  private final PlatformSecurityContext context;
	    private final ClientReadPlatformService readPlatformService;
	    private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
	    private final ToApiJsonSerializer<AccountSummaryCollectionData> clientAccountSummaryToApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final AccountDetailsReadPlatformService accountDetailsReadPlatformService;
	    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
	    private final BulkImportWorkbookService bulkImportWorkbookService;
	    private final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService;
	    private final CodeValueReadPlatformService codeValueReadPlatformService;
	    private final DocumentWritePlatformService documentWritePlatformService;
	    private final DLDATA dldata;
	    private final VoterIdDataCommand voteriddata;
	    private final AdharDataCommand adhardata;
	    private final mobileotpresponsedata mobiledata;
	    private final PassportDataCommand passport;
	    private final AdharWithoutMobileCommandData adharwithoutmobile;
	    private final CreditReportsReadPlatformService cibilreadPlatformService;
	    private final ClientAddressApiResources addresswriteplatformservice;
	    private final AddressReadPlatformService adressreadservices;
	    Long clientid=null;

	 ExternalApiReadService ok = new ExternalApiReadService();
	
		@Autowired
		public ExternalApiWritePlatformServices(FromJsonHelper fromApiJsonHelper,PanAuthCommand panauthcommand,final PlatformSecurityContext context, final ClientReadPlatformService readPlatformService,
	            final ToApiJsonSerializer<ClientData> toApiJsonSerializer,
	            final ToApiJsonSerializer<AccountSummaryCollectionData> clientAccountSummaryToApiJsonSerializer,
	            final ApiRequestParameterHelper apiRequestParameterHelper,
	            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	            final AccountDetailsReadPlatformService accountDetailsReadPlatformService,
	            final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
	            final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService,
	            final BulkImportWorkbookService bulkImportWorkbookService,final CodeValueReadPlatformService codeValueReadPlatformService,final DocumentWritePlatformService documentWritePlatformService,final DLDATA dldata,final VoterIdDataCommand voteriddata,
	            final AdharDataCommand adhardata,final mobileotpresponsedata mobiledata,final PassportDataCommand passport, final AdharWithoutMobileCommandData adharwithoutmobile,final CreditReportsReadPlatformService cibilreadPlatformService, final ClientAddressApiResources addresswriteplatformservice,final AddressReadPlatformService adressreadservices) {
			super();
			this.fromApiJsonHelper = fromApiJsonHelper;	
			this.panauthcommand = panauthcommand;
	
			  this.context = context;
		        this.readPlatformService = readPlatformService;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.clientAccountSummaryToApiJsonSerializer = clientAccountSummaryToApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.accountDetailsReadPlatformService = accountDetailsReadPlatformService;
		        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
		        this.bulkImportWorkbookPopulatorService = bulkImportWorkbookPopulatorService;
		        this.bulkImportWorkbookService = bulkImportWorkbookService;
		        this.codeValueReadPlatformService = codeValueReadPlatformService;
		        this.documentWritePlatformService =documentWritePlatformService;
		        this.dldata = dldata;
		        this.voteriddata = voteriddata;
		        this.adhardata = adhardata;
		        this.mobiledata = mobiledata;
		        this.passport = passport;
		        this.adharwithoutmobile = adharwithoutmobile;
		        this.cibilreadPlatformService = cibilreadPlatformService;
		        this.addresswriteplatformservice = addresswriteplatformservice;
		        this.adressreadservices = adressreadservices;
		}
		
	  
		@SuppressWarnings("null")
		public String validateforpanAuth(final String requestData,final String karza,final String value,final String url,String timeout) throws IOException, Exception
	    {
	    	//creating a constructor for clientApi resource
	    	ClientsApiResource cp = new ClientsApiResource(context,readPlatformService,toApiJsonSerializer,clientAccountSummaryToApiJsonSerializer,apiRequestParameterHelper,commandsSourceWritePlatformService,
	    												   accountDetailsReadPlatformService,savingsAccountReadPlatformService,bulkImportWorkbookPopulatorService,bulkImportWorkbookService,fromApiJsonHelper,cibilreadPlatformService);
	       
	    	//if the request is blank then we throw an exception
	    	if (StringUtils.isBlank(requestData)) 
	        {
	            throw new InvalidJsonException();
	        }
	    
	   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	   this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, requestData, this.supportedParameters);  
	   
	   //parsing the request and removing clientid and sending the request
	   final JsonElement requestelement = this.fromApiJsonHelper.parse(requestData);
	   final JsonObject re = requestelement.getAsJsonObject();
	   re.remove("clientid");
	   re.remove("officeId");
	   re.remove("firstname");
	   String request = requestelement.toString();
	   
	   ////System.out.println("request sent"+request);
	   //creating a new response body by extracting request and response from karza
	   final JsonElement element1 = this.fromApiJsonHelper.parse(requestData);
	   
	   final JsonObject result = element1.getAsJsonObject();
	   result.remove("clientid");
	   result.remove("consent");
	   result.remove("pan");
	   result.remove("firstname");
	   result.remove("officeId");
	   String pan = null;
	   final JsonElement element = this.fromApiJsonHelper.parse(requestData);
	   Date date = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
	   formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
		String strDate = formatter.format(date);
	  
//	   	Timestamp ts=new Timestamp(System.currentTimeMillis());
//	   	Date date=ts; 
//	   String strDate = date.toString();
	  
	 
	   
	   Long clientid = this.fromApiJsonHelper.extractLongNamed("clientid", element);
	   this.setClientid(clientid);
	   Long officeid = this.fromApiJsonHelper.extractLongNamed("officeId", element);
	   String name = this.fromApiJsonHelper.extractStringNamed("firstname", element);
	   ////System.out.println(clientid+" clientid");
	   String documentkey = this.fromApiJsonHelper.extractStringNamed("pan", element);
	   //sending request to karza 
	   String response = ok.post(karza,value,url,request,timeout);
	   ////System.out.println("pan response "+response);
	   //obtaining the response parameters from karza
	   String []abc= this.panauthcommand.validateForCreate(response);
	 
	   String firstname = this.panauthcommand.getName();
	   PdfApp.pdf(abc, "",firstname+" panstatus");
	   ////System.out.println("-----------------"+this.panauthcommand.getStatuscode());
	   if(this.panauthcommand.getStatuscode()==101)
	   {
		   pan =  this.fromApiJsonHelper.extractStringNamed("pan", element);
		  
	   }
	   else
	   {
		   
		   
		   result.addProperty("result", "failure");
		   
		   return result.toString();
	   }
	   result.addProperty("lastverifiedsecondaryid", pan);
	   result.addProperty("dateFormat", "dd MMMM yyyy HH:mm:ss");
	   result.addProperty("locale", "en");
	   result.addProperty("lastverifiedSecondaryidDate", strDate);
		//if client id is not null we update the columns if client id is null then we create a new record 
	   if(clientid!=null)
	   {
		   ////System.out.println(result.toString());
		   cp.update(this.clientid,result.toString());
	   }
	   else
	   {
		   result.addProperty("firstname", name);
		   result.addProperty("officeId", officeid);
		  
		   ////System.out.println(result.toString());
		  final String clientresponse = cp.create(result.toString());
		  final JsonElement clientelement = this.fromApiJsonHelper.parse(clientresponse);
		  final JsonObject client = clientelement.getAsJsonObject();
		  clientid = client.get("clientId").getAsLong();
		  this.setClientid(clientid);
		  ////System.out.println("clientId after creation is"+this.getClientid());
	   }
	    result.addProperty("clientid", this.getClientid());
	    result.addProperty("result", "success");
	   try {
		   String sIdentifier = "Pan Card";
           Optional<CodeValueData> codevalue = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier")
               .stream().filter( codeval -> codeval.getName().equals(sIdentifier)).findAny();
           // replace the Filter condition used above respectively based on which verification is done.
           Long mCodeValId = codevalue.get().getId();

           // Construct Json for Creating Identity Entry.. 
           JsonObject newIdentity = new JsonObject();
           newIdentity.addProperty("documentTypeId", mCodeValId); // Identities of Dropdown value Id.. 
           // only one active per DocumentTypeId for a client is allowed. So before inserting new, old should be made inactive and that too same UniqueId/DocumentKey its not allowed.
           newIdentity.addProperty("status", "Active"); 
           newIdentity.addProperty("documentKey", documentkey); // Pan No, Voter ID, DL No.
           newIdentity.addProperty("isdigiverified", true);
           //newIdentity.addProperty("description", "PP112233"); // This is Optional.

           // Below will insert the m_client_identifier record. 
           final CommandWrapper commandRequestIdentity = new CommandWrapperBuilder().createClientIdentifier(clientid)
                   .withJson(newIdentity.toString()).build();
           final CommandProcessingResult resultIdentity = this.commandsSourceWritePlatformService.logCommandSource(commandRequestIdentity);
           
           ////System.out.println("after creating record in identifier ");
          
           File file = new File(PdfApp.path);
           // Now insert the document record for above inserted Identity record
           final DocumentCommand documentCommandIdentity = new DocumentCommand(null, null, "client_identifiers", resultIdentity.resourceId(), sIdentifier, file.getName(),
           file.length(), "application/pdf", null, null,this.panauthcommand.getRequestid());
           InputStream inputStream = new FileInputStream(PdfApp.path);
		final Long documentIdIdentity = this.documentWritePlatformService.createDocument(documentCommandIdentity, inputStream);
 
	   }catch(Exception e)
	   {
		   ////System.out.println(e.getMessage());
		   //to handle the exception
	   }
	   
	   	result.remove("firstname");
	   	result.remove("officeId");
	   	result.remove("dateFormat");
	   	result.remove("locale");
	
	    return result.toString();
	    
	    }
		
		public String validateForDl(final String requestData,final String karza,final String value,final String url,String timeout) throws IOException, Exception
	    {
	    	//creating a constructor for clientApi resource
	    	ClientsApiResource cp = new ClientsApiResource(context,readPlatformService,toApiJsonSerializer,clientAccountSummaryToApiJsonSerializer,apiRequestParameterHelper,commandsSourceWritePlatformService,
	    												   accountDetailsReadPlatformService,savingsAccountReadPlatformService,bulkImportWorkbookPopulatorService,bulkImportWorkbookService,fromApiJsonHelper,cibilreadPlatformService);
	       
	    	//if the request is blank then we throw an exception
	    	if (StringUtils.isBlank(requestData)) 
	        {
	            throw new InvalidJsonException();
	        }
	    
	   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	   this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, requestData, this.supportedParameters);  
	   
	   //parsing the request and removing clientid and sending the request
	   final JsonElement requestelement = this.fromApiJsonHelper.parse(requestData);
	   final JsonObject re = requestelement.getAsJsonObject();
	   re.remove("clientid");
	   re.remove("officeId");
	   re.remove("firstname");
	   String request = requestelement.toString();
	   ////System.out.println("request sent"+request);
	   //creating a new response body by extracting request and response from karza
	   final JsonElement element1 = this.fromApiJsonHelper.parse(requestData);
	   
	   final JsonObject result = element1.getAsJsonObject();
	   result.remove("clientid");
	   result.remove("consent");
	   result.remove("dlNo");
	   result.remove("officeId");
	   result.remove("dob");
	   String dlNo = null;
	   final JsonElement element = this.fromApiJsonHelper.parse(requestData);
	   Date date = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
	   formatter = new SimpleDateFormat("dd MMMM yyyy");
		String strDate = formatter.format(date);
	  
	 ////System.out.println(request+"---");
	 
	   Long clientid = this.fromApiJsonHelper.extractLongNamed("clientid", element);
	   this.setClientid(clientid);
	   Long officeid = this.fromApiJsonHelper.extractLongNamed("officeId", element);
	   this.setClientid(clientid);
	   String name = this.fromApiJsonHelper.extractStringNamed("firstname", element);
	   ////System.out.println(clientid+" clientid");
	   String documentkey = this.fromApiJsonHelper.extractStringNamed("dlNo", element);
	   //sending request to karza 
	   String response = ok.post(karza,value,url,request,timeout);
	   ////System.out.println("DL response "+response);
	   //obtaining the response parameters from karza
	   String []abc= this.dldata.validateForCreate(response);
	 
	   String firstname = this.dldata.getName();
	   
	   PdfApp.pdf(abc,this.dldata.getImage(),firstname+" DL");
	   ////System.out.println("-----------------"+this.panauthcommand.getStatuscode());
	   if(this.dldata.getStatus().equals("Active"))
	   {
		   dlNo =  this.fromApiJsonHelper.extractStringNamed("dlNo", element);
		  
	   }
	   else
	   {
		   result.addProperty("result", "failure");
		   
		   return result.toString();
	   }
	   result.addProperty("lastverifiedsecondaryid", dlNo);
	   result.addProperty("dateFormat", "dd MMMM yyyy");
	   result.addProperty("locale", "en");
	   result.addProperty("lastverifiedSecondaryidDate", strDate);
		//if client id is not null we update the columns if client id is null then we create a new record 
	   if(clientid!=null)
	   {
		   ////System.out.println(result.toString());
		   cp.update(this.clientid,result.toString());
	   }
	   else
	   {
		   result.addProperty("firstname", name);
		   result.addProperty("officeId", officeid);
		  
		   ////System.out.println(result.toString());
		  final String clientresponse = cp.create(result.toString());
		  final JsonElement clientelement = this.fromApiJsonHelper.parse(clientresponse);
		  final JsonObject client = clientelement.getAsJsonObject();
		  clientid = client.get("clientId").getAsLong();
		  this.setClientid(clientid);
		  ////System.out.println("clientId after creation is"+clientid);
	   }
	 	result.addProperty("clientid",this.getClientid());
	 	result.addProperty("result", "success");
	   try {
		   String sIdentifier = "Drivers License";
           Optional<CodeValueData> codevalue = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier")
               .stream().filter( codeval -> codeval.getName().equals(sIdentifier)).findAny();
           // replace the Filter condition used above respectively based on which verification is done.
           Long mCodeValId = codevalue.get().getId();

           // Construct Json for Creating Identity Entry.. 
           JsonObject newIdentity = new JsonObject();
           newIdentity.addProperty("documentTypeId", mCodeValId); // Identities of Dropdown value Id.. 
           // only one active per DocumentTypeId for a client is allowed. So before inserting new, old should be made inactive and that too same UniqueId/DocumentKey its not allowed.
           newIdentity.addProperty("status", "Active"); 
           newIdentity.addProperty("documentKey", documentkey); // Pan No, Voter ID, DL No.
           newIdentity.addProperty("isdigiverified", true);
           //newIdentity.addProperty("description", "PP112233"); // This is Optional.

           // Below will insert the m_client_identifier record. 
           final CommandWrapper commandRequestIdentity = new CommandWrapperBuilder().createClientIdentifier(clientid)
                   .withJson(newIdentity.toString()).build();
           final CommandProcessingResult resultIdentity = this.commandsSourceWritePlatformService.logCommandSource(commandRequestIdentity);
           
           ////System.out.println("after creating record in identifier ");
          
           File file = new File(PdfApp.path);
           // Now insert the document record for above inserted Identity record
           final DocumentCommand documentCommandIdentity = new DocumentCommand(null, null, "client_identifiers", resultIdentity.resourceId(), sIdentifier, file.getName(),
           file.length(), "application/pdf", null, null,this.dldata.getRequestid());
           InputStream inputStream = new FileInputStream(PdfApp.path);
		final Long documentIdIdentity = this.documentWritePlatformService.createDocument(documentCommandIdentity, inputStream);
 
	   }catch(Exception e)
	   {
		   ////System.out.println(e.getMessage());
		   //to handle the exception
	   }
	   
	   	result.remove("firstname");
	   	result.remove("officeId");
	   	result.remove("dateFormat");
	   	result.remove("locale");
	
	    return result.toString();
	    
	    }
		
		public String validateForVoter(final String requestData,final String karza,final String value,final String url,String timeout) throws IOException, Exception
	    {
	    	//creating a constructor for clientApi resource
	    	ClientsApiResource cp = new ClientsApiResource(context,readPlatformService,toApiJsonSerializer,clientAccountSummaryToApiJsonSerializer,apiRequestParameterHelper,commandsSourceWritePlatformService,
	    												   accountDetailsReadPlatformService,savingsAccountReadPlatformService,bulkImportWorkbookPopulatorService,bulkImportWorkbookService,fromApiJsonHelper,cibilreadPlatformService);
	       
	    	//if the request is blank then we throw an exception
	    	if (StringUtils.isBlank(requestData)) 
	        {
	            throw new InvalidJsonException();
	        }
	    
	   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	   this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, requestData, this.supportedParameters);  
	   
	   //parsing the request and removing clientid and sending the request
	   final JsonElement requestelement = this.fromApiJsonHelper.parse(requestData);
	   final JsonObject re = requestelement.getAsJsonObject();
	   re.remove("clientid");
	   re.remove("officeId");
	   re.remove("firstname");
	   String request = requestelement.toString();
	   ////System.out.println("request sent"+request);
	   //creating a new response body by extracting request and response from karza
	   final JsonElement element1 = this.fromApiJsonHelper.parse(requestData);
	   
	   final JsonObject result = element1.getAsJsonObject();
	   result.remove("clientid");
	   result.remove("consent");
	   result.remove("epic_no");
	   result.remove("dob");
	   result.remove("officeId");
	   String epic_no = null;
	   final JsonElement element = this.fromApiJsonHelper.parse(requestData);
	   Date date = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
	   formatter = new SimpleDateFormat("dd MMMM yyyy");
		String strDate = formatter.format(date);
	  
	 ////System.out.println(request+"---");
	 
	   Long clientid = this.fromApiJsonHelper.extractLongNamed("clientid", element);
	   this.setClientid(clientid);
	   Long officeid = this.fromApiJsonHelper.extractLongNamed("officeId", element);
	  
	   String name = this.fromApiJsonHelper.extractStringNamed("firstname", element);
	   ////System.out.println(clientid+" clientid");
	   String documentkey = this.fromApiJsonHelper.extractStringNamed("epic_no", element);
	   //sending request to karza 
	   String response = ok.post(karza,value,url,request,timeout);
	   ////System.out.println("VoterId response "+response);
	   //obtaining the response parameters from karza
	   String []abc= this.voteriddata.validateForCreate(response);
	 
	   String firstname = this.voteriddata.getName();
	   
	   PdfApp.pdf(abc,"",firstname+" VoterID");
	   ////System.out.println("-----------------"+this.panauthcommand.getStatuscode());
	   if(this.voteriddata.getStatuscode()==101)
	   {
		   epic_no =  this.fromApiJsonHelper.extractStringNamed("epic_no", element);
		  
	   }
	   else
	   {
		   result.addProperty("result", "failed");
		   return result.toString();
	   }
	   result.addProperty("lastverifiedsecondaryid", epic_no);
	   result.addProperty("dateFormat", "dd MMMM yyyy");
	   result.addProperty("locale", "en");
	   result.addProperty("lastverifiedSecondaryidDate", strDate);
		//if client id is not null we update the columns if client id is null then we create a new record 
	   if(clientid!=null)
	   {
		   ////System.out.println(result.toString());
		   cp.update(this.clientid,result.toString());
	   }
	   else
	   {
		   result.addProperty("firstname", name);
		   result.addProperty("officeId", officeid);
		  
		   ////System.out.println(result.toString());
		  final String clientresponse = cp.create(result.toString());
		  final JsonElement clientelement = this.fromApiJsonHelper.parse(clientresponse);
		  final JsonObject client = clientelement.getAsJsonObject();
		  clientid = client.get("clientId").getAsLong();
		  this.setClientid(clientid);
		  ////System.out.println("clientId after creation is"+clientid);
			
	   }
	   result.addProperty("clientid",this.getClientid());
	   result.addProperty("result", "success");
	   try {
		   String sIdentifier = "Voter ID";
           Optional<CodeValueData> codevalue = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier")
               .stream().filter( codeval -> codeval.getName().equals(sIdentifier)).findAny();
           // replace the Filter condition used above respectively based on which verification is done.
           Long mCodeValId = codevalue.get().getId();

           // Construct Json for Creating Identity Entry.. 
           JsonObject newIdentity = new JsonObject();
           newIdentity.addProperty("documentTypeId", mCodeValId); // Identities of Dropdown value Id.. 
           // only one active per DocumentTypeId for a client is allowed. So before inserting new, old should be made inactive and that too same UniqueId/DocumentKey its not allowed.
           newIdentity.addProperty("status", "Active"); 
           newIdentity.addProperty("documentKey", documentkey); // Pan No, Voter ID, DL No.
           newIdentity.addProperty("isdigiverified", true);
           //newIdentity.addProperty("description", "PP112233"); // This is Optional.

           // Below will insert the m_client_identifier record. 
           final CommandWrapper commandRequestIdentity = new CommandWrapperBuilder().createClientIdentifier(clientid)
                   .withJson(newIdentity.toString()).build();
           final CommandProcessingResult resultIdentity = this.commandsSourceWritePlatformService.logCommandSource(commandRequestIdentity);
           
           ////System.out.println("after creating record in identifier ");
          
           File file = new File(PdfApp.path);
           // Now insert the document record for above inserted Identity record
           final DocumentCommand documentCommandIdentity = new DocumentCommand(null, null, "client_identifiers", resultIdentity.resourceId(), sIdentifier, file.getName(),
           file.length(), "application/pdf", null, null,this.voteriddata.getRequestid());
           InputStream inputStream = new FileInputStream(PdfApp.path);
		final Long documentIdIdentity = this.documentWritePlatformService.createDocument(documentCommandIdentity, inputStream);
 
	   }catch(Exception e)
	   {
		   ////System.out.println(e.getMessage());
		   //to handle the exception
	   }
	   
	   	result.remove("firstname");
	   	result.remove("officeId");
	   	result.remove("dateFormat");
	   	result.remove("locale");
	 
	    return result.toString();
	    
	    }
		
		@SuppressWarnings("null")
		public String validateForAdhar(final String requestData,final String karza,final String value,final String url,String timeout) throws IOException, Exception
	    {
	    	//creating a constructor for clientApi resource
	    	ClientsApiResource cp = new ClientsApiResource(context,readPlatformService,toApiJsonSerializer,clientAccountSummaryToApiJsonSerializer,apiRequestParameterHelper,commandsSourceWritePlatformService,
	    												   accountDetailsReadPlatformService,savingsAccountReadPlatformService,bulkImportWorkbookPopulatorService,bulkImportWorkbookService,fromApiJsonHelper,cibilreadPlatformService);
	       
	    	//if the request is blank then we throw an exception
	    	if (StringUtils.isBlank(requestData)) 
	        {
	            throw new InvalidJsonException();
	        }
	    
	   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	   this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, requestData, this.supportedParameters1);  
	   
	   //parsing the request and removing clientid and sending the request
	   final JsonElement requestelement = this.fromApiJsonHelper.parse(requestData);
	   final JsonObject re = requestelement.getAsJsonObject();
	   re.remove("clientid");
	   re.remove("officeId");
	   re.remove("adharnumber");
	   re.remove("firstname");
	   re.addProperty("consent", "Y");
	   String request = requestelement.toString();
	   ////System.out.println("request sent"+request);
	   //creating a new response body by extracting request and response from karza
	   final JsonElement element1 = this.fromApiJsonHelper.parse(requestData);
	   
	   final JsonObject result = element1.getAsJsonObject();
	   result.remove("clientid");
	   result.remove("consent");
	   result.remove("adharnumber");
	   result.remove("otp");
	   result.remove("shareCode");
	   result.remove("accessKey");
	   result.remove("clientData");
	   
	   
	   String adharnumber = null;
	   final JsonElement element = this.fromApiJsonHelper.parse(requestData);
	   Date date = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
	   formatter = new SimpleDateFormat("dd MMMM yyyy");
		String strDate = formatter.format(date);
	  
	 ////System.out.println(request+"---");
	 
	   Long clientid = this.fromApiJsonHelper.extractLongNamed("clientid", element);
	   this.setClientid(clientid);
	   Long officeid = this.fromApiJsonHelper.extractLongNamed("officeId", element);
	   String name = this.fromApiJsonHelper.extractStringNamed("firstname", element);
	   ////System.out.println(clientid+" clientid");
	   String documentkey = this.fromApiJsonHelper.extractStringNamed("adharnumber", element);
	   //sending request to karza 
	   String response = ok.post(karza,value,url,request,timeout);
	   ////System.out.println("Ahar WIth OTP response "+response);
	   //obtaining the response parameters from karza
	   
	   adharnumber =  this.fromApiJsonHelper.extractStringNamed("adharnumber", element);
	   String []abc= this.adhardata.validateForCreate(response,adharnumber);
	 
	   String firstname = this.adhardata.getName();
	   
	
	   if(this.adhardata.getStatus()==101)
	   {
		   result.addProperty("adhar", adharnumber);
		   result.addProperty("lastverifiedadhar", adharnumber);
		   result.addProperty("dateFormat", "dd MMMM yyyy");
		   result.addProperty("locale", "en");
		   result.addProperty("lastverifiedadhardate", strDate);
	   }
	   else
	   {
		   result.addProperty("result", "failure");
		   return result.toString();
	   }
	   
	   PdfApp.pdf(abc,this.adhardata.getImage(),firstname+" Adhar");
	   
	   //creating a new json body for saving the  kyc address
	   final JsonObject arrayofaddress =  new JsonObject();
	
		
		arrayofaddress.addProperty("houseNo", this.adhardata.getHouseNumber());
		arrayofaddress.addProperty("street", this.adhardata.getStreet());
		arrayofaddress.addProperty("addressLine1", this.adhardata.getLocation());
		arrayofaddress.addProperty("townVillage", this.adhardata.getVtcName());
		
		Long talukaid = retrievecodefromMcode("Taluka",this.adhardata.getSubdistrict());
		arrayofaddress.addProperty("talukaId", talukaid);
		
		Long districtid = retrievecodefromMcode("District",this.adhardata.getDistrict());
		
		arrayofaddress.addProperty("TalukaName", this.adhardata.getSubdistrict());
		arrayofaddress.addProperty("districtName",this.adhardata.getDistrict());
		arrayofaddress.addProperty("LandMark",this.adhardata.getLandmark());
		arrayofaddress.addProperty("districtId", districtid);
		
		Long stateProvinceId = retrievecodefromMcode("State",this.adhardata.getState());
		arrayofaddress.addProperty("stateProvinceId", stateProvinceId);
	  
		arrayofaddress.addProperty("isActive", true);
		
		arrayofaddress.addProperty("postalCode", this.adhardata.getPincode());
	 
		boolean addressset = false;
		//if client id is not null we update the columns if client id is null then we create a new record 
	   if(clientid!=null)
	   {
		   ////System.out.println(result.toString());
		   cp.update(this.clientid,result.toString());
		   Collection<AddressData> clientaddress = adressreadservices.retrieveAllClientAddress(this.getClientid());
		   List<AddressData> clientaddressList = new ArrayList<AddressData>(clientaddress);
		   Long kycid = retrievecodefromMcode("ADDRESS_TYPE", "KYC Address");
		   for(int i = 0 ; i < clientaddressList.size() ; i++)
		   {
			   Object obj = clientaddressList.get(i);
			   if(obj instanceof AddressData)
			   {
				   if(((AddressData) obj).getAddressTypeId().equals(kycid))
				   {
					   Long adridofclient = ((AddressData) obj).getAddressId();
					   arrayofaddress.addProperty("addressId", adridofclient);
					   addressset = true;
				   }				   
			   }
		   }
		   
		   if(addressset)
		   {
			   addresswriteplatformservice.updateClientAddress(this.getClientid(), arrayofaddress.toString());
		   }
		   else
		   {
			   Long addrtypeid = retrievecodefromMcode("ADDRESS_TYPE","KYC Address");
			   addresswriteplatformservice.addClientAddress(addrtypeid, this.getClientid(), arrayofaddress.toString());
		   }
	   }
	   else
	   {
		   result.addProperty("firstname", name);
		   result.addProperty("officeId", officeid);
		  
		   ////System.out.println(result.toString());
		  final String clientresponse = cp.create(result.toString());
		  final JsonElement clientelement = this.fromApiJsonHelper.parse(clientresponse);
		  final JsonObject client = clientelement.getAsJsonObject();
		  clientid = client.get("clientId").getAsLong();
		  this.setClientid(clientid);
		  ////System.out.println("clientId after creation is"+clientid);
		  Long addrtypeid = retrievecodefromMcode("ADDRESS_TYPE","KYC Address");
		  ////System.out.println("addrtypeid "+addrtypeid);
		  ////System.out.println("this.getClientid() "+this.getClientid()); 
		  ////System.out.println("arrayofaddress.toString() "+arrayofaddress.toString());
		  addresswriteplatformservice.addClientAddress(addrtypeid, this.getClientid(), arrayofaddress.toString());
		  
			
	   }
	   result.addProperty("name", this.adhardata.getName());
	   result.addProperty("gender", this.adhardata.getGender());
	   result.addProperty("dob", this.adhardata.getDob());
	   result.addProperty("fathername", this.adhardata.getFatherName());
	   result.add("split-address", this.adhardata.getSplitaddress());
	   result.addProperty("TalukaName", this.adhardata.getSubdistrict());
	   result.addProperty("districtName",this.adhardata.getDistrict());
	   result.addProperty("LandMark",this.adhardata.getLandmark());
	   result.addProperty("clientid",this.getClientid());
	   
	   result.addProperty("result", "success");
	   try {
		   String sIdentifier = "Adhar";
           Optional<CodeValueData> codevalue = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier")
               .stream().filter( codeval -> codeval.getName().equals(sIdentifier)).findAny();
           // replace the Filter condition used above respectively based on which verification is done.
           Long mCodeValId = codevalue.get().getId();

           // Construct Json for Creating Identity Entry.. 
           JsonObject newIdentity = new JsonObject();
           newIdentity.addProperty("documentTypeId", mCodeValId); // Identities of Dropdown value Id.. 
           // only one active per DocumentTypeId for a client is allowed. So before inserting new, old should be made inactive and that too same UniqueId/DocumentKey its not allowed.
           newIdentity.addProperty("status", "Active"); 
           newIdentity.addProperty("documentKey", documentkey); // Pan No, Voter ID, DL No.
           newIdentity.addProperty("isdigiverified", true);
           //newIdentity.addProperty("description", "PP112233"); // This is Optional.

           // Below will insert the m_client_identifier record. 
           final CommandWrapper commandRequestIdentity = new CommandWrapperBuilder().createClientIdentifier(clientid)
                   .withJson(newIdentity.toString()).build();
           final CommandProcessingResult resultIdentity = this.commandsSourceWritePlatformService.logCommandSource(commandRequestIdentity);
           
           ////System.out.println("after creating record in identifier ");
          
           File file = new File(PdfApp.path);
           // Now insert the document record for above inserted Identity record
           final DocumentCommand documentCommandIdentity = new DocumentCommand(null, null, "client_identifiers", resultIdentity.resourceId(), sIdentifier, file.getName(),
           file.length(), "application/pdf", null, null,this.adhardata.getRequestid());
           InputStream inputStream = new FileInputStream(PdfApp.path);
		final Long documentIdIdentity = this.documentWritePlatformService.createDocument(documentCommandIdentity, inputStream);
 
	   }catch(Exception e)
	   {
		   ////System.out.println(e.getMessage());
		   //to handle the exception
	   }
	   
	   	result.remove("firstname");
	   	result.remove("officeId");
	   	result.remove("dateFormat");
	   	result.remove("locale");
	 
	    return result.toString();
	    
	    }
		
		public String validatemobile(final String requestData,final String karza,final String value,final String url,String timeout) throws IOException, Exception
	    {
	    	//creating a constructor for clientApi resource
	    	ClientsApiResource cp = new ClientsApiResource(context,readPlatformService,toApiJsonSerializer,clientAccountSummaryToApiJsonSerializer,apiRequestParameterHelper,commandsSourceWritePlatformService,
	    												   accountDetailsReadPlatformService,savingsAccountReadPlatformService,bulkImportWorkbookPopulatorService,bulkImportWorkbookService,fromApiJsonHelper,cibilreadPlatformService);
	       
	    	//if the request is blank then we throw an exception
	    	if (StringUtils.isBlank(requestData)) 
	        {
	            throw new InvalidJsonException();
	        }
	    
	   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	   this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, requestData, this.supportedParameters);  
	   
	   //parsing the request and removing clientid and sending the request
	   final JsonElement requestelement = this.fromApiJsonHelper.parse(requestData);
	   final JsonObject re = requestelement.getAsJsonObject();
	   re.remove("clientid");
	   re.remove("officeId");
	   re.remove("firstname");
	   re.remove("mobile");
	  
	   String request = requestelement.toString();
	   ////System.out.println("request sent"+request);
	   //creating a new response body by extracting request and response from karza
	   final JsonElement element1 = this.fromApiJsonHelper.parse(requestData);
	   
	   final JsonObject result = element1.getAsJsonObject();
	   String firstname = result.get("firstname").getAsString();
	   result.remove("clientid");
	   result.remove("consent");
	   result.remove("mobile");
	   result.remove("request_id");
	   result.remove("otp");
	   result.remove("firstname");
	   String mobile = null;
	   final JsonElement element = this.fromApiJsonHelper.parse(requestData);
	   Date date = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
	   formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
		String strDate = formatter.format(date);
	  
//	   	Timestamp ts=new Timestamp(System.currentTimeMillis());
//	   	Date date=ts; 
//	   String strDate = date.toString();
	  
	 
	 
	   Long clientid = this.fromApiJsonHelper.extractLongNamed("clientid", element);
	   Long officeid = this.fromApiJsonHelper.extractLongNamed("officeId", element);
	   String name = this.fromApiJsonHelper.extractStringNamed("firstname", element);
	   this.setClientid(clientid);
	   ////System.out.println(clientid+" clientid");
	
	   //sending request to karza 
	   String response = ok.post(karza,value,url,request,timeout);
	   
	   ////System.out.println("Mobile OTP response "+response);
	   //obtaining the response parameters from karza
	   
	   String []abc= this.mobiledata.create(response);
	   if(this.mobiledata.getOtpvalidated().equals("true"))
	   {
		   mobile =  this.fromApiJsonHelper.extractStringNamed("mobile", element);
		   
			
		   ////System.out.println("the data before pdf generation ");
		   for(String b : abc)
		   {
			   ////System.out.println(b);
		   }
		   PdfApp.pdf(abc, "",firstname+" mobile");
		  
	   }
	   else
	   {
		   result.addProperty("result", "failed");
		   return result.toString();
	   }
	   result.addProperty("lastverifiedmobile", mobile);
	   result.addProperty("mobileNo", mobile);
	   result.addProperty("dateFormat", "dd MMMM yyyy HH:mm:ss");
	   result.addProperty("locale", "en");
	   result.addProperty("lastverifiedmobiledate", strDate);
		//if client id is not null we update the columns if client id is null then we create a new record 
	   if(clientid!=null)
	   {
		   ////System.out.println(result.toString());
		   cp.update(this.clientid,result.toString());
	   }
	   else
	   {
		   result.addProperty("firstname", name);
		   result.addProperty("officeId", officeid);
		  
		   ////System.out.println(result.toString());
		  final String clientresponse = cp.create(result.toString());
		  final JsonElement clientelement = this.fromApiJsonHelper.parse(clientresponse);
		  final JsonObject client = clientelement.getAsJsonObject();
		  clientid = client.get("clientId").getAsLong();
		  this.setClientid(clientid);
		  ////System.out.println("clientId after creation is"+clientid);
	   }
	   
	   try {
	
          String sIdentifier = "mobile";
           File file = new File(PdfApp.path);
           // Now insert the document record for above inserted Identity record
           final DocumentCommand documentCommandIdentity = new DocumentCommand(null, null,"clients", this.getClientid(), sIdentifier, file.getName(),
           file.length(), "application/pdf", null, null,this.mobiledata.getRequestid());
           InputStream inputStream = new FileInputStream(PdfApp.path);
		final Long documentIdIdentity = this.documentWritePlatformService.createDocument(documentCommandIdentity, inputStream);
 
	   }catch(Exception e)
	   {
		   ////System.out.println(e.getMessage());
		   //to handle the exception
	   }
	   
	   	result.remove("firstname");
	   	result.remove("officeId");
	   	result.remove("dateFormat");
	   	result.remove("locale");
	   	result.addProperty("clientid",this.getClientid());
	   	result.addProperty("result", "success");
	    return result.toString();
	    
	    }
		
		public String validateForPassport(final String requestData,final String karza,final String value,final String url,String timeout) throws IOException, Exception
	    {
	    	//creating a constructor for clientApi resource
	    	ClientsApiResource cp = new ClientsApiResource(context,readPlatformService,toApiJsonSerializer,clientAccountSummaryToApiJsonSerializer,apiRequestParameterHelper,commandsSourceWritePlatformService,
	    												   accountDetailsReadPlatformService,savingsAccountReadPlatformService,bulkImportWorkbookPopulatorService,bulkImportWorkbookService,fromApiJsonHelper,cibilreadPlatformService);
	       
	    	//if the request is blank then we throw an exception
	    	if (StringUtils.isBlank(requestData)) 
	        {
	            throw new InvalidJsonException();
	        }
	    
	   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	   this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, requestData, this.supportedParameters);  
	   
	   //parsing the request and removing clientid and sending the request
	   final JsonElement requestelement = this.fromApiJsonHelper.parse(requestData);
	   final JsonObject re = requestelement.getAsJsonObject();
	   re.remove("clientid");
	   re.remove("officeId");
	   re.remove("firstname");
	   String request = requestelement.toString();
	   ////System.out.println("request sent"+request);
	   //creating a new response body by extracting request and response from karza
	   final JsonElement element1 = this.fromApiJsonHelper.parse(requestData);
	   
	   final JsonObject result = element1.getAsJsonObject();
	   result.remove("clientid");
	   result.remove("consent");
	   result.remove("passportNo");
	   result.remove("fileNo");
	   result.remove("officeId");
	   result.remove("dob");
	   result.remove("fileNo");
	   result.remove("name");
	   result.remove("passportNo");
	   result.remove("doi");
	   String passportNo = null;
	   final JsonElement element = this.fromApiJsonHelper.parse(requestData);
	   Date date = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
	   formatter = new SimpleDateFormat("dd MMMM yyyy");
		String strDate = formatter.format(date);
	  
	 ////System.out.println(request+"---");
	 
	   Long clientid = this.fromApiJsonHelper.extractLongNamed("clientid", element);
	   this.setClientid(clientid);
	   Long officeid = this.fromApiJsonHelper.extractLongNamed("officeId", element);
	   this.setClientid(clientid);
	   String name = this.fromApiJsonHelper.extractStringNamed("name", element);
	   ////System.out.println(clientid+" clientid");
	   String documentkey = this.fromApiJsonHelper.extractStringNamed("passportNo", element);
	   //sending request to karza 
	   String response = ok.post(karza,value,url,request,timeout);
	   ////System.out.println("Passport response "+response);
	   //obtaining the response parameters from karza
	   String []abc= this.passport.validate(response);
	 
	   String firstname = this.passport.getName();
	   
	   PdfApp.pdf(abc,"",firstname+" Passport");
	  
	   if(this.passport.getStatusCode()==101)
	   {
		   passportNo =  this.fromApiJsonHelper.extractStringNamed("passportNo", element);
		  
	   }
	   else
	   {
		   result.addProperty("result", "failure");
		   
		   return result.toString();
	   }
	   result.addProperty("lastverifiedsecondaryid", passportNo);
	   result.addProperty("dateFormat", "dd MMMM yyyy");
	   result.addProperty("locale", "en");
	   result.addProperty("lastverifiedSecondaryidDate", strDate);
		//if client id is not null we update the columns if client id is null then we create a new record 
	   if(clientid!=null)
	   {
		   ////System.out.println(result.toString());
		   cp.update(this.clientid,result.toString());
	   }
	   else
	   {
		   result.addProperty("firstname", name);
		   result.addProperty("officeId", officeid);
		  
		   ////System.out.println(result.toString());
		  final String clientresponse = cp.create(result.toString());
		  final JsonElement clientelement = this.fromApiJsonHelper.parse(clientresponse);
		  final JsonObject client = clientelement.getAsJsonObject();
		  clientid = client.get("clientId").getAsLong();
		  this.setClientid(clientid);
		  ////System.out.println("clientId after creation is"+clientid);
	   }
	 	result.addProperty("clientid",this.getClientid());
	 	result.addProperty("result", "success");
	   try {
		   String sIdentifier = "Passport";
           Optional<CodeValueData> codevalue = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier")
               .stream().filter( codeval -> codeval.getName().equals(sIdentifier)).findAny();
           // replace the Filter condition used above respectively based on which verification is done.
           Long mCodeValId = codevalue.get().getId();

           // Construct Json for Creating Identity Entry.. 
           JsonObject newIdentity = new JsonObject();
           newIdentity.addProperty("documentTypeId", mCodeValId); // Identities of Dropdown value Id.. 
           // only one active per DocumentTypeId for a client is allowed. So before inserting new, old should be made inactive and that too same UniqueId/DocumentKey its not allowed.
           newIdentity.addProperty("status", "Active"); 
           newIdentity.addProperty("documentKey", documentkey); // Pan No, Voter ID, DL No.
           newIdentity.addProperty("isdigiverified", true);
           //newIdentity.addProperty("description", "PP112233"); // This is Optional.

           // Below will insert the m_client_identifier record. 
           final CommandWrapper commandRequestIdentity = new CommandWrapperBuilder().createClientIdentifier(clientid)
                   .withJson(newIdentity.toString()).build();
           final CommandProcessingResult resultIdentity = this.commandsSourceWritePlatformService.logCommandSource(commandRequestIdentity);
           
           ////System.out.println("after creating record in identifier ");
          
           File file = new File(PdfApp.path);
           // Now insert the document record for above inserted Identity record
           final DocumentCommand documentCommandIdentity = new DocumentCommand(null, null, "client_identifiers", resultIdentity.resourceId(), sIdentifier, file.getName(),
           file.length(), "application/pdf", null, null,null);
           InputStream inputStream = new FileInputStream(PdfApp.path);
		final Long documentIdIdentity = this.documentWritePlatformService.createDocument(documentCommandIdentity, inputStream);
 
	   }catch(Exception e)
	   {
		   ////System.out.println(e.getMessage());
		   //to handle the exception
	   }
	   
	   	result.remove("firstname");
	   	result.remove("officeId");
	   	result.remove("dateFormat");
	   	result.remove("locale");
	
	    return result.toString();
	    
	    }
		
		public String validateForAdharWithoutmobile(final String requestData,final String karza,final String value,final String url,String timeout) throws IOException, Exception
	    {
	    	//creating a constructor for clientApi resource
	    	ClientsApiResource cp = new ClientsApiResource(context,readPlatformService,toApiJsonSerializer,clientAccountSummaryToApiJsonSerializer,apiRequestParameterHelper,commandsSourceWritePlatformService,
	    												   accountDetailsReadPlatformService,savingsAccountReadPlatformService,bulkImportWorkbookPopulatorService,bulkImportWorkbookService,fromApiJsonHelper,cibilreadPlatformService);
	       
	    	//if the request is blank then we throw an exception
	    	if (StringUtils.isBlank(requestData)) 
	        {
	            throw new InvalidJsonException();
	        }
	    
	   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	   this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, requestData, this.supportedParameters);  
	   
	   //parsing the request and removing clientid and sending the request
	   final JsonElement requestelement = this.fromApiJsonHelper.parse(requestData);
	   final JsonObject re = requestelement.getAsJsonObject();
	   re.remove("clientid");
	   re.remove("officeId");
	
	   String request = requestelement.toString();
	   ////System.out.println("request sent"+request);
	   //creating a new response body by extracting request and response from karza
	   final JsonElement element1 = this.fromApiJsonHelper.parse(requestData);
	   
	   final JsonObject result = element1.getAsJsonObject();
	   result.remove("clientid");
	   result.remove("consent");
	   result.remove("aadhaarNo");
	  
	   result.remove("officeId");
	   result.remove("clientData");
	   result.remove("name");
	   result.remove("accessKey");
	   String aadhaarNo = null;
	   final JsonElement element = this.fromApiJsonHelper.parse(requestData);
	   Date date = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
	   formatter = new SimpleDateFormat("dd MMMM yyyy");
		String strDate = formatter.format(date);
	  
	 ////System.out.println(request+"---");
	 
	   Long clientid = this.fromApiJsonHelper.extractLongNamed("clientid", element);
	   this.setClientid(clientid);
	   Long officeid = this.fromApiJsonHelper.extractLongNamed("officeId", element);
	 
	   String name = this.fromApiJsonHelper.extractStringNamed("name", element);
	   ////System.out.println(clientid+" clientid");
	   String documentkey = this.fromApiJsonHelper.extractStringNamed("aadhaarNo", element);
	   //sending request to karza 
	   String response = ok.post(karza,value,url,request,timeout);
	  
	   ////System.out.println("adharwithoutmobile response"+response);
	   //obtaining the response parameters from karza
	   String []abc= this.adharwithoutmobile.validate(response);
	 
	 
	
	   
	   PdfApp.pdf(abc,"",name+" Adhar");
	  
	   if(this.adharwithoutmobile.getStatusCode()==101)
	   {
		   aadhaarNo =  this.fromApiJsonHelper.extractStringNamed("aadhaarNo", element);
		  
	   }
	   else
	   {
		   result.addProperty("result", "failure");
		   
		   return result.toString();
	   }
	   result.addProperty("lastverifiedadhar", aadhaarNo);
	   result.addProperty("dateFormat", "dd MMMM yyyy");
	   result.addProperty("locale", "en");
	   result.addProperty("lastverifiedadhardate", strDate);
		//if client id is not null we update the columns if client id is null then we create a new record 
	   if(clientid!=null)
	   {
		   ////System.out.println(result.toString());
		   cp.update(this.clientid,result.toString());
	   }
	   else
	   {
		   result.addProperty("firstname", name);
		   result.addProperty("officeId", officeid);
		  
		   ////System.out.println(result.toString());
		  final String clientresponse = cp.create(result.toString());
		  final JsonElement clientelement = this.fromApiJsonHelper.parse(clientresponse);
		  final JsonObject client = clientelement.getAsJsonObject();
		  clientid = client.get("clientId").getAsLong();
		  this.setClientid(clientid);
		  ////System.out.println("clientId after creation is"+clientid);
	   }
	 	result.addProperty("clientid",this.getClientid());
	 	result.addProperty("result", "success");
	   try {
		   String sIdentifier = "Adhar";
           Optional<CodeValueData> codevalue = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier")
               .stream().filter( codeval -> codeval.getName().equals(sIdentifier)).findAny();
           // replace the Filter condition used above respectively based on which verification is done.
           Long mCodeValId = codevalue.get().getId();

           // Construct Json for Creating Identity Entry.. 
           JsonObject newIdentity = new JsonObject();
           newIdentity.addProperty("documentTypeId", mCodeValId); // Identities of Dropdown value Id.. 
           // only one active per DocumentTypeId for a client is allowed. So before inserting new, old should be made inactive and that too same UniqueId/DocumentKey its not allowed.
           newIdentity.addProperty("status", "Active"); 
           newIdentity.addProperty("documentKey", documentkey); // Pan No, Voter ID, DL No.
           newIdentity.addProperty("isdigiverified", true);
           //newIdentity.addProperty("description", "PP112233"); // This is Optional.

           // Below will insert the m_client_identifier record. 
           final CommandWrapper commandRequestIdentity = new CommandWrapperBuilder().createClientIdentifier(clientid)
                   .withJson(newIdentity.toString()).build();
           final CommandProcessingResult resultIdentity = this.commandsSourceWritePlatformService.logCommandSource(commandRequestIdentity);
           
           ////System.out.println("after creating record in identifier ");
          
           File file = new File(PdfApp.path);
           // Now insert the document record for above inserted Identity record
           final DocumentCommand documentCommandIdentity = new DocumentCommand(null, null, "client_identifiers", resultIdentity.resourceId(), sIdentifier, file.getName(),
           file.length(), "application/pdf", null, null,null);
           InputStream inputStream = new FileInputStream(PdfApp.path);
		final Long documentIdIdentity = this.documentWritePlatformService.createDocument(documentCommandIdentity, inputStream);
 
	   }catch(Exception e)
	   {
		   ////System.out.println(e.getMessage());
		   //to handle the exception
	   }
	   
	   	result.remove("firstname");
	   	result.remove("officeId");
	   	result.remove("dateFormat");
	   	result.remove("locale");
	
	    return result.toString();
	    
	    }
	    public Long getClientid() {
			return clientid;
		}
	    public  void setClientid(Long clientid) {
			this.clientid = clientid;
		}
	    
	   public Long retrievecodefromMcode(String mcodename,String mcodevalue)
	    {
		   Long mcodeid = null;
		   Collection<CodeValueData> addr = codeValueReadPlatformService.retrieveCodeValuesByCode(mcodename);
		   
			List<CodeValueData> addrList = new ArrayList<CodeValueData>(addr);
			
			for(int i = 0 ; i < addrList.size();i++)
			{
				Object obj = addrList.get(i);
				if(obj instanceof CodeValueData)
				{
					if(((CodeValueData) obj).getName().equals(mcodevalue))
					{
						 mcodeid = ((CodeValueData) obj).getId();
						
						return mcodeid;
					
					}
				}
			}
			
			return mcodeid;
	    }
	    
	  
	 
}
