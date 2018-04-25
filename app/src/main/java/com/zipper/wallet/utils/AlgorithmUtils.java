package com.zipper.wallet.utils;

/**
 * Created by Administrator on 2018/4/24.
 */

public class AlgorithmUtils {

    /**
     * 密码等级算法
     * @param pwd
     * @return
     */
    public static int pwdLevel(String pwd){
        int level = 0 ;
        boolean flag = false;
        try{
            Integer.parseInt(pwd);
        }catch (Exception e){
            flag =true;
        }


        boolean isHigh = false;
        boolean hasUp = false;
        boolean hasLow = false;
        boolean hasNum = false;
        for(int i = 0 ; i < pwd.length(); i ++ ){
            int chars = (int)pwd.toCharArray()[i];
            if(chars>64 && chars<91){//包含大写字母
                hasUp = true;
            }else if(chars>96 && chars<123){//包含小写字母
                hasLow = true;
            }else if(chars>47 && chars<58){//数字
                hasNum = true;
            }else{//特殊字符
                isHigh = true;
            }
        }
        if(pwd.length()>7 && isHigh && hasLow && hasUp && hasNum){//长度足够，包含大小写+特殊字符+数字
            level = 4 ;
        }else if(pwd.length()>7 &&  isHigh && !hasLow && hasUp && hasNum){//长度足够，包含大写+特殊字符+数字
            level = 3 ;
        }else if(pwd.length()>7 && isHigh && hasLow && !hasUp && hasNum){//长度足够，包含小写+特殊字符+数字
            level = 3 ;
        }else if(pwd.length()>7 &&  !isHigh && hasLow && hasUp && hasNum){//长度足够，包含大、小写+数字
            level = 3 ;
        }else if(pwd.length()>7 && isHigh && !hasLow && !hasUp && hasNum){//长度足够，只包含特殊符号+数字
            level = 2 ;
        }else if(pwd.length()>7 && !isHigh && !hasLow && hasUp && hasNum){//长度足够，只包含大写字母+数字
            level = 2 ;
        }else if(pwd.length()>7 &&  !isHigh && hasLow && !hasUp && hasNum){//长度足够，只包含小写字母+数字
            level = 2 ;
        }else if(pwd.length()>7 && isHigh && !hasLow && !hasUp && !hasNum){//长度足够，只包含特殊符号
            level = 1 ;
        }else if(pwd.length()>7 && !isHigh && !hasLow && hasUp && !hasNum){//长度足够，只包含大写字母
            level = 1 ;
        }else if(pwd.length()>7 &&  !isHigh && hasLow && !hasUp && !hasNum){//长度足够，只包含小写字母
            level = 1 ;
        }else if((pwd.length()<8 && pwd.length() > 0 ) || !flag){//全是数字或长度太小
            level = 0 ;
        }else if(pwd.length() == 0){
            level = -1 ;
        }

        MyLog.i("AlgorithmUtils",String.format("pwdLevel isHigh:%s , hasLow:%s , hasUp:%s , hasNum:%s ,level:%s",isHigh , hasLow , hasUp , hasNum ,level));

        return  level;
    }


    public static boolean validAddress(String addr){
        boolean flag = true;
        if(addr.length()<34 || addr.length()>38){
            return false;
        }
        for(int i = 0 ; i < addr.length(); i ++ ){
            int chars = (int)addr.toCharArray()[i];
            if(chars>64 && chars<91){//包含大写字母
            }else if(chars>96 && chars<123){//包含小写字母
            }else if(chars>47 && chars<58){//数字
            }else{
                return false;
            }
        }
        return flag;
    }

}
