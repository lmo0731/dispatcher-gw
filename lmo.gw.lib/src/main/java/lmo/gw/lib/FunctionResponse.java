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
 * @ munkhochir<lmo0731@gmail.com>
 */
public class FunctionResponse {

    private int code;
    private Object responseObject;
    private Map<String, String> headers = new HashMap<String, String>();

    public FunctionResponse() {
        this.code = HttpServletResponse.SC_OK;
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

    public Map<String, String> getHeaders() {
        return headers;
    }
}
