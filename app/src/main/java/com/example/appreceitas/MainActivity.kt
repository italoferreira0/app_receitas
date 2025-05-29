package com.example.appreceitas

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.appreceitas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHelper
    private var imagemSelecionada: Uri? = null

    // Launcher para pegar imagem da galeria
    private val launcherImagem = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imagemSelecionada = it
        binding.imagePreview.setImageURI(it)
    }

    // Launcher para abrir a ReceitaActivity e detectar retorno
    private val launcherReceita = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val deleted = result.data?.getBooleanExtra("deleted", false) ?: false
            if (deleted) {
                carregarReceitas()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.btnEscolherImagem.setOnClickListener {
            launcherImagem.launch("image/*")
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
            launcherReceita.launch(intent) // <- abre ReceitaActivity esperando resultado
        }

        carregarReceitas()
    }

    private fun carregarReceitas() {
        val receitas = db.listarReceitas()
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, receitas.map { it.titulo })
        binding.listReceitas.adapter = adapter
    }
}
