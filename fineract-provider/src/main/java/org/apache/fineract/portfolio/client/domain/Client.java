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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;
import org.apache.fineract.infrastructure.security.service.RandomPasswordGenerator;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.useradministration.domain.AppUser;


import java.time.LocalDate;

import java.time.ZoneId;

import java.time.format.DateTimeFormatter;
// import org.joda.time.LocalDate;
// import org.joda.time.format.DateTimeFormatter;

@Entity
@Table(name = "m_client", uniqueConstraints = { @UniqueConstraint(columnNames = { "account_no" }, name = "account_no_UNIQUE"), //
        @UniqueConstraint(columnNames = { "mobile_no" }, name = "mobile_no_UNIQUE") })
public final class Client extends AbstractPersistableCustom {

    @Column(name = "account_no", length = 20, unique = true, nullable = false)
    private String accountNumber;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @ManyToOne
    @JoinColumn(name = "transfer_to_office_id", nullable = true)
    private Office transferToOffice;

    @OneToOne(optional = true)
    @JoinColumn(name = "image_id", nullable = true)
    private Image image;

    /**
     * A value from {@link ClientStatus}.
     */
    @Column(name = "status_enum", nullable = false)
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_status", nullable = true)
    private CodeValue subStatus;
    
    @Column(name = "activation_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date activationDate;

    @Column(name = "office_joining_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date officeJoiningDate;

    @Column(name = "firstname", length = 50, nullable = true)
    private String firstname;

    @Column(name = "spousename", length = 50, nullable = true)
    private String spousename;

//    @Column(name = "lastname", length = 50, nullable = true)
//    private String lastname;
//    
    @Column(name = "fsfirstname", length = 50, nullable = true)
    private String fsfirstname;

//    @Column(name = "fsmiddlename", length = 50, nullable = true)
//    private String fsmiddlename;
//
//    @Column(name = "fslastname", length = 50, nullable = true)
//    private String fslastname;

    @Column(name = "maidenname", length = 50, nullable = true)
    private String maidenname;

    @Column(name = "custmothername", length = 50, nullable = true)
    private String custmothername;

    @Column(name = "alternatemobileno", length = 50, nullable = true)
    private String alternateMobileNo;

    @Column(name = "secidproofno", length = 50, nullable = true)
    private String secIdProofNo;
    
//    @Column(name = "addressproofno", length = 50, nullable = true)
//    private String addressProofNo;

    @Column(name = "adhar", length = 50, nullable = true)
    private String adhar;
    
    @Column(name = "proposed_transfer_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date proposedTransferDate;

    @Column(name = "nrega", length = 50, nullable = true)
    private String nrega;

    @Column(name = "pan", length = 50, nullable = true)
    private String pan;
   
    @Column(name = "fullname", length = 100, nullable = true)
    private String fullname;

    @Column(name = "display_name", length = 100, nullable = false)
    private String displayName;

    @Column(name = "mobile_no", length = 50, nullable = false, unique = true)
    private String mobileNo;
    
    @Column(name = "gst_no", length = 50, nullable = false, unique = true)
    private String gstNo;
    
    @Column(name = "age", length = 50, nullable = false)
    private String age;
    
    @Column(name = "nom_relationship_id", length = 11, nullable = false)
    private String nomrelationshipid;
    
    @Column(name = "nom_gender_id", length = 11, nullable = false)
    private String nomgenderid;
    
    @Column(name = "nom_age", length = 11, nullable = false)
    private String nomage;
    
    @Column(name = "nom_profession_id", length = 11, nullable = false)
    private String nomprofessionid;
    
    @Column(name = "nom_education_id", length = 11, nullable = false)
    private String nomeducationalid;
    
    @Column(name = "nom_marital_id", length = 11, nullable = false)
    private String nommaritalid;
    
    @Column(name = "inc_daily_sales", length = 50, nullable = false)
    private String aofmOne;
    
    @Column(name = "exp_raw_material", length = 50, nullable = false)
    private String exprawmaterial;
    
    @Column(name = "exp_staff_sal", length = 50, nullable = false)
    private String expstaffsal;
    
    @Column(name = "exp_power_telephone", length = 50, nullable = false)
    private String exppowertelephone;
    
    @Column(name = "exp_repairs_maintainance", length = 50, nullable = false)
    private String exprepairsmaintainance;
    
    @Column(name = "exp_comm_brokerage", length = 50, nullable = false)
    private String expcommbrokerage;
    
    @Column(name = "inc_rent", length = 50, nullable = false)
    private String incrent;
    
    @Column(name = "inc_interest", length = 50, nullable = false)
    private String incinterest;
    
    @Column(name = "inc_others", length = 50, nullable = false)
    private String incothers;
    
    @Column(name = "tot_house_hold_inc", length = 50, nullable = false)
    private String tothouseholdinc;
    
    @Column(name = "exp_household", length = 50, nullable = false)
    private String exphousehold;
    
    @Column(name = "exp_other_loans", length = 50, nullable = false)
    private String expotherloans;
    
    @Column(name = "tot_net_disp_family", length = 50, nullable = false)
    private String totnetdispfamily;
    
  

    
    @Column(name = "exp_interest", length = 50, nullable = false)
    private String expinterest;
	
    @Column(name = "exp_office_rent", length = 50, nullable = false)
    private String expofficerent;
	
    @Column(name = "exp_travel", length = 50, nullable = false)
    private String exptravel;
	
    @Column(name = "exp_others", length = 50, nullable = false)
    private String expothers;
	
    @Column(name = "tot_business_profit", length = 50, nullable = false)
    private String totbusinessprofit;
	
    @Column(name = "inc_spouse", length = 50, nullable = false)
    private String incspouse;
    
	
	@Column(name = "email_address", length = 50, unique = true)
    private String emailAddress;

	@Column(name = "is_staff", nullable = false)
    private boolean isStaff;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;
    
    @Column(name = "external_idd", length = 100, nullable = true, unique = true)
    private String externalIdd;
    
    @Column(name = "idproof_no", length = 50, nullable = true, unique = true)
    private String idproofNo;
    
    @Column(name = "secondary_address_proof_no", length = 50, nullable = true, unique = true)
    private String secaddressproofno;
    
    @Column(name = "last_verified_mobile", length = 100, nullable = true)
    private String lastverifiedmobile;
    
    @Column(name = "other_expenses_tf", length = 50, nullable = true)
    private String otherexpensestf;
    
    @Column(name = "other_src_inc_tf", length = 50, nullable = true)
    private String othersrcinctf;
    
    
    @Column(name = "other_obligations", length = 500, nullable = true)
    private String otherobligations;
    
    @Column(name = "last_verified_secondaryid", length = 50, nullable = true)
    private String lastverifiedsecondaryid;
    
    @Column(name = "last_verified_adhar", length = 50, nullable = true)
    private String lastverifiedadhar;
    
    @Column(name = "date_of_birth", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    
    @Column(name = "last_verified_secondaryid_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date lastverifiedSecondaryidDate;
    
    @Column(name = "last_verified_mobile_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date lastverifiedmobiledate;
    
    
    @Column(name = "last_verified_adhar_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date lastverifiedadhardate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_cv_id", nullable = true)
    private CodeValue gender;
     


    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fatherspouse_fs_id", nullable = true)
    private CodeValue fatherspouse;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_vv_id", nullable = true)
    private CodeValue education;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marital_mm_id", nullable = true)
    private CodeValue marital;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_pp_id", nullable = true)
    private CodeValue profession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belonging_bb_id", nullable = true)
    private CodeValue belonging;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annual_aa_id", nullable = true)
    private CodeValue annual;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_ll_id", nullable = true)
    private CodeValue land;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_hh_id", nullable = true)
    private CodeValue house;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_ff_id", nullable = true)
    private CodeValue form;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_tt_id", nullable = true)
    private CodeValue title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "religion_rr_id", nullable = true)
    private CodeValue religion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alternateno_id", nullable = true)
    private CodeValue alternateNo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproof_dp_id", nullable = true)
    private CodeValue idproof;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addrproof_ap_id", nullable = true)
    private CodeValue addrproof;
     
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "m_group_client", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups;

    @Transient
    private boolean accountNumberRequiresAutoGeneration = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closure_reason_cv_id", nullable = true)
    private CodeValue closureReason;

    @Column(name = "closedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date closureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reject_reason_cv_id", nullable = true)
    private CodeValue rejectionReason;

    @Column(name = "rejectedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date rejectionDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "rejectedon_userid", nullable = true)
    private AppUser rejectedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "withdraw_reason_cv_id", nullable = true)
    private CodeValue withdrawalReason;

    @Column(name = "withdrawn_on_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date withdrawalDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "withdraw_on_userid", nullable = true)
    private AppUser withdrawnBy;

    @Column(name = "reactivated_on_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date reactivateDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "reactivated_on_userid", nullable = true)
    private AppUser reactivatedBy;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "closedon_userid", nullable = true)
    private AppUser closedBy;

    @Column(name = "submittedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date submittedOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "submittedon_userid", nullable = true)
    private AppUser submittedBy;

    @Column(name = "updated_on", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date updatedOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = true)
    private AppUser updatedBy;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "activatedon_userid", nullable = true)
    private AppUser activatedBy;

    @Column(name = "default_savings_product", nullable = true)
    private Long savingsProductId;
    
    @Column(name = "default_savings_account", nullable = true)
    private Long savingsAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_type_cv_id", nullable = true)
    private CodeValue clientType;
    
  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_classification_cv_id", nullable = true)
    private CodeValue clientClassification;
    
    @Column(name = "legal_form_enum", nullable = true)
    private Integer legalForm;

    @Column(name = "reopened_on_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date reopenedDate;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "reopened_by_userid", nullable = true)
    private AppUser reopenedBy;
    
    
    @Column(name ="debt")
    private String debt;
    
    @Column(name ="income")
    private String income;
    
    @Column(name ="debt_inc_ratio")
    private String debtincratio;
    
    @Column(name ="amt_applied")
    private Long AmountApplied;
    
    @Column(name ="cpv_data")
    private Boolean cpv_data;
    
   /* @Column(name = "aadhaar_number", length = 500, nullable = false, unique = true)
    private String aadhaarNo;
    
    
    @Column(name = "group_id", nullable = true)
    private Long group;
    
    
    @Column(name = "center_id", nullable = true)
    private Long center;
    
    @Column(name = "alternate_no", length = 50, nullable = false, unique = true)
    private String alternateNo;*/

    public static Client createNew(final AppUser currentUser, final Office clientOffice, final Group clientParentGroup, final Staff staff,
            final Long savingsProductId, final CodeValue gender, final CodeValue statusOne, final CodeValue statusTwo,final CodeValue introducer,final CodeValue sourceone,final CodeValue sourcetwo,final CodeValue purposeone,final CodeValue purposetwo,final CodeValue fatherspouse, final CodeValue education,final CodeValue marital,final CodeValue profession,final CodeValue belonging,final CodeValue annual, 
            final CodeValue land,final CodeValue house,final CodeValue form,final CodeValue title,final CodeValue religion, final CodeValue alternateNo, final CodeValue idproof,final CodeValue addrproof,
           final CodeValue clientType, final CodeValue clientClassification,
            final Integer legalForm, final JsonCommand command) {

        final String accountNo = command.stringValueOfParameterNamed(ClientApiConstants.accountNoParamName);
        final String externalId = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);
        final String externalIdd = command.stringValueOfParameterNamed(ClientApiConstants.externalIddParamName);
        final String idproofNo = command.stringValueOfParameterNamed(ClientApiConstants.idproofNoParamName);
        final String addrproofNo = command.stringValueOfParameterNamed(ClientApiConstants.addrproofNoParamName);
        final String mobileNo = command.stringValueOfParameterNamed(ClientApiConstants.mobileNoParamName);
        final String gstNo = command.stringValueOfParameterNamed(ClientApiConstants.gstNoParamName);
        final String age = command.stringValueOfParameterNamed(ClientApiConstants.ageParamName);
        final String nomrelationshipid = command.stringValueOfParameterNamed(ClientApiConstants.nomrelationshipidParamName);
        final String nomgenderid = command.stringValueOfParameterNamed(ClientApiConstants.nomgenderidParamName);
        final String nomage = command.stringValueOfParameterNamed(ClientApiConstants.nomageParamName);
        final String nomprofessionid = command.stringValueOfParameterNamed(ClientApiConstants.nomprofessionidParamName);
        final String nomeducationalid = command.stringValueOfParameterNamed(ClientApiConstants.nomeducationalidParamName);
        final String nommaritalid = command.stringValueOfParameterNamed(ClientApiConstants.nommaritalidParamName);
        final String aofmOne = command.stringValueOfParameterNamed(ClientApiConstants.incdailysalesParamName);
        final String exprawmaterial = command.stringValueOfParameterNamed(ClientApiConstants.exprawmaterialParamName);
        final String expstaffsal = command.stringValueOfParameterNamed(ClientApiConstants.expstaffsalParamName);
        final String exppowertelephone = command.stringValueOfParameterNamed(ClientApiConstants.exppowertelephoneParamName);
        final String exprepairsmaintainance = command.stringValueOfParameterNamed(ClientApiConstants.exprepairsmaintainanceParamName);
        final String expcommbrokerage = command.stringValueOfParameterNamed(ClientApiConstants.expcommbrokerageParamName);
        final String incrent = command.stringValueOfParameterNamed(ClientApiConstants.increntParamName);
        final String incinterest = command.stringValueOfParameterNamed(ClientApiConstants.incinterestParamName);
        final String incothers = command.stringValueOfParameterNamed(ClientApiConstants.incothersParamName);
        final String tothouseholdinc = command.stringValueOfParameterNamed(ClientApiConstants.tothouseholdincParamName);
        final String exphousehold = command.stringValueOfParameterNamed(ClientApiConstants.exphouseholdParamName);
        final String expotherloans = command.stringValueOfParameterNamed(ClientApiConstants.expotherloansParamName);
        final String totnetdispfamily = command.stringValueOfParameterNamed(ClientApiConstants.totnetdispfamilyParamName);

        final String expinterest = command.stringValueOfParameterNamed(ClientApiConstants.expinterestParamName);
        final String expofficerent = command.stringValueOfParameterNamed(ClientApiConstants.expofficerentParamName);
        final String exptravel = command.stringValueOfParameterNamed(ClientApiConstants.exptravelParamName);
        final String expothers = command.stringValueOfParameterNamed(ClientApiConstants.expothersParamName);
        final String totbusinessprofit = command.stringValueOfParameterNamed(ClientApiConstants.totbusinessprofitParamName);
        final String incspouse = command.stringValueOfParameterNamed(ClientApiConstants.incspouseParamName);
		final String emailAddress = command.stringValueOfParameterNamed(ClientApiConstants.emailAddressParamName);
		//final String alternateNo = command.stringValueOfParameterNamed(ClientApiConstants.alternateNoParamName);
        final String firstname = command.stringValueOfParameterNamed(ClientApiConstants.firstnameParamName);
        final String spousename = command.stringValueOfParameterNamed(ClientApiConstants.spousenameParamName);
        //final String lastname = command.stringValueOfParameterNamed(ClientApiConstants.lastnameParamName);
        final String fsfirstname = command.stringValueOfParameterNamed(ClientApiConstants.fsfirstnameParamName);
        final String fsmiddlename = command.stringValueOfParameterNamed(ClientApiConstants.fsmiddlenameParamName);
       // final String fslastname = command.stringValueOfParameterNamed(ClientApiConstants.fslastnameParamName);
        final String maidenname = command.stringValueOfParameterNamed(ClientApiConstants.maidennameParamName);
        final String custmothername = command.stringValueOfParameterNamed(ClientApiConstants.custmothernameParamName);

        final String alternateMobileNo = command.stringValueOfParameterNamed(ClientApiConstants.alternateMobileNoParamName);        
        final String secIdProofNo = command.stringValueOfParameterNamed(ClientApiConstants.secIdProofNoParamName);
        final String secaddressproofno = command.stringValueOfParameterNamed(ClientApiConstants.secaddressproofnoParamName);
        final String lastverifiedmobile = command.stringValueOfParameterNamed(ClientApiConstants.lastverifiedmobileParamName);
        final String otherexpensestf = command.stringValueOfParameterNamed(ClientApiConstants.otherexpensestfParamName);
        final String othersrcinctf = command.stringValueOfParameterNamed(ClientApiConstants.othersrcinctfParamName);
        final String otherobligations = command.stringValueOfParameterNamed(ClientApiConstants.otherobligationsParamName);
        final String lastverifiedsecondaryid = command.stringValueOfParameterNamed(ClientApiConstants.lastverifiedsecondaryidParamName);
        final String lastverifiedadhar = command.stringValueOfParameterNamed(ClientApiConstants.lastverifiedadharParamName);
       // final String addressProofNo = command.stringValueOfParameterNamed(ClientApiConstants.addressProofNoParamName);

        final String adhar = command.stringValueOfParameterNamed(ClientApiConstants.adharParamName);
        final String nrega = command.stringValueOfParameterNamed(ClientApiConstants.nregaParamName);
        final String pan = command.stringValueOfParameterNamed(ClientApiConstants.panParamName);
        final String fullname = command.stringValueOfParameterNamed(ClientApiConstants.fullnameParamName);
		
		final boolean isStaff = command.booleanPrimitiveValueOfParameterNamed(ClientApiConstants.isStaffParamName);

        final LocalDate dataOfBirth = command.localDateValueOfParameterNamed(ClientApiConstants.dateOfBirthParamName);
        final LocalDate nomdataOfBirth = command.localDateValueOfParameterNamed(ClientApiConstants.lastverifiedSecondaryidDateParamName);
        final LocalDate lastverifiedmobiledate = command.localDateValueOfParameterNamed(ClientApiConstants.lastverifiedmobiledateParamName);
        final LocalDate lastverifiedadhardate = command.localDateValueOfParameterNamed(ClientApiConstants.lastverifiedadhardateParamName);
        
        final String debt = command.stringValueOfParameterNamed(ClientApiConstants.debtParamName);
        final String income = command.stringValueOfParameterNamed(ClientApiConstants.incomeParamName);
        final String debtincratio = command.stringValueOfParameterNamed(ClientApiConstants.debtincratioParamName);
        final Long AmountApplied = command.longValueOfParameterNamed(ClientApiConstants.AmountAppliedParamName);
        //final String aadhaarNo = command.stringValueOfParameterNamed(ClientApiConstants.aadhaarNoParamName);
       // final Long group = command.longValueOfParameterNamed(ClientApiConstants.groupParamName);
        //final Long center = command.longValueOfParameterNamed(ClientApiConstants.centerParamName);
        ClientStatus status = ClientStatus.PENDING;
        boolean active = false;
        if (command.hasParameter("active")) {
            active = command.booleanPrimitiveValueOfParameterNamed(ClientApiConstants.activeParamName);
        }

        LocalDate activationDate = null;
        LocalDate officeJoiningDate = null;
        if (active) {
            status = ClientStatus.ACTIVE;
            activationDate = command.localDateValueOfParameterNamed(ClientApiConstants.activationDateParamName);
            officeJoiningDate = activationDate;
        }

        LocalDate submittedOnDate = LocalDate.now(DateUtils.getDateTimeZoneOfTenant());
        if (active && submittedOnDate.isAfter(activationDate)) {
            submittedOnDate = activationDate;
        }
        if (command.hasParameter(ClientApiConstants.submittedOnDateParamName)) {
            submittedOnDate = command.localDateValueOfParameterNamed(ClientApiConstants.submittedOnDateParamName);
        }
        final Long savingsAccountId = null;
        return new Client(currentUser, status, clientOffice, clientParentGroup, accountNo, firstname,
        		fsfirstname,maidenname, custmothername, alternateMobileNo, secIdProofNo,secaddressproofno,lastverifiedmobile,otherexpensestf,othersrcinctf,otherobligations,lastverifiedsecondaryid,lastverifiedadhar, adhar, nrega,pan, fullname,
                activationDate, officeJoiningDate, externalId, externalIdd, mobileNo,gstNo,age,  nomrelationshipid,
                nomgenderid,
                nomage,
               nomprofessionid,
                nomeducationalid,
               nommaritalid,
               aofmOne,exprawmaterial,expstaffsal,
                exppowertelephone,exprepairsmaintainance,expcommbrokerage,incrent,incinterest,incothers,tothouseholdinc,exphousehold,expotherloans,totnetdispfamily,expofficerent,
                exptravel,expinterest,expothers,totbusinessprofit,incspouse,idproofNo, addrproofNo, emailAddress, staff, submittedOnDate, savingsProductId, savingsAccountId, dataOfBirth,nomdataOfBirth,
                lastverifiedmobiledate, lastverifiedadhardate, gender,statusOne, statusTwo,introducer,sourceone,sourcetwo,purposeone,purposetwo,fatherspouse,education,marital, profession, belonging,
                annual,land,house, form, title, religion, alternateNo, idproof,addrproof, clientType,  clientClassification, legalForm, isStaff,debt,income,debtincratio,AmountApplied,spousename);
    }

    Client() {
        this.setLegalForm(null);
    }

    private Client(final AppUser currentUser, final ClientStatus status, final Office office, final Group clientParentGroup,
            final String accountNo, final String firstname,
            final String fsfirstname, 
            final String maidenname, final String custmothername, final String alternateMobileNo, final String secIdProofNo,final String secaddressproofno,final String lastverifiedmobile,final String otherexpensestf,final String othersrcinctf,final String otherobligations,final String lastverifiedsecondaryid,final String lastverifiedadhar, final String adhar, final String nrega, final String pan,final String fullname,
            final LocalDate activationDate, final LocalDate officeJoiningDate, final String externalId, final String externalIdd, 
            final String mobileNo,final String gstNo,final String age,final String nomrelationshipid,
            final String nomgenderid,
            final String nomage,
            final String nomprofessionid,
            final String nomeducationalid,
            final String nommaritalid,final String aofmOne,final String exprawmaterial,final String expstaffsal,
            final String exppowertelephone,final String exprepairsmaintainance,final String expcommbrokerage,
            final String incrent,final String incinterest,final String incothers, final String tothouseholdinc,final String exphousehold,final String expotherloans,
            final String totnetdispfamily,final String expofficerent,
            final String exptravel,final String expinterest, 
            final String expothers,final String totbusinessprofit,final String incspouse, final String idproofNo,final String addrproofNo, final String emailAddress,
            final Staff staff, final LocalDate submittedOnDate, final Long savingsProductId, final Long savingsAccountId,
            final LocalDate dateOfBirth,final LocalDate lastverifiedSecondaryidDate,final LocalDate lastverifiedmobiledate,final LocalDate lastverifiedadhardate, final CodeValue gender,final CodeValue statusOne,final CodeValue statusTwo,final CodeValue introducer,final CodeValue sourceone,final CodeValue sourcetwo,final CodeValue purposeone,final CodeValue purposetwo,final CodeValue fatherspouse,final CodeValue education,final CodeValue marital,final CodeValue profession,final CodeValue belonging,final CodeValue annual,
            final CodeValue land,final CodeValue house,final CodeValue form,final CodeValue title,final CodeValue religion, final CodeValue alternateNo, final CodeValue idproof,final CodeValue addrproof,
            final CodeValue clientType, final CodeValue clientClassification, 
            final Integer legalForm, final Boolean isStaff,final String debt,final String income,final String debtincratio,final Long AmountApplied,final String spousename) {

        if (StringUtils.isBlank(accountNo)) {
            this.accountNumber = new RandomPasswordGenerator(19).generate();
            this.accountNumberRequiresAutoGeneration = true;
        } else {
            this.accountNumber = accountNo;
        }

        this.submittedOnDate = Date.from(submittedOnDate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        this.submittedBy = currentUser;

        this.status = status.getValue();
        this.office = office;
        if (StringUtils.isNotBlank(externalId)) {
            this.externalId = externalId.trim();
        } else {
            this.externalId = null;
        }
                
        if (StringUtils.isNotBlank(externalIdd)) {
            this.externalIdd = externalIdd.trim();
        } else {
            this.externalIdd = null;
        }
        
                
        //this.group = group;
       // this.center = center;

        if (StringUtils.isNotBlank(mobileNo)) {
            this.mobileNo = mobileNo.trim();
        } else {
            this.mobileNo = null;
        }
        
        if (StringUtils.isNotBlank(spousename)) {
            this.spousename = spousename.trim();
        } else {
            this.spousename = null;
        }
        
        if (StringUtils.isNotBlank(gstNo)) {
            this.gstNo = gstNo.trim();
        } else {
            this.gstNo = null;
        }
        
        if (StringUtils.isNotBlank(age)) {
            this.age = age.trim();
        } else {
            this.age = null;
        }
        
        if (StringUtils.isNotBlank(nomrelationshipid)) {
            this.nomrelationshipid = nomrelationshipid.trim();
        } else {
            this.nomrelationshipid = null;
        }
        
        if (StringUtils.isNotBlank(nomgenderid)) {
            this.nomgenderid = nomgenderid.trim();
        } else {
            this.nomgenderid = null;
        }
        
        if (StringUtils.isNotBlank(nomage)) {
            this.nomage = nomage.trim();
        } else {
            this.nomage = null;
        }
        if (StringUtils.isNotBlank(nomprofessionid)) {
            this.nomprofessionid = nomprofessionid.trim();
        } else {
            this.nomprofessionid = null;
        }
        if (StringUtils.isNotBlank(nomeducationalid)) {
            this.nomeducationalid = nomeducationalid.trim();
        } else {
            this.nomeducationalid = null;
        }
        if (StringUtils.isNotBlank(nommaritalid)) {
            this.nommaritalid = nommaritalid.trim();
        } else {
            this.nommaritalid = null;
        }
        
        
        if (StringUtils.isNotBlank(aofmOne)) {
            this.aofmOne = aofmOne.trim();
        } else {
            this.aofmOne = null;
        }
        

        if (StringUtils.isNotBlank(exprawmaterial)) {
            this.exprawmaterial = exprawmaterial.trim();
        } else {
            this.exprawmaterial = null;
        }

        if (StringUtils.isNotBlank(expstaffsal)) {
            this.expstaffsal = expstaffsal.trim();
        } else {
            this.expstaffsal = null;
        }
       

        if (StringUtils.isNotBlank(exppowertelephone)) {
            this.exppowertelephone = exppowertelephone.trim();
        } else {
            this.exppowertelephone = null;
        }
        
        if (StringUtils.isNotBlank(exprepairsmaintainance)) {
            this.exprepairsmaintainance = exprepairsmaintainance.trim();
        } else {
            this.exprepairsmaintainance = null;
        }
        
        if (StringUtils.isNotBlank(expcommbrokerage)) {
            this.expcommbrokerage = expcommbrokerage.trim();
        } else {
            this.expcommbrokerage = null;
        }
        
        if (StringUtils.isNotBlank(incrent)) {
            this.incrent = incrent.trim();
        } else {
            this.incrent = null;
        }
        
        if (StringUtils.isNotBlank(incinterest)) {
            this.incinterest = incinterest.trim();
        } else {
            this.incinterest = null;
        }
        
        if (StringUtils.isNotBlank(incothers)) {
            this.incothers = incothers.trim();
        } else {
            this.incothers = null;
        }
        
        if (StringUtils.isNotBlank(tothouseholdinc)) {
            this.tothouseholdinc = tothouseholdinc.trim();
        } else {
            this.tothouseholdinc = null;
        }
        
        if (StringUtils.isNotBlank(exphousehold)) {
            this.exphousehold = exphousehold.trim();
        } else {
            this.exphousehold = null;
        }
        
        if (StringUtils.isNotBlank(expotherloans)) {
            this.expotherloans = expotherloans.trim();
        } else {
            this.expotherloans = null;
        }
        
        if (StringUtils.isNotBlank(totnetdispfamily)) {
            this.totnetdispfamily = totnetdispfamily.trim();
        } else {
            this.totnetdispfamily = null;
        }
        
      
    
        
        if (StringUtils.isNotBlank(idproofNo)) {
            this.idproofNo = idproofNo.trim();
        } else {
            this.idproofNo = null;
        }
   
        if (StringUtils.isNotBlank(expinterest)) {
            this.expinterest = expinterest.trim();
        } else {
            this.expinterest = null;
        }
        if (StringUtils.isNotBlank(expofficerent)) {
            this.expofficerent = expofficerent.trim();
        } else {
            this.expofficerent = null;
        }
        if (StringUtils.isNotBlank(exptravel)) {
            this.exptravel = exptravel.trim();
        } else {
            this.exptravel = null;
        }
        if (StringUtils.isNotBlank(expothers)) {
            this.expothers = expothers.trim();
        } else {
            this.expothers = null;
        }
        if (StringUtils.isNotBlank(totbusinessprofit)) {
            this.totbusinessprofit = totbusinessprofit.trim();
        } else {
            this.totbusinessprofit = null;
        }
        if (StringUtils.isNotBlank(incspouse)) {
            this.incspouse = incspouse.trim();
        } else {
            this.incspouse = null;
        }
              
 		if (StringUtils.isNotBlank(emailAddress)) {
            this.emailAddress = emailAddress.trim();
        } else {
            this.emailAddress = null;
        }

        if (activationDate != null) {
            this.activationDate = Date.from(activationDate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
            this.activatedBy = currentUser;
        }
        if (officeJoiningDate != null) {
            this.officeJoiningDate = Date.from(officeJoiningDate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        if (StringUtils.isNotBlank(firstname)) {
            this.firstname = firstname.trim();
        } else {
            this.firstname = null;
        }

//        if (StringUtils.isNotBlank(middlename)) {
//            this.middlename = middlename.trim();
//        } else {
//            this.middlename = null;
//        }

//        if (StringUtils.isNotBlank(lastname)) {
//            this.lastname = lastname.trim();
//        } else {
//            this.lastname = null;
//        }
        
        if (StringUtils.isNotBlank(fsfirstname)) {
            this.fsfirstname = fsfirstname.trim();
        } else {
            this.fsfirstname = null;
        }

//        if (StringUtils.isNotBlank(fsmiddlename)) {
//            this.fsmiddlename = fsmiddlename.trim();
//        } else {
//            this.fsmiddlename = null;
//        }

//        if (StringUtils.isNotBlank(fslastname)) {
//            this.fslastname = fslastname.trim();
//        } else {
//            this.fslastname = null;
//        }
        
        if (StringUtils.isNotBlank(maidenname)) {
            this.maidenname = maidenname.trim();
        } else {
            this.maidenname = null;
        }

        if (StringUtils.isNotBlank(custmothername)) {
            this.custmothername = custmothername.trim();
        } else {
            this.custmothername = null;
        }

        if (StringUtils.isNotBlank(alternateMobileNo)) {
            this.alternateMobileNo = alternateMobileNo.trim();
        } else {
            this.alternateMobileNo = null;
        }
        if (StringUtils.isNotBlank(secIdProofNo)) {
            this.secIdProofNo = secIdProofNo.trim();
        } else {
            this.secIdProofNo = null;
        }
        
        if (StringUtils.isNotBlank(secaddressproofno)) {
            this.secaddressproofno = secaddressproofno.trim();
        } else {
            this.secaddressproofno = null;
        }
        
        if (StringUtils.isNotBlank(lastverifiedmobile)) {
            this.lastverifiedmobile = lastverifiedmobile.trim();
        } else {
            this.lastverifiedmobile = null;
        }
        if (StringUtils.isNotBlank(othersrcinctf)) {
            this.othersrcinctf = othersrcinctf.trim();
        } else {
            this.othersrcinctf = null;
        }
        if (StringUtils.isNotBlank(otherexpensestf)) {
            this.otherexpensestf = otherexpensestf.trim();
        } else {
            this.otherexpensestf = null;
        }
        if (StringUtils.isNotBlank(otherobligations)) {
            this.otherobligations = otherobligations.trim();
        } else {
            this.otherobligations = null;
        }
        
        if (StringUtils.isNotBlank(lastverifiedsecondaryid)) {
            this.lastverifiedsecondaryid = lastverifiedsecondaryid.trim();
        } else {
            this.lastverifiedsecondaryid = null;
        }
        
        if (StringUtils.isNotBlank(lastverifiedadhar)) {
            this.lastverifiedadhar = lastverifiedadhar.trim();
        } else {
            this.lastverifiedadhar = null;
        }
//        if (StringUtils.isNotBlank(addressProofNo)) {
//            this.addressProofNo = addressProofNo.trim();
//        } else {
//            this.addressProofNo = null;
//        }

        if (StringUtils.isNotBlank(adhar)) {
            this.adhar = adhar.trim();
        } else {
            this.adhar = null;
        }
        
        if (StringUtils.isNotBlank(nrega)) {
            this.nrega = nrega.trim();
        } else {
            this.nrega = null;
        }

        if (StringUtils.isNotBlank(pan)) {
            this.pan = pan.trim();
        } else {
            this.pan = null;
        }

        if (StringUtils.isNotBlank(fullname)) {
            this.fullname = fullname.trim();
        } else {
            this.fullname = null;
        }

        if (clientParentGroup != null) {
            this.groups = new HashSet<>();
            this.groups.add(clientParentGroup);
        }

        this.staff = staff;
        this.savingsProductId = savingsProductId;
        this.savingsAccountId = savingsAccountId;

        if (gender != null) {
            this.gender = gender;
        }
        
        if (fatherspouse != null) {
            this.fatherspouse = fatherspouse;
        }
        
        if (education != null) {
            this.education = education;
        }
        
        if (marital != null) {
            this.marital = marital;
        }
        
        if (profession != null) {
            this.profession = profession;
        }
        if (belonging != null) {
            this.belonging = belonging;
        }
        
        if (annual != null) {
            this.annual = annual;
        }
        
        if (land != null) {
            this.land = land;
        }
        
        if (house != null) {
            this.house = house;
        }
        
        if (form != null) {
            this.form = form;
        }
        if (title != null) {
            this.title = title;
        }
        
        if (religion != null) {
            this.religion = religion;
        }
        
        if (alternateNo != null) {
            this.alternateNo = alternateNo;
        }

        if (idproof != null) {
            this.idproof = idproof;
        }
        
        if (addrproof != null) {
            this.addrproof = addrproof;
        }
        
        
        
        if (dateOfBirth != null) {
            this.dateOfBirth = Date.from(dateOfBirth.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        
        if (lastverifiedSecondaryidDate != null) {
            this.lastverifiedSecondaryidDate =  Date.from(lastverifiedSecondaryidDate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        
        
        if (lastverifiedmobiledate != null) {
            this.lastverifiedmobiledate =  Date.from(lastverifiedmobiledate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        
        if (lastverifiedadhardate != null) {
            this.lastverifiedadhardate = Date.from(lastverifiedadhardate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        
        if (StringUtils.isNotBlank(debt)) {
            this.debt = debt.trim();
        }
        else
        {
        	this.debt = null;
        }
      
        if (StringUtils.isNotBlank(income)) {
            this.income = income.trim();
        }
        else
        {
        	this.income = null;
        }
     
        
        if (StringUtils.isNotBlank(debtincratio)) {
            this.debtincratio = debtincratio.trim();
        }
        else
        {
        	this.debtincratio = null;
        }
     
        this.AmountApplied = AmountApplied;
        this.clientType = clientType;
        this.clientClassification = clientClassification;
        this.setLegalForm(legalForm);

        deriveDisplayName();
        validate();
    }

    private void validate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        validateNameParts(dataValidationErrors);
        validateActivationDate(dataValidationErrors);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }
    
    private void validateUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        //Not validating name parts while update request as firstname/lastname can be along with fullname 
        //when we change clientType from Individual to Organisation or vice-cersa
        validateActivationDate(dataValidationErrors);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }

    public boolean isAccountNumberRequiresAutoGeneration() {
        return this.accountNumberRequiresAutoGeneration;
    }

    public void setAccountNumberRequiresAutoGeneration(final boolean accountNumberRequiresAutoGeneration) {
        this.accountNumberRequiresAutoGeneration = accountNumberRequiresAutoGeneration;
    }

    public boolean identifiedBy(final String identifier) {
        return identifier.equalsIgnoreCase(this.externalId);
    }
       
    public boolean identifiedBy(final Long clientId) {
        return getId().equals(clientId);
    }

    public void updateAccountNo(final String accountIdentifier) {
        this.accountNumber = accountIdentifier;
        this.accountNumberRequiresAutoGeneration = false;
    }

    public void activate(final AppUser currentUser, final DateTimeFormatter formatter, final LocalDate activationLocalDate) {

        if (isActive()) {
            final String defaultUserMessage = "Cannot activate client. Client is already active.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.already.active", defaultUserMessage,
                    ClientApiConstants.activationDateParamName, activationLocalDate.format(formatter));

            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }

        this.activationDate = Date.from(activationLocalDate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        this.activatedBy = currentUser;
        this.officeJoiningDate = this.activationDate;
        if (this.getStatus().equals(ClientStatus.INPROGRESS.getValue()))
            this.status = ClientStatus.ACTIVE.getValue();
        else
            this.status = ClientStatus.INPROGRESS.getValue();

        
        // in case a closed client is being re open
        this.closureDate = null;
        this.closureReason = null;
        this.closedBy = null;

        validate();
    }

    public boolean isNotActive() {
        return !isActive();
    }

    public boolean isActive() {
        return ClientStatus.fromInt(this.status).isActive();
    }

    public boolean isClosed() {
        return ClientStatus.fromInt(this.status).isClosed();
    }

    public boolean isTransferInProgress() {
        return ClientStatus.fromInt(this.status).isTransferInProgress();
    }

    public boolean isTransferOnHold() {
        return ClientStatus.fromInt(this.status).isTransferOnHold();
    }

    public boolean isTransferInProgressOrOnHold() {
        return isTransferInProgress() || isTransferOnHold();
    }

    public boolean isNotPending() {
        return !isPending();
    }

    public boolean isPending() {
        return ClientStatus.fromInt(this.status).isPending();
    }

    private boolean isDateInTheFuture(final LocalDate localDate) {
        return localDate.isAfter(DateUtils.getLocalDateOfTenant());
    }
    
    public boolean isRejected() {
        return ClientStatus.fromInt(this.status).isRejected();
    }
    
    public boolean isWithdrawn() {
        return ClientStatus.fromInt(this.status).isWithdrawn();
    }
    
    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.statusParamName, this.status)) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.statusParamName);
            actualChanges.put(ClientApiConstants.statusParamName, ClientEnumerations.status(newValue));
            this.status = ClientStatus.fromInt(newValue).getValue();
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.accountNoParamName, this.accountNumber)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.accountNoParamName);
            actualChanges.put(ClientApiConstants.accountNoParamName, newValue);
            this.accountNumber = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInBooleanParameterNamed(ClientApiConstants.cpvdataParamName, this.cpv_data)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(ClientApiConstants.cpvdataParamName);
            actualChanges.put(ClientApiConstants.cpvdataParamName, newValue);
            this.cpv_data = newValue;
        }


        if (command.isChangeInStringParameterNamed(ClientApiConstants.externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);
            actualChanges.put(ClientApiConstants.externalIdParamName, newValue);
            this.externalId = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.debtParamName, this.debt)) {
        	final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.debtParamName);
            actualChanges.put(ClientApiConstants.debtParamName, newValue);
            this.debt = StringUtils.defaultIfEmpty(newValue, null);
            
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.incomeParamName, this.income)) {
        	final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.incomeParamName);
            actualChanges.put(ClientApiConstants.incomeParamName, newValue);
            this.income = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.debtincratioParamName, this.debtincratio)) {
        	final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.debtincratioParamName);
            actualChanges.put(ClientApiConstants.debtincratioParamName, newValue);
            this.debtincratio = StringUtils.defaultIfEmpty(newValue, null);
          
        }
        
//        if (command.isChangeInStringParameterNamed(ClientApiConstants.idproofnumIdParamName, this.idproofnumId)) {
//            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.idproofnumIdParamName);
//            actualChanges.put(ClientApiConstants.idproofnumIdParamName, newValue);
//            this.idproofnumId = StringUtils.defaultIfEmpty(newValue, null);
//        }
//
//        if (command.isChangeInStringParameterNamed(ClientApiConstants.addrproofnumIdParamName, this.addrproofnumId)) {
//            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.addrproofnumIdParamName);
//            actualChanges.put(ClientApiConstants.addrproofnumIdParamName, newValue);
//            this.addrproofnumId = StringUtils.defaultIfEmpty(newValue, null);
//        }
//        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.mobileNoParamName, this.mobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.mobileNoParamName);
            actualChanges.put(ClientApiConstants.mobileNoParamName, newValue);
            this.mobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.gstNoParamName, this.gstNo)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.gstNoParamName);
            actualChanges.put(ClientApiConstants.gstNoParamName, newValue);
            this.gstNo = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.ageParamName, this.age)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.ageParamName);
            actualChanges.put(ClientApiConstants.ageParamName, newValue);
            this.age = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.nomrelationshipidParamName, this.nomrelationshipid)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.nomrelationshipidParamName);
            actualChanges.put(ClientApiConstants.nomrelationshipidParamName, newValue);
            this.nomrelationshipid = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.nomgenderidParamName, this.nomgenderid)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.nomgenderidParamName);
            actualChanges.put(ClientApiConstants.nomgenderidParamName, newValue);
            this.nomgenderid = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.nomageParamName, this.nomage)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.nomageParamName);
            actualChanges.put(ClientApiConstants.nomageParamName, newValue);
            this.nomage = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.nomprofessionidParamName, this.nomprofessionid)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.nomprofessionidParamName);
            actualChanges.put(ClientApiConstants.nomprofessionidParamName, newValue);
            this.nomprofessionid = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.nomeducationalidParamName, this.nomeducationalid)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.nomeducationalidParamName);
            actualChanges.put(ClientApiConstants.nomeducationalidParamName, newValue);
            this.nomeducationalid = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.nommaritalidParamName, this.nommaritalid)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.nommaritalidParamName);
            actualChanges.put(ClientApiConstants.nommaritalidParamName, newValue);
            this.nommaritalid = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.incdailysalesParamName, this.aofmOne)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.incdailysalesParamName);
            actualChanges.put(ClientApiConstants.incdailysalesParamName, newValue);
            this.aofmOne = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.exprawmaterialParamName, this.exprawmaterial)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.exprawmaterialParamName);
            actualChanges.put(ClientApiConstants.exprawmaterialParamName, newValue);
            this.exprawmaterial = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.expstaffsalParamName, this.expstaffsal)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.expstaffsalParamName);
            actualChanges.put(ClientApiConstants.expstaffsalParamName, newValue);
            this.expstaffsal = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.exppowertelephoneParamName, this.exppowertelephone)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.exppowertelephoneParamName);
            actualChanges.put(ClientApiConstants.exppowertelephoneParamName, newValue);
            this.exppowertelephone = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.exprepairsmaintainanceParamName, this.exprepairsmaintainance)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.exprepairsmaintainanceParamName);
            actualChanges.put(ClientApiConstants.exprepairsmaintainanceParamName, newValue);
            this.exprepairsmaintainance = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.expcommbrokerageParamName, this.expcommbrokerage)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.expcommbrokerageParamName);
            actualChanges.put(ClientApiConstants.expcommbrokerageParamName, newValue);
            this.expcommbrokerage = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.increntParamName, this.incrent)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.increntParamName);
            actualChanges.put(ClientApiConstants.increntParamName, newValue);
            this.incrent = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.totnetdispfamilyParamName, this.totnetdispfamily)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.totnetdispfamilyParamName);
            actualChanges.put(ClientApiConstants.totnetdispfamilyParamName, newValue);
            this.totnetdispfamily = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.incinterestParamName, this.incinterest)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.incinterestParamName);
            actualChanges.put(ClientApiConstants.incinterestParamName, newValue);
            this.incinterest = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.incothersParamName, this.incothers)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.incothersParamName);
            actualChanges.put(ClientApiConstants.incothersParamName, newValue);
            this.incothers = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.tothouseholdincParamName, this.tothouseholdinc)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.tothouseholdincParamName);
            actualChanges.put(ClientApiConstants.tothouseholdincParamName, newValue);
            this.tothouseholdinc = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.exphouseholdParamName, this.exphousehold)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.exphouseholdParamName);
            actualChanges.put(ClientApiConstants.exphouseholdParamName, newValue);
            this.exphousehold = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.expotherloansParamName, this.expotherloans)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.expotherloansParamName);
            actualChanges.put(ClientApiConstants.expotherloansParamName, newValue);
            this.expotherloans = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.idproofNoParamName, this.idproofNo)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.idproofNoParamName);
            actualChanges.put(ClientApiConstants.idproofNoParamName, newValue);
            this.idproofNo = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.expinterestParamName, this.expinterest)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.expinterestParamName);
            actualChanges.put(ClientApiConstants.expinterestParamName, newValue);
            this.expinterest = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.expofficerentParamName, this.expofficerent)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.expofficerentParamName);
            actualChanges.put(ClientApiConstants.expofficerentParamName, newValue);
            this.expofficerent = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.exptravelParamName, this.exptravel)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.exptravelParamName);
            actualChanges.put(ClientApiConstants.exptravelParamName, newValue);
            this.exptravel = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.expothersParamName, this.expothers)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.expothersParamName);
            actualChanges.put(ClientApiConstants.expothersParamName, newValue);
            this.expothers = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.totbusinessprofitParamName, this.totbusinessprofit)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.totbusinessprofitParamName);
            actualChanges.put(ClientApiConstants.totbusinessprofitParamName, newValue);
            this.totbusinessprofit = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.incspouseParamName, this.incspouse)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.incspouseParamName);
            actualChanges.put(ClientApiConstants.incspouseParamName, newValue);
            this.incspouse = StringUtils.defaultIfEmpty(newValue, null);
        }
        
                             
		if (command.isChangeInStringParameterNamed(ClientApiConstants.emailAddressParamName, this.emailAddress)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.emailAddressParamName);
            actualChanges.put(ClientApiConstants.emailAddressParamName, newValue);
            this.emailAddress = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.firstnameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.firstnameParamName);
            actualChanges.put(ClientApiConstants.firstnameParamName, newValue);
            this.firstname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.spousenameParamName, this.spousename)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.spousenameParamName);
            actualChanges.put(ClientApiConstants.spousenameParamName, newValue);
            this.spousename = StringUtils.defaultIfEmpty(newValue, null);
        }

