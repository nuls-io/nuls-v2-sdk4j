package io.nuls.v2.model.dto;

import java.math.BigInteger;

public class TransferTxFeeDto {

    private int addressCount;

    private int fromLength;

    private int toLength;

    private String remark;

    private BigInteger price;

    public int getAddressCount() {
        return addressCount;
    }

    public void setAddressCount(int addressCount) {
        this.addressCount = addressCount;
    }

    public int getFromLength() {
        return fromLength;
    }

    public void setFromLength(int fromLength) {
        this.fromLength = fromLength;
    }

    public int getToLength() {
        return toLength;
    }

    public void setToLength(int toLength) {
        this.toLength = toLength;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }
}
