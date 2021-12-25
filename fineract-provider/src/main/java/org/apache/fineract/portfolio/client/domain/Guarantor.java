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
package org.apache.fineract.portfolio.client.domain;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.data.GuarantorType;

import com.google.gson.JsonObject;

@Entity
@Table(name = "m_guarantor")
public final class Guarantor extends AbstractPersistableCustom 
{
	@Column(name = "client_reln_cv_id")
	private Long relationshipId;
	
	@Column(name = "type_enum")
	private Long typeEnum;
	
	@Column(name = "entity_id")
	private String entity;
	
	@Column(name = "firstname")
	private String firstname;
	
	@Column(name = "lastname")
	private String lastname;
	
	@Column(name = "dob")
	private LocalDate dob;
	
	@Column(name = "address_line_1")
	private String address1;
	
	@Column(name = "address_line_2")
	private String address2;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "zip")
	private String zip;
	
	@Column(name = "mobile_number")
	private String Phone;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "is_active")
	private Boolean isActive;
	
	@Column(name = "client_id")
	private Long clientId;
	
	@Column(name = "qualification")
	private Long qualification;
	
	@Column(name = "profession")
	private Long profession;

	public Guarantor(Long relationshipId, Long typeEnum, String entity, String firstname, String lastname,
			LocalDate dob, String address1, String address2, String city, String state, String country, String zip,
			String Phone, String comment, Boolean isActive, Long clientId, Long qualification, Long profession) {
		super();
		this.relationshipId = relationshipId;
		this.typeEnum = typeEnum;
		this.entity = entity;
		this.firstname = firstname;
		this.lastname = lastname;
		this.dob = dob;
		this.address1 = address1;
		this.address2 = address2;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zip = zip;
		this.Phone = Phone;
		this.comment = comment;
		this.isActive = isActive;
		this.clientId = clientId;
		this.qualification = qualification;
		this.profession = profession;
	}
	
	public static Guarantor create(final JsonObject body)
	{
		Long relationshipId = null;
		if(body.get("ClientRelationshipId")!=null && !body.get("ClientRelationshipId").isJsonNull())
		{
			relationshipId = body.get("ClientRelationshipId").getAsLong();
		}
		
		Long typeEnum = null;
		if(body.get("typeEnum")!=null && !body.get("typeEnum").isJsonNull())
		{
			String tempValue = body.get("typeEnum").getAsString();
			typeEnum = GuarantorType.valueOf(tempValue).getValue().longValue();
		}
		
		String Entityid = null;
		if(body.get("Entityid")!=null && !body.get("Entityid").isJsonNull())
		{
			Entityid = body.get("Entityid").getAsString();
		}
		
		String firstName = null;
		if(body.get("firstName")!=null && !body.get("firstName").isJsonNull())
		{
			firstName = body.get("firstName").getAsString();
		}
		
		String LastName = null;
		if(body.get("LastName")!=null && !body.get("LastName").isJsonNull())
		{
			LastName = body.get("LastName").getAsString();
		}
		
		LocalDate dob = null;
		if(body.get("dob")!=null && !body.get("dob").isJsonNull())
		{
			String tempValue = body.get("dob").getAsString();
			dob = LocalDate.parse(tempValue);
		}
		
		String Address1 = null;
		if(body.get("Address1")!=null && !body.get("Address1").isJsonNull())
		{
			Address1 = body.get("Address1").getAsString();
		}
		
		String Address2 = null;
		if(body.get("Address2")!=null && !body.get("Address2").isJsonNull())
		{
			Address2 = body.get("Address2").getAsString();
		}
		
		String city = null;
		if(body.get("city")!=null && !body.get("city").isJsonNull())
		{
			city = body.get("city").getAsString();
		}
		
		String state = null;
		if(body.get("state")!=null && !body.get("state").isJsonNull())
		{
			state = body.get("state").getAsString();
		}
		
		String country = null;
		if(body.get("country")!=null && !body.get("country").isJsonNull())
		{
			country = body.get("country").getAsString();
		}
		
		String zip = null;
		if(body.get("zip")!=null && !body.get("zip").isJsonNull())
		{
			zip = body.get("zip").getAsString();
		}
		
		String Phone = null;
		if(body.get("Phone")!=null && !body.get("Phone").isJsonNull())
		{
			Phone = body.get("Phone").getAsString();
		}
		
		String comments = null;
		if(body.get("comments")!=null && !body.get("comments").isJsonNull())
		{
			comments = body.get("comments").getAsString();
		}
		
		
		Boolean isActive = null;
		if(body.get("isActive")!=null && !body.get("isActive").isJsonNull())
		{
			isActive = body.get("isActive").getAsBoolean();
		}
		
		Long clientId = null;
		if(body.get("clientId")!=null && !body.get("clientId").isJsonNull())
		{
			clientId = body.get("clientId").getAsLong();
		}
		
		Long qualification = null;
		if(body.get("qualification")!=null && !body.get("qualification").isJsonNull())
		{
			qualification = body.get("qualification").getAsLong();
		}
		
		Long profession = null;
		if(body.get("profession")!=null && !body.get("profession").isJsonNull())
		{
			profession = body.get("profession").getAsLong();
		}
		
		return new Guarantor(relationshipId,typeEnum,Entityid,firstName,LastName,dob,Address1,Address2,city,state,country,zip,Phone,comments,isActive,clientId,qualification,profession);
	}
	
	public Map<String,Object> Update(JsonObject body)
	{
		Map<String,Object> actualChanges = new LinkedHashMap<>();
		
		Long relationshipId = null;
		if(body.get("ClientRelationshipId")!=null && !body.get("ClientRelationshipId").isJsonNull())
		{
			relationshipId = body.get("ClientRelationshipId").getAsLong();
			actualChanges.put("relationshipId", relationshipId);
			this.setRelationshipId(relationshipId);
		}
		
		Long typeEnum = null;
		if(body.get("typeEnum")!=null && !body.get("typeEnum").isJsonNull())
		{
			String tempValue = body.get("typeEnum").getAsString();
			typeEnum = GuarantorType.valueOf(tempValue).getValue().longValue();
			actualChanges.put("typeEnum", tempValue);
			this.setTypeEnum(typeEnum);
		}
		
		String Entityid = null;
		if(body.get("Entityid")!=null && !body.get("Entityid").isJsonNull())
		{
			Entityid = body.get("Entityid").getAsString();
			actualChanges.put("Entityid", Entityid);
			this.setEntity(Entityid);
		}
		
		String firstName = null;
		if(body.get("firstName")!=null && !body.get("firstName").isJsonNull())
		{
			firstName = body.get("firstName").getAsString();
			actualChanges.put("firstName", firstName);
			this.setFirstname(firstName);
		}
		
		String LastName = null;
		if(body.get("LastName")!=null && !body.get("LastName").isJsonNull())
		{
			LastName = body.get("LastName").getAsString();
			actualChanges.put("LastName", LastName);
			this.setLastname(LastName);
		}
		
		LocalDate dob = null;
		if(body.get("dob")!=null && !body.get("dob").isJsonNull())
		{
			String tempValue = body.get("dob").getAsString();
			dob = LocalDate.parse(tempValue);
			actualChanges.put("dob", dob);
			this.setDob(dob);
		}
		
		String Address1 = null;
		if(body.get("Address1")!=null && !body.get("Address1").isJsonNull())
		{
			Address1 = body.get("Address1").getAsString();
			actualChanges.put("Address1", Address1);
			this.setAddress1(Address1);
		}
		
		String Address2 = null;
		if(body.get("Address2")!=null && !body.get("Address2").isJsonNull())
		{
			Address2 = body.get("Address2").getAsString();
			actualChanges.put("Address2", Address2);
			this.setAddress2(Address2);
		}
		
		String city = null;
		if(body.get("city")!=null && !body.get("city").isJsonNull())
		{
			city = body.get("city").getAsString();
			actualChanges.put("city", city);
			this.setCity(city);
		}
		
		String state = null;
		if(body.get("state")!=null && !body.get("state").isJsonNull())
		{
			state = body.get("state").getAsString();
			actualChanges.put("state", state);
			this.setState(state);
		}
		
		String country = null;
		if(body.get("country")!=null && !body.get("country").isJsonNull())
		{
			country = body.get("country").getAsString();
			actualChanges.put("country", country);
			this.setCountry(country);
		}
		
		String zip = null;
		if(body.get("zip")!=null && !body.get("zip").isJsonNull())
		{
			zip = body.get("zip").getAsString();
			actualChanges.put("zip", zip);
			this.setZip(zip);
		}
		
		String Phone = null;
		if(body.get("Phone")!=null && !body.get("Phone").isJsonNull())
		{
			Phone = body.get("Phone").getAsString();
			actualChanges.put("Phone", Phone);
			this.setPhone(Phone);
		}
		
		String comments = null;
		if(body.get("comments")!=null && !body.get("comments").isJsonNull())
		{
			comments = body.get("comments").getAsString();
			actualChanges.put("comments", comments);
			this.setComment(comments);
		}
		
		
		Boolean isActive = null;
		if(body.get("isActive")!=null && !body.get("isActive").isJsonNull())
		{
			isActive = body.get("isActive").getAsBoolean();
			actualChanges.put("isActive", isActive);
			this.setIsActive(isActive);
		}
		
		Long clientId = null;
		if(body.get("clientId")!=null && !body.get("clientId").isJsonNull())
		{
			clientId = body.get("clientId").getAsLong();
			actualChanges.put("clientId", clientId);
			this.setClientId(clientId);
		}
		
		Long qualification = null;
		if(body.get("qualification")!=null && !body.get("qualification").isJsonNull())
		{
			qualification = body.get("qualification").getAsLong();
			actualChanges.put("qualification", qualification);
			this.setQualification(qualification);
		}
		
		Long profession = null;
		if(body.get("profession")!=null && !body.get("profession").isJsonNull())
		{
			profession = body.get("profession").getAsLong();
			actualChanges.put("profession", profession);
			this.setProfession(profession);
			
		}
		
		return actualChanges;
	}

	public Long getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(Long relationshipId) {
		this.relationshipId = relationshipId;
	}

	public Long getTypeEnum() {
		return typeEnum;
	}

	public void setTypeEnum(Long typeEnum) {
		this.typeEnum = typeEnum;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String Phone) {
		this.Phone = Phone;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getQualification() {
		return qualification;
	}

	public void setQualification(Long qualification) {
		this.qualification = qualification;
	}

	public Long getProfession() {
		return profession;
	}

	public void setProfession(Long profession) {
		this.profession = profession;
	}
}
