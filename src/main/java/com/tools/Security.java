package com.tools;

import org.springframework.stereotype.Component;

@Component
public class Security {

    public String xssSecurity(String message) {
        char[] chars = message.toCharArray();
        boolean closeAccept = false;
        for (int i = 0, j; i < chars.length; i++) {
            char c = chars[i];
            if (chars[i] == '<') {
                System.out.println(chars.length);
                for (j = i; j < chars.length; j++) {
                    char h = chars[j];
                    if (chars[j] == ' ' || chars[j] == '>') {
                        String tag = message.substring(i + 1, j);
                        System.out.println(tag);
                        if (tag.matches("/?b") || tag.matches("/?font") || tag.matches("/?i") || tag.matches("/?br") || tag.matches("/?div")) {
                            closeAccept = true;
                            break;
                        } else {
                            chars[i] = '•';
                            break;
                        }
                    }
                }
            }
            if (chars[i] == '>') {
                if (!closeAccept) {
                    chars[i] = '·';
                } else {
                    closeAccept = false;
                }
            }
        }
        return new String(chars).replaceAll("·", "&gt;").replaceAll("•", "&lt;");
    }
}
