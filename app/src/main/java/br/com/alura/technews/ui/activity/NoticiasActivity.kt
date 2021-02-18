package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.fragment.ListaNoticiasFragment
import br.com.alura.technews.ui.fragment.VisualizaNoticiaFragment

private const val TITULO_APPBAR = "Notícias"


class NoticiasActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)
        title = TITULO_APPBAR

        val transacao = supportFragmentManager.beginTransaction()
        transacao.add(R.id.activity_noticias_container, ListaNoticiasFragment(), "lista-noticias")
        transacao.commit()

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

        if(fragment is VisualizaNoticiaFragment){
            fragment.quandoFinalizaTela = { finish() }
            fragment.quandoSelecionaMenuEdicao = { noticiaSelecionada -> abreFormularioEdicao(noticiaSelecionada) }
        }
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(noticia: Noticia) {
//        val intent = Intent(this, VisualizaNoticiaActivity::class.java)
//        intent.putExtra(NOTICIA_ID_CHAVE, it.id)
//        startActivity(intent)

        //criando um fragment programaticamente
        val transacao = supportFragmentManager.beginTransaction()
        val fragment = VisualizaNoticiaFragment()
        val dados = Bundle()
        dados.putLong(NOTICIA_ID_CHAVE, noticia.id)
        fragment.arguments = dados

//        val fragmentEncontrado = supportFragmentManager.findFragmentByTag("lista-noticias")
//        if(fragmentEncontrado != null){
//            transacao.remove(fragmentEncontrado)
//        }
//        transacao.add(R.id.activity_visualiza_noticia_viewgroup_fragment_container,fragment)
        //esta linha de codigo equivale a remover o fragment conforme o comportamento programado acima
        transacao.replace(R.id.activity_visualiza_noticia_viewgroup_fragment_container,fragment)
        //executa a criacao do fragment
        transacao.commit()

    }

    private fun abreFormularioEdicao(noticia: Noticia) {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticia.id)
        startActivity(intent)
    }

}
