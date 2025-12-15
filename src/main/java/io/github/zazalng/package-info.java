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
 * Utilities to convert numeric amounts into Thai-language baht text.
 * <p>
 * This package provides a comprehensive API for converting {@link java.math.BigDecimal} monetary
 * amounts into Thai-language text describing baht (บาท) and satang (สตางค์). The conversion
 * respects Thai linguistic conventions for digit naming and currency formatting.
 *
 * <h2>Public API</h2>
 * <p>
 * The main entry points for users are:
 * <ul>
 *   <li>{@link io.github.zazalng.ThaiBaht} - Static utility methods and instance-based fluent API
 *       for converting amounts to Thai text</li>
 *   <li>{@link io.github.zazalng.ThaiBahtConfig} - Configuration for controlling output formatting,
 *       including unit word inclusion and negative prefixes</li>
 * </ul>
 * <p>
 * Internal implementation classes such as {@code ThaiTextConverter} are package-private
 * and not part of the public API surface.
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Static Conversion with Default Configuration</h3>
 * <pre>
 *   import java.math.BigDecimal;
 *   import io.github.zazalng.ThaiBaht;
 *
 *   String text = ThaiBaht.of(new BigDecimal("1234.56"));
 *   // Returns: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
 * </pre>
 *
 * <h3>Instance-Based Fluent API</h3>
 * <pre>
 *   ThaiBaht converter = ThaiBaht.create(new BigDecimal("100.00"))
 *       .config(b -> b.useUnit(true));
 *   String text = converter.toString();
 *   // Returns: "หนึ่งร้อยบาทถ้วน"
 * </pre>
 *
 * <h3>Custom Configuration</h3>
 * <pre>
 *   ThaiBahtConfig config = ThaiBahtConfig.builder()
 *       .useUnit(false)
 *       .setPrefix("ลบ")
 *       .formal(true)
 *       .build();
 *   String text = ThaiBaht.of(new BigDecimal("500.25"), config);
 *   // Returns: "ห้าร้อยยี่สิบห้า"
 * </pre>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Immutable Configuration:</strong> All configuration objects are immutable and thread-safe</li>
 *   <li><strong>Flexible API:</strong> Both static utility methods and fluent instance-based API</li>
 *   <li><strong>Customizable Output:</strong> Control whether unit words are included in the output</li>
 *   <li><strong>Negative Amount Support:</strong> Negative amounts are prefixed with configurable Thai text</li>
 *   <li><strong>Precise Conversion:</strong> Uses {@link java.math.BigDecimal} for accurate monetary arithmetic</li>
 *   <li><strong>Thai Linguistic Rules:</strong> Respects special Thai digit naming conventions</li>
 * </ul>
 *
 * <h2>Configuration</h2>
 * <p>
 * The {@link io.github.zazalng.ThaiBahtConfig} class provides fine-grained control over output:
 * <ul>
 *   <li><strong>Unit Words:</strong> When enabled, includes "บาท" (baht) and "สตางค์" (satang)
 *       or "ถ้วน" (whole) in the output</li>
 *   <li><strong>Formal Mode:</strong> Reserved for future use with different Thai wording conventions</li>
 *   <li><strong>Negative Prefix:</strong> Customizable prefix for negative amounts (default: "ลบ")</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * All configuration objects ({@link io.github.zazalng.ThaiBahtConfig}) are immutable and thread-safe.
 * The conversion process is stateless and does not maintain any mutable state between calls.
 *
 * @since 1.0
 * @author Zazalng
 */
package io.github.zazalng;

