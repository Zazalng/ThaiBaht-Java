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
package io.github.zazalng.contracts;

/**
 * Enumeration of supported output languages for currency text conversion.
 * <p>
 * This enum defines the available languages that can be used when converting
 * numeric amounts to textual representations. Each language has a default negative
 * prefix that will be automatically used when the language is selected.
 *
 * <p>
 * <strong>Prefix Behavior (v1.3.0+):</strong>
 * <ul>
 *   <li>Each language has a default negative prefix: THAI uses "ลบ" (Thai minus),
 *       ENGLISH uses "Minus"</li>
 *   <li>When switching languages via {@link io.github.zazalng.ThaiBahtConfig.Builder#language(Language)},
 *       the prefix automatically updates to the new language's default</li>
 *   <li>If you explicitly set a prefix with {@link io.github.zazalng.ThaiBahtConfig.Builder#setPrefix(String)},
 *       it will be preserved even when changing languages</li>
 *   <li>To reset to language defaults after setting a custom prefix, create a new builder</li>
 * </ul>
 *
 * <p>
 * Usage example:
 * <pre>
 *   // Language automatically provides correct prefix
 *   ThaiBahtConfig thaiConfig = ThaiBahtConfig.builder(Language.THAI).build();
 *   // Prefix: "ลบ" (Thai default)
 *
 *   ThaiBahtConfig englishConfig = ThaiBahtConfig.builder(Language.ENGLISH).build();
 *   // Prefix: "Minus" (English default)
 * </pre>
 *
 * @since 1.3.0
 */
public enum Language {
    /**
     * Thai language output.
     * <p>
     * Converts amounts to Thai text representation (e.g., "หนึ่งร้อยบาท").
     */
    THAI,

    /**
     * English language output.
     * <p>
     * Converts amounts to English text representation (e.g., "one hundred baht").
     */
    ENGLISH
}

