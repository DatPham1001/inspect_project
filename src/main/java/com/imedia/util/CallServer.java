package com.imedia.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CallServer {
    static final Logger logger = LogManager.getLogger(CallServer.class);
    static final Integer TIMEOUT = 60000;
    private static CallServer instance;

    public static CallServer getInstance() throws Exception {
        if (instance == null) {
            synchronized (CallServer.class) {
                if (instance == null) {
                    instance = new CallServer();
                }
            }
        }
        return instance;
    }

    private static CloseableHttpClient createAcceptSelfSignedCertificateClient()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();
        return HttpClients.custom().setSSLSocketFactory(connectionFactory).setDefaultRequestConfig(config).build();
    }

    private static CloseableHttpClient createAcceptSelfSignedCertificateClient(int timeOut)
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeOut)
                .setConnectionRequestTimeout(timeOut)
                .setSocketTimeout(timeOut).build();
        return HttpClients.custom().setSSLSocketFactory(connectionFactory).setDefaultRequestConfig(config).build();
    }

    public String postWithHeaders(String url, HashMap<String, String> headers, String jsonData)
            throws IOException {
        HttpResponse httpresponse = null;
        HttpPost httpPost = new HttpPost(url);
        if (null != headers && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        try (final CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {
            StringEntity data = new StringEntity(jsonData, "utf-8");
            httpPost.setEntity(data);
            logger.info("===POST URL=== " + url);
            logger.info("=======POST DATA======= " + jsonData);
            httpresponse = httpclient.execute(httpPost);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            String response = EntityUtils.toString(httpresponse.getEntity(), "UTF-8");
            logger.info("======Response: http status=======: " + statusCode + " . response body: " + response);
            if (statusCode == HttpStatus.SC_OK) {
                return response;
            } else {
                logger.info("=========ServerResponse HttpCode==========: " + statusCode + ". body: " + response);
            }
        } catch (Exception e) {
            logger.info("==========POST Exception======= :" + jsonData, e);
        }
        return null;
    }

    public String postWithParam(String url)
            throws IOException {
        HttpResponse httpresponse = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        try (final CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {
            httpresponse = httpclient.execute(httpPost);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            String response = EntityUtils.toString(httpresponse.getEntity(), "UTF-8");
            logger.info("======RESPONSE: http status=======: " + statusCode + " . response body: " + response);
            if (statusCode == HttpStatus.SC_OK) {
                return response;
            } else {
                logger.info("=========ServerResponse HttpCode==========: " + statusCode + ". body: " + response);
            }
        } catch (Exception e) {
            logger.info("==========POST Exception======= :", e);
        }
        return null;
    }

    public String post(String url, String jsonData)
            throws IOException {
        HttpResponse httpresponse = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        try (final CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {
            StringEntity data = new StringEntity(jsonData, "utf-8");
            httpPost.setEntity(data);
            logger.info("===POST URL=== " + url);
            logger.info("=======POST DATA======= " + jsonData);
            httpresponse = httpclient.execute(httpPost);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            String response = EntityUtils.toString(httpresponse.getEntity(), "UTF-8");
            logger.info("======RESPONSE: http status=======: " + statusCode + " . response body: " + response);
            if (statusCode == HttpStatus.SC_OK) {
                return response;
            } else {
                logger.info("=========ServerResponse HttpCode==========: " + statusCode + ". body: " + response);
            }
        } catch (Exception e) {
            logger.info("==========POST Exception======= :" + jsonData, e);
        }
        return null;
    }

    public String postUnlimitTimeout(String url, String jsonData)
            throws IOException {
        HttpResponse httpresponse = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        try (final CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient(180000)) {
            StringEntity data = new StringEntity(jsonData, "utf-8");
            httpPost.setEntity(data);
            logger.info("===POST URL=== " + url);
            logger.info("=======POST DATA======= " + jsonData);
            httpresponse = httpclient.execute(httpPost);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            String response = EntityUtils.toString(httpresponse.getEntity(), "UTF-8");
            logger.info("======RESPONSE: http status=======: " + statusCode + " . response body: " + response);
            if (statusCode == HttpStatus.SC_OK) {
                return response;
            } else {
                logger.info("=========ServerResponse HttpCode==========: " + statusCode + ". body: " + response);
            }
        } catch (Exception e) {
            logger.info("==========POST Exception======= :" + jsonData, e);
        }
        return null;
    }

    public String postSOAP(String url, String xmlData, String soapAction) {
        HttpResponse httpresponse = null;
        HttpPost httpPost = new HttpPost(url);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/xml; charset=utf-8");
        headers.put("SOAPAction", soapAction);
        if (headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        try (final CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {
            StringEntity data = new StringEntity(xmlData, "utf-8");
            httpPost.setEntity(data);
            logger.info("===POST URL=== " + url);
            logger.info("======POST SOAP DATA====== :" + xmlData);
            httpresponse = httpclient.execute(httpPost);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            String response = EntityUtils.toString(httpresponse.getEntity(), "UTF-8");
            logger.info("=========Response: http status==========: " + statusCode + " . response body: " + response);
            if (statusCode == HttpStatus.SC_OK) {
                return response;
            } else {
                logger.info("======ServerResponse HttpCode:======= " + statusCode + ". body: " + response);
            }
        } catch (Exception e) {
            logger.info("======POST SOAP Exception===== :" + xmlData, e);
        }
        return null;
    }

    public String get(String url)
            throws IOException {
        HttpResponse httpresponse = null;
        try (final CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {
            HttpGet httpGet = new HttpGet(url);
            httpresponse = httpclient.execute(httpGet);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            logger.info("========GET REQUEST=========" + url);
            String response = EntityUtils.toString(httpresponse.getEntity(), "UTF-8");
            logger.info("========GET RESPONSE=========" + response);
            if (statusCode == HttpStatus.SC_OK) {
                return response;
            } else {
                logger.info("=========ServerResponse HttpCode==========: " + statusCode + ". body: " + response);
            }
        } catch (Exception e) {
            logger.info("==========GET Exception======= :", e);
        }
        return null;
    }

}
