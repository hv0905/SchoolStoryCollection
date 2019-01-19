package net.sakuratrak.schoolstorycollection.core;

import java.util.List;

public final class AppHelper {

    public static String stringList2String(List<String> strList){
        StringBuilder sb = new StringBuilder();
        for (String item :
                strList) {
            sb.append(item).append(';');
        }
        return sb.toString();
    }

    public static String[] string2StringArray(String str){
        return str.split(";");
    }

}
