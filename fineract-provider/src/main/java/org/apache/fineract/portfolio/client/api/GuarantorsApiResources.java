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
package org.apache.fineract.portfolio.client.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;


import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.data.GurantorData;
import org.apache.fineract.portfolio.client.service.GuarantoReadPlatformService;
import org.apache.fineract.portfolio.client.service.GuarantorWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/guarantor")
@Component
@Scope("singleton")
@Tag(name = "ChitCharge", description = "Allows you to create update and read the details Chit charge")
public class GuarantorsApiResources 
{
	private final GuarantoReadPlatformService guarantoReadPlatformService ;
	private final GuarantorWritePlatformService guarantorWritePlatformService ;
	private final DefaultToApiJsonSerializer<GurantorData> toApiJsonSerializer;
	private final FromJsonHelper fromJsonHelper;
	@Autowired
	public GuarantorsApiResources(GuarantoReadPlatformService guarantoReadPlatformService,
			GuarantorWritePlatformService guarantorWritePlatformService,final DefaultToApiJsonSerializer<GurantorData> toApiJsonSerializer,final FromJsonHelper fromJsonHelper) 
	{
		
		this.guarantoReadPlatformService = guarantoReadPlatformService;
		this.guarantorWritePlatformService = guarantorWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.fromJsonHelper = fromJsonHelper;
	}
	
	@GET
	@Path("/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Guaranotrs of a client", description = "Retrieve all Guaranotrs of a client")
	public String retrieveAll(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId) {
		final Collection<GurantorData> guarantor = this.guarantoReadPlatformService.RetrievGuarantorClientWise(clientId);
		return this.toApiJsonSerializer.serialize(guarantor);
	}
	
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a Guarantor", description = "Create a Guarantor" )
	public String createGuarantor(@Parameter(hidden = true) final String apiRequestBodyAsJson) 
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.guarantorWritePlatformService.AddGuarantor(parsedData);
		
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Update a Guarantor", description = "Update a Guarantor" )
	public String updateGuarantor(@Parameter(hidden = true) final String apiRequestBodyAsJson,@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.guarantorWritePlatformService.UpdateGuarantor(parsedData,id);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
	@DELETE
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Delete a Guarantor", description = "Delete a Guarantor" )
	public String deleteGuarantor(@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		
		final CommandProcessingResult result = this.guarantorWritePlatformService.deleteGuarantor(id);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
}
