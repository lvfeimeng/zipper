package com.zipper.wallet.bean;

public class CoinsBean {

    private int id;
    private int type;
    private String name;
    private String full_name;
    private String addr_algorithm;
    private String addr_algorithm_param;
    private String sign_algorithm;
    private String sing_algorithm_param;
    private String token_type;
    private String token_addr;
    private long decimals;
    private boolean is_default;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getAddr_algorithm() {
        return addr_algorithm;
    }

    public void setAddr_algorithm(String addr_algorithm) {
        this.addr_algorithm = addr_algorithm;
    }

    public String getAddr_algorithm_param() {
        return addr_algorithm_param;
    }

    public void setAddr_algorithm_param(String addr_algorithm_param) {
        this.addr_algorithm_param = addr_algorithm_param;
    }

    public String getSign_algorithm() {
        return sign_algorithm;
    }

    public void setSign_algorithm(String sign_algorithm) {
        this.sign_algorithm = sign_algorithm;
    }

    public String getSing_algorithm_param() {
        return sing_algorithm_param;
    }

    public void setSing_algorithm_param(String sing_algorithm_param) {
        this.sing_algorithm_param = sing_algorithm_param;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getToken_addr() {
        return token_addr;
    }

    public void setToken_addr(String token_addr) {
        this.token_addr = token_addr;
    }

    public long getDecimals() {
        return decimals;
    }

    public void setDecimals(long decimals) {
        this.decimals = decimals;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    private boolean selected;
    private String icon;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
