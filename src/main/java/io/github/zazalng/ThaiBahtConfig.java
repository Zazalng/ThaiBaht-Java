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
import io.github.zazalng.utils.FormatTemplate;

/**
 * Immutable configuration object that controls all aspects of currency text conversion.
 * <p>
 * This class encapsulates formatting preferences for converting numeric amounts into textual
 * representations. All instances are immutable and thread-safe, making them safe for concurrent
 * use across application threads. Configuration options are set via the {@link Builder} class
 * and cannot be modified after construction.
 *
 * <h2>Configuration Options</h2>
 * <ul>
 *   <li><strong>Language:</strong> The output language (THAI or ENGLISH). Determines which language
 *       handler is used and sets default negative prefixes.</li>
 *   <li><strong>Unit Words:</strong> Whether to include currency unit words in the output.
 *       When enabled (default), includes "บาท"/"Baht" and "สตางค์"/"Satang". When disabled,
 *       only numeric words appear.</li>
 *   <li><strong>Formal Mode:</strong> Reserved for future language variations (formal vs. casual wording).
 *       Currently always treated as true but may enable different conventions in future versions.</li>
 *   <li><strong>Negative Prefix:</strong> The text prefix prepended to negative amounts.
 *       Defaults to "ลบ" (Thai) or "Minus" (English) if not explicitly set.</li>
 *   <li><strong>Format Templates:</strong> Custom format strings with named placeholders for
 *       precise control over output layout. Separate templates can be used for positive and negative amounts.</li>
 * </ul>
 *
 * <h2>Language and Prefix Behavior</h2>
 * <p>
 * Each language has a default negative prefix that is automatically applied when the language is selected:
 * <ul>
 *   <li><strong>Thai:</strong> Default prefix "ลบ" (Thai minus symbol)</li>
 *   <li><strong>English:</strong> Default prefix "Minus"</li>
 * </ul>
 *
 * When switching languages via the builder, the prefix automatically updates to the new language's
 * default UNLESS it has been explicitly set via {@link Builder#setPrefix(String)}. Once you set
 * a custom prefix, it is preserved across language changes.
 *
 * <h2>Format Templates (1.4.0+)</h2>
 * <p>
 * Custom format strings support the following named placeholders:
 * <ul>
 *   <li>{@code {INTEGER}} - The baht (integer) part as text</li>
 *   <li>{@code {UNIT}} - Currency unit word (บาท/Baht)</li>
 *   <li>{@code {EXACT}} - The exact indicator when satang is zero (ถ้วน/Only)</li>
 *   <li>{@code {FLOAT}} - The satang (fractional) part as text</li>
 *   <li>{@code {SATANG}} - Satang unit word (สตางค์/Satang)</li>
 *   <li>{@code {NEGPREFIX}} - Negative prefix (ลบ/Minus)</li>
 *   <li>{@code {FLOAT?content}} - Conditional: show content only if satang is non-zero</li>
 *   <li>{@code {SATANG?content}} - Conditional: show content only if satang is non-zero</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <p>
 * <strong>Example 1 - Default configuration:</strong>
 * <pre>{@code
 * ThaiBahtConfig config = ThaiBahtConfig.defaultConfig();
 * // Language: Thai, Units: true, Formal: true, Prefix: "ลบ"
 * }</pre>
 *
 * <p>
 * <strong>Example 2 - Builder pattern:</strong>
 * <pre>{@code
 * ThaiBahtConfig config = ThaiBahtConfig.builder(Language.ENGLISH)
 *     .useUnit(true)
 *     .formal(true)
 *     .setPrefix("Negative:")
 *     .build();
 * }</pre>
 *
 * <p>
 * <strong>Example 3 - Custom format template:</strong>
 * <pre>{@code
 * ThaiBahtConfig config = ThaiBahtConfig.builder()
 *     .setFormatTemplate("{INTEGER}{UNIT}{FLOAT?และ{FLOAT}{SATANG}}")
 *     .build();
 * }</pre>
 *
 * <p>
 * <strong>Example 4 - Convert existing to builder:</strong>
 * <pre>{@code
 * ThaiBahtConfig newConfig = existingConfig.toBuilder()
 *     .language(Language.ENGLISH)
 *     .useUnit(false)
 *     .build();
 * }</pre>
 *
 * <h2>Immutability and Thread Safety</h2>
 * <p>
 * This class is fully immutable - all fields are private, final, and initialized at construction time.
 * Instances are safe to share across multiple threads without synchronization. The builder is not
 * thread-safe and should not be shared across threads.
 *
 * @see ThaiBaht
 * @see Language
 * @see Builder
 * @see FormatTemplate
 * @author Zazalng
 * @since 1.0
 * @version 1.4.0
 */
