/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class HttpRequest {

    byte[] body;
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
        this.body = body;
    }

    public void setBasicAuthentication(String username, String password) {
        String auth = username + ":" + password;
        this.setHeader("Authorization",
                "Basic " + DatatypeConverter.printBase64Binary(auth.getBytes()));
    }

    public HttpResponse execute() throws IOException {
        if (logger != null) {
            logger.info(con.getRequestMethod() + " " + con.getURL().toString());
        }
        if (this.body != null) {
            OutputStream out = this.con.getOutputStream();
            out.write(this.body);
            out.flush();
            out.close();
        }
        HttpResponse response = new HttpResponse();
        try {
            this.con.connect();
            response.status = this.con.getResponseCode();
            InputStream in;
            if (response.status >= 400) {
                in = this.con.getErrorStream();
            } else {
                in = this.con.getInputStream();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (true) {
                int b = in.read();
                if (b == -1) {
                    break;
                }
                baos.write(b);
            }

            if (logger != null) {
                logger.info(this.con.getHeaderField(null));
                for (String key : this.con.getHeaderFields().keySet()) {
                    if (key != null && key.toLowerCase().startsWith("x-")) {
                        logger.info(key + ": " + this.con.getHeaderField(key));
                    }
                }
            }
            response.headers.putAll(this.con.getHeaderFields());
            response.body = baos.toByteArray();
        } finally {
            this.con.disconnect();
        }
        return response;
    }

    public static void main(String args[]) throws IOException {
        BasicConfigurator.configure();
        String url = "https://www.google.com";
        HttpRequest request = new HttpRequest("POST", url);
        request.setLogger(Logger.getRootLogger());
        HttpResponse response = request.execute();
        System.out.println(response.getStatusLine());
        System.out.println(response.getHeaders().keySet());
        System.out.println(response.getFirstHeader("content-type"));
        System.out.println(new String(response.getBody(), "UTF-8"));
    }
}
