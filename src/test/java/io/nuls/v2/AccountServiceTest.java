package io.nuls.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.core.basic.Result;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.AccountDto;
import io.nuls.v2.model.dto.SignDto;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple SDKContext.
 */
public class AccountServiceTest {

    static String address = "tNULSeBaMkm6c3ShAFMzfDX8RKdapZdUcseSw8";
    static String pubKey = "03ac18d40eb3131f934441f81c631b3898097b606a84893da1559de61fe3d3cfe9";
    static String priKey = "6df381435098e47b685cdc00fa1d7c66fa2ba9cc441179c6dd1a5686153fb0ee";
    static String encryptedPrivateKey = "0c8e925d27660dbd04104455c001efe7a5d4cba8fc484d06506c8ff4baa653be2d69e31c971243e2185782cabbbe265a";
    static String password = "Nuls123546";


    @Before
    public void before() {
        NulsSDKBootStrap.initTest("http://127.0.0.1:9898/");
    }

    @Test
    public void testCreateAccount() {
        int count = 1;
        Result<List<String>> result = NulsSDKTool.createAccount(count, password);
        for (String address : result.getData()) {
            System.out.println(address);
        }
    }

    @Test
    public void testCreateOfflineAccount() {
        int count = 1;

        Result<List<AccountDto>> result = NulsSDKTool.createOffLineAccount(count, password);

        for (AccountDto accountDto : result.getData()) {
            try {
                System.out.println(JSONUtils.obj2json(accountDto));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testResetPassword() {
        Result result = NulsSDKTool.resetPassword("GJbpb6GNSvkBNoNa3YH2skPLSEVvEYMfS99", password, "abcd4321");
        System.out.println(result);

    }

    @Test
    public void testRestPasswordOffline() {
        //
        String enPrikey = "3ce173a674c9b8ea218eb9c47fab069b3ca1f8150e8d22793729be5bd01084bcd17fd80060504ce62bcda792116fa30a";
        Result result = NulsSDKTool.resetPasswordOffline("GJbpb666UcupYQfY2DgigShaMb2kRbbhitW", enPrikey, password, "abcd4321");
        System.out.println(result);
    }

    @Test
    public void testImportPriKey() {
        Result result = NulsSDKTool.importPriKey("57b65cefbfcf73ec000158f3e6a98cfcac0ff36b70d68171955b87522360ddbf", password);
        System.out.println(result.getData());
        System.out.println(result);
    }

    @Test
    public void testGetPriKey() {
        Result result = NulsSDKTool.getPriKey(address, password);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void testGetPriKeyOffline() {
        Result result = NulsSDKTool.getPriKeyOffline(address, encryptedPrivateKey, password);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void importKeystore() {
        String keyStore = "{\"address\":\"GJbpb61kDNUMipDrRrPeqRRdidDY9SuXeED\",\"pubKey\":\"03370c185231a54a2a9d5bf399c7d4df54f8a47fd3ac09e601ec7e9f5945c5767f\",\"prikey\":\"\",\"encryptedPrivateKey\":\"102aaff2a1e80dda9d97a333d9a23aadad15cfe84d303f85dc835217e5defc61ca94a8f09af9e8feb5787c71a7f704e4\"}";
        Result result = NulsSDKTool.importKeystore(keyStore, password);
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
        String txHex = "02004e001a5d00008c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100201d9a0000000000000000000000000000000000000000000000000000000000086db83fdd14f6f233000117640001425026ca27e88bce748ab4b6b2c14140f76b90b9640001008096980000000000000000000000000000000000000000000000000000000000000000000000000000";

        List<SignDto> signDtoList = new ArrayList<>();
        SignDto signDto = new SignDto();
        signDto.setAddress(address);
//        signDto.setPriKey("dsfsdfsddf");
        signDto.setEncryptedPrivateKey(encryptedPrivateKey);
        signDto.setPassword(password);
        signDtoList.add(signDto);

        Result result = NulsSDKTool.sign(signDtoList, txHex);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void testBalance() {
        Result result = NulsSDKTool.getAccountBalance("tNULSeBaMshNPEnuqiDhMdSA4iNs6LMgjY6tcL", 2, 1);
        System.out.println(result);
    }

}
