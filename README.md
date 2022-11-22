# TextWatcher For Money / EditText For Money

This is a class that extends TextWatcher which is help you to make Edit Text which is contain money-formated Text

### MainFeature
- Auto Dot separator
- Money currency
- Edit in the middle of money text will not move the cursor position to end of text

![EditTextForMoney.gif](https://github.com/zihadrizkyef/TextWatcherForMoney/blob/master/EditTextForMoney.gif)

### How to use it?
Just add a text listener to your edit text with this text watcher

    val textWatcher = CurrencyTextWatcher(
        "$ ",
        '.',
        ',',
    )
    textWatcher.isShowZero = true //if "0" should be shown when no data inserted
    textWatcher.applyTo(binding.edit1)
    binding.button1.setOnClickListener {
        Log.i("AOEU", "anu1 ${textWatcher.value}")
    }

And you're go! :D
