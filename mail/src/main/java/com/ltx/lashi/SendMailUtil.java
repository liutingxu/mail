package com.ltx.lashi;

import com.csvreader.CsvReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;


/**
 * 发送邮件服务
 */
public class SendMailUtil {

    // 私有配置类
    private Properties props = new Properties();

    // 私有权限类
    private Authenticator authenticator;

    // 163邮箱smtp服务器地址
    private static final String MAIL_HOST = "smtp.163.com";

    // 权限验证标识位
    private static final String MAIL_SMTP_AUTH = "true";

    // 邮箱使用协议
    private static final String MAIL_PROTOCOL = "smtp";

    // 编码格式
    private static final String DEFAULT_ENCODING = "UTF-8";

    // smtp端口
    private static final String SMTP_PORT = "465";

    public static void main(String[] args) throws Exception, MessagingException {
        try {
            SendMailUtil util = new SendMailUtil();
            util.init();
            while (true) {
                Calendar calendar = Calendar.getInstance();
                int now_h = calendar.get(Calendar.HOUR_OF_DAY);
                if (now_h >= 9 && now_h < 10) {
                    CsvReader reader = null;
                    reader = new CsvReader(System.getProperty("MAIL_FILE"), ' ', Charset.forName("UTF-8"));
                    // 读取标题
                    reader.readHeaders();
                    // 逐条读取记录，直至读完
                    while (reader.readRecord()) {
                        util.sendMail(reader.get(1), reader.get(0));
                    }
                    Thread.sleep(60 * 60 * 1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 邮件初始化
     */
    public void init() throws Exception {
        try {
            String path = System.getProperty("CONFIG_FILE");
            File file = new File(path);
            InputStreamReader in = new InputStreamReader(new FileInputStream(file), DEFAULT_ENCODING);
            this.props.load(in);
            //props.setProperty("mail.transport.protocol", MAIL_PROTOCOL);
            this.props.put("mail.smtp.ssl.enable", true);
            this.props.setProperty("mail.smtp.port", SMTP_PORT);
            this.props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            this.props.setProperty("mail.smtp.socketFactory.fallback", "false");
            this.props.setProperty("mail.smtp.socketFactory.port", SMTP_PORT);
            this.props.setProperty("mail.smtp.host", SendMailUtil.MAIL_HOST);
            this.props.setProperty("mail.smtp.auth", SendMailUtil.MAIL_SMTP_AUTH);
            this.authenticator = new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    //填写自己的163邮箱的登录帐号和授权密码，授权密码的获取，在后面会进行讲解。
                    return new PasswordAuthentication(props.getProperty("username"), props.getProperty("password"));
                }
            };
        } catch (Exception e) {
            System.out.println("初始化失败！");
            throw e;
        }
    }


    public void sendMail(String sendToMail, String name) throws MessagingException {

        Session session = Session.getDefaultInstance(props, authenticator);

        session.setDebug(true);

        // 创建消息
        Message message = new MimeMessage(session);
        // 发件人
        message.setFrom(new InternetAddress(this.props.getProperty("username")));
        // 收件人
        message.setRecipient(RecipientType.TO, new InternetAddress(sendToMail));
        // 主题（标题）
        message.setSubject("拉屎");
        // 正文
        String str = name + " " + "您是来拉屎的吗？";
        // 设置编码，防止发送的内容中文乱码。
        message.setContent(str, "text/html;charset=UTF-8");

        // 发送消息
        Transport.send(message);


    }

}
