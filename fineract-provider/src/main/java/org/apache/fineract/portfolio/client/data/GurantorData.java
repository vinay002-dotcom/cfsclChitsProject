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
package org.apache.fineract.portfolio.client.data;

import java.io.Serializable;
import java.time.LocalDate;

public class GurantorData implements Serializable
{
	final private Long id;
	final private Long ClientRelationshipId;
	final private GuarantorType typeEnum;
	final private String Entityid;
	final private String firstName;
	final private String LastName;
	final private LocalDate dob;
	final private String Address1;
	final private String Address2;
	final private String city;
	final private String state;
	final private String country;
	final private String zip;
	final private String Phone;
	final private String comments;
	final private Boolean isActive;
	final private Long clientId;
	final private Long qualification;
	final private Long profession;
	final private String professionType;
	final private String qualificationtype;
	final private String relationshipType;
	public GurantorData(Long id, Long ClientRelationshipId, GuarantorType typeEnum, String Entityid, String firstName,
			String LastName, LocalDate dob, String Address1, String Address2, String city, String state, String country,
			String zip, String Phone, String comments, Boolean isActive, Long clientId, Long qualification,
			Long profession,String professionType,String qualificationtype,String relationshipType) {
		super();
		this.id = id;
		this.ClientRelationshipId = ClientRelationshipId;
		this.typeEnum = typeEnum;
		this.Entityid = Entityid;
		this.firstName = firstName;
		this.LastName = LastName;
		this.dob = dob;
		this.Address1 = Address1;
		this.Address2 = Address2;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zip = zip;
		this.Phone = Phone;
		this.comments = comments;
		this.isActive = isActive;
		this.clientId = clientId;
		this.qualification = qualification;
		this.profession = profession;
		this.relationshipType = relationshipType;
		this.qualificationtype = qualificationtype;
		this.professionType = professionType;
	}
	
	public static GurantorData instance(Long id, Long ClientRelationshipId, GuarantorType typeEnum, String Entityid, String firstName,
			String LastName, LocalDate dob, String Address1, String Address2, String city, String state, String country,
			String zip, String Phone, String comments, Boolean isActive, Long clientId, Long qualification,
			Long profession,String professionType,String qualificationtype,String relationshipType) 
	{
		return new GurantorData(id,ClientRelationshipId,typeEnum,Entityid,firstName,LastName,dob,Address1,Address2,city,state,country,zip,Phone,comments,isActive,clientId,qualification,profession,
				professionType,qualificationtype,relationshipType);
	}

	public Long getId() {
		return id;
	}

	public Long getClientRelationshipId() {
		return ClientRelationshipId;
	}

	public GuarantorType getTypeEnum() {
		return typeEnum;
	}

	public String getEntityid() {
		return Entityid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return LastName;
	}

	public LocalDate getDob() {
		return dob;
	}

	public String getAddress1() {
		return Address1;
	}

	public String getAddress2() {
		return Address2;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getZip() {
		return zip;
	}

	public String getPhone() {
		return Phone;
	}

	public String getComments() {
		return comments;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public Long getClientId() {
		return clientId;
	}

	public Long getQualification() {
		return qualification;
	}

	public Long getProfession() {
		return profession;
	}
	
	
	
}
