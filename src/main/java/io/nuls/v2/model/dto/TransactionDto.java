/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.v2.model.dto;

import io.nuls.core.parse.JSONUtils;
import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;
import io.nuls.core.rpc.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: PierreLuo
 * @date: 2019-06-29
 */

@ApiModel
public class TransactionDto {
    @ApiModelProperty(description = "交易的hash值")
    private String hash;
    @ApiModelProperty(description = "交易类型")
    private int type;
    @ApiModelProperty(description = "交易时间")
    private String time;
    @ApiModelProperty(description = "区块高度")
    private long blockHeight = -1L;
    @ApiModelProperty(description = "区块hash")
    private String blockHash;
    @ApiModelProperty(description = "交易备注")
    private String remark;
    @ApiModelProperty(description = "交易签名")
    private String transactionSignature;
    @ApiModelProperty(description = "交易业务数据序列化字符串")
    private String txDataHex;
    @ApiModelProperty(description = "交易状态 0:unConfirm(待确认), 1:confirm(已确认)")
    private int status = 0;
    @ApiModelProperty(description = "交易大小")
    private int size;
    @ApiModelProperty(description = "在区块中的顺序，存储在rocksDB中是无序的，保存区块时赋值，取出后根据此值排序")
    private int inBlockIndex;
    @ApiModelProperty(description = "输入", type = @TypeDescriptor(value = List.class, collectionElement = CoinFromsDto.class))
    private List<CoinFromsDto> form;
    @ApiModelProperty(description = "输出", type = @TypeDescriptor(value = List.class, collectionElement = CoinTosDto.class))
    private List<CoinTosDto> to;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTransactionSignature() {
        return transactionSignature;
    }

    public void setTransactionSignature(String transactionSignature) {
        this.transactionSignature = transactionSignature;
    }

    public String getTxDataHex() {
        return txDataHex;
    }

    public void setTxDataHex(String txDataHex) {
        this.txDataHex = txDataHex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getInBlockIndex() {
        return inBlockIndex;
    }

    public void setInBlockIndex(int inBlockIndex) {
        this.inBlockIndex = inBlockIndex;
    }

    public List<CoinFromsDto> getForm() {
        return form;
    }

    public void setForm(List<CoinFromsDto> form) {
        this.form = form;
    }

    public List<CoinTosDto> getTo() {
        return to;
    }

    public void setTo(List<CoinTosDto> to) {
        this.to = to;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public static TransactionDto mapToPojo(Map map) {
        TransactionDto tx = new TransactionDto();
        tx.hash = (String) map.get("hash");
        tx.type = (int) map.get("type");
        tx.time = (String) map.get("time");
        tx.blockHeight = Long.parseLong(map.get("blockHeight").toString());
        tx.remark = (String) map.get("remark");
        tx.transactionSignature = (String) map.get("transactionSignature");
        tx.txDataHex = (String) map.get("txDataHex");
        tx.status = (int) map.get("status");
        tx.size = (int) map.get("size");
        tx.inBlockIndex = (int) map.get("inBlockIndex");
        List<Map<String, Object>> fromMaps = (List<Map<String, Object>>) map.get("from");
        List<CoinFromsDto> fromsDtos = new ArrayList<>();
        if (fromMaps != null) {
            for (Map<String, Object> fmap : fromMaps) {
                CoinFromsDto from = JSONUtils.map2pojo(fmap, CoinFromsDto.class);
                fromsDtos.add(from);
            }
        }
        tx.setForm(fromsDtos);

        List<Map<String, Object>> toMaps = (List<Map<String, Object>>) map.get("to");
        List<CoinTosDto> toDtos = new ArrayList<>();
        if (toMaps != null) {
            for (Map<String, Object> tmap : toMaps) {
                CoinTosDto to = JSONUtils.map2pojo(tmap, CoinTosDto.class);
                toDtos.add(to);
            }
        }
        tx.setTo(toDtos);
        return tx;
    }
}
