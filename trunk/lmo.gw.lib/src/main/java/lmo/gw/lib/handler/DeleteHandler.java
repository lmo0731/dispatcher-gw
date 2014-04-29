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
public abstract class DeleteHandler<T> extends Handler<T> {

    public DeleteHandler(T target) {
        super(target);
    }
}
