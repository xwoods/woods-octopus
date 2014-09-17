package org.octopus.core.fs;

/**
 * 
 * 文件读取类型, 像什么类型文件一样进行读取操作
 * 
 * @author pw
 * 
 */
public enum FsAs {

    /**
     * 包含子文件的目录
     */
    DIR,
    /**
     * 二进制文件, 可以读取
     */
    BIN,
    /**
     * 文本文件, 可以读取并显示
     */
    TXT,
    /**
     * 复杂文件, 本身是一个目录, 里面所有子文件做为一个整体代表当前文件本身
     */
    CPX
}
