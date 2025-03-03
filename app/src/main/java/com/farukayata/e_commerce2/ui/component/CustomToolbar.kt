package com.farukayata.e_commerce2.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
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
}
