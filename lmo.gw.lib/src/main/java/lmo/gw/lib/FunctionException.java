/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.io.Serializable;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class FunctionException extends RuntimeException implements Serializable {

    int code;

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

    public int getCode() {
        return code;
    }
}
