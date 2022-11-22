package com.zref.experiment

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.widget.EditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.max

class CurrencyTextWatcher(
    var currency: String = "$ ",
    var decimal: Char = '.',
    var separator: Char = ',',
) {
    private var editText: EditText? = null
    var isShowZero = false
        set(value) {
            field = value
            if (editText != null) {
                if (field) {
                    if (editText!!.text.toString() == currency) {
                        editText!!.setText("0")
                    }
                } else {
                    if (editText!!.text.toString() == currency + "0") {
                        editText!!.setText(currency)
                    }
                }
            }
        }

    fun applyTo(editText: EditText) {
        this.editText = editText

        editText.inputType = InputType.TYPE_CLASS_PHONE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        if (isShowZero) {
            editText.setText(currency + "0")
        } else {
            editText.setText(currency)
        }
        editText.setSelection(currency.length)
        editText.keyListener = DigitsKeyListener.getInstance("0123456789$decimal")

        editText.addTextChangedListener(object : TextWatcher {
            private var prefString = ""
            private var prefCursorStartPos = -1
            private var prefCursorEndPos = -1
            private var startEditablePos = currency.length

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
                    var textToFormat = it.toString()

                    //prevent to delete or write in [currency]
                    if (prefCursorStartPos <= startEditablePos) {
                        if (actionIsWrite) {
                            val textInserted = it.substring(start until start + lengthInserted)
                            textToFormat = currency + textInserted + prefString.substring(currency.length)
                            cursorPos = currency.length + textInserted.length
                        } else if (actionIsDelete) {
                            cursorPos = currency.length
                            if (prefString.length > currency.length) { //If edit text is not just [currency] (there's number)
                                if (prefCursorEndPos == -1 || prefCursorEndPos == prefCursorStartPos) { //If not selecting text
                                    textToFormat = currency + prefString.substring(currency.length + 1) //delete first number
                                } else { //If selecting part or all text
                                    textToFormat = currency + prefString.substring(prefCursorEndPos) //delete selected number but keep [currency]
                                }
                            } else {
                                textToFormat = currency
                            }
                        }
                    }

                    //prevent to write decimal as first char
                    if (actionIsWrite && textToFormat.startsWith(currency + decimal)) {
                        textToFormat = textToFormat.replaceFirst(Regex("$currency(\\$decimal)+"), currency)
                        cursorPos = startEditablePos
                    }

                    if (textToFormat.length > currency.length) {
                        //delete money prefix
                        var filteredText = textToFormat.substring(currency.length)
                        cursorPos = max(0, cursorPos - currency.length)

                        //use only first decimal symbol
                        while (filteredText.count { it == decimal } > 1) {
                            val lastDecimalPos = filteredText.lastIndexOf(decimal)
                            filteredText = filteredText.removeLastChar(decimal)
                            if (lastDecimalPos < cursorPos) {
                                cursorPos--
                            }
                        }

                        //delete all $separator and use first decimal symbol
                        for (i in cursorPos - 1 downTo 0) {
                            if (filteredText[i] == separator) {
                                cursorPos--
                            }
                        }
                        filteredText = filteredText.replace(separator.toString(), "")

                        //delete 0 in the begining
                        var zeroCount = 0
                        val prefCursorPos = cursorPos
                        for (i in filteredText.indices) {
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
                        val decimalPos = filteredText.indexOf(decimal)
                        val separatorStart = if (decimalPos > -1) decimalPos - 3 else (filteredText.length - 3)
                        for (i in separatorStart downTo 1 step 3) {
                            if (i < cursorPos) {
                                cursorPos += 1
                            }
                            separatorInserter.insert(i, separator)
                        }
                        filteredText = separatorInserter.toString()

                        //add currency
                        filteredText = currency + filteredText
                        cursorPos += currency.length

                        if (filteredText == currency && isShowZero) {
                            filteredText += "0"
                        }
                        editText.setText(filteredText)
                        editText.setSelection(cursorPos)
                    } else {
                        if (textToFormat == currency && isShowZero) {
                            textToFormat += "0"
                        }
                        editText.setText(textToFormat)
                        editText.setSelection(cursorPos)
                    }

                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    val value: Double
        get() {
            if (editText == null) return 0.0

            val numberAndSymbol = editText!!.text.toString().dropWhile { !it.isDigit() && it != decimal && it != separator }
            return if (numberAndSymbol.isNotBlank()) {
                val symbolFormatter = DecimalFormatSymbols()
                symbolFormatter.groupingSeparator = separator
                symbolFormatter.decimalSeparator = decimal
                val formatter = DecimalFormat()
                formatter.decimalFormatSymbols = symbolFormatter
                formatter.parse(numberAndSymbol)!!.toDouble()
            } else {
                0.0
            }
        }

    fun String.removeLastChar(char: Char): String {
        return substringBeforeLast(char) + substringAfterLast(char)
    }
}