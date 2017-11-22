package com.example.nicolasdarr.rccontroller.MessageService;

/**
 * Created by Nicolas on 22.11.2017.
 */

public enum EStatusCode {
    HELLO(100, "Hello"),
    ACK(101, "Acknowledge"),
    // car control codes
    CENTER_STEERING(200, "Center Steering"),
    SET_STEERING_LEFT(201, "Steering Left"),
    SET_STEERING_RIGHT(202, "Steering Right"),
    SET_STEERING_AUTO_CENTER(203, "Steering Auto Center"),
    INCREASE_STEERING_LEFT(204, "Increase Steering Left"),
    INCREASE_STEERING_RIGHT(205, "Increase Steering Right"),
    EMERGENCY_BRAKE(210, "Emergency Break"),
    SET_SPEED(211, "Set Speed"),
    SET_VIRTUAL_FRICTION(212, "Set Virtual Friction"),
    INCREASE_SPEED(213, "Increase Speed"),
    DECREASE_SPEED(214, "Decrease Speed"),
    // status and report codes
    REQUEST_DISTANCE_SENSOR_VALUE(300, "Request Distance Sensor Value"),
    TRANSMIT_DISTANCE_SENSOR_VALUE(350, "Transmit Distance Sensor Value"),
    // communication errors
    GENERIC_COMMUNICATION_ERROR(400, "Generic Communication Error"),
    UNABLE_TO_CONNECT(401, "Unable to Connect"),
    CONNECTION_TIMEOUT(402, "Connection Timeout"),
    // car errors
    GENERIC_CAR_ERROR(420, "Generic Car Error"),
    STEERING_ERROR(421, "Steering Error"),
    ENGINE_ERROR(422, "Engine Error"),
    // sensor errors
    GENERIC_SENSOR_ERROR(440, "Generic Sensor Error"),
    DISTANCE_SENSOR_ERROR(441, "Distance Sensor Error");

    public final int status;
    public final String label;

    EStatusCode(int status,String label){
        this.status = status;
        this.label = label;
    }
}
