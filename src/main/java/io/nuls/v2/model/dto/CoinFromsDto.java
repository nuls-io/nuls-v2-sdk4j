package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

@ApiModel
public class CoinFromsDto {

    @ApiModelProperty(description = "账户地址")
    protected String address;

    @ApiModelProperty(description = "资产发行链的id")
    protected int assetsChainId;

    @ApiModelProperty(description = "资产id")
    protected int assetsId;

    @ApiModelProperty(description = "数量")
    protected String amount;
    @ApiModelProperty(description = "账户nonce值")
    private String nonce;
    @ApiModelProperty(description = "0普通交易，-1解锁金额交易（退出共识，退出委托）")
    private byte locked;

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

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public byte getLocked() {
        return locked;
    }

    public void setLocked(byte locked) {
        this.locked = locked;
    }
}
