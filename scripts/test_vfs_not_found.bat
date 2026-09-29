@echo off

call run.bat ^
    --vfs ./vfs/not-found.zip ^
    --log ./logs/not-found.csv ^
    --script ./startup/stage3_deep.txt ^
    --config ./config/config.ini