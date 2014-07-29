/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson;

import flexjson.JSONSerializer;
import flexjson.OutputHandler;
import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.util.Date;
import lmo.utils.bson.factory.BSONBinaryFactory;
import lmo.utils.bson.factory.BSONDateFactory;
import lmo.utils.bson.factory.BSONNullTransformer;
import lmo.utils.bson.factory.BSONObjectIdFactory;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class BSONSerializer extends JSONSerializer {

    public BSONSerializer() {
        this.exclude("class", "*.class");
        this.transform(new BSONDateFactory(), Date.class);
        this.transform(new BSONBinaryFactory(), ByteArrayOutputStream.class);
        this.transform(new BSONObjectIdFactory(), ObjectId.class);
        this.transform(new BSONNullTransformer(), void.class);
    }

    @Override
    public String serialize(Object target) {
        return super.deepSerialize(target); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String serialize(Object target, OutputHandler out) {
        return super.deepSerialize(target, out); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String serialize(Object target, StringBuffer out) {
        return super.deepSerialize(target, out); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String serialize(Object target, StringBuilder out) {
        return super.deepSerialize(target, out); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void serialize(Object target, Writer out) {
        super.deepSerialize(target, out); //To change body of generated methods, choose Tools | Templates.
    }
}
