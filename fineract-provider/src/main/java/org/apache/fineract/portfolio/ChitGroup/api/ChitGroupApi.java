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


import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.accounting.common.AccountingDropdownReadPlatformService;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.documentmanagement.api.FileUploadValidator;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;
import org.apache.fineract.portfolio.ChitGroup.service.ChitBidsReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupWritePlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ReportReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientStatementReadService;
import org.apache.fineract.portfolio.client.service.ClientTransactionReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/chitgroup")
@Component
@Scope("singleton")
@Tag(name = "ChitGroup", description = "Allows you to create update and read the details of chit group")
public class ChitGroupApi 
{
	private final Set<String> responseDataParameters = new HashSet<>(Arrays.asList("id", "name"));
	private final String resourceNameForPermissions = "CHITGROUP";
	private final PlatformSecurityContext context;
	private final ChitGroupReadPlatformService readPlatformService;
	private final ChitGroupWritePlatformServices writePlatformService;
	private final DefaultToApiJsonSerializer<ChitGroupData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<ChitGroupSubscriberData> toApiJsonSerializerSubscriber;
	private final DefaultToApiJsonSerializer<Map<String, List<GLAccountData>>> toApiJsonSerializerMap;
	private final FromJsonHelper fromJsonHelper;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final FileUploadValidator fileUploadValidator;
	private final DocumentWritePlatformService documentWritePlatformService;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService;
	private final ClientTransactionReadPlatformService clientTransactionReadPlatformService;
	private final ReportReadPlatformService reportReadPlatformService;
	private final ChitBidsReadPlatformService chitBidsReadPlatformService;
	private final ClientStatementReadService clientStatementReadService;
	@Autowired
	public ChitGroupApi(final PlatformSecurityContext context,final ChitGroupReadPlatformService readPlatformService,final ChitGroupWritePlatformServices writePlatformService,
			final DefaultToApiJsonSerializer<ChitGroupData> toApiJsonSerializer, final FromJsonHelper fromJsonHelper, final DefaultToApiJsonSerializer<ChitGroupSubscriberData> toApiJsonSerializerSubscriber, final DefaultToApiJsonSerializer<Map<String, List<GLAccountData>>> toApiJsonSerializerMap, final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final OfficeReadPlatformService officeReadPlatformService,
			final FileUploadValidator fileUploadValidator, final DocumentWritePlatformService documentWritePlatformService, final CodeValueReadPlatformService codeValueReadPlatformService, final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService,
			ClientTransactionReadPlatformService clientTransactionReadPlatformService,
			final ReportReadPlatformService reportReadPlatformService,final ChitBidsReadPlatformService chitBidsReadPlatformService,final ClientStatementReadService clientStatementReadService)
	{
		this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.context=context;
		this.readPlatformService = readPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.fromJsonHelper = fromJsonHelper;
		this.toApiJsonSerializerSubscriber = toApiJsonSerializerSubscriber;
		this.toApiJsonSerializerMap = toApiJsonSerializerMap;
		this.writePlatformService = writePlatformService ;
		this.officeReadPlatformService = officeReadPlatformService;
		this.fileUploadValidator = fileUploadValidator;
		this.documentWritePlatformService = documentWritePlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.accountingDropdownReadPlatformService = accountingDropdownReadPlatformService;
		this.clientStatementReadService =
				clientStatementReadService;
		this.clientTransactionReadPlatformService = clientTransactionReadPlatformService;
		this.reportReadPlatformService = reportReadPlatformService;
		this.chitBidsReadPlatformService = chitBidsReadPlatformService;
	}
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve chitgroup", description = "Returns the list of chitgroup.\n" )
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation =  ChitGroupApiSwagger.RetrieveOneResponse.class)))) })
	public String retrieveAll(@Context final UriInfo uriInfo,
			@QueryParam("name") @Parameter(description = "name") final String name,
			@QueryParam("status") @Parameter(description = "name") final String status,
			@QueryParam("officeId") @Parameter(description = "officeId") final Long officeId,
			@QueryParam("staffId") @Parameter(description = "staffId") final Long staffId,
			@QueryParam("paged") @Parameter(description = "paged") final Boolean paged,
			@QueryParam("offset") @Parameter(description = "offset") final Integer offset,
			@QueryParam("limit") @Parameter(description = "limit") final Integer limit,
			@QueryParam("orderBy") @Parameter(description = "orderBy") final String orderBy,
			@QueryParam("sortOrder") @Parameter(description = "sortOrder") final String sortOrder) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		final PaginationParameters parameters = PaginationParameters.instance(paged, offset, limit, orderBy, sortOrder);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

		final SearchParameters searchParameters = SearchParameters.forChitGroups(officeId, staffId, status, name, offset, limit, orderBy, sortOrder);

		if (parameters.isPaged()) {
			final Page<ChitGroupData> ChitGroup = this.readPlatformService.retrievePagedAll(searchParameters, parameters);
			return this.toApiJsonSerializer.serialize(settings, ChitGroup, this.responseDataParameters);
		}
		final Collection<ChitGroupData> ChitGroup = this.readPlatformService.retrieveAll(searchParameters, parameters);
		return this.toApiJsonSerializer.serialize(settings, ChitGroup, this.responseDataParameters);
	}


	@GET
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit group", description = "Returns the details of a Chit group.\n" + "\n" + "Example Requests:\n"
			+ "\n" + "chitgroup/1")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ChitGroupApiSwagger.RetrieveOneResponse.class))) })
	public String retrieveOne(@PathParam("id") @Parameter(description = "id") final Long id,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

		ChitGroupData chit = this.readPlatformService.retrieveChitGroup(id);
		if (settings.isTemplate()) {
			final Collection<OfficeData> allowedOffices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
			chit = ChitGroupData.templateData(chit,allowedOffices);
		}
		return this.toApiJsonSerializer.serialize(settings, chit, this.responseDataParameters);
	}

	@GET
	@Path("/clientsStatement/{id}/{chitgroup}/{ticketnum}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit group", description = "Returns the details of a Chit group.\n" + "\n" + "Example Requests:\n"
			+ "\n" + "chitgroup/1")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ChitGroupApiSwagger.RetrieveOneResponse.class))) })
	public String retrieveStatement(@PathParam("id") @Parameter(description = "id") final Long id,
			@PathParam("chitgroup") @Parameter(description = "chitgroup") final Long chitgroup,@PathParam("ticketnum") @Parameter(description = "ticketnum") final Long ticketnum,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

		List<JsonObject> chit = clientStatementReadService.memberstatement(id,chitgroup,ticketnum);
		return this.toApiJsonSerializer.serialize(chit);
	}
	
	
	@GET
	@Path("/DashBoard/{clientid}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit group", description = "Returns the details of a Chit group.\n" + "\n" + "Example Requests:\n"
			+ "\n" + "chitgroup/1")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ChitGroupApiSwagger.RetrieveOneResponse.class))) })
	public String retrieveDashBoardData(@PathParam("clientid") @Parameter(description = "clientid") final Long clientid,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

		
		Map<String,List<JsonObject>> a = new LinkedHashMap<>();
		if(clientid!=null)
		{
			Iterator<ChitGroupSubscriberData> itr = this.readPlatformService.getChitSubscribersUsingClientId(clientid).iterator();
			
			while(itr.hasNext())
			{
			
				ChitGroupSubscriberData itrData = itr.next();
				
				Long chitId = itrData.getChitId();
			
				Long ticketnum = itrData.getChitNumber().longValue();
				List<JsonObject> j = this.clientStatementReadService.memberstatement(clientid, chitId, ticketnum);
				
				a.put(chitId+"-"+ticketnum, j);
				
			}
		}
	
		return this.toApiJsonSerializer.serialize(a);
	}


	@POST
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a Chit group", description = "Creates a Chit group")
	@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ChitGroupApiSwagger.PostChitGroupRequest.class)))
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ChitGroupApiSwagger.CreateChitGroupResponse.class))) })
	@Transactional
	public String create(
			@FormDataParam("fdrfileSize")  Long fdrfileSize, @FormDataParam("psofileSize")  Long psofileSize, @FormDataParam("ccfileSize")  Long ccfileSize, 
			@FormDataParam("chitgroup")  String chitGroupData, 
			@FormDataParam("fdrfile" ) final InputStream fdrInputStream, @FormDataParam("fdrfile") final FormDataContentDisposition fdrFileDetails, @FormDataParam("fdrfile") final FormDataBodyPart fdrBodyPart, 
			@FormDataParam("psofile") final InputStream psoInputStream, @FormDataParam("psofile") final FormDataContentDisposition psoFileDetails, @FormDataParam("psofile") final FormDataBodyPart psoBodyPart, 
			@FormDataParam("ccfile") final InputStream ccInputStream, @FormDataParam("ccfile") final FormDataContentDisposition ccFileDetails, @FormDataParam("ccfile") final FormDataBodyPart ccBodyPart) 
	{
		////System.out.println("fdrFileSize = " + fdrfileSize);
		//////System.out.println(fdrInputStream);
		//////System.out.println(fdrBodyPart);
		//////System.out.println(psoInputStream);
		////System.out.println(psofileSize);
		//////System.out.println(psoBodyPart);

		// Save group first and then the FDR and other attachements in document.
		List<String> permissions = new ArrayList<String>();
    	permissions.add("CREATE");
    	this.context.authenticatedUser().validateHasPermissionTo(resourceNameForPermissions, permissions);
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createChitGroup().withJson(chitGroupData.toString()).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		// FDR Doc
		if (fdrInputStream != null && fdrFileDetails != null &&  fdrBodyPart != null ) {
			fileUploadValidator.validate(fdrfileSize, fdrInputStream, fdrFileDetails, fdrBodyPart);
			final DocumentCommand fdrDocumentCommand = new DocumentCommand(null, null, "ChitGroups", result.resourceId(), "FDR_Certificate", fdrFileDetails.getFileName(),
					fdrfileSize, fdrBodyPart.getMediaType().toString(), null, null,null);
			this.documentWritePlatformService.createDocument(fdrDocumentCommand, fdrInputStream);
		}

		// PSO Doc
		if (psoInputStream != null && psoFileDetails != null &&  psoBodyPart != null ) {
			fileUploadValidator.validate(psofileSize, psoInputStream, psoFileDetails, psoBodyPart);
			final DocumentCommand psoDocumentCommand = new DocumentCommand(null, null, "ChitGroups", result.resourceId(), "PSO_Certificate", psoFileDetails.getFileName(),
					psofileSize, psoBodyPart.getMediaType().toString(), null, null,null);
			this.documentWritePlatformService.createDocument(psoDocumentCommand, psoInputStream);
		}

		// CC Doc
		if (ccInputStream != null && ccFileDetails != null &&  ccBodyPart != null ) {
			fileUploadValidator.validate(ccfileSize, ccInputStream, ccFileDetails, ccBodyPart);
			final DocumentCommand ccDocumentCommand = new DocumentCommand(null, null, "ChitGroups", result.resourceId(), "CC_Certificate", ccFileDetails.getFileName(),
					ccfileSize, ccBodyPart.getMediaType().toString(), null, null,null);
			this.documentWritePlatformService.createDocument(ccDocumentCommand, ccInputStream);	
		}

		return this.toApiJsonSerializer.serialize(result);
	}

	@POST
	@Path("{chitgroupId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Activate a Chit Group |  Close a Group | ", description = "Activate a Group:\n\n"
			+ "Groups can be created in a Pending state. This API exists to enable group activation and move to Running state.\n\n" + "\n\n"
			+ "Close a Group: This API exists to close a group.\n\n")
	@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ChitGroupApiSwagger.PostGroupsGroupIdRequest.class)))
	public void updateChitGroupOnCommand(@PathParam("chitgroupId") @Parameter(description = "chitgroupId") final Long chitgroupId,
			@QueryParam("command") @Parameter(description = "command") final String commandParam,
			@Parameter(hidden = true) final String apiRequestBodyAsJson, @Context final UriInfo uriInfo) throws Exception {
		final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
		final AppUser currentUser = this.context.authenticatedUser();
		System.out.println(currentUser.getFirstname());
		CommandProcessingResult result = null;
		if (is(commandParam, "activate")) {
			try {
				final JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
				this.writePlatformService.activateChitGroup(chitgroupId, data);

			} catch (Exception e) {
				throw new Exception (e);
			}
		} else {
			throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "activate", "associateCLients", "generateCollectionSheet",
			"saveCollectionSheet" });
		}

		// else if (is(commandParam, "associateClients")) {
		// 	final CommandWrapper commandRequest = builder.associateClientsToGroup(groupId).build();
		// 	result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		// 	return this.toApiJsonSerializer.serialize(result);
		// } else if (is(commandParam, "disassociateClients")) {
		// 	final CommandWrapper commandRequest = builder.disassociateClientsFromGroup(groupId).build();
		// 	result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		// 	return this.toApiJsonSerializer.serialize(result);
		// } else if (is(commandParam, "generateCollectionSheet")) {
		// 	final JsonElement parsedQuery = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		// 	final JsonQuery query = JsonQuery.from(apiRequestBodyAsJson, parsedQuery, this.fromJsonHelper);
		// 	final JLGCollectionSheetData collectionSheet = this.collectionSheetReadPlatformService.generateGroupCollectionSheet(groupId,
		// 			query);
		// 	final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		// 	return this.toApiJsonSerializer.serialize(settings, collectionSheet, GroupingTypesApiConstants.COLLECTIONSHEET_DATA_PARAMETERS);
		// } else if (is(commandParam, "saveCollectionSheet")) {
		// 	final CommandWrapper commandRequest = builder.saveGroupCollectionSheet(groupId).build();
		// 	result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		// 	return this.toApiJsonSerializer.serialize(result);
		// } else if (is(commandParam, "close")) {
		// 	final CommandWrapper commandRequest = builder.closeGroup(groupId).build();
		// 	result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		// 	return this.toApiJsonSerializer.serialize(result);

	}

	private boolean is(final String commandParam, final String commandValue) {
		return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
	}

	@PUT
	@Path("{id}")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Update a chit group", description = "Updates the details of chit group.")
	@RequestBody(required = true, content = @Content(schema = @Schema(implementation = ChitGroupApiSwagger.PostChitGroupRequest.class)))
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ChitGroupApiSwagger.PostChitGroupRequest.class))) })
	@Transactional
	public String update(@PathParam("id") @Parameter(description = "chitgroup") final Long id,
			@FormDataParam("fdrfileSize")  Long fdrfileSize, @FormDataParam("psofileSize")  Long psofileSize, @FormDataParam("ccfileSize")  Long ccfileSize, 
			@FormDataParam("chitgroup")  String chitGroupData, 
			@FormDataParam("fdrfile" ) final InputStream fdrInputStream, @FormDataParam("fdrfile") final FormDataContentDisposition fdrFileDetails, @FormDataParam("fdrfile") final FormDataBodyPart fdrBodyPart, 
			@FormDataParam("psofile") final InputStream psoInputStream, @FormDataParam("psofile") final FormDataContentDisposition psoFileDetails, @FormDataParam("psofile") final FormDataBodyPart psoBodyPart, 
			@FormDataParam("ccfile") final InputStream ccInputStream, @FormDataParam("ccfile") final FormDataContentDisposition ccFileDetails, @FormDataParam("ccfile") final FormDataBodyPart ccBodyPart) {
		////System.out.println("fdrFileSize = " + fdrfileSize);
		////System.out.println(fdrInputStream);
		////System.out.println(fdrBodyPart);
		////System.out.println(psoInputStream);
		////System.out.println(psofileSize);
		// Update group first and then the FDR and other attachements in document.				
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateChitGroup(id).withJson(chitGroupData).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		// FDR Doc
		if (fdrInputStream != null && fdrFileDetails != null &&  fdrBodyPart != null ) {
			fileUploadValidator.validate(fdrfileSize, fdrInputStream, fdrFileDetails, fdrBodyPart);
			final DocumentCommand fdrDocumentCommand = new DocumentCommand(null, null, "ChitGroups", result.resourceId(), "FDR_Certificate", fdrFileDetails.getFileName(),
					fdrfileSize, fdrBodyPart.getMediaType().toString(), null, null,null);
			this.documentWritePlatformService.createDocument(fdrDocumentCommand, fdrInputStream);
		}

		// PSO Doc
		if (psoInputStream != null && psoFileDetails != null &&  psoBodyPart != null ) {
			fileUploadValidator.validate(psofileSize, psoInputStream, psoFileDetails, psoBodyPart);
			final DocumentCommand psoDocumentCommand = new DocumentCommand(null, null, "ChitGroups", result.resourceId(), "PSO_Certificate", psoFileDetails.getFileName(),
					psofileSize, psoBodyPart.getMediaType().toString(), null, null,null);
			this.documentWritePlatformService.createDocument(psoDocumentCommand, psoInputStream);
		}

		// CC Doc
		if (ccInputStream != null && ccFileDetails != null &&  ccBodyPart != null ) {
			fileUploadValidator.validate(ccfileSize, ccInputStream, ccFileDetails, ccBodyPart);
			final DocumentCommand ccDocumentCommand = new DocumentCommand(null, null, "ChitGroups", result.resourceId(), "CC_Certificate", ccFileDetails.getFileName(),
					ccfileSize, ccBodyPart.getMediaType().toString(), null, null,null);
			this.documentWritePlatformService.createDocument(ccDocumentCommand, ccInputStream);	
		}

		return this.toApiJsonSerializer.serialize(result);
	}

	@GET
	@Path("{id}/subscribers")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit group's Subsribers")
	public String getSubscribersList(@PathParam("id") @Parameter(description = "id") final Long id,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		Collection<ChitGroupSubscriberData> chitSubs = this.readPlatformService.getChitSubscribers(id);
		return this.toApiJsonSerializerSubscriber.serialize(settings, chitSubs, this.responseDataParameters);

	}
	
	

	@GET
	@Path("/acccountingtemplate")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve accounting ledger entries for drop down")
	public String retrieveAccountMappingOptionsForChitProducts(@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

		Map<String, List<GLAccountData>> data =  this.accountingDropdownReadPlatformService.retrieveAccountMappingOptionsForChitProducts();
		return this.toApiJsonSerializerMap.serialize(settings, data, this.responseDataParameters);
	}

	@POST
	@Path("{id}/subscribers")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a Subscriber for a Chit group")
	public String createSubscriber(@PathParam("id") @Parameter(description = "id") final Long id,
			@Parameter(hidden = true) final String apiRequestBodyAsJson) throws Exception {
		JsonObject obb = new JsonObject();
		try {
			////System.out.println("id "+id);
			final JsonElement datas = this.fromJsonHelper.parse(apiRequestBodyAsJson);
			final JsonObject data = datas.getAsJsonObject();
			//final JsonCommand  data = new JsonCommand(id, datas);
		    this.writePlatformService.createChitGroupSubscriber(id, data);
			obb.addProperty("status", "success");
			return obb.toString();
		} catch (Exception e) {
			obb.addProperty("status", "failuree");
			return obb.toString();
			//throw new Exception (e);
		}
	}
	
