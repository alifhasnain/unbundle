# aapt2 (arm64-v8a)

`app/src/main/jniLibs/arm64-v8a/libaapt2.so` is the `aapt2-arm64-v8a` asset from
[ReVanced/aapt2 v1.1.0](https://github.com/ReVanced/aapt2/releases/tag/v1.1.0)
(tag commit `adb1d7acef11a67849b8a6204a824e271c4c1952`), renamed so Android extracts it as a native library.
It is a statically linked AOSP aapt2 built for Android API 30 with 16 KB ELF alignment.

- SHA-256: `7e5ae2e1f62fc24cab14072555ffd0a1a7e1ce27e82cc1008967b835b6d8df5b`.
  The `verifyAapt2` Gradle task checks this before every build.
- License: GPL-3.0 (see [LICENSE](LICENSE)). Corresponding source and build scripts:
  https://github.com/ReVanced/aapt2/tree/v1.1.0
- Unbundle runs it as a separate program (`ProcessBuilder`), not as a linked library.
