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

CREATE TABLE `acc_accounting_rule` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(100) NULL DEFAULT NULL,
    `office_id` BIGINT NULL DEFAULT NULL,
    `debit_account_id` BIGINT NOT NULL,
    `credit_account_id` BIGINT NOT NULL,
    `description` VARCHAR(500) NULL DEFAULT NULL,
    `system_defined` tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `accounting_rule_name_unique` (`name`),
    INDEX `FK_acc_accounting_rule_acc_gl_account_debit` (`debit_account_id`),
    INDEX `FK_acc_accounting_rule_acc_gl_account_credit` (`credit_account_id`),
    INDEX `FK_acc_accounting_rule_m_office` (`office_id`),
    CONSTRAINT `FK_acc_accounting_rule_acc_gl_account_credit` FOREIGN KEY (`credit_account_id`) REFERENCES `acc_gl_account` (`id`),
    CONSTRAINT `FK_acc_accounting_rule_acc_gl_account_debit` FOREIGN KEY (`debit_account_id`) REFERENCES `acc_gl_account` (`id`),
    CONSTRAINT `FK_acc_accounting_rule_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;

CREATE TABLE `acc_auto_posting` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(500) NULL DEFAULT NULL,
    `office_id` BIGINT NULL DEFAULT NULL,
    `product_type_enum` SMALLINT NOT NULL,
    `product_id` BIGINT NULL DEFAULT NULL,
    `charge_id` BIGINT NULL DEFAULT NULL,
    `event` INT NOT NULL,
    `event_attribute` INT NULL DEFAULT NULL,
    `accounting_rule_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `auto_posting_name_unique` (`name`),
    INDEX `FK_acc_auto_posting_m_office` (`office_id`),
    INDEX `FK_acc_auto_posting_acc_accounting_rule` (`accounting_rule_id`),
    INDEX `FK_acc_auto_posting_m_code` (`event`),
    INDEX `FK_acc_auto_posting_m_charge` (`charge_id`),
    INDEX `FK_acc_auto_posting_m_code_value` (`event_attribute`),
    CONSTRAINT `FK_acc_auto_posting_acc_accounting_rule` FOREIGN KEY (`accounting_rule_id`) REFERENCES `acc_accounting_rule` (`id`),
    CONSTRAINT `FK_acc_auto_posting_m_charge` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
    CONSTRAINT `FK_acc_auto_posting_m_code` FOREIGN KEY (`event`) REFERENCES `m_code` (`id`),
    CONSTRAINT `FK_acc_auto_posting_m_code_value` FOREIGN KEY (`event_attribute`) REFERENCES `m_code_value` (`id`),
    CONSTRAINT `FK_acc_auto_posting_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;
