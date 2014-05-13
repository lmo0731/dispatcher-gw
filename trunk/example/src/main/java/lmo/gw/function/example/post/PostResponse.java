/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.function.example.post;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
@XmlRootElement(name = "response")
public class PostResponse {

    public String info;
    @XmlTransient
    public Object map;
}