public final class ThaiBahtConfig {
    private final Language language; // output language (THAI or ENGLISH)
    private final boolean useUnit; // include unit words
    private final boolean formal; // placeholder for formal vs casual rules (future)
    private final String negativePrefix; // prefix for negative config
    private final FormatTemplate formatTemplate; // custom format for positive amounts (1.4.0+)
    private final FormatTemplate negativeFormatTemplate; // custom format for negative amounts (1.4.0+)

    private ThaiBahtConfig(Language language, boolean useUnit, boolean formal, String negativePrefix,
                           FormatTemplate formatTemplate, FormatTemplate negativeFormatTemplate) {
        this.language = language;
        this.useUnit = useUnit;
        this.formal = formal;
        this.negativePrefix = negativePrefix == null ? "":negativePrefix;
        this.formatTemplate = formatTemplate;
        this.negativeFormatTemplate = negativeFormatTemplate;
    }

    /**
     * Returns the output language for text conversion.
     * <p>
     * This determines which language handler is used and affects unit words and default prefixes.
     *
     * @return the configured language (THAI or ENGLISH), never {@code null}
     */
    public Language getLanguage() { return language; }

    /**
     * Returns whether unit words are included in the output.
     * <p>
     * When true, the output includes currency unit words ("บาท"/"Baht" and "สตางค์"/"Satang").
     * When false, only numeric text is returned.
     *
     * @return {@code true} if unit words are included in the textual representation, {@code false} otherwise
     */
    public boolean isUseUnit() { return useUnit; }

    /**
     * Returns whether formal wording rules should be applied.
     * <p>
     * Currently reserved for future behavior. This flag may enable different wording conventions
     * (formal vs. casual) in future versions. Always true in current implementation.
     *
     * @return {@code true} if formal rules are requested
     */
    public boolean isFormal() { return formal; }

    /**
     * Returns the negative prefix wording for this configuration.
     * <p>
     * This string is prepended to negative amounts. It may be explicitly set or default to
     * the language's standard prefix ("ลบ" for Thai, "Minus" for English).
     *
     * @return the current negative prefix, possibly empty string, never {@code null}
     */
    public String getNegativePrefix() { return negativePrefix; }

    /**
     * Returns the custom format template for positive amounts.
     * <p>
     * If a custom format was configured via {@link Builder#setFormatTemplate(String)}, it is
     * returned here. Otherwise, standard formatting rules are used.
     *
     * @return the format template for positive values, or {@code null} if not configured
     * @since 1.4.0
     */
    public FormatTemplate getFormatTemplate() { return formatTemplate; }

    /**
     * Returns the custom format template for negative amounts.
     * <p>
     * If a custom format was configured via {@link Builder#setNegativeFormatTemplate(String)}, it is
     * returned here. If not configured, the positive format template is used (if any).
     *
     * @return the format template for negative values, or {@code null} if not configured
     * @since 1.4.0
     */
    public FormatTemplate getNegativeFormatTemplate() { return negativeFormatTemplate; }

