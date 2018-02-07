package com.example.nicolasdarr.rccontroller.Util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import java.util.HashMap;
import java.util.Map;

public class Devices {
    public static UsbSerialDevice uartDevice;

    public static boolean initDevice(Context context){
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        UsbDevice device = null;
        UsbDeviceConnection connection = null;
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        System.out.println("USB Devices: " + usbDevices.size());
        if(!usbDevices.isEmpty())
        {
            boolean keep = true;
            for(Map.Entry<String, UsbDevice> entry : usbDevices.entrySet())
            {
                device = entry.getValue();
                if(!usbManager.hasPermission(device)){
                    PendingIntent mPermissionIntent =
                            PendingIntent.getBroadcast(context, 0,
                                    new Intent("mbed.mbedwrapper.action.USB_PERMISSION"), 0);
                    usbManager.requestPermission(device, mPermissionIntent);
                    return false;
                }
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                if(deviceVID != 0x1d6b || (devicePID != 0x0001 || devicePID != 0x0002 || devicePID != 0x0003))
                {
                    // We are supposing here there is only one device connected and it is our serial device
                    connection = usbManager.openDevice(device);
                    keep = false;
                }else
                {
                    connection = null;
                    device = null;
                    return false;
                }

                if(!keep)
                    break;
            }
        }
        else{
            return false;
        }
        uartDevice = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if(uartDevice != null)
        {
            if(uartDevice.open())
            {
                // Devices are opened with default values, Usually 9600,8,1,None,OFF
                // CDC driver default values 115200,8,1,None,OFF
                uartDevice.setBaudRate(38400);
                uartDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
                uartDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);
                uartDevice.setParity(UsbSerialInterface.PARITY_NONE);
                uartDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                return true;
            }else
            {
                // Serial port could not be opened, maybe an I/O error or it CDC driver was chosen it does not really fit
            }
        }else
        {
            // No driver for given device, even generic CDC driver could not be loaded

        }
        return false;
    }
}
