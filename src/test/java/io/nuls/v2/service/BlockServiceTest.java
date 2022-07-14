package io.nuls.v2.service;

import io.nuls.core.basic.Result;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.model.dto.BlockDto;
import io.nuls.v2.model.dto.BlockHeaderDto;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class BlockServiceTest {
    @Before
    public void before() {
        NulsSDKBootStrap.initTest("http://39.98.226.51:18004");
    }

    @Test
    public void testGetHeaderHeight() {
        long height = 1;
        Result<BlockHeaderDto> result = NulsSDKTool.getBlockHeader(height);
        BlockHeaderDto dto = result.getData();
        System.out.println(dto.getHash());
    }

    @Test
    public void testGetHeaderHash() {
        String hash = "63516e4b16530cc1bf4de51bc39abfdebeaec5fced287f015842043e2fb4dce6";
        Result result = NulsSDKTool.getBlockHeader(hash);
        System.out.println(result.getData());
    }

    @Test
    public void testGetBlock() {
        long height = 1L;
        Result<BlockDto> result = NulsSDKTool.getBlock(height);
        BlockDto dto = result.getData();
        System.out.println(dto.getHeader().getHash());
    }

    @Test
    public void testGetBlockHash() {
        String hash = "7fcbd32ffcbaefd8e1cad77140cead4fd50d9beb01fe388328e615f5b03c4462";
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

    @Test
    public void testInfo() {
        Result result = NulsSDKTool.getInfo();
        System.out.println(result.getData());
    }
}