    /**
     * Obtains the default configuration for currency text conversion.
     * <p>
     * The default configuration is pre-configured with standard settings suitable for most applications:
     * <ul>
     *   <li>Language: THAI</li>
     *   <li>Unit Words: Enabled (includes "บาท" and "สตางค์")</li>
     *   <li>Formal Mode: Enabled</li>
     *   <li>Negative Prefix: Empty string (uses language default "ลบ")</li>
     *   <li>Format Templates: None (uses standard formatting)</li>
     * </ul>
     *
     * <p>
     * This is a convenient factory method for creating a ready-to-use configuration without
     * building one manually. Use {@link #builder()} if you need custom settings.
     *
     * @return a default immutable {@link ThaiBahtConfig} suitable for general use
     * @see #builder()
     * @see #builder(Language)
     */
    public static ThaiBahtConfig defaultConfig() {
        return new ThaiBahtConfig(Language.THAI, true, true, null, null, null);
    }

    /**
     * Converts this configuration into a {@link Builder} for modification.
     * <p>
     * This method is useful for creating a modified version of an existing configuration
     * without starting from defaults. The returned builder is initialized with all values
     * from this configuration, including any custom format templates.
     *
     * <p>
     * <strong>Important Prefix Behavior:</strong> When you convert to a builder, the current
     * prefix value is marked as "explicitly set". This means that if you subsequently change
     * the language via {@link Builder#language(Language)}, the prefix will NOT automatically
     * update to the new language's default. It will be preserved as-is. To reset to language
     * defaults after setting a custom prefix, you must:
     * <ul>
     *   <li>Create a new builder with {@link #builder(Language)}, OR</li>
     *   <li>Explicitly set the prefix to null or empty string in the builder</li>
     * </ul>
     *
     * <p>
     * <strong>Example - preserving prefix across language change:</strong>
     * <pre>{@code
     * ThaiBahtConfig thai = ThaiBahtConfig.defaultConfig();
     * ThaiBahtConfig modified = thai.toBuilder()
     *     .language(Language.ENGLISH)
     *     .build();
     * // Prefix remains "ลบ" (Thai default) even though language is now English
     * }</pre>
     *
     * <p>
     * <strong>Example - modifying multiple settings:</strong>
     * <pre>{@code
     * ThaiBahtConfig newConfig = existingConfig.toBuilder()
     *     .language(Language.ENGLISH)
     *     .useUnit(false)
     *     .setPrefix("Negative:")
     *     .build();
     * }</pre>
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
        // Preserve format templates
        if (formatTemplate != null) {
            builder.setFormatTemplate(formatTemplate);
        }
        if (negativeFormatTemplate != null) {
            builder.setNegativeFormatTemplate(negativeFormatTemplate);
        }
        return builder;
    }

    /**
     * Creates a new {@link Builder} with default initialization for building custom configurations.
     * <p>
     * The returned builder starts with Thai language defaults:
     * <ul>
     *   <li>Language: THAI</li>
     *   <li>Unit Words: {@code true}</li>
     *   <li>Formal Mode: {@code true}</li>
     *   <li>Negative Prefix: Empty string (uses Thai default "ลบ")</li>
     *   <li>Format Templates: None</li>
     * </ul>
     *
     * <p>
     * <strong>Example - English with no units:</strong>
     * <pre>{@code
     * ThaiBahtConfig config = ThaiBahtConfig.builder()
     *     .language(Language.ENGLISH)
     *     .useUnit(false)
     *     .build();
     * }</pre>
     *
     * @return a new builder instance with Thai language as default
     * @since 1.0
     * @see #builder(Language)
     */
    public static Builder builder() {
        return new Builder(Language.THAI);
    }

    /**
     * Creates a new {@link Builder} with a specific initial language.
     * <p>
     * This factory method is useful when you want to build a configuration for a specific language
     * without having to separately call {@link Builder#language(Language)}. The builder is initialized
     * with the language-appropriate defaults including the language's default negative prefix.
     *
     * <p>
     * <strong>Example - create English configuration:</strong>
     * <pre>{@code
     * ThaiBahtConfig englishConfig = ThaiBahtConfig.builder(Language.ENGLISH)
     *     .useUnit(true)
     *     .formal(true)
     *     .build();
     * // Prefix defaults to "Minus" (English default)
     * }</pre>
     *
     * @param language the initial language for the configuration, must not be {@code null}
     * @return a new builder instance initialized with the specified language
     * @since 2.0
     * @see #builder()
     */
    public static Builder builder(Language language) {
        return new Builder(language);
    }

