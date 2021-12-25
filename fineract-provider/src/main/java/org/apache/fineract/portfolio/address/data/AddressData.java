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
package org.apache.fineract.portfolio.address.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

public class AddressData {
private final Long client_id;

	private final String addressType;

	private final Long addressId;

	private final Long addressTypeId;

	private final Boolean isActive;

	private final String houseNo;
	
	private final String street;

	private final String addressLine1;

	private final String addressLine2;

	private final String addressLine3;

	private final String townVillage;

	private final String city;
	
	//private final String email;
	
	//private final String landLine;
	
	//private final String mobileNum;

	private final String countyDistrict;

	private final Long stateProvinceId;

	private final String countryName;
	
	private final String districtName;

	private final String stateName;

	private final Long countryId;
	
	private final Long districtId;
	
	private final Long talukaId;

	private final String talukaName;

	private final String postalCode;

	private final BigDecimal latitude;

	private final BigDecimal longitude;

	private final String createdBy;

	private final Date createdOn;

	private final String updatedBy;

	private final Date updatedOn;
	
	private final String taluka;
	private final String district;
	private final String landmark;

	// template holder
	private final Collection<CodeValueData> countryIdOptions;
	private final Collection<CodeValueData> districtIdOptions;
	private final Collection<CodeValueData> talukaIdOptions;
	private final Collection<CodeValueData> stateProvinceIdOptions;
	private final Collection<CodeValueData> addressTypeIdOptions;

	public AddressData(Long addressTypeId,String houseNo,String street, String addressLine1, String addressLine2, String addressLine3,
			String city, Long talukaId,  Long districtId, String postalCode, Boolean isActive,Long stateProvinceId,
			Long countryId,String talukaName,String districtName) {

		this.addressTypeId = addressTypeId;
		this.isActive = isActive;
		this.houseNo = houseNo;
		this.street = street;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.addressLine3 = addressLine3;
		this.countryId = countryId;
		this.districtId = districtId;
		this.talukaId = talukaId;
		this.postalCode = postalCode;
		this.stateProvinceId = stateProvinceId;
		this.city = city;
		// this.email = email;
		// this.landLine = landLine;
		// this.mobileNum = mobileNum;
		this.townVillage = null;
		this.client_id = null;
		this.addressType = null;
		this.addressId = null;
		this.countyDistrict = null;
		this.countryName = null;
		this.talukaName = talukaName;
		this.districtName = districtName;
		this.stateName = null;
		this.latitude = null;
		this.longitude = null;
		this.createdBy = null;
		this.createdOn = null;
		this.updatedBy = null;
		this.updatedOn = null;
		this.countryIdOptions = null;
		this.districtIdOptions = null;
		this.talukaIdOptions = null;
		this.stateProvinceIdOptions = null;
		this.addressTypeIdOptions = null;
		this.taluka = null;
		this.district = null;
		this.landmark = null;
	}


	private AddressData(final String addressType, final Long client_id, final Long addressId, final Long addressTypeId,
			final Boolean is_active, final String houseNo,final String street, final String addressLine1, final String addressLine2,
			final String addressLine3, final String townVillage, final String city, final String countyDistrict,final Long talukaId, final Long districtId, 
			final Long stateProvinceId, final Long countryId, final String talukaName, final String districtName, final String stateName, final String countryName,
			 final String postalCode, final BigDecimal latitude, final BigDecimal longitude, final String createdBy,
			final Date createdOn, final String updatedBy, final Date updatedOn, final Collection<CodeValueData> talukaIdOptions,
			final Collection<CodeValueData> districtIdOptions, final Collection<CodeValueData> stateProvinceIdOptions,final Collection<CodeValueData> countryIdOptions, 
			final Collection<CodeValueData> addressTypeIdOptions) {
		this.addressType = addressType;
		this.client_id = client_id;
		this.addressId = addressId;
		this.addressTypeId = addressTypeId;
		this.isActive = is_active;
		this.houseNo = houseNo;
		this.street = street;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.addressLine3 = addressLine3;
		this.townVillage = townVillage;
		this.city = city;
		// this.email = email;
		// this.landLine = landLine;
		// this.mobileNum = mobileNum;
		this.countyDistrict = countyDistrict;
		this.stateProvinceId = stateProvinceId;
		this.countryId = countryId;
		this.districtId = districtId;
		this.talukaId = talukaId;
		this.stateName = stateName;
		this.countryName = countryName;
		this.talukaName = talukaName;
		this.districtName = districtName;
		this.postalCode = postalCode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
		this.countryIdOptions = countryIdOptions;
		this.talukaIdOptions = talukaIdOptions;
		this.districtIdOptions = districtIdOptions;
		this.stateProvinceIdOptions = stateProvinceIdOptions;
		this.addressTypeIdOptions = addressTypeIdOptions;
		this.taluka = null;
		this.district = null;
		this.landmark = null;
	}
	
