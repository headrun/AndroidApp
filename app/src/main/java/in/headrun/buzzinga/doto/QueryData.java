package in.headrun.buzzinga.doto;

import java.util.ArrayList;

/**
 * Created by headrun on 4/9/15.
 */
public class QueryData {

    //Context context;
    public String Bkey = "";
    public ArrayList<String> Bvalue;

    public QueryData(String Bkey, ArrayList<String> Bvalue) {
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

    public ArrayList<String> getBvalue() {
        return Bvalue;
    }

    public void setBvalue(ArrayList<String> bvalue) {
        Bvalue = bvalue;
    }


}