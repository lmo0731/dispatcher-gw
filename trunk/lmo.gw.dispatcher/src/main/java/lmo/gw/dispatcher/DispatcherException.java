/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class DispatcherException extends RuntimeException {

    public DispatcherException(String message) {
        super(message);
    }

    public DispatcherException(Throwable cause) {
        super(cause);
    }

    public DispatcherException(String message, Throwable cause) {
        super(message, cause);
    }
}
