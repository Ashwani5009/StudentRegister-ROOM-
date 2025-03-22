package com.example.studentregister

import android.os.Bundle
import android.view.LayoutInflater
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
import com.example.studentregister.databinding.ActivityMainBinding
import com.example.studentregister.db.Student
import com.example.studentregister.db.StudentDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: StudentViewModel
    private lateinit var adapter: StudentRecyclerViewAdapter

    private var selectedStudent: Student? = null
    private var isListItemClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val dao = StudentDatabase.getInstance(application).studentDao()
        val factory = StudentViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory).get(StudentViewModel::class.java)

        binding.apply {
            btnSave.setOnClickListener {
                if(isListItemClicked){
                    updateStudentData()
                    clearInput()
                }
                else{
                    saveStudentData()
                    clearInput()
                }
            }

            btnClear.setOnClickListener {
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
    }

    private fun saveStudentData(){
        binding.apply {
            viewModel.insertStudent(
                Student(
                    0,
                    etName.text.toString(),
                    etEmail.text.toString()
                )
            )
        }
    }

    private fun updateStudentData(){
        binding.apply {
            viewModel.updateStudent(
                Student(
                    selectedStudent!!.id,
                    etName.text.toString(),
                    etEmail.text.toString()
                )
            )

            selectedStudent = null
            btnSave.text = "Save"
            btnClear.text = "Clear"
            isListItemClicked = false
        }
    }

    private fun deleteStudentData(){
        binding.apply {
            viewModel.deleteStudent(
                Student(
                    selectedStudent!!.id,
                    etName.text.toString(),
                    etEmail.text.toString()
                )
            )

            selectedStudent = null
            btnSave.text = "Save"
            btnClear.text = "Clear"
            isListItemClicked = false
        }
    }

    private fun clearInput(){
        binding.apply {
            etName.setText("")
            etEmail.setText("")
        }
    }

    private fun initRecyclerView(){
        binding.apply {
            rvStudent.layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = StudentRecyclerViewAdapter{
                    selectedItem: Student -> listItemClicked(selectedItem)
            }
            rvStudent.adapter = adapter

            displayStudentsList()
        }
    }

    private fun displayStudentsList(){
        viewModel.students.observe(this, {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(student: Student){
        binding.apply {
            selectedStudent = student
            btnSave.text = "Update"
            btnClear.text = "Delete"
            isListItemClicked = true
            etName.setText(selectedStudent!!.name)
            etEmail.setText(selectedStudent!!.email)
        }
    }
}