package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Qualifier : Serializable {
    @SerializedName("uri")
    @Expose
    var uri: String? = null

    @SerializedName("label")
    @Expose
    var label: String? = null
}