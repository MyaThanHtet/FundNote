package com.mth.bettingnote

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    var isPaid: Boolean = false
    private lateinit var db: RoomSingleton
    lateinit var calendar: Calendar
    lateinit var simpleDateFormat: SimpleDateFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            val titleText = "Note"
            val titleTextColor = ForegroundColorSpan(Color.BLACK)
            val spannString = SpannableString(titleText)
            spannString.setSpan(
                titleTextColor, 0, titleText.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            title = spannString
        }


        db = RoomSingleton.getInstance(applicationContext)
        recyclerView = findViewById(R.id.recyclerview)

        val linearLayoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        recyclerView.layoutManager = linearLayoutManager



        selectData()

        val fab: View = findViewById(R.id.add_note_fab)
        fab.setOnClickListener { view ->
            showDialog()
        }

    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val nameEtd = dialog.findViewById<EditText>(R.id.name_edt)
        val amountEtd = dialog.findViewById<EditText>(R.id.amount_edt)
        val dateEtd = dialog.findViewById<EditText>(R.id.date_edt)
        val noteEtd = dialog.findViewById<EditText>(R.id.note_edt)
        val confirmBnt = dialog.findViewById<TextView>(R.id.confirm_btn)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("dd/MM/yy")
        val dateTime: String = simpleDateFormat.format(calendar.time).toString()
        dateEtd.setText(dateTime)

        radioGroup.setOnCheckedChangeListener { _, i ->
            isPaid = when (i) {
                R.id.paid_rb -> true
                R.id.not_paid_rb -> false
                else -> false
            }
        }



        confirmBnt.setOnClickListener {
            val notes = Note(
                null, nameEtd.text.toString(),
                dateEtd.text.toString(),
                amountEtd.text.toString(),
                isPaid,
                noteEtd.text.toString()
            )
            doAsync {
                // Put the student in database
                db.noteDao().insert(notes)

                uiThread {
                    toast("One record inserted.")
                    selectData()
                }
            }
            dialog.dismiss()

        }

        dialog.show()

    }

    private fun selectData() {
        Log.i("isPaidData", isPaid.toString())
        doAsync {
            val list = db.noteDao().allNote()

            uiThread {
                // toast("${list.size} records found.")
                recyclerView.adapter = RecyclerViewAdapter(
                    applicationContext,
                    list as ArrayList<Note>
                )

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_total_amount) {
            doAsync {
                val totalAmount = db.noteDao().totalAmount()

                uiThread {
                    showToatalAmountDialog(totalAmount)
                }
            }


            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showToatalAmountDialog(totalAmount: Int) {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.total_amount_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val totalAmountTv = dialog.findViewById<TextView>(R.id.total_amount_tv)
        totalAmountTv.text = "$totalAmount Ks"
        dialog.show()

    }
}