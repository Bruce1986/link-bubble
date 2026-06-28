# AI 代理程式協作指南

本文件旨在規範與 AI 代理程式（如 gemini-code-assist）在此專案中的協作方式，以確保程式碼品質與開發流程的一致性。

## 1. AI 助理回饋處理

本專案使用 **gemini-code-assist** 提供程式碼審查建議。

- 請處理所有來自 AI 助理的建議。
- 如果某個建議未被採納，請在 Pull Request 的討論中提供簡要理由（例如，作為對 PR 的評論或直接在相關程式碼行上留言）。

## 2. 單元測試要求

- 所有新功能（函式）都必須包含對應的單元測試。
- 如果對特定功能進行單元測試不可行，應提供明確的解釋。

---

## 專案特定指南

- 如果 `npm test` 存在，也請執行它。
- 如果 `lint` 腳本可用，請在提交變更前運行 `npm run lint`。
- 對於 JS，請使用兩格縮排和 semistandard 風格。
- 請遵循 [project-handbook.md](./project-handbook.md) 中的協作指南。
- 摘要應簡要提及 lint 和測試結果。
- 使用 Node.js 18 (LTS) 進行構建，以確保與某些依賴項的兼容性並避免本機模組問題。
- 在運行 npm 命令之前，請使用 `node --version` 驗證您正在使用 Node.js 18。
- Gradle 構建需要 JDK 17。請確保 `JAVA_HOME` 指向 JDK 17 或在構建 Android 專案時使用 `.java-version` 檔案。該專案使用 Android Gradle Plugin 8.1.1 和 Gradle 8.1。
- CI 環境不再支持 Python 2；請使用 Python 3，不要配置 Python 2。


---

## PR Review 自動化循環

收到「抓取 review 意見」或 `/Gemini review` 相關指令後，執行以下循環：

### 步驟

1. **抓取 review comments**
   - `gh api repos/{owner}/{repo}/pulls/{pr}/comments` 取得 inline comments
   - `gh api repos/{owner}/{repo}/pulls/{pr}/reviews` 取得 review summary
   - 篩選 `user.login == "gemini-code-assist[bot]"`
   - 用時間戳過濾只處理新的 comments

2. **修改程式碼**
   - 合理建議直接實作
   - 已被 owner 明確拒絕的建議跳過（查看先前對話紀錄）
   - 純文件/風格建議也要處理

3. **驗證**
   - 跑 `ruff check src/ tests/`
   - 跑 `mypy src/autonomous_agent/`
   - 跑 `pytest -v`
   - 三項全部通過才能繼續

4. **Commit & Push**
   - 寫清楚的 commit message（見下方慣例）
   - 推到 origin 和 homee 兩個 remote

5. **觸發下一輪 review**
   - 在 PR 留言 `/Gemini review`

6. **等待**
   - `sleep 210` 秒（約 3.5 分鐘）等待 Gemini 回應
   - 若時間到了沒有新 review，再等 60-120 秒重試

7. **檢查終止條件後回到步驟 1**

### 終止條件

- Gemini 回覆包含 **"I have no feedback"** 或 **"no review comments were submitted"**
- Gemini bot 達到每日額度限制（"daily quota"）
- 使用者手動要求停止

---

## Commit 慣例

```
<type>: <簡述>

<詳細說明>

Co-Authored-By: <agent-name> <noreply@anthropic.com>
```

Type: `fix`, `feat`, `refactor`, `chore`, `docs`, `test`
