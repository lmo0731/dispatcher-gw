/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author LMO
 */
public abstract class HandlerObjectBinder<T> {

    protected abstract void bind(HttpServletRequest req, HttpServletResponse resp, T target) throws FunctionException;
}
