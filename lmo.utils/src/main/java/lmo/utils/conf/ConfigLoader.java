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
 * @munkhochir<lmo0731@gmail.com>
 */
public class ConfigLoader {

    public static Logger logger = Logger.getLogger(ConfigLoader.class);

    public static Properties load(Object o, String p, String prefix) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream(p);
        try {
            prop.load(in);
            ConfigLoader.load(o, prop, prefix);
            return prop;
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
    }

    public static int load(Object o, Properties p) {
        return load(o, p, null);
    }

    public static Properties load(Object o, String p) throws FileNotFoundException, IOException {
        return load(o, p, null);
    }

    public static int load(Object o, Properties p, String prefix) {
        int ret = 0;
        Class c;
        if (o.getClass() == Class.class) {
            c = (Class) o;
        } else {
            c = o.getClass();
        }
        for (Field f : c.getFields()) {
            String key = (prefix == null ? "" : (prefix + ".")) + f.getName();
            boolean required = false;
            if (f.isAnnotationPresent(ConfigName.class)) {
                ConfigName name = f.getAnnotation(ConfigName.class);
                if (!name.value().equals("##default")) {
                    key = (prefix == null ? "" : (prefix + ".")) + name.value();
                }
                required = name.required();
            }
            String value = p.getProperty(key);
            if (value != null) {
                try {
                    Object v = parse(f.getType(), value, p, key);
                    if (v != null) {
                        f.set(o, v);
                        logger.debug(o + "." + f.getName() + " = " + f.get(o));
                        ret++;
                    } else if (required) {
                        logger.warn(o + "." + f.getName() + " is required");
                    }
                } catch (Exception ex) {
                    logger.info("", ex);
                    continue;
                }
            } else {
                try {
                    Object v = parse(f.getType(), value, p, key);
                    if (v != null) {
                        f.set(o, v);
                        logger.debug(o + "." + f.getName() + " = " + f.get(o));
                        ret++;
                    } else if (required) {
                        logger.warn(key + " is missing");
                    }
                } catch (Exception ex) {
                    logger.info("", ex);
                    continue;
                }
            }
        }
        return ret;
    }

    public static Object parse(Class type, String value, Properties p, String prefix) {
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
                    o[i] = (parse(type.getComponentType(), splitted[i].trim(), p, prefix));
                }
                v = o;
            } else {
                logger.warn("Unsupported array type: " + type.getComponentType() + "[]");
            }
        } else {
            try {
                Object o = type.newInstance();
                int k = load(o, p, prefix);
                if (k > 0) {
                    return o;
                }
            } catch (InstantiationException ex) {
                logger.warn("Construct error: " + type);
            } catch (IllegalAccessException ex) {
                logger.warn("Access error: " + type);
            }
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
