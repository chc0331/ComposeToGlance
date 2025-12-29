package com.widgetkit.core.component.reminder.today.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

/**
 * Speech Recognition을 관리하는 클래스
 * SpeechRecognizer를 래핑하여 음성 인식 기능을 제공합니다.
 */
class SpeechRecognitionManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    init {
        initializeSpeechRecognizer()
    }

    /**
     * SpeechRecognizer 초기화
     */
    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createRecognitionListener())
            }
        } else {
            Log.e(TAG, "Speech recognition is not available on this device")
            onError("음성 인식이 이 기기에서 사용할 수 없습니다")
        }
    }

    /**
     * 음성 인식 시작
     */
    fun startListening() {
        if (isListening) {
            Log.w(TAG, "Already listening, ignoring start request")
            return
        }

        val recognizer = speechRecognizer
        if (recognizer == null) {
            Log.e(TAG, "SpeechRecognizer is not initialized")
            onError("음성 인식을 초기화할 수 없습니다")
            return
        }

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR") // 한국어 우선, 없으면 시스템 기본 언어
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            recognizer.startListening(intent)
            isListening = true
            Log.d(TAG, "Started listening for speech")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition", e)
            isListening = false
            onError("음성 인식을 시작할 수 없습니다: ${e.message}")
        }
    }

    /**
     * 음성 인식 중지
     */
    fun stopListening() {
        if (!isListening) {
            return
        }

        try {
            speechRecognizer?.stopListening()
            isListening = false
            Log.d(TAG, "Stopped listening for speech")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping speech recognition", e)
            isListening = false
        }
    }

    /**
     * 음성 인식 취소
     */
    fun cancel() {
        try {
            speechRecognizer?.cancel()
            isListening = false
            Log.d(TAG, "Cancelled speech recognition")
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling speech recognition", e)
            isListening = false
        }
    }

    /**
     * 현재 음성 인식 중인지 확인
     */
    fun isListening(): Boolean = isListening

    /**
     * 리소스 정리
     */
    fun destroy() {
        cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    /**
     * RecognitionListener 생성
     */
    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Beginning of speech detected")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // 음성 레벨 변화 (선택적)
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // 버퍼 수신 (선택적)
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "End of speech detected")
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        // ERROR_NO_MATCH는 심각한 에러가 아니므로 조용히 처리
                        // 사용자가 다시 말할 수 있도록 에러 메시지를 표시하지 않음
                        Log.d(TAG, "Speech recognition ended without match - user may need to speak again")
                        // 에러 콜백을 호출하지 않음
                        return
                    }
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        // 음성이 감지되지 않은 경우도 조용히 처리
                        Log.d(TAG, "Speech timeout - no speech detected")
                        // 에러 콜백을 호출하지 않음
                        return
                    }
                }
                
                // 그 외의 에러는 사용자에게 알림
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "오디오 오류가 발생했습니다"
                    SpeechRecognizer.ERROR_CLIENT -> "클라이언트 오류가 발생했습니다"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "마이크 권한이 필요합니다"
                    SpeechRecognizer.ERROR_NETWORK -> "네트워크 오류가 발생했습니다"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 시간 초과"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "음성 인식기가 사용 중입니다"
                    SpeechRecognizer.ERROR_SERVER -> "서버 오류가 발생했습니다"
                    else -> "알 수 없는 오류가 발생했습니다"
                }
                Log.e(TAG, "Speech recognition error: $error - $errorMessage")
                onError(errorMessage)
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    val recognizedText = matches[0]
                    Log.d(TAG, "Speech recognition result: $recognizedText")
                    onResult(recognizedText)
                } else {
                    Log.w(TAG, "No speech recognition results")
                    onError("음성을 인식할 수 없습니다")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // 부분 결과는 사용하지 않음 (최종 결과만 사용)
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // 이벤트 처리 (선택적)
            }
        }
    }

    companion object {
        private const val TAG = "SpeechRecognitionManager"
    }
}

