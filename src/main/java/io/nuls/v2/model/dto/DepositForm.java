package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

@ApiModel(name = "委托参与共识表单数据")
public class DepositForm {

    @ApiModelProperty(description = "账户地址")
    private String address;
    @ApiModelProperty(description = "共识节点hash")
    private String agentHash;
    @ApiModelProperty(description = "委托金")
    private String deposit;
    @ApiModelProperty(description = "密码")
    private String password;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAgentHash() {
        return agentHash;
    }

    public void setAgentHash(String agentHash) {
        this.agentHash = agentHash;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
