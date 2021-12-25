--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

ALTER TABLE `m_product_loan`
    ADD COLUMN `multi_disburse_loan` tinyint NOT NULL DEFAULT '0' AFTER `close_date`,
    ADD COLUMN `max_tranche_count` INT NULL DEFAULT NULL AFTER `multi_disburse_loan`,
    ADD COLUMN `outstanding_loan_balance` DECIMAL(19,6) NULL DEFAULT NULL AFTER `max_tranche_count`;

CREATE TABLE `m_loan_disbursement_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `loan_id` BIGINT NOT NULL,
    `expected_disburse_date` DATETIME NOT NULL,
    `disbursedon_date` DATETIME NULL,
    `principal` DECIMAL(19,6) NOT NULL,
    `approved_principal` DECIMAL(19,6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_loan_disbursement_detail_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;

ALTER TABLE `m_loan`
    ADD COLUMN `fixed_emi_amount` DECIMAL(19,6) NULL AFTER `loan_product_counter`,
    ADD COLUMN `approved_principal` DECIMAL(19,6) NOT NULL AFTER `principal_amount`,
    ADD COLUMN `max_outstanding_loan_balance` DECIMAL(19,6) NULL DEFAULT NULL AFTER `fixed_emi_amount`;

UPDATE m_loan ml  SET ml.approved_principal = ml.principal_amount;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_DISBURSEMENTDETAIL', 'DISBURSEMENTDETAIL', 'UPDATE', 0);

CREATE TABLE `m_loan_term_variations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `loan_id` BIGINT NOT NULL,
    `term_type` SMALLINT NOT NULL,
    `applicable_from` DATE NOT NULL,
    `term_value` DECIMAL(19,6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_loan_id_m_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;
