/*
 *    Copyright 2018 Auktion & Markt AG
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.auktionmarkt.formular.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration model for Formular.
 */
@Configuration
@ConfigurationProperties(prefix = "formular")
@Data
public class FormularConfiguration {

    /**
     * -- GETTER --
     * Returns {@code true} whenever missing converters should be registered automatically by Formular.
     *
     * @return {@code true} whenever missing converters should be registered automatically
     *
     * -- SETTER --
     * Sets whenever missing converters should be registered automatically.
     *
     * @param registerMissingConverters Sets whenever missing converters should be registered automatically
     */
    private boolean registerMissingConverters;
}
