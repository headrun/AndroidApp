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

        @SerializedName("image")
        public String IMAGE_LINK;

        @SerializedName("original_data")
        public OriginalData original_data;


        public void setIMAGE_LINK(String IMAGE_LINK) {
            this.IMAGE_LINK = IMAGE_LINK;
        }

    }

    public class OriginalData {

        @SerializedName("user")
        public UserData user_data;
        @SerializedName("actor")
        public GoogleUserData google_user;
        @SerializedName("id_str")
        public String id_str;
        @SerializedName("entities")
        public Entite entities;
    }

    public class Entite {

        public List<Urls> entite_urls;
    }

    public class Urls {
        @SerializedName("expanded_url")
        public String expanded_url;

    }

    public class GoogleUserData {
        @SerializedName("image")
        public GoogleImage google_img;
    }

    public class GoogleImage {
        @SerializedName("url")
        public String img_url;
    }


    public class UserData {

        @SerializedName("profile_image_url_https")
        public String profile_image_url_https;

        @SerializedName("profile_image_url")
        public String profile_image_url;

        @SerializedName("name")
        public String name;

        @SerializedName("screen_name")
        public String screen_name;

    }

    public class Author {

        @SerializedName("name")
        public String NAME;

    }

}
