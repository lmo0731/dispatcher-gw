/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.jaxb;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @ munkhochir<lmo0731@gmail.com> <munkhochir@munkhochir.mn>
 */
public class XmlDateTimeAdapter extends XmlAdapter<String, Date> {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date unmarshal(String v) throws Exception {
        return sdf.parse(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
        return sdf.format(v);
    }
}
