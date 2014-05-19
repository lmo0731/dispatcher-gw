/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson.factory;

import flexjson.ObjectBinder;
import flexjson.factories.MapObjectFactory;
import java.lang.reflect.Type;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class BSONMapFactoryWithoutObjectId extends MapObjectFactory {

    BSONDateFactory dateFactory = new BSONDateFactory();
    BSONBinaryFactory binaryFactory = new BSONBinaryFactory();

    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if (targetType == null) {
            Object ret = dateFactory.getDate(value);
            if (ret != null) {
                return ret;
            }
            ret = binaryFactory.getInputStream(value);
            if (ret != null) {
                return ret;
            }
        }
        return super.instantiate(context, value, targetType, targetClass); //To change body of generated methods, choose Tools | Templates.
    }
}
