package com.farukayata.e_commerce2.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentProfileDetailBinding
import com.farukayata.e_commerce2.model.UserProfile
import com.farukayata.e_commerce2.ui.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailFragment : Fragment() {

    private lateinit var binding: FragmentProfileDetailBinding
    private val viewModel: ProfileViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner  // LiveData değişikliklerini UI’ya yansıtmak için gerekli
        binding.viewModel = viewModel  // Direkt bağlanıyor.

        // Kullanıcı verilerini yükle
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            binding.editTextFirstName.setText(user.firstName)
            binding.editTextLastName.setText(user.lastName)
            binding.editTextGender.setText(user.gender) // eksikti
            binding.editTextAddress.setText(user.address)
            binding.editTextPhone.setText(user.phoneNumber)
            Glide.with(this).load(user.profileImageUrl).into(binding.imageViewProfileDetail)
        }

        // Fotoğraf Seçme Butonu
        binding.imageViewProfileDetail.setOnClickListener {
            openGallery()
        }


        // Güncelle Butonu
        binding.buttonUpdate.setOnClickListener {
            val updatedProfile = UserProfile(
                firstName = binding.editTextFirstName.text.toString(),
                lastName = binding.editTextLastName.text.toString(),
                gender = binding.editTextGender.text.toString(),
                address = binding.editTextAddress.text.toString(),
                phoneNumber = binding.editTextPhone.text.toString(),
                profileImageUrl = viewModel.userProfile.value?.profileImageUrl ?: "" //eski foto url sini koruduk
            )
            println("Güncellenen Profili Firestore’a Gönderiyoruz: $updatedProfile")
            viewModel.updateUserProfile(updatedProfile)

            // Kullanıcı yeni fotoğraf seçtiyse Firebase'e yükle
            selectedImageUri?.let { uri ->
                viewModel.uploadProfileImage(uri)
            }

            //Profil güncellendiğinde anında livedata aktifleşcek
            viewModel.loadUserProfile()
        }

        return binding.root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = getString(R.string.profile_detail_fragment_image_type)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    binding.imageViewProfileDetail.setImageURI(it) // UI Güncelle

                    // Fotoğraf seçildiğinde Firebase'e otomatik yüklencek gücelle butouna basmadan
                    viewModel.uploadProfileImage(it)
                }
            }
        }

}
