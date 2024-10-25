package org.ukma.notificationserver.mails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.ukma.notificationserver.jms.models.UserEvent;
import org.ukma.notificationserver.metrics.EmailProcessingTimeMetric;
import org.ukma.notificationserver.metrics.FailedEmailsCountMetric;
import org.ukma.notificationserver.models.OrderNotification;
import org.ukma.notificationserver.models.OrderNotificationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final EmailProcessingTimeMetric emailProcessingTimeMetric;
    private final FailedEmailsCountMetric failedEmailsCountMetric;
    private final AtomicInteger countOfFailed = new AtomicInteger(0);

    public void processOrderNotification(OrderNotification order) {
        if (order.getType() == OrderNotificationType.CREATION)
            sendOrderCreationMail(order);
        else
            sendOrderNotificationForUser(order);
    }

    public void sendOrderCreationMail(OrderNotification order) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(order.getEmail());
        mailMessage.setSubject("Your order");
        mailMessage.setText("Thank you for your order with number " + order.getOrderId()
                + ". Our administrator will verify it. You can check status of your order, in our website "
                + ", orders tab.\n We hope you're having a great day!\n\n"
                + "Best regards,\n"
                + "The Spring Boot Cleaning Team");
        try {
            javaMailSender.send(mailMessage);
            emailProcessingTimeMetric.recordExecutionTime(order.getCreationTime(), LocalDateTime.now());
        } catch (MailException e) {
            countOfFailed.incrementAndGet();
            log.error("Can`t send email for user: {}, for order with id: {}, with error:", order.getEmail(), order.getOrderId(), e);
        }
    }

    public void sendOrderNotificationForUser(OrderNotification order) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(order.getEmail());
        mailMessage.setSubject("Notification");
        mailMessage.setText("Good morning, our team preparing to start your order " + order.getOrderId()
                + ". Wait for our team at: " + order.getOrderTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME)
                + ".\n We hope you're having a great day!\n\n"
                + "Best regards,\n"
                + "The Spring Boot Cleaning Team");
        try {
            javaMailSender.send(mailMessage);
            emailProcessingTimeMetric.recordExecutionTime(order.getCreationTime(), LocalDateTime.now());
        } catch (MailException e) {
            countOfFailed.incrementAndGet();
            log.error("Can`t send email for user: {}, for order with id: {}, with error:", order.getEmail(), order.getOrderId(), e);
        }
    }

    public void sendUserCreateEmail(UserEvent user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.email());
        mailMessage.setSubject("Notification");
        mailMessage.setText(String.format("""
                Dear %s,
                
                Thank you for registering with Spring Boot Cleaning! We’re excited to have you on board and look forward to helping you get the most out of our services.
                
                Thank you for being with us!
                
                Best regards,
                """, user.name()));
        try {
            javaMailSender.send(mailMessage);
            emailProcessingTimeMetric.recordExecutionTime(user.creationTime(), LocalDateTime.now());
        } catch (MailException e) {
            countOfFailed.incrementAndGet();
            log.error("Can`t send email for user: {}, with error: ", user.email(), e);
        }
    }

    public void sendUserDeletionEmail(UserEvent user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.email());
        mailMessage.setSubject("Notification");
        mailMessage.setText(String.format("""
                Dear %s,
                
                We’re sorry to see you go. Your account has been successfully deleted. If you have any feedback or questions, please feel free to reach out.
                
                Thank you for being with us!
                
                Best regards,
                """, user.name()));
        try {
            javaMailSender.send(mailMessage);
            emailProcessingTimeMetric.recordExecutionTime(user.creationTime(), LocalDateTime.now());
        } catch (MailException e) {
            countOfFailed.incrementAndGet();
            log.error("Can`t send email for user: {}, with error: ", user.email(), e);
        }
    }
}
