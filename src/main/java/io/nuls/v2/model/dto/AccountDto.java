package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

@ApiModel(name = "账户keystore")
public class AccountDto {

    @ApiModelProperty(description = "账户地址")
    private String address;
    @ApiModelProperty(description = "公钥")
    private String pubKey;
    @ApiModelProperty(description = "明文私钥")
    private String prikey;
    @ApiModelProperty(description = "加密后的私钥")
    private String encryptedPrivateKey;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrikey() {
        return prikey;
    }

    public void setPrikey(String priKey) {
        this.prikey = priKey;
    }

    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }
}
