package pt.vilhena.ai.trabalhopratico.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import pt.vilhena.ai.trabalhopratico.R
import pt.vilhena.ai.trabalhopratico.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.primaryButton.setOnClickListener {
            if (binding.activityChooser.checkedRadioButtonId == -1) {
                Toast.makeText(context, R.string.missing_activity_toast, Toast.LENGTH_SHORT).show()
            } else {
                Log.v("App", binding.activityChooser.checkedRadioButtonId.toString())
                binding.informationText.text = "Atividade"
                binding.activityChooser.isVisible = false
                binding.timer.isVisible = true
                binding.SecondaryButton.isVisible = true
            }
        }

        binding.activityChooser.setOnCheckedChangeListener { _, checkedId ->
            changeAnimation(checkedId)
        }
    }

    private fun changeAnimation(checkId: Int) {
        binding.activityAnimation.apply {
            when (checkId) {
                R.id.radioButtonWalking -> {
                    setAnimation(R.raw.walking_animation)
                    playAnimation()
                }
                R.id.radioButtonRunning -> {
                    setAnimation(R.raw.running_animation)
                    playAnimation()
                }
                R.id.radioButtonClimbingUp -> {
                    setAnimation(R.raw.climbing_up_stairs_animation)
                    playAnimation()
                }
                R.id.radioButtonClimbingDown -> {
                    setAnimation(R.raw.climbing_down_stairs_animation)
                    playAnimation()
                }
            }
        }
    }
}
