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
import java.net.InetAddress;
import java.sql.Timestamp;
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
public class AdharForwarding 
{
	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("clientid","officeId","firstname","ipAddress","userAgent","name","clientData","aadhaarNo","adharnumber","otp","officeId","clientid","shareCode","accessKey","error","status","requestId","statusMessage"));
	private final Set<String> supportedresponse = new HashSet<>(Arrays.asList("requestId","result","statusCode","clientData","accessKey","error","status","statusMessage","adharnumber"));
	
	ExternalApiReadService ok = new ExternalApiReadService();
	private final FromJsonHelper fromApiJsonHelper;
	

	@Autowired
	public AdharForwarding(FromJsonHelper fromApiJsonHelper) {
		super();
		this.fromApiJsonHelper = fromApiJsonHelper;
	
	}
	String name;
	Long clientid;
	Long officeId;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getClientid() {
		return clientid;
	}

	public void setClientid(Long clientid) {
		this.clientid = clientid;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}
	String accessKey;
	String adhar;
	 JsonObject respo;
	 Boolean ismobileregistered;
	
	public Boolean getIsmobileregistered() {
		return ismobileregistered;
	}

	public void setIsmobileregistered(Boolean ismobileregistered) {
		this.ismobileregistered = ismobileregistered;
	}

	public JsonObject getRespo() {
		return respo;
	}

	public void setRespo(JsonObject respo) {
		this.respo = respo;
	}

	public String getAdhar() {
		return adhar;
	}

	public void setAdhar(String adhar) {
		this.adhar = adhar;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String sendconsent(String karzakey,String karzavalue,String adharConsent,String requestData,String timeout,String callfrom) throws IOException, Exception
	{
		
		if (StringUtils.isBlank(requestData)) 
        {
            throw new InvalidJsonException();
        }
		   
		   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		   this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, requestData, this.supportedParameters);  
		   
		   final JsonElement requestelement = this.fromApiJsonHelper.parse(requestData);
		   final JsonObject re = requestelement.getAsJsonObject();
		  
		  if(re.has("officeId"))
		  {
		   officeId = re.get("officeId").getAsLong();
		   this.setOfficeId(officeId);
		  }
		  else if(re.has("clientid"))
		   {
			   clientid = re.get("clientid").getAsLong();
			   this.setClientid(clientid);
		   }
	
		   adhar = re.get("aadhaarNo").getAsString();
		   this.setAdhar(adhar);
		   JsonObject respo = re.get("clientData").getAsJsonObject();
		   this.setRespo(respo);
		   re.remove("aadhaarNo");
		   re.remove("firstname");
		   re.remove("clientid");
		   re.remove("officeId");
		   re.remove("ismobileregistered");
		   re.addProperty("consent","Y");
		   re.addProperty("consentText", "I am ok");
		   re.addProperty("consentTime", this.getconsentTime());
		   String myIP=InetAddress.getLocalHost().getHostAddress();
		   re.addProperty("ipAddress", myIP);
		   String request = re.toString();
		   ////System.out.println("request sent "+request);
		   String response = ok.post(karzakey,karzavalue,adharConsent,request,timeout);
		   ////System.out.println("reponse recieved "+response);
		   if(callfrom.equals("adharconsent"))
		   {
			  
			   String response1 = verifyresponse(response,re);
			   this.setClientid(null);
			   this.setOfficeId(null);
			   ////System.out.println("response from with mobile adhar "+response1);
			   return response1;
		   }
		   else
		   {
			  String response1 = verifyresponsewithoutmobile(response,re);
			   this.setClientid(null);
			   this.setOfficeId(null);
			   ////System.out.println("response of without mobile adhar "+response1);
			  return response1;
		   }
	}
	
	public String verifyresponse(String json,JsonObject re)
	{
		 if (StringUtils.isBlank(json)) 
	        {
	            throw new InvalidJsonException();
	        }
	      
	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedresponse);
	        final JsonElement element = this.fromApiJsonHelper.parse(json);
	        final JsonObject result = this.fromApiJsonHelper.extractJsonObjectNamed("result", element);
	        if(this.fromApiJsonHelper.extractJsonObjectNamed("result", element)!=null)
	        {
	        accessKey = result.get("accessKey").getAsString();
	        this.setAccessKey(accessKey);
	        re.remove("consentTime");
	        re.remove("ipAddress");
	        re.remove("userAgent");
	        re.remove("consentText");
	        re.remove("name");
	        re.addProperty("aadhaarNo",this.getAdhar());
	        re.addProperty("accessKey",this.getAccessKey());
	        
	        ////System.out.println("second step "+re.toString());
	        }
	        else
	        {
	        	re.addProperty("result", "failure");
	        	return re.toString();
	        }
		return re.toString();
	}
	
	public String verifyresponsewithoutmobile(String json,JsonObject re)
	{
		 if (StringUtils.isBlank(json)) 
	        {
	            throw new InvalidJsonException();
	        }
	      
	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedresponse);
	        final JsonElement element = this.fromApiJsonHelper.parse(json);
	        final JsonObject result = this.fromApiJsonHelper.extractJsonObjectNamed("result", element);
	        if(result.get("accessKey").isJsonNull())
	        {
	        	 result.addProperty("message", "access key is not provided");
	        	 return result.toString();
	        }
	        accessKey = result.get("accessKey").getAsString();
	        this.setAccessKey(accessKey);
	        re.remove("consentTime");
	        re.remove("ipAddress");
	        re.remove("userAgent");
	        re.remove("consentText");
	        re.addProperty("aadhaarNo",this.getAdhar());
	        re.addProperty("accessKey",this.getAccessKey());
	        re.addProperty("clientid", this.getClientid());
	        re.addProperty("officeId", this.getOfficeId());
	      
	        ////System.out.println("second step "+re.toString());
	        
		return re.toString();
	}
	
	
	public String createbodyfordetails(String json)
	{
		if (StringUtils.isBlank(json)) 
        {
            throw new InvalidJsonException();
        }
		  final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	      this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
	      final JsonElement element = this.fromApiJsonHelper.parse(json);
	      final JsonObject re = element.getAsJsonObject();
	     
	 
	      re.addProperty("consent", "Y");
	     
	      ////System.out.println("details :"+re.toString());
	      return re.toString();
		
	}
	
	public Long getconsentTime()
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Long s = timestamp.getTime();
		String time = ""+s;
		String newtime = "";
		char[] c = time.toCharArray();
		for(int i = 0 ;i<10;i++)
		{
			newtime = newtime + c[i];
		}
		Long time1 = Long.parseLong(newtime);
		return time1-10;
	}
}
