package org.octopus.core.fs;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class FS {

    private static Log log = Logs.get();

    private FileType defaultSupport;
    private List<FileType> support;
    private List<FileCate> category;
    private Map<String, FileType> supportMap;
    private Map<String, FileCate> categoryMap;

    public List<FileType> getSupport() {
        return support;
    }

    public void setSupport(List<FileType> support) {
        this.support = support;
    }

    public List<FileCate> getCategory() {
        return category;
    }

    public void setCategory(List<FileCate> category) {
        this.category = category;
    }

    public FS() {}

    private static FS me;

    public static FS ME() {
        return me;
    }

    public static void loadConf() {
        try {
            String fsConf = Streams.readAndClose(new InputStreamReader(FS.class.getResourceAsStream("/fs.js")));
            me = Json.fromJson(FS.class, fsConf);
            me.initConf();
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    private void initConf() {
        supportMap = new HashMap<String, FileType>();
        for (FileType sft : support) {
            if (sft.getAs() == null) {
                sft.setAs(defaultSupport.getAs());
            }
            if (Strings.isBlank(sft.getCate())) {
                sft.setCate(defaultSupport.getCate());
            }
            if (Strings.isBlank(sft.getMime())) {
                sft.setMime(defaultSupport.getMime());
            }
            sft.setCate("");
            supportMap.put(sft.getName(), sft);
        }
        categoryMap = new HashMap<String, FileCate>();
        for (FileCate fc : category) {
            List<String> ctps = fc.getTypes();
            for (String ctp : ctps) {
                if (supportMap.containsKey(ctp)) {
                    supportMap.get(ctp).setCate(fc.getName());
                    categoryMap.put(ctp, fc);
                }
            }
        }
        log.infof("FS-Support : Support %d-Type %d-Cate", supportMap.size(), categoryMap.size());
        log.infof("| %-8s| %-8s| %-8s| %-25s|", "TYPE", "AS", "CATE", "MIME");
        for (FileType sft : supportMap.values()) {
            log.infof("| %-8s| %-8s| %-8s| %-25s|",
                      sft.getName(),
                      sft.getAs(),
                      sft.getCate(),
                      sft.getMime());
        }
    }

    // ===================

    public FileType getFileType(String ftp) {
        FileType sft = supportMap.get(ftp);
        if (sft == null) {
            log.warnf("FS-Support : Not Find Type [%s]", ftp);
        }
        return sft;
    }

    public FileCate getFileCate(String ftp) {
        FileCate fc = categoryMap.get(ftp);
        if (fc == null) {
            log.warnf("FS-Support : Not Find Cate [%s]", ftp);
        }
        return fc;
    }

}
