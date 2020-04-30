package com.example.solutionchallenge;

public class CustomListviewItem {

    private String name;
    private String info;
    private String code;
    private Object object;

    public CustomListviewItem() {}
    public CustomListviewItem(String getName, String getInfo, String getCode, Object getObject) {
        name = getName;
        info = getInfo;
        code = getCode;
        object = getObject;
    }

    public void setName(String getName) {
        name = getName;
    }
    public void setInfo(String getInfo) {
        info = getInfo;
    }
    public void setCode(String getCode) {
        code = getCode;
    }
    public void setObject(Object getObject) {
        object = getObject;
    }

    public String getName() {
        return this.name;
    }
    public String getInfo() {
        return this.info;
    }
    public String getCode() { return this.code; }
    public Object getObject() { return this.object; }
}
