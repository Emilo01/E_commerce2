package com.farukayata.e_commerce2.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.farukayata.e_commerce2.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CustomChipGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val chipGroup: ChipGroup

    // Seçilen Chipi yakalamak için bir callback
    var onChipSelected: ((String) -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_chip_group, this, true)
        chipGroup = view.findViewById(R.id.chipGroup)
        //custom_chip_group.xml yükleerek içindeki chipgroupu chipgroup a atadık
    }

    //Chipleri Dinamik Olarak Yükleme Fonksiyonu
    fun setChipItems(items: List<String>) {
        chipGroup.removeAllViews() // Önceki Chipleri temizler

        items.forEach { category ->
            val chip = Chip(context).apply {
                text = category
                isCheckable = true
                isClickable = true
                setChipBackgroundColorResource(R.color.gray1) // Chipin arka plan rengi
                setTextColor(resources.getColor(R.color.main_green, null))
                //Bir Chip tıklandığında, bu fonksiyon çağrılır ve seçilen Chip’in kategorisini döndürüyor
                setOnClickListener {
                    onChipSelected?.invoke(category) // Seçilen chip bilgisini geri döndür
                }
            }
            chipGroup.addView(chip)
        }
    }
}
