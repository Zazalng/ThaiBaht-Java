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
package io.github.zazalng.handler;

import io.github.zazalng.ThaiBaht;
import io.github.zazalng.ThaiBahtConfig;
import io.github.zazalng.contracts.LanguageHandler;
import io.github.zazalng.utils.FormatTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Thai language implementation of {@link LanguageHandler}.
 * <p>
 * This handler converts numeric amounts to Thai textual representation using traditional
 * Thai numerical conventions and grammar rules. It implements the complete conversion logic
 * previously found in {@code ThaiConvertHandler}.
 *
 * <h2>Thai Grammar Rules Implemented</h2>
 * <p>
 * This converter implements comprehensive Thai numerical grammar including:
 * <ul>
 *   <li><strong>"เอ็ด" (edt):</strong> Used for the ones digit in compound numbers
 *       (e.g., 101 = "หนึ่งร้อยเอ็ด", 21 = "ยี่สิบเอ็ด" but NOT 1 alone)</li>
 *   <li><strong>"ยี่" (yee):</strong> Special form for twenty (20 = "ยี่สิบ") and
 *       for twenties in compound numbers (120 = "หนึ่งร้อยยี่สิบ")</li>
 *   <li><strong>Silent "หนึ่ง" (1):</strong> In tens position, 10 is "สิบ" not "หนึ่งสิบ"</li>
 *   <li><strong>Repeating "ล้าน" (millions):</strong> Large numbers repeat the word "ล้าน"
 *       for groups of millions (e.g., 1,000,000 = "หนึ่งล้าน", 1,000,001,000 = "หนึ่งล้านหนึ่งล้าน")</li>
 * </ul>
 *
 * <h2>Unit Words</h2>
 * <ul>
 *   <li>"บาท" - Currency unit (Baht)</li>
 *   <li>"สตางค์" - Fractional unit (Satang)</li>
 *   <li>"ถ้วน" - Exact/full indicator (used when satang is zero)</li>
 * </ul>
 *
 * <h2>Examples</h2>
 * <pre>{@code
 * handler.convert(ThaiBaht.of(new BigDecimal("100.00"), config));
 * // Output: "หนึ่งร้อยบาทถ้วน"
 *
 * handler.convert(ThaiBaht.of(new BigDecimal("1234.56"), config));
 * // Output: "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
 * }</pre>
 *
 * @see LanguageHandler
 * @see ThaiConvertHandler
 * @author Zazalng
 * @since 2.0.0
 */
public class ThaiLanguageHandler implements LanguageHandler {
    private static final String[] THAI_DIGITS = {
            "ศูนย์", "หนึ่ง", "สอง", "สาม", "สี่",
            "ห้า", "หก", "เจ็ด", "แปด", "เก้า"
    };

    @Override
    public String convert(ThaiBaht baht) {
        if (baht == null || baht.getAmount() == null) {
            throw new IllegalArgumentException("baht and amount must not be null");
        }
        return convertToThai(baht.getAmount(), baht.getConfig());
    }

    @Override
    public String getLanguageCode() {
        return "th";
    }

    @Override
    public String getLanguageName() {
        return "Thai";
    }

    @Override
    public String getUnitWord() {
        return "บาท";
    }

    @Override
    public String getExactWord() {
        return "ถ้วน";
    }

    @Override
    public String getSatangWord() {
        return "สตางค์";
    }

    @Override
    public String getNegativePrefix() {
        return "ลบ";
    }

