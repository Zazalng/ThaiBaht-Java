package io.github.zazalng;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Utility entry point for converting numeric amounts into Thai baht text.
 * <p>
 * This class provides simple static helpers to convert a {@link BigDecimal}
 * amount to Thai-language text describing baht and satang.
 * </p>
 *
 * @since 1.0
 */
public final class ThaiBaht {
    private ThaiBaht() {}

    /**
     * Convert the given amount to Thai baht text using the default configuration.
     *
     * @param amount the monetary amount to convert (baht and satang), must not be {@code null}
     * @return the Thai-language textual representation (for example: "หนึ่งร้อยบาทถ้วน")
     * @throws NullPointerException if {@code amount} is {@code null}
     */
    public static String of(BigDecimal amount) {
        return of(amount, ThaiBahtConfig.defaultConfig());
    }

    /**
     * Convert the given amount to Thai baht text using the provided configuration.
     *
     * @param amount the monetary amount to convert (baht and satang), must not be {@code null}
     * @param config formatting configuration to control inclusion of unit words and other options, must not be {@code null}
     * @return the Thai-language textual representation
     * @throws NullPointerException if {@code amount} or {@code config} is {@code null}
     */
    public static String of(BigDecimal amount, ThaiBahtConfig config) {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(config, "config must not be null");
        return ThaiTextConverter.toBahtText(amount, config);
    }
}