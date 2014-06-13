/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib.handler;

import lmo.gw.lib.Handler;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public abstract class PutHandler<T> extends Handler<T> {

    public PutHandler(T target) {
        super(target);
    }
}
