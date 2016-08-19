package in.headrun.buzzinga.doto;

import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sujith on 11/8/16.
 */
public class SearchArticles {

    @SerializedName("_id")
    public String ID;
    @SerializedName("_source")
    public Source source;

    public class Source {
        @SerializedName("url")
        public String URL;

        @SerializedName("text")
        public String TEXT;

        @SerializedName("dt_added")
        public String DATE_ADDED;

        @SerializedName("xtags")
        public List<String> XTAGS = new ArrayList<>();

        @SerializedName("author")
        public Author AUTHOR;


        @SerializedName("title")
        public String TITLE;

    }

    public class Author {

        @SerializedName("name")
        public String NAME;

    }
}
