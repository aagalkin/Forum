package com.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Test {
    public static void main(String[] args) throws ParseException {
        List<Msg> msgs = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        msgs.add(new Msg("bla bla bla", format.parse("15.07.2019 15:31")));
        msgs.add(new Msg("lo lo lo", format.parse("14.06.2019 14:33")));
        msgs.add(new Msg("bl bl bl", format.parse("16.08.2019 15:15")));

        msgs.forEach(System.out::println);

        Collections.sort(msgs);

        msgs.forEach(System.out::println);
    }

    static class Msg implements Comparable<Msg>{
        private String text;
        private Date date;

        public Msg(String text, Date date) {
            this.text = text;
            this.date = date;
        }

        @Override
        public String toString() {
            return "Msg{" +
                    "text='" + text + '\'' +
                    ", date=" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date) +
                    '}';
        }

        @Override
        public int compareTo(Msg o) {
            return o.date.compareTo(this.date);
        }
    }
}
