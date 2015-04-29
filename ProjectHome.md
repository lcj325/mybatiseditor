# MyBatis Editor  #
## What's new ##
### 1.1.1, April 1, 2013 ###
  * Updated included DTDs for MyBatis 3.2.1.
  * Some minor bug fixes.
  * Tested on Eclipse Juno SR2.
### 1.1.0, July 6, 2012 ###
  * Tested on Eclipse Juno (1.0.3 works fine on Juno too).
  * Code completion introduced on various parts of the Mapper XML: resultMaps, parameterMaps, includes, and Java properties are now auto-completed. Please note that this is a first increment on this feature, file your comments on the enhancement [issue #5](https://code.google.com/p/mybatiseditor/issues/detail?id=#5).
  * Navigate to Java file from XML if you are using MyBatis 3 Mapper interfaces.
  * Added some compatibility config to co-exist better with other XML editor enhancing plug-ins (like M2E).
## Introduction ##
<p><a href='http://www.mybatis.org'>MyBatis</a> (formerly known as iBatis) is a popular database access framework. MyBatis  can be configured using XML configuration files. The EclipseLabs MyBatis Editor plug-in adds extra support for this in Eclipse.</p>

MyBatis Editor requires no configuration, adds no memory- or build-time overhead, and assumes all SQL Mapper configuration files in the same Eclipse project are related. It recognizes both iBatis 2 and MyBatis configuration syntax. It provides:

  * Inclusion of MyBatis/iBatis DTD files in the Eclipse XML catalog for faster validation.
  * Ctrl-click/F3 navigation in XML files to declaring elements, such as includes, resultmaps and parametermaps.
  * A separate view to show the resolved declaration of a selected MyBatis element, especially useful when using a lot of includes.
![http://mybatiseditor.eclipselabs.org.codespot.com/files/mybatisdeclaration.png](http://mybatiseditor.eclipselabs.org.codespot.com/files/mybatisdeclaration.png)
  * Navigate from a Java Mapper interface to the corresponding XML.
![http://mybatiseditor.eclipselabs.org.codespot.com/files/mybatisjava.png](http://mybatiseditor.eclipselabs.org.codespot.com/files/mybatisjava.png)

MyBatis integrates with the Eclipse Web Tools XML editor (a required dependency).

## Installation ##
You use one of the following methods to install MyBatis Editor:
  1. Eclipse 3.7 (Marketplace client installed): drag and drop this image to your running Eclipse window on a view or toolbar area: [![](http://marketplace.eclipse.org/misc/installbutton.png)](http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=100055)
  1. Install using Eclipse 3.6 marketplace: Open Eclipse and select Help > Eclipse Marketplace... and type "mybatiseditor" in the find text field.
  1. Install using update site: Open Eclipse and select Help > Install New Software... and paste the update site url in the "Work with:" text field: http://mybatiseditor.eclipselabs.org.codespot.com/git/org.eclipselabs.mybatiseditor.updatesite
  1. Install using dropins: create a mybatiseditor/plugins directory in the dropins directory of your Eclipse installation. Download the provided release jar and add to this directory.