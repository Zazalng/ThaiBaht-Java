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
 * <strong>Version 2.0.0 Features (NEW):</strong>
 * <ul>
 *   <li><strong>Pluggable Language Handlers:</strong> Implement {@link io.github.zazalng.contracts.LanguageHandler}
 *       to add ANY language without modifying core code</li>
 *   <li><strong>Zero Enum Coupling:</strong> Languages no longer locked to fixed enum - unlimited extensibility</li>
 *   <li><strong>Built-in Handlers:</strong> {@link io.github.zazalng.handler.ThaiLanguageHandler} and
 *       {@link io.github.zazalng.handler.EnglishLanguageHandler} provided</li>
 *   <li><strong>Backward Compatible:</strong> v1.4.0 code works unchanged via soft compatibility builders</li>
 *   <li><strong>Thread-Safe Configuration:</strong> Immutable config objects for safe concurrent access</li>
 * </ul>
 *
 * <h2>Public API</h2>
 * <p>
 * The main entry points for users are:
 * <ul>
 *   <li>{@link io.github.zazalng.ThaiBaht} - Static utility methods and instance-based fluent API
 *       for converting amounts to language-specific text</li>
 *   <li>{@link io.github.zazalng.ThaiBahtConfig} - Immutable configuration for controlling output
 *       formatting, including language handler selection, unit word inclusion, and negative prefixes</li>
 *   <li>{@link io.github.zazalng.contracts.LanguageHandler} - Interface for implementing custom language handlers</li>
 *   <li>{@link io.github.zazalng.contracts.Language} - Enum defining built-in languages (THAI, ENGLISH) - backward compat only</li>
 * </ul>
 * <p>
 * Internal implementation classes such as {@link io.github.zazalng.handler} are package-private
 * and not part of the public API surface.
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Thai Output (Default - Using Handler)</h3>
 * <pre>{@code
 *   import java.math.BigDecimal;
 *   import io.github.zazalng.ThaiBaht;
 *   import io.github.zazalng.handler.ThaiLanguageHandler;
 *
 *   // v2.0.0 recommended approach
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler())
 *       .useUnit(true)
 *       .build();
 *   String text = ThaiBaht.of(new BigDecimal("1234.56"), config);
 *   // Returns: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
 *
 *   // Or use convenience one-liner (backward compatible)
 *   String text = ThaiBaht.of(new BigDecimal("1234.56"));
 *   // Returns: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
 * }</pre>
 *
 * <h3>English Output (Using Handler)</h3>
 * <pre>{@code
 *   import java.math.BigDecimal;
 *   import io.github.zazalng.ThaiBaht;
 *   import io.github.zazalng.handler.EnglishLanguageHandler;
 *
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(new EnglishLanguageHandler())
 *       .useUnit(true)
 *       .build();
 *   String text = ThaiBaht.of(new BigDecimal("1234.56"), config);
 *   // Returns: "One Thousand Two Hundred Thirty-Four Baht Fifty-Six Satang"
 * }</pre>
 *
 * <h3>Custom Language Handler (v2.0.0 NEW)</h3>
 * <pre>{@code
 *   import io.github.zazalng.contracts.LanguageHandler;
 *
 *   public class LaotianLanguageHandler implements LanguageHandler {
 *       @Override public String convert(ThaiBaht baht) { return ...// conversion logic }
 *       @Override public String getLanguageCode() { return "lo"; }
 *       @Override public String getLanguageName() { return "Laotian"; }
 *       @Override public String getUnitWord() { return "ກີບ"; }
 *       @Override public String getExactWord() { return "ເທົ່າ"; }
 *       @Override public String getSatangWord() { return "ແອັດ"; }
 *       @Override public String getNegativePrefix() { return "ລົບ"; }
 *   }
 *
 *   // Use immediately - no core library changes!
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(new LaotianLanguageHandler())
 *       .useUnit(true)
 *       .build();
 *   String text = ThaiBaht.of(new BigDecimal("1234.56"), config);
 * }</pre>
 *
 * <h3>Backward Compatibility (v1.4.0 Code Still Works)</h3>
 * <pre>{@code
 *   import io.github.zazalng.contracts.Language;
 *
 *   // v1.4.0 style - still works in v2.0.0
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(Language.ENGLISH)
 *       .useUnit(true)
 *       .build();
 *   // Internally creates EnglishLanguageHandler
 * }</pre>
 *
 * <h3>Instance-Based Fluent API</h3>
 * <pre>{@code
 *   import io.github.zazalng.ThaiBaht;
 *   import io.github.zazalng.handler.ThaiLanguageHandler;
 *
 *   ThaiBaht converter = ThaiBaht.create(new BigDecimal("100.00"))
 *       .config(b -> b
 *           .languageHandler(new ThaiLanguageHandler())
 *           .useUnit(true)
 *       );
 *   String text = converter.toString();
 *   // Returns: "หนึ่งร้อยบาทถ้วน"
 * }</pre>
 *
 * <h3>Custom Configuration</h3>
 * <pre>{@code
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler())
 *       .useUnit(true)
 *       .setPrefix("ติดลบ")  // Custom negative prefix
 *       .build();
 *   String negative = ThaiBaht.of(new BigDecimal("-100.50"), config);
 *   // Returns: "ติดลบหนึ่งร้อยบาทห้าสิบสตางค์"
 * }</pre>
 *
 * <h2>v2.0.0 Architecture Highlights</h2>
 * <p>
 * Version 2.0.0 introduces a breakthrough design:
 * Before v2.0.0, languages were locked in the Language enum (Limited extensibility).
 * After v2.0.0, languages implement LanguageHandler interface (Unlimited extensibility).
 * Benefit: Add any language without modifying core code.
 * Compatibility: Soft backward compatibility via builders - v1.4.0 code still works.
 *
 * <h2>Migration from v1.4.0 to v2.0.0</h2>
 * <p>
 * <strong>No changes required!</strong> Existing code continues to work. To adopt v2.0.0 syntax:
 * <pre>{@code
 *   // Old (still works)
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(Language.THAI).build();
 *
 *   // New (recommended)
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler()).build();
 * }</pre>
 *
 * @see io.github.zazalng.ThaiBaht
 * @see io.github.zazalng.ThaiBahtConfig
 * @see io.github.zazalng.contracts.LanguageHandler
 * @see io.github.zazalng.contracts.Language
 * @author Zazalng
 * @since 1.0
 * @version 2.0.0
 */
package io.github.zazalng;

