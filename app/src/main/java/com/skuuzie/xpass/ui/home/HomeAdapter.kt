package com.skuuzie.xpass.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skuuzie.xpass.data.local.database.Credential
import com.skuuzie.xpass.databinding.ItemCredentialBinding

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    private var credentials = ArrayList<Credential>()
    private lateinit var onItemClickListener: OnItemClickListener

    class MyViewHolder(val binding: ItemCredentialBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(credential: Credential) {
            with(binding) {
                tvPlatform.text = credential.platform
                tvIdentifier.text = credential.email
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCredentialBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = credentials.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(credentials[position])
        holder.binding.rootLayout.setOnClickListener {
            onItemClickListener.onItemClick(credentials[position])
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    fun submitList(list: List<Credential>) {
        this.credentials.clear()
        if (list.isNotEmpty()) {
            this.credentials.addAll(list)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(credential: Credential)
    }
}