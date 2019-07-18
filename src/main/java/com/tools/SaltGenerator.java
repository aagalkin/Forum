package com.tools;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class SaltGenerator {
    private List<String> chars = new ArrayList<>();

    public SaltGenerator() {
        chars.add("1");
        chars.add("2");
        chars.add("3");
        chars.add("4");
        chars.add("5");
        chars.add("6");
        chars.add("7");
        chars.add("8");
        chars.add("9");
        chars.add("0");
        chars.add("a");
        chars.add("b");
        chars.add("c");
        chars.add("d");
        chars.add("e");
        chars.add("f");
        chars.add("g");
        chars.add("h");
        chars.add("i");
        chars.add("j");
        chars.add("k");
        chars.add("l");
        chars.add("m");
        chars.add("n");
        chars.add("o");
        chars.add("p");
        chars.add("q");
        chars.add("r");
        chars.add("s");
        chars.add("t");
        chars.add("u");
        chars.add("v");
        chars.add("w");
        chars.add("x");
        chars.add("y");
        chars.add("z");
        chars.add("A");
        chars.add("B");
        chars.add("C");
        chars.add("D");
        chars.add("E");
        chars.add("F");
        chars.add("G");
        chars.add("H");
        chars.add("I");
        chars.add("J");
        chars.add("K");
        chars.add("L");
        chars.add("M");
        chars.add("N");
        chars.add("O");
        chars.add("P");
        chars.add("Q");
        chars.add("R");
        chars.add("S");
        chars.add("T");
        chars.add("U");
        chars.add("V");
        chars.add("W");
        chars.add("X");
        chars.add("Y");
        chars.add("Z");
    }

    public String getRandomSalt () {
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            result.append(chars.get(random.nextInt(chars.size())));
        }
        return result.toString();
    }
}
