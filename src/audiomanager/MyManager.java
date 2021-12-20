/*
 * Copyright (C) JasonPercus Systems, Inc - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by JasonPercus, December 2021
 */
package audiomanager;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.jasonpercus.plugincreator.models.Context;
import com.jasonpercus.plugincreator.models.Extension;
import com.jasonpercus.plugincreator.models.Target;
import com.jasonpercus.plugincreator.models.events.KeyUp;
import com.jasonpercus.plugincreator.models.events.WillAppear;
import com.jasonpercus.plugincreator.models.events.WillDisappear;



/**
 * Allows control of sound tiles
 * @author JasonPercus
 * @version 1.0
 */
@SuppressWarnings({"LeakingThisInConstructor", "NestedSynchronizedStatement"})
public final class MyManager extends com.jasonpercus.plugincreator.EventManager {

    
    
//ATTRIBUTS
    /**
     * Corresponds to the mute image
     */
    private byte[] muteImg;
    
    /**
     * Corresponds to the unmute image
     */
    private byte[] unmuteImg;
    
    /**
     * Corresponds to the list of "mute" action contexts
     */
    private final java.util.List<Context> MUTE_ACTIONS = new java.util.ArrayList<>();
    
    /**
     * Corresponds to the full list of action contexts
     */
    private final java.util.List<Context> All_ACTIONS = new java.util.ArrayList<>();
    

    
//CONSTRUCTOR
    /**
     * Create a sound tiles manager
     */
    public MyManager() {
        try {
            this.muteImg = java.nio.file.Files.readAllBytes(new java.io.File("images/mute.png").toPath());
        } catch (java.io.IOException ex) {
        }
        try {
            this.unmuteImg = java.nio.file.Files.readAllBytes(new java.io.File("images/unmute.png").toPath());
        } catch (java.io.IOException ex) {
        }
    }
    
    
    
//ON
    /**
     * When the EventManager has been created
     */
    @Override
    public void onCreate() {
        Audio.manager = this;
        Audio.init();
    }
    
    /**
     * When the EventManager is destroyed. This happens before the app is closed.
     */
    @Override
    public void onDestroy() {
        Audio.stop();
    }
    
    
    
//EVENTS
    /**
     * When the user releases a key, the plugin will receive the keyUp event
     * @param event Corresponds to the Stream Deck event
     * @param context Corresponds to the context (or ID) of the action
     * @param builder Allows to deserialize the received json
     */
    @Override
    public void keyUp(KeyUp event, Context context, GsonBuilder builder) {
        if(event.action.equals("discrease")){
            int value = getValue(event.payload.settings, builder);
            value = Audio.discrease(value);
            synchronized(All_ACTIONS){
                for(Context c : All_ACTIONS)
                    setTitle(c, value + "%", Target.HARDWARE);
            }
        }
        if(event.action.equals("increase")){
            int value = getValue(event.payload.settings, builder);
            value = Audio.increase(value);
            synchronized(All_ACTIONS){
                for(Context c : All_ACTIONS)
                    setTitle(c, value + "%", Target.HARDWARE);
            }
        }
        if(event.action.equals("mute")){
            if(Audio.toggleMute()){
                //il est mute
                synchronized(MUTE_ACTIONS){
                    for(Context c : MUTE_ACTIONS)
                        setImage(c, muteImg, Extension.PNG, Target.BOTH);
                }
            }else{
                //il n'est pas mute
                synchronized(MUTE_ACTIONS){
                    for(Context c : MUTE_ACTIONS)
                        setImage(c, unmuteImg, Extension.PNG, Target.BOTH);
                }
            }
        }
        if(event.action.equals("set")){
            int value = getValue(event.payload.settings, builder);
            value = Audio.set(value);
            synchronized(All_ACTIONS){
                for(Context c : All_ACTIONS)
                    setTitle(c, value + "%", Target.BOTH);
            }
        }
    }

