package com.nick.tord

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlin.random.Random

class TordActivity : Activity() {

    private var npst: List<Any> = listOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tord)

        npst = listOf(
            intent.getIntExtra("num_players", 1),
            intent.getBooleanExtra("pick_first", false),
            intent.getBooleanExtra("seat_order", false),
            intent.getStringExtra("turn_rotation")
        )

        val p1: TextView = findViewById(R.id.p1TextView)
        val p2: TextView = findViewById(R.id.p2TextView)
        val p3: TextView = findViewById(R.id.p3TextView)
        val p4: TextView = findViewById(R.id.p4TextView)
        val p5: TextView = findViewById(R.id.p5TextView)
        val p6: TextView = findViewById(R.id.p6TextView)
        val p7: TextView = findViewById(R.id.p7TextView)
        val p8: TextView = findViewById(R.id.p8TextView)

        val pList: List<TextView> = listOf(p1, p2, p3, p4, p5, p6, p7, p8)

        val firstPlayer: TextView = findViewById(R.id.firstPlayerTextView)
        val turnRotation: TextView = findViewById(R.id.TurnRotTextView)

        firstPlayer.text = getString(R.string.counter_clockwise)

        val regenerateButton: Button = findViewById(R.id.regenerateButton)
        regenerateButton.setOnClickListener {
            generate(pList, firstPlayer, turnRotation, npst[0] as Int,
                npst[1] as Boolean, npst[2] as Boolean, npst[3] as String)
        }

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        findViewById<TextView>(R.id.testTextView).text = npst.toString()
        generate(pList, firstPlayer, turnRotation, npst[0] as Int,
            npst[1] as Boolean, npst[2] as Boolean, npst[3] as String)
    }

    private fun generate(pL: List<TextView>, fP: TextView, tR: TextView, numPlayers: Int,
                         genFirst: Boolean, genSeats: Boolean, turnRot: String) {
        val newPList: List<TextView> = pL.subList(0, numPlayers)

        if (genFirst) {
            //randomly pick first
            val n: Int = Random.nextInt(1, numPlayers + 1)
            fP.text = getString(R.string.player, n)
        } else {
            //p1 is first
            val n = 1
            fP.text = getString(R.string.player, n)
        }

        if (genSeats) {
            //randomly assign spots
            val shuffledPList: List<TextView> = newPList.shuffled()
            for ((n: Int, p: TextView) in shuffledPList.withIndex()) {
                val a: Int = n + 1
                p.text = getString(R.string.player, a)
            }
        } else {
            //place p's in order
            for ((n: Int, p: TextView) in newPList.withIndex()) {
                val a: Int = n + 1
                p.text = getString(R.string.player, a)
            }
        }

        if (turnRot == "Random") {
            //Randomly choose direction
            if (Random.nextInt(2) == 0) {
                tR.text = getString(R.string.clockwise)
            } else {
                tR.text = getString(R.string.counter_clockwise)
            }
        } else {
            //Output the direction
            tR.text = turnRot
        }
    }
}