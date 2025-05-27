package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentCartBinding
import com.farukayata.e_commerce2.model.CartItem
import com.farukayata.e_commerce2.ui.adapter.CartAdapter
import com.farukayata.e_commerce2.ui.adapter.CouponAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CartViewModel
import com.farukayata.e_commerce2.ui.viewmodel.CouponViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.farukayata.e_commerce2.model.Product
import android.widget.NumberPicker

//nullable olabilirlik durumu için

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter
    private var discountPrice: Double? = null
    private var finalPrice: Double? = null

    private lateinit var couponAdapter: CouponAdapter
    //recycler view kullanmıcaz duruma göre kaldırıla bilir
    private val couponViewModel: CouponViewModel by viewModels()

    private var isCouponApplied = false //kupon aktifliğini check etmek için

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        viewModel.loadCartItems()

        //lambda fonksiyonları tanımlama
        adapter = CartAdapter(
            context = requireContext(),
            onRemoveClick = { productId ->
                //viewModel.removeFromCart(productId)
                showDeleteDialog(productId) //popup la silme
            },
            onIncreaseClick = { cartItem ->
                val newCount = (cartItem.count ?: 0) - 1 // Null
                if (newCount > 0) {
                    viewModel.updateItemCount(cartItem.id.orEmpty(), newCount)
                    // Adeti -
                } else {
                    showDeleteDialog(cartItem.id.orEmpty())
                    //popup ile onay alarak silme için
                }

                //sepetteki değişiklikte kuponun aktifliğini bozcak
                if (isCouponApplied) {
                    couponViewModel.removeCoupon(viewModel.cartItems.value ?: listOf())
                    resetCouponUI()
                }



            },
            onDecreaseClick = { cartItem ->
                val newCount = (cartItem.count ?: 0) + 1 // Null =?
                viewModel.updateItemCount(cartItem.id.orEmpty(), newCount) // Adeti +

                //kupon değişikliğinde kuponu kaldır
                if (isCouponApplied) {
                    couponViewModel.removeCoupon(viewModel.cartItems.value ?: listOf())
                    resetCouponUI()
                }
            },
            onSwipedToDelete = { productId ->
                //Kaydırarak silme burada popup ile yönettik
                showDeleteDialog(productId)
            },
            onItemClick = { cartItem ->
                // cart itemi product ta dönüştürdük
                val product = Product(
                    id = cartItem.id?.toIntOrNull(),
                    title = cartItem.title,
                    price = cartItem.price,
                    description = cartItem.description,
                    category = cartItem.category,
                    image = cartItem.image
                )
                val action = CartFragmentDirections.actionCartFragmentToDetailFragment(product)
                findNavController().navigate(action)
            },
            onCountClick = { cartItem ->
                showCountPickerDialog(cartItem)
            }

        )

        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = adapter

        //recycler viewda kaydırarak silme
        adapter.attachSwipeToDelete(binding.recyclerViewCart)


        //lotie ekledik ve sepet boş olma ve olmama durumu olucağı için artık aşağıdaki gibi değilde bi aşağıdaki gibi kullancaz
        // Sepet verilerini gözlemle ve RecyclerView'a bağla

        viewModel.cartItems.observe(viewLifecycleOwner) { cartList ->
            if (cartList.isEmpty()) {
                binding.recyclerViewCart.visibility = View.GONE
                binding.emptyCartLayout.visibility = View.VISIBLE

                //kupon kodu giriş alanını temizledik
                binding.editTextCouponCode.text.clear()

                isCouponApplied = false
                couponViewModel.removeCoupon(listOf())//kupon efekti gitti

                //kırmızı yeşil fiyat efekti kısımlarını gizlicek sepet boşalınca kupon aktif olsada
                binding.textViewOldPrice.visibility = View.GONE
                binding.textViewTotalPrice.text = "Toplam: 0 TL"
                binding.textViewTotalPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))


                //kupon devre dışı sepet boşsa
                binding.buttonApplyCoupon.isEnabled = false
                binding.buttonApplyCoupon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_gray))

            } else {
                binding.recyclerViewCart.visibility = View.VISIBLE
                binding.emptyCartLayout.visibility = View.GONE

                //kupon butonunu tekrar aktif
                binding.buttonApplyCoupon.isEnabled = true
                binding.buttonApplyCoupon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_green))

                binding.buttonApplyCoupon.isEnabled = true
                binding.buttonApplyCoupon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_green))
            }

            updateTotalPrice(cartList)
            adapter.submitList(cartList)
        }



        couponViewModel.totalPrice.observe(viewLifecycleOwner) { discountedPrice ->
            binding.textViewTotalPrice.text = String.format("Toplam: %.2f TL", discountedPrice)
        }

        //kupon uygulandığında toplam fiyatı dinler
        couponViewModel.totalPrice.observe(viewLifecycleOwner) { discountedPrice ->
            Log.d("CartFragment", "Kupon uygulandı, toplam fiyat güncellendi: $discountedPrice")
            binding.textViewTotalPrice.text = String.format("Toplam: %.2f TL", discountedPrice)
        }

        binding.buttonShopNow.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_homeFragment)
        }

        //satın al Butonuile PaymentSelectionFragmenta yönlendirdik
        binding.btnCheckout.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_paymentSelectionFragment)
        }

        binding.editTextCouponCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()

                if (text.isNotEmpty()) {
                    couponViewModel.validateCoupon(text)
                //kullanıcının girdiği her kodu dığruluğuna bakıyoruz
                }

                //kuponun geçerli olup olmadığını dinledik
                couponViewModel.isCouponValid.observe(viewLifecycleOwner) { isValid ->
                    binding.buttonApplyCoupon.isEnabled = isValid
                    binding.buttonApplyCoupon.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (isValid) R.color.main_green else R.color.dim_gray
                        )
                    )

                    //kupon kodu geçersizse
                    if (!isValid && text.isNotEmpty()) {
                        //Toast.makeText(requireContext(), "Geçersiz kupon kodu!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.buttonApplyCoupon.setOnClickListener {

            //yukarıda yaptığımız işlemi ui gecikmesinden dolayı hata almamak adına
            //burdada sepet boşsa kuponu uygulatmamayı check ediyoruz-sepetin doluluğuna bakıyoruz
            if (viewModel.cartItems.value.isNullOrEmpty()) {
                //Toast.makeText(requireContext(), "Önce sepete ürün ekleyin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // İşlem burada durcak
            }

            val couponCode = binding.editTextCouponCode.text.toString().trim()

            if (!isCouponApplied) {
                if (couponCode.isNotEmpty()) {
                    //sadece geçerli kuponlar için buton aktif olmasını burda kontrol edip sağlıyoruz
                    couponViewModel.isCouponValid.observe(viewLifecycleOwner) { isValid ->
                        if (isValid) {
                            couponViewModel.applyCoupon(couponCode, viewModel.cartItems.value ?: listOf())

                            isCouponApplied = true
                            binding.buttonApplyCoupon.text = "Kaldır"
                            binding.buttonApplyCoupon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                            binding.editTextCouponCode.isEnabled = false

                            //kupon uygulandığında fiyatı güncelledik
                            updateTotalPrice(viewModel.cartItems.value ?: listOf())

                        } else {
                            //Toast.makeText(requireContext(), "Geçersiz kupon kodu!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    //Toast.makeText(requireContext(), "Kupon kodunu girin", Toast.LENGTH_SHORT).show()
                }
            } else {
                couponViewModel.removeCoupon(viewModel.cartItems.value ?: listOf())

                isCouponApplied = false
                binding.buttonApplyCoupon.text = "Uygula"
                binding.buttonApplyCoupon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_green))
                binding.editTextCouponCode.isEnabled = true
                binding.buttonApplyCoupon.isEnabled = true

                //Toast.makeText(requireContext(), "Kupon kaldırıldı", Toast.LENGTH_SHORT).show()

                //kupon kaldırıldığında fiyatı hemen güncelliyoruz
                updateTotalPrice(viewModel.cartItems.value ?: listOf())
            }
        }




        //recyclerViewa kaydırarak silme özelliğini ekliyoruz
        adapter.attachSwipeToDelete(binding.recyclerViewCart)

        return binding.root
    }

    override fun onDestroyView() {
        //Memory Leak Önleme: Fragment kapandığında UI referanslarını serbest bırakır
        super.onDestroyView()
        _binding = null
    }


    private fun updateTotalPrice(cartList: List<CartItem>) {
        val totalPrice = cartList.sumOf { (it.price ?: 0.0) * (it.count ?: 0) } // toplam fiyat hesaplandı
        val discountAmount = couponViewModel.totalPrice.value ?: 0.0 // kupon indirimini doğrudan al

        // yanlış hesaplamayı önlemek için doğrudan totalPrice üzerinden hesaplama yapıldı
        val finalPrice = if (isCouponApplied) {
            (totalPrice - discountAmount).coerceAtLeast(0.0) // negatif değerler engelli
        } else {
            totalPrice
        }

        // isCouponApplied güncellendiğinde eski fiyatı hemen göster
        //bunu eklemediğimizde kuponu uygula kaldır yapmadan düşülen indirim fiyaatını yazmıyordu
        if (isCouponApplied) {
            binding.textViewOldPrice.visibility = View.VISIBLE
            binding.textViewOldPrice.text = String.format("%.2f TL", totalPrice)
            binding.textViewOldPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            binding.textViewOldPrice.paintFlags = binding.textViewOldPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG

            // yeni fiyatı yeşil yap
            binding.textViewTotalPrice.text = String.format("%.2f TL", finalPrice)
            binding.textViewTotalPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_green))
        } else {
            // kupon kaldırılmışsa eski fiyatı gizle
            binding.textViewOldPrice.visibility = View.GONE

            // toplam fiyatı normal göster
            binding.textViewTotalPrice.text = String.format("Toplam: %.2f TL", totalPrice)
            binding.textViewTotalPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }




    //popup ile silme gonksiyonnumuz
    private fun showDeleteDialog(productId: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_delete_dialog, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        val btnCancel: Button? = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnDelete: Button? = dialogView.findViewById<Button>(R.id.btnDelete)

        btnDelete?.setOnClickListener {
            viewModel.removeFromCart(productId) //viewModelden silme işlemi çağrıldı
            dialog.dismiss()
        }

        btnCancel?.setOnClickListener {
            adapter.notifyDataSetChanged() //kaydırma işlemini geri almak içinn
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun resetCouponUI() {
        isCouponApplied = false
        binding.buttonApplyCoupon.text = "Uygula"
        binding.buttonApplyCoupon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_green))
        binding.editTextCouponCode.isEnabled = true
        binding.buttonApplyCoupon.isEnabled = true
        //Toast.makeText(requireContext(), "Kupon kaldırıldı", Toast.LENGTH_SHORT).show()

        //kupon kaldırıldığında fiyatı güncelledik
        updateTotalPrice(viewModel.cartItems.value ?: listOf())
    }

    private fun showCountPickerDialog(cartItem: CartItem) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_number_picker, null)
        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 100 // veya stok adedine göre dinamik belirleyebilirsin
        numberPicker.value = cartItem.count ?: 1

        AlertDialog.Builder(requireContext())
            .setTitle("Adet Seç")
            .setView(dialogView)
            .setPositiveButton("Tamam") { _, _ ->
                val newCount = numberPicker.value
                //sayı değişiminde kupon işlemlerini uygun düzelttik yukarıdaki gibi
                if (isCouponApplied) {
                    couponViewModel.removeCoupon(viewModel.cartItems.value ?: listOf())
                    resetCouponUI()
                }
                viewModel.updateItemCount(cartItem.id.orEmpty(), newCount)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

}

