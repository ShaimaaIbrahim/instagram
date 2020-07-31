package com.example.instagram.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    constructor(
        parent: ViewGroup,
        layoutId: Int
    ) : this(LayoutInflater.from(parent.context).inflate(layoutId , parent , false))

    fun getString(@StringRes res: Int): String = itemView.context.getString(res)

    open fun bind(item: Any?) {}

    open fun onRecycled(success: Boolean) {}

    open fun onAttached() {}

    open fun onDetached() {}
}