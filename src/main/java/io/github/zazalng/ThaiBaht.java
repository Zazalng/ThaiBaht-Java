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
import io.github.zazalng.handler.TextConverter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Primary entry point for converting monetary amounts ({@link BigDecimal}) into beautifully formatted
 * Thai Baht text in multiple languages.
 * <p>
 * This immutable class provides both static convenience methods and instance-based APIs to convert
 * numeric currency amounts to their textual representation. It supports multiple output languages
 * (Thai and English) and flexible formatting options through the {@link ThaiBahtConfig} configuration object.
 *
 * <h2>Core Functionality</h2>
 * <ul>
 *   <li><strong>Multi-language support:</strong> Convert to Thai (บาท/สตางค์) or English (Baht/Satang)</li>
 *   <li><strong>Precise handling:</strong> Amounts are normalized to 2 decimal places (satang precision)</li>
 *   <li><strong>Negative support:</strong> Customizable prefixes for negative amounts</li>
 *   <li><strong>Flexible formatting:</strong> Control unit word inclusion and custom output formats</li>
 *   <li><strong>Thread-safe:</strong> All instances are immutable and safe for concurrent use</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Static API (simplest approach)</h3>
 * <pre>{@code
 * // Thai (default)
 * String thai = ThaiBaht.of(new BigDecimal("1234.56"));
 * // Output: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
 *
 * // English
 * String english = ThaiBaht.of(
 *     new BigDecimal("1234.56"),
 *     ThaiBahtConfig.builder(Language.ENGLISH).build()
 * );
 * // Output: "One Thousand Two Hundred Thirty-Four Baht Fifty-Six Satang"
 * }</pre>
 *
 * <h3>Instance API (for reuse and chaining)</h3>
 * <pre>{@code
 * ThaiBaht converter = ThaiBaht.create(new BigDecimal("500.25"));
 * String result = converter.setAmount(new BigDecimal("1000.50")).toString();
 * // Output: "หนึ่งพันบาทห้าสิบสตางค์"
 * }</pre>
 *
 * <h3>Fluent Configuration</h3>
 * <pre>{@code
 * ThaiBaht converter = ThaiBaht.create(new BigDecimal("100"))
 *     .config(builder -> builder
 *         .language(Language.ENGLISH)
 *         .useUnit(true)
 *         .formal(true)
 *     );
 * String result = converter.toString();
 * // Output: "One Hundred Baht Only"
 * }</pre>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Accurate Thai Grammar:</strong> Handles special Thai number formatting rules including
 *       "เอ็ด" (1 in units of larger numbers), "ยี่" (2 in tens), and repeating "ล้าน" (millions)</li>
 *   <li><strong>Satang Precision:</strong> Correctly displays "ถ้วน" (Only) when no satang are present</li>
 *   <li><strong>Large Number Support:</strong> Handles amounts in the billions and beyond</li>
 *   <li><strong>Custom Formatting:</strong> Apply custom format strings with named placeholders for flexible output</li>
 *   <li><strong>Negative Amount Handling:</strong> Configure custom prefixes for negative values</li>
 * </ul>
 *
 * <h2>Design Notes</h2>
 * This class is immutable and designed for both convenience (static methods) and flexibility (builder pattern).
 * All monetary computations use {@link BigDecimal} to avoid floating-point precision issues.
 * The class is optimized for enterprise applications including payment processors, e-invoicing, and
 * government form generation.
 *
 * @see ThaiBahtConfig
 * @see ThaiBahtConfig.Builder
 * @see Language
 * @author Zazalng
 * @since 1.0
 * @version 1.4.0
 */
public final class ThaiBaht {
    private ThaiBahtConfig config;
    private BigDecimal amount;

