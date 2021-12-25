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

package org.apache.fineract.portfolio.creditreport.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;

import org.apache.fineract.portfolio.creditreport.data.CreditReport;
import org.apache.fineract.portfolio.creditreport.service.CreditReportsReadPlatformService;
import org.apache.fineract.portfolio.creditreport.service.CreditReportsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clients/{clientId}/creditreports")
@Component
@Scope("singleton")
public class CreditReportsApiResources {

    private final Set<String> responseDataParameters = new HashSet<>(Arrays.asList("id", "clientId", "bureau", "scoretype", "scorevalue",
            "scorecomments", "reportid", "dateofissue"));
    private final String resourceNameForPermissions = "CreditReports";
    private final PlatformSecurityContext context;
    private final CreditReportsReadPlatformService readPlatformService;
    private final ToApiJsonSerializer<CreditReport> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CreditReportsWritePlatformService creditreportswriteplatformservice;
    @Autowired
    public CreditReportsApiResources(final PlatformSecurityContext context,
            final CreditReportsReadPlatformService readPlatformService,
            final ToApiJsonSerializer<CreditReport> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final CreditReportsWritePlatformService creditreportswriteplatformservice) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.creditreportswriteplatformservice= creditreportswriteplatformservice;
    }


    
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getCreditreports(@Context final UriInfo uriInfo, @PathParam("clientId") final long clientId) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<CreditReport> familyMembers = this.readPlatformService.getreport(clientId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, familyMembers, this.responseDataParameters);

    }


    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String addCreditReport(@PathParam("clientId") final long clientid, final String apiRequestBodyAsJson) {

      
        final CommandProcessingResult result = this.creditreportswriteplatformservice.addCreditReport(clientid, apiRequestBodyAsJson);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String UpdateCreditReport(@PathParam("clientId") final long clientid, final String apiRequestBodyAsJson) {

      
        final CommandProcessingResult result = this.creditreportswriteplatformservice.updateCreditReport(clientid, apiRequestBodyAsJson);

        return this.toApiJsonSerializer.serialize(result);
    }

   

}
