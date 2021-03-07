package com.ls.download;

import com.ls.http.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

public class AesUtils {

    public static void main(String[] args) throws Exception {
        byte[] key = FileUtils.readFileToByteArray("C:\\Users\\warho\\Desktop\\aa\\get_dk");
        byte[] content = FileUtils.readFileToByteArray("C:\\Users\\warho\\Desktop\\aa\\56435119.ts");

        byte[] iv = new byte[16];
        byte[] data = decrypt(content, key, iv);

        FileOutputStream fos = new FileOutputStream("C:\\Users\\warho\\Desktop\\aa\\56435119_dec.ts");
        fos.write(data);
        fos.flush();
        fos.close();
    }

    public static boolean initialized = false;

    /**BouncyCastle作为安全提供，防止我们加密解密时候因为jdk内置的不支持改模式运行报错。**/
    public static void initialize() {
        if (initialized)
            return;
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;
    }

    // 生成iv
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }


    public static byte[] decrypt(byte[] content, byte[] aesKey, byte[] ivByte) {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            Key sKeySpec = new SecretKeySpec(aesKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
