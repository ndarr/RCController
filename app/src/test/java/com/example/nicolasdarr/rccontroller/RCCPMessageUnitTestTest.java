package com.example.nicolasdarr.rccontroller;

import com.example.nicolasdarr.rccontroller.MessageService.EStatusCode;
import com.example.nicolasdarr.rccontroller.MessageService.RCCPMessage;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by nicolas on 04.12.17.
 */
public class RCCPMessageUnitTestTest {
    @Test
    public void test(){
        RCCPMessageTestData testData = new RCCPMessageTestData();
        for(int i = 0; i < testData.getTestIterations(); i++) {
            boolean equals;
            try{
                System.out.println(Arrays.toString(testData.getExpectedAt(i).toByteArray()) + "=" + Arrays.toString(testData.getActualAt(i)));
                assertEquals(testData.getExpectedAt(i).toByteArray(), testData.getActualAt(i));
                equals = true;
            }catch (AssertionError e){
                equals = false;
            }
            System.out.println("Test " + Integer.toString(i + 1) + ": " + Boolean.toString(equals));
        }
    }

}