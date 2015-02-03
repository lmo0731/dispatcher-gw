/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.conf;

import java.util.Properties;
import junit.framework.TestCase;
import lmo.utils.bson.BSONSerializer;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author LMO
 */
public class ConfigLoaderTest extends TestCase {

    public ConfigLoaderTest(String testName) {
        super(testName);
    }

    /**
     * Test of load method, of class ConfigLoader.
     */
    public void testLoad() throws Exception {
        BasicConfigurator.configure();
        System.out.println("load");
        Object o = null;
        Properties p = new Properties();
        p.setProperty("pref.a.b.c", "abcTest");
        p.setProperty("pref.x.y.z.k", "test");
        p.setProperty("pref.cons.b", "test");
        String prefix = "pref";
        Config c = new Config();
        ConfigLoader.load(c, p, prefix);
        System.out.println(new BSONSerializer().serialize(c));
    }

    public static class Config {

        @ConfigName("a.b.c")
        public static String abc;
        @ConfigName(value = "d.e.f", required = true)
        public static String def;
        @ConfigName(value = "x.y", required = true)
        public static SubObject subObject;
        @ConfigName(value = "cons")
        public ConsObject cons;
    }

    public static class SubObject {

        @ConfigName(required = true)
        public static String z;
    }

    public static class ConsObject {

        public String b;

        public ConsObject() {
        }
    }
}
