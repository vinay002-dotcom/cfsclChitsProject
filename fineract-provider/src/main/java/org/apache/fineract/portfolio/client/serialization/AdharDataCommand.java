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
public class AdharDataCommand 
{
	public String getVtcName() {
		return vtcName;
	}

	public void setVtcName(String vtcName) {
		this.vtcName = vtcName;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getSubdistrict() {
		return subdistrict;
	}

	public void setSubdistrict(String subdistrict) {
		this.subdistrict = subdistrict;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPostOffice() {
		return postOffice;
	}

	public void setPostOffice(String postOffice) {
		this.postOffice = postOffice;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	JsonElement splitaddress;
	public JsonElement getSplitaddress() {
		return splitaddress;
	}

	public void setSplitaddress(JsonElement splitaddress) {
		this.splitaddress = splitaddress;
	}
	String dob;
	String gender;
	String  fatherName;
	String houseNumber;
	String street ;
	String landmark ;
	String subdistrict ;
	String district;
	String vtcName ;
	String location ;
	String postOffice ;
	String state ;
	String country ;
	String pincode;
	String combinedaddr;
	String splitaddr;
	String requestid;
	
	
	public String getRequestid() {
		return requestid;
	}

	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
	String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	Long status;
	String image;
	private final Set<String> supportedParameters =  new HashSet<>(Arrays.asList("result","statusCode","requestId","clientData","statusMessage","status","error"));

	private final FromJsonHelper fromApiJsonHelper;
	private final AdharForwarding ap;

	@Autowired
	public AdharDataCommand(FromJsonHelper fromApiJsonHelper,final AdharForwarding ap) 
	{
		super();
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.ap = ap;
	}

	public String[] validateForCreate(final String json,final String adhar)
	{
		if (StringUtils.isBlank(json)) 
		{
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
		final JsonElement element;
		JsonObject result = null ;
		if(this.fromApiJsonHelper.parse(json)!=null)
		{
			element = this.fromApiJsonHelper.parse(json);

		}
		else
		{
			element = null;
			this.setStatus(90l);
			return null;
		}

		if(this.fromApiJsonHelper.extractLongNamed("statusCode", element)!=null)
		{
			status =  this.fromApiJsonHelper.extractLongNamed("statusCode", element);
			this.setStatus(status);
		}
		else
		{
			this.setStatus(90l);
			return null;
		}

		if(status !=null && status == 101)
		{

			if(this.fromApiJsonHelper.extractStringNamed("requestId", element)!=null)
			{
				requestid =  this.fromApiJsonHelper.extractStringNamed("requestId", element);
				this.setRequestid(requestid);
			}
			if(this.fromApiJsonHelper.extractJsonObjectNamed("result", element)!=null)
			{
				if(!this.fromApiJsonHelper.extractJsonObjectNamed("result", element).isJsonNull())
				{
					result = this.fromApiJsonHelper.extractJsonObjectNamed("result", element);   
				}
			}
			else
			{
				result = null;
				this.setStatus(90l);
				return null;
			}
			String re = result.toString();
			@SuppressWarnings("deprecation")
			com.google.gson.JsonObject jsonObject = new JsonParser().parse(re).getAsJsonObject();

			if(jsonObject.get("dataFromAadhaar")!=null)
			{
				if(!jsonObject.get("dataFromAadhaar").isJsonNull())
				{

					com.google.gson.JsonElement elm = jsonObject.get("dataFromAadhaar");

					jsonObject = elm.getAsJsonObject();
					String generatedDateTime;
					if(jsonObject.get("generatedDateTime")!=null)
					{
						if(!jsonObject.get("generatedDateTime").isJsonNull())
						{
							generatedDateTime = jsonObject.get("generatedDateTime").getAsString();
						}
						else
						{
							generatedDateTime = "Data Unavailable";
						}
					}
					else
					{
						generatedDateTime = "Data Unavailable";
					}

					if(jsonObject.get("name")!=null)
					{
						if(!jsonObject.get("name").isJsonNull())
						{
							name = jsonObject.get("name").getAsString();
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
					if(jsonObject.get("dob")!=null)
					{
						if(!jsonObject.get("dob").isJsonNull())
						{
							dob = jsonObject.get("dob").getAsString();
							this.setDob(dob);
						}
						else
						{
							dob = "Data Unavailable";
							this.setDob(dob);
						}
					}
					else
					{
						dob = "Data Unavailable";
						this.setDob(dob);
					}
					if(jsonObject.get("gender")!=null)
					{
						if(!jsonObject.get("gender").isJsonNull())
						{
							gender = jsonObject.get("gender").getAsString();
							this.setGender(gender);
						}
						else
						{
							gender = "Data Unavailable";
							this.setGender(gender);
						}

					}
					else
					{
						gender = "Data Unavailable";
						this.setGender(gender);
					}

					if(jsonObject.get("fatherName")!=null)
					{
						if(!jsonObject.get("fatherName").isJsonNull())
						{
							fatherName = jsonObject.get("fatherName").getAsString();
							this.setFatherName(fatherName);
						}
						else
						{
							fatherName = "Data Unavailable";
							this.setFatherName(fatherName);
						}

					}
					else
					{
						fatherName = "Data Unavailable";
						this.setFatherName(fatherName);
					}

					if(jsonObject.get("image")!=null)
					{
						if(!jsonObject.get("image").isJsonNull())
						{
							image = jsonObject.get("image").getAsString();
							this.setImage(image);
						}
						else
						{
							image = "Image unavailable";
							this.setImage(image);
						}
					}
					else
					{
						image = "Image unavailable";
						this.setImage(image);
					}

					String adr = elm.toString();

					@SuppressWarnings("deprecation")
					com.google.gson.JsonObject jsonObject1 = new JsonParser().parse(adr).getAsJsonObject();

					if(jsonObject1.get("address")!=null)
					{
						if(!jsonObject1.get("address").isJsonNull())
						{
							com.google.gson.JsonElement elm1 = jsonObject1.get("address");

							jsonObject1 = elm1.getAsJsonObject();

							if(jsonObject1.get("combinedAddress")!=null)
							{
								if(!jsonObject1.get("combinedAddress").isJsonNull())
								{
									combinedaddr = jsonObject1.get("combinedAddress").getAsString();
								}
								else
								{
									combinedaddr = "Data Unavailable";
								}
							}
							else
							{
								combinedaddr = "Data Unavailable";
							}
							splitaddr = elm1.toString();
							@SuppressWarnings("deprecation")

							com.google.gson.JsonObject jsonObject2 = new JsonParser().parse(splitaddr).getAsJsonObject();

							if(jsonObject1.get("splitAddress")!=null)
							{
								if(!jsonObject2.get("splitAddress").isJsonNull())
								{
									com.google.gson.JsonElement elm2 = jsonObject2.get("splitAddress");
									this.setSplitaddress(elm2);
									jsonObject2 = elm2.getAsJsonObject();
									if(jsonObject2.get("houseNumber")!=null)
									{
										if(!jsonObject2.get("houseNumber").isJsonNull())
										{
											houseNumber = jsonObject2.get("houseNumber").getAsString();
											this.setHouseNumber(houseNumber);
											////System.out.println("this housenumber"+this.getHouseNumber());
										}
										else
										{
											houseNumber ="Data Unavailable";
											this.setHouseNumber(houseNumber);
										}
									}
									else
									{
										houseNumber ="Data Unavailable";
										this.setHouseNumber(houseNumber);
									}
									if(jsonObject2.get("street")!=null)
									{
										if(!jsonObject2.get("street").isJsonNull())
										{
											street = jsonObject2.get("street").getAsString();
											this.setStreet(street);
										}
										else
										{
											street ="Data Unavailable";
											this.setStreet(street);
										}
									}
									else
									{
										street ="Data Unavailable";
										this.setStreet(street);
									}
									if(jsonObject2.get("landmark")!=null)
									{
										if(!jsonObject2.get("landmark").isJsonNull())
										{
											landmark = jsonObject2.get("landmark").getAsString();
											this.setLandmark(landmark);
										}
										else
										{
											landmark ="Data Unavailable";
											this.setLandmark(landmark);
										}

									}
									else
									{
										landmark ="Data Unavailable";
										this.setLandmark(landmark);
									}

									if(jsonObject2.get("subdistrict")!=null)
									{
										if(!jsonObject2.get("subdistrict").isJsonNull())
										{
											subdistrict = jsonObject2.get("subdistrict").getAsString();
											this.setSubdistrict(subdistrict);
										}
										else
										{
											subdistrict ="Data Unavailable";
											this.setSubdistrict(subdistrict);
										}

									}
									else
									{
										subdistrict ="Data Unavailable";
										this.setSubdistrict(subdistrict);
									}

									if(jsonObject2.get("district")!=null)
									{
										if(!jsonObject2.get("district").isJsonNull())
										{
											district = jsonObject2.get("district").getAsString();
											this.setDistrict(district);
										}
										else
										{
											district ="Data Unavailable";
											this.setDistrict(district);
										}
									}
									else
									{
										district ="Data Unavailable";
										this.setDistrict(district);
									}

									if(jsonObject2.get("vtcName")!=null)
									{
										if(!jsonObject2.get("vtcName").isJsonNull())
										{
											vtcName = jsonObject2.get("vtcName").getAsString();
											this.setVtcName(vtcName);
										}
										else
										{
											vtcName ="Data Unavailable";
											this.setVtcName(vtcName);
										}
									}
									else
									{
										vtcName ="Data Unavailable";
										this.setVtcName(vtcName);
									}

									if(jsonObject2.get("location")!=null)
									{
										if(!jsonObject2.get("location").isJsonNull())
										{
											location = jsonObject2.get("location").getAsString();
											this.setLocation(location);
										}
										else
										{
											location ="Data Unavailable";
											this.setLocation(location);
										}
									}
									else
									{
										location ="Data Unavailable";
										this.setLocation(location);
									}

									if(jsonObject2.get("postOffice")!=null)
									{
										if(!jsonObject2.get("postOffice").isJsonNull())
										{
											postOffice = jsonObject2.get("postOffice").getAsString();
											this.setPostOffice(postOffice);
										}
										else
										{
											postOffice ="Data Unavailable";
											this.setPostOffice(postOffice);
										}
									}
									else
									{
										postOffice ="Data Unavailable";
										this.setPostOffice(postOffice);
									}

									if(jsonObject2.get("state")!=null)
									{
										if(!jsonObject2.get("state").isJsonNull())
										{
											state = jsonObject2.get("state").getAsString();
											this.setState(state);
										}
										else
										{
											state ="Data Unavailable";
											this.setState(state);
										}
									}
									else
									{
										state ="Data Unavailable";
										this.setState(state);
									}

									if(jsonObject2.get("country")!=null)
									{
										if(!jsonObject2.get("country").isJsonNull())
										{
											country = jsonObject2.get("country").getAsString();
											this.setCountry(country);
										}
										else
										{
											country ="Data Unavailable";
											this.setCountry(country);
										}
									}
									else
									{
										country ="Data Unavailable";
										this.setCountry(country);
									}

									if(jsonObject2.get("pincode")!=null)
									{
										if(!jsonObject2.get("pincode").isJsonNull())
										{
											pincode = jsonObject2.get("pincode").getAsString();
											this.setPincode(pincode);
										}
										else
										{
											pincode ="Data Unavailable";
											this.setPincode(pincode);
										}
									}
									else
									{
										pincode ="Data Unavailable";
										this.setPincode(pincode);
									}

								}
								else
								{
									houseNumber ="Data Unavailable";
									street = "Data Unavailable" ;
									landmark = "Data Unavailable";
									subdistrict = "Data Unavailable";
									district = "Data Unavailable";
									vtcName = "Data Unavailable" ;
									location = "Data Unavailable";
									postOffice = "Data Unavailable";
									state = "Data Unavailable";
									country = "Data Unavailable" ;
									pincode = "Data Unavailable";
									combinedaddr = "Data Unavailable";
									splitaddr = "Data Unavailable";
								}
							}
							else
							{
								houseNumber ="Data Unavailable";
								street = "Data Unavailable" ;
								landmark = "Data Unavailable";
								subdistrict = "Data Unavailable";
								district = "Data Unavailable";
								vtcName = "Data Unavailable" ;
								location = "Data Unavailable";
								postOffice = "Data Unavailable";
								state = "Data Unavailable";
								country = "Data Unavailable" ;
								pincode = "Data Unavailable";
								combinedaddr = "Data Unavailable";
								splitaddr = "Data Unavailable";
							}
						}
					}
					else
					{
						houseNumber ="Data Unavailable";
						street = "Data Unavailable" ;
						landmark = "Data Unavailable";
						subdistrict = "Data Unavailable";
						district = "Data Unavailable";
						vtcName = "Data Unavailable" ;
						location = "Data Unavailable";
						postOffice = "Data Unavailable";
						state = "Data Unavailable";
						country = "Data Unavailable" ;
						pincode = "Data Unavailable";
						combinedaddr = "Data Unavailable";
						splitaddr = "Data Unavailable";
						combinedaddr = "Data Unavailable";
					}
					String[] a = new String[] {"generatedDateTime",generatedDateTime,"name",this.getName(),"AadhaarNumber",adhar,"dob",dob,"gender",gender,"fatherName",fatherName,"combinedaddr",combinedaddr,"splitaddress","-","houseNumber"
							,houseNumber,"street",street,"landmark",landmark,"subdistrict",subdistrict,"district",district,"vtcName",vtcName,"location",location,"postOffice",postOffice,"state",state,"country",country,"pincode",pincode};

					return a;
				}
			}
			else
			{
				this.setStatus(90l);

			}
		}
		return null;


	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}