//        if (command.isChangeInStringParameterNamed(ClientApiConstants.lastnameParamName, this.lastname)) {
//            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.lastnameParamName);
//            actualChanges.put(ClientApiConstants.lastnameParamName, newValue);
//            this.lastname = StringUtils.defaultIfEmpty(newValue, null);
//        }
//        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.fsfirstnameParamName, this.fsfirstname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.fsfirstnameParamName);
            actualChanges.put(ClientApiConstants.fsfirstnameParamName, newValue);
            this.fsfirstname = StringUtils.defaultIfEmpty(newValue, null);
        }

//        if (command.isChangeInStringParameterNamed(ClientApiConstants.fsmiddlenameParamName, this.fsmiddlename)) {
//            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.fsmiddlenameParamName);
//            actualChanges.put(ClientApiConstants.fsmiddlenameParamName, newValue);
//            this.fsmiddlename = StringUtils.defaultIfEmpty(newValue, null);
//        }

//        if (command.isChangeInStringParameterNamed(ClientApiConstants.fslastnameParamName, this.fslastname)) {
//            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.fslastnameParamName);
//            actualChanges.put(ClientApiConstants.fslastnameParamName, newValue);
//            this.fslastname = StringUtils.defaultIfEmpty(newValue, null);
//        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.maidennameParamName, this.maidenname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.maidennameParamName);
            actualChanges.put(ClientApiConstants.maidennameParamName, newValue);
            this.maidenname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.custmothernameParamName, this.custmothername)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.custmothernameParamName);
            actualChanges.put(ClientApiConstants.custmothernameParamName, newValue);
            this.custmothername = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.alternateMobileNoParamName, this.alternateMobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.alternateMobileNoParamName);
            actualChanges.put(ClientApiConstants.alternateMobileNoParamName, newValue);
            this.alternateMobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.secIdProofNoParamName, this.secIdProofNo)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.secIdProofNoParamName);
            actualChanges.put(ClientApiConstants.secIdProofNoParamName, newValue);
            this.secIdProofNo = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.secaddressproofnoParamName, this.secaddressproofno)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.secaddressproofnoParamName);
            actualChanges.put(ClientApiConstants.secaddressproofnoParamName, newValue);
            this.secaddressproofno = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.lastverifiedmobileParamName, this.lastverifiedmobile)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.lastverifiedmobileParamName);
            actualChanges.put(ClientApiConstants.lastverifiedmobileParamName, newValue);
            this.lastverifiedmobile = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.otherexpensestfParamName, this.otherexpensestf)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.otherexpensestfParamName);
            actualChanges.put(ClientApiConstants.otherexpensestfParamName, newValue);
            this.otherexpensestf = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.othersrcinctfParamName, this.othersrcinctf)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.othersrcinctfParamName);
            actualChanges.put(ClientApiConstants.othersrcinctfParamName, newValue);
            this.othersrcinctf = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.otherobligationsParamName, this.otherobligations)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.otherobligationsParamName);
            actualChanges.put(ClientApiConstants.otherobligationsParamName, newValue);
            this.otherobligations = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.lastverifiedsecondaryidParamName, this.lastverifiedsecondaryid)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.lastverifiedsecondaryidParamName);
            actualChanges.put(ClientApiConstants.lastverifiedsecondaryidParamName, newValue);
            this.lastverifiedsecondaryid = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.lastverifiedadharParamName, this.lastverifiedadhar)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.lastverifiedadharParamName);
            actualChanges.put(ClientApiConstants.lastverifiedadharParamName, newValue);
            this.lastverifiedadhar = StringUtils.defaultIfEmpty(newValue, null);
        }
        
