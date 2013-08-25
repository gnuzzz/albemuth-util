package ru.albemuth.util.deprecated;

import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: -
 * Date: 30.05.2008
 * Time: 1:19:06
 * To change this template use File | Settings | File Templates.
 */
class HTTPlog {

    public static Writer out;
    private static String logname = "servlog.txt";

    public static void HTTPlogInit(String aLogName) {
        logname = aLogName;
        try {
            out = new OutputStreamWriter(new FileOutputStream(logname, true), WWWUtil.ENC_WINDOWS_1251);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to open file " + logname + ":" + e.getMessage());
        }
    }

    public static void HttpLogClose() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Can't close log file: " + e.getMessage());
        }
    }

    public static synchronized void error(String entry) {
        try {
            out.write(entry);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Can't write error string to log file: " + e.getMessage());
        }
    }

    public static synchronized void request(String request) {
        try {
            out.write("-------- Request begin -------\n" + request + "\n-------- Request end ---------\n");
            out.flush();
        } catch (IOException e) {
            System.err.println("Can't write request to log file: " + e.getMessage());
        }
    }

    public static synchronized void response(String response) {
        try {
            out.write("---- Response begin-----------\n" + response + "\n---- Response end ------------\n\n");
            out.flush();
        } catch (IOException e) {
            System.err.println("Can't write response to log file: " + e.getMessage());
        }
    }
}
