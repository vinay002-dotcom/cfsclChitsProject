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

public enum subscriberStatusEnum {

	INVALID(0, "subscriberStatusType.invalid"),
	
	BID_ADVANCE(1, "subscriberType.bidAdvance"), //
	TERMINATE(2, "subscriberType.terminate"),
	FORECLOSURE(3,"subscriberType.fore-Closure");
	
	private final Integer value;
    private final String code;

    public static subscriberStatusEnum fromInt(final Integer statusValue) {

    	subscriberStatusEnum enumeration = subscriberStatusEnum.INVALID;
        switch (statusValue) {
            case 1:
                enumeration = subscriberStatusEnum.BID_ADVANCE;
            break;
            case 2:
                enumeration = subscriberStatusEnum.TERMINATE;
            break;
            case 3:
                enumeration = subscriberStatusEnum.FORECLOSURE;
            break;
           
        }
        return enumeration;
    }

    public static subscriberStatusEnum fromString(final String clientString) {

    	subscriberStatusEnum chitTransactionStatus = subscriberStatusEnum.INVALID;

        if (StringUtils.isEmpty(clientString)) {
            return chitTransactionStatus;
        }

        if (clientString.equalsIgnoreCase(subscriberStatusEnum.BID_ADVANCE.toString())) {
        	chitTransactionStatus = subscriberStatusEnum.BID_ADVANCE;
        } else if (clientString.equalsIgnoreCase(subscriberStatusEnum.TERMINATE.toString())) {
        	chitTransactionStatus = subscriberStatusEnum.TERMINATE;
        } else if (clientString.equalsIgnoreCase(subscriberStatusEnum.FORECLOSURE.toString())) {
        	chitTransactionStatus = subscriberStatusEnum.FORECLOSURE;
        }
        return chitTransactionStatus;
    }

    subscriberStatusEnum(final Integer value, final String code) {
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
