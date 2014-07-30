/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author LMO
 */
public abstract class ContentBinder<T> {

    protected abstract T deserialize(InputStream in, Class<T> t) throws ContentBindException, Exception;

    protected abstract void serialize(Object o, OutputStream out) throws ContentBindException, Exception;

}
