package com.chippy.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

/**
 * String工具类
 *
 * @author: chippy
 */
public class StringUtil {

    /**
     * 将字节转换为字符串
     *
     * @param b 字节内容
     * @return java.lang.String
     * @author chippy
     */
    public static String toHexString(byte[] b) {
        return (new BASE64Encoder()).encodeBuffer(b);
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

}
