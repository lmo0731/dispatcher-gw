/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import lmo.utils.bson.BSONSerializer;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class Config {

    public static String LOG = "GW";
    public static String NAME = "GW";
    public static Logger logger;
    public static HashMap<String, String> users = new HashMap<String, String>();
    public static HashMap<String, Set<String>> userips = new HashMap<String, Set<String>>();
    public static HashMap<String, Set<String>> userperms = new HashMap<String, Set<String>>();
    public static HashMap<String, String> functions = new HashMap<String, String>();
    static BSONSerializer serializer = new BSONSerializer();

    public static Object reload() {
        String name = Config.NAME;
        String path = System.getProperty("catalina.base") + "/conf/" + name + ".properties";
        File f = new File(path);
        if (!f.exists()) {
            throw new RuntimeException("configuration not found: " + f.getAbsolutePath());
        }
        Properties properties = new Properties();
        FileInputStream fis = null;
        Config.LOG = name.toUpperCase();
        try {
            fis = new FileInputStream(f);
            properties.load(fis);
            PropertyConfigurator.configure(properties);
            Object ret = Config.init(properties);
            logger.info(ret);
            return ret;
        } catch (IOException ex) {
            throw new RuntimeException("configuration failed", ex);
        } finally {
            try {
                fis.close();
            } catch (Exception ex) {
            }
        }
    }

    public static Object init(Properties p) throws IOException {
        logger = Logger.getLogger(LOG + ".CONFIG");
        for (String s : p.stringPropertyNames()) {
            String[] k = s.split("[.]");
            if (k.length > 1) {
                if (k[0].trim().equalsIgnoreCase("user")) {
                    String username = k[1].trim();
                    if (k.length == 2) {
                        String pass = p.getProperty(s, "");
                        users.put(username, pass);
                    } else if (k.length == 3 && k[2].equals("roles")) {
                        String perms[] = p.getProperty(s, "").split("[,]");
                        Set<String> permset = new HashSet<String>();
                        for (String r : perms) {
                            if (!r.trim().isEmpty()) {
                                permset.add(r.trim());
                            }
                        }
                        userperms.put(username, permset);
                    } else if (k.length == 3 && k[2].equals("ips")) {
                        String ips[] = p.getProperty(s, "").split("[,]");
                        Set<String> ipset = new HashSet<String>();
                        for (String r : ips) {
                            if (!r.trim().isEmpty()) {
                                ipset.add(r.trim());
                            }
                        }
                        userips.put(username, ipset);
                    }
                }
                if (k[0].trim().equalsIgnoreCase("func")) {
                    k = s.split("[.]", 2);
                    String funcname = k[1].trim();
                    String path = p.getProperty(s);
                    functions.put(funcname, path);
                }
            }
        }
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("functions", functions);
        config.put("users", users.keySet());
        config.put("userperms", userperms);
        config.put("userips", userips);
        return config;
    }
}
