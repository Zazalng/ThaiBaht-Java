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
package io.github.zazalng.contracts;

import io.github.zazalng.ThaiBaht;

/**
 * Interface for language-specific text conversion with self-contained language configuration.
 * <p>
 * Implement this interface to provide custom language conversion logic for the Thai Baht library.
 * Each handler completely defines its language's behavior, including conversion rules and unit words.
 * This design allows unlimited language extensibility without modifying core library code.
 *
 * <h2>Implementation Guidelines</h2>
 * <p>
 * When implementing this interface:
 * <ul>
 *   <li>The {@code convert()} method must return non-null text representation</li>
 *   <li>Language metadata methods must return non-null values</li>
 *   <li>Conversion must respect the configuration within the {@link ThaiBaht} instance</li>
 *   <li>The implementation should be stateless and thread-safe</li>
 * </ul>
 *
 * <h2>Built-in Handlers</h2>
 * <p>
 * The library provides default implementations:
 * <ul>
 *   <li>{@link io.github.zazalng.handler.ThaiLanguageHandler} - Thai language conversion</li>
 *   <li>{@link io.github.zazalng.handler.EnglishLanguageHandler} - English language conversion</li>
 * </ul>
 *
 * <h2>Custom Language Example</h2>
 * <pre>{@code
 * public class LaotianLanguageHandler implements LanguageHandler {
 *     @Override
 *     public String convert(ThaiBaht baht) {
 *         // Implement Laotian conversion logic
 *         return "Laotian text representation";
 *     }
 *
 *     @Override
 *     public String getLanguageCode() { return "lo"; }
 *
 *     @Override
 *     public String getLanguageName() { return "Laotian"; }
 *
 *     @Override
 *     public String getUnitWord() { return "ກີບ"; } // Kip
 *
 *     @Override
 *     public String getExactWord() { return "ເທົ່າ"; }
 *
 *     @Override
 *     public String getSatangWord() { return "ແອັດ"; }
 *
 *     @Override
 *     public String getNegativePrefix() { return "ລົບ"; }
 * }
 *
 * // Usage:
 * ThaiBahtConfig config = ThaiBahtConfig.builder(new LaotianLanguageHandler())
 *     .useUnit(true)
 *     .build();
 * }</pre>
 *
 * @see io.github.zazalng.ThaiBaht
 * @see io.github.zazalng.ThaiBahtConfig
 * @see io.github.zazalng.handler.ThaiLanguageHandler
 * @see io.github.zazalng.handler.EnglishLanguageHandler
 * @author Zazalng
 * @since 2.0.0
 */
public interface LanguageHandler {

    /**
     * Converts the amount in the given {@link ThaiBaht} instance to language-specific text.
     * <p>
     * The implementation must respect the configuration options from {@code baht.getConfig()},
     * including unit word inclusion, custom format templates, and negative prefix settings.
     *
     * @param baht the {@link ThaiBaht} instance containing amount and configuration, never null
     * @return the language-specific textual representation of the amount, never null
     * @throws NullPointerException if baht is null
     * @throws IllegalArgumentException if the amount is null or invalid
     */
    String convert(ThaiBaht baht);

    /**
     * Returns the language code identifier for this handler.
     * <p>
     * This is typically a two or three-letter ISO 639 code (e.g., "th" for Thai, "en" for English).
     *
     * @return the language code, never null or empty
     */
    String getLanguageCode();

    /**
     * Returns the human-readable name of this language.
     * <p>
     * This is typically used for display and identification purposes.
     *
     * @return the language name (e.g., "Thai", "English"), never null or empty
     */
    String getLanguageName();

    /**
     * Returns the currency unit word for the integer (baht) part.
     * <p>
     * Examples:
     * <ul>
     *   <li>Thai: "บาท" (Baht)</li>
     *   <li>English: "Baht"</li>
     *   <li>Laotian: "ກີບ" (Kip)</li>
     * </ul>
     *
     * @return the unit word, never null or empty
     */
    String getUnitWord();

    /**
     * Returns the exact/only indicator word displayed when satang (fractional part) is zero.
     * <p>
     * Examples:
     * <ul>
     *   <li>Thai: "ถ้วน" (Exact/Full)</li>
     *   <li>English: "Only"</li>
     * </ul>
     *
     * @return the exact indicator word, never null or empty
     */
    String getExactWord();

    /**
     * Returns the satang (fractional currency unit) word.
     * <p>
     * Examples:
     * <ul>
     *   <li>Thai: "สตางค์" (Satang)</li>
     *   <li>English: "Satang"</li>
     * </ul>
     *
     * @return the satang unit word, never null or empty
     */
    String getSatangWord();

    /**
     * Returns the default prefix applied to negative amounts.
     * <p>
     * This prefix is used when the amount is negative, unless explicitly overridden
     * via {@link io.github.zazalng.ThaiBahtConfig.Builder#setPrefix(String)}.
     * <p>
     * Examples:
     * <ul>
     *   <li>Thai: "ลบ" (Thai minus symbol)</li>
     *   <li>English: "Minus"</li>
     * </ul>
     *
     * @return the negative prefix, never null or empty
     */
    String getNegativePrefix();
}

