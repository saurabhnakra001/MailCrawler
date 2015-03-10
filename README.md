#Maven mail chain crawler 

A mail crawler that extracts hyperlinks on ```mail-archives.apache.org/mod_mbox/maven-users/```, generates seeds 
and downloads the mail contents into the output folder. User has the ability to configure the number of threads 
to generate links and download mails so as to improve the speed of processing. 
The crawler can also withstand internet connection loss for a specified timeout. 

###How to run
  *  Download the zip from git and extract contents. 
  *  On shell/command prompt, change directory to the extracted folder. 
  *  The packaged crawler.jar contains all the classes to run the crawler and its dependencies.
  *  Run command ```java -jar crawler.jar``` to execute the program.
  *  An output folder is created with downloaded mails organized under year and month folders. 

###How to build 
  *  Ensure maven is installed. 
  *  Run ```pom.xml``` with the goal(s) ```clean compile assembly:single```
  *  Check target folder for compiled and created crawler.jar. 


