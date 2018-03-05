package zihadrizkyef.com.edittextformoney

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val moneyPrefix = "Rp "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etMoney.setText("${moneyPrefix}5.000.000.000.000")
        etMoney.setSelection(moneyPrefix.length)

        etMoney.addTextChangedListener(object: TextWatcher {
            var prefString = ""
            var prefCursorPosition = 0
            val startEditablePosition = moneyPrefix.length

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                prefString = s.toString()
                prefCursorPosition = etMoney.selectionStart
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    etMoney.removeTextChangedListener(this)
                    val currentPosition = etMoney.selectionStart
                    if (prefCursorPosition < startEditablePosition ||
                            (currentPosition < prefCursorPosition && currentPosition<startEditablePosition)) { //prevent user to write or delete in the $moneyPrefix text
                        etMoney.setText(prefString)
                        etMoney.setSelection(prefCursorPosition)
                    } else {
                        var cursorPos = etMoney.selectionStart
                        val strBuilder = StringBuilder(it)

                        var deleted = 0
                        for (i in startEditablePosition .. it.length - 2) {
                            val charAt = strBuilder.substring(i - deleted, i - deleted + 1)
                            if (charAt == ".") {
                                strBuilder.deleteCharAt(i - deleted)
                                deleted++
                                if (i - deleted < cursorPos) {
                                    cursorPos -= 1
                                }
                            }
                        }

                        val filteredText = strBuilder.toString()

                        if (filteredText.length > 3+startEditablePosition) {
                            for (i in filteredText.length - 3 downTo startEditablePosition+1 step 3) {
                                if (i < cursorPos) {
                                    cursorPos += 1
                                }
                                strBuilder.insert(i, ".")
                            }
                        }

                        Log.i("AOEU", "length ${strBuilder.toString().length} pos $cursorPos")
                        etMoney.setText(strBuilder.toString())
                        etMoney.setSelection(cursorPos)
                    }
                    etMoney.addTextChangedListener(this)
                }
            }
        })
    }
}
