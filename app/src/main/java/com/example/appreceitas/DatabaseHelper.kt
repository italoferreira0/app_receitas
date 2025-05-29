package com.example.appreceitas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "ReceitasDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE receitas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                imagem TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE ingredientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_receita INTEGER,
                nome TEXT,
                FOREIGN KEY (id_receita) REFERENCES receitas(id)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS receitas")
        db.execSQL("DROP TABLE IF EXISTS ingredientes")
        onCreate(db)
    }

    fun adicionarReceita(titulo: String, imagem: String, ingredientes: List<String>) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("titulo", titulo)
            put("imagem", imagem)
        }
        val idReceita = db.insert("receitas", null, values)

        for (ingrediente in ingredientes) {
            val valuesIng = ContentValues().apply {
                put("id_receita", idReceita)
                put("nome", ingrediente)
            }
            db.insert("ingredientes", null, valuesIng)
        }
    }

    fun listarReceitas(): List<Receita> {
        val lista = mutableListOf<Receita>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM receitas", null)

        while (cursor.moveToNext()) {
            val receita = Receita(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2)
            )
            lista.add(receita)
        }
        cursor.close()
        return lista
    }

    fun deleteReceitas(idReceita: Int): Boolean {
        val db = writableDatabase

        try {
            db.delete("receitas", "id = ?", arrayOf(idReceita.toString()))
            db.delete("ingredientes", "id_receita = ?", arrayOf(idReceita.toString()))
            return true
        }catch (

            e: Exception
        ){
            println("Erro ao excluir receita: ${e.message}")
            return false
        }

    }

    fun listarIngredientes(idReceita: Int): List<String> {
        val lista = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nome FROM ingredientes WHERE id_receita = ?", arrayOf(idReceita.toString()))
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0))
        }
        cursor.close()
        return lista
    }
}