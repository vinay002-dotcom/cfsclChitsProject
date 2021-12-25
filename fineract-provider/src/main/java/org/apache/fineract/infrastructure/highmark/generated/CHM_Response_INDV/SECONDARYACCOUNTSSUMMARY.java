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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11-b140731.1112 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.13 at 03:32:34 PM IST 
//


package org.apache.fineract.infrastructure.highmark.generated.CHM_Response_INDV;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="SECONDARY-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SECONDARY-ACTIVE-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SECONDARY-OVERDUE-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SECONDARY-SECURED-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SECONDARY-UNSECURED-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SECONDARY-UNTAGGED-NUMBER-OF-ACCOUNTS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SECONDARY-CURRENT-BALANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SECONDARY-SANCTIONED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SECONDARY-DISBURSED-AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "SECONDARY-ACCOUNTS-SUMMARY")
public class SECONDARYACCOUNTSSUMMARY {

    @XmlElement(name = "SECONDARY-NUMBER-OF-ACCOUNTS")
    protected String secondarynumberofaccounts;
    @XmlElement(name = "SECONDARY-ACTIVE-NUMBER-OF-ACCOUNTS")
    protected String secondaryactivenumberofaccounts;
    @XmlElement(name = "SECONDARY-OVERDUE-NUMBER-OF-ACCOUNTS")
    protected String secondaryoverduenumberofaccounts;
    @XmlElement(name = "SECONDARY-SECURED-NUMBER-OF-ACCOUNTS")
    protected String secondarysecurednumberofaccounts;
    @XmlElement(name = "SECONDARY-UNSECURED-NUMBER-OF-ACCOUNTS")
    protected String secondaryunsecurednumberofaccounts;
    @XmlElement(name = "SECONDARY-UNTAGGED-NUMBER-OF-ACCOUNTS")
    protected String secondaryuntaggednumberofaccounts;
    @XmlElement(name = "SECONDARY-CURRENT-BALANCE")
    protected String secondarycurrentbalance;
    @XmlElement(name = "SECONDARY-SANCTIONED-AMOUNT")
    protected String secondarysanctionedamount;
    @XmlElement(name = "SECONDARY-DISBURSED-AMOUNT")
    protected String secondarydisbursedamount;

    /**
     * Gets the value of the secondarynumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECONDARYNUMBEROFACCOUNTS() {
        return secondarynumberofaccounts;
    }

    /**
     * Sets the value of the secondarynumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECONDARYNUMBEROFACCOUNTS(String value) {
        this.secondarynumberofaccounts = value;
    }

    /**
     * Gets the value of the secondaryactivenumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECONDARYACTIVENUMBEROFACCOUNTS() {
        return secondaryactivenumberofaccounts;
    }

    /**
     * Sets the value of the secondaryactivenumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECONDARYACTIVENUMBEROFACCOUNTS(String value) {
        this.secondaryactivenumberofaccounts = value;
    }

    /**
     * Gets the value of the secondaryoverduenumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECONDARYOVERDUENUMBEROFACCOUNTS() {
        return secondaryoverduenumberofaccounts;
    }

    /**
     * Sets the value of the secondaryoverduenumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECONDARYOVERDUENUMBEROFACCOUNTS(String value) {
        this.secondaryoverduenumberofaccounts = value;
    }

    /**
     * Gets the value of the secondarysecurednumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECONDARYSECUREDNUMBEROFACCOUNTS() {
        return secondarysecurednumberofaccounts;
    }

    /**
     * Sets the value of the secondarysecurednumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECONDARYSECUREDNUMBEROFACCOUNTS(String value) {
        this.secondarysecurednumberofaccounts = value;
    }

    /**
     * Gets the value of the secondaryunsecurednumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECONDARYUNSECUREDNUMBEROFACCOUNTS() {
        return secondaryunsecurednumberofaccounts;
    }

    /**
     * Sets the value of the secondaryunsecurednumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECONDARYUNSECUREDNUMBEROFACCOUNTS(String value) {
        this.secondaryunsecurednumberofaccounts = value;
    }

    /**
     * Gets the value of the secondaryuntaggednumberofaccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECONDARYUNTAGGEDNUMBEROFACCOUNTS() {
        return secondaryuntaggednumberofaccounts;
    }

    /**
     * Sets the value of the secondaryuntaggednumberofaccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECONDARYUNTAGGEDNUMBEROFACCOUNTS(String value) {
        this.secondaryuntaggednumberofaccounts = value;
    }

    /**
     * Gets the value of the secondarycurrentbalance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECONDARYCURRENTBALANCE() {
        return secondarycurrentbalance;
    }

    /**
     * Sets the value of the secondarycurrentbalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECONDARYCURRENTBALANCE(String value) {
        this.secondarycurrentbalance = value;
    }

    /**
     * Gets the value of the secondarysanctionedamount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECONDARYSANCTIONEDAMOUNT() {
        return secondarysanctionedamount;
    }

    /**
     * Sets the value of the secondarysanctionedamount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECONDARYSANCTIONEDAMOUNT(String value) {
        this.secondarysanctionedamount = value;
    }

    /**
     * Gets the value of the secondarydisbursedamount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECONDARYDISBURSEDAMOUNT() {
        return secondarydisbursedamount;
    }

    /**
     * Sets the value of the secondarydisbursedamount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECONDARYDISBURSEDAMOUNT(String value) {
        this.secondarydisbursedamount = value;
    }

}
