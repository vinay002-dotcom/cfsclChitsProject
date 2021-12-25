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
package org.apache.fineract.portfolio.ChitGroup.api;

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

import org.apache.fineract.portfolio.ChitGroup.data.ChitChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupDropDownData;
import org.apache.fineract.portfolio.ChitGroup.service.ChitChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitChargeWritePlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberChargeReadPlatformServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/chitcharge")
@Component
@Scope("singleton")
@Tag(name = "ChitCharge", description = "Allows you to create update and read the details Chit charge")
public class ChitChargeApi 
{
	private final ChitChargeReadPlatformServices chitChargeReadPlatformServices ;
	private final ChitChargeWritePlatformService chitChargeWritePlatformService ;
	private final DefaultToApiJsonSerializer<ChitChargeData> toApiJsonSerializer;
	private final FromJsonHelper fromJsonHelper;
	private final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices;
	@Autowired
	public ChitChargeApi(ChitChargeReadPlatformServices chitChargeReadPlatformServices,
			ChitChargeWritePlatformService chitChargeWritePlatformService,final DefaultToApiJsonSerializer<ChitChargeData> toApiJsonSerializer,final FromJsonHelper fromJsonHelper,final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices) 
	{
		
		this.chitChargeReadPlatformServices = chitChargeReadPlatformServices;
		this.chitChargeWritePlatformService = chitChargeWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.fromJsonHelper = fromJsonHelper;
		this.chitSubscriberChargeReadPlatformServices = chitSubscriberChargeReadPlatformServices;
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Charge bids", description = "Retrieve all Chit Charge bids.\n" )
	public String retrieveAll() {
		final Collection<ChitChargeData> ChitCharge = this.chitChargeReadPlatformServices.retrieveAll();
		return this.toApiJsonSerializer.serialize(ChitCharge);
	}
	
	
	
	@GET
	@Path("/dropdown/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Charge bids", description = "Retrieve all Chit Charge bids.\n" )
	public String retrieveDatafordropdown(@PathParam("id") @Parameter(description = "id") final Long id) {
		
		final Collection<ChitGroupDropDownData> ChitCharge = this.chitSubscriberChargeReadPlatformServices.retrieveChitDataForDropDown(id);
		return this.toApiJsonSerializer.serialize(ChitCharge);
	}
	
	@GET
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Charge bids", description = "Retrieve all Chit Charge bids.\n" )
	public String retrieveNamebyId(@PathParam("id") @Parameter(description = "id") final Long id) {
		
		final ChitChargeData ChitCharge = this.chitChargeReadPlatformServices.retrieveNameById(id);
		return this.toApiJsonSerializer.serialize(ChitCharge);
	}
	
//	@GET
//	@Path("{name}")
//	@Consumes({ MediaType.APPLICATION_JSON })
//	@Produces({ MediaType.APPLICATION_JSON })
//	@Operation(summary = "Retrieve all Chit Charge bids", description = "Retrieve all Chit Charge bids.\n" )
//	public String retrieveIdbyName(@PathParam("name") @Parameter(description = "name") final String name) 
//	{
//		
//		final ChitChargeData ChitCharge = this.chitChargeReadPlatformServices.retrieveIdByName(name);
//		return this.toApiJsonSerializer.serialize(ChitCharge);
//	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Charge bids", description = "Retrieve all Chit Charge bids.\n" )
	public String createchitcharge(@Parameter(hidden = true) final String apiRequestBodyAsJson) 
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.chitChargeWritePlatformService.createChitCharge(parsedData);
		
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Charge bids", description = "Retrieve all Chit Charge bids.\n" )
	public String updateChitCharge(@Parameter(hidden = true) final String apiRequestBodyAsJson,@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.chitChargeWritePlatformService.updateChitCharge(id, parsedData);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
	@DELETE
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Charge bids", description = "Retrieve all Chit Charge bids.\n" )
	public String updateChitCharge(@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		
		final CommandProcessingResult result = this.chitChargeWritePlatformService.deleteChitCharge(id);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
}
