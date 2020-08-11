package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

import java.math.BigInteger;

@ApiModel
public class CrossTransferTxFeeDto {

    @ApiModelProperty(description = "资产链ID")
    private int assetChainId;
    @ApiModelProperty(description = "资产ID")
    private int assetId;
    @ApiModelProperty(description = "转账地址数量")
    private int addressCount;
    @ApiModelProperty(description = "转账输入长度")
    private int fromLength;
    @ApiModelProperty(description = "转账输出长度")
    private int toLength;
    @ApiModelProperty(description = "交易备注")
    private String remark;
    @ApiModelProperty(description = "手续费单价" ,required = false)
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

    public int getAssetChainId() {
        return assetChainId;
    }

    public void setAssetChainId(int assetChainId) {
        this.assetChainId = assetChainId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }
}
