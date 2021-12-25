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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberTransactionData;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberTransactionReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberTransactionWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/chitsubscriptionTransaction")
@Component
@Scope("singleton")
@Tag(name = "ChitCharge", description = "Allows you to create update and read the details Chit charge")
public class ChitSubscriberTransactionApi 
{
	private final ChitSubscriberTransactionReadPlatformService chitSubsTranReadPlatformServices ;
	private final ChitSubscriberTransactionWritePlatformService chitChargeWritePlatformService ;
	private final DefaultToApiJsonSerializer<ChitSubscriberChargeData> toApiJsonSerializer;
	private final FromJsonHelper fromJsonHelper;
	
	@Autowired
	public ChitSubscriberTransactionApi(ChitSubscriberTransactionReadPlatformService chitSubsTranReadPlatformServices,
			ChitSubscriberTransactionWritePlatformService chitChargeWritePlatformService,final DefaultToApiJsonSerializer<ChitSubscriberChargeData> toApiJsonSerializer,final FromJsonHelper fromJsonHelper) 
	{
		
		this.chitSubsTranReadPlatformServices = chitSubsTranReadPlatformServices;
		this.chitChargeWritePlatformService = chitChargeWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.fromJsonHelper = fromJsonHelper;
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Subscriber charge ", description = "Retrieve all Chit Charge bids.\n" )
	public String retrieveAll() {
		final Collection<ChitSubscriberTransactionData> ChitCharge = this.chitSubsTranReadPlatformServices.retrieveAll();
		return this.toApiJsonSerializer.serialize(ChitCharge);
	}
	
	@GET
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve  Chit Subscriber Charge ", description = "Retrieve  Chit Subscriber Charge bids.\n" )
	public String retrieveNamebyId(@PathParam("id") @Parameter(description = "id") final Long id) {
		
		final ChitSubscriberTransactionData ChitCharge = this.chitSubsTranReadPlatformServices.retrieveById(id);
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
//		final ChitChargeData ChitCharge = this.chitSubsTranReadPlatformServices.retrieveIdByName(name);
//		return this.toApiJsonSerializer.serialize(ChitCharge);
//	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "create Chit Subscriber Charge", description = "create Chit Subscriber Charge.\n" )
	public String createchitcharge(@Parameter(hidden = true) final String apiRequestBodyAsJson) 
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.chitChargeWritePlatformService.createSubscriberChitCharge(parsedData);
		
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@POST
	@Path("/collectionprocessing")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Update Chits Subscriber Transaction", description = "Update Chits Subscriber Transaction.\n" )
	public String updateChitsSubscriberTransactionProcess(@Parameter(hidden = true) final String apiRequestBodyAsJson)
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonArray parsedData = data.getAsJsonArray();
		for(int i=0; i<parsedData.size(); i++)
		{
			JsonObject jsonobject = parsedData.get(i).getAsJsonObject();
			Long id= jsonobject.get("id").getAsLong();
			jsonobject.remove("id");
			
			final CommandProcessingResult result = this.chitChargeWritePlatformService.collectionProcessing(id,jsonobject);
			
		}
		JsonObject resp = new JsonObject();
		resp.addProperty("status", "success");
		return this.toApiJsonSerializer.serialize(resp);
	
	}
	
	@PUT
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "update Chit Subscriber Charge", description = "update  Chit Subscriber Charge bids.\n" )
	public String updateChitCharge(@Parameter(hidden = true) final String apiRequestBodyAsJson,@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.chitChargeWritePlatformService.updateChitSubscriberCharge(id, parsedData);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
	@DELETE
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "delete  Chit Subscriber Charge ", description = "delete  Chit Subscriber Charge .\n" )
	public String updateChitCharge(@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		
		final CommandProcessingResult result = this.chitChargeWritePlatformService.deleteChitSubscriberCharge(id);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}

	@GET
	@Path("/agentcollections/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve collections done by Agent ", description = "Retrieve collections done by Agent.\n" )
	public String retrieveCollectionsbyAgentId(@PathParam("id") @Parameter(description = "id") final Long agentId,
		@QueryParam("date") @Parameter(description = "date") final String sDate) {
		return this.toApiJsonSerializer.serialize(this.chitSubsTranReadPlatformServices.retrieveUnProcessedCollectionsByAgentByDate(agentId, sDate));
	} 
	
	
	@GET
	@Path("/agentcollectionsforcharges/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve collections done by Agent ", description = "Retrieve collections done by Agent.\n" )
	public String retrieveCollectionsbyAgentIdForCharges(@PathParam("id") @Parameter(description = "id") final Long agentId,
		@QueryParam("date") @Parameter(description = "date") final String sDate) {
		return this.toApiJsonSerializer.serialize(this.chitSubsTranReadPlatformServices.retrieveUnProcessedCollectionsByAgentByDateForCharges(agentId, sDate));
	} 
	
}
