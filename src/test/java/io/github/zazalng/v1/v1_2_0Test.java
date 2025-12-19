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
package io.github.zazalng.v1;

import io.github.zazalng.ThaiBaht;
import io.github.zazalng.ThaiBahtConfig;
import io.github.zazalng.contracts.Language;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test Implement for English update
 */
public class v1_2_0Test {

    @Test
    public void testEnglishZero() {
        String result = ThaiBaht.of(new BigDecimal("0"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("Zero Baht Only", result);

        String resultWithSatang = ThaiBaht.of(new BigDecimal("0.05"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("Zero Baht Five Satang", resultWithSatang);
    }

    @Test
    public void testEnglishSimpleBaht() {
        String result = ThaiBaht.of(new BigDecimal("1.00"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("One Baht Only", result);

        String result11 = ThaiBaht.of(new BigDecimal("11.0"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("Eleven Baht Only", result11);

        String result21 = ThaiBaht.of(new BigDecimal("21.0"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("Twenty-One Baht Only", result21);

        String result101 = ThaiBaht.of(new BigDecimal("101.00"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("One Hundred One Baht Only", result101);
    }

    @Test
    public void testEnglishSatang() {
        String result = ThaiBaht.of(new BigDecimal("1.01"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("One Baht One Satang", result);

        String result20 = ThaiBaht.of(new BigDecimal("1.20"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("One Baht Twenty Satang", result20);

        String result11sat = ThaiBaht.of(new BigDecimal("1.11"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("One Baht Eleven Satang", result11sat);
    }

    @Test
    public void testEnglishMillionAndLargeNumbers() {
        String result = ThaiBaht.of(new BigDecimal("1000000"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("One Million Baht Only", result);

        String result1234567 = ThaiBaht.of(new BigDecimal("1234567"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("One Million Two Hundred Thirty-Four Thousand Five Hundred Sixty-Seven Baht Only", result1234567);

        String result10million = ThaiBaht.of(new BigDecimal("10000001"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("Ten Million One Baht Only", result10million);
    }

    @Test
    public void testEnglishNegative() {
        String result = ThaiBaht.of(new BigDecimal("-100.00"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).build());
        assertEquals("Minus One Hundred Baht Only", result);

        String resultCustom = ThaiBaht.of(new BigDecimal("-101.21"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).setPrefix("negative").build());
        assertEquals("negative One Hundred One Baht Twenty-One Satang", resultCustom);
    }

    @Test
    public void testEnglishWithoutUnits() {
        String result = ThaiBaht.of(new BigDecimal("100.25"),
            ThaiBahtConfig.builder().language(Language.ENGLISH).useUnit(false).build());
        assertEquals("One Hundred Twenty-Five", result);
    }

    @Test
    public void testDefaultThaiLanguageStillWorks() {
        // Verify that default config still uses Thai
        String result = ThaiBaht.of(new BigDecimal("100.00"));
        assertEquals("หนึ่งร้อยบาทถ้วน", result);
    }

    @Test
    public void testLanguageConfigBuilder() {
        // Test using the ThaiBaht builder with config
        ThaiBaht converter = ThaiBaht.create(new BigDecimal("500.25"))
            .config(b -> b.language(Language.ENGLISH));
        String result = converter.toString();
        assertEquals("Five Hundred Baht Twenty-Five Satang", result);
    }
}

