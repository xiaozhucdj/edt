package com.yougy.common.new_network;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Administrator on 2018/7/31.
 */

public class MyHttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    public enum Level {
        /** No logs. */
        NONE,
        /**
         * Logs request and response lines.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    public MyHttpLoggingInterceptor() {
    }

    private volatile HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.NONE;

    /** Change the level at which this interceptor logs. */
    public MyHttpLoggingInterceptor setLevel(HttpLoggingInterceptor.Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public HttpLoggingInterceptor.Level getLevel() {
        return level;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        HttpLoggingInterceptor.Level level = this.level;

        Request request = chain.request();
        if (level == HttpLoggingInterceptor.Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == HttpLoggingInterceptor.Level.BODY;
        boolean logHeaders = logBody || level == HttpLoggingInterceptor.Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        okhttp3.Protocol protocol = connection != null ? connection.protocol() : okhttp3.Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        Log.e("okHttp" , requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    Log.e("okHttp" , "Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    Log.e("okHttp" , "Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    Log.e("okHttp" , name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                Log.e("okHttp" , "--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                Log.e("okHttp" , "--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                Log.e("okHttp" , "");
                if (isPlaintext(buffer)) {
                    Log.e("okHttp" , buffer.readString(charset));
                    Log.e("okHttp" , "--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    Log.e("okHttp" , "--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            Log.e("okHttp" , "<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        Log.e("okHttp" , "<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
                + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                Log.e("okHttp" , headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                Log.e("okHttp" , "<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                Log.e("okHttp" , "<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        Log.e("okHttp" , "");
                        Log.e("okHttp" , "Couldn't decode the response body; charset is likely malformed.");
                        Log.e("okHttp" , "<-- END HTTP");

                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    Log.e("okHttp" , "");
                    Log.e("okHttp" , "<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    String tempStr = buffer.clone().readString(charset);
                    Log.e("okHttp" , "body size = " + tempStr.length());
                    for (int i = 1 ; ; ){
                        int maxLength = tempStr.length();
                        int endThisTime = i + 1000;
                        if (endThisTime >= maxLength){
                            Log.e("okHttp" , tempStr.substring(i - 1 , maxLength));
                            break;
                        }
                        else {
                            Log.e("okHttp" , tempStr.substring(i - 1 , endThisTime));
                            i = endThisTime + 1;
                        }
                    }
                }
                Log.e("okHttp" , "<-- END HTTP (" + buffer.size() + "-byte body)");
            }
        }

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}

