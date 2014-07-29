/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson.factory;

import flexjson.ObjectBinder;
import java.lang.reflect.Type;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class BSONMapFactory extends BSONMapFactoryWithoutObjectId {

    BSONObjectIdFactory objectIdFactory = new BSONObjectIdFactory();

    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if (targetType == null) {
            Object ret = objectIdFactory.getObjectId(value);
            if (ret != null) {
                return ret;
            }
        }
        return super.instantiate(context, value, targetType, targetClass); //To change body of generated methods, choose Tools | Templates.
    }
}
