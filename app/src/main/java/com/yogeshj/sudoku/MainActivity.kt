package com.yogeshj.sudoku

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yogeshj.sudoku.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.random.nextInt



class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    private var choose:Boolean=true
    private var currNum:Int=-1
    private lateinit var buttons: Array<TextView>

    private lateinit var btns: Array<TextView>
    private var grid:Array<IntArray> = Array(9) { IntArray(9) }

    private var moves:ArrayList<Pair> = ArrayList()
    private var mistakes:Int=0

    private var posOfCell:ArrayList<ArrayList<kotlin.Pair<Int,Int>>>  = ArrayList()
    private var posOfFilledCell:ArrayList<ArrayList<kotlin.Pair<Int,Int>>>  = ArrayList()


    var arr=Array<Int>(9){0}

    private var score:Int=0

    private var empty:Int=0

    private var difficulty:String=""

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var startTime: Long = 0L

//    override fun onStart() {
//        super.onStart()
//        timer.start()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        timer.cancel()
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        for(i in 0..8)
        {
            posOfCell.add(ArrayList())
            posOfFilledCell.add(ArrayList())
        }

        difficulty=intent.getStringExtra("level").toString()
        when(difficulty){
            "easy"->binding.level.text="Easy"
            "medium"->binding.level.text="Medium"
            "hard"->binding.level.text="Hard"
        }

        buttons= arrayOf(
        binding.b1, binding.b2, binding.b3, binding.b4, binding.b5, binding.b6, binding.b7, binding.b8, binding.b9,
        binding.b10, binding.b11, binding.b12, binding.b13, binding.b14, binding.b15, binding.b16, binding.b17, binding.b18,
        binding.b19, binding.b20, binding.b21, binding.b22, binding.b23, binding.b24, binding.b25, binding.b26, binding.b27,
        binding.b28, binding.b29, binding.b30, binding.b31, binding.b32, binding.b33, binding.b34, binding.b35, binding.b36,
        binding.b37, binding.b38, binding.b39, binding.b40, binding.b41, binding.b42, binding.b43, binding.b44, binding.b45,
        binding.b46, binding.b47, binding.b48, binding.b49, binding.b50, binding.b51, binding.b52, binding.b53, binding.b54,
        binding.b55, binding.b56, binding.b57, binding.b58, binding.b59, binding.b60, binding.b61, binding.b62, binding.b63,
        binding.b64, binding.b65, binding.b66, binding.b67, binding.b68, binding.b69, binding.b70, binding.b71, binding.b72,
        binding.b73, binding.b74, binding.b75, binding.b76, binding.b77, binding.b78, binding.b79, binding.b80, binding.b81)

        btns = arrayOf(binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9)

        board()

        getBoard()
        solveSudoku()

        binding.back.setOnClickListener {
            stopTimer()
            finish()
        }

        // counter
        handler = Handler(Looper.getMainLooper())
        startTimer()

        for (i in 0..8)
        {
            for(j in 0..8)
            {
                print("${grid[i][j]} ")
            }
            println()
        }
        for(i in 0..8)
        {
            btns[i].text="${9-arr[i]}"
            if(btns[i].text=="0")
                btns[i].visibility=View.GONE
        }
        clickListenerBtns()
        binding.undo.setOnClickListener {
            if(moves.size>0)
            {
                var pair:Pair=moves.get(moves.size-1)
                pair.second.text=" "
                moves.removeAt(moves.size-1)
                pair.second.setBackgroundResource(R.drawable.blackborder)
                pair.second.setTextColor(resources.getColor(R.color.black))
                arr[pair.first-1]--
                for(i in 0..8)
                {
                    btns[i].text="${9-arr[i]}"
                    if(btns[i].text=="0")
                        btns[i].visibility=View.GONE
                }
            }
        }

        binding.hint.setOnClickListener {
            val temp = (0..80).shuffled()
            for (i in 0..80)
            {
                if(buttons[temp[i]].text==" ")
                {

                    buttons[temp[i]].text="${grid[temp[i] / 9][temp[i] % 9]}"
                    buttons[temp[i]].setBackgroundResource(R.drawable.highlightcell)

                    android.os.Handler().postDelayed({
                        buttons[temp[i]].setBackgroundResource(R.drawable.blackborder)
                    }, 3000)
                    empty--
                    arr[buttons[temp[i]].text.toString().toInt()-1]++
                    for(i in 0..8)
                    {
                        btns[i].text="${9-arr[i]}"
                        if(btns[i].text=="0")
                            btns[i].visibility=View.GONE
                    }
                    if(empty==0)
                    {
                        stopTimer()
                        gameOver()
                    }

                    break
                }
            }
            var flag=true
            for(i in 0..8)
            {
                if(arr[i]!=9)
                {
                    flag=false
                    break
                }
            }
            if(flag)
                gameOver()
        }
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()

        runnable = object : Runnable {
            override fun run() {
                val elapsedMillis = System.currentTimeMillis() - startTime
                val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60

                binding.timer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }

        handler.postDelayed(runnable, 0)
    }

    private fun stopTimer() {
        handler.removeCallbacks(runnable)
    }


    private fun fillBoard() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        for(i in 0..80)
        {
            buttons[i].setOnClickListener {
                if(buttons[i].text==" " && currNum!=-1 && arr[currNum-1]!=9)
                {
                    val row=i/9
                    val col=i%9
                    buttons[i].text=currNum.toString()

                    if(grid[row][col]!=currNum)
                    {
                        buttons[i].setTextColor(resources.getColor(R.color.white))
                        mistakes++;
                        binding.mistake.text="Mistakes:$mistakes/3"
                        if(mistakes>=3){
                            stopTimer()
                            val dialog=AlertDialog.Builder(this@MainActivity)
                            dialog.setMessage("You have made 3 mistakes. Please Try Again!")
                            dialog.setCancelable(false)
                            dialog.setTitle("Game Over")
                            dialog.setPositiveButton("OK"){_,_->

                                finish()
                            }
                            val box=dialog.create()
                            box.show()
                        }
                        moves.add(Pair(currNum,buttons[i]))
                        buttons[i].setBackgroundResource(R.drawable.error_cell)

                        //vibrate
                        if (vibrator.hasVibrator()) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        500,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                vibrator.vibrate(500)
                            }
                        }
                    }
                    else
                    {
                        score+=50
                        binding.score.text="Score:$score"
                        posOfFilledCell.get(currNum-1).add(kotlin.Pair(row,col))

                        buttons[row*9+col].setBackgroundResource(R.drawable.highlight_filled_cell)


                    }
                    arr[currNum-1]++
                    var flag=true
                    for(i in 0..8)
                    {
                        btns[i].text="${9-arr[i]}"
                        if(btns[i].text=="0")
                            btns[i].visibility=View.GONE
                        if(arr[i]!=9)
                            flag=false
                    }
                    if(flag)
                        gameOver()
                    empty--
                    if (empty==0)
                    {
                        stopTimer()
                        gameOver()
                    }




                }
            }
        }

    }

    private fun gameOver() {
        stopTimer()
        val dialog=AlertDialog.Builder(this@MainActivity)
        dialog.setMessage("Congratulations! Sudoku Solved Successfully.\nYour time: ${binding.timer.text}")
        dialog.setCancelable(false)
        dialog.setTitle("Game Over")
        dialog.setPositiveButton("OK"){_,_->
            finish()
        }
        val box=dialog.create()
        box.show()
        Toast.makeText(this@MainActivity,"Sudoku completed",Toast.LENGTH_LONG).show()
    }

    private fun clickListenerBtns() {
        val greenBorder = ContextCompat.getDrawable(this, R.drawable.blueborder)
        val orangeBorder = R.drawable.whiteborder



        for (i in 0..8) {
            btns[i].setOnClickListener {
                val currentDrawable = btns[i].background
                if (currentDrawable.constantState == greenBorder?.constantState && btns[i].text!="0" && arr[i]!=9) {
                    for(i in 0..8)
                    {
                        btns[i].setBackgroundResource(R.drawable.blueborder)
                    }
                    btns[i].setBackgroundResource(orangeBorder)
                    if(currNum!=-1)
                    {
                        for(value in posOfCell[currNum-1])
                        {
                            val row=value.first
                            val col=value.second
                            buttons[row*9+col].setBackgroundResource(R.drawable.fillcell)

                        }
                        for(value in posOfFilledCell[currNum-1])
                        {
                            val row=value.first
                            val col=value.second
                            buttons[row*9+col].setBackgroundResource(R.drawable.blackborder)

                        }
                    }

                    currNum=(i+1)
//                    Toast.makeText(this@MainActivity,"Choosen no: $currNum",Toast.LENGTH_LONG).show()
                    fillBoard()

                    //row
                    for(value in posOfCell[i])
                    {
                        val row=value.first
                        val col=value.second
                        buttons[row*9+col].setBackgroundResource(R.drawable.highlightcell)

                    }
                    for(value in posOfFilledCell[i])
                    {
                        val row=value.first
                        val col=value.second
                        buttons[row*9+col].setBackgroundResource(R.drawable.highlight_filled_cell)

                    }
                }
            }
        }



    }

    private fun solveSudoku():Boolean {
        for(i in 0..8)
        {
            for(j in 0..8)
            {
                if(grid[i][j]==0)
                {
                    for(k in 1..9)
                    {
                        if(checkValid(i,j,k))
                        {
                            grid[i][j]=k
                            if(solveSudoku())
                                return true
                            else
                                grid[i][j]=0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun checkValid( row: Int, col: Int, num: Int): Boolean {
        val size=3
        for(i in 0..8)
        {
            if(grid[row][i]==num)
                    return false
            if(grid[i][col]==num)
                    return false
            if(grid[size*(row/size)+(i/size)][size*(col/size)+(i%size)]==num)
                    return false
        }
        return true
    }

    private fun getBoard() {

        for(i in 0..8)
        {
            for(j in 0..8)
            {
                val ind=i*9+j
                if(buttons[ind].text!=" ")
                {
                    grid[i][j]=buttons[ind].text.toString().toInt()
                    arr[grid[i][j]-1]++
                    posOfCell.get(grid[i][j]-1).add(Pair(i,j))
                }
                else
                    grid[i][j]=0
            }
        }

    }


    private fun board() {
        val nums = (1..9).shuffled()
        val grids = Array(9) { IntArray(9) }

        for (i in 0 until 9) {
            for (j in 0 until 9) {
                grids[i][j] = nums[(i * 3 + i / 3 + j) % 9]
            }
        }

        val flatGrid = grids.flatMap { it.toList() }

        for (i in flatGrid.indices) {
            val value = flatGrid[i]
            buttons[i].text = value.toString()
            buttons[i].setBackgroundResource(R.drawable.fillcell)
        }

        // Randomly remove cells
        var cellsToRemove = 0
        when (difficulty) {
            "easy" -> cellsToRemove = 28
            "medium" -> cellsToRemove = 35
            "hard" -> cellsToRemove = 45
        }

        val removedCells = mutableSetOf<Int>()
        var empty = 0

        repeat(cellsToRemove) {
            var r: Int
            do {
                r = Random.nextInt(0 until 81)
            } while (removedCells.contains(r))

            removedCells.add(r)
            empty++
            buttons[r].text = " "
            buttons[r].setBackgroundResource(R.drawable.blackborder)
        }
//        var cellsToRemove=0
//        if(difficulty=="easy")
//            cellsToRemove=Random.nextInt(50..53)
//        else if(difficulty == "medium")
//            cellsToRemove=Random.nextInt(60..63)
//        else
//            cellsToRemove=Random.nextInt(70..73)
//        repeat(cellsToRemove) {
//            val r = Random.nextInt(0 until 81)
//            empty++
//            buttons[r].text = " "
//            buttons[r].setBackgroundResource(R.drawable.blackborder)
//        }
    }



}