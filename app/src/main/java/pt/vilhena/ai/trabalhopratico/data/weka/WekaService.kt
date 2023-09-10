package pt.vilhena.ai.trabalhopratico.data.weka

import android.app.Application
import android.util.Log
import weka.classifiers.trees.J48
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.FastVector
import weka.core.Instances
import weka.core.SerializationHelper

class WekaService(
    private val application: Application,
) {

    fun wekaClassifyFromModel(instanceString: String): String {
        try {
            // Load the pre-trained J48 model from assets
            val scheme = SerializationHelper.read(application.assets.open("j48Model.model")) as J48

            val atr1 = Attribute("alt")
            val atr2 = Attribute("accuracy")
            val atr3 = Attribute("bearing")
            val atr4 = Attribute("x_acc")
            val atr5 = Attribute("y_acc")
            val atr6 = Attribute("z_acc")
            val atr7 = Attribute("x_gyro")
            val atr8 = Attribute("y_gyro")
            val atr9 = Attribute("z_gyro")
            val atr10 = Attribute("x_mag")
            val atr11 = Attribute("y_mag")
            val atr12 = Attribute("z_mag")
            val atrClass = Attribute("activity", listOf("upstairs", "walking", "downstairs", "standing"))

            val array = instanceString.split(",")

            val wekaAttributes = FastVector<Attribute>(13)
            wekaAttributes.addElement(atr1)
            wekaAttributes.addElement(atr2)
            wekaAttributes.addElement(atr3)
            wekaAttributes.addElement(atr4)
            wekaAttributes.addElement(atr5)
            wekaAttributes.addElement(atr6)
            wekaAttributes.addElement(atr7)
            wekaAttributes.addElement(atr8)
            wekaAttributes.addElement(atr9)
            wekaAttributes.addElement(atr10)
            wekaAttributes.addElement(atr11)
            wekaAttributes.addElement(atr12)
            wekaAttributes.addElement(atrClass)

            val instance = Instances("dd", wekaAttributes, 12)
            instance.setClassIndex(12)

            val denseInstance = DenseInstance(12)
            Log.d("ff", "${array[0].toDouble()}")

            Log.d("ff", "Num attributes: ${denseInstance.numAttributes()}")
            Log.d("ff", "Num values: ${denseInstance.numValues()}")
            denseInstance.setValue(atr1, array[0].toDouble())
            denseInstance.setValue(atr2, array[1].toDouble())
            denseInstance.setValue(atr3, array[2].toDouble())
            denseInstance.setValue(atr4, array[3].toDouble())
            denseInstance.setValue(atr5, array[4].toDouble())
            denseInstance.setValue(atr6, array[5].toDouble())
            denseInstance.setValue(atr7, array[6].toDouble())
            denseInstance.setValue(atr8, array[7].toDouble())
            denseInstance.setValue(atr9, array[8].toDouble())
            denseInstance.setValue(atr10, array[9].toDouble())
            denseInstance.setValue(atr11, array[10].toDouble())
            denseInstance.setValue(atr12, array[11].toDouble())

            instance.add(denseInstance)

            val pred = scheme.classifyInstance(instance.instance(0))
            val predi = instance.classAttribute().value(pred.toInt())
            Log.d("ff", "pred: $predi")
            return predi
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}
