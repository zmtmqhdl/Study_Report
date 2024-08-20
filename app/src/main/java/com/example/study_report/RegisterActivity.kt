package com.example.study_report

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.study_report.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegister.setOnClickListener {
            val inputID = binding.editId.text.toString().trim()
            val inputPW = binding.editPw.text.toString()
            val inputNAME = binding.editName.text.toString()
            val intent = Intent(this, LoginActivity::class.java)

            if (inputID.isEmpty() || inputPW.isEmpty() || inputNAME.isEmpty()) {
                Toast.makeText(this, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 닉네임 중복 확인
            database.child("users").orderByChild("name").equalTo(inputNAME)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // 닉네임이 이미 존재할 경우
                            Toast.makeText(this@RegisterActivity, "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            // 닉네임 중복이 없을 때 회원가입 진행
                            registerUser(inputID, inputPW, inputNAME, intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@RegisterActivity, "데이터베이스 에러: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun registerUser(inputID: String, inputPW: String, inputNAME: String, intent: Intent) {
        auth.createUserWithEmailAndPassword(inputID, inputPW)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = mapOf(
                        "name" to inputNAME,
                        "ID" to inputID,
                        "PW" to inputPW
                    )
                    userId?.let {
                        database.child("users").child(it).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "데이터베이스 저장 실패", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun isAllEnglish(input: String): Boolean {
        return input.matches(Regex("^[a-zA-Z]+$"))
    }
}
