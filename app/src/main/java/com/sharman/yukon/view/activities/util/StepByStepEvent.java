package com.sharman.yukon.view.activities.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by poten on 16/11/2015.
 */
public class StepByStepEvent {
    private Map<String, Boolean> currentSteps;
    private Set<String> allSteps;
    private Set<String> failedSteps;
    private FinishStepByStepEventCallback finishStepByStepEventCallback;
    private RegisterStepByStepEventCallback registerStepByStepEventCallback;
    private boolean alreadyFinished;


    public StepByStepEvent(@Nonnull Set<String> allSteps) {
        this.allSteps = allSteps;
        currentSteps = new HashMap<>();
        failedSteps = new HashSet<>();
        alreadyFinished = false;
    }


    /**
     * Registers a (successful or not) step for an event
     * @param step The step of the event that occurred
     * @param success The status of the step
     */
    public synchronized void registerStep(@Nonnull String step, @Nullable Boolean success){
        success = (success==null) ? false:success;
        currentSteps.put(step, success);

        if (!success) {
            failedSteps.add(step);
        }

        if(registerStepByStepEventCallback!=null){
            if(success){
                registerStepByStepEventCallback.onRegisterSuccess(step);
            } else{
                registerStepByStepEventCallback.onRegisterFailure(step);
            }
        }

        if (!alreadyFinished) {
            if (isCompleted()) {
                alreadyFinished = true;
                if(finishStepByStepEventCallback!=null) {
                    if (isSuccessful()) {
                        finishStepByStepEventCallback.onSuccess();
                    } else {
                        finishStepByStepEventCallback.onFailure(failedSteps);
                    }
                }
            }
        }
    }


    /**
     * Tells if all steps for an event was occurred
     * @return Returns true if all events occurred, false otherwise
     */
    public boolean isCompleted(){
        return currentSteps.keySet().containsAll(allSteps);
    }


    /**
     * Tells if all occurred steps for an event was been successful
     * @return Returns true if all occurred events was successful, false otherwise
     */
    public boolean isSuccessful(){
        return !currentSteps.values().contains(false);
    }



    public StepByStepEvent setRegisterStepCallback(@Nonnull RegisterStepByStepEventCallback registerStepByStepEventCallback){
        this.registerStepByStepEventCallback = registerStepByStepEventCallback;
        return this;
    }

    public StepByStepEvent setFinishStepCallback(@Nonnull FinishStepByStepEventCallback finishStepByStepEventCallback){
        this.finishStepByStepEventCallback = finishStepByStepEventCallback;
        if(allSteps.size()==0){
            finishStepByStepEventCallback.onSuccess();
        }
        return this;
    }

    public Map<String, Boolean> getCurrentSteps() {
        return currentSteps;
    }

    public Set<String> getFailedSteps() {
        return failedSteps;
    }

    public Set<String> getAllSteps() {
        return allSteps;
    }
}
