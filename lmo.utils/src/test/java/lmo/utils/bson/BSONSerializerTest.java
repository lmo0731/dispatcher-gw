/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class BSONSerializerTest extends TestCase {

    public BSONSerializerTest(String testName) {
        super(testName);
    }

    public void testSomeMethod() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", null);
        map.put("b", 1);
        map.put("c", new Date());
        System.out.println(new BSONSerializer().serialize(map));
    }
}
