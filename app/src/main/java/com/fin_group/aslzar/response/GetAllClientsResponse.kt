package com.fin_group.aslzar.response

data class GetAllClientsResponse(
    val result: List<Client>
)

data class Client(
    val bonus: Double,
    val client_name: String,
    val id: String,
    val status: String
)