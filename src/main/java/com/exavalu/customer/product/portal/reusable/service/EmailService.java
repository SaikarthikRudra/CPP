package com.exavalu.customer.product.portal.reusable.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private static final Logger log = LogManager.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender javaMailSender;

	public MimeMessage createMimeMessage() {
		return javaMailSender.createMimeMessage();
	}

	public void sendMimeMessage(MimeMessage mimeMessage) {
		javaMailSender.send(mimeMessage);
	}

	@Async
	public void sendEmail(String to, String subject, String body) {

		try {
			MimeMessage mimeMessage = createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true); // Set to true for HTML content
			sendMimeMessage(mimeMessage);
		} catch (MessagingException e) {
			log.error("Failed to send email"+ e.toString());
		}
	}

	@Async
	public void sendEmailPdf(String to, String subject, String body) {
		MimeMessage message = createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);
			sendMimeMessage(message);
		} catch (MessagingException e) {
			log.error("Failed to send email: " + e.toString());
			throw new RuntimeException("Failed to send email", e);
		}
	}

	@Async
	public void sendOtpEmail(String to, String otp) {
		String subject = "Your OTP Code";
		String body = generateOtpEmailBody(otp);
//		String body = "Your OTP code is: " + otp;
		sendEmail(to, subject, body);
	}

	@Async
	public void sendEnquiryMail(String to, String productTitle) {
		String subject = "Enquiry Mail Response";
		String body = generateEnquiryEmailBody(productTitle);
		sendEmail(to, subject, body);
	}

	private String generateOtpEmailBody(String otp) {
		return "<!DOCTYPE html>" + "<html>" + "<head>" + "    <style>" + "        .container {"
				+ "            font-family: Arial, sans-serif;" + "            margin: 0 auto;"
				+ "            padding: 20px;" + "            max-width: 600px;"
				+ "            background-color: #f9f9f9;" + "            border: 1px solid #ddd;"
				+ "            border-radius: 5px;" + "        }" + "        .header {"
				+ "            background-color: #4CAF50;" + "            color: white;"
				+ "            padding: 10px 20px;" + "            text-align: center;"
				+ "            border-top-left-radius: 5px;" + "            border-top-right-radius: 5px;" + "        }"
				+ "        .body {" + "            padding: 20px;" + "        }" + "        .otp {"
				+ "            font-size: 24px;" + "            font-weight: bold;" + "            color: #4CAF50;"
				+ "        }" + "        .footer {" + "            text-align: center;" + "            padding: 10px;"
				+ "            font-size: 12px;" + "            color: #888;" + "        }" + "    </style>" + "</head>"
				+ "<body>" + "    <div class=\"container\">" + "        <div class=\"header\">"
				+ "            <h1>Customer Product Portal</h1>" + "        </div>" + "        <div class=\"body\">"
				+ "            <p>Dear User,</p>" + "            <p>Your OTP code is:</p>"
				+ "            <p class=\"otp\">" + otp + "</p>"
				+ "            <p>Please use this code to complete your authentication process.</p>"
				+ "            <p>If you did not request this code, please ignore this email.</p>" + "        </div>"
				+ "        <div class=\"footer\">" + "            <p>Thank you for using our service!</p>"
				+ "        </div>" + "    </div>" + "</body>" + "</html>";
	}

	private String generateEnquiryEmailBody(String productTitle) {
		return "<!DOCTYPE html>" + "<html>" + "<head>" + "    <style>" + "        .container {"
				+ "            font-family: Arial, sans-serif;" + "            margin: 0 auto;"
				+ "            padding: 20px;" + "            max-width: 600px;"
				+ "            background-color: #f9f9f9;" + "            border: 1px solid #ddd;"
				+ "            border-radius: 5px;" + "        }" + "        .header {"
				+ "            background-color: #4CAF50;" + "            color: white;"
				+ "            padding: 10px 20px;" + "            text-align: center;"
				+ "            border-top-left-radius: 5px;" + "            border-top-right-radius: 5px;" + "        }"
				+ "        .body {" + "            padding: 20px;" + "        }" + "        .otp {"
				+ "            font-size: 24px;" + "            font-weight: bold;" + "            color: #4CAF50;"
				+ "        }" + "        .footer {" + "            text-align: center;" + "            padding: 10px;"
				+ "            font-size: 12px;" + "            color: #888;" + "        }" + "    </style>" + "</head>"
				+ "<body>" + "    <div class=\"container\">" + "        <div class=\"header\">"
				+ "            <h1>Customer Product Portal</h1>" + "        </div>" + "        <div class=\"body\">"
				+ "            <p>Dear User,</p>" + "            <p>Your OTP code is:</p>"
				+ "            <p class=\"otp\">" + productTitle + "</p>"
				+ "            <p>The Product is now available.</p>"
				+ "            <p>If you did not enquire this product on CPP, please ignore this email.</p>"
				+ "        </div>" + "        <div class=\"footer\">"
				+ "            <p>Thank you for using our service!</p>" + "        </div>" + "    </div>" + "</body>"
				+ "</html>";
	}

	public String generateInvoiceEmailBody() {
		return "<!DOCTYPE html>" + "<html>" + "<head>" + "    <style>" + "        .container {"
				+ "            font-family: Arial, sans-serif;" + "            margin: 0 auto;"
				+ "            padding: 20px;" + "            max-width: 600px;"
				+ "            background-color: #f9f9f9;" + "            border: 1px solid #ddd;"
				+ "            border-radius: 5px;" + "        }" + "        .header {"
				+ "            background-color: #4CAF50;" + "            color: white;"
				+ "            padding: 10px 20px;" + "            text-align: center;"
				+ "            border-top-left-radius: 5px;" + "            border-top-right-radius: 5px;" + "        }"
				+ "        .body {" + "            padding: 20px;" + "        }" + "        .footer {"
				+ "            text-align: center;" + "            padding: 10px;" + "            font-size: 12px;"
				+ "            color: #888;" + "        }" + "    </style>" + "</head>" + "<body>"
				+ "    <div class=\"container\">" + "        <div class=\"header\">"
				+ "            <h1>Customer Product Portal</h1>" + "        </div>" + "        <div class=\"body\">"
				+ "            <p>Dear Customer,</p>"
				+ "            <p>Please find the attached invoice for your recent purchase from Customer Product Portal.</p>"
				+ "            <p>Thank you for your Purchase!</p>" + "        </div>"
				+ "        <div class=\"footer\">" + "            <p>Thank you for using our service!</p>"
				+ "        </div>" + "    </div>" + "</body>" + "</html>";
	}

}
