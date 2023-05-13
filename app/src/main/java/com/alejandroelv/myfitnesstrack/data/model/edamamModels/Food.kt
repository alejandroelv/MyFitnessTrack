package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated
import java.io.Serializable

@Generated("jsonschema2pojo")
class Food : Serializable{
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
    var nutrients: Nutrients? = null

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("categoryLabel")
    @Expose
    var categoryLabel: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null
}