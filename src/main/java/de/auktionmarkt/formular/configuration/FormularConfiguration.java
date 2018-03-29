package de.auktionmarkt.formular.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration model for Formular.
 */
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
