package com.sharman.yukon.view.activities.util;

import android.widget.EditText;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by poten on 04/12/2015.
 */
public class FormValidator {
    private Map<EditText, EnumSet<EValidation>> fieldMap;
    private Set<Validatable> complexFieldSet;

    public enum EValidation{
        REQUIRED,
        INTEGER,
        FLOAT
    }

    public FormValidator(){
        fieldMap = new HashMap<>();
        complexFieldSet = new HashSet<>();
    }


    /**
     * Maps an {@link EditText} with its proper validation set.
     * @param field The {@code EditText} to be added
     * @param validation Its validation set
     * @return The same {@code FormValidator} object
     */
    public FormValidator addField(EditText field, EnumSet<EValidation> validation){
        fieldMap.put(field, validation);
        return this;
    }

    /**
     * Removes an {@link EditText} from the map.
     * @param field The {@code EditText} to be removed
     * @return The same {@code FormValidator} object
     */
    public FormValidator removeField(EditText field){
        fieldMap.remove(field);
        return this;
    }



    /**
     * Adds a {@link Validatable} on the set.
     * @param field The {@code Validatable} to be added
     * @return The same {@code FormValidator} object
     */
    public FormValidator addComplexField(Validatable field){
        complexFieldSet.add(field);
        return this;
    }

    /**
     * Removes a {@link Validatable} from the set.
     * @param field The {@code Validatable} to be removed
     * @return The same {@code FormValidator} object
     */
    public FormValidator removeComplexField(Validatable field){
        complexFieldSet.remove(field);
        return this;
    }



    /**
     * Verifies if all the registered fields are valid.
     * @return {@code True} if all fields are valid, {@code True} otherwise
     */
    public boolean isValid(){
        boolean valid = true;
        for(EditText field : fieldMap.keySet()){
            EnumSet<EValidation> validation = fieldMap.get(field);

            if(validation.contains(EValidation.REQUIRED)){
                if(field.getText().toString().trim().length() == 0) {
                    return false;
                }
            }

            if(validation.contains(EValidation.INTEGER)){
                try{
                    Integer.parseInt(field.getText().toString());
                } catch (NumberFormatException e){
                    return false;
                }
            }

            if(validation.contains(EValidation.FLOAT)){
                try{
                    Float.parseFloat(field.getText().toString());
                } catch (NumberFormatException e){
                    return false;
                }
            }
        }


        for(Validatable field : complexFieldSet){
            if(!field.isValid()){
                return false;
            }
        }

        return true;
    }


}
