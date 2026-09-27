@echo off

call run.bat ^
    --vfs ./vfs ^
    --log ./logs/errors.csv ^
    --script ./startup/errors.txt ^
    --config ./config/config.ini