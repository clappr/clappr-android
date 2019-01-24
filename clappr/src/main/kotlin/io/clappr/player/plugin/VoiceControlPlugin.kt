package io.clappr.player.plugin

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.plugin.core.UICorePlugin

class VoiceControlPlugin(core: Core) : UICorePlugin(core) {
    companion object : NamedType {
        private const val MAX_RESULTS = 2
        private const val MINIMUM_LENGTH_MILLIS = 100000

        override val name = "voiceControlPlugin"

        val entry = PluginEntry.Core(name = name, factory = { core -> VoiceControlPlugin(core) })
    }

    private var recognizer: SpeechRecognizer? = null
    private var recognizeIntent: Intent? = null

    init {
        initRecognizer()

        recognizer?.startListening(recognizeIntent)
    }

    private fun initRecognizer() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)

        recognizeIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        recognizeIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        recognizeIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, applicationContext.packageName)
        recognizeIntent!!.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS) // Optional
        recognizeIntent!!.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, MINIMUM_LENGTH_MILLIS)

        recognizer!!.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
                Log.d("@@@", "onReadyForSpeech - $p0")
            }

            override fun onRmsChanged(p0: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {
                Log.d("@@@", "onBufferReceived - $p0")
            }

            override fun onPartialResults(p0: Bundle?) {
                Log.d("@@@", "onPartialResults - $p0")
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
                Log.d("@@@", "onEvent - $p0")
            }

            override fun onBeginningOfSpeech() {
                Log.d("@@@", "onBeginningOfSpeech")
            }

            override fun onEndOfSpeech() {
                Log.d("@@@", "onEndOfSpeech")
            }

            override fun onError(p0: Int) {
                Log.d("@@@", "onError - ${getErrorText(p0)}")
                recognizer?.startListening(recognizeIntent)
            }

            override fun onResults(command: Bundle?) {
                Log.d("@@@", "onResults - $command")
                var resultStringArrayList = command?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d("@@@", command?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).toString())
                recognizer?.startListening(recognizeIntent)
                if (resultStringArrayList?.get(0).equals("pause")) {
                    core.activePlayback?.pause()
                }
                if (resultStringArrayList?.get(0).equals("play")) {
                    core.activePlayback?.play()
                }
                if (resultStringArrayList?.get(0).equals("stop")) {
                    core.activePlayback?.stop()
                }
            }

        })
    }

    fun getErrorText(errorCode: Int): String {
        val message: String
        when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> message = "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> message = "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> message = "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message = "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> message = "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> message = "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "No speech input"
            else -> message = "Didn't understand, please try again."
        }
        return message
    }
}