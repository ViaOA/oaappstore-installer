

OAAppStoreInstaller Windows installer used for OAAppStore



20240422 windows using jdk 21 jpackage

*** NOTE: Only need to create installer once, or when a new JDK is needed.  Otherwise, OAAppStore will automatically 
    update itself from github/OAAppStore-Run project

C:\Program Files\Java\jdk-21.0.2
JAVA_HOME="C:\Program Files\Java\jdk-21.0.2\bin\java" 

Downloaded WiX 3.0 or later from https://wixtoolset.org and add it to the PATH.
    after install, it will be in programs, search "wix"
    jpackage uses it for Windows installer
    

#0) set correct cmd line JDK version, ex: jdk 21
    java -version
    
    control panel / Programs / java
        add jvm version to list
    
    Control Panel\Environment Variables
        add to begin of path:  "C:\Program Files\Java\jdk-21.0.2\bin"

        
#1) remove previous installation
    windows uninstall OAAppStore

#1.1) make any changes to Installer.java, and change value of "Release) if there are changes that users who run reset.bat will need to get.
    update OAAppStore-Run appstore/com/viaoa/oaappstoreinstaller/version.ini with the new Release value.

#1.2) clear jpackage files
    cd C:\Users\vince\eclipse-workspace\OAAppStore-Installer\jpackage\windows
    del OAAppStore*.msi
    rmdir tempfiles /s

#1.3) new jar file
    maven clear install, to create new jar

    copy target\oaappstoreinstaller*.jar 
        to  
        "jpackage\windows\input\oaappstoreinstaller.jar" 
            (remove the version from the file's name)
    
    copy target\oaappstoreinstaller*.jar 
        to OAAppStore-Run project
            OAAppStore-Run jarstore/com/viaoa/oaappstoreinstaller/ 
                (remove the version from the file's name)
       
     

*troubleshooting only*, otherwise go to step "2&3 combined"
    <NOTE**: not yet updated for this project>
    #2) CREATE application image 
            this option will first create a image, and step #3 will then use the image to create installer
        jpackage --type app-image --input ./input --dest . --name OAAppStore --main-jar oaAppStore.jar --main-class com.viaoa.appstore.control.Startup --icon OAAppStore.ico 
        --arguments rootDirectory=app --arguments single --verbose --app-version 1.0.0 --java-options "-Xmx2g" --vendor ViaOA
            
        
        
        > creates OAAppStore
            /bin/OABuilder is executable
            /lib/app is where uber jar and other files are
            /lib/runtime is jdk
    
    #3) CREATE installer 
        copy oabulder.jar to windows\input directory
        cd c:\users\vvia\git\oabuilder\jpackage\windows
        
        jpackage --resource-dir resource_dir --temp tempfiles --name OAAppStore --app-image OAAppStore --dest . --type msi --vendor ViaOA --app-version 1.0.0 --verbose --icon OABuilder.ico --file-associations filemap.txt --copyright "Copyright 2024 ViaOA" --license-file license --win-menu --win-menu-group ViaOA --win-shortcut-prompt
            > creates OAAppStore-*.msi
    
    
***One step,  use this unless there is a need for troublshooting (previous steps 2 & 3) ***
#2&3 combined) create installer directly 
    cd C:\Users\vince\eclipse-workspace\OAAppStore-Installer\jpackage\windows
    jpackage --verbose --temp tempfiles --name OAAppStore --input ./input --main-jar oaappstoreinstaller.jar --main-class com.viaoa.appstore.installer.Installer --arguments rootDirectory=app --resource-dir resource_dir --dest . --type msi --vendor ViaOA --app-version 1.0.0 --icon OAAppStore.ico --file-associations filemap.txt --copyright "Copyright 2024 ViaOA" --license-file license.txt --win-menu --win-menu-group ViaOA --win-shortcut-prompt --win-per-user-install
        # others
        --win-dir-chooser 
        --win-console
     
        > creates 
            OAAppStore-*.msi   (rename, remove the "-version")

4) copy jpackage\windows\OAAppStore*.msi to 
    OAAppStore-Run\windows-installer\OAAppStore.msi  (remove version in file name)
    git commit
    

5) installation
    > download windows installer
        https://github.com/ViaOA/oaappstore-run/tree/master/windows-installer/OAAppStore.msi
           use the download icon (toolbar with "Raw" button beside it)
           or use:
        https://github.com/ViaOA/oaappstore-run/raw/master/windows-installer/OAAppStore.msi        
        
    run OAAppStore.msi
    
    installs:  
    C:\Users\vince\AppData\Local\OAAppStore
        OAAppStore.exe
    C:\Users\vince\AppData\Local\OAAppStore\app
        OAAppStore.cfg
            Args: 
                rootDirectory=app
                single
           




 
 
