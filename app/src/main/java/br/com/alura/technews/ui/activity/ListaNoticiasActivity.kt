package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.fragment.ListaNoticiasFragment

private const val TITULO_APPBAR = "Notícias"


class ListaNoticiasActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_noticias)
        title = TITULO_APPBAR

    }

    //este metodo indica quando um fragment é atachado a activity
    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        //verifico qual o tipo de fragment q esta sendo utilizado,
        // lembrando que essa imoplementação permite uma flexibilidade maior no uso de fragments
        if(fragment is ListaNoticiasFragment){
            fragment.quandoNoticiaSelecionada = {
                abreVisualizadorNoticia(it)
            }

            fragment.quandoFabSalvaNoticiaClicada = {
                abreFormularioModoCriacao()
            }
        }
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(it: Noticia) {
        val intent = Intent(this, VisualizaNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, it.id)
        startActivity(intent)
    }

}
