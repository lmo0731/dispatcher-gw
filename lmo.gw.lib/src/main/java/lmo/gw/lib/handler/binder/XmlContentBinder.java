/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib.handler.binder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import lmo.gw.lib.ContentBinder;
import lmo.utils.jaxb.XmlUtil;

/**
 *
 * @author lmoo
 */
public class XmlContentBinder<T> extends ContentBinder<T> {

    @Override
    protected String getContentType() {
        return "application/xml";
    }

    @Override
    protected T deserialize(InputStream in, Class<T> t) throws Exception {
        if (t == null) {
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        StringBuilder xml = new StringBuilder();
        while ((line = br.readLine()) != null) {
            xml.append(line);
        }
        T res = (T) XmlUtil.unmarshal(xml.toString(), t);
        return res;
    }

    @Override
    protected void serialize(Object o, OutputStream out) throws Exception {
        String xml = XmlUtil.marshal(o);
        PrintWriter pw = new PrintWriter(out);
        pw.append(xml);
        pw.flush();
    }

}
