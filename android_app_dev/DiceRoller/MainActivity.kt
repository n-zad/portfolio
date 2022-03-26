package com.nick.diceroller

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlin.random.Random


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val d4in: EditText = findViewById(R.id.d4in)
        val d6in: EditText = findViewById(R.id.d6in)
        val d8in: EditText = findViewById(R.id.d8in)
        val d10in: EditText = findViewById(R.id.d10in)
        val d12in: EditText = findViewById(R.id.d12in)
        val d20in: EditText = findViewById(R.id.d20in)
        val d100in: EditText = findViewById(R.id.d100in)

        val d4out: TextView = findViewById(R.id.d4out)
        val d6out: TextView = findViewById(R.id.d6out)
        val d8out: TextView = findViewById(R.id.d8out)
        val d10out: TextView = findViewById(R.id.d10out)
        val d12out: TextView = findViewById(R.id.d12out)
        val d20out: TextView = findViewById(R.id.d20out)
        val d100out: TextView = findViewById(R.id.d100out)

        val d4 = Dice(4, d4in, d4out)
        val d6 = Dice(6, d6in, d6out)
        val d8 = Dice(8, d8in, d8out)
        val d10 = Dice(10, d10in, d10out)
        val d12 = Dice(12, d12in, d12out)
        val d20 = Dice(20, d20in, d20out)
        val d100 = Dice(100, d100in, d100out)

        val diceList: Array<Dice> = arrayOf(d4, d6, d8, d10, d12, d20, d100)

        val rollButton: Button = findViewById(R.id.rollButton)
        rollButton.setOnClickListener {
            rollAll(diceList)
        }

        val resetButton: Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            resetAll(diceList)
        }
    }

    private fun rollAll(dice_list: Array<Dice>) {
        dice_list.forEach {
            it.roll()
        }
    }

    private fun resetAll(dice_list: Array<Dice>) {
        dice_list.forEach {
            it.reset()
        }
    }
}


class Dice(num: Int, input: EditText, output: TextView) {
    var numDice = input
    var result = output
    var diceType = num

    fun roll() {
        var txt: String = numDice.text.toString()
        if (txt.isBlank()) {
            txt = "0"
        }
        if (txt.toInt() > 0) {
            var res = 0
            if (txt.toInt() == 1) {
                res += Random.nextInt(diceType) + 1
            } else {
                for (i: Int in 1 .. txt.toInt()) {
                    res += Random.nextInt(diceType) + 1
                }
            }

            result.setText(res.toString(), TextView.BufferType.EDITABLE)
        }
    }

    fun reset() {
        numDice.setText("", TextView.BufferType.EDITABLE)
        result.setText("0", TextView.BufferType.EDITABLE)
    }
}
