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

    @SerializedName("nutrients")
    @Expose
    var nutrients: Nutrients__1? = null

    var selectedWeight : Int = 100

}