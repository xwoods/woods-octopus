package org.octopus.core.fs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.core.bean.DocumentCate;
import org.octopus.core.bean.DocumentMeta;
import org.octopus.core.bean.DocumentMetaRef;
import org.octopus.core.bean.DocumentType;

/**
 * 缓存文件类型,分类类型等信息, 方便FsIO使用
 * 
 * @author pw
 * 
 */
/**
 * @author pw
 * 
 */
public class FsSetting {

    private static Log log = Logs.get();

    /**
     * typeName -> docType
     * 
     * 例如:
     * 
     * txt -> txt : TXT : false : false : false : text/plain
     * 
     */
    public static Map<String, DocumentType> typeMap = new ConcurrentHashMap<String, DocumentType>();

    /**
     * cateName -> [typeName, typeName, typeName, ...]
     * 
     * 例如:
     * 
     * video -> swf|avi|mpeg|mpg|mp4|mov|3gp|wmv|vob
     */
    public static Map<String, Set<String>> cate2type = new ConcurrentHashMap<String, Set<String>>();

    /**
     * typeName -> cateName
     * 
     * 例如:
     * 
     * avi -> video
     */
    public static Map<String, String> type2Cate = new ConcurrentHashMap<String, String>();

    /**
     * metaName -> meta
     * 
     * 例如:
     * 
     * width -> width : INT : 0
     */
    public static Map<String, DocumentMeta> metaMap = new ConcurrentHashMap<String, DocumentMeta>();

    /**
     * typeName -> 带着默认值的meta对象
     * 
     * 例如:
     * 
     * video -> { width: 0, height:0, duration: 0}
     * 
     */
    public static Map<String, NutMap> type2metaMap = new ConcurrentHashMap<String, NutMap>();

    /**
     * @param type
     *            文件类型
     * @return 返回对应的带有默认值的meta对象
     */
    public static NutMap dfMeta(String type) {
        return type2metaMap.get(type);
    }

    /**
     * 加载文件系统配置文件
     * 
     * @param fsIns
     * 
     */
    public static void loadSetting(Dao dao, InputStream fsIns) {
        PropertiesProxy fspp = new PropertiesProxy(fsIns);
        log.info("Load File-type :");
        List<String> ftlist = fspp.getList("file-type");
        for (String ft : ftlist) {
            log.info(ft);
        }
        log.info("Load File-Cate :");
        List<String> fclist = fspp.getList("file-cate");
        for (String fc : fclist) {
            log.info(fc);
        }
        log.info("Load File-Meta :");
        List<String> fmlist = fspp.getList("file-meta");
        for (String fm : fmlist) {
            log.info(fm);
        }
        log.info("Load File-Meta-Ref :");
        List<String> fmreflist = fspp.getList("file-meta-ref");
        for (String fmref : fmreflist) {
            log.info(fmref);
        }
        // 更新到数据库中
        for (String ft : ftlist) {
            String[] fts = Strings.splitIgnoreBlank(ft, ":");
            DocumentType docTp = new DocumentType();
            docTp.setName(fts[0]);
            docTp.setReadAs(ReadType.valueOf(fts[1]));
            docTp.setHasPreview(Boolean.valueOf(fts[2]));
            docTp.setHasInfo(Boolean.valueOf(fts[3]));
            docTp.setHasTrans(Boolean.valueOf(fts[4]));
            docTp.setMime(fts[5]);
            if (dao.count(DocumentType.class, Cnd.where("name", "=", docTp.getName())) == 0) {
                dao.insert(docTp);
            }
        }
        for (String fc : fclist) {
            String[] fcs = Strings.splitIgnoreBlank(fc, ":");
            String cateName = fcs[0];
            String[] cateTps = Strings.splitIgnoreBlank(fcs[1], "\\|");
            for (String ctp : cateTps) {
                DocumentCate docCate = new DocumentCate();
                docCate.setName(cateName);
                docCate.setTypeName(ctp);
                if (dao.count(DocumentCate.class,
                              Cnd.where("name", "=", cateName).and("typeName", "=", ctp)) == 0) {
                    dao.insert(docCate);
                }
            }
        }
        for (String fm : fmlist) {
            String[] fms = Strings.splitIgnoreBlank(fm, ":");
            String metaName = fms[0];
            DocumentMeta docMeta = new DocumentMeta();
            docMeta.setName(metaName);
            docMeta.setType(MetaType.valueOf(fms[1]));
            docMeta.setDfValue(fms[2]);
            if (dao.count(DocumentMeta.class, Cnd.where("name", "=", metaName)) == 0) {
                dao.insert(docMeta);
            }
        }
        for (String fmref : fmreflist) {
            String[] fmrefs = Strings.splitIgnoreBlank(fmref, ":");
            String metaName = fmrefs[0];
            String[] tys = Strings.splitIgnoreBlank(fmrefs[1], "\\|");
            List<String> tpList = new ArrayList<String>();
            for (String ty : tys) {
                if (ty.startsWith("@")) {
                    // 说明是cateName
                    List<DocumentCate> dclist = dao.query(DocumentCate.class,
                                                          Cnd.where("name", "=", ty.substring(1)));
                    for (DocumentCate dc : dclist) {
                        tpList.add(dc.getTypeName());
                    }
                } else {
                    tpList.add(ty);
                }
            }
            for (String tp : tpList) {
                DocumentMetaRef docMRef = new DocumentMetaRef();
                docMRef.setTypeName(tp);
                docMRef.setMetaName(metaName);
                if (dao.count(DocumentMetaRef.class,
                              Cnd.where("typeName", "=", tp).and("metaName", "=", metaName)) == 0) {
                    dao.insert(docMRef);
                }
            }
        }

        // 重置缓存
        resetCache(dao);
    }

