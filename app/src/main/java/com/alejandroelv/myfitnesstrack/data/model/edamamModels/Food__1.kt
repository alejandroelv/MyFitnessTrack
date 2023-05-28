package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated
import java.io.Serializable

@Generated("jsonschema2pojo")
class Food__1 : Serializable{
    @SerializedName("foodId")
    @Expose
    var foodId: String? = null

    @SerializedName("label")
    @Expose
    var label: String? = null

    @SerializedName("knownAs")
    @Expose
    var knownAs: String? = null

    @SerializedName("nutrients")
    @Expose
    var nutrients: Nutrients__1? = null

    var selectedWeight : Int = 100

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("categoryLabel")
    @Expose
    var categoryLabel: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("foodContentsLabel")
    @Expose
    var foodContentsLabel: String? = null

    @SerializedName("brand")
    @Expose
    var brand: String? = null

    @SerializedName("servingSizes")
    @Expose
    var servingSizes: List<ServingSize>? = null
}