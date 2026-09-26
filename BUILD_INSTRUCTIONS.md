# Build Instructions for Autonomous Android AI Agent

Follow these step-by-step instructions to open, configure, and build a signed release APK in Android Studio:

## 1. Prerequisites
- Install **Android Studio Ladybug** (or latest stable version).
- Ensure JDK 17 or 21 is installed.
- Android SDK 36 (compileSdk) and Build-Tools installed.

## 2. Opening the Project
1. Open Android Studio.
2. Select **Open** and choose the root directory of this project (`Autonomous Agent`).
3. Allow Gradle to sync dependencies automatically.

## 3. Configuring Secrets & API Keys
1. Create a `.env` file in the root directory (or use AI Studio Secrets panel).
2. Add your Gemini API key:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
3. The project uses the Secrets Gradle Plugin to inject this key into `BuildConfig.GEMINI_API_KEY` at build time.

## 4. Building a Signed Release APK
1. In Android Studio, go to **Build > Generate Signed Bundle / APK...**.
2. Select **APK** and click **Next**.
3. Create or select your upload keystore (`my-upload-key.jks`).
4. Enter your keystore password, key alias, and key password.
5. Select **release** build variant and check **V1 (Jar Signature)** and **V2 (Full APK Signature)**.
6. Click **Finish**. Android Studio will compile and output the signed release APK in `app/release/app-release.apk`.

## 5. Running Tests
- To run unit tests locally via JVM/Robolectric:
  ```bash
  gradle :app:testDebugUnitTest
  ```
