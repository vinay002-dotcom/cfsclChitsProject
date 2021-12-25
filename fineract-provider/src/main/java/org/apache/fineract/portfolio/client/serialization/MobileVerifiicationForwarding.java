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

import java.io.IOException;
import java.lang.reflect.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.service.ExternalApiReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class MobileVerifiicationForwarding 
{
	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("mobile","officeId","clientid","result","request_id","status-code","otp","firstname","status","error"));
	
	ExternalApiReadService ok = new ExternalApiReadService();
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public MobileVerifiicationForwarding(FromJsonHelper fromApiJsonHelper) {
		super();
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	
	String requestid;
	String mobileno;
	Long statuscode;
	
	

	public Long getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(Long statuscode) {
		this.statuscode = statuscode;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getrequestid() {
		return requestid;
	}

	public void setrequestid(String requestid) {
		this.requestid = requestid;
	}
	
	public String sendrequest(String karzakey,String karzavalue,String mobileotp,String requestData,String timeout) throws IOException, Exception
	{
		if (StringUtils.isBlank(requestData)) 
        {
            throw new InvalidJsonException();
        }
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, requestData, this.supportedParameters);
		
		  final JsonElement requestelement = this.fromApiJsonHelper.parse(requestData);
		   final JsonObject re = requestelement.getAsJsonObject();
		   mobileno = re.get("mobile").getAsString();
		   this.setMobileno(mobileno);
		 
		   re.addProperty("consent","Y");
		   String request = re.toString();
		   String response = ok.post(karzakey,karzavalue,mobileotp,request,timeout);
		   final JsonElement element = this.fromApiJsonHelper.parse(response);
		   final JsonObject resp = element.getAsJsonObject();
		   this.getrequestfromjson(response);
		   
		   if(this.getStatuscode()!=101)
		   {
			   resp.addProperty("result", "failed");
			   return resp.toString();
		   }
		   else
		   {
		   resp.addProperty("result", "success");;
		 
		   return resp.toString();
		   }
	}
	
	public void getrequestfromjson(String json)
	{
		if (StringUtils.isBlank(json)) 
        {
            throw new InvalidJsonException();
        }
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	      this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
	      final JsonElement element = this.fromApiJsonHelper.parse(json);
	      final JsonObject re = element.getAsJsonObject();
	      
	      if(this.fromApiJsonHelper.extractLongNamed("status-code", element)!=null)
	      {
		      statuscode = this.fromApiJsonHelper.extractLongNamed("status-code", element);
			   
		      this.setStatuscode(statuscode);
		      requestid = re.get("request_id").getAsString();
		     
		      this.setrequestid(requestid);
	      }
	      else
	      {
	    	  this.setStatuscode(90l);
	      }
	}
	
	public String getbody(String json)
	{
		
		if (StringUtils.isBlank(json)) 
        {
            throw new InvalidJsonException();
        }
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	      this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
	      final JsonElement element = this.fromApiJsonHelper.parse(json);
	      final JsonObject re = element.getAsJsonObject();
	      
	     re.addProperty("mobile", this.getMobileno());
		return re.toString();
	}
	
	
}
