package com.alejandroelv.myfitnesstrack.data.model.edamamModels

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Nutrients__1 : Serializable {
    @SerializedName("ENERC_KCAL")
    @Expose
    var enercKcal: Double? = null

    @SerializedName("PROCNT")
    @Expose
    var procnt: Double? = null

    @SerializedName("FAT")
    @Expose
    var fat: Double? = null

    @SerializedName("CHOCDF")
    @Expose
    var chocdf: Double? = null

    @SerializedName("FIBTG")
    @Expose
    var fibtg: Double? = null
}