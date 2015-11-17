package com.sharman.yukon.view.activities.util;

import java.util.EnumMap;
import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by poten on 10/11/2015.
 */
@Deprecated
public class FlaggedEvent<T extends java.lang.Enum<T>> {
    private EnumSet<T> completeState;
    private EnumSet<T> failedEvents;
    private EnumMap<T, Boolean> currentState;
    private FlaggedEventCallback<T> callback;


    public FlaggedEvent(@Nonnull Class<T> enumClass, @Nonnull FlaggedEventCallback<T> callback) throws ClassCastException{
        this.completeState = EnumSet.allOf(enumClass);
        this.currentState = new EnumMap<>(enumClass);
        this.failedEvents = EnumSet.noneOf(enumClass);
        this.callback = callback;
    }


    /**
     * Registers a (successful or not) flag for an event
     * @param state The flag of the event that occurred
     * @param success The status of the event
     */
    public synchronized void registerFlag(@Nonnull T state, @Nullable Boolean success){
        success = (success==null) ? false:success;
        currentState.put(state, success);

        if(!success){
            failedEvents.add(state);
        }

        if(isCompleted()){
            if(isSuccessful()){
                callback.onSuccess();
            } else{
                callback.onFailure(failedEvents);
            }
        }
    }


    /**
     * Tells if all flags for an event was been set
     * @return Returns true if all events occurred, false otherwise
     */
    public boolean isCompleted(){
        return currentState.keySet().containsAll(completeState);
    }


    /**
     * Tells if all registered flags for an event was been successful
     * @return Returns true if all registered events was successful, false otherwise
     */
    public boolean isSuccessful(){
        return !currentState.values().contains(false);
    }


    public EnumMap<T, Boolean> getCurrentState() {
        return currentState;
    }
}
