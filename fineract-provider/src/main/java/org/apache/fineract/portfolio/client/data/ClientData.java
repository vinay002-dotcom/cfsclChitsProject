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

import java.util.Collection;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.dataqueries.data.DatatableData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.address.data.AddressData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;


import java.time.LocalDate;


/**
 * Immutable data object representing client data.
 */
final public class ClientData implements Comparable<ClientData> {

    private final Long id;
    private final String accountNo;
    private final String externalId;
    private final String externalIdd;

    private final EnumOptionData status;
    private final CodeValueData subStatus;

    @SuppressWarnings("unused")
    private final Boolean active;
    private final LocalDate activationDate;

    private final String firstName;
    private final String spousename;
    private final String lastName;
    
    private final String fsFirstName;
    private final String fsMiddleName;
    private final String fsLastName;
    
    private final String maidenName;
    private final String custmotherName;

    private final String alternateMobileNo;
    private final String secIdProofNo;
    private final String secaddressproofno;
    private final String lastverifiedmobile;
    private final String otherexpensestf ;
    private final String othersrcinctf;
    private final String otherobligations;
    private final String lastverifiedsecondaryid;
    private final String lastverifiedadhar;

    private final String adhar;
    private final String nrega;
    private final String pan;
    
    
    private final String fullname;
    private final String displayName;
    private final String mobileNo;
    private final String gstNo;
    private final String age;
    private final String nomrelationshipid;
    private final String nomgenderid;
    private final String nomage;
    private final String nomprofessionid;
    private final String nomeducationalid;
    private final String nommaritalid;
    private final String incdailysales;
    private final String exprawmaterial;
    private final String expstaffsal;
    private final String exppowertelephone;
    private final String exprepairsmaintainance;
    private final String expcommbrokerage;
    private final String incrent;
    private final String incinterest;
    private final String incothers;
    private final String tothouseholdinc;
    private final String exphousehold;
    private final String expotherloans;  
    private final String totnetdispfamily;
 
    private final String expinterest;
    private final String expofficerent;
    private final String exptravel;
    private final String expothers;
    private final String totbusinessprofit;
    private final String incspouse;
    private final String idproofNo;
    private final String addrproofNo;
	private final String emailAddress;
    private final LocalDate dateOfBirth;
    private final LocalDate lastverifiedSecondaryidDate;
    private final LocalDate lastverifiedmobiledate;
    private final LocalDate lastverifiedadhardate;
    private final CodeValueData gender;
    private final CodeValueData fatherspouse;
    private final CodeValueData education;
    private final CodeValueData marital;
    private final CodeValueData profession;
    private final CodeValueData belonging;
    private final CodeValueData annual;
    private final CodeValueData land;
    private final CodeValueData house;
    private final CodeValueData form;
    private final CodeValueData title;
    private final CodeValueData religion;
    private final CodeValueData alternateNo;
    private final CodeValueData idproof;
    private final CodeValueData addrproof;
    private final CodeValueData clientType;
    private final CodeValueData clientClassification;
	private final Boolean isStaff;
	//private final LocalDate submittedOnDate1;
    private final Long officeId;
    private final String officeName;
    private final Long transferToOfficeId;
    private final String transferToOfficeName;

    private final Long imageId;
    private final Boolean imagePresent;
    private final Long staffId;
    private final String staffName;
    private final ClientTimelineData timeline;

    private final Long savingsProductId;
    private final String savingsProductName;
    //private final Long group;
    //private final Long center;

    private final Long savingsAccountId;
    private final EnumOptionData legalForm;

    // associations
    private final Collection<GroupGeneralData> groups;
    //private final Collection<CenterData> centers;

    // template
    private final Collection<OfficeData> officeOptions;
    private final Collection<StaffData> staffOptions;
    private final Collection<CodeValueData> narrations;
    private final Collection<SavingsProductData> savingProductOptions;
    private final Collection<SavingsAccountData> savingAccountOptions;
    private final Collection<CodeValueData> genderOptions;
  
    private final Collection<CodeValueData> fatherspouseOptions;
    private final Collection<CodeValueData> educationOptions;
    private final Collection<CodeValueData> maritalOptions;
    private final Collection<CodeValueData> professionOptions;
    private final Collection<CodeValueData> belongingOptions;
    private final Collection<CodeValueData> annualOptions;
    private final Collection<CodeValueData> altMobNumOfOptions;
    private final Collection<CodeValueData> houseOptions;
    private final Collection<CodeValueData> formOptions;
    private final Collection<CodeValueData> titleOptions;
    private final Collection<CodeValueData> religionOptions;
    private final Collection<CodeValueData> idproofOptions;
    private final Collection<CodeValueData> addrproofOptions;
    private final Collection<CodeValueData> clientTypeOptions;
    private final Collection<CodeValueData> clientClassificationOptions;    
    private final Collection<CodeValueData> clientNonPersonConstitutionOptions;
    private final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions;
    private final List<EnumOptionData> clientLegalFormOptions;
    private final ClientFamilyMembersData familyMemberOptions;
    
    private final ClientNonPersonData clientNonPersonDetails;
    
    private final AddressData address;

	private final Boolean isAddressEnabled;

    private final List<DatatableData> datatables;

    //import fields
    private transient Integer rowIndex;
    private String dateFormat;
    private String locale;
    private Long clientTypeId;
    private Long genderId;
    private Long fatherspouseId;
    private Long educationId;
    private Long maritalId;
    private Long professionId;
    private Long belongingId;
    private Long annualId;
    private Long landId;
    private Long houseId;
    private Long formId;
    private Long titleId;
    private Long religionId;
    private Long alternateNoId;
    private Long idproofId;
    private Long addrproofId;
    private Long clientClassificationId;
    private Long legalFormId;
    private LocalDate submittedOnDate;
   private Long debt;
   private Long income;
   private Float debtincratio;
    //private String aadhaarNo;
    //private String alternateNo;
   private Long AmountApplied;
   private Boolean cpvData;

    public static ClientData importClientEntityInstance(Long legalFormId,Integer rowIndex,String fullname,Long officeId, Long clientTypeId, 
            Long clientClassificationId, Long staffId,Boolean active,LocalDate activationDate,LocalDate submittedOnDate,
            String externalId,String externalIdd,LocalDate dateOfBirth,LocalDate lastverifiedSecondaryidDate,LocalDate lastverifiedmobiledate,LocalDate lastverifiedadhardate,String mobileNo,String gstNo,String age,String nomrelationshipid,
             String nomgenderid,
             String nomage,
             String nomprofessionid,
             String nomeducationalid,
             String nommaritalid, String incdailysales,String exprawmaterial,String expstaffsal,String exppowertelephone,String exprepairsmaintainance,String expcommbrokerage,
            String incrent,String incinterest,String incothers,String tothouseholdinc,String exphousehold,String expotherloans,String totnetdispfamily,String expinterest,String expofficerent,
            String exptravel,String expothers,String totbusinessprofit,String incspouse,String idproofNo,
            String addrproofNo, ClientNonPersonData clientNonPersonDetails,
            AddressData address,String locale,String dateFormat,Long debt,Long income,Float debtincratio, Long AmountApplied,Boolean cpvData){
        return  new ClientData(legalFormId,rowIndex,fullname, null,null,null,null,null,null,null, null, null,null, null, null,null,null, null, null, null, null, null,null, activationDate,submittedOnDate,active, externalId, externalIdd,
        		officeId, staffId, mobileNo,gstNo,age,nomrelationshipid,nomgenderid,nomage,nomprofessionid,nomeducationalid,
        		nommaritalid,incdailysales,exprawmaterial,expstaffsal, exppowertelephone,exprepairsmaintainance,expcommbrokerage,incrent,incinterest,incothers,tothouseholdinc,exphousehold,expotherloans,totnetdispfamily,expinterest,expofficerent,exptravel,expothers,totbusinessprofit,incspouse,idproofNo,addrproofNo, dateOfBirth,lastverifiedSecondaryidDate, lastverifiedmobiledate,lastverifiedadhardate,clientTypeId, null,clientClassificationId,null,null,
        		null, null, null, null,null, null,null, null,null, null, null, null, null, null, null, null, null, debt, income, debtincratio,AmountApplied,cpvData);
    }

    public Long getAmountApplied() {
		return AmountApplied;
	}

	public void setAmountApplied(Long AmountApplied) {
		this.AmountApplied = AmountApplied;
	}

	public String getLastverifiedadhar() {
		return lastverifiedadhar;
	}

