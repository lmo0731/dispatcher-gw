/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson.factory;

import flexjson.transformer.AbstractTransformer;

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
    }
}