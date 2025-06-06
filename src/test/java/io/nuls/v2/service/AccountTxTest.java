package io.nuls.v2.service;

import io.nuls.core.basic.Result;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.SDKContext;
import io.nuls.v2.model.dto.CrossTransferForm;
import io.nuls.v2.model.dto.TransferForm;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

public class AccountTxTest {

    static String address = "GJbpb6AeSbA68tyPe67CeTaZZeJjWqiw7Wd";

    static String toAddress = "GJbpb656PMFKnYVH9eLgvgtfD3VWbG86iqB";

    static String password = "nuls123456";

    @Before
    public void before() {
        NulsSDKBootStrap.initTest("http://beta.api.nuls.io/");
    }



    @Test
    public void testGetBalance() throws Exception {
        Result result = NulsSDKTool.getAccountBalance("tNULSeBaMvCL5jpJiaiUmo2sBEFAVbypXG692o", SDKContext.main_chain_id, SDKContext.main_asset_id);
        System.out.println(JSONUtils.obj2PrettyJson(result));
    }

    @Test
    public void testTransfer() {
        TransferForm form = new TransferForm();
        form.setAddress(address);
        form.setToAddress(toAddress);
        form.setPassword(password);
        form.setAmount(new BigInteger("100000000"));

        Result result = NulsSDKTool.transfer(form);
        System.out.println(result.getData());
    }

    @Test
    public void testCrossTransfer() {
        CrossTransferForm form = new CrossTransferForm();
        form.setAddress("tNULSeBaMoRp6QhNYSF8xjiwBFYnvCoCjkQzvU");
        form.setToAddress("TNVTdTSPFnCMgr9mzvgibQ4hKspVSGEc6XTKE");
        form.setPassword(password);
        form.setAmount(new BigInteger("10000000"));
        form.setAssetChainId(2);
        form.setAssetId(1);
        Result result = NulsSDKTool.crossTransfer(form);
        System.out.println(result.getData());
    }


    @Test
    public void testTx() throws Exception {
        Result result = NulsSDKTool.getTx("1b83ccfe868fa361a11c44fbb39eec6c913138cfaee53962d364399aa245125d");
        System.out.println(JSONUtils.obj2json(result.getData()));
    }

    @Test
    public void testa() {
        double d = 100;

        for(int i = 0;i <3650;i++) {
            d = xxx(d);
        }
        System.out.println(d);
    }

    private double xxx(double d) {
        return d * 1.01;
    }

}
