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
package org.apache.fineract.notification.data;

import java.time.LocalDate;
import org.apache.fineract.infrastructure.core.service.DateUtils;

public class TopicSubscriberData {

    private final Long id;
    private final Long topicId;
    private final Long userId;
    private final LocalDate subscriptionDate;

    public TopicSubscriberData(Long id, Long topicId, Long userId, LocalDate subscriptionDate) {
        this.id = id;
        this.topicId = topicId;
        this.userId = userId;
        this.subscriptionDate = subscriptionDate;
    }

    public TopicSubscriberData(Long id, Long topicId, Long userId) {
        this.id = id;
        this.topicId = topicId;
        this.userId = userId;
        this.subscriptionDate = LocalDate.now(DateUtils.getDateTimeZoneOfTenant());
    }

    public Long getId() {
        return this.id;
    }

    public Long getTopicId() {
        return this.topicId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public LocalDate getSubscriptionDate() {
        return this.subscriptionDate;
    }

}
