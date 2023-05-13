package com.alejandroelv.myfitnesstrack.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.data.model.Meal
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import java.util.ArrayList

class FoodAdapter(private var datos: List<Hint>, private val listener: OnItemClickListener) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    fun setDatos(datos: List<Hint>) {
        this.datos = datos;
    }

    /* Defino un interface con el OnItemClickListener*/
    interface OnItemClickListener {
        fun onItemClick(item: Hint)
    }

    /* Incluyo el Viewholder en el Adapter */
    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /* Como atributos se incluyen los elementos que van a referenciar a los elementos de la vista*/
        private val tvFoodName: TextView
        private val tvFoodCalories: TextView
        private val tvFoodDescription: TextView

        /*constructor con parámetro de la vista*/
        init{
            this.tvFoodName = itemView.findViewById(R.id.tvFoodName)
            this.tvFoodCalories = itemView.findViewById(R.id.tvFoodCalories)
            this.tvFoodDescription = itemView.findViewById(R.id.tvFoodDescription)
        }

        /*Muestra los datos de jugador en el item*/
        public fun bindFood(result: Hint, listener: OnItemClickListener) {
            this.tvFoodName.text = result.food?.label
            this.tvFoodCalories.text = "${result.food?.nutrients?.enercKcal?.toInt().toString()} kcal"
            this.tvFoodDescription.text = "${result.food?.label} ${result.measures?.get(0)?.label}"


            /* Coloco el listener a la vista*/
            itemView.setOnClickListener{ listener.onItemClick(result); }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : FoodViewHolder{
        /*Crea la vista de un item y la "pinta"*/
        val itemView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.food_item, viewGroup, false);

        /* Crea un objeto de la clase PeliculaViewHolder, pasándole la vista anteriormente creada*/
        val foodVH = FoodViewHolder(itemView);
        /* devuelve la vissta*/
        return foodVH;
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val result: Hint = datos[position];
        holder.bindFood(result,listener);
    }

    override fun getItemCount() : Int {
        return datos.size;
    }
}