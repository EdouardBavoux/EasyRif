package com.eb.zoom.easyrif;

/**
 * Created by Edouard on 05/08/2017.
 */

public class Chord {
    protected String[] content = {null,null,null,null,null,null};
    protected String name;
    protected String key;

    public String getName(){
        return name;
    }

    public void setName(String iName){
        name=iName;
    }

    public String[] getContent(){
        return content;
    }

    public void setcontent(String[] iContent){
        content=iContent;
    }

    public String getContentAsString(){
        return ""+content[0]+content[1]+content[2]+content[3]+content[4]+content[5];
    }

    public String getKey(){
        return key;
    }

    public void setKey(String iKey){
        key=iKey;
    }
}
