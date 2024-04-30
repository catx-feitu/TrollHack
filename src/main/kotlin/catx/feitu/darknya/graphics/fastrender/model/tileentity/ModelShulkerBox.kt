package catx.feitu.darknya.graphics.fastrender.model.tileentity

import catx.feitu.darknya.graphics.fastrender.model.Model
import catx.feitu.darknya.graphics.fastrender.model.ModelBuilder

class ModelShulkerBox : catx.feitu.darknya.graphics.fastrender.model.Model(64, 64) {
    override fun catx.feitu.darknya.graphics.fastrender.model.ModelBuilder.buildModel() {
        // Base
        childModel(0.0f, 28.0f) {
            addBox(-8.0f, 0.0f, -8.0f, 16.0f, 8.0f, 16.0f)
        }

        // Lid
        childModel {
            addBox(-8.0f, 4.0f, -8.0f, 16.0f, 12.0f, 16.0f)
        }
    }
}