    /**
     * Builder for constructing immutable {@link ThaiBahtConfig} instances.
     * <p>
     * This builder class uses the builder pattern to configure all aspects of currency text conversion.
     * Once configured, call {@link #build()} to create an immutable {@link ThaiBahtConfig}. The built
     * configuration is thread-safe and can be safely shared across application threads.
     *
     * <h2>Key Features</h2>
     * <ul>
     *   <li><strong>Fluent Interface:</strong> All setter methods return {@code this} for method chaining</li>
     *   <li><strong>Not Thread-Safe:</strong> Builder instances should not be shared across threads</li>
     *   <li><strong>Immutable Results:</strong> Each {@link #build()} call creates a new immutable configuration</li>
     *   <li><strong>Smart Prefix Handling:</strong> Negative prefix automatically updates when language changes,
     *       unless explicitly set by user</li>
     * </ul>
     *
     * <h2>Negative Prefix Behavior</h2>
     * <p>
     * The builder handles negative prefixes intelligently:
     * <ul>
     *   <li>When first created, prefix starts empty and defaults to the language's standard prefix</li>
     *   <li>If you call {@link #language(Language)}, the prefix automatically updates to the new language's default</li>
     *   <li>Once you call {@link #setPrefix(String)}, the prefix is marked as "user-set" and no longer updates with language changes</li>
     *   <li>To reset to language defaults, create a new builder or explicitly set prefix to null</li>
     * </ul>
     *
     * <h2>Usage Examples</h2>
     *
     * <p>
     * <strong>Example 1 - Basic builder pattern:</strong>
     * <pre>{@code
     * ThaiBahtConfig config = ThaiBahtConfig.builder()
     *     .language(Language.THAI)
     *     .useUnit(true)
     *     .formal(true)
     *     .build();
     * }</pre>
     *
     * <p>
     * <strong>Example 2 - Chaining multiple settings:</strong>
     * <pre>{@code
     * ThaiBahtConfig config = ThaiBahtConfig.builder(Language.ENGLISH)
     *     .useUnit(true)
     *     .setPrefix("Payment Amount")
     *     .formal(true)
     *     .build();
     * }</pre>
     *
     * <p>
     * <strong>Example 3 - Custom format templates:</strong>
     * <pre>{@code
     * ThaiBahtConfig config = ThaiBahtConfig.builder()
     *     .setFormatTemplate("{INTEGER}{UNIT}{FLOAT?และ{FLOAT}{SATANG}}")
     *     .setNegativeFormatTemplate("({NEGPREFIX}){INTEGER}{UNIT}{FLOAT?และ{FLOAT}{SATANG}}")
     *     .build();
     * }</pre>
     *
     * @see ThaiBahtConfig
     * @see #build()
     * @since 1.0
     */
    public static final class Builder {
        private Language language;
        private boolean useUnit = true;
        private boolean formal = true;
        private String negativePrefix = "";
        private FormatTemplate formatTemplate = null;
        private FormatTemplate negativeFormatTemplate = null;

        /**
         * Constructs a builder with a specific initial language.
         * <p>
         * This constructor sets up the builder with the given language and initializes
         * other settings to their defaults. The negative prefix starts as empty string
         * and will default to the language's standard prefix unless explicitly set.
         *
         * @param language the initial language, must not be {@code null}
         */
        public Builder(Language language){
            this.language = language;
        }

