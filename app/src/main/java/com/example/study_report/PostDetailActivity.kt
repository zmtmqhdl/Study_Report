package com.example.study_report

import BulletinPost
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.study_report.databinding.ActivityPostDetailBinding

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonDownload.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Intent에서 BulletinPost 객체 가져오기
        val post = intent.getParcelableExtra<BulletinPost>("POST_KEY")

        // 데이터가 null이 아닌 경우에만 처리
        post?.let {
            binding.textTitle.text = it.title
            binding.textContent.text = it.content


            // 첨부파일 표시
            if (it.fileUrl != "0") {
                binding.buttonDownload.visibility = View.VISIBLE
                // 첨부파일 이미지 설정 또는 파일 열기 처리 추가
            } else {
                binding.buttonDownload.visibility = View.GONE
            }
        }
    }
}
