package com.sharman.yukon.model;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 20/10/2015.
 */
public class Answer extends JSONObject {
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum AnswerJSONKeys{
        ANSWER_ARRAY("answerArray");

        private String key;
        private AnswerJSONKeys(String key){
            this.key = key;
        }
        public String getKey(){
            return this.key;
        }
    }

    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public Answer(String[] answerArray){
        super();
        try {
            this.setAnswerArray(answerArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Answer(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public String getFormattedAnswerString(){
        String answerStr = "";
        String[] answer = getAnswerArray();

        for(int i=0; i<answer.length; i++){
            answerStr += answer[i] + (i<answer.length-1?", ":"");
        }

        return answerStr;
    }


    @Nullable
    public Boolean compareAnswerTo(Answer otherAnswer){
        String[] answerArrayTHIS = getAnswerArray();
        String[] answerArrayOTHER = otherAnswer.getAnswerArray();

        if(answerArrayTHIS.length != answerArrayOTHER.length){
            return null;
        }

        for(int i=0; i<answerArrayTHIS.length; i++){
            if(!answerArrayTHIS[i].equals(answerArrayOTHER[i])){
                return false;
            }
        }

        return true;
    }


    /**
     * Receives an array of indexes (starting at 0), and return a same size array of {@code String} containing the alphabitical equivalent.
     * @param indexArray the array of indexes
     * @return The equivalents in alphabetical form
     */
    public static String[] convertIntArray_AlphabetArray(int[] indexArray){
        String[] alphabetArray = new String[indexArray.length];

        for(int i=0; i<alphabetArray.length; i++){
            alphabetArray[i] = convertInt_Alphabet(indexArray[i]);
        }

        return alphabetArray;
    }

    /**
     * Transforms an index (starting at 0) into an equivalent in its alphabetical form. Examples:
     * <pre>
     * index = 0,  returns A
     * index = 25, returns Z
     * index = 26, returns AA
     * index = 51, returns ZZ
     * </pre>
     * @param index The index to be transformed
     * @return The alphabetical form of the given index
     */
    public static String convertInt_Alphabet(int index){
        String stringIndex = "";
        if(index>=0){
            int loops = ((int) Math.floor(index / 26)) + 1;
            int alphabeticalPos = index - ((loops - 1) * 26);

            for (int i = 0; i < loops; i++) {
                char letter = (char) (alphabeticalPos + 1 + 64);
                stringIndex += letter;
            }
        }

        return stringIndex;
    }




    public static int[] convertAlphabetArray_IntArray(String[] alphabetArray){
        int[] indexArray = new int[alphabetArray.length];

        for(int i=0; i<indexArray.length; i++){
            indexArray[i] = convertAlphabet_Int(alphabetArray[i]);
        }

        return indexArray;
    }

    public static int convertAlphabet_Int(String alphabetArray){
        alphabetArray = alphabetArray.trim().toUpperCase();
        if(alphabetArray.length() == 0){
            return -1;
        }

        char[] charArray = alphabetArray.toCharArray();
        if(charArray[0] < 65 || charArray[0] > 90){
            return -1;
        }

        int index = ((charArray.length-1) * 26) + (charArray[0] - 64 - 1);
        return index;
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *AnswerArray:
    public String[] getAnswerArray(){
        JSONArray titleJSONArray = super.optJSONArray(AnswerJSONKeys.ANSWER_ARRAY.getKey());
        String[] titleArray = new String[titleJSONArray.length()];
        for(int i=0; i<titleJSONArray.length(); i++){
            try {
                titleArray[i] = titleJSONArray.getString(i);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return titleArray;
    }
    public void setAnswerArray(String[] answerArray) throws JSONException{
        super.putOpt(AnswerJSONKeys.ANSWER_ARRAY.getKey(), new JSONArray(answerArray));
    }
}
