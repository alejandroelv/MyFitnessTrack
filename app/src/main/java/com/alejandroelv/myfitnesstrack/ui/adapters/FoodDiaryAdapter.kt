package com.alejandroelv.myfitnesstrack.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint

class FoodDiaryAdapter(private var datos: List<Hint>, private var meal: String, private val listener: OnItemClickListener) : RecyclerView.Adapter<FoodDiaryAdapter.FoodViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    fun setDatos(datos: List<Hint>, meal: String) {
        this.datos = datos;
        this.meal = meal
    }

    /* Defino un interface con el OnItemClickListener*/
    interface OnItemClickListener {
        fun onItemClick(item: Hint)
    }

    /* Incluyo el Viewholder en el Adapter */
    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /* Como atributos se incluyen los elementos que van a referenciar a los elementos de la vista*/
        private val tvFoodName: TextView
        private val tvFoodCalories: TextView
        private val deleteButton: Button

        /*constructor con parámetro de la vista*/
        init{
            this.tvFoodName = itemView.findViewById(R.id.tvFoodName)
            this.tvFoodCalories = itemView.findViewById(R.id.tvFoodCalories)
            this.deleteButton = itemView.findViewById(R.id.deleteButton)
        }

        /*Muestra los datos de jugador en el item*/
        fun bindFood(result: Hint, listener: OnItemClickListener) {
            this.tvFoodName.text = result.food?.label
            this.tvFoodCalories.text = "${result.food?.nutrients?.enercKcal?.toInt().toString()} kcal"

            this.deleteButton.setOnClickListener{
                itemClickListener.onDeleteButtonClicked(result, meal)
            }

            /* Coloco el listener a la vista*/
            itemView.setOnClickListener{ listener.onItemClick(result); }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : FoodViewHolder{
        val itemView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.food_item_diary, viewGroup, false);

        return FoodViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val result: Hint = datos[position];
        holder.bindFood(result,listener);
    }

    override fun getItemCount() : Int {
        return datos.size;
    }
}