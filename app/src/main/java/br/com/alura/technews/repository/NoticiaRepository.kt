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

        buscaInterno(quandoSucesso = {novasNoticias ->
            //quando sucesso passamos a lista de noticias
            noticiasEncontradas.value = Resource(dado = novasNoticias)
        })

        buscaNaApi(quandoSucesso = {novasNoticias ->
            noticiasEncontradas.value = Resource(dado = novasNoticias)
        }, quandoFalha = {mensagemDeErro ->
            //pego meu recurso atual
            val resourceAtual = noticiasEncontradas.value
            //verifico se a lista e diferente de nula significando que ja tenho informacoes previamente carregadas
            val resourceCriado: Resource<List<Noticia>?> = if(resourceAtual != null){
                Resource(dado = resourceAtual.dado, erro = mensagemDeErro)
            }else{
                //caso nao tenha nada na lista de erro eu seto somente a mensagem e a informacao do dado nula
                Resource(dado = null, erro = mensagemDeErro)
            }

            noticiasEncontradas.value = resourceCriado
        })

        return noticiasEncontradas;
    }

    fun salva(
        noticia: Noticia,
        quandoSucesso: (noticiaNova: Noticia) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        salvaNaApi(noticia, quandoSucesso, quandoFalha)
    }

    fun remove(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        removeNaApi(noticia, quandoSucesso, quandoFalha)
    }

    fun edita(
        noticia: Noticia,
        quandoSucesso: (noticiaEditada: Noticia) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        editaNaApi(noticia, quandoSucesso, quandoFalha)
    }

    fun buscaPorId(
        noticiaId: Long,
        quandoSucesso: (noticiaEncontrada: Noticia?) -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.buscaPorId(noticiaId)
        }, quandoFinaliza = quandoSucesso)
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
            Thread.sleep(5000)
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
