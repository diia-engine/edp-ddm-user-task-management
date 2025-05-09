/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.usrtaskmgt.config;

import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.epam.digital.data.platform.storage.form.model.CephKeysSearchParams;
import com.epam.digital.data.platform.storage.form.repository.CephFormDataRepository;
import com.epam.digital.data.platform.storage.form.repository.FormDataRepository;
import com.epam.digital.data.platform.storage.form.service.CephFormDataStorageService;
import com.epam.digital.data.platform.storage.form.service.FormDataKeyProviderImpl;
import com.epam.digital.data.platform.storage.form.service.FormDataStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestCephConfig {

  @Value("${ceph.bucket}")
  private String cephBucketName;
  @Inject
  private ObjectMapper objectMapper;

  @Bean
  @Primary
  public CephService cephService() {
    return new TestCephServiceImpl(cephBucketName, objectMapper);
  }

  @Bean
  public FormDataRepository<?> formDataRepository(CephService cephService) {
    return CephFormDataRepository.builder()
        .cephBucketName(cephBucketName)
        .objectMapper(objectMapper)
        .cephService(cephService)
        .build();
  }

  @Bean
  @Primary
  @ConditionalOnProperty(prefix = "storage.form-data", name = "type", havingValue = "test-ceph")
  public FormDataStorageService<?> formDataStorageService(FormDataRepository<CephKeysSearchParams> formDataRepository) {
    return CephFormDataStorageService.builder()
        .keyProvider(new FormDataKeyProviderImpl())
        .repository(formDataRepository)
        .build();
  }
}
