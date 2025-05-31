package com.shivam.userservice.utils;

public class IntegerUtils {
    public static boolean isValidId(Long num){
        return num != null && num > 0;
    }
}
