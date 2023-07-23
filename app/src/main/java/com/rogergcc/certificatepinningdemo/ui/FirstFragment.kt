package com.rogergcc.certificatepinningdemo.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rogergcc.certificatepinningdemo.R
import com.rogergcc.certificatepinningdemo.core.Resource
import com.rogergcc.certificatepinningdemo.core.hide
import com.rogergcc.certificatepinningdemo.core.hideKeyboard
import com.rogergcc.certificatepinningdemo.core.show
import com.rogergcc.certificatepinningdemo.data.GithubRepositoryImpl
import com.rogergcc.certificatepinningdemo.data.cloud.GithubApi
import com.rogergcc.certificatepinningdemo.databinding.FragmentFirstBinding
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
    private val progressBar by lazy { ProgressBar(requireContext()) }
    private val viewModel by viewModels<MainViewModel> {
        GithubViewModelFactory(
            GithubRepositoryImpl(
                GithubApi.retrofitService
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
                    binding.etProfileName.hideKeyboard()
                    performSearch()
//                viewModel.fetchUserData(binding.etProfileName.text.toString())

                }
                return@setOnEditorActionListener true
            }
            false
        }

//        viewModel.userData.observe(viewLifecycleOwner) { githubUser ->
//            Log.d("FirstFragment", "onViewCreated: $githubUser")
//
//            binding.run {
//                if (githubUser == null) {
//                    tvGithuUserName.text = getString(R.string.user_not_found_message)
//
//                    Glide.with(requireContext())
//                        .load(R.mipmap.icon_octogithub)
//                        .centerCrop()
//                        .into(ivProfile)
//                    return@observe
//                }
//                tvGithuUserName.text = githubUser.name
//                tvFollowers.text =
//                    getString(R.string.followers_total_message, githubUser.followers.toString())
//                tvFollowing.text =
//                    getString(R.string.following_total_message, githubUser.following.toString())
//
//                Glide.with(requireContext())
//                    .load(githubUser.avatarUrl)
//                    .centerCrop()
//                    .into(ivProfile)
//            }
//
//        }

        viewModel.resultLiveData.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resource.Loading -> {
                    Log.d("FirstFragment", "onViewCreated: Loading")
                    progressBar.show()
                }
                is Resource.Success -> {
                    progressBar.hide()
                    Log.d("FirstFragment", "onViewCreated: Success")
                    binding.apply {
                        tvGithuUserName.text = resources.data.name
                        tvFollowers.text =
                            getString(
                                R.string.followers_total_message,
                                resources.data.followers.toString()
                            )
                        tvFollowing.text =
                            getString(
                                R.string.following_total_message,
                                resources.data.following.toString()
                            )

                        Glide.with(requireContext())
                            .load(resources.data.avatarUrl)
                            .centerCrop()
                            .into(ivProfile)
                    }
                }
                is Resource.Failure -> {
                    progressBar.hide()
                    Log.d("FirstFragment", "onViewCreated: Failure")
                    binding.apply {
                        tvGithuUserName.text = resources.exception.message
                        tvFollowers.text = resources.exception.message
                        tvFollowing.text = resources.exception.message


                        Glide.with(requireContext())
                            .load(R.mipmap.icon_octogithub)
                            .centerCrop()
                            .into(ivProfile)
                    }
                }
            }

        }
//        viewModel.userNotFound.observe(viewLifecycleOwner) {
//            binding.run {
//                tvGithuUserName.text = it.message
//                tvFollowers.text = it.message
//                tvFollowing.text = it.message
//                Glide.with(requireContext())
//                    .load(R.mipmap.icon_octogithub)
//                    .centerCrop()
//                    .into(ivProfile)
//            }
//        }
    }


    private fun performSearch() {
        binding.etProfileName.clearFocus()

        //...perform search
        viewModel.fetchUserData(binding.etProfileName.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}