package com.blax.k8s.operator;

import lombok.Data;

@Data
public class AirCondSimulator {

    String name;
    boolean on;
    int temperature = 35; // the initial room temperature

    public void increaseTemp() {
        temperature ++;
    }

    public void decreaseTemp() {
        temperature --;
    }
}
