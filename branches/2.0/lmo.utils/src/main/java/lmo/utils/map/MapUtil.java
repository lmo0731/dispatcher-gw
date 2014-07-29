/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.map;

import java.util.Map;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class MapUtil {

    public static <T> T get(Object o, String path, Class<T> c) {
        if (path == null) {
            return null;
        }
        if (o == null) {
            return null;
        }
        try {
            String[] keys = path.split("[.]", 2);
            if (o instanceof Map) {
                Object k = ((Map) o).get(keys[0]);
                if (keys.length == 2) {
                    return get(k, keys[1], c);
                } else {
                    return c.cast(k);
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }
}
