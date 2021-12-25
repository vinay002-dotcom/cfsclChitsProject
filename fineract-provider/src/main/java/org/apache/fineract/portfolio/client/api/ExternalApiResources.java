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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.serialization.AdharDataCommand;
import org.apache.fineract.portfolio.client.serialization.AdharForwarding;
import org.apache.fineract.portfolio.client.serialization.MobileVerifiicationForwarding;
import org.apache.fineract.portfolio.client.service.ExternalApiReadService;
import org.apache.fineract.portfolio.client.service.ExternalApiServices;
import org.apache.fineract.portfolio.client.service.ExternalApiWritePlatformServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Component
@Scope("singleton")
@PropertySource ("classpath:ChethanaChits.properties")
@Path("/ExternalApi")
public class ExternalApiResources 
{
	
	
	 private final ExternalApiWritePlatformServices requestdata;
	 private final AdharDataCommand ap;
	 private final AdharForwarding af;
	 private final MobileVerifiicationForwarding mb;
	 private final FromJsonHelper fromApiJsonHelper;
	 @Autowired
	public ExternalApiResources(final ExternalApiWritePlatformServices requestdata,final AdharDataCommand ap,final AdharForwarding af,final MobileVerifiicationForwarding mb,final FromJsonHelper fromApiJsonHelper)
	 {
		this.requestdata =requestdata;     
		this.ap = ap;
		this.af = af;
		this.mb = mb;
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	@Autowired
    Environment env;
	    
		
	ExternalApiReadService ok = new ExternalApiReadService();
		@POST
		@Path("/cibilscore")
		@Consumes({ MediaType.APPLICATION_XML })
		@Produces({ MediaType.APPLICATION_XML })
		@Operation(summary = "Here cibilScores can be retrieved")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String cibilscore(@Parameter(hidden = true) final String apiRequestBodyAsJson) throws Exception
		{
			ExternalApiServices a = new ExternalApiServices();
			return a.getcibilScore();
			//return b;
		}
		
		@POST
		@Path("/Karza/Panauthentication")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "Pan Authentication")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String panauthentication(@Parameter(hidden = true) final String apiRequestBodyAsJson) throws IOException,Exception
		{
			
			String timeout = env.getProperty("timeout");
			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String panauth = env.getProperty("panauth");
			String request = requestdata.validateforpanAuth(apiRequestBodyAsJson,karzakey,karzavalue,panauth,timeout);
			
			return request;
			
			 
		}
		
