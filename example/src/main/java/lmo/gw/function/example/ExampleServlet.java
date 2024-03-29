/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.function.example;

import java.lang.Object;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.function.example.post.PostRequest;
import lmo.gw.function.example.post.PostResponse;
import lmo.gw.lib.Function;
import lmo.gw.lib.FunctionException;
import lmo.gw.lib.FunctionRequest;
import lmo.gw.lib.FunctionResponse;
import lmo.gw.lib.handler.GetHandler;
import lmo.gw.lib.handler.PostHandler;
import org.apache.log4j.Logger;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
@WebServlet(name = "ExampleServlet", urlPatterns = {"/"})
public class ExampleServlet extends Function {

    @Override
    protected void init(Logger logger, Properties p) throws ServletException {
    }

    @Override
    protected GetHandler get() {
        return new GetHandler() {
            @Override
            public void handle(FunctionRequest<Object> request, FunctionResponse response) throws FunctionException {
                Map<String, Object> res = new HashMap<String, Object>();
                response.setCode(HttpServletResponse.SC_OK);
                response.setResponseObject(request);
            }
        };
    }

    @Override
    protected PostHandler post() {
        return new PostHandler<PostRequest>(new PostRequest()) {
            @Override
            public void handle(FunctionRequest<PostRequest> request, FunctionResponse response) throws FunctionException {
                response.setCode(HttpServletResponse.SC_OK);
                PostResponse resObj = new PostResponse();
                resObj.info = "OK info";
                resObj.map = resObj;
                response.setResponseObject(resObj);
            }
        };
    }

    @Override
    protected void destroy(Logger logger) {
    }
}
