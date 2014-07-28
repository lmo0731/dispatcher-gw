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

    void init(Properties p) throws Exception;

    void destroy() throws Exception;
}
