package br.com.alura.technews.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val noticiasEncontradas = MutableLiveData<Resource<List<Noticia>?>>();

    fun buscaTodos() : LiveData<Resource<List<Noticia>?>> {

        val atualizaListaDeNoticias: (List<Noticia>) -> Unit = { novasNoticias ->
            //quando sucesso passamos a lista de noticias
            noticiasEncontradas.value = Resource(dado = novasNoticias)
        }
        buscaInterno(quandoSucesso = atualizaListaDeNoticias)

        buscaNaApi(quandoSucesso = atualizaListaDeNoticias, quandoFalha = { erro ->
            //pego meu recurso atual
            val resourceAtual = noticiasEncontradas.value
            //verifico se a lista e diferente de nula significando que ja tenho informacoes previamente carregadas
            val resourceDeFalha = criaResourceDeFalha(resourceAtual, erro)
            noticiasEncontradas.value = resourceDeFalha
        })

        return noticiasEncontradas;
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
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        removeNaApi(noticia, quandoSucesso, quandoFalha)
    }

    fun edita(
        noticia: Noticia
    ): LiveData<Resource<Void?>> {

        val liveData = MutableLiveData<Resource<Void?>>()
        editaNaApi(noticia, quandoSucesso = {
            //aqui a intenção é apenas notificar entao eu posso criar um Resource null
            liveData.value = Resource(null)
        }, quandoFalha = {erro ->
            liveData.value = Resource(dado = null,erro = erro)
        })
        return liveData

    }

    fun buscaPorId(
        noticiaId: Long
    ): LiveData<Resource<Noticia?>>{
        val liveData = MutableLiveData<Resource<Noticia?>>()
        buscaPorIdInterno(noticiaId, quandoSucesso = {
            Log.i("buscaporid","quandosucesso recebemos o seguinte valor: "+it.texto)
            liveData.value = Resource(it)
        }, quandoFalha = {
            Log.i("buscaporid","quandoFalha encontramos o seguinte erro: "+it.toString())
            liveData.value = Resource(null,it)
        }
        )
        Log.i("buscaporid","retornando do metodo buscaPorId")
        return liveData
    }

    private fun buscaPorIdInterno(noticiaId: Long, quandoSucesso: (Noticia) -> Unit, quandoFalha: (erro: String?) -> Unit){
        Log.i("buscaporid","Entramos em buscaPorIdInterno")
        BaseAsyncTask(quandoExecuta = {
            val dado = dao.buscaPorId(noticiaId)
            Log.i("buscaporid","dado encontrado:"+dado.toString())
            dado
        }, quandoFinaliza = {
            if (it != null) {
                Log.i("buscaporid","entramos em finalizacao e retornando sucesso")
                quandoSucesso(it)
            }
            quandoFalha("Erro ao buscar por Id Interno")
        })
            .execute()
    }

    private fun buscaNaApi(
        quandoSucesso: (List<Noticia>) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.buscaTodas(
            quandoSucesso = { noticiasNovas ->
                noticiasNovas?.let {
                    salvaInterno(noticiasNovas, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun buscaInterno(quandoSucesso: (List<Noticia>) -> Unit) {
        BaseAsyncTask(quandoExecuta = {
            Log.i("teste", "buscando noticias no banco")
//            Thread.sleep(5000)
            dao.buscaTodos()
        }, quandoFinaliza = {noticiasNovas ->
            Log.i("teste", "finalizou a busca")
            quandoSucesso(noticiasNovas)
        }).execute()
    }

    private fun salvaNaApi(
        noticia: Noticia,
        quandoSucesso: (noticiaNova: Noticia) -> Unit,
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

    private fun salvaInterno(
        noticias: List<Noticia>,
        quandoSucesso: (noticiasNovas: List<Noticia>) -> Unit
    ) {
        BaseAsyncTask(
            quandoExecuta = {
                dao.salva(noticias)
                dao.buscaTodos()
            }, quandoFinaliza = quandoSucesso
        ).execute()
    }

    private fun salvaInterno(
        noticia: Noticia,
        quandoSucesso: (noticiaNova: Noticia) -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.salva(noticia)
            dao.buscaPorId(noticia.id)
        }, quandoFinaliza = { noticiaEncontrada ->
            noticiaEncontrada?.let {
                quandoSucesso(it)
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
        quandoSucesso: (noticiaEditada: Noticia) -> Unit,
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
