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
package org.apache.fineract.portfolio.voucher.service;

import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.voucher.Exception.JournalVoucherException;
import org.apache.fineract.portfolio.voucher.domain.JournalVoucherRepository;
import org.apache.fineract.portfolio.voucher.domain.JournlVoucher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;


@Service
public class JournalVoucherWritePlatformServiceImpl implements JournalVoucherWritePlatformService{

	private final JournalVoucherRepository jornalVoucherRepository;
	private final FromJsonHelper fromJsonHelper;
	private final JournalVoucherReadPlatformService journalVoucherReadPlatformService;
	private final CodeValueReadPlatformService codeValueReadPlatformService;

	
	@Autowired
	JournalVoucherWritePlatformServiceImpl(JournalVoucherRepository jornalVoucherRepository,
			 final FromJsonHelper fromJsonHelper,final JournalVoucherReadPlatformService journalVoucherReadPlatformService,
			 final CodeValueReadPlatformService codeValueReadPlatformService){
		this.jornalVoucherRepository = jornalVoucherRepository;
		this.fromJsonHelper = fromJsonHelper;
		this.journalVoucherReadPlatformService =journalVoucherReadPlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createJournalVoucher(JsonObject command) {
		try {
			//this.fromApiJsonDeserializer.validateForCreate(command.json());
			JournlVoucher ps = JournlVoucher.create(command);
			JournlVoucher newPs = this.jornalVoucherRepository.save(ps);

			CommandProcessingResult result = new CommandProcessingResultBuilder() //
					.withCommandId(ps.getId()) //
					.withEntityId(newPs.getId()) //
					.build();
			return result;
			
		} catch (final JpaSystemException   | DataIntegrityViolationException  dve ) {
			handlePaymentStatusDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
			return CommandProcessingResult.empty();
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handlePaymentStatusDataIntegrityIssues(command, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateJournalVoucher(Long id, JsonObject command) {
		try {
			final JournlVoucher JournalVoucherForUpdate = this.jornalVoucherRepository.findById(id)
						.orElseThrow(() -> new JournalVoucherException(id));
			final Map<String, Object> changesOnly = JournalVoucherForUpdate.update(command);
			if (!changesOnly.isEmpty()) {
				this.jornalVoucherRepository.saveAndFlush(JournalVoucherForUpdate);
			}
			return new CommandProcessingResultBuilder().withCommandId(id).withEntityId(id).build();
			
		} catch (final PersistenceException dve) {
			Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
			handlePaymentStatusDataIntegrityIssues(command, throwable, dve);
			return CommandProcessingResult.empty();
		}
	}

	private void handlePaymentStatusDataIntegrityIssues(final JsonObject command, final Throwable realCause,
			final Exception dve) {

		// if (realCause.getMessage().contains("external_id")) {
		// final String externalId = command.stringValueOfParameterNamed("externalId");
		// throw new
		// PlatformDataIntegrityException("error.msg.staff.duplicate.externalId",
		// "Staff with externalId `" + externalId + "` already exists", "externalId",
		// externalId);
		// } 

		//LOG.error("Error occured.", dve);
		throw new PlatformDataIntegrityException("error.msg.paymentStatusId.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}

	@Transactional
	@Override
	public void addingDataFromAccJounalEntry(String apiRequestBody, String transactionId) {
		
		try {
			JsonObject object = this.fromJsonHelper.parse(apiRequestBody).getAsJsonObject();
			
			Long valueId = object.get("voucherTypeId").getAsLong();
					
			CodeValueData value = this.codeValueReadPlatformService.retrieveCodeValue(valueId);
			if(!value.getName().equalsIgnoreCase("Journal Entry")) {
				Integer voucherTypeId = object.get("voucherTypeId").getAsInt();
				String voucherNumber = object.get("voucherNumber").getAsString();
				String vendorName = object.get("vendorName").getAsString();
				
				JsonObject obj =new JsonObject();
				obj.addProperty("journalTransactionId", transactionId);
				obj.addProperty("voucherTypeId", voucherTypeId);
				obj.addProperty("voucherNumber", voucherNumber);
				obj.addProperty("vendorName", vendorName);
				this.createJournalVoucher(obj);
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	@Override
	public String incrementVoucherNumber(Integer id) {
		 String data = this.journalVoucherReadPlatformService.getVoucherNumber(id);
		 String realValue = null;
		 if(data!=null) {
			 String onlyString = data.substring(0, 3);
			 String numString = data.substring(3);
			 Integer num = Integer.parseInt(numString);
			 num += 1;
			 String actualValue = onlyString + num;
			 realValue = actualValue;
			}else {
				CodeValueData codeDatas = this.codeValueReadPlatformService.retrieveCodeValue(id.longValue());
				String desrciption = codeDatas.getDescription();
				realValue =  desrciption+"-1";
			}	 
		return realValue;
	}
}
