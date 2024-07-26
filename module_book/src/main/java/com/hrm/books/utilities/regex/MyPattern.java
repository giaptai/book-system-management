package com.hrm.books.utilities.regex;

import java.util.regex.Pattern;

public abstract class MyPattern {
    static Pattern authorPattern = Pattern.compile("\\w{1,50}");
    static Pattern passwordPattern = Pattern.compile("\\w{6,20}");
    public static Pattern emailPattern = Pattern.compile("^\\w{1,25}+@\\w{1,15}+\\.[a-zA-Z0-9]{1,10}$");

    public static boolean compareEmail(String email){
        return emailPattern.matcher(email).matches();
    }

    public static boolean comparePassword(String pass){
        return passwordPattern.matcher(pass).matches();
    }
}
