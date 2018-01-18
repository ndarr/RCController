package com.example.nicolasdarr.rccontroller.Util;

/**
 * Created by nicolas on 03.12.17.
 */

public class Array<T>{
    public static <T> void replacePart(T[] source, T[] replacement, int begin){
        for(int i = begin; i < replacement.length; i++){
            source[i] = replacement[i];
        }
    }

    public static byte[] leftShiftByteArray(byte[] array, int i){
        for(int j = i, n = 0; j < array.length; j++, i++){
            array[n] = array[j];
        }
        return array;
    }
}
