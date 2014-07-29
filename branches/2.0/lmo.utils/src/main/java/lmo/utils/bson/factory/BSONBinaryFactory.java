/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson.factory;

import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Map;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class BSONBinaryFactory extends AbstractTransformer implements ObjectFactory {

    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if (targetType == null) {
            return getInputStream(value);
        } else {
            try {
                ByteArrayInputStream.class.asSubclass(targetClass);
                return getInputStream(value);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    Object getInputStream(Object value) {
        if (value instanceof Map) {
            Map map = (Map) value;
            if (map.containsKey("$binary") && map.size() == 1) {
                Object ts = map.get("$binary");
                if (ts instanceof String) {
                    try {
                        byte[] bytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(ts.toString());
                        return new ByteArrayInputStream(bytes);
                    } catch (Exception ex) {
                        throw new JSONException("invalid binary string", ex);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void transform(Object object) {
        ByteArrayOutputStream baos = (ByteArrayOutputStream) object;
        String base64 = javax.xml.bind.DatatypeConverter.printBase64Binary(baos.toByteArray());
        this.getContext().writeOpenObject();
        this.getContext().writeName("$binary");
        this.getContext().writeQuoted(base64);
        this.getContext().writeCloseObject();
    }
}
