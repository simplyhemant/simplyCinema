package com.simply.Cinema.validation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationOtpEmail(String userEmail,
                                         String otp)
                                          throws MessagingException {

          String subject = "Your OTP for Login";
          String text = "Dear User,\n\nYour OTP is: " + otp + "\n\nThe OTP is valid for 2 minutes.\n\nThank you!";

        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                    mimeMessage,"utf-8");

            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text);
            mimeMessageHelper.setTo(userEmail);
            javaMailSender.send(mimeMessage);
        }
        catch (MailException e){
            throw new MailSendException("failed to send email");
        }

    }

    public void sendTicketEmail(String userEmail) {

        String subject = "Your Booking Ticket";

        String text = "Dear User,\n\n"
                + "Here are your ticket details: 📩\n"
                + "-- read full --" + "\n\n"
                + "Thank you for booking with us!\n"
                + "Ticket PDF will be sent soon.\n"
                + "--- AFTER I GET THE JOB ---\n";

        log.info("📩 Sending ticket email to: {}", userEmail);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(text, false);
            javaMailSender.send(mimeMessage);

            log.info("✅ Ticket email sent successfully to: {}", userEmail);

        } catch (MailException e) {
            log.error("❌ Failed to send ticket email to: {}", userEmail, e);
            throw new MailSendException("Failed to send email", e);

        } catch (MessagingException e) {
            log.error("❌ MessagingException while sending ticket email to: {}", userEmail, e);
            throw new RuntimeException("Messaging exception while sending email", e);
        }
    }

}
