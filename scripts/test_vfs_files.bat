@echo off

call run.bat ^
    --vfs ./vfs/files.zip ^
    --log ./logs/files.csv ^
    --script ./startup/stage3.txt ^
    --config ./config/config.ini