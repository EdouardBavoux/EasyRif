/*Copyright 2017 Edouard Bavoux

This file is part of EasyRif.

        EasyRif is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        EasyRif is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with EasyRif.  If not, see <http://www.gnu.org/licenses/>.*/

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
