package com.example.appreceitas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appreceitas.databinding.ActivityReceitaBinding

class ReceitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceitaBinding
    private lateinit var db: DatabaseHelper
    private var idReceita: Int = -1 // Correto: declarar fora do onCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        // Pegando o id da receita que foi passado pela MainActivity
        idReceita = intent.getIntExtra("idReceita", -1)

        if (idReceita != -1) {
            val receita = db.listarReceitas().find { it.id == idReceita }
            receita?.let {
                binding.txtTitulo.text = it.titulo
                binding.imgReceita.setImageURI(Uri.parse(it.imagem))
                val ingredientes = db.listarIngredientes(idReceita).joinToString("\n") { ingrediente -> "• $ingrediente" }
                binding.txtIngredientes.text = "Ingredientes:\n$ingredientes"
            }
        }

        binding.btnVoltar.setOnClickListener {
            finish() // Volta para a MainActivity
        }

        binding.btnDelete.setOnClickListener {
            if (idReceita != -1) {
                db.deleteReceitas(idReceita)
                val resultIntent = Intent()
                resultIntent.putExtra("deleted", true)
                setResult(RESULT_OK, resultIntent)
                finish() // Fecha a activity após deletar
            }
        }
    }
}
