package com.rogergcc.certificatepinningdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rogergcc.certificatepinningdemo.databinding.FragmentFirstBinding
import com.rogergcc.certificatepinningdemo.network.GithubUser
import com.rogergcc.certificatepinningdemo.presentation.MainViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

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
                viewModel.getUserData(binding.etProfileName.text.toString())

            }
        }

//        binding.etProfileName.setOnEditorActionListener { v, actionId, event ->
//            if (actionId === EditorInfo.IME_ACTION_SEARCH) {
//                //For this example only use seach option
//                //U can use a other view with activityresult
//                performSearch()
//
//                return@setOnEditorActionListener true
//            }
//            false
//        }

        viewModel.userData.observe(viewLifecycleOwner) { githubUser ->
            Log.d("FirstFragment", "onViewCreated: $githubUser")

            binding.run {
                details(githubUser)

            }

        }
        viewModel.userNotFound.observe(viewLifecycleOwner) {
            binding.run {
                tvName.text = it.message
                tvFollowers.text = it.message
                tvFollowing.text = it.message
                Glide.with(requireContext())
                    .load(R.mipmap.icon_octogithub)
                    .centerCrop()
                    .into(ivProfile)
            }
        }
    }

    private fun FragmentFirstBinding.details(githubUser: GithubUser) {
        tvName.text = githubUser.name
        tvFollowers.text = githubUser.followers.toString()
        tvFollowing.text = githubUser.following.toString()

        Glide.with(requireContext())
            .load(githubUser.avatarUrl)
            .centerCrop()
            .into(ivProfile)
    }

    private fun performSearch() {
        binding.etProfileName.clearFocus()
        val `in` =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        `in`.hideSoftInputFromWindow(binding.etProfileName.windowToken, 0)
        //...perform search
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}