	public static ClientData importClientPersonInstance(Long legalFormId,Integer rowIndex,String firstName,String lastName,String spousename,
    		String fsFirstName,String fsLastName, String fsMiddleName,String maidenName,String custmotherName, String alternateMobileNo, String secIdProofNo, String secaddressproofno,String lastverifiedmobile,String otherexpensestf,String othersrcinctf,String otherobligations,String lastverifiedsecondaryid,String lastverifiedadhar, String adhar,String nrega,String pan,
            LocalDate submittedOn,LocalDate activationDate,Boolean active,String externalId,String externalIdd,Long officeId,
            Long staffId, String mobileNo,String gstNo,String age,String nomrelationshipid,
            String nomgenderid,
            String nomage,
            String nomprofessionid,
            String nomeducationalid,
            String nommaritalid,String incdailysales,String exprawmaterial,String expstaffsal,String exppowertelephone,String exprepairsmaintainance,String expcommbrokerage,String incrent,String incinterest,String incothers,String tothouseholdinc,String exphousehold,String expotherloans,String totnetdispfamily,String expinterest,String expofficerent,
            String exptravel,String expothers,String totbusinessprofit,String incspouse,String idproofNo,String addrproofNo, LocalDate dob,LocalDate nomdob,LocalDate lastverifiedmobiledate,LocalDate lastverifiedadhardate,Long clientTypeId,Long genderId, Long fatherspouseId, Long educationId, Long maritalId, Long professionId, Long belongingId, 
            Long annualId, Long landId, Long houseId, Long formId, Long titleId, Long religionId, Long alternateNoId, Long idproofId,Long addrproofId,
            Long clientClassificationId, Boolean isStaff, AddressData address,String locale,String dateFormat,Long debt,Long income,Float debtincratio,Long AmountApplied,Boolean cpvData){

        return new ClientData(legalFormId,rowIndex, null, firstName,lastName,spousename,
        		fsFirstName,fsLastName,fsMiddleName,maidenName, custmotherName, alternateMobileNo, secIdProofNo, secaddressproofno,lastverifiedmobile,otherexpensestf,othersrcinctf,otherobligations,lastverifiedsecondaryid,lastverifiedadhar, adhar,nrega,pan,
        		submittedOn,activationDate,active,externalId, externalIdd,
                officeId,staffId, mobileNo,gstNo, age,nomrelationshipid,nomgenderid,nomage,nomprofessionid,nomeducationalid,
        		nommaritalid,incdailysales,exprawmaterial,expstaffsal, exppowertelephone,exprepairsmaintainance,expcommbrokerage,incrent,incinterest,incothers,tothouseholdinc,exphousehold,expotherloans,totnetdispfamily,expinterest,expofficerent,exptravel,expothers,totbusinessprofit,incspouse, idproofNo,addrproofNo,dob,nomdob,lastverifiedmobiledate,lastverifiedadhardate,clientTypeId, 
                genderId,fatherspouseId, educationId, maritalId, professionId, 
                belongingId, annualId, landId, houseId, formId, titleId, religionId, alternateNoId, idproofId, addrproofId,
                clientClassificationId, isStaff,address, null, locale,dateFormat,debt,income,debtincratio,AmountApplied,cpvData);
    }
    
    
    public static ClientData emptyInstance(Long clientId) {
    		return lookup(clientId, null, null, null);
    }

    private ClientData(Long legalFormId,Integer rowIndex, String fullname, String firstName,String lastName,String spousename,
    		String fsFirstName,String fsLastName,String fsMiddleName,String maidenName,String custmotherName,String alternateMobileNo, String secIdProofNo, String secaddressproofno,String lastverifiedmobile,String otherexpensestf,String othersrcinctf,String otherobligations,String lastverifiedsecondaryid, String lastverifiedadhar,String adhar,String nrega,String pan,
            LocalDate submittedOn,LocalDate activationDate,Boolean active,String externalId,String externalIdd,Long officeId,
            Long staffId, String mobileNo,String gstNo,String age,String nomrelationshipid,
            String nomgenderid,
            String nomage,
            String nomprofessionid,
            String nomeducationalid,
            String nommaritalid,String incdailysales,String exprawmaterial,String expstaffsal,String exppowertelephone,String exprepairsmaintainance,String expcommbrokerage,String incrent,String incinterest,String incothers,String tothouseholdinc,String exphousehold,String expotherloans,String totnetdispfamily,String expinterest,String expofficerent,
            String exptravel,String expothers,String totbusinessprofit,String incspouse,String idproofNo,String addrproofNo, LocalDate dob,LocalDate nomdob,LocalDate lastverifiedmobiledate,LocalDate lastverifiedadhardate,
            Long clientTypeId,  Long genderId,Long fatherspouseId, Long educationId,Long maritalId, Long professionId, Long belongingId,
            Long annualId,  Long landId,Long houseId, Long formId, Long titleId, Long religionId, Long alternateNoId, Long idproofId, Long addrproofId, 
            Long clientClassificationId,Boolean isStaff, AddressData address, ClientNonPersonData clientNonPersonDetails,
            String locale,String dateFormat,Long debt,Long income,Float debtincratio,Long AmountApplied,Boolean cpvData) {
        this.rowIndex=rowIndex;
        this.dateFormat=dateFormat;
        this.locale= locale;
        this.firstName = firstName;
        this.lastName = lastName;
        this.spousename = spousename;
        this.fsFirstName = fsFirstName;
        this.fsLastName = fsLastName;
        this.fsMiddleName = fsMiddleName;
        this.maidenName = maidenName;
        this.custmotherName = custmotherName;
        this.alternateMobileNo = alternateMobileNo;
        this.secIdProofNo = secIdProofNo;
        this.secaddressproofno = secaddressproofno;
        this.lastverifiedmobile=lastverifiedmobile;
        this.otherexpensestf = otherexpensestf;
        this.othersrcinctf = othersrcinctf;
        this.otherobligations=otherobligations;
        this.lastverifiedsecondaryid=lastverifiedsecondaryid;
        this.lastverifiedadhar = lastverifiedadhar;
        this.adhar = adhar;
        this.nrega = nrega;
        this.pan = pan;
        this.fullname = fullname;
        this.activationDate=activationDate;
        this.submittedOnDate=submittedOn;
      
        this.active=active;
        this.externalId=externalId;
        this.externalIdd=externalIdd;
        this.officeId=officeId;
        this.staffId=staffId;
        this.legalFormId=legalFormId;
        this.mobileNo=mobileNo;
        this.gstNo=gstNo;
        this.age=age;
        this.nomeducationalid=nomeducationalid;
        this.nomgenderid=nomgenderid;
        this.nommaritalid=nommaritalid;
        this.nomprofessionid=nomprofessionid;
        this.nomage=nomage;
        this.nomrelationshipid=nomrelationshipid;
        this.incdailysales=incdailysales;
        this.exprawmaterial=exprawmaterial;
        this.expstaffsal=expstaffsal;
        this.exppowertelephone=exppowertelephone;
        this.exprepairsmaintainance=exprepairsmaintainance;
        this.expcommbrokerage=expcommbrokerage;
        this.incrent=incrent;
        this.incinterest=incinterest;
        this.incothers=incothers;
        this.tothouseholdinc=tothouseholdinc;
        this.exphousehold=exphousehold;
        this.expotherloans=expotherloans;
        this.totnetdispfamily=totnetdispfamily;
        this.idproofNo=idproofNo;
        this.addrproofNo=addrproofNo;
        this.expinterest=expinterest;
        this.expofficerent=expofficerent;
        this.exptravel=exptravel;
        this.expothers=expothers;
        this.totbusinessprofit=totbusinessprofit;
        this.incspouse=incspouse;
        //this.alternateNo = alternateNo;
        this.dateOfBirth=dob;
       // this.submittedOnDate=submittedOnDate;
        this.lastverifiedSecondaryidDate = nomdob;
        this.lastverifiedmobiledate =lastverifiedmobiledate;
        this.lastverifiedadhardate = lastverifiedadhardate;
        this.clientTypeId=clientTypeId;
        this.genderId=genderId;
        this.fatherspouseId = fatherspouseId;
        this.educationId = educationId;
        this.maritalId=maritalId;
        this.professionId = professionId;
        this.belongingId=belongingId;
        this.annualId = annualId;
        this.landId = landId;
        this.houseId=houseId;
        this.formId = formId;
        this.titleId=titleId;
        this.religionId = religionId;
        this.alternateNoId = alternateNoId;
        this.idproofId=idproofId;
        this.addrproofId=addrproofId;
        this.clientClassificationId=clientClassificationId;
        this.isStaff=isStaff;
        this.address=address;
        this.id = null;
        this.accountNo = null;
        this.status = null;
        this.subStatus = null;
        this.displayName = null;
        this.gender = null;
        this.fatherspouse = null;
        this.education = null;
        this.marital = null;
        this.profession = null;
        this.belonging = null;
        this.annual = null;
        this.land = null;
        this.house = null;
        this.form = null;
        this.title = null;
        this.religion = null;
        this.alternateNo = null;
        this.idproof = null;
        this.addrproof = null;
        this.clientType = null;
        this.clientClassification = null;
        this.officeName = null;
        this.transferToOfficeId = null;
        this.transferToOfficeName =null;
        this.imageId = null;
        this.imagePresent = null;
        this.staffName = null;
        this.timeline = null;
        this.savingsProductId = null;
        this.savingsProductName = null;
        this.savingsAccountId =null;
        this.legalForm = null;
        this.groups = null;
        this.officeOptions = null;
        this.staffOptions = null;
        this.narrations = null;
        this.savingProductOptions = null;
        this.savingAccountOptions = null;
        this.genderOptions = null;
        this.fatherspouseOptions = null;
        this.educationOptions = null;
        this.maritalOptions = null;
        this.professionOptions = null;
        this.belongingOptions = null;
        this.annualOptions = null;
        this.altMobNumOfOptions = null;
        this.houseOptions = null;
        this.formOptions = null;
        this.titleOptions = null;
        this.religionOptions = null;
        this.idproofOptions = null;
        this.addrproofOptions = null;
        this.clientTypeOptions = null;
        this.clientClassificationOptions = null;
        this.clientNonPersonConstitutionOptions = null;
        this.clientNonPersonMainBusinessLineOptions = null;
        this.clientLegalFormOptions = null;
        this.clientNonPersonDetails = null;
        this.isAddressEnabled =null;
        this.datatables = null;
        this.familyMemberOptions=null;
        this.emailAddress = null;
        this.income = income;
        this.debt = debt;
        this.debtincratio = debtincratio;
        this.AmountApplied  = AmountApplied;
        this.cpvData = cpvData;
        //this.aadhaarNo = null;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public Long getSavingsAccountId() {
        return savingsAccountId;
    }

