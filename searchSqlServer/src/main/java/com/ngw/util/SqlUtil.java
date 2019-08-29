package com.ngw.util;

import com.alibaba.fastjson.JSONObject;
import com.ngw.common.Constants;
import jodd.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.query.SqlElasticRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static jodd.util.StringPool.EMPTY;

/**
 * Created by zy-xx on 2019/8/22.
 */
public class SqlUtil {
    private static Logger logger = LoggerFactory.getLogger(SqlUtil.class);

    private static RequestConfig requestConfig;
    private static int MAX_TIMEOUT = 7000;
    static {
        RequestConfig.Builder configBulder = RequestConfig.custom();
        //设置连接超时
        configBulder.setConnectTimeout(MAX_TIMEOUT);
        //设置读取超时
        configBulder.setSocketTimeout(MAX_TIMEOUT);
        //设置从连接池获取连接实例超时
        configBulder.setConnectionRequestTimeout(MAX_TIMEOUT);
        requestConfig = configBulder.build();
    }

    public static String indexsJoin(String[] indexs,String separator){
        if (indexs == null) {
            return EMPTY;
        }
        StringBuilder buf = new StringBuilder();
        if (separator == null) {
            separator = EMPTY;
        }
        for (Object o : indexs) {
            if (buf.length() > 0) {
                buf.append(separator);
            }
            buf.append(o);
        }
        return buf.toString();
    }

    /**
     * 创建请求客户端
     * @param url
     * @return
     */
    private static CloseableHttpClient getClient(String url){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        if(url.startsWith("https://")){
            return createSSLClient();
        }
        return httpClient;
    }

    /**
     * 创建HTTPS请求客户端
     * @return
     */
    private static CloseableHttpClient createSSLClient(){
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        }catch (KeyManagementException e){
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch (KeyStoreException e){
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    /**
     * 发送post请求-json数据方式提交
     * @param url  请求url
     * @param headers  请求头
     * @param body  请求参数
     * @param charset  字符编码
     * @return
     */
    public static String post(String url, Map<String,String> headers, String body,String charset){
        logger.debug("请求地址:"+url);
        logger.debug("请求方式:POST");
        logger.debug("请求编码:"+charset);
        logger.debug("请求时间:"+new Date());
        logger.debug("请求头:"+headers);
        logger.debug("请求参数:"+body);

        String resultStr = null;
        //创建HttpClient对象
        CloseableHttpClient httpClient = getClient(url);
        //创建请求方法的实例，并填充url
        HttpPost request = new HttpPost(url);
        if(null != headers && !headers.isEmpty()){
            for(Map.Entry<String,String> header : headers.entrySet()){
                request.addHeader(header.getKey(),header.getValue());
            }
        }
        request.setConfig(requestConfig);
        try {
            //设置参数队列实体
            if(StringUtil.isNotBlank(body)){
                StringEntity entity = new StringEntity(body,charset);
                entity.setContentEncoding(charset);
                entity.setContentType("application/json");
                request.setEntity(entity);
            }
            //发送请求
            CloseableHttpResponse response = httpClient.execute(request);
            //获取响应头，内容
            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("请求状态码："+statusCode);
            HttpEntity entity = response.getEntity();
            resultStr = IOUtils.toString(entity.getContent(), Constants.CHARSET_UTF8);
            logger.debug("请求结果:"+resultStr);
            response.close();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                httpClient.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return resultStr;
    }
}
