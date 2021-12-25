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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;

@SuppressWarnings("unused")
public final class ClientTransactionData {

    private  Long id;
    private  Long officeId;
    private  String officeName;
    private  EnumOptionData type;
    private  LocalDate date;
    private  CurrencyData currency;
    private  PaymentDetailData paymentDetailData;
    private  BigDecimal amount;
    private  String externalId;
    private  LocalDate submittedOnDate;
    private  Boolean reversed;
    private ZonedDateTime createddate;
    private Long clientId;
    private Boolean isProcessed;
    
    public Boolean getIsProcessed() {
		return isProcessed;
	}

	public void setIsProcessed(Boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	public Long getClientId() {
		return this.clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public ZonedDateTime getCreateddate() {
		return createddate;
	}

	public void setCreateddate(ZonedDateTime createddate) {
		this.createddate = createddate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public void setType(EnumOptionData type) {
		this.type = type;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setCurrency(CurrencyData currency) {
		this.currency = currency;
	}

	public void setPaymentDetailData(PaymentDetailData paymentDetailData) {
		this.paymentDetailData = paymentDetailData;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setSubmittedOnDate(LocalDate submittedOnDate) {
		this.submittedOnDate = submittedOnDate;
	}

	public void setReversed(Boolean reversed) {
		this.reversed = reversed;
	}

	public Boolean getReversed() {
		return reversed;
	}

	public void setAdjusted(Boolean adjusted) {
		this.adjusted = adjusted;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	private  Boolean adjusted;
    private  Long paymentId;

    // templates
    final Collection<PaymentTypeData> paymentTypeOptions;

    public static ClientTransactionData create(Long id, Long officeId, String officeName, EnumOptionData type, LocalDate date,
            CurrencyData currency, PaymentDetailData paymentDetailData, BigDecimal amount, String externalId, LocalDate submittedOnDate,
            boolean reversed,Boolean adjusted,Long paymentId,ZonedDateTime createddate,Long clientId,Boolean isProccessed) {
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        return new ClientTransactionData(id, officeId, officeName, type, date, currency, paymentDetailData, amount, externalId,
                submittedOnDate, reversed, paymentTypeOptions,adjusted,paymentId,createddate,clientId,isProccessed);
    }

    public Long getPaymentId() {
		return paymentId;
	}

	private ClientTransactionData(Long id, Long officeId, String officeName, EnumOptionData type, LocalDate date, CurrencyData currency,
            PaymentDetailData paymentDetailData, BigDecimal amount, String externalId, LocalDate submittedOnDate, boolean reversed,
            Collection<PaymentTypeData> paymentTypeOptions,Boolean adjusted,final Long paymentId,final ZonedDateTime createddate,Long clientId,Boolean isProcessed) {

        this.id = id;
        this.officeId = officeId;
        this.officeName = officeName;
        this.type = type;
        this.date = date;
        this.currency = currency;
        this.paymentDetailData = paymentDetailData;
        this.amount = amount;
        this.externalId = externalId;
        this.submittedOnDate = submittedOnDate;
        this.reversed = reversed;
        this.paymentTypeOptions = paymentTypeOptions;
        this.adjusted =adjusted;
        this.paymentId = paymentId;
        this.createddate = createddate;
        this.clientId = clientId;
        this.isProcessed = isProcessed;
    }

	public Long getId() {
		return id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public EnumOptionData getType() {
		return type;
	}

	public LocalDate getDate() {
		return date;
	}

	public CurrencyData getCurrency() {
		return currency;
	}

	public PaymentDetailData getPaymentDetailData() {
		return paymentDetailData;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getExternalId() {
		return externalId;
	}

	public LocalDate getSubmittedOnDate() {
		return submittedOnDate;
	}

	public boolean isReversed() {
		return reversed;
	}

	public Boolean getAdjusted() {
		return adjusted;
	}

	public Collection<PaymentTypeData> getPaymentTypeOptions() {
		return paymentTypeOptions;
	}
    

}
