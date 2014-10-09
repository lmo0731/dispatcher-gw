/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib.handler.binder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import lmo.gw.lib.ContentBinder;
import lmo.utils.bson.BSONDeserializer;
import lmo.utils.bson.BSONSerializer;

/**
 *
 * @author LMO
 */
public class JsonContentBinder<T> extends ContentBinder<T> {

    BSONDeserializer<T> deserializer = new BSONDeserializer<T>();
    BSONSerializer serializer = new BSONSerializer();

    @Override
    protected T deserialize(InputStream in, Class<T> t) throws Exception {
        if (t == null) {
            return null;
        }
        T target = t.newInstance();
        target = deserializer.deserializeInto(new InputStreamReader(in), target);
        return target;
    }

    @Override
    protected void serialize(Object o, OutputStream out) throws Exception {
        String bson = serializer.deepSerialize(o);
        PrintWriter p = new PrintWriter(out);
        p.append(bson);
        p.flush();
    }

    @Override
    protected String getContentType() {
        return "application/json";
    }

}