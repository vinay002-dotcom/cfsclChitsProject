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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_family_members")
public class ClientFamilyMembers extends AbstractPersistableCustom {
	
	@ManyToOne
	@JoinColumn(name="client_id")
	private Client client;
	
	@Column(name="firstname")
	private String firstName;
	
	@Column(name="middleName")
	private String middleName;
	
	@Column(name="lastName")
	private String lastName;
	
	@Column(name="mobile_number")
	private String mobileNumber;
	
	@Column(name="age")
	private Long age;
	
	@Column(name="is_dependent")
	private Boolean isDependent;
	
	@Column(name = "is_nominee")
	private Boolean isnominee ;
	
	@Column(name = "is_nominee_addr")
	private Boolean isnomineeaddr ;
	
	@Column(name = "nom_adhar")
	private String nomadhar;
	
	@Column(name = "nom_secondary_id")
	private Long nomsecondaryid;
	
	@Column(name = "nom_house_no")
	private String nomhouseno ;
	
	@Column(name = "nom_street_no")
	private String nomstreetno ;
	
	@Column(name = "nom_area_locality")
	private String nomarealocality;
	
	@Column(name = "nom_taluka")
	private Long nomtaluka;
	
	@Column(name = "nom_district")
	private Long nomdistrict;
	
	@Column(name = "nom_state")
	private Long nomstate ;
	
	@Column(name = "nom_pincode")
	private Long nompincode;
	
	@Column(name = "nom_secondary_id_num")
	private String nomsecondaryidnum;
	
	@ManyToOne
	@JoinColumn(name = "relationship_cv_id")
	private CodeValue relationship;
	
	@ManyToOne
	@JoinColumn(name = "marital_status_cv_id")
	private CodeValue maritalStatus;
	
	@ManyToOne
	@JoinColumn(name = "gender_cv_id")
	private CodeValue gender;
	
	@ManyToOne
	@JoinColumn(name = "qualification_cv_id")
	private CodeValue qualification;
	
	@ManyToOne
	@JoinColumn(name = "profession_cv_id")
	private CodeValue profession;
	
	 @Column(name = "date_of_birth", nullable = true)
	 @Temporal(TemporalType.DATE)
	 private Date dateOfBirth;
	 
	 @Column(name = "nom_village")
	 private String nomvillage;
	
		private ClientFamilyMembers(final Client client,final String firstName,
				final String middleName,final String lastName,
				final String mobileNumber,final Long age,final Boolean isDependent,
				final CodeValue relationship,final CodeValue maritalStatus,final CodeValue gender,
				final CodeValue qualification,final Date dateOfBirth,final CodeValue profession,final Boolean isnominee, final Boolean isnomineeaddr, final String nomadhar, final Long nomsecondaryid, final String nomsecondaryidnum, final String nomhouseno,
	            final String nomstreetno, final String nomarealocality , final Long nomtaluka , final Long nomdistrict , final Long nomstate , final Long nompincode,final String nomvillage)
		{
			
			this.client=client;
			this.firstName=firstName;
			this.middleName=middleName;
			this.lastName=lastName;
			this.age=age;
			this.mobileNumber=mobileNumber;
			this.isDependent=isDependent;
			this.relationship=relationship;
			this.maritalStatus=maritalStatus;
			this.gender=gender;
			this.qualification=qualification;
			this.dateOfBirth=dateOfBirth;
			this.profession=profession;
			this.nomadhar = nomadhar;
			this.nomarealocality = nomarealocality;
			this.nomdistrict = nomdistrict;
			this.nomhouseno = nomhouseno;
			this.nompincode = nompincode;
			this.nomsecondaryid = nomsecondaryid;
			this.nomsecondaryidnum = nomsecondaryidnum;
			this.nomstate = nomstate;
			this.nomstreetno = nomstreetno;
			this.nomtaluka = nomtaluka;
			this.isnominee = isnominee;
			this.isnomineeaddr=isnomineeaddr;
			this.nomvillage = nomvillage;
		}
		
		
		public ClientFamilyMembers()
		{
			
		}
		
