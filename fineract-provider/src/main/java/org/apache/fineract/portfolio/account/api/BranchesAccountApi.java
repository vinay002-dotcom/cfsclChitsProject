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
package org.apache.fineract.portfolio.account.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.account.data.BranchesAccountData;
import org.apache.fineract.portfolio.account.service.BranchesAccountReadPlatformService;
import org.apache.fineract.portfolio.account.service.BranchesAccountWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/branchesAccount")
@Component
@Scope("singleton")
@Tag(name = "Branch Account", description = "Allow you to create update and read branch account Data")
public class BranchesAccountApi {

	private final BranchesAccountReadPlatformService branchesAccountReadPlatformService; 
	private final BranchesAccountWritePlatformService branchesAccountWritePlatformService;
	private final DefaultToApiJsonSerializer defaultToApiJsonHelper;
	private final FromJsonHelper fromJsonHelper;
	
	@Autowired
	public BranchesAccountApi(BranchesAccountReadPlatformService branchesAccountReadPlatformService,
			BranchesAccountWritePlatformService branchesAccountWritePlatformService,
			final DefaultToApiJsonSerializer<BranchesAccountData> defaultToApiJsonHelper, 
			final FromJsonHelper fromJsonHelper) {
		this.branchesAccountReadPlatformService = branchesAccountReadPlatformService;
		this.branchesAccountWritePlatformService = branchesAccountWritePlatformService;
		this.defaultToApiJsonHelper = defaultToApiJsonHelper;
		this.fromJsonHelper = fromJsonHelper;
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Bank Account Details", description = "Retrieve all Bank Account Details. \n" )
	public String retrieveBranchesData()
	{
		final Collection<BranchesAccountData> branchesData =  this.branchesAccountReadPlatformService.retrieveBranchesData();
		return this.defaultToApiJsonHelper.serialize(branchesData);
	}
	
	@GET
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation( description = "Retrieve all Bank Details by Branch id. \n" )
	public String retrieveBranchesById(@PathParam("id") @Parameter(description = "branchId") final Long id) {
		final Collection<BranchesAccountData> branchesData = this.branchesAccountReadPlatformService.retrieveBranchesById(id);
		return this.defaultToApiJsonHelper.serialize(branchesData);
		
	}
	
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Adding Branches Account Data", description ="Mapping branches account Data")
	public String createBranches(@Parameter (hidden=true) final String apiRequestAsBodyJson)
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestAsBodyJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.branchesAccountWritePlatformService.createBranches(parsedData);
		return this.defaultToApiJsonHelper.serialize(result);
		
	}
	
	@PUT
	@Path("/{branchId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Updating Branches", description = "update branches account details based on branch id. \n" )
	public String updateBranches(@Parameter (hidden = true) final String apiRequestAsBodyJson, 
			@PathParam("branchId") @Parameter(description= "branchId") final Long branchId) {
		JsonElement data = this.fromJsonHelper.parse(apiRequestAsBodyJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.branchesAccountWritePlatformService.updateBranches(branchId, parsedData);
		return this.defaultToApiJsonHelper.serialize(result);
	}
	
	@GET
	@Path("cashDetails")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "getting cash details from accglaccount")
	public String retriveCashesData() {
		final Collection<GLAccountData> accountData = this.branchesAccountReadPlatformService.retriveCashesData();
		return this.defaultToApiJsonHelper.serialize(accountData);
		
	}
	
	@GET
	@Path("bankDetails")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "getting cash details from accglaccount")
	public String retriveBanksData() {
		final Collection<GLAccountData> accountData = this.branchesAccountReadPlatformService.retriveBanksData();
		return this.defaultToApiJsonHelper.serialize(accountData);
		
	}
	
	
}
