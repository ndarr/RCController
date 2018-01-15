package com.example.nicolasdarr.rccontroller.Util;

import android.content.Context;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

public class Devices {
    public static FT_Device uartDevice;

    public static boolean initDevice(Context context){
        //Open connected UART Device
        //If device is already open, return true
        if(Devices.uartDevice != null && Devices.uartDevice.isOpen()){
            return true;
        }
        //Try to open the device
        try {
            D2xxManager manager = D2xxManager.getInstance(context);
            //Check if there are devices available
            if(manager.createDeviceInfoList(context) > 0){
                //Open the UART device
                Devices.uartDevice = manager.openByIndex(context, 0);
                Devices.uartDevice.setBitMode((byte)0 , D2xxManager.FT_BITMODE_RESET);
                Devices.uartDevice.setBaudRate(38400);
                Devices.uartDevice.setDataCharacteristics(  D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
                Devices.uartDevice.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x0b, (byte) 0x0d);
                //Check if device has been opened
                if(Devices.uartDevice != null && Devices.uartDevice.isOpen()){
                    //Device is now open and ready to use
                    return true;
                }
            }
            //Catch any exception occuring while opening the device
        } catch (D2xxManager.D2xxException e) {
            e.printStackTrace();
        }
        //Opening the device failed
        return false;
    }
}
