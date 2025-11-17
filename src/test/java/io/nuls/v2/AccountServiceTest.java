package io.nuls.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.base.data.Transaction;
import io.nuls.base.signture.MultiSignTxSignature;
import io.nuls.core.basic.Result;
import io.nuls.core.crypto.ECIESUtil;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.enums.EncodeType;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.service.AccountService;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple SDKContext.
 */
public class AccountServiceTest {

    static String address = "tNULSeBaMkm6c3ShAFMzfDX8RKdapZdUcseSw8";
    static String pubKey = "03ac18d40eb3131f934441f81c631b3898097b606a84893da1559de61fe3d3cfe9";
    static String priKey = "???";
    static String encryptedPrivateKey = "???";
    static String password = "???";


    @Before
    public void before() {
        //NulsSDKBootStrap.initTest("http://beta.api.nuls.io");
        NulsSDKBootStrap.initMain("");
    }

    @Test
    public void testCreateAccount() {
        int count = 5;
        Result<List<String>> result = NulsSDKTool.createAccount(count, password);
        for (String address : result.getData()) {
            System.out.println(address);
        }
    }

    @Test
    public void testCreateOfflineAccount() {
        while (true) {
            int count = 100;
            Result<List<AccountDto>> result = NulsSDKTool.createOffLineAccount(count, password);

            for (AccountDto accountDto : result.getData()) {
                try {
                    if(accountDto.getAddress().toUpperCase().endsWith("VIVI")) {
                        System.out.println(JSONUtils.obj2json(accountDto));
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testResetPassword() {
        Result result = NulsSDKTool.resetPassword("GJbpb6GNSvkBNoNa3YH2skPLSEVvEYMfS99", password, "abcd4321");
        System.out.println(result);

    }

    @Test
    public void testImportPriKey() {
        Result result = NulsSDKTool.importPriKey("???", password);
        System.out.println(result.getData());
        System.out.println(result);
    }

    @Test
    public void testGetPriKey() {
        Result result = NulsSDKTool.getPriKey("tNULSeBaMhUxmEFAiHj1ysd9UXYbFRnZ5yknq1", password);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void testGetPriKeyOffline() {
        String address = "TNVTdTSPFnCMgr9mzvgibQ4hKspVSGEc6XTKE";
        String encryptedPrivateKey = "???";
        Result result = NulsSDKTool.getPriKeyOffline(address, encryptedPrivateKey, password);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void importKeystore() {
        AccountKeyStoreDto dto = new AccountKeyStoreDto();
        dto.setAddress("tNULSeBaMhZzUJghK8G8pQDwjVHwmgDSK2698F");
        dto.setPubKey("02f8fdf297dfdb2d4dc92698d2cb8988e15f9a63b39f1db32bf8c74071b7ee2462");
        dto.setEncryptedPrivateKey("xxx");
        Result result = NulsSDKTool.importKeystore(dto, password);
        System.out.println(result.getData());
    }

    @Test
    public void testExportKeystore() {
        String address = "GJbpb61kDNUMipDrRrPeqRRdidDY9SuXeED";
        String filePath = "D:";
        Result result = NulsSDKTool.exportKeyStore(address, password, filePath);
        System.out.println(result.getData());
    }


    @Test
    public void testSign() {
        String txHex = "1000cbe3736000de02000123481a9a762b3b73feba8728e1fde99ced75e1070200024369d472f2adc0a4f4ce3b56a67b13665a2c56bb000000000000000000000000000000000000000000000000000000000000000080841e00000000001900000000000000106d696e7457697468546f6b656e55524900030126744e554c536542614d6b4c5656506261646d3835315a73444c7755526a7753363152346a616701013101403263663234646261356662306133306532366538336232616335623965323965316231363165356331666137343235653733303433333632393338623938323448011702000123481a9a762b3b73feba8728e1fde99ced75e107020001002077fc020000000000000000000000000000000000000000000000000000000008eb62c65a34695377000000";

        List<SignDto> signDtoList = new ArrayList<>();
        SignDto signDto = new SignDto();
        signDto.setAddress("tNULSeBaMidSH7amSTjaNvVkL9VDdFEc9rUztf");
        signDto.setPriKey("???");
//        signDto.setEncryptedPrivateKey("xxx");
//        signDto.setPassword(password);
        signDtoList.add(signDto);

        Result result = NulsSDKTool.sign(signDtoList, txHex);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void testMultiSign() {
        String txHex = "02007ad64c6800008c01170100037ca289208cef62a3572f4680fb77d59a0a7d2fe201000100e0c810000000000000000000000000000000000000000000000000000000000008190dcbcad078dd9f000117010001b6bb67f692db16fb45739610f39a9ab56160897f0100010040420f0000000000000000000000000000000000000000000000000000000000000000000000000046020221023a31fd7232e027ef06a6950d4e6a68b1fb24fea4eb3e913a2f35b0da1addaa9d2103bf7f76fed8161c20fc54404cfb29725fb9a415990fe9cb786a84d00c11a0324c";
        SignDto signDto = new SignDto();
        signDto.setAddress("NULSd6Hh91ujCRFuJaJxwENAExBW3MWFxA25m");
        signDto.setPriKey("xxx");

        Result result = NulsSDKTool.multiSign(signDto, txHex);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void testSign1() {
        String txHex = "02007ad64c6800008c01170100037ca289208cef62a3572f4680fb77d59a0a7d2fe201000100e0c810000000000000000000000000000000000000000000000000000000000008190dcbcad078dd9f000117010001b6bb67f692db16fb45739610f39a9ab56160897f0100010040420f00000000000000000000000000000000000000000000000000000000000000000000000000af020221023a31fd7232e027ef06a6950d4e6a68b1fb24fea4eb3e913a2f35b0da1addaa9d2103bf7f76fed8161c20fc54404cfb29725fb9a415990fe9cb786a84d00c11a0324c2103bf7f76fed8161c20fc54404cfb29725fb9a415990fe9cb786a84d00c11a0324c46304402207e0de09269f1c73eaaba4a28a0012788db558bff43a7a0eab3824c6b4d37c95d022030beec7ad779212db1a6c06ea99879cc87a3e0e3dc2238e042fbce8a148720c3";
        String address = "NULSd6Hgj21NKWSNuEgwPXCSddpmNy825sbN7";
        String priKey = "xxx";
        //String address = "NULSd6Hgj51fZacC38EhwKZCvzq4K6nVjuMkv";
        //String priKey = "xxx";

        Result result = NulsSDKTool.sign(txHex, address, priKey);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void desTx() throws Exception {
        String hex = "02003ae64b6800008c01170100037ca289208cef62a3572f4680fb77d59a0a7d2fe201000100b0ad010000000000000000000000000000000000000000000000000000000000080000000000000000000117010001b6bb67f692db16fb45739610f39a9ab56160897f0100010010270000000000000000000000000000000000000000000000000000000000000000000000000000fd1a01020221023a31fd7232e027ef06a6950d4e6a68b1fb24fea4eb3e913a2f35b0da1addaa9d2103bf7f76fed8161c20fc54404cfb29725fb9a415990fe9cb786a84d00c11a0324c21023a31fd7232e027ef06a6950d4e6a68b1fb24fea4eb3e913a2f35b0da1addaa9d4630440220351f1c58ad2ac8c6a57b32b40f64488cda63d0de851fd9c2a82bcc327955ec2202206653ee65c152cb38ed329d70a31440d9ec6a52390fe07385b4d8f6b1edb2334e2103bf7f76fed8161c20fc54404cfb29725fb9a415990fe9cb786a84d00c11a0324c483046022100ced02a7ba129edb75d63c86a99375d00a87d0f22fc28dcadfd924d3f4954f449022100e17840bbce3335121543f2799f9a6d8462ccdf5251806342f71d4a0233eaa500";
        Transaction tx = new Transaction();
        tx.parse(HexUtil.decode(hex), 0);
        System.out.println(tx.getTransactionSignature().length);
        System.out.println(HexUtil.encode(tx.getTransactionSignature()));
        MultiSignTxSignature txSignature = new MultiSignTxSignature();
        txSignature.parse(tx.getTransactionSignature(), 0);
        System.out.println(txSignature.size());
    }

    @Test
    public void testBalance() {
        Result result = NulsSDKTool.getAccountBalance("tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG", 2, 1);
        System.out.println(result);
    }

    @Test
    public void testAliasTx() {
        String address = "tNULSeBaMoDwD9pvR4cYMvBdJYPs9LmTnHyq8y";
        String alias = "testalias";
        AliasDto aliasDto = new AliasDto();
        aliasDto.setAddress(address);
        aliasDto.setAlias(alias);
        aliasDto.setNonce("0000000000000000");

        Result result = NulsSDKTool.createAliasTxOffline(aliasDto);
        System.out.println(result.getData());
    }

    @Test
    public void testMultiSignAliasTx() {
        String address = "tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy";
        String alias = "aaddbbees";
        //List<String> pubKeys = List.of("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db", "03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        List<String> pubKeys = new ArrayList<>();


        MultiSignAliasDto aliasDto = new MultiSignAliasDto();
        aliasDto.setPubKeys(pubKeys);
        aliasDto.setMinSigns(2);
        aliasDto.setAddress(address);
        aliasDto.setAlias(alias);
        aliasDto.setNonce("e2a45f6068c4bb7a");

        Result result = NulsSDKTool.createMultiSignAliasTxOffline(aliasDto);
        System.out.println(result.getData());
    }

    @Test
    public void testAlias() {
        String address = "tNULSeBaMk4YTkZaUXrLXbUtaHeTWF1Bx6aiBm";
        String alias = "ddaab";
        Result result = NulsSDKTool.setAlias(address, alias, password);
        System.out.println(result.getData());
    }

    @Test
    public void testValidateAddress() {
        String address = "NERVEepb6AcV55NzyXsAP8KKmZNApAE3JS3gvA";
        Result result = NulsSDKTool.validateAddress(9, address);
        System.out.println(result);
    }

    @Test
    public void testChangeV1addressToV2address() {
        String address = "Nsdwnd4auFisFJKU6iDvBxTdPkeg8qkB";
        Result result = NulsSDKTool.changeV1addressToV2address(address);
        System.out.println(result);
    }

    @Test
    public void encrypAndDecryptMsgTest() throws Exception {
        String pri = "xxx";
        String pub = "03958b790c331954ed367d37bac901de5c2f06ac8368b37d7bd6cd5ae143c1d7e3";
        String msg = "werwerwerwe新光天地，没有#$%^&*()cvbcvbcvb";
        byte[] encrypt = ECIESUtil.encrypt(HexUtil.decode(pub), msg.getBytes(StandardCharsets.UTF_8));
        String encryptMsg = HexUtil.encode(encrypt);
        Result result = NulsSDKTool.decryptData(pri, encryptMsg, EncodeType.UTF8);
        System.out.println(result.isSuccess());
        System.out.println(result.getData());
        System.out.println(result.getErrorCode().getCode());
        System.out.println(result.getMsg());

    }

}
