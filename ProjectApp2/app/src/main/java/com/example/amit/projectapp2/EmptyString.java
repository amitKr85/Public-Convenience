package com.example.amit.projectapp2;

public class EmptyString {
    public static boolean isEmptyString(String arg){
        if(arg.replaceAll("\\W","").replaceAll("_","").length()==0)
            return true;
        else
            return false;
    }
    public static boolean isAnyEmptyString(String ... args){
        for(String arg:args){
            if(isEmptyString(arg))
                return true;
        }
        return false;
    }
}
