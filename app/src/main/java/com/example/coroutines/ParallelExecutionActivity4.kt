package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_parallel_execution4.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class ParallelExecutionActivity4 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parallel_execution4)

        button.setOnClickListener {
            setNewText("Click!")
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }
    }

    private fun setNewText(input: String) {
        val newText = textview.text.toString() + "\n$input"
        textview.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    /**
     * Job1 and Job2 run in parallel as different coroutines
     * Also see "Deferred, Async, Await" branch for parallel execution
     */
    private suspend fun fakeApiRequest() {

        val startTime = System.currentTimeMillis()

        val parentJob = CoroutineScope(IO).launch {

            val job1 = launch {
                val time1 = measureTimeMillis {
                    Log.e("debug: ", "launching job1 in thread: ${Thread.currentThread().name}")
                    val result1 = getResult1FromApi()
                    setTextOnMainThread("Got $result1")
                }
                Log.e("debug: ", " compeleted job1 in $time1 ms.")
            }

            val job2 = launch {
                val time2 = measureTimeMillis {
                    Log.e("debug: ", "launching job2 in thread: ${Thread.currentThread().name}")
                    val result2 = getResult2FromApi()
                    setTextOnMainThread("Got $result2")
                }
                Log.e("debug:", "compeleted job2 in  $time2 ms.")
            }

        }
        parentJob.invokeOnCompletion {
            Log.e("debug :",  ((System.currentTimeMillis() - startTime).toString()))
        }


    }


    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        delay(1500)
        return "Result #2"
    }
}
