package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentCardPaymentBinding
import com.farukayata.e_commerce2.model.Order
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class CardPaymentFragment : Fragment() {

    private var _binding: FragmentCardPaymentBinding? = null
    private val binding get() = _binding!!
    private var latestOrder: Order? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        latestOrder = arguments?.getParcelable("latest_order") // latestOrder alındı

        setupCardNumberFormatting()
        setupExpiryDateValidation()

        binding.btnPayNow.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
            val expiryMonth = binding.etExpiryMonth.text.toString()
            val expiryYear = binding.etExpiryYear.text.toString()
            val cvv = binding.etCVV.text.toString()
            val cardHolder = binding.etCardHolder.text.toString()

            if (validateCardInfo(cardNumber, expiryMonth, expiryYear, cvv, cardHolder)) {
                val bundle = Bundle()
                bundle.putParcelable("latest_order", latestOrder)
                findNavController().navigate(R.id.action_cardPaymentFragment_to_orderConfirmationFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Lütfen geçerli bir kart bilgisi girin!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Kart Numarasını 4’er 4’er ayırdık
    private fun setupCardNumberFormatting() {
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            private var isDeleting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                isDeleting = count > after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val cleaned = s.toString().replace(" ", "") // Boşlukları temizle
                    val formatted = StringBuilder()

                    for (i in cleaned.indices) {
                        formatted.append(cleaned[i])
                        if ((i + 1) % 4 == 0 && i + 1 != cleaned.length) {
                            formatted.append(" ") // Her 4 rakamdan sonra boşluk ekle
                        }
                    }

                    binding.etCardNumber.removeTextChangedListener(this)
                    binding.etCardNumber.setText(formatted.toString())
                    binding.etCardNumber.setSelection(formatted.length)
                    binding.etCardNumber.addTextChangedListener(this)
                }
            }
        })
    }

    //son kullanma tarihi MM ve YYYY için
    private fun setupExpiryDateValidation() {
        binding.etExpiryMonth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val month = s.toString()
                if (month.length == 2) {
                    val monthInt = month.toIntOrNull()
                    if (monthInt == null || monthInt !in 1..12) {
                        binding.etExpiryMonth.error = "Geçersiz Ay"
                    } else {
                        binding.etExpiryYear.requestFocus() // Ay girildiğinde yıl alanına geç
                    }
                }
            }
        })

        binding.etExpiryYear.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val year = s.toString()
                if (year.length == 4) {
                    val yearInt = year.toIntOrNull()
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

                    if (yearInt == null || yearInt < currentYear) {
                        binding.etExpiryYear.error = "Geçersiz Yıl"
                    }
                }
            }
        })
    }

    //Kart Bilgisini check ettik
    private fun validateCardInfo(cardNumber: String, expiryMonth: String, expiryYear: String, cvv: String, cardHolder: String): Boolean {
        // Kart numarası tam 16 haneli olmalı
        if (cardNumber.length != 16) return false

        // Son Kullanma Tarihini Kontrol Et
        val expiryMonthInt = expiryMonth.toIntOrNull()
        val expiryYearInt = expiryYear.toIntOrNull()

        if (expiryMonthInt == null || expiryMonthInt !in 1..12) return false

        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        if (expiryYearInt == null || expiryYearInt < currentYear || (expiryYearInt == currentYear && expiryMonthInt < currentMonth)) {
            return false
        }

        //cvv
        if (cvv.length != 3 || cvv.toIntOrNull() == null) return false

        // Kart Sahibinin Adı Boş Olamaz
        if (cardHolder.isBlank()) return false

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
