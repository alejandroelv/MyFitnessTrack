package com.alejandroelv.myfitnesstrack.ui.adapters

import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint

interface ItemClickListener {
    fun onDeleteButtonClicked(food: Hint, meal: String)
}