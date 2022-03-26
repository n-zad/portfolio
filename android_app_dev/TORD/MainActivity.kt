package com.nick.tord

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val numPlayersEditText: EditText = findViewById(R.id.numPlayersEditText)
        val pickFirstCheckBox: CheckBox = findViewById(R.id.pickFirstCheckBox)
        val seatOrderCheckBox: CheckBox = findViewById(R.id.seatOrderCheckBox)

        val turnRotationSpinner: Spinner = findViewById(R.id.turnRotSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.turn_rotation,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            turnRotationSpinner.adapter = adapter
        }

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            val t0: String = numPlayersEditText.text.toString()
            var t1: Int
            if (t0 == "") {
                t1 = 1
            } else {
                t1 = t0.toInt()
                if (t1 < 1) {
                    t1 = 1
                } else if (t1 > 8) {
                    t1 = 8
                }
            }
            val numPlayers: Int = t1
            val pickFirst: Boolean = pickFirstCheckBox.isChecked
            val seatOrder: Boolean = seatOrderCheckBox.isChecked
            val turnRotation: String = turnRotationSpinner.selectedItem.toString()

            val intent = Intent(this, TordActivity::class.java)
            intent.putExtra("num_players", numPlayers)
            intent.putExtra("pick_first", pickFirst)
            intent.putExtra("seat_order", seatOrder)
            intent.putExtra("turn_rotation", turnRotation)
            startActivity(intent)
        }
    }
}