    /**
     * When an instance of an action is displayed on the Stream Deck, for example when the hardware is first plugged in, or when a folder containing that action is entered, the plugin will receive a willAppear event
     * @param event Corresponds to the Stream Deck event
     * @param context Corresponds to the context (or ID) of the action
     * @param builder Allows to deserialize the received json
     */
    @Override
    public void willAppear(WillAppear event, Context context, GsonBuilder builder) {
        synchronized(MUTE_ACTIONS){
            if(event.action.equals("mute")){
                if(!MUTE_ACTIONS.contains(context))
                    MUTE_ACTIONS.add(context);
            }
        }
        synchronized(All_ACTIONS){
            if(event.action.equals("mute") || event.action.equals("increase") || event.action.equals("discrease") || event.action.equals("show")){
                if(!All_ACTIONS.contains(context))
                    All_ACTIONS.add(context);
            }
        }
        if(event.action.equals("mute")){
            if(Audio.isMute()){
                //il est mute
                setImage(context, muteImg, Extension.PNG, Target.BOTH);
            }else{
                //il n'est pas mute
                setImage(context, unmuteImg, Extension.PNG, Target.BOTH);
            }
            setTitle(context, Audio.getVolume() + "%", Target.HARDWARE);
        }
        if(event.action.equals("increase")){
            setTitle(context, Audio.getVolume() + "%", Target.BOTH);
        }
        if(event.action.equals("discrease")){
            setTitle(context, Audio.getVolume() + "%", Target.BOTH);
        }
        if(event.action.equals("show")){
            setTitle(context, Audio.getVolume() + "%", Target.BOTH);
        }
    }

    /**
     * When an instance of an action ceases to be displayed on Stream Deck, for example when switching profiles or folders, the plugin will receive a willDisappear event
     * @param event Corresponds to the Stream Deck event
     * @param context Corresponds to the context (or ID) of the action
     * @param builder Allows to deserialize the received json
     */
    @Override
    public void willDisappear(WillDisappear event, Context context, GsonBuilder builder) {
        synchronized(MUTE_ACTIONS){
            if(event.action.equals("mute")){
                if(MUTE_ACTIONS.contains(context))
                    MUTE_ACTIONS.remove(context);
            }
        }
        synchronized(All_ACTIONS){
            if(event.action.equals("mute") || event.action.equals("increase") || event.action.equals("discrease") || event.action.equals("show")){
                if(All_ACTIONS.contains(context))
                    All_ACTIONS.remove(context);
            }
        }
    }
    
    
    
//METHODES PUBLICS
    /**
     * When the sound is muted or not from the computer
     * @param isMute Determines whether the sound is muted
     */
    public synchronized void muteChangedListener(boolean isMute){
        synchronized(MUTE_ACTIONS){
            if(isMute){
                for(Context context : MUTE_ACTIONS){
                    setImage(context, muteImg, Extension.PNG, Target.BOTH);
                }
            }else{
                for(Context context : MUTE_ACTIONS){
                    setImage(context, unmuteImg, Extension.PNG, Target.BOTH);
                }
            }
        }
    }
    
    /**
     * When the sound volume has changed from the computer
     * @param value Corresponds to the new percentage of sound
     */
    public synchronized void volumeChangedListener(int value){
        synchronized(All_ACTIONS){
            for(Context context : All_ACTIONS){
                setTitle(context, value + "%", Target.BOTH);
            }
        }
    }
    
    
    
//METHODE PRIVATE
    /**
     * Returns the value included in the settings at the click of a button
     * @param json Corresponds to settings
     * @param builder Allows to deserialize the received json
     * @return Allows to deserialize the received json
     */
    private int getValue(String json, GsonBuilder builder){
        Gson gson = builder.registerTypeAdapter(String.class, new ValueDeserializer()).create();
        String value = gson.fromJson(json, String.class);
        if(value != null)
            return Integer.parseInt(value);
        return -1;
    }
    
    
    
//CLASS
    /**
     * Allows to deserialize the received json
     * @author JasonPercus
     * @version 1.0
     */
    private class ValueDeserializer implements JsonDeserializer<String>{

        @Override
        public String deserialize(JsonElement je, java.lang.reflect.Type type, JsonDeserializationContext jdc) throws JsonParseException {
            JsonObject obj = je.getAsJsonObject();

            JsonPrimitive increase  = obj.getAsJsonPrimitive("increaseValue");
            JsonPrimitive discrease = obj.getAsJsonPrimitive("discreaseValue");
            JsonPrimitive set       = obj.getAsJsonPrimitive("setValue");
            
            if(increase != null && discrease == null && set == null)
                return increase.getAsString();
            
            if(increase == null && discrease != null && set == null)
                return discrease.getAsString();
            
            if(increase == null && discrease == null && set != null)
                return set.getAsString();
            
            return "5";
        }
        
    }
    
    
    
}