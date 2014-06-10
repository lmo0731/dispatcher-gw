/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import flexjson.ObjectFactory;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    HttpServletRequest request;
    HttpServletResponse response;
    T target;

    public Handler(Class<? extends T> c) {
        try {
            this.target = c.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Handler(T target) {
        this.target = target;
    }

    public final Handler use(String path, Class c) {
        deserializer.use(path, c);
        return this;
    }

    public final Handler use(Class c, ObjectFactory o) {
        deserializer.use(c, o);
        return this;
    }

    public final Handler exclude(String... fields) {
        serializer.exclude(fields);
        return this;
    }

    final FunctionRequest<T> getRequest(Logger logger, String functionName, T target, Map<String, String[]> params) {
        return new FunctionRequest<T>(logger, functionName, target, params);
    }

    public abstract void handle(FunctionRequest<T> request, FunctionResponse response) throws FunctionException;

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        return response;
    }
}
