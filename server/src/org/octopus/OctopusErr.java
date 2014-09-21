package org.octopus;

public class OctopusErr {

    public static RuntimeException NEED_MORE_DISK_SPACE() {
        return NEW("Disk is FULL, Free Space is too small!");
    }

    public static RuntimeException DOCUMENT_NOT_EXIST(String docId) {
        return NEW("Document Not Exist, Please Check DocId %s", docId);
    }

    public static RuntimeException DOCUMENT_PARENT_NOT_EXIST(String parentId) {
        return NEW("Document Parent Not Exist, Please Check DocId %s", parentId);
    }

    public static RuntimeException NEW(String reason, Object... args) {
        return new RuntimeException(String.format(reason, args));
    }

    public static RuntimeException NEW(Throwable e, String reason, Object... args) {
        return new RuntimeException(String.format(reason, args), e);
    }
}
