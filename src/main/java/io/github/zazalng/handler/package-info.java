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
 * Internal conversion handlers for transforming numeric amounts into language-specific text.
 * <p>
 * <strong>This package is internal (package-private) and not part of the public API.</strong>
 * Users should interact with this functionality only through the public API in
 * {@link io.github.zazalng} and {@link io.github.zazalng.contracts} packages.
 *
 * <h2>Package Purpose</h2>
 * <p>
 * This package contains the internal implementation of the text conversion algorithm that
 * transforms {@link java.math.BigDecimal} monetary amounts into language-specific textual
 * representations. It handles:
 * <ul>
 *   <li>Amount normalization (precision handling)</li>
 *   <li>Language-specific digit and word conversion</li>
 *   <li>Negative amount prefix handling</li>
 *   <li>Unit word inclusion/exclusion</li>
 *   <li>Multi-language routing and dispatch</li>
 * </ul>
 *
 * <h2>Implementation Classes</h2>
 * <p>
 * The main implementation class in this package is {@code TextConverter} (or similar),
 * which provides:
 * <ul>
 *   <li><strong>Language-Agnostic Routing:</strong> Routes conversions to appropriate
 *       language-specific handlers based on configuration</li>
 *   <li><strong>Precision Handling:</strong> Normalizes amounts to 2 decimal places
 *       using {@link java.math.RoundingMode#DOWN}</li>
 *   <li><strong>Negative Amount Support:</strong> Extracts sign and applies configured prefix</li>
 *   <li><strong>Language-Specific Conversion:</strong> Handles Thai and English conversion
 *       with language-appropriate linguistic rules</li>
 * </ul>
 *
 * <h2>Conversion Algorithm (Overview)</h2>
 * <p>
 * The conversion process follows these high-level steps:
 * <ol>
 *   <li>Validate input (amount not null)</li>
 *   <li>Normalize amount to 2 decimal places (satang precision)</li>
 *   <li>Extract absolute value for conversion</li>
 *   <li>Route to language-specific converter based on config</li>
 *   <li>Convert integer part (baht) to words</li>
 *   <li>Add unit words if enabled</li>
 *   <li>Convert fractional part (satang) to words</li>
 *   <li>Add fractional unit words if enabled</li>
 *   <li>Apply negative prefix if amount is negative</li>
 *   <li>Return formatted text</li>
 * </ol>
 *
 * <h2>Time Complexity</h2>
 * <p>
 * The conversion algorithm is O(log n) where n is the magnitude of the amount.
 * This is because:
 * <ul>
 *   <li>Processing is linear in the number of digits</li>
 *   <li>Number of digits grows logarithmically with magnitude</li>
 *   <li>No nested loops or recursive calls with exponential growth</li>
 * </ul>
 *
 * <h2>Space Complexity</h2>
 * <p>
 * Space usage is O(log n) where n is the magnitude of the amount, limited by:
 * <ul>
 *   <li>Output string length (proportional to number of digits)</li>
 *   <li>No allocation of large intermediate structures</li>
 *   <li>All processing done with single StringBuffer</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * The conversion handlers are stateless and thread-safe:
 * <ul>
 *   <li>No mutable instance state maintained between calls</li>
 *   <li>All state is local to method invocations</li>
 *   <li>Configuration objects are immutable (passed in)</li>
 *   <li>Safe for concurrent use without synchronization</li>
 * </ul>
 *
 * <h2>Language-Specific Implementation</h2>
 * <p>
 * For each supported language, there are dedicated conversion methods:
 * <ul>
 *   <li><strong>Thai:</strong> Implements Thai linguistic rules for digit naming
 *       (e.g., "หนึ่ง" becomes "เอ็ด", "สอง" becomes "ยี่" in specific contexts)</li>
 *   <li><strong>English:</strong> Implements English linguistic rules with standard
 *       number naming conventions (e.g., "twenty-one" for 21)</li>
 * </ul>
 * <p>
 * Each language handler has its own:
 * <ul>
 *   <li>Digit arrays/maps</li>
 *   <li>Integer conversion logic</li>
 *   <li>Fractional (satang) conversion logic</li>
 *   <li>Unit word definitions</li>
 * </ul>
 *
 * <h2>Adding Support for New Languages</h2>
 * <p>
 * To add a new language, follow these steps:
 * <ol>
 *   <li>Add enum constant to {@link io.github.zazalng.contracts.Language}
 *       with appropriate default prefix</li>
 *   <li>Add language-specific digit arrays/maps</li>
 *   <li>Implement {@code convertTo[Language]()} method following existing pattern</li>
 *   <li>Implement helper methods for integer and fractional conversion</li>
 *   <li>Update routing logic in main converter to dispatch to new language handler</li>
 *   <li>Add comprehensive tests for the new language</li>
 *   <li>Update documentation with language-specific examples</li>
 * </ol>
 *
 * <h2>Error Handling</h2>
 * <p>
 * The conversion handlers:
 * <ul>
 *   <li>Validate null inputs and throw {@link java.lang.NullPointerException}</li>
 *   <li>Handle zero amounts gracefully</li>
 *   <li>Support arbitrarily large amounts</li>
 *   <li>Never throw checked exceptions</li>
 * </ul>
 *
 * <h2>Version History</h2>
 * <ul>
 *   <li><strong>v1.0.0:</strong> Initial Thai conversion implementation</li>
 *   <li><strong>v1.3.0:</strong> Multi-language framework introduced, Smart prefix tracking, maintain backward compatibility</li>
 * </ul>
 *
 * <h2>Design Notes</h2>
 * <p>
 * The internal implementation follows these design principles:
 * <ul>
 *   <li><strong>Separation of Concerns:</strong> Conversion logic separated by language</li>
 *   <li><strong>Stateless Design:</strong> No mutable state between invocations</li>
 *   <li><strong>Immutable Configuration:</strong> Configuration passed as immutable parameter</li>
 *   <li><strong>Efficient String Building:</strong> Uses StringBuilder for efficient concatenation</li>
 *   <li><strong>Language Extensibility:</strong> Easy to add new languages without modifying existing code</li>
 * </ul>
 *
 * <h2>Not For Direct Use</h2>
 * <p>
 * This package is internal implementation. Always use the public API:
 * <ul>
 *   <li>{@link io.github.zazalng.ThaiBaht} for conversions</li>
 *   <li>{@link io.github.zazalng.ThaiBahtConfig} for configuration</li>
 *   <li>{@link io.github.zazalng.contracts.Language} for language selection</li>
 * </ul>
 *
 * @see io.github.zazalng.ThaiBaht
 * @see io.github.zazalng.ThaiBahtConfig
 * @see io.github.zazalng.contracts.Language
 * @since 1.0
 * @version 1.3.0
 * @author Zazalng
 */
package io.github.zazalng.handler;