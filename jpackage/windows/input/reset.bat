@echo off
REM used to "restore" back to original. Re-run OAAppStore.exe once done. 
REM   NOTE: no data is lost.

del OAAppStore.cfg
copy OAAppStore_.cfg OAAppStore.cfg
del newrelease.txt
