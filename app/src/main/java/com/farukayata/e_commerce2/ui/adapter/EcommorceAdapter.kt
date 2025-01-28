package com.farukayata.e_commerce2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Product
import com.farukayata.e_commerce2.ui.fragment.HomeFragmentDirections

class EcommorceAdapter(
    private val context: Context, //context vermek mantıklı değil ve clikği içerde tanımlamak lazım
    private val onProductClick: (Product) -> Unit, // Ürün tıklama için lambda
    private val onFavoriteClick: (Product) -> Unit // Favorilere ekleme için lambda fonksiyonu
) : ListAdapter<Product, EcommorceAdapter.CardDesignViewHolder>(ProductDiffCallback()) {

    inner class CardDesignViewHolder(val binding: CardDesignBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignViewHolder {
        val binding: CardDesignBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.card_design, parent, false
        )
        return CardDesignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardDesignViewHolder, position: Int) {
        val product = getItem(position)
        holder.binding.product = product

        Glide.with(context)//older.binding.imageViewProductCard.context -- adapterin connnstraktırındacontext tutulmaz
            .load(product.image)
            .into(holder.binding.imageViewProductCard)

        // Ürüne tıklama olayını tanımladık (detail sayfasına geçiş için)
        holder.binding.cardViewEcommorceProduct.setOnClickListener {
            onProductClick(product) // Lambda ile geçişi tetikle.  artık burdasetonclick ortak olu

        //yukarıda ki gibi bıraktık bu sayede sadece home için değil categoryspecial içinde kullandık
        //val action = HomeFragmentDirections.detailGecis(product)
        //Navigation.findNavController(it).navigate(action)
        }

        // Favorilere ekleme butonuna tıklama
        holder.binding.buttonShop.text = "Add to Favorites" // Buton yazısını favori işlemi için değiştiriyoruz
        holder.binding.buttonShop.setOnClickListener {
            onFavoriteClick(product) // Lambda fonksiyonu üzerinden favorilere ekleme işlemini tetikle
        }
    }

    // DiffUtil: ListAdapter performansını optimize etmek için gerekli
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}









/*
package com.farukayata.e_commerce2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Product
import com.farukayata.e_commerce2.ui.fragment.HomeFragmentDirections

class EcommorceAdapter(
    private val context: Context,
    //Görsellerin yüklenmesi ve kaynakların kullanılması için gerekli
    private val productList: List<Product>,
    private val onFavoriteClick: (Product) -> Unit // Favorilere ekleme için lambda fun
) : RecyclerView.Adapter<EcommorceAdapter.CardDesignViewHolder>() {

    inner class CardDesignViewHolder(val binding: CardDesignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignViewHolder {
        val binding: CardDesignBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.card_design, parent, false
        )
        return CardDesignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardDesignViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.product = product

        Glide.with(context)
            .load(product.image)
            .into(holder.binding.imageViewProductCard)

        // Ürüne tıklama olayını tanımladık (detail sayfasına geçiş için)
        holder.binding.cardViewEcommorceProduct.setOnClickListener {
            val action = HomeFragmentDirections.detailGecis(product)
            Navigation.findNavController(it).navigate(action)
        }

        // Favorilere ekleme butonuna tıklama
        holder.binding.buttonShop.text = "Add to Favorites" // Buton yazısını favori işlemi için değiştiriyoruz
        holder.binding.buttonShop.setOnClickListener {
            onFavoriteClick(product) // Lambda fonksiyonu üzerinden favorilere ekleme işlemini tetikle
        }
    }

    override fun getItemCount(): Int = productList.size
}


 */







/*
package com.farukayata.e_commerce2.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Product
import com.farukayata.e_commerce2.ui.fragment.HomeFragmentDirections

class EcommorceAdapter(private val context: Context, private val productList: List<Product>)
    : RecyclerView.Adapter<EcommorceAdapter.CardDesignViewHolder>() {

    inner class CardDesignViewHolder(val binding: CardDesignBinding) : RecyclerView.ViewHolder(binding.root)
    //binding ile CardDesign deki itemlere erşitik

    //recyclerview için her iteme bir kart oluşturur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignViewHolder {
        val binding: CardDesignBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.card_design, parent, false)
        return CardDesignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardDesignViewHolder, position: Int) {
        val product = productList[position]
        //(Aşağıdaki kısım)Ürün verisini card_design.xml'de tanımlanan product değişkenine bağlar.
        holder.binding.product = product
        Glide.with(context)
            .load(product.image)
            .into(holder.binding.imageViewProductCard)
        //Ürünün görsel URL'sini alır ve ImageView içine yükler.

        // Ürüne tıklama olayını tanımladık (detail sayfasına geçiş için)
        holder.binding.cardViewEcommorceProduct.setOnClickListener {
            val action = HomeFragmentDirections.detailGecis(product)
            //Tıklanan ürünün bilgileri (product) DetailFragment'e aktarır
            Navigation.findNavController(it).navigate(action)
        }


    }

    override fun getItemCount(): Int = productList.size
}



 */


















/*
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.R
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
        val binding:CardDesignBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.card_design, parent, false)
        //yukarıyıda revize ettil cardesign xml kısmını düzenledikten sonra
        return CardDesignTutucu(binding)
    }

    override fun onBindViewHolder(holder: CardDesignTutucu, position: Int) {
        val product = commerceProductListesi.get(position)
        val t = holder.tasarim //aşada bu sayede tüm görsel nesnelere erişe biliriz

        t.imageViewProductCard.setImageResource(mContext.resources.getIdentifier(product.image, "drawable", mContext.packageName))
        //drawble kulladık çünkü burda api den değil localde drawblede olan resimleri kullancaz

        t.commoreceNesnesi = product

        //t.textViewProdutcPrice.text = "${product.price} TL"
        //xml de düzenledik zaten gerek kalmadı

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

 */