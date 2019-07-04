package io.nuls.v2.service;

import io.nuls.core.basic.Result;
import io.nuls.core.constant.ErrorCode;
import io.nuls.v2.model.dto.CreateAgentForm;
import io.nuls.v2.model.dto.RestFulResult;
import io.nuls.v2.util.RestFulUtil;

import java.util.HashMap;
import java.util.Map;

import static io.nuls.v2.util.ValidateUtil.validateChainId;

public class ConsensusService {

    private ConsensusService() {
    }

    private static ConsensusService instance = new ConsensusService();

    public static ConsensusService getInstance() {
        return instance;
    }

    public Result createAgent(CreateAgentForm form) {
        validateChainId();
        Map<String, Object> map = new HashMap<>();
        map.put("agentAddress", form.getAgentAddress());
        map.put("packingAddress", form.getPackingAddress());
        map.put("rewardAddress", form.getRewardAddress());
        map.put("commissionRate", form.getCommissionRate());
        map.put("deposit", form.getDeposit());
        map.put("password", form.getPassword());
        RestFulResult restFulResult = RestFulUtil.post("api/consensus/agent", map);
        io.nuls.core.basic.Result result;
        if (restFulResult.isSuccess()) {
            result = io.nuls.core.basic.Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = io.nuls.core.basic.Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;

    }
}
