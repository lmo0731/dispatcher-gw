/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson.factory;

import flexjson.JSONException;
import flexjson.JsonNumber;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import java.lang.reflect.Type;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class BSONNumberFactory implements ObjectFactory {

    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonNumber) {
            JsonNumber number = (JsonNumber) value;
            if (targetType == Integer.class) {
                return number.intValue();
            }
            if (targetType == Long.class) {
                return number.longValue();
            }
            if (targetType == Double.class) {
                return number.doubleValue();
            }
            if (targetType == Float.class) {
                return number.floatValue();
            }
            if (targetType == Short.class) {
                return number.shortValue();
            }
            if (targetType == Byte.class) {
                return number.byteValue();
            }
            if (targetType == String.class) {
                return "" + number.doubleValue();
            }
            if (number.isDecimal()) {
                return number.doubleValue();
            } else {
                return number.longValue();
            }
        }
        throw new JSONException(context.getCurrentPath().toString() + " is not number object");
    }
}
