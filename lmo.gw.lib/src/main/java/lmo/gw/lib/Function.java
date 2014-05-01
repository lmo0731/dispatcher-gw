/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import flexjson.JSONException;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
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
import lmo.utils.jaxb.XmlUtil;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public abstract class Function extends HttpServlet {

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
        Logger logger = Logger.getLogger(this.logger.getName() + "." + REQID);
        Object responseObject = null;
        boolean xml = false;
        Handler handler = null;
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
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
            String requestString = (String) req.getAttribute(Attribute.REQUEST);
            logger.info("request received: " + requestString);
            Object o = null;
            if (handler == null) {
                throw new FunctionException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "method not allowed");
            }
            if (requestString.isEmpty()) {
                o = null;
            } else {
                logger.info("Content-Type: " + req.getContentType());
                if (req.getContentType().toLowerCase().contains("application/xml")) {
                    xml = true;
                }
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
            FunctionRequest funcReq = handler.getRequest(logger, o, req.getParameterMap());
            funcReq.setRequestId(REQID);
            Enumeration<String> headers = req.getHeaderNames();
            while (headers.hasMoreElements()) {
                String header = headers.nextElement();
                funcReq.getHeaders().put(header, req.getHeaders(header));
            }
            FunctionResponse funcRes = new FunctionResponse();
            handler.handle(funcReq, funcRes);
            resp.setStatus(funcRes.getCode());
            responseObject = funcRes.getResponseObject();
        } catch (JSONException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseObject = "Bad request";
        } catch (FunctionException ex) {
            if (ex.getCause() != null) {
                logger.debug("Function error cause", ex);
            }
            resp.setStatus(ex.getCode());
            responseObject = ex.getMessage();
        } catch (Exception ex) {
            logger.error("Internal error", ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseObject = "internal error";
        }
        String response = null;
        if (xml) {
            try {
                response = XmlUtil.marshal(responseObject);
                resp.setContentType("application/xml;charset=UTF-8");
            } catch (Exception ex) {
                logger.error("marshaling response", ex);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response = "internal error";
            }
        } else {
            response = handler.serializer.serialize(responseObject);
            resp.setContentType("application/json;charset=UTF-8");
        }
        logger.info("response: " + response);
        resp.getWriter().write(response);
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
