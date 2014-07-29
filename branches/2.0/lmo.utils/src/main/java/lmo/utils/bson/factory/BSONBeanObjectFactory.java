/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson.factory;

import flexjson.JSONException;
import flexjson.JsonNumber;
import flexjson.ObjectBinder;
import flexjson.factories.BeanObjectFactory;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lmo.utils.bson.BSONValidator;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class BSONBeanObjectFactory extends BeanObjectFactory {

    BSONMapFactory mapFactory = new BSONMapFactory();
    BSONNumberFactory numberFactory = new BSONNumberFactory();

    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        String path = context.getCurrentPath().toString();
        path = path.substring(1, path.length() - 2).trim();
        if (targetType == Object.class || targetType == null) {
            if (value instanceof Map) {
                return mapFactory.instantiate(context, value, null, targetClass);
            } else if (value instanceof JsonNumber) {
                return numberFactory.instantiate(context, value, null, targetClass);
            } else if (value instanceof Collection) {
                ArrayList target = new ArrayList();
                return context.bindIntoCollection((Collection) value, target, null);
            } else {
                return BSONValidator.validate(path, value);
            }
        } else {
            try {
                return BSONValidator.validate(path, super.instantiate(context, value, targetType, targetClass));
            } catch (JSONException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new JSONException(ex.getMessage(), ex);
            }
        }
    }
}
