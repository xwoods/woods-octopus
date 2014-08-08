package org.octopus.fs;

import java.util.List;
import java.util.Map;

public class FileCate {

    private String name;

    private List<String> types;

    private Map<String, FileType> typeMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Map<String, FileType> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<String, FileType> typeMap) {
        this.typeMap = typeMap;
    }

}
