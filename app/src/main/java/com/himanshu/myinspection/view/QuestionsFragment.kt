package com.example.inspectionapplication.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inspectionapplication.model.inspection.AnswerChoice
import com.example.inspectionapplication.model.inspection.Area
import com.example.inspectionapplication.model.inspection.InspectionType
import com.example.inspectionapplication.model.inspection.Question
import com.example.inspectionapplication.model.roomdatabase.UserDatabase
import com.example.inspectionapplication.viewmodel.LoginViewModel
import com.example.inspectionapplication.viewmodel.LoginViewModelFactory
import com.himanshu.myinspection.R
import com.himanshu.myinspection.databinding.FragmentQuestionsBinding
import com.himanshu.myinspection.model.repository.UserRepository
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val TAG = "QuestionsFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [QuestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuestionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var questionList: List<Question>

    private var areaName = ""
    private var inspectionTypeName = ""
    private var categoryNames = ""
    var questionData = mutableListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)

        val database = UserDatabase.getInstance(requireContext().applicationContext)
        val repository = UserRepository(database.userDao())
        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(repository)
        ).get(LoginViewModel::class.java)

        setupJSONInspectionTypesData()
        setupJSONAreasData()
        setupJSONCategoryData()
        setupJSONQuestionData()
        setupListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun setupJSONInspectionTypesData() {

        val jsondata = requireActivity().applicationContext.resources.openRawResource(
            requireActivity().applicationContext.resources.getIdentifier(
                "inspection_types",
                "raw",
                requireActivity().applicationContext.packageName
            )
        ).bufferedReader().use { it.readText() }

        val outputJsonString = JSONObject(jsondata)
        Log.d(TAG, "Inspection Types data: " + outputJsonString)

        val inspectionType = outputJsonString.getJSONArray("inspectionTypes") as JSONArray

        var inspectionTypeData = mutableListOf<String>()

        for (i in 0 until inspectionType.length()) {
            val jsonInspectionType = inspectionType.getJSONObject(i)
            val id = jsonInspectionType.getInt("id")
            val name = jsonInspectionType.getString("name")
            val access = jsonInspectionType.getString("access")

            val inspectionType = id?.let { InspectionType(id, name, access) }
            if (inspectionType != null) {
                inspectionTypeData.add(inspectionType.inspectionTypeName)
            }
        }

        println("inspectionList = $inspectionTypeData")
        setupInspectionTypeSpinner(inspectionTypeData)

    }

    private fun setupJSONAreasData() {

        val jsondata = requireActivity().applicationContext.resources.openRawResource(
            requireActivity().applicationContext.resources.getIdentifier(
                "areas",
                "raw",
                requireActivity().applicationContext.packageName
            )
        ).bufferedReader().use { it.readText() }

        val outputJsonString = JSONObject(jsondata)
        Log.d(TAG, "Areas data: " + outputJsonString)

        val areas = outputJsonString.getJSONArray("areas") as JSONArray

        var areaData = mutableListOf<String>()

        for (i in 0 until areas.length()) {
            val jsonArea = areas.getJSONObject(i)
            val id = jsonArea.getInt("id")
            val name = jsonArea.getString("name")

            val area = id?.let { Area(id, name) }
            if (area != null) {
                areaData.add(area.areaName)
            }
        }

        println("areaList = $areaData")
        setupAreaSpinner(areaData)

    }

    private fun setupJSONCategoryData() {

        val jsondata = requireActivity().applicationContext.resources.openRawResource(
            requireActivity().applicationContext.resources.getIdentifier(
                "category",
                "raw",
                requireActivity().applicationContext.packageName
            )
        ).bufferedReader().use { it.readText() }

        val outputJsonString = JSONObject(jsondata)
        Log.d(TAG, "Category data: " + outputJsonString)

        val category = outputJsonString.getJSONArray("categoryNames") as JSONArray

        val categoryData = mutableListOf<String>()
        for (i in 0 until category.length()) {
            val jsonCategory = category.getString(i)
            categoryData.add(jsonCategory.toString())
        }

        println("categoryList = $categoryData")

        setupCategorySpinner(categoryData)

    }

    private fun setupJSONQuestionData() {

        val jsondata = requireActivity().applicationContext.resources.openRawResource(
            requireActivity().applicationContext.resources.getIdentifier(
                "questions",
                "raw",
                requireActivity().applicationContext.packageName
            )
        ).bufferedReader().use { it.readText() }

        val outputJsonString = JSONObject(jsondata)
        Log.d(TAG, "Question data: " + outputJsonString)

        val questions = outputJsonString.getJSONArray("questions") as JSONArray



        for (i in 0 until questions.length()) {
            val jsonQuestion = questions.getJSONObject(i)
            val id = jsonQuestion.getInt("id")
            val name = jsonQuestion.getString("name")
            val selectedAnswerChoiceId =
                if (jsonQuestion.isNull("selectedAnswerChoiceId")) null else jsonQuestion.getInt("selectedAnswerChoiceId")
            val answerChoicesArray = jsonQuestion.getJSONArray("answerChoices")

            val answerChoices = mutableListOf<AnswerChoice>()
            for (j in 0 until answerChoicesArray.length()) {
                val jsonAnswerChoice = answerChoicesArray.getJSONObject(j)
                val answerChoiceId = jsonAnswerChoice.getInt("id")
                val answerChoiceName = jsonAnswerChoice.getString("name")
                val score = jsonAnswerChoice.getDouble("score")
                val answerChoice = AnswerChoice(answerChoiceId, answerChoiceName, score)
                answerChoices.add(answerChoice)
            }

            val question = id?.let {
                Question(
                    questionId = id,
                    questionName = name,
                    selectedAnswerChoiceId = it,
                    answerChoices = answerChoices
                )
            }
            if (question != null) {
                questionData.add(question)
            }
        }

        println("questionList = $questionData")

        setupRecyclerView(questionData)
    }

    private fun setupRecyclerView(questionList: List<Question>) {

        // create  layoutManager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())

        // pass it to rvLists layoutManager
        binding.recyclerQuestions.setLayoutManager(layoutManager)

        questionAdapter = QuestionAdapter(requireContext(), questionList)
        binding.recyclerQuestions.adapter = questionAdapter

    }

    private fun setupInspectionTypeSpinner(inspectionType: List<String>) {

        // access the spinner
        if (binding.spinnerInspectionType != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, inspectionType
            )
            binding.spinnerInspectionType.adapter = adapter

            binding.spinnerInspectionType.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    inspectionTypeName = parent.getItemAtPosition(position).toString()
//                    Toast.makeText(requireContext(),
//                        getString(R.string.selected_item) + " " +
//                                "" + inspectionType[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

    }

    private fun setupAreaSpinner(area: List<String>) {

        // access the spinner
        if (binding.spinnerArea != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, area
            )
            binding.spinnerArea.adapter = adapter

            binding.spinnerArea.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    areaName = parent.getItemAtPosition(position).toString()
//                    Toast.makeText(requireContext(),
//                        getString(R.string.selected_item) + " " +
//                                "" + inspectionType[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

    }

    private fun setupCategorySpinner(category: List<String>) {


        // access the spinner
        if (binding.spinnerCategory != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, category
            )
            binding.spinnerCategory.adapter = adapter

            binding.spinnerCategory.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    categoryNames = parent.getItemAtPosition(position).toString()
//                    Toast.makeText(requireContext(),
//                        getString(R.string.selected_item) + " " +
//                                "" + inspectionType[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

    }


    private fun setupListener() {

        binding.btnSave.setOnClickListener {

            showToast("Data Saved Successfully")

        }

        binding.btnSubmit.setOnClickListener {

            showToast("Data Submited Successfully")

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuestionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}