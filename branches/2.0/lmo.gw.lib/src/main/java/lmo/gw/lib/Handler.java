/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @munkhochir<lmo0731@gmail.com> 
 */
public abstract class Handler<T> {

    Map<String, ContentDeserializer> binders;
    HttpServletRequest request;
    HttpServletResponse response;
    T target;

    public Handler(T target) {
        this.target = target;
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
