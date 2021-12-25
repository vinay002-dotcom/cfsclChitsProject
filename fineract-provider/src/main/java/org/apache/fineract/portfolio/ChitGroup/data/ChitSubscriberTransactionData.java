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

import java.time.LocalDateTime;

import org.apache.fineract.portfolio.ChitGroup.domain.ChitTransactionEnum;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;

public class ChitSubscriberTransactionData implements Serializable 
{
	private Long id;
	private Long chitdemandscheduleId;
	private Long chitsubscriberId;
	private Long chitsubscriberchargeId;
	private Double amount;
	private ChitTransactionEnum trantypeenum;
	private Long trantype;
	private Long paymentdetailId;
	private LocalDateTime transactionDate;
	private Boolean isreversed;
	private Boolean isprocessed;
	private PaymentDetailData paymentDetailData;
	private Long waiveOffAmount; 
	
	public ChitSubscriberTransactionData(Long id, Long chitdemandscheduleId, Long chitsubscriberId,
			Long chitsubscriberchargeId, Double amount, ChitTransactionEnum trantypeenum, Long trantype, Long paymentdetailId,
			LocalDateTime transactionDate, Boolean isreversed, Boolean isprocessed,PaymentDetailData paymentDetailData) {
		this.id = id;
		this.chitdemandscheduleId = chitdemandscheduleId;
		this.chitsubscriberId = chitsubscriberId;
		this.chitsubscriberchargeId = chitsubscriberchargeId;
		this.amount = amount;
		this.trantypeenum = trantypeenum;
		this.trantype = trantype;
		this.paymentdetailId = paymentdetailId;
		this.transactionDate = transactionDate;
		this.isreversed = isreversed;
		this.isprocessed = isprocessed;
		this.paymentDetailData = paymentDetailData;
	}
	public ChitSubscriberTransactionData(Long id, Long chitdemandscheduleId, Long chitsubscriberId,
			Long chitsubscriberchargeId, Double amount, ChitTransactionEnum trantypeenum, Long trantype, Long paymentdetailId,
			LocalDateTime transactionDate, Boolean isreversed, Boolean isprocessed,PaymentDetailData paymentDetailData,Long waiveOffAmount) {
		this.id = id;
		this.chitdemandscheduleId = chitdemandscheduleId;
		this.chitsubscriberId = chitsubscriberId;
		this.chitsubscriberchargeId = chitsubscriberchargeId;
		this.amount = amount;
		this.trantypeenum = trantypeenum;
		this.trantype = trantype;
		this.paymentdetailId = paymentdetailId;
		this.transactionDate = transactionDate;
		this.isreversed = isreversed;
		this.isprocessed = isprocessed;
		this.paymentDetailData = paymentDetailData;
		this.waiveOffAmount = waiveOffAmount;
	}
	
	public static ChitSubscriberTransactionData instance(Long id, Long chitdemandscheduleId, Long chitsubscriberId,
			Long chitsubscriberchargeId, Double amount, ChitTransactionEnum trantypeenum, Long trantype, Long paymentdetailId,
			LocalDateTime transactionDate, Boolean isreversed, Boolean isprocessed,PaymentDetailData paymentDetailData)
	{
		return new ChitSubscriberTransactionData(id,chitdemandscheduleId,chitsubscriberId,chitsubscriberchargeId,amount,trantypeenum,trantype,paymentdetailId,transactionDate,isreversed,isprocessed,paymentDetailData);
	}
	
	public static ChitSubscriberTransactionData newinstance(Long id, Long chitdemandscheduleId, Long chitsubscriberId,
			Long chitsubscriberchargeId, Double amount, ChitTransactionEnum trantypeenum, Long trantype, Long paymentdetailId,
			LocalDateTime transactionDate, Boolean isreversed, Boolean isprocessed,PaymentDetailData paymentDetailData,Long waiveOffAmount)
	{
		return new ChitSubscriberTransactionData(id,chitdemandscheduleId,chitsubscriberId,chitsubscriberchargeId,amount,trantypeenum,trantype,paymentdetailId,transactionDate,isreversed,isprocessed,paymentDetailData, waiveOffAmount);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChitdemandscheduleId() {
		return chitdemandscheduleId;
	}

	public void setChitdemandscheduleId(Long chitdemandscheduleId) {
		this.chitdemandscheduleId = chitdemandscheduleId;
	}

	public Long getChitsubscriberId() {
		return chitsubscriberId;
	}

	public void setChitsubscriberId(Long chitsubscriberId) {
		this.chitsubscriberId = chitsubscriberId;
	}

	public Long getChitsubscriberchargeId() {
		return chitsubscriberchargeId;
	}

	public void setChitsubscriberchargeId(Long chitsubscriberchargeId) {
		this.chitsubscriberchargeId = chitsubscriberchargeId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public ChitTransactionEnum getTrantypeenum() {
		return trantypeenum;
	}

	public void setTrantypeenum(ChitTransactionEnum trantypeenum) {
		this.trantypeenum = trantypeenum;
	}

	public Long getTrantype() {
		return trantype;
	}

	public void setTrantype(Long trantype) {
		this.trantype = trantype;
	}

	public Long getPaymentdetailId() {
		return paymentdetailId;
	}

	public void setPaymentdetailId(Long paymentdetailId) {
		this.paymentdetailId = paymentdetailId;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Boolean getIsreversed() {
		return isreversed;
	}

	public void setIsreversed(Boolean isreversed) {
		this.isreversed = isreversed;
	}

	public Boolean getIsprocessed() {
		return isprocessed;
	}

	public void setIsprocessed(Boolean isprocessed) {
		this.isprocessed = isprocessed;
	}
	
	public Long getWaiveOffAmount() {
		return waiveOffAmount;
	}
	
	public void setWaiveOffAmount(Long waiveOffAmount) {
		this.waiveOffAmount = waiveOffAmount;
	}
	
}
