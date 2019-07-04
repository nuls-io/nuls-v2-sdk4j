package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

import java.math.BigInteger;

@ApiModel(name = "创建共识交易表单")
public class ConsensusDto {

    @ApiModelProperty(description = "节点创建地址")
    private String agentAddress;

    @ApiModelProperty(description = "节点出块地址")
    private String packingAddress;

    @ApiModelProperty(description = "获取共识奖励地址")
    private String rewardAddress;

    @ApiModelProperty(description = "节点佣金比例")
    private int commissionRate;

    @ApiModelProperty(description = "创建节点保证金")
    private BigInteger deposit;

    @ApiModelProperty(description = "交易输入信息")
    private CoinFromDto input;

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getPackingAddress() {
        return packingAddress;
    }

    public void setPackingAddress(String packingAddress) {
        this.packingAddress = packingAddress;
    }

    public String getRewardAddress() {
        return rewardAddress;
    }

    public void setRewardAddress(String rewardAddress) {
        this.rewardAddress = rewardAddress;
    }

    public int getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(int commissionRate) {
        this.commissionRate = commissionRate;
    }

    public BigInteger getDeposit() {
        return deposit;
    }

    public void setDeposit(BigInteger deposit) {
        this.deposit = deposit;
    }

    public CoinFromDto getInput() {
        return input;
    }

    public void setInput(CoinFromDto input) {
        this.input = input;
    }
}
