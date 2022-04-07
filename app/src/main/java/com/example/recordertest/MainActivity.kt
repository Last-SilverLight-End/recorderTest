package com.example.recordertest

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper.prepare
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    private val recordButton : RecordButton by lazy {
        findViewById(R.id.recordButton)
    }
    // 메모리 관리로 null 로 둠
    private var recorder : MediaRecorder ? = null
    private var state_record = State.BEFORE_RECORDING
        set(value){
            field = value
            recordButton.updateIconWithState(value)
        }
    private var player : MediaPlayer? = null
    private val recordingFilePathway : String by lazy{
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }
    private val requiredPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission()
        initViews()
        bindViews()
    }


      /*private val requestPermissionLauncher =
          registerForActivityResult(
              ActivityResultContracts.RequestPermission()
          ){
              isGranted: Boolean->
              if(isGranted) {

              }
              else{
                  finish()
              }
          }
          private fun requestAudioPermission() {
              requestPermissionLauncher.launch(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
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



    // MediaRecorder apply deprecated 되었으므로 밑의 형식과 같이 바꾼다.
    private fun initViews() {
        recordButton.updateIconWithState(state_record)
    }

    private fun bindViews(){
        recordButton.setOnClickListener{
            when(state_record){
                State.BEFORE_RECORDING ->{
                    startRecording()
                }
                State.ON_RECORDING->{
                    stopRecording()
                }
                State.AFTER_RECORDING ->{
                    startPlaying()
                }
                State.ON_PLAYING ->{
                    stopPlaying()
                }
            }
        }
    }

    private fun startRecording(){
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder?.setOutputFile(recordingFilePathway)
        prepare()

        recorder?.start()
        state_record = State.ON_RECORDING
    }
    private fun startPlaying(){
        try {
            player?.setDataSource(recordingFilePathway)
            player?.prepare()
        }
        catch (e : IOException)
        {
            println(e)

        }
        catch(e : IllegalArgumentException){
            println(e)
        }
        player?.start();
        state_record= State.ON_PLAYING
    }
    private fun stopPlaying(){
        player?.stop();
        player?.release()
        player = null
        state_record = State.AFTER_RECORDING
    }

    private fun stopRecording(){
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        state_record = State.AFTER_RECORDING
    }

    companion object{
        private const val REQUEST_RECORD_AUDIO_PERMISSION =201
    }
}