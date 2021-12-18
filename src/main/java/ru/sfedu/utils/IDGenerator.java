package ru.sfedu.utils;

import java.util.UUID;

public class IDGenerator {
    public static String generate(){
        return UUID.randomUUID().toString();
    }
}
