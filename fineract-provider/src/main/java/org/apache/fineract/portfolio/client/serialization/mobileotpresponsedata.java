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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

@Component
public class mobileotpresponsedata
{
	private final FromJsonHelper fromApiJsonHelper;
	private final MobileVerifiicationForwarding mb;
	
	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("result","request_id","status-code","status","error"));
	String otpvalidated;
	Long statuscode;
	String provider;
	String requestid;
	public String getRequestid() {
		return requestid;
	}



	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}



	public Long getStatuscode() {
		return statuscode;
	}



	public void setStatuscode(Long statuscode) {
		this.statuscode = statuscode;
	}



	public String getOtpvalidated() {
		return otpvalidated;
	}

	String name;
	

	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public void setOtpvalidated(String otpvalidated) {
		this.otpvalidated = otpvalidated;
	}



	@Autowired
	public mobileotpresponsedata(FromJsonHelper fromApiJsonHelper,final MobileVerifiicationForwarding mb) {
		super();
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.mb = mb;
	}



	public String[] create(final String json)
	{
		 if (StringUtils.isBlank(json)) 
	        {
	            throw new InvalidJsonException();
	        }
		 final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
	        final JsonElement element = this.fromApiJsonHelper.parse(json);
	    
	        if(this.fromApiJsonHelper.extractLongNamed("status-code", element)!=null)
	        {
	        	 statuscode = this.fromApiJsonHelper.extractLongNamed("status-code", element);
	        }
	        else
	        {
	        	this.setStatuscode(90l);
	        	return null;
	        }
	       
	        if(statuscode!=null && statuscode==101)
	        {
	        	  if(this.fromApiJsonHelper.extractStringNamed("request_id", element)!=null)
	  	        {
	  	        	 requestid = this.fromApiJsonHelper.extractStringNamed("request_id", element);
	  	        	 this.setRequestid(requestid);
	  	        }
	        	 this.setStatuscode(statuscode);
	        	 if(this.fromApiJsonHelper.extractJsonObjectNamed("result", element)!=null)
	        	 {
	        		 if(!this.fromApiJsonHelper.extractJsonObjectNamed("result", element).isJsonNull())
	        		 {
	        			 final JsonObject result = this.fromApiJsonHelper.extractJsonObjectNamed("result", element);
	     		        String re = result.toString();
	     		        @SuppressWarnings("deprecation")
	     				com.google.gson.JsonObject jsonObject = new JsonParser().parse(re).getAsJsonObject();
	     		        
	     		        if(jsonObject.get("sim_details")!=null)
	     		        {
	     		        	if(!jsonObject.get("sim_details").isJsonNull())
	     		        	{
	     		        		com.google.gson.JsonElement elm = jsonObject.get("sim_details");
	     		        		jsonObject = elm.getAsJsonObject();
	     		        		
	     		        		if(jsonObject.get("provider")!=null)
	     		        		{
	     		        			if(!jsonObject.get("provider").isJsonNull())
	     		        			{
	     		        				provider = jsonObject.get("provider").getAsString();
	     		        			}
	     		        			else
	     		        			{
	     		        				provider = "Data Unavailable";
	     		        			}
	     		        		}
	     		        		else
     		        			{
     		        				provider = "Data Unavailable";
     		        			}
	     		        		
	     		        		if(jsonObject.get("otp_validated")!=null)
	     		        		{
	     		        			if(!jsonObject.get("otp_validated").isJsonNull())
	     		        			{
	     		        				otpvalidated = jsonObject.get("otp_validated").getAsString();
	     		        				this.setOtpvalidated(otpvalidated);
	     		        			}
	     		        			else
	     		        			{
	     		        				otpvalidated = "Data Unavailable";
	     		        				this.setOtpvalidated(otpvalidated);
	     		        			}
	     		        		}
	     		        		else
     		        			{
	     		        			this.setOtpvalidated(otpvalidated);
	     		        			otpvalidated = "Data Unavailable";
     		        			}
	     		        		
	     		        	}
	     		        }

	     				String []response = new String[] {"MobileNo",mb.getMobileno(),"provider",provider,"otpvalidated",otpvalidated};
	     				
	     				return response;
	        		 }
	        		 else
	        		 {
	        			this.setOtpvalidated("false");
	     	        	return null;
	        		 }
	        	 }
	        	 else
	        	 {
	        		 this.setOtpvalidated("false");
	 	        	return null;
	        	 }
		       
	        }
	        else
	        {
	        	this.setOtpvalidated("false");
	        	return null;
	        }
	}
}
