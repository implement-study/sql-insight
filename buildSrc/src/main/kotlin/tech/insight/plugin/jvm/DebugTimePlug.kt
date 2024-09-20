package tech.insight.plugin.jvm

import java.io.File
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DebugTimePlug : Plugin {

    val file = File("/Users/gongxuanzhang/Desktop/medivh.cache")

    override fun matches(target: TypeDescription): Boolean {
        println(target.name)
        file.appendText(target.name)
        return true
    }

    override fun close() {
        file.appendText("close")
    }

    override fun apply(
        builder: DynamicType.Builder<*>,
        typeDescription: TypeDescription,
        classFileLocator: ClassFileLocator
    ): DynamicType.Builder<*> {
        
        return builder
    }
}
