package com.easypay;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * 代扣测试
 * @author suhtc
 *
 */
public class DKMain {
	
	//标记生产还是测试环境
    public static boolean isTest = true ;

    //根据接口文档生成对应的json请求字符串
    private static String biz_content = "";

    //接口文档中的方法名
    private static String service = "easypay.pay.unionDK.dkpay";

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

    //代扣 - 获取短信验证码
    public static void dkSendSMS(){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("out_trade_no", KeyUtils.getOutTradeNo());
        sParaTemp.put("mobile", getEncode("13552535506")); //手机号
        sParaTemp.put("acc", getEncode("6216261000000000018"));   //银行卡号
//        sParaTemp.put("name", getEncode("聂剑平"));    //账户姓名
//        sParaTemp.put("idno", getEncode("340827199311106316")); //身份证号
//        sParaTemp.put("cvv", getEncode("123"));
//        sParaTemp.put("validity_year", getEncode("12"));
//        sParaTemp.put("validity_month", getEncode("23"));
        biz_content = sParaTemp.toString();

        service  = "easypay.pay.unionDK.sendSMS";
    }


    private static String getEncode(String data){
        return StringUtils.bytesToHexStr(DesUtil.desEncode(data, DES_ENCODE_KEY));
    }

    //代扣 - 签约
    public static void dkSign(){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("out_trade_no", KeyUtils.getOutTradeNo());
        sParaTemp.put("bill_type", "03");
        sParaTemp.put("mobile", getEncode("13552535506")); //手机号
        sParaTemp.put("acc", getEncode("6216261000000000018"));   //银行卡号
        sParaTemp.put("bank_code", "PAB");
        sParaTemp.put("name", "全渠道");    //账户姓名
        sParaTemp.put("idno_type", 1);
        sParaTemp.put("idno", getEncode("341126197709218366")); //身份证号
//        sParaTemp.put("cvv", getEncode("123"));
//        sParaTemp.put("validity_year", getEncode("12"));
//        sParaTemp.put("validity_month", getEncode("23"));
        sParaTemp.put("sms_code", "111111");

        biz_content = sParaTemp.toString();
        service  = "easypay.pay.unionDK.sign";
    }

    //代扣支付
    public static void dkPay(){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("out_trade_no", KeyUtils.getOutTradeNo());
        sParaTemp.put("acc", getEncode("6216261000000000018"));   //银行卡号
        sParaTemp.put("amount", "1");
        sParaTemp.put("business_time", "2020-01-16 12:54:00");
        sParaTemp.put("subject", "subject");
        sParaTemp.put("notify_url", "https://www.baidu.com");

        biz_content = sParaTemp.toString();
        service  = "easypay.pay.unionDK.dkpay";
    }
    
    //代扣 - 解约
    public static void dkRelease(){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("out_trade_no", KeyUtils.getOutTradeNo());
        sParaTemp.put("acc", getEncode("6216261000000000018"));   //银行卡号

        biz_content = sParaTemp.toString();
        service  = "easypay.pay.unionDK.dkRelease";
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

            //代扣 - 获取短信验证码
//            dkSendSMS();

            //代扣 - 签约
//            dkSign();

            //代扣 - 支付
//            dkPay();
            
            //代扣 - 解约
            dkRelease();

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
