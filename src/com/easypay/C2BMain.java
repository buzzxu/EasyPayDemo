package com.easypay;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * c2b测试
 * @author njp
 *
 */
public class C2BMain {

	//标记生产还是测试环境
    public static boolean isTest = true ;

    //根据接口文档生成对应的json请求字符串
    private static String biz_content = "";

    //接口文档中的方法名
    private static String service = "easypay.pay.c2b.bindcard";

    //商户号
    private static String merchant_id = KeyUtils.TEST_DEFAULT_MERCHANT_ID;

    //接入机构号
    private static String partner = KeyUtils.TEST_DEFAULT_PARTNER;

    //请求地址
    private static String url = KeyUtils.DEFAULT_URL;

    //商户私钥
    private static String key = KeyUtils.TEST_MERCHANT_PRIVATE_KEY;

    //易生公钥
    private static String easypay_pub_key = KeyUtils.TEST_EASYPAY_PUBLIC_KEY;

    //加密密钥
    private static String DES_ENCODE_KEY = KeyUtils.TEST_DES_ENCODE_KEY;

    //C2B绑卡
    public static void c2bBindCard(){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("name", getEncode("测试"));    //账户姓名
        sParaTemp.put("id_no", getEncode("340827199311101234")); //身份证号
        sParaTemp.put("bank_code", "ICBC");//民生银行(银行编号请见‘特约支付-绑卡’页面的银行表)
        sParaTemp.put("acc", getEncode("6212260200089401234"));   //银行卡号
//        sParaTemp.put("acc_attr", "2"); //卡属性1 – 借记卡；2-贷记卡
        sParaTemp.put("mobile", getEncode("18010461234")); //手机号
        sParaTemp.put("out_trade_no", KeyUtils.getOutTradeNo());
//        sParaTemp.put("cvv", getEncode("123"));
//        sParaTemp.put("validity_date", getEncode("1223"));
        biz_content = sParaTemp.toString();

        service  = "easypay.pay.c2b.bindcard";
    }


    private static String getEncode(String data){
        return StringUtils.bytesToHexStr(DesUtil.desEncode(data, DES_ENCODE_KEY));
    }

    //c2b获取码
    public static void getC2BCode(String wtaccid){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("out_trade_no", KeyUtils.getOutTradeNo());
        sParaTemp.put("wtaccid", wtaccid);
        sParaTemp.put("device_id", "A0-C5-89-AE-A4-25");
        sParaTemp.put("device_type", "4");
        sParaTemp.put("source_ip", "114.86.194.17");
        sParaTemp.put("accountid_hash", "8dcf7d1da221d91f513520eca5c25c48");

        biz_content = sParaTemp.toString();
        service  = "easypay.pay.c2b.getCode";
    }

    //c2b支付
    public static void c2bPay(String pay_code){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("seller_email", "18679106330@gmail.com");
        sParaTemp.put("amount", "1");
        sParaTemp.put("business_time", "2019-04-08 09:46:00");
        sParaTemp.put("notify_url", "https://www.baidu.com");
        sParaTemp.put("order_desc", "c2b");
        sParaTemp.put("out_trade_no", KeyUtils.getOutTradeNo());
        sParaTemp.put("pay_code", pay_code);
        sParaTemp.put("pay_type", "unionBarCodePay");
        sParaTemp.put("subject", "subject");
        sParaTemp.put("body", "body");

        biz_content = sParaTemp.toString();
        service  = "easypay.pay.c2b.pay";
    }

    public static void main(String[] args) {
        //易生请求示例子
        try {

            //系统入件之后生成的合作伙伴ID（一般会通过邮件发送）
            if (!isTest) {
                //商户号
                merchant_id = KeyUtils.SC_DEFAULT_MERCHANT_ID;
                //接入机构号
                partner = KeyUtils.SC_DEFAULT_PARTNER;
                //请求地址
                url = KeyUtils.SC_URL;
                //商户私钥
                key = KeyUtils.SC_MERCHANT_PRIVATE_KEY;
                //易生公钥
                easypay_pub_key = KeyUtils.SC_EASYPAY_PUBLIC_KEY;
                //加密密钥
                DES_ENCODE_KEY = KeyUtils.SC_DES_ENCODE_KEY;
            }

            //c2b绑卡
//            c2bBindCard();

            //c2b获取码
//            getC2BCode("ES2019041700105793");

            //c2b特约支付
//            c2bPay("6226229270603618238");

            //加密类型，默认RSA
            String sign_type = KeyUtils.TEST_DEFAULT_ENCODE_TYPE;
            //编码类型
            String charset = KeyUtils.TEST_DEFAULT_CHARSET;

            //根据请求参数生成的机密串
            String sign = KeyUtils.getSign(key, charset, biz_content);
            System.out.print("计算签名数据为：" + sign + "\n");
            Map<String, String> reqMap = new HashMap<String, String>(6);
            reqMap.put("biz_content", biz_content);
            reqMap.put("service", service);
            reqMap.put("partner", partner);
            reqMap.put("sign_type", sign_type);
            reqMap.put("charset", charset);
            reqMap.put("sign", sign);

            StringBuilder resultStrBuilder = new StringBuilder();
            int ret = HttpConnectUtils.sendRequest(url, KeyUtils.TEST_DEFAULT_CHARSET, reqMap, 30000, 60000, "POST", resultStrBuilder, null);
            System.out.print(" \n请求地址为：" + url +
                    "\n 请求结果为：" + ret +
                    "\n 请求参数为：" + reqMap.toString() +
                    "\n 返回内容为：" + resultStrBuilder.toString() + "\n");
            //易生公钥验证返回签名
            StringUtils.rsaVerifySign(resultStrBuilder, easypay_pub_key);
        }catch (Exception e){
            System.out.print(e.getMessage()+ "\n");
        }
    }

}
