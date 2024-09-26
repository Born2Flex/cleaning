package org.ukma.notificationserver.mails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.ukma.notificationserver.models.OrderNotification;
import org.ukma.notificationserver.models.OrderNotificationType;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

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
        } catch (MailException e) {
            log.error("Can`t send email for user: " + order.getEmail()
                    + ", for order with id: " + order.getOrderId());
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
        } catch (MailException e) {
            log.error("Can`t send email for user: " + order.getEmail()
                    + ", for order with id: " + order.getOrderId());
        }
    }
}
