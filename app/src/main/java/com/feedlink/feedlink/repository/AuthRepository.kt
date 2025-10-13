package com.feedlink.feedlink.repository

import android.util.Log
import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.model.ForgotPasswordRequest
import com.feedlink.feedlink.model.ResetPasswordRequest
import com.feedlink.feedlink.model.SignUpRequest
import com.feedlink.feedlink.model.SignUpResponse
import com.feedlink.feedlink.model.SignInRequest
import com.feedlink.feedlink.model.SignInResponse
import com.feedlink.feedlink.model.VerificationRequest
import com.feedlink.feedlink.model.VerificationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository(
    private val apiInterface: ApiInterface
) {

    suspend fun signup(request: SignUpRequest): Result<SignUpResponse> {
        return try {
            val response: Response<SignUpResponse> = withContext(Dispatchers.IO) {
                apiInterface.signup(request)
            }
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Signup: Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "Signup API Error: Code: ${response.code()}, Body: $errorBody")
                Result.failure(Exception(parseSignupError(errorBody, response.code())))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Signup Network/Exception: ${e.message}", e)
            Result.failure(Exception("Signup Network error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }


    suspend fun signin(request: SignInRequest): Result<SignInResponse> {
        return try {
            val response: Response<SignInResponse> = withContext(Dispatchers.IO) {
                apiInterface.login(request)
            }
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Login: Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "Login API Error: Code: ${response.code()}, Body: $errorBody")
                Result.failure(Exception(parseSigninError(errorBody, response.code())))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login Network/Exception: ${e.message}", e)
            Result.failure(Exception("Login Network error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }


    suspend fun sendOtp(email: String): Result<VerificationResponse> {
        return try {
            val request = ForgotPasswordRequest(email = email)
            val response: Response<VerificationResponse> = withContext(Dispatchers.IO) {
                apiInterface.forgotPassword(request)
            }
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Send OTP: Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "Send OTP API Error: Code: ${response.code()}, Body: $errorBody")
                Result.failure(Exception(parseSendOtpError(errorBody, response.code())))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Send OTP Network/Exception: ${e.message}", e)
            Result.failure(Exception("Send OTP Network error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }


    suspend fun verifyOtp(email: String, otp: String): Result<VerificationResponse> {
        return try {

            val request = VerificationRequest(email = email, otp = otp)

            val response: Response<VerificationResponse> = withContext(Dispatchers.IO) {
                apiInterface.verification(request)
            }

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Verify OTP: Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "Verify OTP API Error: Code: ${response.code()}, Body: $errorBody")
                Result.failure(Exception(parseOtpVerificationError(errorBody, response.code())))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Verify OTP Network/Exception: ${e.message}", e)
            Result.failure(Exception("Verify OTP Network error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }

    suspend fun resetPassword(email: String, newPassword: String, confirmPasswordFromVm: String): Result<VerificationResponse> {
        return try {

            val request = ResetPasswordRequest(
                email = email,
                password = newPassword,
                confirm_password = confirmPasswordFromVm
            )

            val response: Response<VerificationResponse> = withContext(Dispatchers.IO) {
                apiInterface.resetPassword(request)
            }
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Reset Password: Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "Reset Password API Error: Code: ${response.code()}, Body: $errorBody")
                Result.failure(Exception(parseResetPasswordError(errorBody, response.code())))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Reset Password Network/Exception: ${e.message}", e)
            Result.failure(Exception("Reset Password Network error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }


    suspend fun fetchUserRole(userId: Int): Result<String> {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiInterface.getUserProfileById(userId)
            }
            if (response.isSuccessful && response.body() != null) {
                val role = response.body()!!.role
                if (!role.isNullOrBlank()) {
                    Result.success(role.lowercase().trim())
                } else {
                    Result.failure(Exception("Role is missing in user profile"))
                }
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to load user role: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun parseSignupError(errorBody: String?, statusCode: Int): String {
        Log.d("AuthRepository", "Parsing Signup Error: $errorBody, Code: $statusCode")
        if (errorBody == null) return "Signup failed (Code: $statusCode). Please try again."
        if (errorBody.contains("already exists", ignoreCase = true)) return "An account with this email already exists."
        if (errorBody.contains("\"detail\":")) {
            return errorBody.substringAfter("\"detail\":\"").substringBefore("\"")
        }
        return "Signup failed: $errorBody"
    }

    private fun parseSigninError(errorBody: String?, statusCode: Int): String {
        Log.d("AuthRepository", "Parsing Signin Error: $errorBody, Code: $statusCode")
        if (errorBody == null) return "Login failed (Code: $statusCode). Please try again."
        if (errorBody.contains("No active account found with the given credentials", ignoreCase = true) ||
            errorBody.contains("Unable to log in with provided credentials", ignoreCase = true) ||
            errorBody.contains("Invalid credentials", ignoreCase = true) ) {
            return "Invalid email or password."
        }
        if (errorBody.contains("\"detail\":")) {
            return errorBody.substringAfter("\"detail\":\"").substringBefore("\"")
        }
        return "Login failed: $errorBody"
    }

    private fun parseSendOtpError(errorBody: String?, statusCode: Int): String {
        Log.d("AuthRepository", "Parsing Send OTP Error: $errorBody, Code: $statusCode")
        if (errorBody == null) return "Failed to send OTP (Code: $statusCode). Please try again."
        if (errorBody.contains("User with this email does not exist", ignoreCase = true)) {
            return "No account found with this email address."
        }
        if (errorBody.contains("\"detail\":")) {
            return errorBody.substringAfter("\"detail\":\"").substringBefore("\"")
        }
        return "Failed to send OTP: $errorBody"
    }


    private fun parseOtpVerificationError(errorBody: String?, statusCode: Int): String {
        Log.d("AuthRepository", "Parsing OTP Verification Error: $errorBody, Code: $statusCode")
        if (errorBody == null) return "OTP verification failed (Code: $statusCode)."
        if (errorBody.contains("OTP expired", ignoreCase = true)) return "OTP has expired. Please request a new one."
        if (errorBody.contains("Invalid OTP", ignoreCase = true) || errorBody.contains("incorrect", ignoreCase = true)) return "Invalid OTP. Please check and try again."
        if (errorBody.contains("OTP field is required", ignoreCase = true) || (errorBody.contains("otp", ignoreCase = true) && errorBody.contains("This field is required", ignoreCase = true))) {
            return "OTP is missing. Please enter the OTP."
        }
        if (errorBody.contains("\"detail\":")) {
            return errorBody.substringAfter("\"detail\":\"").substringBefore("\"")
        }
        return "OTP verification failed: $errorBody"
    }

    private fun parseResetPasswordError(errorBody: String?, statusCode: Int): String {
        Log.d("AuthRepository", "Parsing Reset Password Error: $errorBody, Code: $statusCode")
        if (errorBody == null) return "Failed to reset password (Code: $statusCode)."
        if (errorBody.contains("Password mismatch", ignoreCase = true)) return "New passwords do not match."
        if (errorBody.contains("Invalid OTP", ignoreCase = true)) return "Invalid or expired session for password reset."
        if (errorBody.contains("Password too weak", ignoreCase = true)) return "Password is too weak. Please choose a stronger one."
        if (errorBody.contains("\"detail\":")) {
            return errorBody.substringAfter("\"detail\":\"").substringBefore("\"")
        }
        return "Failed to reset password: $errorBody"
    }
}