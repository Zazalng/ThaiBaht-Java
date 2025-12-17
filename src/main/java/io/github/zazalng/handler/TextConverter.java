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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Internal converter that transforms numeric {@link BigDecimal} amounts into
 * language-specific text describing baht and satang.
 * <p>
 * This class is package-private and intended for internal use only. It
 * normalizes amounts to 2 decimal places using {@link RoundingMode#DOWN}
 * (truncation) before converting; negative values are prefixed with
 * the configured negative prefix.
 *
 * <p>
 * Supports multiple languages:
 * <ul>
 *   <li><strong>Thai:</strong> Converts amounts to Thai text (e.g., "หนึ่งร้อยบาท")</li>
 *   <li><strong>English:</strong> Converts amounts to English text (e.g., "one hundred baht")</li>
 * </ul>
 *
 * <p>
 * <strong>Conversion Algorithm:</strong>
 * <ul>
 *   <li>Amounts are normalized to 2 decimal places (satang precision)</li>
 *   <li>Negative amounts are handled by extracting the absolute value and prefixing with the negative prefix</li>
 *   <li>The integer part (baht) and fractional part (satang) are converted separately to language-specific text</li>
 *   <li>Special handling for large numbers (e.g., millions)</li>
 * </ul>
 *
 * <p>
 * <strong>Language-specific Rules:</strong>
 * <ul>
 *   <li><strong>Thai:</strong> Special rules apply for "หนึ่ง" (one), "สอง" (two), and "ล้าน" (million)</li>
 *   <li><strong>English:</strong> Standard English naming conventions with hyphens for compound numbers</li>
 * </ul>
 *
 * @implNote This class uses a stateless conversion algorithm and is thread-safe.
 * The conversion process maintains precision by operating on the normalized BigDecimal
 * representation rather than floating-point arithmetic.
 *
 * @see ThaiBaht
 * @see ThaiBahtConfig
 * @see Language
 */
public final class TextConverter {
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private TextConverter() {}

    /**
     * Convert a {@link BigDecimal} amount to language-specific baht text.
     * <p>
     * This method is the core conversion entry point. It:
     * <ol>
     *   <li>Normalizes the input amount to 2 decimal places</li>
     *   <li>Handles negative amounts by extracting the absolute value and using the configured prefix</li>
     *   <li>Routes to the appropriate language-specific converter</li>
     *   <li>Separates baht (integer part) and satang (fractional part)</li>
     *   <li>Converts each part according to configuration rules</li>
     * </ol>
     *
     * @param baht normalize class that keep API standalone
     * @return the language-specific textual representation of the amount
     * @throws IllegalArgumentException if {@code amount} is {@code null}
     * @see RoundingMode#DOWN
     * @see Language
     */
    public static String toBahtText(ThaiBaht baht) {
        if (baht.getAmount() == null) throw new IllegalArgumentException("amount must not be null");
        if (baht.getConfig() == null) baht.setConfig(ThaiBahtConfig.defaultConfig());
        String result;

        // Route to appropriate language converter
        switch(baht.getConfig().getLanguage()){
            case THAI:
                result = ThaiConvertHandler.convert(baht);
                break;
            case ENGLISH:
                result = EnglishConvertHandler.convert(baht);
                break;
            default:
                result = "";
        }

        return result;
    }
}