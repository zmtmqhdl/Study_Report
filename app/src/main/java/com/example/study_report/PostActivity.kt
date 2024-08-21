package com.example.study_report

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.study_report.databinding.ActivityPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    private var fileUri: Uri? = null
    private val PICK_FILE_REQUEST = 1

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAttachFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST)
        }

        binding.buttonSubmit.setOnClickListener {
            val title = binding.editTitle.text.toString().trim()
            val content = binding.editContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "제목과 내용을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid
            val nicknameRef = userId?.let { database.child("users").child(it).child("name") }
            nicknameRef?.get()?.addOnSuccessListener { snapshot ->
                val nickname = snapshot.getValue(String::class.java) ?: "Unknown"
                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val postId = database.child("posts").push().key ?: return@addOnSuccessListener


                val post = Post(
                    title = title,
                    content = content,
                    userId = userId,
                    userNickname = nickname,
                    timestamp = currentTime,
                )

                if (fileUri != null) {
                    val fileRef = storage.child("posts/$postId/${fileUri!!.lastPathSegment}")
                    fileRef.putFile(fileUri!!)
                        .addOnSuccessListener {
                            fileRef.downloadUrl.addOnSuccessListener { uri ->
                                post.fileUrl = uri.toString()
                                savePostToDatabase(postId, post)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "파일 업로드 실패", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    post.fileUrl = 0.toString()
                    savePostToDatabase(postId, post)
                }
            }?.addOnFailureListener {
                Toast.makeText(this, "사용자 정보를 가져오는 데 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePostToDatabase(postId: String, post: Post) {
        database.child("posts").child(postId).setValue(post)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "게시글 작성 완료", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "게시글 저장 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            val fileName = fileUri?.let { getFileName(it) }
            binding.textFileName.text = fileName ?: "파일 이름을 알 수 없음"
            Toast.makeText(this, "파일이 선택되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileName(uri: Uri): String {
        val filePath = uri.path ?: return "Unknown"
        return filePath.substring(filePath.lastIndexOf('/') + 1)
    }
}

data class Post(
    val title: String = "",
    val content: String = "",
    val userId: String? = null,
    val userNickname: String = "",
    var fileUrl: String? = null,
    val timestamp: String = ""
)
