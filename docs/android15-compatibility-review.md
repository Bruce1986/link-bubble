# Android 15 Compatibility Review

This review inspects the current Link Bubble Android app to explain why Android 13-15 devices can still fail even after the SDK 34 migration. It converts the high-level findings into concrete remediation steps so the issues can be queued, implemented, and verified.

## 1. Foreground service fails without runtime notification permission
**Observed behaviour**  `MainService` promotes itself to a foreground service whenever a URL is opened or restored by calling `startForeground(NotificationHideActivity.NOTIFICATION_ID, notification)`.【F:Application/LinkBubble/src/main/java/com/linkbubble/MainService.java†L234-L315】

**Root cause**  Because the app now targets SDK 34, Android 13+ requires the user-granted `POST_NOTIFICATIONS` runtime permission before a foreground service posts its notification. The manifest declares the permission statically, and `HomeActivity` now includes a basic runtime request path, but the flow still needs validation on API 33-35 devices. If the permission is missing, Android 13+ can throw `ForegroundServiceStartNotAllowedException`, preventing the overlay service from starting.

**Remediation plan**
1. Introduce a `ActivityResultLauncher`-based permission workflow in `HomeActivity` that checks `POST_NOTIFICATIONS` status before starting `MainService`.
2. Surface a rationale dialog when the user previously denied the permission to comply with best-practice UX guidance.
3. Gate calls to `startForeground` until the permission has been granted and show a non-blocking heads-up message if the user permanently denies the request.
4. Add an automated UI test that exercises the permission flow on API 33+ using the AndroidX Test APIs to prevent regressions.

## 2. Missing `android.permission.FOREGROUND_SERVICE`
**Observed behaviour**  Earlier migration notes reported that the manifest lacked `android.permission.FOREGROUND_SERVICE`. The current manifest already declares `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_SPECIAL_USE`, so this item is now a verification task rather than an implementation gap.

**Remediation plan**
1. Update `AndroidManifest.xml` with `<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />` and annotate service declarations with the appropriate `android:foregroundServiceType` (likely `dataSync` or `mediaProjection` depending on the notification behaviour).
2. Audit whether the app performs any location, camera, or microphone work; if so, add the corresponding specific foreground service type permissions introduced in Android 14.
3. Add an instrumentation test that asserts the manifest export includes the permission to avoid future regressions.
4. Re-run the app on Android 13, 14, and 15 emulators to confirm the service starts without `SecurityException`.

## 3. Services started with `Context.startService` instead of `Context.startForegroundService`
**Observed behaviour**  Earlier migration notes reported that both the link interception path and tab-restore path used `context.startService(intent)`. The current code already uses `ContextCompat.startForegroundService`, so this item is now a regression check to keep the fix in place.

**Remediation plan**
1. Replace `context.startService` with `ContextCompat.startForegroundService` (or `Context.startForegroundService` when `Build.VERSION.SDK_INT >= 26`) across all service entry points.
2. Confirm that the service calls `startForeground` within five seconds to satisfy the Android platform contract.
3. Add unit coverage that verifies the application selects the correct API based on the runtime SDK version (use Robolectric to stub `Build.VERSION.SDK_INT`).
4. Capture logcat traces on Android 15 to ensure `ForegroundServiceStartNotAllowedException` no longer appears during cold start and tab restore flows.

## Validation checklist
- [ ] Instrumented notification-permission flow passes on API 33, 34, and 35 emulator suites. *(Pending)*
- [x] Manifest declares `android.permission.FOREGROUND_SERVICE` and `android.permission.FOREGROUND_SERVICE_SPECIAL_USE`. *(Implemented; still needs device validation)*
- [x] Service entry points use `ContextCompat.startForegroundService`. *(Implemented; still needs regression coverage)*
- [ ] Manual regression on ASUS Android 15 devices confirms the overlay bubble renders without background-start exceptions. *(Pending)*

Following this plan keeps the remediation effort actionable, testable, and aligned with modern Android foreground service policies.
