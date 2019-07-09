package io.nuls.v2.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Author: zhoulijun
 * @Time: 2019-03-07 15:11
 * @Description: 功能描述
 */
public class BaseForm {

    @JsonIgnore
    private Integer chainId;

    public Integer getChainId() {
        return chainId;
    }

    public void setChainId(Integer chainId) {
        this.chainId = chainId;
    }
}
