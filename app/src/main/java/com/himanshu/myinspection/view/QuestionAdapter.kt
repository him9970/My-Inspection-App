package com.example.inspectionapplication.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inspectionapplication.model.inspection.AnswerChoice
import com.example.inspectionapplication.model.inspection.Question
import com.himanshu.myinspection.databinding.QuestionListItemBinding

class QuestionAdapter(
    var context : Context,
    var questionList: List<Question>,
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: QuestionListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QuestionListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(questionList[position]){
                binding.txtSerialNo.text = "${position+1}."
                binding.txtQuestion.text = this.questionName

                binding.radioGroup.removeAllViews()
                val radioButtonTexts = this.answerChoices
                // Create and add radio buttons dynamically
                for (text in radioButtonTexts) {
                    val radioButton = RadioButton(binding.root.context)
                    radioButton.text = text.answerChoicesName
                    radioButton.isSelected = text.isSelected
                    radioButton.layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    )
                    binding.radioGroup.addView(radioButton)
                }



                // Set listener for radio button selection if needed
                binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    // Handle radio button selection here
                    this.selectedAnswerChoiceId = checkedId
                }

            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return questionList.size
    }
}