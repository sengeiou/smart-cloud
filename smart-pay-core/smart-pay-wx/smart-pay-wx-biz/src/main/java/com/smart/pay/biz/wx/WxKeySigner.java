package com.smart.pay.biz.wx;

import com.wechat.pay.contrib.apache.httpclient.auth.Signer;

import java.security.*;
import java.util.Base64;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/18 15:17
 * @see com.smart.pay.biz.wx
 * @since 1.0
 **/
public class WxKeySigner implements Signer {


    private String certificateSerialNumber;

    private PrivateKey privateKey;

    public WxKeySigner(String serialNumber, PrivateKey privateKey) {
        this.certificateSerialNumber = serialNumber;
        this.privateKey = privateKey;
    }


    @Override
    public SignatureResult sign(byte[] message) {
        return new SignatureResult(signStr(message),certificateSerialNumber);
    }

    public String signStr(byte[] message) {
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(privateKey);
            sign.update(message);

            return Base64.getEncoder().encodeToString(sign.sign());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持SHA256withRSA", e);
        } catch (SignatureException e) {
            throw new RuntimeException("签名计算失败", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("无效的私钥", e);
        }
    }

}
