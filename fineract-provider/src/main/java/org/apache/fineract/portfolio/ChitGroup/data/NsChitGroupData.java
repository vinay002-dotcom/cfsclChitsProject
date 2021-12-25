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

public class NsChitGroupData implements Serializable
{
	Long id;
	String name;
	Double chitValue;
	Long chitDuration;
	Long NoOfSubs;
	Double minPercent;
	Double maxPercent;
	Boolean isEnabled;
	Double enrollmentfee;
	
	public Double getMinPercent() {
		return minPercent;
	}
	public void setMinPercent(Double minPercent) {
		this.minPercent = minPercent;
	}
	public Double getMaxPercent() {
		return maxPercent;
	}
	public void setMaxPercent(Double maxPercent) {
		this.maxPercent = maxPercent;
	}
	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getChitValue() {
		return chitValue;
	}
	public void setChitValue(Double chitValue) {
		this.chitValue = chitValue;
	}
	public Long getChitDuration() {
		return chitDuration;
	}
	public void setChitDuration(Long chitDuration) {
		this.chitDuration = chitDuration;
	}
	public Long getNoOfSubs() {
		return NoOfSubs;
	}
	public void setNoOfSubs(Long NoOfSubs) {
		this.NoOfSubs = NoOfSubs;
	}
	public NsChitGroupData(Long id, String name, Double chitValue, Long chitDuration, Long NoOfSubs,Double minPercent,Double maxPercent,
	Boolean isEnabled, Double enrollmentfee) {
		super();
		this.id = id;
		this.name = name;
		this.chitValue = chitValue;
		this.chitDuration = chitDuration;
		this.NoOfSubs = NoOfSubs;
		this.maxPercent = maxPercent;
		this.minPercent = minPercent;
		this.isEnabled = isEnabled;
		this.enrollmentfee = enrollmentfee;
	}
	public NsChitGroupData() {
		super();
	}
	
	public static NsChitGroupData instance(Long id, String name, Double chitValue, Long chitDuration, Long NoOfSubs,Double minPercent,Double maxPercent,
			Boolean isEnabled, Double enrollmentfee)
	{
		return new NsChitGroupData(id,name,chitValue,chitDuration,NoOfSubs,minPercent,maxPercent,isEnabled, enrollmentfee);
	}
}
