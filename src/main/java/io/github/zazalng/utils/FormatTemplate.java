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
package io.github.zazalng.utils;


/**
 * Immutable template for custom format strings with named placeholder support (v1.4.0+).
 * <p>
 * This class encapsulates a custom format string and provides validation to ensure the format
 * is valid before use. All instances are immutable and thread-safe. Format templates enable
 * precise control over how monetary amounts are displayed in text form.
 *
 * <h2>Supported Placeholders</h2>
 * <p>
 * All placeholders must be wrapped in curly braces. The following placeholders are recognized:
 *
 * <h3>Required Placeholders</h3>
 * <ul>
 *   <li><strong>{@code {INTEGER}}</strong> - The baht (integer) part of the amount as text.
 *       This placeholder is mandatory and must appear in every format.</li>
 *   <li><strong>{@code {FLOAT}}</strong> - The satang (fractional) part as text.
 *       This placeholder is mandatory and must appear in every format.</li>
 * </ul>
 *
 * <h3>Optional Currency Placeholders</h3>
 * <ul>
 *   <li><strong>{@code {UNIT}}</strong> - Currency unit word ("บาท"/"Baht")</li>
 *   <li><strong>{@code {EXACT}}</strong> - Exact/only indicator ("ถ้วน"/"Only") shown when satang = 0</li>
 *   <li><strong>{@code {SATANG}}</strong> - Satang unit word ("สตางค์"/"Satang")</li>
 *   <li><strong>{@code {NEGPREFIX}}</strong> - Negative prefix ("ลบ"/"Minus") for negative format only</li>
 * </ul>
 *
 * <h3>Conditional Placeholders</h3>
 * <p>
 * Conditional placeholders allow content to be shown or hidden based on whether satang is zero:
 * <ul>
 *   <li><strong>{@code {FLOAT?content}}</strong> - Show content only if satang is not zero.
 *       Useful for omitting satang when there are no fractional units.</li>
 *   <li><strong>{@code {SATANG?content}}</strong> - Show content only if satang is not zero.
 *       Useful for conditional unit word inclusion.</li>
 * </ul>
 *
 * <p>
 * Conditional content can contain arbitrary text and other placeholders, supporting complex
 * nested structures.
 *
 * <h2>Validation and Requirements</h2>
 * <p>
 * Format strings are validated at construction time to ensure:
 * <ul>
 *   <li>The string is not null or empty</li>
 *   <li>Both {@code {INTEGER}} and {@code {FLOAT}} placeholders are present</li>
 *   <li>If validation fails, an {@link IllegalArgumentException} is thrown with a descriptive message</li>
 * </ul>
 *
 * <p>
 * <strong>Validation errors:</strong>
 * <pre>{@code
 * // Will throw: Missing {INTEGER}
 * FormatTemplate.of("{FLOAT}");
 *
 * // Will throw: Missing {FLOAT}
 * FormatTemplate.of("{INTEGER}");
 *
 * // OK: Both required placeholders present
 * FormatTemplate.of("{INTEGER}{FLOAT}");
 * }</pre>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Basic Thai Format</h3>
 * <pre>{@code
 * FormatTemplate template = FormatTemplate.of("{INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}}");
 * // Output for 100.50: "หนึ่งร้อยบาทห้าสิบสตางค์"
 * // Output for 100.00: "หนึ่งร้อยบาท"
 * }</pre>
 *
 * <h3>English with Explicit Exact Value</h3>
 * <pre>{@code
 * FormatTemplate template = FormatTemplate.of("{INTEGER} {UNIT} {EXACT}{FLOAT? {FLOAT} {SATANG}}");
 * // Output for 100.50: "One Hundred Baht Fifty Satang"
 * // Output for 100.00: "One Hundred Baht Only"
 * }</pre>
 *
 * <h3>Parenthesized Negative Format</h3>
 * <pre>{@code
 * FormatTemplate template = FormatTemplate.of("({NEGPREFIX} {INTEGER}{UNIT}{EXACT}{FLOAT? {FLOAT} {SATANG}})");
 * // Output for negative 100.50: "(ลบ หนึ่งร้อยบาทห้าสิบสตางค์)"
 * }</pre>
 *
 * <h3>Advanced with Conjunctions</h3>
 * <pre>{@code
 * FormatTemplate template = FormatTemplate.of(
 *     "{INTEGER}{UNIT}{FLOAT?และ{FLOAT}{SATANG}}"
 * );
 * // Output for 100.50: "หนึ่งร้อยบาทและห้าสิบสตางค์"
 * // Output for 100.00: "หนึ่งร้อยบาท"
 * }</pre>
 *
 * <h2>Immutability and Thread Safety</h2>
 * <p>
 * This class is fully immutable - all fields are private, final, and set at construction time.
 * Instances are safe to share across threads without synchronization. Format templates
 * can be constructed once and reused across multiple configurations.
 *
 * <p>
 * <strong>Example - reusing format templates:</strong>
 * <pre>{@code
 * // Create template once
 * FormatTemplate template = FormatTemplate.of("{INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}}");
 *
 * // Reuse in multiple configurations
 * ThaiBahtConfig config1 = ThaiBahtConfig.builder()
 *     .setFormatTemplate(template)
 *     .build();
 *
 * ThaiBahtConfig config2 = ThaiBahtConfig.builder(Language.ENGLISH)
 *     .setFormatTemplate(template)
 *     .build();
 * }</pre>
 *
 * @see io.github.zazalng.ThaiBahtConfig.Builder
 * @see io.github.zazalng.handler.FormatApplier
 * @author Zazalng
 * @since 1.4.0
 */
