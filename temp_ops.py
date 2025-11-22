"""Utility script for temporary file operations."""

import argparse
import os
import shutil
import sys


def move_op(old_path: str, new_path: str) -> None:
    """Move or rename a file or directory and handle errors."""
    try:
        shutil.move(old_path, new_path)
        print(f"Renamed: {old_path} -> {new_path}")
    except FileNotFoundError:
        print(f"Error: File not found - {old_path}", file=sys.stderr)
        sys.exit(1)
    except OSError as e:
        print(f"Error renaming {old_path}: {e}", file=sys.stderr)
        sys.exit(1)


def remove_op(path_to_delete: str) -> None:
    """Remove a file or directory and handle errors."""
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
    except OSError as e:
        print(f"Error deleting {path_to_delete}: {e}", file=sys.stderr)
        sys.exit(1)


def main() -> None:
    """Parse command-line arguments and dispatch operations."""
    parser = argparse.ArgumentParser(
        description="Perform temporary file operations."
    )
    subparsers = parser.add_subparsers(
        dest="command", required=True, help="Available commands"
    )

    mv_parser = subparsers.add_parser(
        "mv", help="Move or rename a file or directory."
    )
    mv_parser.add_argument("old_path", help="The source path.")
    mv_parser.add_argument("new_path", help="The destination path.")

    rm_parser = subparsers.add_parser(
        "rm", help="Remove a file or directory."
    )
    rm_parser.add_argument(
        "path", help="The path to the file or directory to remove."
    )

    args = parser.parse_args()

    if args.command == "mv":
        move_op(args.old_path, args.new_path)
    elif args.command == "rm":
        remove_op(args.path)


if __name__ == "__main__":
    main()

