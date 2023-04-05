package com.imedia.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Crypto {
    static final Logger logger = LogManager.getLogger(Crypto.class);
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA maximum decrypted ciphertext size
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static String[] GenRSAKey(int keySize) throws NoSuchAlgorithmException, IOException {
        String[] array = new String[2];
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(keySize, new SecureRandom());
        KeyPair keys = kpg.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keys.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keys.getPublic();
        array[0] = genKey(publicKey);
        array[1] = genKey(privateKey);
        return array;
    }

    static String genKey(Key key) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        } else {
            byte[] bKeyEncoded = key.getEncoded();
            byte[] b = DERtoString(bKeyEncoded);
            String rsaKey = new String(b);
            return rsaKey;
        }
    }

    private static byte[] DERtoString(byte[] bytes) {
        ByteArrayOutputStream pemStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(pemStream);
        byte[] stringBytes = encodeBase64(bytes).getBytes();
        String encoded = new String(stringBytes);
        encoded = encoded.replace("\r", "");
        encoded = encoded.replace("\n", "");
        int i;
        for (i = 0; (i + 1) * 64 <= encoded.length(); i++)
            writer.print(encoded.substring(i * 64, (i + 1) * 64));

        if (encoded.length() % 64 != 0)
            writer.print(encoded.substring(i * 64));
        writer.flush();
        return pemStream.toByteArray();
    }

    public static String Encrypt(String dataToEncrypt, String pubCer, Boolean isFile)
            throws Exception {
        RSAPublicKey _publicKey = LoadPublicKey(pubCer, isFile);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(1, _publicKey);
        int keySize = _publicKey.getModulus().bitLength() / 8;
        int maxLength = keySize - 42;
        byte[] bytes = dataToEncrypt.getBytes();
        int dataLength = bytes.length;
        int iterations = dataLength / maxLength;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= iterations; i++) {
            byte[] tempBytes = new byte[Math.min(dataLength - maxLength * i, maxLength)];
            System.arraycopy(bytes, maxLength * i, tempBytes, 0, tempBytes.length);
            byte[] encryptedBytes = cipher.doFinal(tempBytes);
            encryptedBytes = reverse(encryptedBytes);
            sb.append(encodeBase64(encryptedBytes));
        }

        String sEncrypted = sb.toString();
        sEncrypted = sEncrypted.replace("\r", "");
        sEncrypted = sEncrypted.replace("\n", "");
        return sEncrypted;
    }

    public static boolean Verify(String dataToVerify, String signedData, String pubCer,
                                 Boolean isFile)
            throws Exception {
        RSAPublicKey _publicKey = LoadPublicKey(pubCer, isFile);
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(_publicKey);
        signature.update(dataToVerify.getBytes(), 0, dataToVerify.getBytes().length);
        byte[] bSign = decodeBase64(signedData);
        boolean pass = signature.verify(bSign);
        return pass;
    }

    public static String signSHA1(String dataToSign, String privateKey, Boolean isFile) throws Exception {
        RSAPrivateKey _privateKey = LoadPrivateKey(privateKey, isFile);
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(_privateKey);
        signature.update(dataToSign.getBytes());
        byte[] bSigned = signature.sign();
        String sResult = encodeBase64(bSigned);
        return sResult;
    }

    /**
     * @param strPrivateKey
     * @return
     */
    public static PrivateKey parsePrivateKey(String strPrivateKey) {
        PrivateKey privateKey = null;
        try {
            Security.addProvider(new BouncyCastleProvider());
            byte[] privateKeyBytes = java.util.Base64.getDecoder().decode(strPrivateKey);
            privateKey = KeyFactory.getInstance("RSA", "BC").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        } catch (Exception e) {
            logger.error("RSA--ParsePrivateKey--Error:", e);
        }
        return privateKey;
    }

    public static String signSha2(String data, String strPrivateKey) {
        try {
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate((new PKCS8EncodedKeySpec(java.util.Base64.getDecoder().decode(strPrivateKey))));
            //PrivateKey privateKey = parsePrivateKey(strPrivateKey.replaceAll("\r\n", ""));
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(privateKey);
            rsa.update(data.getBytes());
            //	return HexUtils.toHexString(rsa.sign());
            return java.util.Base64.getEncoder().encodeToString(rsa.sign());
        } catch (Exception e) {
            logger.error("RSA--Sign--Sha256--Error:", e);
        }
        return "";
    }

    public static boolean verifySha2(String data, String sign, String strpublicKey) {
        try {
            PublicKey publickey = parsePublicKey(strpublicKey);
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initVerify(publickey);
            rsa.update(data.getBytes());
            byte[] signByte = java.util.Base64.getDecoder().decode(sign);
            return (rsa.verify(signByte));
        } catch (Exception e) {
            logger.error("RSA--Verify--Sha2--Error:", e);
        }
        return false;
    }

    public static PublicKey parsePublicKey(String strPublicKey) {
        PublicKey publicKey = null;
        try {
            Security.addProvider(new BouncyCastleProvider());
            byte[] publicKeyBytes = java.util.Base64.getDecoder().decode(strPublicKey);
            publicKey = KeyFactory.getInstance("RSA", "BC").generatePublic(new
                    X509EncodedKeySpec(publicKeyBytes));
        } catch (Exception e) {
            logger.error("RSA--ParsePublicKey--Error:", e);
        }
        return publicKey;
    }

