package br.com.alura.technews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

class FormularioNoticiaViewModel(
    private val repository: NoticiaRepository
) : ViewModel() {


    //posso enviar um valor nulo e notificar se teve erro, caso teve eu mostro caso nao eu apenas finalizo
    fun salva(noticia: Noticia): LiveData<Resource<Void?>> {
        if (noticia.id > 0) {
            return repository.edita(noticia)
        }
        return repository.salva(noticia)
    }

    fun buscaPorId(noticiaId: Long): LiveData<Noticia?> {
        Log.i("buscaporid","entramos no buscaPorId da ViewModel")
        return repository.buscaPorId(noticiaId)
    }
}