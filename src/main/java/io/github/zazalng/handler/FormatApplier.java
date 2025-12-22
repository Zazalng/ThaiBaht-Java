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

import io.github.zazalng.contracts.Language;
import io.github.zazalng.contracts.LanguageHandler;
import io.github.zazalng.utils.FormatTemplate;

/**
 * Internal utility for applying custom format templates to baht/satang conversions.
 * <p>
 * This package-private class handles the substitution of named placeholders in custom format strings
 * with actual numeric text values and language-specific unit words. It enables flexible output
 * formatting for both positive and negative amounts while maintaining support for conditional placeholders.
 * Not intended for direct use; accessed through the public {@link io.github.zazalng.ThaiBaht} API via configuration.
 *
 * <h2>Placeholder Types</h2>
 *
 * <h3>Standard Placeholders (always available)</h3>
 * <ul>
 *   <li><strong>{@code {INTEGER}}</strong> - The baht (integer) part as text
 *       (e.g., "หนึ่งพัน", "One Thousand")</li>
 *   <li><strong>{@code {FLOAT}}</strong> - The satang (fractional) part as text
 *       (e.g., "ห้าสิบหก", "Fifty-Six")</li>
 *   <li><strong>{@code {UNIT}}</strong> - Currency unit word for this language
 *       ("บาท"/"Baht")</li>
 *   <li><strong>{@code {EXACT}}</strong> - The exact/only indicator when satang is zero
 *       ("ถ้วน"/"Only")</li>
 *   <li><strong>{@code {SATANG}}</strong> - Satang unit word for this language
 *       ("สตางค์"/"Satang")</li>
 *   <li><strong>{@code {NEGPREFIX}}</strong> - Negative prefix for this language/config
 *       ("ลบ"/"Minus")</li>
 * </ul>
 *
 * <h3>Conditional Placeholders (smart inclusion/exclusion)</h3>
 * <p>
 * Conditional placeholders show content only when the satang value is non-zero:
 * <ul>
 *   <li><strong>{@code {FLOAT?content}}</strong> - Show content only if satang ≠ zero.
 *       Useful for omitting satang when it's zero.</li>
 *   <li><strong>{@code {SATANG?content}}</strong> - Show content only if satang ≠ zero.
 *       Useful for conditional unit word inclusion.</li>
 * </ul>
 *
 * <p>
 * <strong>Important:</strong> Conditional logic works by checking if the value equals
 * the language's "zero" representation. For Thai: "ศูนย์", for English: "Zero".
 *
 * <h2>Format Template Examples</h2>
 *
 * <h3>Thai Examples</h3>
 * <table border="1">
 *   <tr>
 *     <th>Format Template</th>
 *     <th>Input: 100.50</th>
 *     <th>Input: 100.00</th>
 *   </tr>
 *   <tr>
 *     <td>{@code {INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}}}</td>
 *     <td>หนึ่งร้อยบาทห้าสิบสตางค์</td>
 *     <td>หนึ่งร้อยบาท</td>
 *   </tr>
 *   <tr>
 *     <td>{@code {INTEGER}{UNIT}{EXACT}{FLOAT?และ{FLOAT}{SATANG}}}</td>
 *     <td>หนึ่งร้อยบาทและห้าสิบสตางค์</td>
 *     <td>หนึ่งร้อยบาทถ้วน</td>
 *   </tr>
 *   <tr>
 *     <td>{@code [{INTEGER} {UNIT}]{FLOAT? ({FLOAT} {SATANG})}}</td>
 *     <td>[หนึ่งร้อย บาท] (ห้าสิบ สตางค์)</td>
 *     <td>[หนึ่งร้อย บาท]</td>
 *   </tr>
 * </table>
 *
 * <h3>English Examples</h3>
 * <table border="1">
 *   <tr>
 *     <th>Format Template</th>
 *     <th>Input: 100.50</th>
 *     <th>Input: 100.00</th>
 *   </tr>
 *   <tr>
 *     <td>{@code {INTEGER} {UNIT}{FLOAT? {FLOAT} {SATANG}}}</td>
 *     <td>One Hundred Baht Fifty Satang</td>
 *     <td>One Hundred Baht</td>
 *   </tr>
 *   <tr>
 *     <td>{@code {INTEGER} {UNIT} {EXACT}{FLOAT?and {FLOAT} {SATANG}}}</td>
 *     <td>One Hundred Baht and Fifty Satang</td>
 *     <td>One Hundred Baht Only</td>
 *   </tr>
 * </table>
 *
 * <h2>Processing Algorithm</h2>
 * <p>
 * The formatter processes templates in the following order:
 * <ol>
 *   <li><strong>Conditional placeholders first:</strong> {FLOAT?...} and {SATANG?...} are processed.
 *       These use brace-matching logic to find content boundaries, supporting nested braces.</li>
 *   <li><strong>Exact value handling:</strong> {EXACT} is replaced with the exact/only word if satang = 0,
 *       otherwise replaced with empty string.</li>
 *   <li><strong>Standard placeholders:</strong> {INTEGER}, {FLOAT}, {UNIT}, {SATANG}, {NEGPREFIX}
 *       are replaced with their corresponding values.</li>
 * </ol>
 *
 * <h2>Nesting and Complexity</h2>
 * <p>
 * The formatter supports arbitrary complexity including:
 * <ul>
 *   <li>Nested braces within conditional blocks</li>
 *   <li>Multiple conditional blocks in one template</li>
 *   <li>Arbitrary text between and around placeholders</li>
 *   <li>Conditional blocks wrapping multiple placeholders (e.g., {FLOAT?and {FLOAT} {SATANG}})</li>
 * </ul>
 *
 * <p>
 * <strong>Example - complex nested template:</strong>
 * <pre>{@code
 * Format: "Amount: {INTEGER} {UNIT}{FLOAT?{FLOAT? (with {FLOAT})} {SATANG}}"
 * Input: 100.50
 * Output: "Amount: One Hundred Baht (with Fifty) Satang"
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * This class is stateless and thread-safe. All state comes from method parameters.
 *
 * @see io.github.zazalng.ThaiBahtConfig
 * @see FormatTemplate
 * @see io.github.zazalng.ThaiBaht
 * @author Zazalng
 * @since 1.4.0
 */
