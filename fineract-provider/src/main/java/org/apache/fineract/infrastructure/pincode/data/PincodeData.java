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
package org.apache.fineract.infrastructure.pincode.data;

import java.io.Serializable;
//import org.apache.fineract.organisation.staff.data.StaffData;
@SuppressWarnings("unused")
public class PincodeData implements Serializable
{
	private final String AreaName;
	private final String division;
	private final String region;
	private final String taluk;
	private final String district;
	private final String state;
	private final Long pincode;
	private final Long talukaid;
	private final Long districtid;
	private final Long stateid;
	
	public PincodeData(String AreaName, String division, String region, String taluk, String district,
			String state,final Long pincode,Long talukaid,Long districtid,Long stateid) 
	{
		this.AreaName = AreaName;
		this.taluk = taluk;
		this.district = district;
		this.division = division;
		this.region = region;
		this.state = state;
		this.pincode = pincode;
		this.talukaid = talukaid;
		this.districtid = districtid;
		this.stateid =stateid;
	}

	public static PincodeData importInstance(final String AreaName,final String division,final String region,final String taluk,final String district,final String state,final Long pincode,Long talukaid,Long districtid,Long stateid) {
		
		return new PincodeData(AreaName,division,region,taluk,district,state,pincode,talukaid,districtid,stateid);
	}
	
	public static PincodeData instance(final String AreaName,final String division,final String region,final String taluk,final String district,final String state,final Long pincode,Long talukaid,Long districtid,Long stateid) {
		return new PincodeData(AreaName,division,region,taluk,district,state,pincode,talukaid,districtid,stateid);
	}

	public Long getPincode() {
		return pincode;
	}

	public String getAreaName() {
		return AreaName;
	}

	public String getDivision() {
		return division;
	}

	public String getRegion() {
		return region;
	}

	public String getTaluk() {
		return taluk;
	}

	public String getDistrict() {
		return district;
	}

	public String getState() {
		return state;
	}
}
