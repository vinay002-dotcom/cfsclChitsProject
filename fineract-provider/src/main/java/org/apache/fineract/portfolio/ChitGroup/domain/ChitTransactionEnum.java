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
package org.apache.fineract.portfolio.ChitGroup.domain;

import org.springframework.util.StringUtils;

/**
 * Enum representation of client status states.
 */
public enum ChitTransactionEnum {

	INVALID(0, "chitTransactionStatusType.invalid"),
	
	INSTALLMENT_PENALTY(1, "chitTransactionStatusType.installment.penalty"), //
	INSTALLMENT_EMI(2, "chitTransactionStatusType.installment.emi"),
	INSTALLMENT_OVERDUE(3, "chitTransactionStatusType.installment.overdue"),
    CHARGES(4, "chitTransactionStatusType.charges"), //
    WINNERPRIZEMONEY(5,"chitTransactionStatusType.winnerprizemoney"),
	FORCLOSURE(6,"chitTransactionStatusType.forclosure"),
	TERMINATE(7,"chitTransactionStatusType.terminate");
  //  INSTALLMENT_OVERDUE(4, "chitTransactionStatusType.installment.overdue"); //
   // INSTALLMENT_EMI(5, "chitTransactionStatusType.installment.penalty");

    private final Integer value;
    private final String code;

    public static ChitTransactionEnum fromInt(final Integer statusValue) {

        ChitTransactionEnum enumeration = ChitTransactionEnum.INVALID;
        switch (statusValue) {
            case 1:
                enumeration = ChitTransactionEnum.INSTALLMENT_PENALTY;
            break;
            case 2:
                enumeration = ChitTransactionEnum.INSTALLMENT_EMI;
            break;
            case 3:
                enumeration = ChitTransactionEnum.INSTALLMENT_OVERDUE;
            break;
            case 4:
                enumeration = ChitTransactionEnum.CHARGES;
            break;
            case 5:
                enumeration = ChitTransactionEnum.WINNERPRIZEMONEY;
            break;
            case 6:
                enumeration = ChitTransactionEnum.FORCLOSURE;
            break;
            case 7:
            	enumeration = ChitTransactionEnum.TERMINATE;
            break;
        }
        return enumeration;
    }

    public static ChitTransactionEnum fromString(final String clientString) {

        ChitTransactionEnum chitTransactionStatus = ChitTransactionEnum.INVALID;

        if (StringUtils.isEmpty(clientString)) {
            return chitTransactionStatus;
        }

        if (clientString.equalsIgnoreCase(ChitTransactionEnum.INSTALLMENT_EMI.toString())) {
        	chitTransactionStatus = ChitTransactionEnum.INSTALLMENT_EMI;
        } else if (clientString.equalsIgnoreCase(ChitTransactionEnum.CHARGES.toString())) {
        	chitTransactionStatus = ChitTransactionEnum.CHARGES;
        } else if (clientString.equalsIgnoreCase(ChitTransactionEnum.WINNERPRIZEMONEY.toString())) {
        	chitTransactionStatus = ChitTransactionEnum.WINNERPRIZEMONEY;
        } else if (clientString.equalsIgnoreCase(ChitTransactionEnum.INSTALLMENT_OVERDUE.toString())) {
        	chitTransactionStatus = ChitTransactionEnum.INSTALLMENT_OVERDUE;
        } else if (clientString.equalsIgnoreCase(ChitTransactionEnum.INSTALLMENT_PENALTY.toString())) {
        	chitTransactionStatus = ChitTransactionEnum.INSTALLMENT_PENALTY;
        } else if (clientString.equalsIgnoreCase(ChitTransactionEnum.FORCLOSURE.toString())) {
        	chitTransactionStatus = ChitTransactionEnum.FORCLOSURE;
        } else if (clientString.equalsIgnoreCase(ChitTransactionEnum.TERMINATE.toString())) {
        	chitTransactionStatus = ChitTransactionEnum.TERMINATE;
        }
        return chitTransactionStatus;
    }

    ChitTransactionEnum(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

	public Integer getValue() {
		return value;
	}

	public String getCode() {
		return code;
	}

    
}
