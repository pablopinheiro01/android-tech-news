package br.com.alura.technews.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.ui.viewmodel.FormularioNoticiaViewModel
import br.com.alura.technews.ui.viewmodel.ListaNoticiasViewModel

class FormularioNoticiaViewModelFactory (
    private val repository: NoticiaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        //aqui criamos a instancia que tera o repository que sera utilizado na View da Lista
        return FormularioNoticiaViewModel(repository) as T
    }
}