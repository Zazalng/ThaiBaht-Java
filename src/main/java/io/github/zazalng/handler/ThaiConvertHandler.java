package io.github.zazalng.handler;

import io.github.zazalng.ThaiBaht;
import io.github.zazalng.ThaiBahtConfig;
import io.github.zazalng.contracts.Language;
import io.github.zazalng.utils.FormatTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Internal Thai language converter for converting numeric amounts to Thai Baht text.
 * <p>
 * This package-private handler implements the specialized logic for converting
 * {@link BigDecimal} amounts to their Thai textual representation. It is not intended
 * for direct use; instead, access through the public {@link ThaiBaht} API.
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
 * <h2>Conversion Process</h2>
 * <p>
 * The conversion follows these high-level steps:
 * <ol>
 *   <li>Normalize amount to 2 decimal places using {@link java.math.RoundingMode#DOWN}</li>
 *   <li>Extract absolute value to handle magnitude separately</li>
 *   <li>Split into integer (baht) and fractional (satang) parts</li>
 *   <li>Convert each part using specialized Thai digit conversion methods</li>
 *   <li>Apply configuration options (unit words, custom format)</li>
 *   <li>Handle negative prefix if amount is negative</li>
 * </ol>
 *
 * <h2>Number Conversion Methods</h2>
 * <p>
 * The converter uses specialized methods for different numeric ranges:
 * <ul>
 *   <li><strong>convertThaiInteger():</strong> Handles full integers including millions
 *       (recursively handles millions and remainder)</li>
 *   <li><strong>convertThaiUnderMillion():</strong> Handles 1-999,999 using positional system
 *       (แสน, หมื่น, พัน, ร้อย, สิบ, หน่วย)</li>
 *   <li><strong>convertThaiTwoDigits():</strong> Handles 1-99 for satang conversion (special handling for tens)</li>
 * </ul>
 *
 * <h2>Satang Handling</h2>
 * <p>
 * Satang (fractional part) is handled as follows:
 * <ul>
 *   <li>When satang = 0: Output "ถ้วน" (Only/Exact) if units are enabled</li>
 *   <li>When satang > 0: Convert using specialized two-digit converter and append "สตางค์"</li>
 *   <li>Uses {@link java.math.RoundingMode#DOWN} for truncation (100.999 → 100.99)</li>
 * </ul>
 *
 * <h2>Configuration Support</h2>
 * <p>
 * This handler respects all {@link ThaiBahtConfig} options:
 * <ul>
 *   <li>Unit words inclusion (บาท, สตางค์, ถ้วน)</li>
 *   <li>Custom format templates via {@link FormatApplier}</li>
 *   <li>Negative prefix configuration (defaults to "ลบ" for Thai)</li>
 *   <li>Formal wording mode (reserved for future use)</li>
 * </ul>
 *
 * <h2>Examples</h2>
 * <p>
 * <strong>Thai conversion examples:</strong>
 * <pre>{@code
 * // With units enabled (default)
 * "100.00"  → "หนึ่งร้อยบาทถ้วน"     (hundred baht exact)
 * "101.01"  → "หนึ่งร้อยเอ็ดบาทหนึ่งสตางค์"  (hundred one baht one satang)
 * "1234.56" → "หนึ่งพันสองร้อยสามสิบสี่บาทห้าสิบหกสตางค์"
 *
 * // Without units
 * "100.00"  → "หนึ่งร้อย"
 * "1000.50" → "หนึ่งพันห้าสิบ"
 * }</pre>
 *
 * @implNote This class is package-private and stateless. It uses static methods only.
 *           The conversion algorithm maintains no mutable state and is thread-safe.
 * @see ThaiBaht
 * @see ThaiBahtConfig
 * @see TextConverter
 * @see FormatApplier
 * @author Zazalng
 * @since 1.0
 * @version 1.4.0
 */
public final class ThaiConvertHandler {
    /**
     * Default negative prefix for Thai
     */
    private static final String[] THAI_DIGITS = {
            "ศูนย์", "หนึ่ง", "สอง", "สาม", "สี่",
            "ห้า", "หก", "เจ็ด", "แปด", "เก้า"
    };

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ThaiConvertHandler() {}

    static String convert(ThaiBaht baht){
        return convertToThai(baht.getAmount(), baht.getConfig());
    }

    private static String convertToThai(BigDecimal amount, ThaiBahtConfig config) {
        // normalize to 2 decimal places (satang) - truncate toward floor for positive/negative consistent behaviour
        BigDecimal normalized = amount.setScale(2, RoundingMode.DOWN);

        BigDecimal abs = normalized.abs();

        long baht = abs.longValue();
        int satang = abs.subtract(new BigDecimal(baht)).movePointRight(2).intValue();
        Language lang = config.getLanguage();

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
            String negPrefix = config.getNegativePrefix().isEmpty() ? lang.getPrefix() : config.getNegativePrefix();
            result = FormatApplier.apply(template, bahtText, satangText, satang, lang, negPrefix, THAI_DIGITS[0]);
        } else {
            // Use standard formatting
            StringBuilder sb = new StringBuilder();

            if (config.isUseUnit()) {
                sb.append(bahtText).append(lang.getUnit());
            } else {
                sb.append(bahtText);
            }

            if (satang == 0) {
                if (config.isUseUnit()) sb.append(lang.getExact());
            } else {
                sb.append(satangText);
                if (config.isUseUnit()) sb.append(lang.getSatang());
            }

            result = sb.toString();

            // Apply negative prefix only if no custom format is used
            if (isNegative) {
                return (config.getNegativePrefix().isEmpty() ? lang.getPrefix() : config.getNegativePrefix()) + result;
            }
        }

        return result;
    }

    // convert Thai full integer (handles repeating 'ล้าน' segments)
    private static String convertThaiInteger(long number) {
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
    private static String convertThaiUnderMillion(long number) {
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
    private static String convertThaiTwoDigits(int n) {
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
