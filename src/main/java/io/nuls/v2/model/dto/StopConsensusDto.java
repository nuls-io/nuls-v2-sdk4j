package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;
import io.nuls.core.rpc.model.TypeDescriptor;

import java.math.BigInteger;
import java.util.List;

@ApiModel
public class StopConsensusDto {

    @ApiModelProperty(description = "创建节点的交易hash")
    private String agentHash;
    @ApiModelProperty(description = "节点地址")
    private String agentAddress;
    @ApiModelProperty(description = "创建节点的保证金")
    private BigInteger deposit;
    @ApiModelProperty(description = "手续费单价" ,required = false)
    private BigInteger price;
    @ApiModelProperty(description = "停止委托列表", type = @TypeDescriptor(value = List.class, collectionElement = StopDepositDto.class))
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

    public BigInteger getDeposit() {
        return deposit;
    }

    public void setDeposit(BigInteger deposit) {
        this.deposit = deposit;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }
}


