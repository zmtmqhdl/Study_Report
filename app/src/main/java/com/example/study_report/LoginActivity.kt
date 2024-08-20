package com.example.study_report

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.study_report.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 버튼 클릭 시 RegisterActivity로 이동
        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 클릭 시
        binding.buttonLogin.setOnClickListener {
            val inputID = binding.editId.text.toString().trim()
            val inputPW = binding.editPw.text.toString().trim()

            if (inputID.isEmpty() || inputPW.isEmpty()) {
                Toast.makeText(this, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(inputID, inputPW)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공 시 BulletinBoardActivity로 이동
                        val intent = Intent(this, BulletinBoardActivity::class.java)
                        startActivity(intent)
                        finish() // 현재 액티비티를 종료하여 로그인 상태를 유지
                    } else {
                        // 로그인 실패 시 알림 메시지 표시
                        binding.textAlarm.text = "잘못된 접속: ${task.exception?.message}"
                    }
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
