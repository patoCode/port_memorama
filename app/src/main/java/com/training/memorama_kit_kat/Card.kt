package com.training.memorama_kit_kat

import android.widget.ImageButton
import android.widget.ImageView
import com.wajahatkarim3.easyflipview.EasyFlipView

class Card(val back:ImageButton, val front:ImageButton, val animation: EasyFlipView, val point:Int, var value:Int) {
    fun loadFront(position:Int){
        if(!animation.isFrontSide){
            animation.flipTheView()
        }
        front.scaleType = ImageView.ScaleType.CENTER_CROP
        front.setImageResource(position)
    }
    fun loadBack(position:Int){
        back.scaleType = ImageView.ScaleType.CENTER_CROP
        back.setImageResource(position)
        animation.isEnabled = true
    }
    fun showCard(){
        if(!animation.isFrontSide) {
            animation.flipTheView()
            animation.isEnabled = false
        }
    }
    fun hideCard(){
        if(animation.isFrontSide) {
            animation.flipTheView()
            animation.isEnabled = true
        }
    }
    fun lockFlip(){
        animation.isEnabled = !animation.isEnabled
    }
}