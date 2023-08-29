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
import pt.vilhena.ai.trabalhopratico.data.common.ActivitiesEnum
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
        setupObservers()
        if (viewModel.activityStarted) {
            startCapture()
        } else {
            setupUI()
        }
    }

    private fun setupUI() {
        binding.primaryButton.setOnClickListener {
            if (binding.activityChooser.checkedRadioButtonId == -1) {
                Toast.makeText(context, R.string.missing_activity_toast, Toast.LENGTH_SHORT).show()
            } else {
                startCapture()
            }
        }

        binding.activityChooser.setOnCheckedChangeListener { _, checkedId ->
            changeAnimation(checkedId)
        }
    }

    private fun setupObservers() {
        viewModel.fileSentFlag.observe(
            viewLifecycleOwner,
        ) {
            fileToastMaker(it)
        }

        viewModel.elapsedTime.observe(
            viewLifecycleOwner,
        ) {
            binding.timer.text = it
        }
    }

    private fun changeAnimation(checkId: Int) {
        binding.activityAnimation.apply {
            when (checkId) {
                R.id.radioButtonWalking -> {
                    viewModel.setSelectedActivity(ActivitiesEnum.WALKING.activity)
                    setAnimation(ActivitiesEnum.WALKING.animationResource)
                    playAnimation()
                }
                R.id.radioButtonRunning -> {
                    viewModel.setSelectedActivity(ActivitiesEnum.RUNNING.activity)
                    setAnimation(ActivitiesEnum.RUNNING.animationResource)
                    playAnimation()
                }
                R.id.radioButtonClimbingUp -> {
                    viewModel.setSelectedActivity(ActivitiesEnum.CLIMBING_UP_STAIRS.activity)
                    setAnimation(ActivitiesEnum.CLIMBING_UP_STAIRS.animationResource)
                    playAnimation()
                }
                R.id.radioButtonClimbingDown -> {
                    viewModel.setSelectedActivity(ActivitiesEnum.CLIMBING_DOWN_STAIRS.activity)
                    setAnimation(ActivitiesEnum.CLIMBING_DOWN_STAIRS.animationResource)
                    playAnimation()
                }
                else -> {
                    viewModel.setSelectedActivity("")
                    setAnimation(R.raw.hi_animation)
                    playAnimation()
                }
            }
        }
    }

    //  Start capture sensor data
    private fun startCapture() {
        binding.secondaryButton.setOnClickListener {
            stopCapture()
        }
        binding.informationText.text = viewModel.currentActivity.value
        binding.activityChooser.isVisible = false
        binding.timer.isVisible = true
        binding.primaryButton.isVisible = false
        binding.secondaryButton.isVisible = true
        if (!viewModel.activityStarted) {
            viewModel.startCapture()
        }
        binding.sessionId.apply {
            isVisible = true
            text = viewModel.sessionID
        }
    }

    //  Stop capture sensor data
    private fun stopCapture() {
        viewModel.stopCapture()
        binding.activityChooser.clearCheck()
        binding.activityAnimation.setAnimation(R.raw.hi_animation)
        binding.informationText.text = getString(R.string.choose_activity)
        binding.activityChooser.isVisible = true
        binding.timer.isVisible = false
        binding.primaryButton.isVisible = true
        binding.secondaryButton.isVisible = false
        binding.sessionId.apply {
            isVisible = false
            text = ""
        }
    }

    private fun fileToastMaker(wasFileSent: Boolean) {
        if (wasFileSent) {
            Toast.makeText(context, R.string.file_sent_success, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, R.string.file_sent_error, Toast.LENGTH_SHORT).show()
        }
    }
}
