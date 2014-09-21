package org.octopus.core.fs;

public interface PathDefine {

    /**
     * 根据当前模块的关键字, 给出对应的目录名称
     * 
     * @param mkey
     * @return
     */
    String define(String mkey);
}
