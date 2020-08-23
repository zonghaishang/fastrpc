package com.fast.fastrpc.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yiji
 * @version : ReflectUtils.java, v 0.1 2020-08-19
 */
public class ReflectUtils {

    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    private static final ConcurrentMap<String, Class<?>> DESC_CLASS_CACHE = new ConcurrentHashMap<>();

    private static final String[] TYPES = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    public static Class<?>[] desc2classArray(String desc) throws ClassNotFoundException {
        Class<?>[] ret = desc2classArray(ReflectUtils.class.getClassLoader(), desc);
        return ret;
    }

    private static Class<?>[] desc2classArray(ClassLoader cl, String desc) throws ClassNotFoundException {
        if (desc.length() == 0)
            return EMPTY_CLASS_ARRAY;

        List<Class<?>> classArray = new ArrayList<>();
        for (String argumentType : splitArgumentTypes(desc)) {
            classArray.add(desc2class(cl, argumentType));
        }
        return classArray.toArray(EMPTY_CLASS_ARRAY);
    }

    public static String getDesc(final Class<?>[] cs) {
        if (cs.length == 0)
            return "";

        StringBuilder sb = new StringBuilder(64);
        for (Class<?> c : cs)
            sb.append(getDesc(c));
        return sb.toString();
    }

    public static String getDesc(Class<?> c) {
        StringBuilder ret = new StringBuilder();

        while (c.isArray()) {
            ret.append('[');
            c = c.getComponentType();
        }

        if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t)) ret.append('V');
            else if ("boolean".equals(t)) ret.append('Z');
            else if ("byte".equals(t)) ret.append('B');
            else if ("char".equals(t)) ret.append('C');
            else if ("double".equals(t)) ret.append('D');
            else if ("float".equals(t)) ret.append('F');
            else if ("int".equals(t)) ret.append('I');
            else if ("long".equals(t)) ret.append('J');
            else if ("short".equals(t)) ret.append('S');
        } else {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }

    public static List<String> splitArgumentTypes(String desc) {
        if (desc == null || desc.length() == 0) {
            return Collections.emptyList();
        }

        List<String> arguments = new ArrayList<>();

        boolean next = false;
        int pos = -1;

        char ch;
        for (int i = 0, len = desc.length(); i < len; i++) {
            ch = desc.charAt(i);
            // is array ?
            if (ch == '[') {
                if (pos == -1) { // first time discovery of the tag
                    pos = i;
                }
                continue;
            }

            // is object ?
            if (next && ch != ';') {
                continue;
            }

            switch (ch) {
                case 'V': // void
                case 'Z': // boolean
                case 'B': // byte
                case 'C': // char
                case 'D': // double
                case 'F': // float
                case 'I': // int
                case 'J': // long
                case 'S': // short
                {
                    arguments.add(TYPES[ch - 65]);
                    break;
                }
                default: {
                    // we found object
                    if (ch == 'L') {
                        if (pos == -1) { // first time discovery of the tag
                            pos = i;
                        }
                        next = true;
                    } else if (ch == ';') {  // end of object ?
                        next = false;
                        arguments.add(desc.substring(pos, i));
                        pos = -1;
                    }
                }
            }
        }

        return arguments;
    }

    private static Class<?> desc2class(ClassLoader cl, String desc) throws ClassNotFoundException {
        switch (desc.charAt(0)) {
            case 'V':
                return void.class;
            case 'Z':
                return boolean.class;
            case 'B':
                return byte.class;
            case 'C':
                return char.class;
            case 'D':
                return double.class;
            case 'F':
                return float.class;
            case 'I':
                return int.class;
            case 'J':
                return long.class;
            case 'S':
                return short.class;
            case 'L':
                desc = desc.substring(1, desc.length() - 1).replace('/', '.');
                break;
            case '[':
                desc = desc.replace('/', '.');
                break;
            default:
                throw new ClassNotFoundException("Class not found: " + desc);
        }

        if (cl == null)
            cl = ReflectUtils.class.getClassLoader();
        Class<?> clazz = DESC_CLASS_CACHE.get(desc);
        if (clazz == null) {
            clazz = Class.forName(desc, true, cl);
            DESC_CLASS_CACHE.put(desc, clazz);
        }
        return clazz;
    }

}