    public Long getId(){return id;}

    public String getOfficeName() {
        return officeName;
    }
    
    public String getStaffName() {
        return staffName;
    }

    public static ClientData template(final Long officeId, final LocalDate joinedDate, 
    		final Collection<OfficeData> officeOptions,
            final Collection<StaffData> staffOptions, final Collection<CodeValueData> narrations,
            final Collection<CodeValueData> genderOptions,
            final Collection<CodeValueData> fatherspouseOptions, final Collection<CodeValueData> educationOptions,
            final Collection<CodeValueData> maritalOptions,final Collection<CodeValueData> professionOptions,
            final Collection<CodeValueData> belongingOptions,final Collection<CodeValueData> annualOptions,
            final Collection<CodeValueData> altMobNumOfOptions,final Collection<CodeValueData> houseOptions,
            final Collection<CodeValueData> formOptions,final Collection<CodeValueData> titleOptions,final Collection<CodeValueData> religionOptions,
            final Collection<CodeValueData> idproofOptions,final Collection<CodeValueData> addrproofOptions,
            final Collection<SavingsProductData> savingProductOptions,
            final Collection<CodeValueData> clientTypeOptions,  final Collection<CodeValueData> clientClassificationOptions, final Collection<CodeValueData> clientNonPersonConstitutionOptions,
            final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions, final List<EnumOptionData> clientLegalFormOptions,final ClientFamilyMembersData familyMemberOptions, 
            final AddressData address,
            final Boolean isAddressEnabled, final List<DatatableData> datatables) {
        final String accountNo = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final String officeName = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final Long id = null;
        final String firstName = null;
        final String spousename = null;
        final String lastName = null;
        final String fsFirstName = null;
        final String fsMiddleName = null;
        final String fsLastName = null;
        final String maidenName = null;
        final String custmotherName = null;
        final String alternateMobileNo = null;
        final String secIdProofNo = null;
        final String secaddressproofno = null;    
        final String lastverifiedmobile = null;  
        final String otherexpensestf = null;
        final String othersrcinctf = null;
        final String otherobligations = null; 
        final String lastverifiedsecondaryid = null; 
        final String lastverifiedadhar = null;
        final String adhar = null;
        final String nrega = null;
        final String pan = null;
        final String fullname = null;
        final String displayName = null;
        final String externalId = null;
        final String externalIdd = null;
        final String mobileNo = null;
        final String gstNo = null;
        final String age = null;
        final String nomrelationshipid = null;
        final String nomgenderid = null;
         final String nomage = null;
         final String nomprofessionid = null;
         final String nomeducationalid = null;
         final String nommaritalid = null;
        final String incdailysales = null;
        final String exprawmaterial = null;
        final String expstaffsal = null;
        final String exppowertelephone = null;
        final String exprepairsmaintainance = null;
        final String expcommbrokerage = null;       
        final String incrent = null;
        final String incinterest = null;
        final String incothers = null;
        final String tothouseholdinc = null;
        final String exphousehold = null;
        final String expotherloans = null;
        final String totnetdispfamily = null;
       
        final String idproofNo = null;
        final String addrproofNo = null;
        final String expinterest=null;
        final String expofficerent=null;
        final String exptravel = null;
        final String expothers = null;
        final String totbusinessprofit = null;
        final String incspouse = null;
        final String emailAddress = null;
         //final String aadhaarNo = null;
        final LocalDate dateOfBirth = null;
        final LocalDate lastverifiedSecondaryidDate = null;
        final LocalDate lastverifiedmobiledate = null;
        final LocalDate lastverifiedadhardate = null;
        final CodeValueData gender = null;
        final LocalDate submittedOnDate = null;
      
        final LocalDate activationDate = null; 
        final CodeValueData fatherspouse = null;
        final CodeValueData education = null;
        final CodeValueData marital = null;
        final CodeValueData profession = null;
        final CodeValueData belonging = null;        
        final CodeValueData annual = null;
        final CodeValueData land = null;
        final CodeValueData house = null;
        final CodeValueData form = null;
        final CodeValueData title = null;        
        final CodeValueData religion = null;
        final CodeValueData alternateNo = null;
        final CodeValueData idproof = null;
        final CodeValueData addrproof = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final ClientTimelineData timeline = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final Collection<GroupGeneralData> groups = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final EnumOptionData legalForm = null;
		final Boolean isStaff = false;
        final ClientNonPersonData clientNonPersonDetails = null;
        final Long debt = null;
        final Long income = null;
        final Float debtincratio = null;
        final Boolean cpvData = false;
        //final String alternateNo = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstName,
        		spousename, lastName, fsFirstName, fsMiddleName, fsLastName,maidenName, custmotherName,alternateMobileNo, secIdProofNo, secaddressproofno,lastverifiedmobile,otherexpensestf,othersrcinctf,otherobligations,lastverifiedsecondaryid,lastverifiedadhar, adhar,nrega,pan, fullname, displayName,
                externalId,externalIdd, mobileNo,gstNo,age,nomrelationshipid,
                nomgenderid,
                nomage,
                nomprofessionid,
                nomeducationalid,
                nommaritalid,incdailysales,exprawmaterial,expstaffsal, exppowertelephone,exprepairsmaintainance,expcommbrokerage,incrent,incinterest,incothers,tothouseholdinc,exphousehold,expotherloans,
                totnetdispfamily,expinterest,expofficerent,exptravel,expothers,totbusinessprofit,incspouse,idproofNo, 
                addrproofNo,emailAddress, dateOfBirth,lastverifiedSecondaryidDate,lastverifiedmobiledate,lastverifiedadhardate, gender,fatherspouse, education, 
                marital, profession, belonging, annual,land, house, form, title,religion, alternateNo, idproof,addrproof, joinedDate, imageId, staffId,
                staffName, officeOptions, groups, staffOptions, narrations, genderOptions,fatherspouseOptions,educationOptions,maritalOptions, professionOptions,belongingOptions,annualOptions, 
                altMobNumOfOptions, houseOptions, formOptions,titleOptions,religionOptions,idproofOptions, addrproofOptions,timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType,  clientClassification,
                clientTypeOptions, clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions, 
                clientNonPersonDetails, clientLegalFormOptions,familyMemberOptions, legalForm,address, isAddressEnabled, datatables, 
                isStaff,submittedOnDate,debt,income,debtincratio,null,cpvData);

    }

    public static ClientData templateOnTop(final ClientData clientData, final ClientData templateData) {

        return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstName, clientData.spousename,
                clientData.lastName,clientData.fsFirstName, clientData.fsMiddleName,clientData.fsLastName,clientData.maidenName, clientData.custmotherName,clientData.alternateMobileNo, clientData.secIdProofNo, clientData.secaddressproofno,clientData.lastverifiedmobile,clientData.otherexpensestf,clientData.othersrcinctf,clientData.otherobligations,clientData.lastverifiedsecondaryid,clientData.lastverifiedadhar,clientData.adhar, 
                clientData.nrega,clientData.pan,clientData.fullname, clientData.displayName, 
                clientData.externalId, clientData.externalIdd, clientData.mobileNo,clientData.gstNo,clientData.age,clientData.nomrelationshipid,clientData.nomgenderid,clientData.nomage,clientData.nomprofessionid,clientData.nomeducationalid,
                clientData.nommaritalid,clientData.incdailysales,clientData.exprawmaterial,clientData.expstaffsal,clientData.exppowertelephone,clientData.exprepairsmaintainance,clientData.expcommbrokerage,clientData.incrent,clientData.incinterest,clientData.incothers,clientData.tothouseholdinc,clientData.exphousehold,
                clientData.expotherloans,clientData.totnetdispfamily,clientData.expinterest,clientData.expofficerent,clientData.exptravel,clientData.expothers,
                clientData.totbusinessprofit,clientData.incspouse,clientData.idproofNo,clientData.addrproofNo, clientData.emailAddress,
                clientData.dateOfBirth,clientData.lastverifiedSecondaryidDate,clientData.lastverifiedmobiledate,clientData.lastverifiedadhardate,clientData.gender,clientData.fatherspouse,clientData.education,clientData.marital,clientData.profession,clientData.belonging,clientData.annual,
                clientData.land,clientData.house,clientData.form,clientData.title,clientData.religion, clientData.alternateNo, clientData.idproof,clientData.addrproof,clientData.activationDate, clientData.imageId, clientData.staffId, 
                clientData.staffName, templateData.officeOptions, templateData.groups, templateData.staffOptions, templateData.narrations,
                templateData.genderOptions,templateData.fatherspouseOptions,templateData.educationOptions,templateData.maritalOptions,templateData.professionOptions,templateData.belongingOptions,templateData.annualOptions,
                templateData.altMobNumOfOptions,templateData.houseOptions,templateData.formOptions,templateData.titleOptions,templateData.religionOptions,
                templateData.idproofOptions,templateData.addrproofOptions,clientData.timeline, templateData.savingProductOptions, clientData.savingsProductId,
                clientData.savingsProductName, clientData.savingsAccountId, clientData.savingAccountOptions, clientData.clientType,
                clientData.clientClassification, templateData.clientTypeOptions, templateData.clientClassificationOptions, 
                templateData.clientNonPersonConstitutionOptions, templateData.clientNonPersonMainBusinessLineOptions, clientData.clientNonPersonDetails,
                templateData.clientLegalFormOptions,templateData.familyMemberOptions, clientData.legalForm, clientData.address,clientData.isAddressEnabled, null, 
                clientData.isStaff,clientData.submittedOnDate,clientData.debt,clientData.income,clientData.debtincratio,clientData.AmountApplied,clientData.cpvData);

    }

    public static ClientData templateWithSavingAccountOptions(final ClientData clientData,
            final Collection<SavingsAccountData> savingAccountOptions) {

        return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstName, clientData.spousename,
                clientData.lastName, clientData.fsFirstName, clientData.fsMiddleName,clientData.fsLastName,clientData.maidenName,
                clientData.custmotherName,clientData.alternateMobileNo, clientData.secIdProofNo, clientData.secaddressproofno,clientData.lastverifiedmobile,clientData.otherexpensestf,clientData.othersrcinctf,clientData.otherobligations,clientData.lastverifiedsecondaryid,clientData.lastverifiedadhar,clientData.adhar,clientData.nrega,clientData.pan,
                clientData.fullname, clientData.displayName, clientData.externalId,clientData.externalIdd, clientData.mobileNo,clientData.gstNo,clientData.age,clientData.nomrelationshipid,clientData.nomgenderid,clientData.nomage,clientData.nomprofessionid,clientData.nomeducationalid,
                clientData.nommaritalid, clientData.incdailysales, clientData.exprawmaterial, clientData.expstaffsal,clientData.exppowertelephone,clientData.exprepairsmaintainance,clientData.expcommbrokerage,
                clientData.incrent,clientData.incinterest,clientData.incothers,clientData.tothouseholdinc,clientData.exphousehold,
                clientData.expotherloans,clientData.totnetdispfamily,clientData.expinterest,clientData.expofficerent,clientData.exptravel,clientData.expothers,
                clientData.totbusinessprofit,clientData.incspouse,clientData.idproofNo,clientData.addrproofNo, clientData.emailAddress,
                clientData.dateOfBirth,clientData.lastverifiedSecondaryidDate,clientData.lastverifiedmobiledate, clientData.lastverifiedadhardate,clientData.gender,clientData.fatherspouse,clientData.education,clientData.marital,clientData.profession,clientData.belonging,clientData.annual,
                clientData.land,clientData.house,clientData.form,clientData.title,clientData.religion,clientData.alternateNo, clientData.idproof,clientData.addrproof,clientData.activationDate, clientData.imageId, clientData.staffId,
                clientData.staffName, clientData.officeOptions, clientData.groups, clientData.staffOptions, clientData.narrations,
                clientData.genderOptions,clientData.fatherspouseOptions,clientData.educationOptions,clientData.maritalOptions,clientData.professionOptions,clientData.belongingOptions,clientData.annualOptions, 
                clientData.altMobNumOfOptions,clientData.houseOptions,clientData.formOptions,clientData.titleOptions,clientData.religionOptions,clientData.idproofOptions,clientData.addrproofOptions,clientData.timeline, clientData.savingProductOptions, clientData.savingsProductId,
                clientData.savingsProductName, clientData.savingsAccountId, savingAccountOptions, clientData.clientType, 
                clientData.clientClassification, clientData.clientTypeOptions,  clientData.clientClassificationOptions,
                clientData.clientNonPersonConstitutionOptions, clientData.clientNonPersonMainBusinessLineOptions, clientData.clientNonPersonDetails,
                clientData.clientLegalFormOptions,clientData.familyMemberOptions, clientData.legalForm,clientData.address, 
                clientData.isAddressEnabled, null, clientData.isStaff,clientData.submittedOnDate,clientData.debt,clientData.income,clientData.debtincratio,clientData.AmountApplied,clientData.cpvData);

    }

    public static ClientData setParentGroups(final ClientData clientData, final Collection<GroupGeneralData> parentGroups) {
        return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstName, clientData.spousename,
                clientData.lastName, clientData.fsFirstName, clientData.fsMiddleName,clientData.fsLastName,clientData.maidenName, clientData.custmotherName,clientData.alternateMobileNo, clientData.secIdProofNo, clientData.secaddressproofno,clientData.lastverifiedmobile,clientData.otherexpensestf,clientData.othersrcinctf,clientData.otherobligations,clientData.lastverifiedsecondaryid,clientData.lastverifiedadhar,clientData.adhar,clientData.nrega,clientData.pan,
                clientData.fullname, clientData.displayName, clientData.externalId,clientData.externalIdd, clientData.mobileNo,clientData.gstNo,clientData.age,clientData.nomrelationshipid,
                clientData.nomgenderid,
                clientData.nomage,
                clientData.nomprofessionid,
                clientData.nomeducationalid,
                clientData.nommaritalid,clientData.incdailysales, clientData.exprawmaterial, clientData.expstaffsal,clientData.exppowertelephone,
                clientData.exprepairsmaintainance,clientData.expcommbrokerage,clientData.incrent,clientData.incinterest,clientData.incothers,clientData.tothouseholdinc,clientData.exphousehold,
                clientData.expotherloans,clientData.totnetdispfamily,clientData.expinterest,clientData.expofficerent,clientData.exptravel,clientData.expothers,
                clientData.totbusinessprofit,clientData.incspouse,clientData.idproofNo,clientData.addrproofNo, clientData.emailAddress,
                clientData.dateOfBirth,clientData.lastverifiedSecondaryidDate,clientData.lastverifiedmobiledate,clientData.lastverifiedadhardate, clientData.gender,clientData.fatherspouse,clientData.education,clientData.marital,clientData.profession, clientData.belonging,clientData.annual,
                clientData.land,clientData.house,clientData.form, clientData.title,clientData.religion,clientData.alternateNo, clientData.idproof,clientData.addrproof,clientData.activationDate, clientData.imageId, clientData.staffId,
                clientData.staffName, clientData.officeOptions,parentGroups, clientData.staffOptions,null,null,null,null,null, null, null,
                null,null,null,null,null,null,null,null, null, null, null, null, null, null, null, clientData.clientType, clientData.clientTypeOptions,
                clientData.clientClassificationOptions, clientData.clientNonPersonConstitutionOptions, clientData.clientNonPersonMainBusinessLineOptions, 
                clientData.clientNonPersonDetails, clientData.clientLegalFormOptions,clientData.familyMemberOptions, clientData.legalForm,clientData.address,
				clientData.isAddressEnabled, null, clientData.isStaff,clientData.submittedOnDate,clientData.debt,clientData.income,clientData.debtincratio,clientData.AmountApplied,clientData.cpvData);

    }

    public static ClientData clientIdentifier(final Long id, final String accountNo, final String firstName, final String spousename,
            final String lastName,final String fsFirstName, final String fsMiddleName,final String fsLastName,
            final String maidenName, final String custmotherName,final String alternateMobileNo, final String secIdProofNo, final String secaddressproofno,final String lastverifiedmobile,final String otherexpensestf,final String othersrcinctf,final String otherobligations,final String lastverifiedsecondaryid,final String lastverifiedadhar, final String adhar,final String nrega,final String pan,
            final String fullname, final String displayName, final Long officeId, final String officeName) {

        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String externalId = null;
        final String externalIdd = null;
        final String mobileNo = null;
        final String gstNo = null;
        final String age = null;
        final String nomrelationshipid = null;
        final String nomgenderid = null;
         final String nomage = null;
         final String nomprofessionid = null;
         final String nomeducationalid = null;
         final String nommaritalid = null;
        final String incdailysales = null;
        final String exprawmaterial = null;
        final String expstaffsal = null;
        final String exppowertelephone = null;
        final String exprepairsmaintainance = null;
        final String expcommbrokerage = null;
        final String incrent = null;
        final String incinterest = null;
        final String incothers = null;
        final String tothouseholdinc = null;
        final String exphousehold = null;
        final String expotherloans = null;
        final String totnetdispfamily = null;
       
        final String expinterest= null;
        final String expofficerent= null;
        final String exptravel= null;
        final String expothers= null;
        final String totbusinessprofit= null;
        final String incspouse= null;
        final String idproofNo = null;
        final String addrproofNo = null;
        //final String aadhaarNo = null;
        //final String alternateNo = null;
		final String emailAddress = null;
        final LocalDate dateOfBirth = null;
        final LocalDate lastverifiedSecondaryidDate = null;
        final LocalDate lastverifiedmobiledate = null;
        final LocalDate lastverifiedadhardate = null;
        final LocalDate submittedOnDate = null;
       
        final CodeValueData gender = null;
      
        final CodeValueData fatherspouse = null;
        final CodeValueData education = null;
        final CodeValueData marital = null;
        final CodeValueData profession = null;
        final CodeValueData belonging = null;
        final CodeValueData annual = null;
        final CodeValueData land = null;
        final CodeValueData house = null;
        final CodeValueData form = null;
        final CodeValueData title = null;
        final CodeValueData religion = null;
        final CodeValueData alternateNo = null;
        final CodeValueData idproof = null;
        final CodeValueData addrproof = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
       
        final Collection<CodeValueData> fatherspouseOptions = null;
        final Collection<CodeValueData> educationOptions = null;
        final Collection<CodeValueData> maritalOptions = null;
        final Collection<CodeValueData> professionOptions = null;
        final Collection<CodeValueData> belongingOptions = null;
        final Collection<CodeValueData> annualOptions = null;
        final Collection<CodeValueData> altMobNumOfOptions = null;
        final Collection<CodeValueData> houseOptions = null;
        final Collection<CodeValueData> formOptions = null;
        final Collection<CodeValueData> titleOptions = null;
        final Collection<CodeValueData> religionOptions = null;
        final Collection<CodeValueData>	idproofOptions = null;
        final Collection<CodeValueData> addrproofOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final ClientFamilyMembersData familyMemberOptions=null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final EnumOptionData legalForm = null;
		final Boolean isStaff = false;
        final ClientNonPersonData clientNonPerson = null;
        
        final Long debt = null;
        final Long income = null;
        final Float debtincratio = null;
        final Boolean cpvData = false;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstName,
        		spousename, lastName, fsFirstName, fsMiddleName, fsLastName,maidenName, custmotherName, alternateMobileNo, secIdProofNo, secaddressproofno,lastverifiedmobile,otherexpensestf,othersrcinctf,otherobligations,lastverifiedsecondaryid,lastverifiedadhar, adhar,nrega, pan,
                fullname, displayName, externalId,externalIdd,mobileNo,gstNo,age,nomrelationshipid,
                nomgenderid,
                nomage,
                nomprofessionid,
                nomeducationalid,
                nommaritalid,incdailysales,exprawmaterial,expstaffsal, exppowertelephone,exprepairsmaintainance,expcommbrokerage,
                incrent,incinterest,incothers,tothouseholdinc,exphousehold,expotherloans,totnetdispfamily,expinterest,expofficerent,exptravel,expothers,totbusinessprofit,incspouse,idproofNo,addrproofNo, emailAddress, dateOfBirth,lastverifiedSecondaryidDate,lastverifiedmobiledate,lastverifiedadhardate,
                gender,fatherspouse,
                education, marital, profession, belonging, annual,land, house, form, title, religion,alternateNo, idproof,addrproof, activationDate, imageId, staffId,
                staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions,fatherspouseOptions,educationOptions,maritalOptions, professionOptions,belongingOptions,annualOptions,
                altMobNumOfOptions,houseOptions, formOptions,titleOptions,religionOptions,idproofOptions, addrproofOptions,timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType,  clientClassification,
                clientTypeOptions,  clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions, 
                clientNonPerson, clientLegalFormOptions,familyMemberOptions, legalForm,null,null, null, isStaff,submittedOnDate,debt,income,debtincratio,null,cpvData);
    }

    public static ClientData lookup(final Long id, final String displayName, final Long officeId, final String officeName) {
        final String accountNo = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String firstName = null;
        final String spousename = null;
        final String lastName = null;
        final String fsFirstName = null;
        final String fsMiddleName = null;
        final String fsLastName = null;
        final String maidenName = null;
        final String custmotherName = null;
        final String alternateMobileNo = null;
        final String secIdProofNo = null;
        final String secaddressproofno = null; 
        final String lastverifiedmobile = null;
        final String otherexpensestf = null;
        final String othersrcinctf = null;
        final String otherobligations = null;
        final String lastverifiedsecondaryid = null;
        final String lastverifiedadhar = null;
        final String adhar = null;
        final String nrega = null;
        final String pan = null;
        final String fullname = null;
        final String externalId = null;
        final String externalIdd = null;
        final String mobileNo = null;
        final String gstNo = null;
        final String age = null;
        final String nomrelationshipid = null;
        final String nomgenderid = null;
         final String nomage = null;
         final String nomprofessionid = null;
         final String nomeducationalid = null;
         final String nommaritalid = null;
        final String incdailysales = null;
        final String exprawmaterial = null;
        final String expstaffsal = null;
        final String exppowertelephone = null;
        final String exprepairsmaintainance = null;
        final String expcommbrokerage = null;
        final String incrent = null;
        final String incinterest = null;
        final String incothers = null;
        final String tothouseholdinc = null;
        final String exphousehold = null;
        final String expotherloans = null;
        final String totnetdispfamily = null;
        final String foaTwo = null;
        final String foaThree = null;
        final String expinterest= null;
        final String expofficerent= null;
        final String exptravel= null;
        final String expothers= null;
        final String totbusinessprofit= null;
        final String incspouse= null;
       
        final String idproofNo = null;
        final String addrproofNo = null;
       // final String alternateNo = null;
        //final String aadhaarNo = null;
		final String emailAddress = null;
        final LocalDate dateOfBirth = null;
        final LocalDate lastverifiedSecondaryidDate = null;
        final LocalDate lastverifiedmobiledate = null;
        final LocalDate lastverifiedadhardate = null;
        final LocalDate submittedOnDate = null;
      
        final CodeValueData gender = null;
      
        final CodeValueData fatherspouse = null;
        final CodeValueData education = null;
        final CodeValueData marital = null;
        final CodeValueData profession = null;
        final CodeValueData belonging = null;
        final CodeValueData annual = null;
        final CodeValueData land = null;
        final CodeValueData house = null;
        final CodeValueData form = null;
        final CodeValueData title = null;
        final CodeValueData religion = null;
        final CodeValueData alternateNo = null;
        final CodeValueData idproof = null;
        final CodeValueData addrproof = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
       
        final Collection<CodeValueData> fatherspouseOptions = null;
        final Collection<CodeValueData> educationOptions = null;
        final Collection<CodeValueData> maritalOptions = null;
        final Collection<CodeValueData> professionOptions = null;
        final Collection<CodeValueData> belongingOptions = null;
        final Collection<CodeValueData> annualOptions = null;
        final Collection<CodeValueData> altMobNumOfOptions = null;
        final Collection<CodeValueData> houseOptions = null;
        final Collection<CodeValueData> formOptions = null;
        final Collection<CodeValueData> titleOptions = null;
        final Collection<CodeValueData> religionOptions = null;
        final Collection<CodeValueData> idproofOptions = null;
        final Collection<CodeValueData> addrproofOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final ClientFamilyMembersData familyMemberOptions=null;
        final EnumOptionData legalForm = null;
		final Boolean isStaff = false;
        final ClientNonPersonData clientNonPerson = null;
        
        final Long debt = null;
        final Long income = null;
        final Float debtincratio = null;
        final Boolean cpvData = false;
       // final Long group = null;
       // final Long center = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstName,
        		spousename, lastName, fsFirstName, fsMiddleName, fsLastName,  maidenName, custmotherName, alternateMobileNo, secIdProofNo, secaddressproofno,lastverifiedmobile,otherexpensestf,othersrcinctf,otherobligations,lastverifiedsecondaryid,lastverifiedadhar, adhar,nrega, pan, 
                fullname, displayName, externalId, externalIdd,  mobileNo,gstNo,age,nomrelationshipid,
                nomgenderid,
                nomage,
                nomprofessionid,
                nomeducationalid,
                nommaritalid,incdailysales,exprawmaterial,expstaffsal, exppowertelephone,exprepairsmaintainance,expcommbrokerage,
                incrent,incinterest,incothers,tothouseholdinc,exphousehold,expotherloans,totnetdispfamily,expinterest,expofficerent,exptravel,expothers,totbusinessprofit,incspouse,idproofNo,addrproofNo, emailAddress, dateOfBirth,lastverifiedSecondaryidDate,lastverifiedmobiledate,lastverifiedadhardate,
                gender,
                fatherspouse,education,marital, profession, belonging, annual,land, house, form, title, religion,alternateNo, idproof, addrproof, activationDate, imageId, staffId,
                staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions,fatherspouseOptions,
                educationOptions,maritalOptions, professionOptions,belongingOptions,annualOptions,altMobNumOfOptions, houseOptions, formOptions, titleOptions, 
                religionOptions, idproofOptions, addrproofOptions,timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions,  clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions, 
                clientNonPerson, clientLegalFormOptions,familyMemberOptions, legalForm,  null, isStaff, null, null,submittedOnDate,debt,income,debtincratio,null,cpvData);
    }
    
    public static ClientData instance(final Long id, final String displayName){
    	 final Long officeId = null;
    	 final String officeName = null;
    	 return lookup(id, displayName, officeId, officeName);
    }

    public static ClientData instance(final String accountNo, final EnumOptionData status, final CodeValueData subStatus,
            final Long officeId, final String officeName, final Long transferToOfficeId, final String transferToOfficeName, final Long id,
            final String firstName, final String spousename, final String lastName,final String fsFirstName, final String fsMiddleName, final String fsLastName,
            final String maidenName, final String custmotherName, final String alternateMobileNo, final String secIdProofNo, final String secaddressproofno,final String lastverifiedmobile,final String otherexpensestf,final String othersrcinctf,final String otherobligations,final String lastverifiedsecondaryid,final String lastverifiedadhar, final String adhar,final String nrega, final String pan,final String fullname, final String displayName,
            final String externalId,final String externalIdd, final String mobileNo,final String gstNo,final String age, final String nomrelationshipid,
            final String nomgenderid,
            final String nomage,
            final String nomprofessionid ,
            final String nomeducationalid ,
            final String nommaritalid,final String incdailysales,final String exprawmaterial,final String expstaffsal,
            final String exppowertelephone,final String exprepairsmaintainance,final String expcommbrokerage,final String incrent,final String incinterest,final String incothers,final String tothouseholdinc,
            final String exphousehold,final String expotherloans,final String totnetdispfamily,final String expinterest,final String expofficerent,
            final String exptravel,final String expothers,final String totbusinessprofit,final String incspouse,final String idproofNo,final String addrproofNo, final String emailAddress, final LocalDate dateOfBirth, final LocalDate lastverifiedSecondaryidDate,final LocalDate lastverifiedmobiledate,final LocalDate lastverifiedadhardate,
            final CodeValueData gender,
            final CodeValueData fatherspouse,final CodeValueData education,final CodeValueData marital,final CodeValueData profession,
            final CodeValueData belonging,final CodeValueData annual,final CodeValueData land,final CodeValueData house,
            final CodeValueData form,final CodeValueData title,final CodeValueData religion,final CodeValueData alternateNo, final CodeValueData idproof,final CodeValueData addrproof,
            final LocalDate activationDate, final Long imageId, final Long staffId, final String staffName,
            final ClientTimelineData timeline, final Long savingsProductId, final String savingsProductName, final Long savingsAccountId,
            final CodeValueData clientType, final CodeValueData clientClassification, final EnumOptionData legalForm, 
            final ClientNonPersonData clientNonPerson, final Boolean isStaff,final LocalDate submittedOnDate,final Long debt,final Long income,final Float debtincratio,Long AmountApplied,Boolean cpvData) {

        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
      
        final Collection<CodeValueData> fatherspouseOptions = null;
        final Collection<CodeValueData> educationOptions = null;
        final Collection<CodeValueData> maritalOptions = null;
        final Collection<CodeValueData> professionOptions = null;
        final Collection<CodeValueData> belongingOptions = null;
        final Collection<CodeValueData> annualOptions = null;
        final Collection<CodeValueData> altMobNumOfOptions = null;
        final Collection<CodeValueData> houseOptions = null;
        final Collection<CodeValueData> formOptions = null;
        final Collection<CodeValueData> titleOptions = null;
        final Collection<CodeValueData> religionOptions = null;
        final Collection<CodeValueData> idproofOptions = null;
        final Collection<CodeValueData> addrproofOptions = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
        final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
        final List<EnumOptionData> clientLegalFormOptions = null;
        final ClientFamilyMembersData familyMemberOptions=null;
        
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstName,
        		spousename, lastName,fsFirstName,fsMiddleName, fsLastName, maidenName,custmotherName,alternateMobileNo, secIdProofNo, secaddressproofno,lastverifiedmobile,otherexpensestf,othersrcinctf,otherobligations,lastverifiedsecondaryid,lastverifiedadhar, adhar,nrega,pan, 
                fullname, displayName, externalId, externalIdd, mobileNo,gstNo,age,nomrelationshipid,
                nomgenderid,
                nomage,
                nomprofessionid,
                nomeducationalid,
                nommaritalid,incdailysales,exprawmaterial,expstaffsal, exppowertelephone,exprepairsmaintainance,expcommbrokerage,
                incrent,incinterest,incothers,tothouseholdinc,exphousehold,expotherloans,totnetdispfamily,expinterest,expofficerent,exptravel,expothers,totbusinessprofit,incspouse,
                idproofNo,addrproofNo, emailAddress, dateOfBirth,lastverifiedSecondaryidDate,lastverifiedmobiledate, lastverifiedadhardate,
                gender,
                fatherspouse,education,marital, profession, belonging, annual,land,house, form, title, religion,alternateNo, 
                idproof, addrproof, activationDate ,imageId, staffId, staffName,  null,  null, null, genderOptions,
                fatherspouseOptions,educationOptions,maritalOptions, professionOptions,belongingOptions,annualOptions,
                altMobNumOfOptions,houseOptions, formOptions,titleOptions,religionOptions, idproofOptions, addrproofOptions,null, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, null, clientType,  clientClassification, clientTypeOptions,
                clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions, clientNonPerson,
                clientLegalFormOptions,familyMemberOptions, legalForm,null,null, null, isStaff,submittedOnDate,debt,income,debtincratio,AmountApplied,cpvData);

    }

    private ClientData(final String accountNo, final EnumOptionData status, final CodeValueData subStatus, final Long officeId,
            final String officeName, final Long transferToOfficeId, final String transferToOfficeName, final Long id,
            final String firstName, final String spousename, final String lastName,
            final String fsFirstName, final String fsMiddleName, final String fsLastName,final String maidenName, final String custmotherName,final String alternateMobileNo, final String secIdProofNo, final String secaddressproofno,final String lastverifiedmobile,final String otherexpensestf,final String othersrcinctf,final String otherobligations,final String lastverifiedsecondaryid,final String lastverifiedadhar,
            final String adhar,final String nrega, final String pan, final String fullname, final String displayName,
            final String externalId,final String externalIdd, final String mobileNo, final String gstNo,final String age,final String nomrelationshipid,
            final String nomgenderid,
            final String nomage,
            final String nomprofessionid,
            final String nomeducationalid,
            final String nommaritalid,final String incdailysales,final String exprawmaterial,final String expstaffsal,
            final String exppowertelephone,final String exprepairsmaintainance,final String expcommbrokerage,final String incrent,final String incinterest,final String incothers,final String tothouseholdinc,
            final String exphousehold,final String expotherloans,final String totnetdispfamily,final String expinterest,final String expofficerent,
            final String exptravel,final String expothers,final String totbusinessprofit,final String incspouse,final String idproofNo,final String addrproofNo, final String emailAddress, final LocalDate dateOfBirth, final LocalDate lastverifiedSecondaryidDate,final LocalDate lastverifiedmobiledate,final LocalDate lastverifiedadhardate,
            final CodeValueData gender,
            final CodeValueData fatherspouse,final CodeValueData education,final CodeValueData marital,final CodeValueData profession,   
            final CodeValueData belonging,final CodeValueData annual,
            final CodeValueData land,final CodeValueData house,final CodeValueData form,final CodeValueData title,final CodeValueData religion, final CodeValueData alternateNo, 
            final CodeValueData idproof,final CodeValueData addrproof,
            final LocalDate activationDate, final Long imageId, final Long staffId, final String staffName,
            final Collection<OfficeData> allowedOffices, final Collection<GroupGeneralData> groups, 
            final Collection<StaffData> staffOptions, final Collection<CodeValueData> narrations,
            final Collection<CodeValueData> genderOptions,
            final Collection<CodeValueData> fatherspouseOptions,final Collection<CodeValueData> educationOptions,final Collection<CodeValueData> maritalOptions, final Collection<CodeValueData> professionOptions,
            final Collection<CodeValueData> belongingOptions,final Collection<CodeValueData> annualOptions,final Collection<CodeValueData> altMobNumOfOptions,final Collection<CodeValueData> houseOptions, final Collection<CodeValueData> formOptions,
            final Collection<CodeValueData> titleOptions,final Collection<CodeValueData> religionOptions,final Collection<CodeValueData> idproofOptions,final Collection<CodeValueData> addrproofOptions, final ClientTimelineData timeline,
            final Collection<SavingsProductData> savingProductOptions, final Long savingsProductId, final String savingsProductName,
            final Long savingsAccountId, final Collection<SavingsAccountData> savingAccountOptions, final CodeValueData clientType,
            final CodeValueData clientClassification, final Collection<CodeValueData> clientTypeOptions, 
            final Collection<CodeValueData> clientClassificationOptions, final Collection<CodeValueData> clientNonPersonConstitutionOptions,
            final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions, final ClientNonPersonData clientNonPerson,
            final List<EnumOptionData> clientLegalFormOptions,final ClientFamilyMembersData familyMemberOptions, final EnumOptionData legalForm, final AddressData address,
            final Boolean isAddressEnabled, final List<DatatableData> datatables, final Boolean isStaff,final LocalDate submittedOnDate,final Long debt,final Long income,final Float debtincratio,final Long AmountApplied,final Boolean cpvData) {
        this.accountNo = accountNo;
        this.status = status;
        if (status != null) {
            this.active = status.getId().equals(300L);
        } else {
            this.active = null;
        }
        this.subStatus = subStatus;
        this.officeId = officeId;
        this.officeName = officeName;
        this.transferToOfficeId = transferToOfficeId;
        this.transferToOfficeName = transferToOfficeName;
        this.id = id;
        this.firstName = StringUtils.defaultIfEmpty(firstName, null);
        this.spousename = StringUtils.defaultIfEmpty(spousename, null);
        this.lastName = StringUtils.defaultIfEmpty(lastName, null);
        this.fsFirstName = StringUtils.defaultIfEmpty(fsFirstName, null);
        this.fsMiddleName = StringUtils.defaultIfEmpty(fsMiddleName, null);
        this.fsLastName = StringUtils.defaultIfEmpty(fsLastName, null);
        this.maidenName = StringUtils.defaultIfEmpty(maidenName, null);
        this.custmotherName = StringUtils.defaultIfEmpty(custmotherName, null);
        this.alternateMobileNo = StringUtils.defaultIfEmpty(alternateMobileNo, null);
        this.secIdProofNo = StringUtils.defaultIfEmpty(secIdProofNo, null);
        this.secaddressproofno = StringUtils.defaultIfEmpty(secaddressproofno, null); 
        this.lastverifiedmobile = StringUtils.defaultIfEmpty(lastverifiedmobile, null);
        this.otherexpensestf = StringUtils.defaultIfEmpty(otherexpensestf, null);
        this.othersrcinctf = StringUtils.defaultIfEmpty(othersrcinctf, null);
        this.otherobligations = StringUtils.defaultIfEmpty(otherobligations, null);
        this.lastverifiedsecondaryid = StringUtils.defaultIfEmpty(lastverifiedsecondaryid, null);
        this.lastverifiedadhar = StringUtils.defaultIfEmpty(lastverifiedadhar,null);
        this.adhar = StringUtils.defaultIfEmpty(adhar, null);
        this.nrega = StringUtils.defaultIfEmpty(nrega, null);
        this.pan = StringUtils.defaultIfEmpty(pan, null);
        this.fullname = StringUtils.defaultIfEmpty(fullname, null);
        this.displayName = StringUtils.defaultIfEmpty(displayName, null);
        this.externalId = StringUtils.defaultIfEmpty(externalId, null);
        this.externalIdd = StringUtils.defaultIfEmpty(externalIdd, null);
        this.mobileNo = StringUtils.defaultIfEmpty(mobileNo, null);
        this.gstNo = StringUtils.defaultIfEmpty(gstNo, null);
        this.age = StringUtils.defaultIfEmpty(age, null);
        this.nomrelationshipid = StringUtils.defaultIfEmpty(nomrelationshipid, null);
        this.nomgenderid = StringUtils.defaultIfEmpty(nomgenderid, null);
        this.nomage = StringUtils.defaultIfEmpty(nomage, null);
        this.nomprofessionid = StringUtils.defaultIfEmpty(nomprofessionid, null);
        this.nomeducationalid = StringUtils.defaultIfEmpty(nomeducationalid, null);
        this.nommaritalid = StringUtils.defaultIfEmpty(nommaritalid, null);
        this.incdailysales = StringUtils.defaultIfEmpty(incdailysales, null);
        this.exprawmaterial = StringUtils.defaultIfEmpty(exprawmaterial, null);
        this.expstaffsal = StringUtils.defaultIfEmpty(expstaffsal, null);
        this.exppowertelephone = StringUtils.defaultIfEmpty(exppowertelephone, null);
        this.exprepairsmaintainance = StringUtils.defaultIfEmpty(exprepairsmaintainance, null);
        this.expcommbrokerage = StringUtils.defaultIfEmpty(expcommbrokerage, null);
        this.incrent = StringUtils.defaultIfEmpty(incrent, null);
        this.incinterest = StringUtils.defaultIfEmpty(incinterest, null);
        this.incothers = StringUtils.defaultIfEmpty(incothers, null);
        this.tothouseholdinc = StringUtils.defaultIfEmpty(tothouseholdinc, null);
        this.exphousehold = StringUtils.defaultIfEmpty(exphousehold, null);
        this.expotherloans = StringUtils.defaultIfEmpty(expotherloans, null);
        this.totnetdispfamily = StringUtils.defaultIfEmpty(totnetdispfamily, null);

        this.expinterest = StringUtils.defaultIfEmpty(expinterest, null);
        this.expofficerent = StringUtils.defaultIfEmpty(expofficerent, null);
        this.exptravel = StringUtils.defaultIfEmpty(exptravel, null);
        this.expothers = StringUtils.defaultIfEmpty(expothers, null);
        this.totbusinessprofit = StringUtils.defaultIfEmpty(totbusinessprofit, null);
        this.incspouse = StringUtils.defaultIfEmpty(incspouse, null);
        this.idproofNo = StringUtils.defaultIfEmpty(idproofNo, null);
        this.addrproofNo = StringUtils.defaultIfEmpty(addrproofNo, null);
       // this.alternateNo = StringUtils.defaultIfEmpty(alternateNo, null);
		this.emailAddress = StringUtils.defaultIfEmpty(emailAddress, null);
        this.activationDate = activationDate;
        this.dateOfBirth = dateOfBirth;
        this.submittedOnDate = submittedOnDate;
       
        this.lastverifiedSecondaryidDate = lastverifiedSecondaryidDate;
        this.lastverifiedmobiledate = lastverifiedmobiledate;
        this.lastverifiedadhardate = lastverifiedadhardate;
        this.gender = gender;
        this.fatherspouse = fatherspouse;
        this.education = education;
        this.marital = marital;
        this.profession = profession;
        this.belonging = belonging;
        this.annual = annual;
        this.land = land;
        this.house = house;
        this.form = form;
        this.title = title;
        this.religion = religion;
        this.alternateNo = alternateNo;
        this.idproof = idproof;
        this.addrproof = addrproof;
        this.clientClassification = clientClassification;
        this.clientType = clientType;
        this.imageId = imageId;
        if (imageId != null) {
            this.imagePresent = Boolean.TRUE;
        } else {
            this.imagePresent = null;
        }
        this.staffId = staffId;
        this.staffName = staffName;
        //this.aadhaarNo = StringUtils.defaultIfEmpty(aadhaarNo , null);
        // associations
        this.groups = groups;
        // template
        this.officeOptions = allowedOffices;
        this.staffOptions = staffOptions;
        this.narrations = narrations;

        this.genderOptions = genderOptions;
      
        this.fatherspouseOptions = fatherspouseOptions;
        this.educationOptions = educationOptions;
        this.maritalOptions = maritalOptions;
        this.professionOptions = professionOptions;
        this.belongingOptions = belongingOptions;
        this.annualOptions = annualOptions;
        this.altMobNumOfOptions = altMobNumOfOptions;
        this.houseOptions = houseOptions;
        this.formOptions = formOptions;
        this.titleOptions = titleOptions;
        this.religionOptions = religionOptions;
        this.idproofOptions = idproofOptions;
        this.addrproofOptions = addrproofOptions;
        this.clientClassificationOptions = clientClassificationOptions;
        this.clientTypeOptions = clientTypeOptions;       
        
        this.clientNonPersonConstitutionOptions = clientNonPersonConstitutionOptions;
        this.clientNonPersonMainBusinessLineOptions = clientNonPersonMainBusinessLineOptions;
        this.clientLegalFormOptions = clientLegalFormOptions;
        this.familyMemberOptions=familyMemberOptions;

        this.timeline = timeline;
        this.savingProductOptions = savingProductOptions;
        this.savingsProductId = savingsProductId;
        this.savingsProductName = savingsProductName;
        this.savingsAccountId = savingsAccountId;
        this.savingAccountOptions = savingAccountOptions;
        this.legalForm = legalForm;
		this.isStaff = isStaff;
        this.clientNonPersonDetails = clientNonPerson;
        
      	this.address = address;
		this.isAddressEnabled = isAddressEnabled;
        this.datatables = datatables;
        this.debt = debt;
        this.income = income;
        this.debtincratio = debtincratio;
        this.AmountApplied = AmountApplied;
        this.cpvData = cpvData;
    }

    public Long getDebt() {
		return debt;
	}

	public Long getIncome() {
		return income;
	}

	public Float getDebtincratio() {
		return debtincratio;
	}

	public Long id() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }
    
    public String accountNo() {
    	return this.accountNo;
    }

    public Long officeId() {
        return this.officeId;
    }

    public String officeName() {
        return this.officeName;
    }

   // public String aadhaarNo() {
    //    return this.aadhaarNo;
   // }

    public Long getImageId() {
        return this.imageId;
    }
    
   // public Long getGroup() {
    //    return this.group;
    //}
    
  //  public Long getCenter() {
  //      return this.center;
   // }

    public Boolean getImagePresent() {
        return this.imagePresent;
    }

    public ClientTimelineData getTimeline() {
        return this.timeline;
    }
    
    public Collection<GroupGeneralData> getGroups() {
		return groups;
	}
    

	public Collection<OfficeData> getOfficeOptions() {
		return officeOptions;
	}

	public Collection<StaffData> getStaffOptions() {
		return staffOptions;
	}

	public Collection<CodeValueData> getNarrations() {
		return narrations;
	}

	public Collection<SavingsProductData> getSavingProductOptions() {
		return savingProductOptions;
	}

	public Collection<SavingsAccountData> getSavingAccountOptions() {
		return savingAccountOptions;
	}

	public Collection<CodeValueData> getGenderOptions() {
		return genderOptions;
	}
	
	
	public Collection<CodeValueData> getFatherspouseOptions() {
		return fatherspouseOptions;
	}

	public Collection<CodeValueData> getEducationOptions() {
		return educationOptions;
	}
	
	public Collection<CodeValueData> getMaritalOptions() {
		return maritalOptions;
	}

	public Collection<CodeValueData> getProfessionOptions() {
		return professionOptions;
	}
	
	public Collection<CodeValueData> getBelongingOptions() {
		return belongingOptions;
	}

	public Collection<CodeValueData> getAnnualOptions() {
		return annualOptions;
	}
	
	public Collection<CodeValueData> getAltMobNumOfOptions() {
		return altMobNumOfOptions;
	}
	
	public Collection<CodeValueData> getHouseOptions() {
		return houseOptions;
	}

	public Collection<CodeValueData> getFormOptions() {
		return formOptions;
	}
	
	public Collection<CodeValueData> getTitleOptions() {
		return titleOptions;
	}

	public Collection<CodeValueData> getReligionOptions() {
		return religionOptions;
	}
	
	public Collection<CodeValueData> getIdproofOptions() {
		return idproofOptions;
	}
	
	public Collection<CodeValueData> getAddrproofOptions() {
		return addrproofOptions;
	}
	
	public Collection<CodeValueData> getClientTypeOptions() {
		return clientTypeOptions;
	}
	
	
	public Collection<CodeValueData> getClientClassificationOptions() {
		return clientClassificationOptions;
	}

	public Collection<CodeValueData> getClientNonPersonConstitutionOptions() {
		return clientNonPersonConstitutionOptions;
	}

	public Collection<CodeValueData> getClientNonPersonMainBusinessLineOptions() {
		return clientNonPersonMainBusinessLineOptions;
	}

	public List<EnumOptionData> getClientLegalFormOptions() {
		return clientLegalFormOptions;
	}

	public ClientNonPersonData getClientNonPersonDetails() {
		return clientNonPersonDetails;
	}

	public AddressData getAddress() {
		return address;
	}

	public List<DatatableData> getDatatables() {
		return datatables;
	}
	
    
