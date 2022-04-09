package com.happyzombie.springinitializr.common.util;

import lombok.extern.slf4j.Slf4j;
import org.brotli.dec.BrotliInputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author admin
 */
@Slf4j
public class CompressAndDecompressUtil {

    /**
     * gzip压缩
     *
     * @param str
     * @param encoding
     * @return
     */
    public static byte[] gzipCompress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        byte[] result = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out);) {
            gzip.write(str.getBytes(encoding));
            result = out.toByteArray();
        } catch (Exception e) {
            log.error("gzip 压缩异常.", e);
        }
        return result;
    }

    /**
     * gizp解压缩
     *
     * @param bytes
     * @return
     */
    public static byte[] gzipDecompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        byte[] result = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             GZIPInputStream ungzip = new GZIPInputStream(in);) {
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            result = out.toByteArray();
        } catch (Exception e) {
            log.error("gzip 解压 error.", e);
        }
        return result;
    }

    /**
     * br方式解压
     *
     * @param bytes
     * @return
     */
    public static String brDecompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             BrotliInputStream brotliInputStream = new BrotliInputStream(byteArrayInputStream);
             InputStreamReader inputStreamReader = new InputStreamReader(brotliInputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader);
        ) {
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception e) {
            log.error("br 解压 error.", e);
        }
        return sb.toString();
    }

    public static String base64(String str) {
        return Base64.getEncoder().encodeToString(str.toString().getBytes(StandardCharsets.UTF_8));
    }

}
