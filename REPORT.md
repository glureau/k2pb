# K2PB Audit Report

**Date:** April 19, 2026

Legend:
- **CONFIRMED (2x)** — Found independently in both analyses and verified still present in code
- **CONFIRMED (1x)** — Found in one analysis only, but verified still present in code
- **FIXED** — Was reported but has since been fixed in the codebase

---

## CONFIRMED Bugs (2x) — High Confidence

These issues were independently identified by both analyses and are still present.

### 1. Enum codec uses ordinal instead of proto field numbers
**File:** `k2pb-compiler/.../codegen/generateEnumCodecType.kt:13`
**Priority:** P0 — Data corruption when enums have non-contiguous proto numbers.

### 2. `readString` missing bounds check
**File:** `k2pb-runtime/.../ktx/ByteArrayInput.kt:42-45`
**Priority:** P0 — Can read past logical buffer boundary into adjacent data.

### 3. Boolean writeMethod hardcodes `writeInt(1, ...)`
**File:** `k2pb-compiler/.../struct/ScalarFieldType.kt:154`
**Priority:** P0 — `false` values in maps are encoded as `true`. ANALYSIS.md focused on maps, REPORT.md also flagged lists/sets; verified that lists/sets use `writeMethodNoTag` (correct), so the bug is **map-values only**.

### 4. `readIntLittleEndian` / `readLongLittleEndian` don't check for EOF
**File:** `k2pb-runtime/.../ktx/ProtobufReaderImpl.kt:126-144`
**Priority:** P0 — Truncated streams silently produce corrupt fixed32/fixed64/float/double values.

### 5. Nested collections silently drop data
**Files:** `k2pb-compiler/.../struct/ListType.kt:57-59`, `SetType.kt:57-59`
**Priority:** P0 — `List<List<Int>>` etc. emit only a comment, data silently lost.

### 6. `commonPrefixWith` produces malformed package names
**File:** `k2pb-compiler/.../ProtobufCodecProducer.kt:40`
**Priority:** P1 — Character-level prefix instead of package-segment prefix.

### 7. Map decoding assumes key-before-value ordering
**File:** `k2pb-compiler/.../struct/MapType.kt:64-85`
**Priority:** P1 — Protobuf spec does not guarantee field order within a message.

### 8. Map key enum uses ordinal instead of proto number
**File:** `k2pb-compiler/.../struct/MapType.kt:28-29`
**Priority:** P1 — Same ordinal issue as #1 but for map keys.

### 9. `checkLength` has no upper bound validation
**File:** `k2pb-runtime/.../ktx/ProtobufReaderImpl.kt:168-172`
**Priority:** P1 — Corrupted length of `Int.MAX_VALUE` causes OOM.

### 10. Varint slow path doesn't detect EOF
**File:** `k2pb-runtime/.../ktx/ByteArrayInput.kt:104-130`
**Priority:** P1 — EOF (-1) interpreted as continuation byte 0x7F/0xFF.

### 11. Nullable map fields cause NPE in generated code
**File:** `k2pb-compiler/.../struct/MapType.kt:23`
**Priority:** P1 — No null check unlike ListType/SetType.

### 12. No proto field number range validation
**File:** `k2pb-compiler/.../struct/NumberManager.kt`
**Priority:** P1 — No check for max (536870911), reserved range (19000-19999), or minimum (1).

### 13. `Thread.sleep(3000)` left in compiler code
**File:** `k2pb-compiler/.../mapping/Mapping.kt:116`
**Priority:** P2 — Debug artifact blocking KSP compiler thread.

### 14. Static mutable state leaks across Gradle daemon builds
**Files:** `K2PBCompiler.kt:30` (`sharedLogger`), `TypeResolver.kt:8` (mutable map)
**Priority:** P2 — Stale data persists in long-running Gradle daemons. Note: `OneOfRecorder.kt` has been removed.

### 15. Missing wire types 3 and 4 (deprecated groups)
**File:** `k2pb-runtime/.../ktx/ProtoWireType.kt`
**Priority:** P2 — Breaks forward compatibility with proto2-encoded data.

### 16. No recursion depth limit
**File:** `k2pb-runtime/.../runtime/protobufWriter.kt`
**Priority:** P2 — Deeply nested messages cause stack overflow.

### 17. Force-unwrap on class resolution
**File:** `k2pb-compiler/.../K2PBCompiler.kt:146`
**Priority:** P2 — Crashes with NPE instead of helpful error.

### 18. `readMessage` allocates unnecessary copy
**File:** `k2pb-runtime/.../runtime/protobufWriter.kt:23`
**Priority:** P2 — Could use `slice()` instead of `readByteArray()`.

