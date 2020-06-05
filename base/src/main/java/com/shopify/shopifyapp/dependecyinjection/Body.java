
package com.shopify.shopifyapp.dependecyinjection;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Body {

    @SerializedName("queries")
    @Expose
    private List<InnerData> queries = null;

    public List<InnerData> getQueries() {
        return queries;
    }

    public void setQueries(List<InnerData> queries) {
        this.queries = queries;
    }

}
