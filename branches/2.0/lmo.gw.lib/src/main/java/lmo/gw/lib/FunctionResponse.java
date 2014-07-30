/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class FunctionResponse {

    private int code;
    private Object responseObject;
    private final HttpServletResponse response;

    public FunctionResponse(HttpServletResponse resp) {
        this.code = HttpServletResponse.SC_NOT_IMPLEMENTED;
        this.response = resp;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
