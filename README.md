# Bedtime Book Picker (Android, Java)

A local-first Android app for parents to quickly catalog children's books and randomly pick one at bedtime.

## Tech Stack
- Java, minSdk 26, targetSdk 34
- MVVM + LiveData + Executors
- Room (SQLite) for local persistence
- Jetpack Navigation + XML layouts + Material Components
- CameraX + ML Kit Barcode Scanning
- Optional Open Library ISBN lookup via OkHttp
- Google Sign-In (identity only, no Firebase/backend)

## Setup
1. Open this project in Android Studio Iguana+.
2. Let Gradle sync and install Android SDK 34.
3. Build and run on a device/emulator (camera features work best on physical device).

## Google Identity Services configuration
1. In Google Cloud Console, create an Android OAuth client.
2. Configure package name `com.bookspin`.
3. Add SHA-1 and SHA-256 fingerprints from your debug/release keys.
4. Download/configure as required by Play Services Sign-In for your project setup.
5. This app uses identity-only sign-in (display name + sign-out), and remains local-only.

## App behavior
- First launch: Login screen with **Continue with Google** or **Skip for now**.
- Skipping login still enables full local app usage.
- Signing out does not clear data.
- Settings has **Clear all local data** as an explicit action.

## Barcode scanning
- Uses CameraX preview + ML Kit barcode scanner.
- Reads ISBN-13/EAN-13 from camera stream.
- Manually entered ISBN accepts ISBN-10 and ISBN-13 validation.

## Testing scanning without physical barcodes
- Open a barcode generator website and render an ISBN-13 barcode on another screen.
- Point the device camera at the displayed barcode.
- You can also test the manual ISBN tab directly.

## Package structure
- `ui/` fragments, activity, adapters
- `viewmodel/` app ViewModels
- `data/` Room entities, DAO, database
- `repository/` data/business orchestration
- `auth/` Google sign-in helper
- `network/` optional Open Library client
- `util/` ISBN validation, prefs, random picker
