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

/**
 * Enumeration of supported output languages for currency text conversion.
 * <p>
 * This enum defines the available languages that can be used when converting numeric amounts
 * to textual representations in the Thai Baht library. Each language has specific formatting rules,
 * unit words, and a default negative prefix that is automatically applied when using the configuration builder.
 *
 * <h2>Supported Languages</h2>
 * <ul>
 *   <li><strong>THAI:</strong> Converts amounts to Thai text with Thai grammar rules.
 *       Unit words: "บาท" (Baht), "สตางค์" (Satang), "ถ้วน" (Only/Exact).
 *       Default negative prefix: "ลบ" (Thai minus)</li>
 *   <li><strong>ENGLISH:</strong> Converts amounts to English with standard English conventions.
 *       Unit words: "Baht", "Satang", "Only".
 *       Default negative prefix: "Minus"</li>
 * </ul>
 *
 * <h2>Language-Specific Rules</h2>
 * <p>
 * Each language has specialized conversion handlers:
 * <ul>
 *   <li><strong>Thai:</strong> Implements Thai numerical grammar including:
 *       <ul>
 *         <li>"เอ็ด" for ones in larger numbers (e.g., 101 = "หนึ่งร้อยเอ็ด")</li>
 *         <li>"ยี่" for tens/twenties (e.g., 20 = "ยี่สิบ")</li>
 *         <li>Repeating "ล้าน" for millions and beyond</li>
 *         <li>Silent "หนึ่ง" in certain positions</li>
 *       </ul>
 *   </li>
 *   <li><strong>English:</strong> Implements standard English conventions including:
 *       <ul>
 *         <li>Hyphenated compound numbers (e.g., "Twenty-Five")</li>
 *         <li>Standard position words: "Hundred", "Thousand", "Million"</li>
 *         <li>Regular singular/plural handling</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <h2>Negative Prefix Behavior (v1.3.0+)</h2>
 * <p>
 * Each language has a default negative prefix that is applied when configuring for negative amounts:
 * <ul>
 *   <li><strong>THAI:</strong> Default prefix "ลบ" (Thai minus)</li>
 *   <li><strong>ENGLISH:</strong> Default prefix "Minus"</li>
 * </ul>
 *
 * When switching languages via {@link io.github.zazalng.ThaiBahtConfig.Builder#language(Language)},
 * the prefix automatically updates to the new language's default UNLESS it has been explicitly set
 * via {@link io.github.zazalng.ThaiBahtConfig.Builder#setPrefix(String)}. Once you set a custom prefix,
 * it is preserved even when changing languages. To reset to language defaults, create a new builder.
 *
 * <h2>Usage Examples</h2>
 *
 * <p>
 * <strong>Example 1 - Thai language (default):</strong>
 * <pre>{@code
 * ThaiBahtConfig config = ThaiBahtConfig.builder()
 *     .language(Language.THAI)
 *     .build();
 * String result = ThaiBaht.of(new BigDecimal("1234.56"), config);
 * // Output: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
 * }</pre>
 *
 * <p>
 * <strong>Example 2 - English language:</strong>
 * <pre>{@code
 * ThaiBahtConfig config = ThaiBahtConfig.builder(Language.ENGLISH)
 *     .build();
 * String result = ThaiBaht.of(new BigDecimal("1234.56"), config);
 * // Output: "One Thousand Two Hundred Thirty-Four Baht Fifty-Six Satang"
 * }</pre>
 *
 * <p>
 * <strong>Example 3 - Language switching with prefix preservation:</strong>
 * <pre>{@code
 * // Thai with custom prefix
 * ThaiBahtConfig thai = ThaiBahtConfig.builder()
 *     .setPrefix("ติดลบ")  // Explicitly set
 *     .build();
 *
 * // Switch to English - custom prefix is preserved
 * ThaiBahtConfig english = thai.toBuilder()
 *     .language(Language.ENGLISH)
 *     .build();
 * // Prefix remains "ติดลบ" instead of updating to "Minus"
 * }</pre>
 *
 * <p>
 * <strong>Example 4 - Negative amounts:</strong>
 * <pre>{@code
 * String thai = ThaiBaht.of(new BigDecimal("-500"),
 *     ThaiBahtConfig.builder(Language.THAI).build());
 * // Output: "ลบห้าร้อยบาทถ้วน"
 *
 * String english = ThaiBaht.of(new BigDecimal("-500"),
 *     ThaiBahtConfig.builder(Language.ENGLISH).build());
 * // Output: "Minus Five Hundred Baht Only"
 * }</pre>
 *
 * @see io.github.zazalng.ThaiBaht
 * @see io.github.zazalng.ThaiBahtConfig
 * @see io.github.zazalng.ThaiBahtConfig.Builder
 * @author Zazalng
 * @since 1.3.0
 */