        /**
         * Sets the output language for text conversion.
         * <p>
         * This method controls which language handler is used and affects unit words and default prefixes.
         * If the negative prefix has not been explicitly set (via {@link #setPrefix(String)}), it will
         * automatically update to the new language's default. If you have explicitly set a custom prefix,
         * it will be preserved.
         *
         * <p>
         * <strong>Example:</strong>
         * <pre>{@code
         * ThaiBahtConfig config = ThaiBahtConfig.builder()
         *     .language(Language.ENGLISH)  // Prefix now defaults to "Minus"
         *     .build();
         * }</pre>
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
         * Sets whether unit words should be included in the output.
         * <p>
         * When {@code true} (default), the text will include currency unit words ("บาท"/"Baht"
         * and "สตางค์"/"Satang"). When {@code false}, only numeric words are output.
         *
         * <p>
         * <strong>Example:</strong>
         * <pre>{@code
         * // With units: "One Hundred Baht"
         * // Without units: "One Hundred"
         * ThaiBahtConfig config = ThaiBahtConfig.builder()
         *     .language(Language.ENGLISH)
         *     .useUnit(true)  // Include "Baht"
         *     .build();
         * }</pre>
         *
         * @param v {@code true} to include unit words (default), {@code false} to omit them
         * @return this builder for method chaining
         */
        public Builder useUnit(boolean v) { this.useUnit = v; return this; }

        /**
         * Sets whether formal wording rules should be applied.
         * <p>
         * This setting is currently reserved for future use and may enable different wording
         * conventions (formal vs. casual) in future versions. Currently, formal mode is always
         * treated as enabled but serves as a placeholder for language variation support.
         *
         * @param v {@code true} to use formal rules (default), {@code false} for casual
         * @return this builder for method chaining
         */
        public Builder formal(boolean v) { this.formal = v; return this; }

        /**
         * Sets the prefix wording for negative amounts.
         * <p>
         * This string will be prepended to negative numeric representations. Once explicitly set,
         * this prefix will be preserved even when the language is changed via {@link #language(Language)}.
         * If you want to reset to language defaults after setting a custom prefix, you must create a new builder
         * or set the prefix back to null or empty string.
         *
         * <p>
         * Language defaults:
         * <ul>
         *   <li>Thai: "ลบ" (Thai minus)</li>
         *   <li>English: "Minus"</li>
         * </ul>
         *
         * <p>
         * <strong>Examples:</strong>
         * <pre>{@code
         * // Custom Thai prefix
         * ThaiBahtConfig config1 = ThaiBahtConfig.builder()
         *     .setPrefix("ติดลบ")
         *     .build();
         * // Output for -100: "ติดลบหนึ่งร้อยบาทถ้วน"
         *
         * // Custom English prefix
         * ThaiBahtConfig config2 = ThaiBahtConfig.builder(Language.ENGLISH)
         *     .setPrefix("Negative:")
         *     .build();
         * // Output for -100: "Negative: One Hundred Baht Only"
         *
         * // Resetting prefix (set to empty or null)
         * ThaiBahtConfig config3 = ThaiBahtConfig.builder(Language.THAI)
         *     .setPrefix("")
         *     .build();
         * // Prefix reverts to "" (no prefix for this config)
         * }</pre>
         *
         * @param negativePrefix the new prefix to use for negative amounts (null treated as empty string)
         * @return this builder for method chaining
         * @since 1.0
         */
        public Builder setPrefix(String negativePrefix){
            this.negativePrefix = negativePrefix;
            return this;
        }

