package io.nuls.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
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
        NulsSDKBootStrap.initTest("http://beta.api.nuls.io");
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
        dto.setEncryptedPrivateKey("87c7946d48e4e056aee42baa1fecab604620d55b9c44dd73b33b1bc16a12a10799cf7fc844116c6f9317c988bbcf32e2");
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
//        signDto.setEncryptedPrivateKey("298d69f9a7ed29d734769945a1788beecc0498d596da622e1d89909af29c07629ccd8a9df1b60196a4659e0e3c6cf9ce");
//        signDto.setPassword(password);
        signDtoList.add(signDto);

        Result result = NulsSDKTool.sign(signDtoList, txHex);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void testMultiSign() {
        String txHex = "02002ef2435d0672656d61726b008c0117020003f6231825aa05e4d25b4772909a15c9ba3c0b6fe202000100402a86481700000000000000000000000000000000000000000000000000000008e2a45f6068c4bb7a00011702000191866cefc8c9e1181b4e1e068b64fa288405b3e60200010000e87648170000000000000000000000000000000000000000000000000000000000000000000000460202210377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db2103f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1";
        SignDto signDto = new SignDto();
        signDto.setAddress("tNULSeBaMoDwD9pvR4cYMvBdJYPs9LmTnHyq8y");
        signDto.setPriKey("???");

        Result result = NulsSDKTool.multiSign(signDto, txHex);
        Map map = (Map) result.getData();
        System.out.println(map);
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
        String pri = "9ce21dad67e0f0af2599b41b515a7f7018059418bab892a7b68f283d489abc4b";
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
