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
package org.apache.fineract.portfolio.client.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.ChitGroup.exception.SomethingWentWrongException;

import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.ClientTransaction;
import org.apache.fineract.portfolio.client.domain.ClientTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.ClientTransactionReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientTransactionWritePlatformService;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

@Component
public class ClientTransactionChitAdvanceAdjustment
{

	private final ClientTransactionReadPlatformService clientTransactionReadPlatformService;
	private final ClientTransactionWritePlatformService clientTransactionWritePlatformService;
	private final ClientRepositoryWrapper clientRepository;
	private final ClientTransactionRepositoryWrapper clientTransactionRepository;
	private final PlatformSecurityContext context;


	@Autowired
	public ClientTransactionChitAdvanceAdjustment(ClientTransactionReadPlatformService clientTransactionReadPlatformService,final ClientTransactionWritePlatformService clientTransactionWritePlatformService,
			final ClientTransactionRepositoryWrapper clientTransactionRepository,final ClientRepositoryWrapper clientRepository,final PlatformSecurityContext context) {

		this.clientTransactionReadPlatformService = clientTransactionReadPlatformService;
		this.clientTransactionWritePlatformService = clientTransactionWritePlatformService;
		this.clientTransactionRepository = clientTransactionRepository;
		this.clientRepository = clientRepository;
		this.context = context;
	}
	@SuppressWarnings("deprecation")
	public void ClientTransactionAdjustment(Double toDeduct,final Long ClientId)
	{
		try {
		
			Collection<ClientTransactionData> CollectionOfTransaction = this.clientTransactionReadPlatformService.retrieveAllTransactionsUsingId(ClientId);



			Iterator<ClientTransactionData> iterator =  CollectionOfTransaction.iterator();

			

			Map<Long,BigDecimal> SetOfTransAndAmount = new LinkedHashMap<>(9);
			
		

			BigDecimal totalAmount = BigDecimal.ZERO;
			while(iterator.hasNext())
			{
				
				ClientTransactionData clientTransactionData = iterator.next();
				
				BigDecimal Amount = clientTransactionData.getAmount();
				Long tranId = clientTransactionData.getId();
				totalAmount = totalAmount.add(Amount);
				SetOfTransAndAmount.put(tranId, Amount);
			}
				
			if(totalAmount.doubleValue() == toDeduct)
			{
				
				Iterator<ClientTransactionData> clientTransactionArray =  CollectionOfTransaction.iterator();

				while(clientTransactionArray.hasNext())
				{
					
					ClientTransactionData clientTransactionData= clientTransactionArray.next();
					Long clientTransactionId = clientTransactionData.getId() ;
					JsonObject updateAdjustedTrue = new JsonObject();
					updateAdjustedTrue.addProperty("adjusted", true);
					clientTransactionWritePlatformService.UpdateClientTransaction(ClientId, clientTransactionId, updateAdjustedTrue);
				}
				
				return;
			}

			//toDeduct exactly matches one of the values in clientTransactionArray
			for(Map.Entry<Long, BigDecimal> itr : SetOfTransAndAmount.entrySet())
			{
				if(itr.getValue().doubleValue() == toDeduct)	
				{
					
					Long tranId = itr.getKey();
					JsonObject updateAdjustedTrue = new JsonObject();
					updateAdjustedTrue.addProperty("adjusted", true);
					clientTransactionWritePlatformService.UpdateClientTransaction(ClientId, tranId, updateAdjustedTrue);
					return;
				}
			}
			

			//sort clientTrans[] with highest to lowest amount value
			int size = CollectionOfTransaction.size();
			
			if(size!=0)
			{
				List<ClientTransactionData> ListOfTransactions = new ArrayList<ClientTransactionData>(CollectionOfTransaction);
				System.out.println("3");
				List<ClientTransactionData> sortedList = this.sort(ListOfTransactions);
				Collection<ClientTransactionData> sortedTransactionList = new ArrayList<ClientTransactionData>();
				
			
				for(int i = sortedList.size()-1 ; i>=0 ; i--)
				{
					
					ClientTransactionData obj = sortedList.get(i);
					sortedTransactionList.add(obj);	
					
				}
				
				Iterator<ClientTransactionData> clientTransactionArray =  sortedTransactionList.iterator();
				
				while(clientTransactionArray.hasNext())
				{
					
					ClientTransactionData clientTransactionData = clientTransactionArray.next();
					BigDecimal AmountInList = clientTransactionData.getAmount();
					
					//Split and return
					if(AmountInList.doubleValue()>=toDeduct)
					{
						
					
						Date transacDate = null;
						Date createdOn = null;
						PaymentDetailData paymentData = clientTransactionData.getPaymentDetailData();
						if( clientTransactionData.getDate()!=null)
						{
							LocalDate trandate = clientTransactionData.getDate();
							transacDate = java.sql.Date.valueOf(trandate);
						}
						if( clientTransactionData.getCreateddate()!=null)
						{
							LocalDate trandate = clientTransactionData.getCreateddate().toLocalDate();
							createdOn = java.sql.Date.valueOf(trandate);
						}
					
						final Client client = this.clientRepository.findOneWithNotFoundDetection(ClientId);
						BigDecimal Amount  = AmountInList.subtract(BigDecimal.valueOf(toDeduct));
						ClientTransaction data = new ClientTransaction(client,client.getOffice(),null,clientTransactionData.getCurrency().code(),clientTransactionData.getType().getId().intValue(),
								transacDate,Amount,clientTransactionData.getReversed(),clientTransactionData.getExternalId(),createdOn,this.context.getAuthenticatedUserIfPresent(),null,false,null,paymentData.getId(),true);

						clientTransactionRepository.saveAndFlush(data);
						
						Long tranId = clientTransactionData.getId();
						JsonObject updateAdjustedTrue = new JsonObject();
						updateAdjustedTrue.addProperty("adjusted", true);
						updateAdjustedTrue.addProperty("amount", AmountInList.subtract(Amount));
						clientTransactionWritePlatformService.UpdateClientTransaction(ClientId, tranId, updateAdjustedTrue);
					
						
					}
					else
					{
					
						Long tranId = clientTransactionData.getId();
						toDeduct = toDeduct-AmountInList.doubleValue();
						JsonObject updateAdjustedTrue = new JsonObject();
						updateAdjustedTrue.addProperty("adjusted", true);
						clientTransactionWritePlatformService.UpdateClientTransaction(ClientId, tranId, updateAdjustedTrue);
					}
					
			
					
				}
				return;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new SomethingWentWrongException();
		}
	}
	
	List<ClientTransactionData> sort(List<ClientTransactionData> transactions)
	{
		List<ClientTransactionData> sortedList = new ArrayList<ClientTransactionData>();
		Map<Object,BigDecimal> itr = new LinkedHashMap<>();
		for(int i =  0 ; i < transactions.size() ; i++)
		{
			Object obj = transactions.get(i);
			if(obj instanceof ClientTransactionData)
			{
				BigDecimal amount = ((ClientTransactionData) obj).getAmount();
				itr.put(obj,amount);
			}
		}
		
		final Map<Object,BigDecimal> sortedByCount = itr.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		for(Map.Entry<Object, BigDecimal> itr1 : sortedByCount.entrySet())
		{
			sortedList.add((ClientTransactionData) itr1.getKey());
		}
		
		return sortedList;
	}

}
