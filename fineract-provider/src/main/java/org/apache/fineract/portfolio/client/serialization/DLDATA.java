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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;



@Component
public class DLDATA 
{
	String name;
	String status;
	String image;
	Long statuscode;
	String fatherOrhusband;
	String bloodGroup ;
	String dlNumber ;
	String dob;
	String issueDate;
	String district ;
	String nonTransport;

	String pin ;
	String completeAddress ;
	String country ;
	String state ;
	String addressLine1 ;
	String type;
	String cov;
	String transport;
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
	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("result","statusCode","requestId","validity","statusCode","status","error"));


	private final FromJsonHelper fromApiJsonHelper;


	@Autowired
	public DLDATA(FromJsonHelper fromApiJsonHelper) {
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


		if(this.fromApiJsonHelper.extractLongNamed("statusCode", element)!=null)
		{
			statuscode = this.fromApiJsonHelper.extractLongNamed("statusCode", element);

		}
		else
		{
			this.setStatus("Inactive");
			return null;
		}

		if(statuscode != null && statuscode==101)
		{
			if(this.fromApiJsonHelper.extractStringNamed("requestId", element)!=null)
			{
				requestid = this.fromApiJsonHelper.extractStringNamed("requestId", element);
				this.setRequestid(requestid);

			}
			this.setStatuscode(statuscode);
			if(!this.fromApiJsonHelper.extractJsonObjectNamed("result", element).isJsonNull())
			{
				final JsonObject result = this.fromApiJsonHelper.extractJsonObjectNamed("result", element);
				String re = result.toString();
				if(result.get("name")!=null)
				{
					if(!result.get("name").isJsonNull())
					{
						name = result.get("name").getAsString();
						this.setName(name);
					}
					else
					{
						name = "data Unavailable";
						this.setName(name);
					}
				}
				else
				{
					name = "data Unavailable";
					this.setName(name);
				}
				if(result.get("status")!=null)
				{
					if(!result.get("status").isJsonNull())
					{
						status = result.get("status").getAsString();
						this.setStatus(status);
					}
					else
					{
						status = "data Unavailable";
						this.setStatus(status);
					}
				}
				else
				{
					status = "data Unavailable";
					this.setStatus(status);
				}

				if(result.get("father/husband")!=null)
				{
					if(!result.get("father/husband").isJsonNull())
					{
						fatherOrhusband = result.get("father/husband").getAsString();

					}
					else
					{
						fatherOrhusband = "data Unavailable";

					}
				}
				else
				{
					fatherOrhusband = "data Unavailable";

				}

				if(result.get("bloodGroup")!=null)
				{
					if(!result.get("bloodGroup").isJsonNull())
					{
						bloodGroup = result.get("bloodGroup").getAsString();

					}
					else
					{
						bloodGroup = "data Unavailable";

					}
				}
				else
				{
					bloodGroup = "data Unavailable";

				}

				if(result.get("dlNumber")!=null)
				{
					if(!result.get("dlNumber").isJsonNull())
					{
						dlNumber = result.get("dlNumber").getAsString();

					}
					else
					{
						dlNumber = "data Unavailable";

					}

				}
				else
				{
					dlNumber = "data Unavailable";

				}

				if(result.get("img")!=null)
				{
					if(!result.get("img").isJsonNull())
					{
						image = result.get("img").getAsString();
						this.setImage(image);

					}
					else
					{
						image = "data Unavailable";
						this.setImage(image);
					}
				}
				else
				{
					image = "data Unavailable";
					this.setImage(image);
				}
				if(result.get("dob")!=null)
				{
					if(!result.get("dob").isJsonNull())
					{
						dob = result.get("dob").getAsString();

					}
					else
					{
						dob = "data Unavailable";

					}
				}
				else
				{
					dob = "data Unavailable";

				}
				if(result.get("issueDate")!=null)
				{
					if(!result.get("issueDate").isJsonNull())
					{
						issueDate = result.get("issueDate").getAsString();

					}
					else
					{
						issueDate = "data Unavailable";

					}
				}
				else
				{
					issueDate = "data Unavailable";

				}

				@SuppressWarnings("deprecation")
				com.google.gson.JsonObject jsonObject = new JsonParser().parse(re).getAsJsonObject();
				if(jsonObject.get("address")!=null)
				{
					if(!jsonObject.get("address").isJsonNull())
					{
						com.google.gson.JsonElement elmw = jsonObject.get("address");
						JsonArray elm = elmw.getAsJsonArray();
						
						for(int k = 0 ; k < elm.size() ; k++)
						{
							jsonObject = elm.get(k).getAsJsonObject();

							if(jsonObject.get("district")!=null)
							{
								if(!jsonObject.get("district").isJsonNull())
								{
									district = jsonObject.get("district").getAsString();
								}
								else
								{
									district = "data Unavailable";

								}
							}
							else
							{
								district = "data Unavailable";

							}

							if(jsonObject.get("pin")!=null)
							{
								if(!jsonObject.get("pin").isJsonNull())
								{
									district = jsonObject.get("pin").getAsString();

								}
								else
								{
									pin = "data Unavailable";

								}
							}
							else
							{
								pin = "data Unavailable";

							}

							if(jsonObject.get("completeAddress")!=null)
							{
								if(!jsonObject.get("completeAddress").isJsonNull())
								{
									completeAddress = jsonObject.get("completeAddress").getAsString();

								}
								else
								{
									completeAddress = "data Unavailable";

								}
							}
							else
							{
								completeAddress = "data Unavailable";

							}

							if(jsonObject.get("country")!=null)
							{
								if(!jsonObject.get("country").isJsonNull())
								{
									country = jsonObject.get("country").getAsString();

								}
								else
								{
									country = "data Unavailable";

								}
							}
							else
							{
								country = "data Unavailable";

							}

							if(jsonObject.get("state")!=null)
							{
								if(!jsonObject.get("state").isJsonNull())
								{
									state = jsonObject.get("state").getAsString();

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

							if(jsonObject.get("addressLine1")!=null)
							{
								if(!jsonObject.get("addressLine1").isJsonNull())
								{
									addressLine1 = jsonObject.get("addressLine1").getAsString();

								}
								else
								{
									addressLine1 = "data Unavailable";

								}
							}
							else
							{
								addressLine1 = "data Unavailable";

							}

							if(jsonObject.get("type")!=null)
							{
								if(!jsonObject.get("type").isJsonNull())
								{
									type = jsonObject.get("type").getAsString();

								}
								else
								{
									type = "data Unavailable";

								}
							}
							else
							{
								type = "data Unavailable";

							}
						}
					
					

					}
					else
					{
						district = "data Unavailable";
						pin = "data Unavailable";
						completeAddress = "data Unavailable" ;
						country = "data Unavailable";
						state = "data Unavailable";
						addressLine1 = "data Unavailable";
						type = "data Unavailable";
					}
				}
				else
				{
					district = "data Unavailable";
					pin = "data Unavailable";
					completeAddress = "data Unavailable" ;
					country = "data Unavailable";
					state = "data Unavailable";
					addressLine1 = "data Unavailable";
					type = "data Unavailable";
				}
				@SuppressWarnings("deprecation")
				com.google.gson.JsonObject jsonObject1 = new JsonParser().parse(re).getAsJsonObject();
				if(jsonObject1.get("covDetails")!=null)
				{
					if(!jsonObject1.get("covDetails").isJsonNull())
					{
						com.google.gson.JsonElement elm1 = jsonObject1.get("covDetails").getAsJsonArray().get(0);

						jsonObject1 = elm1.getAsJsonObject();

						if(jsonObject1.get("cov")!=null)
						{
							if(!jsonObject1.get("cov").isJsonNull())
							{
								cov = jsonObject1.get("cov").getAsString();
							}
							else
							{
								cov = "Data Unavailable";
							}
						}
					}
					else
					{
						cov = "Data Unavailable";
					}
				}
				else
				{
					cov = "Data Unavailable";
				}
				@SuppressWarnings("deprecation")
				com.google.gson.JsonObject jsonObject2 = new JsonParser().parse(re).getAsJsonObject();

				if(jsonObject2.get("validity")!=null)
				{
					if(!jsonObject2.get("validity").isJsonNull())
					{
						com.google.gson.JsonElement elm2 = jsonObject2.get("validity");

						jsonObject2 = elm2.getAsJsonObject();

						if(jsonObject2.get("nonTransport")!=null)
						{
							if(!jsonObject2.get("nonTransport").isJsonNull())
							{
								nonTransport = jsonObject2.get("nonTransport").getAsString();
							}
							else
							{
								nonTransport = "Data Unavailable" ;
							}

						}
						else
						{
							nonTransport = "Data Unavailable" ;
						}


						if(jsonObject2.get("transport")!=null)
						{
							if(!jsonObject2.get("transport").isJsonNull())
							{
								transport = jsonObject2.get("transport").getAsString();
							}
							else
							{
								transport = "Data Unavailable" ;
							}
						}
						else
						{
							transport = "Data Unavailable" ;
						}

					}
				}

				String []dldetails = new String[] {"name",name,"status",status,"fatherOrhusband",fatherOrhusband,"bloodGroup",bloodGroup,"dlNumber",dlNumber,"dob",dob,"issueDate",issueDate,"address","-","district",district,
						"pin",pin,"completeAddress",completeAddress,"country",country,"state",state,"addressLine1",addressLine1,"type",type,"cov",cov,"nonTransport",nonTransport,"transport",transport};

				return dldetails;

			}
		}
		else
		{
			this.setStatus("Inactive");
			return null;
		}
		this.setStatus("Inactive");
		return null;

	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

}
