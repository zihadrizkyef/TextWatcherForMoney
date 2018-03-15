package zihadrizkyef.com.edittextformoney

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText

/**
 * بِسْمِ اللهِ الرَّحْمٰنِ الرَّحِيْمِ
 * Created by zihadrizkyef on 05/03/18.
 */

/**
 * TextWatcher for EditText which is containing money formatted text
 *
 * @param editText : The edit text which is applying this text watcher
 * @param moneyPrefix : Text to show before money text. Might be "$ ", "Rp ", etc.
 * @param showHint : Wheter to show hint or not. If not then moneyPrefix will be shown
 */
class TWMoney(val editText: EditText, var moneyPrefix: String, var showHint: Boolean) : TextWatcher {
    private var prefString = ""
    private var prefCursorPos = 0
    private val startEditablePos = moneyPrefix.length

    init {
        if (editText.text.isEmpty()) {
            if (!showHint) {
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.setText(moneyPrefix)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        prefString = s.toString()
        prefCursorPos = editText.selectionStart
    }

    @SuppressLint("SetTextI18n")
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.let {
            editText.removeTextChangedListener(this)

            var cursorPos = editText.selectionStart

            if (prefString.isEmpty() && showHint) {
                editText.setText(moneyPrefix + it.toString())
                editText.setSelection(editText.text.length)
            } else if (it.toString() == moneyPrefix && showHint) {
                editText.setText("")
            } else if (prefCursorPos < startEditablePos || (cursorPos < prefCursorPos && cursorPos < startEditablePos)) {
                //prevent user to write or delete in the $moneyPrefix text
                editText.setText(prefString + "0")
                editText.setSelection(prefCursorPos)
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

                editText.setText(filteredText)
                editText.setSelection(cursorPos)
            }

            editText.addTextChangedListener(this)
        }
    }
}