public final class FormatApplier {
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private FormatApplier() {}

    /**
     * Applies a format template by replacing named placeholders with actual values using a LanguageHandler.
     * <p>
     * This method processes the format template string, replacing all supported placeholders with
     * their corresponding values from the provided LanguageHandler. It handles both standard and
     * conditional placeholders, respecting the language-specific formatting conventions.
     *
     * <h2>Placeholder Substitution</h2>
     * <p>
     * The method performs substitutions in the following order:
     * <ol>
     *   <li>Conditional placeholders ({@code {FLOAT?...}} and {@code {SATANG?...}}): Evaluated first
     *       with content shown only if satang is non-zero</li>
     *   <li>Exact value placeholder ({@code {EXACT}}): Replaced based on satang value</li>
     *   <li>Standard placeholders: {@code {INTEGER}}, {@code {FLOAT}}, {@code {UNIT}},
     *       {@code {SATANG}}, {@code {NEGPREFIX}}</li>
     * </ol>
     *
     * @param template the FormatTemplate containing the format string with named placeholders,
     *                 must not be {@code null}
     * @param bahtText the text representation of the baht (integer) part, replaces {@code {INTEGER}}
     * @param satangText the text representation of the satang (fractional) part, replaces {@code {FLOAT}}
     * @param satangValue the numeric satang value for conditional logic evaluation
     * @param handler the LanguageHandler providing currency units and conventions
     * @param negativePrefix the negative prefix for this configuration (replaces {@code {NEGPREFIX}})
     * @param zeroText the language-specific zero representation used for conditional comparison
     * @return the formatted string with all placeholders replaced, never {@code null}
     * @throws IllegalArgumentException if template or handler is null
     * @since 2.0.0
     */
    static String apply(
            FormatTemplate template,
            String bahtText,
            String satangText,
            int satangValue,
            LanguageHandler handler,
            String negativePrefix,
            String zeroText
    ) {
        if (template == null) {
            throw new IllegalArgumentException("Template must not be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("LanguageHandler must not be null");
        }

        String result = template.getTemplate();

        String currencyUnit = handler.getUnitWord();
        String exactValue = handler.getExactWord();
        String satangUnit = handler.getSatangWord();
        String prefix = (negativePrefix == null || negativePrefix.isEmpty())
                ? handler.getNegativePrefix() : negativePrefix;

        // Process conditionals first
        result = processConditionalPlaceholder(result, "{FLOAT?", satangText, zeroText);
        result = processConditionalPlaceholder(result, "{SATANG?", satangText, zeroText);

        // Replace {EXACT}
        if (satangValue == 0) {
            result = result.replace("{EXACT}", exactValue);
        } else {
            result = result.replace("{EXACT}", "");
        }

        // Replace standard placeholders
        result = result.replace("{INTEGER}", bahtText);
        result = result.replace("{FLOAT}", satangText);
        result = result.replace("{UNIT}", currencyUnit);
        result = result.replace("{SATANG}", satangUnit);
        result = result.replace("{NEGPREFIX}", prefix);

        return result;
    }

    /**
     * Applies a format template by replacing named placeholders with actual values.
     * <p>
     * This method processes the format template string, replacing all supported placeholders with
     * their corresponding values. It handles both standard and conditional placeholders, respecting
     * the language-specific formatting conventions.
     *
     * <h2>Placeholder Substitution</h2>
     * <p>
     * The method performs substitutions in the following order:
     * <ol>
     *   <li>Conditional placeholders ({@code {FLOAT?...}} and {@code {SATANG?...}}): Evaluated first
     *       with content shown only if satang is non-zero</li>
     *   <li>Exact value placeholder ({@code {EXACT}}): Replaced based on satang value</li>
     *   <li>Standard placeholders: {@code {INTEGER}}, {@code {FLOAT}}, {@code {UNIT}},
     *       {@code {SATANG}}, {@code {NEGPREFIX}}</li>
     * </ol>
     *
     * <h2>Conditional Logic</h2>
     * <p>
     * Conditional placeholders work by comparing the satang text against the language's zero
     * representation:
     * <ul>
     *   <li>For Thai: Compare against "ศูนย์"</li>
     *   <li>For English: Compare against "Zero"</li>
     * </ul>
     *
     * If the satang is non-zero, the content within the conditional block is included;
     * otherwise, it is omitted entirely.
     *
     * <h2>Brace Matching for Conditionals</h2>
     * <p>
     * The formatter uses sophisticated brace-matching logic to handle nested conditional blocks.
     * When processing {@code {FLOAT?content}}, it finds the matching closing brace by counting
     * nesting levels, allowing for complex nested structures.
     *
     * <p>
     * <strong>Example of nesting:</strong>
     * <pre>{@code
     * Template: "{FLOAT?{FLOAT? ({FLOAT})} {SATANG}}"
     * The outer {FLOAT?...} finds its closing brace despite the inner {FLOAT?...}
     * }</pre>
     *
     * <h2>Error Handling</h2>
     * <p>
     * If the template is null, an {@link IllegalArgumentException} is thrown.
     * If placeholders contain unmatched braces, remaining text is included as-is.
     *
     * <p>
     * <strong>Examples:</strong>
     * <pre>{@code
     * // Thai example with conditional
     * FormatTemplate template = FormatTemplate.of("{INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}}");
     * String result = FormatApplier.apply(
     *     template, "หนึ่งร้อย", "ห้าสิบ", 50,
     *     Language.THAI, "ลบ", "ศูนย์"
     * );
     * // Result: "หนึ่งร้อยบาทห้าสิบสตางค์"
     *
     * // English example without satang
     * FormatTemplate template2 = FormatTemplate.of("{INTEGER} {UNIT} {EXACT}");
     * String result2 = FormatApplier.apply(
     *     template2, "One Hundred", "Zero", 0,
     *     Language.ENGLISH, "Minus", "Zero"
     * );
     * // Result: "One Hundred Baht Only"
     * }</pre>
     *
     * @param template the FormatTemplate containing the format string with named placeholders,
     *                 must not be {@code null}
     * @param bahtText the text representation of the baht (integer) part, replaces {@code {INTEGER}}
     * @param satangText the text representation of the satang (fractional) part, replaces {@code {FLOAT}}
     * @param satangValue the numeric satang value for conditional logic evaluation
     * @param language the language context for currency units and conventions
     * @param negativePrefix the negative prefix for this configuration (replaces {@code {NEGPREFIX}})
     * @param zeroText the language-specific zero representation used for conditional comparison
     * @return the formatted string with all placeholders replaced, never {@code null}
     * @throws IllegalArgumentException if template is null
     * @since 1.4.0
     * @deprecated Use {@link #apply(FormatTemplate, String, String, int, LanguageHandler, String, String)} instead
     */
    @Deprecated
    static String apply(
            FormatTemplate template,
            String bahtText,
            String satangText,
            int satangValue,
            Language language,
            String negativePrefix,
            String zeroText
    ) {
        if (template == null) {
            throw new IllegalArgumentException("Template must not be null");
        }

        String result = template.getTemplate();

        String currencyUnit = language.getUnit();
        String exactValue = language.getExact();
        String satangUnit = language.getSatang();
        String prefix = (negativePrefix == null || negativePrefix.isEmpty())
                ? language.getPrefix() : negativePrefix;

        // Process conditionals first
        result = processConditionalPlaceholder(result, "{FLOAT?", satangText, zeroText);
        result = processConditionalPlaceholder(result, "{SATANG?", satangText, zeroText);

        // Replace {EXACT}
        if (satangValue == 0) {
            result = result.replace("{EXACT}", exactValue);
        } else {
            result = result.replace("{EXACT}", "");
        }

        // Replace standard placeholders
        result = result.replace("{INTEGER}", bahtText);
        result = result.replace("{FLOAT}", satangText);
        result = result.replace("{UNIT}", currencyUnit);
        result = result.replace("{SATANG}", satangUnit);
        result = result.replace("{NEGPREFIX}", prefix);

        return result;
    }

    /**
     * Processes a single type of conditional placeholder in the text.
     * <p>
     * This helper method finds all instances of a conditional placeholder (e.g., {@code {FLOAT?})
     * and evaluates the condition. If the value is not the zero representation, the content
     * within the braces is included; otherwise, it is omitted.
     *
     * <p>
     * <strong>Algorithm:</strong>
     * <ol>
     *   <li>Find the next occurrence of the placeholder prefix</li>
     *   <li>Locate the matching closing brace using {@link #findMatchingBrace(String, int)}</li>
     *   <li>Extract content between opening and closing brace</li>
     *   <li>Compare value against zeroText</li>
     *   <li>Include or exclude content accordingly</li>
     *   <li>Repeat until no more occurrences found</li>
     * </ol>
     *
     * @param text the format string being processed
     * @param placeholder the conditional placeholder prefix including prefix portion (e.g., "{FLOAT?")
     * @param value the value to check against zero representation
     * @param zeroText the zero representation in the target language
     * @return the text with all instances of the conditional placeholder processed
     */
    private static String processConditionalPlaceholder(String text, String placeholder, String value, String zeroText) {
        int startIdx = text.indexOf(placeholder);
        if (startIdx == -1) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int lastIdx = 0;

        while (startIdx != -1) {
            // Append text before the placeholder
            result.append(text, lastIdx, startIdx);

            // Find the MATCHING closing brace by counting nesting levels
            int contentStart = startIdx + placeholder.length();
            int endIdx = findMatchingBrace(text, contentStart);

            if (endIdx == -1) {
                // Formatting error: no matching brace found.
                // Treat rest of string as literal to avoid crash, or throw exception.
                result.append(text, startIdx, text.length());
                lastIdx = text.length();
                break;
            }

            // Extract the content inside the conditional block
            String content = text.substring(contentStart, endIdx);

            // Check if value is not "Zero"
            if (!value.equals(zeroText)) {
                result.append(content);
            }

            // Move past the closing brace
            lastIdx = endIdx + 1;

            // Find next occurrence
            startIdx = text.indexOf(placeholder, lastIdx);
        }

        // Append remaining text
        if (lastIdx < text.length()) {
            result.append(text, lastIdx, text.length());
        }

        return result.toString();
    }

    /**
     * Finds the index of the closing brace that matches an opening brace using nesting level tracking.
     * <p>
     * This helper method locates the matching closing brace for an opening brace by maintaining
     * a nesting level counter. Each opening brace increments the counter, each closing brace
     * decrements it. When the counter reaches 0, the matching brace is found.
     *
     * <p>
     * <strong>Algorithm:</strong>
     * <ul>
     *   <li>Start with nesting level = 1 (accounting for the opening brace at the start of the search region)</li>
     *   <li>Scan each character from startSearchIndex onward</li>
     *   <li>Increment nesting level on each '{' encountered</li>
     *   <li>Decrement nesting level on each '}' encountered</li>
     *   <li>Return index when nesting level reaches 0</li>
     *   <li>Return -1 if no matching brace found (end of string reached)</li>
     * </ul>
     *
     * <p>
     * <strong>Example:</strong>
     * <pre>{@code
     * Text: "abc{def{ghi}jkl}mno"
     * Search from index 4 (first '{'):
     * - Index 4: '{' → nesting = 2
     * - Index 8: '{' → nesting = 3
     * - Index 12: '}' → nesting = 2
     * - Index 16: '}' → nesting = 1 (returns 16)
     * }</pre>
     *
     * @param text The full text being searched
     * @param startSearchIndex The index to start searching from (immediately after or inside the first '{'
     * @return The index of the matching '}', or -1 if not found
     */
    private static int findMatchingBrace(String text, int startSearchIndex) {
        int nestingLevel = 1; // We start at 1 because the placeholder prefix included the opening '{'

        for (int i = startSearchIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                nestingLevel++;
            } else if (c == '}') {
                nestingLevel--;
                if (nestingLevel == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
}

