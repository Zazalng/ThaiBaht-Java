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
 * Contract types and language definitions for baht text conversion.
 * <p>
 * This package contains the core contract interfaces and enumerations that define
 * the behavior and capabilities of the baht conversion system.
 *
 * <h2>Key Types in v2.0.0</h2>
 * <p>
 * The main types in this package are:
 * <ul>
 *   <li><strong>{@link io.github.zazalng.contracts.LanguageHandler} (NEW)</strong> - Interface for implementing
 *       language-specific conversion logic. Implement this to add ANY language without core modifications.</li>
 *   <li><strong>{@link io.github.zazalng.contracts.Language}</strong> - Enum for built-in languages (THAI, ENGLISH).
 *       Kept for backward compatibility; use LanguageHandler for new code.</li>
 * </ul>
 *
 * <h2>LanguageHandler Interface (v2.0.0 NEW)</h2>
 * <p>
 * The {@link io.github.zazalng.contracts.LanguageHandler} interface is the new extensibility mechanism:
 * <ul>
 *   <li><strong>Method: convert(ThaiBaht)</strong> - Performs the actual conversion to language-specific text</li>
 *   <li><strong>Method: getLanguageCode()</strong> - Returns ISO 639 language code (e.g., "th", "en")</li>
 *   <li><strong>Method: getLanguageName()</strong> - Returns human-readable language name</li>
 *   <li><strong>Method: getUnitWord()</strong> - Returns currency unit (บาท, Baht, ກີບ, etc.)</li>
 *   <li><strong>Method: getExactWord()</strong> - Returns exact indicator (ถ้วน, Only, ເທົ່າ, etc.)</li>
 *   <li><strong>Method: getSatangWord()</strong> - Returns satang unit (สตางค์, Satang, ແອັດ, etc.)</li>
 *   <li><strong>Method: getNegativePrefix()</strong> - Returns negative prefix (ลบ, Minus, ລົບ, etc.)</li>
 * </ul>
 *
 * <h3>Creating Custom Languages</h3>
 * <p>
 * Implement the interface to create custom language handlers:
 * <pre>{@code
 *   public class YourLanguageHandler implements LanguageHandler {
 *       @Override
 *       public String convert(ThaiBaht baht) {
 *           // Your conversion logic here
 *       }
 *
 *       @Override public String getLanguageCode() { return "xx"; }
 *       @Override public String getLanguageName() { return "Your Language"; }
 *       @Override public String getUnitWord() { return "unit"; }
 *       @Override public String getExactWord() { return "exact"; }
 *       @Override public String getSatangWord() { return "subunit"; }
 *       @Override public String getNegativePrefix() { return "neg"; }
 *   }
 * }</pre>
 *
 * <h2>Language Enum (Backward Compatibility)</h2>
 * <p>
 * The {@link io.github.zazalng.contracts.Language} enum defines built-in languages:
 * <ul>
 *   <li><strong>{@code THAI}</strong> - Thai language output with default prefix "ลบ" (Thai minus)</li>
 *   <li><strong>{@code ENGLISH}</strong> - English language output with default prefix "Minus"</li>
 * </ul>
 *
 * <h2>Default Prefixes by Language</h2>
 * <p>
 * Each built-in language has a default prefix for negative amounts:
 * <table border="1">
 *   <tr>
 *     <th>Language</th>
 *     <th>Default Prefix</th>
 *     <th>Example Output</th>
 *   </tr>
 *   <tr>
 *     <td>{@code THAI}</td>
 *     <td>"ลบ"</td>
 *     <td>"ลบหนึ่งร้อยบาท"</td>
 *   </tr>
 *   <tr>
 *     <td>{@code ENGLISH}</td>
 *     <td>"Minus"</td>
 *     <td>"Minus One Hundred Baht"</td>
 *   </tr>
 * </table>
 *
 * <h2>v2.0.0 Architecture Change</h2>
 * <p>
 * Version 2.0.0 shifts from enum-based language selection to interface-based handlers:
 * <ul>
 *   <li><strong>Before (v1.4.0):</strong> Languages locked in Language enum → Limited extensibility</li>
 *   <li><strong>After (v2.0.0):</strong> Languages implement LanguageHandler interface → Unlimited extensibility</li>
 *   <li><strong>Benefit:</strong> Add new languages without modifying core library code</li>
 *   <li><strong>Backward Compat:</strong> Language enum still supported for v1.4.0 code</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Using Built-in Handler (v2.0.0 Recommended)</h3>
 * <pre>{@code
 *   import io.github.zazalng.ThaiBahtConfig;
 *   import io.github.zazalng.handler.ThaiLanguageHandler;
 *
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler())
 *       .build();
 * }</pre>
 *
 * <h3>Using Custom Handler (v2.0.0 NEW)</h3>
 * <pre>{@code
 *   import io.github.zazalng.ThaiBahtConfig;
 *   import io.github.zazalng.contracts.LanguageHandler;
 *
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(new LaotianLanguageHandler())
 *       .build();
 * }</pre>
 *
 * <h3>Using Language Enum (v1.4.0 Backward Compat)</h3>
 * <pre>{@code
 *   import io.github.zazalng.ThaiBahtConfig;
 *   import io.github.zazalng.contracts.Language;
 *
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(Language.THAI)
 *       .build();
 *   // Internally creates ThaiLanguageHandler
 * }</pre>
 *
 * <h2>Migration from v1.4.0 to v2.0.0</h2>
 * <p>
 * No changes required - existing code continues to work. To adopt v2.0.0 syntax:
 * <pre>{@code
 *   // Old (still works)
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(Language.THAI).build();
 *
 *   // New (recommended)
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler()).build();
 * }</pre>
 *
 * @see io.github.zazalng.contracts.LanguageHandler
 * @see io.github.zazalng.contracts.Language
 * @see io.github.zazalng.ThaiBahtConfig
 * @author Zazalng
 * @since 1.3.0
 * @version 2.0.0
 */
package io.github.zazalng.contracts;

