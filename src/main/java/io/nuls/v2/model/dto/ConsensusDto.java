package io.nuls.v2.model.dto;

import java.math.BigInteger;

public class ConsensusDto {

    /**
     * 节点地址
     */
    private String agentAddress;

    /**
     * 打包出块地址
     */
    private String packingAddress;

    /**
     * 获取奖励地址
     */
    private String rewardAddress;

    /**
     * 佣金比例 单位%
     */
    private int commissionRate;

    /**
     * 创建节点保证金
     */
    private BigInteger deposit;

    /**
     * 交易输入
     */
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
