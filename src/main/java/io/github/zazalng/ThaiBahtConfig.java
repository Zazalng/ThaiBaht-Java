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

/**
 * Immutable configuration for formatting Thai baht textual output.
 * <p>
 * This class controls various aspects of how numeric amounts are converted to Thai text,
 * including whether unit words ("บาท", "สตางค์", "ถ้วน") are included in the output.
 * All configuration instances are immutable and thread-safe.
 * </p>
 * <p>
 * Configuration options:
 * <ul>
 *   <li><strong>Unit Words:</strong> Controls whether Thai currency unit words are included.
 *       When enabled (default), amounts include "บาท" (baht) and "สตางค์" (satang) or "ถ้วน" (whole).
 *       When disabled, only the numeric words are returned.</li>
 *   <li><strong>Formal Mode:</strong> Reserved for future use where different Thai wording rules
 *       may be applied (formal vs. casual).</li>
 *   <li><strong>Negative Prefix:</strong> The Thai word to prefix negative amounts. Default is "ลบ" (minus).</li>
 * </ul>
 * </p>
 * <p>
 * Usage examples:
 * <pre>
 *   // Default configuration
 *   ThaiBahtConfig config = ThaiBahtConfig.defaultConfig();
 *
 *   // Custom configuration using builder
 *   ThaiBahtConfig customConfig = ThaiBahtConfig.builder()
 *       .useUnit(false)
 *       .formal(true)
 *       .setPrefix("ลบ")
 *       .build();
 *
 *   // Convert existing config to builder for modification
 *   ThaiBahtConfig modified = config.toBuilder()
 *       .useUnit(false)
 *       .build();
 * </pre>
 * </p>
 *
 * @see ThaiBaht
 * @see Builder
 * @since 1.0
 */
public final class ThaiBahtConfig {
    private final boolean useUnit; // include "บาท" and "สตางค์" / "ถ้วน"
    private final boolean formal; // placeholder for formal vs casual rules (future)
    private final String negativePrefix; // prefix for negative config

    private ThaiBahtConfig(boolean useUnit, boolean formal, String negativePrefix) {
        this.useUnit = useUnit;
        this.formal = formal;
        this.negativePrefix = negativePrefix;
    }

    /**
     * Return whether unit words ("บาท" and "สตางค์" / "ถ้วน") are included in the output.
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
     * @return the current prefix set by this configuration (typically "ลบ" for minus)
     */
    public String getNegativePrefix() { return negativePrefix; }

    /**
     * Obtain the default configuration.
     * <p>
     * The default configuration includes:
     * <ul>
     *   <li>Unit words enabled ("บาท", "สตางค์", "ถ้วน")</li>
     *   <li>Formal wording enabled</li>
     *   <li>Negative prefix set to "ลบ" (Thai minus)</li>
     * </ul>
     * </p>
     *
     * @return a default immutable {@link ThaiBahtConfig}
     */
    public static ThaiBahtConfig defaultConfig() {
        return new ThaiBahtConfig(true, true, "ลบ");
    }

    /**
     * Convert this configuration to a {@link Builder} for modification.
     * <p>
     * This method is useful for creating a modified version of an existing configuration
     * without creating a new builder from scratch.
     * </p>
     *
     * @return a new builder initialized with this configuration's values
     */
    public Builder toBuilder() {
        return new Builder()
                .useUnit(useUnit)
                .formal(formal)
                .setPrefix(negativePrefix);
    }

    /**
     * Create a new {@link Builder} to customize configuration values.
     * <p>
     * The returned builder is initialized with the default values:
     * <ul>
     *   <li>{@code useUnit} = {@code true}</li>
     *   <li>{@code formal} = {@code true}</li>
     *   <li>{@code negativePrefix} = "ลบ"</li>
     * </ul>
     * </p>
     *
     * @return a new builder instance
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for {@link ThaiBahtConfig}.
     * <p>
     * This builder is not thread-safe and should not be shared across threads.
     * Each builder instance produces an immutable {@link ThaiBahtConfig} via the {@link #build()} method.
     * </p>
     */
    public static final class Builder {
        private boolean useUnit = true;
        private boolean formal = true;
        private String negativePrefix = "ลบ";

        /**
         * Set whether unit words should be included ("บาท"/"สตางค์"/"ถ้วน").
         * <p>
         * When {@code true} (default), the Thai text will include currency unit words.
         * When {@code false}, only numeric words are included.
         * </p>
         *
         * @param v {@code true} to include unit words, {@code false} to omit them
         * @return this builder for method chaining
         */
        public Builder useUnit(boolean v) { this.useUnit = v; return this; }

        /**
         * Set whether formal wording rules should be used.
         * <p>
         * Currently reserved for future use where different Thai wording conventions
         * may be applied.
         * </p>
         *
         * @param v {@code true} to use formal rules, {@code false} for casual
         * @return this builder for method chaining
         */
        public Builder formal(boolean v) { this.formal = v; return this; }

        /**
         * Set the prefix wording for negative amounts.
         * <p>
         * This string will be prepended to the numeric representation for negative values.
         * The default is "ลบ" (Thai word for minus).
         * </p>
         *
         * @param negativePrefix the new prefix to use for negative amounts, must not be {@code null}
         * @return this builder for method chaining
         */
        public Builder setPrefix(String negativePrefix){ this.negativePrefix = negativePrefix; return this; }

        /**
         * Build an immutable {@link ThaiBahtConfig} instance with the configured values.
         * <p>
         * Once built, the configuration is immutable and can be safely shared across threads.
         * </p>
         *
         * @return a new immutable {@code ThaiBahtConfig}
         */
        public ThaiBahtConfig build() { return new ThaiBahtConfig(useUnit, formal, negativePrefix); }
    }
}