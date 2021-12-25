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
package org.apache.fineract.portfolio.address.domain;

import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.client.domain.ClientAddress;

@Entity
@Table(name = "m_address")
public class Address extends AbstractPersistableCustom {

    /*
     * @OneToMany(mappedBy = "address", cascade = CascadeType.ALL) private List<ClientAddress> clientaddress = new
     * ArrayList<>();
     */

    public String getTalukaName() {
		return TalukaName;
	}

	public void setTalukaName(String TalukaName) {
		this.TalukaName = TalukaName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	@OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private Set<ClientAddress> clientaddress;

    @Column(name = "houseNo")
	private String houseNo;

    @Column(name = "street")
    private String street;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "address_line_3")
    private String addressLine3;

    @Column(name = "town_village")
    private String townVillage;

    @Column(name = "city")
    private String city;

    @ManyToOne
    @JoinColumn(name = "taluka_id")
    private CodeValue taluka;

    @ManyToOne
	@JoinColumn(name = "district_id")
	private CodeValue district;

    @Column(name = "county_district")
    private String countyDistrict;
    
    @Column(name = "taluka")
    private String TalukaName;
    
    @Column(name = "district")
    private String districtName;
    
    @Column(name = "landmark")
    private String landmark;

    @ManyToOne
    @JoinColumn(name = "state_province_id")
    private CodeValue stateProvince;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private CodeValue country;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_on")
    private Date updatedOn;