		public static ClientFamilyMembers fromJson(final Client client,final String firstName,
				final String middleName,final String lastName,
				final String mobileNumber,final Long age,final Boolean isDependent,
				final CodeValue relationship,final CodeValue maritalStatus,final CodeValue gender,
				final CodeValue qualification,final Date dateOfBirth,final CodeValue profession,final Boolean isnominee, final Boolean isnomineeaddr, final String nomadhar, final Long nomsecondaryid, final String nomsecondaryidnum, final String nomhouseno,
	            final String nomstreetno, final String nomarealocality , final Long nomtaluka , final Long nomdistrict , final Long nomstate , final Long nompincode,final String nomvillage)
		{
			return new ClientFamilyMembers(client,firstName,middleName,lastName,
					mobileNumber,age,isDependent,relationship,maritalStatus,gender,qualification,
					dateOfBirth,profession,isnominee,isnomineeaddr, nomadhar,nomsecondaryid,nomsecondaryidnum,nomhouseno,
		            nomstreetno, nomarealocality ,nomtaluka ,  nomdistrict , nomstate , nompincode,nomvillage);
		}

		public String getNomvillage() {
			return nomvillage;
		}


		public void setNomvillage(String nomvillage) {
			this.nomvillage = nomvillage;
		}


		public Boolean getIsnominee() {
			return isnominee;
		}


		public void setIsnominee(Boolean isnominee) {
			this.isnominee = isnominee;
		}


		public Boolean getIsnomineeaddr() {
			return isnomineeaddr;
		}


		public void setIsnomineeaddr(Boolean isnomineeaddr) {
			this.isnomineeaddr = isnomineeaddr;
		}


		public String getNomadhar() {
			return nomadhar;
		}


		public void setNomadhar(String nomadhar) {
			this.nomadhar = nomadhar;
		}


		public Long getNomsecondaryid() {
			return nomsecondaryid;
		}


		public void setNomsecondaryid(Long nomsecondaryid) {
			this.nomsecondaryid = nomsecondaryid;
		}


		public String getNomhouseno() {
			return nomhouseno;
		}


		public void setNomhouseno(String nomhouseno) {
			this.nomhouseno = nomhouseno;
		}


		public String getNomstreetno() {
			return nomstreetno;
		}


		public void setNomstreetno(String nomstreetno) {
			this.nomstreetno = nomstreetno;
		}


		public String getNomarealocality() {
			return nomarealocality;
		}


		public void setNomarealocality(String nomarealocality) {
			this.nomarealocality = nomarealocality;
		}


		public Long getNomtaluka() {
			return nomtaluka;
		}


		public void setNomtaluka(Long nomtaluka) {
			this.nomtaluka = nomtaluka;
		}


		public Long getNomdistrict() {
			return nomdistrict;
		}


		public void setNomdistrict(Long nomdistrict) {
			this.nomdistrict = nomdistrict;
		}


		public Long getNomstate() {
			return nomstate;
		}


		public void setNomstate(Long nomstate) {
			this.nomstate = nomstate;
		}


		public Long getNompincode() {
			return nompincode;
		}


		public void setNompincode(Long nompincode) {
			this.nompincode = nompincode;
		}


		public String getNomsecondaryidnum() {
			return nomsecondaryidnum;
		}


		public void setNomsecondaryidnum(String nomsecondaryidnum) {
			this.nomsecondaryidnum = nomsecondaryidnum;
		}


		public Client getClient() {
			return this.client;
		}

		public void setClient(Client client) {
			this.client = client;
		}

		public String getFirstName() {
			return this.firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getMiddleName() {
			return this.middleName;
		}

		public void setMiddleName(String middleName) {
			this.middleName = middleName;
		}

		public String getLastName() {
			return this.lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		
		public CodeValue getRelationship() {
			return this.relationship;
		}

		public void setRelationship(CodeValue relationship) {
			this.relationship = relationship;
		}

		public CodeValue getMaritalStatus() {
			return this.maritalStatus;
		}

		public void setMaritalStatus(CodeValue maritalStatus) {
			this.maritalStatus = maritalStatus;
		}

		public CodeValue getGender() {
			return this.gender;
		}

		public void setGender(CodeValue gender) {
			this.gender = gender;
		}
		
		public CodeValue getQualification() {
			return this.qualification;
		}

		public void setQualification(CodeValue qualification) {
			this.qualification = qualification;
		}

		public CodeValue getProfession() {
			return this.profession;
		}

		public void setProfession(CodeValue profession) {
			this.profession = profession;
		}

		public Date getDateOfBirth() {
			return this.dateOfBirth;
		}

		public void setDateOfBirth(Date dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}


		public String getMobileNumber() {
			return this.mobileNumber;
		}


		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}


		public Long getAge() {
			return this.age;
		}


		public void setAge(Long age) {
			this.age = age;
		}


		public Boolean getIsDependent() {
			return this.isDependent;
		}


		public void setIsDependent(Boolean isDependent) {
			this.isDependent = isDependent;
		}


		
		
}
