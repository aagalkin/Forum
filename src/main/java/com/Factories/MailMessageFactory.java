package com.Factories;

import com.Entity.MailMessage;
import org.springframework.stereotype.Component;

@Component
public class MailMessageFactory {
    public MailMessage createNewMessage() {
        MailMessage mailMessage = new MailMessage();
        return mailMessage;
    }
}
