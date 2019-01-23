package aliyun;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

public class AliyunTagManager {

    private final static String PROTOCAL;
    private final static String HOST;
    private final static String ACCESSKEYID;
    private final static String ACCESSKEYSECRET;
    private final static String FORMAT;
    private final static String SIGNATUREMETHOD;
    private final static String SIGNATUREVERSION;
    private final static String VERSION;
    private final static String REGIONID;


    static {
        //加载配置文件
        InputStream f = Config.class.getClassLoader().getResourceAsStream("config.properties");
        Properties pros = new Properties();
        try {
            pros.load(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PROTOCAL = pros.getProperty("protocol");
        HOST = pros.getProperty("host");
        ACCESSKEYID = pros.getProperty("accesskey_id");
        ACCESSKEYSECRET = pros.getProperty("accesskey_secret");
        FORMAT = pros.getProperty("format");
        SIGNATUREMETHOD = pros.getProperty("signature_method");
        SIGNATUREVERSION = pros.getProperty("signature_version");
        VERSION = pros.getProperty("version");
        REGIONID = pros.getProperty("region_id");
    }

    /**
     * 创建tag
     * @param tagName 创建tag的name 必须填写
     * @param method    http访问方法
     * @return
     */
    public String createTag(String tagName,HttpMethod method) {
        String action = "CreateTag";
        String result = null;
        String utcTime = InnerHttpRequestUtil.getUTCTimeStr();
        String signatureNonce = UUID.randomUUID().toString();
        String[] keys = new String[]{"AccessKeyId", "Action", "Format", "RegionId", "SignatureMethod",
                "SignatureNonce", "SignatureVersion", "TagName", "Timestamp", "Version"};
        Object[] values = new Object[]{ACCESSKEYID, action, FORMAT, REGIONID, SIGNATUREMETHOD, signatureNonce
                ,SIGNATUREVERSION, tagName, utcTime, VERSION};
        String signature = null;
        try {
            signature = InnerHttpRequestUtil.getSignature(InnerHttpRequestUtil.prepareParamStrURLEncoder(keys, values), method,ACCESSKEYSECRET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] keys1 = new String[]{"AccessKeyId", "Action", "Format", "RegionId", "Signature", "SignatureMethod",
                "SignatureNonce", "SignatureVersion", "TagName", "Timestamp", "Version"};
        Object[] values1 = new Object[]{ACCESSKEYID, action, FORMAT, REGIONID, signature, SIGNATUREMETHOD, signatureNonce
                , SIGNATUREVERSION, tagName, utcTime, VERSION};
        String param = InnerHttpRequestUtil.prepareParamStrURLEncoder(keys1, values1);
        if (method == HttpMethod.GET) {
            result = InnerHttpRequestUtil.doHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        } else {
            result = InnerHttpRequestUtil.doPostHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        }
        return result;
    }

    /**
     * 删除tag信息
     * @param tagId tag的id （查询tag可得） 必须填写
     * @param method Http的访问方式
     * @return
     */
    public String deleteTag(String tagId,HttpMethod method){
        String action = "DeleteTag";
        String result = null;
        String utcTime = InnerHttpRequestUtil.getUTCTimeStr();
        String signatureNonce = UUID.randomUUID().toString();
        String[] keys = new String[]{"AccessKeyId", "Action", "Format", "RegionId", "SignatureMethod",
                "SignatureNonce", "SignatureVersion", "TagId", "Timestamp", "Version"};
        Object[] values = new Object[]{ACCESSKEYID, action, FORMAT, REGIONID, SIGNATUREMETHOD, signatureNonce
                ,SIGNATUREVERSION, tagId, utcTime, VERSION};
        String signature = null;
        try {
            signature = InnerHttpRequestUtil.getSignature(InnerHttpRequestUtil.prepareParamStrURLEncoder(keys, values), method,ACCESSKEYSECRET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] keys1 = new String[]{"AccessKeyId", "Action", "Format", "RegionId", "Signature", "SignatureMethod",
                "SignatureNonce", "SignatureVersion", "TagId", "Timestamp", "Version"};
        Object[] values1 = new Object[]{ACCESSKEYID, action, FORMAT, REGIONID, signature, SIGNATUREMETHOD, signatureNonce
                , SIGNATUREVERSION, tagId, utcTime, VERSION};
        String param = InnerHttpRequestUtil.prepareParamStrURLEncoder(keys1, values1);
        if (method == HttpMethod.GET) {
            result = InnerHttpRequestUtil.doHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        } else {
            result = InnerHttpRequestUtil.doPostHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        }
        return result;
    }

    /**
     * 查询tag
     * @param keyWord 匹配规则 为空则全部查询 否则查询匹配的tag
     * @param method  Http访问方式
     * @return
     */
    public String queryTag(String keyWord , HttpMethod method){
        String action = "QueryTagByParam";
        String result = null;
        String utcTime = InnerHttpRequestUtil.getUTCTimeStr();
        String signatureNonce = UUID.randomUUID().toString();
        String[] keys = null;
        Object[] values = null;
        if (StringUtils.isBlank(keyWord)){
            keys = new String[]{"AccessKeyId", "Action", "Format", "RegionId", "SignatureMethod",
                    "SignatureNonce", "SignatureVersion", "Timestamp", "Version"};
            values = new Object[]{ACCESSKEYID,action, FORMAT, REGIONID, SIGNATUREMETHOD, signatureNonce
                    ,SIGNATUREVERSION, utcTime, VERSION};
        }else {
            keys = new String[]{"AccessKeyId", "Action","KeyWord", "Format", "RegionId", "SignatureMethod",
                    "SignatureNonce", "SignatureVersion", "Timestamp", "Version"};
            values = new Object[]{ACCESSKEYID, action, keyWord, FORMAT, REGIONID, SIGNATUREMETHOD, signatureNonce
                    ,SIGNATUREVERSION, utcTime, VERSION};
        }
        String signature = null;
        try {
            signature = InnerHttpRequestUtil.getSignature(InnerHttpRequestUtil.prepareParamStrURLEncoder(keys, values), method,ACCESSKEYSECRET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] keys1 = null;
        Object[] values1 = null;
        if (StringUtils.isBlank(keyWord)){
            keys1 = new String[]{"AccessKeyId", "Action","Format", "RegionId", "Signature", "SignatureMethod",
                    "SignatureNonce", "SignatureVersion", "Timestamp", "Version"};
            values1 = new Object[]{ACCESSKEYID, action ,FORMAT, REGIONID, signature, SIGNATUREMETHOD, signatureNonce
                    , SIGNATUREVERSION, utcTime, VERSION};
        }
        String param = InnerHttpRequestUtil.prepareParamStrURLEncoder(keys1, values1);
        if (method == HttpMethod.GET) {
            result = InnerHttpRequestUtil.doHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        } else {
            result = InnerHttpRequestUtil.doPostHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        }
        return result;
    }

    public String queryStatisticsByTagnameAndStarttimeAndEndtime(String tagName, String startTime, String endTime, HttpMethod method){
        String action = "SenderStatisticsByTagNameAndBatchID";
        String result = null;
        String utcTime = InnerHttpRequestUtil.getUTCTimeStr();
        String signatureNonce = UUID.randomUUID().toString();
        String[] keys = new String[]{"AccessKeyId", "Action", "EndTime", "Format", "RegionId", "SignatureMethod",
                "SignatureNonce", "SignatureVersion", "StartTime", "TagName", "Timestamp", "Version"};
        Object[] values = new Object[]{ACCESSKEYID, action, endTime, FORMAT, REGIONID, SIGNATUREMETHOD, signatureNonce
                ,SIGNATUREVERSION, startTime, tagName, utcTime, VERSION};
        String signature = null;
        try {
            signature = InnerHttpRequestUtil.getSignature(InnerHttpRequestUtil.prepareParamStrURLEncoder(keys, values), method,ACCESSKEYSECRET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] keys1 = new String[]{"AccessKeyId", "Action", "EndTime", "Format", "RegionId", "Signature", "SignatureMethod",
                "SignatureNonce", "SignatureVersion", "StartTime", "TagName", "Timestamp", "Version"};
        Object[] values1 = new Object[]{ACCESSKEYID, action, endTime, FORMAT, REGIONID, signature, SIGNATUREMETHOD, signatureNonce
                , SIGNATUREVERSION, startTime, tagName, utcTime, VERSION};
        String param = InnerHttpRequestUtil.prepareParamStrURLEncoder(keys1, values1);
        if (method == HttpMethod.GET) {
            result = InnerHttpRequestUtil.doHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        } else {
            result = InnerHttpRequestUtil.doPostHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        }
        return result;
    }

    public String queryTrackListByTagnameAndStarttimeAndEndtime(String tagName,String accountName, String startTime, String endTime, HttpMethod method){
        String action = "GetTrackListByMailFromAndTagName";
        String result = null;
        String utcTime = InnerHttpRequestUtil.getUTCTimeStr();
        String signatureNonce = UUID.randomUUID().toString();
        String[] keys = new String[]{"AccessKeyId", "AccountName", "Action", "EndTime", "Format", "RegionId", "SignatureMethod",
                "SignatureNonce", "SignatureVersion", "StartTime", "TagName", "Timestamp", "Version"};
        Object[] values = new Object[]{ACCESSKEYID, accountName, action, endTime, FORMAT, REGIONID, SIGNATUREMETHOD, signatureNonce
                ,SIGNATUREVERSION, startTime, tagName, utcTime, VERSION};
        String signature = null;
        try {
            signature = InnerHttpRequestUtil.getSignature(InnerHttpRequestUtil.prepareParamStrURLEncoder(keys, values), method,ACCESSKEYSECRET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] keys1 = new String[]{"AccessKeyId", "AccountName", "Action", "EndTime", "Format", "RegionId", "Signature", "SignatureMethod",
                "SignatureNonce", "SignatureVersion", "StartTime", "TagName", "Timestamp", "Version"};
        Object[] values1 = new Object[]{ACCESSKEYID, accountName, action, endTime, FORMAT, REGIONID, signature, SIGNATUREMETHOD, signatureNonce
                , SIGNATUREVERSION, startTime, tagName, utcTime, VERSION};
        String param = InnerHttpRequestUtil.prepareParamStrURLEncoder(keys1, values1);
        if (method == HttpMethod.GET) {
            result = InnerHttpRequestUtil.doHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        } else {
            result = InnerHttpRequestUtil.doPostHttpRequest(PROTOCAL + "://" + HOST + "/", param);
        }
        return result;
    }

    public static void main(String[] args) {
        AliyunTagManager aliyunTagManager = new AliyunTagManager();
        String startTime = "2019-01-22";
        String endTime = "2019-01-22";
//        System.out.println(aliyunTagManager.queryTrackListByTagnameAndStarttimeAndEndtime("tag","csdn@mail.edm.csdn.net",startTime,endTime,HttpMethod.GET));
        System.out.println(aliyunTagManager.queryStatisticsByTagnameAndStarttimeAndEndtime("tag",startTime,endTime,HttpMethod.GET));
//        System.out.println(aliyunTagManager.createTag("test1",HttpMethod.GET));d
    }

}
