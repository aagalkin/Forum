package com.tools;

import java.io.Serializable;

public class Counter implements Serializable {
    private Integer i;
    public Counter(Integer startValue) {
        i = startValue;
    }

    public Integer getValue() {
        return i;
    }

    public boolean nextValue() {
        i++;
        return false;
    }
}
