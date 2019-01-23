package aliyun;

import com.alibaba.fastjson.JSON;
import com.sun.deploy.util.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;

/**
 * 阿里云邮件发送服务类
 *
 */
public class AliyunEmailManager {

    private static final String ALIDM_SMTP_HOST;
    private static final String ALIDM_SMTP_PORT;
    private static final String LOGIN_MAIL_USERNAME;
    private static final String LOGIN_MAIL_PASSWORD;
    private static final String USER_MAIL_ADDR_FROM;

    static{
        //加载配置文件
        InputStream f = Config.class.getClassLoader().getResourceAsStream("config.properties");
        Properties pros = new Properties();
        try {
            pros.load(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ALIDM_SMTP_HOST = pros.getProperty("alidm_smtp_host");
        ALIDM_SMTP_PORT = pros.getProperty("alidm_smtp_port");
        LOGIN_MAIL_USERNAME = pros.getProperty("login_mail_username");
        LOGIN_MAIL_PASSWORD = pros.getProperty("login_mail_password");
        //与控制台配置相同的发信地址
        USER_MAIL_ADDR_FROM = pros.getProperty("login_mail_username");
    }

    /**
     * 有tag标签
     * @param mailBodyAliyun
     * @param tagName
     */
    public void send(MailBodyAliyun mailBodyAliyun, String tagName){
        final Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", ALIDM_SMTP_HOST);
        props.put("mail.smtp.port", ALIDM_SMTP_PORT);
        props.put("mail.user", LOGIN_MAIL_USERNAME);
        props.put("mail.password", LOGIN_MAIL_PASSWORD);
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        Session mailSession = Session.getInstance(props, authenticator);
        MimeMessage message = new MimeMessage(mailSession);
        try {
            if (StringUtils.isNotBlank(tagName)){
                HashMap<String,String> trace  = new HashMap<String,String>(2);
                trace.put("OpenTrace","1");
                trace.put("TagName", tagName);
                String jsonTrace  = JSON.toJSONString(trace);
                String base64Trace =  new String( Base64.encodeBase64(jsonTrace.getBytes()) );
                //设置跟踪链接头
                message.addHeader("X-AliDM-Trace", base64Trace);
            }
            InternetAddress from = new InternetAddress(USER_MAIL_ADDR_FROM,"CSDN");
            message.setFrom(from);
            InternetAddress to = new InternetAddress(mailBodyAliyun.getReceiver());
            message.setRecipient(MimeMessage.RecipientType.TO, to);
            message.setSubject(mailBodyAliyun.getTitle());
            message.setContent(mailBodyAliyun.getContent(), "text/html;charset=UTF-8");
            Transport.send(message);
        }
        catch (MessagingException e) {
            String err = e.getMessage();
            System.out.println(err);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println(e);
        }

    }

    /**
     * 没有tag标签
     * @param mailBodyAliyun
     */
    public void send(MailBodyAliyun mailBodyAliyun){
        send(mailBodyAliyun,null);
    }

    /**
     * 查询发送者发送邮件的统计信息
     * @param tagName
     * @param startTime
     * @param endTime
     */
    public String querySenderStatistics(String tagName, String startTime, String endTime){
        return new AliyunTagManager().queryStatisticsByTagnameAndStarttimeAndEndtime(tagName,startTime,endTime, HttpMethod.GET);
    }

    /**
     * 查询邮件的追踪数据
     * @param tagName
     * @param startTime
     * @param endTime
     */
    public String queryTrackList(String tagName, String accountName, String startTime, String endTime){
        return new AliyunTagManager().queryTrackListByTagnameAndStarttimeAndEndtime(tagName,accountName,startTime,endTime,HttpMethod.GET);
    }

    /**
     * 查询tag标记邮箱的统计结果
     * @param tagName
     * @param accountName
     * @param startTime
     * @param endTime
     * @return
     */
    public DataContent queryStatistics(String tagName, String accountName, String startTime, String endTime){
        String senderStatistics = querySenderStatistics(tagName,startTime,endTime);
        String trackList = queryTrackList(tagName,accountName,startTime,endTime);

        StatisticsJSON statisticsJSON = StatisticsJSON.parseSelfByJson(senderStatistics);
//        private String TotalCount;
//        private String RequestId;

        TrackJSON trackJSON = TrackJSON.parseSelfByJson(trackList);
//        private String offsetCreateTimeDesc;
//        private String pageSize;
//        private String pageNo;
//        private String requestId;
//        private String offsetCreateTime;

        List<StatisticsPercentJSON> statisticsPercentJSONSList = statisticsJSON.getData().get("stat");
//        private String succeededPercent;
//        private String unavailablePercent;
//        private String createTime;
//        private String unavailableCount;
//        private String faildCount;
//        private String requestCount;
//        private String successCount;

        List<TrackRateJSON> trackRateJSONList = trackJSON.getTrackList().get("Stat");
        TrackRateJSON trackRateJSON = trackRateJSONList.get(0);
//        private String rcptUniqueOpenRate;
//        private String rcptOpenCount;
//        private String rcptClickCount;
//        private String createTime;
//        private String rcptUniqueClickRate;
//        private String rcptUniqueClickCount;
//        private String rcptUniqueOpenCount;
//        private String totalNumber;


        int totalRequest = 0;
        int totalSuccess = 0;
        int totalFaild = 0;
        int totalUnavailavle = 0;
        for (StatisticsPercentJSON spj : statisticsPercentJSONSList){
            totalRequest += Integer.valueOf(spj.getRequestCount());
            totalSuccess += Integer.valueOf(spj.getSuccessCount());
            totalFaild += Integer.valueOf(spj.getFaildCount());
            totalUnavailavle += Integer.valueOf(spj.getUnavailableCount());
        }

        float succeededPercent = 0;
        float unavailablePercent = 0;
        if (totalRequest != 0){
            succeededPercent = totalSuccess / totalRequest;
            unavailablePercent = totalUnavailavle / totalRequest;
        }



        return null;
    }

    public void print(){
        System.out.println(ALIDM_SMTP_HOST);
        System.out.println(ALIDM_SMTP_PORT);
        System.out.println(LOGIN_MAIL_USERNAME);
        System.out.println(LOGIN_MAIL_PASSWORD);
        System.out.println(USER_MAIL_ADDR_FROM);
    }

    public static void main(String[] args) {
        MailBodyAliyun mailBody = new MailBodyAliyun();
        mailBody.setContent("test mail");
        mailBody.setTitle("test");
        mailBody.setReceiver("yanghao3306@163.com");
        AliyunEmailManager aliyunEmailManager = new AliyunEmailManager();
        aliyunEmailManager.send(mailBody,"tag");
        aliyunEmailManager.print();

//        AliyunEmailManager aliyunEmailManager = new AliyunEmailManager();
//        DataContent dataContent = aliyunEmailManager.queryStatistics("tag","csdn@mail.edm.csdn.net","2019-01-09","2019-01-11");
//        System.out.println(dataContent.toString());

    }

}