### 19. `writeMessage` double-copies message body
**File:** `k2pb-runtime/.../runtime/protobufWriter.kt:17-19`
**Priority:** P2 — Two copies of every nested message.

### 20. Inconsistent `ProtoWireType` naming
**File:** `k2pb-runtime/.../ktx/ProtoWireType.kt`
**Priority:** P3 — `VARINT`, `SIZE_DELIMITED` vs `I64`, `I32`.

### 21. Duplicated platform `Bytes.kt`
**Priority:** P3 — jsMain/appleMain identical; jvmMain/androidMain identical.

### 22. `readVarint64` returns `-1L` as EOF sentinel
**File:** `k2pb-runtime/.../ktx/ByteArrayInput.kt:77-81`
**Priority:** P3 — `-1L` is a valid varint value, fragile API.

### 23. Zero unit tests for core modules
**Priority:** P3 — All testing is end-to-end via sample-app only.

---

## CONFIRMED Bugs (1x) — Single Source, Verified Present

These issues were flagged by only one analysis but verified still present.

### 24. `encodeVarint32` buffer overrun for negative integers (ANALYSIS only)
**File:** `k2pb-runtime/.../ktx/ByteArrayOutput.kt:81`
**Priority:** P0 — `ensureCapacity(5)` but negative int32 needs 10 bytes of varint encoding.

### 25. Variable named `"oot"` in generated code (REPORT only)
**File:** `k2pb-compiler/.../poet/generateInlineSerializer.kt:65`
**Priority:** P3 — Unclear generated variable name, likely meant to be `"out"`.

### 26. Mutable state on public `ProtobufReader` interface (REPORT only)
**File:** `k2pb-annotations/.../ProtobufReader.kt:4-6`
**Priority:** P3 — `var currentId`, `var pushBack`, `var pushBackHeader` exposed publicly.

### 27. `Logger.warn` used for debug output (REPORT only)
**File:** `k2pb-compiler/.../ProtobufFileProducer.kt:47`
**Priority:** P3 — `Logger.warn("imports for ...")` is debug-level.

### 28. Enum proto3 zero-value requirement not enforced (REPORT only)
**File:** `k2pb-compiler/.../mapping/mapEnumNode.kt`
**Priority:** P1 — Proto3 requires first enum value to be 0.

### 29. No `allow_alias` for duplicate enum numbers (REPORT only)
**Priority:** P2 — Duplicate `@ProtoField` numbers produce invalid .proto files.

### 30. Excessive `TODO()` calls that throw at runtime (both, but different framing)
**Priority:** P2 — 30+ `TODO()` on reachable code paths throw `NotImplementedError`.

---

## FIXED — No Longer Relevant

These issues were reported but have been fixed in the current codebase:

| # | Issue | Status |
|---|-------|--------|
| F1 | Double `return` keyword in `nameOrDefault.kt` | **FIXED** — single return now |
| F2 | Swallowed `FileAlreadyExistsException` in `KspExt.kt` | **FIXED** — now logged via `Logger.warn` |
| F3 | `println` for logging in `K2PB.kt` | **FIXED** — removed |
| F4 | Inconsistent `Locale` in `ClassNameToOneOfField.kt` | **FIXED** — both use `Locale.US` now |
| F5 | `SerializationException` extends `IllegalArgumentException` | **FIXED** — now extends `RuntimeException` |
| F6 | Non-thread-safe `indentsCache` | **FIXED** — uses `ConcurrentHashMap` |
| F7 | Dead code (`OneOfRecorder`, `OptionManager`, `registerConverter`) | **FIXED** — all removed |
| F8 | Computed value discarded in `appendFields.kt` | **FIXED** — value now used via `appendComment` |

---

## Summary

| Category | Count |
|----------|-------|
| **Confirmed (2x)** — both analyses, still present | 23 |
| **Confirmed (1x)** — one analysis, verified present | 7 |
| **Fixed** — no longer relevant | 8 |

### By Priority (active issues only)

| Priority | Count | Examples |
|----------|-------|---------|
| **P0 — Critical** | 6 | Buffer overrun, enum ordinal, boolean maps, EOF handling, nested collections, readString bounds |
| **P1 — High** | 7 | Package prefix, map ordering, nullable maps, field number validation, enum zero-value |
| **P2 — Medium** | 8 | Thread.sleep, static state, recursion limit, allow_alias, TODO() calls |
| **P3 — Low** | 9 | Wire type naming, duplicated files, oot variable, mutable interface, varint sentinel |
