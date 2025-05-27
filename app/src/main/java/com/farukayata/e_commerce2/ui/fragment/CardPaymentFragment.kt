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

    //kart bilgisi için 4 4 ve fokus detayalrı
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
                //buton aktifliğinde güncellemek
                updateButtonState()
            }
        })

        //sadece focus kaybında validasyon kızarması
        binding.etCardNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
                binding.tilCardNumber.error = if (cardNumber.length != 16 && cardNumber.isNotEmpty()) "Geçersiz kart numarası" else null
            } else {
                binding.tilCardNumber.error = null
            }
        }
    }

    //ay ve yıl için anlık validasyon
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

    //cvv ve kart sahibi için anlık validasyon
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

    //anlık hata mesajları
    private fun validateFields() {
        // Kart numarası için
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

    //butonn aktifliği konntrolü
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