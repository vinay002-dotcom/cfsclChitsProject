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
package org.apache.fineract.portfolio.ChitGroup.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;


@SuppressWarnings("unused")
public class ChitBidsWinnerData implements Serializable
{
	private final Long id;
	private final Long chitId;
	private final Long chitCycleId;
	private final Long clientId;
	private final Integer chitNumber;
	private final String name;
	private final String adhar;
	private final String mobileno;
	private final BigDecimal bidAmount;
	private final LocalDate	bidDate;
	
		
	public static ChitBidsWinnerData lookup(final Long id) {
		return new ChitBidsWinnerData(id, null, null, null, null, null, null, null, null,null);
	}
	
	private ChitBidsWinnerData(Long id, Long chitId, Long chitCycleId, Long clientId, Integer chitNumber, String name, String adhar, String mobileno,BigDecimal bidAmount,
			LocalDate bidDate) {
		this.id = id;
		this.chitId = chitId;
		this.chitCycleId = chitCycleId;
		this.clientId = clientId;
		this.chitNumber = chitNumber;
		this.name = name;
		this.adhar = adhar;
		this.mobileno = mobileno;
		this.bidAmount = bidAmount;
		this.bidDate = bidDate;
	}

	public Long getId() {
		return id;
	}
	
	public static ChitBidsWinnerData instance(Long id, Long chitId, Long chitCycleId, Long clientId, Integer chitNumber, String name, String adhar, String mobileno,BigDecimal bidAmount,LocalDate bidDate){
		return new ChitBidsWinnerData(id, chitId, chitCycleId, clientId, chitNumber, name, adhar, mobileno,bidAmount,bidDate);
	}
	
	public Long getClientId() {
		return clientId;
	}

	public Long getChitId() {
		return chitId;
	}

	public Integer getChitNumber() {
		return chitNumber;
	}

	public String getName() {
		return name;
	}

	public String getAdhar() {
		return adhar;
	}

	public String getMobileno() {
		return mobileno;
	}

	public Long getChitCycleId() {
		return chitCycleId;
	}

	public BigDecimal getBidAmount() {
		return bidAmount;
	}

	public LocalDate getBidDate() {
		return bidDate;
	}

}
