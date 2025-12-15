# 🌟 Thai Baht — Convert BigDecimal to Thai Baht Words
![Maven Central](https://img.shields.io/maven-central/v/io.github.zazalng/thai-baht.svg?label=Maven%20Central)
![Java](https://img.shields.io/badge/JDK-8%2B-green)
![License](https://img.shields.io/github/license/Zazalng/ThaiBaht-Java)
[![Javadoc](https://img.shields.io/badge/javadoc-latest-blue.svg)](https://zazalng.github.io/ThaiBaht-Java/latest/)

Effortlessly convert any Java `BigDecimal` into fully accurate and beautifully formatted **Thai Baht text**.  
This library is designed for **enterprise systems**, **payment processors**, **e-tax invoices**, **Thai government forms**, and any application requiring Thai monetary wording.

---

## 🚀 Features

### ✔ Accurate Thai Baht wording
- Correct use of Thai numerical grammar:  
  *“เอ็ด”, “ยี่”, silent “หนึ่ง” in tens, repeated “ล้าน” groups, etc.*
- Matches official government invoice conventions.

### ✔ Correct Satang Handling
- Outputs `ถ้วน` when satang = 0
- Reads satang normally when decimals exist

### ✔ Negative number support
- Adds prefix `ลบ` for negative values
- Enhance custom prefix for negative values though config object

### ✔ Configurable output
- Include/omit currency unit (`บาท`, `สตางค์`)
- Future expansion: formal Thai, English, dialect variations

### ✔ Pure Java — No dependencies
Minimal, lightweight, and works on **Java 8+**.

---

## 📦 Installation

### **Maven:**
```xml
<dependency>
    <groupId>io.github.zazalng</groupId>
    <artifactId>thai-baht</artifactId>
    <version>1.2.0</version>
</dependency>
```

### **Local build:**
```bash
mvn clean install
```

---

## 🔧 Usage

### **One‑line API**
```java
String text = ThaiBaht.of(new BigDecimal("4520.75"));
// → "สี่พันห้าร้อยยี่สิบบาทเจ็ดสิบห้าสตางค์"
```

### **Instance API**
```java
ThaiBaht obj = ThaiBaht.create(new BigDecimal("101.01"));
System.out.print(obj); // → "หนึ่งร้อยเอ็ดบาทหนึ่งสตางค์"
```

### **Using Config**
```java
ThaiBahtConfig config = ThaiBahtConfig.builder()
        .includeUnit(true)
        .build();

String text = ThaiBaht.of(new BigDecimal("100.00"), config);
// → "หนึ่งร้อยบาทถ้วน"
```

### **Large numbers supported**
```java
ThaiBaht.of(new BigDecimal("1250000000.50"));
// → "หนึ่งพันสองร้อยห้าสิบล้านบาทห้าสิบสตางค์"
```

---

## 🧪 Unit Tests
Includes JUnit 5 coverage for:
- Standard integers and decimals
- Edge cases (`0`, `11`, `21`, `101`, multi‑million values)
- Negative values
- Satang formatting behavior

Run tests:
```bash
mvn test
```

---

## 🏗 Project Structure
```
src/
 └── main/java/io/github/zazalng/
       ├── ThaiBaht.java
       ├── ThaiTextConverter.java
       └── ThaiBahtConfig.java

src/
 └── test/java/io/github/zazalng/
       └── ThaiBahtConverterTest.java
```

---

## 📜 License
Apache License 2.0 — free for personal and commercial use.

---

## ❤️ Contributing
Pull requests are welcome!  
Add features, expand dialect support, improve formatting, or enhance unit tests.

---

## ✨ Author
**Zazalng** — Stupid Java Developer Fsian
