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
@ApiModel(description = "blockJSON 区块信息(包含区块头信息, 交易信息), 只返回对应的部分数据")
public class BlockDto {

    @ApiModelProperty(description = "区块头信息, 只返回对应的部分数据")
    private BlockHeaderDto header;
    @ApiModelProperty(description = "交易列表", type = @TypeDescriptor(value = List.class, collectionElement = TransactionDto.class))
    private List<TransactionDto> txs;

    public BlockHeaderDto getHeader() {
        return header;
    }

    public void setHeader(BlockHeaderDto header) {
        this.header = header;
    }

    public List<TransactionDto> getTxs() {
        return txs;
    }

    public void setTxs(List<TransactionDto> txs) {
        this.txs = txs;
    }

    public static BlockDto mapToPojo(Map map) {
        BlockDto dto = new BlockDto();
        BlockHeaderDto headerDto = BlockHeaderDto.mapToPojo((Map) map.get("header"));
        List<Map<String, Object>> txMaps = (List<Map<String, Object>>) map.get("txs");
        List<TransactionDto> txList = new ArrayList<>();
        for (Map<String, Object> txMap : txMaps) {
            TransactionDto tx = TransactionDto.mapToPojo(txMap);
            txList.add(tx);
        }
        dto.setHeader(headerDto);
        dto.setTxs(txList);

        return dto;
    }
}
