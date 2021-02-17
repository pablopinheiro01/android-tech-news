package br.com.alura.technews.repository

import br.com.alura.technews.model.Noticia

//recurso que sera utilizado no LiveData para flexibilizar o envio de informacoes de sucesso e de erro
class Resource<T>(
    val dado : T?,
    val erro: String? = null
){}

//- não utilizamos mais essa implementação devido todas as atualizações ficarem na responsabilidade do LiveData
//deixo no arquivo para ser acessivel a todos dentro do package
//transformando este metodo de forma generica para ser compativel independente do tipo que vamos trabalhar
//fun <T> criaResourceDeFalha(
//    resourceAtual: Resource<T?>?,
//    erro: String?
//): Resource<T?> {
//    if (resourceAtual != null) {
//        return Resource(dado = resourceAtual.dado, erro = erro)
//    }
//    //caso nao tenha nada na lista de erro eu seto somente a mensagem e a informacao do dado nula
//    return Resource(dado = null, erro = erro)
//}
