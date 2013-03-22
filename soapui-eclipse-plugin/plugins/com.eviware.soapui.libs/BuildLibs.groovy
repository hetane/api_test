import groovy.util.AntBuilder
import groovy.util.XmlSlurper
import groovy.xml.*

class BuildLibs
{
   def corePlugin
   def project
   def libDir = "lib"
   def mavenRepo = System.getenv("HOME") + "/.maven/repository"
   def libs = []
   def ant = new AntBuilder();
   def oldFiles = [:]
   
   static void main(args)
   {
      if(args.length != 1)
      {
         println "Got ${args.length} arguments: ${args}, expected 1"
      	 println "Usage: BildLibs <core plugin>"
      	 return
      }

      def builder = new BuildLibs()
      builder.corePlugin = args[0]
      builder.build()
   }
   
   def build()
   {
      def projectXml = "${corePlugin}/project.xml"
      println "Copy libraries in ${projectXml} from ${mavenRepo} to ${libDir}"
      
      // Find the existing files in the libs plugins
      new File(libDir).eachFile
      {
         if(it.name != ".svn")
         {
            oldFiles[it.name] = it
         }
      }
      
      // Copy all dependencies with <jnlp.jar>true</jnlp.jar> from mavenRepo to libDir
      project = new XmlSlurper().parse(projectXml)
      for(dependency in project.dependencies.dependency)
      {
      	def jnlp = dependency.properties['jnlp.jar'].text()
      	def eclipse = dependency.properties['eclipse.jar'].text()
      	if(jnlp == 'true' || eclipse == 'true')
      	{
/*
      		// Special handling of soapui-xmlbeans <version>${pom.currentVersion}</version>
      		def version = dependency.version
      		if(version == '${pom.currentVersion}')
      			version = project.currentVersion
*/
      	    copyLib(dependency.groupId, dependency.artifactId, dependency.version, false)
      	}
      }
      
      copyLib(project.groupId, "soapui", project.currentVersion, true)
      copyLib(project.groupId, "soapui-xmlbeans", project.currentVersion, true)
      copyLib(project.groupId, "soapui-coverage-xmlbeans", project.currentVersion, true)
      
      // Don't delete the whole directory, because we don't want to delete .svn
      for(oldFile in oldFiles.values)
      {
      	ant.delete(file: oldFile)
      }
      
      // Update libs/.classpath
      def classpathFile= new FileWriter(".classpath")
      classpathFile.write '<?xml version="1.0" encoding="UTF-8"?>\n'
      def classpathXML = new MarkupBuilder(classpathFile)
      classpathXML.classpath {
         classpathentry(kind: "src", path: "src")
         new File(libDir).eachFile{ file ->
            classpathentry(kind: "lib", exported: "true", path: "lib/" + file.name)   
         }
         classpathentry(kind: "con", path:"org.eclipse.jdt.launching.JRE_CONTAINER")
         classpathentry(kind: "output", path:"bin")
      }
      classpathFile.close()
      
      
      println "Refresh the project!"
   }
   
/* This does not work with plugin.xml ->
   
   // Update Bundle-ClassPath in MANIFEST.MF
   // Careful, this is extremely sensitive
   def manifest = 'META-INF/MANIFEST.MF'
   def backup = 'MANIFEST.MF.bak'
   ant.move(tofile: backup, file: manifest)
   def writer = new PrintStream(manifest)
   def inBundleClasspath = false
   new File(backup).eachLine
   {
   	if(it.startsWith('Bundle-ClassPath: '))
   	{
   		inBundleClasspath = true
   		
   		writer.print 'Bundle-ClassPath: soapui-plugin.jar,\n'
   		int i = 0
   		for(lib in libs)
   		{
   			writer.print " ${libDir}/${lib}"
   			if(i < libs.size() - 1)
   				writer.print ","
   			writer.print "\n"
   			i++
   		}
   	}
   	else if(inBundleClasspath)
   	{
   		if(!it.startsWith(' '))
   		{
   			inBundleClasspath = false
   		}
   	}
   	if(!inBundleClasspath)
   	{
   		writer.print it + "\n"
   	}
   }
*/
   
   def copyLib(groupId, artifactId, version, boolean force)
   {
      def jarName = "${artifactId}-${version}.jar"
      libs.add(jarName)
      
      if(force || oldFiles[jarName] == null)
      {
         def fileName = "${mavenRepo}/${groupId}/jars/${jarName}"
         println "copy $fileName"
         ant.copy(todir: libDir, file: fileName)
      }
      else
      {
         oldFiles.remove(jarName)
      }
   }
}
