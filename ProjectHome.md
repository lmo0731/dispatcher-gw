# Config #
## gw.properties ##
```
#----------log4j configuration----------
log4j.rootLogger=ALL, R, F
log4j.appender.R=org.apache.log4j.ConsoleAppender
log4j.appender.R.layout=org.apache.log4j.TTCCLayout
log4j.appender.R.encoding=UTF-8
log4j.appender.F=org.apache.log4j.DailyRollingFileAppender
log4j.appender.F.file=${catalina.base}/logs/gw.log
log4j.appender.F.layout=org.apache.log4j.PatternLayout
log4j.appender.F.encoding=UTF-8
log4j.appender.F.DatePattern='.'yyyy-MM-dd'.log'
log4j.appender.F.layout.conversionPattern = [%-5p][%d{HH:mm:ss,SSS}][%c][%t][%C:(%3L)]: %m%n
log4j.appender.F.append=true

#lmo.gw.dispatcher.lib.Authenticator-s udamshsan class bna. User-n permission shalgah zoriulalttai.
authenticator=lmo.gw.dispatcher.lib.impl.DefaultAuthenticator

#lmo.gw.dispatcher.lib.ConfigReloader-s udamshsan class bna. Config unshih zoriulalttai.
configReloader=lmo.gw.dispatcher.lib.impl.DefaultConfigReloader


#----Function config--------------------
# func.<Function>.pattern=<Pattern>
# pattern wildcard-g zovhon /*/ iim nohtsold demjine. /*.xml/ zereg nohtsold demjihgui.
#
# func.<Function>=<Context>!<Servlet>
#---------------------------------------

func.Example.pattern=/example/*/url/*
func.Example=/example-1.0-SNAPSHOT!/
func.ReloadConfig=/GW!/ConfigReloader
func.ReloadConfig.pattern=/ReloadConfig

#-----User configuration----------------
# user.<Username>=<Password>
# user.<Username>.ip=<IP>, <IP>
#---------------------------------------

user.admin=admin
user.admin.ips=127.0.0.1
user.developer=Password1
user.developer.ips=*

#-----User permission-------------------
# user.<Username>.roles=<Function>#<Method>, ...
#---------------------------------------

user.developer.roles=Test#POST, Example#GET
user.admin.roles=ReloadConfig#GET

```