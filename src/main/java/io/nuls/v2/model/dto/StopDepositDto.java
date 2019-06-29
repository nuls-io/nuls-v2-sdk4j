package io.nuls.v2.model.dto;

public class StopDepositDto {

    private String depositHash;

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
