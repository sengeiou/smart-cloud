package com.smart.pay.biz.wx;

import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import com.smart.pay.biz.wx.exceptions.WxPayException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/23 15:22
 * @see com.smart.pay.biz.wx
 * @since 1.0
 **/
public class AesUtils {

    static final int KEY_LENGTH_BYTE = 32;
    static final int TAG_LENGTH_BIT = 128;
    private final byte[] aesKey;

    public AesUtils(byte[] key) {
        if (key.length != KEY_LENGTH_BYTE) {
            throw new IllegalArgumentException("无效的ApiV3Key，长度必须为32个字节");
        }
        this.aesKey = key;
    }

    public static byte[] decryptToByte(byte[] nonce, byte[] cipherData, byte[] key)
            throws GeneralSecurityException {
        return decryptToByte(null, nonce, cipherData, key);
    }

    public static byte[] decryptToByte(byte[] associatedData, byte[] nonce, byte[] cipherData, byte[] key)
            throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, spec);
            if (associatedData != null) {
                cipher.updateAAD(associatedData);
            }
            return cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext)
            throws GeneralSecurityException, IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData);

            return new String(cipher.doFinal(BaseEncoding.base64().decode(CharMatcher.whitespace().removeFrom(ciphertext))), "utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String decryptToString(String associatedData, String nonce, String ciphertext,String apiV3Key)
            throws GeneralSecurityException, IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key = new SecretKeySpec(apiV3Key.getBytes(), "AES");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData.getBytes());

            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String decryptToString(String param, String mchKey) {
        try {

            final String keyMd5String = DigestUtils.md5Hex(mchKey).toLowerCase();
            SecretKeySpec key = new SecretKeySpec(keyMd5String.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(param)), StandardCharsets.UTF_8);
        }catch (Exception e) {
            throw new WxPayException("解密退款通知加密信息时出错", e);
        }
    }

}