        /**
         * Sets a custom format template for positive amounts.
         * <p>
         * Custom format templates provide precise control over output layout through named placeholders.
         * The format string supports the following placeholders (wrapped in curly braces):
         * <ul>
         *   <li>{@code {INTEGER}} - The baht (integer) part as text</li>
         *   <li>{@code {UNIT}} - Currency unit word (บาท/Baht)</li>
         *   <li>{@code {EXACT}} - The exact indicator when satang is zero (ถ้วน/Only)</li>
         *   <li>{@code {FLOAT}} - The satang (fractional) part as text</li>
         *   <li>{@code {SATANG}} - Satang unit word (สตางค์/Satang)</li>
         *   <li>{@code {FLOAT?content}} - Conditional: show content only if satang is non-zero</li>
         *   <li>{@code {SATANG?content}} - Conditional: show content only if satang is non-zero</li>
         * </ul>
         *
         * <p>
         * <strong>Format Requirements:</strong>
         * <ul>
         *   <li>Must contain both {@code {INTEGER}} and {@code {FLOAT}} placeholders</li>
         *   <li>Other placeholders are optional</li>
         *   <li>Placeholders can be used multiple times</li>
         *   <li>Regular text can be placed between placeholders freely</li>
         * </ul>
         *
         * <p>
         * <strong>Examples:</strong>
         * <pre>{@code
         * // Standard format
         * builder.setFormatTemplate("{INTEGER}{UNIT}{FLOAT?และ{FLOAT}{SATANG}}");
         * // Output: "หนึ่งร้อยบาทและห้าสิบสตางค์" for 100.50
         *
         * // With spaces and parentheses
         * builder.setFormatTemplate("{INTEGER} {UNIT}{FLOAT? ({FLOAT} {SATANG})}");
         * // Output: "หนึ่งร้อย บาท (ห้าสิบ สตางค์)" for 100.50
         *
         * // English example
         * builder.setFormatTemplate("{INTEGER} {UNIT}{FLOAT?  and  {FLOAT} {SATANG}}");
         * // Output: "Five Hundred Baht  and  Fifty Satang" for 500.50
         * }</pre>
         *
         * @param formatString the custom format string with named placeholders,
         *                    null or empty string will clear the format template
         * @return this builder for method chaining
         * @throws IllegalArgumentException if format string is invalid (missing required placeholders)
         * @since 1.4.0
         */
        public Builder setFormatTemplate(String formatString) {
            if (formatString != null && !formatString.isEmpty()) {
                this.formatTemplate = FormatTemplate.of(formatString);
            }
            return this;
        }

        /**
         * Sets a custom format template for positive amounts using a FormatTemplate instance.
         * <p>
         * This method accepts a pre-constructed {@link FormatTemplate} object. Use this
         * when you want to reuse the same format across multiple configurations or when
         * you need to validate the format before applying it.
         *
         * @param template the format template instance, null clears the template
         * @return this builder for method chaining
         * @since 1.4.0
         * @see FormatTemplate#of(String)
         */
        public Builder setFormatTemplate(FormatTemplate template) {
            this.formatTemplate = template;
            return this;
        }

        /**
         * Sets a custom format template for negative amounts.
         * <p>
         * Custom format templates for negative amounts provide precise control over how negative
         * values are displayed. Use this when you want different formatting for negative vs. positive amounts.
         * If no negative template is configured, the positive template (if any) will be used instead.
         *
         * <p>
         * Supported placeholders (same as positive templates):
         * <ul>
         *   <li>{@code {INTEGER}} - The baht (integer) part as text</li>
         *   <li>{@code {UNIT}} - Currency unit word (บาท/Baht)</li>
         *   <li>{@code {EXACT}} - The exact indicator when satang is zero (ถ้วน/Only)</li>
         *   <li>{@code {FLOAT}} - The satang (fractional) part as text</li>
         *   <li>{@code {SATANG}} - Satang unit word (สตางค์/Satang)</li>
         *   <li>{@code {NEGPREFIX}} - Negative prefix (ลบ/Minus) - useful in negative format</li>
         *   <li>{@code {FLOAT?content}} - Conditional: show content only if satang is non-zero</li>
         *   <li>{@code {SATANG?content}} - Conditional: show content only if satang is non-zero</li>
         * </ul>
         *
         * <p>
         * <strong>Examples:</strong>
         * <pre>{@code
         * // Negative in parentheses
         * builder.setNegativeFormatTemplate("({NEGPREFIX} {INTEGER}{UNIT}{FLOAT?และ{FLOAT}{SATANG}})");
         * // Output for -100.50: "(ลบ หนึ่งร้อยบาทและห้าสิบสตางค์)"
         *
         * // Negative with different prefix
         * builder.setNegativeFormatTemplate("{NEGPREFIX}: {INTEGER}{UNIT}{FLOAT?และ{FLOAT}{SATANG}}");
         * // Output for -100.50: "Minus: One Hundred Baht and Fifty Satang"
         * }</pre>
         *
         * @param formatString the custom format string with named placeholders for negative amounts,
         *                    null or empty string will clear the template
         * @return this builder for method chaining
         * @throws IllegalArgumentException if format string is invalid (missing required placeholders)
         * @since 1.4.0
         */
        public Builder setNegativeFormatTemplate(String formatString) {
            if (formatString != null && !formatString.isEmpty()) {
                this.negativeFormatTemplate = FormatTemplate.of(formatString);
            }
            return this;
        }

