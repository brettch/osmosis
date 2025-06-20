INSTALLATION
Unzip the distribution in the location of your choice.
On unix/linux systems, make the bin/osmosis script executable (ie. chmod u+x osmosis).
If desired, create a symbolic link to the osmosis script somewhere on your path (eg. ln -s appdir/bin/osmosis ~/bin/osmosis).

CONFIGURATION
Common command line options can be specified in configuration files.

On Linux, the file can reside in the following locations with later files overriding earlier files:
/etc/osmosis
$HOME/.osmosis

On Windows, the file can reside in the following locations with later files overriding earlier files:
%ALLUSERSPROFILE%\osmosis.bat
%USERPROFILE%\osmosis.bat

The following variables can be defined in these files:
JAVACMD - The java command to be invoked.  Default is "java".
JAVACMD_OPTIONS - The java jvm options to apply (eg. -Xmx512M).
OSMOSIS_OPTIONS - The osmosis options to apply (eg. -q or -plugin MyPluginLoaderClass).

COMPILATION
To perform a complete osmosis rebuild, the following command may be run from the osmosis root directory.

HELP
Osmosis documentation is available at:
http://wiki.openstreetmap.org/index.php/Osmosis
