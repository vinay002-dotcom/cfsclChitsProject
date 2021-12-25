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
package org.apache.fineract.infrastructure.pincode.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.pincode.services.PincodeReadPlatformServices;
import org.apache.fineract.infrastructure.pincode.data.PincodeData;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.ChitGroup.api.ChitGroupApiSwagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@SuppressWarnings("unused")
@Path("/pincode")
@Component
@Scope("singleton")

public class PincodeApiResources 
{
	 private final Set<String> responseDataParameters = new HashSet<>(Arrays.asList("Areaname", "division","region","circle","taluk","district","state","talukaid","districtid","stateid"));
	 private final String resourceNameForPermissions = "PINCODE";
    private final PlatformSecurityContext context;
    private final PincodeReadPlatformServices readPlatformService;
    private final DefaultToApiJsonSerializer<PincodeData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public PincodeApiResources(PlatformSecurityContext context, PincodeReadPlatformServices readPlatformService,
			DefaultToApiJsonSerializer<PincodeData> toApiJsonSerializer,
			ApiRequestParameterHelper apiRequestParameterHelper,
			PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
		super();
		this.context = context;
		this.readPlatformService = readPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	}
    
    @GET
    @Path("{pincode}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Codes", description = "Returns the list of codes.\n" + "\n" + "Example Requests:\n" + "\n" + "codes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChitGroupApiSwagger.RetrieveOneResponse.class)))) })
    public String retrievepinCodes(@PathParam("pincode") @Parameter(description = "pincode") final Long pincode,@Context final UriInfo uriInfo) {

    	////System.out.println("pincode "+pincode);

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        
        Page<PincodeData> pincodeDat = this.readPlatformService.retriveaddress(pincode);
       
        return this.toApiJsonSerializer.serialize(settings, pincodeDat, this.responseDataParameters);
    }

	
  
}
