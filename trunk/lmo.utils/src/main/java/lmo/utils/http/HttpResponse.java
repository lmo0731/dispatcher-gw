/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class HttpResponse {

    Map<String, List<String>> headers = new HashMap<String, List<String>>();
    byte[] body;
    int status;

    HttpResponse() {
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public List<String> getHeader(String name) {
        if (headers == null) {
            return null;
        }
        return headers.get(name);
    }

    public String getFirstHeader(String name) {
        try {
            return headers.get(name).get(0);
        } catch (Exception ex) {
            return null;
        }
    }

    public String getStatusLine() {
        try {
            return headers.get(null).get(0);
        } catch (Exception ex) {
            return null;
        }
    }

    public int getStatus() {
        return status;
    }

    public byte[] getBody() {
        return body;
    }
}
