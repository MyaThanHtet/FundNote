package com.mth.bettingnote

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


class RecyclerViewAdapter(val c: Context, val note: ArrayList<Note>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    var db = RoomSingleton.getInstance(c)
    var isPaid: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerViewAdapter.ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {

        holder.nameTv.text = note[position].name
        holder.dateTv.text = note[position].date
        holder.amountTv.text = note[position].amount + "Ks"
        holder.noteTv.text = note[position].description
        if (note[position].isPaid) {
            holder.isPaidTv.text = "Paid"
            holder.isPaidIv.setImageResource(R.drawable.check_mark)
        } else {
            holder.isPaidTv.text = "Not Paid"
            holder.isPaidIv.setImageResource(R.drawable.exc_mark)
        }
        holder.showPopupMenuIv.setOnClickListener {
            it
            val popupMenu: PopupMenu = PopupMenu(c, holder.showPopupMenuIv)
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.editText -> {

                        val dialog = Dialog(it.context)
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

                        nameEtd.setText(note[position].name)
                        amountEtd.setText(note[position].amount)
                        dateEtd.setText(note[position].date)
                        noteEtd.setText(note[position].description)
                        isPaid = note[position].isPaid

                        val paidRb = dialog.findViewById<RadioButton>(R.id.paid_rb)
                        val notPaidRb = dialog.findViewById<RadioButton>(R.id.not_paid_rb)
                        if (isPaid) {
                            paidRb.isChecked = true
                        } else {
                            notPaidRb.isChecked = true
                        }
                        radioGroup.setOnCheckedChangeListener { _, i ->
                            isPaid = when (i) {
                                R.id.paid_rb -> true
                                R.id.not_paid_rb -> false
                                else -> false
                            }
                        }



                        confirmBnt.setOnClickListener {
                            val notes = Note(
                                note[position].id, nameEtd.text.toString(),
                                dateEtd.text.toString(),
                                amountEtd.text.toString(),
                                isPaid,
                                noteEtd.text.toString()
                            )
                            doAsync {
                                note[position] = notes
                                db.noteDao().update(notes)

                                uiThread {
                                    Toast.makeText(c, "Updated!!", Toast.LENGTH_SHORT).show()
                                    notifyDataSetChanged()

                                }
                            }
                            dialog.dismiss()

                        }

                        dialog.show()

                        true
                    }
                    R.id.deleteText -> {


                        doAsync {
                            db.noteDao().delete(note[position])
                            note.removeAt(position)
                            Log.i("isPaidData", "$position")


                            uiThread {
                                Toast.makeText(c, "Deleted!", Toast.LENGTH_SHORT).show()
                                notifyDataSetChanged()
                            }
                        }
                        true
                    }
                    else -> true
                }
            })
            popupMenu.show()
        }

    }

    override fun getItemCount(): Int {
        return note.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTv = itemView.findViewById<TextView>(R.id.name)
        val dateTv = itemView.findViewById<TextView>(R.id.date)
        val amountTv = itemView.findViewById<TextView>(R.id.amount_tv)
        val noteTv = itemView.findViewById<TextView>(R.id.noteTv)
        val isPaidTv = itemView.findViewById<TextView>(R.id.isPaid_tv)
        val isPaidIv = itemView.findViewById<ImageView>(R.id.isPaid_iv)
        val showPopupMenuIv = itemView.findViewById<ImageView>(R.id.show_popup_iv)


        /*   fun popupMenus(v: View) {
               val position = note[adapterPosition]
               val popupMenus = PopupMenu(c, v)
               popupMenus.menuInflater.inflate(R.menu.menu, popupMenus.menu)

               popupMenus.setOnMenuItemClickListener {
                   when (it.itemId) {
                       R.id.editText -> {


                           true
                       }
                       R.id.deleteText -> {


                           doAsync {
                               db.noteDao().delete(adapterPosition)
                               note.removeAt(adapterPosition)
                               Log.i("isPaidData", "$adapterPosition")


                               uiThread {
                                   Toast.makeText(c, "Deleted!", Toast.LENGTH_SHORT).show()
                                   notifyDataSetChanged()
                               }
                           }
                           true
                       }
                       else -> true
                   }

               }
               popupMenus.show()
           }*/
    }
    /* interface noteAdapterListener {
         fun onNoteSelected(position: Int)
     }*/

}