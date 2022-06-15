package com.training.memorama_kit_kat

import android.R.*
import android.animation.AnimatorSet
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker.OnValueChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.training.memorama_kit_kat.R.drawable.*
import com.training.memorama_kit_kat.databinding.ActivityMainBinding
import com.wajahatkarim3.easyflipview.EasyFlipView
import java.util.*


class MainActivity : AppCompatActivity(){

    private lateinit var binding : ActivityMainBinding
    /* ANIMATION */
    private lateinit var frontAnimation:AnimatorSet
    private lateinit var backAnimation:AnimatorSet
    var isFront:Boolean = true
    var scale:Float = 0f

    /* VARIABLES AUXILIARES*/
    private var _qty = 12
    private var _pointPerCard = 1
    private var _scoreGoals:Int = 0
    private var _scoreFails:Int = 0
    private var _lives:Int = 3
    private var _level:Int = 2
    private var _backgroundPanel : Int = 0 // fondo
    private val _panelImages = arrayOfNulls<ImageButton>(_qty)
    private val _panelImagesFront = arrayOfNulls<ImageButton>(_qty)
    private val _animation = arrayOfNulls<EasyFlipView>(_qty)
    private val _elementsList = arrayOfNulls<Card>(_qty)

    /* AUXILIARES - GAMEPLAY */
    private var _baseArrayShuffle = arrayListOf<Int>()
    var _cardsImgs = arrayListOf<Int>()
    private var _baseCard: ImageButton? = null
    private var _firstCard :Card? = null
    private var _secondCard :Card? = null
    private var _baseImg :Int = 0
    private var _matchImg :Int = 0
    var _lockPanel:Boolean = false
    /* DELAY */
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.mbtLevel!!.addOnButtonCheckedListener{ group, checkerId, isChecked ->
            if(isChecked){
                when(checkerId){
                    R.id.btnEasy -> {
                        _level = 3
                        init()
                        Log.d("JJ","LEVEL "+_level)
                    }
                    R.id.btnMedium -> {
                        _level = 2
                        init()
                    }
                    R.id.btnHard -> {
                        _level = 1
                        init()
                    }
                }
            }else{
                if(binding.mbtLevel!!.checkedButtonId == View.NO_ID){
                    Toast.makeText(this, "Iniciara por defecto en el nivel intermedio",Toast.LENGTH_LONG).show()
                    _level = 2
                    init()
                }
            }
        };
        setContentView(binding.root)
        actionButtons()
        init()
    }
    fun actionButtons() {
       binding.btnReset.setOnClickListener(View.OnClickListener { init() })
//        binding.btnPower.setOnClickListener(View.OnClickListener { finish() })
    }
    private fun _loadPanel(){
        _elementsList[0] = Card(binding.ib11, binding.ibF11!!, binding.easyFlipView0!!,0,0)
        _elementsList[1] = Card(binding.ib12, binding.ibF12!!, binding.easyFlipView1!!,0,0)
        _elementsList[2] = Card(binding.ib13, binding.ibF13!!, binding.easyFlipView2!!,0,0)
        _elementsList[3] = Card(binding.ib14, binding.ibF14!!, binding.easyFlipView3!!,0,0)
        _elementsList[4] = Card(binding.ib21, binding.ibF21!!, binding.easyFlipView4!!,0,0)
        _elementsList[5] = Card(binding.ib22, binding.ibF22!!, binding.easyFlipView5!!,0,0)
        _elementsList[6] = Card(binding.ib23, binding.ibF23!!, binding.easyFlipView6!!,0,0)
        _elementsList[7] = Card(binding.ib24, binding.ibF24!!, binding.easyFlipView7!!,0,0)
        _elementsList[8] = Card(binding.ib31, binding.ibF31!!, binding.easyFlipView8!!,0,0)
        _elementsList[9] = Card(binding.ib32, binding.ibF32!!, binding.easyFlipView9!!,0,0)
        _elementsList[10] = Card(binding.ib33, binding.ibF33!!, binding.easyFlipView10!!,0,0)
        _elementsList[11] = Card(binding.ib34, binding.ibF34!!, binding.easyFlipView11!!,0,0)
    }
    private fun _reset(){
        _scoreGoals = 0
        _lives = 3
        _lockPanel = false
        _firstCard = null
        binding.tvScore.text = _scoreGoals.toString()
        binding.tvLive.text = _lives.toString()
    }
    private fun _loadCardsImg(){
        _cardsImgs.clear()
        _cardsImgs.addAll(
            listOf(f01,
                f02,
                f03,
                f04,
                f05,
                f06
            )
        )
        _backgroundPanel  = R.drawable.fondo
    }
    private fun _shuffleCards(): ArrayList<Int> {
        //Log.d("TEST","SUFFLE: "+ Arrays.toString(res.toArray()))
        val res: ArrayList<Int> = ArrayList<Int>()
        for (i in 0 until _qty)
            res.add(i % _qty / 2)
        res.shuffle()
        Log.d("TEST","SUFFLE: "+ Arrays.toString(res.toArray()))
        return res
    }
    private fun _evaluate(i:Int, _elementFlip:Card){
        if(_firstCard == null){
            _firstCard = _elementFlip
            _elementFlip.lockFlip()
            _elementFlip.showCard()
        }else{
            _lockPanel = true
            _secondCard = _elementFlip
            _elementFlip.lockFlip()
            _elementFlip.showCard()
            if( _firstCard!!.value === _secondCard!!.value ){
                _firstCard = null
                _lockPanel = false
                _scoreGoals++
                binding.tvScore.text = _scoreGoals.toString()
                if(_scoreGoals === _cardsImgs.size){
                    _showWinDialog()
                }
            }else{
                handler.postDelayed({
                    _lives--
                    if(_lives == 0){
                        _showEndDialog()
                    }else{
                        _lockPanel = false
                        _firstCard!!.hideCard()
                        _firstCard = null
                        _secondCard!!.hideCard()
                        _secondCard = null
                        binding.tvLive.text = _lives.toString()
                        _scoreFails++
                    }
                }, 1500)
            }
        }
    }
    private fun init(){
        _loadPanel()
        _loadCardsImg()
        _baseArrayShuffle = _shuffleCards()
        for (i in 0 until _qty){
            _elementsList[i]!!.value = _baseArrayShuffle[i]
            _elementsList[i]!!.loadFront(_cardsImgs[_baseArrayShuffle[i]])
        }
        handler.postDelayed({
            for (i in 0 until _qty){
                _elementsList[i]!!.loadBack(_backgroundPanel)
                _elementsList[i]!!.hideCard()
            }
        },(_level * 1000).toLong())
        Log.d("JJ", (_level * 1000).toString())
        for (i in 0 until _qty){
            _elementsList[i]!!.back.setOnClickListener {
                if(!_lockPanel){
                    _evaluate(i, _elementsList[i]!!)
                }
            }
        }
        _reset()
    }
    private fun _showEndDialog() {
        val view = View.inflate(this@MainActivity, R.layout.dialog_game_lose, null)
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var btn : Button = view.findViewById<Button>(R.id.btnLoseOk)
        btn.setOnClickListener {
            dialog.dismiss()
            init()
        }
    }
    private fun _showWinDialog() {
        val view = View.inflate(this@MainActivity, R.layout.dialog_game_win, null)
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var btn : Button = view.findViewById<Button>(R.id.btnWinOk)
        btn.setOnClickListener {
            dialog.dismiss()
            init()
        }
    }

}