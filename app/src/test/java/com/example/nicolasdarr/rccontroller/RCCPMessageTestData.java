package com.example.nicolasdarr.rccontroller;

import com.example.nicolasdarr.rccontroller.MessageService.EStatusCode;
import com.example.nicolasdarr.rccontroller.MessageService.RCCPMessage;

import java.util.ArrayList;

/**
 * Created by nicolas on 04.12.17.
 */

public class RCCPMessageTestData {
    private ArrayList<RCCPMessage> expected;
    private ArrayList<byte[]> actual;
    RCCPMessageTestData(){
        //Init arrays
        expected = new ArrayList<>();
        actual = new ArrayList<>();

        /*
        Testcase 1
        Should succeed
         */
        actual.add(
                new byte[]{
                        //Sequencenumber    = 1
                        (byte)0x01,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Statuscode        = HELLO
                        (byte)0x64,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Payload           = 0
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
        expected.add(new RCCPMessage(1, EStatusCode.HELLO, 0));

        /*
        Testcase 2
        Should succeed
         */
        actual.add(
                new byte[]{
                        //Sequencenumber    = 1337
                        (byte)0x39,
                        (byte)0x05,
                        (byte)0x00,
                        (byte)0x00,
                        //Statuscode        = SET_STEERING_LEFT
                        (byte)0xC9,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Payload           = 255
                        (byte)0xff,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
        expected.add(new RCCPMessage(1337, EStatusCode.SET_STEERING_LEFT, 255));

        /*
        Testcase 3
        Should fail
         */
        actual.add(
                new byte[]{
                        //Sequencenumber    = 2
                        (byte)0x02,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Statuscode        = HELLO
                        (byte)0x64,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Payload           = 0
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
        expected.add(new RCCPMessage(1, EStatusCode.HELLO, 0));

        /*
        Testcase 4
        Should fail
         */
        actual.add(
                new byte[]{
                        //Sequencenumber    = 1
                        (byte)0x01,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Statuscode        = SET_STEERING_LEFT
                        (byte)0xC9,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Payload           = 1
                        (byte)0x01,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
        expected.add(new RCCPMessage(100, EStatusCode.HELLO, 0));

        /*
        Testcase 5
        Should succeed
         */
        actual.add(
                new byte[]{
                        //Sequencenumber    = 16
                        (byte)0x10,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Statuscode        = DECREASE_SPEED
                        (byte)0xD6,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Payload           = 213
                        (byte)0xD5,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
        expected.add(new RCCPMessage(16, EStatusCode.DECREASE_SPEED, 213));

        /*
        Testcase 6
        Should succeed
         */
        actual.add(
                new byte[]{
                        //Sequencenumber    = 31213
                        (byte)0xED,
                        (byte)0x79,
                        (byte)0x00,
                        (byte)0x00,
                        //Statuscode        = CENTER_STEERING
                        (byte)0xC8,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        //Payload           = 0
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
        expected.add(new RCCPMessage(31213, EStatusCode.CENTER_STEERING, 0));
    }

    public RCCPMessage getExpectedAt(int n){
        return this.expected.get(n);
    }

    public byte[] getActualAt(int n){
        return this.actual.get(n);
    }

    public int getTestIterations(){
        if(expected.size() != actual.size()) return 0;
        return expected.size();
    }
}
