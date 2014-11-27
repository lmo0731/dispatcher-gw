/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.Properties;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public interface ConfigListener {

    String getName();

    void initConfig(Properties p) throws Exception;

    void destroyConfig() throws Exception;
}
