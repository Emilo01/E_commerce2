package com.farukayata.e_commerce2.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.farukayata.e_commerce2.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            binding.firstOnboardingImageView.setImageResource(it.getInt(IMAGE))
            binding.firstOnboardingTitle.text = requireContext().getString(it.getInt(TITLE))
            binding.firstOnboardingDescription.text = requireContext().getString(it.getInt(DESCRIPTION))
        }

    }

    companion object {
        private const val IMAGE = "image"
        private const val TITLE = "title"
        private const val DESCRIPTION = "description"

        fun newInstance(image: Int, title: Int, description: Int) =
            OnBoardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(IMAGE, image)
                    putInt(TITLE, title)
                    putInt(DESCRIPTION, description)
                }
            }
    }
}
