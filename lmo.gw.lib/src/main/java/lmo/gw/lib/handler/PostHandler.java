/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib.handler;

import lmo.gw.lib.Handler;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public abstract class PostHandler<T> extends Handler<T> {

    public PostHandler(Class<T> target) {
        super(target);
    }
}
