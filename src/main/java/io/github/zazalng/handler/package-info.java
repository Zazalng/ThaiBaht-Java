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
 * <h2>v2.0.0 Architecture (NEW)</h2>
 * <p>
 * Version 2.0.0 introduces a handler-based architecture:
 * <ul>
 *   <li><strong>LanguageHandler Interface:</strong> Public interface in contracts package for language implementations</li>
 *   <li><strong>Built-in Handlers:</strong> ThaiLanguageHandler and EnglishLanguageHandler
 *       implement the interface with full conversion logic</li>
 *   <li><strong>TextConverter Router:</strong> Routes conversion requests to the appropriate handler
 *       (delegating responsibility to handlers instead of routing on enum)</li>
 *   <li><strong>FormatApplier:</strong> Applies custom format templates (works with handlers)</li>
 * </ul>
 *
 * <h2>Package Contents</h2>
 * <p>
 * This package contains the internal implementation of the text conversion algorithm:
 * <ul>
 *   <li><strong>TextConverter</strong> - Main routing dispatcher (simplified in v2.0.0)</li>
 *   <li><strong>ThaiLanguageHandler</strong> (v2.0.0) - Thai conversion implementation</li>
 *   <li><strong>EnglishLanguageHandler</strong> (v2.0.0) - English conversion implementation</li>
 *   <li><strong>ThaiConvertHandler</strong> (legacy) - Original Thai logic (kept for reference)</li>
 *   <li><strong>EnglishConvertHandler</strong> (legacy) - Original English logic (kept for reference)</li>
 *   <li><strong>FormatApplier</strong> - Custom format template processor</li>
 * </ul>
 *
 * <h2>Handler Responsibility (v2.0.0)</h2>
 * <p>
 * In v2.0.0, language handlers take full responsibility for:
 * <ul>
 *   <li>Amount normalization (precision handling to 2 decimal places)</li>
 *   <li>Language-specific digit and word conversion</li>
 *   <li>Negative amount prefix handling</li>
 *   <li>Unit word inclusion/exclusion based on config</li>
 *   <li>Format template application (if custom format is configured)</li>
 *   <li>Returning properly formatted text</li>
 * </ul>
 *
 * <h2>Conversion Process (v2.0.0)</h2>
 * <p>
 * The simplified v2.0.0 flow:
 * <ol>
 *   <li>TextConverter.toBahtText() receives ThaiBaht instance</li>
 *   <li>Validates input (amount not null)</li>
 *   <li>Delegates to config.getLanguageHandler().convert()</li>
 *   <li>Handler performs complete conversion</li>
 *   <li>Returns formatted text</li>
 * </ol>
 *
 * <h2>Before vs After Architecture</h2>
 * <p>
 * <strong>v1.4.0 (Old):</strong>
 * <pre>
 * TextConverter (switch on Language enum)
 *   ├─ ThaiConvertHandler (static methods)
 *   └─ EnglishConvertHandler (static methods)
 * </pre>
 * <p>
 * <strong>v2.0.0 (New):</strong>
 * <pre>
 * TextConverter (delegates to handler)
 *   └─ LanguageHandler (polymorphic dispatch)
 *       ├─ ThaiLanguageHandler (implements interface)
 *       ├─ EnglishLanguageHandler (implements interface)
 *       └─ CustomLanguageHandler (user-implemented, no core changes!)
 * </pre>
 *
 * <h2>Performance Characteristics</h2>
 * <p>
 * Conversion algorithm maintains the same complexity as v1.4.0:
 * <ul>
 *   <li><strong>Time Complexity:</strong> O(log n) where n is the magnitude</li>
 *   <li><strong>Space Complexity:</strong> O(log n) for output string length</li>
 *   <li><strong>No Performance Regression:</strong> Handler polymorphism has minimal overhead</li>
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
 * <h2>Creating Custom Handlers</h2>
 * <p>
 * Users can create custom language handlers by implementing {@link io.github.zazalng.contracts.LanguageHandler}:
 * <pre>{@code
 *   public class MyLanguageHandler implements LanguageHandler {
 *       @Override
 *       public String convert(ThaiBaht baht) {
 *           // 1. Get amount and config
 *           // 2. Normalize to 2 decimal places
 *           // 3. Split into baht and satang parts
 *           // 4. Convert each part to words
 *           // 5. Apply configuration options
 *           // 6. Return formatted text
 *       }
 *
 *       // ... implement other 6 required methods
 *   }
 * }</pre>
 *
 * This package design allows for unlimited extensibility while keeping the core library clean and maintainable.
 *
 * @see io.github.zazalng.contracts.LanguageHandler
 * @see io.github.zazalng.handler.ThaiLanguageHandler
 * @see io.github.zazalng.handler.EnglishLanguageHandler
 * @see io.github.zazalng.handler.TextConverter
 * @author Zazalng
 * @since 1.0
 * @version 2.0.0
 */
package io.github.zazalng.handler;

