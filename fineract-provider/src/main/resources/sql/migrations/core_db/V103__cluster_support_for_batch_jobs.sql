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

ALTER TABLE `job`
    ADD COLUMN `is_misfired` tinyint NOT NULL DEFAULT '0' AFTER `scheduler_group`;

CREATE TABLE `scheduler_detail` (
    `id` SMALLINT NOT NULL AUTO_INCREMENT,
    `is_suspended` tinyint NOT NULL DEFAULT '0',
    `execute_misfired_jobs` tinyint NOT NULL DEFAULT '1',
    `reset_scheduler_on_bootup` tinyint NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;

INSERT INTO `scheduler_detail` (`is_suspended`, `execute_misfired_jobs`, `reset_scheduler_on_bootup`) VALUES (FALSE,TRUE,TRUE);
