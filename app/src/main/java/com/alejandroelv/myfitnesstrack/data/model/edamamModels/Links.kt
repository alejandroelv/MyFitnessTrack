package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Links : Serializable {
    @SerializedName("next")
    @Expose
    var next: Next? = null
}