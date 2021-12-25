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
public class VoterIdDataCommand 
{
	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("result","status-code","request_id","status","error"));

	private final FromJsonHelper fromApiJsonHelper;

	private String name;

	private Long statuscode;

	String rln_name;
	String rln_type ;
	String gender ;
	String district ;
	String ac_name ;
	String pc_name;
	String state;
	String epic_no ;
	String dob ;
	String age;
	String part_no;
	String slno_inpart ;
	String ps_name;
	String part_name ;
	String last_update;
	String ps_lat_long ;
	String section_no;
	String id ;
	String ac_no ;
	String st_code;
	String house_no ;
	String requestid;


	@Autowired
	public VoterIdDataCommand(FromJsonHelper fromApiJsonHelper) 
	{
		super();
		this.fromApiJsonHelper = fromApiJsonHelper;

	}

	public String getRequestid() {
		return requestid;
	}

	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}

	public FromJsonHelper getFromApiJsonHelper() {
		return fromApiJsonHelper;
	}

	public String[] validateForCreate(final String json)
	{
		if (StringUtils.isBlank(json)) 
		{
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

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

					if(result.get("name")!=null)
					{
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
					}
					else
					{
						name = "Data Unavailable";
						this.setName(name);
					}

					if(result.get("rln_name")!=null)
					{
						if(!result.get("rln_name").isJsonNull())
						{
							rln_name = result.get("rln_name").getAsString();

						}
						else
						{
							rln_name = "Data Unavailable";

						}
					}
					else
					{
						rln_name = "Data Unavailable";
					}

					if(result.get("rln_type")!=null)
					{
						if(!result.get("rln_type").isJsonNull())
						{
							rln_type = result.get("rln_type").getAsString();

						}
						else
						{
							rln_type = "Data Unavailable";

						}
					}
					else
					{
						rln_type = "Data Unavailable";
					}

					if(result.get("gender")!=null)
					{
						if(!result.get("gender").isJsonNull())
						{
							gender = result.get("gender").getAsString();

						}
						else
						{
							gender = "Data Unavailable";

						}
					}
					else
					{
						gender = "Data Unavailable";
					}

					if(result.get("district")!=null)
					{
						if(!result.get("district").isJsonNull())
						{
							district = result.get("district").getAsString();

						}
						else
						{
							district = "Data Unavailable";

						}
					}
					else
					{
						district = "Data Unavailable";
					}

					if(result.get("ac_name")!=null)
					{
						if(!result.get("ac_name").isJsonNull())
						{
							ac_name = result.get("ac_name").getAsString();

						}
						else
						{
							ac_name = "Data Unavailable";

						}
					}
					else
					{
						ac_name = "Data Unavailable";
					}


					if(result.get("pc_name")!=null)
					{
						if(!result.get("pc_name").isJsonNull())
						{
							pc_name = result.get("pc_name").getAsString();

						}
						else
						{
							pc_name = "Data Unavailable";

						}
					}
					else
					{
						pc_name = "Data Unavailable";
					}

					if(result.get("state")!=null)
					{
						if(!result.get("state").isJsonNull())
						{
							state = result.get("state").getAsString();

						}
						else
						{
							state = "Data Unavailable";

						}
					}
					else
					{
						state = "Data Unavailable";
					}

					if(result.get("epic_no")!=null)
					{
						if(!result.get("epic_no").isJsonNull())
						{
							epic_no = result.get("epic_no").getAsString();

						}
						else
						{
							epic_no = "Data Unavailable";

						}
					}
					else
					{
						epic_no = "Data Unavailable";
					}

					if(result.get("dob")!=null)
					{
						if(!result.get("dob").isJsonNull())
						{
							dob = result.get("dob").getAsString();

						}
						else
						{
							dob = "Data Unavailable";

						}
					}
					else
					{
						dob = "Data Unavailable";
					}

					if(result.get("age")!=null)
					{
						if(!result.get("age").isJsonNull())
						{
							age = result.get("age").getAsString();

						}
						else
						{
							age = "Data Unavailable";

						}
					}
					else
					{
						age = "Data Unavailable";
					}

					if(result.get("part_no")!=null)
					{
						if(!result.get("part_no").isJsonNull())
						{
							part_no = result.get("part_no").getAsString();

						}
						else
						{
							part_no = "Data Unavailable";

						}
					}
					else
					{
						part_no = "Data Unavailable";
					}

					if(result.get("slno_inpart")!=null)
					{
						if(!result.get("slno_inpart").isJsonNull())
						{
							slno_inpart = result.get("slno_inpart").getAsString();

						}
						else
						{
							slno_inpart = "Data Unavailable";

						}
					}
					else
					{
						slno_inpart = "Data Unavailable";
					}

					if(result.get("ps_name")!=null)
					{
						if(!result.get("ps_name").isJsonNull())
						{
							ps_name = result.get("ps_name").getAsString();

						}
						else
						{
							ps_name = "Data Unavailable";

						}
					}
					else
					{
						ps_name = "Data Unavailable";
					}

					if(result.get("part_name")!=null)
					{
						if(!result.get("part_name").isJsonNull())
						{
							part_name = result.get("part_name").getAsString();

						}
						else
						{
							part_name = "Data Unavailable";

						}
					}
					else
					{
						part_name = "Data Unavailable";
					}

					if(result.get("last_update")!=null)
					{
						if(!result.get("last_update").isJsonNull())
						{
							last_update = result.get("last_update").getAsString();

						}
						else
						{
							last_update = "Data Unavailable";

						}
					}
					else
					{
						last_update = "Data Unavailable";
					}


					if(result.get("ps_lat_long")!=null)
					{
						if(!result.get("ps_lat_long").isJsonNull())
						{
							ps_lat_long = result.get("ps_lat_long").getAsString();

						}
						else
						{
							ps_lat_long = "Data Unavailable";

						}
					}
					else
					{
						ps_lat_long = "Data Unavailable";
					}

					if(result.get("section_no")!=null)
					{
						if(!result.get("section_no").isJsonNull())
						{
							section_no = result.get("section_no").getAsString();

						}
						else
						{
							section_no = "Data Unavailable";

						}
					}
					else
					{
						section_no = "Data Unavailable";
					}

					if(result.get("id")!=null)
					{
						if(!result.get("id").isJsonNull())
						{
							id = result.get("id").getAsString();

						}
						else
						{
							id = "Data Unavailable";

						}
					}
					else
					{
						id = "Data Unavailable";
					}

					if(result.get("ac_no")!=null)
					{
						if(!result.get("ac_no").isJsonNull())
						{
							ac_no = result.get("ac_no").getAsString();

						}
						else
						{
							ac_no = "Data Unavailable";

						}
					}
					else
					{
						ac_no = "Data Unavailable";
					}

					if(result.get("st_code")!=null)
					{
						if(!result.get("st_code").isJsonNull())
						{
							st_code = result.get("st_code").getAsString();

						}
						else
						{
							st_code = "Data Unavailable";

						}
					}
					else
					{
						st_code = "Data Unavailable";
					}

					if(result.get("house_no")!=null)
					{
						if(!result.get("house_no").isJsonNull())
						{
							house_no = result.get("house_no").getAsString();

						}
						else
						{
							house_no = "Data Unavailable";

						}
					}
					else
					{
						house_no = "Data Unavailable";
					}

					String [] voter = new String[] {"name",name,"rln_name",rln_name,"rln_type",rln_type,"gender",gender,"district",district,"ac_name",ac_name,"pc_name",pc_name,"state",state,
							"epic_no",epic_no,"dob",dob,"age",age,"part_no",part_no,"slno_inpart",slno_inpart,"ps_name",ps_name,"part_name",part_name,"last_update",last_update,"ps_lat_long",ps_lat_long,
							"section_no",section_no,"id",id,"ac_no",ac_no,"st_code",st_code,"house_no",house_no};
					return voter;
				}
			}
		}
		else
		{
			this.setStatuscode(90l);
			return null;
		}
		this.setStatuscode(90l);
		return null;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(Long statuscode) {
		this.statuscode = statuscode;
	}
}