//	@POST
//	@Path("{id}/subscribers")
//	@Consumes({ MediaType.APPLICATION_JSON })
//	@Operation(summary = "Create a Subscriber for a Chit group")
//	public void createSubscriber(@PathParam("id") @Parameter(description = "id") final Long id,
//			@Parameter(hidden = true) final String apiRequestBodyAsJson) throws Exception {
//
//		try {
//			////System.out.println("id "+id);
//			final JsonElement datas = this.fromJsonHelper.parse(apiRequestBodyAsJson);
//			final JsonObject data = datas.getAsJsonObject();
//			//final JsonCommand  data = new JsonCommand(id, datas);
//			this.writePlatformService.createChitGroupSubscriber(id, data);
//			return;
//		} catch (Exception e) {
//			throw new Exception (e);
//		}
//	}

	@POST
	@Path("/firstAuctionToCompany/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "updating chits subscribers. \n")
	public String firstAuctionToCompany(@PathParam("id") @Parameter(description = "id") final Long id) 
	{			

		final JsonObject objectData = new JsonObject();
		//			Long id = objectData.get("id").getAsLong();
		this.writePlatformService.firstAuctionToCompany(id);
		objectData.addProperty("result", "success");
		return objectData.toString();
	}

	@PUT
	@Path("subscribers/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a Subscriber for a Chit group")
	public void updateSubscriber(@PathParam("id") @Parameter(description = "id") final Long id,
			@Parameter(hidden = true) final String apiRequestBodyAsJson) throws Exception {

		try {
			////System.out.println("id "+id);
			final JsonElement datas = this.fromJsonHelper.parse(apiRequestBodyAsJson);
			final JsonObject data = datas.getAsJsonObject();
			//final JsonCommand  data = new JsonCommand(id, datas);
			this.writePlatformService.updateChitgroupSubscriber(id, data);
			return;
		} catch (Exception e) {
			throw new Exception (e);
		}
	}

	@DELETE
	@Path("{id}/subscribers/{subscriberid}")
	@Operation(summary = "Delete a Subscriber for a Chit group")
	public void deleteSubscriber(@PathParam("id") final Long id,@PathParam("subscriberid") final Long subscriberid) throws Exception {

		try {
			this.writePlatformService.deleteChitGroupSubscriber(subscriberid);
			return;

		} catch (Exception e) {
			throw new Exception (e);
		}

	}

	@DELETE
	@Path("{id}")
	@Operation(summary = "Delete a Chit Group")
	public void deleteChitGroup(@PathParam("id") final Long id) throws Exception {

		try {
			this.writePlatformService.deleteChitGroup(id);
			return;

		} catch (Exception e) {
			throw new Exception (e);
		}

	}

	@PUT
	@Path("/movetonextcycle/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve accounting ledger entries for drop down")
	public String movechitcycle(@PathParam("id") final Long id) 
	{
		this.writePlatformService.move(id);
		JsonObject resp = new JsonObject();
		resp.addProperty("result", "Success");
		return resp.toString();

	}

	@POST
	@Path("{chitId}/prizeCalculations/{cycleNumber}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Operation(summary = " updating prized subscriber , prized cycle and dividend")
	public String prizeMoneyCalculations(@PathParam("chitId")final Long chitId, @PathParam("cycleNumber") final Long cycleNumber,@Parameter(hidden = true) final String apiRequestBodyAsJson) {

		//////System.out.println("Im In!!");
		JsonElement datatobeParsed = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject data = datatobeParsed.getAsJsonObject();
		this.writePlatformService.prizeMoneyCalculations(chitId, cycleNumber,data);
		//////System.out.println("Executed prizeMoney Calculations!!");
		return this.toApiJsonSerializer.serialize("success");
	}

	@GET
	@Path("/chitsubscribers/advances")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit group's Subsribers advance payments")
	public String getSubscribersAdvancePaymentList(@QueryParam("clientlist") final String clientlist) {

		//////System.out.println(clientlist);
		return this.toApiJsonSerializer.serialize(this.clientTransactionReadPlatformService.retrieveAllUnAdjustedOfChitSubscribers(clientlist));

	}

	@PUT
	@Path("/closechitgroup/{chitId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Closing the chit group")
	public String CloseChitGroup(@PathParam("chitId")final Long chitId,@Parameter(hidden = true) final String apiRequestBodyAsJson)
	{
		String response =null;
		JsonElement datatobeParsed = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject data = datatobeParsed.getAsJsonObject();
		if(data.get("dateofclosing")!=null && !data.get("dateofclosing").isJsonNull())
		{
			String date = data.get("dateofclosing").getAsString();
			LocalDate closingDate = LocalDate.parse(date);
			response = this.writePlatformService.closeChitGroup(chitId, closingDate);
		}
		else
		{
			JsonObject respone = new JsonObject();
			respone.addProperty("Status", "Failed");
			respone.addProperty("Message", "Input the Date");
		}
		return response;

	}

	@GET
	@Path("/getactivesubs/{chitId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit group's Subsribers who are having outstanding even after the group id closed")
	public String getActivesubsAfterClosingGroup(@PathParam("chitId") @Parameter(description = "chitId") final Long chitId,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

		JsonObject chitSubs = this.readPlatformService.getSubscriberListoFClosedGroups(chitId);
		return this.toApiJsonSerializerSubscriber.serialize(chitSubs);

	}
	
	@GET
	@Path("/getindvactivesubs/{chitId}/{clientId}/{chitNum}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit group's Subsribers who are having outstanding even after the group id closed")
	public String getIndvActivesubsAfterClosingGroup(@PathParam("chitId") @Parameter(description = "chitId") final Long chitId,@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,@PathParam("chitNum") @Parameter(description = "chitNum") final Long chitNum,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

		JsonObject chitSubs = this.readPlatformService.getSingleSubscriberClosedGroups(clientId,chitId,chitNum);
		return this.toApiJsonSerializerSubscriber.serialize(chitSubs);

	}


	@GET
	@Path("/dashboard/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary ="getting Datas for DashBoard")
	public String getDashBoardData(@PathParam("id")final Long id) {

		return this.toApiJsonSerializer.serialize(this.readPlatformService.retriveDashBoardData(id));
	}
	
	@GET
	@Path("/chitGroupCurrentCycle/{chitId}/{cycleNumber}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary ="getting Datas for DashBoard")
	public String retriveChitGroupCycleData(@PathParam("chitId")final Long chitId, @PathParam("cycleNumber")final int cycleNumber) {

		return this.toApiJsonSerializer.serialize(this.readPlatformService.retriveChitGroupCycleData(chitId,cycleNumber));
	}
	
	@GET
	@Path("/reports/dailycollection")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "report for daily collection")
	public String retrievereports(@Context final UriInfo uriInfo,@QueryParam("officeId") @Parameter(description = "officeId") final Long officeId,
			@QueryParam("date") @Parameter(description = "date") final String date) {
		JsonObject re = reportReadPlatformService.retrievereportDailyCollection(officeId,date);
		return this.toApiJsonSerializerMap.serialize(re);
	}
	
	@GET
	@Path("/reports/psbucketreport")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievereportspsbucket(@Context final UriInfo uriInfo,@QueryParam("officeId") @Parameter(description = "officeId") final Long officeId,
			@QueryParam("date") @Parameter(description = "date") final String date) {
		JsonObject re = reportReadPlatformService.retrievereportDailyCollectionOne(officeId,date);
		return this.toApiJsonSerializerMap.serialize(re);
	}
	
	@GET
	@Path("/reports/npsbucketreport")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievereportsnpsbucket(@Context final UriInfo uriInfo,@QueryParam("officeId") @Parameter(description = "officeId") final Long officeId,
			@QueryParam("date") @Parameter(description = "date") final String date) {
		JsonObject re = reportReadPlatformService.retrievereportNPSBucketWise(officeId,date);
		return this.toApiJsonSerializerMap.serialize(re);
	}
	
	@GET
	@Path("/reports/nonstartedgroup")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievereportsnonstartedgroup(@Context final UriInfo uriInfo,@QueryParam("officeId") @Parameter(description = "officeId") final Long officeId,
			@QueryParam("date") @Parameter(description = "date") final String date){
		JsonObject re = reportReadPlatformService.retrievereportNonStartedChitGroup(officeId,date);
		return this.toApiJsonSerializerMap.serialize(re);
	}
	
	
	@GET
	@Path("/winnerspayable/{chitId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit group's Subsribers who has payouts")
	public String getWinnersSubscribersList(@PathParam("chitId") @Parameter(description = "chitId") final Long chitId,
			@Context final UriInfo uriInfo) {

		JsonObject response  = chitBidsReadPlatformService.getListofWinnersForPayable(chitId);
		return this.toApiJsonSerializerMap.serialize(response);

	}
	
	@POST
	@Path("/winnerspayable/{ChitSubsid}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit group's Subsribers who has payouts")
	public String PayWinners(@PathParam("ChitSubsid") @Parameter(description = "ChitSubsid") final Long ChitSubsid,
			@QueryParam("bidId") @Parameter(description = "bidId") final Long bidId,@QueryParam("SubscriptionPayable") @Parameter(description = "SubscriptionPayable") final Double SubscriptionPayable,
			@QueryParam("date") @Parameter(description = "date") final String date,@QueryParam("accId") @Parameter(description = "accId") final Long accId,@Parameter(hidden = true) final String apiRequestBodyAsJson) {
		
		JsonElement string = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject apiRequestAsJson = string.getAsJsonObject();
		JsonObject response  = this.writePlatformService.winnerspayable(ChitSubsid, SubscriptionPayable, date,bidId,accId,apiRequestAsJson);
		return this.toApiJsonSerializerMap.serialize(response);

	}
	
	@POST
	@Path("{chitid}/rebid/{oldSubId}/{newSubId}/{chitGroupCycle}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@RequestBody(required = true )
	public String reBidChitGroup(@PathParam("chitid") @Parameter final Long chitid,
			@PathParam("oldSubId") @Parameter final Long oldSubId,
			@PathParam("newSubId") @Parameter final Long newSubId,
			@PathParam("chitGroupCycle") @Parameter final Long chitGroupCycle,
			@Parameter(hidden = true) final String apiRequestBodyAsJson){

		JsonElement elementData = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedJson = elementData.getAsJsonObject();
		LocalDate localdate = null;
		if(parsedJson.get("date")!=null && !parsedJson.get("date").isJsonNull())
		{
			String date = parsedJson.get("date").getAsString();
			localdate = LocalDate.parse(date);
		}
		Long bidderparticipationId = 0l;
		if(parsedJson.get("bidderparticipationId")!=null && !parsedJson.get("bidderparticipationId").isJsonNull())
		{
			String participationId = parsedJson.get("bidderparticipationId").getAsString();
			bidderparticipationId = Long.parseLong(participationId);
		}
		final JsonObject result = this.writePlatformService.reBid(chitid, oldSubId, newSubId, chitGroupCycle, localdate,bidderparticipationId);

		return this.toApiJsonSerializer.serialize(result);
	}
	
	
	@GET
	@Path("/winnerspayableforrebidcheck/{chitId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve a Chit group's Subsribers who has payouts")
	public String getReBidListofWinnersForPayable(@PathParam("chitId") @Parameter(description = "chitId") final Long chitId,
			@Context final UriInfo uriInfo) {

		JsonObject response  = chitBidsReadPlatformService.getReBidListofWinnersForPayable(chitId);
		return this.toApiJsonSerializerMap.serialize(response);
	}

	@PUT
	@Path("{chitId}/bidAdvance/{subscriberId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String bidAdvance(@PathParam("chitId")@Parameter final Long chitId, @PathParam("subscriberId")@Parameter final Long subscriberId,
			@Parameter(hidden=true)final String apiRequestBodyAsJson){
		JsonElement elementData = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData =elementData.getAsJsonObject();
		Long bidAmountInPercent = 0l;
		Double paybleAmount = null;
		JsonObject obj = new JsonObject();
		if(parsedData.get("bidAmountInPercent")!=null && !parsedData.get("bidAmountInPercent").isJsonNull())
		{
			String amount = parsedData.get("bidAmountInPercent").getAsString();
			bidAmountInPercent = Long.parseLong(amount); 
		}
		LocalDate localdate = null;
		if(parsedData.get("date")!=null && !parsedData.get("date").isJsonNull())
		{
			String datee = parsedData.get("date").getAsString();
			localdate = LocalDate.parse(datee); 
		}
		
		if(parsedData.get("paybleAmount") == null) {
			obj = this.writePlatformService.bidAdvance(chitId, subscriberId,bidAmountInPercent);
		} else {
			String paybleAmt = parsedData.get("paybleAmount").getAsString();
			paybleAmount = Double.parseDouble(paybleAmt); 
			obj = this.writePlatformService.terminatePayout(chitId, subscriberId,paybleAmount);
		}
		
		return obj.toString();
		
	}
	
	@POST
	@Path("{chitId}/bidAdvancePayOut/{subscriberId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String bidAdvancePayOut(@PathParam("chitId")@Parameter final Long chitId,
			@PathParam("subscriberId")@Parameter final Long subscriberId,
		 @Parameter(hidden=true)final String apiRequestBodyAsJson){
		JsonElement elementData = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData =elementData.getAsJsonObject();
		
		JsonObject obj = this.writePlatformService.bidAdvancePayOut(chitId, subscriberId, parsedData);
		return obj.toString();
		
	}
	
	
	@POST
	@Path("{chitId}/forClosure/{ticketNumber}/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String forClosure(@PathParam("chitId")@Parameter final Long chitId, 
			@PathParam("ticketNumber")@Parameter final Long ticketNumber, 
			@PathParam("clientId")@Parameter final Long clientId,
			@Parameter(hidden=true)final String apiRequestBodyAsJson){
		JsonElement element = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject object = element.getAsJsonObject();
		JsonObject data = new JsonObject();
		String searchParam = null;
		String param =  "Terminated-Subscriber";
		if(object.get("searchParam")!=null && !object.get("searchParam").isJsonNull()) {
			searchParam = object.get("searchParam").getAsString();
		}
		if(param.equalsIgnoreCase(searchParam)) {
			data = this.writePlatformService.terminateSubscriber(chitId, clientId, ticketNumber);
		} else {
			data = this.writePlatformService.forClosure(chitId, clientId, ticketNumber);
		}
		return data.toString();
		
	}	
	
	@POST
	@Path("/forClosureAdjust/{subscriberId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String forClosureAdjust(@PathParam("subscriberId")@Parameter final Long subscriberId,  
			@Parameter(hidden=true)final String apiRequestBodyAsJson){
		System.out.println("11");
		JsonElement element = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject object = element.getAsJsonObject();
		String param = "Terminated-Subscriber";
		String searchParam = null;
		JsonObject data = new JsonObject();
		if(object.get("searchParam")!=null && !object.get("searchParam").isJsonNull()) {
			searchParam = object.get("searchParam").getAsString();
		}
		if(param.equalsIgnoreCase(searchParam)) {
			data = this.writePlatformService.terminateAdjust(subscriberId,object);
		} else {
			data = this.writePlatformService.forClosureAdjust(subscriberId,object);
		}
		return data.toString();
	}	
	
	@POST
	@Path("/forClosureApproval/{subscriberId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String forClosureApproval(@PathParam("subscriberId")@Parameter final Long subscriberId,
			@Parameter(hidden=true)final String apiRequestBodyAsJson){
		JsonElement element = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject object = element.getAsJsonObject();
		String searchParam = null;
		String param = "Terminated-Subscriber";
		JsonObject data = new JsonObject();
		if(object.get("searchParam")!=null && !object.get("searchParam").isJsonNull()) {
			searchParam = object.get("searchParam").getAsString();
			System.out.println(searchParam);
		}
		if(param.equalsIgnoreCase(searchParam)) {
			data = this.writePlatformService.terminateApproval(subscriberId,object);		
		} else {
			data = this.writePlatformService.forClosureApproval(subscriberId,object);	
		}
		return data.toString();
		
	}	
	
	@GET
	@Path("/replaceSubscriber/{chitId}/client/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String replaceSubscriber(@PathParam("chitId")@Parameter final Long chitId,
			@PathParam("clientId")@Parameter final Long clientId){
		JsonObject data = new JsonObject();
		data = this.writePlatformService.replaceSubscriber(chitId, clientId);
		return data.toString();
	}
	
}