public String getAccountNo() {		    
	return accountNo;		
}		
		
//public String getAadhaarNo() {		
//	return aadhaarNo;		
//}		
	
public EnumOptionData getStatus() {		
	return status;		
}		
	
public CodeValueData getSubStatus() {		
	return subStatus;		
}		
	
public Boolean getActive() {		
	return active;		
}		
	
public String getSpousename() {		
	return spousename;		
}		
	
public String getFullname() {		
	return fullname;		
}		
	
public String getDisplayName() {		
	return displayName;		
}		

public String getMobileNo() {		
	return mobileNo;		
}

public String getGstNo() {		
	return gstNo;		
}

public String getnomrelationshipid() {		
	return nomrelationshipid;		
}

public String getnomgenderid() {		
	return nomgenderid;		
}

public String getnomage() {		
	return nomage;		
}

public String getnomprofessionid() {		
	return nomprofessionid;		
}

public String getnomeducationalid() {		
	return nomeducationalid;		
}

public String getnommaritalid() {		
	return nommaritalid;		
}


public String getAge() {		
	return age;		
}

public String getincdailysales() {		
	return incdailysales;		
}

public String getexprawmaterial() {		
	return exprawmaterial;		
}

public String getexpstaffsal() {		
	return expstaffsal;		
}

