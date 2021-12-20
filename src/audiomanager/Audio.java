/*
 * Copyright (C) JasonPercus Systems, Inc - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by JasonPercus, December 2021
 */
package audiomanager;



/**
 * This class manages the main windows volume
 * @author JasonPercus
 * @version 1.0
 */
@SuppressWarnings({"SleepWhileInLoop", "SleepWhileHoldingLock"})
public final class Audio {
    
    
    
//ATTRIBUTS STATICS
    /**
     * Determines whether or not the class has been initialized
     */
    public static boolean inited = false;
    
    /**
     * Corresponds to the manager who should be informed of the results
     */
    public static MyManager manager;
    
    /**
     * Corresponds to the id of the speaker windows
     */
    private static String audioID;
    
    /**
     * Determines if the speaker windows is muted
     */
    private static boolean muted;
    
    /**
     * Corresponds to the value of the windows volume
     */
    private static int volumeValue;
    
    /**
     * Corresponds to a thread which will regularly analyze the value of the windows volume as well as its muted value
     */
    private static Analyser analyser = null;
    
    /**
     * Determines whether the scanning process is in progress or not
     */
    private static boolean analyse;
    
    
    
//METHODES PUBLICS STATICS
    /**
     * Initializes the class
     */
    public static void init(){
        if(!inited){
            audioID = id();
            analyse = true;
            startAnalyser();
        }
    }
    
    /**
     * Stop analysis
     */
    public synchronized static void stop(){
        analyse = false;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            manager.log(ex);
        }
    }
    
    /**
     * Returns whether or not the windows sound is muted or not
     * @return Returns whether or not the windows sound is muted or not
     */
    public synchronized static boolean isMute(){
        return muted = isMute(audioID);
    }
    
    /**
     * Mute or restore windows sound
     * @return Returns true if the sound is muted, otherwise false
     */
    public synchronized static boolean toggleMute(){
        if(isMute()){
            unmute(audioID);
            return false;
        }else{
            mute(audioID);
            return true;
        }
    }
    
    /**
     * Returns the value of the windows volume [0, 100]
     * @return Returns the value of the windows volume
     */
    public synchronized static int getVolume(){
        return volumeValue = (int) getVolume(audioID);
    }
    
    /**
     * Increase windows volume
     * @param value Corresponds to the new value of the windows sound [0, 100]
     * @return Returns the new value of the windows sound
     */
    public synchronized static int increase(int value){
        int volume = (int) getVolume(audioID);
        volume += value;
        if(volume > 100) volume = 100;
        
        setVolume(audioID, volume);
        
        return volume;
    }
    
    /**
     * Decrease windows volume
     * @param value Corresponds to the new value of the windows sound [0, 100]
     * @return Returns the new value of the windows sound
     */
    public synchronized static int discrease(int value){
        int volume = (int) getVolume(audioID);
        volume -= value;
        if(volume < 0) volume = 0;
        
        setVolume(audioID, volume);
        
        return volume;
    }
    
    /**
     * Set windows volume
     * @param value Corresponds to the new value of the windows sound [0, 100]
     * @return Returns the new value of the windows sound
     */
    public synchronized static int set(int value){
        setVolume(audioID, value);
        
        return value;
    }
    
    
    
//METHODES PRIVATES STATICS
    /**
     * Starts the analyzer will regularly analyze the value of the windows volume as well as its muted value
     */
    private synchronized static void startAnalyser(){
        if(analyser == null){
            analyser = new Analyser();
            analyser.start();
        }
    }
    
    /**
     * Returns the id of the windows speaker
     * @return Returns the id of the windows speaker
     */
    private synchronized static String id(){
        String line = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "Audio.bat");
            Process p = pb.start();
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                bw.write("1");
                bw.flush();
                line=br.readLine();
            }
        } catch (java.io.IOException ex) {}
        return line;
    }
    
    /**
     * Returns whether or not the windows sound is muted or not
     * @param id Corresponds to the id of the speaker windows
     * @return Returns whether or not the windows sound is muted or not
     */
    private synchronized static boolean isMute(String id){
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "Audio.bat");
            Process p = pb.start();
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                bw.write("2");
                bw.flush();
                br.readLine();
                bw.write(id);
                bw.flush();
                return br.readLine().equals("1");
            }
        } catch (java.io.IOException ex) {
            manager.log(ex);
        }
        return false;
    }
    
    /**
     * Returns the value of the windows volume [0, 100]
     * @param id Corresponds to the id of the speaker windows
     * @return Returns the value of the windows volume
     */
    private synchronized static float getVolume(String id){
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "Audio.bat");
            Process p = pb.start();
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                bw.write("3");
                bw.flush();
                br.readLine();
                bw.write(id);
                bw.flush();
                return Float.parseFloat(br.readLine());
            }
        } catch (java.io.IOException ex) {
            manager.log(ex);
        }
        return -1f;
    }
    
    /**
     * Mutes main windows sound
     * @param id Corresponds to the id of the speaker windows
     */
    private synchronized static void mute(String id){
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "Audio.bat");
            Process p = pb.start();
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                bw.write("4");
                bw.flush();
                br.readLine();
                bw.write(id);
                bw.flush();
            }
        } catch (java.io.IOException ex) {
            manager.log(ex);
        }
    }
    
    /**
     * Unmutes main windows sound
     * @param id Corresponds to the id of the speaker windows
     */
    private synchronized static void unmute(String id){
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "Audio.bat");
            Process p = pb.start();
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                bw.write("5");
                bw.flush();
                br.readLine();
                bw.write(id);
                bw.flush();
            }
        } catch (java.io.IOException ex) {
            manager.log(ex);
        }
    }
    
    /**
     * Modifies the value of the main sound of windows
     * @param id Corresponds to the id of the speaker windows
     * @param volume Corresponds to the new sound value [0, 100]
     */
    private synchronized static void setVolume(String id, float volume){
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "Audio.bat");
            Process p = pb.start();
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                bw.write("6");
                bw.flush();
                br.readLine();
                bw.write(id);
                bw.flush();
                br.readLine();
                bw.write(""+((int)volume));
                bw.flush();
            }
        } catch (java.io.IOException ex) {
            manager.log(ex);
        }
    }
    
    
    
//CLASS STATIC
    /**
     * This static class represents the windows sound analyzer
     * @author JasonPercus
     * @version 1.0
     */
    private static class Analyser extends Thread {

        
        
        /**
         * Starts analysis
         */
        @Override
        public void run() {
            while(analyse){
                try {
                    Thread.sleep(1000);
                    
                    boolean res = isMute(audioID);
                    if(res != muted){
                        muted = res;
                        manager.muteChangedListener(res);
                    }
                    
                    int val = (int) getVolume(audioID);
                    if(val != volumeValue){
                        volumeValue = val;
                        manager.volumeChangedListener(val);
                    }
                    
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        
        
    }
    
    
    
}