package io.github.zazalng;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThaiBahtConverterTest {
    @Test
    public void testZero() {
        assertEquals("ศูนย์บาทถ้วน", ThaiBaht.of(new BigDecimal("0")));
        assertEquals("ศูนย์บาทห้าสตางค์", ThaiBaht.of(new BigDecimal("0.05")));
    }

    @Test
    public void testSimpleBaht() {
        assertEquals("หนึ่งบาทถ้วน", ThaiBaht.of(new BigDecimal("1.00")));
        assertEquals("สิบเอ็ดบาทถ้วน", ThaiBaht.of(new BigDecimal("11.0")));
        assertEquals("ยี่สิบเอ็ดบาทถ้วน", ThaiBaht.of(new BigDecimal("21.0")));
        assertEquals("หนึ่งร้อยเอ็ดบาทถ้วน", ThaiBaht.of(new BigDecimal("101.00")));
    }

    @Test
    public void testSatang() {
        assertEquals("หนึ่งบาทหนึ่งสตางค์", ThaiBaht.of(new BigDecimal("1.01")));
        assertEquals("หนึ่งบาทยี่สิบสตางค์", ThaiBaht.of(new BigDecimal("1.20")));
        assertEquals("หนึ่งบาทสิบเอ็ดสตางค์", ThaiBaht.of(new BigDecimal("1.11")));
    }

    @Test
    public void testMillionAndLargeNumbers() {
        assertEquals("หนึ่งล้านบาทถ้วน", ThaiBaht.of(new BigDecimal("1000000")));
        assertEquals("หนึ่งล้านสองแสนสามหมื่นสี่พันห้าร้อยหกสิบเจ็ดบาทถ้วน",
                ThaiBaht.of(new BigDecimal("1234567")));
        assertEquals("สิบล้านหนึ่งบาทถ้วน", ThaiBaht.of(new BigDecimal("10000001")));
        assertEquals("หนึ่งพันหนึ่งร้อยยี่สิบเอ็ดล้านหนึ่งแสนหนึ่งหมื่นหนึ่งพันหนึ่งร้อยยี่สิบเอ็ดบาทสิบเอ็ดสตางค์", ThaiBaht.of(new BigDecimal("1121111121.11")));
    }

    @Test
    public void testNegative() {
        assertEquals("ลบหนึ่งร้อยบาทถ้วน", ThaiBaht.of(new BigDecimal("-100.00")));
    }

    @Test
    public void testTruncationOfSatang() {
// verify that more than 2 decimals are truncated (RoundingMode.DOWN)
        assertEquals("หนึ่งบาทยี่สิบสามสตางค์", ThaiBaht.of(new BigDecimal("1.239")));
    }
}
