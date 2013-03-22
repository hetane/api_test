import groovy.util.AntBuilder
import groovy.util.XmlSlurper
import groovy.xml.*

if(args.length != 1)
{
	println "Got ${args.length}, expected 1"
    println "Usage: Build <core project.xml>"
	return
}

def projectXml = args[0]
def libDir = "lib"
def mavenRepo = System.getProperty('user.home') + '/.maven/repository'
println "Copy libraries in ${projectXml} from ${mavenRepo} to ${libDir}"

def project = new XmlSlurper().parse(projectXml)
def currentVersion = project.currentVersion.text()
def artifactId = project.artifactId.text()
def groupId = project.groupId.text()
println "groupId=${groupId}, artifactId=${artifactId}, currentVersion=${currentVersion}"


def libs = []

def ant = new AntBuilder();

// Find the existing files in the libs plugins
oldFiles = [:]
new File(libDir).eachFile
{
   if(it.name != ".svn" && it.name != ".DS_Store")
   {
      oldFiles[it.name] = it
   }
}

// Copy soapui.jar to libDir
def jarName = "${artifactId}-${currentVersion}.jar"
def fileName = "${mavenRepo}/${groupId}/jars/${jarName}"
libs.add(jarName)
ant.copy(todir: libDir, file: fileName)

// Copy all dependencies with <jnlp.jar>true</jnlp.jar> from mavenRepo to libDir
//def project = new XmlSlurper().parse(projectXml)
for(dependency in project.dependencies.dependency)
{
	def jnlp = dependency.properties['jnlp.jar'].text()
	def eclipse = dependency.properties['eclipse.jar'].text()
	if(jnlp == 'true' || eclipse == 'true')
	{
		// Special handling of soapui-xmlbeans <version>${pom.currentVersion}</version>
		def version = dependency.version
		if(version == '${pom.currentVersion}')
			version = currentVersion
				
		jarName = "${dependency.artifactId}-${version}.${dependency.type}"
		fileName = "${mavenRepo}/${dependency.groupId}/jars/${jarName}"
		libs.add(jarName)
		if(oldFiles[jarName] == null)
		{
			ant.copy(todir: libDir, file: fileName)
		}
		else
		{
		   def oldJar = oldFiles[jarName]
		   if(oldJar.name != jarName)
		   {
//		      println "delete old version: ${oldJar.name}, new version=${jarName}"
		      ant.delete(file: oldJar)
		   }
		   else
		   {
//		      println "keep oldFile: $jarName"
		      oldFiles.remove(jarName)
		   }
		}
	}
}

// Don't delete the whole directory, because we don't want to delete .svn
for(oldFile in oldFiles.values)
{
   println "delete oldFile: $file"
   ant.delete(file: oldFile)
}

// Update .classpath
def classpathFile= new FileWriter(".classpath")
classpathFile.write '<?xml version="1.0" encoding="UTF-8"?>\n'
def classpathXML = new MarkupBuilder(classpathFile)
classpathXML.classpath {
   classpathentry(kind: "src", path: "src")
   new File(libDir).eachFile{ file ->
      if(file.name != '.DS_Store' && file.name != '.svn' && file.name != 'soapui-coverage-xmlbeans') {
         classpathentry(kind: "lib", exported: "true", path: "lib/" + file.name)
      }
   }
   classpathentry(kind: "con", path:"org.eclipse.jdt.launching.JRE_CONTAINER")
   classpathentry(kind: "output", path:"bin")
}
classpathFile.close()

println "The Eclipse project is updated, please refresh it."



// This does not work with plugin.xml ->

// Update Bundle-ClassPath in MANIFEST.MF
// Careful, this is extremely sensitive
def manifest = 'META-INF/MANIFEST.MF'
def backup = 'MANIFEST.MF.bak'
def buildproperties = 'build.properties'
def buildbackup = 'build.properties.bak'
ant.move(tofile: backup, file: manifest)
ant.move(tofile: buildbackup, file: buildproperties)
def writer = new PrintStream(manifest)
def buildwriter = new PrintStream(buildproperties)
buildwriter.print "bin.includes = plugin.xml,\\\n"
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
			buildwriter.print "               ${libDir}/${lib}"
			if(i < libs.size() - 1)
				writer.print ","
				buildwriter.print ",\\"
			writer.print "\n"
			buildwriter.print "\n"
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
buildwriter.print "jars.compile.order =\n"
buildwriter.print "src.includes = lib/\n"
//*/
