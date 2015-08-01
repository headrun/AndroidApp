package com.headrun.buzzinga.doto;

/**
 * Created by headrun on 8/7/15.
 */
public class Listitems {

    String sourcename;
    boolean selectd = false;

    public Listitems(String sourcename) {
        super();
        this.sourcename = sourcename;
    }

    public String getSourcename() {
        return sourcename;
    }

    public void setSourcename(String sourcename) {
        this.sourcename = sourcename;
    }

    public boolean isSelectd() {
        return selectd;
    }

    public void setSelectd(boolean selectd) {
        this.selectd = selectd;
    }
}
