package org.lorenzoantonelli.tapdebloater.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
     * @see #isWindows
     * @see #adbPath
     */
    public AdbUtils(){
        String os=(System.getProperty("os.name").toLowerCase());
        isWindows=os.contains("win");
        adbPath=(isWindows)? "adb.exe":((os.contains("mac"))?"./adb-macos":"./adb-linux");
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
        String[] toParse=
                runShell(adbPath+" shell dumpsys activity recents | " +
                        ((isWindows)? "findstr":"grep")+" Recent").split("\n");

        for(String toCheck:toParse){
            if(toCheck.contains("Recent #0") && toCheck.contains("A")){
                return toCheck.substring(toCheck.indexOf("A")+2,toCheck.indexOf("U"));
            }
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
                String path = result.substring(result.indexOf("package:") + 8, result.indexOf("apk") + 3);
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
        return (!runShell(adbPath+" shell pm uninstall -k --user 0 "+packageName).equals("-1"));
    }

    /**
     * Copy aapt-arm-pie (taken from https://github.com/Calsign/APDE/blob/master/APDE/src/main/assets/aapt-binaries/aapt-arm-pie)
     * in /data/local/tmp with 0755 permissions.
     */
    public void installTool(){
        runShell(adbPath+" push aapt-arm-pie /data/local/tmp");
        if(!runShell(adbPath+" shell chmod 0755 /data/local/tmp/aapt-arm-pie").equals("-1")){
            hasTool=true;
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