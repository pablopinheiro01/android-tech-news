package br.com.alura.technews.ui.viewmodel

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
        return repository.salva(noticia)
    }
}