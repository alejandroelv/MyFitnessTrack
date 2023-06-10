package com.alejandroelv.myfitnesstrack.ui.register.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.alejandroelv.myfitnesstrack.databinding.FragmentRegisterWeightBinding
import com.google.android.material.chip.Chip

class RegisterWeight : Fragment() {
    private var user: User = User()
    private var chipList: ArrayList<Chip> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register_weight, container, false)
        val binding = FragmentRegisterWeightBinding.bind(view)

        binding.etHeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isDigitsOnly()) {
                    user.height = Integer.valueOf(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isDigitsOnly()){
                    user.weight = Integer.valueOf(s.toString())
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
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spWeight.adapter = adapter
        }

        binding.spWeight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                when (position) {
                    0 -> {binding.tvEnterWeight.text = getString(R.string.kg); user.weightMode = 1}
                    1 -> {binding.tvEnterWeight.text = getString(R.string.pounds); user.weightMode = 2}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.etAge.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                user.age = Integer.valueOf(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.rbMale.setOnClickListener{
            this.user.gender = 1
            binding.rbFemale.isChecked = false
        }

        binding.rbFemale.setOnClickListener{
            this.user.gender = 2
            binding.rbMale.isChecked = false
        }

        binding.cpNotActive.setOnClickListener{this.user.activityLevel = 0; uncheckOtherChips(binding.cpNotActive)}
        this.chipList.add(binding.cpNotActive)

        binding.cpModeratelyActive.setOnClickListener{this.user.activityLevel = 1; uncheckOtherChips(binding.cpModeratelyActive)}
        this.chipList.add(binding.cpModeratelyActive)

        binding.cpActive.setOnClickListener{this.user.activityLevel = 2; uncheckOtherChips(binding.cpActive)}
        this.chipList.add(binding.cpActive)

        binding.cpVeryActive.setOnClickListener{this.user.activityLevel = 3; uncheckOtherChips(binding.cpVeryActive)}
        this.chipList.add(binding.cpVeryActive)

        binding.buttonNext.setOnClickListener{checkAnwers()}

        return view
    }

    private fun uncheckOtherChips(checkedChip: Chip){
        this.chipList.filter{cp -> cp != checkedChip}.forEach{cp -> cp.isChecked = false}
    }

    private fun checkAnwers(){
        when{
            user.height <= 0 -> Toast.makeText(this@RegisterWeight.context, R.string.invalid_height, Toast.LENGTH_SHORT).show()
            user.weight <= 0 -> Toast.makeText(this@RegisterWeight.context, R.string.invalid_weight, Toast.LENGTH_SHORT).show()
            user.age < 16 || user.age > 120 -> Toast.makeText(this@RegisterWeight.context, R.string.invalid_age, Toast.LENGTH_SHORT).show()
            user.gender == 0 -> Toast.makeText(this@RegisterWeight.context, R.string.select_gender, Toast.LENGTH_SHORT).show()
            user.activityLevel == 0 -> Toast.makeText(this@RegisterWeight.context, R.string.select_activity_level, Toast.LENGTH_SHORT).show()
            else -> passToNextFragment()
        }
    }

    private fun passToNextFragment(){
        val bundle = Bundle().apply {
            putParcelable("user", user)
        }

        val fragment = RegisterUserGoal()
        fragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}