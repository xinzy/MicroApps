package com.xinzy.lib.mvvm.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.xinzy.lib.mvvm.BR
import com.xinzy.lib.mvvm.base.BaseViewModel

class MultiAdapter constructor(private var mItemBinding: ItemBinding) : RecyclerView.Adapter<MultiViewHolder>() {
    private val mData = mutableListOf<Any>()
    private var mOnItemClickListener: OnItemClickListener? = null

    companion object {
        @JvmStatic
        fun createAdapter(viewModel: BaseViewModel<*>, itemLayoutId: Int) = MultiAdapter(ItemBinding(itemLayoutId, viewModel))
    }

    fun setItemBinding(itemBinding: ItemBinding) {
        this.mItemBinding = itemBinding
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewHolder {
        val databinding = mItemBinding.createDataBinding(parent, viewType)
        return MultiViewHolder(databinding, mOnItemClickListener)
    }

    override fun getItemCount(): Int {
        return mItemBinding.getItemCount(mData)
    }

    override fun onBindViewHolder(holder: MultiViewHolder, position: Int) {
        val item = mData[position]
        holder.convert(mItemBinding, position, item)
    }

    override fun getItemViewType(position: Int): Int {
        return mItemBinding.getItemViewType(mData[position])
    }

    fun indexOf(item: Any): Int {
        return mData.indexOf(item)
    }

    fun addAll(items: List<Any>?) {
        if (items == null || items.isEmpty()) return
        val size = mData.size
        mData.addAll(items)
        notifyItemRangeInserted(size, items.size)
    }

    fun add(position: Int, item: Any?) {
        if (item == null) return
        if (position > mData.size) return
        mData.add(position, item)
        notifyItemInserted(position)
    }

    fun add(item: Any?) {
        if (item == null) return
        val size = mData.size
        mData.add(item)
        notifyItemInserted(size)
    }

    fun remove(position: Int) {
        if (position < 0) return
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun remove(item: Any) {
        val position = indexOf(item)
        remove(position)
    }

    fun replace(items: List<Any>?) {
        mData.clear()
        items?.let { mData.addAll(items) }
        notifyDataSetChanged()
    }

    fun clear() {
        val size = itemCount
        if (size == 0) return
        mData.clear()
        notifyItemRangeRemoved(0, size)
    }
}

interface OnItemClickListener {
    fun onItemClick(any: Any, position: Int)
}

class MultiViewHolder(private val dataBinding: ViewDataBinding, private val onItemClickListener: OnItemClickListener?)
    : RecyclerView.ViewHolder(dataBinding.root), View.OnClickListener {

    private var mPosition = -1
    private var mItem: Any? = null

    init {
        onItemClickListener?.let { dataBinding.root.setOnClickListener(this) }
    }

    override fun onClick(v: View?) {
        onItemClickListener?.onItemClick(mItem!!, mPosition)
    }

    fun convert(itemBinding: ItemBinding, position: Int, item: Any) {
        this.mPosition = position
        this.mItem = item
        itemBinding.bindItem(dataBinding, item, position)
    }
}

open class ItemBinding constructor(private var itemLayoutId: Int = 0, private var viewModel: BaseViewModel<*>? = null) {
    private var mLayoutInflater: LayoutInflater? = null

    open fun getItemViewType(any: Any) = itemLayoutId

    internal fun createDataBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        if (mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.context)
        return onCreateDataBinding(parent, viewType)
    }

    open fun onCreateDataBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(mLayoutInflater!!, viewType, parent, false)
    }

    internal fun bindItem(dataBinding: ViewDataBinding, item: Any, position: Int) {
        onBindItem(dataBinding, item, position)
        dataBinding.executePendingBindings()
    }

    @CallSuper
    open fun onBindItem(dataBinding: ViewDataBinding, item: Any, position: Int) {
        dataBinding.setVariable(BR.item, item)
        dataBinding.setVariable(BR.position, position)
        dataBinding.setVariable(BR.viewModel, viewModel)
    }

    open fun getItemCount(items: List<Any>): Int {
        return items.size
    }

    fun getViewModel(): ViewModel? {
        return viewModel
    }
}