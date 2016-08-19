package in.headrun.buzzinga.doto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by headrun on 4/9/15.
 */
public class QueryData {

    //Context context;
    public String Bkey = "";
    public List<String> Bvalue;

    public QueryData(String Bkey, List<String> Bvalue) {
       // this.context = context;
        this.Bkey = Bkey;
        this.Bvalue = Bvalue;

    }

    public String getBkey() {
        return Bkey;
    }

    public void setBkey(String bkey) {
        Bkey = bkey;
    }

    public List<String> getBvalue() {
        return Bvalue;
    }

    public void setBvalue(List<String> bvalue) {
        Bvalue = bvalue;
    }


}