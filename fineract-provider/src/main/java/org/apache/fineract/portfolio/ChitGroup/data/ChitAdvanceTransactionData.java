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

public class ChitAdvanceTransactionData implements Serializable
{
	Long id;
	String firstName;
	String Adhar;
	String mobileNo;
	Double AdvanceAmountRecieved;
	
	private ChitAdvanceTransactionData(Long id, String firstName, String Adhar, String mobileNo,
			Double AdvanceAmountRecieved) {
	
		this.id = id;
		this.firstName = firstName;
		this.Adhar = Adhar;
		this.mobileNo = mobileNo;
		this.AdvanceAmountRecieved = AdvanceAmountRecieved;
	}
	
	public static ChitAdvanceTransactionData instance(Long id, String firstName, String Adhar, String mobileNo,
			Double AdvanceAmountRecieved)
	{
		return new ChitAdvanceTransactionData(id,firstName,Adhar,mobileNo,AdvanceAmountRecieved);
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getAdhar() {
		return Adhar;
	}

	public void setAdhar(String Adhar) {
		this.Adhar = Adhar;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Double getAdvanceAmountRecieved() {
		return AdvanceAmountRecieved;
	}

	public void setAdvanceAmountRecieved(Double AdvanceAmountRecieved) {
		this.AdvanceAmountRecieved = AdvanceAmountRecieved;
	}
	
	
}
