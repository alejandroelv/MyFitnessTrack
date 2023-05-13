package com.alejandroelv.myfitnesstrack.ui.register.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.data.model.User
import com.alejandroelv.myfitnesstrack.databinding.FragmentRegisterUserGoalBinding
import com.google.android.material.chip.Chip

class RegisterUserGoal : Fragment() {
    private var chipList: ArrayList<Chip> = ArrayList()
    private lateinit var user: User
    private val goals = listOf<Double>(0.25, 0.5, 0.75, 1.0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register_user_goal, container, false)
        val binding = FragmentRegisterUserGoalBinding.bind(view)

        user = arguments?.getParcelable<User>("user")!!

        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isDigitsOnly()) {
                    user.goal = Integer.valueOf(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.weight_units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spWeight.adapter = adapter
        }

        binding.spWeight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                when (position) {
                    0 -> {binding.tvEnterWeight.text = getString(R.string.kg); user.weightMode = 1}
                    1 -> {binding.tvEnterWeight.text = getString(R.string.pounds); user.weightMode = 2}
                }

                changeTextsByMode()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.cploseHalf.setOnClickListener{
            this.user.goalByWeek = goals[0] * user.weightMode
            uncheckOtherChips(binding.cploseHalf)
        }
        this.chipList.add(binding.cploseHalf)

        binding.cploseOne.setOnClickListener{
            this.user.goalByWeek = goals[1] * user.weightMode
            uncheckOtherChips(binding.cploseOne)
        }
        this.chipList.add(binding.cploseOne)

        binding.cploseOneAndHalf.setOnClickListener{
            this.user.goalByWeek = goals[2] * user.weightMode
            uncheckOtherChips(binding.cploseOneAndHalf)
        }
        this.chipList.add(binding.cploseOneAndHalf)

        binding.cploseTwo.setOnClickListener{
            this.user.goalByWeek = goals[3] * user.weightMode
            uncheckOtherChips(binding.cploseTwo)
        }
        this.chipList.add(binding.cploseTwo)

        binding.buttonPrevious.setOnClickListener{passToPreviousFragment()}
        binding.buttonNext.setOnClickListener{checkFields()}

        return view
    }

    private fun changeTextsByMode(){
        val unit = if (user.weightMode == 1) getString(R.string.kg) else getString(R.string.pounds)

        this.chipList.forEachIndexed{i, cp ->
            cp.text = "${getString(R.string.lose)} ${goals[i] * user.weightMode} $unit ${getString(R.string.by_week)}"
        }
    }

    private fun uncheckOtherChips(checkedChip: Chip){
        this.chipList.filter{cp -> cp != checkedChip}.forEach{cp -> cp.isChecked = false}
    }

    private fun passToPreviousFragment(){
        parentFragmentManager.popBackStack()
    }

    private fun checkFields(){
        when{
            user.goal <= 0 || (user.goal <= (user.height - 120)) -> {
                Toast.makeText(context, R.string.invalid_goal, Toast.LENGTH_SHORT).show()
                Log.e("Booleano", (user.goal <= (user.height - 120)).toString())
                Log.e("User goal", user.goal.toString())
            }
            user.goalByWeek == 0.0 ->{
                Toast.makeText(context, R.string.select_your_weekly_goal, Toast.LENGTH_SHORT).show()
            }
            else -> passToNextFragment()
        }
    }

    private fun passToNextFragment(){
        val bundle = Bundle().apply {
            putParcelable("user", user)
        }

        val fragment = RegisterUser()
        fragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}