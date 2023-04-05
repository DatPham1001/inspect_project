package com.imedia.util;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

public class TripleDES {
    static final Logger logger = LogManager.getLogger(TripleDES.class);
    static final Gson gson = new Gson();

    public static String decrypt(String key, String cipher)
            throws Exception {
        byte[] bytes = null;
        SecretKey sKey = null;
        Security.addProvider(new BouncyCastleProvider());
        Cipher encipher = Cipher.getInstance("DESede/ECB/PKCS5Padding", "BC");
        bytes = Commons.hexToBytes(cipher);
        // bytes=Base64Utils.base64Decode(cipher);
        // bytes = hexToBytes(cipher);
        sKey = getKey(key);
        // Encrypt
        byte[] enc;
        encipher.init(Cipher.DECRYPT_MODE, sKey);
        enc = encipher.doFinal(bytes);
        String returnStr = new String(enc, StandardCharsets.UTF_8);
        logger.info("====DECRYPT DATA==== " + returnStr);
        return returnStr;

    }

    public static String encrypt(String key, String input)
            throws Exception {
        logger.info("====ENCRYPT DATA==== " + input);
        byte[] bytes = null;
        SecretKey sKey = null;
        Security.addProvider(new BouncyCastleProvider());
        Cipher encipher = Cipher.getInstance("DESede/ECB/PKCS5Padding", "BC");
        bytes = input.getBytes(StandardCharsets.UTF_8);
        sKey = getKey(key);
        // Encrypt
        byte[] enc;
        encipher.init(Cipher.ENCRYPT_MODE, sKey);
        enc = encipher.doFinal(bytes);
        String res = Commons.bytesToHex(enc);

        return res;
    }

    private static SecretKey getKey(String key) {
        // key=TripleDESEncryption.md5(key);
        key = key.substring(0, 24);
        byte[] bKey = key.getBytes();
        try {
            DESedeKeySpec keyspec = new DESedeKeySpec(bKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");

            SecretKey lclSK = keyFactory.generateSecret(keyspec);

            return lclSK;
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

    }


    public static void main(String[] args) throws Exception {
        String e = "5365d4e13313cc4aa76eec3a738b4754f318a3a20d6a1ab58cd87f93632fb3cc07fec1b93aeb071cffdfdff83d9dbe29fe34910ade2ff38abde8e2c550485864bde5b16df0e00b47836795d66c6d2750ce4c1659a0a4e6a46a158f625f5e21e10cf1f1f2725c4c8aadb7cb5791b2b93433f5680d27391679f3c907ca20fc2025bc0b2efa46ca1dd5e96d2b2beee069c1abf4da4c501d087c0617b4427ab082f35df222507c1ac8a1c4c672a318d1d323537a05262dbb7a247c48ea8cd26a0e04271199c7ae7533777161d2e45104725beebc79c1d8a26f8d7ef3ee24d6f0601d00b559e2d2b89b8fe79f781998904431896ff4fa10c00c9ce62c0ac3712a8e042a674bc8c71583c0cce97148dd61ef8845f2b1cdd927a94a07b917aecace7ca1287d835d27b49b603c3beaf04f2a30dfb4140f3069b770ccf8f1e5904fd870164794a08f6e062d4a0b74fef06fe0e32794a31d87bb08487192c8a1a01696ccabcada9f4c89e6d15c20b3cfdb7904fe9952cf280812af513f8112bc98a8737f34cbda48e013f4f3c7f04759559611339a0579a83eca8414af5007bbb875b05c4a";
////        String req = encrypt("1f2f8c540779987923165751", e);
        String res = decrypt("1f2f8c540779987923165751",
                e);
        System.out.println(res);
        System.out.println(encrypt("1f2f8c540779987923165751", "{\"username\":\"0981281015\",\"login_from\":0,\"client_identity_str\":\"\",\"account_epurse_id\":0}"));
    }
}
