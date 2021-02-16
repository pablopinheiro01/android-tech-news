package br.com.alura.technews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository

class ListaNoticiasViewModel(
    private val repository: NoticiaRepository
) : ViewModel() {

    init{
        Log.i("ViewModel","criando viewModel")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("viewModel","Destruindo viewModel")
    }

    //sempre que disponibilizamos livedata devemos enviar somente a referencia para nao alterarem o objeto
    fun buscaTodos() : LiveData<List<Noticia>> {
        //mutablelivedata temos acesso a leitura e a escrita
        val liveData = MutableLiveData<List<Noticia>>()
        repository.buscaTodos(quandoSucesso = {noticiasNovas ->
            liveData.value = noticiasNovas
        }, quandoFalha={})
        return liveData
    }

}