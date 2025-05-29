package com.example.appreceitas

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appreceitas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHelper
    private var imagemSelecionada: Uri? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imagemSelecionada = it
        binding.imagePreview.setImageURI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.btnEscolherImagem.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.btnSalvar.setOnClickListener {
            val titulo = binding.edtTitulo.text.toString()
            val ingredientes = binding.edtIngredientes.text.toString().split(",").map { it.trim() }
            val imagemPath = imagemSelecionada?.toString() ?: ""

            if (titulo.isNotEmpty() && ingredientes.isNotEmpty()) {
                db.adicionarReceita(titulo, imagemPath, ingredientes)
                carregarReceitas()
            }
        }

        binding.listReceitas.setOnItemClickListener { _, _, position, _ ->
            val receita = db.listarReceitas()[position]
            val intent = Intent(this, ReceitaActivity::class.java)
            intent.putExtra("idReceita", receita.id)
            startActivity(intent)
        }

        carregarReceitas()

    }

    private fun carregarReceitas() {
        val receitas = db.listarReceitas()
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, receitas.map { it.titulo })
        binding.listReceitas.adapter = adapter
    }

}