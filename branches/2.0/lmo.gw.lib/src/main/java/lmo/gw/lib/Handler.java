/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import lmo.gw.lib.handler.binder.JsonContentBinder;
import org.apache.log4j.Logger;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public abstract class Handler<T> {

    Class<T> target;
    HashMap<String, ContentBinder<T>> binders = new HashMap<String, ContentBinder<T>>();

    public Handler(Class<T> target) {
        this.target = target;
        this.binders.put(null, new JsonContentBinder<T>());
        this.binders.put("application/json", new JsonContentBinder<T>());
    }

    final FunctionRequest<T> getRequest(Logger logger, String functionName, T target, HttpServletRequest req) {
        return new FunctionRequest<T>(logger, functionName, target, req);
    }

    public abstract void handle(FunctionRequest<T> request, FunctionResponse response) throws FunctionException;
}
