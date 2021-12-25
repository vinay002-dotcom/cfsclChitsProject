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
package org.apache.fineract.scheduledjobs.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.fineract.accounting.glaccount.domain.TrialBalance;
import org.apache.fineract.accounting.glaccount.domain.TrialBalanceRepositoryWrapper;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSourceServiceFactory;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.ChitGroup.data.ChitChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitCycleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.handler.FindWorkingDays;
import org.apache.fineract.portfolio.ChitGroup.service.ChitChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitCycleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitDemandScheduleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitDemandScheduleWritePlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberChargeWritePlatformService;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.DepositAccountUtils;
import org.apache.fineract.portfolio.savings.data.DepositAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountAnnualFeeData;
import org.apache.fineract.portfolio.savings.service.DepositAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.DepositAccountWritePlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountChargeReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountDividendReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountSchedularService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.JsonObject;

@Service(value = "scheduledJobRunnerService")
public class ScheduledJobRunnerServiceImpl implements ScheduledJobRunnerService {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledJobRunnerServiceImpl.class);

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final RoutingDataSourceServiceFactory dataSourceServiceFactory;
	private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
	private final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService;
	private final DepositAccountReadPlatformService depositAccountReadPlatformService;
	private final DepositAccountWritePlatformService depositAccountWritePlatformService;
	private final ShareAccountDividendReadPlatformService shareAccountDividendReadPlatformService;
	private final ShareAccountSchedularService shareAccountSchedularService;
	private final TrialBalanceRepositoryWrapper trialBalanceRepositoryWrapper;
	private final ChitGroupReadPlatformService chitGroupReadPlatformService ;
	private final ChitChargeReadPlatformServices chitChargeReadPlatformServices;
	private final ChitCycleReadPlatformService chitCycleReadPlatformService ;
	private final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices;
	private final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService;
	private FindWorkingDays findWorkingDays = new FindWorkingDays();
	private final ChitDemandScheduleWritePlatformService chitDemandScheduleWritePlatformService;
	private final ChitSubscriberChargeWritePlatformService chitSubscriberChargeWritePlatformService;
	private final ConfigurationReadPlatformService configurationReadPlatformService;

	@Autowired
	public ScheduledJobRunnerServiceImpl(final RoutingDataSourceServiceFactory dataSourceServiceFactory,
			final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
			final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService,
			final DepositAccountReadPlatformService depositAccountReadPlatformService,
			final DepositAccountWritePlatformService depositAccountWritePlatformService,
			final ShareAccountDividendReadPlatformService shareAccountDividendReadPlatformService,
			final ShareAccountSchedularService shareAccountSchedularService,
			final TrialBalanceRepositoryWrapper trialBalanceRepositoryWrapper,final ChitGroupReadPlatformService chitGroupReadPlatformService,final ChitChargeReadPlatformServices chitChargeReadPlatformServices ,
			final ChitCycleReadPlatformService chitCycleReadPlatformService,
			final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices,final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService,
			final ChitDemandScheduleWritePlatformService chitDemandScheduleWritePlatformService,final ChitSubscriberChargeWritePlatformService chitSubscriberChargeWritePlatformService,
			final ConfigurationReadPlatformService configurationReadPlatformService) {
		this.dataSourceServiceFactory = dataSourceServiceFactory;
		this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
		this.savingsAccountChargeReadPlatformService = savingsAccountChargeReadPlatformService;
		this.depositAccountReadPlatformService = depositAccountReadPlatformService;
		this.depositAccountWritePlatformService = depositAccountWritePlatformService;
		this.shareAccountDividendReadPlatformService = shareAccountDividendReadPlatformService;
		this.shareAccountSchedularService = shareAccountSchedularService;
		this.trialBalanceRepositoryWrapper = trialBalanceRepositoryWrapper;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.chitChargeReadPlatformServices = chitChargeReadPlatformServices;
		this.chitCycleReadPlatformService = chitCycleReadPlatformService;
		this.chitSubscriberChargeReadPlatformServices = chitSubscriberChargeReadPlatformServices;
		this.chitDemandScheduleReadPlatformService = chitDemandScheduleReadPlatformService;
		this.chitDemandScheduleWritePlatformService = chitDemandScheduleWritePlatformService;
		this.chitSubscriberChargeWritePlatformService = chitSubscriberChargeWritePlatformService;
		this.configurationReadPlatformService = configurationReadPlatformService;
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.UPDATE_LOAN_SUMMARY)
	public void updateLoanSummaryDetails() {

		final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

		final StringBuilder updateSqlBuilder = new StringBuilder(900);
		updateSqlBuilder.append("update m_loan ");
		updateSqlBuilder.append("join (");
		updateSqlBuilder.append("SELECT ml.id AS loanId,");
		updateSqlBuilder.append("SUM(mr.principal_amount) as principal_disbursed_derived, ");
		updateSqlBuilder.append("SUM(IFNULL(mr.principal_completed_derived,0)) as principal_repaid_derived, ");
		updateSqlBuilder.append("SUM(IFNULL(mr.principal_writtenoff_derived,0)) as principal_writtenoff_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.interest_amount,0)) as interest_charged_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.interest_completed_derived,0)) as interest_repaid_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.interest_waived_derived,0)) as interest_waived_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.interest_writtenoff_derived,0)) as interest_writtenoff_derived,");
		updateSqlBuilder.append(
				"SUM(IFNULL(mr.fee_charges_amount,0)) + IFNULL((select SUM(lc.amount) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_time_enum=1),0) as fee_charges_charged_derived,");
		updateSqlBuilder.append(
				"SUM(IFNULL(mr.fee_charges_completed_derived,0)) + IFNULL((select SUM(lc.amount_paid_derived) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_time_enum=1),0) as fee_charges_repaid_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.fee_charges_waived_derived,0)) as fee_charges_waived_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.fee_charges_writtenoff_derived,0)) as fee_charges_writtenoff_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_amount,0)) as penalty_charges_charged_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_completed_derived,0)) as penalty_charges_repaid_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_waived_derived,0)) as penalty_charges_waived_derived,");
		updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_writtenoff_derived,0)) as penalty_charges_writtenoff_derived ");
		updateSqlBuilder.append(" FROM m_loan ml ");
		updateSqlBuilder.append("INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
		updateSqlBuilder.append("WHERE ml.disbursedon_date is not null ");
		updateSqlBuilder.append("GROUP BY ml.id ");
		updateSqlBuilder.append(") x on x.loanId = m_loan.id ");

		updateSqlBuilder.append("SET m_loan.principal_disbursed_derived = x.principal_disbursed_derived,");
		updateSqlBuilder.append("m_loan.principal_repaid_derived = x.principal_repaid_derived,");
		updateSqlBuilder.append("m_loan.principal_writtenoff_derived = x.principal_writtenoff_derived,");
		updateSqlBuilder.append(
				"m_loan.principal_outstanding_derived = (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)),");
		updateSqlBuilder.append("m_loan.interest_charged_derived = x.interest_charged_derived,");
		updateSqlBuilder.append("m_loan.interest_repaid_derived = x.interest_repaid_derived,");
		updateSqlBuilder.append("m_loan.interest_waived_derived = x.interest_waived_derived,");
		updateSqlBuilder.append("m_loan.interest_writtenoff_derived = x.interest_writtenoff_derived,");
		updateSqlBuilder.append(
				"m_loan.interest_outstanding_derived = (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)),");
		updateSqlBuilder.append("m_loan.fee_charges_charged_derived = x.fee_charges_charged_derived,");
		updateSqlBuilder.append("m_loan.fee_charges_repaid_derived = x.fee_charges_repaid_derived,");
		updateSqlBuilder.append("m_loan.fee_charges_waived_derived = x.fee_charges_waived_derived,");
		updateSqlBuilder.append("m_loan.fee_charges_writtenoff_derived = x.fee_charges_writtenoff_derived,");
		updateSqlBuilder.append(
				"m_loan.fee_charges_outstanding_derived = (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)),");
		updateSqlBuilder.append("m_loan.penalty_charges_charged_derived = x.penalty_charges_charged_derived,");
		updateSqlBuilder.append("m_loan.penalty_charges_repaid_derived = x.penalty_charges_repaid_derived,");
		updateSqlBuilder.append("m_loan.penalty_charges_waived_derived = x.penalty_charges_waived_derived,");
		updateSqlBuilder.append("m_loan.penalty_charges_writtenoff_derived = x.penalty_charges_writtenoff_derived,");
		updateSqlBuilder.append(
				"m_loan.penalty_charges_outstanding_derived = (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived)),");
		updateSqlBuilder.append(
				"m_loan.total_expected_repayment_derived = (x.principal_disbursed_derived + x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
		updateSqlBuilder.append(
				"m_loan.total_repayment_derived = (x.principal_repaid_derived + x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
		updateSqlBuilder.append(
				"m_loan.total_expected_costofloan_derived = (x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
		updateSqlBuilder.append(
				"m_loan.total_costofloan_derived = (x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
		updateSqlBuilder.append(
				"m_loan.total_waived_derived = (x.interest_waived_derived + x.fee_charges_waived_derived + x.penalty_charges_waived_derived),");
		updateSqlBuilder.append(
				"m_loan.total_writtenoff_derived = (x.interest_writtenoff_derived +  x.fee_charges_writtenoff_derived + x.penalty_charges_writtenoff_derived),");
		updateSqlBuilder.append("m_loan.total_outstanding_derived=");
		updateSqlBuilder.append(" (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)) + ");
		updateSqlBuilder.append(
				" (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)) +");
		updateSqlBuilder.append(
				" (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)) +");
		updateSqlBuilder.append(
				" (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived))");

		final int result = jdbcTemplate.update(updateSqlBuilder.toString());

		LOG.info("{}: Records affected by updateLoanSummaryDetails: {}", ThreadLocalContextUtil.getTenant().getName(), result);
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.UPDATE_LOAN_PAID_IN_ADVANCE)
	public void updateLoanPaidInAdvance() {

		final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

		jdbcTemplate.execute("truncate table m_loan_paid_in_advance");

		final StringBuilder updateSqlBuilder = new StringBuilder(900);

		updateSqlBuilder.append(
				"INSERT INTO m_loan_paid_in_advance(loan_id, principal_in_advance_derived, interest_in_advance_derived, fee_charges_in_advance_derived, penalty_charges_in_advance_derived, total_in_advance_derived)");
		updateSqlBuilder.append(" select ml.id as loanId,");
		updateSqlBuilder.append(" SUM(ifnull(mr.principal_completed_derived, 0)) as principal_in_advance_derived,");
		updateSqlBuilder.append(" SUM(ifnull(mr.interest_completed_derived, 0)) as interest_in_advance_derived,");
		updateSqlBuilder.append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) as fee_charges_in_advance_derived,");
		updateSqlBuilder.append(" SUM(ifnull(mr.penalty_charges_completed_derived, 0)) as penalty_charges_in_advance_derived,");
		updateSqlBuilder.append(
				" (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) + SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) as total_in_advance_derived");
		updateSqlBuilder.append(" FROM m_loan ml ");
		updateSqlBuilder.append(" INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
		updateSqlBuilder.append(" WHERE ml.loan_status_id = 300 ");
		updateSqlBuilder.append(" and mr.duedate >= CURDATE() ");
		updateSqlBuilder.append(" GROUP BY ml.id");
		updateSqlBuilder
		.append(" HAVING (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) +");
		updateSqlBuilder
		.append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) > 0.0");

		final int result = jdbcTemplate.update(updateSqlBuilder.toString());

		LOG.info("{}: Records affected by updateLoanPaidInAdvance: {}", ThreadLocalContextUtil.getTenant().getName(), result);
	}

	@Override
	@CronTarget(jobName = JobName.APPLY_ANNUAL_FEE_FOR_SAVINGS)
	public void applyAnnualFeeForSavings() {

		final Collection<SavingsAccountAnnualFeeData> annualFeeData = this.savingsAccountChargeReadPlatformService
				.retrieveChargesWithAnnualFeeDue();

		for (final SavingsAccountAnnualFeeData savingsAccountReference : annualFeeData) {
			try {
				this.savingsAccountWritePlatformService.applyAnnualFee(savingsAccountReference.getId(),
						savingsAccountReference.getAccountId());
			} catch (final PlatformApiDataValidationException e) {
				final List<ApiParameterError> errors = e.getErrors();
				for (final ApiParameterError error : errors) {
					LOG.error("Apply annual fee failed for account: {} with message {}", savingsAccountReference.getAccountNo(), error);
				}
			} catch (final Exception ex) {
				LOG.error("Apply annual fee failed for account: {}", savingsAccountReference.getAccountNo(), ex);
			}
		}

		LOG.info("{}: Records affected by applyAnnualFeeForSavings: {}", ThreadLocalContextUtil.getTenant().getName(),
				annualFeeData.size());
	}

	@Override
	@CronTarget(jobName = JobName.PAY_DUE_SAVINGS_CHARGES)
	public void applyDueChargesForSavings() throws JobExecutionException {
		final Collection<SavingsAccountAnnualFeeData> chargesDueData = this.savingsAccountChargeReadPlatformService
				.retrieveChargesWithDue();
		List<Throwable> exceptions = new ArrayList<>();
		for (final SavingsAccountAnnualFeeData savingsAccountReference : chargesDueData) {
			try {
				this.savingsAccountWritePlatformService.applyChargeDue(savingsAccountReference.getId(),
						savingsAccountReference.getAccountId());
			} catch (final PlatformApiDataValidationException e) {
				exceptions.add(e);
				final List<ApiParameterError> errors = e.getErrors();
				for (final ApiParameterError error : errors) {
					LOG.error("Apply Charges due for savings failed for account {} with message: {}",
							savingsAccountReference.getAccountNo(), error.getDeveloperMessage(), e);
				}
			} catch (final Exception ex) {
				exceptions.add(ex);
				LOG.error("Apply Charges due for savings failed for account: {}", savingsAccountReference.getAccountNo(), ex);
			}
		}
		LOG.info("{}: Records affected by applyDueChargesForSavings: {}", ThreadLocalContextUtil.getTenant().getName(),
				chargesDueData.size());
		if (!exceptions.isEmpty()) {
			throw new JobExecutionException(exceptions);
		}
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.UPDATE_NPA)
	public void updateNPA() {

		final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

		final StringBuilder resetNPASqlBuilder = new StringBuilder(900);
		resetNPASqlBuilder.append("update m_loan loan ");
		resetNPASqlBuilder.append("left join m_loan_arrears_aging laa on laa.loan_id = loan.id ");
		resetNPASqlBuilder.append("inner join m_product_loan mpl on mpl.id = loan.product_id and mpl.overdue_days_for_npa is not null ");
		resetNPASqlBuilder.append("set loan.is_npa = 0 ");
		resetNPASqlBuilder.append("where  loan.loan_status_id = 300 and mpl.account_moves_out_of_npa_only_on_arrears_completion = 0 ");
		resetNPASqlBuilder
		.append("or (mpl.account_moves_out_of_npa_only_on_arrears_completion = 1 and laa.overdue_since_date_derived is null)");

		jdbcTemplate.update(resetNPASqlBuilder.toString());

		final StringBuilder updateSqlBuilder = new StringBuilder(900);

		updateSqlBuilder.append("UPDATE m_loan as ml,");
		updateSqlBuilder.append(" (select loan.id ");
		updateSqlBuilder.append("from m_loan_arrears_aging laa");
		updateSqlBuilder.append(" INNER JOIN  m_loan loan on laa.loan_id = loan.id ");
		updateSqlBuilder.append(" INNER JOIN m_product_loan mpl on mpl.id = loan.product_id AND mpl.overdue_days_for_npa is not null ");
		updateSqlBuilder.append("WHERE loan.loan_status_id = 300  and ");
		updateSqlBuilder.append("laa.overdue_since_date_derived < SUBDATE(CURDATE(),INTERVAL  ifnull(mpl.overdue_days_for_npa,0) day) ");
		updateSqlBuilder.append("group by loan.id) as sl ");
		updateSqlBuilder.append("SET ml.is_npa=1 where ml.id=sl.id ");

		final int result = jdbcTemplate.update(updateSqlBuilder.toString());

		LOG.info("{}: Records affected by updateNPA: {}", ThreadLocalContextUtil.getTenant().getName(), result);
	}

	@Override
	@CronTarget(jobName = JobName.UPDATE_DEPOSITS_ACCOUNT_MATURITY_DETAILS)
	public void updateMaturityDetailsOfDepositAccounts() {

		final Collection<DepositAccountData> depositAccounts = this.depositAccountReadPlatformService.retrieveForMaturityUpdate();

		for (final DepositAccountData depositAccount : depositAccounts) {
			try {
				final DepositAccountType depositAccountType = DepositAccountType.fromInt(depositAccount.depositType().getId().intValue());
				this.depositAccountWritePlatformService.updateMaturityDetails(depositAccount.id(), depositAccountType);
			} catch (final PlatformApiDataValidationException e) {
				final List<ApiParameterError> errors = e.getErrors();
				for (final ApiParameterError error : errors) {
					LOG.error("Update maturity details failed for account: {} with message {}", depositAccount.accountNo(),
							error.getDeveloperMessage());
				}
			} catch (final Exception ex) {
				LOG.error("Update maturity details failed for account: {}", depositAccount.accountNo(), ex);
			}
		}

		LOG.info("{}: Records affected by updateMaturityDetailsOfDepositAccounts: {}", ThreadLocalContextUtil.getTenant().getName(),
				depositAccounts.size());
	}

	@Override
	@CronTarget(jobName = JobName.GENERATE_RD_SCEHDULE)
	public void generateRDSchedule() {
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
		final Collection<Map<String, Object>> scheduleDetails = this.depositAccountReadPlatformService.retriveDataForRDScheduleCreation();
		String insertSql = "INSERT INTO `m_mandatory_savings_schedule` (`savings_account_id`, `duedate`, `installment`, `deposit_amount`, `completed_derived`, `created_date`, `lastmodified_date`) VALUES ";
		StringBuilder sb = new StringBuilder();
		String currentDate = formatterWithTime.format(DateUtils.getLocalDateTimeOfTenant());
		int iterations = 0;
		for (Map<String, Object> details : scheduleDetails) {
			Long count = (Long) details.get("futureInstallemts");
			if (count == null) {
				count = 0L;
			}
			final Long savingsId = (Long) details.get("savingsId");
			final BigDecimal amount = (BigDecimal) details.get("amount");
			final String recurrence = (String) details.get("recurrence");
			Date date = (Date) details.get("dueDate");
			LocalDate lastDepositDate = LocalDate.ofInstant(date.toInstant(), DateUtils.getDateTimeZoneOfTenant());
			Integer installmentNumber = (Integer) details.get("installment");
			while (count < DepositAccountUtils.GENERATE_MINIMUM_NUMBER_OF_FUTURE_INSTALMENTS) {
				count++;
				installmentNumber++;
				lastDepositDate = DepositAccountUtils.calculateNextDepositDate(lastDepositDate, recurrence);

				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append("(");
				sb.append(savingsId);
				sb.append(",'");
				sb.append(formatter.format(lastDepositDate));
				sb.append("',");
				sb.append(installmentNumber);
				sb.append(",");
				sb.append(amount);
				sb.append(", b'0','");
				sb.append(currentDate);
				sb.append("','");
				sb.append(currentDate);
				sb.append("')");
				iterations++;
				if (iterations > 200) {
					jdbcTemplate.update(insertSql + sb.toString());
					sb = new StringBuilder();
				}

			}
		}

		if (sb.length() > 0) {
			jdbcTemplate.update(insertSql + sb.toString());
		}

	}

	@Override
	@CronTarget(jobName = JobName.POST_DIVIDENTS_FOR_SHARES)
	public void postDividends() throws JobExecutionException {
		List<Throwable> exceptions = new ArrayList<>();
		List<Map<String, Object>> dividendDetails = this.shareAccountDividendReadPlatformService.retriveDividendDetailsForPostDividents();
		for (Map<String, Object> dividendMap : dividendDetails) {
			Long id = null;
			Long savingsId = null;
			if (dividendMap.get("id") instanceof BigInteger) {
				// Drizzle is returningBigInteger
				id = ((BigInteger) dividendMap.get("id")).longValue();
				savingsId = ((BigInteger) dividendMap.get("savingsAccountId")).longValue();
			} else { // MySQL connector is returning Long
				id = (Long) dividendMap.get("id");
				savingsId = (Long) dividendMap.get("savingsAccountId");
			}
			try {
				this.shareAccountSchedularService.postDividend(id, savingsId);
			} catch (final PlatformApiDataValidationException e) {
				exceptions.add(e);
				final List<ApiParameterError> errors = e.getErrors();
				for (final ApiParameterError error : errors) {
					LOG.error(
							"Post Dividends to savings failed due to ApiParameterError for Divident detail Id: {} and savings Id: {} with message: {}",
							id, savingsId, error.getDeveloperMessage(), e);
				}
			} catch (final Exception e) {
				LOG.error("Post Dividends to savings failed for Divident detail Id: {} and savings Id: {}", id, savingsId, e);
				exceptions.add(e);
			}
		}

		if (!exceptions.isEmpty()) {
			throw new JobExecutionException(exceptions);
		}
	}

	@Override
	@CronTarget(jobName = JobName.UPDATE_TRAIL_BALANCE_DETAILS)
	public void updateTrialBalanceDetails() throws JobExecutionException {
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
		final StringBuilder tbGapSqlBuilder = new StringBuilder(500);
		tbGapSqlBuilder.append("select distinct(je.transaction_date) ").append("from acc_gl_journal_entry je ")
		.append("where je.transaction_date > (select IFNULL(MAX(created_date),'2010-01-01') from m_trial_balance)");

		final List<Date> tbGaps = jdbcTemplate.queryForList(tbGapSqlBuilder.toString(), Date.class);

		for (Date tbGap : tbGaps) {
			LocalDate convDate = ZonedDateTime.ofInstant(tbGap.toInstant(), DateUtils.getDateTimeZoneOfTenant()).toLocalDate();
			int days = Math.toIntExact(ChronoUnit.DAYS.between(convDate, DateUtils.getLocalDateOfTenant()));
			if (days < 1) {
				continue;
			}
			final String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(tbGap);
			final StringBuilder sqlBuilder = new StringBuilder(600);
			sqlBuilder.append("Insert Into m_trial_balance(office_id, account_id, Amount, entry_date, created_date,closing_balance) ")
			.append("Select je.office_id, je.account_id, sum(if(je.type_enum=1, (-1) * je.amount, je.amount)) ")
			.append("as Amount, Date(je.entry_date) as 'Entry_Date', je.transaction_date as 'Created_Date',sum(je.amount) as closing_balance ")
			.append("from acc_gl_journal_entry je WHERE je.transaction_date = ? ")
			.append("group by je.account_id, je.office_id, je.transaction_date, Date(je.entry_date)");

			final int result = jdbcTemplate.update(sqlBuilder.toString(), formattedDate);
			LOG.info("{}: Records affected by updateTrialBalanceDetails: {}", ThreadLocalContextUtil.getTenant().getName(), result);
		}

		// Updating closing balance
		String distinctOfficeQuery = "select distinct(office_id) from m_trial_balance where closing_balance is null group by office_id";
		final List<Long> officeIds = jdbcTemplate.queryForList(distinctOfficeQuery, new Object[] {}, Long.class);

		for (Long officeId : officeIds) {
			String distinctAccountQuery = "select distinct(account_id) from m_trial_balance where office_id=? and closing_balance is null group by account_id";
			final List<Long> accountIds = jdbcTemplate.queryForList(distinctAccountQuery, new Object[] { officeId }, Long.class);
			for (Long accountId : accountIds) {
				final String closingBalanceQuery = "select closing_balance from m_trial_balance where office_id=? and account_id=? and closing_balance "
						+ "is not null order by created_date desc, entry_date desc limit 1";
				List<BigDecimal> closingBalanceData = jdbcTemplate.queryForList(closingBalanceQuery, new Object[] { officeId, accountId },
						BigDecimal.class);
				List<TrialBalance> tbRows = this.trialBalanceRepositoryWrapper.findNewByOfficeAndAccount(officeId, accountId);
				BigDecimal closingBalance = null;
				if (!CollectionUtils.isEmpty(closingBalanceData)) {
					closingBalance = closingBalanceData.get(0);
				}
				if (CollectionUtils.isEmpty(closingBalanceData)) {
					closingBalance = BigDecimal.ZERO;
					for (TrialBalance row : tbRows) {
						closingBalance = closingBalance.add(row.getAmount());
						row.setClosingBalance(closingBalance);
					}
				} else {
					for (TrialBalance tbRow : tbRows) {
						closingBalance = closingBalance.add(tbRow.getAmount());
						tbRow.setClosingBalance(closingBalance);
					}
				}
				this.trialBalanceRepositoryWrapper.save(tbRows);
			}
		}

	}

	@Override
	@Transactional
	@CronTarget(jobName = JobName.DAILY_DEMAND_UPDATE)
	public void dailyDemandUpdate() throws JobExecutionException 
	{
		ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
		Long chitChargeID = chitchargeIds.getId();

		////System.out.println("executed ");
		List<Throwable> exceptions = new ArrayList<>();
		try
		{
			//retrieving all Active Chits
			Collection<ChitGroupData> ActiveChits = chitGroupReadPlatformService.retrieveAllActiveChitGroups();

			int sizeofChit = ActiveChits.size();

			if(sizeofChit!=0)
			{
				List<ChitGroupData> ListOfActiveChits = new ArrayList<ChitGroupData>(ActiveChits);

				for(int i = 0 ; i < ListOfActiveChits.size() ; i ++)
				{
					Object chitData = ListOfActiveChits.get(i);

					if(chitData instanceof ChitGroupData)
					{
						Long ChitId = ((ChitGroupData) chitData).getId();

						//getting all the subscribers
						Collection<ChitGroupSubscriberData> chitSubscribers = chitGroupReadPlatformService.getChitSubscribers(ChitId);

						int sizeofchitSubscribers = chitSubscribers.size();

						if(sizeofchitSubscribers!=0)
						{
							List<ChitGroupSubscriberData> ListofchitSubsribers = new ArrayList<ChitGroupSubscriberData>(chitSubscribers);

							for(int j = 0 ; j < ListofchitSubsribers.size() ; j++)
							{
								Object subscriberData = ListofchitSubsribers.get(j);

								if(subscriberData instanceof ChitGroupSubscriberData)
								{
									if(((ChitGroupSubscriberData) subscriberData).getChitNumber()!=1)
									{
										Long ChitSubscriberId = ((ChitGroupSubscriberData) subscriberData).getId();

										Long cycleNumber = ((ChitGroupData) chitData).getCurrentcycle();

										ChitCycleData datafromchitcycle = chitCycleReadPlatformService.retrieveAll(ChitId, cycleNumber);

										Long cycleId = datafromchitcycle.getId();

										////System.out.println("cycleId "+cycleId);
										////System.out.println("ChitSubscriberId "+ChitSubscriberId);
										////System.out.println("cycleId "+cycleId);

										ChitSubscriberChargeData chitsubsID = this.chitSubscriberChargeReadPlatformServices.retrieveById(ChitSubscriberId, chitChargeID, cycleId);


										DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

										LocalDate date = LocalDate.now(ZoneId.systemDefault());
										LocalDate demandDate = LocalDate.parse(date.toString(), formatter);		

										ChitDemandScheduleData todaysdemandData = chitDemandScheduleReadPlatformService.getunproccesedDemands(chitsubsID.getId(), false, demandDate);

										if(todaysdemandData!=null)
										{
											Boolean lastDayofCycle = false;
											LocalDate todaysDate = todaysdemandData.getDemandDate();
											LocalDate nextDay = findWorkingDays.nextWorkingDay(todaysDate);
											nextDay = findWorkingDays.validateworkingDayorNot(nextDay);
											if(todaysDate.equals(nextDay))
											{
												lastDayofCycle = true;
											}

											if(!lastDayofCycle)
											{
												LocalDate tommorowsDemandDate = findWorkingDays.nextWorkingDay(demandDate);
												tommorowsDemandDate = findWorkingDays.validateworkingDayorNot(date);
												ChitDemandScheduleData tomdemandData = chitDemandScheduleReadPlatformService.getunproccesedDemands(chitsubsID.getId(), false, tommorowsDemandDate);

												Double todaysoverdue = todaysdemandData.getOverdueAmount();
												Double todaysdue = todaysdemandData.getDueAmount();
												Double todaysPenalty = todaysdemandData.getPenaltyAmount();
												Double todaysInstallment = todaysdemandData.getInstallmentAmount();

												Double tommorowsOverDue = todaysoverdue;
												Double tommorowsDue = todaysInstallment + todaysdue ;
												Double tommorowsPenalty = todaysPenalty;
												Long tommorowsDemandId = tomdemandData.getId();

												JsonObject todaysDataforUpdate = new JsonObject();
												todaysDataforUpdate.addProperty("isCalculated", true);

												Long todaysDemandId = todaysdemandData.getId();

												chitDemandScheduleWritePlatformService.updateChitDemandSchedule(todaysDemandId, todaysDataforUpdate);

												JsonObject tommorrowsDataforUpdate = new JsonObject();

												tommorrowsDataforUpdate.addProperty("dueAmount", tommorowsDue);
												tommorrowsDataforUpdate.addProperty("overdueAmount", tommorowsOverDue);
												tommorrowsDataforUpdate.addProperty("penaltyAmount", tommorowsPenalty);

												chitDemandScheduleWritePlatformService.updateChitDemandSchedule(tommorowsDemandId, tommorrowsDataforUpdate);

											}

											if(lastDayofCycle)
											{
												Long ChitDuration = ((ChitGroupData) chitData).getChitduration();
												Long currentCycle = datafromchitcycle.getCycleNumber();

												if(currentCycle<ChitDuration)
												{

													Double todaysoverdue = todaysdemandData.getOverdueAmount();
													Double todaysdue = todaysdemandData.getDueAmount();
													Double todaysPenalty = todaysdemandData.getPenaltyAmount();
													Double todaysInstallment = todaysdemandData.getInstallmentAmount();
													Long cyclecount = datafromchitcycle.getCycleNumber();


													Long MonthlyContribution  = ((ChitGroupData) chitData).getMonthlycontribution();
													Long staffId  = todaysdemandData.getStaffId();

													cyclecount = cyclecount+1;

													ChitCycleData chitcycleData = chitCycleReadPlatformService.retrieveAll(ChitId, cyclecount);

													Long cycleId1 = chitcycleData.getId();

													LocalDate auctiondates = chitcycleData.getAuctiondate().withDayOfMonth(1);

													LocalDate auctiondate = this.findWorkingDays.nextWorkingDay(auctiondates);
													LocalDate newDate = this.findWorkingDays.validateworkingDayorNot(auctiondate);

													if(todaysdue>=0.0)
													{
														Double penaltypercentage  = configurationReadPlatformService.retrieveGlobalConfiguration("penalty").getValue().doubleValue();
														Double  penaltyAmount = penaltypercentage/100l;

														Double OverDuetobeUpdated = todaysInstallment+todaysoverdue+todaysPenalty+todaysdue;
														Double penalty = 0.0;
														Boolean prizedSubscriber = false;
														if(chitsubsID.getChitSubscriberId()!=null)
														{
															ChitGroupSubscriberData chitsubscriberData = this.chitGroupReadPlatformService.getChitSubscriber(chitsubsID.getChitSubscriberId());
															prizedSubscriber = chitsubscriberData.getPrizedsubscriber();
														}

														if(prizedSubscriber)
														{
															penalty = OverDuetobeUpdated * penaltyAmount;
														}



														JsonObject dataToCreateChitSubsCharges = new JsonObject();
														//creating data for chitsubscribercharge

														dataToCreateChitSubsCharges.addProperty("subscriberId", ChitSubscriberId);
														dataToCreateChitSubsCharges.addProperty("chitCycleId", cycleId1);
														dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
														dataToCreateChitSubsCharges.addProperty("amount", MonthlyContribution);	
														CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);

														Long tempChitSubscriberChargeId = Ids.commandId();
														JsonObject dataToCreate = new JsonObject();
														dataToCreate.addProperty("demandDate", newDate.toString());
														dataToCreate.addProperty("dueAmount", 0.0);
														dataToCreate.addProperty("installmentAmount", 0.0);
														dataToCreate.addProperty("overdueAmount", OverDuetobeUpdated);
														dataToCreate.addProperty("penaltyAmount", penalty);
														dataToCreate.addProperty("staffId", staffId);
														dataToCreate.addProperty("collectedAmount", 0.0);
														dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
														this.chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataToCreate);		
													}	
													if(todaysdue<0.0)
													{
														JsonObject dataToCreateChitSubsCharges = new JsonObject();
														//creating data for chitsubscribercharge
														todaysdue = todaysdue * -1;
														dataToCreateChitSubsCharges.addProperty("subscriberId", ChitSubscriberId);
														dataToCreateChitSubsCharges.addProperty("chitCycleId", cycleId1);
														dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
														dataToCreateChitSubsCharges.addProperty("amount", MonthlyContribution);	
														CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);

														Long tempChitSubscriberChargeId = Ids.commandId();
														JsonObject dataToCreate = new JsonObject();
														dataToCreate.addProperty("demandDate", newDate.toString());
														dataToCreate.addProperty("dueAmount", 0.0);
														dataToCreate.addProperty("installmentAmount", todaysdue);
														dataToCreate.addProperty("overdueAmount", 0.0);
														dataToCreate.addProperty("penaltyAmount", 0.0);
														dataToCreate.addProperty("staffId", staffId);
														dataToCreate.addProperty("collectedAmount", todaysdue);
														dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
														dataToCreate.addProperty("isCalculated", true);
														this.chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataToCreate);
													}
												}
											}	
										}
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e)
		{
			exceptions.add(e);
			e.printStackTrace();
			throw new JobExecutionException(exceptions);
		}

	}


	@Override
	@Transactional
	//	@CronTarget(jobName = JobName.DAILY_DEMAND_UPDATE)
	public void dailyDemandUpdate(LocalDate date) throws JobExecutionException 
	{
		ChitChargeData chitchargeIds = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT");
		Long chitChargeID = chitchargeIds.getId();

		List<Throwable> exceptions = new ArrayList<>();
		try
		{
			//retrieving all Active Chits
			Collection<ChitGroupData> ActiveChits = chitGroupReadPlatformService.retrieveAllActiveChitGroups();

			int sizeofChit = ActiveChits.size();

			if(sizeofChit!=0)
			{
				List<ChitGroupData> ListOfActiveChits = new ArrayList<ChitGroupData>(ActiveChits);

				for(int i = 0 ; i < ListOfActiveChits.size() ; i ++)
				{
					Object chitData = ListOfActiveChits.get(i);

					if(chitData instanceof ChitGroupData)
					{
						Long ChitId = ((ChitGroupData) chitData).getId();

						//getting all the subscribers
						Collection<ChitGroupSubscriberData> chitSubscribers = chitGroupReadPlatformService.getChitSubscribers(ChitId);

						int sizeofchitSubscribers = chitSubscribers.size();

						if(sizeofchitSubscribers!=0)
						{
							List<ChitGroupSubscriberData> ListofchitSubsribers = new ArrayList<ChitGroupSubscriberData>(chitSubscribers);

							for(int j = 0 ; j < ListofchitSubsribers.size() ; j++)
							{
								Object subscriberData = ListofchitSubsribers.get(j);

								if(subscriberData instanceof ChitGroupSubscriberData)
								{
									if(((ChitGroupSubscriberData) subscriberData).getChitNumber()!=1)
									{
										Long ChitSubscriberId = ((ChitGroupSubscriberData) subscriberData).getId();

										Long cycleNumber = ((ChitGroupData) chitData).getCurrentcycle();

										ChitCycleData datafromchitcycle = chitCycleReadPlatformService.retrieveAll(ChitId, cycleNumber);

										Long cycleId = datafromchitcycle.getId();

										////System.out.println("cycleId "+cycleId);
										////System.out.println("ChitSubscriberId "+ChitSubscriberId);
										////System.out.println("cycleId "+cycleId);

										ChitSubscriberChargeData chitsubsID = this.chitSubscriberChargeReadPlatformServices.retrieveById(ChitSubscriberId, chitChargeID, cycleId);


										DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

										//	LocalDate date = LocalDate.now(ZoneId.systemDefault());
										LocalDate demandDate = LocalDate.parse(date.toString(), formatter);		

										ChitDemandScheduleData todaysdemandData = chitDemandScheduleReadPlatformService.getunproccesedDemands(chitsubsID.getId(), false, demandDate);

										if(todaysdemandData!=null)
										{
											Boolean lastDayofCycle = false;
											LocalDate todaysDate = todaysdemandData.getDemandDate();
											LocalDate nextDay = findWorkingDays.nextWorkingDay(todaysDate);
											if(todaysDate.equals(nextDay))
											{
												lastDayofCycle = true;
											}

											if(!lastDayofCycle)
											{
												LocalDate tommorowsDemandDate = findWorkingDays.nextWorkingDay(demandDate);

												ChitDemandScheduleData tomdemandData = chitDemandScheduleReadPlatformService.getunproccesedDemands(chitsubsID.getId(), false, tommorowsDemandDate);

												if(tomdemandData!=null)
												{
													Double todaysoverdue = todaysdemandData.getOverdueAmount();
													Double todaysdue = todaysdemandData.getDueAmount();
													Double todaysPenalty = todaysdemandData.getPenaltyAmount();
													Double todaysInstallment = todaysdemandData.getInstallmentAmount();

													Double tommorowsOverDue = todaysoverdue;
													Double tommorowsDue = todaysInstallment + todaysdue ;
													Double tommorowsPenalty = todaysPenalty;
													Long tommorowsDemandId = tomdemandData.getId();

													JsonObject todaysDataforUpdate = new JsonObject();
													todaysDataforUpdate.addProperty("isCalculated", true);

													Long todaysDemandId = todaysdemandData.getId();

													chitDemandScheduleWritePlatformService.updateChitDemandSchedule(todaysDemandId, todaysDataforUpdate);

													JsonObject tommorrowsDataforUpdate = new JsonObject();

													tommorrowsDataforUpdate.addProperty("dueAmount", tommorowsDue);
													tommorrowsDataforUpdate.addProperty("overdueAmount", tommorowsOverDue);
													tommorrowsDataforUpdate.addProperty("penaltyAmount", tommorowsPenalty);

													chitDemandScheduleWritePlatformService.updateChitDemandSchedule(tommorowsDemandId, tommorrowsDataforUpdate);
												}
											}

											if(lastDayofCycle)
											{
												Long ChitDuration = ((ChitGroupData) chitData).getChitduration();
												Long currentCycle = datafromchitcycle.getCycleNumber();

												if(currentCycle<ChitDuration)
												{

													Double todaysoverdue = todaysdemandData.getOverdueAmount();
													Double todaysdue = todaysdemandData.getDueAmount();
													Double todaysPenalty = todaysdemandData.getPenaltyAmount();
													Double todaysInstallment = todaysdemandData.getInstallmentAmount();
													Long cyclecount = datafromchitcycle.getCycleNumber();


													Long MonthlyContribution  = ((ChitGroupData) chitData).getMonthlycontribution();
													Long staffId  = todaysdemandData.getStaffId();

													cyclecount = cyclecount+1;

													ChitCycleData chitcycleData = chitCycleReadPlatformService.retrieveAll(ChitId, cyclecount);

													Long cycleId1 = chitcycleData.getId();
													LocalDate auctiondates = chitcycleData.getAuctiondate().withDayOfMonth(1);

													LocalDate auctiondate = this.findWorkingDays.nextWorkingDay(auctiondates);
													LocalDate newDate = this.findWorkingDays.validateworkingDayorNot(auctiondate);

													if(todaysdue>=0.0)
													{

														Double penaltypercentage  = configurationReadPlatformService.retrieveGlobalConfiguration("penalty").getValue().doubleValue();
														Double  penaltyAmount = penaltypercentage/100l;

														Double OverDuetobeUpdated = todaysInstallment+todaysoverdue+todaysPenalty+todaysdue;
														Double penalty = 0.0;
														Boolean prizedSubscriber = false;
														if(chitsubsID.getChitSubscriberId()!=null)
														{
															ChitGroupSubscriberData chitsubscriberData = this.chitGroupReadPlatformService.getChitSubscriber(chitsubsID.getChitSubscriberId());
															prizedSubscriber = chitsubscriberData.getPrizedsubscriber();
														}

														if(prizedSubscriber)
														{
															penalty = OverDuetobeUpdated * penaltyAmount;
														}

														JsonObject dataToCreateChitSubsCharges = new JsonObject();
														//creating data for chitsubscribercharge

														dataToCreateChitSubsCharges.addProperty("subscriberId", ChitSubscriberId);
														dataToCreateChitSubsCharges.addProperty("chitCycleId", cycleId1);
														dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
														dataToCreateChitSubsCharges.addProperty("amount", MonthlyContribution);	
														CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);

														Long tempChitSubscriberChargeId = Ids.commandId();
														JsonObject dataToCreate = new JsonObject();
														dataToCreate.addProperty("demandDate", newDate.toString());
														dataToCreate.addProperty("dueAmount", 0.0);
														dataToCreate.addProperty("installmentAmount", 0.0);
														dataToCreate.addProperty("overdueAmount", OverDuetobeUpdated);
														dataToCreate.addProperty("penaltyAmount", penalty);
														dataToCreate.addProperty("staffId", staffId);
														dataToCreate.addProperty("collectedAmount", 0.0);
														dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
														this.chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataToCreate);		
													}	
													if(todaysdue<0.0)
													{
														JsonObject dataToCreateChitSubsCharges = new JsonObject();
														//creating data for chitsubscribercharge
														todaysdue = todaysdue * -1;
														dataToCreateChitSubsCharges.addProperty("subscriberId", ChitSubscriberId);
														dataToCreateChitSubsCharges.addProperty("chitCycleId", cycleId1);
														dataToCreateChitSubsCharges.addProperty("chitChargeId", chitchargeIds.getId());
														dataToCreateChitSubsCharges.addProperty("amount", MonthlyContribution);	
														CommandProcessingResult Ids = chitSubscriberChargeWritePlatformService.createSubscriberChitCharge(dataToCreateChitSubsCharges);

														Long tempChitSubscriberChargeId = Ids.commandId();
														JsonObject dataToCreate = new JsonObject();
														dataToCreate.addProperty("demandDate", newDate.toString());
														dataToCreate.addProperty("dueAmount", 0.0);
														dataToCreate.addProperty("installmentAmount", todaysdue);
														dataToCreate.addProperty("overdueAmount", 0.0);
														dataToCreate.addProperty("penaltyAmount", 0.0);
														dataToCreate.addProperty("staffId", staffId);
														dataToCreate.addProperty("collectedAmount", todaysdue);
														dataToCreate.addProperty("chitsubscriberchargeId", tempChitSubscriberChargeId);
														dataToCreate.addProperty("isCalculated", true);
														this.chitDemandScheduleWritePlatformService.createChitDemandSchedule(dataToCreate);
													}
												}
											}	
										}
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e)
		{
			exceptions.add(e);
			e.printStackTrace();
			throw new JobExecutionException(exceptions);
		}

	}



}
