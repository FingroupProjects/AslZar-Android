package com.fin_group.aslzar.util

enum class ErrorCode(val code: Int, val description: String) {
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable");

    companion object {
        fun fromCode(code: Int): ErrorCode? {
            return values().find { it.code == code }
        }
    }
}
