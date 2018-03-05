package zihadrizkyef.com.edittextformoney

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etMoney.setText("Rp ")
        etMoney.addTextChangedListener(object: TextWatcher {
            /**
             * Text to show before money number
             */
            val moneyPrefix = "Rp "


            //DONT EDIT FIELD BELOW
            var prefString = ""
            var prefCursorPos = 0
            val startEditablePos = moneyPrefix.length

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                prefString = s.toString()
                prefCursorPos = etMoney.selectionStart
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    etMoney.removeTextChangedListener(this)

                    var cursorPos = etMoney.selectionStart

                    //prevent user to write or delete in the $moneyPrefix text
                    if (prefCursorPos < startEditablePos || (cursorPos < prefCursorPos && cursorPos < startEditablePos)) {
                        etMoney.setText(prefString)
                        etMoney.setSelection(prefCursorPos)
                    } else {
                        //delete money prefix
                        var filteredText = it.substring(moneyPrefix.length)
                        cursorPos = Math.max(0, cursorPos - moneyPrefix.length)

                        //delete all dot
                        for (i in cursorPos - 1 downTo 0) {
                            if (filteredText[i] == '.') {
                                cursorPos -= 1
                            }
                        }
                        filteredText = filteredText.replace(".", "")

                        //add new dot
                        val dotBuilder = StringBuilder(filteredText)
                        if (filteredText.length > 3) {
                            for (i in filteredText.length - 3 downTo 1 step 3) {
                                if (i < cursorPos) {
                                    cursorPos += 1
                                }
                                dotBuilder.insert(i, ".")
                            }
                        }
                        filteredText = dotBuilder.toString()

                        //add money prefix
                        filteredText = moneyPrefix + filteredText
                        cursorPos += moneyPrefix.length

                        Log.i("AOEU", "length ${filteredText.length} pos $cursorPos")
                        etMoney.setText(filteredText)
                        etMoney.setSelection(cursorPos)
                    }

                    etMoney.addTextChangedListener(this)
                }
            }
        })
    }
}
