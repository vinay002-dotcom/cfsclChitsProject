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
public class PassportDataCommand 
{
	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("result","statusCode","requestId","status","error"));

	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public PassportDataCommand(FromJsonHelper fromApiJsonHelper) {
		super();
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	Long statusCode;
	String name;
	String typeOfApplication;
	String applicationDate;
	String surnameFromPassport;
	String nameMatch;
	String nameScore;
	String passportNumberFromSource;
	String passportNumberMatch ;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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

		if(this.fromApiJsonHelper.extractLongNamed("statusCode", element)!=null)
		{
			statusCode = this.fromApiJsonHelper.extractLongNamed("statusCode", element);
		}
		else
		{
			this.setStatusCode(90l);
			return null;
		}

		if(statusCode!=null && statusCode==101)
		{
			this.setStatusCode(statusCode);
			if(this.fromApiJsonHelper.extractJsonObjectNamed("result", element)!=null)
			{
				if(!this.fromApiJsonHelper.extractJsonObjectNamed("result", element).isJsonNull())
				{
					final JsonObject result = this.fromApiJsonHelper.extractJsonObjectNamed("result", element);

					if(result.get("typeOfApplication")!=null)
					{
						if(!result.get("typeOfApplication").isJsonNull())
						{
							typeOfApplication = result.get("typeOfApplication").getAsString();
						}
						else
						{
							typeOfApplication = "Data Unavailable";
						}

					}
					else
					{
						typeOfApplication = "Data Unavailable";
					}

					if(result.get("applicationDate")!=null)
					{
						if(!result.get("applicationDate").isJsonNull())
						{
							applicationDate = result.get("applicationDate").toString();
						}
						else
						{
							applicationDate = "Data Unavailable";
						}

					}
					else
					{
						applicationDate = "Data Unavailable";
					}


					String re = result.toString();
					@SuppressWarnings("deprecation")
					com.google.gson.JsonObject jsonObject2 = new JsonParser().parse(re).getAsJsonObject();

					if(jsonObject2.get("name")!=null)
					{
						if(!jsonObject2.get("name").isJsonNull())
						{
							com.google.gson.JsonElement elm2 = jsonObject2.get("name");

							jsonObject2 = elm2.getAsJsonObject();

							name = jsonObject2.get("nameFromPassport").getAsString();
							this.setName(name);
						}
						else
						{
							name = "Data Unavailable";
							this.setName(name);
						}
					}
					else
					{
						name = "Data Unavailable";
						this.setName(name);
					}

					if(jsonObject2.get("surnameFromPassport")!=null)
					{
						if(!jsonObject2.get("surnameFromPassport").isJsonNull())
						{


							surnameFromPassport = jsonObject2.get("surnameFromPassport").getAsString();

						}
						else
						{
							surnameFromPassport = "Data Unavailable";

						}
					}
					else
					{
						surnameFromPassport = "Data Unavailable";

					}

					if(jsonObject2.get("nameMatch")!=null)
					{
						if(!jsonObject2.get("nameMatch").isJsonNull())
						{


							nameMatch = jsonObject2.get("nameMatch").getAsString();

						}
						else
						{
							nameMatch = "Data Unavailable";

						}
					}
					else
					{
						nameMatch = "Data Unavailable";

					}

					if(jsonObject2.get("nameScore")!=null)
					{
						if(!jsonObject2.get("nameScore").isJsonNull())
						{


							nameScore = jsonObject2.get("nameScore").getAsString();

						}
						else
						{
							nameScore = "Data Unavailable";

						}
					}
					else
					{
						nameScore = "Data Unavailable";

					}

					@SuppressWarnings("deprecation")
					com.google.gson.JsonObject jsonObject3 = new JsonParser().parse(re).getAsJsonObject();

					if(jsonObject3.get("passportNumber")!=null)
					{
						if(!jsonObject3.get("passportNumber").isJsonNull())
						{
							com.google.gson.JsonElement elm3 = jsonObject3.get("passportNumber");

							jsonObject3 = elm3.getAsJsonObject();

							if(jsonObject3.get("passportNumberFromSource")!=null)
							{
								if(!jsonObject3.get("passportNumberFromSource").isJsonNull())
								{
									passportNumberFromSource  = jsonObject3.get("passportNumberFromSource").getAsString();
								}
								else
								{
									passportNumberFromSource = "Data Unavailable";
								}
							}
							else
							{
								passportNumberFromSource = "Data Unavailable";
							}

							if(jsonObject3.get("passportNumberMatch")!=null)
							{
								if(!jsonObject3.get("passportNumberMatch").isJsonNull())
								{
									passportNumberMatch  = jsonObject3.get("passportNumberMatch").getAsString();
								}
								else
								{
									passportNumberMatch = "Data Unavailable";
								}
							}
							else
							{
								passportNumberMatch = "Data Unavailable";
							}

						}
					}

					String[] response= new String[] {"name","-","nameFromPassport",name,"surnameFromPassport",surnameFromPassport,"nameMatch",nameMatch,"nameScore",nameScore,"passportNumber","-","passportNumberFromSource",passportNumberFromSource,"passportNumberMatch",passportNumberMatch,"typeOfApplication",typeOfApplication,"applicationDate",applicationDate};

					return response;

				}
			}

		}
		else
		{
			this.setStatusCode(90l);
			return null;
		}
		this.setStatusCode(90l);
		return null;
	}

}
