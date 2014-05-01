/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class FunctionException extends RuntimeException implements Serializable {

    static final long serialVersionUID = 1L;
    int code;
    Map<String, String> headers;

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

    public FunctionException(int code, Map<String, String> headers) {
        this.code = code;
        this.headers = headers;
    }

    public FunctionException(int code, Map<String, String> headers, String message) {
        super(message);
        this.code = code;
        this.headers = headers;
    }

    public FunctionException(int code, Map<String, String> headers, Throwable cause) {
        super(cause);
        this.code = code;
        this.headers = headers;
    }

    public FunctionException(int code, Map<String, String> headers, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.headers = headers;
    }

    public int getCode() {
        return code;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
