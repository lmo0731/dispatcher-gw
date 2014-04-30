/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.Map;
import lmo.utils.bson.BSONDeserializer;
import lmo.utils.bson.BSONSerializer;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com> <munkhochir@munkhochir.mn>
 */
public abstract class Handler<T> {

    final BSONDeserializer<T> deserializer = new BSONDeserializer<T>();
    final BSONSerializer serializer = new BSONSerializer();
    T target;

    public Handler(T target) {
        this.target = target;
    }

    final Handler use(String path, Class c) {
        deserializer.use(path, c);
        return this;
    }

    final Handler exclude(String... fields) {
        serializer.exclude(fields);
        return this;
    }

    final FunctionRequest<T> getRequest(Logger logger, T target, Map<String, String[]> params) {
        return new FunctionRequest<T>(logger, target, params);
    }

    public abstract void handle(FunctionRequest<T> request, FunctionResponse response) throws FunctionException;
}
