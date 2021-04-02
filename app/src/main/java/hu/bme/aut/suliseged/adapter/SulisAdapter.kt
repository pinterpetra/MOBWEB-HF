package hu.bme.aut.suliseged.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.suliseged.EditActivity
import hu.bme.aut.suliseged.MainActivity
import hu.bme.aut.suliseged.R
import hu.bme.aut.suliseged.data.SulisFeladat
import hu.bme.aut.suliseged.data.SulisFeladatlistaAdatbazis
import java.util.*

class SulisAdapter(private val listener: SulisFeladatClickListener) :
    RecyclerView.Adapter<SulisAdapter.SulisViewHolder>() {

    private val items = mutableListOf<SulisFeladat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SulisViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.feladatlista_egy_elem, parent, false)
        return SulisViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SulisViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.descriptionTextView.text = item.description
        holder.categoryTextView.text = item.category.name
        val deadline = item.deadline

        deadline?.let {
            holder.deadlineDate.text = makeDateFrom(deadline)
        }
        holder.iconImageView.setImageResource(getImageResource(item.category))
        holder.isDoneCheckBox.isChecked = item.isDone

        holder.item = item
    }

    @DrawableRes
    private fun getImageResource(category: SulisFeladat.Category) = when (category) {
        SulisFeladat.Category.ZH -> R.drawable.zh
        SulisFeladat.Category.HF -> R.drawable.hf
        SulisFeladat.Category.EA -> R.drawable.ea
    }

    fun addItem(item: SulisFeladat) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(sulisFeladatok: List<SulisFeladat>) {
        items.clear()
        items.addAll(sulisFeladatok)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface SulisFeladatClickListener {
        fun onItemChanged(item: SulisFeladat)
        fun onItemDeleted(oldItem: SulisFeladat)
    }

    inner class SulisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iconImageView: ImageView
        val nameTextView: TextView
        val descriptionTextView: TextView
        val categoryTextView: TextView
        val deadlineDate: TextView
        val isDoneCheckBox: CheckBox
        val removeButton: ImageButton
        val editButton: ImageButton

        var item: SulisFeladat? = null

        init {
            iconImageView = itemView.findViewById(R.id.SulisFeladatIconImageView)
            nameTextView = itemView.findViewById(R.id.SulisFeladatNameTextView)
            descriptionTextView = itemView.findViewById(R.id.SulisFeladatDescriptionTextView)
            categoryTextView = itemView.findViewById(R.id.SulisFeladatCategoryTextView)
            deadlineDate = itemView.findViewById(R.id.SulisFeladatDeadline)
            isDoneCheckBox = itemView.findViewById(R.id.SulisFeladatIsDoneCheckBox)
            removeButton = itemView.findViewById(R.id.SulisFeladatRemoveButton)
            editButton = itemView.findViewById(R.id.SulisFeladatEditButton)
            isDoneCheckBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                item?.let {
                    it.isDone = isChecked
                    listener.onItemChanged(it)
                }
            })
            removeButton.setOnClickListener(){
                listener.onItemDeleted(item!!)
            }
            //szerkeszthetoseghez
            editButton.setOnClickListener(){
                val intent = Intent(it.context, EditActivity::class.java)
                intent.putExtra("FELADAT_ID", item?.id)
                it.context.startActivity(intent)
            }
        }
    }

    fun deleteItem(item: SulisFeladat){
        val index = items.indexOf(item)
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    private fun makeDateFrom(timestamp: Long): String? {

        val c = Calendar.getInstance()
        c.timeInMillis = timestamp * 1000

        return String.format(
            Locale.getDefault(), "%04d.%02d.%02d.",
            c[Calendar.YEAR], c[Calendar.MONTH]+1, c[Calendar.DAY_OF_MONTH]
        )
    }
}