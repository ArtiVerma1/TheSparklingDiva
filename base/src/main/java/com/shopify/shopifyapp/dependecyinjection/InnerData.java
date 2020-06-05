
package com.shopify.shopifyapp.dependecyinjection;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InnerData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("recommendation_type")
    @Expose
    private String recommendationType;
    @SerializedName("max_recommendations")
    @Expose
    private Integer maxRecommendations;
    @SerializedName("product_ids")
    @Expose
    private List<Long> productIds = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public Integer getMaxRecommendations() {
        return maxRecommendations;
    }

    public void setMaxRecommendations(Integer maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

}
