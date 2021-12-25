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

INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `scheduler_group`) VALUES ('Recalculate Interest For Loans', 'Recalculate Interest For Loans', '0 1 0 1/1 * ? *', now(), 4, 3);

UPDATE `job` SET `scheduler_group`=3 WHERE  `name`='Update Non Performing Assets';

UPDATE `job` SET `task_priority`=3 WHERE  `name`='Add Accrual Transactions';

UPDATE `job` SET `task_priority`=2 WHERE  `name`='Add Periodic Accrual Transactions';
