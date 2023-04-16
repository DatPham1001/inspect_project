package com.imedia.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * gen key:
 * -
 * <p>
 * type private key: PKCS#8 <br>
 * type public key: PKCS#1 <br>
 * <p>
 * to sign and verify data:
 * - use RSAPublicKey and X509EncodedKeySpec
 * to build java.security.PublicKey<br>
 * - type: signature and modify: SHA256withRSA <br>
 * <p>
 * decrypt and encrypt:
 * - use type Cipher: RSA/ECB/PKCS1Padding <br>
 * - encrypt: using private key <br>
 * - decrypt: using public key
 *
 * @author luongvv <br>
 */
public class RSAUtil {
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCw+qVx1FE/qO/Big1gwU4Xvqk5lV7Qi+UM0tPFA7aZOWCrPENsZJH3y50JfOEtSSsiJjdWUmI09rgUAzA+L6FoS16FC0MObnReWd5WOblHgZt6Y6n7MZnD5BbiJ2OSke6INqfQ/M22JgcDumV3PVVjUU4OPWy/B7pz+XvUlmfEcQIDAQAB";
    private static String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALD6pXHUUT+o78GKDWDBThe+qTmVXtCL5QzS08UDtpk5YKs8Q2xkkffLnQl84S1JKyImN1ZSYjT2uBQDMD4voWhLXoULQw5udF5Z3lY5uUeBm3pjqfsxmcPkFuInY5KR7og2p9D8zbYmBwO6ZXc9VWNRTg49bL8HunP5e9SWZ8RxAgMBAAECgYABFTeH78WjfxQgegjE68nfiVIdwRU4b3KyVmPnU7N9UoWN5qt+8UqXWo6WbUYvccAq0FMMWyqwqkXMEYztBHuXVivkUC4gYpwo3dR4MHkrMDC6pjZ7jt+OW4/vHlVkqF14A6TZwoU42WE4vP1AT+PE5tdymhAqPBxtRF4kByI9QQJBANTgJD+Kye4NNXgMybLupJvBV7ycTkZ+2fs+i26uxhE+o03ixNf9kMJoUYmFZJGCfF6EkusbLyIZEYxqbIUrpakCQQDU1ORC0hTKHCmG8188ghMHGhQBn2C4mSuW8XC5qAlOLCQKDZFVV+PjQWnqqFbj85gI6Hr+1ijwD5ZElpSUk1WJAkAHSEu1KMhTrnv/yWA1ENjswyZ/O1GqM7i9UZxvgucfu+bWSUW2peQH4o23iKqyF6atjReBl4J7RiRFi3Z+78lRAkAhswflkLAcBHOK27RDt3Dd2gI684p0EES3pMktjG7UwuZ99QIDGmSKEkx8DGWduG9lUbJP8YcP49fuSk2CbQXJAkASlcWuMqbTjf0tjzv7b8nvW2lUcY+BWosVlzQHPsA/zK5t5Dtr05TBPLcAFrzVl+N6VWC724PzJMrB5CUbTdDX";

    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(
                    base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(
                base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException,
            IllegalBlockSizeException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    public static String encryptData(String data, String publicKey) throws BadPaddingException,
            IllegalBlockSizeException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        String encrypted = Base64.getEncoder().encodeToString(encrypt(data, publicKey));
        return encrypted;
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public static String decrypt(String data, String base64PrivateKey)
            throws IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
            NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(
                base64PrivateKey));
    }

    public static String sign(String privateKey, String message) throws NoSuchAlgorithmException,
            InvalidKeyException,
            SignatureException, UnsupportedEncodingException, InvalidKeySpecException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        String privateKeyStr1 = privateKey.replace("\n", "");
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr1);
        PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(
                privateKeyBytes));
        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initSign(priKey);
        sign.update(message.getBytes("UTF-8"));
        return new String(Base64.getEncoder().encode(sign.sign()), "UTF-8");
    }

    public static boolean verify(String publicKey, String message, String signature)
            throws SignatureException,
            NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException,
            InvalidKeySpecException {
        System.out.println("publicKey: " + publicKey);
        byte[] encoded = Base64.getDecoder().decode(publicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initVerify(pubKey);
        sign.update(message.getBytes("UTF-8"));
        return sign.verify(Base64.getDecoder().decode(signature.getBytes("UTF-8")));
    }

    public static void main(String[] args)
            throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException,
            BadPaddingException {
        exampleEncryptDecrypt();
        exampleSignVerify();
    }

    private static void exampleEncryptDecrypt()
            throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException,
            NoSuchPaddingException {
        try {
            String encryptedString = "mqoSm06HKS/UEgP/AIorUa0Wbs5DXqfDN4jwylb1SRUgam8SDFBCF7WWF3gyjEQFEtNTu+Bcrjwt+1Le6ioXbH6R6593F2hGU/ZFEM000Rkqp74n8lJQW+KjmeuhxiI/ObywP1AwNgR/ACvRa4YE1LjIf5cBro81pWZI9XchwC0=";
//			String encryptedString = Base64.getEncoder().encodeToString(encrypt(
//					"Dhiraj is the author", publicKey));
            System.out.println(encryptedString);
            String decryptedString = RSAUtil.decrypt(encryptedString, privateKey);
            System.out.println(decryptedString);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void exampleSignVerify()
            throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException,
            NoSuchPaddingException {
        try {
            String textRow = "this is sign";
            String strangerRow = "this is stranger text";
            String contentSign = sign(privateKey, textRow);
            System.err.println("contentSign: " + contentSign);

            boolean verifyOK = verify(publicKey, textRow, contentSign);
            System.err.println(verifyOK);

            boolean verifyNOK = verify(publicKey, strangerRow, contentSign);
            System.err.println(verifyNOK);

        } catch (NoSuchAlgorithmException | SignatureException | UnsupportedEncodingException
                | InvalidKeySpecException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}
