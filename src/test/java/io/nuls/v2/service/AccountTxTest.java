package io.nuls.v2.service;

import io.nuls.core.basic.Result;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.model.dto.TransferForm;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

public class AccountTxTest {

    static String address = "GJbpb6AeSbA68tyPe67CeTaZZeJjWqiw7Wd";

    static String toAddress = "GJbpb656PMFKnYVH9eLgvgtfD3VWbG86iqB";

    static String password = "abcd1234";

    @Before
    public void before() {
        NulsSDKBootStrap.initTest("http://127.0.0.1:9898/");
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
    public void testTx() {
        Result result = NulsSDKTool.getTx("49dc5c6db817d98ad4bc5152bff7cc26827fc659271057938586a1a24b7c733b");
        System.out.println(result.getData());
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
