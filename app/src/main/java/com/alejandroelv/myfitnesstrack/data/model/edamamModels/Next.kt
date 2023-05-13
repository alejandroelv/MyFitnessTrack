package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Next : Serializable {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("href")
    @Expose
    var href: String? = null
}