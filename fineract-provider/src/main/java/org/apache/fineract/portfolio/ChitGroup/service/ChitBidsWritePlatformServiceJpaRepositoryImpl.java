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

import org.apache.fineract.accounting.journalentry.service.AccountingProcessorHelper;
import org.apache.fineract.accounting.journalentry.service.JournalEntryReadPlatformService;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.accounting.producttoaccountmapping.service.GlAccountChitReadPlatformService;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;

import org.apache.fineract.portfolio.ChitGroup.domain.ChitBids;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitBidsRepository;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitCycle;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitCycleRepository;
import org.apache.fineract.portfolio.ChitGroup.exception.ChitBidNotFoundException;
import org.apache.fineract.portfolio.ChitGroup.serialization.ChitBidsCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.google.gson.JsonObject;


@Service
public class ChitBidsWritePlatformServiceJpaRepositoryImpl implements ChitBidsWritePlatformService {
	//private static final Logger LOG = LoggerFactory.getLogger(ChitGroupWritePlatformServiceJpaRepositoryImpl.class);
	private final ChitBidsCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ChitGroupReadPlatformService chitGroupReadPlatformService;
	private final ChitBidsRepository chitBidsRepository;
	private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ChitCycleRepository chitCycleRepository;
    private final ChitBidsReadPlatformService chitBidsReadPlatformService;
    private final JournalEntryReadPlatformService journalEntryReadPlatformService;
    private final AccountingProcessorHelper accountingProcessorHelper;
    private final GlAccountChitReadPlatformService glAccountChitReadPlatformService;
    final ChitCycleReadPlatformService chitCycleReadPlatformService;