public String getexppowertelephone() {		
	return exppowertelephone;		
}

public String getexprepairsmaintainance() {		
	return exprepairsmaintainance;		
}

public String getexpcommbrokerage() {		
	return expcommbrokerage;		
}


public String getincrent() {		
	return incrent;		
}

public String getincinterest() {		
	return incinterest;		
}

public String getincothers() {		
	return incothers;		
}

public String gettothouseholdinc() {		
	return tothouseholdinc;		
}

public String getexphousehold() {		
	return exphousehold;		
}

public String getexpotherloans() {		
	return expotherloans;		
}

public String gettotnetdispfamily() {		
	return totnetdispfamily;		
}


public String getexpinterest() {		
	return expinterest;		
}		

public String getexpofficerent() {		
	return expofficerent;		
}		

public String gettotbusinessprofit() {		
	return totbusinessprofit;		
}		

public String getincspouse() {		
	return incspouse;		
}		

public String getexptravel() {		
	return exptravel;		
}		

public String getexpothers() {		
	return expothers;		
}

public String getIdproofNo() {		
	return idproofNo;		
}		
	
public String getaddrproofNo() {		
	return addrproofNo;		
}		
	

//public String getAlternateNo() {		
	//return alternateNo;		
//}		
	
public LocalDate getDateOfBirth() {		
	return dateOfBirth;		
}	

