/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @param <T>
 * @munkhochir<lmo0731@gmail.com>
 */
public class FunctionRequest<T> {

    private final String requestId;
    private final Logger logger;
    private final ArrayList<String> pathParams = new ArrayList<String>();
    private final String functionName;
    private final HttpServletRequest request;
    private final T requestObject;

    public FunctionRequest(Logger logger, String functionName, T requestObject, HttpServletRequest request) {
        this.functionName = functionName;
        this.logger = logger;
        this.requestObject = requestObject;
        this.request = request;
        this.requestId = (String) request.getAttribute(Attribute.REQID);
        this.pathParams.addAll((Collection<? extends String>) request.getAttribute(Attribute.PATHPARAMS));
    }

    public T getRequestObject() {
        return requestObject;
    }

    public Logger getLogger() {
        return logger;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public ArrayList<String> getPathParams() {
        return pathParams;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getFunctionName() {
        return functionName;
    }

}
