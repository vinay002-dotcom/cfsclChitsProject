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
package org.apache.fineract.portfolio.account.service;

import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.account.domain.BranchesAccount;
import org.apache.fineract.portfolio.account.domain.BranchesAccountRepository;
import org.apache.fineract.portfolio.account.exception.BranchNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

@Service
public class BranchesAccountWritePlatformServiceImpl implements BranchesAccountWritePlatformService {

	private static final Logger LOG = LoggerFactory.getLogger(BranchesAccountWritePlatformServiceImpl.class);
	private final BranchesAccountRepository branchesAccountRepository;
	private final PlatformSecurityContext platformSecurityContext;
	
	@Autowired
	public BranchesAccountWritePlatformServiceImpl(PlatformSecurityContext platformSecurityContext,
			BranchesAccountRepository branchesAccountRepository) {
		this.platformSecurityContext =platformSecurityContext;
		this.branchesAccountRepository = branchesAccountRepository;
	}

	@Transactional
	@Override
	public CommandProcessingResult createBranches(JsonObject command) {

		try {
			BranchesAccount branchesData = BranchesAccount.create(command);
			
			BranchesAccount newBranchesData = this.branchesAccountRepository.save(branchesData);
			CommandProcessingResult result = new CommandProcessingResultBuilder().withCommandId(branchesData.getBranchId()).withEntityId(branchesData.getBranchId()).build();
			
			return result;
		}catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleBranchesAccountDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
				return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleBranchesAccountDataIntegrityIssues(command, throwable, dve);
			return CommandProcessingResult.empty();
		}
		
	}
	
	@Transactional
	@Override
	public CommandProcessingResult updateBranches(Long branchId, JsonObject command) {

		try {
				final BranchesAccount branchToUpdate = this.branchesAccountRepository.findById(branchId)
						.orElseThrow(() -> new BranchNotFoundException(branchId));
				final Map<String,Object> changesValues = branchToUpdate.update(command);
				
				if(!changesValues.isEmpty())
				{
					this.branchesAccountRepository.saveAndFlush(branchToUpdate);
				}
				return new CommandProcessingResultBuilder().withCommandId(branchId).withEntityId(branchId).with(changesValues).build();
	
			} catch (final JpaSystemException | DataIntegrityViolationException dve) {
					handleBranchesAccountDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
					return CommandProcessingResult.empty();
			} catch (final PersistenceException dve) {
					Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
					handleBranchesAccountDataIntegrityIssues(command, throwable, dve);
					return CommandProcessingResult.empty();
				}
			}

	
	
	private void handleBranchesAccountDataIntegrityIssues(final JsonObject command, final Throwable realCause,
			final Exception dve) {

		// if (realCause.getMessage().contains("external_id")) {
		// final String externalId = command.stringValueOfParameterNamed("externalId");
		// throw new
		// PlatformDataIntegrityException("error.msg.staff.duplicate.externalId",
		// "Staff with externalId `" + externalId + "` already exists", "externalId",
		// externalId);
		// } 

		
		throw new PlatformDataIntegrityException("error.msg.Branch.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}

	

}
