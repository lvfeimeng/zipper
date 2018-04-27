package com.zipper.wallet.number;

import com.zipper.wallet.utils.MyLog;

import java.math.BigInteger;

/**
 * Created by Administrator on 2018/4/23.
 */

public class BigNumber {


    private String intStr,doubleStr;
    public BigNumber(String str){
        str = clearZero(str);
        MyLog.i("BigNumber","str:"+str+" length:"+str.length());
        int index = str.indexOf(".");
        MyLog.i("BigNumber","index:"+index);
        String[] strs = new String[]{index == 0 ? "0":str.substring(0,index>-1?index:str.length()),index>-1?str.substring(index+1,str.length()):"0"};
        MyLog.i("BigNumber","strs:"+strs.length);
        setIntStr( strs[0]);
        setDoubleStr(strs[1]);
    }


    public String getIntStr() {
        return intStr;
    }

    public void setIntStr(String intStr) {
        this.intStr = intStr;
    }

    public String getDoubleStr() {
        return doubleStr;
    }

    public void setDoubleStr(String doubleStr) {
        this.doubleStr = doubleStr;
    }

    public BigInteger getIntBig() {
        return new BigInteger(intStr);
    }


    public BigInteger getDoubleBig() {
        return new BigInteger(doubleStr);
    }
    public BigInteger getBigInteger() {
        return new BigInteger(intStr+doubleStr);
    }

    @Override
    public String toString() {
        return intStr+"."+doubleStr;
    }


    /*
 * public BigInteger add(BigInteger val):加
 * public BigInteger subtract(BigInteger val):减
 * public BigInteger multiply(BigInteger val):乘
 * public BigInteger divide(BigInteger val):除
 * public BigInteger[] divideAndRemainder(BingInteger val):返回商和余数的数组
 */

    /**
     * 加
     * @param val
     * @return
     */
    public BigNumber add(BigNumber val){
        BigNumber[] numbers = castSameLenth(this,val);
        int poitPosition = numbers[0].getDoubleStr().length();
        BigInteger intBig = numbers[0].getIntBig().add(numbers[1].getIntBig());
        BigInteger doubleBig = numbers[0].getDoubleBig().add(numbers[1].getDoubleBig());
        if(doubleBig.toString().length()> numbers[0].getDoubleBig().toString().length()){
            intBig = intBig.add(new BigInteger("1"));
            doubleBig = new BigInteger(doubleBig.toString().substring(1));
        }else{

            int cha = numbers[0].getDoubleStr().length() - doubleBig.toString().length();
            String str = doubleBig.toString();
            for(int i = 0 ; i < cha; i ++ ){
                str = "0"+str;
            }
            doubleBig = new BigInteger(str);
        }
        return new BigNumber(intBig+"."+doubleBig);
    }

    /**
     * 减
     * @param val
     * @return
     */
    public BigNumber subtract(BigNumber val){
        BigNumber[] numbers = castSameLenth(this,val);
        int poitPosition = numbers[0].getDoubleStr().length();
        String left = numbers[0].getIntStr()+numbers[0].getDoubleStr();
        String right = numbers[1].getIntStr()+numbers[1].getDoubleStr();
        MyLog.i("BigNumber","subtract left:"+left+" right:"+right);
        String sub = new BigInteger(left).subtract(new BigInteger(right)).toString();
        MyLog.i("BigNumber","subtract sub:"+sub+" poitPosition:"+poitPosition);
        if(sub.indexOf("-") == 0){
            sub = sub.substring(1);
            if(sub.length()<poitPosition){
                for(int i = 0 ; i < poitPosition - sub.length(); i ++ ){
                    sub = "0"+sub;
                }
                sub = "0."+sub;
            }else{
                StringBuilder sb = new StringBuilder(sub);
                sb.insert(sb.length() - poitPosition,(sb.length() - poitPosition == 0? "0.":"."));
                sub = sb.toString();
            }
            sub = "-"+sub;
        }else{
            if(sub.length()<poitPosition){
                for(int i = 0 ; i < poitPosition - sub.length(); i ++ ){
                    sub = "0"+sub;
                }
                sub = "0."+sub;
            }else{
                StringBuilder sb = new StringBuilder(sub);
                sb.insert(sb.length() - poitPosition,(sb.length() - poitPosition == 0? "0.":"."));
                sub = sb.toString();
            }
        }
        MyLog.i("BigNumber","subtract :"+intStr+"."+doubleStr);
        return new BigNumber(sub);

    }

    /**
     * 乘
     * @param val
     * @return
     */
    public BigNumber multiply(BigNumber val){
        BigNumber[] numbers = castSameLenth(this,val);

        MyLog.i("BigNumber","multiply numbers0:"+numbers[0].toString());
        MyLog.i("BigNumber","multiply numbers1:"+numbers[1].toString());
        int poitPosition = numbers[0].getDoubleStr().length()*2;
        BigInteger big = numbers[0].getBigInteger().multiply(numbers[1].getBigInteger());
        StringBuilder sb = new StringBuilder(big.toString());
        MyLog.i("BigNumber","multiply big:"+big);
        if(!big.toString().equals("0")) {
            if(sb.length()<poitPosition){
                int cha = poitPosition - sb.length();
                for(int i = 0 ; i <  cha; i ++){
                    sb.insert(0,"0");
                }
            }
            sb.insert(sb.length() - poitPosition, (sb.length() - poitPosition == 0? "0.":"."));
        }
        return new BigNumber(sb.toString());

    }

    /**
     * 除
     * @param val
     * @return
     */
    public BigNumber divide(BigNumber val){
        BigNumber[] numbers = castSameLenth(this,val);
        BigInteger[] big = numbers[0].getBigInteger().divideAndRemainder(numbers[1].getBigInteger());
        BigInteger yu = new BigInteger(big[1].toString()+"00000000").divide(numbers[1].getBigInteger());
        return new BigNumber(big[0].toString()+"."+ yu.toString());

    }

    public BigNumber[] castSameLenth(BigNumber val1,BigNumber val2){
        MyLog.i("BigNumber","castSameLenth val1:"+val1);
        MyLog.i("BigNumber","castSameLenth val2:"+val2);
        String valStr = val1.getDoubleStr();
        String val2Str = val2.getDoubleStr();
        if(valStr.length() != val2Str.length()){
            int abs = valStr.length()- val2Str.length();
            if(valStr.length() > val2Str.length()){
                for(int i = 0 ; i < Math.abs(abs); i ++ ){
                    val2Str+="0";
                }
                val2.setDoubleStr(val2Str);
            }else{
                for(int i = 0 ; i < Math.abs(abs); i ++ ){
                    valStr+="0";
                }
                val1.setDoubleStr(valStr);
            }
        }
        MyLog.i("BigNumber","castSameLenth val1:"+val1);
        MyLog.i("BigNumber","castSameLenth val2:"+val2);
        return  new BigNumber[]{val1,val2};
    }


    private String clearZero(String str){
        if(str.length()< 8 || str.indexOf(".") == -1){
            return str;
        }
        char[] strs = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean falg = true;
        for(int i = strs.length ; i > 0; i -- ){
            if(falg && strs[i-1] == '0'){
                continue;
            }else if((falg && strs[i-1] == '.') || (falg && strs[i-1] != '0')){
                falg = false;
            }
            sb.append(strs[i-1]);
        }
        strs = sb.toString().toCharArray();
        sb = new StringBuilder();
        for(int i = strs.length ; i > 0; i -- ){
            sb.append(strs[i-1]);
        }
        if(sb.indexOf(".") == sb.length()-1){
            sb.append("00");
        }
        return  sb.toString();


    }

}
