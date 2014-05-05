/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public abstract class ConfigReloader {

    public void setFunctionPattern(String name, String pattern) {
        Config.functionPaths.insert(pattern, name);
    }

    public void setFunctionConfig(String name, String context, String servlet) {
        Config.functions.put(name, context + "!" + servlet);
    }

    public String getFunctions() {
        return Config.functionPaths.toString("GW");
    }

    public abstract Object reload(Properties p, Logger logger) throws RuntimeException;
}
