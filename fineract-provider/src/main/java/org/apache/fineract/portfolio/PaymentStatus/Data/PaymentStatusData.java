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
package org.apache.fineract.portfolio.PaymentStatus.Data;

import java.io.Serializable;
import java.time.LocalDate;

public class PaymentStatusData implements Serializable
{
	private final Long id;
	private final Long paymentType;
	private final Long status;
	private final LocalDate date;
	private final Long tansactionId;
	private final Long officeId;
	
	public PaymentStatusData(Long id, Long paymentType, Long status, LocalDate date, Long tansactionId,Long officeId) {
		super();
		this.id = id;
		this.paymentType = paymentType;
		this.status = status;
		this.date = date;
		this.officeId = officeId;
		this.tansactionId = tansactionId;
	}
	
	
	public static PaymentStatusData instance(Long id, Long paymentType, Long status, LocalDate date, Long tansactionId,Long officeId)
	{
		return new PaymentStatusData(id,paymentType,status,date,tansactionId,officeId);
	}


	public Long getId() {
		return id;
	}


	public Long getPaymentType() {
		return paymentType;
	}


	public Long getStatus() {
		return status;
	}


	public LocalDate getDate() {
		return date;
	}


	public Long getTansactionId() {
		return tansactionId;
	}
	
	
	
}