    /**
     * 重置缓存内容
     * 
     * @param dao
     */
    public static void resetCache(Dao dao) {
        typeMap.clear();
        type2Cate.clear();
        cate2type.clear();
        metaMap.clear();
        type2metaMap.clear();

        // typeMap
        dao.each(DocumentType.class, null, new Each<DocumentType>() {
            public void invoke(int index, DocumentType docTp, int length) throws ExitLoop,
                    ContinueLoop, LoopException {
                typeMap.put(docTp.getName(), docTp);
            }
        });

        // type2Cate & cate2type
        dao.each(DocumentCate.class, null, new Each<DocumentCate>() {
            public void invoke(int index, DocumentCate docCate, int length) throws ExitLoop,
                    ContinueLoop, LoopException {
                type2Cate.put(docCate.getTypeName(), docCate.getName());
                Set<String> typeList = cate2type.get(docCate.getName());
                if (typeList == null) {
                    typeList = new HashSet<String>();
                    cate2type.put(docCate.getName(), typeList);
                }
                typeList.add(docCate.getTypeName());
            }
        });

        // metaMap
        dao.each(DocumentMeta.class, null, new Each<DocumentMeta>() {
            public void invoke(int index, DocumentMeta docMeta, int length) throws ExitLoop,
                    ContinueLoop, LoopException {
                metaMap.put(docMeta.getName(), docMeta);
            }
        });

        // type2metaMap
        for (DocumentType docTp : typeMap.values()) {
            NutMap dfMeta = NutMap.NEW();
            List<DocumentMetaRef> tpMetas = dao.query(DocumentMetaRef.class,
                                                      Cnd.where("typeName", "=", docTp.getName()));
            for (DocumentMetaRef mRef : tpMetas) {
                DocumentMeta m = metaMap.get(mRef.getMetaName());
                dfMeta.setv(m.getName(), m.value(mRef.getSpDfValue()));
            }
            type2metaMap.put(docTp.getName(), dfMeta);
        }
    }
}
