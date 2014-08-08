package org.octopus.fs;

import java.io.InputStreamReader;
import java.util.List;

import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class FileConf {

    private FileType _default;

    private List<FileType> support;

    private List<FileCate> category;

    private FileConf() {}

    private static FileConf me;

    public static FileConf ME() {
        return me;
    }

    private static Log log = Logs.get();

    static {
        try {
            me = Json.fromJson(FileConf.class,
                               new InputStreamReader(FileConf.class.getResourceAsStream("/fileConf.js")));
        }
        catch (Exception e) {}
    }

}
