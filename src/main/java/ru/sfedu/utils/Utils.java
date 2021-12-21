package ru.sfedu.utils;

import ru.sfedu.model.Security;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<String> buildStringListFromString(String str, String regex){
        return new ArrayList<>(List.of(str.split(regex)));
    }
    public static String buildStringFromStringList(List<String> list, String delimiter){
        return String.join(delimiter,  list.toArray(new String[0]));
    }
}
