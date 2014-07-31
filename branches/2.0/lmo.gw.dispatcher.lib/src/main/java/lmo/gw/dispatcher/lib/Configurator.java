/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public abstract class Configurator {

    public void setFunctionPattern(String name, String pattern) {
        Config.functionPaths.insert(pattern, name);
    }

    public void setFunctionConfig(String name, String context, String servlet) {
        Config.functions.put(name, context + "!" + servlet);
    }

    public String getFunctions() {
        return Config.functionPaths.toString(Dispatcher.NAME);
    }

    public Map<String, String> getFunctionsMap() {
        Map<String, String> map = new HashMap<String, String>();
        Config.functionPaths.toMap(Dispatcher.NAME, map);
        return map;
    }

    public Map<String, String> getFunctionConfig() {
        return Config.functions;
    }

    public abstract Object configure(Properties p, Logger logger) throws RuntimeException;

    public void destroy(Logger logger) {
    }
}
