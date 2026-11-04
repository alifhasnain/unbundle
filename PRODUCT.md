# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Stack

Kotlin + Jetpack Compose with Material 3 (user's choice). Gradle with the Android Gradle Plugin; single `app` module.

## Users

The owner and their team (developers and QA). They hold Android App Bundles (`.aab`) from builds or Play Console downloads and need an installable APK on the phone in their hand, plus a quick read of what the bundle actually is (package, version name, version code) before they install or pass it on.

## Product Purpose

Turn an `.aab` into a single installable universal APK on the device itself, show the bundle's identity, then let the user save the APK or install it. Success: pick a file, wait through a conversion that can take a while without wondering whether it is stuck, read the version facts, and save or install in one tap.

## Positioning

Does on the phone what normally needs a desktop with `bundletool` and `aapt2`. No upload, no server, no desktop.

## Operating Context

- Sideloaded internal tool. It is not published on the Play Store, so `REQUEST_INSTALL_PACKAGES` and a bundled native binary are acceptable.
- The input arrives through the system file picker (Storage Access Framework), from Downloads, Drive, or chat apps. Output leaves through the system save dialog or the system package installer.
- Conversion runs bundletool (universal mode) with an on-device `aapt2` binary. Depending on bundle size and phone it takes seconds to a minute or more.

## Capabilities and Constraints

- Pick an `.aab` with the system file picker; convert it to a universal APK; show package name, version name, version code, and min/target SDK.
- Save the APK through the system save dialog; install it through the system package installer.
- The output is signed with a per-device key the app generates (any key is acceptable to the owner). It therefore cannot update a copy installed from Play; the user must uninstall that copy first.
- `aapt2` comes from ReVanced's public prebuilt releases (github.com/ReVanced/aapt2), arm64-v8a only, checksum recorded in the repo. This is a third-party binary the owner accepted.
- No network permission. Bundles stay on the device.
- Verification constraint: no emulator runs on the owner's development PC. On-device behavior must be checked on a physical phone.

## Brand Commitments

App name "Unbundle", chosen by Claude at the owner's request (delegated). Package `com.alifhasnain.unbundle`, set by the owner. No logo or other brand assets exist.

## Evidence on Hand

No screenshots, sample bundles, logos, or copy were provided. Do not invent usage numbers or claims.

## Product Principles

1. On-device and private: a bundle never leaves the phone unless the user saves or shares it.
2. Honest waiting: conversion is slow, so the app shows that work is happening and which step it is on, and never fakes a percentage it cannot measure.
3. One job, one screen: pick, convert, read, save or install. Nothing else competes for attention.
4. Exact data for engineers: version code as the integer, package name verbatim and copyable, errors that name the cause.
