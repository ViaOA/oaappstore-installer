package com.viaoa.appstore.installer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * This is the bootstrap installer that will then install the "real" OAAppStore application.
 * <p>
 * This is used by the OAAppStore MS Windows installer, created using Java jpackage
 * <p>
 * It will read from github project "OAAppStore-Run"
 * appstore/com/viaoa/oaappstoreinstaller/version.ini
 * to get the value of "Release" and compare it to the the value from Installer.release
 * If differnt, then it will get  
 * jarstore/com/viaoa/oaappstoreinstaller/oaappstoreinstaller.jar 
 * and then rerun OAAppStore.exe
 * 
 * @author vince
 */
public class Installer {
    // NOTE: this will be ran from Windows directory "C:\\Users\\username\\AppData\\Local\\OAAppStore"

    private static final String urlDownload = "https://github.com/ViaOA/oaappstore-run/raw/master";
    private final Properties gitProps = new Properties();
    
    // IMPORTANT NOTE:  this should to match the version.ini release
    //   in github:   /appstore/com/viaoa/oaappstoreinstaller/version.ini
    private static final String release = "202404300";
    
    public void log(String msg) {
        System.out.println(msg);
    }

    public boolean updateInstaller() throws Exception {
        log("Starting updateInstaller");
        File file = new File("app\\newrelease.txt");
        if (file.exists()) {
            log("installer already updated (app\\newrelease.txt file exists)");
            return false;
        }
        
        log("urlDownload=" + urlDownload);
        
        String s = urlDownload + "/appstore/com/viaoa/oaappstoreinstaller/version.ini";
        log("getting oaappstoreinstaller " + s);
        URL url = new URL(s);
        URLConnection conn = url.openConnection();
        
        Properties props = new Properties();
        props.load(conn.getInputStream());
        s = props.getProperty("Release");
        log("This (Installer.java) release="+release+", online Release="+s);
        
        if (release.equals(s)) return false;;

        DataInputStream dis;
        final byte[] bs = new byte[8196];

        s = urlDownload + "/jarstore/com/viaoa/oaappstoreinstaller/oaappstoreinstaller.jar";
        log("getting updated installer jar from URL=" + s);
        url = new URL(s);
        conn = url.openConnection();

        dis = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
    
        s = "app\\oaappstoreinstaller.jar";
        log("saving jar to=" + s);
        file = new File(s);
        file.createNewFile();
        OutputStream fos = new FileOutputStream(file);
    
        for ( ;; ) {
            int x = dis.read(bs);
            if (x < 0) {
                break;
            }
            fos.write(bs, 0, x);
        }
        fos.close();
        log("saved " + s);
        log("New OAAppStore installed");

        file = new File("app\\newrelease.txt");
        file.createNewFile();
        
        return true;
    }
        
    public boolean downloadAppStore() throws Exception {
        String s;
        File file;
        
        log("Starting, urlDownload=" + urlDownload);
        URL url;
        URLConnection conn;
        DataInputStream dis;
        final byte[] bs = new byte[8196];

        s = urlDownload + "/appstore/com/viaoa/oaappstore/version.ini";
        log("getting "+s);
        url = new URL(s);
        conn = url.openConnection();
        
        gitProps.load(conn.getInputStream());

        s = "app\\appstore\\com\\viaoa\\oaappstore";
        file = new File(s);
        if (!file.exists()) {
            log("creating directory for application files: " + s);
            file.mkdirs();
        }

        s = "app\\jarstore\\com\\viaoa\\oaappstore";
        file = new File(s);
        if (!file.exists()) {
            log("creating directory for jar files: " + s);
            file.mkdirs();
        }
        
        // load jar files
        for (int i = 1;; i++) {
            final String fn = gitProps.getProperty("jar" + i);
            if (fn == null || fn.trim().length() == 0) {
                if (i > 10) {
                    break;
                }
                continue;
            }
        
            s = urlDownload + "/jarstore/" + fn;
            log("getting jar from URL=" + s);
            url = new URL(s);
            conn = url.openConnection();

            dis = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
        
            s = fn.replace('/', '\\');
            s = "app\\jarstore\\" + s;
        
            log("saving jar to=" + s);
            file = new File(s);
            file.createNewFile();
            OutputStream fos = new FileOutputStream(file);
        
            for ( ;; ) {
                int x = dis.read(bs);
                if (x < 0) {
                    break;
                }
                fos.write(bs, 0, x);
            }
            fos.close();
            log("saved " + fn);
        }
        
        // load other files
        for (int i = 1;; i++) {
            final String fn = gitProps.getProperty("file" + i);
            if (fn == null || fn.trim().length() == 0) {
                if (i > 10) {
                    break;
                }
                continue;
            }

            s = urlDownload + "/appstore/com/viaoa/oaappstore/" + fn;
            log("getting file from URL="+s);
            url = new URL(s);
            conn = url.openConnection();
            dis = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
            
            s = fn.replace('/', '\\');
            s = "app\\appstore\\com\\viaoa\\oaappstore\\" + s;
            log("saving file to=" + s);
            file = new File(s);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            
            for (;;) {
                int x = dis.read(bs);
                if (x < 0) {
                    break;
                }
                fos.write(bs, 0, x);
            }
            fos.close();
            log("saved " + fn);
        }
        log("finished successfully");
        return true;
    }
    
    
    public void updateConfig() throws Exception {
        String txt = "";
        txt += "[Application]\n";
        
        txt += "app.classpath=";
        // was: txt += "app.classpath=$APPDIR\\oaappstore.jar\n";
        
        for (int i = 1;; i++) {
            String fn = gitProps.getProperty("jar" + i);
            if (fn == null || fn.trim().length() == 0) {
                if (i > 10) {
                    break;
                }
                continue;
            }
            fn = fn.replace('/', '\\');

            if (i > 1) txt += ";";
            
            String s = "app\\jarstore\\" + fn;
            txt += s;
        }        
        txt += "\n";
        
        txt += "app.mainclass=com.viaoa.appstore.control.StartupController\n";

        txt += "\n";
        txt += "[JavaOptions]\n";
        txt += "java-options=-Xmx1g\n";
        txt += "\n";
        
        txt += "[ArgOptions]\n";
        txt += "arguments=single\n";
        // Important note:  $APPDIR does not work for ArgOptions/arguments
        txt += "arguments=RootDirectory=app\\appstore\\com\\viaoa\\oaappstore\n";

        log("updating app\\OAAppStore.cfg");
        log("new text="+txt);
        
        File file = new File("app\\OAAppStore.cfg");
        OutputStream os = new FileOutputStream(file);
        os.write(txt.getBytes());
        os.close();
    }
    
    
    public void run() throws Exception {
        log("running OAAppStore.exe");
        ProcessBuilder builder = new ProcessBuilder("OAAppStore.exe");
        Process process = builder.start();
        
        for (;;) {
            Thread.sleep(10 * 1000);
            if (!process.isAlive()) break;
        }
    }
    
    public static void main(String[] args) throws Exception {
        Installer installer = new Installer();
        if (installer.updateInstaller()) {
            installer.log("new oaappstoreinstaller.jar downloaded, re-running OAAppStore.exe now");
            installer.run();
        }
        else {
            installer.downloadAppStore();
            installer.updateConfig();
            installer.run();
        }
    }
}
