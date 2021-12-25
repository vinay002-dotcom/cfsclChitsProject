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
import java.time.LocalDate;

public final class ChitSubscriberChargeData implements Serializable 
{

	private Long id;
	private Long chitSubscriberId;
	private Long chitChargeId;
	private Long chitCycleId;
	private Long amount;
	private LocalDate dueDate;
	private Boolean isPaid;
	private Boolean isWaived;
	private Long staffId;
	
	
	private ChitSubscriberChargeData(Long id, Long chitSubscriberId, Long chitChargeId, Long chitCycleId, Long amount,
			LocalDate dueDate, Boolean isPaid, Boolean isWaived,Long staffId) {
		this.id = id;
		this.chitSubscriberId = chitSubscriberId;
		this.chitChargeId = chitChargeId;
		this.chitCycleId = chitCycleId;
		this.amount = amount;
		this.dueDate = dueDate;
		this.isPaid = isPaid;
		this.isWaived = isWaived;
		this.staffId = staffId;
	}
	
	public static ChitSubscriberChargeData instance(Long id, Long chitSubscriberId, Long chitChargeId, Long chitCycleId, Long amount,
			LocalDate dueDate, Boolean isPaid, Boolean isWaived,Long staffId)
	{
		return new ChitSubscriberChargeData(id,chitSubscriberId,chitChargeId,chitCycleId,amount,dueDate,isPaid,isWaived,staffId);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChitSubscriberId() {
		return chitSubscriberId;
	}

	public void setChitSubscriberId(Long chitSubscriberId) {
		this.chitSubscriberId = chitSubscriberId;
	}

	public Long getChitChargeId() {
		return chitChargeId;
	}

	public void setChitChargeId(Long chitChargeId) {
		this.chitChargeId = chitChargeId;
	}

	public Long getChitCycleId() {
		return chitCycleId;
	}

	public void setChitCycleId(Long chitCycleId) {
		this.chitCycleId = chitCycleId;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public Boolean getIsWaived() {
		return isWaived;
	}

	public void setIsWaived(Boolean isWaived) {
		this.isWaived = isWaived;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}
	
	
	
	
 
}
