package br.com.alura.technews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia

private const val NOME_BANCO_DE_DADOS = "news.db"

@Database(entities = [Noticia::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val noticiaDAO: NoticiaDAO

    companion object {

        //vamos tratar a base de dados como Singleton
        private lateinit var  db: AppDatabase

        fun getInstance(context: Context): AppDatabase {
            //caso tenha sido inicializado devolvemos o mesmo objeto
            if(::db.isInitialized) return db

            //caso nao tenha sido inicializado realizamos o procedimento de inicializacao
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                NOME_BANCO_DE_DADOS
            ).build()

            return db
        }

    }

}