    /**
     * Constructs a ThaiBaht instance with the specified amount and configuration.
     * <p>
     * This private constructor creates an immutable ThaiBaht converter that holds
     * the monetary amount and formatting configuration. The amount and configuration
     * must not be null as they are validated via {@link Objects#requireNonNull(Object)}.
     *
     * @param amount the monetary amount to convert (in baht), must not be {@code null}
     * @param config the formatting configuration controlling language, units, and prefixes, must not be {@code null}
     * @throws NullPointerException if either {@code amount} or {@code config} is {@code null}
     * @see ThaiBahtConfig
     */
    private ThaiBaht(BigDecimal amount, ThaiBahtConfig config) {
        this.amount = Objects.requireNonNull(amount);
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Creates a new ThaiBaht converter instance with the specified amount and default configuration.
     * <p>
     * The default configuration includes Thai language output, unit words enabled ("บาท", "สตางค์"),
     * and formal wording rules. This is the simplest way to create a converter for standard use cases.
     *
     * <p>
     * <strong>Example:</strong>
     * <pre>{@code
     * ThaiBaht converter = ThaiBaht.create(new BigDecimal("1234.56"));
     * System.out.println(converter);  // Output: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
     * }</pre>
     *
     * @param amount the monetary amount to convert, must not be {@code null}
     * @return a new ThaiBaht instance configured with default settings
     * @throws NullPointerException if {@code amount} is {@code null}
     * @see #create(BigDecimal, ThaiBahtConfig)
     * @see ThaiBahtConfig#defaultConfig()
     */
    public static ThaiBaht create(BigDecimal amount) {
        return new ThaiBaht(amount, ThaiBahtConfig.defaultConfig());
    }

    /**
     * Creates a new ThaiBaht converter instance with the specified amount and custom configuration.
     * <p>
     * This method allows you to control all aspects of the text conversion, including the output
     * language, inclusion of unit words, custom format templates, and negative number prefixes.
     * The instance is immutable but supports fluent method chaining through setter methods.
     *
     * <p>
     * <strong>Example with English output:</strong>
     * <pre>{@code
     * ThaiBahtConfig config = ThaiBahtConfig.builder(Language.ENGLISH)
     *     .useUnit(true)
     *     .build();
     * ThaiBaht converter = ThaiBaht.create(new BigDecimal("500.50"), config);
     * System.out.println(converter);  // Output: "Five Hundred Baht Fifty Satang"
     * }</pre>
     *
     * <p>
     * <strong>Example with custom format:</strong>
     * <pre>{@code
     * ThaiBahtConfig config = ThaiBahtConfig.builder()
     *     .setFormatTemplate("{INTEGER}{UNIT}{FLOAT?และ{FLOAT}{SATANG}}")
     *     .build();
     * ThaiBaht converter = ThaiBaht.create(new BigDecimal("100.50"), config);
     * System.out.println(converter);  // Custom formatting with placeholders
     * }</pre>
     *
     * @param amount the monetary amount to convert, must not be {@code null}
     * @param config the formatting configuration controlling language, units, format, and more, must not be {@code null}
     * @return a new ThaiBaht instance with the specified configuration
     * @throws NullPointerException if either {@code amount} or {@code config} is {@code null}
     * @see #create(BigDecimal)
     * @see ThaiBahtConfig
     * @see ThaiBahtConfig.Builder
     */
    public static ThaiBaht create(BigDecimal amount, ThaiBahtConfig config) {
        return new ThaiBaht(amount, config);
    }

    /**
     * Updates the monetary amount to be converted.
     * <p>
     * This method allows changing the amount while keeping the same configuration. It returns
     * this instance to support fluent method chaining for convenient workflow patterns.
     *
     * <p>
     * <strong>Example:</strong>
     * <pre>{@code
     * ThaiBaht converter = ThaiBaht.create(new BigDecimal("100"))
     *     .setAmount(new BigDecimal("500"))
     *     .setAmount(new BigDecimal("1000.50"));
     * System.out.println(converter);  // Final amount: 1000.50
     * }</pre>
     *
     * @param amount the new monetary amount to convert, must not be {@code null}
     * @return this ThaiBaht instance for method chaining
     * @throws NullPointerException if {@code amount} is {@code null}
     */
    public ThaiBaht setAmount(BigDecimal amount) {
        this.amount = Objects.requireNonNull(amount);
        return this;
    }

    /**
     * Returns the current monetary amount held by this instance.
     * <p>
     * The amount represents the complete currency value in baht, including fractional satang.
     *
     * @return the current {@link BigDecimal} amount, never {@code null}
     */
    public BigDecimal getAmount(){
        return amount;
    }

    /**
     * Returns the current formatting configuration held by this instance.
     * <p>
     * The configuration controls language, unit words, custom formats, and negative number handling.
     *
     * @return the current {@link ThaiBahtConfig}, never {@code null}
     */
    public ThaiBahtConfig getConfig(){
        return config;
    }

    /**
     * Updates the formatting configuration used by this instance.
     * <p>
     * This method allows changing the language, unit words, format templates, and other
     * configuration options while keeping the same amount. It returns this instance to
     * support fluent method chaining.
     *
     * @param config the new formatting configuration, must not be {@code null}
     * @return this ThaiBaht instance for method chaining
     * @throws NullPointerException if {@code config} is {@code null}
     */
    public ThaiBaht setConfig(ThaiBahtConfig config) {
        this.config = Objects.requireNonNull(config);
        return this;
    }

    /**
     * Modifies the current configuration using a consumer function for flexible updates.
     * <p>
     * This method provides a convenient fluent interface for modifying configuration without
     * creating a new builder from scratch. The configuration builder is created from the current
     * config, passed to the updater function for modification, then built back into this instance.
     *
     * <p>
     * <strong>Example - switching languages:</strong>
     * <pre>{@code
     * ThaiBaht converter = ThaiBaht.create(new BigDecimal("100"))
     *     .config(b -> b.language(Language.ENGLISH).useUnit(false));
     * System.out.println(converter);  // English output without unit words
     * }</pre>
     *
     * <p>
     * <strong>Example - adding custom format:</strong>
     * <pre>{@code
     * converter.config(b -> b.setFormatTemplate("{INTEGER}{UNIT} ({FLOAT} {SATANG})"));
     * }</pre>
     *
     * @param updater a consumer function that accepts a {@link ThaiBahtConfig.Builder} for modification,
     *                must not be {@code null}
     * @return this ThaiBaht instance for method chaining
     * @throws NullPointerException if {@code updater} is {@code null}
     * @see ThaiBahtConfig.Builder
     */
    public ThaiBaht config(Consumer<ThaiBahtConfig.Builder> updater) {
        ThaiBahtConfig.Builder builder = this.config.toBuilder();
        updater.accept(builder);
        this.config = builder.build();
        return this;
    }

    /**
     * Converts the stored amount to Thai baht text using the current configuration.
     * <p>
     * This method performs the actual currency-to-text conversion, routing to the appropriate
     * language handler (Thai or English) based on the configuration language setting. The output
     * respects all configuration options including unit words, custom formats, and negative prefixes.
     *
     * @return the formatted textual representation of the amount according to the configuration,
     *         never {@code null}
     */
    @Override
    public String toString() {
        return TextConverter.toBahtText(this);
    }

    /**
     * Converts the given amount to Thai baht text using the default configuration.
     * <p>
     * This is a convenience static method that creates a one-time converter using the
     * {@link ThaiBahtConfig#defaultConfig() default configuration}. The default includes:
     * <ul>
     *   <li>Output language: Thai (with Thai digits and unit words)</li>
     *   <li>Unit words: Enabled (includes "บาท" and "สตางค์")</li>
     *   <li>Formal wording: Enabled</li>
     *   <li>Negative prefix: "ลบ" (Thai minus)</li>
     * </ul>
     *
     * <p>
     * The amount is normalized to 2 decimal places (satang precision). Negative values are prefixed
     * with the language's default negative prefix.
     *
     * <p>
     * <strong>Examples:</strong>
     * <pre>{@code
     * // Positive amount
     * String result = ThaiBaht.of(new BigDecimal("1234.56"));
     * // Output: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
     *
     * // Zero amount
     * String zero = ThaiBaht.of(new BigDecimal("0"));
     * // Output: "ศูนย์บาทถ้วน"
     *
     * // Negative amount
     * String negative = ThaiBaht.of(new BigDecimal("-100.50"));
     * // Output: "ลบหนึ่งร้อยบาทห้าสิบสตางค์"
     * }</pre>
     *
     * @param amount the monetary amount to convert (baht and satang), must not be {@code null}
     * @return the Thai-language textual representation of the amount, never {@code null}
     * @throws NullPointerException if {@code amount} is {@code null}
     * @see #of(BigDecimal, ThaiBahtConfig)
     * @see ThaiBahtConfig#defaultConfig()
     */
    public static String of(BigDecimal amount) {
        return of(amount, ThaiBahtConfig.defaultConfig());
    }

    /**
     * Converts the given amount to Thai baht text using the provided configuration.
     * <p>
     * This method offers fine-grained control over output formatting by allowing a custom
     * {@link ThaiBahtConfig} to be specified. Use this for one-time conversions that require
     * non-default formatting options. You can customize:
     * <ul>
     *   <li><strong>Language:</strong> Thai or English via {@link ThaiBahtConfig.Builder#language(Language)}</li>
     *   <li><strong>Unit words:</strong> Include/exclude via {@link ThaiBahtConfig.Builder#useUnit(boolean)}</li>
     *   <li><strong>Negative prefix:</strong> Custom prefix via {@link ThaiBahtConfig.Builder#setPrefix(String)}</li>
     *   <li><strong>Format templates:</strong> Custom output format via {@link ThaiBahtConfig.Builder#setFormatTemplate(String)}</li>
     *   <li><strong>Formal rules:</strong> Apply formal conventions via {@link ThaiBahtConfig.Builder#formal(boolean)}</li>
     * </ul>
     *
     * <p>
     * <strong>Usage Examples:</strong>
     *
     * <p>
     * <strong>Example 1 - English output with unit words:</strong>
     * <pre>{@code
     * String english = ThaiBaht.of(
     *     new BigDecimal("500.25"),
     *     ThaiBahtConfig.builder(Language.ENGLISH)
     *         .useUnit(true)
     *         .build()
     * );
     * // Output: "Five Hundred Baht Twenty-Five Satang"
     * }</pre>
     *
     * <p>
     * <strong>Example 2 - Thai without unit words:</strong>
     * <pre>{@code
     * String noUnits = ThaiBaht.of(
     *     new BigDecimal("100.50"),
     *     ThaiBahtConfig.builder()
     *         .useUnit(false)
     *         .build()
     * );
     * // Output: "หนึ่งร้อยห้าสิบ"
     * }</pre>
     *
     * <p>
     * <strong>Example 3 - Custom format template:</strong>
     * <pre>{@code
     * String custom = ThaiBaht.of(
     *     new BigDecimal("100.50"),
     *     ThaiBahtConfig.builder()
     *         .setFormatTemplate("[{INTEGER} {UNIT}] + [{FLOAT} {SATANG}]")
     *         .build()
     * );
     * // Output: "[หนึ่งร้อย บาท] + [ห้าสิบ สตางค์]"
     * }</pre>
     *
     * <p>
     * <strong>Example 4 - Custom negative prefix:</strong>
     * <pre>{@code
     * String negative = ThaiBaht.of(
     *     new BigDecimal("-50.00"),
     *     ThaiBahtConfig.builder()
     *         .setPrefix("ติดลบ")
     *         .build()
     * );
     * // Output: "ติดลบห้าสิบบาทถ้วน"
     * }</pre>
     *
     * @param amount the monetary amount to convert (baht and satang), must not be {@code null}
     * @param config formatting configuration to control output language, unit words, and formatting options,
     *               must not be {@code null}
     * @return the textual representation of the amount according to the specified configuration, never {@code null}
     * @throws NullPointerException if either {@code amount} or {@code config} is {@code null}
     * @see #of(BigDecimal)
     * @see ThaiBahtConfig
     * @see ThaiBahtConfig.Builder
     * @see Language
     */
    public static String of(BigDecimal amount, ThaiBahtConfig config) {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(config, "config must not be null");
        return TextConverter.toBahtText(new ThaiBaht(amount, config));
    }
}