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

/**
 * Utilities to convert numeric amounts into Thai-language baht text with multi-language support.
 * <p>
 * This package provides a comprehensive API for converting {@link java.math.BigDecimal} monetary
 * amounts into language-specific text describing baht (บาท) and satang (สตางค์). The conversion
 * respects linguistic conventions for digit naming and currency formatting in the selected language.
 * <p>
 * <strong>Version 1.3.0 Features:</strong>
 * <ul>
 *   <li><strong>Multi-Language Support:</strong> Output in Thai (default) or English, with easy
 *       extensibility for additional languages</li>
 *   <li><strong>Automatic Prefix Updates:</strong> When switching languages, the negative amount
 *       prefix automatically updates to the language's default (unless explicitly overridden)</li>
 *   <li><strong>Backward Compatible:</strong> Version 1.2.0 code works unchanged with all new
 *       features available for opt-in use</li>
 *   <li><strong>Thread-Safe Configuration:</strong> Immutable configuration objects designed for
 *       safe concurrent access</li>
 * </ul>
 *
 * <h2>Public API</h2>
 * <p>
 * The main entry points for users are:
 * <ul>
 *   <li>{@link io.github.zazalng.ThaiBaht} - Static utility methods and instance-based fluent API
 *       for converting amounts to language-specific text</li>
 *   <li>{@link io.github.zazalng.ThaiBahtConfig} - Immutable configuration for controlling output
 *       formatting, including language selection, unit word inclusion, and negative prefixes</li>
 *   <li>{@link io.github.zazalng.contracts.Language} - Enum defining supported languages (THAI, ENGLISH)</li>
 * </ul>
 * <p>
 * Internal implementation classes such as {@link io.github.zazalng.handler} are package-private
 * and not part of the public API surface.
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Thai Output (Default - Unchanged from v1.2.0)</h3>
 * <pre>
 *   import java.math.BigDecimal;
 *   import io.github.zazalng.ThaiBaht;
 *
 *   String text = ThaiBaht.of(new BigDecimal("1234.56"));
 *   // Returns: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
 *
 *   String negative = ThaiBaht.of(new BigDecimal("-100.00"));
 *   // Returns: "ลบหนึ่งร้อยบาทถ้วน"
 * </pre>
 *
 * <h3>English Output (New in v1.3.0 - Automatic Prefix)</h3>
 * <pre>
 *   import java.math.BigDecimal;
 *   import io.github.zazalng.ThaiBaht;
 *   import io.github.zazalng.ThaiBahtConfig;
 *   import io.github.zazalng.contracts.Language;
 *
 *   ThaiBahtConfig config = ThaiBahtConfig.builder()
 *       .language(Language.ENGLISH)
 *       .build();
 *   // Prefix automatically: "Minus"
 *
 *   String text = ThaiBaht.of(new BigDecimal("1234.56"), config);
 *   // Returns: "One Thousand Two Hundred Thirty-Four Baht Fifty-Six Satang"
 *
 *   String negative = ThaiBaht.of(new BigDecimal("-100.00"), config);
 *   // Returns: "Minus One Hundred Baht Only"
 * </pre>
 *
 * <h3>Instance-Based Fluent API</h3>
 * <pre>
 *   import io.github.zazalng.ThaiBaht;
 *   import io.github.zazalng.contracts.Language;
 *
 *   ThaiBaht converter = ThaiBaht.create(new BigDecimal("100.00"))
 *       .config(b -> b
 *           .language(Language.ENGLISH)
 *           .useUnit(true)
 *       );
 *   String text = converter.toString();
 *   // Returns: "One Hundred Baht Only"
 * </pre>
 *
 * <h3>Custom Configuration</h3>
 * <pre>
 *   ThaiBahtConfig config = ThaiBahtConfig.builder()
 *       .language(Language.ENGLISH)
 *       .useUnit(false)
 *       .setPrefix("negative")  // Custom prefix
 *       .build();
 *
 *   String text = ThaiBaht.of(new BigDecimal("-500.25"), config);
 *   // Returns: "negative Five Hundred Twenty-Five"
 * </pre>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Multi-Language Support:</strong> Thai (default) and English, extensible for more</li>
 *   <li><strong>Immutable Configuration:</strong> All configuration objects are immutable and thread-safe</li>
 *   <li><strong>Flexible API:</strong> Both static utility methods and fluent instance-based API</li>
 *   <li><strong>Customizable Output:</strong> Control language, whether unit words are included,
 *       and negative amount prefixes</li>
 *   <li><strong>Automatic Prefix Updates:</strong> Prefixes automatically update to language defaults
 *       unless explicitly set</li>
 *   <li><strong>Negative Amount Support:</strong> Negative amounts are prefixed with
 *       language-appropriate text</li>
 *   <li><strong>Precise Conversion:</strong> Uses {@link java.math.BigDecimal} for accurate
 *       monetary arithmetic</li>
 *   <li><strong>Linguistic Rules:</strong> Respects language-specific digit naming conventions</li>
 * </ul>
 *
 * <h2>Configuration</h2>
 * <p>
 * The {@link io.github.zazalng.ThaiBahtConfig} class provides fine-grained control over output:
 * <ul>
 *   <li><strong>Language:</strong> Choose output language (THAI or ENGLISH)</li>
 *   <li><strong>Unit Words:</strong> When enabled, includes language-specific currency unit words
 *       in the output (e.g., "บาท"/"สตางค์" for Thai, "Baht"/"Satang" for English)</li>
 *   <li><strong>Formal Mode:</strong> Reserved for future use with different wording conventions</li>
 *   <li><strong>Negative Prefix:</strong> Customizable prefix for negative amounts
 *       (default: "ลบ" for Thai, "Minus" for English)</li>
 * </ul>
 *
 * <h2>Version 1.3.0 - Prefix Auto-Update Behavior</h2>
 * <p>
 * Version 1.3.0 introduces smart prefix handling that automatically updates prefixes based on
 * the selected language:
 * <ul>
 *   <li><strong>Fresh Builder:</strong> Creates a new configuration with language-default prefix
 *       that will auto-update if language changes</li>
 *   <li><strong>Explicit Prefix:</strong> When you explicitly set a prefix, it becomes locked
 *       and won't change even if language changes</li>
 *   <li><strong>toBuilder():</strong> Converts existing config back to builder, treating current
 *       prefix as explicitly set (preserves original)</li>
 * </ul>
 *
 * <h3>Example: Auto-Update Behavior</h3>
 * <pre>
 *   // Fresh builder: prefix can auto-update
 *   ThaiBahtConfig config = ThaiBahtConfig.builder()
 *       .language(Language.ENGLISH)
 *       .build();
 *   // Prefix automatically: "Minus" (English default)
 *
 *   // Explicit prefix: locked in place
 *   ThaiBahtConfig custom = ThaiBahtConfig.builder()
 *       .setPrefix("custom")
 *       .language(Language.ENGLISH)
 *       .build();
 *   // Prefix: "custom" (preserved)
 * </pre>
 *
 * <h2>Backward Compatibility</h2>
 * <p>
 * Version 1.3.0 is 100% backward compatible with version 1.2.0. Existing code continues to work
 * unchanged:
 * <ul>
 *   <li>Default behavior is Thai output with "ลบ" prefix (unchanged)</li>
 *   <li>All v1.2.0 method signatures still work</li>
 *   <li>New methods and features are opt-in</li>
 * </ul>
 * <pre>
 *   // v1.2.0 code still works perfectly
 *   String thai = ThaiBaht.of(new BigDecimal("100.00"));
 *   // Returns: "หนึ่งร้อยบาทถ้วน" (unchanged)
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * All configuration objects ({@link io.github.zazalng.ThaiBahtConfig}) are immutable and thread-safe.
 * The conversion process is stateless and does not maintain any mutable state between calls.
 * Configurations can be safely shared across multiple threads without synchronization.
 *
 * <h2>Sub-Packages</h2>
 * <ul>
 *   <li>{@link io.github.zazalng.contracts} - Contract types and language definitions</li>
 *   <li>{@link io.github.zazalng.handler} - Internal conversion implementation (package-private)</li>
 * </ul>
 *
 * <h2>Getting Started</h2>
 * <p>
 * For most use cases, you only need:
 * <ol>
 *   <li>Import {@link io.github.zazalng.ThaiBaht} for conversion</li>
 *   <li>Optionally import {@link io.github.zazalng.ThaiBahtConfig} for custom configuration</li>
 *   <li>Optionally import {@link io.github.zazalng.contracts.Language} to specify language</li>
 *   <li>Call {@link io.github.zazalng.ThaiBaht#of(java.math.BigDecimal)} for Thai output</li>
 *   <li>Or use {@link io.github.zazalng.ThaiBaht#of(java.math.BigDecimal, io.github.zazalng.ThaiBahtConfig)}
 *       for custom output</li>
 * </ol>
 *
 * @see io.github.zazalng.ThaiBaht
 * @see io.github.zazalng.ThaiBahtConfig
 * @see io.github.zazalng.contracts.Language
 * @since 1.0
 * @version 1.3.0
 * @author Zazalng
 */
package io.github.zazalng;

