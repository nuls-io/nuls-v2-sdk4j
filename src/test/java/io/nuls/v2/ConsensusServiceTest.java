package io.nuls.v2;

import org.junit.Before;

public class ConsensusServiceTest {

    @Before
    public void before() {
        NulsSDKBootStrap.init(9, "http://127.0.0.1:9898/");
    }


}
