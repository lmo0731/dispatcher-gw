/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lmo.utils.Pair;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class HttpResponse {

    Map<String, List<String>> headers = new HashMap<String, List<String>>() {
        @Override
        public List<String> put(String key, List<String> value) {
            if (key != null) {
                key = key.toLowerCase();
            }
            return super.put(key, value); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<String> get(Object key) {
            if (key != null && key instanceof String) {
                key = ((String) key).toLowerCase();
            }
            return super.get(key); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean containsKey(Object key) {
            if (key != null && key instanceof String) {
                key = ((String) key).toLowerCase();
            }
            return super.containsKey(key); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<String> remove(Object key) {
            if (key != null && key instanceof String) {
                key = ((String) key).toLowerCase();
            }
            return super.remove(key); //To change body of generated methods, choose Tools | Templates.
        }
    };
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
