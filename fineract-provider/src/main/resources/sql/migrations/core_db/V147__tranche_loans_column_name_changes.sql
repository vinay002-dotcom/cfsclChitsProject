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
    CHANGE COLUMN `multi_disburse_loan` `allow_multiple_disbursals` tinyint NOT NULL DEFAULT '0' AFTER `close_date`,
    CHANGE COLUMN `max_tranche_count` `max_disbursals` INT NULL DEFAULT NULL AFTER `allow_multiple_disbursals`,
    CHANGE COLUMN `outstanding_loan_balance` `max_outstanding_loan_balance` DECIMAL(19,6) NULL DEFAULT NULL AFTER `max_disbursals`;
