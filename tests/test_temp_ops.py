from pathlib import Path
import sys

import pytest

sys.path.append(str(Path(__file__).resolve().parents[1]))
from temp_ops import move_op, remove_op


def test_move_op(tmp_path: Path) -> None:
    src = tmp_path / "src.txt"
    src.write_text("content")
    dest = tmp_path / "dest.txt"

    move_op(str(src), str(dest))

    assert not src.exists()
    assert dest.exists()
    assert dest.read_text() == "content"


def test_remove_op_file(tmp_path: Path) -> None:
    file_path = tmp_path / "to_delete.txt"
    file_path.write_text("content")

    remove_op(str(file_path))

    assert not file_path.exists()


def test_remove_op_directory(tmp_path: Path) -> None:
    dir_path = tmp_path / "dir"
    dir_path.mkdir()
    (dir_path / "file.txt").write_text("content")

    remove_op(str(dir_path))

    assert not dir_path.exists()

