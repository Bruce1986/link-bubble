# WORKLOG

### Codex 的日誌（2026/03/29）
- ✅ 完成：比對 `LinkBubble_CodeReview_Report.md` 與目前程式碼狀態，確認哪些問題仍有效
- ✅ 完成：從 npm registry vendor `abp-filter-parser-cpp`、`tracking-protection`、`hashset-cpp`、`bloom-filter-cpp` 所需 JNI 原始碼
- 🟡 進行中：恢復 `externalNativeBuild` / `release` build 設定並驗證 native 編譯
- 🟡 進行中：補強 `POST_NOTIFICATIONS` 權限提示與文件一致性
- 🤔 問題：正式 release 簽名仍需實際 keystore 與 CI secret，當前僅能先提供可建置配置
