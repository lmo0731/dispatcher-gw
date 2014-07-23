/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class FunctionException extends RuntimeException implements Serializable {

    int code;
    Map<String, String> headers = new HashMap<String, String>();

    public FunctionException(int code) {
        this.code = code;
    }

    public FunctionException(int code, String message) {
        super(message);
        this.code = code;
    }

    public FunctionException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public FunctionException(int code, String errorCode, String message) {
        super(message);
        this.code = code;
        this.headers.put("X-Error-Code", errorCode);
    }

    public FunctionException(int code, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.headers.put("X-Error-Code", errorCode);
    }

    public int getCode() {
        return code;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
