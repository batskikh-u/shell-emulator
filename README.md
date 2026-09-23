# Shell Emulator

Эмулятор командной оболочки UNIX-подобной ОС.

Этап 1: REPL с парсером кавычек и командами-заглушками `ls`, `cd`, `exit`.

## Примеры работы

```myVFS> ls -l "my fonder"

ls

argument: -l

argument: my fonder

myVFS> cd 'a b c'

cd

argument: a b c

myVFS> ls "unclosed

Error: unclosed quote: "

myVFS> ls "diffenet'

Error: unclosed quote: expected " but got '

myVFS> foo

Error: unknow command foo

myVFS> exit

The session is over
```

## Требования

\- JDK 17+ (или 11)

\- (опционально) `make` для сборки через Makefile

### Windows

```cmd

run.bat
```
