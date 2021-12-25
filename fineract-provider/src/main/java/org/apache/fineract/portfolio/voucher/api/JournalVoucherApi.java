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
package org.apache.fineract.portfolio.voucher.api;


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.apache.fineract.accounting.journalentry.api.DateParam;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.accounting.journalentry.data.JournalEntryData;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.voucher.data.JournalVoucherData;
import org.apache.fineract.portfolio.voucher.data.JournalVoucherEntriesData;
import org.apache.fineract.portfolio.voucher.service.JournalVoucherReadPlatformService;
import org.apache.fineract.portfolio.voucher.service.JournalVoucherWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/voucher")

@Component
@Scope(value="singleton")
public class JournalVoucherApi {
	
	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("id", "officeId", "officeName", "glAccountName", "glAccountId", "glAccountCode", "glAccountType",
                    "transactionDate", "entryType", "amount", "transactionId","referenceNumber", "manualEntry", "entityType", "entityId", "createdByUserId",
                    "createdDate", "createdByUserName", "comments", "reversed", "referenceNumber", "currency", "transactionDetails"));

    private final String resourceNameForPermission = "JOURNALENTRY";
    
    
	private final Set<String> responseDataParameters = new HashSet<>(Arrays.asList("id", "voucherName"));
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromJsonHelper;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final JournalVoucherWritePlatformService journalVoucherWritePlatformService;
	private final JournalVoucherReadPlatformService journalVoucherReadPlatformService;
	private final DefaultToApiJsonSerializer<JournalVoucherData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<JournalVoucherEntriesData> toApiSerializer;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final DefaultToApiJsonSerializer<Object> apiJsonSerializerService;
	
	
	@Autowired
	JournalVoucherApi(final PlatformSecurityContext context,
			final FromJsonHelper fromJsonHelper,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final JournalVoucherWritePlatformService journalVoucherWritePlatformService,
			final DefaultToApiJsonSerializer<JournalVoucherData> toApiJsonSerializer,
			final DefaultToApiJsonSerializer<JournalVoucherEntriesData> toApiSerializer,
			final CodeValueReadPlatformService codeValueReadPlatformService,
			final DefaultToApiJsonSerializer<Object> apiJsonSerializerService,
			final JournalVoucherReadPlatformService journalVoucherReadPlatformService){
		this.context = context;
		this.fromJsonHelper=fromJsonHelper;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.journalVoucherWritePlatformService = journalVoucherWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.toApiSerializer = toApiSerializer;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.apiJsonSerializerService = apiJsonSerializerService;
		this.journalVoucherReadPlatformService = journalVoucherReadPlatformService;
	}
	
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createJournalVoucher(@Parameter(hidden=true)String apiRequestBodyAsJson) {
		JsonObject element = this.fromJsonHelper.parse(apiRequestBodyAsJson).getAsJsonObject();
		this.journalVoucherWritePlatformService.createJournalVoucher(element);
		JsonObject data = new JsonObject();
		data.addProperty("status", "success");
		return data.toString();
	}
	
	@GET
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String getJournalVoucherDetails(@PathParam("id") @Parameter final Long id) {
		System.out.println("001");
		List<JournalVoucherEntriesData> data = this.journalVoucherReadPlatformService.getJournalVoucher(id);
		return this.toApiJsonSerializer.serialize(data);
	}
	
	@PUT
	@Path("/updateVoucher/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateJournalVoucher(@PathParam("id") @Parameter final Long id,@Parameter(hidden=true)String apiRequestBodyAsJson) {
		JsonObject obj = this.fromJsonHelper.parse(apiRequestBodyAsJson).getAsJsonObject();
		this.journalVoucherWritePlatformService.updateJournalVoucher(id, obj);
		JsonObject sta = new JsonObject();
		sta.addProperty("status", "success");
		return obj.toString();
	}
	
	@DELETE
	@Path("/deleteVoucher/{id}")
	public String deleteVoucher(@PathParam("id") @Parameter final Long id) throws Exception {
		try {
			System.out.println("001");
			this.journalVoucherReadPlatformService.deleteJournalVoucher(id);
			JsonObject obb = new JsonObject();
			obb.addProperty("status", "success");
			return obb.toString();
		} catch (Exception e) {
			throw new Exception (e);
		}
	}
	
	@GET
	@Path("/getCodeValue/{codeName}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String getCodeValues(@PathParam("codeName") @Parameter final String codeName) {
		Collection<CodeValueData> obbj = this.codeValueReadPlatformService.retrieveCodeValuesByCode(codeName);	
		return this.toApiJsonSerializer.serialize(obbj);
	}
	
	@POST
	@Path("/voucherTypeNumber/{id}")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String getJournalVoucherNumber(@PathParam("id") @Parameter final Integer id) {
		JsonObject object =new JsonObject();
		String data = this.journalVoucherWritePlatformService.incrementVoucherNumber(id);
		object.addProperty("voucherNumber", data);
		return object.toString();
	}
	
	    @GET
	    @Path("/voucherJournalEntries")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    @Operation(summary = "List Journal Entries", description = "The list capability of journal entries can support pagination and sorting.\n\n"
	            + "Example Requests:\n" + "\n" + "journalentries\n" + "\n" + "journalentries?transactionId=PB37X8Y21EQUY4S\n" + "\n"
	            + "journalentries?officeId=1&manualEntriesOnly=true&fromDate=1 July 2013&toDate=15 July 2013&dateFormat=dd MMMM yyyy&locale=en\n"
	            + "\n" + "journalentries?fields=officeName,glAccountName,transactionDate\n" + "\n" + "journalentries?offset=10&limit=50\n"
	            + "\n" + "journalentries?orderBy=transactionId&sortOrder=DESC\n" + "\n" + "journalentries?runningBalance=true\n" + "\n"
	            + "journalentries?transactionDetails=true\n" + "\n" + "journalentries?loanId=12\n" + "\n" + "journalentries?savingsId=24")
	    @ApiResponses({
	            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = JournalEntryData.class)))) })
	    public String retrieveAll(@Context final UriInfo uriInfo,
	            @QueryParam("officeId") @Parameter(description = "officeId") final Long officeId,
	            @QueryParam("glAccountId") @Parameter(description = "glAccountId") final Long glAccountId,
	            @QueryParam("manualEntriesOnly") @Parameter(description = "manualEntriesOnly") final Boolean onlyManualEntries,
	            @QueryParam("fromDate") @Parameter(description = "fromDate") final DateParam fromDateParam,
	            @QueryParam("toDate") @Parameter(description = "toDate") final DateParam toDateParam,
	            @QueryParam("transactionId") @Parameter(description = "transactionId") final String transactionId,
	            @QueryParam("referenceNumber") @Parameter(description = "referenceNumber") final String referenceNumber,
	            @QueryParam("entityType") @Parameter(description = "entityType") final Integer entityType,
	            @QueryParam("offset") @Parameter(description = "offset") final Integer offset,
	            @QueryParam("limit") @Parameter(description = "limit") final Integer limit,
	            @QueryParam("orderBy") @Parameter(description = "orderBy") final String orderBy,
	            @QueryParam("sortOrder") @Parameter(description = "sortOrder") final String sortOrder,
	            @QueryParam("locale") @Parameter(description = "locale") final String locale,
	            @QueryParam("dateFormat") @Parameter(description = "dateFormat") final String dateFormat,
	            @QueryParam("loanId") @Parameter(description = "loanId") final Long loanId,
	            @QueryParam("savingsId") @Parameter(description = "savingsId") final Long savingsId,
	            @QueryParam("runningBalance") @Parameter(description = "runningBalance") final boolean runningBalance,
	            @QueryParam("transactionDetails") @Parameter(description = "transactionDetails") final boolean transactionDetails) {

	        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);
	        
	     
	        Date fromDate = null;
	        if (fromDateParam != null) {
	            fromDate = fromDateParam.getDate("fromDate", dateFormat, locale);
	        }
	        Date toDate = null;
	        if (toDateParam != null) {
	            toDate = toDateParam.getDate("toDate", dateFormat, locale);
	        }

	        final SearchParameters searchParameters = SearchParameters.forJournalEntries(officeId, offset, limit, orderBy, sortOrder, loanId,
	                savingsId);
	        JournalEntryAssociationParametersData associationParametersData = new JournalEntryAssociationParametersData(transactionDetails,
	                runningBalance);

	        final Page<JournalVoucherEntriesData> glJournalEntries = this.journalVoucherReadPlatformService.retrieveAll(searchParameters, glAccountId,
	                onlyManualEntries, fromDate, toDate, transactionId,referenceNumber, entityType, associationParametersData);
	        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.apiJsonSerializerService.serialize(settings, glJournalEntries, RESPONSE_DATA_PARAMETERS);
	    }

}