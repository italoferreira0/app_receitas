package com.example.appreceitas

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appreceitas.databinding.ActivityReceitaBinding

class ReceitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceitaBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVoltar.setOnClickListener {
            finish() // Finaliza esta activity e retorna para a anterior (MainActivity)
        }

        db = DatabaseHelper(this)

        val idReceita = intent.getIntExtra("idReceita", -1)

        if (idReceita != -1) {
            val receita = db.listarReceitas().find { it.id == idReceita }!!
            binding.txtTitulo.text = receita.titulo
            binding.imgReceita.setImageURI(Uri.parse(receita.imagem))
            val ingredientes = db.listarIngredientes(idReceita).joinToString("\n") { "• $it" }
            binding.txtIngredientes.text = "Ingredientes:\n$ingredientes"
        }
    }
}