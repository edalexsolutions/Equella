libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "18.0",
  "com.github.insubstantial" % "flamingo" % "7.3",
  "com.miglayout" % "miglayout-swing" % "4.2",
  "org.springframework" % "spring-web" % "2.5.5",
  "org.springframework" % "spring-aop" % "2.5.5"
)

scalaVersion := "2.11.7"

dependsOn(platformSwing, LocalProject("com_tle_common_applet"), LocalProject("com_tle_applet_filemanager"))

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
