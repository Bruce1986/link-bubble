# Android 15 Compatibility Review

This review inspects the current Link Bubble Android app to explain why the build fails to run on Android 15 devices such as the ASUS ZenFone 10 and ROG 6D. It follows the clarity, best-practice, and robustness principles outlined in the provided guidelines.

## 1. Foreground service fails without runtime notification permission
- **What happens:** `MainService` promotes itself to a foreground service whenever a URL is opened or restored by calling `startForeground(NotificationHideActivity.NOTIFICATION_ID, notification)`.【F:Application/LinkBubble/src/main/java/com/linkbubble/MainService.java†L234-L315】
- **Why Android 15 breaks:** Because the app now targets SDK 34, Android 13+ requires the user-granted `POST_NOTIFICATIONS` runtime permission before a foreground service posts its notification. The manifest only declares the permission statically, and there is no runtime request path anywhere in the codebase.【F:Application/LinkBubble/src/main/AndroidManifest.xml†L1-L80】【F:Application/LinkBubble/src/main/java/com/linkbubble/ui/HomeActivity.java†L41-L95】 When the permission is missing, Android 13+ throws `ForegroundServiceStartNotAllowedException`, preventing the overlay service from starting. This leaves the app stuck on devices that ship with Android 15.
- **Suggested fix:** Add a runtime permission request flow before starting `MainService` (for example, alongside the existing overlay-permission prompt in `HomeActivity`). Defer the call to `startForeground` until `POST_NOTIFICATIONS` has been granted, or fall back to a heads-up UX that explains why the permission is required.

## 2. Missing `android.permission.FOREGROUND_SERVICE`
- **Observation:** The manifest lacks the mandatory `android.permission.FOREGROUND_SERVICE` declaration even though `MainService` calls `startForeground` during normal operation.【F:Application/LinkBubble/src/main/AndroidManifest.xml†L1-L80】【F:Application/LinkBubble/src/main/java/com/linkbubble/MainService.java†L234-L315】 Android 9+ enforces this permission for apps targeting API 28 or above. Vendor builds of Android 15 (including the cited ASUS firmware) aggressively enforce the check, causing a `SecurityException` and killing the service.
- **Suggested fix:** Declare `<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />` (and specific foreground service type permissions if Android 14+ behavior requires them) so that the foreground transition is allowed across OEM builds.

## 3. Services started with `Context.startService` instead of `Context.startForegroundService`
- **Observation:** Both the link interception path and tab-restore path launch `MainService` with `context.startService(intent)` after the activity quickly finishes.【F:Application/LinkBubble/src/main/java/com/linkbubble/MainApplication.java†L273-L326】 Once the activity returns to the background, Android 8+ may treat the app as backgrounded and throw `ForegroundServiceStartNotAllowedException` before `startForeground` executes.
- **Suggested fix:** Replace `context.startService` with `ContextCompat.startForegroundService` (or `Context.startForegroundService` on API 26+) and keep the existing immediate `startForeground` call. This aligns with Android’s background execution limits and avoids race conditions on Android 15 where the activity finishes faster than the service promotes itself.

## Next steps
1. Implement a notifications-permission request UI (with rationale copy) before launching `MainService`.
2. Update the manifest with `android.permission.FOREGROUND_SERVICE` (and specific foreground types if targeting Android 14+ service policies).
3. Switch service launches to `startForegroundService` / `ServiceCompat.startForeground`.
4. Retest on Android 15 builds from ASUS to confirm the overlay bubble renders and that no background-start exceptions are logged.

Documenting these fixes now should streamline future remediation work and keep the project aligned with modern Android best practices.
