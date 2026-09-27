Stage 2 tests

1. Проверка запуска:
   run.bat

2. Проверка параметров:
   run.bat --vfs ./vfs-test --log ./logs/test.csv --script ./startup/basic.txt --config config/config.ini

3. Проверка ошибки конфигурации:
   run.bat --config config/not-found.ini

4. Проверка команд:
   ls
   ls "hello world"
   cd test
   cd "test folder"
   unknown
   exit