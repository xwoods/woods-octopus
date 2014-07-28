package org.nutz.web.comet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class Comet {

    private static Log log = Logs.get();

    public static boolean replyByXHR(HttpServletResponse resp, String respTxt) {
        Writer wr = null;
        OutputStream out = null;
        try {
            out = resp.getOutputStream();
            wr = new OutputStreamWriter(out);
            String data = respTxt;
            log.infof("send by xhr [%s]", respTxt);
            wr.write(data);
            wr.flush();
            Lang.quiteSleep(1 * 1000);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean replyByES(HttpServletResponse resp, String respTxt) {
        Writer wr = null;
        OutputStream out = null;
        resp.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        try {
            out = resp.getOutputStream();
            wr = new OutputStreamWriter(out);
            String data = "data:" + respTxt + "\n\n";
            log.infof("send by es [%s]", respTxt);
            wr.write(data);
            wr.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
