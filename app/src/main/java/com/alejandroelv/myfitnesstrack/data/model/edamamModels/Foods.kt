package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Foods : Serializable {
    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("hints")
    @Expose
    var hints: List<Hint>? = null
}