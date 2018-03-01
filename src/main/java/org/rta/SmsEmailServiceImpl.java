package org.rta;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SmsEmailServiceImpl implements SmsEmailService {

    private static final Log log = LogFactory.getLog(SmsEmailServiceImpl.class);
    public SmsEmailServiceImpl() {}

    static HttpURLConnection connection = null;

    public String sendSms(CommunicationModel model, MessageConfig config)throws IllegalArgumentException {
    	 String responseMsg="";
          URL url;
			try {
				url = new URL(config.getMessUrl());
			
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setFollowRedirects(true);
            connection = sendBulkSMS(config.getUserNm(), config.getPasswrd(), config.getSenderId(),
                    model.getMobileNos(), model.getMessage(),config.getContentType());
            responseMsg=connection.getResponseMessage();
            System.out.println("Resp Code:" + connection.getResponseCode());
            System.out.println("Resp Message:" + responseMsg);

            connection.disconnect();

        } catch (Exception e) {
        	log.error("SMS could not be send on mobile no/nos "+ model.getMobileNos());    
        	e.getStackTrace();
            throw new IllegalArgumentException();
        }

        return responseMsg+"";
    }

    // method for sending bulk SMS
    public HttpURLConnection sendBulkSMS(String username, String password, String senderId, String mobileNos,
            String message,String contentType) {
        try {
            String smsservicetype = "bulkmsg"; // For bulk msg
            String query = "username=" + URLEncoder.encode(username) + "&password=" + URLEncoder.encode(password)
                    + "&smsservicetype=" + URLEncoder.encode(smsservicetype) + "&content=" + URLEncoder.encode(message)
                    + "&bulkmobno=" + URLEncoder.encode(mobileNos, "UTF-8") + "&senderid="
                    + URLEncoder.encode(senderId);

            connection.setRequestProperty("Content-length", String.valueOf(query.length()));
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

            // open up the output stream of the connection
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());

            // write out the data
            int queryLength = query.length();
            output.writeBytes(query);

            // get ready to read the response from the cgi script
            DataInputStream input = new DataInputStream(connection.getInputStream());

            // read in each character until end-of-stream is detected
            for (int c = input.read(); c != -1; c = input.read())
                System.out.print((char) c);
            input.close();
        } catch (Exception e) {
      
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        return connection;
    }

    public String sendEmail(CommunicationModel model, MessageConfig config) throws IllegalArgumentException{

        String msgstatus = "Initial";
        MimeMessage msg;
        Transport tr;
        try {
            Properties props = getProperties(config);
            Session session = Session.getInstance(props, null);
            msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(config.getMailUserName()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(model.getTo()));
            if (model.getCc() != null && !model.getCc().equals("")) {
                msg.setRecipients(Message.RecipientType.CC, model.getCc());
            } else {
                msg.setRecipients(Message.RecipientType.CC, "");
            }
            if (model.getBcc() != null && !model.getBcc().equals("")) {
                msg.setRecipients(Message.RecipientType.BCC, model.getBcc());
            } else {
                msg.setRecipients(Message.RecipientType.BCC, "");
            }
            msg.setSubject(model.getSubject());
            
            String[] splittedFileWithPathAndFileName={"",""};
            File remoteAttFile=null;
		      Multipart multipart = new MimeMultipart();
		      MimeBodyPart messageBodyPart = new MimeBodyPart();
		      messageBodyPart.setContent(model.getMailContent(), config.getMailContentType());
		      multipart.addBodyPart(messageBodyPart);
		      
		      if(model!=null && model.getAttachments()!=null && model.getAttachments().size()>0)
				for (String str : model.getAttachments()) {
					
					splittedFileWithPathAndFileName = str.split(",");
					
					if (splittedFileWithPathAndFileName.length < 2)
						throw new IllegalArgumentException("The file path of attachement not received");
					
					messageBodyPart = new MimeBodyPart();
					remoteAttFile = new File(splittedFileWithPathAndFileName[0]);
					
					if (!remoteAttFile.exists())
						throw new IllegalArgumentException("The file path incorrect");

					DataSource fds = new FileDataSource(remoteAttFile);
					messageBodyPart.setDataHandler(new DataHandler(fds));
					messageBodyPart.setFileName(splittedFileWithPathAndFileName[1]);
					multipart.addBodyPart(messageBodyPart);
				}
	      
		      msg.setContent(multipart);
//            msg.setContent(model.getMailContent(), config.getMailContentType());
            tr = session.getTransport(config.getProtocol());
            tr.connect(config.getHost(), config.getMailUserName(), config.getMailPassword());
            msg.saveChanges();
            tr.sendMessage(msg, msg.getAllRecipients());
            tr.close();

        } catch (Exception e) {
        	log.error("Email could not be sent to  "+ model.getTo());
            e.printStackTrace();
            msgstatus += e.toString();
            throw new IllegalArgumentException();
        }
        msgstatus = new String("OK");
        return msgstatus;

    }

    private static Properties getProperties(MessageConfig config) {
        Properties props = new Properties();
        String smtpHost = config.getHost();
        String smtpPort = config.getPort().toString();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put(config.getSmtp(), "true");
        // props.put("mail.debug", "true");
        props.put(config.getSmtpStatus(), "true");
        return props;
    }


}
