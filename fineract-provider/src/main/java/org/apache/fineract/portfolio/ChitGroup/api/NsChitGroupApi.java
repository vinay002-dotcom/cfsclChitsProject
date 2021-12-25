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

import org.apache.fineract.portfolio.ChitGroup.data.NsChitGroupData;

import org.apache.fineract.portfolio.ChitGroup.service.NsChitGroupReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.NsChitGroupWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/NsChitGroup")
@Component
@Scope("singleton")
@Tag(name = "NsChitGroup", description = "Allows you to create a non started chitGroup")
public class NsChitGroupApi 
{
	private final NsChitGroupReadPlatformService nsChitGroupReadPlatformService ;
	private final NsChitGroupWritePlatformService nsChitGroupWritePlatformService ;
	private final DefaultToApiJsonSerializer<NsChitGroupData> toApiJsonSerializer;
	private final FromJsonHelper fromJsonHelper;
	@Autowired
	public NsChitGroupApi(NsChitGroupReadPlatformService nsChitGroupReadPlatformService,
			NsChitGroupWritePlatformService nsChitGroupWritePlatformService,
			DefaultToApiJsonSerializer<NsChitGroupData> toApiJsonSerializer, FromJsonHelper fromJsonHelper) {
		super();
		this.nsChitGroupReadPlatformService = nsChitGroupReadPlatformService;
		this.nsChitGroupWritePlatformService = nsChitGroupWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.fromJsonHelper = fromJsonHelper;
	}

	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieves all Non started chit group", description = "Retrieves all Non started chit group" )
	public String retrieveAll() {
		final JsonObject ChitGroup = this.nsChitGroupReadPlatformService.retrieveAll();
		return this.toApiJsonSerializer.serialize(ChitGroup);
	}
	
	@GET
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a given NS chit Group", description = "Retrieve a given NS chit Group" )
	public String RetrieveChitById(@PathParam("id") @Parameter(description = "id") final Long id) {
		
		final NsChitGroupData ChitGroup = this.nsChitGroupReadPlatformService.retrieveById(id);
		return this.toApiJsonSerializer.serialize(ChitGroup);
	}
	


	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a new NS chit Group", description = "Create a new NS chit Group" )
	public String CreateNewNsChitGroup(@Parameter(hidden = true) final String apiRequestBodyAsJson) 
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.nsChitGroupWritePlatformService.CreateChitGroup(parsedData);
		
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Update a NS chitGroup", description = "Update a NS chitGroup" )
	public String updateChitGroup(@Parameter(hidden = true) final String apiRequestBodyAsJson,@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.nsChitGroupWritePlatformService.updateChitGroup(id, parsedData);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
	@DELETE
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Delete a NS ChitGroup", description = "Delete a NS ChitGroup" )
	public String Delete(@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		
		final CommandProcessingResult result = this.nsChitGroupWritePlatformService.deleteChitGroup(id);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
}
