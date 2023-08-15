package pt.vilhena.ai.trabalhopratico.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import pt.vilhena.ai.trabalhopratico.R
import pt.vilhena.ai.trabalhopratico.databinding.FragmentMainBinding
import pt.vilhena.ai.trabalhopratico.viewmodel.SharedViewModel

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
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
                startCapture()
            }
        }

        binding.secondaryButton.setOnClickListener {
            stopCapture()
        }

        binding.activityChooser.setOnCheckedChangeListener { _, checkedId ->
            changeAnimation(checkedId)
        }
    }

    // TODO Refactor This
    private fun changeAnimation(checkId: Int) {
        binding.activityAnimation.apply {
            when (checkId) {
                R.id.radioButtonWalking -> {
                    viewModel.changeSelectedActivity(getString(R.string.walking))
                    setAnimation(R.raw.walking_animation)
                    playAnimation()
                }
                R.id.radioButtonRunning -> {
                    viewModel.changeSelectedActivity(getString(R.string.running))
                    setAnimation(R.raw.running_animation)
                    playAnimation()
                }
                R.id.radioButtonClimbingUp -> {
                    viewModel.changeSelectedActivity(getString(R.string.climbing_up_stairs))
                    setAnimation(R.raw.climbing_up_stairs_animation)
                    playAnimation()
                }
                R.id.radioButtonClimbingDown -> {
                    viewModel.changeSelectedActivity(getString(R.string.climbing_down_stairs))
                    setAnimation(R.raw.climbing_down_stairs_animation)
                    playAnimation()
                }
                else -> {
                    viewModel.changeSelectedActivity("")
                    setAnimation(R.raw.hi_animation)
                    playAnimation()
                }
            }
        }
    }

    //  Start capture sensor data
    private fun startCapture() {
        binding.informationText.text = viewModel.currentActivity.value
        binding.activityChooser.isVisible = false
        binding.timer.isVisible = true
        binding.secondaryButton.isVisible = true
        viewModel.startCapture()
        binding.sessionId.apply {
            isVisible = true
            text = viewModel.sessionID
        }
    }

    //  Stop capture sensor data
    private fun stopCapture() {
        viewModel.stopCapture()
        binding.activityChooser.clearCheck()

        binding.informationText.text = getString(R.string.choose_activity)
        binding.activityChooser.isVisible = true
        binding.timer.isVisible = false
        binding.secondaryButton.isVisible = false
        binding.sessionId.apply {
            isVisible = false
            text = ""
        }
    }
}
