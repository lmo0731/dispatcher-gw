/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import flexjson.JSONException;
import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
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
public abstract class Function extends HttpServlet {

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String TEXT_PLAIN = "text/plain";
    Logger logger;
    String name = "Function";
    ConfigReloader mbean;

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
                .replaceAll("^[.]+", "");
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
            try {
                ConfigReloader.lock.wait(500);
            } catch (InterruptedException ex) {
            }
        }
        String REQID = (String) req.getAttribute(Attribute.REQID);
        String funcname = (String) req.getAttribute(Attribute.FUNCNAME);
        ArrayList<String> PATHPARARMS = (ArrayList<String>) req.getAttribute(Attribute.PATHPARAMS);
        Logger logger = Logger.getLogger(funcname + "." + REQID);
        Object responseObject = null;
        String response;
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
            if (req.getDispatcherType() != DispatcherType.FORWARD && req.getDispatcherType() != DispatcherType.INCLUDE) {
                throw new FunctionException(HttpServletResponse.SC_FORBIDDEN, "not gateway request");
            }
            resp.setCharacterEncoding(req.getCharacterEncoding());
            String requestString = (String) req.getAttribute(Attribute.REQUEST);
            logger.info("request received: " + requestString);
            Object o = null;
            if (handler == null) {
                throw new FunctionException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "method not allowed");
            }
            if (req.getContentType() == null || req.getContentType().isEmpty()) {
                xml = false;
                resp.setContentType(APPLICATION_JSON);
            } else if (req.getContentType().toLowerCase().contains(APPLICATION_XML.toLowerCase())) {
                xml = true;
                resp.setContentType(APPLICATION_XML);
            } else if (req.getContentType().toLowerCase().contains(APPLICATION_JSON.toLowerCase())) {
                xml = false;
                resp.setContentType(APPLICATION_JSON);
            } else {
                resp.setHeader("Accept", APPLICATION_JSON + ", " + APPLICATION_XML);
                throw new FunctionException(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not acceptable Content-Type");
            }
            if (requestString.isEmpty()) {
                o = null;
            } else {
                logger.info("Content-Type: " + req.getContentType());
                if (xml) {
                    try {
                        o = XmlUtil.unmarshal(requestString, handler.target.getClass());
                    } catch (Exception ex) {
                        throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                    }
                } else {
                    try {
                        if (handler.target != null && handler.target instanceof Collection) {
                            handler.deserializer.use("values", handler.target.getClass().getComponentType());
                        }
                        if (handler.target != null && handler.target.getClass() != Object.class) {
                            o = handler.deserializer.deserializeInto(requestString, handler.target);
                        } else {
                            o = handler.deserializer.deserialize(requestString);
                        }
                    } catch (Exception ex) {
                        throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                    }
                }
            }
            if (handler.target != null && handler.target.getClass().isAnnotationPresent(BSONNotNull.class) && o == null) {
                throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, "request must not be null");
            }
            FunctionRequest funcReq = handler.getRequest(logger, funcname, o, req.getParameterMap());
            funcReq.setRequestId(REQID);
            funcReq.setPathParams(PATHPARARMS);
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
                response = XmlUtil.marshal(responseObject);
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
