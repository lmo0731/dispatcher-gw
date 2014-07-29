/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib.handler.binder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.lib.FunctionException;
import lmo.gw.lib.ContentDeserializer;

/**
 *
 * @author LMO
 */
public class JsonContentDeserializer<T> extends ContentDeserializer<T> {

    @Override
    protected void bind(HttpServletRequest req, HttpServletResponse resp, T target) throws FunctionException {
        
    }
}
