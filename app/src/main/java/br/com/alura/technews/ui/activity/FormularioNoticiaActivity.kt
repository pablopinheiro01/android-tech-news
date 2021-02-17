package br.com.alura.technews.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import br.com.alura.technews.R
import br.com.alura.technews.database.AppDatabase
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource
import br.com.alura.technews.ui.activity.extensions.mostraErro
import br.com.alura.technews.ui.viewmodel.FormularioNoticiaViewModel
import br.com.alura.technews.ui.viewmodel.factory.FormularioNoticiaViewModelFactory
import kotlinx.android.synthetic.main.activity_formulario_noticia.*

private const val TITULO_APPBAR_EDICAO = "Editando notícia"
private const val TITULO_APPBAR_CRIACAO = "Criando notícia"
private const val MENSAGEM_ERRO_SALVAR = "Não foi possível salvar notícia"

class FormularioNoticiaActivity : AppCompatActivity() {

    private val noticiaId: Long by lazy {
        intent.getLongExtra(NOTICIA_ID_CHAVE, 0)
    }

//    private val repository by lazy {
//        NoticiaRepository(AppDatabase.getInstance(this).noticiaDAO)
//    }

    private val viewModel by lazy{
        val noticiaRepository = NoticiaRepository(AppDatabase.getInstance(this).noticiaDAO)
        val factory = FormularioNoticiaViewModelFactory(noticiaRepository)
        ViewModelProviders.of(this, factory).get(FormularioNoticiaViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("buscaporid","teste")
        setContentView(R.layout.activity_formulario_noticia)
        definindoTitulo()
        preencheFormulario()
    }

    private fun definindoTitulo() {
        title = if (noticiaId > 0) {
            TITULO_APPBAR_EDICAO
        } else {
            TITULO_APPBAR_CRIACAO
        }
    }

    private fun preencheFormulario() {

        Log.i("buscaporid", "buscando a noticia...")
        viewModel.buscaPorId(noticiaId).observe(this, Observer { noticiaEncontrada ->
            Log.i("buscaporid","Entrou no observer com a noticia de id: "+noticiaId)
            if(noticiaEncontrada != null){
                Log.i("buscaporid","valor do titulo encontrado: "+noticiaEncontrada.titulo)
                Log.i("buscaporid","valor do texto encontrado: "+noticiaEncontrada.texto)
                activity_formulario_noticia_texto.setText(noticiaEncontrada.texto)
                activity_formulario_noticia_titulo.setText(noticiaEncontrada.titulo)
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.formulario_noticia_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.formulario_noticia_salva -> {
                val titulo = activity_formulario_noticia_titulo.text.toString()
                val texto = activity_formulario_noticia_texto.text.toString()
                salva(Noticia(noticiaId, titulo, texto))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun salva(noticia: Noticia) {

        viewModel.salva(noticia).observe(this, Observer {
            if (it.erro == null) {
                //funcao que a activity deve executar
                finish()
            } else {
                mostraErro(MENSAGEM_ERRO_SALVAR)
            }
        })
    }

}
