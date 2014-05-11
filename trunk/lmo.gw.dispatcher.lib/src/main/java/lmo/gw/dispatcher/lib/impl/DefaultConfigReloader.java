/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib.impl;

import lmo.gw.dispatcher.lib.ConfigReloader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import lmo.gw.dispatcher.lib.Node;
import lmo.utils.bson.BSONSerializer;
import org.apache.log4j.Logger;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class DefaultConfigReloader extends ConfigReloader {

    public static HashMap<String, String> users = new HashMap<String, String>();
    public static HashMap<String, Set<String>> userips = new HashMap<String, Set<String>>();
    public static HashMap<String, Set<String>> userperms = new HashMap<String, Set<String>>();
    static BSONSerializer serializer = new BSONSerializer();

    public Object reload(Properties p, Logger logger) {
        for (String s : p.stringPropertyNames()) {
            String[] k = s.split("[.]");
            if (k.length > 1) {
                if (k[0].trim().equalsIgnoreCase("user")) {
                    String username = k[1].trim();
                    if (k.length == 2) {
                        String pass = p.getProperty(s, "");
                        users.put(username, pass);
                    } else if (k.length == 3 && k[2].equalsIgnoreCase("roles")) {
                        String perms[] = p.getProperty(s, "").split("[,]");
                        Set<String> permset = new HashSet<String>();
                        for (String r : perms) {
                            if (!r.trim().isEmpty()) {
                                permset.add(r.trim());
                            }
                        }
                        userperms.put(username, permset);
                    } else if (k.length == 3 && k[2].equalsIgnoreCase("ips")) {
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
                Node.SPLITTER = "/";
                if (k[0].trim().equalsIgnoreCase("func")) {
                    String funcname = s.split("[.]", 2)[1].trim();
                    if (k.length == 2) {
                        String path = p.getProperty(s);
                        String[] paths = path.split("[!]", 2);
                        if (paths.length == 2) {
                            this.setFunctionConfig(funcname, paths[0], paths[1]);
                        }
                    } else if (k.length == 3 && k[2].equalsIgnoreCase("pattern")) {
                        this.setFunctionPattern(funcname, p.getProperty(s));
                    }
                }
            }
        }
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("functions", getFunctions());
        config.put("users", users.keySet());
        config.put("userperms", userperms);
        config.put("userips", userips);
        return config;
    }
}
