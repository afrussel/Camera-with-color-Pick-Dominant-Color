package io.left.core.assignment.data.helper;

import android.content.Context;

import io.left.core.util.lib.shared_preference.SharedPreferencePrivateMode;



public class SharedPreferenceHelper extends SharedPreferencePrivateMode {
    public SharedPreferenceHelper(Context mContext) {
        super(mContext);
    }

    private static SharedPreferenceHelper instance;

    synchronized public static SharedPreferenceHelper onInstance(Context context){

        if(instance==null){
            instance = new SharedPreferenceHelper(context);
        }
        return instance;
    }

    private static String FirstName = "first_name";
    private static String LastName = "last_name";
    private static String IMAGE="image";

    private static String COLOR="color";



    public  String getFirstName() {
        return readString(FirstName,"");
    }
    public  String getImage() {
        return readString(IMAGE,"");
    }
    public  String getColor() {
        return readString(COLOR,"");
    }

    public  void setFirstName(String firstName) {
        writeString(FirstName,firstName);
    }

    public  void setColor(String setColor) {
        writeString(COLOR,setColor);
    }
    public  void setImage(String image) {
        writeString(IMAGE,image);
    }

    public  String getLastName() {
        return readString(LastName,"");
    }

    public  void setLastName(String lastName) {
        writeString(LastName,lastName);
    }
}
