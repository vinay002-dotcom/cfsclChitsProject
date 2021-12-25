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

package org.apache.fineract.portfolio.creditreport.service;





import org.apache.fineract.infrastructure.codes.domain.CodeValueRepository;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;

import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;

import org.apache.fineract.portfolio.creditreport.domain.CreditReports;
import org.apache.fineract.portfolio.creditreport.domain.CreditRepository;
import org.apache.fineract.portfolio.creditreport.serialization.CreditReportCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;



@Service
public class CreditReportsWritePlatformServiceImpl implements CreditReportsWritePlatformService 
{
	
	private final PlatformSecurityContext context;
	
	private final CreditRepository creditRepository;
	private final ClientRepositoryWrapper clientRepositoryWrapper;
	private final CreditReportCommandFromApiJsonDeserializer  apiJsonDeserializer;
	 private final FromJsonHelper fromApiJsonHelper;
	
	
	@Autowired
	public CreditReportsWritePlatformServiceImpl(final PlatformSecurityContext context,final CodeValueRepository codeValueRepository,
			final CreditRepository creditRepository,final ClientRepositoryWrapper clientRepositoryWrapper,final CreditReportCommandFromApiJsonDeserializer  apiJsonDeserializer
			, final FromJsonHelper fromApiJsonHelper)
	{
		this.context=context;
		
		this.creditRepository=creditRepository;
		this.clientRepositoryWrapper=clientRepositoryWrapper;
		this.apiJsonDeserializer=apiJsonDeserializer;
		this.fromApiJsonHelper = fromApiJsonHelper;
		
	}
	
	

	@Override
	public CommandProcessingResult addCreditReport(final long clientId,final String command) 
	{
		Client client=clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
		String bureau=null;
		String scoretype=null;
		String scorevalue=null;
		String scorecomments=null;
		String reportid=null;
		String dateofissue=null;
		Long Sid = client.getId();
		
        final JsonElement element = this.fromApiJsonHelper.parse(command);

        if (this.fromApiJsonHelper.extractStringNamed("bureau", element) != null) {
             bureau = this.fromApiJsonHelper.extractStringNamed("bureau", element);
        } 
        
        if (this.fromApiJsonHelper.extractStringNamed("scoretype", element) != null) {
             scoretype = this.fromApiJsonHelper.extractStringNamed("scoretype", element);
           
        }

        if (this.fromApiJsonHelper.extractStringNamed("scorevalue", element) != null) {
            scorevalue = this.fromApiJsonHelper.extractStringNamed("scorevalue", element);
            
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("scorecomments", element) != null) {
             scorecomments = this.fromApiJsonHelper.extractStringNamed("scorecomments", element);
           
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("reportid", element) != null) {
         reportid = this.fromApiJsonHelper.extractStringNamed("reportid", element);
          
        }
        
        if (this.fromApiJsonHelper.extractStringNamed("dateofissue", element) != null) {
            dateofissue = this.fromApiJsonHelper.extractStringNamed("dateofissue", element);
           
        }
	
		CreditReports creditreports=CreditReports.fromJson(client,bureau,scoretype,scorevalue,scorecomments,reportid,dateofissue,Sid);
		
		this.creditRepository.save(creditreports);
		
		return new CommandProcessingResultBuilder().withCommandId(client.getId())
				.withEntityId(creditreports.getId()).build();
		
		
	
	}



	@Override
	public CommandProcessingResult updateCreditReport(Long clientId, String command) {
		
	
		
		Client client=clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
		
		////System.out.println(creditRepository.existsById(clientId));
		creditRepository.deleteById(clientId);
		
		this.addCreditReport(clientId, command);
		
		return new CommandProcessingResultBuilder().withCommandId(client.getId())
				.withEntityId(client.getId()).build();
		
		
		
	}
	
	
}
