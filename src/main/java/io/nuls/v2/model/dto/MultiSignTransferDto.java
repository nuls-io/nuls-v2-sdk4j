package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;
import io.nuls.core.rpc.model.TypeDescriptor;

import java.util.List;

@ApiModel
public class MultiSignTransferDto {

    @ApiModelProperty(description = "公钥集合",type = @TypeDescriptor(value = List.class, collectionElement = String.class))
    private List<String> pubKeys;
    @ApiModelProperty(description = "最小签名数")
    private int minSigns;

    @ApiModelProperty(description = "转账交易输入列表", type = @TypeDescriptor(value = List.class, collectionElement = CoinFromDto.class))
    private List<CoinFromDto> inputs;

    @ApiModelProperty(description = "转账交易输出列表", type = @TypeDescriptor(value = List.class, collectionElement = CoinToDto.class))
    private List<CoinToDto> outputs;

    @ApiModelProperty(description = "交易备注")
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

    public List<String> getPubKeys() {
        return pubKeys;
    }

    public void setPubKeys(List<String> pubKeys) {
        this.pubKeys = pubKeys;
    }

    public int getMinSigns() {
        return minSigns;
    }

    public void setMinSigns(int minSigns) {
        this.minSigns = minSigns;
    }
}
