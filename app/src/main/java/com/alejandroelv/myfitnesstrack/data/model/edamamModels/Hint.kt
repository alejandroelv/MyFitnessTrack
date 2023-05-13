package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Hint : java.io.Serializable{
    @SerializedName("food")
    @Expose
    var food: Food__1? = null

    @SerializedName("measures")
    @Expose
    var measures: List<Measure>? = null
}