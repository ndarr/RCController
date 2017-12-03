package com.example.nicolasdarr.rccontroller.Util;

/**
 * Created by nicolas on 03.12.17.
 */

public class Array{
    public static void replacePart(byte[] source, byte[] replacement, int begin){
        for(int i = begin; i < replacement.length; i++){
            source[i] = replacement[i];
        }
    }
}
