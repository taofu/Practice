package com.practice.justkeep.logger;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * adb shell
 * setprop log.tag {@link #PRIVATE_TAG} LOG_LEVEL
 * Created by taofu on 2015/1/8.
 */
public class Logger {


    private static final String PREFIX = "at %s.%s(%s:%d)";
    private static final String CACHE_BUG_FILE_NAME = "861d43d7-db00-49bb-9827-caa1a90ec08c";
    private static final StringBuffer mMessageBuffer = new StringBuffer();
    private static String PRIVATE_TAG = "Logger";
    private static String PUBLIC_TAG = "Logger";
    public static boolean DEBUG = true;
    private static String mBugReportUrl;
    private static String mCacheBugFileDir;


    /**
     * @param privateTag coder debug log tag
     * @param publicTag  expose to developer debug log tag
     */
    public static void setTag(String publicTag) {
        PUBLIC_TAG = publicTag;
    }

    /**
     * 正式发布产品的时候应该关闭debug 模式
     *
     * @param debug 设置Log 模式，如果true 表示debug模式该模式可以从控制台查看相关log信息，否则不是
     */
    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

   /* public static void setAllowReportBug(String url) {
        mBugReportUrl = url;
    }
*/
    public static void configBugReport(String sendTargetUrl,String cacheBugFilePath){
        mBugReportUrl = sendTargetUrl;
        mCacheBugFileDir = cacheBugFilePath;
    }
    /**
     * 级别最低，用于输出任意对调试或者开发过程中有帮助的信息
     *
     * @param format the format string (see {@link java.util.Formatter#format})
     * @param args   the list of arguments passed to the formatter. If there are
     *               more arguments than required by {@code format},
     *               additional arguments are ignored.
     */

    public static synchronized void v(String format, Object... args) {
        if (Log.isLoggable(PRIVATE_TAG, Log.VERBOSE)) {
            Log.v(PRIVATE_TAG, buildPrivateMessage(format, args));
        }
    }

    public static synchronized void v(Throwable throwable, String format, Object... args) {
        if (Log.isLoggable(PRIVATE_TAG, Log.VERBOSE)) {
            Log.v(PRIVATE_TAG, buildPrivateMessage(format, args), throwable);
        }
    }

    /**
     * 调试级别, 比VERBOSE高，用于输出调试信息
     *
     * @param format the format string (see {@link java.util.Formatter#format})
     * @param args   the list of arguments passed to the formatter. If there are
     *               more arguments than required by {@code format},
     *               additional arguments are ignored.
     */
    public static synchronized void d(String format, Object... args) {
        if (Log.isLoggable(PRIVATE_TAG, Log.DEBUG)) {
            Log.d(PRIVATE_TAG, buildPrivateMessage(format, args));
        }
    }

    public static synchronized void d(Throwable throwable, String format, Object... args) {
        if (Log.isLoggable(PRIVATE_TAG, Log.DEBUG)) {
            Log.d(PRIVATE_TAG, buildPrivateMessage(format, args), throwable);
        }
    }

    /**
     * 用于输出比较重要的信息，系统默认的级别，
     *
     * @param format the format string (see {@link java.util.Formatter#format})
     * @param args   the list of arguments passed to the formatter. If there are
     *               more arguments than required by {@code format},
     *               additional arguments are ignored.
     */
    public static void i(String format, Object... args) {
        if (DEBUG) {
            Log.i(PUBLIC_TAG, (args == null) ? format : String.format(Locale.CHINA, format, args));
        }
    }

    public static void i(Throwable throwable, String format, Object... args) {
        if (DEBUG) {
            Log.w(PUBLIC_TAG, (args == null) ? format : String.format(Locale.CHINA, format, args), throwable);
        }
    }

    public static void w(String format, Object... args) {
        Log.w(PUBLIC_TAG, (args == null) ? format : String.format(Locale.CHINA, format, args));

    }

    public static void w(Throwable throwable, String format, Object... args) {
        Log.w(PUBLIC_TAG, (args == null) ? format : String.format(Locale.CHINA, format, args), throwable);
    }

    public static void e(String format, Object... args) {
        Log.e(PUBLIC_TAG, (args == null) ? format : String.format(Locale.CHINA, format, args));
    }

    public static void e(Throwable throwable, String format, Object... args) {
        Log.e(PUBLIC_TAG, (args == null) ? format : String.format(Locale.CHINA, format, args), throwable);
    }

    private static String getStackTrace() {
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[5];
        return String.format(PREFIX, traceElement.getClassName(),
                traceElement.getMethodName(), traceElement.getFileName(), traceElement.getLineNumber());
    }


    private static String buildPrivateMessage(String format, Object... args) {
        mMessageBuffer.delete(0, mMessageBuffer.length());
        mMessageBuffer.append((args == null) ? format : String.format(Locale.CHINA, format, args));
        mMessageBuffer.append("\n");
        mMessageBuffer.append("        ").append(getStackTrace());
        return mMessageBuffer.toString();
    }


    public static void sendBugReport(final BugReport bugReport) {
        if (TextUtils.isEmpty(mBugReportUrl))
            throw new NullPointerException("bug report url is null, " +
                    "you need invoke method setAllowReportBug(String url) to set url before call this method");


        new AsyncTask<BugReport, Void, Void>() {
            @Override
            protected Void doInBackground(BugReport... params) {
                BufferedInputStream in = null;
                HttpStack httpStack = null;
                byte body[] = null;
                ArrayList<byte[]> datas = null;

                datas = readbug();

                BugReport report = params[0];


                if (report.getError() == null && datas == null || datas.size() == 0) {
                    i("send bug report fail no throwable object ");
                    return null;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    httpStack = new HttpUrlStack();
                } else {
                    httpStack = new HttpClientStack();
                }
                Map<String, String> additionalParams = new HashMap<>();
                if (report.getAdditionalParams() != null) {
                    additionalParams.putAll(report.getAdditionalParams());
                }
                additionalParams.put(report.getBugKey(), Log.getStackTraceString(report.getError()));
                try {
                    body = HttpStack.getBody(additionalParams);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (datas == null) {
                    datas = new ArrayList<>();
                }
                datas.add(body);

                for (byte[] data : datas) {
                    try {
                        HttpResponse httpResponse = httpStack.performRequest(data);
                        StatusLine statusLine = httpResponse.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        if (statusCode < 200 || statusCode > 299) {
                            i("send bug report fail status code = %d ", statusCode);
                        } else {
                            i("send bug report success status code = %d ", statusCode);
                        }
                    } catch (Exception e) {
                        i(e, "send bug report fail ");
                        cacheBug(body);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                            }
                        }
                        if (httpStack != null) {
                            httpStack.disconnect();
                        }
                    }
                }

                return null;
            }
        }.execute(bugReport);
    }


   private static void report(byte [] data){}


    public static abstract class BugReport{
        private Throwable error;
        public BugReport(Throwable throwable){
            error = throwable;
        }
        public Throwable getError(){
            return error;
        }

        /**
         * 返回额外需要的信息，比如报名，版本号，等等
         * @return
         */
        abstract Map<String,String> getAdditionalParams();

        /**
         * 返回 用post 方式提交throwable 对应的key ，value 这是 throwable 信息
         * @return
         */
        abstract String getBugKey();

    }

    private static abstract class HttpStack{

        abstract HttpResponse performRequest(byte[] body) throws IOException;
        abstract void disconnect();

        private static byte[] getBody(Map<String, String> params ) throws UnsupportedEncodingException {
            if (params != null && params.size() > 0) {
                return encodeParameters(params, "utf-8");
            }
            return null;
        }

        private static byte[] encodeParameters(Map<String, String> params, String paramsEncoding) throws UnsupportedEncodingException {
            StringBuilder encodedParams = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            encodedParams.delete(encodedParams.length() -1,encodedParams.length());
            return encodedParams.toString().getBytes(paramsEncoding);
        }
    }

    private static class HttpUrlStack extends HttpStack{
        private HttpURLConnection connection;
        @Override
        public HttpResponse performRequest(byte[] body) throws IOException {
            HttpResponse response;
            connection = openConnection(body);
            ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
            int responseCode = connection.getResponseCode();
            if (responseCode == -1) {
                throw new IOException("Could not retrieve response code from HttpUrlConnection.");
            }
            StatusLine responseStatusLine = new BasicStatusLine(protocolVersion, responseCode,
                    connection.getResponseMessage());
            response = new BasicHttpResponse(responseStatusLine);
            response.setEntity(entityFromConnection(connection));
            for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                if (header.getKey() != null) {
                    Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
                    response.addHeader(h);
                }
            }
            return response;
        }
        private  HttpEntity entityFromConnection(HttpURLConnection connection) {
            BasicHttpEntity entity = new BasicHttpEntity();
            InputStream inputStream;
            try {
                inputStream = connection.getInputStream();
            } catch (IOException ioe) {
                inputStream = connection.getErrorStream();
            }
            entity.setContent(inputStream);
            entity.setContentLength(connection.getContentLength());
            entity.setContentEncoding(connection.getContentEncoding());
            entity.setContentType(connection.getContentType());
            return entity;
        }
        private  HttpURLConnection openConnection(byte[] body) throws IOException {
            URL parseUrl = new URL(mBugReportUrl);
            HttpURLConnection connection = (HttpURLConnection) parseUrl.openConnection();
            connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.addRequestProperty("Connection", "close");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            addBody(connection, body);
            return connection;
        }
        private  void addBody(HttpURLConnection connection, byte[] body) throws IOException {
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
        @Override
        public void disconnect() {
            if(connection != null){
                connection.disconnect();
                connection = null;
            }
        }
    }

    private static class HttpClientStack extends  HttpStack{

        private AndroidHttpClient client;
        @Override
        public HttpResponse performRequest(byte[] body) throws IOException {
            HttpUriRequest httpRequest = createHttpUriPostRequest(body);
            client = AndroidHttpClient.newInstance("loggerbugreport");
            return client.execute(httpRequest);
        }
        private  HttpUriRequest createHttpUriPostRequest(byte[] body) throws IOException {
            HttpPost postRequest = new HttpPost(mBugReportUrl);
            postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            postRequest.setHeader("Connection", "close");

            HttpParams httpParams = postRequest.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            HttpConnectionParams.setSoTimeout(httpParams, 30000);
            HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(httpParams, "utf-8");
            addBody(postRequest, body);
            return postRequest;
        }

        private  void addBody(HttpEntityEnclosingRequestBase httpRequest, byte[] body) throws IOException {
            HttpEntity entity = new ByteArrayEntity(body);
            httpRequest.setEntity(entity);
        }

        @Override
        public void disconnect() {
            if (client != null) {
                client.getConnectionManager().shutdown();
                client.close();
                client = null;
            }
        }
    }


    private static void cacheBug(byte [] data){
        File dir = new File(mCacheBugFileDir);

        if(!dir.exists()){
            dir.mkdirs();
        }


        BufferedWriter bufferedWriter = null;
        try {
            File cacheFile = new File(dir,CACHE_BUG_FILE_NAME);
            FileWriter writer = new FileWriter(cacheFile,true);
            bufferedWriter = new BufferedWriter(writer);
            String content =  Base64.encodeToString(data,Base64.DEFAULT);
            bufferedWriter.write(content);
            bufferedWriter.newLine();

            bufferedWriter.close();

        } catch (Exception e) {
            i(e,"cache bug fail ");
        }finally {
            if(bufferedWriter != null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static ArrayList<byte []> readbug(){
        File dir = new File(mCacheBugFileDir);

        File cacheFile = new File(dir,CACHE_BUG_FILE_NAME);
        if(!cacheFile.exists()){
            return null;
        }
        BufferedReader bufferedReader = null;
        try {
            FileReader reader = new FileReader(cacheFile);
            bufferedReader = new BufferedReader(reader);
            String content = null;
            ArrayList<byte []> datas = new ArrayList<>();
            while (!TextUtils.isEmpty((content = bufferedReader.readLine()))){
                datas.add(Base64.decode(content,Base64.DEFAULT));
            }

            bufferedReader.close();
            return datas;
        } catch (Exception e) {
            i(e,"read cache bug fail ");
        }finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

}
