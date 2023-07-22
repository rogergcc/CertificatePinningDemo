package com.rogergcc.certificatepinningdemo.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rogergcc.certificatepinningdemo.R
import com.rogergcc.certificatepinningdemo.data.GithubRepositoryImpl
import com.rogergcc.certificatepinningdemo.databinding.FragmentFirstBinding
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import com.rogergcc.certificatepinningdemo.ui.presentation.GithubViewModelFactory
import com.rogergcc.certificatepinningdemo.ui.presentation.MainViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

//    private val viewModel: MainViewModel by viewModels()

    private val viewModel by viewModels<MainViewModel> {
        GithubViewModelFactory(
            GithubRepositoryImpl(
            )
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.btnSearch.setOnClickListener {
            if (binding.etProfileName.text.isNotBlank()) {
                performSearch()
//                viewModel.fetchUserData(binding.etProfileName.text.toString())

            }
        }

        binding.etProfileName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //For this example only use seach option
                //U can use a other view with activityresult
                if (binding.etProfileName.text.isNotBlank()) {
                    performSearch()
//                viewModel.fetchUserData(binding.etProfileName.text.toString())

                }
                return@setOnEditorActionListener true
            }
            false
        }

        viewModel.userData.observe(viewLifecycleOwner) { githubUser ->
            Log.d("FirstFragment", "onViewCreated: $githubUser")

            binding.run {
                details(githubUser)

            }

        }
        viewModel.userNotFound.observe(viewLifecycleOwner) {
            binding.run {
                tvGithuUserName.text = it.message
                tvFollowers.text = it.message
                tvFollowing.text = it.message
                Glide.with(requireContext())
                    .load(R.mipmap.icon_octogithub)
                    .centerCrop()
                    .into(ivProfile)
            }
        }
    }

    private fun FragmentFirstBinding.details(githubUserDomain: GithubUserDomain) {
        tvGithuUserName.text = githubUserDomain.name
        tvFollowers.text = githubUserDomain.followers.toString()
        tvFollowing.text = githubUserDomain.following.toString()

        Glide.with(requireContext())
            .load(githubUserDomain.avatarUrl)
            .centerCrop()
            .into(ivProfile)
    }

    private fun performSearch() {
        binding.etProfileName.clearFocus()
        val `in` =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        `in`.hideSoftInputFromWindow(binding.etProfileName.windowToken, 0)
        //...perform search
        viewModel.fetchUserData(binding.etProfileName.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}