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
package io.nuls.v2.model;

import io.nuls.v2.model.dto.Asset;

/**
 * @author: PierreLuo
 * @date: 2024/8/14
 */
public class ChainFeeSetting {
    //{
    //    "assetId": "1-1",
    //    "symbol": "NULS",
    //    "decimals": 8,
    //    "feePerKB": "100000",
    //    "scFeeFoefficient": "1"
    //}
    private String assetId;
    private String symbol;
    private int decimals;
    private String feePerKB;
    private String scFeeFoefficient;

    private Asset asset;

    public Asset getAsset() {
        if (asset == null) {
            if (assetId == null) {
                return null;
            }
            String[] split = assetId.split("-");
            asset = new Asset(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()));
        }
        return asset;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getFeePerKB() {
        return feePerKB;
    }

    public void setFeePerKB(String feePerKB) {
        this.feePerKB = feePerKB;
    }

    public String getScFeeFoefficient() {
        return scFeeFoefficient;
    }

    public void setScFeeFoefficient(String scFeeFoefficient) {
        this.scFeeFoefficient = scFeeFoefficient;
    }
}