//        if (command.isChangeInStringParameterNamed(ClientApiConstants.addressProofNoParamName, this.addressProofNo)) {
//            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.addressProofNoParamName);
//            actualChanges.put(ClientApiConstants.addressProofNoParamName, newValue);
//            this.addressProofNo = StringUtils.defaultIfEmpty(newValue, null);
//        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.adharParamName, this.adhar)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.adharParamName);
            actualChanges.put(ClientApiConstants.adharParamName, newValue);
            this.adhar = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.nregaParamName, this.nrega)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.nregaParamName);
            actualChanges.put(ClientApiConstants.nregaParamName, newValue);
            this.nrega = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.panParamName, this.pan)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.panParamName);
            actualChanges.put(ClientApiConstants.panParamName, newValue);
            this.pan = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.fullnameParamName, this.fullname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.fullnameParamName);
            actualChanges.put(ClientApiConstants.fullnameParamName, newValue);
            this.fullname = newValue;
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.staffIdParamName, staffId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.staffIdParamName);
            actualChanges.put(ClientApiConstants.staffIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.genderIdParamName, genderId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.genderIdParamName);
            actualChanges.put(ClientApiConstants.genderIdParamName, newValue);
        }
        
    
      
    
  
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.fatherspouseIdParamName, fatherspouseId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.fatherspouseIdParamName);
            actualChanges.put(ClientApiConstants.fatherspouseIdParamName, newValue);
        }
                
        if (command.isChangeInLongParameterNamed(ClientApiConstants.educationIdParamName, educationId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.educationIdParamName);
            actualChanges.put(ClientApiConstants.educationIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.maritalIdParamName, maritalId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.maritalIdParamName);
            actualChanges.put(ClientApiConstants.maritalIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.professionIdParamName, professionId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.professionIdParamName);
            actualChanges.put(ClientApiConstants.professionIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.belongingIdParamName, belongingId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.belongingIdParamName);
            actualChanges.put(ClientApiConstants.belongingIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.annualIdParamName, annualId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.annualIdParamName);
            actualChanges.put(ClientApiConstants.annualIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.landIdParamName, landId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.educationIdParamName);
            actualChanges.put(ClientApiConstants.educationIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.houseIdParamName, houseId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.houseIdParamName);
            actualChanges.put(ClientApiConstants.houseIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.formIdParamName, formId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.formIdParamName);
            actualChanges.put(ClientApiConstants.formIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.titleIdParamName, titleId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.titleIdParamName);
            actualChanges.put(ClientApiConstants.titleIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.religionIdParamName, religionId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.religionIdParamName);
            actualChanges.put(ClientApiConstants.religionIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.alternateNoIdParamName, alternateNoId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.alternateNoIdParamName);
            actualChanges.put(ClientApiConstants.alternateNoIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.idproofIdParamName, idproofId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.idproofIdParamName);
            actualChanges.put(ClientApiConstants.idproofIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.addrproofIdParamName, addrproofId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.addrproofIdParamName);
            actualChanges.put(ClientApiConstants.addrproofIdParamName, newValue);
        }
        

        if (command.isChangeInLongParameterNamed(ClientApiConstants.savingsProductIdParamName, savingsProductId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.savingsProductIdParamName);
            actualChanges.put(ClientApiConstants.savingsProductIdParamName, newValue);
        }       

        if (command.isChangeInLongParameterNamed(ClientApiConstants.clientTypeIdParamName, clientTypeId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.clientTypeIdParamName);
            actualChanges.put(ClientApiConstants.clientTypeIdParamName, newValue);
        }
        
      
        if (command.isChangeInLongParameterNamed(ClientApiConstants.clientClassificationIdParamName, clientClassificationId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.clientClassificationIdParamName);
            actualChanges.put(ClientApiConstants.clientClassificationIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.AmountAppliedParamName, AmountApplied())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.AmountAppliedParamName);
            actualChanges.put(ClientApiConstants.AmountAppliedParamName, newValue);
            this.setAmountApplied(newValue);
        }
        
        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.legalFormIdParamName, this.getLegalForm())) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.legalFormIdParamName);
            if(newValue != null)
            {
            	LegalForm legalForm = LegalForm.fromInt(newValue);
            	if(legalForm != null)
            	{
            		actualChanges.put(ClientApiConstants.legalFormIdParamName, ClientEnumerations.legalForm(newValue));
                    this.setLegalForm(legalForm.getValue());
                    if(legalForm.isPerson()){
                        this.fullname = null;
                    }else if(legalForm.isEntity()){
                        this.firstname = null;
                        //this.lastname = null;
                        this.displayName = null;
                    }
            	}
            	else
            	{
            		actualChanges.put(ClientApiConstants.legalFormIdParamName, null);
                    this.setLegalForm(null);
            	}
            }
            else
            {
            	actualChanges.put(ClientApiConstants.legalFormIdParamName, null);
                this.setLegalForm(null);
            }
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.activationDateParamName, getActivationLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.activationDateParamName);
            actualChanges.put(ClientApiConstants.activationDateParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.activationDateParamName);
            this.activationDate = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
            this.officeJoiningDate = this.activationDate;
        }

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.dateOfBirthParamName, dateOfBirthLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.dateOfBirthParamName);
            actualChanges.put(ClientApiConstants.dateOfBirthParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.dateOfBirthParamName);
            this.dateOfBirth = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.lastverifiedSecondaryidDateParamName, lastverifiedSecondaryidDateLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.lastverifiedSecondaryidDateParamName);
            actualChanges.put(ClientApiConstants.lastverifiedSecondaryidDateParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.lastverifiedSecondaryidDateParamName);
            ZoneId zoneid1 = ZoneId.of("Asia/Kolkata");
            this.lastverifiedSecondaryidDate = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        
        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.lastverifiedmobiledateParamName, lastverifiedmobiledateLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.lastverifiedmobiledateParamName);
            actualChanges.put(ClientApiConstants.lastverifiedmobiledateParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.lastverifiedmobiledateParamName);
            ZoneId zoneid1 = ZoneId.of("Asia/Kolkata");
            this.lastverifiedmobiledate = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        
        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.lastverifiedadhardateParamName, lastverifiedadhardateLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.lastverifiedadhardateParamName);
            actualChanges.put(ClientApiConstants.lastverifiedadhardateParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.lastverifiedadhardateParamName);
            this.lastverifiedadhardate = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.submittedOnDateParamName, getSubmittedOnDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.submittedOnDateParamName);
            actualChanges.put(ClientApiConstants.submittedOnDateParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.submittedOnDateParamName);
            this.submittedOnDate = Date.from(newValue.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }
        
        /*if (command.isChangeInLongParameterNamed(ClientApiConstants.groupParamName, this.group)) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.groupParamName);
            actualChanges.put(ClientApiConstants.groupParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.centerParamName, this.center)) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.groupParamName);
            actualChanges.put(ClientApiConstants.groupParamName, newValue);
        }*/

        validateUpdate();

        deriveDisplayName();

        return actualChanges;
    }

    public Long getAmountApplied() {
		return AmountApplied;
	}

	public void setAmountApplied(Long AmountApplied) {
		this.AmountApplied = AmountApplied;
	}

	public String getDebt() {
		return debt;
	}

	public void setDebt(String debt) {
		this.debt = debt;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getDebtincratio() {
		return debtincratio;
	}

	public void setDebtincratio(String debtincratio) {
		this.debtincratio = debtincratio;
	}

	private void validateNameParts(final List<ApiParameterError> dataValidationErrors) {
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("client");

        if (StringUtils.isNotBlank(this.fullname)) {

            baseDataValidator.reset().parameter(ClientApiConstants.firstnameParamName).value(this.firstname)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.fullnameParamName, this.fullname);
//
//            baseDataValidator.reset().parameter(ClientApiConstants.middlenameParamName).value(this.middlename)
//                    .mustBeBlankWhenParameterProvided(ClientApiConstants.fullnameParamName, this.fullname);

//            baseDataValidator.reset().parameter(ClientApiConstants.lastnameParamName).value(this.lastname)
//                    .mustBeBlankWhenParameterProvided(ClientApiConstants.fullnameParamName, this.fullname);
        } else {

            baseDataValidator.reset().parameter(ClientApiConstants.firstnameParamName).value(this.firstname).notBlank()
                    .notExceedingLengthOf(50);
//            baseDataValidator.reset().parameter(ClientApiConstants.middlenameParamName).value(this.middlename).ignoreIfNull()
//                    .notExceedingLengthOf(50);
//            baseDataValidator.reset().parameter(ClientApiConstants.lastnameParamName).value(this.lastname).notBlank()
//                    .notExceedingLengthOf(50);
        }
    }

    private void validateActivationDate(final List<ApiParameterError> dataValidationErrors) {

        if (getSubmittedOnDate() != null && isDateInTheFuture(getSubmittedOnDate())) {

            final String defaultUserMessage = "submitted date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.submittedOnDate.in.the.future",
                    defaultUserMessage, ClientApiConstants.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null && getSubmittedOnDate() != null && getSubmittedOnDate().isAfter(getActivationLocalDate())) {

            final String defaultUserMessage = "submitted date cannot be after the activation date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.submittedOnDate.after.activation.date",
                    defaultUserMessage, ClientApiConstants.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

		if (getReopenedDate() != null && getActivationLocalDate() != null
				&& getReopenedDate().isAfter(getActivationLocalDate())) {

			final String defaultUserMessage = "reopened date cannot be after the submittedon date";
			final ApiParameterError error = ApiParameterError.parameterError(
					"error.msg.clients.submittedOnDate.after.reopened.date", defaultUserMessage,
					ClientApiConstants.reopenedDateParamName, this.reopenedDate);

			dataValidationErrors.add(error);
		}

        if (getActivationLocalDate() != null && isDateInTheFuture(getActivationLocalDate())) {

            final String defaultUserMessage = "Activation date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.activationDate.in.the.future",
                    defaultUserMessage, ClientApiConstants.activationDateParamName, getActivationLocalDate());

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null) {
            if (this.office.isOpeningDateAfter(getActivationLocalDate())) {
                final String defaultUserMessage = "Client activation date cannot be a date before the office opening date.";
                final ApiParameterError error = ApiParameterError.parameterError(
                        "error.msg.clients.activationDate.cannot.be.before.office.activation.date", defaultUserMessage,
                        ClientApiConstants.activationDateParamName, getActivationLocalDate());
                dataValidationErrors.add(error);
            }
        }
    }

    private void deriveDisplayName() {

        StringBuilder nameBuilder = new StringBuilder();
        Integer legalForm = this.getLegalForm();
        if(legalForm == null || LegalForm.fromInt(legalForm).isPerson())
        {
        	if (StringUtils.isNotBlank(this.firstname)) {
                nameBuilder.append(this.firstname).append(' ');
            }

//            if (StringUtils.isNotBlank(this.middlename)) {
//                nameBuilder.append(this.middlename).append(' ');
//            }

//            if (StringUtils.isNotBlank(this.lastname)) {
//                nameBuilder.append(this.lastname);
//            }
            
            if (StringUtils.isNotBlank(this.fullname)) {
                nameBuilder = new StringBuilder(this.fullname);
            }
        }
        else if(LegalForm.fromInt(legalForm).isEntity())
        {
        	if (StringUtils.isNotBlank(this.fullname)) {
                nameBuilder = new StringBuilder(this.fullname);
            }
        }
        
        this.displayName = nameBuilder.toString();
    }

    public LocalDate getSubmittedOnDate() {
        return ObjectUtils.defaultIfNull(LocalDate.ofInstant(this.submittedOnDate.toInstant(), DateUtils.getDateTimeZoneOfTenant()), null);
    }

    public LocalDate getActivationLocalDate() {
        LocalDate activationLocalDate = null;
        if (this.activationDate != null) {
            activationLocalDate = LocalDate.ofInstant(this.activationDate.toInstant(), DateUtils.getDateTimeZoneOfTenant());
        }
        return activationLocalDate;
    }

    public LocalDate getOfficeJoiningLocalDate() {
        LocalDate officeJoiningLocalDate = null;
        if (this.officeJoiningDate != null) {
            officeJoiningLocalDate = LocalDate.ofInstant(this.officeJoiningDate.toInstant(), DateUtils.getDateTimeZoneOfTenant());
        }
        return officeJoiningLocalDate;
    }

    public boolean isOfficeIdentifiedBy(final Long officeId) {
        return this.office.identifiedBy(officeId);
    }

    public Long officeId() {
        return this.office.getId();
    }

    public void setImage(final Image image) {
        this.image = image;
    }

    public Image getImage() {
        return this.image;
    }

    public String mobileNo() {
        return this.mobileNo;
    }
    
    public String gstNo() {
        return this.gstNo;
    }
    
    public String age() {
        return this.age;
    }
    
    public String nomrelationshipid() {
        return this.nomrelationshipid;
    }
    
    public String nomgenderid()
    {
    	return this.nomgenderid;
    }
    
    public String nomage()
    {
    	return this.nomage;
    }
    
    public String nomprofessionid()
    {
    	return this.nomprofessionid;
    }
    
    public String nomeducationalid()
    {
    	return this.nomeducationalid;
    }
    
    public String nommaritalid()
    {
    	return this.nommaritalid;
    }
    
    public String idproofNo() {
        return this.idproofNo;
    }
    
    public String secaddressproofno() {
        return this.secaddressproofno;
    }
    
    public String lastverifiedmobile() {
        return this.lastverifiedmobile;
    }
    public String othersrcinctf() {
        return this.othersrcinctf;
    }
    public String otherexpensestf() {
        return this.otherexpensestf;
    }
    public String otherobligations() {
        return this.otherobligations;
    }
    public String lastverifiedsecondaryid() {
        return this.lastverifiedsecondaryid;
    }
    
    public String lastverifiedadhar()
    {
    	return this.lastverifiedadhar;
    }
    public String expinterest() {
        return this.expinterest;
    }
    
    public String expofficerent() {
        return this.expofficerent;
    }
    
    public String exptravel() {
        return this.exptravel;
    }
    
    public String expothers() {
        return this.expothers;
    }
    
    public String totbusinessprofit() {
        return this.totbusinessprofit;
    }
    
    public String incspouse() {
        return this.incspouse;
    }
    
      
    
   /* public String alternateNo() {
        return this.alternateNo;
    }
    
    public String getAlternateNo() {
        return this.alternateNo;
    }
    
    public String aadhaarNo() {
        return this.aadhaarNo;
    }
    
    public Long get() {
    	return this.group;
    }
    
    public Long getCenter() {
    	return this.center;
    }*/

	public String emailAddress() {
        return this.emailAddress;
    }

    public void setMobileNo(final String mobileNo) {
        this.mobileNo = mobileNo;
    }
    
    public void setGstNo(final String gstNo) {
        this.gstNo = gstNo;
    }
    
    public void setAge(final String age) {
        this.age = age;
    }
    
    public void setNomrelationshipid(final String nomrelationshipid) {
        this.nomrelationshipid = nomrelationshipid;
    }
    
    public void SetNomgenderid(final String nomgenderid)
    {
    	 this.nomgenderid = nomgenderid ;
    }
    
    public void SetNomage(final String nomage )
    {
    	this.nomage = nomage;
    }
    
    public void nomprofessionid(final String nomprofessionid )
    {
    	 this.nomprofessionid = nomprofessionid;
    }
    
    public void nomeducationalid(final String nomeducationalid)
    {
    	 this.nomeducationalid = nomeducationalid;
    }
    
    public void nommaritalid(final String nommaritalid)
    {
    	 this.nommaritalid = nommaritalid;
    }
    
    public void setIdproofNo(final String idproofNo) {
        this.idproofNo = idproofNo;
    }
    
    public void setSecaddressproofno(final String secaddressproofno) {
        this.secaddressproofno = secaddressproofno;
    }
    
    public void setlastverifiedmobile(final String lastverifiedmobile) {
        this.lastverifiedmobile = lastverifiedmobile;
    }
    public void setOtherexpensestf(final String otherexpensestf) {
        this.otherexpensestf = otherexpensestf;
    }
    public void setOthersrcinctf(final String othersrcinctf) {
        this.othersrcinctf = othersrcinctf;
    }
    
    public void setotherobligations(final String otherobligations) {
        this.otherobligations = otherobligations;
    }
    
    public void setlastverifiedsecondaryid(final String lastverifiedsecondaryid) {
        this.lastverifiedsecondaryid = lastverifiedsecondaryid;
    }
    
    public void setlastverifiedadhar(final String lastverifiedadhar)
    {
    	this.lastverifiedadhar = lastverifiedadhar;
    }
    
	public boolean isNotStaff() {
        return !isStaff();
    }

    public boolean isStaff() {
        return this.isStaff;
    }

	public String getExternalId() {
		return this.externalId; 
	}
	
