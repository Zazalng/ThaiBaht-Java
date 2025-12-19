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
 * Internal utility classes and helper functions for the ThaiBaht conversion library.
 * <p>
 * This package contains non-public utility implementations that support the core conversion
 * functionality. These classes are internal to the library and not part of the public API.
 *
 * <h2>Purpose</h2>
 * <p>
 * The {@code io.github.zazalng.utils} package provides:
 * <ul>
 *   <li><strong>Number Conversion Utilities:</strong> Helper methods for converting numeric
 *       values and performing calculations</li>
 *   <li><strong>String Processing:</strong> Common string manipulation and formatting operations</li>
 *   <li><strong>Validation Helpers:</strong> Input validation and constraint checking</li>
 *   <li><strong>Data Transformation:</strong> Internal data structure conversions</li>
 * </ul>
 *
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><strong>Package-Private:</strong> All classes in this package are package-private and
 *       not exposed in the public API</li>
 *   <li><strong>Supporting Role:</strong> Classes exist solely to support public API classes
 *       like {@link io.github.zazalng.ThaiBaht} and {@link io.github.zazalng.ThaiBahtConfig}</li>
 *   <li><strong>No External Dependencies:</strong> Utilities use only standard Java libraries</li>
 *   <li><strong>Thread-Safe:</strong> All utility methods are stateless and thread-safe</li>
 *   <li><strong>Reusability:</strong> Common operations are extracted to avoid duplication</li>
 * </ul>
 *
 * <h2>Internal Implementation Details</h2>
 * <p>
 * While these utilities support the public API, they are subject to change without notice
 * between versions. External code should not depend on classes in this package:
 * <ul>
 *   <li>No backward compatibility guarantees for internal implementations</li>
 *   <li>Method signatures may change between minor versions</li>
 *   <li>New utilities may be added or removed as needed</li>
 *   <li>Use the public API ({@link io.github.zazalng.ThaiBaht}, {@link io.github.zazalng.ThaiBahtConfig})
 *       for stable, supported functionality</li>
 * </ul>
 *
 * <h2>Common Utility Categories</h2>
 *
 * <h3>Number and Monetary Calculations</h3>
 * <p>
 * Internal utilities handle:
 * <ul>
 *   <li>Breaking down BigDecimal amounts into baht and satang components</li>
 *   <li>Validating monetary values (non-negative, precision constraints)</li>
 *   <li>Rounding and formatting currency amounts</li>
 *   <li>Handling edge cases (zero, maximum values, etc.)</li>
 * </ul>
 *
 * <h3>Text Processing</h3>
 * <p>
 * Internal utilities provide:
 * <ul>
 *   <li>String concatenation and formatting</li>
 *   <li>Language-specific text transformations</li>
 *   <li>Prefix and suffix management</li>
 *   <li>Character case handling</li>
 * </ul>
 *
 * <h3>Configuration and Constants</h3>
 * <p>
 * Internal utilities manage:
 * <ul>
 *   <li>Language-specific constants and lookup tables</li>
 *   <li>Default configuration values</li>
 *   <li>Validation rules and constraints</li>
 *   <li>Supported language definitions</li>
 * </ul>
 *
 * <h2>Usage Notes for Maintainers</h2>
 * <p>
 * When extending the ThaiBaht library:
 * <ul>
 *   <li>Add new internal utilities to this package rather than the public package</li>
 *   <li>Keep utilities focused and single-purpose</li>
 *   <li>Document assumptions about input validity</li>
 *   <li>Ensure all utilities are stateless and thread-safe</li>
 *   <li>Write comprehensive unit tests for utility methods</li>
 *   <li>Avoid circular dependencies between utility classes</li>
 * </ul>
 *
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link io.github.zazalng} - Main public API package</li>
 *   <li>{@link io.github.zazalng.contracts} - Public contract interfaces and enums</li>
 *   <li>{@link io.github.zazalng.handler} - Conversion logic and algorithm implementations</li>
 * </ul>
 *
 * <h2>See Also</h2>
 * <p>
 * External code should not import from this package. Use the public API instead:
 * <ul>
 *   <li>{@link io.github.zazalng.ThaiBaht} - Primary entry point for conversions</li>
 *   <li>{@link io.github.zazalng.ThaiBahtConfig} - Configuration and customization</li>
 * </ul>
 *
 * @since 1.0
 * @version 1.3.0
 * @author Zazalng
 */
package io.github.zazalng.utils;