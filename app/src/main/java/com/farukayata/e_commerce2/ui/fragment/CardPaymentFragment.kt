package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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

        latestOrder = arguments?.getParcelable("latest_order")

        setupCardNumberFormatting()
        setupExpiryDateValidation()
        setupFieldValidation()
        updateButtonState()

        binding.btnPayNow.setOnClickListener {
            if (binding.btnPayNow.isEnabled) {
                val bundle = Bundle()
                bundle.putParcelable("latest_order", latestOrder)
                findNavController().navigate(R.id.action_cardPaymentFragment_to_orderConfirmationFragment, bundle)
            }
        }
    }

    // Kart numarası 4'erli ayırma ve sadece focus kaybında hata gösterme
    private fun setupCardNumberFormatting() {
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val cleaned = s.toString().replace(" ", "")
                    val formatted = StringBuilder()
                    for (i in cleaned.indices) {
                        formatted.append(cleaned[i])
                        if ((i + 1) % 4 == 0 && i + 1 != cleaned.length) {
                            formatted.append(" ")
                        }
                    }
                    binding.etCardNumber.removeTextChangedListener(this)
                    binding.etCardNumber.setText(formatted.toString())
                    binding.etCardNumber.setSelection(formatted.length)
                    binding.etCardNumber.addTextChangedListener(this)
                }
                // Sadece butonun aktifliğini güncelle
                updateButtonState()
            }
        })

        // Sadece focus kaybında hata göster
        binding.etCardNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
                binding.tilCardNumber.error = if (cardNumber.length != 16 && cardNumber.isNotEmpty()) "Geçersiz kart numarası" else null
            } else {
                binding.tilCardNumber.error = null
            }
        }
    }

    // Ay ve yıl için anlık validasyon ve buton kontrolü
    private fun setupExpiryDateValidation() {
        binding.etExpiryMonth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateFields()
                updateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etExpiryYear.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateFields()
                updateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // CVV ve kart sahibi için anlık validasyon ve buton kontrolü
    private fun setupFieldValidation() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateFields()
                updateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.etCVV.addTextChangedListener(watcher)
        binding.etCardHolder.addTextChangedListener(watcher)
    }

    // Sadece ay, yıl, cvv, kart sahibi için anlık hata göster
    private fun validateFields() {
        // Kart numarası için burada hata gösterme!
        val expiryMonth = binding.etExpiryMonth.text.toString()
        val monthInt = expiryMonth.toIntOrNull()
        binding.tilExpiryMonth.error = if ((monthInt == null || monthInt !in 1..12) && expiryMonth.isNotEmpty()) "Geçersiz ay" else null

        val expiryYear = binding.etExpiryYear.text.toString()
        val yearInt = expiryYear.toIntOrNull()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        binding.tilExpiryYear.error = if ((yearInt == null || expiryYear.length != 4 || yearInt < currentYear) && expiryYear.isNotEmpty()) "Geçersiz yıl" else null

        val cvv = binding.etCVV.text.toString()
        binding.tilCVV.error = if (cvv.length != 3 && cvv.isNotEmpty()) "Geçersiz CVV" else null

        val cardHolder = binding.etCardHolder.text.toString()
        binding.tilCardHolder.error = if (cardHolder.isBlank() && cardHolder.isNotEmpty()) "Kart sahibi adı zorunlu" else null
    }

    // Butonun aktifliğini her zaman kontrol et
    private fun updateButtonState() {
        val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
        val expiryMonth = binding.etExpiryMonth.text.toString()
        val expiryYear = binding.etExpiryYear.text.toString()
        val cvv = binding.etCVV.text.toString()
        val cardHolder = binding.etCardHolder.text.toString()

        val expiryMonthInt = expiryMonth.toIntOrNull()
        val isMonthValid = expiryMonthInt != null && expiryMonthInt in 1..12

        val expiryYearInt = expiryYear.toIntOrNull()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val isYearValid = expiryYear.length == 4 && expiryYearInt != null && expiryYearInt >= currentYear

        val isCVVValid = cvv.length == 3
        val isHolderValid = cardHolder.isNotBlank()
        val isCardNumberValid = cardNumber.length == 16

        val allValid = isCardNumberValid && isMonthValid && isYearValid && isCVVValid && isHolderValid

        binding.btnPayNow.isEnabled = allValid
        binding.btnPayNow.setBackgroundColor(
            requireContext().getColor(if (allValid) R.color.main_green else R.color.dim_gray)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
















/*
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

        latestOrder = arguments?.getParcelable("latest_order")

        setupCardNumberFormatting()
        setupExpiryDateValidation()

        binding.btnPayNow.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
            val expiryMonth = binding.etExpiryMonth.text.toString()
            val expiryYear = binding.etExpiryYear.text.toString()
            val cvv = binding.etCVV.text.toString()
            val cardHolder = binding.etCardHolder.text.toString()

            var isValid = true

            // Kart numarası validasyonu
            if (cardNumber.length != 16) {
                binding.tilCardNumber.error = "16 haneli kart numarası girin"
                isValid = false
            } else {
                binding.tilCardNumber.error = null
            }

            // Ay validasyonu
            val expiryMonthInt = expiryMonth.toIntOrNull()
            if (expiryMonthInt == null || expiryMonthInt !in 1..12) {
                binding.tilExpiryMonth.error = "Geçersiz ay"
                isValid = false
            } else {
                binding.tilExpiryMonth.error = null
            }

            // Yıl validasyonu
            val expiryYearInt = expiryYear.toIntOrNull()
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            if (expiryYearInt == null || expiryYearInt < currentYear) {
                binding.tilExpiryYear.error = "Geçersiz yıl"
                isValid = false
            } else {
                binding.tilExpiryYear.error = null
            }

            // CVV validasyonu
            if (cvv.length != 3) {
                binding.tilCVV.error = "3 haneli CVV girin"
                isValid = false
            } else {
                binding.tilCVV.error = null
            }

            // Kart sahibi validasyonu
            if (cardHolder.isBlank()) {
                binding.tilCardHolder.error = "Kart sahibi adı zorunlu"
                isValid = false
            } else {
                binding.tilCardHolder.error = null
            }

            if (isValid) {
                val bundle = Bundle()
                bundle.putParcelable("latest_order", latestOrder)
                findNavController().navigate(R.id.action_cardPaymentFragment_to_orderConfirmationFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Lütfen geçerli bir kart bilgisi girin!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Kart Numarasını 4 e 4 ayırdık
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


 */