		@POST
		@Path("/Karza/Panstatus")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "checking of pan status")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String panstatus(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{

			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String panstatus = env.getProperty("panstatus");
		String timeout = env.getProperty("timeout");
			String response = ok.post(karzakey,karzavalue,panstatus,apiRequestBodyAsJson,timeout);
			////System.out.println(response);
			//String []abc= this.panStatusCommand.validateForCreate(response);
			String image = "";
			//PdfApp.pdf(abc,image);
			return response;
		}
		
		@POST
		@Path("/Karza/DL")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "checking of pan status")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String Dl(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{
			String timeout = env.getProperty("timeout");
		    String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String dl = env.getProperty("dl");
			////System.out.println(apiRequestBodyAsJson);
			String request = requestdata.validateForDl(apiRequestBodyAsJson,karzakey,karzavalue,dl,timeout);
			return request;
		}
		
		@POST
		@Path("/Karza/voterid")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "voter id verification")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String voterid(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{
			String timeout = env.getProperty("timeout");
			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String voterid = env.getProperty("voterid");
			String request = requestdata.validateForVoter(apiRequestBodyAsJson, karzakey, karzavalue, voterid, timeout);
			return request;
		}
		
		@POST
		@Path("/Karza/mobileotp")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "sending mobile otp")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String otp(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{
			String timeout = env.getProperty("timeout");
			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String mobileotp = env.getProperty("mobileotp");
			//String response = ok.post(karzakey,karzavalue,mobileotp,apiRequestBodyAsJson,timeout);
			
			String response = mb.sendrequest(karzakey, karzavalue, mobileotp, apiRequestBodyAsJson, timeout);
			return response;
		}
		
		@POST
		@Path("/Karza/mobilestatus")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "mobile status")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String mobilestatus(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{
			String timeout = env.getProperty("timeout");
			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String mobilestatus = env.getProperty("mobilestatus");
			String body = mb.getbody(apiRequestBodyAsJson);
			String reponse = requestdata.validatemobile(body, karzakey, karzavalue, mobilestatus, timeout);
			//String response = ok.post(karzakey,karzavalue,mobilestatus,body,timeout);
			return reponse;
		}
		
//		@POST
//		@Path("/Karza/mobiledetails")
//		@Consumes({MediaType.APPLICATION_JSON})
//		@Produces({MediaType.APPLICATION_JSON})
//		@Operation(summary= "mobile details")
//		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
//		@ApiResponses({
//		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
//		public String mobiledetails(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
//		{
//			String timeout = env.getProperty("timeout");
//			String karzakey = env.getProperty("karza-key");
//			String karzavalue = env.getProperty("karza-value");
//			String mobiledetails = env.getProperty("mobiledetails");
//			String response = ok.post(karzakey,karzavalue,mobiledetails,apiRequestBodyAsJson,timeout);
//			return response;
//		}
		
		@POST
		@Path("/Karza/adharconsent")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "mobile details")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String adharconsent(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{
			String timeout = env.getProperty("timeout");
			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String adharConsent = env.getProperty("adharConsent");
		//	String response = ok.post(karzakey,karzavalue,adharConsent,apiRequestBodyAsJson,timeout);
			String body = af.sendconsent(karzakey, karzavalue, adharConsent, apiRequestBodyAsJson, timeout,"adharconsent");
			////System.out.println(body+"**");
			String response  = this.adharotp(body);
			 final JsonElement requestelement = this.fromApiJsonHelper.parse(response);
			   final JsonObject re = requestelement.getAsJsonObject();
			   
			  Long status =  this.fromApiJsonHelper.extractLongNamed("statusCode", requestelement);
			  
			  if(status !=null && status == 101)
			  {
				  re.addProperty("karzaresult","success");
					 return re.toString();
				 
			
			  }
			  else
			  {
				  re.addProperty("karzaresult","failure");
					 return re.toString();
			  }
			
		}
		
		@POST
		@Path("/Karza/adharotp")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "mobile details")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String adharotp(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{
			String timeout = env.getProperty("timeout");
			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String adharotp = env.getProperty("adharotp");
			String response = ok.post(karzakey,karzavalue,adharotp,apiRequestBodyAsJson,timeout);
			return response;
		}
		
		
		@POST
		@Path("/Karza/adhardetails")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "mobile details")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String adhardetails(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{
			String timeout = env.getProperty("timeout");
			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String adhardetails = env.getProperty("adhardetails");
			String respbody = af.createbodyfordetails(apiRequestBodyAsJson);
			String request = requestdata.validateForAdhar(respbody, karzakey, karzavalue, adhardetails, timeout);
			return request;
		}
		
		

		@POST
		@Path("/Karza/adharwithoutotp")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "mobile details")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String adharwithoutotp(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{
			String timeout = env.getProperty("timeout");
			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String adharwithoutotp = env.getProperty("adharwithoutotp");
			String adharConsent = env.getProperty("adharConsent");
			String body = af.sendconsent(karzakey, karzavalue, adharConsent, apiRequestBodyAsJson, timeout,"adharwithoutotp");
			
			 String request = requestdata.validateForAdharWithoutmobile(body, karzakey, karzavalue, adharwithoutotp, timeout);
					return request;
			
			
		}

		// This is just a testing method which returns the json that it receives. helps in testing without calling actual api.
		@POST	
		@Path("/Karza/Test")	
		@Consumes({MediaType.APPLICATION_JSON})	
		@Produces({MediaType.APPLICATION_JSON})	
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))	
		@ApiResponses({	
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })	
		public String Test(@Parameter(hidden = true) final String apiRequestBodyAsJson) throws IOException,Exception	
		{	
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch(Exception e){
				////System.out.println(e.getMessage());
			}
			return apiRequestBodyAsJson;
			 
		}	

		
		@POST
		@Path("/Karza/passportverification")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		@Operation(summary= "mobile details")
		@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditRequest.class)))
		@ApiResponses({
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResourcesSwagger.PostCreditResponse.class))) })
		public String passportverification(@Parameter(hidden = true) final String apiRequestBodyAsJson)throws IOException,Exception
		{
			String timeout = env.getProperty("timeout");
			String karzakey = env.getProperty("karza-key");
			String karzavalue = env.getProperty("karza-value");
			String passport = env.getProperty("passport");
			String request = requestdata.validateForPassport(apiRequestBodyAsJson, karzakey, karzavalue, passport, timeout);
			return request;
		}
		
		
}
