package ru.albemuth.util.deprecated;

import java.net.Socket;
import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: -
 * Date: 30.05.2008
 * Time: 1:19:05
 * To change this template use File | Settings | File Templates.
 */
class HTTPHandler implements Runnable {

    public static final String INDEX_FILE                           = "index.html";
    public static final String CONTENT_LENGTH_FIELD                 = "Content-length";
    public static final int BUFFER_SIZE                             = 2048;
    public static final int RT_GET                                  = 1;
    public static final int RT_UNSUP                                = 2;
    public static final int RT_END                                  = 4;
    public static final int RT_HEAD                                 = 8;
    public static final int RT_POST                                 = 16;

    private Socket mySocket;
    static String docRoot;

    public HTTPHandler(Socket newSocket) {
        mySocket = newSocket;
    }

    public static void Init(String htmlRoot) {
        docRoot = htmlRoot;
    }

    public void run() {
        String request;
        OutputStream output = null;
        InputStream input = null;
        try {
            output = mySocket.getOutputStream();
            input = mySocket.getInputStream();
            if ((request = getRawRequest(input)) != null) {
                switch (HTTPRequestType(request)) {
                    case RT_GET :
                        handleGet(request, output);
                        break;
                    case RT_HEAD :
                        handleHead(request, output);
                        break;
                    case RT_POST :
                        handlePost(request, output);
                        break;
                    case RT_UNSUP :
                    default :
                        handleUnsup(request, output);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException while processing request: " + e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Can't close input stream: " + e.getMessage());
                }
            }
            if (output != null ) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Can't close output stream: " + e.getMessage());
                }
            }
            try {
                mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Can't close client socket: " + e.getMessage());
            }
        }
    }

    private static void handleUnsup(String request, OutputStream out) throws IOException {
        out.write(WWWUtil.error(405, "Unsupported request", ""));
        HTTPlog.request(request);
        HTTPlog.response("405: Unsupported request");
    }

    private static void handleGet(String request, OutputStream out) {
        int fsp = request.indexOf(' ');
        int nsp = request.indexOf(' ', fsp + 1);
        String filename = request.substring(fsp + 1, nsp);
        filename = docRoot + filename + (filename.endsWith("/") ? INDEX_FILE : "");
        InputStream in = null;
        try {
            File f = new File(filename);
            if (!f.exists()) {
                out.write(WWWUtil.error(404, "Not Found", filename));
                HTTPlog.request(request);
                HTTPlog.response("404: Not Found: " + filename);
                return;
            }
            if (!f.canRead()) {
                out.write(WWWUtil.error(403, "Permission Denied", filename));
                HTTPlog.request(request);
                HTTPlog.response("403: Permission Denied: " + filename);
                return;
            }
            in = new FileInputStream(filename);
            String mime_header = WWWUtil.mimeTypeString(filename);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buf[] = new byte[BUFFER_SIZE];
            for (int read = in.read(buf); read != -1; read = in.read(buf)) {
                bout.write(buf, 0, read);
            }
            int contentLength = bout.size();
            out.write(WWWUtil.mimeHeader(mime_header, contentLength));
            HTTPlog.request(request);
            HTTPlog.response(mime_header + " " + contentLength);
            out.write(bout.toByteArray());
            bout.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException while processing get request\n " + request);
            HTTPlog.error("Exception: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Can't close file input stream: " + e.getMessage());
                }
            }
        }
    }

    private static void handleHead(String request, OutputStream out) {
        int fsp = request.indexOf(' ');
        int nsp = request.indexOf(' ', fsp + 1);
        String filename = request.substring(fsp + 1, nsp);
        filename = docRoot + filename + (filename.endsWith("/") ? INDEX_FILE : "");
        try {
            File f = new File(filename);
            if (!f.exists()) {
                out.write(WWWUtil.error(404, "Not Found", filename));
                HTTPlog.request(request);
                HTTPlog.response("404: Not Found: " + filename);
                return;
            }
            if (!f.canRead()) {
                out.write(WWWUtil.error(403,"Permission Denied", filename));
                HTTPlog.request(request);
                HTTPlog.response("403: Permission Denied: " + filename);
                return;
            }
            String mime_header = WWWUtil.mimeTypeString(filename);
            out.write(WWWUtil.mimeHeader(mime_header, 0));
            HTTPlog.request(request);
            HTTPlog.response(mime_header + " " + 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException while processing head request\n " + request);
            HTTPlog.error("Exception: " + e.getMessage());
        }
    }

    private static void handlePost(String request, OutputStream out) {
        try {
            String msg = "This method is not allowed";
            out.write(WWWUtil.mimeHeader("text/html", msg.length()));
            out.write(msg.getBytes());
            HTTPlog.request(request);
            HTTPlog.response("text/html " + msg.length());
        } catch(IOException e) {
            e.printStackTrace();
            System.err.println("IOException while processing post request\n " + request);
            HTTPlog.error("Exception: " + e.getMessage());
        }
    }

    private static String getRawRequest(InputStream in) {
        String ret = null;
        try {
            byte buf[] = new byte[BUFFER_SIZE];
            boolean gotCR = false;
            int pos = 0;
            int c;
            while (ret == null && (c = in.read()) != -1) {
                switch (c) {
                    case '\r':
                        break;
                    case '\n':
                        if (gotCR) {
                            buf[pos++] = (byte) c;
                            String header = new String(buf, 0, pos, WWWUtil.ENC_WINDOWS_1251);
                            String contentLengthS = headerForKey(header, CONTENT_LENGTH_FIELD);
                            if (contentLengthS != null) {
                                int cl = Integer.parseInt(contentLengthS);
                                in.read(buf, pos, cl);
                                pos += cl;
                            }
                            ret = new String(buf, 0, pos, WWWUtil.ENC_WINDOWS_1251);
                        }
                        gotCR = true;
                    //FALLSTHROUGH (put the 1st \n in the string)
                    default:
                        if (c != '\n') gotCR = false;
                        buf[pos++] = (byte) c;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException while getting raw request: " + e.getMessage());
            HTTPlog.error("Receive Error");
        }
        return ret;
    }

    public static int HTTPRequestType(String request) {
        if (request.regionMatches(true,0,"get",0,3)) {
            return RT_GET;
        } else if (request.regionMatches(true,0,"head",0,3)) {
            return RT_HEAD;
        } else if (request.regionMatches(true,0,"post",0,3)) {
            return RT_POST;
        } else {
            return RT_UNSUP;
        }
    }

    public static String headerForKey(String aHeader, String aKey) {
        StringTokenizer stringSt = new StringTokenizer(aHeader, "\r\n");
        while (stringSt.hasMoreTokens()) {
            String s = stringSt.nextToken();
            StringTokenizer keySt = new StringTokenizer(s, ": ");
            if (aKey.equalsIgnoreCase(keySt.nextToken())) {
                return keySt.nextToken();
            }
        }
        return null;
    }

}
