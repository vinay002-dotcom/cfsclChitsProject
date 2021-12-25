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
package org.apache.fineract.portfolio.voucher.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import com.google.gson.JsonObject;

@Entity
@Table(name="m_journal_vouchers")
public class JournlVoucher extends AbstractPersistableCustom {

	@Column(name = "journal_transaction_id")
	private String journalTransactionId;
	
	@Column(name = "voucher_type_id")
	private Integer voucherTypeId;
	
	@Column(name= "voucher_number")
	private  String voucherNumber;
	
	@Column(name = "vendor_name")
	private String vendorName;
	
	protected JournlVoucher() {
		super();
	}



	public JournlVoucher(String journalTransactionId, Integer voucherTypeId, String voucherNumber,	String vendorName) {
		super();
		this.journalTransactionId = journalTransactionId;
		this.voucherTypeId = voucherTypeId;
		this.voucherNumber = voucherNumber;
		this.vendorName = vendorName;
	}
	
	
	public static JournlVoucher create(JsonObject object) {
		String journalTransactionId = null;
		Integer voucherTypeId = null;
		String voucherNumber = null;
		String vendorName = null;
		
		System.out.println("001001");
		if(object.get("journalTransactionId")!=null && !object.get("journalTransactionId").isJsonNull()) {
			journalTransactionId = object.get("journalTransactionId").getAsString();
		}
		
		if(object.get("voucherTypeId")!=null && !object.get("voucherTypeId").isJsonNull()) {
			voucherTypeId = object.get("voucherTypeId").getAsInt();
		}
		
		if(object.get("voucherNumber")!=null && !object.get("voucherNumber").isJsonNull()) {
			voucherNumber = object.get("voucherNumber").getAsString();
		}
		
		if(object.get("vendorName")!=null && !object.get("vendorName").isJsonNull()) {
			vendorName = object.get("vendorName").getAsString();
		}
		System.out.println("001001");
		return new JournlVoucher(journalTransactionId, voucherTypeId, voucherNumber, vendorName);	
	}
	
	public Map<String, Object> update(JsonObject object){
		Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);
		
		
		final String journalTransactionIdParamName = "journalTransactionId";
		if(object.get(journalTransactionIdParamName)!=null && !object.get(journalTransactionIdParamName).isJsonNull()) {
			
			final String newValue = object.get(journalTransactionIdParamName).getAsString();
			actualChanges.put(journalTransactionIdParamName, newValue);
			this.journalTransactionId = newValue;
		}
		
		final String voucherTypeIdParamName = "voucherTypeId";
		if(object.get(voucherTypeIdParamName)!=null && !object.get(voucherTypeIdParamName).isJsonNull()) {
			
			final Integer newValue = object.get(voucherTypeIdParamName).getAsInt();
			actualChanges.put(voucherTypeIdParamName, newValue);
			this.voucherTypeId = newValue;
		}
		
		final String voucherNumberParamName = "voucherNumber";
		if(object.get(voucherNumberParamName)!=null && !object.get(voucherNumberParamName).isJsonNull()) {
			
			final String newValue = object.get(voucherNumberParamName).getAsString();
			actualChanges.put(voucherNumberParamName, newValue);
			this.voucherNumber = newValue;
		}
		
		final String vendorNameParamName = "vendorName";
		if(object.get(vendorNameParamName)!=null && !object.get(vendorNameParamName).isJsonNull()) {
			
			final String newValue = object.get(vendorNameParamName).getAsString();
			actualChanges.put(vendorNameParamName, newValue);
			this.vendorName = newValue;
		}
		
		return actualChanges;
	}


	public String getJournalTransactionId() {
		return journalTransactionId;
	}

	public void setJournalTransactionId(String journalTransactionId) {
		this.journalTransactionId = journalTransactionId;
	}

	public Integer getVoucherTypeId() {
		return voucherTypeId;
	}

	public void setVoucherTypeId(Integer voucherTypeId) {
		this.voucherTypeId = voucherTypeId;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public String getVendorName() {
		return vendorName;
	}
	
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	
}
