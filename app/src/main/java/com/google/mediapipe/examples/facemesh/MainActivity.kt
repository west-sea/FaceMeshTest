package com.google.mediapipe.examples.facemesh

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.mediapipe.examples.facemesh.databinding.ActivityMainBinding
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions
import com.google.mediapipe.solutions.facemesh.FaceMeshResult

class MainActivity : AppCompatActivity() {
    private val TAG: String = javaClass.name
    private lateinit var binding: ActivityMainBinding
    private lateinit var faceMesh: FaceMesh
    private lateinit var cameraInput: CameraInput
    private lateinit var glSurfaceView: SolutionGlSurfaceView<FaceMeshResult>
    private lateinit var ballView: CustomBallView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkCameraPermission()
        Log.i(
            TAG,
            "Android SDK Version -> ${Build.VERSION.SDK_INT} / ${Build.VERSION.CODENAME}"
        )
        setup()
    }

    private fun setup() {
        ballView = CustomBallView(this)
        binding.frameLayout.addView(ballView)

        ballView.bringToFront()


        Log.i(TAG, "Starting setup...")
        faceMesh = FaceMesh(
            this,
            FaceMeshOptions.builder()
                .setStaticImageMode(false)
                .setRefineLandmarks(true)
                .setRunOnGpu(true)
                .build()
        )
        Log.i(TAG, "FaceMesh initialized...")
        faceMesh.setErrorListener { message, _ ->
            Log.e(
                TAG,
                "Error MediaPipe Face Mesh -> $message"
            )
        }
        Log.i(TAG, "FaceMesh initialized... -> Done")

        Log.i(TAG, "Camera check  initializing...")
        cameraInput = CameraInput(this)
        cameraInput.setNewFrameListener {
            faceMesh.send(it)
        }
        Log.i(TAG, "Camera check  initializing... -> Done")

        // Initializes a new Gl surface view with a user-defined FaceMeshResultGlRenderer.
        Log.i(TAG, "GlSurfaceView initializing...")
        glSurfaceView = SolutionGlSurfaceView(
            this,
            faceMesh.glContext,
            faceMesh.glMajorVersion
        )
        Log.i(TAG, "GlSurfaceView initializing... -> Done")
        Log.i(TAG, "Setting up GlSurfaceView...")
        glSurfaceView.setSolutionResultRenderer(FaceMeshResultGlRenderer())
        glSurfaceView.setRenderInputImage(true)
        faceMesh.setResultListener {result ->

            //입 벌림 인식 코드
            result.multiFaceLandmarks()?.forEach { faceLandmarks ->
                // 입 벌림 정도 계산을 위한 랜드마크 인덱스 찾기
                val upperLip = faceLandmarks.landmarkList[13] // 상단 입술 랜드마크
                val lowerLip = faceLandmarks.landmarkList[14] // 하단 입술 랜드마크
                val leftEnd = faceLandmarks.landmarkList[78] // 왼쪽 끝 랜드마크
                val rightEnd = faceLandmarks.landmarkList[308] // 오른쪽 끝 랜드마크

                //공 먹기 구현
                ballView.updateBallsWithMouthCoordinates(upperLip.y, lowerLip.y, leftEnd.x, rightEnd.x)

//                if (ball.top < upperLip.y && ball.bottom > lowerLip.y && ball.left > leftEnd.x && ball.right < rightEnd.x) {
//                    // 공이 입 안에 있으면 사라지게 처리
//                }


                // 입 벌림 정도 계산
                val mouthOpenness = Math.abs(upperLip.y - lowerLip.y)

                // 로그로 입 벌림 정도 출력
                Log.d(TAG, "Mouth Openness: $mouthOpenness")

                // 입 벌림 정도가 0.05를 넘어가면 "MOUTH OPEN" 텍스트 설정
                if (mouthOpenness > 0.05) {
                    runOnUiThread {
                        binding.openmouth.text = "MOUTH OPEN"
                    }
                } else {
                    runOnUiThread {
                        binding.openmouth.text = "" // 또는 다른 상태의 텍스트를 설정할 수 있습니다.
                    }
                }
            }

            glSurfaceView.setRenderData(result)
            glSurfaceView.requestRender()
        }
        Log.i(TAG, "Setting up GlSurfaceView... -> Done")

        // The runnable to start camera after the gl surface view is attached.
        // For video input source, videoInput.start() will be called when the video uri is available.
        glSurfaceView.post {
            startCamera()
        }
        // Updates the preview layout.
        binding.frameLayout.removeAllViewsInLayout()
        binding.frameLayout.addView(glSurfaceView)
        glSurfaceView.visibility = View.VISIBLE
        binding.frameLayout.requestLayout()

        //ballView = CustomBallView(this)
        binding.frameLayout.addView(ballView)
//
        ballView.bringToFront()
    }

    private fun startCamera() {
        cameraInput.start(
            this,
            faceMesh.glContext,
            CameraInput.CameraFacing.FRONT,
            glSurfaceView.width,
            glSurfaceView.height
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkCameraPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) -> {
                val requestPermissionLauncher = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (!isGranted) {
                        Snackbar.make(
                            binding.root,
                            "Grant camera permission in order to use the app",
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                }
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Restarts the camera and the opengl surface rendering.
        cameraInput = CameraInput(this)
        cameraInput.setNewFrameListener {
            faceMesh.send(it)
        }
        glSurfaceView.post { startCamera() }
    }

    override fun onPause() {
        super.onPause()
        cameraInput.close()
    }
}