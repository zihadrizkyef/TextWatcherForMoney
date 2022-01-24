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
 */
class TWMoney(val editText: EditText) : TextWatcher {
    
     companion object {
        fun applyTo(
            editText: EditText,
            moneyPrefix: String = "$ ",
            separator: Char = '.',
            decimal: Char = ',',
        ) {
            val watcher = TWMoney(editText).apply {
                this.moneyPrefix = moneyPrefix
                this.separator = separator
                this.decimal = decimal
            }
            editText.addTextChangedListener(watcher)
        }
    }
    
    var moneyPrefix = "$ "
        set(value) {
            startEditablePos = value.length
            val oldMoneyPrefix = moneyPrefix
            field = value
            val moneyText = editText.text.toString()
            var startSelectionPos = editText.selectionStart
            editText.setText(moneyText.replaceFirst(oldMoneyPrefix, value))
            if (startSelectionPos > oldMoneyPrefix.length) {
                startSelectionPos -= oldMoneyPrefix.length
                startSelectionPos += value.length
            } else {
                if (startSelectionPos > 0) {
                    startSelectionPos = value.length
                }
            }
            editText.setSelection(startSelectionPos)
        }
    var separator = '.'
        set(value) {
            if (separator == decimal) {
                throw UnsupportedOperationException("separator should not same as decimal")
            } else {
                val moneyText = editText.text.toString()
                val startSelectionPos = editText.selectionStart
                val oldSeparator = separator
                field = value
                editText.setText(moneyText.replace(oldSeparator, value))
                editText.setSelection(startSelectionPos)
            }
        }
    var decimal = ','
        set(value) {
            if (separator == decimal) {
                throw UnsupportedOperationException("decimal should not same as separator")
            } else {
                val moneyText = editText.text.toString()
                val startSelectionPos = editText.selectionStart
                val oldDecimal = decimal
                field = value
                editText.setText(moneyText.replace(oldDecimal, value))
                editText.setSelection(startSelectionPos)
            }
        }

    private var prefString = ""
    private var prefCursorStartPos = -1
    private var prefCursorEndPos = -1
    private var startEditablePos = moneyPrefix.length

    init {
        editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        editText.setText(moneyPrefix)
        editText.setSelection(startEditablePos)
    }

    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        prefString = s.toString()
        prefCursorStartPos = editText.selectionStart
        prefCursorEndPos = editText.selectionEnd
    }

    @SuppressLint("SetTextI18n")
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, lengthInserted: Int) {
        s?.let {
            editText.removeTextChangedListener(this)

            var cursorPos = editText.selectionStart
            val actionIsWrite = (prefString.length < it.length)
            val actionIsDelete = (prefString.length > it.length)
            var textToFormat = it

            //prevent to delete or write in $moneyPrefix
            if (prefCursorStartPos <= startEditablePos) {
                if (actionIsWrite) {
                    val textInserted = it.substring(start until start + lengthInserted)
                    textToFormat = moneyPrefix + textInserted + prefString.substring(moneyPrefix.length)
                    cursorPos = moneyPrefix.length + textInserted.length
                } else if (actionIsDelete) {
                    cursorPos = moneyPrefix.length
                    if (prefString.length > moneyPrefix.length) { //If edit text is not just $moneyPrefix (there's number)
                        if (prefCursorEndPos == -1 || prefCursorEndPos == prefCursorStartPos) { //If not selecting text
                            textToFormat = moneyPrefix + prefString.substring(moneyPrefix.length + 1) //delete first number
                        } else { //If selecting part or all text
                            textToFormat = moneyPrefix + prefString.substring(prefCursorEndPos) //delete selected number but keep $moneyPrefix
                        }
                    } else {
                        textToFormat = moneyPrefix
                    }
                }
            }

            if (textToFormat.length > moneyPrefix.length) {
                //delete money prefix
                var filteredText = textToFormat.substring(moneyPrefix.length)
                cursorPos = Math.max(0, cursorPos - moneyPrefix.length)

                //delete all $separator
                for (i in cursorPos - 1 downTo 0) {
                    if (filteredText[i] == separator) {
                        cursorPos -= 1
                    }
                }
                filteredText = filteredText.replace(separator.toString(), "")

                //delete 0 in the begining
                var zeroCount = 0
                val prefCursorPos = cursorPos
                for (i in 0 until filteredText.length) {
                    if (filteredText[i] == '0') {
                        zeroCount++
                        if (i < prefCursorPos) {
                            cursorPos--
                        }
                    } else {
                        break
                    }
                }
                filteredText = filteredText.substring(zeroCount)

                //add new separator
                val separatorInserter = StringBuilder(filteredText)
                if (filteredText.length > 3) {
                    for (i in filteredText.length - 3 downTo 1 step 3) {
                        if (i < cursorPos) {
                            cursorPos += 1
                        }
                        separatorInserter.insert(i, separator)
                    }
                }
                filteredText = separatorInserter.toString()

                //add money prefix
                filteredText = moneyPrefix + filteredText
                cursorPos += moneyPrefix.length

                editText.setText(filteredText)
                editText.setSelection(cursorPos)
            } else {
                editText.setText(textToFormat)
                editText.setSelection(cursorPos)
            }

            editText.addTextChangedListener(this)
        }
    }
}
