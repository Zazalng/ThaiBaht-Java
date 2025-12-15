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

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utility entry point for converting numeric amounts into Thai baht text.
 * <p>
 * This class provides both static and instance-based approaches to convert a {@link BigDecimal}
 * amount to Thai-language text describing baht (บาท) and satang (สตางค์).
 *
 * <p>
 * Usage examples:
 * <pre>
 *   // Static usage
 *   String text = ThaiBaht.of(new BigDecimal("1234.56"));
 *   // Returns: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
 *
 *   // Instance-based with builder pattern
 *   ThaiBaht converter = ThaiBaht.create(new BigDecimal("100.00"))
 *       .config(b -> b.useUnit(true));
 *   String text = converter.toString();
 *   // Returns: "หนึ่งร้อยบาทถ้วน"
 * </pre>
 *
 * <p>
 * The class supports customizable formatting through {@link ThaiBahtConfig},
 * including control over unit word inclusion and negative number prefixes.
 *
 * @see ThaiBahtConfig
 * @since 1.0
 */
public final class ThaiBaht {
    private ThaiBahtConfig config;
    private BigDecimal amount;

    /**
     * Constructs a ThaiBaht instance with the specified amount and configuration.
     *
     * @param amount the monetary amount to convert, must not be {@code null}
     * @param config the formatting configuration, must not be {@code null}
     * @throws NullPointerException if either {@code amount} or {@code config} is {@code null}
     */
    private ThaiBaht(BigDecimal amount, ThaiBahtConfig config) {
        this.amount = Objects.requireNonNull(amount);
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Create a ThaiBaht converter with the specified amount using the default configuration.
     * <p>
     * The default configuration includes unit words ("บาท", "สตางค์") and uses formal wording.
     *
     * @param amount the monetary amount to convert (baht and satang), must not be {@code null}
     * @return a new ThaiBaht instance
     * @throws NullPointerException if {@code amount} is {@code null}
     */
    public static ThaiBaht create(BigDecimal amount) {
        return new ThaiBaht(amount, ThaiBahtConfig.defaultConfig());
    }

    /**
     * Create a ThaiBaht converter with the specified amount and custom configuration.
     * <p>
     * This allows you to control whether unit words are included and other formatting options.
     *
     * @param amount the monetary amount to convert (baht and satang), must not be {@code null}
     * @param config the formatting configuration, must not be {@code null}
     * @return a new ThaiBaht instance
     * @throws NullPointerException if {@code amount} or {@code config} is {@code null}
     */
    public static ThaiBaht create(BigDecimal amount, ThaiBahtConfig config) {
        return new ThaiBaht(amount, config);
    }

    /**
     * Update the amount to be converted.
     * <p>
     * This method supports a fluent builder-style interface.
     *
     * @param amount the new monetary amount, must not be {@code null}
     * @return this ThaiBaht instance for method chaining
     * @throws NullPointerException if {@code amount} is {@code null}
     */
    public ThaiBaht setAmount(BigDecimal amount) {
        this.amount = Objects.requireNonNull(amount);
        return this;
    }

    /**
     * Get the current amount as BigDecimal
     * @return its BigDecimal object
     */
    public BigDecimal getAmount(){
        return amount;
    }

    /**
     * Update the formatting configuration.
     * <p>
     * This method supports a fluent builder-style interface.
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
     * Modify the current configuration using a builder-style update function.
     * <p>
     * This method allows convenient inline configuration updates without having
     * to call {@link ThaiBahtConfig#toBuilder()} and {@link ThaiBahtConfig.Builder#build()} explicitly.
     *
     * @param updater a consumer function that modifies the configuration builder
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
     * Convert the stored amount to Thai baht text using the current configuration.
     * <p>
     * This method is called when the instance is converted to a string
     * (e.g., in string concatenation or explicit toString() calls).
     *
     * @return the Thai-language textual representation of the amount
     */
    @Override
    public String toString() {
        return ThaiTextConverter.toBahtText(amount, config);
    }

    /**
     * Convert the given amount to Thai baht text using the default configuration.
     * <p>
     * This is a convenience static method that uses the {@link ThaiBahtConfig#defaultConfig() default configuration},
     * which includes unit words ("บาท", "สตางค์") and formal wording rules.
     *
     * <p>
     * The amount is normalized to 2 decimal places (satang precision). Negative values are prefixed
     * with the Thai word for minus ("ลบ").
     *
     * @param amount the monetary amount to convert (baht and satang), must not be {@code null}
     * @return the Thai-language textual representation of the amount
     *         (for example: "หนึ่งร้อยบาทถ้วน" for 100.00, or "ลบหนึ่งบาท" for -1.00)
     * @throws NullPointerException if {@code amount} is {@code null}
     * @see #of(BigDecimal, ThaiBahtConfig)
     * @see ThaiBahtConfig#defaultConfig()
     */
    public static String of(BigDecimal amount) {
        return of(amount, ThaiBahtConfig.defaultConfig());
    }

    /**
     * Convert the given amount to Thai baht text using the provided configuration.
     * <p>
     * This method offers fine-grained control over output formatting by allowing
     * a custom {@link ThaiBahtConfig} to be specified. You can use this to:
     * <ul>
     *   <li>Control whether unit words are included via {@link ThaiBahtConfig.Builder#useUnit(boolean)}</li>
     *   <li>Customize the negative number prefix via {@link ThaiBahtConfig.Builder#setPrefix(String)}</li>
     *   <li>Use formal wording rules via {@link ThaiBahtConfig.Builder#formal(boolean)}</li>
     * </ul>
     *
     * <p>
     * Usage example:
     * <pre>
     *   ThaiBahtConfig config = ThaiBahtConfig.builder()
     *       .useUnit(true)
     *       .formal(true)
     *       .setPrefix("ลบ")
     *       .build();
     *   String text = ThaiBaht.of(new BigDecimal("500.25"), config);
     *   // Returns: "ห้าร้อยบาทยี่สิบห้าสตางค์"
     * </pre>
     *
     * @param amount the monetary amount to convert (baht and satang), must not be {@code null}
     * @param config formatting configuration to control inclusion of unit words and other options, must not be {@code null}
     * @return the Thai-language textual representation of the amount
     * @throws NullPointerException if {@code amount} or {@code config} is {@code null}
     * @see #of(BigDecimal)
     * @see ThaiBahtConfig
     * @see ThaiBahtConfig.Builder
     */
    public static String of(BigDecimal amount, ThaiBahtConfig config) {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(config, "config must not be null");
        return ThaiTextConverter.toBahtText(amount, config);
    }
}