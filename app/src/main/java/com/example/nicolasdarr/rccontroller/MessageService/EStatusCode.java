package com.example.nicolasdarr.rccontroller.MessageService;

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
    EMERGENCY_BRAKE_ENGAGE(210, "Emergency Break"),
    SET_SPEED(211, "Set Speed"),
    SET_VIRTUAL_FRICTION(212, "Set Virtual Friction"),
    EMERGENCY_BRAKE_DISENGAGE(213, "Increase Speed"),
    //LED transmitting test
    LED_OFF(220, "LED Off"),
    LED_ON(221, "LED On"),
    LED_TOGGLE(222, "LED Toggle"),
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


    /**
     * Determines the status code based on a given status id
     * @param id    id of the status code
     * @return      enum item identified by the id
     *              NULL if nothing fit
     */
    public static EStatusCode statusById(int id){
        switch (id){
            case 100: return HELLO;
            case 101: return ACK;
            case 200: return CENTER_STEERING;
            case 201: return SET_STEERING_LEFT;
            case 202: return SET_STEERING_RIGHT;
            case 203: return SET_STEERING_AUTO_CENTER;
            case 204: return INCREASE_STEERING_LEFT;
            case 205: return INCREASE_STEERING_RIGHT;
            case 210: return EMERGENCY_BRAKE_ENGAGE;
            case 211: return SET_SPEED;
            case 212: return SET_VIRTUAL_FRICTION;
            case 213: return EMERGENCY_BRAKE_DISENGAGE;
            case 220: return LED_OFF;
            case 221: return LED_ON;
            case 222: return LED_TOGGLE;
            case 300: return REQUEST_DISTANCE_SENSOR_VALUE;
            case 350: return TRANSMIT_DISTANCE_SENSOR_VALUE;
            case 400: return GENERIC_COMMUNICATION_ERROR;
            case 401: return UNABLE_TO_CONNECT;
            case 402: return CONNECTION_TIMEOUT;
            case 420: return GENERIC_CAR_ERROR;
            case 421: return STEERING_ERROR;
            case 422: return ENGINE_ERROR;
            case 440: return GENERIC_SENSOR_ERROR;
            case 441: return DISTANCE_SENSOR_ERROR;
            //To be changed to GENERIC COMMUNICATION ERROR instead of null
            default:  return null;
        }
    }
}
