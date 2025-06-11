# Guidelines for Codex

- Before committing changes to JavaScript files, run `npm run lint`.
- If `npm test` exists, run it as well.
- Use two-space indentation and semistandard style for JS.
- Use Node.js 18 when running npm scripts to avoid node-gyp build issues.
- Summaries should briefly mention lint and test results.
- Gradle builds require JDK 17. Ensure `JAVA_HOME` points to JDK 17 or use the
  `.java-version` file when building the Android project. The project uses the
  Android Gradle Plugin 8.1.1 with Gradle 8.1 and targets **Android SDK 34**.
  All modules should set `compileSdkVersion`, `targetSdkVersion`, and
  `minSdkVersion` to 34. Ensure `local.properties` or the `ANDROID_HOME`
  environment variable points to an SDK that includes API 34.
  For example:

  ```properties
  sdk.dir=/path/to/android/sdk
  ```