//	public String getIdproofnumId() {
//		return this.idproofnumId; 
//	}
//	
//	public String getAddrproofnumId() {
//		return this.addrproofnumId; 
//	}

	public String getexternalIdd() {
		return this.externalIdd; 
	}
	
	public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public Office getOffice() {
        return this.office;
    }

    public Office getTransferToOffice() {
        return this.transferToOffice;
    }

    public void updateOffice(final Office office) {
        this.office = office;
    }

    public void updateTransferToOffice(final Office office) {
        this.transferToOffice = office;
    }

    public void updateOfficeJoiningDate(final Date date) {
        this.officeJoiningDate = date;
    }

    private Long staffId() {
        Long staffId = null;
        if (this.staff != null) {
            staffId = this.staff.getId();
        }
        return staffId;
    }

    public void updateStaff(final Staff staff) {
        this.staff = staff;
    }

    public Staff getStaff() {
        return this.staff;
    }

    public void unassignStaff() {
        this.staff = null;
    }

    public void assignStaff(final Staff staff) {
        this.staff = staff;
    }

    public Set<Group> getGroups() {
        return this.groups;
    }

    public void close(final AppUser currentUser, final CodeValue closureReason, final Date closureDate) {
        this.closureReason = closureReason;
        this.closureDate = closureDate;
        this.closedBy = currentUser;
        this.status = ClientStatus.CLOSED.getValue();
    }

    public Integer getStatus() {
        return this.status;
    }
    
    public CodeValue subStatus() {
        return this.subStatus;
    }
    
    public Long subStatusId() {
        Long subStatusId = null;
        if (this.subStatus != null) {
            subStatusId = this.subStatus.getId();
        }
        return subStatusId;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public boolean isActivatedAfter(final LocalDate submittedOn) {
        return getActivationLocalDate().isAfter(submittedOn);
    }

    public boolean isChildOfGroup(final Long groupId) {
        if (groupId != null && this.groups != null && !this.groups.isEmpty()) {
            for (final Group group : this.groups) {
                if (group.getId().equals(groupId)) { return true; }
            }
        }
        return false;
    }

    public Long savingsProductId() {
        return this.savingsProductId;
    }

    public void updateSavingsProduct(final Long savingsProductId) {
        this.savingsProductId = savingsProductId;
    }

    public AppUser activatedBy() {
        return this.activatedBy;
    }

    public Long savingsAccountId() {
        return this.savingsAccountId;
    }

    public void updateSavingsAccount(Long savingsAccountId) {
        this.savingsAccountId = savingsAccountId;
    }

    public Long genderId() {
        Long genderId = null;
        if (this.gender != null) {
            genderId = this.gender.getId();
        }
        return genderId;
    }

    public Long idproofId() {
        Long idproofId = null;
        if (this.idproof != null) {
        	idproofId = this.idproof.getId();
        }
        return idproofId;
    }
    
    public Long addrproofId() {
        Long addrproofId = null;
        if (this.addrproof != null) {
        	addrproofId = this.addrproof.getId();
        }
        return addrproofId;
    }
    
    public Long fatherspouseId() {
        Long fatherspouseId = null;
        if (this.fatherspouse != null) {
        	fatherspouseId = this.fatherspouse.getId();
        }
        return fatherspouseId;
    }
    
    public Long alternateNoId() {
        Long alternateNoId = null;
        if (this.alternateNo != null) {
        	alternateNoId = this.alternateNo.getId();
        }
        return alternateNoId;
    }
    
    public Long educationId() {
        Long educationId = null;
        if (this.education != null) {
        	educationId = this.education.getId();
        }
        return educationId;
    }
    
    public Long maritalId() {
        Long maritalId = null;
        if (this.marital != null) {
        	maritalId = this.marital.getId();
        }
        return maritalId;
    }
    
    public Long professionId() {
        Long professionId = null;
        if (this.profession != null) {
        	professionId = this.profession.getId();
        }
        return professionId;
    }
    public Long belongingId() {
        Long belongingId = null;
        if (this.belonging != null) {
        	belongingId = this.belonging.getId();
        }
        return belongingId;
    }
    
    public Long annualId() {
        Long annualId = null;
        if (this.annual != null) {
        	annualId = this.annual.getId();
        }
        return annualId;
    }
    
    public Long AmountApplied() {
    	Long AmountApplied = null;
        if (this.AmountApplied != null) {
        	AmountApplied = this.AmountApplied;
        }
        return AmountApplied;
    }
    
    public Long landId() {
        Long landId = null;
        if (this.land != null) {
        	landId = this.land.getId();
        }
        return landId;
    }
    
    public Long houseId() {
        Long houseId = null;
        if (this.house != null) {
        	houseId = this.house.getId();
        }
        return houseId;
    }
    
    public Long formId() {
        Long formId = null;
        if (this.form != null) {
        	formId = this.form.getId();
        }
        return formId;
    }
    public Long titleId() {
        Long titleId = null;
        if (this.title != null) {
        	titleId = this.title.getId();
        }
        return titleId;
    }
    
    public Long religionId() {
        Long religionId = null;
        if (this.religion != null) {
        	religionId = this.religion.getId();
        }
        return religionId;
    }

    public Long clientTypeId() {
        Long clientTypeId = null;
        if (this.clientType != null) {
            clientTypeId = this.clientType.getId();
        }
        return clientTypeId;
    }
  
    public Long clientClassificationId() {
        Long clientClassificationId = null;
        if (this.clientClassification != null) {
            clientClassificationId = this.clientClassification.getId();
        }
        return clientClassificationId;
    }

    public LocalDate getClosureDate() {
        return ObjectUtils.defaultIfNull(LocalDate.ofInstant(this.closureDate.toInstant(), DateUtils.getDateTimeZoneOfTenant()), null);
    }
    public LocalDate getRejectedDate() {
        return ObjectUtils.defaultIfNull(LocalDate.ofInstant(this.rejectionDate.toInstant(), DateUtils.getDateTimeZoneOfTenant()), null);
    }
    public LocalDate getWithdrawalDate() {
        return ObjectUtils.defaultIfNull(LocalDate.ofInstant(this.withdrawalDate.toInstant(), DateUtils.getDateTimeZoneOfTenant()), null);
    }

	public LocalDate getReopenedDate() {
        return this.reopenedDate == null ? null : LocalDate.ofInstant(this.reopenedDate.toInstant(), DateUtils.getDateTimeZoneOfTenant());
    }

	public CodeValue gender() {
        return this.gender;
    }
	





	public CodeValue fatherspouse() {
        return this.fatherspouse;
    }
	
	public CodeValue education() {
        return this.education;
    }
	
	public CodeValue marital() {
        return this.marital;
    }
	
	public CodeValue profession() {
        return this.profession;
    }
	public CodeValue belonging() {
        return this.belonging;
    }
	
	public CodeValue annual() {
        return this.annual;
    }
	
	public CodeValue land() {
        return this.land;
    }
	
	public CodeValue house() {
        return this.house;
    }
	
	public CodeValue form() {
        return this.form;
    }
	public CodeValue title() {
        return this.title;
    }
	
	public CodeValue religion() {
        return this.religion;
    }

	public CodeValue alternateNo() {
        return this.alternateNo;
    }
    
	public CodeValue idproof() {
        return this.idproof;
    }
	
	public CodeValue addrproof() {
        return this.addrproof;
    }
	

    public CodeValue clientType() {
        return this.clientType;
    }

    public void updateClientType(CodeValue clientType) {
        this.clientType = clientType;
    }
  
    public CodeValue clientClassification() {
        return this.clientClassification;
    }

    public void updateClientClassification(CodeValue clientClassification) {
        this.clientClassification = clientClassification;
    }

    public void updateGender(CodeValue gender) {
        this.gender = gender;
    }

    
 

    
  
    
    public void updateFatherspouse(CodeValue fatherspouse) {
        this.fatherspouse = fatherspouse;
    }
    
    
    public void updateEducation(CodeValue education) {
        this.education = education;
    }

    public void updateMarital(CodeValue marital) {
        this.marital = marital;
    }
    
    public void updateProfession(CodeValue profession) {
        this.profession = profession;
    }

    public void updateBelonging(CodeValue belonging) {
        this.belonging = belonging;
    }
    
    public void updateAnnual(CodeValue annual) {
        this.annual = annual;
    }
    
    public void updateLand(CodeValue land) {
        this.land = land;
    }

    public void updateHouse(CodeValue house) {
        this.house = house;
    }
    
    public void updateForm(CodeValue form) {
        this.form = form;
    }

    public void updateTitle(CodeValue title) {
        this.title = title;
    }
    
    public void updateReligion(CodeValue religion) {
        this.religion = religion;
    }
    
    public void updateAlternateNo(CodeValue alternateNo) {
        this.alternateNo = alternateNo;
    }
    
    public void updateIdproof(CodeValue idproof) {
        this.idproof = idproof;
    }
    
    public void updateAddrproof(CodeValue addrproof) {
        this.addrproof = addrproof;
    }

    public Date dateOfBirth() {
        return this.dateOfBirth;
    }
    
    public Date lastverifiedSecondaryidDate() {
        return this.lastverifiedSecondaryidDate;
    }
    
    public Date lastverifiedmobiledate() {
    	return this.lastverifiedmobiledate;
    }
    
    public Date lastverifiedadhardate() {
        return this.lastverifiedadhardate;
    }


    public LocalDate dateOfBirthLocalDate() {
        LocalDate dateOfBirth = null;
        if (this.dateOfBirth != null) {
            dateOfBirth = LocalDate.ofInstant(this.dateOfBirth.toInstant(), DateUtils.getDateTimeZoneOfTenant());
        }
        return dateOfBirth;
    }
    
    public LocalDate lastverifiedSecondaryidDateLocalDate() {
    	LocalDate lastverifiedSecondaryidDate = null;
        if (this.lastverifiedSecondaryidDate != null) {
        	 
            lastverifiedSecondaryidDate = LocalDate.ofInstant(this.lastverifiedSecondaryidDate.toInstant(), DateUtils.getDateTimeZoneOfTenant());
        }
        return lastverifiedSecondaryidDate;
    }
    
    public LocalDate lastverifiedmobiledateLocalDate() {
    	LocalDate lastverifiedmobiledate = null;
        if (this.lastverifiedmobiledate != null) {
        	 
        	lastverifiedmobiledate = LocalDate.ofInstant(this.lastverifiedmobiledate.toInstant(), DateUtils.getDateTimeZoneOfTenant());
        }
        return lastverifiedmobiledate;
    }
    
    public LocalDate lastverifiedadhardateLocalDate() {
        LocalDate lastverifiedadhardate = null;
        if (this.lastverifiedadhardate != null) {
        	lastverifiedadhardate = LocalDate.ofInstant(this.lastverifiedadhardate.toInstant(), DateUtils.getDateTimeZoneOfTenant());
        }
        return lastverifiedadhardate;
    }

    public void reject(AppUser currentUser, CodeValue rejectionReason, Date rejectionDate) {
        this.rejectionReason = rejectionReason;
        this.rejectionDate = rejectionDate;
        this.rejectedBy = currentUser;
        this.updatedBy = currentUser;
        this.updatedOnDate = rejectionDate;
        this.status = ClientStatus.REJECTED.getValue();

    }

    public void withdraw(AppUser currentUser, CodeValue withdrawalReason, Date withdrawalDate) {
        this.withdrawalReason = withdrawalReason;
        this.withdrawalDate = withdrawalDate;
        this.withdrawnBy = currentUser;
        this.updatedBy = currentUser;
        this.updatedOnDate = withdrawalDate;
        this.status = ClientStatus.WITHDRAWN.getValue();

    }

    public void reActivate(AppUser currentUser, Date reactivateDate) {
        this.closureDate = null;
        this.closureReason = null;
        this.reactivateDate = reactivateDate;
        this.reactivatedBy = currentUser;
        this.updatedBy = currentUser;
        this.updatedOnDate = reactivateDate;
        this.status = ClientStatus.PENDING.getValue();

    }
    
    public void reOpened(AppUser currentUser, Date reopenedDate) {
        this.reopenedDate = reopenedDate;
        this.reopenedBy = currentUser;
        this.updatedBy = currentUser;
        this.updatedOnDate = reopenedDate;
        this.status = ClientStatus.PENDING.getValue();

    }

    public Integer getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(Integer legalForm) {
        this.legalForm = legalForm;
    }
    
    public void loadLazyCollections() {
        this.groups.size() ;
    }
    public Date getProposedTransferDate() {
        return proposedTransferDate;
    }

    public void updateProposedTransferDate(Date proposedTransferDate) {
        this.proposedTransferDate = proposedTransferDate;
    }
    
    public String getFirstname(){return this.firstname;}

   // public String getMiddlename(){return this.middlename;}

  //  public String getLastname(){return this.lastname;}
}
