/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class FunctionRequest<T> {

    private Logger logger;
    private T requestObject;
    private Map<String, String[]> params;

    public FunctionRequest(Logger logger, T requestObject, Map<String, String[]> params) {
        this.logger = logger;
        this.requestObject = requestObject;
        this.params = params;
    }

    public T getRequestObject() {
        return requestObject;
    }

    public Logger getLogger() {
        return logger;
    }

    public Map<String, String[]> getParams() {
        return params;
    }

    public Object getRequestObject(Class c) throws FunctionException {
        return FunctionRequest.cast(requestObject, "request object", c, true);
    }

    public static Object cast(Object o, String name, Class c, boolean required) throws FunctionException {
        if (o == null) {
            if (required) {
                throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, String.format("%s required", name, c.getSimpleName()));
            }
            return null;
        }
        try {
            return c.cast(o);
        } catch (Exception ex) {
            throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, String.format("%s type must be %s", name, c.getSimpleName()));
        }
    }
}
