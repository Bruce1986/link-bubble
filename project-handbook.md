# Link Bubble 開發協作手冊

## 📋 目錄
1. [任務分工與進度追蹤](#任務分工與進度追蹤)
2. [Git 開發流程與規範](#git-開發流程與規範)
3. [程式碼撰寫與 Review 注意事項](#程式碼撰寫與-review-注意事項)
4. [每日工作日誌區塊](#每日工作日誌區塊)
5. [資源與文件連結](#資源與文件連結)

---

## 🧱 任務分工與進度追蹤

| 任務編號 | 功能說明 | 負責人 | 狀態 | 備註 |
|----------|----------|--------|------|------|
| #001 | 還原 JNI / CMake native build | 待分配 | 🟡 進行中 | 需 vendor C++ 原始碼並恢復 Gradle 設定 |
| #002 | 建立可發布的 release 簽名流程 | 待分配 | ⏳ 待開始 | 需 CI secrets 與正式 keystore |
| #003 | 驗證 Android 13/14/15 前景服務與通知權限 | 待分配 | 🟡 進行中 | 補齊 runtime permission 與回歸測試 |
| #004 | 清理過時文件與佔位符內容 | 待分配 | 🟡 進行中 | 保持文件與程式碼現況一致 |

- 狀態建議使用：⏳ 待開始｜🟡 進行中｜🔍 審核中｜✅ 完成

---

## 🌱 Git 開發流程與規範

1. 每項任務請建立 feature branch，例如：
   ```bash
   git checkout -b feature/android15-permission-fix
   ```

2. 完成後發起 Pull Request（PR），標題格式：

   ```
   [Fix] Restore native build (#001)
   ```
3. 不能直接 push 到 `main` 分支，需透過 PR。
4. PR 提交後需指派另一位協作者進行 Review。
5. 合併前務必確認：

   * ✅ 沒有衝突
   * ✅ 已自我測試過
   * ✅ 遵守命名與格式規範

---

## 🧠 程式碼撰寫與 Review 注意事項

### 🧹 命名原則

* 變數需有語意：`userInfo` 不要寫成 `x`
* 函式命名盡量是動詞開頭，如 `getUserInfo`

### 🛡 程式邏輯

* 防呆與錯誤處理要寫清楚，例如權限遭拒、native lib 缺失、release 環境變數未設定等情境
* 不要留死 code
* API 或建置錯誤時要留下足夠日誌，方便追查

### 👀 Review 時要看的事

* [ ] 功能是否如預期運作？
* [ ] 是否有不合理命名？
* [ ] 是否有簡化空間或可讀性問題？
* [ ] 有無潛在 Bug（如未處理 null）？
* [ ] 結尾是否有自我測試結果備註？

---

## 🗓 每次工作日誌區塊

> 請每次 commit 前更新一次，記錄格式如下：

```markdown
### 🙋‍♂️ A 的日誌（2026/03/29）
- ✅ 完成：釐清 Android 15 / native build 問題來源
- 🟡 進行中：恢復 JNI 原始碼與通知權限流程
- 🤔 問題：release keystore 尚未納入 CI 或本機安全配置

### 🧑‍💻 B 的日誌（2026/03/29）
- ✅ 完成：整理 code review 報告與文件差異
- ⏳ 明日計畫：補齊 Android 13/14/15 驗證
- 🙋 想問：Play Console 的 `FOREGROUND_SERVICE_SPECIAL_USE` 聲明由誰負責提交？
```

---

## 📚 資源與文件連結

* `build_fix_summary.md`：記錄 SDK 升級與建置修復歷程
* `docs/android15-compatibility-review.md`：Android 13-15 前景服務與通知權限檢查
* `docs/node-dependency-findings.md`：舊 npm/native 依賴鏈調查結果

---

## 📌 補充：協作小叮嚀

* **如果卡住，請互相支援或寫日誌記錄問題點，等老闆回來再補救**
* **分工明確，避免同時改動同一檔案造成衝突**
* **即使只是小改動，也請走 PR 流程，養成良好習慣**

---

## 🏁 TODO 清單樣板（可複製貼上）

```markdown
- [ ] 任務說明：
- [ ] 建立分支：
- [ ] 自我測試情境：
- [ ] 需他人 Review 項目：
- [ ] 文件是否補上：
```

---

## 📎 文件維護原則

* `AGENTS.md` 只放 AI 協作額外規則，不重複手冊內容
* `project-handbook.md` 是協作流程單一真相來源
* 若文件描述與程式碼不一致，請在同一個 PR 一併修正
