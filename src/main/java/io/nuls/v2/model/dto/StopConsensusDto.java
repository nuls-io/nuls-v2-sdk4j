package io.nuls.v2.model.dto;

import java.util.List;

public class StopConsensusDto {

    private String agentHash;

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
}


