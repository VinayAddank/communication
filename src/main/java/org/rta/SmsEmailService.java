package org.rta;

public interface SmsEmailService {
	public String sendSms(CommunicationModel model,MessageConfig config) throws IllegalArgumentException ;

    public String sendEmail(CommunicationModel model, MessageConfig config) throws IllegalArgumentException ;

}