public LocalDate getSubmittedOnDate()
{
	return submittedOnDate;
}


	
public CodeValueData getClientType() {		
	return clientType;		
}	


public CodeValueData getClientClassification() {		
	return clientClassification;		
}		
	
public Boolean getIsStaff() {		
	return isStaff;		
}	

	
public Long getOfficeId() {		
	return officeId;		
}		
	
public Long getTransferToOfficeId() {		
	return transferToOfficeId;		
}		
	
public String getTransferToOfficeName() {		
	return transferToOfficeName;		
}		
	
public Long getStaffId() {		
	return staffId;		
}		
	
	
public Long getSavingsProductId() {		
	return savingsProductId;		
}		
	
public String getSavingsProductName() {		
	return savingsProductName;		
}		
	
	
public EnumOptionData getLegalForm() {		
	return legalForm;		
}


    @Override
    public int compareTo(final ClientData obj) {
        if (obj == null) { return -1; }
        return new CompareToBuilder() //
                .append(this.id, obj.id) //
                .append(this.displayName, obj.displayName) //
                .append(this.mobileNo, obj.mobileNo) //
				.append(this.emailAddress, obj.emailAddress) //
                .toComparison();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final ClientData rhs = (ClientData) obj;
        return new EqualsBuilder() //
                .append(this.id, rhs.id) //
                .append(this.displayName, rhs.displayName) //
                .append(this.mobileNo, rhs.mobileNo) //
				.append(this.emailAddress, rhs.emailAddress) //
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37) //
                .append(this.id) //
                .append(this.displayName) //
                .toHashCode();
    }

    public String getExternalId() {
        return this.externalId;
    }
    
    public String getExternalIdd() {
        return this.externalIdd;
    }
    

    public String getFirstname() {
        return this.firstName;
    }
    
    public String getFsFirstname() {
        return this.fsFirstName;
    }
    public String getmaidenName() {
        return this.maidenName;
    }
    
    public String getAdhar() {
		return adhar;
	}
    public String getNrega() {
		return nrega;
	}
    
    public String getPan() {
		return pan;
	}
    public String getCustmothername() {
		return custmotherName;
	}
    public String getAlternateMobileNo() {
		return alternateMobileNo;
	}
    public String getSecIdProofNo() {
		return secIdProofNo;
	}
    public String getSecaddressproofno() {
		return secaddressproofno;
	}     
    
    public String getlastverifiedmobile() {
  		return lastverifiedmobile;
  	}
    
    public String getOtherexpensestf() {
  		return otherexpensestf;
  	}
    
    public String getOthersrcinctf() {
  		return othersrcinctf;
  	}
    
    
    public String getotherobligations() {
  		return otherobligations;
  	}
    public String getlastverifiedsecondaryid() {
  		return lastverifiedsecondaryid;
  	}
    public CodeValueData getGender() {
		return gender;
	}
    
    
    public CodeValueData getFatherspouse() {
		return fatherspouse;
	}
    
    
    public CodeValueData getEducation() {
		return education;
	}
    
    public CodeValueData getMarital() {
		return marital;
	}
    
    public CodeValueData getProfession() {
		return education;
	}
    public CodeValueData getBelonging() {
		return belonging;
	}
    
    public CodeValueData getAnnual() {
		return annual;
	}
    
    public CodeValueData getLand() {
		return land;
	}
    
    public CodeValueData getHouse() {
		return house;
	}
    
    public CodeValueData getForm() {
		return form;
	}
    public CodeValueData getTitle() {
		return title;
	}
    
    public CodeValueData getReligion() {
		return religion;
	}

    public CodeValueData getAlternateNo() {
		return alternateNo;
	}

    public CodeValueData getIdproof() {
		return idproof;
	} 
    
    public CodeValueData getAddrproof() {
		return addrproof;
	}
    
    public String getLastname() {
        return this.lastName;
    }

    public LocalDate getActivationDate() {
        return this.activationDate;
    }
    
    public Boolean getIsAddressEnabled() {
		return this.isAddressEnabled;
	}

    public String age() {
        return this.age;
    }
    
    public String nomrelationshipid() {		
    	return this.nomrelationshipid;		
    }

    public String nomgenderid() {		
    	return this.nomgenderid;		
    }

    public String nomage() {		
    	return this.nomage;		
    }

    public String nomprofessionid() {		
    	return this.nomprofessionid;		
    }

    public String nomeducationalid() {		
    	return this.nomeducationalid;		
    }

    public String nommaritalid() {		
    	return this.nommaritalid;		
    }

    
    public String moblieNo() {
        return this.mobileNo;
    }
    
    public String gstNo() {
        return this.gstNo;
    }
    
    //public String alternateNo() {
       // return this.alternateNo;
    //}
    
   // public Long group() {
    //    return this.group;
    //}
    
   // public Long center() {
    //    return this.center;
    //}
	
    public LocalDate dateOfBirth() {
    	return this.dateOfBirth;
    }
    
    public LocalDate submittedOnDate()
    {
    	return this.submittedOnDate;
    }
    
    public LocalDate ActivationDate() {
        return this.activationDate;
    }
    
  
    
    public Long genderId() {
    	return this.genderId;
    }
    
    
    
    public Long fatherspouseId() {
    	return this.fatherspouseId;
    }

    public Long educationId() {
    	return this.educationId;
    }
    
    public Long maritalId() {
    	return this.maritalId;
    }
    
    public Long professionId() {
    	return this.professionId;
    }
    public Long belongingId() {
    	return this.belongingId;
    }
    
    public Long annualId() {
    	return this.annualId;
    }
    
    public Long landId() {
    	return this.landId;
    }
    
    public Long houseId() {
    	return this.houseId;
    }
    
    public Long formId() {
    	return this.formId;
    }
    public Long titleId() {
    	return this.titleId;
    }
    
    public Long idproofId() {
    	return this.idproofId;
    }
    
    public Long religionId() {
    	return this.religionId;
    }
    
    public Long alternateNoId() {
    	return this.alternateNoId;
    }
           
    public Long staffId() {
    	return this.staffId;
    }
    

    public Long imageId() {
    	return this.imageId;
    }
}