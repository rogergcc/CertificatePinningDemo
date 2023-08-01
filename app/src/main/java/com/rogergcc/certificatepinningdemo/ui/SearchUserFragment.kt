package com.rogergcc.certificatepinningdemo.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rogergcc.certificatepinningdemo.BuildConfig
import com.rogergcc.certificatepinningdemo.R
import com.rogergcc.certificatepinningdemo.core.ResourceState
import com.rogergcc.certificatepinningdemo.data.GithubRepositoryImpl
import com.rogergcc.certificatepinningdemo.data.cloud.api.GithubApiInstance
import com.rogergcc.certificatepinningdemo.databinding.FragmentSearchUserBinding
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import com.rogergcc.certificatepinningdemo.ui.common.hideKeyboard
import com.rogergcc.certificatepinningdemo.ui.common.loadImageFromResource
import com.rogergcc.certificatepinningdemo.ui.common.loadUrl
import com.rogergcc.certificatepinningdemo.ui.common.showToast
import com.rogergcc.certificatepinningdemo.ui.customs.ErrorDialogFragment
import com.rogergcc.certificatepinningdemo.ui.customs.ErrorTYpe
import com.rogergcc.certificatepinningdemo.ui.customs.SHOW_ERROR_DIALOG
import com.rogergcc.certificatepinningdemo.ui.presentation.GithubViewModelFactory
import com.rogergcc.certificatepinningdemo.ui.presentation.MainViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SearchUserFragment : Fragment() {

    private var _binding: FragmentSearchUserBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //    private val viewModel: MainViewModel by viewModels()
    private val viewModel by viewModels<MainViewModel> {
        GithubViewModelFactory(
            GithubRepositoryImpl(
                GithubApiInstance.retrofitService
            )
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btnNavigate.setOnClickListener {
////            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//
//
//        }

        binding.btnSearch.setOnClickListener {
            if (binding.etProfileName.text.isNotBlank()) {
                binding.etProfileName.hideKeyboard()
                performSearch()

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

        viewModel.resultLiveData.observe(viewLifecycleOwner) { resources ->

            when (resources) {
                is ResourceState.Loading -> {
                    logDegub("Loading")
                    showToast("Loading")
                }
                is ResourceState.Success -> {
                    logDegub("Success")
                    onResultSuccess(resources.data)
//                    progressBar.hide()
                }
//                is ResourceState.Failure -> {
//                    logDegub("Failure")
//                    showToast("Failure")
////                    showErrorDialog(ErrorTYpe.NO_SSL_PINNING)
//                    onsResultError(resources)
////                    progressBar.hide()
//                }
                is ResourceState.Error -> {
                    binding.apply {
                        tvGithuUserName.text = "-"
                        tvFollowers.text = "-"
                        tvFollowing.text = "-"

                        ivProfile.loadImageFromResource(
                            resources.errorType?.image ?: R.drawable.ic_cancel
                        )
                        tvError.text = if (BuildConfig.DEBUG) {
                            getString(
                                resources.errorType?.message ?: R.string.unknown_error_message
                            )
                        } else {
                            getString(
                                R.string.unknown_error_message
                            )
                        }
                    }
//                    if (resources.errorType == ErrorTYpe.NO_SSL_PINNING) {
//                        showErrorDialog(ErrorTYpe.NO_SSL_PINNING)
//                    } else {
//                        showErrorDialog(ErrorTYpe.UNKNOWN)
//                    }
                    showErrorDialog(resources.errorType ?: ErrorTYpe.UNKNOWN)
                }
            }

        }
    }

    private fun logDegub(message: String) {
        Log.d("SearchUserFragment", "onViewCreated: $message")
    }

//    private fun onsResultError(resourceState: ResourceState.Error) {
//        binding.apply {
//            tvGithuUserName.text = "Algo ocurrio"
//            ivProfile.loadImageFromResource(R.drawable.ic_cancel)
//            tvError.text = if (BuildConfig.DEBUG) {
//                resourceState.exception.message
//            } else {
//                "Error"
//            }
//        }
//    }

    private fun onResultSuccess(userData: GithubUserDomain) {
        binding.apply {
            tvGithuUserName.text = userData.name
            tvFollowers.text =
                getString(
                    R.string.followers_total_message,
                    userData.followers.toString()
                )
            tvFollowing.text =
                getString(
                    R.string.following_total_message,
                    userData.following.toString()
                )

            ivProfile.loadUrl(userData.avatarUrl)
        }
    }

    private fun performSearch() {
        binding.etProfileName.clearFocus()
        viewModel.fetchUserData(binding.etProfileName.text.toString())
    }

    fun showErrorDialog(errorTYpe: ErrorTYpe) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        ErrorDialogFragment(errorTYpe).show(fragmentManager, SHOW_ERROR_DIALOG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}