package com.example.nicolasdarr.rccontroller.Car;

import com.example.nicolasdarr.rccontroller.MessageService.EStatusCode;
import com.example.nicolasdarr.rccontroller.MessageService.RCCPMessage;

/**
 * Created by Nicolas on 17.01.2018.
 */

public class CarController {
    private int steering;
    private int throttle;

    public CarController(){}

    public RCCPMessage getSteeringMessage(){
        if(steering < 0){
            //Invert because steering is negative
            steering *= -1;
            return new RCCPMessage(EStatusCode.SET_STEERING_LEFT, steering);
        }
        else if(steering > 0){
            return new RCCPMessage(EStatusCode.SET_STEERING_RIGHT, steering);
        }
        else{
            return new RCCPMessage(EStatusCode.CENTER_STEERING, 0);
        }
    }

    public RCCPMessage getThrottleMessage(){
        return new RCCPMessage(EStatusCode.SET_SPEED, throttle);
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
