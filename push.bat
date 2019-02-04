@echo off
set /p id="Commit name: "
git add *
git commit -m "%id%"
git push origin master
pause