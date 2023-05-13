package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Food
import java.io.Serializable
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Parsed : Serializable {
    @SerializedName("food")
    @Expose
    var food: Food? = null
}