package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

@ApiModel(name = "智能合约表单数据")
public class ContractBaseForm {

    @ApiModelProperty(description = "链ID")
    private Integer chainId;
    @ApiModelProperty(description = "交易创建者")
    private String sender;
    @ApiModelProperty(description = "最大gas消耗")
    private long gasLimit;
    @ApiModelProperty(description = "执行合约单价")
    private long price;
    @ApiModelProperty(description = "交易创建者账户密码")
    private String password;
    @ApiModelProperty(description = "备注", required = false)
    private String remark;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setChainId(Integer chainId) {
        this.chainId = chainId;
    }

    public Integer getChainId() {
        return chainId;
    }
}
