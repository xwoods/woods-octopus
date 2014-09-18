package org.octopus.core.fs;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.LoopException;
import org.nutz.lang.util.NutMap;
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
public class FsHelper {

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
     * 初始化各个缓存内容
     * 
     * @param dao
     */
    public static void init(Dao dao) {
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
