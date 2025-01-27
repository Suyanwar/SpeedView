package com.github.anastr.speedview

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_section.*

class SectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                speedView.makeSections(progress)
                textSpeed.text = "$progress"
                randomColors()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        button_random_color.setOnClickListener {randomColors()}

        seekBar.progress = 5
        speedView.speedTo(50f)
    }

    private fun randomColors() {
        val sections = ArrayList(speedView.sections)
        speedView.clearSections() // this will also clear observers

        sections.forEach {
            it.color = Color.rgb((0..255).random(), (0..255).random(), (0..255).random())
        }
        speedView.addSections(sections)


        /*
          the next code is slow, because if you call
          `section.color = ...`
          every time, it will redraw the speedometer and take a lot of time.
          sections are observable by its speedometer, so any change in section will redraw the speedometer.
         */
//        speedView.sections.forEach {
//            it.color = Color.rgb((0..255).random(), (0..255).random(), (0..255).random())
//        }
    }
}
