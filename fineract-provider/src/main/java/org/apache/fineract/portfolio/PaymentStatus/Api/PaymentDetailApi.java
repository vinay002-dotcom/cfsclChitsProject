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
package org.apache.fineract.portfolio.PaymentStatus.Api;



import java.util.Collection;

import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberTransactionData;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberTransactionReadPlatformService;
import org.apache.fineract.portfolio.PaymentStatus.Data.PaymentStatusData;
import org.apache.fineract.portfolio.PaymentStatus.Service.PaymentStatusReadPlatformService;
import org.apache.fineract.portfolio.PaymentStatus.Service.PaymentStatusWritePlatformService;
import org.apache.fineract.portfolio.client.data.ClientTransactionData;
import org.apache.fineract.portfolio.client.service.ClientTransactionReadPlatformService;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailsReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/PaymentDetail")
@Component
@Scope("singleton")
@Tag(name = "ChitCharge", description = "Allows you to create update and read the details Chit charge")
public class PaymentDetailApi 
{
	private final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService ;
	
	private final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService;
	
	private final ClientTransactionReadPlatformService clientTransactionReadPlatformService ; 
	
	private final DefaultToApiJsonSerializer<Collection<ChitSubscriberTransactionData>> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<Collection<ClientTransactionData>> toApiJsonSerializer1;
	private final DefaultToApiJsonSerializer<Collection<PaymentDetailData>> toApiJsonSerializer2;
	
	private final PaymentStatusWritePlatformService paymentStatusWritePlatformService;
	
	private final FromJsonHelper fromJsonHelper;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PaymentStatusReadPlatformService paymentStatusReadPlatformService;
	

	
	
	@Autowired
	public PaymentDetailApi(PaymentDetailsReadPlatformService paymentDetailsReadPlatformService,
			ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService,
			ClientTransactionReadPlatformService clientTransactionReadPlatformService,
			DefaultToApiJsonSerializer<Collection<ChitSubscriberTransactionData>> toApiJsonSerializer,
			DefaultToApiJsonSerializer<Collection<ClientTransactionData>> toApiJsonSerializer1,
			PaymentStatusWritePlatformService paymentStatusWritePlatformService,
			FromJsonHelper fromJsonHelper,
			ApiRequestParameterHelper apiRequestParameterHelper,
			PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final DefaultToApiJsonSerializer<Collection<PaymentDetailData>> toApiJsonSerializer2,
			PaymentStatusReadPlatformService paymentStatusReadPlatformService) {
		super();
		this.paymentDetailsReadPlatformService = paymentDetailsReadPlatformService;
		this.chitSubscriberTransactionReadPlatformService = chitSubscriberTransactionReadPlatformService;
		this.clientTransactionReadPlatformService = clientTransactionReadPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.toApiJsonSerializer1 = toApiJsonSerializer1;
		this.paymentStatusWritePlatformService = paymentStatusWritePlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.toApiJsonSerializer2 = toApiJsonSerializer2;
		this.paymentStatusReadPlatformService = paymentStatusReadPlatformService;
	}
	
	@GET
	@Path("/{fromdate}/{todate}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	
	public String retrieveTransactions(@PathParam("fromdate") @Parameter(description = "fromdate") final String fromdate,
			@PathParam("todate") @Parameter(description = "todate") final String todate,
			@QueryParam("officeId")final Long officeId) 
		{
		Collection<PaymentDetailData> paymentDetail = paymentDetailsReadPlatformService.retrieveBySearch(fromdate,officeId,todate);
		
		if(paymentDetail.size()!=0)
		{
			return toApiJsonSerializer2.serialize(paymentDetail);
		}
		return "No Data Found";
	}
	
	@GET
	@Path("/gettransactions/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	
	public String retrieveTransactionsUsingPaymentID(@PathParam("id") @Parameter(description = "id") final Long id) 
		{
		if(id!=null)
		{
			Collection<ChitSubscriberTransactionData> Data = this.chitSubscriberTransactionReadPlatformService.retrieveTransactionDataUsingPaymentId(id);
			
			if(Data.size()!=0)
			{
				return this.toApiJsonSerializer.serialize(Data);
			}else
			{
				Collection<ClientTransactionData> clientTran = clientTransactionReadPlatformService.retrieveTransactionByPaymentDetailId(id);
				
				if(clientTran.size()!=0)
				{
					return this.toApiJsonSerializer1.serialize(clientTran);
				}
			}
			
		}
		return "No Data Found";
	}
	
	@POST
	@Path("/reverse/{tranId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String reverseTransactions(@PathParam("tranId") @Parameter(description = "tranId") final Long tranId) 
	{
		return paymentStatusWritePlatformService.ReversePayments(tranId);
	}
	
	
	@POST
	@Path("/paymentStatus")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createPaymentStatus(@Parameter(hidden=true) final String apiRequestBodyAsJson) throws Exception	{
		try {
			////System.out.println("id "+id);
			final JsonElement datas = this.fromJsonHelper.parse(apiRequestBodyAsJson);
			final JsonObject data = datas.getAsJsonObject();
		    this.paymentStatusWritePlatformService.createPaymentStatus(data);
		    JsonObject dataas = new JsonObject();
		    dataas.addProperty("status", "success");
		    return dataas.toString();
		} catch (Exception e) {
			throw new Exception (e);
		}
	}
	
	@PUT
	@Path("/{paymentId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updatePaymentStatus(@PathParam("paymentId") @Parameter final Long paymentId, 
			@Parameter(hidden=true) final String apiRequestBodyAsJson)throws Exception{
		try {
			final JsonElement datas = this.fromJsonHelper.parse(apiRequestBodyAsJson);
			final JsonObject data = datas.getAsJsonObject();
		    this.paymentStatusWritePlatformService.updatePaymentStatus(paymentId, data);
		    JsonObject dataas = new JsonObject();
		    dataas.addProperty("status", "updated");
		    return dataas.toString();
		} catch (Exception e) {
			throw new Exception (e);
		}	
	}
	
	@GET
	@Path("/{paymentStatusId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrivePaymentStatus(@PathParam("paymentStatusId") @Parameter final Long paymentStatusId) {
		PaymentStatusData paymentDatas = this.paymentStatusReadPlatformService.getPaymentDetails(paymentStatusId);
		return this.toApiJsonSerializer.serialize(paymentDatas);
	}
}
