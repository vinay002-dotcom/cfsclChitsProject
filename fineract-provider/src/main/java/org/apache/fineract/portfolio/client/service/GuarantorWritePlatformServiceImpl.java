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
package org.apache.fineract.portfolio.client.service;

import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitBidNotFoundException;
import org.apache.fineract.portfolio.client.domain.Guarantor;
import org.apache.fineract.portfolio.client.domain.GuarantorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

@Service
public class GuarantorWritePlatformServiceImpl implements GuarantorWritePlatformService
{
	private final GuarantorRepository guarantorRepository;
	
	@Autowired
	public GuarantorWritePlatformServiceImpl(final GuarantorRepository guarantorRepository) {
		super();
		this.guarantorRepository = guarantorRepository;
	}


	@Transactional
	@Override
	public CommandProcessingResult AddGuarantor(JsonObject command) {

		try {
			//this.fromApiJsonDeserializer.validateForCreate(command.json());
			
			// final Long chitSubscriberId = command.longValueOfParameterNamed("chitSubscriberId");
			// ChitGroupSubscriberData subscriber = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscriberId);
			
			

			Guarantor gurantr = Guarantor.create(command);
			Guarantor newchitGroup = this.guarantorRepository.save(gurantr);

			CommandProcessingResult result = new CommandProcessingResultBuilder() //
					.withCommandId(newchitGroup.getId()) //
					.withEntityId(newchitGroup.getId()) //
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
	public CommandProcessingResult UpdateGuarantor(JsonObject command,Long id) 
	{
		try {
			//this.fromApiJsonDeserializer.validateForUpdate(command.json(), id);
			final Guarantor gurantr = this.guarantorRepository.findById(id)
					.orElseThrow(() -> new ChitBidNotFoundException(id));
			final Map<String, Object> changesOnly = gurantr.Update(command);

			if (!changesOnly.isEmpty()) {
				this.guarantorRepository.saveAndFlush(gurantr);
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
	public CommandProcessingResult deleteGuarantor(final Long id) {
       
        final Guarantor guarntor = this.guarantorRepository.findById(id).orElseThrow(() -> new ChitBidNotFoundException(id));
        this.guarantorRepository.delete(guarntor);
        return new CommandProcessingResultBuilder().withEntityId(guarntor.getId()).build();
	}
	
	
	
	private void handleChitBidsDataIntegrityIssues(final JsonObject command, final Throwable realCause,
			final Exception dve) {

		throw new PlatformDataIntegrityException("error.msg.NsChitGroup.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}


}
