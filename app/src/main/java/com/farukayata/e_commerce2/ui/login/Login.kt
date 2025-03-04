package com.farukayata.e_commerce2.ui.auth

import android.app.Activity
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.core.Response
import com.farukayata.e_commerce2.databinding.FragmentLoginBinding
import com.farukayata.e_commerce2.ui.login.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.collect => yalnızca Flow'dan veri toplamak için gereklidir.

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var isPasswordVisible = false

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        println("🔥 Google Hesabı Başarıyla Alındı: ${account.email}")
                        viewModel.signInWithGoogle(account)
                    } else {
                        println("Google hesabı null döndü!")
                        Toast.makeText(requireContext(), "Google hesabı alınamadı!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: ApiException) {
                    println("Google Sign-In başarısız! Hata: ${e.message}")
                    Toast.makeText(requireContext(), "Google Sign-In başarısız: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                println("Google Sign-In işlemi iptal edildi!")
                Toast.makeText(requireContext(), "Google Sign-In iptal edildi!", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Şifre görünürlüğünü değiştirme
        binding.imageViewTogglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            val inputType = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD//şifre gizliliğini kaldırır
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                //text ve password metinlerinin metin tiplerini belirttik
            }
            binding.editTextPassword.inputType = inputType
            binding.editTextPassword.text?.let { binding.editTextPassword.setSelection(it.length) }
            binding.imageViewTogglePasswordVisibility.setImageResource(
                if (isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            )
        }

        //bu kısım strig.xml kısmınnda renk katmak için ekledim fakat olmuyor
//        // "Don't have an account? Sign up here." metnini HTML formatlı şekilde ayarla
//        val signUpText = getString(R.string.sign_up_text)
//        binding.textViewSignUp.text = Html.fromHtml(signUpText, Html.FROM_HTML_MODE_LEGACY)
//        binding.textViewSignUp.movementMethod = LinkMovementMethod.getInstance()


        // Giriş butonuna tıklanma
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        // Google ile giriş
        binding.buttonGoogleSignIn.setOnClickListener {
            val signInIntent = viewModel.getGoogleSignInIntent()
            googleSignInLauncher.launch(signInIntent)
        }

        // ViewModel durumlarını gözlemleme
        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collect { state ->
                when (state) {
                    is Response.Loading -> {
                        // Progress bar gösterebilirsiniz
                    }
                    is Response.Success -> {
                        Toast.makeText(requireContext(), state.data as String, Toast.LENGTH_SHORT).show()
                        //findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        navigateAfterLogin()
                    }
                    is Response.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.textViewSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        return binding.root
    }

    // Kullanıcının giriş yaptığını SharedPreferences'e kaydet
    private fun saveUserLoginState() {
        val sharedPreferences = requireActivity().getSharedPreferences("userSession", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
    }

    // Kullanıcı giriş yaptıktan sonra yönlendirme
    private fun navigateAfterLogin() {
        if (!onBoardingFinished()) {
            findNavController().navigate(R.id.action_loginFragment_to_viewPagerFragment)
        } else {
            findNavController().navigate(R.id.action_loginFragment_to_viewPagerFragment)
        }
    }

    //revize

    // Kullanıcının onboarding'i tamamlayıp tamamlamadığını kontrol eden fonksiyon
    private fun onBoardingFinished(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("Finished", false)
    }

}
