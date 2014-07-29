/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson;

import flexjson.JSON;
import flexjson.JSONSerializer;
import flexjson.transformer.AbstractTransformer;
import flexjson.transformer.DateTransformer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    public void testSomeMethod1() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", null);
        map.put("b", 1);
        map.put("c", new Date());
        map.put("d", Arrays.asList((String) null));
        map.put("e", new String[]{null});
        System.out.println(new BSONSerializer().serialize(map));
        System.out.println(new BSONSerializer().transform(new CustomDateTransformer(), Date.class).serialize(map));
    }

    public void testSomeMethod2() {
        System.out.println(new BSONSerializer().transform(new DateTransformer("yyyy-MM-dd HH:mm:ss"), Date.class).serialize(new CustomObject()));
    }

    public static class CustomObject {

        @JSON(name = "date1", transformer = CustomDateTransformer.class)
        public Date date = new Date();
    }

    public static class CustomDateTransformer extends AbstractTransformer {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public void transform(Object object) {
            if (object instanceof Date) {
                this.getContext().writeQuoted(sdf.format((Date) object));
            }
        }

    }
}
