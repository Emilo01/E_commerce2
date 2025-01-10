package com.farukayata.e_commerce2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.data.entity.Commerce_Products
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.databinding.FragmentHomeBinding
import com.farukayata.e_commerce2.ui.fragment.HomeFragmentDirections
import com.google.android.material.snackbar.Snackbar

class EcommorceAdapter (var mContext: Context,var commerceProductListesi : List<Commerce_Products>)
    : RecyclerView.Adapter<EcommorceAdapter.CardDesignTutucu>() {
    //Commerce_Product clasından nesneleri tutan bir liste listelemek
    //context activt yada fragmentta çalıştığını belirttik

    inner class CardDesignTutucu(var tasarim: CardDesignBinding) : RecyclerView.ViewHolder(tasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignTutucu {
        val binding = CardDesignBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return CardDesignTutucu(binding)
    }

    override fun onBindViewHolder(holder: CardDesignTutucu, position: Int) {
        val product = commerceProductListesi.get(position)
        val t = holder.tasarim //aşada bu sayede tüm görsel nesnelere erişe biliriz

        t.imageViewProductCard.setImageResource(mContext.resources.getIdentifier(product.image, "drawable", mContext.packageName))
        //drawble kulladık çünkü burda api den değil localde drawblede olan resimleri kullancaz

        t.textViewProdutcPrice.text = "${product.price} TL"

        t.cardViewEcommorceProduct.setOnClickListener {//karta tıklanndığı zaman kullanmak için
            val gecis = HomeFragmentDirections.detailGecis(product = product) //!!!!!! = karşısı emin dilim
            //yukarıdaki kısım üstüne tıklayıp seçtiğimiz film listesini vericek bize
            Navigation.findNavController(it).navigate(gecis)
        }
        t.buttonShop.setOnClickListener {//buttona tıklandığı kısım
            Snackbar.make(it, "${product.title} sepete eklendi", Snackbar.LENGTH_SHORT).show()
        }
    }
    //holder classı card ın üstündeki görsel neseleri tutan bir class

    override fun getItemCount(): Int {
        return commerceProductListesi.size
    }
}