package com.example.smsapp
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var nameText: EditText
    private lateinit var idText: EditText
    private lateinit var mobileText: EditText
    private lateinit var addressText: EditText
    private lateinit var courcespinner: Spinner
    private lateinit var submitbutton: Button

    private val courseOptions = arrayOf("NVQ 5", "NVQ 4", "NVQ 3", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameText = findViewById(R.id.nameText)
        idText = findViewById(R.id.idText)
        mobileText = findViewById(R.id.mobileText)
        addressText = findViewById(R.id.addressText)
        courcespinner = findViewById(R.id.courcespinner)
        submitbutton = findViewById(R.id.submitbutton)

        database = FirebaseDatabase.getInstance().reference

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courcespinner.adapter = adapter

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 1)
        }
        submitbutton.setOnClickListener {
            submitStudentDetails()
        }
    }
    private fun submitStudentDetails() {
        val studentName = nameText.text.toString()
        val studentId = idText.text.toString()
        val studentMobile = mobileText.text.toString()
        val studentAddress = addressText.text.toString()
        val selectedCourse = courcespinner.selectedItem.toString()

        if (studentName.isNotEmpty() && studentId.isNotEmpty() && studentMobile.isNotEmpty() &&
            studentAddress.isNotEmpty()) {
            val refNumber = System.currentTimeMillis().toString()
            val studentData = Student(studentName, studentId, studentMobile, studentAddress,
                selectedCourse, refNumber)
            database.child("students").child(refNumber).setValue(studentData)
            val studentMessage = "You have applied for $selectedCourse with reference number$refNumber."
            sendSMS(studentMobile, studentMessage)

            val teacherMobile = "+94716771667"
            val teacherMessage = "Student: $studentName (ID: $studentId, Mobile: $studentMobile) hasapplied for $selectedCourse. Ref number: $refNumber."
            sendSMS(teacherMobile, teacherMessage)

            Toast.makeText(this, "Student registered successfully", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this, "SMS sent.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "SMS failed to send.", Toast.LENGTH_SHORT).show()
        }
    }

    data class Student(
        val name: String,
        val id: String,
        val mobile: String,
        val address: String,
        val course: String,
        val refNumber: String
    )
} 