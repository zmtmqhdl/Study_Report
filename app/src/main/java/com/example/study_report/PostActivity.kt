package com.example.study_report

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.study_report.databinding.ActivityPostBinding
import android.net.Uri
import java.io.File

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 100)
            } catch (_: Exception) {
                Toast.makeText(this, "Please install a file manager", Toast.LENGTH_LONG).show()
            }
        }


        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, BulletinBoardActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @Deprecated(message = "Deprecatred in Java")
    override fun onActivityResult(
        requestCode : Int, resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val uri: Uri? = data.data
            val path: String = uri?.path.toString()
            val file = File(path)
            binding.txtResult.text = "Path: $path File name: ${file.name}".trimIndent()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}