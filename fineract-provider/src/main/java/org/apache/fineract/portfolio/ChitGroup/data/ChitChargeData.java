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

/**
 * Immutable data object representing a code.
 */
public final class ChitChargeData implements Serializable {

    private final Long id;
   
    private final String name;
    
    private final Double amount;
    
    private final Boolean isEnabled;

    public static ChitChargeData instance(final Long id, final String name, final Double amount,final Boolean isEnabled) {
        return new ChitChargeData(id, name, amount,isEnabled);
    }

    public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Double getAmount() {
		return amount;
	}

	private ChitChargeData(final Long id, final String name, final Double amount,final Boolean isEnabled) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.isEnabled = isEnabled;
    }

    public Boolean getIsEnabled() {
		return isEnabled;
	}

	public Long getCodeId() {
        return this.id;
    }
}
