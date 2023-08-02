package com.rogergcc.certificatepinningdemo.ui.customs

import com.rogergcc.certificatepinningdemo.R


const val GOOGLE_SIGN_IN = 1001
const val DURATION = "Duration"
const val FROM_NOTIFICATION = 32
const val SHOW_ERROR_DIALOG = "ERROR_DIALOG"

var DATE_WEB_SERVICE = ""
var TIME_WEB_SERVICE = ""

enum class ErrorTYpe(val title: Int, val message: Int, val image: Int) {
    NO_INTERNET(R.string.no_internet_title, R.string.no_internet_message, R.drawable.no_internet),
    NO_SSL_PINNING(
        R.string.no_sslpinning_title,
        R.string.no_sslpinning_message,
        R.drawable.no_internet
    ),

    UNKNOWN(R.string.unknown_error_title, R.string.unknown_error_message, R.drawable.ic_cancel)
}
//        } catch (ex: CertificateExpiredException) {
//            Log.e("GithubRepoImp", "CertificateExpiredException ")
////            return ResourceState.Failure(Exception("CertificateExpiredException"))
//        } catch (ex: CertificateNotYetValidException) {
//            Log.e("GithubRepoImp", "CertificateNotYetValidException ")
////            return ResourceState.Failure(Exception("CertificateNotYetValidException"))

//        }
//        catch (ex: SSLHandshakeException) {
//            Log.e("GithubRepoImp", "SSLHandshakeException ")
////            return ResourceState.Failure(Exception("SSLHandshakeException"))
//

enum class TaskState {
    RUNNING, PAUSED, COMPLETED, NOT_STARTED
}

data class TaskStateCount(
    var completed: Int = 0,
    var total: Int = 0,
    var incomplete: Int = total - completed,
)

enum class StopWatchFor {
    UPCOMING, RUNNING, PAUSED
}

