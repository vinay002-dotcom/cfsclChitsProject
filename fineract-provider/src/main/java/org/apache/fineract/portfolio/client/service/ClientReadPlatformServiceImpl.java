/**
00 * Licensed to the Apache Software Foundation (ASF) under one
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

//import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.dataqueries.data.DatatableData;
import org.apache.fineract.infrastructure.dataqueries.data.EntityTables;
import org.apache.fineract.infrastructure.dataqueries.data.StatusEnum;
import org.apache.fineract.infrastructure.dataqueries.service.EntityDatatableChecksReadService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.infrastructure.security.utils.SQLBuilder;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.address.data.AddressData;
import org.apache.fineract.portfolio.address.service.AddressReadPlatformService;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.data.ClientFamilyMembersData;
import org.apache.fineract.portfolio.client.data.ClientNonPersonData;
import org.apache.fineract.portfolio.client.data.ClientTimelineData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.client.exception.ClientNotFoundException;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import java.time.LocalDate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ClientReadPlatformServiceImpl implements ClientReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final StaffReadPlatformService staffReadPlatformService;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final SavingsProductReadPlatformService savingsProductReadPlatformService;
	// data mappers
	private final PaginationHelper<ClientData> paginationHelper = new PaginationHelper<>();
	private final ClientMapper clientMapper = new ClientMapper();
	private final ClientLookupMapper lookupMapper = new ClientLookupMapper();
	private final ClientMembersOfGroupMapper membersOfGroupMapper = new ClientMembersOfGroupMapper();
	private final ParentGroupsMapper clientGroupsMapper = new ParentGroupsMapper();

	private final AddressReadPlatformService addressReadPlatformService;
	private final ClientFamilyMembersReadPlatformService clientFamilyMembersReadPlatformService;
	private final ConfigurationReadPlatformService configurationReadPlatformService;
	private final EntityDatatableChecksReadService entityDatatableChecksReadService;
	private final ColumnValidator columnValidator;

	@Autowired
	public ClientReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
			final OfficeReadPlatformService officeReadPlatformService, final StaffReadPlatformService staffReadPlatformService,
			final CodeValueReadPlatformService codeValueReadPlatformService,
			final SavingsProductReadPlatformService savingsProductReadPlatformService,
			final AddressReadPlatformService addressReadPlatformService,final ClientFamilyMembersReadPlatformService clientFamilyMembersReadPlatformService,
			final ConfigurationReadPlatformService configurationReadPlatformService,
			final EntityDatatableChecksReadService entityDatatableChecksReadService,
			final ColumnValidator columnValidator) {
		this.context = context;
		this.officeReadPlatformService = officeReadPlatformService;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.staffReadPlatformService = staffReadPlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.savingsProductReadPlatformService = savingsProductReadPlatformService;
		this.addressReadPlatformService=addressReadPlatformService;
		this.clientFamilyMembersReadPlatformService=clientFamilyMembersReadPlatformService;
		this.configurationReadPlatformService=configurationReadPlatformService;
		this.entityDatatableChecksReadService = entityDatatableChecksReadService;
		this.columnValidator = columnValidator;
	}

	@Override
	public ClientData retrieveTemplate(final Long officeId, final boolean staffInSelectedOfficeOnly) {
		this.context.authenticatedUser();

		final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);
		AddressData address=null;

		final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();

		final Collection<SavingsProductData> savingsProductDatas = this.savingsProductReadPlatformService.retrieveAllForLookupByType(null);

		final GlobalConfigurationPropertyData configuration=this.configurationReadPlatformService.retrieveGlobalConfiguration("Enable-Address");

		final Boolean isAddressEnabled=configuration.isEnabled(); 
		if(isAddressEnabled)
		{
			address = this.addressReadPlatformService.retrieveTemplate();
		}

		final ClientFamilyMembersData familyMemberOptions=this.clientFamilyMembersReadPlatformService.retrieveTemplate();

		Collection<StaffData> staffOptions = null;

		final boolean loanOfficersOnly = false;
		if (staffInSelectedOfficeOnly) {
			staffOptions = this.staffReadPlatformService.retrieveAllStaffForDropdown(defaultOfficeId);
		} else {
			staffOptions = this.staffReadPlatformService.retrieveAllStaffInOfficeAndItsParentOfficeHierarchy(defaultOfficeId,
					loanOfficersOnly);
		}
		if (CollectionUtils.isEmpty(staffOptions)) {
			staffOptions = null;
		}
		final List<CodeValueData> genderOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.GENDER));

		final List<CodeValueData> fatherspouseOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.FATHERSPOUSE));

		final List<CodeValueData> educationOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.EDUCATION));

		final List<CodeValueData> maritalOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.MARITAL));

		final List<CodeValueData> professionOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.PROFESSION));

		final List<CodeValueData> belongingOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.BELONGING));

		final List<CodeValueData> annualOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.ANNUAL));

		final List<CodeValueData> altMobNumOfOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.ALTNO));

		final List<CodeValueData> houseOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.HOUSE));

		final List<CodeValueData> formOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.FORM));

		final List<CodeValueData> titleOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.TITLE));

		final List<CodeValueData> religionOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.RELIGION));

		final List<CodeValueData> idproofOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.IDPROOF));

		final List<CodeValueData> addrproofOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.ADDRPROOF));

		final List<CodeValueData> clientTypeOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.CLIENT_TYPE));

		final List<CodeValueData> clientClassificationOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.CLIENT_CLASSIFICATION));

		final List<CodeValueData> clientNonPersonConstitutionOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.CLIENT_NON_PERSON_CONSTITUTION));

		final List<CodeValueData> clientNonPersonMainBusinessLineOptions = new ArrayList<>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.CLIENT_NON_PERSON_MAIN_BUSINESS_LINE));

		final List<EnumOptionData> clientLegalFormOptions = ClientEnumerations.legalForm(LegalForm.values());

		final List<DatatableData> datatableTemplates = this.entityDatatableChecksReadService
				.retrieveTemplates(StatusEnum.CREATE.getCode().longValue(), EntityTables.CLIENT.getName(), null);

		return ClientData.template(defaultOfficeId, null, offices, staffOptions, null, genderOptions,fatherspouseOptions,educationOptions,
				maritalOptions, professionOptions,belongingOptions,annualOptions, altMobNumOfOptions, houseOptions, formOptions,titleOptions, religionOptions,
				idproofOptions, addrproofOptions, savingsProductDatas,
				clientTypeOptions,  clientClassificationOptions, clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions,
				clientLegalFormOptions,familyMemberOptions,address,isAddressEnabled, datatableTemplates);
	}

	@Override
	// @Transactional(readOnly=true)
	public Page<ClientData> retrieveAll(final SearchParameters searchParameters) {

		final String userOfficeHierarchy = this.context.officeHierarchy();
		final String underHierarchySearchString = userOfficeHierarchy + "%";
		final String appUserID = String.valueOf(context.authenticatedUser().getId());

		// if (searchParameters.isScopedByOfficeHierarchy()) {
		// this.context.validateAccessRights(searchParameters.getHierarchy());
		// underHierarchySearchString = searchParameters.getHierarchy() + "%";
		// }
		List<Object> paramList = new ArrayList<>(Arrays.asList(underHierarchySearchString, underHierarchySearchString));
		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
		sqlBuilder.append(this.clientMapper.schema());
		sqlBuilder.append(" where (o.hierarchy like ? or transferToOffice.hierarchy like ?) ");

		if(searchParameters!=null) {
			if (searchParameters.isSelfUser()) {
				sqlBuilder.append(" and c.id in (select umap.client_id from m_selfservice_user_client_mapping as umap where umap.appuser_id = ? ) ");
				paramList.add(appUserID);
			}

			final String extraCriteria = buildSqlStringFromClientCriteria(this.clientMapper.schema(), searchParameters, paramList);

			if (StringUtils.isNotBlank(extraCriteria)) {
				sqlBuilder.append(" and (").append(extraCriteria).append(")");
			}

			if (searchParameters.isOrderByRequested()) {
				sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
				this.columnValidator.validateSqlInjection(sqlBuilder.toString(), searchParameters.getOrderBy());
				if (searchParameters.isSortOrderProvided()) {
					sqlBuilder.append(' ').append(searchParameters.getSortOrder());
					this.columnValidator.validateSqlInjection(sqlBuilder.toString(), searchParameters.getSortOrder());
				}
			}

			if (searchParameters.isLimited()) {
				sqlBuilder.append(" limit ").append(searchParameters.getLimit());
				if (searchParameters.isOffset()) {
					sqlBuilder.append(" offset ").append(searchParameters.getOffset());
				}
			}
		}
		final String sqlCountRows = "SELECT FOUND_ROWS()";
		return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), paramList.toArray(), this.clientMapper);
	}

	private String buildSqlStringFromClientCriteria(String schemaSql, final SearchParameters searchParameters, List<Object> paramList) {

		String sqlSearch = searchParameters.getSqlSearch();
		final Long officeId = searchParameters.getOfficeId();
		final String externalId = searchParameters.getExternalId();
		//        final String idproofnumId = searchParameters.getIdproofnumId();
		//        final String addrproofnumId = searchParameters.getAddrproofnumId();
		final String displayName = searchParameters.getName();
		final String firstname = searchParameters.getFirstname();
		final String lastname = searchParameters.getLastname();

		String extraCriteria = "";
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.replaceAll(" display_name ", " c.display_name ");
			sqlSearch = sqlSearch.replaceAll("display_name ", "c.display_name ");
			extraCriteria = " and (" + sqlSearch + ")";
			this.columnValidator.validateSqlInjection(schemaSql, sqlSearch);
		}

		if (officeId != null) {
			extraCriteria += " and c.office_id = ? ";
			paramList.add(officeId);
		}

		if (externalId != null) {
			paramList.add(ApiParameterHelper.sqlEncodeString(externalId));
			extraCriteria += " and c.external_id like ? " ;
		}


		if (displayName != null) {
			//extraCriteria += " and concat(ifnull(c.firstname, ''), if(c.firstname > '',' ', '') , ifnull(c.lastname, '')) like "
			paramList.add("%" + displayName + "%");
			extraCriteria += " and c.display_name like ? ";
		}

		if (firstname != null) {
			paramList.add(ApiParameterHelper.sqlEncodeString(firstname));
			extraCriteria += " and c.firstname like ? " ;
		}

		if (lastname != null) {
			paramList.add(ApiParameterHelper.sqlEncodeString(lastname));
			extraCriteria += " and c.lastname like ? ";
		}

		if (searchParameters.isScopedByOfficeHierarchy()) {
			paramList.add(ApiParameterHelper.sqlEncodeString(searchParameters.getHierarchy() + "%"));
			extraCriteria += " and o.hierarchy like ? ";
		}

		if(searchParameters.isOrphansOnly()){
			extraCriteria += " and c.id NOT IN (select client_id from m_group_client) ";
		}

		if(searchParameters.getStatus() != null){
			extraCriteria += " and c.status_enum ="+ClientStatus.ACTIVE.getValue();
		}

		if (StringUtils.isNotBlank(extraCriteria)) {
			extraCriteria = extraCriteria.substring(4);
		}
		return extraCriteria;
	}

	@Override
	public ClientData retrieveOne(final Long clientId) {
		try {
			final String hierarchy = this.context.officeHierarchy();
			final String hierarchySearchString = hierarchy + "%";

			final String sql = "select " + this.clientMapper.schema()
			+ " where ( o.hierarchy like ? or transferToOffice.hierarchy like ?) and c.id = ?";
			final ClientData clientData = this.jdbcTemplate.queryForObject(sql, this.clientMapper, new Object[] { hierarchySearchString,
					hierarchySearchString, clientId });

			final String clientGroupsSql = "select " + this.clientGroupsMapper.parentGroupsSchema();

			final Collection<GroupGeneralData> parentGroups = this.jdbcTemplate.query(clientGroupsSql, this.clientGroupsMapper,
					new Object[] { clientId });

			return ClientData.setParentGroups(clientData, parentGroups);

		} catch (final EmptyResultDataAccessException e) {
			throw new ClientNotFoundException(clientId);
		}
	}

	@Override
	public Collection<ClientData> retrieveAllForLookup(final String extraCriteria) {

		String sql = "select " + this.lookupMapper.schema();

		if (StringUtils.isNotBlank(extraCriteria)) {
			sql += " and (" + extraCriteria + ")";
			this.columnValidator.validateSqlInjection(sql, extraCriteria);
		}        
		return this.jdbcTemplate.query(sql, this.lookupMapper, new Object[] {});
	}

	@Override
	public Collection<ClientData> retrieveAllForLookupByOfficeId(final Long officeId) {

		final String sql = "select " + this.lookupMapper.schema() + " where c.office_id = ? and c.status_enum != ?";

		return this.jdbcTemplate.query(sql, this.lookupMapper, new Object[] { officeId, ClientStatus.CLOSED.getValue() });
	}

	@Override
	public Collection<ClientData> retrieveClientMembersOfGroup(final Long groupId) {

		final AppUser currentUser = this.context.authenticatedUser();
		final String hierarchy = currentUser.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		final String sql = "select " + this.membersOfGroupMapper.schema() + " where o.hierarchy like ? and pgc.group_id = ?";

		return this.jdbcTemplate.query(sql, this.membersOfGroupMapper, new Object[] { hierarchySearchString, groupId });
	}

	@Override
	public Collection<ClientData> retrieveActiveClientMembersOfGroup(final Long groupId) {

		final AppUser currentUser = this.context.authenticatedUser();
		final String hierarchy = currentUser.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		final String sql = "select " + this.membersOfGroupMapper.schema()
		+ " where o.hierarchy like ? and pgc.group_id = ? and c.status_enum = ? ";

		return this.jdbcTemplate.query(sql, this.membersOfGroupMapper,
				new Object[] { hierarchySearchString, groupId, ClientStatus.ACTIVE.getValue() });
	}

	private static final class ClientMembersOfGroupMapper implements RowMapper<ClientData> {

		private final String schema;

		public ClientMembersOfGroupMapper() {
			final StringBuilder sqlBuilder = new StringBuilder(200);

			sqlBuilder
			.append("c.id as id, c.account_no as accountNo, c.external_id as externalId, c.external_idd as externalIdd, c.status_enum as statusEnum,c.sub_status as subStatus, ");
			sqlBuilder
			.append("cvSubStatus.code_value as subStatusValue,cvSubStatus.code_description as subStatusDesc,c.office_id as officeId, o.name as officeName, ");
			sqlBuilder.append("c.transfer_to_office_id as transferToOfficeId, transferToOffice.name as transferToOfficeName, ");
			sqlBuilder.append("c.firstname as firstname, c.spousename as spousename, c.lastname as lastname, ");
			sqlBuilder.append("c.fsfirstname as fsfirstname, ");
			sqlBuilder.append("c.maidenname as maidenname, c.custmothername as custmothername, c.adhar as adhar,c.nrega as nrega, c.pan as pan, ");
			sqlBuilder.append("c.alternatemobileno as alternateMobileNo, c.secidproofno as secIdProofNo, c.secondary_address_proof_no as secaddressproofno, ");
			sqlBuilder.append("c.last_verified_mobile as lastverifiedmobile, c.other_obligations as otherobligations, c.last_verified_secondaryid as lastverifiedsecondaryid,c.last_verified_adhar as lastverifiedadhar,c.other_expenses_tf as otherexpensestf,c.other_src_inc_tf as othersrcinctf, ");
			sqlBuilder.append("c.fullname as fullname, c.display_name as displayName, ");
			sqlBuilder.append("c.mobile_no as mobileNo, ");
			sqlBuilder.append("c.gst_no as gstNo, ");
			sqlBuilder.append("c.age as age, ");
			sqlBuilder.append("c.cpv_data as cpvData, ");
			sqlBuilder.append("c.nom_relationship_id as nomrelationshipid,c.nom_gender_id as nomgenderid,c.nom_age as nomage,c.nom_profession_id as nomprofessionid,c.nom_marital_id as nommaritalid,c.nom_education_id as nomeducationalid ,");
			sqlBuilder.append("c.inc_daily_sales as incdailysales, ");
			sqlBuilder.append("c.exp_raw_material as exprawmaterial, ");
			sqlBuilder.append("c.exp_staff_sal as expstaffsal, ");
			sqlBuilder.append("c.exp_power_telephone as exppowertelephone, ");
			sqlBuilder.append("c.exp_repairs_maintainance as exprepairsmaintainance, ");
			sqlBuilder.append("c.exp_comm_brokerage as expcommbrokerage, ");
			sqlBuilder.append("c.inc_rent as incrent, ");
			sqlBuilder.append("c.inc_interest as incinterest, ");
			sqlBuilder.append("c.inc_others as incothers, ");
			sqlBuilder.append("c.tot_house_hold_inc as tothouseholdinc, ");
			sqlBuilder.append("c.exp_household as exphousehold, ");
			sqlBuilder.append("c.exp_other_loans as expotherloans, ");
			sqlBuilder.append("c.tot_net_disp_family as totnetdispfamily, ");
			sqlBuilder.append("c.debt as debt,c.income as income,c.debt_inc_ratio as debtincratio,c.amt_applied as amountapplied ,");

			sqlBuilder.append("c.idproof_no as idproofNo, ");
			sqlBuilder.append("c.addrproof_no as addrproofNo, ");
			sqlBuilder.append("c.exp_interest as expinterest, ");
			sqlBuilder.append("c.exp_office_rent as expofficerent, ");
			sqlBuilder.append("c.exp_travel as exptravel, ");
			sqlBuilder.append("c.exp_others as expothers, ");
			sqlBuilder.append("c.tot_business_profit as totbusinessprofit, ");
			sqlBuilder.append("c.inc_spouse as incspouse, ");
			sqlBuilder.append("c.is_staff as isStaff, ");
			sqlBuilder.append("c.email_address as emailAddress, ");
			sqlBuilder.append("c.date_of_birth as dateOfBirth, ");
			sqlBuilder.append("c.last_verified_secondaryid_date as lastverifiedSecondaryidDate, ");
			sqlBuilder.append("c.last_verified_mobile_date as lastverifiedmobiledate, ");
			sqlBuilder.append("c.last_verified_adhar_date as lastverifiedadhardate, ");
			sqlBuilder.append("c.gender_cv_id as genderId, ");
			sqlBuilder.append("cv.code_value as genderValue, ");


			sqlBuilder.append("c.fatherspouse_fs_id as fatherspouseId, ");
			sqlBuilder.append("cv.code_value as fatherspouseValue, ");
			sqlBuilder.append("c.education_vv_id as educationId, "); 
			sqlBuilder.append("cv.code_value as educationValue, ");
			sqlBuilder.append("c.marital_mm_id as maritalId, ");
			sqlBuilder.append("cv.code_value as maritalValue, ");
			sqlBuilder.append("c.profession_pp_id as professionId, ");
			sqlBuilder.append("cv.code_value as professionValue, ");
			sqlBuilder.append("c.belonging_bb_id as belongingId, ");
			sqlBuilder.append("cv.code_value as belongingValue, ");
			sqlBuilder.append("c.annual_aa_id as annualId, ");
			sqlBuilder.append("cv.code_value as annualValue, "); 
			sqlBuilder.append("c.land_ll_id as landId, ");
			sqlBuilder.append("cv.code_value as landValue, ");
			sqlBuilder.append("c.house_hh_id as houseId, "); 
			sqlBuilder.append("cv.code_value as houseValue, ");
			sqlBuilder.append("c.form_ff_id as formId, ");
			sqlBuilder.append("cv.code_value as formValue, ");
			sqlBuilder.append("c.title_tt_id as titleId, ");
			sqlBuilder.append("cv.code_value as titleValue, ");
			sqlBuilder.append("c.religion_rr_id as religionId, ");
			sqlBuilder.append("cv.code_value as religionValue, ");
			sqlBuilder.append("c.alternateno_id as alternateNoId, ");
			sqlBuilder.append("cv.code_value as alternateNoIdValue, ");
			sqlBuilder.append("c.idproof_dp_id as idproofId, ");
			sqlBuilder.append("cv.code_value as idproofValue, ");
			sqlBuilder.append("c.addrproof_ap_id as addrproofId, ");
			sqlBuilder.append("cv.code_value as addrproofValue, ");
			sqlBuilder.append("c.client_type_cv_id as clienttypeId, ");
			sqlBuilder.append("cvclienttype.code_value as clienttypeValue, ");
			sqlBuilder.append("c.client_classification_cv_id as classificationId, ");
			sqlBuilder.append("cvclassification.code_value as classificationValue, ");
			sqlBuilder.append("c.legal_form_enum as legalFormEnum, ");
			sqlBuilder.append("c.activation_date as activationDate, c.image_id as imageId, ");
			sqlBuilder.append("c.staff_id as staffId, s.display_name as staffName, ");
			sqlBuilder.append("c.default_savings_product as savingsProductId, sp.name as savingsProductName, ");
			sqlBuilder.append("c.default_savings_account as savingsAccountId, ");

			sqlBuilder.append("c.submittedon_date as submittedOnDate, ");
			sqlBuilder.append("sbu.username as submittedByUsername, ");
			sqlBuilder.append("sbu.firstname as submittedByFirstname, ");
			sqlBuilder.append("sbu.lastname as submittedByLastname, ");

			sqlBuilder.append("c.closedon_date as closedOnDate, ");
			sqlBuilder.append("clu.username as closedByUsername, ");
			sqlBuilder.append("clu.firstname as closedByFirstname, ");
			sqlBuilder.append("clu.lastname as closedByLastname, ");

			sqlBuilder.append("acu.username as activatedByUsername, ");
			sqlBuilder.append("acu.firstname as activatedByFirstname, ");
			sqlBuilder.append("acu.lastname as activatedByLastname, ");

			sqlBuilder.append("cnp.constitution_cv_id as constitutionId, ");
			sqlBuilder.append("cvConstitution.code_value as constitutionValue, ");
			sqlBuilder.append("cnp.incorp_no as incorpNo, ");
			sqlBuilder.append("cnp.incorp_validity_till as incorpValidityTill, ");
			sqlBuilder.append("cnp.main_business_line_cv_id as mainBusinessLineId, ");
			sqlBuilder.append("cvMainBusinessLine.code_value as mainBusinessLineValue, ");
			sqlBuilder.append("cnp.remarks as remarks ");

			sqlBuilder.append("from m_client c ");
			sqlBuilder.append("join m_office o on o.id = c.office_id ");
			sqlBuilder.append("left join m_client_non_person cnp on cnp.client_id = c.id ");
			sqlBuilder.append("join m_group_client pgc on pgc.client_id = c.id ");
			sqlBuilder.append("left join m_staff s on s.id = c.staff_id ");
			sqlBuilder.append("left join m_savings_product sp on sp.id = c.default_savings_product ");
			sqlBuilder.append("left join m_office transferToOffice on transferToOffice.id = c.transfer_to_office_id ");

			sqlBuilder.append("left join m_appuser sbu on sbu.id = c.submittedon_userid ");
			sqlBuilder.append("left join m_appuser acu on acu.id = c.activatedon_userid ");
			sqlBuilder.append("left join m_appuser clu on clu.id = c.closedon_userid ");
			sqlBuilder.append("left join m_code_value cv on cv.id = c.gender_cv_id ");





			sqlBuilder.append("left join m_code_value fs on fs.id = c.fatherspouse_fs_id ");
			sqlBuilder.append("left join m_code_value vv on vv.id = c.education_vv_id ");
			sqlBuilder.append("left join m_code_value mm on mm.id = c.marital_mm_id ");
			sqlBuilder.append("left join m_code_value pp on pp.id = c.profession_pp_id ");
			sqlBuilder.append("left join m_code_value bb on bb.id = c.belonging_bb_id ");
			sqlBuilder.append("left join m_code_value aa on aa.id = c.annual_aa_id ");
			sqlBuilder.append("left join m_code_value ll on ll.id = c.land_ll_id ");
			sqlBuilder.append("left join m_code_value hh on hh.id = c.house_hh_id ");
			sqlBuilder.append("left join m_code_value ff on ff.id = c.form_ff_id ");
			sqlBuilder.append("left join m_code_value tt on tt.id = c.title_tt_id ");
			sqlBuilder.append("left join m_code_value rr on rr.id = c.religion_rr_id ");
			sqlBuilder.append("left join m_code_value amn on amn.id = c.alternateno_id ");
			sqlBuilder.append("left join m_code_value dp on dp.id = c.idproof_dp_id ");
			sqlBuilder.append("left join m_code_value ap on ap.id = c.addrproof_ap_id ");
			sqlBuilder.append("left join m_code_value cvclienttype on cvclienttype.id = c.client_type_cv_id ");
			sqlBuilder.append("left join m_code_value cvclassification on cvclassification.id = c.client_classification_cv_id ");
			sqlBuilder.append("left join m_code_value cvSubStatus on cvSubStatus.id = c.sub_status ");
			sqlBuilder.append("left join m_code_value cvConstitution on cvConstitution.id = cnp.constitution_cv_id ");
			sqlBuilder.append("left join m_code_value cvMainBusinessLine on cvMainBusinessLine.id = cnp.main_business_line_cv_id ");

			this.schema = sqlBuilder.toString();
		}

		public String schema() {
			return this.schema;
		}

		@Override
		public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final String accountNo = rs.getString("accountNo");

			final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
			final EnumOptionData status = ClientEnumerations.status(statusEnum);

			final Long subStatusId = JdbcSupport.getLong(rs, "subStatus");
			final String subStatusValue = rs.getString("subStatusValue");
			final String subStatusDesc = rs.getString("subStatusDesc");
			final boolean isActive = false;
			final CodeValueData subStatus = CodeValueData.instance(subStatusId, subStatusValue, subStatusDesc, isActive);

			final Long officeId = JdbcSupport.getLong(rs, "officeId");
			final String officeName = rs.getString("officeName");

			final boolean isStaff = rs.getBoolean("isStaff");

			final Long transferToOfficeId = JdbcSupport.getLong(rs, "transferToOfficeId");
			final String transferToOfficeName = rs.getString("transferToOfficeName");

			final Long id = JdbcSupport.getLong(rs, "id");
			final String firstname = rs.getString("firstname");
			final String spousename = rs.getString("spousename");
			final String lastname = rs.getString("lastname");
			final String fsfirstname = rs.getString("fsfirstname");

			final String maidenname = rs.getString("maidenname");
			final String custmothername = rs.getString("custmothername");
			final String alternateMobileNo = rs.getString("alternateMobileNo");
			final String secIdProofNo = rs.getString("secIdProofNo");
			final String secaddressproofno = rs.getString("secaddressproofno");
			final String lastverifiedmobile = rs.getString("lastverifiedmobile");
			final String otherexpensestf = rs.getString("otherexpensestf");
			final String othersrcinctf = rs.getString("othersrcinctf");
			final String otherobligations = rs.getString("otherobligations");
			final String lastverifiedsecondaryid = rs.getString("lastverifiedsecondaryid");
			final String adhar = rs.getString("adhar");
			final String nrega = rs.getString("nrega");
			final String pan = rs.getString("pan");
			final String fullname = rs.getString("fullname");
			final String displayName = rs.getString("displayName");
			final String externalId = rs.getString("externalId");
			final String externalIdd = rs.getString("externalIdd");
			final String mobileNo = rs.getString("mobileNo");
			final String gstNo = rs.getString("gstNo");
			final String age = rs.getString("age");
			final String nomrelationshipid = rs.getString("nomrelationshipid");
			final String nomgenderid = rs.getString("nomgenderid");
			final String nomage = rs.getString("nomage");
			final String nomprofessionid = rs.getString("nomprofessionid");
			final String nomeducationalid = rs.getString("nomeducationalid");
			final String nommaritalid = rs.getString("nommaritalid");
			final String incdailysales = rs.getString("incdailysales");
			final String exprawmaterial = rs.getString("exprawmaterial");
			final String expstaffsal = rs.getString("expstaffsal");
			final String exppowertelephone = rs.getString("exppowertelephone");
			final String exprepairsmaintainance = rs.getString("exprepairsmaintainance");
			final String expcommbrokerage = rs.getString("expcommbrokerage");
			final String incrent = rs.getString("incrent");
			final String incinterest = rs.getString("incinterest");
			final String incothers = rs.getString("incothers");
			final String tothouseholdinc = rs.getString("tothouseholdinc");
			final String exphousehold = rs.getString("exphousehold");
			final String expotherloans = rs.getString("expotherloans");
			final String totnetdispfamily = rs.getString("totnetdispfamily");

			final String idproofNo = rs.getString("idproofNo");
			final String addrproofNo = rs.getString("addrproofNo");
			final String expinterest = rs.getString("expinterest");
			final String expofficerent = rs.getString("expofficerent");
			final String exptravel = rs.getString("exptravel");
			final String expothers = rs.getString("expothers");
			final String totbusinessprofit = rs.getString("totbusinessprofit");
			final String incspouse = rs.getString("incspouse");
			//final String alternateNo = rs.getString("alternateNo");
			//final String aadhaarNo = rs.getString("aadhaarNo");
			final String lastverifiedadhar = rs.getString("lastverifiedadhar");

			final String emailAddress = rs.getString("emailAddress");
			final LocalDate dateOfBirth = JdbcSupport.getLocalDate(rs, "dateOfBirth");
			final LocalDate lastverifiedSecondaryidDate = JdbcSupport.getLocalDate(rs, "lastverifiedSecondaryidDate");
			final LocalDate lastverifiedmobiledate = JdbcSupport.getLocalDate(rs, "lastverifiedmobiledate");
			final LocalDate lastverifiedadhardate  = JdbcSupport.getLocalDate(rs,"lastverifiedadhardate");
			final Long genderId = JdbcSupport.getLong(rs, "genderId");
			final String genderValue = rs.getString("genderValue");
			final Long amountapplied = rs.getLong("amountapplied");
			final CodeValueData gender = CodeValueData.instance(genderId, genderValue);

			//            final Long statusOneId = JdbcSupport.getLong(rs, "statusOneId");
			//            final String statusOneValue = rs.getString("statusOneValue");
			//            final CodeValueData statusOne = CodeValueData.instance(statusOneId, statusOneValue);
			//            
			//            final Long statusTwoId = JdbcSupport.getLong(rs, "statusTwoId");
			//            final String statusTwoValue = rs.getString("statusTwoValue");
			//            final CodeValueData statusTwo = CodeValueData.instance(statusTwoId, statusTwoValue);
			//            
			//            final Long introducerId = JdbcSupport.getLong(rs, "introducerId");
			//            final String introducerValue = rs.getString("introducerValue");
			//            final CodeValueData introducer = CodeValueData.instance(introducerId, introducerValue);
			//            
			//            final Long sourceoneId = JdbcSupport.getLong(rs, "sourceoneId");
			//            final String sourceoneValue = rs.getString("sourceoneValue");
			//            final CodeValueData sourceone = CodeValueData.instance(sourceoneId, sourceoneValue);
			//            
			//            final Long sourcetwoId = JdbcSupport.getLong(rs, "sourcetwoId");
			//            final String sourcetwoValue = rs.getString("sourcetwoValue");
			//            final CodeValueData sourcetwo = CodeValueData.instance(sourcetwoId, sourcetwoValue);
			//            
			//            final Long purposeoneId = JdbcSupport.getLong(rs, "purposeoneId");
			//            final String purposeoneValue = rs.getString("purposeoneValue");
			//            final CodeValueData purposeone = CodeValueData.instance(purposeoneId, purposeoneValue);
			//            
			//            final Long purposetwoId = JdbcSupport.getLong(rs, "purposetwoId");
			//            final String purposetwoValue = rs.getString("purposetwoValue");
			//            final CodeValueData purposetwo = CodeValueData.instance(purposetwoId, purposetwoValue);

			final Long fatherspouseId = JdbcSupport.getLong(rs, "fatherspouseId");
			final String fatherspouseValue = rs.getString("fatherspouseValue");
			final CodeValueData fatherspouse = CodeValueData.instance(fatherspouseId, fatherspouseValue);

			final Long educationId = JdbcSupport.getLong(rs, "educationId");
			final String educationValue = rs.getString("educationValue");
			final CodeValueData education = CodeValueData.instance(educationId, educationValue);

			final Long maritalId = JdbcSupport.getLong(rs, "maritalId");
			final String maritalValue = rs.getString("maritalValue");
			final CodeValueData marital = CodeValueData.instance(maritalId, maritalValue);

			final Long professionId = JdbcSupport.getLong(rs, "professionId");
			final String professionValue = rs.getString("professionValue");
			final CodeValueData profession = CodeValueData.instance(professionId, professionValue);

			final Long belongingId = JdbcSupport.getLong(rs, "belongingId");
			final String belongingValue = rs.getString("belongingValue");
			final CodeValueData belonging = CodeValueData.instance(belongingId, belongingValue);

			final Long annualId = JdbcSupport.getLong(rs, "annualId");
			final String annualValue = rs.getString("annualValue");
			final CodeValueData annual = CodeValueData.instance(annualId, annualValue);

			final Long landId = JdbcSupport.getLong(rs, "landId");
			final String landValue = rs.getString("landValue");
			final CodeValueData land = CodeValueData.instance(landId, landValue);

			final Long houseId = JdbcSupport.getLong(rs, "houseId");
			final String houseValue = rs.getString("houseValue");
			final CodeValueData house = CodeValueData.instance(houseId, houseValue);

			final Long formId = JdbcSupport.getLong(rs, "formId");
			final String formValue = rs.getString("formValue");
			final CodeValueData form = CodeValueData.instance(formId, formValue);

			final Long titleId = JdbcSupport.getLong(rs, "titleId");
			final String titleValue = rs.getString("titleValue");
			final CodeValueData title = CodeValueData.instance(titleId, titleValue);

			final Long religionId = JdbcSupport.getLong(rs, "religionId");
			final String religionValue = rs.getString("religionValue");
			final CodeValueData religion = CodeValueData.instance(religionId, religionValue);

			final Long alternateNoIdID = JdbcSupport.getLong(rs, "alternatenoid");
			final String alternateNoIdValue = rs.getString("alternateNoIdValue");
			final CodeValueData alternateNoId = CodeValueData.instance(alternateNoIdID, alternateNoIdValue);

			final Long idproofId = JdbcSupport.getLong(rs, "idproofId");
			final String idproofValue = rs.getString("idproofValue");
			final CodeValueData idproof = CodeValueData.instance(idproofId, idproofValue);

			final Long addrproofId = JdbcSupport.getLong(rs, "addrproofId");
			final String addrproofValue = rs.getString("addrproofValue");
			final CodeValueData addrproof = CodeValueData.instance(addrproofId, addrproofValue);

			final Long clienttypeId = JdbcSupport.getLong(rs, "clienttypeId");
			final String clienttypeValue = rs.getString("clienttypeValue");
			final CodeValueData clienttype = CodeValueData.instance(clienttypeId, clienttypeValue);

			final Long classificationId = JdbcSupport.getLong(rs, "classificationId");
			final String classificationValue = rs.getString("classificationValue");
			final CodeValueData classification = CodeValueData.instance(classificationId, classificationValue);

			final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
			final Long imageId = JdbcSupport.getLong(rs, "imageId");

			final Long staffId = JdbcSupport.getLong(rs, "staffId");
			final String staffName = rs.getString("staffName");

			final Long savingsProductId = JdbcSupport.getLong(rs, "savingsProductId");
			final String savingsProductName = rs.getString("savingsProductName");

			final Long savingsAccountId = JdbcSupport.getLong(rs, "savingsAccountId");

			final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
			final String closedByUsername = rs.getString("closedByUsername");
			final String closedByFirstname = rs.getString("closedByFirstname");
			final String closedByLastname = rs.getString("closedByLastname");

			final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
			final String submittedByUsername = rs.getString("submittedByUsername");
			final String submittedByFirstname = rs.getString("submittedByFirstname");
			final String submittedByLastname = rs.getString("submittedByLastname");

			final String activatedByUsername = rs.getString("activatedByUsername");
			final String activatedByFirstname = rs.getString("activatedByFirstname");
			final String activatedByLastname = rs.getString("activatedByLastname");

			final Integer legalFormEnum = JdbcSupport.getInteger(rs, "legalFormEnum");
			EnumOptionData legalForm = null;
			if(legalFormEnum != null)
				legalForm = ClientEnumerations.legalForm(legalFormEnum);

			final Long constitutionId = JdbcSupport.getLong(rs, "constitutionId");
			final String constitutionValue = rs.getString("constitutionValue");
			final CodeValueData constitution = CodeValueData.instance(constitutionId, constitutionValue);
			final String incorpNo = rs.getString("incorpNo");
			final LocalDate incorpValidityTill = JdbcSupport.getLocalDate(rs, "incorpValidityTill");
			final Long mainBusinessLineId = JdbcSupport.getLong(rs, "mainBusinessLineId");            
			final String mainBusinessLineValue = rs.getString("mainBusinessLineValue");
			final CodeValueData mainBusinessLine = CodeValueData.instance(mainBusinessLineId, mainBusinessLineValue);
			final String remarks = rs.getString("remarks");
			final Long debt = rs.getLong("debt");
			final Long income = rs.getLong("income");
			final Float debtincratio = rs.getFloat("debtincratio");
			//final Long group = rs.getLong("group");
			//final Long center = rs.getLong("center");
			String fsmiddlename = null;
			String fslastname = null;
			Boolean cpvData = rs.getBoolean("cpvData");
			final ClientNonPersonData clientNonPerson = new ClientNonPersonData(constitution, incorpNo, incorpValidityTill, mainBusinessLine, remarks);

			final ClientTimelineData timeline = new ClientTimelineData(submittedOnDate, submittedByUsername, submittedByFirstname,
					submittedByLastname, activationDate, activatedByUsername, activatedByFirstname, activatedByLastname, closedOnDate,
					closedByUsername, closedByFirstname, closedByLastname);

			return ClientData.instance(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id,
					firstname, spousename, lastname,fsfirstname, fsmiddlename, fslastname, maidenname, custmothername,alternateMobileNo, secIdProofNo, secaddressproofno,lastverifiedmobile,otherexpensestf,othersrcinctf,otherobligations,lastverifiedsecondaryid,lastverifiedadhar, adhar, nrega, pan, fullname, displayName, externalId, externalIdd, 
					mobileNo,gstNo,age,  nomrelationshipid,
					nomgenderid,
					nomage,
					nomprofessionid,
					nomeducationalid,
					nommaritalid,incdailysales,exprawmaterial,expstaffsal, exppowertelephone,exprepairsmaintainance,expcommbrokerage,incrent,incinterest,incothers,tothouseholdinc,exphousehold,expotherloans,totnetdispfamily,expinterest,expofficerent,
					exptravel,expothers,totbusinessprofit,incspouse,idproofNo, addrproofNo, emailAddress, dateOfBirth,lastverifiedSecondaryidDate,lastverifiedmobiledate,lastverifiedadhardate, gender,
					fatherspouse, education, marital, profession, belonging, annual, land, house, form, title, religion, alternateNoId, idproof, addrproof, activationDate,
					imageId, staffId, staffName, timeline, savingsProductId, savingsProductName, savingsAccountId, clienttype, 
					classification, legalForm, clientNonPerson, isStaff,submittedOnDate,debt,income,debtincratio,amountapplied,cpvData);

		}
	}

	@Override
	public Collection<ClientData> retrieveActiveClientMembersOfCenter(final Long centerId) {

		final AppUser currentUser = this.context.authenticatedUser();
		final String hierarchy = currentUser.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		final String sql = "select "
				+ this.membersOfGroupMapper.schema()
				+ " left join m_group g on pgc.group_id=g.id where o.hierarchy like ? and g.parent_id = ? and c.status_enum = ? group by c.id";

		return this.jdbcTemplate.query(sql, this.membersOfGroupMapper,
				new Object[] { hierarchySearchString, centerId, ClientStatus.ACTIVE.getValue() });
	}

	private static final class ClientMapper implements RowMapper<ClientData> {

		private final String schema;

		public ClientMapper() {
			final StringBuilder builder = new StringBuilder(400);

			builder.append("c.id as id, c.account_no as accountNo, c.external_id as externalId, c.external_idd as externalIdd, c.status_enum as statusEnum,c.sub_status as subStatus, ");
			builder.append("cvSubStatus.code_value as subStatusValue,cvSubStatus.code_description as subStatusDesc,c.office_id as officeId, o.name as officeName, ");
			builder.append("c.transfer_to_office_id as transferToOfficeId, transferToOffice.name as transferToOfficeName, ");
			builder.append("c.firstname as firstname, c.spousename as spousename, c.lastname as lastname, ");
			builder.append("c.fsfirstname as fsfirstname,  ");
			builder.append("c.maidenname as maidenname, c.custmothername as custmothername, ");
			builder.append("c.last_verified_mobile as lastverifiedmobile, c.other_obligations as otherobligations, c.last_verified_secondaryid as lastverifiedsecondaryid,c.last_verified_adhar as lastverifiedadhar,c.other_expenses_tf as otherexpensestf,c.other_src_inc_tf as othersrcinctf, ");
			builder.append("c.alternatemobileno as alternateMobileNo, c.secidproofno as secIdProofNo, c.secondary_address_proof_no as secaddressproofno, ");
			builder.append("c.adhar as adhar, c.nrega as nrega, c.pan as pan, ");
			builder.append("c.fullname as fullname, c.display_name as displayName, ");
			builder.append("c.mobile_no as mobileNo, ");
			builder.append("c.gst_no as gstNo, ");
			builder.append("c.age as age, ");
			builder.append("c.cpv_data as cpvData, ");
			builder.append("c.nom_relationship_id as nomrelationshipid,c.nom_gender_id as nomgenderid,c.nom_age as nomage,c.nom_profession_id as nomprofessionid,c.nom_marital_id as nommaritalid,c.nom_education_id as nomeducationalid ,");
			builder.append("c.inc_daily_sales as incdailysales, ");
			builder.append("c.exp_raw_material as exprawmaterial, ");
			builder.append("c.exp_staff_sal as expstaffsal, ");
			builder.append("c.exp_power_telephone as exppowertelephone, ");
			builder.append("c.exp_repairs_maintainance as exprepairsmaintainance, ");
			builder.append("c.exp_comm_brokerage as expcommbrokerage, ");
			builder.append("c.inc_rent as incrent, ");
			builder.append("c.inc_interest as incinterest, ");
			builder.append("c.inc_others as incothers, ");
			builder.append("c.tot_house_hold_inc as tothouseholdinc, ");
			builder.append("c.exp_household as exphousehold, ");
			builder.append("c.exp_other_loans as expotherloans, ");
			builder.append("c.tot_net_disp_family as totnetdispfamily, ");
			builder.append("c.debt as debt,c.income as income,c.debt_inc_ratio as debtincratio,c.amt_applied as amountapplied,");

			builder.append("c.idproof_no as idproofNo, ");
			builder.append("c.addrproof_no as addrproofNo, ");
			builder.append("c.exp_interest as expinterest, ");
			builder.append("c.exp_office_rent as expofficerent, ");
			builder.append("c.exp_travel as exptravel, ");
			builder.append("c.exp_others as expothers, ");
			builder.append("c.tot_business_profit as totbusinessprofit, ");
			builder.append("c.inc_spouse as incspouse, ");
			builder.append("c.is_staff as isStaff, ");
			builder.append("c.email_address as emailAddress, ");
			builder.append("c.date_of_birth as dateOfBirth, ");
			builder.append("c.last_verified_secondaryid_date as lastverifiedSecondaryidDate, ");
			builder.append("c.last_verified_mobile_date as lastverifiedmobiledate, ");
			builder.append("c.last_verified_adhar_date as lastverifiedadhardate, ");
			builder.append("c.gender_cv_id as genderId, ");
			builder.append("cv.code_value as genderValue, ");



			builder.append("cv.code_value as introducerValue, ");


			builder.append("c.fatherspouse_fs_id as fatherspouseId, ");
			builder.append("cv.code_value as fatherspouseValue, ");
			builder.append("c.education_vv_id as educationId, ");
			builder.append("cv.code_value as educationValue, ");
			builder.append("c.marital_mm_id as maritalId, ");
			builder.append("cv.code_value as maritalValue, ");
			builder.append("c.profession_pp_id as professionId, ");
			builder.append("cv.code_value as professionValue, ");
			builder.append("c.belonging_bb_id as belongingId, ");
			builder.append("cv.code_value as belongingValue, ");
			builder.append("c.annual_aa_id as annualId, ");
			builder.append("cv.code_value as annualValue, ");
			builder.append("c.land_ll_id as landId, ");
			builder.append("cv.code_value as landValue, ");
			builder.append("c.house_hh_id as houseId, ");
			builder.append("cv.code_value as houseValue, ");
			builder.append("c.form_ff_id as formId, ");
			builder.append("cv.code_value as formValue, ");
			builder.append("c.title_tt_id as titleId, ");
			builder.append("cv.code_value as titleValue, ");
			builder.append("c.religion_rr_id as religionId, ");
			builder.append("cv.code_value as religionValue, ");
			builder.append("c.alternateno_id as alternateNoId, ");
			builder.append("cv.code_value as alternateNoIdValue, ");
			builder.append("c.idproof_dp_id as idproofId, ");
			builder.append("cv.code_value as idproofValue, ");
			builder.append("c.addrproof_ap_id as addrproofId, ");
			builder.append("cv.code_value as addrproofValue, ");
			builder.append("c.client_type_cv_id as clienttypeId, ");
			builder.append("cvclienttype.code_value as clienttypeValue, ");
			builder.append("c.client_classification_cv_id as classificationId, ");
			builder.append("cvclassification.code_value as classificationValue, ");
			builder.append("c.legal_form_enum as legalFormEnum, ");

			builder.append("c.submittedon_date as submittedOnDate, ");
			builder.append("sbu.username as submittedByUsername, ");
			builder.append("sbu.firstname as submittedByFirstname, ");
			builder.append("sbu.lastname as submittedByLastname, ");

			builder.append("c.closedon_date as closedOnDate, ");
			builder.append("clu.username as closedByUsername, ");
			builder.append("clu.firstname as closedByFirstname, ");
			builder.append("clu.lastname as closedByLastname, ");

			// builder.append("c.submittedon as submittedOnDate, ");
			builder.append("acu.username as activatedByUsername, ");
			builder.append("acu.firstname as activatedByFirstname, ");
			builder.append("acu.lastname as activatedByLastname, ");

			builder.append("cnp.constitution_cv_id as constitutionId, ");
			builder.append("cvConstitution.code_value as constitutionValue, ");
			builder.append("cnp.incorp_no as incorpNo, ");
			builder.append("cnp.incorp_validity_till as incorpValidityTill, ");
			builder.append("cnp.main_business_line_cv_id as mainBusinessLineId, ");
			builder.append("cvMainBusinessLine.code_value as mainBusinessLineValue, ");
			builder.append("cnp.remarks as remarks, ");

			builder.append("c.activation_date as activationDate, c.image_id as imageId, ");
			builder.append("c.staff_id as staffId, s.display_name as staffName, ");
			builder.append("c.default_savings_product as savingsProductId, sp.name as savingsProductName, ");
			builder.append("c.default_savings_account as savingsAccountId ");
			builder.append("from m_client c ");
			builder.append("join m_office o on o.id = c.office_id ");
			builder.append("left join m_client_non_person cnp on cnp.client_id = c.id ");
			builder.append("left join m_staff s on s.id = c.staff_id ");
			builder.append("left join m_savings_product sp on sp.id = c.default_savings_product ");
			builder.append("left join m_office transferToOffice on transferToOffice.id = c.transfer_to_office_id ");
			builder.append("left join m_appuser sbu on sbu.id = c.submittedon_userid ");
			builder.append("left join m_appuser acu on acu.id = c.activatedon_userid ");
			builder.append("left join m_appuser clu on clu.id = c.closedon_userid ");
			builder.append("left join m_code_value cv on cv.id = c.gender_cv_id ");







			builder.append("left join m_code_value fs on fs.id = c.fatherspouse_fs_id ");
			builder.append("left join m_code_value vv on vv.id  = c.education_vv_id ");
			builder.append("left join m_code_value mm on mm.id = c.marital_mm_id ");
			builder.append("left join m_code_value pp on pp.id  = c.profession_pp_id ");
			builder.append("left join m_code_value bb on bb.id = c.belonging_bb_id ");
			builder.append("left join m_code_value aa on aa.id  = c.annual_aa_id ");
			builder.append("left join m_code_value ll on ll.id  = c.land_ll_id ");
			builder.append("left join m_code_value hh on hh.id = c.house_hh_id ");
			builder.append("left join m_code_value ff on ff.id  = c.form_ff_id ");
			builder.append("left join m_code_value tt on tt.id = c.title_tt_id ");
			builder.append("left join m_code_value rr on rr.id  = c.religion_rr_id ");
			builder.append("left join m_code_value amn on amn.id  = c.alternateno_id ");
			builder.append("left join m_code_value dp on dp.id = c.idproof_dp_id ");
			builder.append("left join m_code_value ap on ap.id = c.addrproof_ap_id ");
			builder.append("left join m_code_value cvclienttype on cvclienttype.id = c.client_type_cv_id ");
			builder.append("left join m_code_value cvclassification on cvclassification.id = c.client_classification_cv_id ");
			builder.append("left join m_code_value cvSubStatus on cvSubStatus.id = c.sub_status ");
			builder.append("left join m_code_value cvConstitution on cvConstitution.id = cnp.constitution_cv_id ");
			builder.append("left join m_code_value cvMainBusinessLine on cvMainBusinessLine.id = cnp.main_business_line_cv_id ");

			this.schema = builder.toString();
		}

		public String schema() {
			return this.schema;
		}

		@Override
		public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final String accountNo = rs.getString("accountNo");

			final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
			final EnumOptionData status = ClientEnumerations.status(statusEnum);

			final Long subStatusId = JdbcSupport.getLong(rs, "subStatus");
			final String subStatusValue = rs.getString("subStatusValue");
			final String subStatusDesc = rs.getString("subStatusDesc");
			final boolean isActive = false;
			final CodeValueData subStatus = CodeValueData.instance(subStatusId, subStatusValue, subStatusDesc, isActive);

			final Long officeId = JdbcSupport.getLong(rs, "officeId");
			final String officeName = rs.getString("officeName");

			final Long transferToOfficeId = JdbcSupport.getLong(rs, "transferToOfficeId");
			final String transferToOfficeName = rs.getString("transferToOfficeName");

			final Long id = JdbcSupport.getLong(rs, "id");
			final String firstname = rs.getString("firstname");
			final String spousename = rs.getString("spousename");
			final String lastname = rs.getString("lastname");
			final String fsfirstname = rs.getString("fsfirstname");
			final String fsmiddlename = null;
			final String fslastname = null;
			final String maidenname = rs.getString("maidenname");
			final String custmothername = rs.getString("custmothername");
			final String alternateMobileNo = rs.getString("alternateMobileNo");
			final String secIdProofNo = rs.getString("secIdProofNo");
			final String secaddressproofno = rs.getString("secaddressproofno");
			final String lastverifiedmobile = rs.getString("lastverifiedmobile");
			final String otherexpensestf = rs.getString("otherexpensestf");
			final String othersrcinctf = rs.getString("othersrcinctf");
			final String otherobligations = rs.getString("otherobligations");
			final String lastverifiedsecondaryid = rs.getString("lastverifiedsecondaryid");
			final String lastverifiedadhar = rs.getString("lastverifiedadhar");
			final String adhar = rs.getString("adhar");
			final String nrega = rs.getString("nrega");
			final String pan = rs.getString("pan");
			final String fullname = rs.getString("fullname");
			final String displayName = rs.getString("displayName");
			final String externalId = rs.getString("externalId");
			final String externalIdd = rs.getString("externalIdd");
			final String mobileNo = rs.getString("mobileNo");
			final String gstNo = rs.getString("gstNo");
			final String age = rs.getString("age");
			final String nomrelationshipid = rs.getString("nomrelationshipid");
			final String nomgenderid = rs.getString("nomgenderid");
			final String nomage = rs.getString("nomage");
			final String nomprofessionid = rs.getString("nomprofessionid");
			final String nomeducationalid = rs.getString("nomeducationalid");
			final String nommaritalid = rs.getString("nommaritalid");
			final String incdailysales = rs.getString("incdailysales");
			final String exprawmaterial = rs.getString("exprawmaterial");
			final String expstaffsal = rs.getString("expstaffsal");
			final String exppowertelephone = rs.getString("exppowertelephone");
			final String exprepairsmaintainance = rs.getString("exprepairsmaintainance");
			final String expcommbrokerage = rs.getString("expcommbrokerage");
			final String incrent = rs.getString("incrent");
			final String incinterest = rs.getString("incinterest");
			final String incothers = rs.getString("incothers");
			final String tothouseholdinc = rs.getString("tothouseholdinc");
			final String exphousehold = rs.getString("exphousehold");
			final String expotherloans = rs.getString("expotherloans");
			final String totnetdispfamily = rs.getString("totnetdispfamily");

			final String idproofNo = rs.getString("idproofNo");
			final String addrproofNo = rs.getString("addrproofNo");
			final String expinterest = rs.getString("expinterest");
			final String expofficerent = rs.getString("expofficerent");
			final String exptravel = rs.getString("exptravel");
			final String expothers = rs.getString("expothers");
			final String totbusinessprofit = rs.getString("totbusinessprofit");
			final String incspouse = rs.getString("incspouse");

			//final String alternateNo = rs.getString("alternateNo");
			//final String aadhaarNo = rs.getString("aadhaarNo");
			final boolean isStaff = rs.getBoolean("isStaff");
			final String emailAddress = rs.getString("emailAddress");
			final LocalDate dateOfBirth = JdbcSupport.getLocalDate(rs, "dateOfBirth");
			final LocalDate lastverifiedSecondaryidDate = JdbcSupport.getLocalDate(rs, "lastverifiedSecondaryidDate");
			final LocalDate lastverifiedmobiledate = JdbcSupport.getLocalDate(rs,"lastverifiedmobiledate");
			final LocalDate lastverifiedadhardate = JdbcSupport.getLocalDate(rs,"lastverifiedadhardate");
			final Long genderId = JdbcSupport.getLong(rs, "genderId");
			final String genderValue = rs.getString("genderValue");
			final CodeValueData gender = CodeValueData.instance(genderId, genderValue);

			//            final Long statusOneId = JdbcSupport.getLong(rs, "statusOneId");
			//            final String statusOneValue = rs.getString("statusOneValue");
			//            final CodeValueData statusOne = CodeValueData.instance(statusOneId, statusOneValue);
			//            
			//            final Long statusTwoId = JdbcSupport.getLong(rs, "statusTwoId");
			//            final String statusTwoValue = rs.getString("statusTwoValue");
			//            final CodeValueData statusTwo = CodeValueData.instance(statusTwoId, statusTwoValue);

			//            final Long introducerId = JdbcSupport.getLong(rs, "introducerId");
			//            final String introducerValue = rs.getString("introducerValue");
			//            final CodeValueData introducer = CodeValueData.instance(introducerId, introducerValue);
			//            
			//            final Long sourceoneId = JdbcSupport.getLong(rs, "sourceoneId");
			//            final String sourceoneValue = rs.getString("sourceoneValue");
			//            final CodeValueData sourceone = CodeValueData.instance(sourceoneId, sourceoneValue);
			//            
			//            final Long sourcetwoId = JdbcSupport.getLong(rs, "sourcetwoId");
			//            final String sourcetwoValue = rs.getString("sourcetwoValue");
			//            final CodeValueData sourcetwo = CodeValueData.instance(sourcetwoId, sourcetwoValue);
			//            
			//            final Long purposeoneId = JdbcSupport.getLong(rs, "purposeoneId");
			//            final String purposeoneValue = rs.getString("purposetwoValue");
			//            final CodeValueData purposeone = CodeValueData.instance(purposeoneId, purposeoneValue);
			//            
			//            final Long purposetwoId = JdbcSupport.getLong(rs, "purposetwoId");
			//            final String purposetwoValue = rs.getString("purposetwoValue");
			//            final CodeValueData purposetwo = CodeValueData.instance(purposetwoId, purposetwoValue);

			final Long fatherspouseId = JdbcSupport.getLong(rs, "fatherspouseId");
			final String fatherspouseValue = rs.getString("fatherspouseValue");
			final CodeValueData fatherspouse = CodeValueData.instance(fatherspouseId, fatherspouseValue);

			final Long educationId = JdbcSupport.getLong(rs, "educationId");
			final String educationValue = rs.getString("educationValue");
			final CodeValueData education = CodeValueData.instance(educationId, educationValue);

			final Long maritalId = JdbcSupport.getLong(rs, "maritalId");
			final String maritalValue = rs.getString("maritalValue");
			final CodeValueData marital = CodeValueData.instance(maritalId, maritalValue);

			final Long professionId = JdbcSupport.getLong(rs, "professionId");
			final String professionValue = rs.getString("professionValue");
			final CodeValueData profession = CodeValueData.instance(professionId, professionValue);

			final Long belongingId = JdbcSupport.getLong(rs, "belongingId");
			final String belongingValue = rs.getString("belongingValue");
			final CodeValueData belonging = CodeValueData.instance(belongingId, belongingValue);

			final Long annualId = JdbcSupport.getLong(rs, "annualId");
			final String annualValue = rs.getString("annualValue");
			final CodeValueData annual = CodeValueData.instance(annualId, annualValue);

			final Long landId = JdbcSupport.getLong(rs, "landId");
			final String landValue = rs.getString("landValue");
			final CodeValueData land = CodeValueData.instance(landId, landValue);

			final Long houseId = JdbcSupport.getLong(rs, "houseId");
			final String houseValue = rs.getString("houseValue");
			final CodeValueData house = CodeValueData.instance(houseId, houseValue);

			final Long formId = JdbcSupport.getLong(rs, "formId");
			final String formValue = rs.getString("formValue");
			final CodeValueData form = CodeValueData.instance(formId, formValue);

			final Long titleId = JdbcSupport.getLong(rs, "titleId");
			final String titleValue = rs.getString("titleValue");
			final CodeValueData title = CodeValueData.instance(titleId, titleValue);

			final Long religionId = JdbcSupport.getLong(rs, "religionId");
			final String religionValue = rs.getString("religionValue");
			final CodeValueData religion = CodeValueData.instance(religionId, religionValue);

			final Long alternateNoIdID = JdbcSupport.getLong(rs, "alternateNoId");
			final String alternateNoIdValue = rs.getString("alternateNoIdValue");
			final CodeValueData alternateNoId = CodeValueData.instance(alternateNoIdID, alternateNoIdValue);

			final Long idproofId = JdbcSupport.getLong(rs, "idproofId");
			final String idproofValue = rs.getString("idproofValue");
			final CodeValueData idproof = CodeValueData.instance(idproofId, idproofValue);

			final Long addrproofId = JdbcSupport.getLong(rs, "addrproofId");
			final String addrproofValue = rs.getString("addrproofValue");
			final CodeValueData addrproof = CodeValueData.instance(addrproofId, addrproofValue);

			final Long clienttypeId = JdbcSupport.getLong(rs, "clienttypeId");
			final String clienttypeValue = rs.getString("clienttypeValue");
			final CodeValueData clienttype = CodeValueData.instance(clienttypeId, clienttypeValue);

			final Long classificationId = JdbcSupport.getLong(rs, "classificationId");
			final String classificationValue = rs.getString("classificationValue");
			final CodeValueData classification = CodeValueData.instance(classificationId, classificationValue);

			final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
			final Long imageId = JdbcSupport.getLong(rs, "imageId");

			final Long staffId = JdbcSupport.getLong(rs, "staffId");
			final String staffName = rs.getString("staffName");

			final Long savingsProductId = JdbcSupport.getLong(rs, "savingsProductId");
			final String savingsProductName = rs.getString("savingsProductName");
			final Long savingsAccountId = JdbcSupport.getLong(rs, "savingsAccountId");

			final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
			final String closedByUsername = rs.getString("closedByUsername");
			final String closedByFirstname = rs.getString("closedByFirstname");
			final String closedByLastname = rs.getString("closedByLastname");

			final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
			final String submittedByUsername = rs.getString("submittedByUsername");
			final String submittedByFirstname = rs.getString("submittedByFirstname");
			final String submittedByLastname = rs.getString("submittedByLastname");

			final String activatedByUsername = rs.getString("activatedByUsername");
			final String activatedByFirstname = rs.getString("activatedByFirstname");
			final String activatedByLastname = rs.getString("activatedByLastname");

			final Integer legalFormEnum = JdbcSupport.getInteger(rs, "legalFormEnum");
			EnumOptionData legalForm = null;
			if(legalFormEnum != null)
				legalForm = ClientEnumerations.legalForm(legalFormEnum);

			final Long constitutionId = JdbcSupport.getLong(rs, "constitutionId");
			final String constitutionValue = rs.getString("constitutionValue");
			final CodeValueData constitution = CodeValueData.instance(constitutionId, constitutionValue);
			final String incorpNo = rs.getString("incorpNo");
			final LocalDate incorpValidityTill = JdbcSupport.getLocalDate(rs, "incorpValidityTill");
			final Long mainBusinessLineId = JdbcSupport.getLong(rs, "mainBusinessLineId");            
			final String mainBusinessLineValue = rs.getString("mainBusinessLineValue");
			final CodeValueData mainBusinessLine = CodeValueData.instance(mainBusinessLineId, mainBusinessLineValue);
			final String remarks = rs.getString("remarks");
			final Long debt = rs.getLong("debt");
			final Long income = rs.getLong("income");
			final Float debtincratio = rs.getFloat("debtincratio");
			final Long amountapplied = rs.getLong("amountapplied");
			//final Long group = rs.getLong("group");
			//final Long center = rs.getLong("center");
			final Boolean cpvData = rs.getBoolean("cpvData");
			final ClientNonPersonData clientNonPerson = new ClientNonPersonData(constitution, incorpNo, incorpValidityTill, mainBusinessLine, remarks);

			final ClientTimelineData timeline = new ClientTimelineData(submittedOnDate, submittedByUsername, submittedByFirstname,
					submittedByLastname, activationDate, activatedByUsername, activatedByFirstname, activatedByLastname, closedOnDate,
					closedByUsername, closedByFirstname, closedByLastname);

			return ClientData.instance(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id,
					firstname, spousename, lastname,fsfirstname, fsmiddlename, fslastname, maidenname, custmothername, alternateMobileNo, secIdProofNo, secaddressproofno,lastverifiedmobile,otherexpensestf,othersrcinctf,otherobligations,lastverifiedsecondaryid,lastverifiedadhar, adhar,nrega,pan,
					fullname, displayName, externalId, externalIdd,mobileNo,gstNo,age,nomrelationshipid,
					nomgenderid,
					nomage,
					nomprofessionid,
					nomeducationalid,
					nommaritalid,incdailysales,exprawmaterial,expstaffsal, exppowertelephone,exprepairsmaintainance,expcommbrokerage,
					incrent,incinterest,incothers,tothouseholdinc,exphousehold,expotherloans, totnetdispfamily,expinterest,expofficerent,exptravel,expothers,totbusinessprofit,incspouse, idproofNo, addrproofNo, emailAddress, dateOfBirth,lastverifiedSecondaryidDate,lastverifiedmobiledate,lastverifiedadhardate,
					gender,fatherspouse,education,marital,profession, belonging, annual,land,house,form, title, religion,alternateNoId, idproof, addrproof,activationDate,
					imageId, staffId, staffName, timeline, savingsProductId, savingsProductName, savingsAccountId, clienttype,
					classification, legalForm, clientNonPerson, isStaff, submittedOnDate,debt,income,debtincratio,amountapplied,cpvData);

		}
	}

	private static final class ParentGroupsMapper implements RowMapper<GroupGeneralData> {

		public String parentGroupsSchema() {
			return "gp.id As groupId , gp.account_no as accountNo, gp.display_name As groupName from m_client cl JOIN m_group_client gc ON cl.id = gc.client_id "
					+ "JOIN m_group gp ON gp.id = gc.group_id WHERE cl.id  = ?";
		}

		@Override
		public GroupGeneralData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final Long groupId = JdbcSupport.getLong(rs, "groupId");
			final String groupName = rs.getString("groupName");
			final String accountNo = rs.getString("accountNo");

			return GroupGeneralData.lookup(groupId, accountNo, groupName);
		}
	}

	private static final class ClientLookupMapper implements RowMapper<ClientData> {

		private final String schema;

		public ClientLookupMapper() {
			final StringBuilder builder = new StringBuilder(200);

			builder.append("c.id as id, c.display_name as displayName, ");
			builder.append("c.office_id as officeId, o.name as officeName ");
			builder.append("from m_client c ");
			builder.append("join m_office o on o.id = c.office_id ");

			this.schema = builder.toString();
		}

		public String schema() {
			return this.schema;
		}

		@Override
		public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String displayName = rs.getString("displayName");
			final Long officeId = rs.getLong("officeId");
			final String officeName = rs.getString("officeName");

			return ClientData.lookup(id, displayName, officeId, officeName);
		}
	}

	@Override
	public ClientData retrieveClientByIdentifier(final Long identifierTypeId, final String identifierKey) {
		try {
			final ClientIdentifierMapper mapper = new ClientIdentifierMapper();

			final String sql = "select " + mapper.clientLookupByIdentifierSchema();

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { identifierTypeId, identifierKey });
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientIdentifierMapper implements RowMapper<ClientData> {

		public String clientLookupByIdentifierSchema() {
			return "c.id as id, c.account_no as accountNo, c.firstname as firstname, c.middlename as middlename, c.lastname as lastname, "
					+ "c.fullname as fullname, c.display_name as displayName," + "c.office_id as officeId, o.name as officeName "
					+ " from m_client c, m_office o, m_client_identifier ci " + "where o.id = c.office_id and c.id=ci.client_id "
					+ "and ci.document_type_id= ? and ci.document_key like ?";
		}

		@Override
		public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String accountNo = rs.getString("accountNo");

			final String firstname = rs.getString("firstname");
			final String middlename = rs.getString("middlename");
			final String lastname = rs.getString("lastname");
			final String fullname = rs.getString("fullname");
			final String displayName = rs.getString("displayName");

			final Long officeId = rs.getLong("officeId");
			final String officeName = rs.getString("officeName");

			return ClientData.clientIdentifier(id, accountNo, firstname, middlename, lastname,null,null,null,null,null,null,null,null,null,null,null, fullname, displayName, null, null, null, null, null, null, officeId, officeName);
		}
	}

	private Long defaultToUsersOfficeIfNull(final Long officeId) {
		Long defaultOfficeId = officeId;
		if (defaultOfficeId == null) {
			defaultOfficeId = this.context.authenticatedUser().getOffice().getId();
		}
		return defaultOfficeId;
	}

	@Override
	public ClientData retrieveAllNarrations(final String clientNarrations) {
		final List<CodeValueData> narrations = new ArrayList<>(this.codeValueReadPlatformService.retrieveCodeValuesByCode(clientNarrations));
		final Collection<CodeValueData> clientTypeOptions = null;
		final Collection<CodeValueData> clientClassificationOptions = null;
		final Collection<CodeValueData> clientNonPersonConstitutionOptions = null;
		final Collection<CodeValueData> clientNonPersonMainBusinessLineOptions = null;
		final List<EnumOptionData> clientLegalFormOptions = null;
		return ClientData.template(null, null, null, null, narrations, null, null, null,null,null,null,null,null,null,null,null,null,null, null, null, clientClassificationOptions, 
				clientNonPersonConstitutionOptions, clientNonPersonMainBusinessLineOptions,  null, null, null, null,null,null);
	}

	@Override
	public Date retrieveClientTransferProposalDate(Long clientId) {
		validateClient(clientId);
		final String sql = "SELECT cl.proposed_transfer_date FROM m_client cl WHERE cl.id =? ";
		try {
			return this.jdbcTemplate.queryForObject(sql, Date.class, clientId);
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void validateClient(Long clientId) {
		try {
			final String sql = "SELECT cl.id FROM m_client cl WHERE cl.id =? ";
			this.jdbcTemplate.queryForObject(sql, Long.class, clientId);
		} catch (final EmptyResultDataAccessException e) {
			throw new ClientNotFoundException(clientId, e);
		}
	}

	@Override
	public Page<ClientData> retrievePagedAll(final SearchParameters searchParameters, final PaginationParameters parameters) {

		////System.out.println("in retrive page all");

		final AppUser currentUser = this.context.authenticatedUser();
		final String hierarchy = currentUser.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
		sqlBuilder.append(new ClientMapper().schema());

		final SQLBuilder extraCriteria = new SQLBuilder();
		final String sqlSearch = searchParameters.getSqlSearch();

		final Long staffId = searchParameters.getStaffId();
	

		
		String status = searchParameters.getStatus();



		if (status != null) {
			if(status.startsWith("!"))
			{
				extraCriteria.addNonNullCriteria("c.status_enum not like", "%" + "300" + "%");
			}
			else
			{
				extraCriteria.addNonNullCriteria("c.status_enum like", "%" + status + "%");

			}
		}


		extraCriteria.addCriteria(" o.hierarchy like ", hierarchySearchString);
		sqlBuilder.append(" ").append(extraCriteria.getSQLTemplate());
		
		if(sqlSearch!=null && staffId!=null)
		{
			sqlBuilder.append(" AND (c.firstname like '%" + sqlSearch + "%' OR c.mobile_no like '%" + sqlSearch + "%' )");
			sqlBuilder.append(" and c.staff_id = " + staffId);
		}
		else
		{
			if(sqlSearch!=null)
			{
				sqlBuilder.append(" AND (c.firstname like '%" + sqlSearch + "%' OR c.mobile_no like '%" + sqlSearch + "%' )");
			}
		}
		
		
		
		if (parameters.isOrderByRequested()) {
			sqlBuilder.append(" order by ").append(searchParameters.getOrderBy()).append(' ').append(searchParameters.getSortOrder());
			this.columnValidator.validateSqlInjection(sqlBuilder.toString(), searchParameters.getOrderBy(),
					searchParameters.getSortOrder());
		}

		if (parameters.isLimited()) {
			sqlBuilder.append(" limit ").append(searchParameters.getLimit());
			if (searchParameters.isOffset()) {
				sqlBuilder.append(" offset ").append(searchParameters.getOffset());
			}
		}
		// sqlBuilder"c.firstname like %" + sqlSearch +"%", "OR c.mobile_no like %" + sqlSearch +"%","and c.staff_id = "+staffId} ; 

		//   ////System.out.println(sqls[0]);
		final String sqlCountRows = "SELECT FOUND_ROWS()";
		return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), extraCriteria.getArguments(),
				this.clientMapper);
	}

}