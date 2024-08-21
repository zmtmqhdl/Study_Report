package com.example.study_report

import BulletinPost
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.study_report.databinding.ActivityPostDetailBinding
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.net.Uri
import android.widget.Toast

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding

    private fun getFileName(uri: Uri): String {
        val filePath = uri.path ?: return "Unknown"
        return filePath.substring(filePath.lastIndexOf('/') + 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 BulletinPost 객체 가져오기
        val post = intent.getParcelableExtra<BulletinPost>("POST_KEY")

        // 데이터가 null이 아닌 경우에만 처리
        post?.let {
            binding.textTitle.text = it.title
            binding.textContent.text = it.content

            // 첨부파일 표시
            if (it.fileUrl != "0") {
                binding.buttonDownload.visibility = View.VISIBLE
            } else {
                binding.buttonDownload.visibility = View.GONE
            }
        }

        binding.buttonDownload.setOnClickListener {
            val url = post!!.fileUrl
            val fileUri = Uri.parse(url)

            // 파일 이름 추출
            val fileName = getFileName(fileUri)

            // DownloadManager.Request 설정
            val request = DownloadManager.Request(fileUri)
            request.setTitle(fileName)  // 파일 이름을 제목으로 설정
            request.setDescription("파일을 다운로드합니다.")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)  // 다운로드 위치와 파일 이름 설정

            // DownloadManager를 사용하여 다운로드 시작
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(this, "다운로드가 시작되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
