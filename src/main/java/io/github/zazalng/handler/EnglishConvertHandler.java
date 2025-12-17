package io.github.zazalng.handler;

import io.github.zazalng.ThaiBaht;
import io.github.zazalng.ThaiBahtConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Internal English language converter for {@link ThaiBaht}.
 * <p>
 * This class is package-private and intended for internal use only.
 * It normalizes amounts to 2 decimal places using {@link RoundingMode#DOWN}
 * (truncation) before converting; negative values are prefixed with
 * the configured negative prefix.
 *
 * @see ThaiBaht
 * @see ThaiBahtConfig
 */
final class EnglishConvertHandler {
    /**
     * Default negative prefix for English
     */
    private static final String negativePrefix = "Minus";
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

        StringBuilder sb = new StringBuilder();

        // handle baht integer part
        if (baht == 0) {
            sb.append(ENGLISH_DIGITS[0]);
        } else {
            sb.append(convertEnglishInteger(baht));
        }

        if (config.isUseUnit()) sb.append(" Baht");

        if (satang == 0) {
            if (config.isUseUnit()) sb.append(" Only");
        } else {
            sb.append(" ");
            sb.append(convertEnglishTwoDigits(satang));
            if (config.isUseUnit()) sb.append(" Satang");
        }

        // negative handling
        if (normalized.signum() < 0) {
            return (config.getNegativePrefix().isEmpty() ? negativePrefix:config.getNegativePrefix()) + " " + sb.toString();
        }
        return sb.toString();
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
