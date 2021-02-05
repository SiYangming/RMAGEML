
# need to do something to ensure destruction of JVM
# when package is unloaded

.First.lib <- function(libname,pkgname)
 {
 library.dynam("RMAGEML",pkgname,libname)
 require(methods)
 .Call("rmageml_init", PACKAGE="RMAGEML")	 
 if(.Platform$OS.type == "windows" && require(Biobase) && interactive() && .Platform$GUI ==  "Rgui"){
    addVigs2WinMenu("RMAGEML")
  }
}
