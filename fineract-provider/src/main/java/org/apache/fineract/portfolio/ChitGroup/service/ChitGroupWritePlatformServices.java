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
import java.sql.SQLException;
import java.time.LocalDate;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;


public interface ChitGroupWritePlatformServices 
{
	 CommandProcessingResult createChitGroup(JsonCommand command);
	 CommandProcessingResult updateChitGroup(Long id,JsonCommand command);
	 CommandProcessingResult createChitGroupSubscriber(final Long chitid, final JsonObject data) throws SQLException;
	 CommandProcessingResult updateChitgroupSubscriber(final Long SubscriberId , final JsonObject data);
	 CommandProcessingResult firstAuctionToCompany(Long id);
	 CommandProcessingResult prizeMoneyCalculations(Long id, Long cycleNumber,JsonObject body);
	 public void PrizeMoneyPosting(Long chitId,Long cycleId);
	 
	 void activateChitGroup(final Long chitid, final JsonElement data) throws SQLException;
	 void deleteChitGroupSubscriber(final Long id) throws SQLException ;
	 void deleteChitGroup(final Long id) throws SQLException ;
	// String moveChitCycle(final JsonObject command);
	 void move(Long chitId);
	String closeChitGroup(final Long chitId,final LocalDate dateOfclosing);
	
	public JsonObject winnerspayable(Long ChitSubsid,Double Amount,String date,Long BidId,Long accId,JsonObject apiRequestBodyAsJson);
	JsonObject reBid(Long chitId, Long oldSubId,Long newSubId, Long cycle, LocalDate date, Long bidderparticipationId);
	
	JsonObject bidAdvance(Long chitId, Long subscriberId, Long bidAmountInPercent);
	JsonObject bidAdvancePayOut(Long chitId, Long subscriberId, JsonObject apiRequestBodyAsJson);
	
	JsonObject forClosure(Long chitId, Long clientId, Long ticketNumber);
	JsonObject forClosureApproval(Long subscriberId, JsonObject command);
	public JsonObject forClosureAdjust(Long subscriberId,JsonObject command);
	
	JsonObject terminatePayout(Long chitId, Long subscriberId, Double paybleAmount);
	JsonObject  terminateAdjust(Long subscriberId,JsonObject command);
	JsonObject terminateApproval(Long subscriberId, JsonObject command);
	JsonObject terminateSubscriber(Long chitId, Long clientId, Long ticketNumber);
	
	JsonObject replaceSubscriber(Long chitId, Long clientId);
	
}