//    public static String Decrypt(String dataEncrypted, String privateKey, Boolean isFile)
//            throws Exception {
//        RSAPrivateKey _privateKey = LoadPrivateKey(privateKey, isFile);
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(2, _privateKey);
//        dataEncrypted = dataEncrypted.replace("\r", "");
//        dataEncrypted = dataEncrypted.replace("\n", "");
//        int dwKeySize = _privateKey.getModulus().bitLength();
//        int base64BlockSize = (dwKeySize / 8) % 3 == 0
//                ? (dwKeySize / 8 / 3) * 4
//                : (dwKeySize / 8 / 3) * 4 + 4;
//        int iterations = dataEncrypted.length() / base64BlockSize;
//        ByteBuffer bb = ByteBuffer.allocate(100000000);
//        for (int i = 0; i < iterations; i++) {
//            String sTemp = dataEncrypted.substring(base64BlockSize * i, base64BlockSize * i
//                    + base64BlockSize);
//            byte[] bTemp = decodeBase64(sTemp);
//            bTemp = reverse(bTemp);
//            byte[] encryptedBytes = cipher.doFinal(bTemp);
//            bb.put(encryptedBytes);
//        }
//
//        byte[] bDecrypted = bb.array();
//        return (new String(bDecrypted)).trim();
//    }

    public static String Decrypt(String dataEncrypted, final String privateKey, final Boolean isFile)
            throws Exception {
        final RSAPrivateKey _privateKey = LoadPrivateKey(privateKey, isFile);
        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(2, _privateKey);
        dataEncrypted = dataEncrypted.replace("\r", "");
        dataEncrypted = dataEncrypted.replace("\n", "");

        return new String(cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(dataEncrypted)), "UTF-8");
    }

    private static RSAPrivateKey LoadPrivateKey(String key, Boolean isFile)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String sReadFile = "";
        if (isFile.booleanValue()) {
            File file = new File(key);
            sReadFile = fullyReadFile(file);
        } else {
            sReadFile = key.trim();
        }
        if (sReadFile.startsWith("-----BEGIN PRIVATE KEY-----") && sReadFile.endsWith(
                "-----END PRIVATE KEY-----")) {
            sReadFile = sReadFile.replace("-----BEGIN PRIVATE KEY-----", "");
            sReadFile = sReadFile.replace("-----END PRIVATE KEY-----", "");
            sReadFile = sReadFile.replace("\n", "");
            sReadFile = sReadFile.replace("\r", "");
            sReadFile = sReadFile.replace(" ", "");
        }
        byte[] b = decodeBase64(sReadFile);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private static RSAPublicKey LoadPublicKey(String pubCer, Boolean isFile)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            CertificateException {
        RSAPublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            java.security.spec.EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decode(
                    pubCer));
            publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        } catch (Exception var5) {
        }
        return publicKey;
    }

    private static String fullyReadFile(File file) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        byte[] bytesOfFile = new byte[(int) file.length()];
        dis.readFully(bytesOfFile);
        dis.close();
        String sRead = new String(bytesOfFile);
        return sRead.trim();
    }

    private static String encodeBase64(byte[] dataToEncode) {
        String strEncoded = "";
        try {
            strEncoded = java.util.Base64.getEncoder().encodeToString(dataToEncode);
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        return strEncoded;
    }

    private static byte[] decodeBase64(String dataToDecode) {
        byte[] bDecoded = null;
        try {
            bDecoded = java.util.Base64.getDecoder().decode(dataToDecode);
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        return bDecoded;
    }

    private static byte[] reverse(byte[] b) {
        int left = 0;
        for (int right = b.length - 1; left < right; right--) {
            byte temp = b[left];
            b[left] = b[right];
            b[right] = temp;
            left++;
        }

        return b;
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static boolean Verify256(String dataToVerify, String signedData, String pubCer,
                                    Boolean isFile)
            throws Exception {
        RSAPublicKey _publicKey = LoadPublicKey(pubCer, isFile);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(_publicKey);
        signature.update(dataToVerify.getBytes(), 0, dataToVerify.getBytes().length);
        byte[] bSign = decodeBase64(signedData);
        boolean pass = signature.verify(bSign);
        return pass;
    }

    public static PublicKey Verify256_(String pubCer
    )
            throws Exception {

        PublicKey privateKey_ = KeyFactory.getInstance("RSA")
                .generatePublic((new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(pubCer))));
        return privateKey_;

    }

    public static boolean verifySha2(String data, String sign, PublicKey publicKey) {
        try {
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initVerify(publicKey);
            rsa.update(data.getBytes());

            byte[] signByte = java.util.Base64.getDecoder().decode(sign);
            return (rsa.verify(signByte));
        } catch (Exception e) {
            logger.error("RSA--Verify--Sha2--Error:", e);
        }
        return false;
    }

    public static String Sign256(String dataToSign, String privateKey, Boolean isFile) throws Exception {
        RSAPrivateKey _privateKey = LoadPrivateKey(privateKey, isFile);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(_privateKey);
        signature.update(dataToSign.getBytes());
        byte[] bSigned = signature.sign();
        String sResult = encodeBase64(bSigned);
        return sResult;
    }

    public static String CreateSHA256Signature(final String stringData, String secret_key) throws Exception {
        String hexHash = "";
        try {
            if ((secret_key.length() % 2) == 1)
                secret_key += '0';
            byte[] bytes = new byte[secret_key.length() / 2];
            for (int i = 0; i < secret_key.length() - 1; i += 2) {
                bytes[i / 2] = (byte) Integer.parseInt(secret_key.substring(i, i + 2), 16);
            }

            SecretKeySpec signingKey = new SecretKeySpec(bytes, "HMACSHA256");

            Mac mac = Mac.getInstance("HMACSHA256");
            mac.init(signingKey);
            mac.update(stringData.getBytes("UTF-8"));
            byte[] digest = mac.doFinal();
            for (byte b : digest) {
                hexHash += String.format("%02X", b);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return hexHash;
    }

    public static String decryptRSA1(String data, String privateKey) throws Exception {
        RSAPrivateKey _privateKey = LoadPrivateKey(privateKey, false);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, _privateKey);
        byte[] dataBytes = java.util.Base64.getDecoder().decode(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // Decrypt data segments
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        // Decrypted content
        return new String(decryptedData, "UTF-8");
    }
}
