/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.function.example.post;

import flexjson.JSON;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lmo.utils.bson.BSONNotNull;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
@BSONNotNull
@XmlRootElement(name = "request")
public class PostRequest {

    @BSONNotNull
    @JSON(name = "string")
    @XmlElement(name = "string")
    public String str;
    public Integer integer;
    @BSONNotNull
    @XmlElement(required = true)
    public Double requiredDouble;
    public Object map;
    public List<String> list;
}
