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
package org.apache.fineract.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.apache.fineract.notification.domain.Topic;
import org.apache.fineract.notification.domain.TopicRepository;
import org.apache.fineract.notification.domain.TopicSubscriber;
import org.apache.fineract.notification.service.TopicSubscriberWritePlatformService;
import org.apache.fineract.notification.service.TopicWritePlatformService;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepository;
import org.apache.fineract.useradministration.domain.Role;
import org.apache.fineract.useradministration.domain.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TopicTest {

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TopicWritePlatformService topicWritePltfService;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TopicSubscriberWritePlatformService topicSubscriberWritePltfService;

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    public void testTopicStorage() {
        Office office = officeRepository.getOne(1L);
        Role role = new Role("New Member_Type", "Testing topic creation");
        Topic topic = topicRepository.getOne(1L);

        lenient().when(this.officeRepository.getOne(1L)).thenReturn(office);
        lenient().when(this.topicRepository.getOne(1L)).thenReturn(topic);
        when(this.roleRepository.save(role)).thenReturn(role);
        when(this.topicWritePltfService.create(refEq(topic))).thenReturn(1L);

        this.roleRepository.save(role);
        Long topicId = this.topicWritePltfService.create(topic);

        verify(this.roleRepository, times(1)).save(role);
        verify(this.topicWritePltfService, times(1)).create(refEq(topic));
        assertEquals(topicId, Long.valueOf(1));

    }

    @Test
    public void testTopicSubscriberStorage() {
        AppUser user = appUserRepository.getOne(1L);
        Topic topic = topicRepository.getOne(1L);

        TopicSubscriber topicSubscriber = new TopicSubscriber(topic, user, new Date());

        lenient().when(this.appUserRepository.getOne(1L)).thenReturn(user);
        lenient().when(this.topicRepository.getOne(1L)).thenReturn(topic);
        when(this.topicSubscriberWritePltfService.create(refEq(topicSubscriber))).thenReturn(1L);

        Long subscriberId = this.topicSubscriberWritePltfService.create(topicSubscriber);

        verify(this.topicSubscriberWritePltfService, times(1)).create(refEq(topicSubscriber));
        assertEquals(subscriberId, Long.valueOf(1));

    }

}
