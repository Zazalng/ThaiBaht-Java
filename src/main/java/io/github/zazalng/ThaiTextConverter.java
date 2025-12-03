package io.github.zazalng;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Internal converter that transforms numeric {@link BigDecimal} amounts into
 * Thai-language text describing baht and satang.
 * <p>
 * This class is package-private and intended for internal use only. It
 * normalizes amounts to 2 decimal places using {@link RoundingMode#DOWN}
 * (truncation) before converting; negative values are prefixed with
 * the Thai word for minus ("ลบ").
 * </p>
 *
 * @implNote The conversion algorithm handles repeated "ล้าน" segments and
 * special Thai-language digit naming rules (for example: last-digit "หนึ่ง"
 * becomes "เอ็ด" in numbers greater than ten).
 */
final class ThaiTextConverter {
    private static final String[] DIGITS = {
            "ศูนย์", "หนึ่ง", "สอง", "สาม", "สี่",
            "ห้า", "หก", "เจ็ด", "แปด", "เก้า"
    };

    private ThaiTextConverter() {}

    static String toBahtText(BigDecimal amount, ThaiBahtConfig config) {
        if (amount == null) throw new IllegalArgumentException("amount must not be null");
        if (config == null) config = ThaiBahtConfig.defaultConfig();

        // normalize to 2 decimal places (satang) - truncate toward floor for positive/negative consistent behaviour
        BigDecimal normalized = amount.setScale(2, RoundingMode.DOWN);

        BigDecimal abs = normalized.abs();

        long baht = abs.longValue();
        int satang = abs.subtract(new BigDecimal(baht)).movePointRight(2).intValue();


        StringBuilder sb = new StringBuilder();

        // handle baht integer part
        if (baht == 0) {
            sb.append(DIGITS[0]);
        } else {
            sb.append(convertInteger(baht));
        }

        if (config.isUseUnit()) sb.append("บาท");

        if (satang == 0) {
            if (config.isUseUnit()) sb.append("ถ้วน");
        } else {
            sb.append(convertTwoDigits(satang));
            if (config.isUseUnit()) sb.append("สตางค์");
        }

        // negative handling
        if (normalized.signum() < 0) {
            return "ลบ" + sb.toString();
        }
        return sb.toString();
    }

    // convert full integer (handles repeating 'ล้าน' segments)
    private static String convertInteger(long number) {
        if (number == 0) return DIGITS[0];


        StringBuilder sb = new StringBuilder();
        long million = 1_000_000L;


        if (number >= million) {
            long high = number / million;
            long rem = number % million;
            sb.append(convertInteger(high)).append("ล้าน");
            if (rem > 0) {
                sb.append(convertUnderMillion(rem));
            }
        } else {
            sb.append(convertUnderMillion(number));
        }
        return sb.toString();
    }

    // convert numbers from 1..999_999
    private static String convertUnderMillion(long number) {
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
                    sb.append(DIGITS[digit]).append("สิบ");
                }
            } else if (div == 1) { // unit place special rule
                if (digit == 1 && number > 10) {
                    // last digit 1 in numbers greater than 10 becomes 'เอ็ด'
                    sb.append("เอ็ด");
                } else {
                    sb.append(DIGITS[digit]);
                }
            } else {
                sb.append(DIGITS[digit]).append(posWords[i]);
            }
        }

        return sb.toString();
    }


    // convert 1..99 for satang
    private static String convertTwoDigits(int n) {
        if (n < 0 || n > 99) throw new IllegalArgumentException("n must be between 0 and 99");
        if (n == 0) return DIGITS[0];
        if (n < 10) return DIGITS[n];

        int tens = n / 10;
        int unit = n % 10;
        StringBuilder sb = new StringBuilder();

        if (tens == 1) {
            sb.append("สิบ");
        } else if (tens == 2) {
            sb.append("ยี่สิบ");
        } else {
            sb.append(DIGITS[tens]).append("สิบ");
        }

        if (unit == 0) {
            // nothing
        } else if (unit == 1) {
            sb.append("เอ็ด");
        } else {
            sb.append(DIGITS[unit]);
        }
        return sb.toString();
    }
}