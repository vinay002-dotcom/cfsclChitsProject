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
package org.apache.fineract.portfolio.ChitGroup.service;



import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public interface ChitGroupReadPlatformService 
{
	ChitGroupData retrieveChitGroup(Long id);

    Page<ChitGroupData> retrievePagedAll(SearchParameters searchParameters, PaginationParameters parameters);
   
    Collection<ChitGroupData> retrieveAll(SearchParameters searchParameters, PaginationParameters parameters);
    
    
    
    Collection<ChitGroupData> retrieveAllActiveChitGroups();

    Collection<ChitGroupSubscriberData> getChitSubscribers(Long id);
    
    ChitGroupSubscriberData getChitSubscriber(Long id);
    
	ChitGroupSubscriberData getChitSubscriberWithClient(Long id, Long accNo);

	Collection<ChitGroupSubscriberData> getChitSubscribersUsingClientId(Long id);
	
	ChitGroupSubscriberData getChitSubscriberUsingChitIDClientId(Long chitId,Long clientId,Long chitNumber);
	
	JsonObject getSubscriberListoFClosedGroups(Long chitId);
	
	public ArrayList<JsonElement> retriveDashBoardData(Long id);
	
	public ArrayList<JsonElement> retriveChitGroupCycleData(Long chitId, int cycleNumber);
	
	public JsonObject getSingleSubscriberClosedGroups(Long clientId,Long chitId,Long chitNum);
	
	Long getSubsciberTicketCounts(Long clientId);

}

