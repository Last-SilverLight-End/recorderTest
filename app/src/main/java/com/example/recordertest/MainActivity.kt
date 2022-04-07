package com.example.recordertest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private val recordButton : RecordButton by lazy {
        findViewById(R.id.recordButton)
    }

    private var state_record = State.BEFORE_RECORDING
    private val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission()
        initViews()
    }


    /*  private val requestPermissionLauncher =
          registerForActivityResult(
              ActivityResultContracts.RequestPermission()
          ){
              isGranted: Boolean->
              if(isGranted) {
                  onStart()
              }
              else{
                  finish()
              }
          }
          private fun requestAudioPermission() {
              requestPermissionLauncher.launch(AUDIO_SERVICE)
         }*/

      //deprecated 된 관계로 위로 대체 작업중
    private fun requestAudioPermission() {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              requestPermissions(requiredPermissions,
              REQUEST_RECORD_AUDIO_PERMISSION
              )
          }
      }

      override fun onRequestPermissionsResult(
              requestCode: Int,
              permissions: Array<out String>,
              grantResults: IntArray
          ) {
              super.onRequestPermissionsResult(requestCode, permissions, grantResults)

              val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                  grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

              if(!audioRecordPermissionGranted){
                  finish()

              }
        }



    private fun initViews() {
        recordButton.updateIconWithState(state_record)
    }

    companion object{
        private const val REQUEST_RECORD_AUDIO_PERMISSION =201
    }
}