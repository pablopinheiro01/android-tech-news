package br.com.alura.technews.repository

//recurso que sera utilizado no LiveData para flexibilizar o envio de informacoes de sucesso e de erro
class Resource<T>(
    val dado : T?,
    val erro: String? = null
) {
}