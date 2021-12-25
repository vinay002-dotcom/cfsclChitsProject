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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.data.ClientTransactionData;
import org.apache.fineract.portfolio.client.service.ClientTransactionBulkApproval;
import org.apache.fineract.portfolio.client.service.ClientTransactionReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientTransactionWritePlatformServiceJpaRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Path("/clients/{clientId}/transactions")
@Component
@Tag(name = "Client Transaction", description = "Client Transactions refer to transactions made directly againt a Client's internal account. Currently, these transactions are only created as a result of charge payments/waivers. You are allowed to undo a transaction, however you cannot explicitly create one. ")
public class ClientTransactionsApiResource {

    private final PlatformSecurityContext context;
    private final ClientTransactionReadPlatformService clientTransactionReadPlatformService;
    private final DefaultToApiJsonSerializer<ClientTransactionData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final FromJsonHelper fromJsonHelper;
    private final ClientTransactionWritePlatformServiceJpaRepositoryImpl clientTransactionWritePlatformServiceJpaRepositoryImpl;
    private final ClientTransactionBulkApproval clientTransactionBulkApproval;

    @Autowired
    public ClientTransactionsApiResource(final PlatformSecurityContext context,
            final ClientTransactionReadPlatformService clientTransactionReadPlatformService,
            final DefaultToApiJsonSerializer<ClientTransactionData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final FromJsonHelper fromJsonHelper,final ClientTransactionWritePlatformServiceJpaRepositoryImpl clientTransactionWritePlatformServiceJpaRepositoryImpl,
            final ClientTransactionBulkApproval clientTransactionBulkApproval) {
        this.context = context;
        this.clientTransactionReadPlatformService = clientTransactionReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.clientTransactionWritePlatformServiceJpaRepositoryImpl = clientTransactionWritePlatformServiceJpaRepositoryImpl;
        this.fromJsonHelper = fromJsonHelper;
        this.clientTransactionBulkApproval = clientTransactionBulkApproval;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "List Client Transactions", description = "The list capability of client transaction can support pagination."
            + "\n\n" + "Example Requests:\n\n" + "clients/189/transactions\n\n" + "clients/189/transactions?offset=10&limit=50")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientTransactionsApiResourceSwagger.GetClientsClientIdTransactionsResponse.class))) })
    public String retrieveAllClientTransactions(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,
            @Context final UriInfo uriInfo, @QueryParam("offset") @Parameter(description = "offset") final Integer offset,
            @QueryParam("limit") @Parameter(description = "limit") final Integer limit) {
        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME);

        SearchParameters searchParameters = SearchParameters.forPagination(offset, limit);
        final Page<ClientTransactionData> clientTransactions = this.clientTransactionReadPlatformService.retrieveAllTransactions(clientId,
                searchParameters);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientTransactions,
                ClientApiConstants.CLIENT_TRANSACTION_RESPONSE_DATA_PARAMETERS);
    }
    
    
    @GET
    @Path("/getclientTransactionsforapproval/{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "List Client Transactions", description = "The list capability of client transaction can support pagination."
            + "\n\n" + "Example Requests:\n\n" + "clients/189/transactions\n\n" + "clients/189/transactions?offset=10&limit=50")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientTransactionsApiResourceSwagger.GetClientsClientIdTransactionsResponse.class))) })
    public String retrieveAllClientTransactionsForBulkApproval(@PathParam("id") @Parameter(description = "id") final Long agentId,
    		@QueryParam("date") @Parameter(description = "date") final String sDate) {
        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME);

     //   SearchParameters searchParameters = SearchParameters.forPagination(offset, limit);
        final Collection<JsonElement> clientTransactions = this.clientTransactionReadPlatformService.retrieveAllTransactionforBulkApproval(agentId,sDate);

      
        return this.toApiJsonSerializer.serialize(clientTransactions);
    }
    
    @GET
    @Path("{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve a Client Transaction", description = "Example Requests:\n" + "clients/1/transactions/1\n" + "\n" + "\n"
            + "clients/1/transactions/1?fields=id,officeName")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientTransactionsApiResourceSwagger.GetClientsClientIdTransactionsTransactionIdResponse.class))) })
    public String retrieveClientTransaction(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,
            @PathParam("transactionId") @Parameter(description = "transactionId") final Long transactionId,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME);

        final ClientTransactionData clientTransaction = this.clientTransactionReadPlatformService.retrieveTransaction(clientId,
                transactionId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientTransaction,
                ClientApiConstants.CLIENT_TRANSACTION_RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Undo a Client Transaction", description = "Undoes a Client Transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientTransactionsApiResourceSwagger.PostClientsClientIdTransactionsTransactionIdResponse.class))) })
    public String undoClientTransaction(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,
            @PathParam("transactionId") @Parameter(description = "transactionId") final Long transactionId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        String json = "";
        if (is(commandParam, ClientApiConstants.CLIENT_TRANSACTION_COMMAND_UNDO)) {
            final CommandWrapper commandRequest = new CommandWrapperBuilder().undoClientTransaction(clientId, transactionId)
                    .withJson(apiRequestBodyAsJson).build();

            final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

            json = this.toApiJsonSerializer.serialize(result);
        } else {
            throw new UnrecognizedQueryParamException("command", commandParam, ClientApiConstants.CLIENT_TRANSACTION_COMMAND_UNDO);
        }
        
        return json;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a Client Transaction used for payment before assigning to Chit Group", description = "Create a Client Transaction used for payment before assigning to Chit Group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientTransactionsApiResourceSwagger.PostClientsClientIdTransactionsTransactionIdResponse.class))) })
    public String createClientTransaction(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {
    	
//    	////System.out.println("advance transaction ");
//    	////System.out.println(apiRequestBodyAsJson);
        String json = "";
        if (is(commandParam, ClientApiConstants.CLIENT_TRANSACTION_COMMAND_CHITADVANCE)) {
            final CommandWrapper commandRequest = new CommandWrapperBuilder().createClientTranChitAdvance(clientId)
                    .withJson(apiRequestBodyAsJson).build();
            final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

            json = this.toApiJsonSerializer.serialize(result);
        } else {
            throw new UnrecognizedQueryParamException("command", commandParam, ClientApiConstants.CLIENT_TRANSACTION_COMMAND_CHITADVANCE);
        }

        return json;
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
    
    
    @PUT
    @Path("/bulkapproval")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String bulkApproval(@Parameter(hidden = true) final String apiRequestBodyAsJson)
    {
    	Long clientId = null;
    	Long tranId = null;
    	Boolean value = false;
    	JsonElement parse = fromJsonHelper.parse(apiRequestBodyAsJson);
    	JsonArray parsedData = parse.getAsJsonArray();
    	JsonObject dataToBeUpdated = new JsonObject();
    	for(int i = 0 ; i < parsedData.size() ; i++)
    	{
    		JsonElement data = parsedData.get(i);
    		JsonObject ObjectData = data.getAsJsonObject();
    		
    		if(ObjectData.get("clientId")!=null && !ObjectData.get("clientId").isJsonNull())
    		{
    			clientId = ObjectData.get("clientId").getAsLong();
    		}
    		
    		if(ObjectData.get("tranId")!=null && !ObjectData.get("tranId").isJsonNull())
    		{
    			tranId = ObjectData.get("tranId").getAsLong();
    		}
    		
    		if(ObjectData.get("isprocessed")!=null && !ObjectData.get("isprocessed").isJsonNull())
    		{
    			value = ObjectData.get("isprocessed").getAsBoolean();
    		
        		dataToBeUpdated.addProperty("isprocessed", value);
        		
        		////System.out.println(i+"ith time "+value);
    		}
    		
    		if(clientId!=null && tranId!=null && value)
    		{
    			this.clientTransactionBulkApproval.BulkApproval(clientId, tranId, dataToBeUpdated);
    		}
    		
    	}
    	
    	JsonObject resp = new JsonObject();
    	resp.addProperty("status", "Success");
    	
    	return resp.toString();
    }
    
}
