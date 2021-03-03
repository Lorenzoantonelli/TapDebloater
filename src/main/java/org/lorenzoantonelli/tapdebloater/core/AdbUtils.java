package org.lorenzoantonelli.tapdebloater.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class AdbUtils {

    /**
     * true if the os is Windows.
     */
    private final boolean isWindows;

    /**
     * Path of the adb binary.
     */
    private final String adbPath;

    /**
     * Flag indicating whether the aapt-arm-pie file is present on the device.
     */
    private boolean hasTool=false;

    /**
     * List of apps that shouldn't be removed.
     */
    private final List<String> blacklist=Arrays.asList("com.android.settings", "com.android.vending");

    /**
     * Android version of the device.
     */
    private int androidVersion;

    /**
     * @see #isWindows
     * @see #adbPath
     */
    public AdbUtils(){
        String os=(System.getProperty("os.name").toLowerCase());
        isWindows=os.contains("win");
        //adb for windows is no longer included
        adbPath=(isWindows)? "adb":((os.contains("mac"))?"./adb-macos":"./adb-linux");
    }

    /**
     * Starts the adb server.
     */
    public void startAdbServer(){
        runShell(adbPath + " start-server");
    }

    /**
     * Kills the adb server
     */
    public void killAdbServer(){
        runShell(adbPath+" kill-server");
    }

    /**
     * Runs the shell command given in input and returns the result as a String.
     * @param command the command to execute.
     * @return the result as a String, "-1" if the command failed.
     */
    private String runShell(String command){

        ProcessBuilder processBuilder = new ProcessBuilder();

        if (isWindows){
            processBuilder.command("cmd","/c", command);
        }

        else{
            processBuilder.command("bash","-c", command);
        }

        try{
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) output.append(line).append("\n");
            return (process.waitFor() == 0)? output.toString():"-1";
        } catch (IOException | InterruptedException e) {return "-1";}

    }

    /**
     * Check if the device is connected and if it has been detected.
     * @return true if the device has been found, false otherwise.
     */
    public boolean findDevice(){

        String[] result=runShell(adbPath+" devices").split("\n");

        if (result.length>1 && !result[1].equals("")){
            String status=result[1].split("\t")[1];
            return status.equals("device");
        }

        return false;
    }

    /**
     * Returns the package name of the current app running in the device as a string.
     * @return the current app's package name, as a string.
     */
    public String getPackageName(){

        String toParse=
                runShell(adbPath+" shell dumpsys activity recents | " +
                        ((isWindows)? "findstr /c:\"Recent #0\"":"grep \"\\<Recent #0\\>\""));

        if(toParse.contains("Recent #0") && (toParse.contains("A="))){
            String temp=toParse.substring(toParse.indexOf("A="));
            return temp.substring((androidVersion>10?temp.indexOf(":")+1:2),temp.indexOf("U=")-1);
        }

        return "";
    }

    /**
     * Returns the name of the current app running in the device as a string.
     * @return the current app's name, as a string.
     */
    public String getAppName(String packageName){

        if (!packageName.equals("")) {

            String result = runShell(adbPath + " shell pm list packages -f | " + ((isWindows) ? "findstr " : "grep ") + packageName);

            if (!result.equals("-1")) {
                String path = result.substring(result.indexOf("package:") + 8, result.indexOf(".apk") + 4);
                String name = runShell(adbPath + " shell /data/local/tmp/aapt-arm-pie d badging " + path + " | " + ((isWindows) ? "findstr" : "grep") + " application-label").split("\n")[0];
                return name.substring(name.indexOf(":'") + 2, name.length() - 1);
            }
        }
        return "";
    }

    /**
     * Removes the application whose package name was given in input.
     * @param packageName the package name of the app to remove.
     * @return true if the application has been removed, false otherwise.
     */
    public boolean removeApp(String packageName){
        if (!blacklist.contains(packageName)) {
            return (!runShell(adbPath + " shell pm uninstall -k --user 0 " + packageName).equals("-1"));
        }
        return false;
    }

    /**
     * Disables the application whose package name was given in input.
     * @param packageName the package name of the app to disable.
     * @return true if the application has been disabled, false otherwise.
     */
    public boolean disableApp(String packageName){
        if (!blacklist.contains(packageName)) {
            return (!runShell(adbPath + " shell pm disable-user --user 0 " + packageName).equals("-1"));
        }
        return false;
    }

    /**
     * Restore a previously uninstalled app.
     * @param packageName the package name of the app to restore.
     * @return true if the application has been restored, false otherwise.
     */
    public boolean restoreApp(String packageName){
        if (!packageName.equals("")) {
            return (!runShell(adbPath + " shell cmd package install-existing " + packageName).equals("-1"));
        }
        return false;
    }

    /**
     * Copy aapt-arm-pie (taken from https://github.com/Calsign/APDE/blob/master/APDE/src/main/assets/aapt-binaries/aapt-arm-pie)
     * in /data/local/tmp with 0755 permissions.
     * Updates the device Android version.
     */
    public void installTool(){
        runShell(adbPath+" push aapt-arm-pie /data/local/tmp");
        if(!runShell(adbPath+" shell chmod 0755 /data/local/tmp/aapt-arm-pie").equals("-1")){
            hasTool=true;
            androidVersion=Integer.parseInt(runShell(adbPath + " shell getprop ro.build.version.release").replace("\n",""));
        }
    }

    /**
     * Removes aapt-arm-pie from /data/local/tmp.
     */
    public void removeTool(){
        runShell(adbPath+" shell rm -rf /data/local/tmp/aapt-arm-pie");
    }

    /**
     * @return {@link #hasTool}
     */
    public boolean checkTools(){
        return hasTool;
    }
}