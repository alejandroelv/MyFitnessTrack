package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated
import java.io.Serializable

@Generated("jsonschema2pojo")
class Food : Serializable{
    @SerializedName("label")
    @Expose
    var label: String? = null
}