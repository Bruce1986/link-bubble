# Guidelines for Codex

- If `npm test` exists, run it as well.
- Run `npm run lint` if a `lint` script is available (before committing changes).
- Use two-space indentation and semistandard style for JS.
- Summaries should briefly mention lint and test results.
- Use Node.js 18 (LTS) for builds to ensure compatibility with certain dependencies and avoid native module issues.
- Gradle builds require JDK 17. Ensure `JAVA_HOME` points to JDK 17 or use the
  `.java-version` file when building the Android project. The project uses the
  Android Gradle Plugin 8.1.1 with Gradle 8.1.
- Python 2 is no longer supported in CI environments; use Python 3 and do not configure Python 2.
