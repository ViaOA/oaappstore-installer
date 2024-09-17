

This project "OAAppStore-Installer" is used to create a small installer that will  
then install the "real" OAAppStore from github

This is so that the installer will include the JDK 21 and then the included program will
be able to get and install other OA apps from a URL, default: https://github.com/ViaOA/oaappstore-run/raw/master/[appstore] & [jarstore]

This will be used to create an OAAppStore.exe using oaappstoreinstaller.jar from this project.

Installer.java is the main code.  It will check github for a new update of using value of Release and comparing
it to "https://github.com/ViaOA/oaappstore-run/raw/master/appstore/com/viaoa/oaappstoreinstaller/version.ini"


follow instructions jpackage\readme, and then jpackage\windows\readme.txt


