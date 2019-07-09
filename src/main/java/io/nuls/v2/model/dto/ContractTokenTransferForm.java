package io.nuls.v2.model.dto;


import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

import java.math.BigInteger;

@ApiModel(description = "token转账交易")
public class ContractTokenTransferForm extends BaseForm {

    @ApiModelProperty(description = "转出者账户地址", required = true)
    private String fromAddress;
    @ApiModelProperty(description = "转出者账户地址密码", required = true)
    private String password;
    @ApiModelProperty(description = "转入者账户地址", required = true)
    private String toAddress;
    @ApiModelProperty(description = "合约地址", required = true)
    private String contractAddress;
    @ApiModelProperty(description = "转出的token资产金额", required = true)
    private BigInteger amount;
    @ApiModelProperty(description = "备注", required = false)
    private String remark;

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
