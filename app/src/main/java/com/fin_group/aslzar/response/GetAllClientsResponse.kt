package com.fin_group.aslzar.response

data class GetAllClientsResponse(
    val result: List<Client>
)

data class Client(
    val id: String,
    val client_name: String,
    val bonus: Number,
    val status: String,
    val client_type: String,
    val limit: Number
)