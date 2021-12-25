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


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;


import com.google.gson.JsonObject;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.api.CodeValuesApiResource;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.codes.service.CodeReadPlatformService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsWinnerData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitCycleData;

import org.apache.fineract.portfolio.ChitGroup.service.ChitBidsReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitBidsWritePlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/chitgroup/{chitid}/bids")
@Component
@Scope("singleton")
@Tag(name = "ChitBids", description = "Allows you to create update and read the details bids for a chit group")
public class ChitBidsApi 
{
	private final Set<String> responseDataParameters = new HashSet<>(Arrays.asList("id", "name"));
	private final String resourceNameForPermissions = "CHITGROUP";
	private final PlatformSecurityContext context;
	private final ChitBidsReadPlatformService readPlatformService;
	 private final ChitGroupReadPlatformService chitGroupReadPlatformService;
	private final ChitBidsWritePlatformService writePlatformService;
	private final DefaultToApiJsonSerializer<ChitBidsData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<ChitBidsWinnerData> toApiJsonSerializerBidWinner;
	private final FromJsonHelper fromJsonHelper;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final CodeReadPlatformService codeReadPlatformService;
	private final CodeValuesApiResource codeValuesApiResource;


	@Autowired
	public ChitBidsApi(final PlatformSecurityContext context,final ChitBidsReadPlatformService readPlatformService,final ChitBidsWritePlatformService writePlatformService,
			final DefaultToApiJsonSerializer<ChitBidsData> toApiJsonSerializer, final DefaultToApiJsonSerializer<ChitBidsWinnerData> toApiJsonSerializerBidWinner, final FromJsonHelper fromJsonHelper, final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final OfficeReadPlatformService officeReadPlatformService,
			final CodeValueReadPlatformService codeValueReadPlatformService,final CodeReadPlatformService codeReadPlatformService,final CodeValuesApiResource codeValuesApiResource,final ChitGroupReadPlatformService chitGroupReadPlatformService)
	{
		this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.context=context;
		this.readPlatformService = readPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.toApiJsonSerializerBidWinner = toApiJsonSerializerBidWinner;
		this.fromJsonHelper = fromJsonHelper;
		this.writePlatformService = writePlatformService ;
		this.officeReadPlatformService = officeReadPlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.codeReadPlatformService = codeReadPlatformService;
		this.codeValuesApiResource = codeValuesApiResource;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
	}

	@GET
	@Path("/cycle/{cycleNumber}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all chitgroup bids", description = "Retrieve all chitgroup bids.\n" )
	public String retrieveAll(@Context final UriInfo uriInfo,
			@PathParam("chitid") @Parameter final Long chitId, @PathParam("cycleNumber") @Parameter final Long cycleNumber) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		//			final PaginationParameters parameters = PaginationParameters.instance(paged, offset, limit, orderBy, sortOrder);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

		//final SearchParameters searchParameters = SearchParameters.forChitGroups(officeId, staffId, status, name, offset, limit, orderBy, sortOrder);

