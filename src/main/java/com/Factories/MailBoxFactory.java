package com.Factories;

import com.Entity.MailBox;
import org.springframework.stereotype.Component;

@Component
public class MailBoxFactory {
    public MailBox createMailBox() {
        return new MailBox();
    }
}
