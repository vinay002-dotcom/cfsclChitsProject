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
package org.apache.fineract.infrastructure.creditbureau.api;



import javax.ws.rs.Consumes;
import javax.ws.rs.POST;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.portfolio.ChitGroup.service.NsChitGroupReadPlatformService;
import org.apache.fineract.portfolio.client.api.ClientsApiResource;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.infrastructure.highmark.service.HighMarkProcessingService;
import org.apache.fineract.infrastructure.utills.vo.GenericResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

@Path("/CreditBureau")
@Component
@Scope("singleton")
public class CreditBureauEvaluateApiResource {

    

    private final ClientReadPlatformService clientReadPlatformService;
    private final LoanAssembler loanAssembler;
    private final DefaultToApiJsonSerializer<LoanAccountData> toApiJsonSerializer;
    private final HighMarkProcessingService highMarkProcessingService;
    private final ClientsApiResource clientsApiResource;
    private final NsChitGroupReadPlatformService nsChitGroupReadPlatformService;

    @Autowired
    public CreditBureauEvaluateApiResource( final  ClientReadPlatformService clientReadPlatformService,
    		final  LoanAssembler loanAssembler,
            final DefaultToApiJsonSerializer<LoanAccountData> toApiJsonSerializer,
            final HighMarkProcessingService highMarkProcessingService,final ClientsApiResource clientsApiResource,NsChitGroupReadPlatformService nsChitGroupReadPlatformService) {
        this.clientReadPlatformService = clientReadPlatformService;
        this.loanAssembler = loanAssembler;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.highMarkProcessingService = highMarkProcessingService;
        this.clientsApiResource = clientsApiResource;
        this.nsChitGroupReadPlatformService = nsChitGroupReadPlatformService;
    }

    
    @SuppressWarnings({ "rawtypes", "unchecked"})
	@POST
	@Path("/evaluateLoan/{loanId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String evaluateCreditBureauLoanApplication(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {

    	 GenericResponseVO responseVO= null ;
        try {
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
       
        if(loan==null) {
        	responseVO = new GenericResponseVO(" ",404,"Loan Not Found",new java.util.Date().getTime());
        	return this.toApiJsonSerializer.serialize(responseVO);
        }
        
        
        
        responseVO = this.highMarkProcessingService.initateLoanCreditRequest(loan);
        return this.toApiJsonSerializer.serialize(responseVO);
        }
        catch(Exception e ) {
        	responseVO = new GenericResponseVO(" ",500,"Exception in Loan credit bureau evaluation",new java.util.Date().getTime());
        	return this.toApiJsonSerializer.serialize(responseVO);
        }
	}
    
    @SuppressWarnings({ "rawtypes", "unchecked"})
	@POST
	@Path("/evaluateClient/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String evaluateCreditBureauClientApplication(@PathParam("clientId") final Long clientId,@QueryParam("loanAmount") final Long AmontId) {

    	 GenericResponseVO responseVO= null ;
        try {
        final ClientData clientData = this.clientReadPlatformService.retrieveOne(clientId);
       
        if(clientData==null) {
        	responseVO = new GenericResponseVO(" ",404,"Client Data Not Found",new java.util.Date().getTime());
        	return this.toApiJsonSerializer.serialize(responseVO);
        }
        Double LoanAMount =  nsChitGroupReadPlatformService.retrieveById(AmontId).getChitValue();
        responseVO = this.highMarkProcessingService.initateClientCreditRequest(clientData,LoanAMount);
        
        JsonObject dataForUpdate = new JsonObject();
        dataForUpdate.addProperty("AmountApplied", AmontId);
        dataForUpdate.addProperty("locale", "en");
        this.clientsApiResource.update(clientId, dataForUpdate.toString());
        return this.toApiJsonSerializer.serialize(responseVO);
        }
        catch(Exception e ) {
        	responseVO = new GenericResponseVO(" ",500,"Exception in Client credit bureau evaluation",new java.util.Date().getTime());
        	return this.toApiJsonSerializer.serialize(responseVO);
        }
	}
}