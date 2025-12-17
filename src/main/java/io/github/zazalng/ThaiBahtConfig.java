/*
 * Copyright 2025 Napapon Kamanee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.zazalng;

import io.github.zazalng.contracts.Language;

/**
 * Immutable configuration for formatting currency textual output.
 * <p>
 * This class controls various aspects of how numeric amounts are converted to text,
 * including the output language (Thai or English), whether unit words are included,
 * and the prefix for negative amounts.
 * All configuration instances are immutable and thread-safe.
 *
 * <p>
 * Configuration options:
 * <ul>
 *   <li><strong>Language:</strong> Controls the output language (THAI or ENGLISH).
 *       Default is THAI. Each language has a default negative prefix.</li>
 *   <li><strong>Unit Words:</strong> Controls whether currency unit words are included.
 *       When enabled (default), amounts include currency unit words.
 *       When disabled, only the numeric words are returned.</li>
 *   <li><strong>Formal Mode:</strong> Reserved for future use where different wording rules
 *       may be applied (formal vs. casual).</li>
 *   <li><strong>Negative Prefix:</strong> The word to prefix negative amounts.</li>
 * </ul>
 *
 * @see ThaiBaht
 * @see Language
 * @see Builder
 * @since 1.0
 * @version 1.3.0
 */
public final class ThaiBahtConfig {
    private final Language language; // output language (THAI or ENGLISH)
    private final boolean useUnit; // include unit words
    private final boolean formal; // placeholder for formal vs casual rules (future)
    private final String negativePrefix; // prefix for negative config

    private ThaiBahtConfig(Language language, boolean useUnit, boolean formal, String negativePrefix) {
        this.language = language;
        this.useUnit = useUnit;
        this.formal = formal;
        this.negativePrefix = negativePrefix == null ? "":negativePrefix;
    }

    /**
     * Return the output language for text conversion.
     *
     * @return the configured language (THAI or ENGLISH)
     */
    public Language getLanguage() { return language; }

    /**
     * Return whether unit words are included in the output.
     *
     * @return {@code true} if unit words are included in the textual representation
     */
    public boolean isUseUnit() { return useUnit; }

    /**
     * Return whether formal wording rules should be used. Currently reserved for future behaviour.
     *
     * @return {@code true} if formal rules are requested
     */
    public boolean isFormal() { return formal; }

    /**
     * Return the negative prefix wording rules.
     *
     * @return the current prefix set by this configuration
     */
    public String getNegativePrefix() { return negativePrefix; }

    /**
     * Obtain the default configuration.
     * <p>
     * The default configuration includes:
     * <ul>
     *   <li>Language set to THAI</li>
     *   <li>Unit words enabled</li>
     *   <li>Formal wording enabled</li>
     *   <li>Negative prefix set to empty String</li>
     * </ul>
     *
     * @return a default immutable {@link ThaiBahtConfig}
     */
    public static ThaiBahtConfig defaultConfig() {
        return new ThaiBahtConfig(Language.THAI, true, true, null);
    }

    /**
     * Convert this configuration to a {@link Builder} for modification.
     * <p>
     * This method is useful for creating a modified version of an existing configuration
     * without creating a new builder from scratch.
     * <p>
     * <strong>Prefix Behavior:</strong> The returned builder will preserve the current prefix value,
     * and treat it as "explicitly set". This means if you change the language afterward,
     * the prefix will NOT automatically update to the new language's default
     * unless you explicitly set a new prefix or empty String.
     *
     * @return a new builder initialized with this configuration's values
     * @since 1.0
     */
    public Builder toBuilder() {
        Builder builder = new Builder(language);
        builder.useUnit(useUnit)
               .formal(formal);
        // Explicitly set the prefix as "user-set" to preserve it across language changes
        builder.setPrefix(negativePrefix);
        return builder;
    }

    /**
     * Create a new {@link Builder} to customize configuration values.
     * <p>
     * The returned builder is initialized with the default values:
     * <ul>
     *   <li>{@code language} = THAI</li>
     *   <li>{@code useUnit} = {@code true}</li>
     *   <li>{@code formal} = {@code true}</li>
     *   <li>{@code negativePrefix} = language-specific default by {@link io.github.zazalng.handler} its class</li>
     * </ul>
     *
     * @return a new builder instance with Thai language default
     * @since 1.0
     */
    public static Builder builder() {
        return new Builder(Language.THAI);
    }

    /**
     * Create a new {@link Builder} to customize configuration values with a specific language.
     * <p>
     * The returned builder is initialized with language-specific defaults.
     *
     * @param language the initial language for the configuration, must not be {@code null}
     * @return a new builder instance
     * @since 2.0
     */
    public static Builder builder(Language language) {
        return new Builder(language);
    }

    /**
     * Builder for {@link ThaiBahtConfig}.
     * <p>
     * This builder is not thread-safe and should not be shared across threads.
     * Each builder instance produces an immutable {@link ThaiBahtConfig} via the {@link #build()} method.
     * <p>
     * When the language is changed via {@link #language(Language)}, the negative prefix will automatically
     * update to the language's default unless it has been explicitly set via {@link #setPrefix(String)}.
     *
     * @since 1.0
     */
    public static final class Builder {
        private Language language;
        private boolean useUnit = true;
        private boolean formal = true;
        private String negativePrefix = "";

        /**
         * Create a builder with a specific initial language.
         *
         * @param language the initial language, must not be {@code null}
         */
        public Builder(Language language){
            this.language = language;
        }

        /**
         * Set the output language for text conversion.
         *
         * @param language the desired output language (THAI or ENGLISH), must not be {@code null}
         * @return this builder for method chaining
         * @since 1.3
         */
        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        /**
         * Set whether unit words should be included.
         * <p>
         * When {@code true} (default), the text will include currency unit words.
         * When {@code false}, only numeric words are included.
         *
         * @param v {@code true} to include unit words, {@code false} to omit them
         * @return this builder for method chaining
         */
        public Builder useUnit(boolean v) { this.useUnit = v; return this; }

        /**
         * Set whether formal wording rules should be used.
         * <p>
         * Currently reserved for future use where different wording conventions
         * may be applied.
         *
         * @param v {@code true} to use formal rules, {@code false} for casual
         * @return this builder for method chaining
         */
        public Builder formal(boolean v) { this.formal = v; return this; }

        /**
         * Set the prefix wording for negative amounts.
         * <p>
         * This string will be prepended to the numeric representation for negative values.
         * Once explicitly set, this prefix will be preserved even if the language is changed.
         * To reset to language defaults, you must set this prefix as {@code null} or empty String
         *
         * @param negativePrefix the new prefix to use for negative amounts.
         * @return this builder for method chaining
         * @since 1.0
         */

        public Builder setPrefix(String negativePrefix){
            this.negativePrefix = negativePrefix;
            return this;
        }

        /**
         * Build an immutable {@link ThaiBahtConfig} instance with the configured values.
         * <p>
         * Once built, the configuration is immutable and can be safely shared across threads.
         *
         * @return a new immutable {@code ThaiBahtConfig}
         */
        public ThaiBahtConfig build() {
            return new ThaiBahtConfig(language, useUnit, formal, negativePrefix);
        }
    }
}