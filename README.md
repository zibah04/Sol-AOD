# Sol AOD — Starter Project (v0.1.0)

This is the first Android Studio project foundation for Sol AOD.

## Included
- Kotlin + Jetpack Compose Android app
- Home screen, theme gallery, preview screen and settings
- Mechanical Edition first-pass recreation: black AMOLED-style background, oversized hour, capsule minutes, partial mechanical dial, date, uppercase weekday and SOL maker's mark
- Separate theme model and screen components so new themes can be added independently
- Future theme-package and visual-editor roadmap

## Important status
This remains a prototype, but now includes an Android DreamService screensaver engine. This is a supported no-root way to display the Sol clock on the device while the system starts its screensaver (typically while charging or docked). It is **not a replacement for Xiaomi's native lock-screen AOD** and does not promise to appear every time the phone is locked. MIUI may hide or restrict the Android screensaver settings.

The battery readout is currently a visual placeholder (`—%`); live battery integration is a next implementation step. The three additional gallery entries are design placeholders, not completed themes. Import/export and the visual editor are planned, not implemented.

## Open and build
1. Install Android Studio.
2. Clone or download this repository.
3. Open the project folder in Android Studio.
4. Allow Gradle sync. Use JDK 17.
5. Connect an Android phone with USB debugging enabled, or use an emulator.
6. Press Run.

To create an APK locally: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
Output: `app/build/outputs/apk/debug/app-debug.apk`

## Theme architecture direction
Future themes will be data-driven packages, not executable plugins. Proposed package:
- `theme.json` — metadata, layout tokens, palette and supported elements
- `preview.png` — gallery thumbnail
- `assets/` — approved images and fonts

The importer should validate package size, manifest schema, file types and asset paths. Do not execute scripts or code supplied by theme packages.

## Automatic cloud APK build
GitHub Actions builds the debug APK on pushes to `main`. Open the **Actions** tab, select the latest successful **Build Sol AOD APK** run, and download the `Sol-AOD-debug-apk` artifact. Artifacts are retained for 14 days.

## Current scope
Version 0.2.0 development adds a first Android screensaver display engine and an activation shortcut. The engine is a charging/docked screensaver fallback, not a true lock-screen AOD. MIUI device testing is still required. Theme importing/exporting, additional finished themes and the visual theme creator are not implemented yet.

Target test device: Xiaomi Redmi Note 10, MIUI Global 12.5.5.0 (RKGMIXM), Android 11.
