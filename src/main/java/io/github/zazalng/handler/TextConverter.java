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
package io.github.zazalng.handler;

import io.github.zazalng.ThaiBaht;
import io.github.zazalng.ThaiBahtConfig;
import io.github.zazalng.contracts.Language;

import java.math.RoundingMode;

/**
 * Internal router and dispatcher for converting numeric amounts to language-specific text.
 * <p>
 * This package-private utility class serves as the central conversion dispatcher. It is not
 * intended for direct use by library consumers; instead, use the public {@link ThaiBaht} API.
 * This class routes conversion requests to the appropriate language-specific handler based on
 * the configured {@link Language}.
 *
 * <h2>Conversion Process</h2>
 * <p>
 * The conversion process follows these steps:
 * <ol>
 *   <li>Receives a {@link ThaiBaht} instance containing an amount and configuration</li>
 *   <li>Validates that the amount and configuration are present</li>
 *   <li>Routes to the appropriate language handler:
 *       <ul>
 *         <li>{@code THAI} → {@link ThaiConvertHandler#convert(ThaiBaht)}</li>
 *         <li>{@code ENGLISH} → {@link EnglishConvertHandler#convert(ThaiBaht)}</li>
 *       </ul>
 *   </li>
 *   <li>Returns the formatted text from the language handler</li>
 * </ol>
 *
 * <h2>Amount Normalization</h2>
 * <p>
 * Each language-specific handler internally normalizes the amount to 2 decimal places using
 * {@link java.math.RoundingMode#DOWN} (truncation). This ensures consistency in satang
 * precision across all conversions.
 *
 * <h2>Language-Specific Rules</h2>
 * <p>
 * Each language handler implements specialized conversion logic:
 * <ul>
 *   <li><strong>Thai:</strong> Applies Thai grammar rules for special cases like "เอ็ด", "ยี่", and repeating "ล้าน"</li>
 *   <li><strong>English:</strong> Applies English conventions including hyphens for compound numbers and
 *       standard position words (Hundred, Thousand, Million)</li>
 * </ul>
 *
 * <h2>Configuration Handling</h2>
 * <p>
 * The converter respects all configuration options from {@link ThaiBahtConfig}, including:
 * <ul>
 *   <li>Unit word inclusion/exclusion</li>
 *   <li>Custom format templates</li>
 *   <li>Negative amount prefixes</li>
 *   <li>Formal wording rules</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * This class is stateless and thread-safe. All instance state comes from the input
 * {@link ThaiBaht} object, and the conversion algorithm maintains no mutable state.
 *
 * @implNote This class is package-private and should not be accessed from outside this package.
 *           Use the {@link ThaiBaht} public API instead.
 * @see ThaiBaht
 * @see ThaiBahtConfig
 * @see Language
 * @see ThaiConvertHandler
 * @see EnglishConvertHandler
 * @author Zazalng
 * @since 1.0
 * @version 1.3.0
 */
public final class TextConverter {
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private TextConverter() {}

    /**
     * Converts a {@link ThaiBaht} instance to language-specific baht text.
     * <p>
     * This method is the core internal conversion entry point for the library. It takes a {@link ThaiBaht}
     * instance containing an amount and configuration, validates the inputs, and routes the conversion
     * to the language handler in the configuration. The result respects all configuration options.
     *
     * <h2>Conversion Process</h2>
     * <p>
     * The conversion process follows these steps:
     * <ol>
     *   <li>Receives a {@link ThaiBaht} instance containing an amount and configuration</li>
     *   <li>Validates that the amount and configuration are present</li>
     *   <li>Routes to the language handler stored in the configuration via
     *       {@link ThaiBahtConfig#getLanguageHandler()}</li>
     *   <li>Returns the formatted text from the language handler</li>
     * </ol>
     *
     * <h2>Amount Normalization</h2>
     * <p>
     * The amount is normalized to 2 decimal places (satang precision) using {@link RoundingMode#DOWN}.
     * This ensures that amounts like 100.567 are truncated to 100.56 (56 satang), not rounded.
     *
     * <h2>Negative Handling</h2>
     * <p>
     * Negative amounts are handled by the language handler by extracting the absolute value
     * and prepending the configured negative prefix (or handler's default if not specified).
     *
     * <p>
     * <strong>Example conversions:</strong>
     * <pre>{@code
     * // Thai positive
     * ThaiBaht baht1 = ThaiBaht.create(new BigDecimal("1234.56"));
     * String thai = TextConverter.toBahtText(baht1);
     * // Result: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
     *
     * // English with units
     * ThaiBahtConfig engConfig = ThaiBahtConfig.builder(new EnglishLanguageHandler())
     *     .useUnit(true).build();
     * ThaiBaht baht2 = ThaiBaht.create(new BigDecimal("500.50"), engConfig);
     * String english = TextConverter.toBahtText(baht2);
     * // Result: "Five Hundred Baht Fifty Satang"
     *
     * // Negative amount
     * ThaiBaht baht3 = ThaiBaht.create(new BigDecimal("-100"));
     * String negative = TextConverter.toBahtText(baht3);
     * // Result: "ลบหนึ่งร้อยบาทถ้วน"
     * }</pre>
     *
     * @param baht the ThaiBaht instance containing amount and configuration, must not be {@code null}
     * @return the language-specific textual representation of the amount, never {@code null}
     * @throws IllegalArgumentException if the amount is {@code null}
     * @see ThaiBaht
     * @see ThaiBahtConfig
     * @since 2.0.0
     */
    public static String toBahtText(ThaiBaht baht) {
        if (baht.getAmount() == null) throw new IllegalArgumentException("amount must not be null");
        if (baht.getConfig() == null) baht.setConfig(ThaiBahtConfig.defaultConfig());

        // Get the language handler from configuration and convert
        return baht.getConfig().getLanguageHandler().convert(baht);
    }
}