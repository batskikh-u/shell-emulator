# Shell Emulator

Эмулятор командной оболочки UNIX-подобной ОС.

## Этап 1

Реализованы:

* REPL;
* парсер команд с поддержкой одинарных и двойных кавычек;
* команды-заглушки `ls`, `cd`, `exit`.

## Этап 2

Добавлены:

* параметры командной строки для настройки VFS, логов, стартового скрипта и конфигурации;
* конфигурационный INI-файл;
* приоритет параметров командной строки над конфигурационным файлом;
* CSV-логирование выполненных команд;
* запуск команд из стартового скрипта;
* обработка комментариев в стартовом скрипте;
* обработка ошибок конфигурации и стартового скрипта;
* тестовые `.bat`-скрипты.

## Этап 3

Добавлена виртуальная файловая система (VFS):

* VFS загружается из ZIP-архива;
* содержимое файлов загружается в оперативную память;
* поддерживаются виртуальные файлы и директории;
* реализованы команды `ls` и `cd`;
* поддерживаются вложенные директории;
* добавлена обработка ошибок отсутствующего и некорректного ZIP-архива;
* добавлены несколько ZIP-вариантов VFS для тестирования.

## Этап 4

Добавлены основные команды для работы с VFS и оболочкой:

* `ls` — вывод содержимого текущей или указанной директории;
* `cd` — переход между директориями;
* `tail` — вывод последних строк виртуального файла;
* `tail -n N` — вывод последних `N` строк файла;
* `history` — вывод истории выполненных команд;
* `history N` — вывод последних `N` команд;
* `uptime` — вывод времени работы оболочки;
* проверка количества аргументов команд;
* обработка ошибок при работе с файлами и директориями;
* стартовый скрипт `startup/stage4.txt` для проверки всех команд и их режимов.

История команд берётся из CSV-лога, который создаётся и дополняется `Logger`.

## Примеры работы

```text
deep.zip> ls
level1

deep.zip> ls level1
file1.txt
level2

deep.zip> cd level1
deep.zip> cd level2

deep.zip> cd ..
deep.zip> ls
file1.txt
level2

deep.zip> tail level1/file1.txt
This is file 1. It is located in level1.

deep.zip> tail -n 1 level1/file1.txt
This is file 1. It is located in level1.

deep.zip> history 5
10  tail level1/file1.txt
11  tail -n 1 level1/file1.txt
12  tail -n 2 level1/level2/file2.txt
13  history
14  history 5

deep.zip> uptime
Uptime: 0 minutes 0 seconds
```

### Обработка ошибок

```text
deep.zip> ls one two
Error: ls accepts at most one argument

deep.zip> cd level1 level2
Error: cd requires exactly one argument

deep.zip> cd missing
Error: directory not found: missing

deep.zip> tail missing.txt
Error: file not found: missing.txt

deep.zip> tail -n abc level1/file1.txt
Error: invalid line count: abc

deep.zip> tail -n 0 level1/file1.txt
Error: line count must be positive

deep.zip> history abc
Error: invalid history count: abc

deep.zip> uptime test
Error: uptime does not accept arguments
```

## Стартовые скрипты

Для тестирования разных этапов используются стартовые скрипты:

* `startup/basic.txt` — базовый сценарий;
* `startup/stage3.txt` — проверка работы с VFS;
* `startup/stage4.txt` — проверка команд `ls`, `cd`, `tail`, `history`, `uptime` и обработки ошибок.

## VFS

Для тестирования используются несколько виртуальных файловых систем:

* `vfs/minimal.zip` — минимальная VFS;
* `vfs/files.zip` — файлы и директории;
* `vfs/deep.zip` — вложенная структура директорий.

## Запуск

### Windows

```cmd
run.bat
```

Для запуска Stage 4 с тестовым VFS:

```cmd
scripts\test_vfs_deep_with_stage4.bat
```

### Параметры командной строки

Пример переопределения настроек:

```cmd
run.bat --vfs ./vfs/deep.zip --log ./logs/stage4.csv --script ./startup/stage4.txt --config ./config/config.ini
```

Параметры командной строки имеют приоритет над значениями из конфигурационного файла.

## Требования

* JDK 17+ (или JDK 11);
* Windows для запуска `.bat`-скриптов;
* `make` не требуется для запуска проекта.
