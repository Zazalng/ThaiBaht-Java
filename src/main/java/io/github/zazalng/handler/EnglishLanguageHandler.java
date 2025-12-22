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
 * English language implementation of {@link LanguageHandler}.
 * <p>
 * This handler converts numeric amounts to English textual representation using standard
 * English conventions. It implements the complete conversion logic previously found in
 * {@code EnglishConvertHandler}.
 *
 * <h2>English Number Formatting Rules</h2>
 * <p>
 * This converter implements standard English number-to-word conventions:
 * <ul>
 *   <li><strong>Hyphens for compound numbers:</strong> Twenty-Five, Ninety-Nine, etc.</li>
 *   <li><strong>Position words:</strong> Hundred, Thousand, Million</li>
 *   <li><strong>Capitalization:</strong> Each word starts with capital letter</li>
 *   <li><strong>Spacing:</strong> Position words separated by spaces</li>
 *   <li><strong>Zero handling:</strong> Single 0 = "Zero", portions of 0 = omitted</li>
 * </ul>
 *
 * <h2>Unit Words</h2>
 * <ul>
 *   <li>"Baht" - Currency unit</li>
 *   <li>"Satang" - Fractional unit</li>
 *   <li>"Only" - Exact indicator (used when satang is zero)</li>
 * </ul>
 *
 * <h2>Examples</h2>
 * <pre>{@code
 * handler.convert(ThaiBaht.of(new BigDecimal("100.00"), config));
 * // Output: "One Hundred Baht Only"
 *
 * handler.convert(ThaiBaht.of(new BigDecimal("1234.56"), config));
 * // Output: "One Thousand Two Hundred Thirty-Four Baht Fifty-Six Satang"
 * }</pre>
 *
 * @see LanguageHandler
 * @see EnglishConvertHandler
 * @author Zazalng
 * @since 2.0.0
 */
public class EnglishLanguageHandler implements LanguageHandler {
    private static final String[] ENGLISH_DIGITS = {
            "Zero", "One", "Two", "Three", "Four",
            "Five", "Six", "Seven", "Eight", "Nine"
    };

