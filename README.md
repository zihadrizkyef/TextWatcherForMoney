# TextWatcher For Money / EditText For Money

This is a class that extends TextWatcher which is help you to make Edit Text which is contain money-formated Text

### MainFeature
- Auto Dot separator
- Money currency
- Edit in the middle of money text will not move the cursor position to end of text

![EditTextForMoney.gif](https://github.com/zihadrizkyef/TextWatcherForMoney/blob/master/EditTextForMoney.gif)

### How to use it?
Just add a text listener to your edit text with this text watcher

    etMoney.addTextChangedListener(TWMoney(etMoney, "Rp ", false))

And you're go! :D
