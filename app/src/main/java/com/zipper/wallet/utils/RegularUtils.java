package com.zipper.wallet.utils;

import java.util.regex.Pattern;

/**
 * Created by admin on 2018/4/24.
 */

public class RegularUtils {

    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    public static boolean idEmail(String email){
        return Pattern.matches(REGEX_EMAIL, email);
    }

}
