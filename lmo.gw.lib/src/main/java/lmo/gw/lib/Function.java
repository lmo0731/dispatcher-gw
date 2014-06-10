/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import flexjson.JSONException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.servlet.DispatcherType;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.lib.handler.DeleteHandler;
import lmo.gw.lib.handler.GetHandler;
import lmo.gw.lib.handler.PostHandler;
import lmo.gw.lib.handler.PutHandler;
import lmo.utils.bson.BSONNotNull;
import lmo.utils.bson.BSONSerializer;
import lmo.utils.jaxb.XmlUtil;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public abstract class Function extends HttpServlet implements ConfigListener {

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String TEXT_PLAIN = "text/plain";
    Logger logger;
    String name = "Function";
    ConfigReloader mbean;

    public String getName() {
        return name;
    }

    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config); //To change body of generated methods, choose Tools | Templates.
        String context = config.getServletContext().getContextPath().substring(1);
        String servletName;
        if (this.getClass().isAnnotationPresent(WebServlet.class)) {
            WebServlet servlet = this.getClass().getAnnotation(WebServlet.class);
            if (servlet.urlPatterns() == null || servlet.urlPatterns().length == 0) {
                servletName = config.getServletName();
            } else {
                servletName = servlet.urlPatterns()[0];
            }
        } else {
            servletName = config.getServletName();
        }
        String name = (context + "/" + servletName)
                .replaceAll("^[/]+", "")
                .replaceAll("[/]+$", "")
                .replaceAll("[.]+$", "")
                .replaceAll("^[.]+", "")
                .replaceAll("[.]+", ".")
                .replaceAll("[/]+", "/");
        this.name = name;
        this.logger = Logger.getLogger("FUNC." + name.toUpperCase());
        mbean = new ConfigReloader(this);
        mbean.register();
        try {
            this.logger.info(mbean.reload());
        } catch (Exception ex) {
            this.logger.error("reloading config", ex);
        }
    }

    public Function() {
    }

    @Override
    public void destroy() {
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
        destroy(logger);
        mbean.unregister();
    }

    public void init(Properties p) throws Exception {
        this.init(logger, p);
    }

    protected void begin(Map<String, Object> params, HttpServletRequest request, String raw, Logger logger) {
    }

    protected void end(Map<String, Object> params, HttpServletResponse response, String raw, Logger logger) {
    }

    protected abstract void init(Logger logger, Properties p) throws ServletException;

    protected abstract void destroy(Logger logger);

    protected GetHandler get() {
        return null;
    }

    protected PostHandler post() {
        return null;
    }

    protected PutHandler put() {
        return null;
    }

    protected DeleteHandler delete() {
        return null;
    }

    protected final void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (ConfigReloader.isLoading) {
            synchronized (ConfigReloader.lock) {
                try {
                    ConfigReloader.lock.wait();
                } catch (InterruptedException ex) {
                }
            }
        }
        String REQID = (String) req.getAttribute(Attribute.REQID);
        String funcname = (String) req.getAttribute(Attribute.FUNCNAME);
        String request = (String) req.getAttribute(Attribute.REQUEST);
        String response = null;
        ArrayList<String> PATHPARARMS = (ArrayList<String>) req.getAttribute(Attribute.PATHPARAMS);
        Map<String, Object> ATTRS = new HashMap<String, Object>();
        Map<String, String[]> QUERY = new HashMap<String, String[]>();
        for (String key : req.getParameterMap().keySet()) {
            key = key.toLowerCase();
            String[] values2 = QUERY.get(key);
            if (QUERY.containsKey(key)) {
                String[] values1 = QUERY.get(key);
                String[] merge = new String[values1.length + values2.length];
                System.arraycopy(values1, 0, merge, 0, values1.length);
                System.arraycopy(values2, 0, merge, values1.length, values2.length);
                QUERY.put(key, merge);
            } else {
                QUERY.put(key, values2);
            }
        }
        Enumeration<String> reqAttributes = req.getAttributeNames();
        while (reqAttributes.hasMoreElements()) {
            String attribute = reqAttributes.nextElement();
            ATTRS.put(attribute, req.getAttribute(attribute));
        }
        Logger logger = Logger.getLogger(funcname + "." + REQID);
        Object responseObject = null;
        boolean xml = false;
        Handler handler = null;
        Map<String, String> resHeaders = null;
        if (req.getMethod().equals("GET")) {
            handler = this.get();
        } else if (req.getMethod().equals("POST")) {
            handler = this.post();
        } else if (req.getMethod().equals("PUT")) {
            handler = this.put();
        } else if (req.getMethod().equals("DELETE")) {
            handler = this.delete();
        }
        try {
            begin(ATTRS, req, request, logger);
            if (req.getDispatcherType() != DispatcherType.FORWARD && req.getDispatcherType() != DispatcherType.INCLUDE) {
                throw new FunctionException(HttpServletResponse.SC_FORBIDDEN, "not gateway request");
            }
            resp.setCharacterEncoding(req.getCharacterEncoding());
            logger.info("request method: " + req.getMethod());
            logger.info("request query: " + req.getQueryString());
            logger.info("request received: " + request);
            Object o = null;
            if (handler == null) {
                throw new FunctionException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "method not allowed");
            }
            handler.request = req;
            handler.response = resp;
            if (req.getContentType() == null || req.getContentType().isEmpty()) {
                xml = false;
                resp.setContentType(APPLICATION_JSON);
            } else if (req.getContentType().toLowerCase().contains(APPLICATION_XML.toLowerCase())) {
                xml = true;
                resp.setContentType(APPLICATION_XML);
            } else if (req.getContentType().toLowerCase().contains(APPLICATION_JSON.toLowerCase())) {
                xml = false;
                resp.setContentType(APPLICATION_JSON);
            } else if (req.getContentType().toLowerCase().contains(X_WWW_FORM_URLENCODED.toLowerCase())) {
                if (request != null) {
                    String[] pairs = request.split("&");
                    Map<String, List<String>> queryMap = new HashMap<String, List<String>>();
                    for (String pair : pairs) {
                        String[] kv = pair.split("=", 2);
                        if (kv.length == 2) {
                            try {
                                String key = URLDecoder.decode(kv[0], "UTF-8");
                                String value = URLDecoder.decode(kv[1], "UTF-8");
                                if (queryMap.containsKey(key)) {
                                    queryMap.get(key).add(value);
                                } else {
                                    queryMap.put(key, new ArrayList<String>(Arrays.asList(new String[]{value})));
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }
                    for (String key : queryMap.keySet()) {
                        String[] values2 = queryMap.get(key).toArray(new String[]{});
                        if (QUERY.containsKey(key)) {
                            key = key.toLowerCase();
                            String[] values1 = QUERY.get(key);
                            String[] merge = new String[values1.length + values2.length];
                            System.arraycopy(values1, 0, merge, 0, values1.length);
                            System.arraycopy(values2, 0, merge, values1.length, values2.length);
                            QUERY.put(key, merge);
                        } else {
                            QUERY.put(key, values2);
                        }
                    }
                }
                xml = false;
                resp.setContentType(APPLICATION_JSON);
            } else {
                resp.setHeader("Accept", APPLICATION_JSON + ", " + APPLICATION_XML);
                throw new FunctionException(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not acceptable Content-Type: " + req.getContentType());
            }

            if (request.isEmpty()) {
                o = null;
            } else {
                logger.info("Content-Type: " + req.getContentType());
                if (xml) {
                    try {
                        o = XmlUtil.unmarshal(request, handler.target.getClass());
                    } catch (Exception ex) {
                        logger.warn("XML unmarhsalling", ex);
                        throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                    }
                } else {
                    try {
                        if (handler.target != null && handler.target instanceof Collection) {
                            handler.deserializer.use("values", handler.target.getClass().getComponentType());
                        }
                        if (handler.target != null && handler.target.getClass() != Object.class) {
                            o = handler.deserializer.deserializeInto(request, handler.target);
                        } else {
                            o = handler.deserializer.deserialize(request);
                        }
                    } catch (Exception ex) {
                        logger.warn("JSON unmarhsalling", ex);
                        throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                    }
                }
            }
            if (handler.target != null && handler.target.getClass().isAnnotationPresent(BSONNotNull.class) && o == null) {
                throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, "request must not be null");
            }
            FunctionRequest funcReq = handler.getRequest(logger, funcname, o, QUERY);
            funcReq.setRequestId(REQID);
            funcReq.setPathParams(PATHPARARMS);
            funcReq.getAttributes().putAll(ATTRS);
            Enumeration<String> reqHeaders = req.getHeaderNames();
            while (reqHeaders.hasMoreElements()) {
                String header = reqHeaders.nextElement();
                funcReq.getHeaders().put(header, req.getHeaders(header));
            }
            FunctionResponse funcRes = new FunctionResponse();
            handler.handle(funcReq, funcRes);
            resp.setStatus(funcRes.getCode());
            resHeaders = funcRes.getHeaders();
            responseObject = funcRes.getResponseObject();
            if (xml) {
                if (responseObject != null) {
                    try {
                        response = XmlUtil.marshal(responseObject);
                    } catch (Exception ex) {
                        logger.warn(o, ex);
                        resp.setContentType(TEXT_PLAIN);
                        throw new FunctionException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported Content-Type: " + req.getContentType() + ", Request processed");
                    }
                } else {
                    resp.setContentType(TEXT_PLAIN);
                    response = "";
                }
            } else {
                if (handler != null) {
                    response = handler.serializer.serialize(responseObject);
                } else {
                    response = new BSONSerializer().serialize(responseObject);
                }
            }
        } catch (JSONException ex) {
            resp.setContentType(TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response = "Bad request";
        } catch (FunctionException ex) {
            resp.setContentType(TEXT_PLAIN);
            if (ex.getCause() != null) {
                logger.debug("Code: " + ex.code);
                logger.debug("Function error cause", ex);
            }
            resp.setStatus(ex.getCode());
            resHeaders = ex.getHeaders();
            response = ex.getMessage();
        } catch (Exception ex) {
            resp.setContentType(TEXT_PLAIN);
            logger.error("Internal error", ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response = "Internal error";
        } finally {
            end(ATTRS, resp, response, logger);
        }
        if (resHeaders != null) {
            for (Entry<String, String> e : resHeaders.entrySet()) {
                resp.setHeader(e.getKey(), e.getValue());
            }
        }
        try {
            logger.info("response: " + response);
            resp.getWriter().write(response);
        } catch (Exception ex) {
            logger.warn("sending response", ex);
        }
        logger.info("DONE: " + resp.getStatus());
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
