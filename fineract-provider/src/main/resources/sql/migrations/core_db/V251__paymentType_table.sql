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

CREATE TABLE `m_payment_type` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `value` VARCHAR(100) NULL DEFAULT NULL,
    `description` VARCHAR(500) NULL DEFAULT NULL,
    `is_cash_payment` tinyint NULL DEFAULT '0',
    `order_position` INT NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
)
COLLATE='utf8mb4_general_ci';

INSERT INTO m_payment_type (id,value,description,order_position)
SELECT id,code_value,code_description,order_position
FROM m_code_value
where code_id in
(select mc.id
from m_code mc where mc.code_name='PaymentType'
);

ALTER TABLE `m_payment_detail`
    DROP FOREIGN KEY `FK_m_payment_detail_m_code_value`;

ALTER TABLE `m_payment_detail`
    CHANGE COLUMN `payment_type_cv_id` `payment_type_id` INT NULL DEFAULT NULL AFTER `id`;

ALTER TABLE `m_payment_detail`
    ADD CONSTRAINT `FK_m_payment_detail_m_payment_type` FOREIGN KEY (`payment_type_id`) REFERENCES `m_payment_type` (`id`);

ALTER TABLE `acc_product_mapping`
    DROP FOREIGN KEY `FK_acc_product_mapping_m_code_value`;

ALTER TABLE `acc_product_mapping`
    ADD CONSTRAINT `FK_acc_product_mapping_m_payment_type` FOREIGN KEY (`payment_type`) REFERENCES `m_payment_type` (`id`);

DELETE from m_code_value
WHERE
m_code_value.code_id in
(SELECT mc.id
FROM m_code mc where mc.code_name='PaymentType' );

DELETE FROM m_code WHERE code_name="PaymentType";

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_PAYMENTTYPE', 'PAYMENTTYPE', 'CREATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_PAYMENTTYPE', 'PAYMENTTYPE', 'UPDATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_PAYMENTTYPE', 'PAYMENTTYPE', 'DELETE', 0);
