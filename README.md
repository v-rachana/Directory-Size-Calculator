# Directory Size Calculator

A command-line Java application that simulates an in-memory hierarchical file system.

## Structure

```
dirsize/
├── src/
│   ├── Main.java               Entry point + REPL loop
│   ├── FileNode.java           Tree node (FILE or DIRECTORY)
│   ├── FileSystemService.java  Core logic: cd, ls, size, mkdir, touch
│   ├── SeedLoader.java         Loads demo data at startup
│   └── CommandParser.java      Parses and dispatches CLI commands
├── test/
│   ├── FileSystemServiceTest.java
│   └── CommandParserTest.java
├── dirsize.jar                 Pre-built runnable jar
└── build.bat                   Build script (Windows)
```

## Requirements

- Java 11 or higher

## Run (pre-built jar)

```
java -jar dirsize.jar
java -jar dirsize.jar --no-seed    # start with empty file system
```

## Build from source

**Windows:**
```
build.bat
```

## Commands

| Command              | Description                        |
|----------------------|------------------------------------|
| `ls`                 | List current directory             |
| `cd <name>`          | Enter a sub-directory              |
| `cd ..`              | Go to parent directory             |
| `cd /`               | Go to root                         |
| `size`               | Recursive total size (uses recursion) |
| `mkdir <name>`       | Create a sub-directory             |
| `touch <name> <bytes>` | Create a file with a size        |
| `pwd`                | Print current path                 |
| `help`               | Show all commands                  |
| `exit`               | Quit                               |

## Design

- **FileNode** — single class for both files and directories (Composite pattern)
- **FileSystemService** — all logic; `computeSize()` recurses over children
- **CommandParser** — tokenises input and dispatches to service
- **All data in memory** — no database or file I/O required
