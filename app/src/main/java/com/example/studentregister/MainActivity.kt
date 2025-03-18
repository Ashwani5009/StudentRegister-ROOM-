package com.example.studentregister

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentregister.db.Student
import com.example.studentregister.db.StudentDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var clearButton: Button

    private lateinit var viewModel: StudentViewModel
    private lateinit var studentRecyclerView: RecyclerView
    private lateinit var adapter: StudentRecyclerViewAdapter

    private var selectedStudent: Student? = null
    private var isListItemClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.etName)
        emailEditText = findViewById(R.id.etEmail)
        saveButton = findViewById(R.id.btnSave)
        clearButton = findViewById(R.id.btnClear)
        studentRecyclerView = findViewById(R.id.rvStudent)

        val dao = StudentDatabase.getInstance(application).studentDao()
        val factory = StudentViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory).get(StudentViewModel::class.java)

        saveButton.setOnClickListener {
            if(isListItemClicked){
                updateStudentData()
                clearInput()
            }
            else{
                saveStudentData()
                clearInput()
            }
        }

        clearButton.setOnClickListener {
            if(isListItemClicked){
                deleteStudentData()
                clearInput()
            }
            else{
                clearInput()
            }
        }

        initRecyclerView()
    }

    private fun saveStudentData(){
        viewModel.insertStudent(
            Student(
                0,
                nameEditText.text.toString(),
                emailEditText.text.toString()
            )
        )
    }

    private fun updateStudentData(){
        viewModel.updateStudent(
            Student(
                selectedStudent!!.id,
                nameEditText.text.toString(),
                emailEditText.text.toString()
            )
        )

        selectedStudent = null
        saveButton.text = "Save"
        clearButton.text = "Clear"
        isListItemClicked = false
    }

    private fun deleteStudentData(){
        viewModel.deleteStudent(
            Student(
                selectedStudent!!.id,
                nameEditText.text.toString(),
                emailEditText.text.toString()
            )
        )

        selectedStudent = null
        saveButton.text = "Save"
        clearButton.text = "Clear"
        isListItemClicked = false
    }

    private fun clearInput(){
        nameEditText.setText("")
        emailEditText.setText("")
    }

    private fun initRecyclerView(){
        studentRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentRecyclerViewAdapter{
            selectedItem: Student -> listItemClicked(selectedItem)
        }
        studentRecyclerView.adapter = adapter

        displayStudentsList()
    }

    private fun displayStudentsList(){
        viewModel.students.observe(this, {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(student: Student){
        selectedStudent = student
        saveButton.text = "Update"
        clearButton.text = "Delete"
        isListItemClicked = true
        nameEditText.setText(selectedStudent!!.name)
        emailEditText.setText(selectedStudent!!.email)
    }
}