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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

import java.util.Map;


import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


@Component
public class PanAuthCommand
{

	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("result","status-code","request_id","status","error"));


	private final FromJsonHelper fromApiJsonHelper;
	private String name = null;
	private Long statuscode = null;
	private String requestid = null;
	
	
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

	@Autowired
	public PanAuthCommand(FromJsonHelper fromApiJsonHelper) {
		super();
		this.fromApiJsonHelper = fromApiJsonHelper;

	}

	public String[] validateForCreate(final String json)
	{
		if (StringUtils.isBlank(json)) 
		{
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
		////System.out.println(supportedParameters);
		final JsonElement element = this.fromApiJsonHelper.parse(json);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("result");



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

					if(!result.get("name").isJsonNull())
					{
						name = result.get("name").getAsString();
						this.setName(name);
					}
					else
					{
						name = "Data Unavailable";
						this.setName(name);
					}

					String []panauth = new String[] {"name",name};
					return panauth;

				}
				else
				{
					this.setStatuscode(10l);   	
					return null;
				}
			}
			else
			{
				this.setStatuscode(10l);   	
				return null;
			}
		}

		else
		{
			this.setStatuscode(10l);   	
			return null;
		}
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {

		this.name = name;
	}
}