		final Collection<ChitBidsData> ChitBids = this.readPlatformService.retrieveAll(chitId, cycleNumber);
		return this.toApiJsonSerializer.serialize(settings, ChitBids, this.responseDataParameters);
	}
	@GET
	@Path("/cycle/{cycleNumber}/winner")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve winner details for a chit cycle", description = "Retrieve winner details for a chit cycle.\n" )
	public String getChitWinnerData(@Context final UriInfo uriInfo,
			@PathParam("chitid") @Parameter final Long chitId, @PathParam("cycleNumber") @Parameter final Long cycleNumber) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		final ChitBidsWinnerData chitBidsWinnerData = this.readPlatformService.getChitWinnerData(chitId, cycleNumber);
		return this.toApiJsonSerializerBidWinner.serialize(settings, chitBidsWinnerData, this.responseDataParameters);
	}



	@GET
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit Bid", description = "Returns the details of a Chit Bid.\n" + "\n" + "Example Requests:\n"
			+ "\n" + "/chitgroup/{chitid}/bids/1")
	public String retrieveOne(@PathParam("id") @Parameter(description = "id") final Long id,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

		ChitBidsData chit = this.readPlatformService.retrieveChitBids(id);
		// if (settings.isTemplate()) {
		// 	 final Collection<OfficeData> allowedOffices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
		//     chit = ChitBidsData.templateData(chit,allowedOffices);
		// }
		return this.toApiJsonSerializer.serialize(settings, chit, this.responseDataParameters);

	}
	
	@GET
	@Path("/ChitGroupCycle/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit Group Cycle", description = "Returns the details of a Chit Group Cycle.\n" + "\n" + "Example Requests:\n"
			+ "\n" + "/chitgroup/{chitid}/bids/1")
	public String retrieveOneChitGroupCycle(@PathParam("id") @Parameter(description = "id") final Long chitid,@PathParam("cycleNumber") @Parameter final Long cycleNumber,
			@Context final UriInfo uriInfo) {
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		Long cyclenumber = chitGroupReadPlatformService.retrieveChitGroup(chitid).getCurrentcycle();
		ChitCycleData chitCycle = null;
		if(chitGroupReadPlatformService.retrieveChitGroup(chitid).getStatus().compareTo(20l)==0)
		{
			
			chitCycle = this.readPlatformService.getChitCycle(chitid, cyclenumber);
		}
	

		return this.toApiJsonSerializer.serialize(chitCycle);

	}


	@POST
	@Path("/cycle/{cycleNumber}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a Chit Bid", description = "Creates a Chit Bid")
	@RequestBody(required = true )
	public String create(@Parameter(hidden = true) final String apiRequestBodyAsJson, @PathParam("chitid") @Parameter final Long chitid,
			@PathParam("cycleNumber") @Parameter final Long cycleNumber)
	{
		// Get ChitCycle ID and set in JSON
		JsonObject parsedJson = this.fromJsonHelper.parse(apiRequestBodyAsJson).getAsJsonObject();
		ChitCycleData chitCycle = this.readPlatformService.getChitCycle(chitid, cycleNumber);
		if(chitCycle!=null)
		{
			parsedJson.addProperty("chitCycleId", chitCycle.getId());
		}
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createChitBids().withJson(parsedJson.toString()).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	@POST
	@Path("/{chitid}/chitGroupCycle")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a Chit Group Cycle", description = "Creates a Chit Group Cycle")
	@RequestBody(required = true )
	public String createChitGroupCycle(@Parameter(hidden = true) final String apiRequestBodyAsJson, @PathParam("chitid") @Parameter final Long chitid,
			@PathParam("cycleNumber") @Parameter final Long cycleNumber)
	{

		JsonObject parsedJson = this.fromJsonHelper.parse(apiRequestBodyAsJson).getAsJsonObject();
		final CommandProcessingResult result = this.writePlatformService.createChitGroupCycle(chitid, parsedJson);

		return this.toApiJsonSerializer.serialize(result);
	}

	@PUT
	@Path("/{id}/chitGroupCycle")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Update a Chit Bid", description = "Updates a Chit Bid")
	@RequestBody(required = true)
	public String updateChitGroupCycle(@PathParam("id") @Parameter final Long id,
			@Parameter(hidden = true) final String apiRequestBodyAsJson) {

		JsonObject parsedJson = this.fromJsonHelper.parse(apiRequestBodyAsJson).getAsJsonObject();
		final CommandProcessingResult result = this.writePlatformService.updateChitGroupCycle(id, parsedJson);

		return this.toApiJsonSerializer.serialize(result);
	}

	@PUT
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Update a Chit Bid", description = "Updates a Chit Bid")
	@RequestBody(required = true)
	public String update(@PathParam("id") @Parameter final Long id,
			@Parameter(hidden = true) final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateChitBids(id).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete a Chit Bid")
	public String delete(@PathParam("id") @Parameter  final Long id) {	

		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteChitBids(id).build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);

	}
	
	
	@GET
	@Path("/templates/{codeName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getTemplateForBids(@Context final UriInfo uriInfo,@PathParam("codeName") @Parameter  final String codeName)
	{
		//Collection<CodeValueData> code = codeValueReadPlatformService.retrieveCodeValuesByCode(codeName);
		CodeData codevalues = codeReadPlatformService.retriveCode(codeName);
		
		
		return codeValuesApiResource.retrieveAllCodeValues(uriInfo, codevalues.getCodeId());
	}
	
	
}


