package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

import java.math.BigInteger;

@ApiModel
public class MultiSignTransferTxFeeDto {

    @ApiModelProperty(description = "多签地址对应公钥数量")
    private int pubKeyCount;
    @ApiModelProperty(description = "转账输入长度")
    private int fromLength;
    @ApiModelProperty(description = "转账输出长度")
    private int toLength;
    @ApiModelProperty(description = "交易备注")
    private String remark;
    @ApiModelProperty(description = "手续费单价" ,required = false)
    private BigInteger price;

    public int getPubKeyCount() {
        return pubKeyCount;
    }

    public void setPubKeyCount(int pubKeyCount) {
        this.pubKeyCount = pubKeyCount;
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
