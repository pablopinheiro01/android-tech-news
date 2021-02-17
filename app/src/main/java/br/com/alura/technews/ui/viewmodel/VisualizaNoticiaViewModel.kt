package br.com.alura.technews.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

class VisualizaNoticiaViewModel(
    id: Long,
    private val repository: NoticiaRepository
) : ViewModel() {

    //realizamos a chamada diretamente com LiveData por este motivo disponibilizamos a noticia
    val noticiaEncontrada = repository.buscaPorId(id)

    fun remove(): LiveData<Resource<Void?>> {
        return noticiaEncontrada.value?.run{
            repository.remove(this)
        } ?: MutableLiveData<Resource<Void?>>().also { it.value = Resource(null, "Noticia Nao encontrada") }
    }
}