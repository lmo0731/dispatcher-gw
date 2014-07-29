/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson;

import flexjson.JSON;
import flexjson.JSONException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class BSONValidator {

    public static Object validate(Object o) throws JSONException {
        return validate(null, o);
    }

    public static Object validate(String path, Object o) throws JSONException {
        if (o == null) {
            return null;
        }
        path = ((path == null || path.isEmpty()) ? "" : (path + "."));
        for (Field f : o.getClass().getFields()) {
            try {
                if (f.isAnnotationPresent(BSONNotNull.class)) {
                    String name = f.getName();
                    if (f.isAnnotationPresent(JSON.class)) {
                        JSON json = f.getAnnotation(JSON.class);
                        if (!json.include()) {
                            continue;
                        }
                        name = json.name();
                    }
                    Object e = f.get(o);
                    if (e == null) {
                        throw new JSONException("'" + path + name + "' element is missing or invalid");
                    }
                }
            } catch (JSONException ex) {
                throw ex;
            } catch (Exception ex) {
            }
        }
        for (Method m : o.getClass().getMethods()) {
            try {
                if (m.isAnnotationPresent(BSONNotNull.class)
                        && m.getParameterTypes().length == 0
                        && m.getName().startsWith("get")) {
                    String name = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
                    if (m.isAnnotationPresent(JSON.class)) {
                        JSON json = m.getAnnotation(JSON.class);
                        if (!json.include()) {
                            continue;
                        }
                        name = json.name();
                    }
                    Object e = m.invoke(o);
                    if (e == null) {
                        throw new JSONException("'" + path + name + "' element is missing or invalid");
                    }
                }
            } catch (JSONException ex) {
                throw ex;
            } catch (Exception ex) {
            }
        }
        return o;
    }
}
