package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Qualified : Serializable {
    @SerializedName("qualifiers")
    @Expose
    var qualifiers: List<Qualifier>? = null
}