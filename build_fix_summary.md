# Android 專案升級與修復日誌

本文檔記錄了將一個舊的 Android 專案成功編譯到 SDK 34 的過程中所遇到的問題與解決方案。

## 最終目標

成功編譯出可安裝的 APK 檔案。

## 修復步驟總結

我們按照以下順序解決了編譯過程中出現的一系列問題：

1.  **修正 `AndroidManifest.xml` 路徑問題**
    *   **問題**: 編譯系統找不到 `AndroidManifest.xml` 檔案。
    *   **原因**: 專案中提供的是一個名為 `AndroidManifest.xml.template` 的範本檔。
    *   **解決方案**: 將 `Application/LinkBubble/src/main/AndroidManifest.xml.template` 重新命名為 `AndroidManifest.xml`。

2.  **停用原生 C++ (JNI/CMake) 編譯**
    *   **問題**: CMake 無法找到 `ABPFilterParser.cpp` 等 C++ 原始碼檔案。
    *   **原因**: 這些 C++ 檔案沒有被包含在版本控制中，導致原始碼遺失。
    *   **解決方案**: 在 `Application/LinkBubble/build.gradle` 中，註解掉 `externalNativeBuild` 區塊，暫時跳過 C++ 部分的編譯。

3.  **解決檔案存取/鎖定問題**
    *   **問題**: 出現 `java.nio.file.AccessDeniedException` 或 `not a regular file` 錯誤，通常指向 `build` 目錄下的檔案。
    *   **原因**: Gradle 的快取或檔案系統狀態異常，可能是由防毒軟體、IDE 同步或權限問題引起。
    *   **解決方案**: 執行 `./gradlew.bat clean` 指令，刪除整個 `build` 目錄，強制 Gradle 在乾淨的環境下重新建置。

4.  **從 Support Library 手動遷移到 AndroidX**
    *   **問題**: 大量的 `package android.support.* does not exist` 編譯錯誤。
    *   **原因**: 專案使用了已過時的 Android Support Library，而 `android.enableJetifier=true` 的自動轉換未能完全成功。
    *   **解決方案**: 手動修改所有 Java 檔案，將 `import android.support.*` 的引用替換為對應的 `import androidx.*`。

5.  **修復遺失的類別和已棄用的 API**
    *   **問題**: 編譯時找不到 `ConfigAPIs`, `CrashTracking`, `Subscribe`, `WebIconDatabase` 等類別。
    *   **原因**:
        *   `ConfigAPIs`, `CrashTracking` 等是專案自訂的、未被版本控制的包裝類別。
        *   `Subscribe` 來自 Otto 事件庫，但缺少 `import`。
        *   `WebIconDatabase` 是已從 Android SDK 中移除的過時 API。
    *   **解決方案**:
        *   建立一個帶有虛擬值的 `ConfigAPIs.java` 檔案。
        *   註解掉所有對 `CrashTracking`, `Fabric`, `Analytics` 的呼叫。
        *   在 `MainService.java` 中加入所有缺少的 `import` 語句。
        *   註解掉對 `WebIconDatabase` 的呼叫。
        *   修正 `NotificationUnhideHandler` 為 `NotificationUnhideActivity` 的打字錯誤。

6.  **指定建置偵錯版 (Debug) APK**
    *   **問題**: 編譯最後階段出現 `SigningConfig "release" is missing required property "storeFile"` 錯誤。
    *   **原因**: 專案試圖建置**發布版 (Release)** APK，但我們沒有提供正式的數位簽章金鑰。
    *   **解決方案**: 執行 `./gradlew.bat assemblePlaystoreDebug` 指令，明確告訴 Gradle 只需建置使用自動偵錯簽名的偵錯版本。

---

經過以上步驟，最終成功在 `Application/LinkBubble/build/outputs/apk/playstore/debug/` 目錄下生成了 `LinkBubble-playstore-debug.apk`。
