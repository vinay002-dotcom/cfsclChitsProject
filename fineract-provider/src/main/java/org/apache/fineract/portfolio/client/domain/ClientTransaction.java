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

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OrganisationCurrency;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.useradministration.domain.AppUser;

import com.google.gson.JsonObject;

@Entity
@Table(name = "m_client_transaction", uniqueConstraints = { @UniqueConstraint(columnNames = { "external_id" }, name = "external_id") })
public class ClientTransaction extends AbstractPersistableCustom {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @ManyToOne(optional = true)
    @JoinColumn(name = "payment_detail_id", nullable = true)
    private PaymentDetail paymentDetail;
    
    @JoinColumn(name = "payment_detail_id", nullable = true)
    private Long paymentId;
    
    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "transaction_type_enum", nullable = false)
    private Integer typeOf;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date", nullable = false)
    private Date dateOf;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "appuser_id", nullable = true)
    private AppUser appUser;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clientTransaction", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ClientChargePaidBy> clientChargePaidByCollection = new HashSet<>();
    
    @Column(name = "adjusted")
    private Boolean adjusted;
    
    @Column(name = "is_processed")
    private Boolean isprocessed;

    @Transient
    private OrganisationCurrency currency;


    public ClientTransaction(Client client, Office office, PaymentDetail paymentDetail, String currencyCode,
			Integer typeOf, Date dateOf, BigDecimal amount, Boolean reversed, String externalId, Date createdDate,
			AppUser appUser, Set<ClientChargePaidBy> clientChargePaidByCollection, Boolean adjusted,
			OrganisationCurrency currency,Long paymentId,Boolean isprocessed) {
		this.client = client;
		this.office = office;
		this.paymentDetail = paymentDetail;
		this.currencyCode = currencyCode;
		this.typeOf = typeOf;
		this.dateOf = dateOf;
		this.amount = amount;
		this.reversed = reversed;
		this.externalId = externalId;
		this.createdDate = createdDate;
		this.appUser = appUser;
		this.clientChargePaidByCollection = clientChargePaidByCollection;
		this.adjusted = adjusted;
		this.currency = currency;
		this.paymentId = paymentId;
		this.isprocessed = isprocessed;
	}

	public static ClientTransaction payCharge(final Client client, final Office office, PaymentDetail paymentDetail, final LocalDate date,
            final Money amount, final String currencyCode, final AppUser appUser) {
        final boolean isReversed = false;
        final String externalId = null;
        final Boolean adjusted = false;
        final Boolean isprocessed = false;
        return new ClientTransaction(client, office, paymentDetail, ClientTransactionType.PAY_CHARGE.getValue(), date, amount, isReversed,
                externalId, DateUtils.getDateOfTenant(), currencyCode, appUser,adjusted,null,isprocessed);
    }

    public static ClientTransaction waiver(final Client client, final Office office, final LocalDate date, final Money amount,
            final String currencyCode, final AppUser appUser) {
        final boolean isReversed = false;
        final String externalId = null;
        final Boolean adjusted = false;
        final PaymentDetail paymentDetail = null;
        final Boolean isprocessed = false;
        return new ClientTransaction(client, office, paymentDetail, ClientTransactionType.WAIVE_CHARGE.getValue(), date, amount, isReversed,
                externalId, DateUtils.getDateOfTenant(), currencyCode, appUser,adjusted,null,isprocessed);
    }

    public static ClientTransaction payChitAdvance(final Client client, final Office office, PaymentDetail paymentDetail, final LocalDate date,
            final Money amount, final String currencyCode, final AppUser appUser,final Long paymentId) {
        final boolean isReversed = false;
        final Boolean adjusted = false;
        final String externalId = null;
        final Boolean isprocessed = false;
        return new ClientTransaction(client, office, paymentDetail, ClientTransactionType.CHIT_ADVANCE.getValue(), date, amount, isReversed,
                externalId, DateUtils.getDateOfTenant(), currencyCode, appUser,adjusted,paymentId,isprocessed);
    }

    public ClientTransaction(Client client, Office office, PaymentDetail paymentDetail, Integer typeOf, LocalDate transactionLocalDate,
            Money amount, boolean reversed, String externalId, Date createdDate, String currencyCode, AppUser appUser,Boolean adjusted,final Long paymentId,Boolean isprocessed) {

        this.client = client;
        this.office = office;
        this.paymentDetail = paymentDetail;
        this.typeOf = typeOf;
        this.dateOf = Date.from(transactionLocalDate.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        this.amount = amount.getAmount();
        this.reversed = reversed;
        this.externalId = externalId;
        this.createdDate = createdDate;
        this.currencyCode = currencyCode;
        this.appUser = appUser;
        this.adjusted = adjusted;
        this.isprocessed = isprocessed;
    }

    public void reverse() {
        this.reversed = true;
    }

    /**
     * Converts the content of this Client Transaction to a map which can be passed to the accounting module
     *
     *
     *
     */
    public Map<String, Object> toMapData() {
        final Map<String, Object> thisTransactionData = new LinkedHashMap<>();

        final EnumOptionData transactionType = ClientEnumerations.clientTransactionType(this.typeOf);
        Boolean accountingEnabledForAtleastOneCharge = false;

        thisTransactionData.put("id", getId());
        thisTransactionData.put("clientId", getClientId());
        thisTransactionData.put("officeId", this.office.getId());
        thisTransactionData.put("type", transactionType);
        thisTransactionData.put("reversed", Boolean.valueOf(this.reversed));
        thisTransactionData.put("date", getTransactionDate());
        thisTransactionData.put("currencyCode", this.currencyCode);
        thisTransactionData.put("amount", this.amount);
        thisTransactionData.put("adjusted", Boolean.valueOf(this.adjusted));

        if (this.paymentDetail != null) {
            thisTransactionData.put("paymentTypeId", this.paymentDetail.getPaymentType().getId());
        }

        if (!this.clientChargePaidByCollection.isEmpty()) {
            final List<Map<String, Object>> clientChargesPaidData = new ArrayList<>();
            for (final ClientChargePaidBy clientChargePaidBy : this.clientChargePaidByCollection) {
                final Map<String, Object> clientChargePaidData = new LinkedHashMap<>();
                clientChargePaidData.put("chargeId", clientChargePaidBy.getClientCharge().getCharge().getId());
                clientChargePaidData.put("isPenalty", clientChargePaidBy.getClientCharge().getCharge().isPenalty());
                clientChargePaidData.put("clientChargeId", clientChargePaidBy.getClientCharge().getId());
                clientChargePaidData.put("amount", clientChargePaidBy.getAmount());
                GLAccount glAccount = clientChargePaidBy.getClientCharge().getCharge().getAccount();
                if (glAccount != null) {
                    accountingEnabledForAtleastOneCharge = true;
                    clientChargePaidData.put("incomeAccountId", glAccount.getId());
                }
                clientChargesPaidData.add(clientChargePaidData);
            }
            thisTransactionData.put("clientChargesPaid", clientChargesPaidData);
        }

        thisTransactionData.put("accountingEnabled", accountingEnabledForAtleastOneCharge);

        return thisTransactionData;
    }
    
    public Map<String, Object> update(final JsonObject command)
	{
		final Map<String, Object> actualChanges = new LinkedHashMap<>(9);
		
		final String adjustedParamName = "adjusted";
		if (command.get(adjustedParamName)!=null && !command.get(adjustedParamName).isJsonNull()) 
		{

			final Boolean newValue = command.get(adjustedParamName).getAsBoolean();
			actualChanges.put(adjustedParamName, newValue);
			this.adjusted = newValue;
		}
		
		final String isprocessedParamName = "isprocessed";
		if (command.get(isprocessedParamName)!=null && !command.get(isprocessedParamName).isJsonNull()) 
		{

			final Boolean newValue = command.get(isprocessedParamName).getAsBoolean();
			actualChanges.put(isprocessedParamName, newValue);
			this.isprocessed = newValue;
		}
		

		final String isreversedParamName = "isreversed";
		if (command.get(isreversedParamName)!=null && !command.get(isreversedParamName).isJsonNull()) 
		{

			final Boolean newValue = command.get(isreversedParamName).getAsBoolean();
			actualChanges.put(isreversedParamName, newValue);
			this.reversed = newValue;
		}
		
		final String amountParamName = "amount";
		if (command.get(amountParamName)!=null && !command.get(amountParamName).isJsonNull()) 
		{

			final BigDecimal newValue = command.get(amountParamName).getAsBigDecimal();
			actualChanges.put(amountParamName, newValue);
			this.amount = newValue;
		}
		return actualChanges;
	}

    public boolean isPayChargeTransaction() {
        return ClientTransactionType.PAY_CHARGE.getValue().equals(this.typeOf);
    }

    public boolean isWaiveChargeTransaction() {
        return ClientTransactionType.WAIVE_CHARGE.getValue().equals(this.typeOf);
    }

    public Set<ClientChargePaidBy> getClientChargePaidByCollection() {
        return this.clientChargePaidByCollection;
    }

    public Long getClientId() {
        return client.getId();
    }

    public Client getClient() {
        return this.client;
    }

    public Money getAmount() {
        return Money.of(getCurrency(), this.amount);
    }

    public MonetaryCurrency getCurrency() {
        return this.currency.toMonetaryCurrency();
    }

    public void setCurrency(OrganisationCurrency currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public PaymentDetail getPaymentDetail() {
		return paymentDetail;
	}

	public void setPaymentDetail(PaymentDetail paymentDetail) {
		this.paymentDetail = paymentDetail;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public Integer getTypeOf() {
		return typeOf;
	}

	public void setTypeOf(Integer typeOf) {
		this.typeOf = typeOf;
	}

	public Date getDateOf() {
		return dateOf;
	}

	public void setDateOf(Date dateOf) {
		this.dateOf = dateOf;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public Boolean getAdjusted() {
		return adjusted;
	}

	public void setAdjusted(Boolean adjusted) {
		this.adjusted = adjusted;
	}

	public Boolean getIsprocessed() {
		return isprocessed;
	}

	public void setIsprocessed(Boolean isprocessed) {
		this.isprocessed = isprocessed;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}

	public void setClientChargePaidByCollection(Set<ClientChargePaidBy> clientChargePaidByCollection) {
		this.clientChargePaidByCollection = clientChargePaidByCollection;
	}

	public LocalDate getTransactionDate() {
        return LocalDate.ofInstant(this.dateOf.toInstant(), DateUtils.getDateTimeZoneOfTenant());
    }

}
