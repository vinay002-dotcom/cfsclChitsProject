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

package org.apache.fineract.portfolio.client.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepository;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientFamilyMembers;
import org.apache.fineract.portfolio.client.domain.ClientFamilyMembersRepository;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.serialization.ClientFamilyMemberCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ClientFamilyMembersWritePlatformServiceImpl implements ClientFamilyMembersWritePlatformService 
{
	
	private final PlatformSecurityContext context;
	private final CodeValueRepository codeValueRepository;
	private final ClientFamilyMembersRepository clientFamilyRepository;
	private final ClientRepositoryWrapper clientRepositoryWrapper;
	private final ClientFamilyMemberCommandFromApiJsonDeserializer  apiJsonDeserializer;
	
	
	@Autowired
	public ClientFamilyMembersWritePlatformServiceImpl(final PlatformSecurityContext context,final CodeValueRepository codeValueRepository,
			final ClientFamilyMembersRepository clientFamilyRepository,final ClientRepositoryWrapper clientRepositoryWrapper,final ClientFamilyMemberCommandFromApiJsonDeserializer  apiJsonDeserializer
			)
	{
		this.context=context;
		this.codeValueRepository=codeValueRepository;
		this.clientFamilyRepository=clientFamilyRepository;
		this.clientRepositoryWrapper=clientRepositoryWrapper;
		this.apiJsonDeserializer=apiJsonDeserializer;
		
	}
	
	

	@Override
	public CommandProcessingResult addFamilyMember(final long clientId,final JsonCommand command) 
	{
		
		Long relationshipId=null;
		CodeValue relationship=null;
		CodeValue maritalStatus=null;
		Long maritalStatusId=null;
		Long genderId=null;
		CodeValue gender=null;
		Long qualificationId=null;
		CodeValue qualification=null;
		Long professionId=null;
		CodeValue profession=null;
		String firstName="";
		String middleName="";
		String lastName="";
		String mobileNumber="";
		Long age=null;
		Boolean isDependent=false;
		Date dateOfBirth=null;
		Boolean isnominee = null;
		Boolean isnomineeaddr = null;
		String nomadhar = null;
		Long nomsecondaryid = null;
		String nomsecondaryidnum = null;
		String nomhouseno = null ;
		String nomstreetno = null ;
		String nomarealocality = null ;
		Long nomtaluka = null;
		Long nomdistrict = null;
		Long nomstate = null;
		Long nompincode = null;
		String nomvillage = null;
		
		
		this.context.authenticatedUser();
		apiJsonDeserializer.validateForCreate(clientId, command.json());
		
		
		Client client=clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
		
		if (command.stringValueOfParameterNamed("firstName") != null) {
			firstName = command.stringValueOfParameterNamed("firstName");
			}
		
		if (command.stringValueOfParameterNamed("middleName") != null) {
			middleName = command.stringValueOfParameterNamed("middleName");
			}
		
		if (command.stringValueOfParameterNamed("lastName") != null) {
			lastName = command.stringValueOfParameterNamed("lastName");
			}
		
		
		if (command.stringValueOfParameterNamed("mobileNumber") != null) {
			mobileNumber = command.stringValueOfParameterNamed("mobileNumber");
			}
		
		
		if (command.longValueOfParameterNamed("age") != null) {
			age = command.longValueOfParameterNamed("age");
			}
		
		if (command.booleanObjectValueOfParameterNamed("isDependent") != null) {
			isDependent = command.booleanObjectValueOfParameterNamed("isDependent");
			}
		
		if (command.longValueOfParameterNamed("relationshipId") != null) {
			relationshipId = command.longValueOfParameterNamed("relationshipId");
			relationship = this.codeValueRepository.getOne(relationshipId);
		}
		
		if (command.longValueOfParameterNamed("maritalStatusId") != null) {
			maritalStatusId = command.longValueOfParameterNamed("maritalStatusId");
			maritalStatus = this.codeValueRepository.getOne(maritalStatusId);
		}

		if (command.longValueOfParameterNamed("genderId") != null) {
			genderId = command.longValueOfParameterNamed("genderId");
			gender = this.codeValueRepository.getOne(genderId);
		}
		
		if (command.longValueOfParameterNamed("qualificationId") != null) {
			qualificationId = command.longValueOfParameterNamed("qualificationId");
			qualification = this.codeValueRepository.getOne(qualificationId);
		}
		
		if (command.longValueOfParameterNamed("professionId") != null) {
			professionId = command.longValueOfParameterNamed("professionId");
			profession = this.codeValueRepository.getOne(professionId);
		}
		
		if(command.dateValueOfParameterNamed("dateOfBirth")!=null)
		{
			dateOfBirth=command.dateValueOfParameterNamed("dateOfBirth");
					
		}
		
		if (command.stringValueOfParameterNamed("nomadhar") != null) {
			nomadhar = command.stringValueOfParameterNamed("nomadhar");
			}
		
		if (command.stringValueOfParameterNamed("nomsecondaryidnum") != null) {
			nomsecondaryidnum = command.stringValueOfParameterNamed("nomsecondaryidnum");
			}
		
		if (command.stringValueOfParameterNamed("nomarealocality") != null) {
			nomarealocality = command.stringValueOfParameterNamed("nomarealocality");
			}
		
		if (command.stringValueOfParameterNamed("nomtaluka") != null) {
			nomtaluka = command.longValueOfParameterNamed("nomtaluka");
			}
		
		if (command.stringValueOfParameterNamed("nomdistrict") != null) {
			nomdistrict = command.longValueOfParameterNamed("nomdistrict");
			}
		if (command.stringValueOfParameterNamed("nomstate") != null) {
			nomstate = command.longValueOfParameterNamed("nomstate");
			}
		if (command.longValueOfParameterNamed("nomsecondaryid") != null) {
			nomsecondaryid = command.longValueOfParameterNamed("nomsecondaryid");
			
		}
		if (command.stringValueOfParameterNamed("nomhouseno") != null) {
			nomhouseno = command.stringValueOfParameterNamed("nomhouseno");
			
		}
		if (command.stringValueOfParameterNamed("nomstreetno") != null) {
			nomstreetno = command.stringValueOfParameterNamed("nomstreetno");
			
		}
		if (command.longValueOfParameterNamed("nompincode") != null) {
			nompincode = command.longValueOfParameterNamed("nompincode");
			
		}
		if (command.booleanObjectValueOfParameterNamed("isnominee") != null) {
			isnominee = command.booleanObjectValueOfParameterNamed("isnominee");
		}
		if (command.booleanObjectValueOfParameterNamed("isnomineeaddr") != null) {
			isnomineeaddr = command.booleanObjectValueOfParameterNamed("isnomineeaddr");
		}
		
		if (command.stringValueOfParameterNamed("nomvillage") != null) {
			nomvillage = command.stringValueOfParameterNamed("nomvillage");
			
		}
	
		ClientFamilyMembers clientFamilyMembers=ClientFamilyMembers.fromJson(client, firstName, middleName, lastName, 
				mobileNumber,age,isDependent, relationship, maritalStatus, gender,qualification, dateOfBirth, profession,isnominee,isnomineeaddr, nomadhar, nomsecondaryid, nomsecondaryidnum, nomhouseno,
                nomstreetno,  nomarealocality ,  nomtaluka ,  nomdistrict ,  nomstate ,  nompincode,nomvillage);
		
		this.clientFamilyRepository.save(clientFamilyMembers);
		
		return new CommandProcessingResultBuilder().withCommandId(command.commandId())
				.withEntityId(clientFamilyMembers.getId()).build();
		
		
	
	}
	
	@Override
	public CommandProcessingResult addClientFamilyMember(final Client client,final JsonCommand command)
	{
		
		Long relationshipId=null;
		CodeValue relationship=null;
		CodeValue maritalStatus=null;
		Long maritalStatusId=null;
		Long genderId=null;
		CodeValue gender=null;
		Long qualificationId=null;
		CodeValue qualification=null;
		Long professionId=null;
		CodeValue profession=null;
		String firstName="";
		String middleName="";
		String lastName="";
		Date dateOfBirth=null;
		String mobileNumber="";
		Long age=null;
		Boolean isDependent=false;
		Boolean isnominee = null;
		Boolean isnomineeaddr = null;
		String nomadhar = null;
		Long nomsecondaryid = null;
		String nomsecondaryidnum = null;
		String nomhouseno = null ;
		String nomstreetno = null ;
		String nomarealocality = null ;
		Long nomtaluka = null;
		Long nomdistrict = null;
		Long nomstate = null;
		Long nompincode = null;
		String nomvillage = null;
		
		this.context.authenticatedUser();
		
		
		//Client client=clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
		
		ClientFamilyMembers familyMember=new ClientFamilyMembers();
		
		//apiJsonDeserializer.validateForCreate(command.json());
							
		
		JsonArray familyMembers=command.arrayOfParameterNamed("familyMembers");
		
		for(JsonElement members :familyMembers)
		{
			
			apiJsonDeserializer.validateForCreate(members.toString());
			
			JsonObject member=members.getAsJsonObject();
			
			
			if (member.get("firstName") != null && !member.get("firstName").isJsonNull()) {
				firstName = member.get("firstName").getAsString();
				}
			
			if (member.get("middleName") != null) {
				middleName = member.get("middleName").getAsString();
				}
			
			if (member.get("lastName") != null) {
				lastName = member.get("lastName").getAsString();
				}
			
			
			if (member.get("mobileNumber") != null && !member.get("mobileNumber").isJsonNull()) {
				mobileNumber = member.get("mobileNumber").getAsString();
				}
			
			
			if (member.get("age") != null && !member.get("age").isJsonNull()) {
				age = member.get("age").getAsLong();
				}
			
			if (member.get("isDependent") != null && !member.get("isDependent").isJsonNull()) {
				isDependent = member.get("isDependent").getAsBoolean();
				}
			
			if (member.get("relationshipId") != null && !member.get("relationshipId").isJsonNull()) {
				relationshipId = member.get("relationshipId").getAsLong();
				relationship = this.codeValueRepository.getOne(relationshipId);
			}
			
			if (member.get("maritalStatusId") != null  && !member.get("maritalStatusId").isJsonNull() ) {
				maritalStatusId = member.get("maritalStatusId").getAsLong();
				maritalStatus = this.codeValueRepository.getOne(maritalStatusId);
			}

			if (member.get("genderId") != null  && !member.get("genderId").isJsonNull()) {
				genderId = member.get("genderId").getAsLong();
				gender = this.codeValueRepository.getOne(genderId);
			}
			
			if (member.get("qualificationId") != null && !member.get("qualificationId").isJsonNull()) {
				qualificationId = member.get("qualificationId").getAsLong();
				qualification = this.codeValueRepository.getOne(qualificationId);
			}
			
			if (member.get("professionId") != null && !member.get("professionId").isJsonNull()) {
				professionId = member.get("professionId").getAsLong();
				profession = this.codeValueRepository.getOne(professionId);
			}
			
			if (member.get("nomadhar") != null && !member.get("nomadhar").isJsonNull()) {
				nomadhar = member.get("nomadhar").getAsString();
				}
			
			if (member.get("nomsecondaryidnum") != null && !member.get("nomsecondaryidnum").isJsonNull()) {
				nomsecondaryidnum = member.get("nomsecondaryidnum").getAsString();
				}
			if (member.get("nomarealocality") != null  && !member.get("nomarealocality").isJsonNull()) {
				nomarealocality = member.get("nomarealocality").getAsString();
				}
			if (member.get("nomtaluka") != null && !member.get("nomtaluka").isJsonNull()) {
				nomtaluka = member.get("nomtaluka").getAsLong();
				}
			if (member.get("nomdistrict") != null && !member.get("nomdistrict").isJsonNull()) {
				nomdistrict = member.get("nomdistrict").getAsLong();
				}
			if (member.get("nomstate") != null && !member.get("nomstate").isJsonNull()) {
				nomstate = member.get("nomstate").getAsLong();
				}
			if (member.get("nomsecondaryidnum") != null && !member.get("nomsecondaryidnum").isJsonNull()) {
				nomsecondaryidnum = member.get("nomsecondaryidnum").getAsString();
				}
			if (member.get("nomsecondaryid") != null && !member.get("nomsecondaryid").isJsonNull()) {
				nomsecondaryid = member.get("nomsecondaryid").getAsLong();		
			}
			if (member.get("nomhouseno") != null && !member.get("nomhouseno").isJsonNull()) {
				nomhouseno = member.get("nomhouseno").getAsString();		
			}
			if (member.get("nomstreetno") != null  && !member.get("nomstreetno").isJsonNull()) {
				nomstreetno = member.get("nomstreetno").getAsString();		
			}
			if (member.get("nompincode") != null && !member.get("nompincode").isJsonNull())  {
				nompincode = member.get("nompincode").getAsLong();		
			}
			if (member.get("isnominee") != null && !member.get("isnominee").isJsonNull()) {
				isnominee = member.get("isnominee").getAsBoolean();
			}
			if (member.get("isnomineeaddr") != null && !member.get("isnomineeaddr").isJsonNull()) {
				isnomineeaddr = member.get("isnomineeaddr").getAsBoolean();
			}
			
			if(member.get("dateOfBirth")!=null && !member.get("dateOfBirth").isJsonNull())
			{
				
				DateFormat format = new SimpleDateFormat(member.get("dateFormat").getAsString());
				Date date;
				try {
					date = format.parse(member.get("dateOfBirth").getAsString());
					dateOfBirth=date;
				} catch (ParseException e) {
					//e.printStackTrace();
				}
				
				
				
		/*	this.fromApiJsonHelper.extractDateFormatParameter(member.get("dateOfBirth").getAsJsonObject());*/
				
						
			}
			if (member.get("nomvillage") != null && !member.get("nomvillage").isJsonNull()) {
				nomvillage = member.get("nomvillage").getAsString();		
			}
			
			familyMember=ClientFamilyMembers.fromJson(client, firstName, middleName, lastName, mobileNumber,age,
					isDependent, relationship, maritalStatus, gender,qualification, dateOfBirth, profession,isnominee,isnomineeaddr, nomadhar, nomsecondaryid, nomsecondaryidnum, nomhouseno,
	                nomstreetno,  nomarealocality ,  nomtaluka ,  nomdistrict ,  nomstate ,  nompincode,nomvillage);
			
			this.clientFamilyRepository.save(familyMember);	
			
		}
		
		
		
		return new CommandProcessingResultBuilder().withCommandId(command.commandId())
				.withEntityId(familyMember.getId()).build();
		
		
	}



	@Override
	public CommandProcessingResult updateFamilyMember(Long familyMemberId, JsonCommand command) {
		
		
		Long relationshipId=null;
		CodeValue relationship=null;
		CodeValue maritalStatus=null;
		Long maritalStatusId=null;
		Long genderId=null;
		CodeValue gender=null;
		Long qualificationId=null;
		CodeValue qualification=null;
		Long professionId=null;
		CodeValue profession=null;
		String firstName="";
		String middleName="";
		String lastName="";
		Date dateOfBirth=null;
		String mobileNumber="";
		Long age=null;
	
		Boolean isDependent=false;
		Boolean isnominee = false;
		Boolean isnomineeaddr = false;
		String nomadhar = null;
		Long nomsecondaryid = null;
		String nomsecondaryidnum = null;
		String nomhouseno = null ;
		String nomstreetno = null ;
		String nomarealocality = null ;
		Long nomtaluka = null;
		Long nomdistrict = null;
		Long nomstate = null;
		Long nompincode = null;
		String nomvillage = null;
		//long clientFamilyMemberId=0;
		
		
		this.context.authenticatedUser();
		
		apiJsonDeserializer.validateForUpdate(familyMemberId, command.json());
		
		/*if (command.stringValueOfParameterNamed("clientFamilyMemberId") != null) {
			clientFamilyMemberId = command.longValueOfParameterNamed("clientFamilyMemberId");
			}*/
		
		
		ClientFamilyMembers clientFamilyMember=clientFamilyRepository.getOne(familyMemberId);
				
		//Client client=clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
		
		if (command.stringValueOfParameterNamed("firstName") != null) {
			firstName = command.stringValueOfParameterNamed("firstName");
			clientFamilyMember.setFirstName(firstName);
			}
		
		if (command.stringValueOfParameterNamed("middleName") != null) {
			middleName = command.stringValueOfParameterNamed("middleName");
			clientFamilyMember.setMiddleName(middleName);
			}
		
		if (command.stringValueOfParameterNamed("lastName") != null) {
			lastName = command.stringValueOfParameterNamed("lastName");
			clientFamilyMember.setLastName(lastName);
			}
		
		if (command.stringValueOfParameterNamed("mobileNumber") != null) {
			mobileNumber = command.stringValueOfParameterNamed("mobileNumber");
			clientFamilyMember.setMobileNumber(mobileNumber);
			}
		
		
		if (command.longValueOfParameterNamed("age") != null) {
			age = command.longValueOfParameterNamed("age");
			clientFamilyMember.setAge(age);
			}
		
		if (command.booleanObjectValueOfParameterNamed("isDependent") != null) {
			isDependent = command.booleanObjectValueOfParameterNamed("isDependent");
			clientFamilyMember.setIsDependent(isDependent);
			}
		
		if (command.longValueOfParameterNamed("relationshipId") != null) {
			relationshipId = command.longValueOfParameterNamed("relationshipId");
			relationship = this.codeValueRepository.getOne(relationshipId);
			clientFamilyMember.setRelationship(relationship);
		}
		
		if (command.longValueOfParameterNamed("maritalStatusId") != null) {
			maritalStatusId = command.longValueOfParameterNamed("maritalStatusId");
			maritalStatus = this.codeValueRepository.getOne(maritalStatusId);
			clientFamilyMember.setMaritalStatus(maritalStatus);
		}

		if (command.longValueOfParameterNamed("genderId") != null) {
			genderId = command.longValueOfParameterNamed("genderId");
			gender = this.codeValueRepository.getOne(genderId);
			clientFamilyMember.setGender(gender);
		}
		
		if (command.longValueOfParameterNamed("qualificationId") != null) {
			qualificationId = command.longValueOfParameterNamed("qualificationId");
			qualification = this.codeValueRepository.getOne(qualificationId);
			clientFamilyMember.setQualification(qualification);
		}
		
		if (command.longValueOfParameterNamed("professionId") != null) {
			professionId = command.longValueOfParameterNamed("professionId");
			profession = this.codeValueRepository.getOne(professionId);
			clientFamilyMember.setProfession(profession);
		}
		
		if(command.dateValueOfParameterNamed("dateOfBirth")!=null)
		{
			dateOfBirth=command.dateValueOfParameterNamed("dateOfBirth");
			clientFamilyMember.setDateOfBirth(dateOfBirth);
			
					
		}
		
		if (command.stringValueOfParameterNamed("nomadhar") != null) {
			nomadhar = command.stringValueOfParameterNamed("nomadhar");
			clientFamilyMember.setNomadhar(nomadhar);
			}
		
		if (command.stringValueOfParameterNamed("nomsecondaryidnum") != null) {
			nomsecondaryidnum = command.stringValueOfParameterNamed("nomsecondaryidnum");
			clientFamilyMember.setNomsecondaryidnum(nomsecondaryidnum);
			}
		
		if (command.stringValueOfParameterNamed("nomarealocality") != null) {
			nomarealocality = command.stringValueOfParameterNamed("nomarealocality");
			clientFamilyMember.setNomarealocality(nomarealocality);
			}
		
		if (command.stringValueOfParameterNamed("nomtaluka") != null) {
			nomtaluka = command.longValueOfParameterNamed("nomtaluka");
			clientFamilyMember.setNomtaluka(nomtaluka);
			}
		
		if (command.stringValueOfParameterNamed("nomdistrict") != null) {
			nomdistrict = command.longValueOfParameterNamed("nomdistrict");
			clientFamilyMember.setNomdistrict(nomdistrict);
			}
		if (command.stringValueOfParameterNamed("nomstate") != null) {
			nomstate = command.longValueOfParameterNamed("nomstate");
			clientFamilyMember.setNomstate(nomstate);
			}
		if (command.longValueOfParameterNamed("nomsecondaryid") != null) {
			nomsecondaryid = command.longValueOfParameterNamed("nomsecondaryid");
			clientFamilyMember.setNomsecondaryid(nomsecondaryid);
			
		}
		if (command.stringValueOfParameterNamed("nomhouseno") != null) {
			nomhouseno = command.stringValueOfParameterNamed("nomhouseno");
			clientFamilyMember.setNomhouseno(nomhouseno);
			
		}
		if (command.stringValueOfParameterNamed("nomstreetno") != null) {
			nomstreetno = command.stringValueOfParameterNamed("nomstreetno");
			clientFamilyMember.setNomstreetno(nomstreetno);
			
			
		}
		
		if (command.stringValueOfParameterNamed("nomvillage") != null) {
			nomvillage = command.stringValueOfParameterNamed("nomvillage");
			clientFamilyMember.setNomvillage(nomvillage);
			
			
		}
		if (command.longValueOfParameterNamed("nompincode") != null) {
			nompincode = command.longValueOfParameterNamed("nompincode");
			clientFamilyMember.setNompincode(nompincode);
			
		}
		if (command.booleanObjectValueOfParameterNamed("isnomineeaddr") != null) {
			isnomineeaddr = command.booleanObjectValueOfParameterNamed("isnomineeaddr");
			clientFamilyMember.setIsnomineeaddr(isnomineeaddr);
		}
		if (command.booleanObjectValueOfParameterNamed("isnominee") != null) {
			isnominee = command.booleanObjectValueOfParameterNamed("isnominee");
			clientFamilyMember.setIsnominee(isnominee);
			// if not nominee resetting values which were non-string fields.
			if (!isnominee){
				clientFamilyMember.setIsnomineeaddr(false);
				clientFamilyMember.setNompincode(null);
				clientFamilyMember.setDateOfBirth(null);
				clientFamilyMember.setNomsecondaryid(null);
			}
		}

		//ClientFamilyMembers clientFamilyMembers=ClientFamilyMembers.fromJson(client, firstName, middleName, lastName, qualification, relationship, maritalStatus, gender, dateOfBirth, profession);
		
		this.clientFamilyRepository.save(clientFamilyMember);
		
		return new CommandProcessingResultBuilder().withCommandId(command.commandId())
				.withEntityId(clientFamilyMember.getId()).build();
	}



	@Override
	public CommandProcessingResult deleteFamilyMember(Long clientFamilyMemberId, JsonCommand command) {
		// TODO Auto-generated method stub
		
		this.context.authenticatedUser();
		
		apiJsonDeserializer.validateForDelete(clientFamilyMemberId);
		
		ClientFamilyMembers clientFamilyMember=null;
		
		
		
		if(clientFamilyMemberId!=null)
		{
			 clientFamilyMember=clientFamilyRepository.getOne(clientFamilyMemberId);
			clientFamilyRepository.delete(clientFamilyMember);
			
		}
		
		
		if(clientFamilyMember!=null)
		{
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(clientFamilyMember.getId()).build();	
		}
		else
		{
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(Long.valueOf(clientFamilyMemberId)).build();	
		}
		
	}
}
