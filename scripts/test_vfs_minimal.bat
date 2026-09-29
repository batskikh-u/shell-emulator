@echo off

call run.bat ^
    --vfs ./vfs/minimal.zip ^
    --log ./logs/minimal.csv ^
    --script ./startup/stage3.txt ^
    --config ./config/config.ini