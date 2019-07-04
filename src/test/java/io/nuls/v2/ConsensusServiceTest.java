package io.nuls.v2;

import io.nuls.core.basic.Result;
import io.nuls.v2.model.dto.CreateAgentForm;
import io.nuls.v2.model.dto.DepositForm;
import io.nuls.v2.model.dto.StopAgentForm;
import io.nuls.v2.model.dto.WithdrawForm;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

public class ConsensusServiceTest {

    static String agentAddress = "tNULSeBaMkm6c3ShAFMzfDX8RKdapZdUcseSw8";
    static String packingAddress = "tNULSeBaMf9i8j2yeLns9RVgAUERWcrT67fPP4";
    static String password = "Nuls123546";

    @Before
    public void before() {
        NulsSDKBootStrap.init(2, "http://127.0.0.1:9898/");
    }

    @Test
    public void testCreateAgent() {
        CreateAgentForm form = new CreateAgentForm();
        form.setAgentAddress(agentAddress);
        form.setRewardAddress(agentAddress);
        form.setPackingAddress(packingAddress);

        form.setCommissionRate(10);
        form.setDeposit("2000000000000");
        form.setPassword(password);

        Result result = NulsSDKTool.createAgent(form);
        System.out.println(result.getData());
    }

    @Test
    public void testDeposit() {
        DepositForm form = new DepositForm();
        form.setAddress(agentAddress);
        form.setAgentHash("e52c29f1406d79281726fc75503384a723e805d9b8391d5ad53347210e7fc6de");
        form.setDeposit("2000000000000");
        form.setPassword(password);

        Result result = NulsSDKTool.depositToAgent(form);
        System.out.println(result.getData());
    }

    @Test
    public void testWithDraw() {
        WithdrawForm form = new WithdrawForm();
        form.setAddress(agentAddress);
        form.setTxHash("629495e2b9d53ee08eff196b9d9b42bd8cb3876d0eaf14a4a6a6a2761960c132");
        form.setPassword(password);

        Result result = NulsSDKTool.withdraw(form);
        System.out.println(result.getData());
    }

    @Test
    public void testStopAgent() {
        StopAgentForm form = new StopAgentForm();
        form.setAgentAddress(agentAddress);
        form.setPassword(password);

        Result result = NulsSDKTool.stopAgent(form);
        System.out.println(result.getData());
    }

}
