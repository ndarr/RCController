package com.example.nicolasdarr.rccontroller.Car;

import java.io.Serializable;

/**
 * Created by Nicolas on 22.11.2017.
 */

public class Car implements Serializable{
    int steering;
    int throttle;

    public Car(){

    }

    public int getSteering() {
        return steering;
    }

    public void setSteering(int steering) {
        this.steering = steering;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }
}
