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

CREATE TABLE `m_tellers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `office_id` BIGINT NOT NULL,
    `debit_account_id` BIGINT,
    `credit_account_id` BIGINT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(100),
    `valid_from` DATE,
    `valid_to` DATE,
    `state` SMALLINT,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `m_tellers_name_unq` (`name`),
    INDEX `IK_m_tellers_m_office` (`office_id`),
    CONSTRAINT `FK_m_tellers_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
    CONSTRAINT `FK_m_tellers_gl_account_debit_account_id` FOREIGN KEY (`debit_account_id`) REFERENCES `acc_gl_account` (`id`),
    CONSTRAINT `FK_m_tellers_gl_account_credit_account_id` FOREIGN KEY (`credit_account_id`) REFERENCES `acc_gl_account` (`id`)
    );

CREATE TABLE `m_cashiers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `staff_id` BIGINT,
    `teller_id` BIGINT,
    `description` VARCHAR(100),
    `start_date` DATE,
    `end_date` DATE,
    `start_time` varchar(10),
    `end_time` varchar(10),
    `full_day` TINYINT,
    PRIMARY KEY (`id`),
    INDEX `IK_m_cashiers_m_staff` (`staff_id`),
    INDEX `IK_m_cashiers_m_teller` (`teller_id`),
    CONSTRAINT `FK_m_cashiers_m_staff` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`),
    CONSTRAINT `FK_m_cashiers_m_teller` FOREIGN KEY (`teller_id`) REFERENCES `m_tellers` (`id`)
    );

CREATE TABLE `m_cashier_transactions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `cashier_id` BIGINT NOT NULL,
    `txn_type` SMALLINT  NOT NULL,
    `txn_amount` DECIMAL (19,6)  NOT NULL,
    `txn_date` DATE  NOT NULL,
    `created_date` DATETIME  NOT NULL,
    `entity_type` VARCHAR(50),
    `entity_id` BIGINT,
    `txn_note` VARCHAR(200),
    PRIMARY KEY (`id`),
    INDEX `IK_m_teller_transactions_m_cashier` (`cashier_id`),
    CONSTRAINT `FK_m_teller_transactions_m_cashiers` FOREIGN KEY (`cashier_id`) REFERENCES `m_cashiers` (`id`)
    );


    INSERT INTO m_permission (
        `grouping`, code, entity_name, action_name, can_maker_checker
    ) values (
        'cash_mgmt', 'CREATE_TELLER', 'TELLER', 'CREATE', 1
    );
    INSERT INTO m_permission (
        `grouping`, code, entity_name, action_name, can_maker_checker
    ) values (
        'cash_mgmt', 'UPDATE_TELLER', 'TELLER', 'CREATE', 1
    );

    INSERT INTO m_permission (
        `grouping`, code, entity_name, action_name, can_maker_checker
    ) values (
        'cash_mgmt', 'ALLOCATECASHIER_TELLER', 'TELLER', 'ALLOCATE', 1
    );

    INSERT INTO m_permission (
        `grouping`, code, entity_name, action_name, can_maker_checker
    ) values (
        'cash_mgmt', 'UPDATECASHIERALLOCATION_TELLER', 'TELLER', 'UPDATECASHIERALLOCATION', 1
    );

    INSERT INTO m_permission (
        `grouping`, code, entity_name, action_name, can_maker_checker
    ) values (
        'cash_mgmt', 'DELETECASHIERALLOCATION_TELLER', 'TELLER', 'DELETECASHIERALLOCATION', 1
    );

    INSERT INTO m_permission (
        `grouping`, code, entity_name, action_name, can_maker_checker
    ) values (
        'cash_mgmt', 'ALLOCATECASHTOCASHIER_TELLER', 'TELLER', 'ALLOCATECASHTOCASHIER', 1
    );

    INSERT INTO m_permission (
        `grouping`, code, entity_name, action_name, can_maker_checker
    ) values (
        'cash_mgmt', 'SETTLECASHFROMCASHIER_TELLER', 'TELLER', 'SETTLECASHFROMCASHIER', 1
    );

    INSERT INTO r_enum_value (
        enum_name, enum_id, enum_message_property, enum_value, enum_type
    ) values (
        'teller_status', 300, 'Active', 'Active',0
    );
    INSERT INTO r_enum_value (
        enum_name, enum_id, enum_message_property, enum_value, enum_type
    ) values (
        'teller_status', 400, 'Inactive', 'Inactive',0
    );
    INSERT INTO r_enum_value (
        enum_name, enum_id, enum_message_property, enum_value, enum_type
    ) values (
        'teller_status', 600, 'Closed', 'Closed',0
    );
