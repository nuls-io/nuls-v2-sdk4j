package io.nuls.v2.model.dto;

import java.util.List;

public class StopConsensusDto {

    /**
     * 创建节点的交易hash
     */
    private String agentHash;

    /**
     * 节点地址
     */
    private String agentAddress;

    private List<StopDepositDto> depositList;

    public String getAgentHash() {
        return agentHash;
    }

    public void setAgentHash(String agentHash) {
        this.agentHash = agentHash;
    }

    public List<StopDepositDto> getDepositList() {
        return depositList;
    }

    public void setDepositList(List<StopDepositDto> depositList) {
        this.depositList = depositList;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }
}


