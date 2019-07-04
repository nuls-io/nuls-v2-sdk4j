package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

import java.math.BigInteger;

@ApiModel(name = "交易资产输出信息")
public class CoinToDto {

    @ApiModelProperty(description = "账户地址")
    private String address;
    @ApiModelProperty(description = "资产的链id")
    private int chainId;
    @ApiModelProperty(description = "资产id")
    private int assetId;
    @ApiModelProperty(description = "资产金额")
    private BigInteger amount;
    @ApiModelProperty(description = "锁定时间")
    private long lockTime = 0L;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public long getLockTime() {
        return lockTime;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }
}
