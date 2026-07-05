# Android 15 Compatibility Review

This review inspects the current Link Bubble Android app to explain why Android 13-15 devices could still fail even after the SDK 34 migration. The code-level remediation for each finding below is now implemented in this change; each section records what was done and the device-validation work that remains.

## 1. Foreground service fails without runtime notification permission
**Observed behaviour**  `MainService` promotes itself to a foreground service whenever a URL is opened or restored by calling `startForeground(NotificationHideActivity.NOTIFICATION_ID, notification)`.【F:Application/LinkBubble/src/main/java/com/linkbubble/MainService.java†L234-L315】

**Root cause**  Because the app now targets SDK 34, Android 13+ requires the user-granted `POST_NOTIFICATIONS` runtime permission before a foreground service posts its notification. The manifest declares the permission statically, and `HomeActivity` now includes a basic runtime request path, but the flow still needs validation on API 33-35 devices. If the permission is missing, Android 13+ can throw `ForegroundServiceStartNotAllowedException`, preventing the overlay service from starting.

**Status — implemented in this change**
1. A dedicated transparent `NotificationPermissionActivity` requests `POST_NOTIFICATIONS`, triggered from the visible entry points (`EntryActivity` for the open/share path and `HomeActivity`), with `MainService` as a best-effort fallback, so the request fires regardless of which entry point starts the service.
2. `HomeActivity` shows a rationale dialog before requesting; the prompt is backed by a persisted flag so it appears at most once per install instead of nagging on every recreation.
3. `MainService.onCreate` posts the foreground notification synchronously via `showDefaultNotification()` → `startForeground`, and passes the `specialUse` foreground-service type only on API 34+; a toast is shown if the user denies.

**Remaining (validation only)** — exercise the flow on API 33/34/35 devices and add an automated UI test to guard against regressions.

## 2. Missing `android.permission.FOREGROUND_SERVICE`
**Observed behaviour**  Earlier migration notes reported that the manifest lacked `android.permission.FOREGROUND_SERVICE`. The current manifest already declares `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_SPECIAL_USE`, so this item is now a verification task rather than an implementation gap.

**Status — done**  The manifest (and its `.template`) declare `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_SPECIAL_USE`, and the `MainService` declaration uses `android:foregroundServiceType="specialUse"` with a `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` property. The app does no location/camera/microphone work, so `specialUse` is the correct type and no other Android 14 FGS-type permission applies.

**Remaining (validation only)** — re-run on Android 13/14/15 to confirm the service starts without `SecurityException`.

## 3. Services started with `Context.startService` instead of `Context.startForegroundService`
**Observed behaviour**  Earlier migration notes reported that both the link interception path and tab-restore path used `context.startService(intent)`. The current code already uses `ContextCompat.startForegroundService`, so this item is now a regression check to keep the fix in place.

**Status — done**  All service entry points use `ContextCompat.startForegroundService`, and `MainService.onCreate` calls `startForeground` synchronously (via `showDefaultNotification()`), satisfying the five-second platform contract.

**Remaining (validation only)** — capture logcat on Android 15 to confirm `ForegroundServiceStartNotAllowedException` no longer appears during cold start and tab-restore flows, and add regression coverage.

## Validation checklist
- [ ] Instrumented notification-permission flow passes on API 33, 34, and 35 emulator suites. *(Pending)*
- [x] Manifest declares `android.permission.FOREGROUND_SERVICE` and `android.permission.FOREGROUND_SERVICE_SPECIAL_USE`. *(Implemented; still needs device validation)*
- [x] Service entry points use `ContextCompat.startForegroundService`. *(Implemented; still needs regression coverage)*
- [ ] Manual regression on ASUS Android 15 devices confirms the overlay bubble renders without background-start exceptions. *(Pending)*

Following this plan keeps the remediation effort actionable, testable, and aligned with modern Android foreground service policies.