        /**
         * Sets a custom format template for negative amounts using a FormatTemplate instance.
         * <p>
         * This method accepts a pre-constructed {@link FormatTemplate} object. Use this when
         * you want to validate or reuse format templates across multiple configurations.
         *
         * @param template the format template instance for negative amounts, null clears the template
         * @return this builder for method chaining
         * @since 1.4.0
         * @see FormatTemplate#of(String)
         */
        public Builder setNegativeFormatTemplate(FormatTemplate template) {
            this.negativeFormatTemplate = template;
            return this;
        }

        /**
         * Sets a custom format for both positive and negative amounts using the same template.
         * <p>
         * This convenience method applies the same format template to both positive and negative amounts.
         * It's useful when you want consistent formatting regardless of sign. For different formats per
         * sign, use {@link #setFormatTemplate(String)} and {@link #setNegativeFormatTemplate(String)} separately.
         *
         * <p>
         * Supported placeholders:
         * <ul>
         *   <li>{@code {INTEGER}} - The baht (integer) part as text</li>
         *   <li>{@code {UNIT}} - Currency unit word (บาท/Baht)</li>
         *   <li>{@code {EXACT}} - The exact indicator when satang is zero (ถ้วน/Only)</li>
         *   <li>{@code {FLOAT}} - The satang (fractional) part as text</li>
         *   <li>{@code {SATANG}} - Satang unit word (สตางค์/Satang)</li>
         *   <li>{@code {NEGPREFIX}} - Negative prefix (ลบ/Minus)</li>
         *   <li>{@code {FLOAT?content}} - Conditional: show content only if satang is non-zero</li>
         * </ul>
         *
         * <p>
         * <strong>Example:</strong>
         * <pre>{@code
         * // Apply same format to both positive and negative
         * builder.setFormat("{INTEGER} {UNIT}{FLOAT? ({FLOAT} {SATANG})}", true);
         *
         * // Apply format only to positive (negative uses standard formatting)
         * builder.setFormat("{INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}}", false);
         * }</pre>
         *
         * @param formatString the custom format string with named placeholders
         * @param applyToNegative if {@code true}, applies the same format to both positive and negative amounts;
         *                       if {@code false}, applies only to positive amounts
         * @return this builder for method chaining
         * @throws IllegalArgumentException if format string is invalid (missing required placeholders)
         * @since 1.4.0
         */
        public Builder setFormat(String formatString, boolean applyToNegative) {
            setFormatTemplate(formatString);
            if (applyToNegative) {
                setNegativeFormatTemplate(formatString);
            }
            return this;
        }

        /**
         * Builds an immutable {@link ThaiBahtConfig} instance with the configured values.
         * <p>
         * Once built, the configuration is immutable and can be safely shared and reused across
         * threads and application instances. Each call to this method creates a new independent
         * configuration object, allowing builder reuse for multiple configurations.
         *
         * <p>
         * <strong>Example - reusing builder for multiple configurations:</strong>
         * <pre>{@code
         * ThaiBahtConfig.Builder builder = ThaiBahtConfig.builder(Language.THAI);
         *
         * ThaiBahtConfig config1 = builder.useUnit(true).build();
         * ThaiBahtConfig config2 = builder.useUnit(false).build();
         * ThaiBahtConfig config3 = builder.language(Language.ENGLISH).build();
         * }</pre>
         *
         * @return a new immutable {@link ThaiBahtConfig} with the builder's settings
         */
        public ThaiBahtConfig build() {
            return new ThaiBahtConfig(language, useUnit, formal, negativePrefix, formatTemplate, negativeFormatTemplate);
        }
    }
}