	private AddressData(final String addressType, final Long client_id, final Long addressId, final Long addressTypeId,
			final Boolean is_active, final String houseNo,final String street, final String addressLine1, final String addressLine2,
			final String addressLine3, final String townVillage, final String city, final String countyDistrict,final Long talukaId, final Long districtId, 
			final Long stateProvinceId, final Long countryId, final String talukaName, final String districtName, final String stateName, final String countryName,
			 final String postalCode, final BigDecimal latitude, final BigDecimal longitude, final String createdBy,
			final Date createdOn, final String updatedBy, final Date updatedOn, final Collection<CodeValueData> talukaIdOptions,
			final Collection<CodeValueData> districtIdOptions, final Collection<CodeValueData> stateProvinceIdOptions,final Collection<CodeValueData> countryIdOptions, 
			final Collection<CodeValueData> addressTypeIdOptions, final String taluka, final String district,final String landmark) {
		this.addressType = addressType;
		this.client_id = client_id;
		this.addressId = addressId;
		this.addressTypeId = addressTypeId;
		this.isActive = is_active;
		this.houseNo = houseNo;
		this.street = street;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.addressLine3 = addressLine3;
		this.townVillage = townVillage;
		this.city = city;
		// this.email = email;
		// this.landLine = landLine;
		// this.mobileNum = mobileNum;
		this.countyDistrict = countyDistrict;
		this.stateProvinceId = stateProvinceId;
		this.countryId = countryId;
		this.districtId = districtId;
		this.talukaId = talukaId;
		this.stateName = stateName;
		this.countryName = countryName;
		this.talukaName = talukaName;
		this.districtName = districtName;
		this.postalCode = postalCode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
		this.countryIdOptions = countryIdOptions;
		this.talukaIdOptions = talukaIdOptions;
		this.districtIdOptions = districtIdOptions;
		this.stateProvinceIdOptions = stateProvinceIdOptions;
		this.addressTypeIdOptions = addressTypeIdOptions;
		this.taluka = taluka;
		this.district = district;
		this.landmark = landmark;
	}

	public static AddressData instance(final String addressType, final Long client_id, final Long addressId,
			final Long addressTypeId, final Boolean is_active, final String houseNo, final String street, final String addressLine1,
			final String addressLine2, final String addressLine3, final String townVillage, final String city,
			final String countyDistrict, final Long talukaId, final Long districtId, 
			final Long stateProvinceId, final Long countryId,  final String talukaName, final String districtName, final String stateName,
			final String countryName,  final String postalCode, final BigDecimal latitude, final BigDecimal longitude,
			final String createdBy, final Date createdOn, final String updatedBy, final Date updatedOn) {

		return new AddressData(addressType, client_id, addressId, addressTypeId, is_active, houseNo, street, addressLine1,
				addressLine2, addressLine3, townVillage, city, countyDistrict, talukaId, districtId, stateProvinceId, countryId,
				 talukaName, districtName, stateName, countryName, postalCode, latitude, longitude, createdBy, createdOn, updatedBy,
				updatedOn, null, null, null, null, null);
	}

	public static AddressData instance1(final Long addressId, final String houseNo, final String street, final String addressLine1,
			final String addressLine2, final String addressLine3, final String townVillage, final String city,
			final String countyDistrict, final Long talukaId, final Long districtId, final Long stateProvinceId,
			final Long countryId,  final String postalCode, final BigDecimal latitude, final BigDecimal longitude, final String createdBy, final Date createdOn,
			final String updatedBy, final Date updatedOn) {
		return new AddressData(null, null, addressId, null, false, houseNo, street, addressLine1, addressLine2,
				addressLine3, townVillage, city, countyDistrict,  talukaId,  districtId, stateProvinceId, countryId, null,null, null,
				null, postalCode, latitude, longitude, createdBy, createdOn, updatedBy, updatedOn,null, null, null, null, null);
	}
	
	public static AddressData instance3(final String addressType, final Long client_id, final Long addressId,
			final Long addressTypeId, final Boolean is_active, final String houseNo, final String street, final String addressLine1,
			final String addressLine2, final String addressLine3, final String townVillage, final String city,
			final String countyDistrict, final Long talukaId, final Long districtId, 
			final Long stateProvinceId, final Long countryId,  final String talukaName, final String districtName, final String stateName,
			final String countryName,  final String postalCode, final BigDecimal latitude, final BigDecimal longitude,
			final String createdBy, final Date createdOn, final String updatedBy,
			final Date updatedOn,final String taluka,final String district, final String landmark) {

		return new AddressData(addressType, client_id, addressId, addressTypeId, is_active, houseNo, street, addressLine1,
				addressLine2, addressLine3, townVillage, city, countyDistrict, talukaId, districtId, stateProvinceId, countryId,
				 talukaName, districtName, stateName, countryName, postalCode, latitude, longitude, createdBy, createdOn, updatedBy,
				updatedOn, null, null, null, null, null, taluka,district, landmark);
	}

