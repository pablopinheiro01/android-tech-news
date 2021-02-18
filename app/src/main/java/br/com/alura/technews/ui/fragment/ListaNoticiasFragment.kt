package br.com.alura.technews.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.fragment.extensions.mostraErro
import br.com.alura.technews.ui.recyclerview.adapter.ListaNoticiasAdapter
import br.com.alura.technews.ui.viewmodel.ListaNoticiasViewModel
import kotlinx.android.synthetic.main.lista_noticias.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.IllegalArgumentException

private const val MENSAGEM_FALHA_CARREGAR_NOTICIAS = "Não foi possível carregar as novas notícias"

class ListaNoticiasFragment : Fragment() {


    private val adapter by lazy {
        context?.let { ListaNoticiasAdapter(context = it) }
    } ?: throw IllegalArgumentException("Contexto invalido")

    private val viewModel: ListaNoticiasViewModel by viewModel<ListaNoticiasViewModel>()

    var quandoFabSalvaNoticiaClicada: () -> Unit = {}
    var quandoNoticiaSelecionada: (noticia: Noticia) -> Unit = {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //aqui nao criamos a View, apenas fazemos inicializações,
        //para view é utilizado outro conceito nos fragments

        buscaNoticias()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lista_noticias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuraRecyclerView()
        configuraFabAdicionaNoticia()
    }

    private fun configuraFabAdicionaNoticia() {
        lista_noticias_fab_salva_noticia.setOnClickListener {
            quandoFabSalvaNoticiaClicada()
        }
    }

    private fun configuraRecyclerView() {
        val divisor = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        lista_noticias_recyclerview.addItemDecoration(divisor)
        lista_noticias_recyclerview.adapter = adapter
        configuraAdapter()
    }

    private fun configuraAdapter() {
        adapter?.quandoItemClicado = quandoNoticiaSelecionada
    }

    private fun buscaNoticias() {
        //quando chamamos o liveData temos acesso as suas funções, no caso o livedata esta atrelado ao observe que e retornado no buscatodos
        //1º parametro - a interface LifeCycleOwner ja e implementado automaticamente por Activits e Fragments
        viewModel.buscaTodos().observe(this, Observer {resource ->
            resource.dado?.let{noticias ->
                //indico o objeto atualizado
                adapter?.atualiza(noticias)
            }
            resource.erro?.let{
                mostraErro(MENSAGEM_FALHA_CARREGAR_NOTICIAS)
            }
        })

    }

}