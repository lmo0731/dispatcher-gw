/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.lib.handler.DeleteHandler;
import lmo.gw.lib.handler.GetHandler;
import lmo.gw.lib.handler.PostHandler;
import lmo.gw.lib.handler.PutHandler;
import org.apache.log4j.Logger;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public abstract class Function extends HttpServlet {

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
        while (ConfigReloader.isLoading) {
            synchronized (ConfigReloader.lock) {
                try {
                    System.out.println("Config Loading.... WAITING");
                    ConfigReloader.lock.wait(10);
                    System.out.println("Config Loaded");
                } catch (InterruptedException ex) {
                }
            }
        }
        String REQID = (String) req.getAttribute(Attribute.REQID);
        String funcname = (String) req.getAttribute(Attribute.FUNCNAME);
        Logger logger = Logger.getLogger(funcname + "." + REQID);
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
        boolean begin = false;
        try {
            resp.setCharacterEncoding("UTF-8");
            if (req.getDispatcherType() != DispatcherType.FORWARD && req.getDispatcherType() != DispatcherType.INCLUDE) {
                throw new FunctionException(HttpServletResponse.SC_FORBIDDEN, "Not gateway request.");
            }
            logger.info("request method: " + req.getMethod());
            logger.info("request query: " + req.getQueryString());
            if (handler == null) {
                throw new FunctionException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not allowed.");
            }
            String contentType = null;
            if (req.getContentType() != null) {
                contentType = req.getContentType().split("[;]")[0];
                logger.info("content type: " + contentType);
            }
            ContentBinder binder = (ContentBinder) handler.binders.get(contentType);
            logger.info("binder: " + binder);
            if ((req.getMethod().equals("POST") || req.getMethod().equals("PUT")) && binder == null) {
                String accept = handler.binders.keySet().toString();
                resp.setHeader("Accept", accept.substring(1, accept.length() - 1));
                throw new FunctionException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, contentType + " is not supported");
            }
            Object reqObj = null;
            if (binder != null) {
                try {
                    reqObj = binder.deserialize(req.getInputStream(), handler.target);
                } catch (ContentBindException ex) {
                    logger.warn("Deserializing object", ex);
                    throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage(), ex);
                }
            }
            FunctionRequest funcReq = handler.getRequest(logger, funcname, reqObj, req);
            FunctionResponse funcRes = new FunctionResponse(resp);
            handler.handle(funcReq, funcRes);
            resp.setStatus(funcRes.getCode());
            if (binder != null) {
                resp.setContentType(binder.getContentType());
                Object resObj = funcRes.getResponseObject();
                try {
                    binder.serialize(resObj, resp.getOutputStream());
                } catch (ContentBindException ex) {
                    logger.warn("Serializing object", ex);
                    throw new FunctionException(HttpServletResponse.SC_NO_CONTENT, "");
                }
            } else {
                throw new FunctionException(HttpServletResponse.SC_NO_CONTENT, "");
            }
        } catch (FunctionException ex) {
            resp.setContentType("text/plain");
            if (ex.getCause() != null) {
                logger.debug("Function error cause", ex);
            }
            resp.setStatus(ex.getCode());
            resp.getWriter().append(ex.getMessage());
        } catch (Exception ex) {
            resp.setContentType("text/plain");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("Internal error.", ex);
            resp.getWriter().append("Internal error.");
        } finally {
            try {
                resp.getWriter().flush();
            } catch (Exception ex) {
            }
            try {
                resp.getWriter().close();
            } catch (Exception ex) {
            }
            logger.info("Headers: " + resp.getHeaderNames());
            logger.info("Charset: " + resp.getCharacterEncoding());
            logger.info("Content-Type: " + resp.getContentType());
            logger.info("DONE: " + resp.getStatus());
        }
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
