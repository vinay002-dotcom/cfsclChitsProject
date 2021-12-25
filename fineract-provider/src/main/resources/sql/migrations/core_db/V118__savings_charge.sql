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

CREATE TABLE `m_savings_account_charge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `savings_account_id` BIGINT NOT NULL,
    `charge_id` BIGINT NOT NULL,
    `is_penalty` tinyint NOT NULL DEFAULT '0',
    `charge_time_enum` SMALLINT NOT NULL,
    `due_for_collection_as_of_date` DATE NULL DEFAULT NULL,
    `charge_calculation_enum` SMALLINT NOT NULL,
    `calculation_percentage` DECIMAL(19,6) NULL DEFAULT NULL,
    `calculation_on_amount` DECIMAL(19,6) NULL DEFAULT NULL,
    `amount` DECIMAL(19,6) NOT NULL,
    `amount_paid_derived` DECIMAL(19,6) NULL DEFAULT NULL,
    `amount_waived_derived` DECIMAL(19,6) NULL DEFAULT NULL,
    `amount_writtenoff_derived` DECIMAL(19,6) NULL DEFAULT NULL,
    `amount_outstanding_derived` DECIMAL(19,6) NOT NULL DEFAULT '0.000000',
    `is_paid_derived` tinyint NOT NULL DEFAULT '0',
    `waived` tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    INDEX `charge_id` (`charge_id`),
    INDEX `m_savings_account_charge_ibfk_2` (`savings_account_id`),
    CONSTRAINT `m_savings_account_charge_ibfk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
    CONSTRAINT `m_savings_account_charge_ibfk_2` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`)
)
ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;


CREATE TABLE `m_savings_product_charge` (
    `savings_product_id` BIGINT NOT NULL,
    `charge_id` BIGINT NOT NULL,
    PRIMARY KEY (`savings_product_id`, `charge_id`),
    INDEX `charge_id` (`charge_id`),
    CONSTRAINT `m_savings_product_charge_ibfk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
    CONSTRAINT `m_savings_product_charge_ibfk_2` FOREIGN KEY (`savings_product_id`) REFERENCES `m_savings_product` (`id`)
)
ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;



INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'CREATE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'CREATE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'UPDATE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'UPDATE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'DELETE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'DELETE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'WAIVE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'WAIVE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'WAIVE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'WAIVE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'PAY_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'PAY', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'PAY_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'PAY', 0);



CREATE TABLE `m_savings_account_charge_paid_by` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `savings_account_transaction_id` BIGINT NOT NULL,
    `savings_account_charge_id` BIGINT NOT NULL,
    `amount` DECIMAL(19,6) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK__m_savings_account_transaction` (`savings_account_transaction_id`),
    INDEX `FK__m_savings_account_charge` (`savings_account_charge_id`),
    CONSTRAINT `FK__m_savings_account_charge` FOREIGN KEY (`savings_account_charge_id`) REFERENCES `m_savings_account_charge` (`id`),
    CONSTRAINT `FK__m_savings_account_transaction` FOREIGN KEY (`savings_account_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;
