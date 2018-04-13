package com.zipper.wallet.bean;

/**
 * Created by AlMn on 2018/04/11.
 */

public class SwitchAccountBean {

    private String totalAmount;
    private String unit;
    private String type;//币种
    private String payerAddress;
    private String payeeName;
    private String payeeAddress;
    private String minerCost;
    private String remark;
    private String realAmount;

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayerAddress() {
        return payerAddress;
    }

    public void setPayerAddress(String payerAddress) {
        this.payerAddress = payerAddress;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getPayeeAddress() {
        return payeeAddress;
    }

    public void setPayeeAddress(String payeeAddress) {
        this.payeeAddress = payeeAddress;
    }

    public String getMinerCost() {
        return minerCost;
    }

    public void setMinerCost(String minerCost) {
        this.minerCost = minerCost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(String realAmount) {
        this.realAmount = realAmount;
    }
}
