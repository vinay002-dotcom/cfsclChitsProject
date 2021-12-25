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

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES
('transaction_savings', 'POSTINTEREST_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'POSTINTEREST', '1'),
('transaction_savings', 'POSTINTEREST_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'POSTINTEREST', '0');


ALTER TABLE `m_savings_product`
ADD COLUMN `interest_posting_period_enum` SMALLINT NOT NULL DEFAULT 4 AFTER `interest_compounding_period_enum`;


ALTER TABLE `m_savings_account`
ADD COLUMN `interest_posting_period_enum` SMALLINT NOT NULL DEFAULT 4 AFTER `interest_compounding_period_enum`;
