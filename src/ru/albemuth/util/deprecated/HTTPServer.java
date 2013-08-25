package ru.albemuth.util.deprecated;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: -
 * Date: 30.05.2008
 * Time: 1:19:06
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServer implements Runnable {

    private static final int MAX_CLIENTS = 50;
    private ServerSocket servSocket;
    private boolean keepRunning = true;
    private boolean closed;
    private int serverPort;

    public static void main(String args[]) {
        if (args.length != 2) {
          System.out.println("Usage: java HTTPServer <port> <HTML root irectory>");
          System.exit(1);
        }
        HTTPServer server = new HTTPServer(Integer.parseInt(args[0]));
        server.init("servlog.txt", args[1]);
        server.run();
    }

    public HTTPServer(int aServerPort) {
        serverPort = aServerPort;
        try {
            servSocket = new ServerSocket(serverPort, MAX_CLIENTS);
        } catch (IOException e) {
            System.err.println("Unable to listen on port " + serverPort + ": "+e);
        }
    }

    public void run() {
        Socket clientSocket;
        try {
            while (keepRunning) {
                clientSocket = servSocket.accept();
                HTTPHandler newHandler = new HTTPHandler(clientSocket);
                Thread newHandlerThread = new Thread(newHandler);
                newHandlerThread.start();
            }
            servSocket.close();
            HTTPlog.HttpLogClose();
        } catch (SocketException e) {

        } catch (IOException e) {
            System.err.println("Failed I/O: " + e.getMessage());
        }
        closed = true;
    }

    public void init(String aServerLogName, String aHtmlRootName) {
        HTTPlog.HTTPlogInit(aServerLogName);
        HTTPHandler.Init(aHtmlRootName);
    }

    public void close() throws IOException {
        keepRunning = false;
        servSocket.close();
        while (!closed) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new IOException("InterruptedException: " + e.getMessage());
            }
        }
    }

}

class WWWUtil {

    public static final String VERSION                                  = "2.0";
    public static final String MIME_TEXT_PLAIN                          = "text/plain; charset=windows-1251";
    public static final String MIME_TEXT_HTML                           = "text/html; charset=windows-1251";
    public static final String MIME_TEXT_XML                            = "text/xml; charset=windows-1251";
    public static final String MIME_IMAGE_GIF                           = "image/gif";
    public static final String MIME_IMAGE_JPG                           = "image/jpg";
    public static final String MIME_APP_OS                              = "application/octet-stream";
    public static final String MIME_APP_JAVA                            = "application/java";
    public static final String MIME_VRML_WORLD                          = "x-world/x-vrml";
    public static final String MIMR_CA_CERTIFICATE                      = "application/x-x509-ca-cert";
    public static final String MIME_TEXT_WML                            = "text/vnd.wap.wml";
    public static final String MIME_IMAGE_WBMP                          = "image/vnd.wap.wbmp";
    public static final String CRLF                                     = "\r\n";

    public static final String ENC_WINDOWS_1251                         = "Cp1251";

    public static byte[] toBytes(String s, String enc) {
        try {
            return s.getBytes(enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.err.println("Unsupported encoding " + ENC_WINDOWS_1251 + ": " + e.getMessage());
            return null;
        }
    }

    public static byte[] byteArrayConcat(byte a[], byte b[]) {
        byte ret[] = new byte[a.length + b.length];
        System.arraycopy(a, 0, ret,0, a.length);
        System.arraycopy(b, 0, ret, a.length, b.length);
        return ret;
    }

    public static byte[] mimeHeader(String ct, int size) {
        return mimeHeader(200, "OK", ct, size);
    }

    public static byte[] mimeHeader(int code, String msg, String ct, int size) {
        return toBytes("HTTP/1.0"+" " + code + " " + msg + CRLF +
                       "Date: Fri, 12 Sep 2003 19:05:00 GMT" + CRLF +
                       "Server: Java/" + VERSION + CRLF +
                       "cache-control: private" + CRLF +
                       "cache-control: no-cache" + CRLF +
                       "cache-control: no-store" + CRLF +
                       "cache-control: must-revalidate" + CRLF +
                       "cache-control: max-age=0" + CRLF +
                       "expires: Fri, 12 Sep 2003 19:10:00 GMT" + CRLF +
                       "pragma: no-cache" + CRLF +
                       "content-length: " + size + CRLF +
                       "Keep-Alive: timeout=15, max=100" + CRLF +
                       "Connection: Keep-Alive" + CRLF +
                       "Content-type: " + ct + CRLF
                       + CRLF, ENC_WINDOWS_1251);
    }

    public static byte[] error(int code, String msg, String fname) {
        String ret = "<body>" + CRLF + "<h1>" + code + " " + msg + "</h1>" + CRLF;
        if (fname!=null) {
          ret += "Error when fetching URL: " + fname + CRLF;
        }
        ret += "</body>" + CRLF;
        byte tmp[] = mimeHeader(code, msg, MIME_TEXT_HTML,0);
        return byteArrayConcat(tmp, toBytes(ret, ENC_WINDOWS_1251));
    }

    public static String mimeTypeString(String filename) {
        String ct;
        if (filename.endsWith(".html") || filename.endsWith(".htm")) {
            ct = MIME_TEXT_HTML;
        } else if (filename.endsWith(".class")) {
           ct = MIME_APP_JAVA;
        } else if (filename.endsWith(".gif")) {
            ct = MIME_IMAGE_GIF;
        } else if (filename.endsWith(".jpg")) {
            ct = MIME_IMAGE_JPG;
        } else if (filename.endsWith(".wrl")) {
            ct = MIME_VRML_WORLD;
        } else if (filename.endsWith(".cacert")) {
            ct = MIMR_CA_CERTIFICATE;
        } else if (filename.endsWith(".xml")) {
            ct = MIME_TEXT_XML;
        } else if (filename.endsWith(".wml")) {
            ct = MIME_TEXT_WML;
        } else if (filename.endsWith(".wbmp")) {
            ct = MIME_IMAGE_WBMP;
        } else {
            ct = MIME_TEXT_PLAIN;
        }
        return ct;
    }

}