    private String convertToThai(BigDecimal amount, ThaiBahtConfig config) {
        // normalize to 2 decimal places (satang) - truncate toward floor
        BigDecimal normalized = amount.setScale(2, RoundingMode.DOWN);

        BigDecimal abs = normalized.abs();

        long baht = abs.longValue();
        int satang = abs.subtract(new BigDecimal(baht)).movePointRight(2).intValue();

        // Generate text for baht part
        String bahtText;
        if (baht == 0) {
            bahtText = THAI_DIGITS[0];
        } else {
            bahtText = convertThaiInteger(baht);
        }

        // Generate text for satang part
        String satangText;
        if (satang == 0) {
            satangText = THAI_DIGITS[0];
        } else {
            satangText = convertThaiTwoDigits(satang);
        }

        // Determine which format template to use
        boolean isNegative = normalized.signum() < 0;
        FormatTemplate template = isNegative ? config.getNegativeFormatTemplate() : config.getFormatTemplate();

        String result;
        if (template != null) {
            // Apply custom format with negative prefix and satang value
            String negPrefix = config.getNegativePrefix().isEmpty() ? getNegativePrefix() : config.getNegativePrefix();
            result = FormatApplier.apply(template, bahtText, satangText, satang, this, negPrefix, THAI_DIGITS[0]);
        } else {
            // Use standard formatting
            StringBuilder sb = new StringBuilder();

            if (config.isUseUnit()) {
                sb.append(bahtText).append(getUnitWord());
            } else {
                sb.append(bahtText);
            }

            if (satang == 0) {
                if (config.isUseUnit()) sb.append(getExactWord());
            } else {
                sb.append(satangText);
                if (config.isUseUnit()) sb.append(getSatangWord());
            }

            result = sb.toString();

            // Apply negative prefix only if no custom format is used
            if (isNegative) {
                return (config.getNegativePrefix().isEmpty() ? getNegativePrefix() : config.getNegativePrefix()) + result;
            }
        }

        return result;
    }

    // convert Thai full integer (handles repeating 'ล้าน' segments)
    private String convertThaiInteger(long number) {
        if (number == 0) return THAI_DIGITS[0];

        StringBuilder sb = new StringBuilder();
        long million = 1_000_000L;

        if (number >= million) {
            long high = number / million;
            long rem = number % million;
            sb.append(convertThaiInteger(high)).append("ล้าน");
            if (rem > 0) {
                sb.append(convertThaiUnderMillion(rem));
            }
        } else {
            sb.append(convertThaiUnderMillion(number));
        }
        return sb.toString();
    }

    // convert Thai numbers from 1..999_999
    private String convertThaiUnderMillion(long number) {
        // positions: แสน(100000), หมื่น(10000), พัน(1000), ร้อย(100), สิบ(10), หน่วย(1)
        int[] divisors = {100000, 10000, 1000, 100, 10, 1};
        String[] posWords = {"แสน", "หมื่น", "พัน", "ร้อย", "สิบ", ""};

        StringBuilder sb = new StringBuilder();
        long remaining = number;

        for (int i = 0; i < divisors.length; i++) {
            int div = divisors[i];
            int digit = (int) (remaining / div);
            remaining = remaining % div;

            if (digit == 0) continue;

            if (div == 10) { // tens place special rules
                if (digit == 1) {
                    sb.append("สิบ");
                } else if (digit == 2) {
                    sb.append("ยี่").append("สิบ");
                } else {
                    sb.append(THAI_DIGITS[digit]).append("สิบ");
                }
            } else if (div == 1) { // unit place special rule
                if (digit == 1 && number > 10) {
                    // last digit 1 in numbers greater than 10 becomes 'เอ็ด'
                    sb.append("เอ็ด");
                } else {
                    sb.append(THAI_DIGITS[digit]);
                }
            } else {
                sb.append(THAI_DIGITS[digit]).append(posWords[i]);
            }
        }

        return sb.toString();
    }

    // convert Thai 1..99 for satang
    private String convertThaiTwoDigits(int n) {
        if (n < 0 || n > 99) throw new IllegalArgumentException("n must be between 0 and 99");
        if (n == 0) return THAI_DIGITS[0];
        if (n < 10) return THAI_DIGITS[n];

        int tens = n / 10;
        int unit = n % 10;
        StringBuilder sb = new StringBuilder();

        if (tens == 1) {
            sb.append("สิบ");
        } else if (tens == 2) {
            sb.append("ยี่สิบ");
        } else {
            sb.append(THAI_DIGITS[tens]).append("สิบ");
        }

        if (unit == 0) {
            // nothing
        } else if (unit == 1) {
            sb.append("เอ็ด");
        } else {
            sb.append(THAI_DIGITS[unit]);
        }
        return sb.toString();
    }
}