    private Address(final String houseNo, final String street, final String addressLine1, final String addressLine2, final String addressLine3,
            final String townVillage, final String city, final CodeValue taluka, final CodeValue district, final String countyDistrict, final CodeValue stateProvince,
            final CodeValue country, final String postalCode, final BigDecimal latitude, final BigDecimal longitude, final String createdBy,
            final LocalDate createdOn, final String updatedBy, final LocalDate updatedOn,final String TalukaName,final String districtName,final String landmark) {
        this.houseNo = houseNo;
        this.street = street;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.townVillage = townVillage;
        this.city = city;
        this.taluka = taluka;
        this.district = district;
        this.countyDistrict = countyDistrict;
        this.taluka = taluka;
        this.stateProvince = stateProvince;
        this.country = country;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
        // this.createdOn = createdOn;
        this.updatedBy = updatedBy;
        // this.updatedOn = updatedOn;
        
        this.TalukaName = TalukaName;
        this.districtName = districtName;
        this.landmark = landmark;

        if (createdOn != null) {
            this.createdOn = Date.from(createdOn.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());

        }

        if (updatedOn != null) {
            this.updatedOn = Date.from(updatedOn.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
        }

    }

    public Address() {

    }

    public static Address fromJson(final JsonCommand command, final CodeValue taluka, final CodeValue district, final CodeValue stateProvince, final CodeValue country) {

        final String houseNo = command.stringValueOfParameterNamed("houseNo");

        final String street = command.stringValueOfParameterNamed("street");

        final String addressLine1 = command.stringValueOfParameterNamed("addressLine1");

        final String addressLine2 = command.stringValueOfParameterNamed("addressLine2");

        final String addressLine3 = command.stringValueOfParameterNamed("addressLine3");

        final String townVillage = command.stringValueOfParameterNamed("townVillage");

        final String city = command.stringValueOfParameterNamed("city");

        final String countyDistrict = command.stringValueOfParameterNamed("countyDistrict");

        final String postalCode = command.stringValueOfParameterNamed("postalCode");

        final BigDecimal latitude = command.bigDecimalValueOfParameterNamed("latitude");

        final BigDecimal longitude = command.bigDecimalValueOfParameterNamed("longitude");

        final String createdBy = command.stringValueOfParameterNamed("createdBy");

        final LocalDate createdOn = command.localDateValueOfParameterNamed("createdOn");

        final String updatedBy = command.stringValueOfParameterNamed("updatedBy");

        final LocalDate updatedOn = command.localDateValueOfParameterNamed("updatedOn");
        
        final String TalukaName = command.stringValueOfParameterNamed("TalukaName");
        
        final String districtName = command.stringValueOfParameterNamed("districtName");
        
        final String Landmark = command.stringValueOfParameterNamed("LandMark");

        return new Address(houseNo, street, addressLine1, addressLine2, addressLine3, townVillage, city, taluka, district, countyDistrict, stateProvince, country,
                postalCode, latitude, longitude, createdBy, createdOn, updatedBy, updatedOn,TalukaName,districtName,Landmark);
    }

    public CodeValue getDistrict() {
		return district;
	}

	public void setDistrict(CodeValue district) {
		this.district = district;
	}

	public static Address fromJsonObject(final JsonObject jsonObject, final CodeValue taluka, final CodeValue district, final CodeValue state_province, final CodeValue country) {
        String houseNo = null ;
        String street =null;
        String addressLine1 =null ;
        String addressLine2 =null;
        String addressLine3 =null;
        String townVillage=null;
        String city =null;
        String countyDistrict =null;
        
        String postalCode =null;
        BigDecimal latitude = BigDecimal.ZERO;
        BigDecimal longitude = BigDecimal.ZERO;
        String createdBy=null ;
        Locale locale = Locale.ENGLISH;
        String updatedBy=null ;
        LocalDate updatedOnDate = null;
        LocalDate createdOnDate = null;
        String talukaname = null;
        String districtname = null;
        String landmark = null;
        if (jsonObject.has("houseNo")) {
			houseNo = jsonObject.get("houseNo").getAsString();
		}
        
        if (jsonObject.has("TalukaName")) {
        	talukaname = jsonObject.get("TalukaName").getAsString();
		}
        
        if (jsonObject.has("districtName")) {
        	districtname = jsonObject.get("districtName").getAsString();
		}
        
        if (jsonObject.has("LandMark")) {
        	landmark = jsonObject.get("LandMark").getAsString();
		}

        if (jsonObject.has("street")) {
            street = jsonObject.get("street").getAsString();

        }

        if (jsonObject.has("addressLine1")) {
            addressLine1 = jsonObject.get("addressLine1").getAsString();
        }
        if (jsonObject.has("addressLine2")) {

            addressLine2 = jsonObject.get("addressLine2").getAsString();
        }
        if (jsonObject.has("addressLine3")) {
            addressLine3 = jsonObject.get("addressLine3").getAsString();
        }
        if (jsonObject.has("townVillage")) {
            townVillage = jsonObject.get("townVillage").getAsString();
        }
        if (jsonObject.has("city")) {
            city = jsonObject.get("city").getAsString();
        }
        if (jsonObject.has("countyDistrict")) {
            countyDistrict = jsonObject.get("countyDistrict").getAsString();
        }
        if (jsonObject.has("postalCode")) {

            postalCode = jsonObject.get("postalCode").getAsString();
        }
        if (jsonObject.has("latitude")) {

            latitude = jsonObject.get("latitude").getAsBigDecimal();
        }
        if (jsonObject.has("longitude")) {

            longitude = jsonObject.get("longitude").getAsBigDecimal();
        }

        if (jsonObject.has("createdBy")) {
            createdBy = jsonObject.get("createdBy").getAsString();
        }
        if (jsonObject.has("createdOn")) {
            String createdOn = jsonObject.get("createdOn").getAsString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            createdOnDate = LocalDate.parse(createdOn, formatter);

        }
        if (jsonObject.has("updatedBy")) {
            updatedBy = jsonObject.get("updatedBy").getAsString();
        }
        if (jsonObject.has("updatedOn")) {
            String updatedOn = jsonObject.get("updatedOn").getAsString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            updatedOnDate = LocalDate.parse(updatedOn, formatter);
        }

        return new Address(houseNo, street, addressLine1, addressLine2, addressLine3, townVillage, city, taluka, district, countyDistrict, state_province, country,
                postalCode, latitude, longitude, createdBy, createdOnDate, updatedBy, updatedOnDate,talukaname,districtname,landmark);
    }

    public Set<ClientAddress> getClientaddress() {
        return this.clientaddress;
    }

    public void setClientaddress(Set<ClientAddress> clientaddress) {
        this.clientaddress = clientaddress;
    }

    public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}
	
	public String getHouseNo() {
		return this.houseNo;
	}

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return this.addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getTownVillage() {
        return this.townVillage;
    }

    public void setTownVillage(String townVillage) {
        this.townVillage = townVillage;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public CodeValue getTaluka() {
        return this.taluka;
    }

    public void setTaluka(CodeValue taluka) {
        this.taluka = taluka;
    }
    
    public String getCountyDistrict() {
        return this.countyDistrict;
    }

    public void setCountyDistrict(String countyDistrict) {
        this.countyDistrict = countyDistrict;
    }

    public CodeValue getStateProvince() {
        return this.stateProvince;
    }

    public void setStateProvince(CodeValue stateProvince) {
        this.stateProvince = stateProvince;
    }

    public CodeValue getCountry() {
        return this.country;
    }

    public void setCountry(CodeValue country) {
        this.country = country;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = Date.from(createdOn.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedOn() {
        return this.updatedOn;
    }

    public void setUpdatedOn(LocalDate updatedOn) {
        this.updatedOn = Date.from(updatedOn.atStartOfDay(DateUtils.getDateTimeZoneOfTenant()).toInstant());
    }

}
