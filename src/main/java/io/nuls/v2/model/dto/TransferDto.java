package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;
import io.nuls.core.rpc.model.TypeDescriptor;

import java.util.List;

@ApiModel
public class TransferDto {

    @ApiModelProperty(description = "转账交易输入列表", type = @TypeDescriptor(value = List.class, collectionElement = CoinFromDto.class))
    private List<CoinFromDto> inputs;

    @ApiModelProperty(description = "转账交易输出列表", type = @TypeDescriptor(value = List.class, collectionElement = CoinToDto.class))
    private List<CoinToDto> outputs;
    @ApiModelProperty(description = "创建时间", required = false)
    private long time;
    @ApiModelProperty(description = "交易备注", required = false)
    private String remark;

    public List<CoinFromDto> getInputs() {
        return inputs;
    }

    public void setInputs(List<CoinFromDto> inputs) {
        this.inputs = inputs;
    }

    public List<CoinToDto> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<CoinToDto> outputs) {
        this.outputs = outputs;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