	public static AddressData template( final Collection<CodeValueData> talukaIdOptions,
			final Collection<CodeValueData> districtIdOptions,
			final Collection<CodeValueData> stateProvinceIdOptions, final Collection<CodeValueData> countryIdOptions,
			final Collection<CodeValueData> addressTypeIdOptions) {
		final Long client_idtemp = null;

		final Long addressIdtemp = null;

		final Long addressTypeIdtemp = null;

		final Boolean is_activetemp = null;

		final String houseNotemp = null;
		
		final String streettemp = null;

		final String addressLine1temp = null;

		final String addressLine2temp = null;

		final String addressLine3temp = null;

		final String townVillagetemp = null;

		final String citytemp = null;
		
		// final String emailtemp = null;
		
		// final String landLinetemp = null;
		
		// final String mobileNumtemp = null;

		final String countyDistricttemp = null;

		final Long stateProvinceIdtemp = null;

		final Long countryIdtemp = null;
		
		final Long districtIdtemp = null;

		final Long talukaIdtemp = null;

		final String postalCodetemp = null;

		final BigDecimal latitudetemp = null;

		final BigDecimal longitudetemp = null;

		final String createdBytemp = null;

		final Date createdOntemp = null;

		final String updatedBytemp = null;

		final Date updatedOntemp = null;

		return new AddressData(null, client_idtemp, addressIdtemp, addressTypeIdtemp, is_activetemp, houseNotemp, streettemp,
				addressLine1temp, addressLine2temp, addressLine3temp, townVillagetemp, citytemp,
				countyDistricttemp, talukaIdtemp, districtIdtemp, stateProvinceIdtemp, countryIdtemp,  null, null, null, null, postalCodetemp, latitudetemp,
				longitudetemp, createdBytemp, createdOntemp, updatedBytemp, updatedOntemp, talukaIdOptions, districtIdOptions,
				stateProvinceIdOptions, countryIdOptions, addressTypeIdOptions);
	}


	public Long getClient_id() {
		return client_id;
	}


	public String getAddressType() {
		return addressType;
	}


	public Long getAddressId() {
		return addressId;
	}


	public Long getAddressTypeId() {
		return addressTypeId;
	}


	public Boolean getIsActive() {
		return isActive;
	}


	public String getHouseNo() {
		return houseNo;
	}


	public String getStreet() {
		return street;
	}


	public String getAddressLine1() {
		return addressLine1;
	}


	public String getAddressLine2() {
		return addressLine2;
	}


	public String getAddressLine3() {
		return addressLine3;
	}


	public String getTownVillage() {
		return townVillage;
	}


	public String getCity() {
		return city;
	}


	public String getCountyDistrict() {
		return countyDistrict;
	}


	public Long getStateProvinceId() {
		return stateProvinceId;
	}


	public String getCountryName() {
		return countryName;
	}


	public String getDistrictName() {
		return districtName;
	}


	public String getStateName() {
		return stateName;
	}


	public Long getCountryId() {
		return countryId;
	}


	public Long getDistrictId() {
		return districtId;
	}


	public Long getTalukaId() {
		return talukaId;
	}


	public String getTalukaName() {
		return talukaName;
	}


	public String getPostalCode() {
		return postalCode;
	}


	public BigDecimal getLatitude() {
		return latitude;
	}


	public BigDecimal getLongitude() {
		return longitude;
	}


	public String getCreatedBy() {
		return createdBy;
	}


	public Date getCreatedOn() {
		return createdOn;
	}


	public String getUpdatedBy() {
		return updatedBy;
	}


	public Date getUpdatedOn() {
		return updatedOn;
	}


	public Collection<CodeValueData> getCountryIdOptions() {
		return countryIdOptions;
	}


	public Collection<CodeValueData> getDistrictIdOptions() {
		return districtIdOptions;
	}


	public Collection<CodeValueData> getTalukaIdOptions() {
		return talukaIdOptions;
	}


	public Collection<CodeValueData> getStateProvinceIdOptions() {
		return stateProvinceIdOptions;
	}


	public Collection<CodeValueData> getAddressTypeIdOptions() {
		return addressTypeIdOptions;
	}



}
