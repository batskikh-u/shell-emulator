@echo off

call run.bat ^
    --vfs ./cli-vfs ^
    --log ./logs/cli.csv ^
    --script ./startup/basic.txt ^
    --config ./config/config.ini