package io.nuls.v2.service;

import io.nuls.base.signture.TransactionSignature;
import io.nuls.core.basic.Result;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.exception.NulsException;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.SDKContext;
import io.nuls.v2.model.Account;
import io.nuls.v2.model.dto.CrossTransferForm;
import io.nuls.v2.model.dto.TransferForm;
import io.nuls.v2.util.AccountTool;
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
        NulsSDKBootStrap.initTest("http://127.0.0.1:8004/");
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
    public void signData() throws Exception {
        String txSign = "210232bdaf6573319eba3b433ed88d6d4b0d06ea3ad9a504596fe967a2dbf95fc07c473045022100f4e1f683803a103b79eeb420a6d04089a673f93b2ea3d3012508320846f91c5302205c133ff512d6865fcb47c7fe07c7d930dfe6857cd5d69ab8065609435929a439";
        TransactionSignature sign = new TransactionSignature();
        sign.parse(HexUtil.decode(txSign),0);
        System.out.println(HexUtil.encode(sign.getP2PHKSignatures().get(0).getSignData().getSignBytes()));
    }

    @Test
    public void testTx() {
        Result result = NulsSDKTool.getTx("49dc5c6db817d98ad4bc5152bff7cc26827fc659271057938586a1a24b7c733b");
        System.out.println(result.getData());
    }

    @Test
    public void testAccount() throws NulsException {
        Account account = AccountTool.createAccount(2, null);
        System.out.println();
    }

    @Test
    public void testAccountBatch() throws Exception {
        for (int i=0;i<50;i++) {
            Account account = AccountTool.createAccount(1, null);
            System.out.println(String.format("\"%s\": \"%s\"", account.getAddress().toString(), HexUtil.encode(account.getPriKey())));
        }
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
