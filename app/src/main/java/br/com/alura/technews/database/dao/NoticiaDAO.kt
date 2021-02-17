package br.com.alura.technews.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import br.com.alura.technews.model.Noticia

@Dao
interface NoticiaDAO {

    @Query("SELECT * FROM Noticia ORDER BY id DESC")
    fun buscaTodos(): List<Noticia>

    @Insert(onConflict = REPLACE)
    fun salva(noticia: Noticia)

    @Delete
    fun remove(noticia: Noticia)

    @Query("SELECT * FROM Noticia WHERE id = :id")
    //o livedata vai ficar responsavel por atualizar o banco de dados quando houver uma atualização
    //utilizamos o principio single source of throught - unica fonte de verdade
    fun buscaPorId(id: Long): LiveData<Noticia?>

    @Insert(onConflict = REPLACE)
    fun salva(noticias: List<Noticia>)

}
