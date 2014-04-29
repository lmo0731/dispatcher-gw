/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com> <munkhochir@munkhochir.mn>
 */
public class ConfigLoader {

    public static Logger logger = Logger.getLogger(ConfigLoader.class);

    public static void load(Object o, String p, String prefix) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream(p);
        try {
            prop.load(in);
            ConfigLoader.load(o, prop, prefix);
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
    }

    public static void load(Object o, Properties p) {
        load(o, p, null);
    }

    public static void load(Object o, String p) throws FileNotFoundException, IOException {
        load(o, p, null);
    }

    public static void load(Object o, Properties p, String prefix) {
        Class c;
        if (o.getClass() == Class.class) {
            c = (Class) o;
        } else {
            c = o.getClass();
        }
        for (Field f : c.getFields()) {
            String key = (prefix == null ? "" : (prefix + ".")) + f.getName();
            String value = p.getProperty(key);
            if (value != null) {
                try {
                    Object v = parse(f.getType(), value);
                    if (v != null) {
                        f.set(o, v);
                        logger.debug(o + "." + f.getName() + " = " + f.get(o));
                    }
                } catch (Exception ex) {
                    logger.info("", ex);
                    continue;
                }
            }
        }
    }

    public static Object parse(Class type, String value) {
        Object v = null;
        if (type == Integer.class || type == Integer.TYPE) {
            v = Integer.parseInt(value);
        } else if (type == Long.class || type == Long.TYPE) {
            v = Long.parseLong(value);
        } else if (type == Float.class || type == Float.TYPE) {
            v = Float.parseFloat(value);
        } else if (type == Double.class || type == Double.TYPE) {
            v = Double.parseDouble(value);
        } else if (type == Boolean.class || type == Boolean.TYPE) {
            v = Boolean.parseBoolean(value);
        } else if (type == Byte.class || type == Byte.TYPE) {
            v = Byte.parseByte(value);
        } else if (type == Short.class || type == Short.TYPE) {
            v = Short.parseShort(value);
        } else if (type == String.class) {
            v = value;
        } else if (type.isArray()) {
            if (!type.getComponentType().isPrimitive()) {
                String[] splitted = split(value, ",");
                Object[] o = (Object[]) Array.newInstance(type.getComponentType(), splitted.length);
                for (int i = 0; i < splitted.length; i++) {
                    o[i] = (parse(type.getComponentType(), splitted[i].trim()));
                }
                v = o;
            } else {
                logger.warn("Unsupported array type: " + type.getComponentType() + "[]");
            }
        } else {
            logger.warn("Unsupported type: " + type);
        }
        return v;
    }

    public static String[] split(String str, String split) {
        String[] splitted = str.split(split);
        Character k = null;
        StringBuilder sb = new StringBuilder();
        LinkedList<String> list = new LinkedList<String>();
        for (int j = 0; j < splitted.length; j++) {
            String value = splitted[j];
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (k == null) {
                    if (c == '\"' || c == '\'') {
                        k = c;
                    }
                } else if (c == k) {
                    k = null;
                }
                sb.append(c);
            }
            if (k == null) {
                list.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(split);
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list.toArray(new String[]{});
    }
}
