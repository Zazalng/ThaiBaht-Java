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
 * Tests for custom format string functionality (1.4.0+)
 */
public class v1_4_0Test {

    @Test
    public void testEnglishFormatPositive() {
        // English format with named placeholders
        String result = ThaiBaht.of(new BigDecimal("0.01"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormat("{INTEGER}{UNIT}กับอีก{FLOAT}{SATANG}", false)
                        .build());
        // Expected: "ZeroBahtกับอีกOneSatang"
        assertEquals("ZeroBahtกับอีกOneSatang", result);
    }

    @Test
    public void testEnglishFormatNegative() {
        // Format with negative template using named placeholders
        String result = ThaiBaht.of(new BigDecimal("-0.01"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormat("({INTEGER}{UNIT}กับอีก{FLOAT}{SATANG})", true)
                        .build());
        // Expected: "(ZeroBahtกับอีกOneSatang)"
        assertEquals("(ZeroBahtกับอีกOneSatang)", result);
    }

    @Test
    public void testThaiFormatPositive() {
        // Thai format with named placeholders
        String result = ThaiBaht.of(new BigDecimal("0.01"),
                ThaiBahtConfig.builder(Language.THAI)
                        .setFormat("{INTEGER}{UNIT}จุด{FLOAT}{SATANG}", false)
                        .build());
        // Expected: "ศูนย์บาทจุดหนึ่งสตางค์"
        assertEquals("ศูนย์บาทจุดหนึ่งสตางค์", result);
    }

    @Test
    public void testThaiFormatNegative() {
        // Thai format with negative template
        String result = ThaiBaht.of(new BigDecimal("-0.01"),
                ThaiBahtConfig.builder(Language.THAI)
                        .setFormat("({INTEGER}{UNIT}จุด{FLOAT}{SATANG})", true)
                        .build());
        // Expected: "(ศูนย์บาทจุดหนึ่งสตางค์)"
        assertEquals("(ศูนย์บาทจุดหนึ่งสตางค์)", result);
    }

    @Test
    public void testEnglishFormatWithoutUnits() {
        // Format without unit symbols - just numbers
        String result = ThaiBaht.of(new BigDecimal("1234.56"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("{INTEGER} and {FLOAT}/100")
                        .build());
        // Expected: "One Thousand Two Hundred Thirty-Four and Fifty-Six/100"
        assertEquals("One Thousand Two Hundred Thirty-Four and Fifty-Six/100", result);
    }

    @Test
    public void testThaiFormatAlternative() {
        // Different Thai format with custom text
        String result = ThaiBaht.of(new BigDecimal("123.45"),
                ThaiBahtConfig.builder(Language.THAI)
                        .setFormatTemplate("จำนวน {INTEGER} ราคา {FLOAT}")
                        .build());
        // Custom words "จำนวน" and "ราคา" should be preserved
        assertEquals("จำนวน หนึ่งร้อยยี่สิบสาม ราคา สี่สิบห้า", result);
    }

    @Test
    public void testMultiplePlaceholders() {
        // Using same placeholder multiple times
        String result = ThaiBaht.of(new BigDecimal("10.50"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("{INTEGER}{UNIT} ({INTEGER} when {FLOAT})")
                        .build());
        // Expected: "TenBaht (Ten when Fifty)"
        assertEquals("TenBaht (Ten when Fifty)", result);
    }

    @Test
    public void testFormatPreservesCustomText() {
        // Test that custom text is preserved and not erased
        String result = ThaiBaht.of(new BigDecimal("5.25"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("Total Cost: {INTEGER}{UNIT} with {FLOAT}{SATANG} charge")
                        .build());
        // Should not erase "Total Cost:", "with", or "charge"
        assertEquals("Total Cost: FiveBaht with Twenty-FiveSatang charge", result);
    }

    @Test
    public void testNegativeFormatOverride() {
        // Different format for negative values
        String result = ThaiBaht.of(new BigDecimal("-50.75"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("{INTEGER}{UNIT} and {FLOAT}{SATANG}")
                        .setNegativeFormatTemplate("({INTEGER}{UNIT}) ({FLOAT}{SATANG})")
                        .build());
        // Expected: "(FiftyBaht) (Seventy-FiveSatang)" format
        assertEquals("(FiftyBaht) (Seventy-FiveSatang)", result);
    }

    @Test
    public void testFormatWithNegativePrefixPlaceholder() {
        // Use {NEGPREFIX} placeholder in negative format
        String result = ThaiBaht.of(new BigDecimal("-1.50"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("{INTEGER}{UNIT} {FLOAT}{SATANG}")
                        .setNegativeFormatTemplate("{NEGPREFIX} {INTEGER}{UNIT} {FLOAT}{SATANG}")
                        .build());
        // Expected: "Minus OneBaht FiftySatang"
        assertEquals("Minus OneBaht FiftySatang", result);
    }

    @Test
    public void testFormatThaiUnitOnly(){
        String result = ThaiBaht.of(new BigDecimal("100.00"),
                ThaiBahtConfig.builder(Language.THAI)
                        .setFormat("({INTEGER}{UNIT}{EXACT}{FLOAT?{FLOAT}{SATANG}})", false)
                        .build());
        // {EXACT} shows "ถ้วน" when satang is 0, then conditional hides satang
        assertEquals("(หนึ่งร้อยบาทถ้วน)", result);

        String resultNeg = ThaiBaht.of(new BigDecimal("-100.00"),
                ThaiBahtConfig.builder(Language.THAI)
                        .setFormat("({NEGPREFIX} {INTEGER}{UNIT}{EXACT}{FLOAT?{FLOAT}{SATANG}})", true)
                        .build());
        assertEquals("(ลบ หนึ่งร้อยบาทถ้วน)", resultNeg);
    }

    @Test
    public void testFormatEnglishUnitOnly(){
        String result = ThaiBaht.of(new BigDecimal("100.00"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormat("({INTEGER} {UNIT} {EXACT}{FLOAT? {FLOAT} {SATANG}})", false)
                        .build());
        // {EXACT} shows "Only" when satang is 0, then conditional hides satang
        assertEquals("(One Hundred Baht Only)", result);

        String resultNeg = ThaiBaht.of(new BigDecimal("-100.00"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormat("({NEGPREFIX} {INTEGER} {UNIT} {EXACT}{FLOAT? {FLOAT} {SATANG}})", true)
                        .build());
        assertEquals("(Minus One Hundred Baht Only)", resultNeg);
    }

    @Test
    public void testConditionalFloatPlaceholder() {
        // Use {FLOAT?...} to hide zero satang
        String result = ThaiBaht.of(new BigDecimal("1.00"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("({INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}})")
                        .build());
        // Expected: "(OneBaht)" - zero satang is hidden!
        assertEquals("(OneBaht)", result);
    }

    @Test
    public void testConditionalFloatPlaceholderWithValue() {
        // Use {FLOAT?...} to show satang when not zero
        String result = ThaiBaht.of(new BigDecimal("1.50"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("({INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}})")
                        .build());
        // Expected: "(OneBahtFiftySatang)" - satang is shown
        assertEquals("(OneBahtFiftySatang)", result);
    }

    @Test
    public void testConditionalFloatWithSpace() {
        // Use {FLOAT? ...} with leading space
        String result = ThaiBaht.of(new BigDecimal("1.00"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("{INTEGER}{UNIT}{FLOAT? {FLOAT} {SATANG}}")
                        .build());
        // Expected: "OneBaht" - no space or satang when zero
        assertEquals("OneBaht", result);
    }

    @Test
    public void testConditionalFloatWithSpaceAndValue() {
        // Use {FLOAT? ...} with leading space and actual value
        String result = ThaiBaht.of(new BigDecimal("1.50"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("{INTEGER}{UNIT}{FLOAT? {FLOAT} {SATANG}}")
                        .build());
        // Expected: "OneBaht Fifty Satang" - space and satang shown
        assertEquals("OneBaht Fifty Satang", result);
    }

    @Test
    public void testConditionalThaiFloatPlaceholder() {
        // Thai with {FLOAT?...} to hide zero satang
        String result = ThaiBaht.of(new BigDecimal("100.00"),
                ThaiBahtConfig.builder(Language.THAI)
                        .setFormatTemplate("{INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}}")
                        .build());
        // Expected: "หนึ่งร้อยบาท" - zero satang is hidden!
        assertEquals("หนึ่งร้อยบาท", result);
    }

    @Test
    public void testConditionalThaiFloatPlaceholderWithValue() {
        // Thai with {FLOAT?...} to show satang when not zero
        String result = ThaiBaht.of(new BigDecimal("100.50"),
                ThaiBahtConfig.builder(Language.THAI)
                        .setFormatTemplate("{INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}}")
                        .build());
        // Expected: "หนึ่งร้อยบาทห้าสิบสตางค์" - satang is shown
        assertEquals("หนึ่งร้อยบาทห้าสิบสตางค์", result);
    }

    @Test
    public void testConditionalNegativePlaceholder() {
        // Solve the issue: hide "Only" / "ถ้วน" when satang is zero
        String resultPos = ThaiBaht.of(new BigDecimal("1.00"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("({INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}})")
                        .setNegativeFormatTemplate("({NEGPREFIX} {INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}})")
                        .build());
        // Expected: "(OneBaht)" - no "Only" when satang is zero
        assertEquals("(OneBaht)", resultPos);

        String resultNeg = ThaiBaht.of(new BigDecimal("-1.00"),
                ThaiBahtConfig.builder(Language.ENGLISH)
                        .setFormatTemplate("({INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}})")
                        .setNegativeFormatTemplate("({NEGPREFIX} {INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}})")
                        .build());
        // Negative: "( Minus OneBaht)" - space included in the conditional content
        assertEquals("(Minus OneBaht)", resultNeg);
    }
}
