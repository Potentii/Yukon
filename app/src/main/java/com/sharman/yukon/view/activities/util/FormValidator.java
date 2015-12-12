package com.sharman.yukon.view.activities.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sharman.yukon.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by poten on 04/12/2015.
 */
public class FormValidator {
    private Map<FieldInputErrorOutputPair, EnumSet<EValidation>> fieldMap;
    private Map<Validatable, FieldInputErrorOutputPair> complexFieldSet;
    private Context context;

    private int invalidFieldColor;

    public enum EValidation{
        REQUIRED,
        INTEGER,
        FLOAT
    }

    public FormValidator(Context context){
        fieldMap = new HashMap<>();
        complexFieldSet = new HashMap<>();

        this.context = context;

        invalidFieldColor = context.getResources().getColor(R.color.input_line_invalid);
    }


    /**
     * Maps an {@link EditText} with its proper validation set.
     * @param field The {@code EditText} to be added
     * @param validation Its validation set
     * @return The same {@code FormValidator} object
     */
    public FormValidator addField(@Nonnull EditText field, @Nullable TextView errorOut, @Nonnull EnumSet<EValidation> validation){
        fieldMap.put(new FieldInputErrorOutputPair(field, errorOut), validation);
        return this;
    }

    /**
     * Removes an {@link EditText} from the map.
     * @param field The {@code EditText} to be removed
     * @return The same {@code FormValidator} object
     */
    public FormValidator removeField(@Nonnull EditText field){
        for (FieldInputErrorOutputPair key : fieldMap.keySet()) {
            if(key.getFieldIn().equals(field)){
                fieldMap.remove(key);
                break;
            }
        }
        return this;
    }



    /**
     * Adds a {@link Validatable} on the set.
     * @param field The {@code Validatable} to be added
     * @return The same {@code FormValidator} object
     */
    public FormValidator addComplexField(@Nonnull Validatable validatable, @Nullable EditText field, @Nullable TextView errorOut){
        complexFieldSet.put(validatable, new FieldInputErrorOutputPair(field, errorOut));
        //validatable.setFormValidator(this);
        return this;
    }

    /**
     * Removes a {@link Validatable} from the set.
     * @param validatable The {@code Validatable} to be removed
     * @return The same {@code FormValidator} object
     */
    public FormValidator removeComplexField(Validatable validatable){
        complexFieldSet.remove(validatable);
        //validatable.setFormValidator(null);
        return this;
    }


    /**
     * Verifies if all the registered fields are valid.
     * @return {@code True} if all fields are valid, {@code True} otherwise
     */
    public boolean isValid(){
        for (FieldInputErrorOutputPair key : fieldMap.keySet()) {
            EnumSet<EValidation> validation = fieldMap.get(key);
            EditText field = key.getFieldIn();
            if(!validation_eachField(field, validation)){
                return false;
            }
        }

        for (Validatable key : complexFieldSet.keySet()) {
            if(!key.isValid()){
                return false;
            }
        }

        return true;
    }




    public FormValidator doVisualValidation(){
        for (FieldInputErrorOutputPair key : fieldMap.keySet()) {
            visualValidation_eachField(key.getFieldIn(), fieldMap.get(key), key.getErrorOut());
        }

        for (Validatable key : complexFieldSet.keySet()) {
            FieldInputErrorOutputPair value = complexFieldSet.get(key);
            visualValidation_eachComplexField(key, value.getFieldIn(), value.getErrorOut());
        }

        return this;
    }




    private boolean validation_eachField(@Nonnull EditText field, @Nonnull EnumSet<EValidation> validation){
        if(validation.contains(EValidation.REQUIRED)){
            if(!containsAnything(field)) {
                return false;
            }
        }

        if(validation.contains(EValidation.INTEGER)){
            if(!containsInteger(field)){
                return false;
            }
        }

        if(validation.contains(EValidation.FLOAT)){
            if(!containsFloat(field)){
                return false;
            }
        }

        return true;
    }

    private String validation_eachField_toString(@Nonnull EditText field, @Nonnull EnumSet<EValidation> validation){
        if(validation.contains(EValidation.REQUIRED)){
            if(!containsAnything(field)) {
                return context.getResources().getString(R.string.output_defaultInvalidField_required);
            }
        }

        if(validation.contains(EValidation.INTEGER)){
            if(!containsInteger(field)){
                return context.getResources().getString(R.string.output_defaultInvalidField_integer);
            }
        }

        if(validation.contains(EValidation.FLOAT)){
            if(!containsFloat(field)){
                return context.getResources().getString(R.string.output_defaultInvalidField_float);
            }
        }

        return "";
    }



    private boolean containsAnything(@Nonnull EditText field){
        return field.getText().toString().trim().length() != 0;
    }

    private boolean containsInteger(@Nonnull EditText field){
        try{
            Integer.parseInt(field.getText().toString().trim());
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    private boolean containsFloat(@Nonnull EditText field){
        try{
            Float.parseFloat(field.getText().toString().trim());
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }







    private void visualValidation_eachField(@Nonnull EditText field, @Nonnull EnumSet<EValidation> validation, @Nullable  TextView errorOut){
        String errorText = validation_eachField_toString(field, validation);
        boolean valid = errorText.length() == 0;

        if (field != null) {
            if(valid) {
                field.getBackground().clearColorFilter();
            } else{
                field.getBackground().setColorFilter(invalidFieldColor, PorterDuff.Mode.SRC_IN);
            }
        }

        if(errorOut != null){
            if(valid){
                errorOut.setVisibility(View.GONE);
                errorOut.setText("");
            } else {
                errorOut.setVisibility(View.VISIBLE);
                errorOut.setText(errorText);
            }
        }
    }

    private void visualValidation_eachComplexField(@Nonnull Validatable validatable, @Nullable EditText field, @Nullable  TextView errorOut){
        boolean valid = validatable.isValid();

        if(field != null) {
            if(valid) {
                field.getBackground().clearColorFilter();
            } else {
                field.getBackground().setColorFilter(invalidFieldColor, PorterDuff.Mode.SRC_IN);
            }
        }

        if(errorOut != null){
            if(valid){
                errorOut.setVisibility(View.GONE);
                errorOut.setText("");
            } else {
                errorOut.setVisibility(View.VISIBLE);
                errorOut.setText(validatable.getInvalidText());
            }
        }
    }









    public class FieldInputErrorOutputPair{
        private EditText fieldIn;
        private TextView errorOut;

        public FieldInputErrorOutputPair(EditText fieldIn, TextView errorOut) {
            this.fieldIn = fieldIn;
            this.errorOut = errorOut;
        }

        public EditText getFieldIn() {
            return fieldIn;
        }
        public void setFieldIn(EditText fieldIn) {
            this.fieldIn = fieldIn;
        }
        public TextView getErrorOut() {
            return errorOut;
        }
        public void setErrorOut(TextView errorOut) {
            this.errorOut = errorOut;
        }
    }
}
