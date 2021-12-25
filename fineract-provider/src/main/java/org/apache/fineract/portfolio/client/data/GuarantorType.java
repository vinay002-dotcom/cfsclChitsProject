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
package org.apache.fineract.portfolio.client.data;

import org.springframework.util.StringUtils;

/**
 * Enum representation of client status states.
 */
public enum GuarantorType {

	INVALID(0, "chitTransactionStatusType.invalid"),

	PRIMARY(1, "GuarantorType.primary"), //
	SECONDARY(2, "GuarantorType.secondary"),
	COAPPLICANT(3,"GuarantorType.coapplicant");
	private final Integer value;
	private final String code;

	public static GuarantorType fromInt(final Integer statusValue) {

		GuarantorType enumeration = GuarantorType.INVALID;
		switch (statusValue) {
		case 1:
			enumeration = GuarantorType.PRIMARY;
			break;
		case 2:
			enumeration = GuarantorType.SECONDARY;
			break;
		case 3:
			enumeration = GuarantorType.COAPPLICANT;
			break;
		}
		return enumeration;
	}

	public static GuarantorType fromString(final String clientString) {

		GuarantorType chitTransactionStatus = GuarantorType.INVALID;

		if (StringUtils.isEmpty(clientString)) {
			return chitTransactionStatus;
		}

		if (clientString.equalsIgnoreCase(GuarantorType.PRIMARY.toString())) {
			chitTransactionStatus = GuarantorType.PRIMARY;
		} else if (clientString.equalsIgnoreCase(GuarantorType.SECONDARY.toString())) {
			chitTransactionStatus = GuarantorType.SECONDARY;
		}
		else if (clientString.equalsIgnoreCase(GuarantorType.COAPPLICANT.toString())) {
			chitTransactionStatus = GuarantorType.COAPPLICANT;
		}
		return chitTransactionStatus;
	}

	GuarantorType(final Integer value, final String code) {
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
