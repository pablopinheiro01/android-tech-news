
package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.viewmodel.VisualizaNoticiaViewModel
import kotlinx.android.synthetic.main.activity_visualiza_noticia.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val NOTICIA_NAO_ENCONTRADA = "Notícia não encontrada"
private const val TITULO_APPBAR = "Notícia"
private const val MENSAGEM_FALHA_REMOCAO = "Não foi possível remover notícia"

class VisualizaNoticiaActivity : AppCompatActivity() {

//    private val database by inject<AppDatabase>()
//    val repository = NoticiaRepository(database.noticiaDAO)


    private val noticiaId: Long by lazy {
        intent.getLongExtra(NOTICIA_ID_CHAVE, 0)
    }

    private val viewModel:VisualizaNoticiaViewModel by viewModel<VisualizaNoticiaViewModel> { parametersOf(noticiaId) }

//    private val viewModel by lazy{
////        val repository = NoticiaRepository(AppDatabase.getInstance(this).noticiaDAO)
//        //injetando a dependencia singleton via koin
//        val repository = NoticiaRepository(database.noticiaDAO)
//        val factory = VisualizaNoticiaViewModelFactory(noticiaId, repository)
//        ViewModelProviders.of(this, factory).get(VisualizaNoticiaViewModel::class.java)
//    }

    private lateinit var noticia: Noticia

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualiza_noticia)
        title = TITULO_APPBAR
        verificaIdDaNoticia()
        buscaNoticiaSelecionada()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.visualiza_noticia_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.visualiza_noticia_menu_edita -> abreFormularioEdicao()
            R.id.visualiza_noticia_menu_remove -> remove()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buscaNoticiaSelecionada() {

        Log.i("buscaporid","buscando a noticia no fluxo de visualizacao...")
        //acesso o objeto noticia encontrada atualizada diretamente pelo LiveData
        viewModel.noticiaEncontrada.observe(this, Observer{ noticiaEncontrada: Noticia? ->
            Log.i("buscaporid","Entrou no observer")
            noticiaEncontrada?.let {
                this.noticia = it
                preencheCampos(it)
            }
        })
    }

    private fun verificaIdDaNoticia() {
        if (noticiaId == 0L) {
            br.com.alura.technews.ui.fragment.extensions.mostraErro(
                NOTICIA_NAO_ENCONTRADA
            )
            finish()
        }
    }

    private fun preencheCampos(noticia: Noticia) {
        activity_visualiza_noticia_titulo.text = noticia.titulo
        activity_visualiza_noticia_texto.text = noticia.texto
    }

    private fun remove() {
        if (::noticia.isInitialized) {

            viewModel.remove().observe(this, Observer {
                if(it.erro == null){
                    finish()
                }else{
                    br.com.alura.technews.ui.fragment.extensions.mostraErro(
                        MENSAGEM_FALHA_REMOCAO
                    )
                }
            })

        }
    }

    private fun abreFormularioEdicao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticiaId)
        startActivity(intent)
    }

}
