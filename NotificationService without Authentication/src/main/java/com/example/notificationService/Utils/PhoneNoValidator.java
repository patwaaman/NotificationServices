package com.example.notificationService.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNoValidator {
    public static boolean isValidPhoneNo(String p_no){
        Pattern pattern = Pattern.compile("(0/91)?[6-9][0-9]{9}");
        Matcher match = pattern.matcher(p_no);
        return (match.find() && match.group().equals(p_no));
    }
}