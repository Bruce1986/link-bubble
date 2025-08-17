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
