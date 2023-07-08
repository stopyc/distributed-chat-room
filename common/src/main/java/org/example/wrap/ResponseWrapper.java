package org.example.wrap;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer;
    private PrintWriter writer;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        buffer = new ByteArrayOutputStream();
        writer = new PrintWriter(buffer);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new FilterServletOutputStream(buffer);
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

    public String getBody() {
        return buffer.toString();
    }
}

class FilterServletOutputStream extends ServletOutputStream {

    private OutputStream outputStream;

    public FilterServletOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        // 空实现
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }
}