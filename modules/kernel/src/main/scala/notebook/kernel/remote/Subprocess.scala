package notebook.kernel.remote

import java.io.File
import notebook.kernel.pfork.ProcessFork
import notebook.kernel.ConfigUtils._
import com.typesafe.config.Config
import notebook.kernel.ConfigUtils

class Subprocess[A : Manifest](config: Config) extends ProcessFork[A] {

  // Make sure the custom classpath comes first, so that child processes can override this process' libs (might be cleaner to load the bare minimum of JARs)
  override lazy val classPathString =
    (config getArray("kernel.classpath") getOrElse(Nil) :+ super.classPathString)
    .mkString(File.pathSeparator)

  override lazy val workingDirectory =
    config get "kernel.dir" match {
      case None => new File(".")
      case Some(f) => new File(f)
    }

  override def heap = config.getMem("heap") getOrElse super.heap
  override def permGen = config.getMem("permGen") getOrElse super.permGen
  override def stack = config.getMem("stack") getOrElse super.stack
  override def reservedCodeCache = config.getMem("reservedCodeCache") getOrElse super.reservedCodeCache

  override def server = config get "server" map { _.toBoolean } getOrElse super.server

  override def jvmArgs = (config.getArray("vmArgs").getOrElse(Nil).toIndexedSeq) ++ super.jvmArgs
}
