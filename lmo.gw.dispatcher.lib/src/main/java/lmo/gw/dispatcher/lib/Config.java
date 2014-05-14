/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import lmo.gw.dispatcher.lib.impl.DefaultAuthenticator;
import lmo.gw.dispatcher.lib.impl.DefaultConfigReloader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class Config {

    static Logger logger;
    static Authenticator authenticator = null;
    static ConfigReloader configReloader = null;
    static ErrorHandler errorHandler = null;
    static final Object lock = new Object();
    static boolean isLoading = false;
    static HashMap<String, String> functions = new HashMap<String, String>();
    static Node functionPaths = new Node();

    public static Object reload(Logger logger) {
        String name = Dispatcher.NAME;
        String path = System.getProperty("catalina.base") + "/conf/" + name + ".properties";
        File f = new File(path);
        if (!f.exists()) {
            throw new RuntimeException("configuration not found: " + f.getAbsolutePath());
        }
        Properties properties = new Properties();
        FileInputStream fis = null;
        Object ret;
        try {
            isLoading = true;
            synchronized (lock) {
                try {
                    fis = new FileInputStream(f);
                    properties.load(fis);
                    PropertyConfigurator.configure(properties);
                    ret = Config.init(properties, logger);
                } finally {
                    lock.notify();
                    isLoading = false;
                }
            }
            logger.info(ret);
            return ret;
        } catch (Exception ex) {
            throw new RuntimeException("configuration failed", ex);
        } finally {
            try {
                fis.close();
            } catch (Exception ex) {
            }
        }
    }

    public static Object init(Properties p, Logger logger) throws IOException {
        try {
            Class authenticatorClass = Class.forName(p.getProperty("authenticator",
                    DefaultAuthenticator.class.getCanonicalName()));
            authenticator = (Authenticator) authenticatorClass.newInstance();
        } catch (Exception ex) {
            logger.warn("authenticator load", ex);
        } finally {
            logger.info("authenticator: " + authenticator);
        }
        try {
            Class configReloaderClass = Class.forName(
                    p.getProperty("configReloader",
                    DefaultConfigReloader.class.getCanonicalName()));
            configReloader = (ConfigReloader) configReloaderClass.newInstance();
        } catch (Exception ex) {
            logger.warn("configReloader load", ex);
        } finally {
            logger.info("configReloader: " + configReloader);
        }
        try {
            Class errorHandlerClass = Class.forName(
                    p.getProperty("errorHandler",
                    ErrorHandler.class.getCanonicalName()));
            errorHandler = (ErrorHandler) errorHandlerClass.newInstance();
        } catch (Exception ex) {
            logger.warn("errorHandler load", ex);
        } finally {
            logger.info("errorHandler: " + errorHandler);
        }
        return configReloader.reload(p, logger);
    }
}
