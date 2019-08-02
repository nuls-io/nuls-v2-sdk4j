package io.nuls.v2.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

import java.util.List;

@ApiModel
public class MultiSignAliasDto extends AliasDto{

    private List<String> pubKeys;

    private int minSigns;

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