    final JournalEntryWritePlatformService journalEntryWritePlatformService;
    @Autowired
	public ChitBidsWritePlatformServiceJpaRepositoryImpl(
			final PlatformSecurityContext context,
			final ChitBidsCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final ChitGroupReadPlatformService chitGroupReadPlatformService,
			final ChitBidsRepository chitBidsRepository,
			final RoutingDataSource dataSource,final ChitCycleRepository chitCycleRepository,
			final ChitBidsReadPlatformService chitBidsReadPlatformService,
			final JournalEntryReadPlatformService journalEntryReadPlatformService,
			final AccountingProcessorHelper accountingProcessorHelper,
			final GlAccountChitReadPlatformService glAccountChitReadPlatformService,
			final ChitCycleReadPlatformService chitCycleReadPlatformService,
			 final JournalEntryWritePlatformService journalEntryWritePlatformService) {
		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.chitBidsRepository = chitBidsRepository;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.chitCycleRepository = chitCycleRepository;
		this.chitBidsReadPlatformService = chitBidsReadPlatformService;
		this.journalEntryReadPlatformService = journalEntryReadPlatformService;
		this.accountingProcessorHelper = accountingProcessorHelper;
		this.glAccountChitReadPlatformService = glAccountChitReadPlatformService;
		this.chitCycleReadPlatformService = chitCycleReadPlatformService;

		this.journalEntryWritePlatformService = journalEntryWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult createChitBids(final JsonCommand command) {

		try {
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			
			// final Long chitSubscriberId = command.longValueOfParameterNamed("chitSubscriberId");
			// ChitGroupSubscriberData subscriber = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscriberId);

			ChitBids chit = ChitBids.create(command);
			ChitBids newchitBid = this.chitBidsRepository.save(chit);

			CommandProcessingResult result = new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(newchitBid.getId()) //
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
	public CommandProcessingResult updateChitBids(Long id, JsonCommand command) {
		try {
			this.fromApiJsonDeserializer.validateForUpdate(command.json(), id);
			final ChitBids chitBidsForUpdate = this.chitBidsRepository.findById(id)
					.orElseThrow(() -> new ChitBidNotFoundException(id));
			final Map<String, Object> changesOnly = chitBidsForUpdate.update(command);

			if (!changesOnly.isEmpty()) {
				this.chitBidsRepository.saveAndFlush(chitBidsForUpdate);
			}

			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(id)
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
	public CommandProcessingResult updateChitBidsJson(Long id, JsonObject command) {
		try {
			
			final ChitBids chitBidsForUpdate = this.chitBidsRepository.findById(id)
					.orElseThrow(() -> new ChitBidNotFoundException(id));
			final Map<String, Object> changesOnly = chitBidsForUpdate.updateJson(command);

			if (!changesOnly.isEmpty()) {
				this.chitBidsRepository.saveAndFlush(chitBidsForUpdate);
			}

			return new CommandProcessingResultBuilder().withCommandId(chitBidsForUpdate.getId()).withEntityId(id)
					.with(changesOnly).build();
		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitBidsDataIntegrityIssues(null, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitBidsDataIntegrityIssues(null, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}
	

	@Transactional
	@Override
	public CommandProcessingResult deleteChitBids(final Long id) {
        this.context.authenticatedUser();
        final ChitBids chitBidForDelete = this.chitBidsRepository.findById(id).orElseThrow(() -> new ChitBidNotFoundException(id));
        this.chitBidsRepository.delete(chitBidForDelete);
        return new CommandProcessingResultBuilder().withEntityId(chitBidForDelete.getId()).build();
	}
	
	

	@Transactional
	@Override
	public CommandProcessingResult createChitGroupCycle(Long chitId,JsonObject command) {

		try {
			//this.fromApiJsonDeserializer.validateForCreate(command.json());
			
			// final Long chitSubscriberId = command.longValueOfParameterNamed("chitSubscriberId");
			// ChitGroupSubscriberData subscriber = this.chitGroupReadPlatformService.getChitSubscriber(chitSubscriberId);

			ChitCycle chitGroupCycle = ChitCycle.create(chitId,command);
			ChitCycle newchitGroupCycle = this.chitCycleRepository.save(chitGroupCycle);

			CommandProcessingResult result = new CommandProcessingResultBuilder() //
					.withCommandId(chitId) //
					.withEntityId(newchitGroupCycle.getId()) //
					.build();
			return result;

		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitBidsDataIntegrityIssues(null, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitBidsDataIntegrityIssues(null, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}
	
	@Transactional
	@Override
	public CommandProcessingResult updateChitGroupCycle(Long chitgroupid,JsonObject command) {
		try {
			//this.fromApiJsonDeserializer.validateForUpdate(command.json(), id);
			final ChitCycle chitGroupCycleForUpdate = this.chitCycleRepository.findById(chitgroupid)
					.orElseThrow(() -> new ChitBidNotFoundException(chitgroupid));
			final Map<String, Object> changesOnly = chitGroupCycleForUpdate.update(command);

			if (!changesOnly.isEmpty()) {
				this.chitCycleRepository.saveAndFlush(chitGroupCycleForUpdate);
			}

			return new CommandProcessingResultBuilder().withCommandId(chitgroupid).withEntityId(chitgroupid)
					.with(changesOnly).build();
		} catch (final JpaSystemException | DataIntegrityViolationException dve) {
			handleChitBidsDataIntegrityIssues(null, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handleChitBidsDataIntegrityIssues(null, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}

	
	@Transactional
	@Override
	public CommandProcessingResult deleteChitGroupCycle(final Long chitgroupid) {
        this.context.authenticatedUser();
        final ChitCycle chitGroupCycleForDelete = this.chitCycleRepository.findById(chitgroupid).orElseThrow(() -> new ChitBidNotFoundException(chitgroupid));
        this.chitCycleRepository.delete(chitGroupCycleForDelete);
        return new CommandProcessingResultBuilder().withEntityId(chitGroupCycleForDelete.getId()).build();
	}
	

	private void handleChitBidsDataIntegrityIssues(final JsonCommand command, final Throwable realCause,
			final Exception dve) {

		// if (realCause.getMessage().contains("external_id")) {
		// final String externalId = command.stringValueOfParameterNamed("externalId");
		// throw new
		// PlatformDataIntegrityException("error.msg.staff.duplicate.externalId",
		// "Staff with externalId `" + externalId + "` already exists", "externalId",
		// externalId);
		// } 

		//LOG.error("Error occured.", dve);
		throw new PlatformDataIntegrityException("error.msg.chitbid.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}
	
	
	
	


}
