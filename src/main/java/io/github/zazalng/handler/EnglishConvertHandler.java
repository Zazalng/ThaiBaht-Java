package io.github.zazalng.handler;

import io.github.zazalng.ThaiBaht;
import io.github.zazalng.ThaiBahtConfig;
import io.github.zazalng.contracts.Language;
import io.github.zazalng.utils.FormatTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Internal English language converter for converting numeric amounts to English Baht text.
 * <p>
 * This package-private handler implements the specialized logic for converting
 * {@link BigDecimal} amounts to their English textual representation. It is not intended
 * for direct use; instead, access through the public {@link ThaiBaht} API.
 *
 * <h2>English Number Formatting Rules</h2>
 * <p>
 * This converter implements standard English number-to-word conventions:
 * <ul>
 *   <li><strong>Hyphens for compound numbers:</strong> Twenty-Five, Ninety-Nine, etc.</li>
 *   <li><strong>Position words:</strong> Hundred, Thousand, Million</li>
 *   <li><strong>Capitalization:</strong> Each word starts with capital letter</li>
 *   <li><strong>Spacing:</strong> Position words separated by spaces (e.g., "One Million Twenty Thousand")</li>
 *   <li><strong>Zero handling:</strong> Single 0 = "Zero", portions of 0 = omitted</li>
 * </ul>
 *
 * <h2>Conversion Process</h2>
 * <p>
 * The conversion follows these high-level steps:
 * <ol>
 *   <li>Normalize amount to 2 decimal places using {@link java.math.RoundingMode#DOWN}</li>
 *   <li>Extract absolute value to handle magnitude separately</li>
 *   <li>Split into integer (baht) and fractional (satang) parts</li>
 *   <li>Convert each part using specialized English digit conversion methods</li>
 *   <li>Apply configuration options (unit words, custom format, spaces)</li>
 *   <li>Handle negative prefix if amount is negative</li>
 * </ol>
 *
 * <h2>Number Conversion Methods</h2>
 * <p>
 * The converter uses specialized methods for different numeric ranges:
 * <ul>
 *   <li><strong>convertEnglishInteger():</strong> Handles full integers including millions.
 *       Recursively processes millions and remainder.</li>
 *   <li><strong>convertEnglishUnderMillion():</strong> Handles 1-999,999 by separating thousands.
 *       Recursively calls under-thousand converter.</li>
 *   <li><strong>convertEnglishUnderThousand():</strong> Handles 1-999 by separating hundreds
 *       and remainder (using under-hundred logic).</li>
 *   <li><strong>convertEnglishTwoDigits():</strong> Handles 1-99 for satang conversion using
 *       proper hyphenation for compound numbers.</li>
 * </ul>
 *
 * <h2>Satang Handling</h2>
 * <p>
 * Satang (fractional part) is handled as follows:
 * <ul>
 *   <li>When satang = 0: Output "Only" (or "Exact") if units are enabled</li>
 *   <li>When satang > 0: Convert using specialized two-digit converter and append "Satang"</li>
 *   <li>Example: 100.50 → "One Hundred Baht Fifty Satang"</li>
 *   <li>Uses {@link java.math.RoundingMode#DOWN} for truncation (100.999 → 100.99)</li>
 * </ul>
 *
 * <h2>Configuration Support</h2>
 * <p>
 * This handler respects all {@link ThaiBahtConfig} options:
 * <ul>
 *   <li>Unit words inclusion (Baht, Satang, Only)</li>
 *   <li>Custom format templates via {@link FormatApplier}</li>
 *   <li>Negative prefix configuration (defaults to "Minus" for English)</li>
 *   <li>Formal wording mode (reserved for future use)</li>
 * </ul>
 *
 * <h2>Number Examples</h2>
 * <p>
 * <strong>English number formatting:</strong>
 * <pre>{@code
 * Integer examples:
 * 0      → "Zero"
 * 5      → "Five"
 * 15     → "Fifteen"
 * 25     → "Twenty-Five"
 * 100    → "One Hundred"
 * 101    → "One Hundred One"
 * 1000   → "One Thousand"
 * 1234   → "One Thousand Two Hundred Thirty-Four"
 * 1000000 → "One Million"
 * 1234567 → "One Million Two Hundred Thirty-Four Thousand Five Hundred Sixty-Seven"
 * }</pre>
 *
 * <h2>Currency Examples</h2>
 * <p>
 * <strong>Currency conversion examples:</strong>
 * <pre>{@code
 * // With units enabled (default)
 * "100.00"  → "One Hundred Baht Only"
 * "100.01"  → "One Hundred Baht One Satang"
 * "1234.56" → "One Thousand Two Hundred Thirty-Four Baht Fifty-Six Satang"
 *
 * // Without units
 * "100.00"  → "One Hundred"
 * "1000.50" → "One Thousand Fifty"
 *
 * // Negative amounts
 * "-100.00" → "Minus One Hundred Baht Only"
 * }</pre>
 *
 * @implNote This class is package-private and stateless. It uses static methods only.
 *           The conversion algorithm maintains no mutable state and is thread-safe.
 * @see ThaiBaht
 * @see ThaiBahtConfig
 * @see TextConverter
 * @see FormatApplier
 * @author Zazalng
 * @since 1.3.0
 * @version 1.4.0
 */
public final class EnglishConvertHandler {
    /**
     * Default negative prefix for English
     */
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

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private EnglishConvertHandler() {}

    static String convert(ThaiBaht baht){
        return convertToEnglish(baht.getAmount(), baht.getConfig());
    }

    private static String convertToEnglish(BigDecimal amount, ThaiBahtConfig config) {
        // normalize to 2 decimal places (satang) - truncate toward floor
        BigDecimal normalized = amount.setScale(2, RoundingMode.DOWN);

        BigDecimal abs = normalized.abs();

        long baht = abs.longValue();
        int satang = abs.subtract(new BigDecimal(baht)).movePointRight(2).intValue();
        Language lang = config.getLanguage();

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
            String negPrefix = config.getNegativePrefix().isEmpty() ? lang.getPrefix() : config.getNegativePrefix();
            result = FormatApplier.apply(template, bahtText, satangText, satang, lang, negPrefix, ENGLISH_DIGITS[0]);
        } else {
            // Use standard formatting
            StringBuilder sb = new StringBuilder();

            if (config.isUseUnit()) {
                sb.append(bahtText).append(" ").append(lang.getUnit());
            } else {
                sb.append(bahtText);
            }

            if (satang == 0) {
                if (config.isUseUnit()) sb.append(" ").append(lang.getExact());
            } else {
                sb.append(" ");
                sb.append(satangText);
                if (config.isUseUnit()) sb.append(" ").append(lang.getSatang());
            }

            result = sb.toString();

            // Apply negative prefix only if no custom format is used
            if (isNegative) {
                return (config.getNegativePrefix().isEmpty() ? lang.getPrefix() : config.getNegativePrefix()) + " " + result;
            }
        }

        return result;
    }

    // convert English full integer (handles millions)
    private static String convertEnglishInteger(long number) {
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
    private static String convertEnglishUnderMillion(long number) {
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
    private static String convertEnglishUnderThousand(long number) {
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
    private static String convertEnglishTwoDigits(int n) {
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
