package com.easypay;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 代付测试
 * @author njp
 *
 */
public class DsfMain {
	
	//标记生产还是测试环境
    public static boolean isTest = true;

    //根据接口文档生成对应的json请求字符串
    private static String biz_content = "";

    //接口文档中的方法名
    private static String service = "trade.acc.dsfpay.pay";

    //商户号
    private static String merchant_id = "900029000000354";

    //接入机构号
    private static String partner = "900029000000354";

    //请求地址
    private static String url = KeyUtils.DEFAULT_URL;

    //商户私钥
    private static String key = KeyUtils.TEST_MERCHANT_PRIVATE_KEY;

    //易生公钥
    private static String easypay_pub_key = KeyUtils.TEST_EASYPAY_PUBLIC_KEY;

    //加密密钥
    private static String DES_ENCODE_KEY = KeyUtils.TEST_DES_ENCODE_KEY;

    //实时代付
    public static void dsfPay(){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("out_trade_no", KeyUtils.getOutTradeNo());
        sParaTemp.put("type", 1);//代付类型 固定传1
        sParaTemp.put("nbkno", "313100001274"); //联行号
//        sParaTemp.put("bank_code", "308");//民生银行(银行编号请见‘代付’页面底部)
//        sParaTemp.put("bank_name", "上海");//银行网点名称
//        sParaTemp.put("city", "上海");
        sParaTemp.put("acc", "6225881008024282");   //银行卡号
        sParaTemp.put("name", "test");    //账户姓名
        sParaTemp.put("acc_type", 2); //付款账户类型：1 结算账户 2 现金账户, 默认2现金账户
        sParaTemp.put("amount", "180");
        sParaTemp.put("bis_type", 1);//收款账户类型：0 对公 1 对私
        biz_content = sParaTemp.toString();

        service  = "trade.acc.dsfpay.pay";
    }
    
  //联机代付
    public static void dsfNewPay(){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("out_trade_no", KeyUtils.getOutTradeNo().substring(0, 18));
//        sParaTemp.put("nbkno", ""); //联行号
        sParaTemp.put("acc", "6225881008024283");   //银行卡号
        sParaTemp.put("name", "test");    //账户姓名
        sParaTemp.put("idno", "123456789012345679");    //身份证号
        sParaTemp.put("acc_type", 2); //付款账户类型：1 结算账户 2 现金账户, 默认2现金账户
        sParaTemp.put("amount", "180");
        biz_content = sParaTemp.toString();

        service  = "trade.acc.dsfpay.newPay";
    }

    

    private static String getEncode(String data){
        return StringUtils.bytesToHexStr(DesUtil.desEncode(data, DES_ENCODE_KEY));
    }

    //代付查询
    public static void dsfQuery(String outTradeNo){
        JSONObject sParaTemp = new JSONObject();
        sParaTemp.put("merchant_id", merchant_id);
        sParaTemp.put("out_trade_no", outTradeNo);

        biz_content = sParaTemp.toString();
        service  = "trade.acc.dsfpay.query";
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

            //实时代付
//            dsfPay();
            
            //联机代付
            dsfNewPay();
            
            //代付查询
//            dsfQuery("dsf1554781247666");

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
