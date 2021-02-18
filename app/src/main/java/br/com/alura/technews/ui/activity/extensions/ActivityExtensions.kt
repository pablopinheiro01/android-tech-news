package br.com.alura.technews.ui.activity.extensions

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction

fun Activity.mostraErro(mensagem: String) {
    Toast.makeText(
        this,
        mensagem,
        Toast.LENGTH_LONG
    ).show()
}

//vamos fazer um metodo que recebe uma expressao lambda que sera executado pelo FragmentTransaction
//utilizamos uma tecnica do kotlin chamada DSL
fun AppCompatActivity.transacaoFragment(executa: FragmentTransaction.() -> Unit ){
    val transaction = supportFragmentManager.beginTransaction()
    executa(transaction)
    transaction.commit()
}