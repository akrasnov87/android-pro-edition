package com.mobwal.pro.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import ru.mobnius.core.data.RequestManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;

/**
 * вспомогательный класс для передачи данных на сервер
 * Применяется только для тестирования синхронизации
 */
public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private final String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL адрес запроса
     * @throws IOException исключение
     */
    public MultipartUtility(String requestURL, BasicCredential basicCredentials)
            throws IOException {
        this.charset = "utf-8";

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);    // indicates POST method
        httpConn.setDoInput(true);
        if(basicCredentials != null)
            httpConn.setRequestProperty(RequestManager.AUTHORIZATION_HEADER, basicCredentials.getToken());
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8),
                true);
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Добавление файла
     * @param fieldName имя
     * @param buffer массив байтов
     * @throws IOException исключение
     */
    public void addFilePart(@SuppressWarnings("SameParameterValue") String fieldName, byte[] buffer)
            throws IOException {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(fieldName).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fieldName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        outputStream.write(buffer, 0, buffer.length);

        outputStream.flush();
        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name).append(": ").append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * результат запроса
     * @return Возвращается массив байтов
     */
    public byte[] finish() throws Exception {
        writer.append(LINE_FEED).flush();
        writer.append("--").append(boundary).append("--").append(LINE_FEED);
        writer.close();

        int status = httpConn.getResponseCode();

        if (status == HttpURLConnection.HTTP_OK) {
            byte[] bytes = getBytes(httpConn.getInputStream());
            destroy();
            return bytes;
        } else {
            byte[] bytes = getBytes(httpConn.getErrorStream());
            destroy();
            throw new Exception("Статус код: " + status + ". " + new String(bytes));
        }
    }

    /**
     * чтение информации из потока
     * @param is входящий поток
     * @return Возмращается массив байтов
     * @throws IOException исключение при чтении
     */
    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        byte[] bytes = buffer.toByteArray();
        buffer.close();
        is.close();
        return bytes;
    }

    /**
     * удаление объекта
     */
    public void destroy(){
        if(httpConn != null) {
            httpConn.disconnect();
            httpConn = null;
        }
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }

            if(writer != null) {
                writer.close();
                writer = null;
            }
        }catch (IOException ignored){

        }
    }
}
