/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson.factory;

import flexjson.transformer.AbstractTransformer;
import java.util.Iterator;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class BSONNullTransformer extends AbstractTransformer {

    @Override
    public Boolean isInline() {
        return true;
    }

    @Override
    public void transform(Object object) {
        Object a = null;
        try {
            a = getContext().getObjectStack().get(1);
        } catch (Exception ex) {
            getContext().write("null");
        }
        if (a == null) {
        } else if (a instanceof Iterable) {
            getContext().write("null");
        } else if (a instanceof Iterator) {
            getContext().write("null");
        } else if (a.getClass().isArray()) {
            getContext().write("null");
        }
    }
}