public final class FormatTemplate {
    private final String template;

    /**
     * Constructs a FormatTemplate from the given format string.
     * <p>
     * This private constructor creates an immutable template object and validates that the
     * format string contains all required named placeholders. The validation ensures that
     * both {@code {INTEGER}} and {@code {FLOAT}} placeholders are present.
     *
     * <p>
     * <strong>Validation checks:</strong>
     * <ul>
     *   <li>Format string must not be null or empty</li>
     *   <li>Format string must contain {@code {INTEGER}} placeholder</li>
     *   <li>Format string must contain {@code {FLOAT}} placeholder</li>
     * </ul>
     *
     * <p>
     * <strong>Validation examples:</strong>
     * <pre>{@code
     * // Valid - has both required placeholders
     * FormatTemplate.of("{INTEGER}{FLOAT}");
     * FormatTemplate.of("{INTEGER}{UNIT}{EXACT}{FLOAT?{FLOAT}{SATANG}}");
     *
     * // Invalid - missing {FLOAT}
     * FormatTemplate.of("{INTEGER}");  // Throws IllegalArgumentException
     *
     * // Invalid - missing {INTEGER}
     * FormatTemplate.of("{FLOAT}");    // Throws IllegalArgumentException
     *
     * // Invalid - empty string
     * FormatTemplate.of("");           // Throws IllegalArgumentException
     * }</pre>
     *
     * @param formatString the format string with named placeholders, must not be {@code null} or empty
     * @throws IllegalArgumentException if format string is null, empty, or missing required placeholders
     * @since 1.4.0
     */
    private FormatTemplate(String formatString) {
        if (formatString == null || formatString.isEmpty()) {
            throw new IllegalArgumentException("Format string must not be null or empty");
        }

        this.template = formatString;

        // Check for required named placeholders
        boolean hasIntegerPlaceholder = formatString.contains("{INTEGER}");
        boolean hasFloatPlaceholder = formatString.contains("{FLOAT}");

        // Validate that required placeholders exist
        if (!hasIntegerPlaceholder || !hasFloatPlaceholder) {
            throw new IllegalArgumentException(
                "Format string must contain {INTEGER} for baht and {FLOAT} for satang placeholders. " +
                "Example: \"{INTEGER}{UNIT}กับอีก{FLOAT}{SATANG}\" or \"{INTEGER} {UNIT} {FLOAT}/{SATANG}\""
            );
        }
    }

    /**
     * Creates a FormatTemplate from a format string.
     * <p>
     * This factory method constructs an immutable FormatTemplate that encapsulates the
     * given format string. The format string is validated to ensure it contains the required
     * placeholders before creating the template.
     *
     * <p>
     * <strong>Example usage:</strong>
     * <pre>{@code
     * // Create template with conditional satang
     * FormatTemplate template = FormatTemplate.of(
     *     "{INTEGER}{UNIT}{EXACT}{FLOAT?{FLOAT}{SATANG}}"
     * );
     *
     * // Use in configuration
     * ThaiBahtConfig config = ThaiBahtConfig.builder()
     *     .setFormatTemplate(template)
     *     .build();
     * }</pre>
     *
     * @param formatString the format string with named placeholders
     * @return a new immutable FormatTemplate with the given format string
     * @throws IllegalArgumentException if format string is invalid (null, empty, or missing required placeholders)
     * @since 1.4.0
     */
    public static FormatTemplate of(String formatString) {
        return new FormatTemplate(formatString);
    }

    /**
     * Returns the original format string with its named placeholders.
     * <p>
     * The returned string is the exact format string provided at construction time,
     * with all placeholders intact. This can be used for debugging, logging, or
     * for creating modified templates.
     *
     * @return the original format string, never {@code null} or empty
     * @since 1.4.0
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Returns a string representation of this FormatTemplate.
     * <p>
     * This method returns the template string itself, making it easy to use
     * FormatTemplate objects in string contexts (e.g., logging, debugging).
     *
     * @return the format string, never {@code null}
     */
    @Override
    public String toString() {
        return template;
    }
}

