package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

@ApiModel
public class CoinTosDto {

    @ApiModelProperty(description = "账户地址")
    protected String address;

    @ApiModelProperty(description = "资产发行链的id")
    protected int assetsChainId;

    @ApiModelProperty(description = "资产id")
    protected int assetsId;

    @ApiModelProperty(description = "数量")
    protected String amount;

    @ApiModelProperty(description = "解锁时间，-1为永久锁定")
    private long lockTime;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAssetsChainId() {
        return assetsChainId;
    }

    public void setAssetsChainId(int assetsChainId) {
        this.assetsChainId = assetsChainId;
    }

    public int getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(int assetsId) {
        this.assetsId = assetsId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getLockTime() {
        return lockTime;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }
}
