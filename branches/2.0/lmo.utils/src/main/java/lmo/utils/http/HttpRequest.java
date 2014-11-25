/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;
import lmo.utils.bson.BSONSerializer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class HttpRequest implements Closeable {

    InputStream body;
    HttpURLConnection con;
    Logger logger;
    boolean secure = false;

    static {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            // Install the all-trusting trust manager
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            });
        } catch (Exception ex) {
        }
    }

    public HttpRequest(String method, String url, int conTimeout, int readTimeout) throws IOException {
        this(method, url);
        this.con.setReadTimeout(readTimeout);
        this.con.setConnectTimeout(conTimeout);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public HttpRequest(String method, String url) throws IOException {
        URL _url = new URL(url);
        if (url.startsWith("https")) {
            this.con = (HttpsURLConnection) _url.openConnection();
        } else {
            this.con = (HttpURLConnection) _url.openConnection();
        }
        this.con.setDoInput(true);
        this.con.setDoOutput(true);
        this.con.setInstanceFollowRedirects(false);
        this.con.setRequestMethod(method);
        this.con.setUseCaches(false);
        this.con.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
    }

    public void setSSLSocketFactory(SSLSocketFactory sslsf) {
        if (this.con != null && this.con instanceof HttpsURLConnection) {
            ((HttpsURLConnection) this.con).setSSLSocketFactory(sslsf);
        }
    }

    public void setHostnameVerifier(HostnameVerifier verifier) {
        if (this.con != null && this.con instanceof HttpsURLConnection) {
            ((HttpsURLConnection) this.con).setHostnameVerifier(verifier);
        }
    }

    public void addHeader(String name, String value) {
        con.addRequestProperty(name, value);
    }

    public void setHeader(String name, String value) {
        con.setRequestProperty(name, value);
    }

    public void setBody(byte[] body) {
        this.body = new ByteArrayInputStream(body);
    }

    public void setBody(InputStream in) {
        this.body = in;
    }

    public void setJson(Object o) throws Exception {
        BSONSerializer serializer = new BSONSerializer();
        this.setBody(serializer.serialize(o).getBytes("UTF-8"));
        this.setHeader("Content-Type", "application/json;charset=UTF-8");
    }

    public void setForm(Map<String, String> form) throws Exception {
        this.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        StringBuilder sb = new StringBuilder();
        for (String k : form.keySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(k, "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(form.get(k), "UTF-8"));
        }
    }

    public void setBasicAuthentication(String username, String password) {
        String auth = username + ":" + password;
        this.setHeader("Authorization",
                "Basic " + DatatypeConverter.printBase64Binary(auth.getBytes()));
    }

    public HttpResponse execute() throws IOException {
        return this.execute(null);
    }

    public HttpResponse execute(final HttpListener l) throws IOException {
        if (logger != null) {
            logger.info(con.getRequestMethod() + " " + con.getURL().toString());
        }
        if (this.body != null) {
            OutputStream out = this.con.getOutputStream();
            while (true) {
                int b = this.body.read();
                if (b == -1) {
                    break;
                }
                out.write(b);
            }
            out.flush();
            out.close();
        }
        final HttpResponse response = new HttpResponse();
        try {
            this.con.connect();
            response.status = this.con.getResponseCode();
            if (logger != null) {
                for (String key : this.con.getHeaderFields().keySet()) {
                    logger.debug(key + ": " + this.con.getHeaderField(key));
                }
            }
            response.headers.putAll(this.con.getHeaderFields());

            InputStream in;
            if (response.status >= 400) {
                in = this.con.getErrorStream();
            } else {
                in = this.con.getInputStream();
            }
            if ("gzip".equals(this.con.getHeaderField("Content-Encoding"))) {
                in = new GZIPInputStream(in);
            }
            if ("deflate".equals(this.con.getHeaderField("Content-Encoding"))) {
                in = new DeflaterInputStream(in);
            }
            final HttpListener listener = new HttpListener() {

                public void onRead(int b) {
                    if (l != null) {
                        l.onRead(b);
                    }
                }

                public void onClose() {
                    con.disconnect();
                    if (l != null) {
                        l.onClose();
                    }
                }
            };
            response.listener = listener;
            response.in = in;
        } catch (IOException ex) {
            this.con.disconnect();
            throw ex;
        }
        return response;
    }

    public void close() {
        try {
            con.disconnect();
        } catch (Exception ex) {
        }
    }

    public static void main(String args[]) throws Exception {
        BasicConfigurator.configure();
        String url = "http://web2.0calc.com/calc";
        HttpRequest request = new HttpRequest("POST", url);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        request.setHeader("Host", "web2.0calc.com");
        //request.setHeader("accept-encoding", "deflare");
        request.setLogger(Logger.getRootLogger());
        String body = URLEncoder.encode("in[]", "UTF-8")
                + "=" + URLEncoder.encode("1+2", "UTF-8")
                + "&trig=rad&p=0&s=0";
        request.setBody(body.getBytes("UTF-8"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HttpResponse response = request.execute(new HttpListener() {

            public void onRead(int b) {
                System.out.print((char) b);
            }

            public void onClose() {
                System.out.println();
            }
        });
        System.out.println(response.getStatusLine());
        System.out.println(response.getFirstHeader("content-type"));
        System.out.println("1 " + new String(response.getBody(), "UTF-8"));
        request.close();
    }
}
