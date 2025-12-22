# 🌟 Thai Baht — Convert BigDecimal to Thai Baht Words
![Maven Central](https://img.shields.io/maven-central/v/io.github.zazalng/thai-baht.svg?label=Maven%20Central)
![Java](https://img.shields.io/badge/JDK-8%2B-green)
![License](https://img.shields.io/github/license/Zazalng/ThaiBaht-Java)
[![Javadoc](https://img.shields.io/badge/javadoc-latest-blue.svg)](https://zazalng.github.io/ThaiBaht-Java/latest/)

Effortlessly convert any Java `BigDecimal` into fully accurate and beautifully formatted **Thai Baht text** in multiple languages.  
This library is designed for **enterprise systems**, **payment processors**, **e-tax invoices**, **Thai government forms**, and any application requiring monetary wording conversion.

> **✨ v2.0.0 NEW**: Completely redesigned architecture with the pluggable `LanguageHandler` interface. You can now add **any** language without waiting for library updates or modifying core code.

---

## 🚀 Features

### ✔ Extensible Language Architecture (v2.0.0+)
- **Pluggable Interface**: The new `LanguageHandler` interface allows you to define conversion logic for any language.
- **Decoupled Design**: Languages are no longer locked to a hardcoded Enum.
- **Zero Core Modification**: Add support for Lao, Khmer, Burmese, or domain-specific jargon purely in your own code.

### ✔ Accurate Thai Baht Wording
- Correct use of Thai numerical grammar rules:
    - *"เอ็ด"* (ones in compounds like 101 → "หนึ่งร้อยเอ็ด")
    - *"ยี่"* (twenties like 20 → "ยี่สิบ")
    - *Silent "หนึ่ง"* in tens (10 → "สิบ", not "หนึ่งสิบ")
    - *Repeating "ล้าน"* for millions and beyond
- Matches official Thai government invoice conventions.

### ✔ Multi-Language Support
- **Thai** (Default) - via `ThaiLanguageHandler`
- **English** - via `EnglishLanguageHandler`
- **Custom** - via your own implementation of `LanguageHandler`

### ✔ Correct Satang Handling
- Outputs `ถ้วน` (Thai) or `Only` (English) when satang = 0.
- Precise 2-decimal place normalization using BigDecimal.

### ✔ Highly Configurable Output
- **Flexible Negative Support**: Custom prefixes (e.g., "ติดลบ", "Minus").
- **Custom Templates**: Define named placeholders like `{INTEGER}`, `{UNIT}`, `{SATANG}`.
- **Thread-Safe**: All configuration objects are immutable and safe for concurrent use.

---

## 📦 Installation

### **Maven:**
```xml
<dependency>
    <groupId>io.github.zazalng</groupId>
    <artifactId>thai-baht</artifactId>
    <version>2.0.0</version>
</dependency>
```

### **Gradle:**
```gradle
implementation 'io.github.zazalng:thai-baht:2.0.0'
```

### **Local Build:**
```bash
mvn clean install
```

---

## 🔧 Quick Start

### **One-Line Conversion (v1.4.0 Compatible)**
```java
import java.math.BigDecimal;
import io.github.zazalng.ThaiBaht;

// Thai (default)
String thai = ThaiBaht.of(new BigDecimal("4520.75"));
// → "สี่พันห้าร้อยยี่สิบบาทเจ็ดสิบห้าสตางค์"
```

### **v2.0.0: Using Built-in Handlers (Recommended)**
```java
import java.math.BigDecimal;
import io.github.zazalng.ThaiBaht;
import io.github.zazalng.handler.ThaiLanguageHandler;

// Explicit handler syntax (v2.0.0+)
ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler())
    .useUnit(true)
    .build();

String text = ThaiBaht.of(new BigDecimal("100.50"), config);
// → "หนึ่งร้อยห้าสิบสตางค์"
```

### **v2.0.0: Adding Custom Languages (NEW!)**
```java
import io.github.zazalng.contracts.LanguageHandler;

// Create your own language handler - no core changes needed!
public class LaotianLanguageHandler implements LanguageHandler {
    @Override
    public String convert(ThaiBaht baht) { /* your logic */ }
    
    @Override
    public String getLanguageCode() { return "lo"; }
    
    @Override
    public String getLanguageName() { return "Laotian"; }
    
    @Override
    public String getUnitWord() { return "ກີບ"; }
    
    @Override
    public String getExactWord() { return "ເທົ່າ"; }
    
    @Override
    public String getSatangWord() { return "ແອັດ"; }
    
    @Override
    public String getNegativePrefix() { return "ລົບ"; }
}

// Use immediately - no core library changes!
ThaiBahtConfig config = ThaiBahtConfig.builder(new LaotianLanguageHandler())
    .useUnit(true)
    .build();
```

### **Instance-Based API**
```java
ThaiBaht converter = ThaiBaht.create(new BigDecimal("101.01"));
System.out.println(converter);
// → "หนึ่งร้อยเอ็ดบาทหนึ่งสตางค์"

// Chaining support
String result = converter
    .setAmount(new BigDecimal("500.50"))
    .toString();
```

### **English Output**
```java
import io.github.zazalng.handler.EnglishLanguageHandler;

ThaiBahtConfig config = ThaiBahtConfig.builder(new EnglishLanguageHandler())
    .useUnit(true)
    .build();

String english = ThaiBaht.of(new BigDecimal("525.50"), config);
// → "Five Hundred Twenty-Five Baht Fifty Satang"
```

### **Custom Configuration**
```java
import io.github.zazalng.handler.ThaiLanguageHandler;

ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler())
    .useUnit(true)
    .setPrefix("ติดลบ")  // Custom negative prefix
    .build();

String negative = ThaiBaht.of(new BigDecimal("-100.50"), config);
// → "ติดลบหนึ่งร้อยบาทห้าสิบสตางค์"
```

### **Custom Format Templates (v1.4.0+)**
```java
ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler())
    .setFormatTemplate("{INTEGER}{UNIT}{EXACT}{FLOAT?{FLOAT}{SATANG}}")
    .build();

String formatted = ThaiBaht.of(new BigDecimal("100.50"), config);
// → "หนึ่งร้อยบาทห้าสิบสตางค์"
```

### **Large Numbers**
```java
String large = ThaiBaht.of(new BigDecimal("1250000000.50"));
// → "หนึ่งพันสองร้อยห้าสิบล้านบาทห้าสิบสตางค์"
```

---

## ⚡ v2.0.0 Breaking Changes & Migration

### **What Changed?**
Version 2.0.0 replaces the **Language enum-based system** with a **pluggable LanguageHandler interface**. This enables unlimited language extensibility without modifying core code.

| Aspect | v1.4.0 | v2.0.0 |
|--------|--------|--------|
| Language Selection | `Language` enum | `LanguageHandler` interface |
| Extensibility | Limited (enum) | Unlimited (handlers) |
| Adding Languages | Modify core | Create handler only |
| Constructor | Direct | Builder pattern only |
| Backward Compat | N/A | Soft (builders work) |

### **Breaking Changes**

1. **Constructor Signature Changed**
   ```java
   // ❌ v1.4.0 style - won't compile
   ThaiBahtConfig config = new ThaiBahtConfig(Language.THAI, true, true, null, null, null);
   
   // ✅ v2.0.0 requires builder pattern
   ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler())
       .useUnit(true)
       .build();
   ```

2. **Factory Method Signature Changed**
   ```java
   // ⚠️ Soft break - still works via backward compatibility
   ThaiBahtConfig config = ThaiBahtConfig.builder(Language.THAI)
       .useUnit(true)
       .build();
   // Internally creates ThaiLanguageHandler automatically
   ```

### **Migration Path**

#### **For v1.4.0 Users → v2.0.0**

**Option 1: No Changes Required (Soft Compatibility)**
```java
// v1.4.0 code - still works in v2.0.0
ThaiBahtConfig config = ThaiBahtConfig.builder(Language.THAI)
    .useUnit(true)
    .build();

// Behind the scenes: 
// Language.THAI is automatically converted to new ThaiLanguageHandler()
```

**Option 2: Recommended - Use New Handler Syntax**
```java
// v2.0.0 recommended approach
import io.github.zazalng.handler.ThaiLanguageHandler;

ThaiBahtConfig config = ThaiBahtConfig.builder(new ThaiLanguageHandler())
    .useUnit(true)
    .build();
```

#### **For New v2.0.0 Projects**

Use the new handler-based syntax for clarity:
```java
// Clear and explicit
ThaiBahtConfig thai = ThaiBahtConfig.builder(new ThaiLanguageHandler())
    .useUnit(true)
    .build();

ThaiBahtConfig english = ThaiBahtConfig.builder(new EnglishLanguageHandler())
    .useUnit(true)
    .build();
```

### **Why Break Backward Compatibility?**

The Language enum architecture had a fundamental limitation: **you couldn't add new languages without modifying core code**. Version 2.0.0 solves this with the `LanguageHandler` interface, which is the proper long-term solution.

See [IMPLEMENTATION_SUMMARY_v2.0.0.md](IMPLEMENTATION_SUMMARY_v2.0.0.md) for complete details.

### **LTS for Version 1.x.x?**

As enum base maintainer and community can still provide their local logic into version 1.x.x because what it needs to be add is what it needs to be created.

e.g. I want to add Japan support language, **action :** modify Language enum -> create Logic.java -> modify switcher in TextConverter.java -> push up

---

## 📚 Documentation

### Comprehensive Javadocs
Full API documentation with detailed explanations, usage examples, and design patterns:

- **[ThaiBaht](src/main/java/io/github/zazalng/ThaiBaht.java)** - Main conversion API
- **[ThaiBahtConfig](src/main/java/io/github/zazalng/ThaiBahtConfig.java)** - Configuration builder
- **[LanguageHandler](src/main/java/io/github/zazalng/contracts/LanguageHandler.java)** - Language handler interface (v2.0.0+)
- **[Language](src/main/java/io/github/zazalng/contracts/Language.java)** - Supported languages (backward compat enum)
- **[FormatTemplate](src/main/java/io/github/zazalng/utils/FormatTemplate.java)** - Custom format support (v1.4.0+)

### Key Classes

| Class | Purpose |
|-------|---------|
| `ThaiBaht` | Main conversion entry point (static & instance API) |
| `ThaiBahtConfig` | Immutable configuration for controlling output |
| `ThaiBahtConfig.Builder` | Fluent builder for constructing configurations |
| `Language` | Enum for supported languages (THAI, ENGLISH) |
| `FormatTemplate` | Custom format strings with named placeholders |

---

## 🎯 Configuration Options

### Language
```java
.language(Language.THAI)      // Default
.language(Language.ENGLISH)   // English output
```

### Unit Words
```java
.useUnit(true)   // Include "บาท", "สตางค์" (default)
.useUnit(false)  // Numeric text only
```

### Negative Prefix
```java
.setPrefix("ลบ")          // Thai default
.setPrefix("Minus")       // English default
.setPrefix("ติดลบ")        // Custom Thai
.setPrefix("Negative:")    // Custom English
```

### Formal Mode
```java
.formal(true)   // Use formal rules (default, reserved for future)
.formal(false)  // Casual rules (future use)
```

### Format Templates (v1.4.0+)
```java
// Basic format
.setFormatTemplate("{INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}}")

// With conditional satang
.setFormatTemplate("{INTEGER}{UNIT}{EXACT}{FLOAT?{FLOAT}{SATANG}}")

// Negative format
.setNegativeFormatTemplate("({NEGPREFIX} {INTEGER}{UNIT}{FLOAT?{FLOAT}{SATANG}})")
```

#### Supported Placeholders
- `{INTEGER}` - Baht (integer) part
- `{UNIT}` - Currency unit
- `{EXACT}` - Exact indicator (ถ้วน/Only)
- `{FLOAT}` - Satang (fractional) part
- `{SATANG}` - Satang unit
- `{NEGPREFIX}` - Negative prefix
- `{FLOAT?content}` - Show content only if satang ≠ 0
- `{SATANG?content}` - Show unit only if satang ≠ 0

---

## 🧪 Testing

Comprehensive test suite covering:
- Standard integers and decimals
- Edge cases (0, 11, 21, 101, multi-million values)
- Negative values with various prefixes
- Satang formatting and rounding
- Multi-language conversions
- Custom format template application

Run tests:
```bash
mvn test
```

**Test Results:** 47 passing tests across all versions (v1.0.0 through v1.4.0)

---

## 🏗️ Project Structure
```
src/main/java/io/github/zazalng/
├── ThaiBaht.java                    # Main public API
├── ThaiBahtConfig.java              # Configuration builder
├── contracts/
│   ├── Language.java                # Supported languages enum
│   └── LanguageHandler.java         # Interface languages control
├── handler/                         # Internal implementation
│   ├── TextConverter.java           # Conversion router
│   ├── ThaiConvertHandler.java      # Thai conversion logic (Old)
│   ├── ThaiLanguageHandler.java     # Thai conversion logic (New)
│   ├── EnglishConvertHandler.java   # English conversion logic (Old)
│   ├── EnglishLanguageHandler.java  # English conversion logic (New)
│   ├── FormatApplier.java           # Custom format processor
│   └── package-info.java            # Handler package docs
├── utils/
│   ├── FormatTemplate.java          # Format template wrapper
│   └── package-info.java            # Utils package docs
└── package-info.java                # Main package docs

src/test/java/io/github/zazalng/v1/
├── v1_0_0Test.java                  # v1.0.0 compatibility
├── v1_2_0Test.java                  # v1.2.0 features
├── v1_3_0Test.java                  # v1.3.0 multi-language
└── v1_4_0Test.java                  # v1.4.0 format templates
```

---

## 📋 Version History

### v2.0.0 - Language Interface (Selfish Relay)
- ✨ **New**: `LanguageHandler` interface for unlimited language extensibility
- ✨ **New**: `ThaiLanguageHandler` and `EnglishLanguageHandler` implementations
- ✨ **Breaking**: Constructor signature changed (builder pattern enforced)
- ✨ **Benefit**: Zero enum coupling - add ANY language without core modifications
- ✨ **Compat**: v1.4.0 code still works via soft backward compatibility

### v1.4.0 - Custom Format Template
- ✨ Custom format templates with named placeholders
- ✨ Conditional placeholder support
- ✨ Separate positive/negative format templates

### v1.3.0 - Multi-Language (Community Relay)
- ✨ Multi-language support (Thai + English)
- ✨ Language-specific default prefixes
- ✨ Auto-updating prefix behavior

### v1.2.0 - Custom Config
- ✨ Improved number handling
- 🐛 Various bug fixes

### v1.0.0 - Initial Release
- Initial release
- Thai Baht conversion

---

## 💡 Usage Examples by Scenario

### Enterprise Invoice Generation
```java
ThaiBahtConfig config = ThaiBahtConfig.builder()
    .language(Language.THAI)
    .useUnit(true)
    .formal(true)
    .build();

BigDecimal invoiceAmount = new BigDecimal("5250.75");
String thaiText = ThaiBaht.of(invoiceAmount, config);
```

### Multi-Currency Reports
```java
ThaiBahtConfig thai = ThaiBahtConfig.builder(Language.THAI).build();
ThaiBahtConfig english = ThaiBahtConfig.builder(Language.ENGLISH).build();

BigDecimal amount = new BigDecimal("1000.00");
String thaiVersion = ThaiBaht.of(amount, thai);
String englishVersion = ThaiBaht.of(amount, english);
```

### Government Forms with Custom Formatting
```java
ThaiBahtConfig config = ThaiBahtConfig.builder()
    .setFormatTemplate("{INTEGER}{UNIT}{FLOAT?และ{FLOAT}{SATANG}}")
    .build();

String formattedAmount = ThaiBaht.of(new BigDecimal("500.50"), config);
// Output: "ห้าร้อยบาทและห้าสิบสตางค์"
```

### Negative Amount Handling
```java
ThaiBahtConfig config = ThaiBahtConfig.builder()
    .setPrefix("(ลบ)")
    .setNegativeFormatTemplate("({NEGPREFIX}{INTEGER}{UNIT})")
    .build();

String negative = ThaiBaht.of(new BigDecimal("-100.00"), config);
// Output: "((ลบ)หนึ่งร้อยบาท)"
```

---

## 🤝 Contributing

We welcome contributions! Areas for enhancement:
- Additional language implementations
- Performance optimizations
- Extended locale support
- More format template examples
- Enhanced test coverage

Please submit pull requests with:
- Clear description of changes
- Unit tests for new features
- Updated documentation
- Backward compatibility verification

---

## 📜 License
Apache License 2.0 — free for personal and commercial use.

See [LICENSE](LICENSE) for details.

---

## ⚙️ Technical Details

### Performance
- **Time Complexity:** O(log n) where n is the magnitude
- **Space Complexity:** O(log n) for output string length
- **No external dependencies:** Lightweight and fast

### Precision
- Uses `java.math.BigDecimal` for accurate monetary arithmetic
- Normalizes to 2 decimal places (satang precision)
- Uses `RoundingMode.DOWN` for truncation (not rounding)

### Thread Safety
- All configuration objects are immutable
- Conversion process is stateless
- Safe for concurrent use across threads
- No synchronization overhead needed

---

## ❓ FAQ

**Q: Does this handle very large amounts?**  
A: Yes! The library supports amounts up to Java's `BigDecimal` limits (billions and beyond).

**Q: Can I use this in production?**  
A: Absolutely! The library is designed for enterprise systems with comprehensive test coverage.

**Q: Is there support for other languages?**  
A: Currently Thai and English are supported. The architecture is designed for easy extension to other languages.

**Q: What about negative amounts?**  
A: Fully supported with customizable prefixes for each language.

**Q: Can I customize the output format?**  
A: Yes! v1.4.0+ supports custom format templates with named placeholders.

**Q: Will major version '2.0.0+' carry on legacy language enum base from v1.x.x logic by community?**  
A: No, Major update for 2.0.0+ build for selfish dev to maintain lightweight. (I mean if You want just Thai and English why do I have to spare resource for other 95+ language that I won't use it anyway?)

---

## 💬 Support

- 📖 Read the [comprehensive javadocs](https://zazalng.github.io/ThaiBaht-Java/latest/)
- 🐛 Report issues on GitHub
- 💬 Discuss on the project wiki
- ❤️ Star the repository if you find it useful!

---

## ✨ Author
**Zazalng** — Stubid Java Developer Fsian