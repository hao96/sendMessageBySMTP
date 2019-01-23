package aliyun;

import org.springframework.http.HttpMethod;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Properties;

public class InnerHttpRequestUtil {

    //    private static final Logger logger = LoggerFactory.getLogger(InnerHttpRequestUtil.class);
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String doHttpRequest(String url, String param) {
        trustAllHosts();
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (realUrl.getProtocol().toLowerCase().equals("https")) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                https.setRequestMethod(HttpMethod.GET.toString());
                https.connect();
                in = new BufferedReader(new InputStreamReader(https.getInputStream()));
            } else {
                connection.connect();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    public static String doPostHttpRequest(String url, String param) {
        trustAllHosts();
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(param.length()));
            if (realUrl.getProtocol().toLowerCase().equals("https")) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                https.setHostnameVerifier(DO_NOT_VERIFY);
                https.setRequestMethod(HttpMethod.POST.toString());
                https.connect();
                in = new BufferedReader(new InputStreamReader(https.getInputStream()));
            } else {
                HttpURLConnection http = (HttpURLConnection) connection;
                http.setRequestMethod(HttpMethod.POST.toString());
                http.connect();
                in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    public static String prepareParamStrURLEncoder(String[] keys, Object[] values) {
        StringBuffer params = new StringBuffer();
        String param = "";
        try {
            for (int i = 0; i < keys.length; i++) {
                if (values[i].equals("") || values[i] == null) {
                    continue;
                }
                params.append(getUtf8Encoder(keys[i]) + "=" + getUtf8Encoder(values[i].toString()) + "&");
            }
            param = params.substring(0, params.lastIndexOf("&"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    /**
     * 获取签名
     *
     * @param param
     * @param method
     * @return
     * @throws Exception
     */
    public static String getSignature(String param, HttpMethod method,String accessKeySecret) throws Exception {
        String toSign = method + "&" + URLEncoder.encode("/", "utf8") + "&"
                + getUtf8Encoder(param);
        byte[] bytes = HmacSHA1Encrypt(toSign, accessKeySecret + "&");
        return new String(Base64.getEncoder().encode(bytes));
    }
    private static String getUtf8Encoder(String param) throws UnsupportedEncodingException {
        return URLEncoder.encode(param, "utf8")
                .replaceAll("\\+", "%20")
                .replaceAll("\\*", "%2A")
                .replaceAll("%7E", "~");
    }

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";
    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return
     * @throws Exception
     */
    public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        //用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        //完成 Mac 操作
        return mac.doFinal(text);
    }
    private static DateFormat daetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 得到UTC时间，类型为字符串，格式为"yyyy-MM-dd HH:mm:ss",成功返回"yyyy-MM-ddTHH:mm:ssZ"<br />
     * 如果获取失败，返回null
     *
     * @return
     */
    public static String getUTCTimeStr() {
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        String date = daetFormat.format(cal.getTime());
        String[] strs = date.split(" ");
        return strs[0] + "T" + strs[1] + "Z";
    }

}
