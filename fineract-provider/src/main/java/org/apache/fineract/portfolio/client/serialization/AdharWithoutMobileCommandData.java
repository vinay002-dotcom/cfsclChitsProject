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
import com.google.gson.reflect.TypeToken;

@Component
public class AdharWithoutMobileCommandData 
{
	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("result","status-code","request_id","clientData","status","error"));
	private final FromJsonHelper fromApiJsonHelper;
	@Autowired
	public AdharWithoutMobileCommandData(FromJsonHelper fromApiJsonHelper) {
		super();
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	Long statusCode;
	String ageBand;
	String gender;
	String state;
	String mobile;
	public Long getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Long statusCode) {
		this.statusCode = statusCode;
	}

	public String[] validate(final String json)
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
			statusCode = this.fromApiJsonHelper.extractLongNamed("status-code", element);

		}
		else
		{
			this.setStatusCode(90l);
			return null;
		}
		if(statusCode != null && statusCode==101)
		{
			this.setStatusCode(statusCode);
			if(!this.fromApiJsonHelper.extractJsonObjectNamed("result", element).isJsonNull())
			{
				final JsonObject result = this.fromApiJsonHelper.extractJsonObjectNamed("result", element);
				if(result.get("ageBand")!=null)
				{
					if(!result.get("ageBand").isJsonNull())
					{
						ageBand = result.get("ageBand").getAsString();
					}
					else
					{
						ageBand = "data Unavailable";
					}
				}
				else
				{
					ageBand = "data Unavailable";
				}
				if(result.get("gender")!=null)
				{
					if(!result.get("gender").isJsonNull())
					{
						gender = result.get("gender").getAsString();
					}
					else
					{
						gender = "data Unavailable";
					}
				}
				else
				{
					gender = "data Unavailable";
				}
				if(result.get("state")!=null)
				{
					if(!result.get("state").isJsonNull())
					{
						state = result.get("state").getAsString();
					}
					else
					{
						state = "data Unavailable";
					}
				}
				else
				{
					state = "data Unavailable";
				}
				if(result.get("mobile")!=null)
				{
					if(!result.get("mobile").isJsonNull())
					{
						mobile = result.get("mobile").getAsString();
					}
					else
					{
						mobile = "data Unavailable";
					}

				}
				else
				{
					mobile = "data Unavailable";
				}

				String[] response = new String[] {"result","-","ageBand",ageBand,"gender",gender,"state",state,"mobile",mobile};
				return response;
			}
			else
			{
				this.setStatusCode(90l);
				return null;
			}


		}
		else
		{
			this.setStatusCode(90l);
			return null;
		}

	}

}
