package com.farukayata.e_commerce2.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.core.Response
import com.farukayata.e_commerce2.databinding.FragmentSignUpBinding
import com.farukayata.e_commerce2.ui.login.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.collect => yalnızca Flow'dan veri toplamak için gereklidir.

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    //private var isPasswordVisible = false
    //private var isConfirmPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)


        // Kayıt ol butonunun başlangıçta devre dışı bırakılması
        binding.buttonSignUp.isEnabled = false

        // TextWatcher ekleyerek alanların tamamlanıp tamamlanmadığını kontrol et
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        // EditText alanlarına TextWatcher ekle
        //Kullanıcı her bir EditText alanına yazdığında, validateInput fonksiyonu çağrılarak gerekli kontroller yapıldı.
        binding.editTextEmail.addTextChangedListener(textWatcher)
        binding.editTextPassword.addTextChangedListener(textWatcher)
        binding.editTextConfirmPassword.addTextChangedListener(textWatcher)

        // Kayıt butonuna tıklanma
        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                //Toast.makeText(requireContext(), "Please enter the email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                //Toast.makeText(requireContext(), "Check the email format type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 5) {
                //Toast.makeText(requireContext(), "Password must be at least five characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                //Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.signUp(email, password)
        }

         //ViewModel durumlarını gözlemleme
        lifecycleScope.launchWhenStarted { //lifecycleScope sayesinde started olunca çalışır
            viewModel.signUpState.collect { state -> //signUpState adlı Flow'u gözlemler. Flow'dan gelen her yeni durum (Response) bu blokta işler
                when (state) {
                    is Response.Loading -> {
                        // Progress bar gösterilir
                        //
                        //binding.progressBar.visibility = View.VISIBLE //progress barı gonedan visible çektik
                        binding.buttonSignUp.isEnabled = false //buttonı disable yaptık.
                    }
                    is Response.Success -> {
                        //binding.progressBar.visibility = View.GONE//tekrar gone a çektik
                        binding.buttonSignUp.isEnabled = true// butonu enable yaptık

                        //Toast.makeText(requireContext(), state.data as String, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                    }
                    is Response.Error -> {
                        //binding.progressBar.visibility = View.GONE//tekrar gone a çektik
                        binding.buttonSignUp.isEnabled = true// butonu enable yaptık

                        //Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

    // Giriş verilerini kontrol eden bir fonksiyon
    private fun validateInput() {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()

        // Şartlar sağlanıyorsa buton aktif hale gelir
        binding.buttonSignUp.isEnabled =
            email.length >= 3 &&
                    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && //email formatını kontrol ediyor
                    password.length >= 5 && // Şifre en az 5 karakter olmalı
                    confirmPassword.length >= 5 && // Şifre doğrulama en az 5 karakter olmalı
                    password == confirmPassword // Şifreler eşleşmeli
                    //password.isNotEmpty() &&
                    //confirmPassword.isNotEmpty()
    }
}