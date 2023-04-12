package pt.vilhena.ai.trabalhopratico

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.vilhena.ai.trabalhopratico.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
