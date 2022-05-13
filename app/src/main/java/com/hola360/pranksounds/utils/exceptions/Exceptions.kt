package com.hola360.pranksounds.utils.exceptions

import java.io.IOException

class NoInternetException(message: String): IOException(message)
class ApiException(message: String): IOException(message)