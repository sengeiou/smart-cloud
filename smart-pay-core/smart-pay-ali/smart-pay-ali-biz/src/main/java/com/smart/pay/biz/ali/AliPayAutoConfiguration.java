package com.smart.pay.biz.ali;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.InputStream;

/**
 * <li>配置类</li>
 *
 * @author wangpeng
 * @date 2021/11/22 15:16
 * @see com.smart.pay.biz.wx
 * @since 1.0
 **/
@Configuration
@Order
public class AliPayAutoConfiguration {

    @Bean
    public ApplicationRunner initialization(){
        return (args) -> {
            /*WxAdminConfig.MCH_ID="1604187129";
            WxAdminConfig.WX_PAY_KEY="PejXZwlTt6oKS42DSF50dKzBmpMK2SF0";
            WxAdminConfig.WX_PAY_KEY_V2="LaiTingKeJi20201116GongXiFaCaiHX";
            WxAdminConfig.SERIAL_NUMBER="5A0FCF9CE4E8D8C35C488F4F73C0BD9F0E45722D";
            String path="cet/apiclient_key.pem";
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            WxAdminConfig.PRIVATE_KEY= PemUtil.loadPrivateKey(resourceAsStream);
            WxAdminConfig.updateTime=120;
            WxKeySigner wxKeySigner = new WxKeySigner(WxAdminConfig.SERIAL_NUMBER, WxAdminConfig.PRIVATE_KEY);
            WxAdminConfig.verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(WxAdminConfig.MCH_ID, wxKeySigner),
                    WxAdminConfig.WX_PAY_KEY.getBytes("utf-8"),
                    WxAdminConfig.updateTime);

            new WxPayV2MicropayImpl();
            new WxPayJsapiImpl();
            new WxPayNativeImpl();
            new WxPayNotifyImpl();
            new WxPayRefundNotifyImpl();
            new WxPayRefundNotifyV2Impl();*/
            AliPayConfig.setOpenApiDomain("https://openapi.alipaydev.com/gateway.do");
            AliPayConfig.setAppid("2021000118658740");
            AliPayConfig.setPid("2088621957222150");
            AliPayConfig.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqKKGTDml9fp1iPvW9Mb8btJlXDfLXfkIdA/0ydclLu8raKB/JgIssJ52YHStPXVQxcNP61S73kkfCg8WbnGAo9QhvoctJHuXxGT31lQuehAc4g+NXmlcgMYHIJMMmgOcCyRnwBVr6lCj2/O+VCg/aaobnGvwP8vQAMbLrMykxo6i6arQFrEw9Y7vWcVck3UWR9acg0+Z23ovcQ6D8PeYmICHEoz5WWUB15WFqdHd7zWiRXwgCynsEX2y7Bnjcc6EIB0202xPxvwc8/n80mtQDi1vNICKPpnXkH9m8dT6OUEZvfJHZU73X+qCfm1NlnLX3LdAEUJ+5E4Jl7C2z0I85QIDAQAB");
            AliPayConfig.setPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCoooZMOaX1+nWI+9b0xvxu0mVcN8td+Qh0D/TJ1yUu7ytooH8mAiywnnZgdK09dVDFw0/rVLveSR8KDxZucYCj1CG+hy0ke5fEZPfWVC56EBziD41eaVyAxgcgkwyaA5wLJGfAFWvqUKPb875UKD9pqhuca/A/y9AAxsuszKTGjqLpqtAWsTD1ju9ZxVyTdRZH1pyDT5nbei9xDoPw95iYgIcSjPlZZQHXlYWp0d3vNaJFfCALKewRfbLsGeNxzoQgHTbTbE/G/Bzz+fzSa1AOLW80gIo+mdeQf2bx1Po5QRm98kdlTvdf6oJ+bU2Wctfct0ARQn7kTgmXsLbPQjz" +
                    "lAgMBAAECggEAfjeHBS5wxTb9GdZFAVieP+RB8U/hayil5xi6QwhA0Gw6tUjLYgtmn5s7Rt6Ne95+4kZZXu5A3xMjpEe+plNV0UlJKG3Mn+3RnjFkPosrbjj5JUo2qV962PdfBkaJvAzPkpdhACgIaYyJ2GsLHboyHEgx+lXfsCBAQxSIBYH2IpYNv7HTgun1Pvk/l7qVtIiice9FTY/67TAVsZbERzJM16H2FTYGvPkL1qBjBW7vhlC4SYZrm2JRkHPrltzEIUUVSXwtMi3rviraAlnlC+ZJjEu1ZFZekxPR6A0TXfe45mvFcAC1pjNRhSI24HStMbJpf6zfU7r1/fq1dCwTn5sKAQKBgQDvRhh1QM27OQbF9ekxAu3c4OTjavbmY1W6q/gJmXn8fR9Bcn16h" +
                    "Rs00+XNYj8TUxmilOknPTBVt5J+FyR/FEdOb/LT0Lsow63ROhcbZmnKCB5+VhkH+VmIDU4U5kXEi3wT5+ZmtxrYFf6rK5bhwqUS23usd9MxGxpIY3m2Hyt1BQKBgQC0bFDPFsiiN8pdyIMF42nw2GUw6voG11dbcFRiw0JNKlqqkGcx6DnORManXFWpfobulKt+u/2nNFWm4fuzqXJ3eawNoxXePUkIwBDk22cY/ymsisGX+UrjteJpuSrYP5Vn5BWo5/DnUYAkjNU5ANW/kPNEmidLUVRi9J6BiPQuYQKBgQC2I8W6e/of6ukdCHTaukAXQ5r5AxOvjS5VS1Mk+4/Ag/6m82BRQ8HSFPt5vLAHJdr9yB0zoYCJv5PkJ/nAQs+FBfLzCpFuFobaHj44vV0KTeP" +
                    "oh+HGECn3RymI/0FcvFFXVdAtoHTuyM9zvVq9wE2obStXvl8lolv/frnHyTJrgQKBgFxzfF0BgcGN8QZpbqofw/OEDyYn4gKLadoTD0qkX18HKvpnkFmEc8p8d4bey45UpXOvfsksL2f0a0deWQnQ9DTAhidewB/nK3tuX7nFq2ilp4cget8TAQr58pDWL1wsCLT5rRel8k2194HDIKdbBCxrbBxlGWmAvUsPqgEaWnVBAoGBAN837O8YnZr1jKdRK+HzGjrnMPYfx7guygCBepAj7kmtcSrxrbY9YZN7B0Gl6Z64JntM9mkMbmxHkMKOdPrflk5OjXQrPd+9GkUq4H87Mk4r4UdS+NjHfDEKbEbko396+IOZS+Kfvd/JWVMDr7IB5ZLVt3sNtdjRcP4QbKStskeD");
            AliPayConfig.setAlipayPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlRIe2Yg/3doVHf/nh52vPmvyeAXFjeYdq8hoSge3Nne14oZR3YOL52Rb6RrLbXgP3Ugkyr+Fd4pWgjWVNnCSWGBPvQ2x0VQE5xumnNFGvTa6421lfk2XLXCv31hOIjQfy9Rc6LoZha6byJ0sVMwCuKemnRWVaXlARmGTd/LtZT2EHgzSl9nqEPXO8R2N4M4fT8doTzn1BddE3Sv0ALQbE1fdlrqYCDGryjv0xzl4RDboR/TMZdj4qG1yu+d7K20allao2UBIT3a+RMYE2bZPwvr9yQMZNLQww8hrBRFyAIvd4o/ND2D7I+EDVD5MfjuVj6IpsgpcB///XdNXfLQlmwIDAQAB");
            AliPayConfig.setSignType("RSA2");
        };
    }

}
