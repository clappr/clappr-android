package io.clappr.player.plugin

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.log.Logger
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

        recognizeIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
        recognizeIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, applicationContext.packageName)
        recognizeIntent?.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS) // Optional
        recognizeIntent?.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, MINIMUM_LENGTH_MILLIS)

        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onRmsChanged(p0: Float) {}
            override fun onReadyForSpeech(bundle: Bundle?) { Logger.debug(name, "onReadyForSpeech - $bundle") }
            override fun onBufferReceived(buffer: ByteArray?) { Logger.debug(name, "onBufferReceived - $buffer") }
            override fun onPartialResults(bundle: Bundle?) { Logger.debug(name, "onPartialResults - $bundle") }
            override fun onEvent(event: Int, bundle: Bundle?) { Logger.debug(name, "onEvent - $bundle") }
            override fun onBeginningOfSpeech() { Logger.debug(name, "onBeginningOfSpeech") }
            override fun onEndOfSpeech() { Logger.debug(name, "onEndOfSpeech") }

            override fun onResults(bundle: Bundle?) {
                bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { resultList ->
                    Logger.debug(name, resultList.toString())

                    resultList.forEach {
                        if (it.toUpperCase().contains("GLOBO")) {
                            when {
                                it.toUpperCase().contains("PAUSE") -> core.activePlayback?.pause()
                                it.toUpperCase().contains("PLAY") -> core.activePlayback?.play()
                                it.toUpperCase().contains("STOP") -> core.activePlayback?.stop()
                            }
                        }
                    }
                }
                
                recognizer?.startListening(recognizeIntent)
            }

            override fun onError(errorCode: Int) {
                Logger.debug(name, "onError - ${errorText(errorCode)}")
                recognizer?.startListening(recognizeIntent)
            }
        })
    }

    private fun errorText(errorCode: Int) = when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "No match"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
        SpeechRecognizer.ERROR_SERVER -> "error from server"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
        else -> "Didn't understand, please try again."
    }
}