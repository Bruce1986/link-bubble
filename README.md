# Brave for Android (formerly Link Bubble)

## Install instructions and setup

`git clone git@github.com:brave/browser-android.git`

If you wish to enable Crashlytics, copy `Application/LinkBubble/fabric.properties.template` to `Application/LinkBubble/fabric.properties` and fill in the apiSecret.

Copy `Application/LinkBubble/src/main/java/com/linkbubble/ConfigAPIs.java.template` to `Application/LinkBubble/src/main/java/com/linkbubble/ConfigAPIs.java` and fill in the youtube apiSecret.

Copy `Application/LinkBubble/src/main/AndroidManifest.xml.template` to `Application/LinkBubble/src/main/AndroidManifest.xml` and, if Crashlytics is used, fill in `com.crashlytics.ApiKey` and
`io.fabric.ApiKey` with your Crashlytics API key. You can obtain it from logging into your Fabric account and going to: `Settings -> Organizations -> Brave (or your organization)` then click on `API Key` at the top.

npm install

### Java Version
The project requires JDK 17 for Gradle builds. Set your default Java to version 17
or use the provided `.java-version` file with tools like `jenv`.
The project now uses the Android Gradle Plugin **8.1.1** and the Gradle wrapper
is pinned to **8.1**, so older Gradle versions will not work with JDK 17.

With AGP 8 and above, make sure your `build.gradle` specifies the
application `namespace` alongside `compileSdkVersion`.

Using older JDK versions can lead to build failures such as a
`NullPointerException` during Gradle configuration. If you encounter such
errors, verify that `JAVA_HOME` points to a JDK 17 installation.

## Update SDK version
The project is configured for Android **SDK 34**.
Check `Application/LinkBubble/build.gradle` and ensure
`compileSdkVersion`, `targetSdkVersion`, and `minSdkVersion` are
all set to **34** for every module. Make sure your
`local.properties` (or the `ANDROID_HOME` environment variable)
points at an SDK installation that includes API 34.
An example `local.properties` entry might look like:

```properties
sdk.dir=/path/to/android/sdk
```

## Building

Open `./Application/` in Android Studio and build.  You'll need the NDK installed if you don't already have it, instructions below.

## Building release build

Copy `build-release.sh.template` to `build-release.sh`.

Modify each of these exported environment variables: `LINK_BUBBLE_KEYSTORE_LOCATION`, `LINK_BUBBLE_KEYSTORE_PASSWORD`, and `LINK_BUBBLE_KEY_PASSWORD`.

If you get an error similar to:

> Failure [INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES]

Try uninstalling the application which already exists on your plugged in device.

## Installing the NDK

Android Studio has an easy way to download and link to the NDK.

In the menu navigate to File, Project Structure. Click the 'Download Android NDK' link. This should download and unzip the NDK, as well as link it inside of local.properties.

If you are not using Android Studio, reference this commit: https://github.com/brave/browser-android/commit/0fa9f58286e0679ec5772e19b995d6a508907691

Recent NDK releases remove the legacy `platforms` directory.  Android
Gradle Plugin 8 generates this directory automatically so r25 or newer works
out of the box.  If you see a warning that your NDK only supports API level
33 when targeting SDK 34, upgrade to a newer NDK (r26 or later) or adjust
`local.properties` to point at an updated installation.

## Telling getlocalization.com about new strings

1. Periodically upload the file `./Application/LinkBubble/src/main/res/values/strings.xml` to [getlocalization.com](https://www.getlocalization.com/LinkBubble/files/).  getlocalization.com will determine which strings are new
2. When prompted on getlocalization.com, press the mark for retranslation (or keep existing) for changed strings.

## Getting new translated strings from getlocalization.com

1. Install npm dependencies with `npm install`.
2. Run `npm run translate <username> <password>` to pull down the translated xml files.
3. Commit and push your change.

Remember to uncomment `checkStrings` from `MainApplication` and call it in `onCreate` to make sure the pulled files don't cause crashes with format specifiers.

## ADB

If you don't have `adb` in your path add it to your `~/.bash_profile` or similar file:

`export PATH=/Users/<your-username>/Library/Android/sdk/platform-tools:$PATH`

- **Installing an apk onto your device:**  
  `adb install -r ./LinkBubble/build/outputs/apk/LinkBubble-playstore-release.apk`
- **Getting a list of devices:**
  `adb devices`
  
  adb -s `<device id>` install -d -r LinkBubble-playstore-release.apk 
