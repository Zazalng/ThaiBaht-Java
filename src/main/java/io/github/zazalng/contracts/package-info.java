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
 * the behavior and capabilities of the baht conversion system. The primary type in
 * this package is the {@link io.github.zazalng.contracts.Language} enum, which
 * specifies available output languages and their default configurations.
 *
 * <h2>Language Support</h2>
 * <p>
 * The {@link io.github.zazalng.contracts.Language} enum defines all supported
 * output languages for currency text conversion:
 * <ul>
 *   <li><strong>{@code THAI}</strong> - Thai language output with default prefix "ลบ" (Thai minus)</li>
 *   <li><strong>{@code ENGLISH}</strong> - English language output with default prefix "Minus"</li>
 * </ul>
 *
 * <h2>Default Prefixes by Language</h2>
 * <p>
 * Each language has a language-specific default prefix for negative amounts:
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
 *     <td>"Minus one hundred baht exact"</td>
 *   </tr>
 *
 * <h2>Version 1.3.0 Features</h2>
 * <ul>
 *   <li><strong>Multi-Language Support:</strong> Choose between THAI (default) and ENGLISH output</li>
 *   <li><strong>Language-Aware Defaults:</strong> Each language provides its own default prefix</li>
 *   <li><strong>Automatic Prefix Updates:</strong> When language is changed, prefix automatically
 *       updates to the language's default (unless explicitly overridden)</li>
 *   <li><strong>Custom Prefix Support:</strong> Explicitly set prefixes are preserved across
 *       language changes</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Using with Configuration</h3>
 * <pre>
 *   import io.github.zazalng.ThaiBahtConfig;
 *   import io.github.zazalng.contracts.Language;
 *
 *   // English with automatic default prefix
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(Language.ENGLISH)
 *       .build();
 *   // Prefix automatically: "Minus"
 *
 *   // Thai with automatic default prefix
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(Language.THAI)
 *       .build();
 *   // Prefix automatically: "ลบ"
 * </pre>
 *
 * <h3>Switching Languages with Auto-Update</h3>
 * <pre>
 *   // Fresh builder for auto-update behavior
 *   ThaiBahtConfig config = ThaiBahtConfig.builder(Language.THAI)
 *       .build();
 *   // Prefix: "ลบ"
 *
 *   // Switch to English - prefix auto-updates
 *   ThaiBahtConfig english = ThaiBahtConfig.builder(Language.ENGLISH)
 *       .build();
 *   // Prefix automatically: "Minus"
 * </pre>
 *
 * <h2>Prefix Behavior</h2>
 * <p>
 * The prefix behavior is intelligent and context-aware:
 * <ul>
 *   <li><strong>Fresh Builder:</strong> When creating a new builder with a language,
 *       the prefix is set to the language's default and can auto-update if language changes</li>
 *   <li><strong>Explicit Prefix:</strong> When you explicitly set a prefix via
 *       {@link io.github.zazalng.ThaiBahtConfig.Builder#setPrefix(String)},
 *       it becomes locked and won't change even if language changes</li>
 *   <li><strong>toBuilder():</strong> When converting back to builder, the current prefix
 *       is treated as explicitly set (to preserve the original configuration)</li>
 * </ul>
 *
 * <h2>Extensibility</h2>
 * <p>
 * Adding a new language is straightforward and follows these steps:
 * <ol>
 *   <li>Add a new enum constant to {@link io.github.zazalng.contracts.Language}</li>
 *   <li>Implement language-specific conversion methods in the internal converter</li>
 *   <li>Add tests for the new language</li>
 *   <li>Update documentation with language-specific rules and examples</li>
 * </ol>
 * <p>
 * The contract is minimal and focused - each language just needs a default prefix
 * and conversion logic. The framework handles the rest automatically.
 *
 * <h2>Thread Safety</h2>
 * <p>
 * The {@link io.github.zazalng.contracts.Language} enum is thread-safe by design
 * (as all Java enums are). Language instances can be safely shared and used
 * across multiple threads without synchronization.
 *
 * @see io.github.zazalng.contracts.Language
 * @see io.github.zazalng.ThaiBahtConfig
 * @see io.github.zazalng.ThaiBaht
 * @since 1.3.0
 * @author Zazalng
 */
package io.github.zazalng.contracts;