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
package io.github.zazalng;

import io.github.zazalng.contracts.Language;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for version 1.3.0 prefix auto-update feature.
 * <p>
 * Tests verify that:
 * 1. When changing language, prefix automatically updates to language default
 * 2. Explicitly set prefixes are preserved across language changes
 * 3. Backward compatibility is maintained
 * 4. toBuilder() behavior is correct
 *
 * @since 1.3.0
 */
public class PrefixAutoUpdateTest {

    @Test
    public void testBackwardCompatibility_ParamlessBuilder() {
        // v1.2.0 code should still work
        ThaiBahtConfig config = ThaiBahtConfig.builder()
            .build();

        String result = ThaiBaht.of(new BigDecimal("-100.00"), config);
        assertEquals("ลบหนึ่งร้อยบาทถ้วน", result);
    }

    @Test
    public void testAutomaticPrefixUpdate_ToEnglish() {
        // When changing language to ENGLISH, prefix should auto-update to "Minus"
        ThaiBahtConfig config = ThaiBahtConfig.builder()
            .language(Language.ENGLISH)
            .build();

        assertEquals("", config.getNegativePrefix());

        String result = ThaiBaht.of(new BigDecimal("-100.00"), config);
        assertEquals("Minus One Hundred Baht Only", result);
    }

    @Test
    public void testAutomaticPrefixUpdate_BackToThai() {
        // When changing language back to THAI, prefix should auto-update to "ลบ"
        ThaiBahtConfig config = ThaiBahtConfig.builder()
            .language(Language.ENGLISH)
            .language(Language.THAI)
            .build();

        assertEquals("", config.getNegativePrefix());

        String result = ThaiBaht.of(new BigDecimal("-100.00"), config);
        assertEquals("ลบหนึ่งร้อยบาทถ้วน", result);
    }

    @Test
    public void testExplicitPrefix_PreservedAcrossLanguageChange() {
        // When explicitly setting prefix, it should be preserved even when language changes
        ThaiBahtConfig config = ThaiBahtConfig.builder()
            .setPrefix("custom_prefix")  // Explicitly set
            .language(Language.ENGLISH)
            .build();

        assertEquals("custom_prefix", config.getNegativePrefix());

        String result = ThaiBaht.of(new BigDecimal("-100.00"), config);
        assertEquals("custom_prefix One Hundred Baht Only", result);
    }

    @Test
    public void testExplicitPrefix_PreservedWhenSwitchingBackToThai() {
        // Explicitly set prefix should survive language switches
        ThaiBahtConfig config = ThaiBahtConfig.builder()
            .setPrefix("NEGATIVE")
            .language(Language.ENGLISH)
            .language(Language.THAI)
            .build();

        assertEquals("NEGATIVE", config.getNegativePrefix());

        String result = ThaiBaht.of(new BigDecimal("-100.00"), config);
        assertEquals("NEGATIVEหนึ่งร้อยบาทถ้วน", result);
    }

    @Test
    public void testExplicitLanguageParameter() {
        // Using builder(Language.ENGLISH) should set correct default prefix
        ThaiBahtConfig config = ThaiBahtConfig.builder(Language.ENGLISH)
            .build();

        assertEquals("", config.getNegativePrefix());

        String result = ThaiBaht.of(new BigDecimal("-50.00"), config);
        assertEquals("Minus Fifty Baht Only", result);
    }

    @Test
    public void testExplicitLanguageParameter_Thai() {
        // Using builder(Language.THAI) should set Thai default prefix
        ThaiBahtConfig config = ThaiBahtConfig.builder(Language.THAI)
            .build();

        assertEquals("", config.getNegativePrefix());

        String result = ThaiBaht.of(new BigDecimal("-50.00"), config);
        assertEquals("ลบห้าสิบบาทถ้วน", result);
    }

    @Test
    public void testToBuilder_PreservesPrefixAsExplicitlySet() {
        // toBuilder() should treat current prefix as explicitly set
        ThaiBahtConfig original = ThaiBahtConfig.builder(Language.THAI)
            .build();

        // Convert to builder and try to change language
        ThaiBahtConfig modified = original.toBuilder()
            .language(Language.ENGLISH)
            .build();

        // Prefix should NOT update (treated as explicitly set)
        assertEquals("", modified.getNegativePrefix());
    }

    @Test
    public void testDefaultConfig_Unchanged() {
        // defaultConfig() should still be Thai with "ลบ"
        ThaiBahtConfig config = ThaiBahtConfig.defaultConfig();

        assertEquals(Language.THAI, config.getLanguage());
        assertEquals("", config.getNegativePrefix());

        String result = ThaiBaht.of(new BigDecimal("-100.00"), config);
        assertEquals("ลบหนึ่งร้อยบาทถ้วน", result);
    }

    @Test
    public void testBuilder_SetPrefixBeforeLanguage() {
        // Set prefix first, then change language - prefix should stay
        ThaiBahtConfig config = ThaiBahtConfig.builder()
            .setPrefix("minus_value")
            .language(Language.ENGLISH)
            .build();

        assertEquals("minus_value", config.getNegativePrefix());
    }

    @Test
    public void testBuilder_LanguageBeforeSetPrefix() {
        // Set language first, then override with explicit prefix
        ThaiBahtConfig config = ThaiBahtConfig.builder()
            .language(Language.ENGLISH)  // Prefix becomes "Minus"
            .setPrefix("custom")         // Override it
            .build();

        assertEquals("custom", config.getNegativePrefix());
    }

    @Test
    public void testInstance_ConfigMethodBehavior() {
        // Using config() method should preserve prefix as explicitly set
        ThaiBaht converter = ThaiBaht.create(new BigDecimal("-100.00"))
            .config(b -> b.language(Language.ENGLISH));

        ThaiBahtConfig resultConfig = converter.getConfig();
        // Prefix should NOT automatically update (toBuilder() treats it as explicit)
        assertEquals("", resultConfig.getNegativePrefix());
    }

    @Test
    public void testEnglishDefault_WithMultipleSatang() {
        // English with auto-updated prefix for larger amounts
        ThaiBahtConfig config = ThaiBahtConfig.builder()
            .language(Language.ENGLISH)
            .build();

        String result = ThaiBaht.of(new BigDecimal("-1234.56"), config);
        assertEquals("Minus One Thousand Two Hundred Thirty-Four Baht Fifty-Six Satang", result);
    }

    @Test
    public void testThaiDefault_WithMultipleSatang() {
        // Thai with auto-updated prefix for larger amounts
        ThaiBahtConfig config = ThaiBahtConfig.builder()
            .language(Language.THAI)
            .build();

        String result = ThaiBaht.of(new BigDecimal("-1234.56"), config);
        assertEquals("ลบหนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์", result);
    }
}

