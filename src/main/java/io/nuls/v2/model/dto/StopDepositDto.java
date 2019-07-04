package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

@ApiModel
public class StopDepositDto {

    @ApiModelProperty(description = "委托共识的交易hash")
    private String depositHash;
    @ApiModelProperty(description = "交易输入信息")
    private CoinFromDto input;

    public String getDepositHash() {
        return depositHash;
    }

    public void setDepositHash(String depositHash) {
        this.depositHash = depositHash;
    }

    public CoinFromDto getInput() {
        return input;
    }

    public void setInput(CoinFromDto input) {
        this.input = input;
    }
}
