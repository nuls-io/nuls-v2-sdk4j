package io.nuls.v2.model.dto;

import java.math.BigInteger;

public class WithDrawDto {

    private String address;

    private String depositHash;

    private BigInteger price;

    private CoinFromDto input;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }
}
