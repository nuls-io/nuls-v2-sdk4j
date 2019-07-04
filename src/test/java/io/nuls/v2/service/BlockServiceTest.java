package io.nuls.v2.service;

import io.nuls.core.basic.Result;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

public class BlockServiceTest {

    @Before
    public void before() {
        NulsSDKBootStrap.init(9, "http://127.0.0.1:9898/");
    }

    @Test
    public void testGetHeaderHeight() {
        long height = -1;
        Result result = NulsSDKTool.getBlockHeader(height);
        System.out.println(result.getData());
    }
    @Test
    public void testGetHeaderHash() {
        String hash = "63516e4b16530cc1bf4de51bc39abfdebeaec5fced287f015842043e2fb4dce6";
        Result result = NulsSDKTool.getBlockHeader(hash);
        System.out.println(result.getData());
    }

    @Test
    public void testGetBlock() {
        long height = 900L;
        Result result = NulsSDKTool.getBlock(height);
        System.out.println(result.getData());
    }

    @Test
    public void testGetBlockHash() {
        String hash = "63516e4b16530cc1bf4de51bc39abfdebeaec5fced287f015842043e2fb4dce6";
        Result result = NulsSDKTool.getBlock(hash);
        System.out.println(result.getData());
    }

    @Test
    public void testGetBestHeader() {
        Result result = NulsSDKTool.getBestBlockHeader();
        System.out.println(result.getData());
    }

    @Test
    public void testGetBestBlock() {
        Result result = NulsSDKTool.getBestBlock();
        System.out.println(result.getData());
    }
}
