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
package org.apache.fineract.infrastructure.pincode.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
//import javax.persistence.OneToOne;
import javax.persistence.Table;

//import javax.persistence.UniqueConstraint;
//import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
//import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@SuppressWarnings("unused")
@Entity
@Table(name = "pin_code")
public class Pincode extends AbstractPersistableCustom
{
	@Column(name = "post_office_name" , length = 45)
	private String Areaname;
	
	@Column(name = "pincode")
	private Long pincode;

	@Column(name = "taluk" , length = 45)
	private String taluk;
	
	@Column(name = "district" , length = 45)
	private String district;
	
	@Column(name = "state" , length = 45)
	private String state;
	
	public static Pincode fromJson(final JsonCommand command) 
	{
		
		 final String AreanameParamName = "Areaname";
	     final String AreaName = command.stringValueOfParameterNamed(AreanameParamName);
	     
	    
	     
	     final String pincodeParamName = "pincode";
	     final Long pincode = command.longValueOfParameterNamed("pincode");
		
	     final String talukParamName = "taluk";
	     final String taluk = command.stringValueOfParameterNamed(talukParamName);
	     
	     final String districtParamName = "district";
	     final String district = command.stringValueOfParameterNamed(districtParamName);
		
	     final String stateParamName = "state";
	     final String state = command.stringValueOfParameterNamed(stateParamName);
		
	     return new Pincode(AreaName,null,null,taluk,district,state,pincode);
		
	}
	
	protected Pincode()
	{
		
	}
	
	private Pincode(String Areaname, String division, String region, String taluk, String district,
			String state,Long pincode)
	{
		this.Areaname = Areaname;
		this.taluk = taluk;
		this.district = district;
		
		this.state = state;
	}

	public Long getPincode() {
		return pincode;
	}

	public String getAreaname() {
		return Areaname;
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
