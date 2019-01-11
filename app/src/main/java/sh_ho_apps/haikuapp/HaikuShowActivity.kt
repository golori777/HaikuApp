package sh_ho_apps.haikuapp

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.android.synthetic.main.activity_haiku_show.*
import android.speech.tts.UtteranceProgressListener
import android.os.Build
import android.view.inputmethod.ExtractedText
import java.util.*


class HaikuShowActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    companion object {
        val TAG = "HaikuShowActivity"
    }
    lateinit var tts: TextToSpeech

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_haiku_show)

        var haijinList = resources.obtainTypedArray(R.array.haijin_list)
        var haijinIndex = 0
        var haikuIndex = 0

        tts = TextToSpeech(this, this)

        haiku.setOnClickListener {

            var haijin = resources.obtainTypedArray(haijinList.getResourceId(haijinIndex, 0))

            haijinName.setText(haijin.getString(0))

            var haikuList = resources.obtainTypedArray(haijin.getResourceId(1, 1))

            if (haikuIndex < haikuList.length()) {
                var haikuStr = haikuList.getString(haikuIndex)
                haiku.setText("タップ")
                haiku1.setText(haikuStr.split(",")[0].split(" ")[0])
                haiku2.setText(haikuStr.split(",")[0].split(" ")[1])
                haiku3.setText(haikuStr.split(",")[0].split(" ")[2])
                if (haikuStr.split(",").size > 1) {
//                    haiku.setText(haikuStr.split(",")[1])
                    haiku1rubi.setText(haikuStr.split(",")[1].split(" ")[0])
                    haiku2rubi.setText(haikuStr.split(",")[1].split(" ")[1])
                    haiku3rubi.setText(haikuStr.split(",")[1].split(" ")[2])
                } else {
                    haiku1rubi.setText("")
                    haiku2rubi.setText("")
                    haiku3rubi.setText("")
                }
                haikuIndex++
            } else {
                haikuIndex = 0;
                if (haijinIndex < haijinList.length() -1) {
                    haijinIndex++
                } else {
                    haijinIndex = 0
                }
            }
        }
    }

    private fun speechText(text: String) {
        if (text.isNotEmpty()) {
            if (tts.isSpeaking) {
                tts.stop()
            }
            setSpeechRate(1.0f)
            setSpeechPitch(1.0f)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "messageID")
            setTtsListener()
        }
    }

    // 読み上げの始まりと終わりを取得
    private fun setTtsListener() {
        // android version more than 15th
        if (Build.VERSION.SDK_INT >= 15) {
            val listenerResult = tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String) {
                    Log.d(TAG, "progress on Done $utteranceId")
                }

                override fun onError(utteranceId: String) {
                    Log.d(TAG, "progress on Error $utteranceId")
                }

                override fun onStart(utteranceId: String) {
                    Log.d(TAG, "progress on Start $utteranceId")
                }
            })

            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance progress listener")
            }
        } else {
            Log.e(TAG, "Build VERSION is less than API 15")
        }
    }

    private fun shutDown() {
        if (null != tts) {
            // to release the resource of TextToSpeech
            tts.shutdown()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        shutDown()
    }
    override fun onInit(status: Int) {
        if (TextToSpeech.SUCCESS == status) {
            //言語選択
            val locale = Locale.JAPAN
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.language = locale
            } else {
                Log.d("Error", "Locale")
            }
        } else {
            Log.e(TAG, "failed initialize")
        }
    }

    // 読み上げのスピード
    private fun setSpeechRate(rate: Float) {
        if (null != tts) {
            tts.setSpeechRate(rate)
        }
    }

    // 読み上げのピッチ
    private fun setSpeechPitch(pitch: Float) {
        if (null != tts) {
            tts.setPitch(pitch)
        }
    }
}
