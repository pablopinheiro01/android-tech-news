package br.com.alura.technews.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import br.com.alura.technews.asynctask.BaseAsyncTask
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.retrofit.webclient.NoticiaWebClient

class NoticiaRepository(
    private val dao: NoticiaDAO,
    private val webclient: NoticiaWebClient = NoticiaWebClient()
) {
    //Esta informação da variavel e previamente carregada durante o uso da App, o componente LiveData ja faz a carga do objeto com os dados existentes antes de realizar uma nova busca
    //o resource permite uma lista nula no caso de alguma inconsistencia na carga
//    private val noticiasEncontradas = MutableLiveData<Resource<List<Noticia>?>>();

    private val mediadorLiveData = MediatorLiveData<Resource<List<Noticia>?>>()

    fun buscaTodos() : LiveData<Resource<List<Noticia>?>> {

        //adiciono arquivo do liveData que sera monitorado
        mediadorLiveData.addSource(buscaInterno(), Observer { noticias ->
            mediadorLiveData.value = Resource(dado = noticias)
        })

        //essa variavel sera preenchida somente no caso de erro, o MutableLiveData monitora as requisições de LiveData
        val falhaDaWebApiLiveData = MutableLiveData<Resource<List<Noticia>?>>()
        //mediador vai monitorar 2 lives data
        mediadorLiveData.addSource(falhaDaWebApiLiveData){
            //agora so trato o resource de falha caso ocorra
            resourceDeFalha ->
            val resourceAtual = mediadorLiveData.value
            //verifico se o meu resource atual é diferente de nulo, caso sim eu recebi um erro mas com dados ja existentes conforme o Resource criado
            val resourceNovo: Resource<List<Noticia>?> = if (resourceAtual != null){
                Resource(dado = resourceAtual.dado, erro = resourceDeFalha.erro)
            }else{
                //caso contrario contem somente o erro entao eu devolvo a informação
                resourceDeFalha
            }
            mediadorLiveData.value = resourceNovo
        }

        ///busca na API e so devolve um HOF no caso de erro.
        buscaNaApi(quandoFalha = { erro -> falhaDaWebApiLiveData.value = Resource(dado = null, erro = erro)})


        return mediadorLiveData;

//        val atualizaListaDeNoticias: (List<Noticia>) -> Unit = { novasNoticias ->
//            //quando sucesso passamos a lista de noticias
//            noticiasEncontradas.value = Resource(dado = novasNoticias)
//        }

//        buscaInterno(quandoSucesso = atualizaListaDeNoticias)
//        noticiasEncontradas.value = buscaInterno().
//        buscaNaApi(quandoSucesso = atualizaListaDeNoticias, quandoFalha = { erro ->
//            //pego meu recurso atual
//            val resourceAtual = noticiasEncontradas.value
//            //verifico se a lista e diferente de nula significando que ja tenho informacoes previamente carregadas
//            val resourceDeFalha = criaResourceDeFalha(resourceAtual, erro)
//            noticiasEncontradas.value = resourceDeFalha
//        })
    }

    fun salva(
        noticia: Noticia
    ) : LiveData<Resource<Void?>> {
        //nesse caso nao precisamos criar uma property para manter o ultimo
        //valor que estaria no livedata, nesse caso podemos sempre criar uma live data novo...
        val liveData = MutableLiveData<Resource<Void?>>()
        salvaNaApi(noticia, quandoSucesso = {
            //aqui a intenção é apenas notificar entao eu posso criar um Resource null
            liveData.value = Resource(null)
        }, quandoFalha = {erro ->
            liveData.value = Resource(dado = null,erro = erro)
        })
        return liveData
    }

    fun remove(
        noticia: Noticia
    ): LiveData<Resource<Void?>> {
        val liveData = MutableLiveData<Resource<Void?>>()
        removeNaApi(noticia, quandoSucesso = {
            liveData.value = Resource(null)
        },
        quandoFalha = {
            liveData.value = Resource(null, it)
        })
        return liveData
    }

    fun edita(
        noticia: Noticia
    ): LiveData<Resource<Void?>> {

        val liveData = MutableLiveData<Resource<Void?>>()
        editaNaApi(noticia, quandoSucesso = {
            //aqui a intenção é apenas notificar entao eu posso criar um Resource null
            liveData.value = Resource(null)
        }, quandoFalha = { erro ->
            liveData.value = Resource(dado = null,erro = erro)
        })
        return liveData

    }


    fun buscaPorId(noticiaId: Long): LiveData<Noticia?>{
        Log.i("buscaporid","Entramos em buscaPorIdInterno")
        return dao.buscaPorId(noticiaId)
        //toda essa responsabilidade sera transferida para o LiveData conforme mapeado no DAO
//        val liveData = MutableLiveData<Noticia?>()
//        BaseAsyncTask(quandoExecuta = {
//            val dado = dao.buscaPorId(noticiaId)
//            Log.i("buscaporid","dado encontrado:"+dado.toString())
//            dado
//        }, quandoFinaliza = {
//           liveData.value = it
//        }).execute()
//        return liveData

    }

    private fun buscaNaApi(
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.buscaTodas(
            quandoSucesso = { noticiasNovas ->
                noticiasNovas?.let {
                    salvaInterno(noticiasNovas)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun buscaInterno(): LiveData<List<Noticia>> {

        //retorna o liveData
        return dao.buscaTodos()
//passado a responsabilidade de busca e atualizacao das fontes atraves do singleton da base, e passado a responsabilidade para o LiveData gerenciar as devidas atualizacoes
//        BaseAsyncTask(quandoExecuta = {
//            Log.i("teste", "buscando noticias no banco")
////            Thread.sleep(5000)
//            dao.buscaTodos()
//        }, quandoFinaliza = {noticiasNovas ->
//            Log.i("teste", "finalizou a busca")
//            quandoSucesso(noticiasNovas)
//        }).execute()
    }

    private fun salvaNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.salva(
            noticia,
            quandoSucesso = {
                it?.let { noticiaSalva ->
                    salvaInterno(noticiaSalva, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun salvaInterno(noticias: List<Noticia>) {
        BaseAsyncTask(
            quandoExecuta = {
                dao.salva(noticias)
//                dao.buscaTodos()
            }, quandoFinaliza = {}
        ).execute()
    }

    private fun salvaInterno(
        noticia: Noticia,
        quandoSucesso: () -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.salva(noticia)
            dao.buscaPorId(noticia.id)
        }, quandoFinaliza = { noticiaEncontrada ->
            noticiaEncontrada?.let {
                quandoSucesso()
            }
        }).execute()

    }

    private fun removeNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.remove(
            noticia.id,
            quandoSucesso = {
                removeInterno(noticia, quandoSucesso)
            },
            quandoFalha = quandoFalha
        )
    }


    private fun removeInterno(
        noticia: Noticia,
        quandoSucesso: () -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.remove(noticia)
        }, quandoFinaliza = {
            quandoSucesso()
        }).execute()
    }

    private fun editaNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.edita(
            noticia.id, noticia,
            quandoSucesso = { noticiaEditada ->
                noticiaEditada?.let {
                    salvaInterno(noticiaEditada, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

}
