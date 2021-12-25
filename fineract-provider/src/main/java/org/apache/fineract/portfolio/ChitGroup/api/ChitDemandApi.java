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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;


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


import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.portfolio.ChitGroup.data.ChitAdvanceTransactionData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandDataForBalance;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleForMobile;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberTransactionData;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitTransactionEnum;
import org.apache.fineract.portfolio.ChitGroup.service.ChitCycleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitDemandScheduleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitDemandScheduleWritePlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberTransactionReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.scheduledjobs.service.ScheduledJobRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/chitdemand")
@Component
@Scope("singleton")
@Tag(name = "ChitCharge", description = "Allows you to create update and read the details Chit charge")
public class ChitDemandApi 
{
	private final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService ;
	private final ChitDemandScheduleWritePlatformService chitDemandScheduleWritePlatformService ;
	private final DefaultToApiJsonSerializer<ChitSubscriberChargeData> toApiJsonSerializer;
	private final FromJsonHelper fromJsonHelper;
	private final ScheduledJobRunnerService scheduledJobRunnerService;
	private final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final ChitGroupReadPlatformService chitGroupReadPlatformService;
	private final ChitCycleReadPlatformService chitCycleReadPlatformService;
	 private final ClientRepositoryWrapper clientRepository;
	
	@Autowired
	public ChitDemandApi(ChitDemandScheduleReadPlatformService chitChargeReadPlatformServices,
			ChitDemandScheduleWritePlatformService chitChargeWritePlatformService,final DefaultToApiJsonSerializer<ChitSubscriberChargeData> toApiJsonSerializer,final FromJsonHelper fromJsonHelper,
			final ScheduledJobRunnerService scheduledJobRunnerService,final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService,final ClientReadPlatformService clientReadPlatformService,
			final ChitGroupReadPlatformService chitGroupReadPlatformService,final ChitCycleReadPlatformService chitCycleReadPlatformService,ClientRepositoryWrapper clientRepository) 
	{
		
		this.chitDemandScheduleReadPlatformService = chitChargeReadPlatformServices;
		this.chitDemandScheduleWritePlatformService = chitChargeWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.fromJsonHelper = fromJsonHelper;
		this.scheduledJobRunnerService = scheduledJobRunnerService;
		this.chitSubscriberTransactionReadPlatformService = chitSubscriberTransactionReadPlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.chitCycleReadPlatformService = chitCycleReadPlatformService;
		this.clientRepository = clientRepository;
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Subscriber charge ", description = "Retrieve all Chit Charge bids.\n" )
	public String retrieveAll(@QueryParam("date") @Parameter(description = "date") final String date,
            @QueryParam("clientId") @Parameter(description = "clientId") final Long clientId) {
		
		if(clientId!=null)
		{
			LocalDate Date;
			if(!StringUtils.isBlank(date))
			{
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				Date = LocalDate.parse(date, formatter);
			}
			else
			{
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				
				LocalDate tempdate = LocalDate.now(ZoneId.systemDefault());;
				
				Date = LocalDate.parse(tempdate.toString(), formatter);
			}
			final Collection<ChitDemandScheduleForMobile> ChitDemandSchedule = this.chitDemandScheduleReadPlatformService.retriveData(clientId, Date);
			return this.toApiJsonSerializer.serialize(ChitDemandSchedule);
		}
		else
		{
			final Collection<ChitDemandScheduleData> ChitCharge = this.chitDemandScheduleReadPlatformService.retrieveAll();
			return this.toApiJsonSerializer.serialize(ChitCharge);
		}
	}
	
	@GET
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve  Chit Subscriber Charge ", description = "Retrieve  Chit Subscriber Charge bids.\n" )
	public String retrieveNamebyId(@PathParam("id") @Parameter(description = "id") final Long id) {
		
		final ChitDemandScheduleData ChitCharge = this.chitDemandScheduleReadPlatformService.retrieveById(id);
		return this.toApiJsonSerializer.serialize(ChitCharge);
	}
	
	@GET
	@Path("/AdvancePayment/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve  sum of advances" )
	public String retrieveAdvance(@PathParam("id") @Parameter(description = "id") final Long id) {
		
		final ChitAdvanceTransactionData ChitAdvance = this.chitDemandScheduleReadPlatformService.retrieveData(id);
		return this.toApiJsonSerializer.serialize(ChitAdvance);
	}
	
	
	@PUT
	@Path("/jobschedular/{date}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve  sum of advances" )
	public String jobSchedular(@PathParam("date") @Parameter(description = "date") final String date) throws JobExecutionException {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		LocalDate Date = LocalDate.parse(date, formatter);
		this.scheduledJobRunnerService.dailyDemandUpdate(Date);
		
		JsonObject resp = new JsonObject();
		resp.addProperty("status", "Success");
		return resp.toString();
	}
		
		
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Post Demand Schedule collections" )
	public String createchitcharge(@Parameter(hidden = true) final String apiRequestBodyAsJson) 
	{
		JsonObject resp = this.chitDemandScheduleWritePlatformService.calculateDemand(apiRequestBodyAsJson);
	
		//JsonObject resp = this.reciept(apiRequestBodyAsJson);
		
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
		final CommandProcessingResult result = this.chitDemandScheduleWritePlatformService.updateChitDemandSchedule(id, parsedData);
		
		return this.toApiJsonSerializer.serialize(result);
	}
	
	
	@PUT
	@Path("/paytoOSofclosedgroup")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String CalculateOSofClosedGroup(@Parameter(hidden = true) final String apiRequestBodyAsJson) 
	{

		JsonObject data = this.chitDemandScheduleWritePlatformService.calculateLastOSsubscription(apiRequestBodyAsJson);
		
		return this.toApiJsonSerializer.serialize(data);
	}
	
	@DELETE
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "delete  Chit Subscriber Charge ", description = "delete  Chit Subscriber Charge .\n" )
	public String updateChitCharge(@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		
		final CommandProcessingResult result = this.chitDemandScheduleWritePlatformService.deleteChitDemandSchedule(id);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
	
	public JsonObject reciept(String apirequestBody)
	{
		JsonElement parsedData = this.fromJsonHelper.parse(apirequestBody);
		JsonObject temp = parsedData.getAsJsonObject();
		
		JsonArray tempdata = null;
		
		if(temp.get("collections")!=null && !temp.get("collections").isJsonNull())
		{
			tempdata = temp.get("collections").getAsJsonArray();
		}
		
		Long ChitSubscId = null;
		Long DemandScheduleId = null;
		Long ChitSubscriberChargeId = null;
		Long ClientId = null;
		Long ChitId = null;
		
		for(int i = 0 ; i < 1 ; i++)
		{
			JsonObject data = tempdata.get(i).getAsJsonObject();

				if(data.get("ChitSubscriberChargeId")!=null && !data.get("ChitSubscriberChargeId").isJsonNull())
				{
					ChitSubscId = data.get("ChitSubscriberChargeId").getAsLong();
				}
	
				if(data.get("DemandScheduleId")!=null && !data.get("DemandScheduleId").isJsonNull())
				{
					DemandScheduleId = data.get("DemandScheduleId").getAsLong();
				}
	
				if(data.get("ChitSubsId")!=null && !data.get("ChitSubsId").isJsonNull())
				{
					ChitSubscriberChargeId = data.get("ChitSubsId").getAsLong();
				}
				
				if(data.get("ClientId")!=null && !data.get("ClientId").isJsonNull())
				{
					ClientId = data.get("ClientId").getAsLong();
				}
				
				if(data.get("ChitId")!=null && !data.get("ChitId").isJsonNull())
				{
					ChitId = data.get("ChitId").getAsLong();
				}
		}
		
		JsonObject reciept = new JsonObject();
		
		Long paymentDetailId = null;
		
		if(DemandScheduleId!=null)
		{
			Collection<ChitSubscriberTransactionData> CollectionTransactionData = chitSubscriberTransactionReadPlatformService.retrieveDataUsingDemandId(DemandScheduleId);
			
			Iterator<ChitSubscriberTransactionData> itr = CollectionTransactionData.iterator();
			////System.out.println("data");
			while(itr.hasNext())
			{
				int count = 0 ;
				ChitSubscriberTransactionData itrData = itr.next();
				Long trantype = itrData.getTrantype();
				ChitTransactionEnum tranType = ChitTransactionEnum.fromInt(trantype.intValue());
				String trType = tranType.name();
				////System.out.println(" "+ trType +" "+itrData.getAmount());
				reciept.addProperty(trType, itrData.getAmount());
				
				if(count==0)
				{
					paymentDetailId = itrData.getPaymentdetailId();
					
					count++;
				}
				
			}
			
			if(paymentDetailId!=null)
			{
				reciept.addProperty("TransactionId", paymentDetailId);
			}
			
		}
		
		ClientData clientdata = clientReadPlatformService.retrieveOne(ClientId);
		
		reciept.addProperty("ClientName", clientdata.getFirstname());
		reciept.addProperty("MobileNumber", clientdata.getMobileNo());
		
		if(ChitSubscId!=null)
		{
			ChitDemandDataForBalance data = this.chitDemandScheduleReadPlatformService.getDemandBalance(ChitSubscId);
			Double collectedAmount = data.getCollectedAmount();
			reciept.addProperty("collectedAmount Till Date ", collectedAmount);
			Long MonthlyContribution = chitGroupReadPlatformService.retrieveChitGroup(ChitId).getMonthlycontribution();
			Long currentCycle = chitGroupReadPlatformService.retrieveChitGroup(ChitId).getCurrentcycle();
			Long dividend  = chitCycleReadPlatformService.retrieveAll(ChitId, currentCycle).getDividend();
			Long Remaining_Balance = null;
			if(dividend!=null)
			{
				Remaining_Balance = (long) ((MonthlyContribution-dividend)-collectedAmount);
			}
			else
			{
				Remaining_Balance = (long)(MonthlyContribution-collectedAmount);
			}
			reciept.addProperty("Remaining Balance", Remaining_Balance);
		}
		
		final Client client = this.clientRepository.findOneWithNotFoundDetection(ClientId);
		reciept.addProperty("OfficeName", client.getOffice().getName());
		reciept.addProperty("OfficeAddress",  client.getOffice().getAddress());
		reciept.addProperty("status", "Success");
		////System.out.println(client.getOffice().getName()+"Im added OfficeName ");
		
		ChitGroupData grpData = chitGroupReadPlatformService.retrieveChitGroup(ChitId);
		reciept.addProperty("GroupName", grpData.getName());
		////System.out.println(grpData.getName()+"Im added group Name");
		return reciept;
	}
	
}
