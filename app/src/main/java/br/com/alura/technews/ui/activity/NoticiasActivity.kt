package br.com.alura.technews.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.activity.extensions.transacaoFragment
import br.com.alura.technews.ui.fragment.ListaNoticiasFragment
import br.com.alura.technews.ui.fragment.VisualizaNoticiaFragment

private const val TAG_FRAGMENT_VISUALIZA_NOTICIA = "visualizaNoticia"


class NoticiasActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)
        //verifica se ja existe um estado criado pela activity
        if(savedInstanceState == null){
            abreListaNoticias()
        }else{
            //caso tenha algum conteudo temos os fragments disponiveis, vou verificar se é o fragment de noticia
            supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_VISUALIZA_NOTICIA)?.let{fragmentCriado ->
                // a partir dos argumentos existentes eu crio um novo fragment
                val argumentos = fragmentCriado.arguments
                //este novo fragment pode ser utilizado no replace passando o novo fragment evitando erro de reuso do fragment anterior
                val novoFragment = VisualizaNoticiaFragment()
                //passo os argumentos
                novoFragment.arguments = argumentos

                transacaoFragment {
                    remove(fragmentCriado)
                }

                //precisamos realizar este procedimento para exibir a lista fazendo o pop na tela
                supportFragmentManager.popBackStack()

                //caso o tipo do fragment seja o mesmo (Visualiza noticia) eu crio um novo container conforme a sua orientação
                transacaoFragment {
                    //verifico qual a posicao da tela
                    val container = if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                        R.id.activity_noticias_container_secundario
                    }else{
                        //permite voltar para um determinado ponto
                        //backstack so executa no modo retrato
                        addToBackStack(null)
                        R.id.activity_noticias_container_primario
                    }
                    //faço o replace do Fragment passando o novo fragmento no container criado
                    replace(container, novoFragment, TAG_FRAGMENT_VISUALIZA_NOTICIA)
                }

            }
        }
    }

    private fun abreListaNoticias() {
        transacaoFragment {
            replace(R.id.activity_noticias_container_primario, ListaNoticiasFragment(), "lista-noticias")
        }
    }

    //este metodo indica quando um fragment é atachado a activity
    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        //verifico qual o tipo de fragment q esta sendo utilizado,
        // lembrando que essa imoplementação permite uma flexibilidade maior no uso de fragments
        when(fragment){
            is ListaNoticiasFragment -> {
                configuraListaNoticias(fragment)
            }

            is VisualizaNoticiaFragment -> {
                configuraVisualizaNoticia(fragment)
            }
        }
    }

    private fun configuraVisualizaNoticia(fragment: VisualizaNoticiaFragment) {
        fragment.quandoFinalizaTela = this::finish
        fragment.quandoSelecionaMenuEdicao = this::abreFormularioEdicao
    }

    private fun configuraListaNoticias(fragment: ListaNoticiasFragment) {
        fragment.quandoNoticiaSelecionada = this::abreVisualizadorNoticia
        fragment.quandoFabSalvaNoticiaClicada = this::abreFormularioModoCriacao
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(noticia: Noticia) {

        val fragment = VisualizaNoticiaFragment()
        val dados = Bundle()
        dados.putLong(NOTICIA_ID_CHAVE, noticia.id)
        fragment.arguments = dados

        transacaoFragment {
            //permite voltar para um determinado ponto
//            addToBackStack("lista-noticias") //eu informo o nome no caso de voltar para uma tela especifica
            //verifico qual a posicao da tela
            val container = if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                R.id.activity_noticias_container_secundario
            }else{
                //backstack so executa no modo retrato
                addToBackStack(null)
                R.id.activity_noticias_container_primario
            }
            replace(container, fragment, TAG_FRAGMENT_VISUALIZA_NOTICIA)
        }
    }

    private fun abreFormularioEdicao(noticia: Noticia) {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticia.id)
        startActivity(intent)
    }

}
