package com.isayevapps.blackbeaty2.viewmodels

sealed class States {
    object WaitForConnection : States()
    object Connection : States()
    object Connected : States()
}
