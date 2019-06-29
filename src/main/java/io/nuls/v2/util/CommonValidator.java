package io.nuls.v2.util;

import io.nuls.base.basic.AddressTool;
import io.nuls.core.exception.NulsException;
import io.nuls.core.model.StringUtils;
import io.nuls.v2.SDKContext;
import io.nuls.v2.error.AccountErrorCode;
import io.nuls.v2.model.dto.*;

import java.util.List;

import static io.nuls.v2.util.ValidateUtil.validateChainId;

public class CommonValidator {

    public static void checkTransferDto(TransferDto transferDto) throws NulsException {
        for (CoinFromDto from : transferDto.getInputs()) {
            validateCoinFrom(from);
        }
        for (CoinToDto to : transferDto.getOutputs()) {
            validateCoinTo(to);
        }
        String remark = transferDto.getRemark();
        if (!ValidateUtil.validTxRemark(remark)) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR);
        }
    }

    public static void validateCoinFrom(CoinFromDto from) throws NulsException {
        if (!AddressTool.validAddress(SDKContext.main_chain_id, from.getAddress())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "from address [" + from.getAddress() + "] is invalid");
        }
        if (!validateChainId(from.getChainId())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "from chainId [" + from.getChainId() + "] is invalid");
        }
        if (!validateChainId(from.getAssetId())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "from assetId [" + from.getAssetId() + "] is invalid");
        }
        if (!ValidateUtil.validateCoinAmount(from.getAmount())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "from amount [" + from.getAmount() + "] is invalid");
        }
        if (!ValidateUtil.validateNonce(from.getNonce())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "from nonce [" + from.getNonce() + "] is invalid");
        }
    }

    public static void validateLockCoinFrom(CoinFromDto from) throws NulsException {
        if (!AddressTool.validAddress(SDKContext.main_chain_id, from.getAddress())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "from address [" + from.getAddress() + "] is invalid");
        }
        if (!validateChainId(from.getChainId())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "from chainId [" + from.getChainId() + "] is invalid");
        }
        if (!validateChainId(from.getAssetId())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "from assetId [" + from.getAssetId() + "] is invalid");
        }
        if (!ValidateUtil.validateCoinAmount(from.getAmount())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "from amount [" + from.getAmount() + "] is invalid");
        }
    }

    public static void validateCoinTo(CoinToDto to) throws NulsException {
        //            if (!AddressTool.validAddress(SDKContext.default_chain_id, to.getAddress())) {
//                throw new NulsRuntimeException(AccountErrorCode.PARAMETER_ERROR, "to address [" + to.getAddress() + "] is invalid");
//            }
        if (!validateChainId(to.getChainId())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "to chainId [" + to.getChainId() + "] is invalid");
        }
        if (!validateChainId(to.getAssetId())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "to assetId [" + to.getAssetId() + "] is invalid");
        }
        if (!ValidateUtil.validateCoinAmount(to.getAmount())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "to amount [" + to.getAmount() + "] is invalid");
        }
        if (!ValidateUtil.validateLockTime(to.getLockTime())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "to lockTime [" + to.getLockTime() + "] is invalid");
        }
    }

    public static void validateSignDto(List<SignDto> signDtoList) throws NulsException {
        for (SignDto signDto : signDtoList) {
            if (!AddressTool.validAddress(SDKContext.main_chain_id, signDto.getAddress())) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "address[" + signDto.getAddress() + "] is invalid");
            }
            if (StringUtils.isBlank(signDto.getEncryptedPrivateKey()) && StringUtils.isBlank(signDto.getPriKey())) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "privateKey or encryptedPrivateKey is at least one of the required fields");
            }
            if (StringUtils.isNotBlank(signDto.getEncryptedPrivateKey()) && StringUtils.isBlank(signDto.getPassword())) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "password is require when encryptedPrivateKey is not null");
            }
        }
    }

    public static void validateConsensusDto(ConsensusDto consensusDto) throws NulsException {
        if (!AddressTool.validAddress(SDKContext.main_chain_id, consensusDto.getAgentAddress())) {
            throw new NulsException(AccountErrorCode.ADDRESS_ERROR, "agentAddress is invalid");
        }
        if (!AddressTool.validAddress(SDKContext.main_chain_id, consensusDto.getPackingAddress())) {
            throw new NulsException(AccountErrorCode.ADDRESS_ERROR, "packingAddress is invalid");
        }
        if (!AddressTool.validAddress(SDKContext.main_chain_id, consensusDto.getRewardAddress())) {
            throw new NulsException(AccountErrorCode.ADDRESS_ERROR, "rewardAddress is invalid");
        }
        if (!ValidateUtil.validateCommissionRate(consensusDto.getCommissionRate())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "commissionRate is invalid");
        }
        validateCoinFrom(consensusDto.getInput());
    }

    public static void validateDepositDto(DepositDto dto) throws NulsException {
        if (!AddressTool.validAddress(SDKContext.main_chain_id, dto.getAddress())) {
            throw new NulsException(AccountErrorCode.ADDRESS_ERROR, "address is invalid");
        }
        if (!ValidateUtil.validHash(dto.getAgentHash())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "agentHash is invalid");
        }
        validateCoinFrom(dto.getInput());
    }

    public static void validateWithDrawDto(WithDrawDto dto) throws NulsException {
        if (!AddressTool.validAddress(SDKContext.main_chain_id, dto.getAddress())) {
            throw new NulsException(AccountErrorCode.ADDRESS_ERROR, "address is invalid");
        }
        if (!ValidateUtil.validHash(dto.getDepositHash())) {
            throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "depositHash is invalid");
        }
        validateLockCoinFrom(dto.getInput());
    }
}
