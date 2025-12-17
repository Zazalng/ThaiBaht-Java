package io.github.zazalng.handler;

import io.github.zazalng.ThaiBaht;
import io.github.zazalng.ThaiBahtConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Internal Thai language converter for {@link ThaiBaht}.
 * <p>
 * This class is package-private and intended for internal use only.
 * It normalizes amounts to 2 decimal places using {@link RoundingMode#DOWN}
 * (truncation) before converting; negative values are prefixed with
 * the configured negative prefix.
 *
 * @see ThaiBaht
 * @see ThaiBahtConfig
 */
final class ThaiConvertHandler {
    /**
     * Default negative prefix for Thai
     */
    private static final String negativePrefix = "ลบ";
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

        StringBuilder sb = new StringBuilder();

        // handle baht integer part
        if (baht == 0) {
            sb.append(THAI_DIGITS[0]);
        } else {
            sb.append(convertThaiInteger(baht));
        }

        System.out.print(sb);

        if (config.isUseUnit()) sb.append("บาท");

        System.out.print(sb);

        if (satang == 0) {
            if (config.isUseUnit()) sb.append("ถ้วน");
        } else {
            sb.append(convertThaiTwoDigits(satang));
            if (config.isUseUnit()) sb.append("สตางค์");
        }

        System.out.print(sb);
        // negative handling
        if (normalized.signum() < 0) {
            return (config.getNegativePrefix().isEmpty() ? negativePrefix:config.getNegativePrefix()) + sb.toString();
        }
        return sb.toString();
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
