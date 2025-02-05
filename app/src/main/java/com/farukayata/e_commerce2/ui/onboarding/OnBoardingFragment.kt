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


/*

package com.farukayata.e_commerce2.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
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

        // "NEXT" butonuna tıklanınca son ekransa Home’a yönlendir
        binding.firstOnBoardingButton.setOnClickListener {
            if (isLastPage()) {
                onboardingFinished()
                findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
            } else {
                (requireActivity() as OnBoardingNavigator).goToNextPage()
            }
        }
    }

    companion object {
        private const val IMAGE = "image"
        private const val TITLE = "title"
        private const val DESCRIPTION = "description"
        private const val LAST_PAGE = "last_page"

        fun newInstance(image: Int, title: Int, description: Int, isLastPage: Boolean) =
            OnBoardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(IMAGE, image)
                    putInt(TITLE, title)
                    putInt(DESCRIPTION, description)
                    putBoolean(LAST_PAGE, isLastPage)
                }
            }
    }

    private fun isLastPage(): Boolean {
        return arguments?.getBoolean(LAST_PAGE, false) ?: false
    }

    private fun onboardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("Finished", true).apply()
    }

    interface OnBoardingNavigator {
        fun goToNextPage()
    }
}


 */