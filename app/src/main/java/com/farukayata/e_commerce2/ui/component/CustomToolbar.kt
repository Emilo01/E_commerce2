package com.farukayata.e_commerce2.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.ToolbarCustomBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ToolbarCustomBinding

    var onBackClick: (() -> Unit)? = null
    var onCartClick: (() -> Unit)? = null

    init {
        binding = ToolbarCustomBinding.inflate(LayoutInflater.from(context), this, true)

        binding.btnBack.setOnClickListener {
            onBackClick?.invoke()
        }

        binding.btnCart.setOnClickListener {
            onCartClick?.invoke()
        }
    }

    fun setTitle(title: String) {
        binding.toolbarTitle.text = title
    }
    fun hideCartIcon() {
        findViewById<ImageView>(R.id.btnCart)?.visibility = View.GONE
    }

    fun showCartIcon() {
        findViewById<ImageView>(R.id.btnCart)?.visibility = View.VISIBLE
    }

    fun hideCartCard() {
        findViewById<View>(R.id.cartButtonContainer)?.visibility = View.GONE
    }

    fun showCartCard() {
        findViewById<View>(R.id.cartButtonContainer)?.visibility = View.VISIBLE
    }

    fun hideBackCard() {
        binding.backButtonContainer.visibility = View.GONE
    }

    fun showBackBack() {
        findViewById<View>(R.id.backButtonContainer)?.visibility = View.VISIBLE
    }

    fun showBackCard() {
        binding.backButtonContainer.visibility = View.VISIBLE
    }

    fun showBackIcon() {
        binding.btnBack.visibility = View.VISIBLE
    }

    fun hideBackIcon() {
        binding.btnBack.visibility = View.GONE
    }


}

