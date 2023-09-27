package com.fin_group.aslzar.api

import com.fin_group.aslzar.response.Auth
import com.fin_group.aslzar.response.ForgotPasswordResponse
import com.fin_group.aslzar.response.GetAllCategoriesResponse
import com.fin_group.aslzar.response.GetAllClientsResponse
import com.fin_group.aslzar.response.GetAllProductsResponse
import com.fin_group.aslzar.response.GetProductByIdResponse
import com.fin_group.aslzar.response.GetSimilarProductsResponse
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.response.ResponseChangePassword
import com.fin_group.aslzar.response.ResponseForgotPassword
import com.fin_group.aslzar.response.SalesPlanResponse
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {

    @POST(Constants.LOGIN)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun userLogin(): Call<Auth>

    @GET(Constants.GET_ALL_PRODUCTS)
    fun getAllProducts(@Header("Authorization") token: String): Call<GetAllProductsResponse>

    @GET(Constants.GET_PRODUCT_BY_ID)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun getProductByID(@Header("Authorization") token: String, @Path("id") id: String): Call<Product>

    @GET(Constants.SIMILAR_PRODUCTS)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun getSimilarProducts(@Header("Authorization") token: String, @Path("id") id: String): Call<GetSimilarProductsResponse>

    @GET(Constants.SET_IN_PRODUCT)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun getSetInProduct(@Header("Authorization") token: String, @Path("id") id: String): Call<GetAllProductsResponse>

    @GET(Constants.GET_CATEGORY)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun getAllCategories(@Header("Authorization") token: String): Call<GetAllCategoriesResponse>

    @GET(Constants.GET_CLIENTS)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun getAllClients(@Header("Authorization") token: String): Call<GetAllClientsResponse>

    @GET(Constants.GET_COEFFICIENT)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun getPercentAndMonth(@Header("Authorization") token: String): Call<PercentInstallment>

    @GET(Constants.GET_SALES_PLAN)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun getSalesPlan(@Header("Authorization") token: String): Call<SalesPlanResponse>

    @POST(Constants.FORGOT_PASSWORD_WITH_MAIL)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun forgotPasswordWithMail( @Path("mail") mail: String): Call<ForgotPasswordResponse>

    @POST(Constants.CHANGE_PASSWORD)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun changePassword(@Path("password") password: String): Call<ResponseChangePassword>

    @POST(Constants.FORGOT_PASSWORD)
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun forgotPassword( @Header("Authorization") token: String): Call<ResponseForgotPassword>

}