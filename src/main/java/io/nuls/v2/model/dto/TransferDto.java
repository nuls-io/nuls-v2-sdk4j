package io.nuls.v2.model.dto;

import java.util.List;

public class TransferDto {

    /**
     * 交易输入
     */
    private List<CoinFromDto> inputs;

    /**
     * 交易输出
     */
    private List<CoinToDto> outputs;

    /**
     * 交易备注
     */
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
}