public enum Language {
    /**
     * Thai language output.
     * <p>
     * Converts amounts to Thai text representation using traditional Thai numerical conventions.
     * Includes special handling for Thai grammar rules such as "เอ็ด" (ones in compounds),
     * "ยี่" (tens), and repeating "ล้าน" (millions).
     *
     * <p>
     * Unit words:
     * <ul>
     *   <li>"บาท" (Baht) - Main currency unit</li>
     *   <li>"สตางค์" (Satang) - Fractional currency unit</li>
     *   <li>"ถ้วน" (Only/Exact) - Indicator when satang is zero</li>
     * </ul>
     *
     * <p>
     * Default negative prefix: "ลบ" (Thai minus)
     *
     * <p>
     * <strong>Example output:</strong>
     * <ul>
     *   <li>1234.56 → "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"</li>
     *   <li>100.00 → "หนึ่งร้อยบาทถ้วน"</li>
     *   <li>-50.00 → "ลบห้าสิบบาทถ้วน"</li>
     * </ul>
     */
    THAI("บาท", "ถ้วน", "สตางค์", "ลบ"),

    /**
     * English language output.
     * <p>
     * Converts amounts to English text representation using standard English conventions.
     * Includes proper formatting with hyphens for compound numbers and standard position names.
     *
     * <p>
     * Unit words:
     * <ul>
     *   <li>"Baht" - Main currency unit</li>
     *   <li>"Satang" - Fractional currency unit</li>
     *   <li>"Only" - Indicator when satang is zero (also called "Exact")</li>
     * </ul>
     *
     * <p>
     * Default negative prefix: "Minus"
     *
     * <p>
     * <strong>Example output:</strong>
     * <ul>
     *   <li>1234.56 → "One Thousand Two Hundred Thirty-Four Baht Fifty-Six Satang"</li>
     *   <li>100.00 → "One Hundred Baht Only"</li>
     *   <li>-50.00 → "Minus Fifty Baht Only"</li>
     * </ul>
     */
    ENGLISH("Baht", "Only", "Satang", "Minus");

    private final String unit;
    private final String exact;
    private final String satang;
    private final String prefix;

    Language(String unit, String exact, String satang, String prefix){
        this.unit = unit;
        this.exact = exact;
        this.satang = satang;
        this.prefix = prefix;
    }
    /**
     * Returns the currency unit word for this language.
     * <p>
     * The main currency unit represents the baht (integer part).
     *
     * @return the currency unit word ("บาท" for THAI, "Baht" for ENGLISH), never {@code null}
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns the exact/only indicator word for this language.
     * <p>
     * This word is displayed when the amount has no fractional satang (i.e., satang = 0).
     *
     * @return the exact indicator ("ถ้วน" for THAI, "Only" for ENGLISH), never {@code null}
     */
    public String getExact() {
        return exact;
    }

    /**
     * Returns the satang (fractional) unit word for this language.
     * <p>
     * The satang unit represents the fractional part of the currency.
     *
     * @return the satang unit word ("สตางค์" for THAI, "Satang" for ENGLISH), never {@code null}
     */
    public String getSatang() {
        return satang;
    }

    /**
     * Returns the default negative prefix for this language.
     * <p>
     * This prefix is automatically used for negative amounts unless explicitly overridden
     * via {@link io.github.zazalng.ThaiBahtConfig.Builder#setPrefix(String)}.
     *
     * @return the negative prefix ("ลบ" for THAI, "Minus" for ENGLISH), never {@code null}
     */
    public String getPrefix() {
        return prefix;
    }
}

