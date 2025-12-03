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
 * Controls whether unit words ("บาท"/"สตางค์"/"ถ้วน") are included and
 * whether a formal wording mode is used. The {@link #formal} flag is reserved
 * for future use where different wording rules may be applied.
 * </p>
 *
 * @since 1.0
 */
public final class ThaiBahtConfig {
    private final boolean useUnit; // include "บาท" and "สตางค์" / "ถ้วน"
    private final boolean formal; // placeholder for formal vs casual rules (future)

    private ThaiBahtConfig(boolean useUnit, boolean formal) {
        this.useUnit = useUnit;
        this.formal = formal;
    }

    /**
     * Return whether unit words ("บาท" and "สตางค์" / "ถ้วน") are included in the output.
     *
     * @return {@code true} if unit words are included
     */
    public boolean isUseUnit() { return useUnit; }

    /**
     * Return whether formal wording rules should be used. Currently reserved for future behaviour.
     *
     * @return {@code true} if formal rules are requested
     */
    public boolean isFormal() { return formal; }

    /**
     * Obtain the default configuration. The default is to include unit words and use formal wording.
     *
     * @return a default immutable {@link ThaiBahtConfig}
     */
    public static ThaiBahtConfig defaultConfig() {
        return new ThaiBahtConfig(true, true);
    }

    /**
     * Create a new {@link Builder} to customize configuration values.
     *
     * @return a new builder instance
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for {@link ThaiBahtConfig}.
     */
    public static final class Builder {
        private boolean useUnit = true;
        private boolean formal = true;

        /**
         * Set whether unit words should be included ("บาท"/"สตางค์"/"ถ้วน").
         *
         * @param v true to include unit words
         * @return this builder
         */
        public Builder useUnit(boolean v) { this.useUnit = v; return this; }

        /**
         * Set whether formal wording rules should be used.
         *
         * @param v true to use formal rules
         * @return this builder
         */
        public Builder formal(boolean v) { this.formal = v; return this; }

        /**
         * Build an immutable {@link ThaiBahtConfig} instance with the configured values.
         *
         * @return a new {@code ThaiBahtConfig}
         */
        public ThaiBahtConfig build() { return new ThaiBahtConfig(useUnit, formal); }
    }
}