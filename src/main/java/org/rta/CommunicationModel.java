package org.rta;

import java.util.List;

public class CommunicationModel {
    private String message;
    private String mobileNos; // , Separated mobile Nos
    private String subject;
    private String to;
    private String cc;
    private String bcc;
    private String mailContent;
    private List<String> attachments;			// file path and fileName separated by ,


    public CommunicationModel() {
        super();
    }

    public CommunicationModel(String message, String mobileNos) {
        super();
        this.message = message;
        this.mobileNos = mobileNos;
    }

    public CommunicationModel(String subject, String to, String cc, String bcc, String mailContent) {
        super();
        this.subject = subject;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.mailContent = mailContent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMobileNos() {
        return mobileNos;
    }

    public void setMobileNos(String mobileNos) {
        this.mobileNos = mobileNos;
    }

    public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}


}
