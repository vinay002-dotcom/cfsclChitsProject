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
package org.apache.fineract.portfolio.savings.domain;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.staff.domain.Staff;

@Entity
@Table(name = "m_savings_officer_assignment_history")
public class SavingsOfficerAssignmentHistory extends AbstractAuditableCustom {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private SavingsAccount savingsAccount;

    @ManyToOne
    @JoinColumn(name = "savings_officer_id", nullable = true)
    private Staff savingsOfficer;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    public static SavingsOfficerAssignmentHistory createNew(final SavingsAccount account, final Staff savingsOfficer,
            final LocalDate assignmentDate) {
        return new SavingsOfficerAssignmentHistory(account, savingsOfficer,
                Date.from(assignmentDate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant()), null);
    }

    protected SavingsOfficerAssignmentHistory() {
        //
    }

    private SavingsOfficerAssignmentHistory(final SavingsAccount account, final Staff savingsOfficer, final Date startDate,
            final Date endDate) {
        this.savingsAccount = account;
        this.savingsOfficer = savingsOfficer;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateSavingsOfficer(final Staff savingsOfficer) {
        this.savingsOfficer = savingsOfficer;
    }

    public boolean isSameSavingsOfficer(final Staff staff) {
        return this.savingsOfficer.identifiedBy(staff);
    }

    public void updateStartDate(final LocalDate startDate) {
        this.startDate = Date.from(startDate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
    }

    public void updateEndDate(final LocalDate endDate) {
        this.endDate = Date.from(endDate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
    }

    public boolean matchesStartDateOf(final LocalDate matchingDate) {
        return getStartDate().isEqual(matchingDate);
    }

    public LocalDate getStartDate() {
        return LocalDate.ofInstant(this.startDate.toInstant(), DateUtils.getDateTimeZoneOfTenant());
    }

    public boolean hasStartDateBefore(final LocalDate matchingDate) {
        return matchingDate.isBefore(getStartDate());
    }

    public boolean isCurrentRecord() {
        return this.endDate == null;
    }

    /**
     * If endDate is null then return false.
     *
     * @param compareDate
     * @return
     */
    public boolean isEndDateAfter(final LocalDate compareDate) {
        return this.endDate == null ? false
                : LocalDate.ofInstant(this.endDate.toInstant(), DateUtils.getDateTimeZoneOfTenant()).isAfter(compareDate);
    }

    public LocalDate getEndDate() {
        return ObjectUtils.defaultIfNull(LocalDate.ofInstant(this.endDate.toInstant(), DateUtils.getDateTimeZoneOfTenant()), null);
    }

}
