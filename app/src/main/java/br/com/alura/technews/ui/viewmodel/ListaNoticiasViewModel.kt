package br.com.alura.technews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

class ListaNoticiasViewModel(
    private val repository: NoticiaRepository
) : ViewModel() {
    //caso ja tenha uma informacao o observer ja devolve rapidamente na segunda chamada.
//    private val liveData = MutableLiveData<List<Noticia>>()

    init{
        Log.i("ViewModel","criando viewModel")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("viewModel","Destruindo viewModel")
    }

    //sempre que disponibilizamos livedata devemos enviar somente a referencia para nao alterarem o objeto
    fun buscaTodos() : LiveData<Resource<List<Noticia>?>> {
        //retorna o liveData direto do repository
        return repository.buscaTodos()
    }

}