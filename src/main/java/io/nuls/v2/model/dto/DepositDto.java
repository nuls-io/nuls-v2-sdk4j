package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

import java.math.BigInteger;

@ApiModel
public class DepositDto {

    @ApiModelProperty(description = "账户地址")
    private String address;
    @ApiModelProperty(description = "委托金额")
    private BigInteger deposit;
    @ApiModelProperty(description = "共识节点hash")
    private String agentHash;
    @ApiModelProperty(description = "交易输入信息")
    private CoinFromDto input;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getDeposit() {
        return deposit;
    }

    public void setDeposit(BigInteger deposit) {
        this.deposit = deposit;
    }

    public String getAgentHash() {
        return agentHash;
    }

    public void setAgentHash(String agentHash) {
        this.agentHash = agentHash;
    }

    public CoinFromDto getInput() {
        return input;
    }

    public void setInput(CoinFromDto input) {
        this.input = input;
    }
}
