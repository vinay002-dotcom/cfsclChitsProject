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

public class ChitGroupDropDownData implements Serializable
{
	private Long chitId;
	private String chitName;
	private Long chitNumber;
	public ChitGroupDropDownData(Long chitId, String chitName, Long chitNumber) {
		super();
		this.chitId = chitId;
		this.chitName = chitName;
		this.chitNumber = chitNumber;
	}
	public Long getChitId() {
		return this.chitId;
	}
	public void setChitId(Long chitId) {
		this.chitId = chitId;
	}
	public String getChitName() {
		return 	this.chitName;
	}
	public void setChitName(String chitName) {
		this.chitName = chitName;
	}
	public Long getChitNumber() {
		return this.chitNumber;
	}
	public void setChitNumber(Long chitNumber) {
		this.chitNumber = chitNumber;
	}
	
	public static ChitGroupDropDownData instance(Long chitId, String chitName, Long chitNumber)
	{
		return new ChitGroupDropDownData(chitId,chitName,chitNumber);
	}
}
