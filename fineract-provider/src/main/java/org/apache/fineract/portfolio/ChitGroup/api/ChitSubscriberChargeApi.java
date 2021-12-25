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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.service.ChitChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitCycleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberChargeWritePlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberTransactionWritePlatformService;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/chitsubscribercharge")
@Component
@Scope("singleton")
@Tag(name = "ChitCharge", description = "Allows you to create update and read the details Chit charge")
public class ChitSubscriberChargeApi 
{
	private final ChitSubscriberChargeReadPlatformServices chitChargeReadPlatformServices ;
	private final ChitChargeReadPlatformServices chargeReadPlatformServices ;
	private final ChitSubscriberChargeWritePlatformService chitChargeWritePlatformService ;
	private final DefaultToApiJsonSerializer<ChitSubscriberChargeData> toApiJsonSerializer;
	private final FromJsonHelper fromJsonHelper;
	private final ChitGroupReadPlatformService chitGroupReadPlatformService;
	private final ChitCycleReadPlatformService chitCycleReadPlatformService;
	private PaymentDetailWritePlatformService paymentDetailWritePlatformService;
	private ChitSubscriberTransactionWritePlatformService chitSubscriberTransactionWritePlatformService;
	private final ClientRepositoryWrapper clientRepository;
	
	@Autowired
	public ChitSubscriberChargeApi(ChitSubscriberChargeReadPlatformServices chitChargeReadPlatformServices,
			ChitSubscriberChargeWritePlatformService chitChargeWritePlatformService,final DefaultToApiJsonSerializer<ChitSubscriberChargeData> toApiJsonSerializer,final FromJsonHelper fromJsonHelper,
			final ChitGroupReadPlatformService chitGroupReadPlatformService,final ChitChargeReadPlatformServices chargeReadPlatformServices,final ChitCycleReadPlatformService chitCycleReadPlatformService,
			PaymentDetailWritePlatformService paymentDetailWritePlatformService,ChitSubscriberTransactionWritePlatformService chitSubscriberTransactionWritePlatformService,final ClientRepositoryWrapper clientRepository) 
	{
		
		this.chitChargeReadPlatformServices = chitChargeReadPlatformServices;
		this.chitChargeWritePlatformService = chitChargeWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.fromJsonHelper = fromJsonHelper;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.chargeReadPlatformServices = chargeReadPlatformServices;
		this.chitCycleReadPlatformService = chitCycleReadPlatformService;
		this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
		this.chitSubscriberTransactionWritePlatformService = chitSubscriberTransactionWritePlatformService;
		this.clientRepository = clientRepository;
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Subscriber charge ", description = "Retrieve all Chit Charge bids.\n" )
	public String retrieveAll() {
		final Collection<ChitSubscriberChargeData> ChitCharge = this.chitChargeReadPlatformServices.retrieveAll();
		return this.toApiJsonSerializer.serialize(ChitCharge);
	}
	
	
	@GET
	@Path("clients/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve all Chit Subscriber charge ", description = "Retrieve all Chit Charge bids.\n" )
	public String retrieveChargeUsingClient(@PathParam("id") @Parameter(description = "id") final Long id) 
	{
		
		
		//final Collection<ChitSubscriberChargeData> ChitCharge = this.chitChargeReadPlatformServices.retrieveAll();
		Collection<ChitGroupSubscriberData> c_subscriber = chitGroupReadPlatformService.getChitSubscribersUsingClientId(id);
		
		Iterator<ChitGroupSubscriberData> itr = c_subscriber.iterator();
		
		Collection<ChitChargeData> c_charge = chargeReadPlatformServices.retrieveAll();
		
		Iterator<ChitChargeData> itr1 = c_charge.iterator();
		
		Long EMI = 0l;
		Long Enrollment_fee = 0l;
		
		while(itr1.hasNext())
		{
			ChitChargeData data = itr1.next();
			if(data.getName().equals("MONTHLY_INSTALLMENT"))
			{
				EMI  = data.getId();
			}
			if(data.getName().equals("ENROLLMENT_FEE"))
			{
				Enrollment_fee  = data.getId();
			}
			
		}
		
		while(itr.hasNext())
		{
			ChitGroupSubscriberData itrData = itr.next();
			
			Long subsId = itrData.getId();
			
			final Collection<ChitSubscriberChargeData> ChitCharge = this.chitChargeReadPlatformServices.retrieveByChargeId(subsId, EMI , Enrollment_fee);
			return this.toApiJsonSerializer.serialize(ChitCharge);
		}
		
		JsonObject resp = new JsonObject();
		resp.addProperty("status", "failure");
		return resp.toString();
	}
	
	@GET
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve  Chit Subscriber Charge ", description = "Retrieve  Chit Subscriber Charge bids.\n" )
	public String retrieveNamebyId(@PathParam("id") @Parameter(description = "id") final Long id) {
		
		final ChitSubscriberChargeData ChitCharge = this.chitChargeReadPlatformServices.retrieveNameById(id);
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
	@Operation(summary = "create Chit Subscriber Charge", description = "create Chit Subscriber Charge.\n" )
	public String createchitcharge(@Parameter(hidden = true) final String apiRequestBodyAsJson) 
	{
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		final CommandProcessingResult result = this.chitChargeWritePlatformService.createSubscriberChitCharge(parsedData);
		
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@POST
	@Path("/chargeCollection")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "create Chit Subscriber Charge", description = "create Chit Subscriber Charge.\n" )
	public String createchitchargeForClient(@Parameter(hidden = true) final String apiRequestBodyAsJson) 
	{
		JsonObject rec = new JsonObject();
		JsonElement data = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		JsonObject parsedData = data.getAsJsonObject();
		Long clientId = null;
		Long chitId = null;
		Long chitNumber = null;
		Long cycleNumber = null;
		//Double Amount = 0.0;
		Long chitChargeId = null;
		BigDecimal amount = null;
		Long paymentdetailId = null;
		JsonObject dataTobeSent = new JsonObject();
		if(parsedData.get("clientId")!=null && !parsedData.get("clientId").isJsonNull())
		{
			clientId = parsedData.get("clientId").getAsLong();
			Client dataT = clientRepository.findOneWithNotFoundDetection(clientId);
			rec.addProperty("Name", dataT.getFirstname());
			rec.addProperty("OfficeName", dataT.getOffice().getName());
			rec.addProperty("Office", dataT.getOffice().getAddress());
			
		}
		if(parsedData.get("chitId")!=null && !parsedData.get("chitId").isJsonNull())
		{
			chitId = parsedData.get("chitId").getAsLong();
			
		}
		if(parsedData.get("amount")!=null && !parsedData.get("amount").isJsonNull())
		{
			amount = parsedData.get("amount").getAsBigDecimal();
			dataTobeSent.addProperty("amount", amount);
			rec.addProperty("amount", amount);
		}
		if(parsedData.get("chitNumber")!=null && !parsedData.get("chitNumber").isJsonNull())
		{
			chitNumber = parsedData.get("chitNumber").getAsLong();
			
		}
		if(parsedData.get("chitChargeId")!=null && !parsedData.get("chitChargeId").isJsonNull())
		{
			chitChargeId = parsedData.get("chitChargeId").getAsLong();
			dataTobeSent.addProperty("chitChargeId", chitChargeId);
			String chargeType = this.chargeReadPlatformServices.retrieveNameById(chitChargeId).getName();
			rec.addProperty("chargeType",chargeType);
		}
		
		Long subsId = null;
		
		if(clientId!=null && chitId!=null && chitNumber!=null)
		{
			subsId = this.chitGroupReadPlatformService.getChitSubscriberUsingChitIDClientId(chitId, clientId, chitNumber).getId();
			dataTobeSent.addProperty("subscriberId", subsId);
			
		}
		
		if(parsedData.get("cycleNumber")!=null && !parsedData.get("cycleNumber").isJsonNull())
		{
			cycleNumber = parsedData.get("cycleNumber").getAsLong();
			Long cycleid = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, cycleNumber).getId();
			dataTobeSent.addProperty("chitCycleId", cycleid);
			
		}
		
		if(parsedData.has("paymentInfo"))
		{
			JsonElement payment = parsedData.get("paymentInfo");
			JsonObject paymentObject = payment.getAsJsonObject();
			final Map<String, Object> changes = new LinkedHashMap<>();
			System.out.println(paymentObject.has("officeId"));
			
			if(paymentObject.has("officeId")){
				JsonElement paymentt = paymentObject.get("officeId");
				JsonObject paymentObjectt = paymentt.getAsJsonObject();
				Long officeId = paymentObjectt.get("value").getAsLong();
				System.out.println(officeId);
				paymentObject.addProperty("officeId", officeId);
			}
			
			////System.out.println("payment Obj "+paymentObject);
			PaymentDetail responseofpaymentdetail = paymentDetailWritePlatformService.createAndPersistPaymentDetailJson(paymentObject, changes);
			paymentdetailId = responseofpaymentdetail.getId();
			
			rec.addProperty("TransactionId", paymentdetailId);
		}
		
		dataTobeSent.addProperty("ispaid", false);
		final CommandProcessingResult result = this.chitChargeWritePlatformService.createSubscriberChitCharge(dataTobeSent);
		
		if(paymentdetailId!=null && result.commandId()!=null)
		{
			JsonObject dataforpaidpenalty = new JsonObject();
			dataforpaidpenalty.addProperty("chitsubscriberchargeId", result.commandId());
			dataforpaidpenalty.addProperty("amount", amount);
			dataforpaidpenalty.addProperty("paymentdetailId", paymentdetailId);
			dataforpaidpenalty.addProperty("isprocessed", false);
			chitSubscriberTransactionWritePlatformService.createSubscriberChitCharge(dataforpaidpenalty);
		}
		
		return rec.toString();
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
	
}
