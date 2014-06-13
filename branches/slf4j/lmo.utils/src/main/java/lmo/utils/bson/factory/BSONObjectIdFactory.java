/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson.factory;

import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Map;
import lmo.utils.bson.ObjectId;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class BSONObjectIdFactory extends AbstractTransformer implements ObjectFactory {

    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if (targetType == null) {
            return getObjectId(value);
        } else {
            try {
                ObjectId.class.asSubclass(targetClass);
                return getObjectId(value);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    Object getObjectId(Object value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (value instanceof Map) {
            Map map = (Map) value;
            if (map.containsKey("$oid") && map.size() == 1) {
                Object ts = map.get("$oid");
                if (ts instanceof String) {
                    return new ObjectId((String) ts);
                } else {
                    throw new JSONException("invalid type");
                }
            }
        }
        return null;
    }

    @Override
    public void transform(Object object) {
        this.getContext().writeOpenObject();
        this.getContext().writeName("$oid");
        this.getContext().write("" + ((ObjectId) object).getId());
        this.getContext().writeCloseObject();
    }
}
