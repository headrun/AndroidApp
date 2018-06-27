package in.headrun.buzzinga.doto;

/**
 * Created by headrun on 8/7/15.
 */
public class Listitems {

    String sourcename;
    boolean selectd;
    String xtag;

    public Listitems(String xtag, String sourcename) {
        super();
        this.sourcename = sourcename;
        this.xtag=xtag;
    }

    public Listitems(String xtag, String sourcename,boolean selected) {
        super();
        this.sourcename = sourcename;
        this.xtag=xtag;
        this.selectd=selected;
    }

    public Listitems(String sourcename) {
        super();
        this.sourcename = sourcename;
    }
    public String getXtag() {
        return xtag;
    }

    public void setXtag(String xtag) {
        this.xtag = xtag;
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
