package io.liuzhilin.mobileanywhere.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DIgestUtils {

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String sha1(String s){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha1");
            byte[] bytes = messageDigest.digest(s.getBytes(Charset.forName("UTF-8")));
            String hex = bytesToHexFun1(bytes);
            return hex;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("do not support sha1");
        }
    }

    public static String bytesToHexFun1(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for(byte b : bytes) { // 使用除与取余进行转换
            if(b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }

}
