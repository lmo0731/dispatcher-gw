/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class FunctionRequest<T> {

    private String requestId;
    private Logger logger;
    private T requestObject;
    private ArrayList<String> pathParams;
    private Map<String, String[]> queryParams;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Map<String, Enumeration<String>> headers = new HashMap<String, Enumeration<String>>() {
        @Override
        public Enumeration<String> get(Object key) {
            if (key != null && key instanceof String) {
                key = key.toString().toLowerCase();
            }
            return super.get(key); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Enumeration<String> put(String key, Enumeration<String> value) {
            if (key != null) {
                key = key.toLowerCase();
            }
            return super.put(key, value); //To change body of generated methods, choose Tools | Templates.
        }
    };
    private String functionName;

    public FunctionRequest(Logger logger, String functionName, T requestObject, Map<String, String[]> params) {
        this.functionName = functionName;
        this.logger = logger;
        this.requestObject = requestObject;
        this.queryParams = params;
    }

    public T getRequestObject() {
        return requestObject;
    }

    public Logger getLogger() {
        return logger;
    }

    public Map<String, String[]> getQueryParams() {
        return queryParams;
    }

    public ArrayList<String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(ArrayList<String> pathParams) {
        this.pathParams = pathParams;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Map<String, Enumeration<String>> getHeaders() {
        return headers;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
