# Windows 環境筆記

來源：`GEMINI.md`

## 2. Windows 環境注意事項

### 2.1. 命令列工具相容性
在 Windows 環境下，部分基於 Unix/Linux 的命令列工具可能無法直接使用或行為不符預期。

*   **`rm` 指令**：`rm` (remove) 指令在 Windows 的命令提示字元 (CMD) 或 PowerShell 中通常無法直接使用。請改用 Windows 對應的指令：
    *   刪除檔案：`del <檔案名稱>`
    *   刪除目錄：`rmdir /s /q <目錄名稱>` (`/s` 刪除目錄及其所有子目錄和檔案，`/q` 安靜模式，不提示確認)

### 2.2. 中文顯示問題
在某些終端機或環境設定下，中文字符可能無法正確顯示，導致亂碼。這通常與終端機的編碼設定有關。

### 2.3. Git Commit Message 引號問題
在使用 `git commit -m "..."` 提交訊息時，如果訊息中包含特殊字符或多行內容，可能會遇到引號解析問題，導致提交失敗。

*   **解決方案**：如果遇到此問題，可以考慮將提交訊息寫入一個臨時檔案，然後使用 `git commit -F <檔案名稱>` 的方式來提交。例如：
    1.  將提交訊息寫入 `COMMIT_EDITMSG` 檔案：
        ```
        echo "feat: My commit message" > COMMIT_EDITMSG
        echo "" >> COMMIT_EDITMSG
        echo "This is a detailed description of my commit." >> COMMIT_EDITMSG
        ```
    2.  使用檔案提交：
        ```
        git commit -F COMMIT_EDITMSG
        ```

### 2.4. Windows 環境下檔案操作問題與解決方案

**問題描述：**

在 Windows 環境下，直接使用 `run_shell_command` 執行 `mv` 或 `rm` 等命令來操作包含中文路徑或特殊字元的檔案時，可能會遇到亂碼、操作失敗或權限問題。此外，在 Git 操作中，臨時檔案的創建和刪除也可能因為權限或路徑問題而失敗。

**解決方案：**

為了確保跨平台相容性和操作的可靠性，建議在需要進行檔案操作時，透過執行一個固定的 Python 腳本來完成。這個 Python 腳本可以使用 `os` 模組中的 `os.rename()` 和 `os.remove()` 等函數來執行檔案的重新命名和刪除操作。

**策略：**

1.  **固定暫存 Python 腳本**：在專案根目錄下創建一個固定的 Python 腳本檔案（例如：`temp_ops.py`）。所有需要執行的臨時檔案操作（如重新命名、刪除）都將透過這個腳本來完成。
2.  **傳遞參數**：將需要操作的檔案路徑和新名稱作為參數傳遞給 `temp_ops.py` 腳本。
3.  **腳本內容**：`temp_ops.py` 腳本將包含處理這些參數並執行實際檔案操作的邏輯。
4.  **忽略 `temp_ops.py`**：將 `temp_ops.py` 加入 `.gitignore`，確保它不會被提交到版本控制中。

**範例 `temp_ops.py` 內容：**

```python
import os
import sys
import shutil

def main():
    if len(sys.argv) < 3:
        print("Usage: python temp_ops.py <command> [args...]", file=sys.stderr)
        print("Commands:", file=sys.stderr)
        print("  mv <old_path> <new_path>", file=sys.stderr)
        print("  rm <path_to_delete>", file=sys.stderr)
        sys.exit(1)

    command = sys.argv[1]

    if command == "mv" and len(sys.argv) == 4:
        old_path = sys.argv[2]
        new_path = sys.argv[3]
        try:
            shutil.move(old_path, new_path)
            print(f"Renamed: {old_path} -> {new_path}")
        except FileNotFoundError:
            print(f"Error: File not found - {old_path}", file=sys.stderr)
            sys.exit(1)
        except Exception as e:
            print(f"Error renaming {old_path}: {e}", file=sys.stderr)
            sys.exit(1)

    elif command == "rm" and len(sys.argv) == 3:
        path_to_delete = sys.argv[2]
        try:
            if os.path.isdir(path_to_delete):
                shutil.rmtree(path_to_delete)
                print(f"Deleted directory: {path_to_delete}")
            else:
                os.remove(path_to_delete)
                print(f"Deleted file: {path_to_delete}")
        except FileNotFoundError:
            print(f"Error: File not found - {path_to_delete}", file=sys.stderr)
            sys.exit(1)
        except Exception as e:
            print(f"Error deleting {path_to_delete}: {e}", file=sys.stderr)
            sys.exit(1)
    else:
        print(f"Invalid command or arguments for '{command}'.", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()
```

**執行方式：**

```bash
# 重新命名檔案
python temp_ops.py mv "D:/path/to/old_name.html" "D:/path/to/new_name.html"

# 刪除檔案或資料夾
python temp_ops.py rm "D:/path/to/file_or_folder"
```

### 2.5. ADB 指令注意事項

在 Windows PowerShell 環境下執行 ADB 指令時，若 `adb.exe` 位於當前目錄，必須使用 `./adb.exe` 而非僅輸入 `adb`。這是 PowerShell 的安全限制。

*   **正確範例**：`./adb.exe devices`
*   **錯誤範例**：`adb devices`
