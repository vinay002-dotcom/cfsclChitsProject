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

import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;

import org.apache.fineract.portfolio.ChitGroup.domain.ChitCharge;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitChargeRepository;

import org.apache.fineract.portfolio.ChitGroup.exception.ChitBidNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

@Service
public class ChitChargeWritePlatformServicesImpl implements ChitChargeWritePlatformService
{
	private final ChitChargeRepository chitchargeRepository;
	
	@Autowired
	public ChitChargeWritePlatformServicesImpl(ChitChargeRepository chitchargeRepository) {
		super();
		this.chitchargeRepository = chitchargeRepository;
	}


	@Transactional
	@Override
	public CommandProcessingResult createChitCharge(JsonObject command) {

		try {
			//this.fromApiJsonDeserializer.validateForCreate(command.json());
			
			// final Long chitSubscriberId = command.longValueOfParameterNamed("chitSubscriberId");
			// ChitGroupSubscriberData subscriber = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscriberId);
			
			

			ChitCharge chitChargeCycle = ChitCharge.create(command);
			ChitCharge newchitChargeCycle = this.chitchargeRepository.save(chitChargeCycle);

			CommandProcessingResult result = new CommandProcessingResultBuilder() //
					.withCommandId(newchitChargeCycle.getId()) //
					.withEntityId(newchitChargeCycle.getId()) //
					.build();
			return result;

		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitBidsDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitBidsDataIntegrityIssues(command, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}
	
	@Transactional
	@Override
	public CommandProcessingResult updateChitCharge(Long id ,JsonObject command) 
	{
		try {
			//this.fromApiJsonDeserializer.validateForUpdate(command.json(), id);
			final ChitCharge chitChargeForUpdate = this.chitchargeRepository.findById(id)
					.orElseThrow(() -> new ChitBidNotFoundException(id));
			final Map<String, Object> changesOnly = chitChargeForUpdate.update(command);

			if (!changesOnly.isEmpty()) {
				this.chitchargeRepository.saveAndFlush(chitChargeForUpdate);
			}

			return new CommandProcessingResultBuilder().withCommandId(id).withEntityId(id)
					.with(changesOnly).build();
		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitBidsDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitBidsDataIntegrityIssues(command, throwable, dve);
			return CommandProcessingResult.empty();
		}

	}
	
	@Transactional
	@Override
	public CommandProcessingResult deleteChitCharge(final Long id) {
       
        final ChitCharge chitchargeForDelete = this.chitchargeRepository.findById(id).orElseThrow(() -> new ChitBidNotFoundException(id));
        this.chitchargeRepository.delete(chitchargeForDelete);
        return new CommandProcessingResultBuilder().withEntityId(chitchargeForDelete.getId()).build();
	}
	
	
	
	private void handleChitBidsDataIntegrityIssues(final JsonObject command, final Throwable realCause,
			final Exception dve) {

		// if (realCause.getMessage().contains("external_id")) {
		// final String externalId = command.stringValueOfParameterNamed("externalId");
		// throw new
		// PlatformDataIntegrityException("error.msg.staff.duplicate.externalId",
		// "Staff with externalId `" + externalId + "` already exists", "externalId",
		// externalId);
		// } 

		
		throw new PlatformDataIntegrityException("error.msg.chitCharge.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}


}