    private static final String[] ENGLISH_TEENS = {
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen",
            "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] ENGLISH_TENS = {
            "", "", "Twenty", "Thirty", "Forty",
            "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    @Override
    public String convert(ThaiBaht baht) {
        if (baht == null || baht.getAmount() == null) {
            throw new IllegalArgumentException("baht and amount must not be null");
        }
        return convertToEnglish(baht.getAmount(), baht.getConfig());
    }

    @Override
    public String getLanguageCode() {
        return "en";
    }

    @Override
    public String getLanguageName() {
        return "English";
    }

    @Override
    public String getUnitWord() {
        return "Baht";
    }

    @Override
    public String getExactWord() {
        return "Only";
    }

    @Override
    public String getSatangWord() {
        return "Satang";
    }

    @Override
    public String getNegativePrefix() {
        return "Minus";
    }

    private String convertToEnglish(BigDecimal amount, ThaiBahtConfig config) {
        // normalize to 2 decimal places (satang) - truncate toward floor
        BigDecimal normalized = amount.setScale(2, RoundingMode.DOWN);

        BigDecimal abs = normalized.abs();

        long baht = abs.longValue();
        int satang = abs.subtract(new BigDecimal(baht)).movePointRight(2).intValue();

        // Generate text for baht part
        String bahtText;
        if (baht == 0) {
            bahtText = ENGLISH_DIGITS[0];
        } else {
            bahtText = convertEnglishInteger(baht);
        }

        // Generate text for satang part
        String satangText;
        if (satang == 0) {
            satangText = ENGLISH_DIGITS[0];
        } else {
            satangText = convertEnglishTwoDigits(satang);
        }

        // Determine which format template to use
        boolean isNegative = normalized.signum() < 0;
        FormatTemplate template = isNegative ? config.getNegativeFormatTemplate() : config.getFormatTemplate();

        String result;
        if (template != null) {
            // Apply custom format with negative prefix and satang value
            String negPrefix = config.getNegativePrefix().isEmpty() ? getNegativePrefix() : config.getNegativePrefix();
            result = FormatApplier.apply(template, bahtText, satangText, satang, this, negPrefix, ENGLISH_DIGITS[0]);
        } else {
            // Use standard formatting
            StringBuilder sb = new StringBuilder();

            if (config.isUseUnit()) {
                sb.append(bahtText).append(" ").append(getUnitWord());
            } else {
                sb.append(bahtText);
            }

            if (satang == 0) {
                if (config.isUseUnit()) sb.append(" ").append(getExactWord());
            } else {
                sb.append(" ");
                sb.append(satangText);
                if (config.isUseUnit()) sb.append(" ").append(getSatangWord());
            }

            result = sb.toString();

            // Apply negative prefix only if no custom format is used
            if (isNegative) {
                return (config.getNegativePrefix().isEmpty() ? getNegativePrefix() : config.getNegativePrefix()) + " " + result;
            }
        }

        return result;
    }

    // convert English full integer (handles millions)
    private String convertEnglishInteger(long number) {
        if (number == 0) return ENGLISH_DIGITS[0];

        StringBuilder sb = new StringBuilder();
        long million = 1_000_000L;

        if (number >= million) {
            long high = number / million;
            long rem = number % million;
            sb.append(convertEnglishInteger(high)).append(" Million");
            if (rem > 0) {
                sb.append(" ").append(convertEnglishUnderMillion(rem));
            }
        } else {
            sb.append(convertEnglishUnderMillion(number));
        }
        return sb.toString();
    }

    // convert English numbers from 1..999_999
    private String convertEnglishUnderMillion(long number) {
        if (number == 0) return "";

        StringBuilder sb = new StringBuilder();

        long thousand = number / 1000;
        long remainder = number % 1000;

        if (thousand > 0) {
            sb.append(convertEnglishUnderThousand(thousand));
            sb.append(" Thousand");
            if (remainder > 0) {
                sb.append(" ");
            }
        }

        if (remainder > 0) {
            sb.append(convertEnglishUnderThousand(remainder));
        }

        return sb.toString();
    }

    // convert English numbers from 1..999
    private String convertEnglishUnderThousand(long number) {
        if (number == 0) return "";

        StringBuilder sb = new StringBuilder();

        long hundred = number / 100;
        long remainder = number % 100;

        if (hundred > 0) {
            sb.append(ENGLISH_DIGITS[(int) hundred]);
            sb.append(" Hundred");
            if (remainder > 0) {
                sb.append(" ");
            }
        }

        if (remainder >= 20) {
            int tens = (int) (remainder / 10);
            int unit = (int) (remainder % 10);
            sb.append(ENGLISH_TENS[tens]);
            if (unit > 0) {
                sb.append("-").append(ENGLISH_DIGITS[unit]);
            }
        } else if (remainder >= 10) {
            sb.append(ENGLISH_TEENS[(int) remainder - 10]);
        } else if (remainder > 0) {
            sb.append(ENGLISH_DIGITS[(int) remainder]);
        }

        return sb.toString();
    }

    // convert English 1..99 for satang
    private String convertEnglishTwoDigits(int n) {
        if (n < 0 || n > 99) throw new IllegalArgumentException("n must be between 0 and 99");
        if (n == 0) return ENGLISH_DIGITS[0];
        if (n < 10) return ENGLISH_DIGITS[n];

        if (n >= 20) {
            int tens = n / 10;
            int unit = n % 10;
            StringBuilder sb = new StringBuilder();
            sb.append(ENGLISH_TENS[tens]);
            if (unit > 0) {
                sb.append("-").append(ENGLISH_DIGITS[unit]);
            }
            return sb.toString();
        } else {
            return ENGLISH_TEENS[n - 10];
        }
    }
}

