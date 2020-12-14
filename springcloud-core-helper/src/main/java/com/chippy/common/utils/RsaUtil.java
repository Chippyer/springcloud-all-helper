package com.chippy.common.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA工具类
 *
 * @author: chippy
 */
public class RsaUtil {

    private static final String RSA = "RSA";

    /**
     * 获取公钥
     *
     * @param publicKey 公钥字节值
     * @return java.security.PublicKey
     */
    public static PublicKey getPublicKey(byte[] publicKey) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return kf.generatePublic(spec);
    }

    /**
     * 获取私钥
     *
     * @param privateKey 私钥字节值
     * @return java.security.PrivateKey
     */
    public static PrivateKey getPrivateKey(byte[] privateKey) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return kf.generatePrivate(spec);
    }

    /**
     * 将字符串转换为字节
     *
     * @param str 字节内容
     * @return java.lang.String
     * @author chippy
     */
    public static byte[] toBytes(String str) throws IOException {
        return (new BASE64Decoder()).decodeBuffer(str);
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey =
            (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }
}
