package com.farukayata.e_commerce2.ui.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.CartItemDesignBinding
import com.farukayata.e_commerce2.model.CartItem

class CartAdapter(
    private val context: Context,
    private val onRemoveClick: (String) -> Unit,
    private val onIncreaseClick: (CartItem) -> Unit,
    private val onDecreaseClick: (CartItem) -> Unit,
    private val onSwipedToDelete: (String) -> Unit,
    private val onItemClick: (CartItem) -> Unit,
    private val onCountClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {//DiffUtil kullanılıyor

    var isPreviewSwipe = true //bir kez sahte kaydırma yapmak için

    inner class CartViewHolder(private val binding: CartItemDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem, animate: Boolean) {
            binding.textViewCartTitle.text = cartItem.title
            binding.textViewCartPrice.text = "${cartItem.price} TL"
            binding.textViewCartCount.text = cartItem.count.toString()

            Glide.with(binding.imageViewCartItem.context)
                .load(cartItem.image)
                .into(binding.imageViewCartItem)

            binding.buttonIncrease.setOnClickListener { onIncreaseClick(cartItem) }
            binding.buttonDecrase.setOnClickListener { onDecreaseClick(cartItem) }
            binding.buttonRemove.setOnClickListener { onRemoveClick(cartItem.id.toString()) }

            binding.root.setOnClickListener { onItemClick(cartItem) }//ürün detay

            //kırmızı arka plan ilk başta gizli
            binding.swipeBackground.visibility = View.GONE
            binding.deleteIcon.visibility = View.GONE

            //ilk itemde animasyon gösterdik sadece
            if (animate) {
                val slideOut = AnimationUtils.loadAnimation(binding.root.context, R.anim.slide_out_left)
                val slideIn = AnimationUtils.loadAnimation(binding.root.context, R.anim.slide_in_right)

                slideOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        binding.swipeBackground.visibility = View.VISIBLE
                        binding.deleteIcon.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        binding.root.startAnimation(slideIn) //Geri getirdik
                        binding.swipeBackground.visibility = View.GONE
                        binding.deleteIcon.visibility = View.GONE
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })

                binding.root.startAnimation(slideOut)
            }
            binding.textViewCartCount.setOnClickListener { onCountClick(cartItem) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val animate = (position == 0 && isPreviewSwipe) //ilk ürün için sahte kaydırma başlattık
        holder.bind(getItem(position),animate)

        if (animate) {
            isPreviewSwipe = false //Sadece bir kere çalışmasını sağladık

            //sağdan sola kaycak
            holder.itemView.post {
                val animator = ValueAnimator.ofFloat(0f, -holder.itemView.width * 0.5f).apply {
                    duration = 500
                    addUpdateListener {
                        holder.itemView.translationX = it.animatedValue as Float
                        holder.itemView.invalidate()
                    }
                    start()
                }

                //eski konumuna getirdik
                animator.doOnEnd {
                    val returnAnimator = ValueAnimator.ofFloat(-holder.itemView.width * 0.5f, 0f).apply {
                        duration = 300
                        addUpdateListener {
                            holder.itemView.translationX = it.animatedValue as Float
                            holder.itemView.invalidate()
                        }
                        start()
                    }
                }
            }
        }
    }

    //gerçek kaydırma ve silme efekti
    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.delete_icon1)
        private val backgroundPaint = Paint().apply { color = Color.parseColor("#E50D11") }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val cartItem = getItem(position)

            onSwipedToDelete(cartItem.id.orEmpty()) //Gerçek silme işlemi
        }

        override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val backgroundPaint = Paint().apply { color = Color.parseColor("#E50D11") } //Kırmızı arka plan
            val deleteIcon = ContextCompat.getDrawable(context, R.drawable.delete_icon1)

            //Eğer item sola kaydırılıyorsa, arka planı gösteriyoruz
            if (dX < 0) {
                //Kırmızı arka planı çiz
                c.drawRect(
                    itemView.right.toFloat() + dX, itemView.top.toFloat(),
                    itemView.right.toFloat(), itemView.bottom.toFloat(), backgroundPaint
                )

                //çöp kutusu ikonu
                deleteIcon?.let {
                    val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                    val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                    val iconBottom = iconTop + it.intrinsicHeight

                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    it.draw(c)
                }
            }

            //Kaydırma efektini uyguladık
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    })

    fun attachSwipeToDelete(recyclerView: RecyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}


//Bu sınıf ListAdapter in verileri daha verimli şekilde güncellemesini sağlar
class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}
