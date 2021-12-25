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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.data.ClientFamilyMembersData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientFamilyMembersReadPlatformServiceImpl implements ClientFamilyMembersReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @Autowired
    public ClientFamilyMembersReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;

    }

    private static final class ClientFamilyMembersMapper implements RowMapper<ClientFamilyMembersData> {

        public String schema() {
            return "fmb.id AS id, fmb.client_id AS clientId, fmb.firstname AS firstName, fmb.middlename AS middleName,fmb.nom_adhar as nomadhar,fmb.nom_secondary_id as nomsecondaryid,fmb.nom_house_no as nomhouseno,fmb.nom_street_no as nomstreetno,fmb.nom_area_locality as nomarealocality,fmb.nom_taluka as nomtaluka,fmb.nom_district as nomdistrict,fmb.nom_state as nomstate,fmb.nom_village as nomvillage,"
                    + "fmb.nom_pincode as nompincode,fmb.is_nominee as isnominee,fmb.is_nominee_addr as isnomineeaddr,fmb.nom_secondary_id_num as nomsecondaryidnum,fmb.lastname AS lastName,fmb.mobile_number as mobileNumber,fmb.age as age,fmb.is_dependent as isDependent,cv.code_value AS relationship,fmb.relationship_cv_id AS relationshipId,"
                    + "c.code_value AS maritalStatus,fmb.marital_status_cv_id AS maritalStatusId,"
                    + "c1.code_value AS gender, fmb.gender_cv_id AS genderId, fmb.date_of_birth AS dateOfBirth, c2.code_value AS profession, fmb.profession_cv_id AS professionId, c3.code_value AS qualification, fmb.qualification_cv_id AS qualificationId"
                    + " FROM m_family_members fmb" + " LEFT JOIN m_code_value cv ON fmb.relationship_cv_id=cv.id"
                    + " LEFT JOIN m_code_value c ON fmb.marital_status_cv_id=c.id" + " LEFT JOIN m_code_value c1 ON fmb.gender_cv_id=c1.id"
                    + " LEFT JOIN m_code_value c2 ON fmb.profession_cv_id=c2.id LEFT JOIN m_code_value c3 ON fmb.qualification_cv_id=c3.id ";
        }

        @Override
        public ClientFamilyMembersData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final long id = rs.getLong("id");
            final long clientId = rs.getLong("clientId");
            final String firstName = rs.getString("firstName");
            final String middleName = rs.getString("middleName");
            final String lastName = rs.getString("lastName");
            final String mobileNumber = rs.getString("mobileNumber");
            final long age = rs.getLong("age");
            final boolean isDependent = rs.getBoolean("isDependent");
            final String relationship = rs.getString("relationship");
            Long relationshipId = rs.getLong("relationshipId");
            final String maritalStatus = rs.getString("maritalStatus");
            Long maritalStatusId = rs.getLong("maritalStatusId");
            final String gender = rs.getString("gender");
             Long genderId = rs.getLong("genderId");
            final LocalDate dateOfBirth = JdbcSupport.getLocalDate(rs, "dateOfBirth");
            final String profession = rs.getString("profession");
            Long professionId = rs.getLong("professionId");
            final String qualification = rs.getString("qualification");
             Long qualificationId = rs.getLong("qualificationId");
            final String nomadhar = rs.getString("nomadhar");
            final String nomarealocality = rs.getString("nomarealocality");
             Long nomtaluka = rs.getLong("nomtaluka");
             Long nomdistrict = rs.getLong("nomdistrict");
             Long nomstate = rs.getLong("nomstate");
            final String nomsecondaryidnum = rs.getString("nomsecondaryidnum");
             Long nomsecondaryid = rs.getLong("nomsecondaryid");
            Long nompincode = rs.getLong("nompincode");
            final String nomhouseno = rs.getString("nomhouseno");
            final String nomstreetno = rs.getString("nomstreetno");
            final boolean isnominee = rs.getBoolean("isnominee");
            final boolean isnomineeaddr = rs.getBoolean("isnomineeaddr");
            final String nomvillage = rs.getString("nomvillage");
            
            if(genderId.compareTo(0l)==0)
            {
            	genderId = null;
            }
            
            if(maritalStatusId.compareTo(0l)==0)
            {
            	maritalStatusId = null;
            }
            
            if(relationshipId.compareTo(0l)==0)
            {
            	relationshipId = null;
            }
            
            
            if(professionId.compareTo(0l)==0)
            {
            	professionId = null;
            }
            
            if(qualificationId.compareTo(0l)==0)
            {
            	qualificationId = null;
            }
            
            if(nomtaluka.compareTo(0l)==0)
            {
            	nomtaluka = null;
            }
            
            if(nomdistrict.compareTo(0l)==0)
            {
            	nomdistrict = null;
            }
            
            if(nomstate.compareTo(0l)==0)
            {
            	nomstate = null;
            }
            
            if(nomsecondaryid.compareTo(0l)==0)
            {
            	nomsecondaryid = null;
            }
            
            if(nompincode.compareTo(0l)==0)
            {
            	nompincode = null;
            }
            return ClientFamilyMembersData.instance(id, clientId, firstName, middleName, lastName, mobileNumber, age,
                    isDependent, relationship, relationshipId, maritalStatus, maritalStatusId, gender, genderId, dateOfBirth,profession,
                    professionId, qualification, qualificationId,isnominee,isnomineeaddr, nomadhar, nomsecondaryid, nomsecondaryidnum, nomhouseno,
                    nomstreetno,  nomarealocality ,  nomtaluka ,  nomdistrict ,  nomstate ,  nompincode,nomvillage);

        }
    }

    @Override
    public Collection<ClientFamilyMembersData> getClientFamilyMembers(long clientId) {

        this.context.authenticatedUser();

        final ClientFamilyMembersMapper rm = new ClientFamilyMembersMapper();
        final String sql = "select " + rm.schema() + " where fmb.client_id=?";

        return this.jdbcTemplate.query(sql, rm, new Object[] { clientId });
    }

    @Override
    public ClientFamilyMembersData getClientFamilyMember(long id) {

        this.context.authenticatedUser();

        final ClientFamilyMembersMapper rm = new ClientFamilyMembersMapper();
        final String sql = "select " + rm.schema() + " where fmb.id=? ";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
    }

    @Override
    public ClientFamilyMembersData retrieveTemplate() {

        final List<CodeValueData> maritalStatusOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode("MARITAL STATUS"));

        final List<CodeValueData> genderOptions = new ArrayList<>(this.codeValueReadPlatformService.retrieveCodeValuesByCode("Gender"));

        final List<CodeValueData> relationshipOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode("RELATIONSHIP"));

        final List<CodeValueData> professionOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode("PROFESSION"));

        final List<CodeValueData> qualifcationOptions = new ArrayList<>(
            this.codeValueReadPlatformService.retrieveCodeValuesByCode("Education"));
    
        return ClientFamilyMembersData.templateInstance(relationshipOptions, genderOptions, maritalStatusOptions, professionOptions, qualifcationOptions);
    }

}
