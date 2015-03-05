Evacuates deleted files and folders from your backup media to a graveyard.

This small application can be run before the ordinary backup run. It checks if there are folders and/or files in the backup folder which are no more present in the original folder, thus likely meaning the user of the original folder has deleted them. 

In order not to loose any artefact such abandoned folder/files will be copied to a third place, a kind of a recycler bin. 


Usage
-----
Usage:

    bin\main.bat [options] OrigDir BackupDir EvacuateDir
	
	or
	
    bin/main [options] OrigDir BackupDir EvacuateDir
	

    where:
        OrigDir    : original directory
        BackupDir  : Backup directory
        EvacuateDir: Evacuation directory
    Options:
        -d, --dry-run: no file operation is done, files to evacuate are listed on console
        -m, --move   : evacuated files are moved from backup to evacuate dir instead of copy
        -e, --exclude: exclude file. Option must be followed by valid file URL (see below)


Needs JAVA_HOME environment variable set to a Java8 (jre) installation. 
	


Exclude File 
------------
A simple text file, each line denotes one exclude pattern. Usual wildcards are allowed and recognized. The syntax follows the java java.nio.file.FileSystem.getPathMatcher(String) (from http://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-):


The path is matched using a limited pattern language that resembles regular expressions but with a simpler syntax. For example: 

*.java 			Matches a path that represents a file name ending in .java 
*.* 			Matches file names containing a dot 
*.{java,class}} Matches file names ending with .java or .class 
foo.? 			Matches file names starting with foo. and a single character extension 
/home/*/*  		Matches /home/gus/data on UNIX platforms 
/home/**  		Matches /home/gus and /home/gus/data on UNIX platforms 
C:\\*  			Matches C:\foo and C:\bar on the Windows platform (note that the backslash is escaped; as a string literal in the Java Language the pattern would be "C:\\\\*")  

The following rules are used to interpret the patterns: 

- The * character matches zero or more characters of a name component without crossing directory boundaries. 
- The ** characters matches zero or more characters crossing directory boundaries. 
- The ? character matches exactly one character of a name component.
- The backslash character (\) is used to escape characters that would otherwise be interpreted as special characters. The expression \\ matches a single backslash and "\{" matches a left brace for example. 
- The [ ] characters are a bracket expression that match a single character of a name component out of a set of characters. For example, [abc] matches "a", "b", or "c". The hyphen (-) may be used to specify a range so [a-z] specifies a range that matches from "a" to "z" (inclusive). These forms can be mixed so [abce-g] matches "a", "b", "c", "e", "f" or "g". If the character after the [ is a ! then it is used for negation so [!a-c] matches any character except "a", "b", or "c". 
- Within a bracket expression the *, ? and \ characters match themselves. The (-) character matches itself if it is the first character within the brackets, or the first character after the ! if negating.
- The { }} characters are a group of subpatterns, where the group matches if any subpattern in the group matches. The "," character is used to separate the subpatterns. Groups cannot be nested. 
- Leading period/dot characters in file name are treated as regular characters in match operations. For example, the "*" glob pattern matches file name ".login". The Files.isHidden method may be used to test whether a file is considered hidden. 
- All other characters match themselves in an implementation dependent manner. This includes characters representing any name-separators. 
- The matching of root components is highly implementation-dependent and is not specified. 

The matching details, such as whether the matching is case sensitive, are implementation-dependent and therefore not specified.

